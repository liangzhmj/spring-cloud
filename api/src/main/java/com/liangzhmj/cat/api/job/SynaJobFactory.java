package com.liangzhmj.cat.api.job;

import com.liangzhmj.cat.api.engine.ClassLoaderEngine;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 动态任务工厂
 * @author liangzhmj
 *
 */
@Component("synaJobFactory")
@Log4j2
public class SynaJobFactory {

	/** 缓存定时任务更新的时间戳 **/
	private Map<String,String> cache = new HashMap<String,String>();
	@Resource(name="baseDao")
	private APIDao baseDao;
	@Value("${api.job.regexp:#{null}}")
	private String regexp;

	/**
	 * 初始化任务
	 */
	@PostConstruct
	public synchronized void init(){
		//获取匹配的aop类
		SchedulerUtils su = SchedulerUtils.getInstance();
		try {
			if(StringUtils.isEmpty(regexp)){
				throw new APIException("没有配置定时任务");
			}
			List<Object[]> infos = baseDao.getObjectList("SELECT fullpackage,updatetime FROM t_inter_class WHERE isUse=1 AND type=1 AND fullpackage REGEXP '"+regexp+"'");
			if(CollectionUtils.isEmpty(infos)){
				throw new APIException("系统中不存在定时任务");
			}
			
			for (Object[] info : infos) {
				try {
					String fp = StringUtils.getCleanString(info[0]);
					String updatetime = StringUtils.getCleanString(info[1]);
					if(StringUtils.isEmpty(fp) || StringUtils.isEmpty(updatetime)){
						throw new APIException("加载定时任务["+fp+"]-["+updatetime+"]失败");
					}
					//获取这个类最近更新的时间戳
					String lasttime = cache.get(fp);
					if(!StringUtils.isEmpty(lasttime) && lasttime.compareTo(updatetime)>=0){//没有更新
						throw new APIException("定时任务["+fp+"]-src["+lasttime+"]-dest["+updatetime+"]-不用更新");
					}
					Class<?> clazz = ClassLoaderEngine.loadClass(baseDao,fp);
					if(clazz == null){
						throw new APIException("加载定时任务["+fp+"]失败");
					}
					SynaJob myJob = (SynaJob)clazz.newInstance();
					if(!myJob.isValid()){
						//停止任务
						int stopRes = su.stopJob(myJob.getId());
						log.info("定时任务["+fp+"]移除"+(stopRes==1?"成功":"失败"));
						throw new APIException("定时任务["+fp+"]不可用");
					}
					//停止任务
					int stopRes = su.stopJob(myJob.getId());
					log.info("定时任务["+fp+"]移除"+(stopRes==1?"成功":"失败"));
					//重新启动任务
					int startRes = su.startJob(myJob);
					log.info("定时任务["+fp+"]启动"+(startRes==1?"成功":"失败"));
					//更新时间
					cache.put(fp, updatetime);
				} catch (Exception e) {
					log.info("初始化部分定时任务异常:"+e.getMessage());
				}
			}
			
		} catch(APIException e){
			log.warn("定时任务初始化异常,此次初始化无效:"+e.getMessage());
		} catch (Exception e) {
			log.warn("定时任务初始化异常,此次初始化无效:",e);
		}
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public String getRegexp() {
		return regexp;
	}
}
