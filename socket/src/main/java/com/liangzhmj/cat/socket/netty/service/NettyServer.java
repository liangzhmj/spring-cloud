package com.liangzhmj.cat.socket.netty.service;

import com.liangzhmj.cat.socket.netty.service.initializer.ServerInitializer;
import com.liangzhmj.cat.socket.netty.service.initializer.WebSocketInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * netty服务器
 * @author liangzhmj
 *
 */
@Log4j2
@AllArgsConstructor
public class NettyServer implements Runnable{

	private int port;
	private int pgSize;
	private int cgSize;
	private String handler;//这里不能直接传一个对象过来，因为channel不是share的，要在ServerInitializer每个都new一个
	private String type;

	@Override
	public void run() {
		//要两个group去管理channel
		//用于接收发来的连接请求
		EventLoopGroup parentGroup = null;
		if(pgSize > 0){
			parentGroup = new NioEventLoopGroup(pgSize);/**用于分配处理业务线程的线程池大小 */
		}else{
			parentGroup = new NioEventLoopGroup();/**用于分配处理业务线程的线程池大小 */
		}
		//用于处理parent接受并且注册给worker的连接中的信息
		EventLoopGroup childGroup = null;
		if(cgSize > 0){
			childGroup = new NioEventLoopGroup(cgSize);/** 业务出现线程池大小*/
		}else{
			childGroup = new NioEventLoopGroup();/** 业务出现线程池大小*/
		}
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			//设置父子组
			bootstrap.group(parentGroup, childGroup)
					//加大channelRead0每次接收的数据了(不够的话，会分多次接收，这样得把包进行标记，把多次的残包进行粘合)
					.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
					//服务器的channel类型
					.channel(NioServerSocketChannel.class);
			if("websocket".equals(type)){//websocket
				log.info("["+port+"]为websocket服务器");
				bootstrap.childHandler(new WebSocketInitializer(String.valueOf(port),handler));
			}else{//默认socket
				bootstrap.childHandler(new ServerInitializer(String.valueOf(port),handler));
			}
			//服务器绑定端口监听
			ChannelFuture f = bootstrap.bind(port).sync();
			log.info("netty服务启动: [" + port + "]");
			//监听服务器关闭监听（会在channel关闭的时候调用，不然阻塞,closeFuture返回的ChannelFuture和上面的f不是同一个对象）
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			log.error(e);
		} finally {
			try {
				parentGroup.shutdownGracefully();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				childGroup.shutdownGracefully();
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

}
