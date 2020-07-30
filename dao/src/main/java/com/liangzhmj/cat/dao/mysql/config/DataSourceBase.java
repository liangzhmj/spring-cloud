package com.liangzhmj.cat.dao.mysql.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.liangzhmj.cat.dao.mysql.impl.BaseDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 默认数据源配置,如果只有一个数据源的话springboot会帮我们自动生成DataSource和JdbcTemplate对象
 * 由于我们另外添加了一条数据源，因此spring管理的bean里面有两套DataSource和JdbcTemplate的对象
 * 由于springboot的数据源注入是根据类型的，因此要这里要显示生成数据源并添加@Primary注解
 * （其实是有的20200721）并且这里的javax.sql.DataSource并没有设置参数的Setter方法，而又可以通过@ConfigurationProperties注入参数（在其他项目中已验证）
 * 可能是springboot初始化的时候特定处理了，这跟druid的监控页面是一样的，通过@Import引入的数据源初始化可能比较靠后
 * 因此druid监控web没有被处理到，所以如果想要web监控，还需要@Import druid对应的bean
 * 
 * @author liangzhmj
 *
 */
@Log4j2
public class DataSourceBase {

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.base")
	public DataSource dataSource() {
		log.info("create base-DataSource");
		return DruidDataSourceBuilder.create().build();
	}

	@Bean
	@Primary
	public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource){
		return new JdbcTemplate(dataSource);
	}
	@Bean
	public BaseDao baseDao(@Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate){
		return new BaseDao(jdbcTemplate);
	}
}
