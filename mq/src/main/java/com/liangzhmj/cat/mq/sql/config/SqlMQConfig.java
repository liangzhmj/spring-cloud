package com.liangzhmj.cat.mq.sql.config;

import com.liangzhmj.cat.dao.mysql.APIDao;
import com.liangzhmj.cat.mq.exception.MqException;
import com.liangzhmj.cat.mq.sql.SqlConsumer;
import com.liangzhmj.cat.mq.sql.SqlMQ;
import com.liangzhmj.cat.mq.sql.queue.SqlQueue;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 实例化一个公共队列,需要自定义的话，可以直接编写一个包含两个队列的类，然后创建消费者，然后调用start()几个启动处理队列，例如：<br/>
 * new SqlConsumer("INSERT-SQL队列", apiDao, 1000, 5, 2,2, "/home/www/wx-tp-api/failSqls/insert/", InsertSqlQ.getQueue1(), InsertSqlQ.getQueue2(), InsertSqlQ.getQueue3()).start();
 * @author liangzhmj
 */
@Log4j2
@Setter
@Getter
@ConfigurationProperties(prefix = "mq")//这里报错不用管，应该是说@ConfigurationProperties要在bean类上，这里通过@Import注入，而编辑并不知道
public class SqlMQConfig {

	private List<Config> sqls;
	@Autowired(required = false)
	@Qualifier("baseDao")
	private APIDao baseDao;
	@Autowired(required = false)
	@Qualifier("serviceDao")
	private APIDao serviceDao;

	@PostConstruct
	public void registySqlMq(){
		if(CollectionUtils.isEmpty(sqls)){
			log.error("配置中找不到mq.sqls配置信息，请在application.yml中配置mq.sqls - [name,max,interval,failFilePath...]");
			return;
		}
		for (Config sql : sqls) {
			try {
				String db = sql.getDb();
				APIDao apiDao = null;
				if("base".equals(db)){
					apiDao = baseDao;
				}else if("service".equals(db)){
					apiDao = serviceDao;
				}else{
					throw new MqException("sql队列["+sql.getName()+"]指定了错误的数据库:"+db+"，目标数据库必须为[base|service]");
				}
				SqlQueue sq = new SqlQueue();
				SqlConsumer sc = new SqlConsumer(sql.getName(),apiDao,sql.getMax(),sql.getPool1(),sql.getPool2(),sql.getInterval(),sql.getFailFilePath(),sq.getQueue1(),sq.getQueue2(),sq.getQueue3());
				sc.start();
				SqlMQ.addMQ(sql.getName(),sc,sq);
				log.info("队列["+sql.getName()+"]已注册-"+sql);
			} catch (MqException e) {
				log.error("部分sql队列初始化失败:"+sql,e);
			}
		}
	}


}