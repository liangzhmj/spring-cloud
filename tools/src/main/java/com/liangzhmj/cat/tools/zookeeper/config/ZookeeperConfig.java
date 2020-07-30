package com.liangzhmj.cat.tools.zookeeper.config;

import com.liangzhmj.cat.tools.zookeeper.SessionConnectionStateListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


@Getter
@Setter
@Log4j2
public class ZookeeperConfig {

	@NonNull
	@Value("${zk.hosts:}")
	private String hosts;
	@NonNull
	@Value("${zk.namespace:'cat-api'}")
	private String namespace;
	@Value("${zk.sessionTimeout:20000}")
	private int sessionTimeout = 20000;
    @Value("${zk.connectionTimeout:10000}")
    private int connectionTimeout = 10000;
    @Value("${zk.baseSleepTimeMs:1000}")
    private int baseSleepTimeMs = 1000;
    @Value("${zk.maxRetries:3}")
    private int maxRetries = 3;


	@Bean
	public CuratorFramework curatorFramework(){
	    log.debug("连接zookeeper:{hosts:"+hosts+",namespace:"+namespace+"}");
		//开关连接属于重量级操作，在项目中不必一个锁一个连接，可以一个项目对应一个连接
		//重连策略（每隔1s重连，最多重连3次）,内建有四种重试策略,也可以自行实现RetryPolicy接口
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
		//通过工厂创建连接
		CuratorFramework cf = CuratorFrameworkFactory.builder()
				.connectString(hosts)
				.sessionTimeoutMs(sessionTimeout)//会话超时时间，单位毫秒，默认60000ms
				.connectionTimeoutMs(connectionTimeout)//连接创建超时时间，单位毫秒，默认60000ms
				.retryPolicy(retryPolicy)//重连策略
				.namespace(namespace)//命名空间，客户端指定了独立命名空间为“/myapi”，那么该客户端对Zookeeper上的数据节点的操作都是基于该目录进行的，这对于实现不同应用之间的相互隔离十分有意义。
				.build();
		//开启连接
		cf.start();
		//添加断开重连listener，可以每个节点都添加
		cf.getConnectionStateListenable().addListener(new SessionConnectionStateListener());
		return cf;
	}

}

