package com.liangzhmj.cat.tools.zookeeper;

import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

@Log4j2
public class SessionConnectionStateListener implements ConnectionStateListener {
	

	@Override
	public void stateChanged(CuratorFramework curatorFramework,
			ConnectionState connectionState) {
		if (connectionState == ConnectionState.LOST) {
			while (true) {
				try {
					// 手动重连
					log.info("触发重连");
					boolean flag = curatorFramework.getZookeeperClient()
							.blockUntilConnectedOrTimedOut();
					log.info("重新连接:" + (flag ? "成功" : "失败"));

					if (flag) {
						// 重连之后(如果session失效了[一般都会失效]，watch和临时节点需要重新添加，
						// 因此，可以对关键节点最监听，每一个节点一个listener，重连之后可以重新注册，不过要注意顺序临时节点删除的问题，不然listener会一直在列表里面)
						// 创建节点后应该绑定listener并且把节点路径和watch传过来
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (connectionState == ConnectionState.RECONNECTED) {
			log.info("执行了RECONNECTED");
			// 重新连接成功
		} else if (connectionState == ConnectionState.SUSPENDED) {
			// 自动重连,自动新建 schedular的临时节点
			log.info("执行了SUSPENDED");
		}
	}

}
