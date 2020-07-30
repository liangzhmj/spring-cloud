package com.liangzhmj.cat.dao.mysql.config;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.liangzhmj.cat.dao.mysql.impl.ServiceDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * druid数据源配置
 * 
 * @author liangzhmj
 *
 */
@Log4j2
public class DataSourceService {

	@Bean // 声明其为Bean实例
	@ConfigurationProperties(prefix = "spring.datasource.service")
	public DataSource serviceDatasource() {
		log.info("create service-DataSource");
		return DruidDataSourceBuilder.create().build();
	}

	@Bean
	public JdbcTemplate serviceJdbcTemplate(@Qualifier("serviceDatasource") DataSource dataSource){
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public ServiceDao serviceDao(@Qualifier("serviceJdbcTemplate") JdbcTemplate jdbcTemplate){
		return new ServiceDao(jdbcTemplate);
	}
}
