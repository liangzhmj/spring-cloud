package com.liangzhmj.cat.dao.dbconfig.config;

import com.liangzhmj.cat.dao.dbconfig.vo.ConfigVO;
import com.liangzhmj.cat.dao.mysql.impl.BaseDao;
import com.liangzhmj.cat.dao.mysql.impl.ServiceDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;

/**
 * 读取数据库配置
 */
@Log4j2
public class DBConfig {

	@Value("${dbconfig.table:#{null}}")
	private String table;

	@Bean("dbConfigVO")
	@ConditionalOnExpression("'${dbconfig.db}'.equals('base')")
	public ConfigVO baseDBConfig(@Qualifier("baseDao") BaseDao baseDao){
		log.info("从base初始化数据库配置表:"+table);
		ConfigVO vo = new ConfigVO(baseDao,table);
		vo.initProperties();
		return vo;
	}

	@Bean("dbConfigVO")
	@ConditionalOnExpression("'${dbconfig.db}'.equals('service')")
	public ConfigVO serviceDBConfig(@Qualifier("serviceDao") ServiceDao serviceDao){
		log.info("从service初始化数据库配置表:"+table);
		ConfigVO vo = new ConfigVO(serviceDao,table);
		vo.initProperties();
		return vo;
	}

}