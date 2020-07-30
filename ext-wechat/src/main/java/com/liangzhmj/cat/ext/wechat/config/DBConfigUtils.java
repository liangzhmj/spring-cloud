package com.liangzhmj.cat.ext.wechat.config;

import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.dao.mysql.utils.DBUtils;
import com.liangzhmj.cat.dao.utils.DaoBeanUtils;
import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;


/**
 * 数据库配置信息工具类，使用该类，必须在数据库存在
 * 1.素材主体信息配置表t_wechat_subject
 * 2.第三方配置信息表t_wechat_third
 * @author liangzhmj
 *
 */
@Log4j2
public class DBConfigUtils {

	private static APIDao serviceDao;
	//缓存小程序的配置信息
	private static Map<String,String[]> subject = new HashMap<>();
	//缓存第三方平台的配置信息
	private static Map<String,String[]> third = new HashMap<>();
	//缓存第三方平台的配置信息(根据授权号获取)
	private static Map<String,String[]> thirdByAppid = new HashMap<>();

	static{
		if(DaoBeanUtils.getServiceDao() != null){
			serviceDao =  DaoBeanUtils.getServiceDao();
		}else{
			serviceDao = DaoBeanUtils.getBaseDao();
		}
	}

	/**
	 * 根据appid获取素材主体配置信息
	 * @param appid
	 * @return 0:appid,1:app密钥,2:商户号,3:商户密钥,4:支付通知路径,5:退款通知路径,6:扩展字段
	 */
	public static String[] getSubjectConfig(String appid){
		if(StringUtils.isEmpty(appid)){
			throw new WechatException("素材主体appid为null");
		}
		String[] configs = subject.get(appid);
		if(configs != null){
			return configs;
		}
		synchronized (DBConfigUtils.class) {
			configs = subject.get(appid);
			if(configs != null){
				return configs;
			}
			//没有缓存,查询数据库
			try {
				Object[] confobjs = serviceDao.getObjects("SELECT appid,secret,mchId,msecret,paySyncUrl,refunSyncUrl,extData FROM t_wechat_subject WHERE isDel=0 AND appid='"+DBUtils.mysql_varchar_escape(appid)+"'");
				if(CollectionUtils.isEmpty(confobjs)){
					throw new WechatException("素材主体["+appid+"]获取不到小程序配置信息");
				}
				configs = new String[7];
				configs[0] = StringUtils.getCleanString(confobjs[0]);
				configs[1] = StringUtils.getCleanString(confobjs[1]);
				configs[2] = StringUtils.getCleanString(confobjs[2]);
				configs[3] = StringUtils.getCleanString(confobjs[3]);
				configs[4] = StringUtils.getCleanString(confobjs[4]);
				configs[5] = StringUtils.getCleanString(confobjs[5]);
				configs[6] = StringUtils.getCleanString(confobjs[6]);
				//保存到缓存里面
				subject.put(appid,configs);
			} catch (Exception e) {
				log.info(e.getMessage());
			}
		}
		if(configs == null){
			throw new WechatException("素材主体["+appid+"]获取不到配置信息");
		}
		return configs;
	}

	/**
	 * 根据appId获取第三方配置信息
	 * @param appid
	 * @return 0:appid,1:secretKey,2:msgToken,3:msgAseKey,4:扩展字段
	 */
	public static String[] getThridPConfig(String appid){
		if(StringUtils.isEmpty(appid)){
			return null;
		}
		String[] configs = third.get(appid);
		if(configs != null){
			return configs;
		}
		synchronized (DBConfigUtils.class) {
			configs = third.get(appid);;
			if(configs != null){
				return configs;
			}
			//没有缓存,查询数据库
			try {
				Object[] confobjs = serviceDao.getObjects("SELECT appid,secretKey,msgToken,msgAseKey,extData FROM t_wechat_third WHERE isDel=0 AND appid='"+DBUtils.mysql_varchar_escape(appid)+"'");
				if(CollectionUtils.isEmpty(confobjs)){
					throw new WechatException("平台["+appid+"]获取不到配置信息1");
				}
				configs = new String[5];
				configs[0] = StringUtils.getCleanString(confobjs[0]);
				configs[1] = StringUtils.getCleanString(confobjs[1]);
				configs[2] = StringUtils.getCleanString(confobjs[2]);
				configs[3] = StringUtils.getCleanString(confobjs[3]);
				configs[4] = StringUtils.getCleanString(confobjs[4]);
				//保存到缓存里面
				third.put(appid,configs);
			} catch (Exception e) {
				log.info(e.getMessage());
			}
		}
		if(configs == null){
			throw new WechatException("平台["+appid+"]获取不到配置信息2");
		}
		return configs;
	}

	public static void clear(){
		third.clear();
		subject.clear();
		thirdByAppid.clear();
	}
	public static void subjectClear(){
		subject.clear();
	}
	public static void thirdClear(){
		third.clear();
	}
	public static void setThirdByappidClear(){
		thirdByAppid.clear();
	}

    /**
     * 根据被授权公众号appId获取第三方配置信息(同一个号不能同时授权给多个个平台)
     * @param appid
     * @return 0:appid,1:secretKey,2:msgToken,3:msgAseKey,4:扩展字段
     */
    public static String[] getThridPConfigByWechatId(String appid){
        if(StringUtils.isEmpty(appid)){
            return null;
        }
        String[] configs = thirdByAppid.get(appid);
        if(configs != null){
            return configs;
        }
        synchronized (DBConfigUtils.class) {
            configs = thirdByAppid.get(appid);
            if(configs != null){
                return configs;
            }
            //没有缓存,查询数据库
            try {
                Object[] confobjs = serviceDao.getObjects("SELECT t1.appid,t1.secretKey,t1.msgToken,t1.msgAseKey,t1.extData "
                        + "FROM t_wechat_third t1 JOIN t_wechat_auth t2 ON(t1.appid=t2.thirdAppId AND t2.authStatus=1 AND t2.isDel=0) "
                        + "WHERE t1.isDel=0 AND t2.appid='"+appid+"' LIMIT 1");
                if(CollectionUtils.isEmpty(confobjs)){
                    throw new WechatException("公众号["+appid+"]获取不到第三方平台配置信息1");
                }
                configs = new String[5];
                configs[0] = StringUtils.getCleanString(confobjs[0]);
                configs[1] = StringUtils.getCleanString(confobjs[1]);
                configs[2] = StringUtils.getCleanString(confobjs[2]);
                configs[3] = StringUtils.getCleanString(confobjs[3]);
                configs[4] = StringUtils.getCleanString(confobjs[4]);
                //保存到缓存里面
                thirdByAppid.put(appid,configs);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        if(configs == null){
            throw new WechatException("公众号["+appid+"]获取不到第三方平台配置信息2");
        }
        return configs;
    }
	
}
