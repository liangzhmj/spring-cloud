package com.liangzhmj.cat.socket.netty.service.handler;

import com.liangzhmj.cat.socket.netty.utils.ServerChannelContexts;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


/**
 * 服务器端，数据进站事件
 * @author liangzhmj
 */
@Log4j2
@Setter
public abstract class ServerInboundHandler extends SimpleChannelInboundHandler<String> {

	//在ServerInitializer被赋值
	protected String port;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {//建立连接时触发的方法
		Channel channel = ctx.channel();
		log.info("与客户端["+channel.remoteAddress()+"]建立连接");
		String addr = channel.remoteAddress().toString();
		ServerChannelContexts.addClientCtx(port,addr, ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		//移除客户端
		Channel channel = ctx.channel();
		String addr = channel.remoteAddress().toString();
		ServerChannelContexts.removeClientCtx(port,addr);
		channel.close();
		log.info("与客户端["+addr+"]移除连接");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		log.error("服务器检测到异常:"+cause.getMessage());
	}

	
}
