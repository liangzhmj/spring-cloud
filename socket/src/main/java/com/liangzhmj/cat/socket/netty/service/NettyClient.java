package com.liangzhmj.cat.socket.netty.service;

import com.liangzhmj.cat.socket.netty.service.initializer.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;

import java.net.InetSocketAddress;

/**
 * netty客户端
 * @author liangzhmj
 *
 */
@AllArgsConstructor
public class NettyClient implements Runnable{

	private String host;
	private int port;
	private String handler;//这里不能直接传一个对象过来，因为channel不是share的，要在ServerInitializer每个都new一个

	@Override
	public void run() {
		//客户端一个group去管理channel
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			//设置组
			bootstrap.group(group)
					//服务器的channel类型
					.channel(NioSocketChannel.class)
					//设置初始化程序
					.handler(new ClientInitializer(handler));
			//连接服务器
			ChannelFuture cf = bootstrap.connect(new InetSocketAddress(host ,port)).sync();
			//监听服务器关闭监听（会在channel关闭的时候调用，不然阻塞,closeFuture返回的ChannelFuture和上面的f不是同一个对象）
			cf.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}