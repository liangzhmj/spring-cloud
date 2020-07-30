package com.liangzhmj.cat.socket.netty.service.handler;

import com.liangzhmj.cat.socket.netty.utils.ClientChannelContexts;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;


/**
 * 客户端，数据进站事件
 * @author liangzhmj
 *
 */
@Log4j2
public abstract class ClientInboundHandler extends SimpleChannelInboundHandler<String> {
	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {//建立连接时触发的方法
		Channel channel = ctx.channel();
		log.info("与服务器["+channel.remoteAddress()+"]建立连接");
		//客户端注册
		String addr = channel.remoteAddress().toString();
		ClientChannelContexts.addServerCtx(addr, ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {//可以在这里做失败重连处理策略
		//移除客户端
		Channel channel = ctx.channel();
		String addr = channel.remoteAddress().toString();
		ClientChannelContexts.removeServerCtx(addr);
		channel.close();
		log.info("与服务器["+addr+"]移除连接");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		log.error("客户端检测到异常:"+cause.getMessage());
	}

}
