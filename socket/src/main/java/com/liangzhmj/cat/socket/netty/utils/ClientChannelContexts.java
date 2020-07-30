package com.liangzhmj.cat.socket.netty.utils;

import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * netty客户端->服务器的ctx
 * @author liangzhmj
 */
public class ClientChannelContexts {

	/** 记录服务器对应的ctx **/
	private static final Map<String,ChannelHandlerContext> chcs = new ConcurrentHashMap<String,ChannelHandlerContext>();

	/**
	 * 根据服务器的地址key获取ctx
	 * @param addr
	 * @return
	 */
	public static ChannelHandlerContext getServerCtx(@NonNull String addr){
		return chcs.get(addr);
	}
	/**
	 * 根据服务器的地址key发送msg
	 * @param addr
	 * @param msg
	 * @return
	 */
	public static void sendMsg(@NonNull String addr,@NonNull String msg){
		ChannelHandlerContext ctx = chcs.get(addr);
		ctx.writeAndFlush(msg);
	}

	/**
	 * 根据服务器的地址移除ctx
	 * @param addr
	 */
	public static void removeServerCtx(@NonNull String addr){
		chcs.remove(addr);
	}
	
	/**
	 * 根据服务器的地址key设置ctx
	 * @param addr
	 * @param ctx
	 */
	public static void addServerCtx(@NonNull String addr,@NonNull ChannelHandlerContext ctx){
		chcs.put(addr, ctx);
	}
	
	/**
	 * 获取全部记录的ctx
	 * @return
	 */
	public static Collection<ChannelHandlerContext> getAllServerCtx(){
		return chcs.values();
	}

	/**
	 * 获取连接服务器的个数
	 * @return
	 */
	public static int getServerSize(){
		return chcs.size();
	}
	
	/**
	 * 清空存储的服务器ctx
	 */
	public static void clearServerCtxs(){
		chcs.clear();
	}

}
