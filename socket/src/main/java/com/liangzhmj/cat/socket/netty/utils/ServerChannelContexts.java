package com.liangzhmj.cat.socket.netty.utils;

import com.liangzhmj.cat.tools.collection.CollectionUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * netty服务器->客户端的ctx
 * @author liangzhmj
 */
public class ServerChannelContexts {

	private static final Map<String,Map<String,ChannelHandlerContext>> sclients = new ConcurrentHashMap<>();

	/**
	 * 根据服务器端口和客户端的地址获取ctx
	 * @param port 服务器端口（识别服务器）
	 * @param addr 客户端地址（识别客户端）
	 * @return
	 */
	public static ChannelHandlerContext getClientCtx(@NonNull String port, @NonNull String addr){
		Map<String,ChannelHandlerContext> clients = sclients.get(port);
		if(CollectionUtils.isEmpty(clients)){
			return null;
		}
		return clients.get(addr);
	}
	/**
	 * 根据服务器端口和客户端的地址获取发送消息
	 * @param port 服务器端口（识别服务器）
	 * @param key 客户端地址（识别客户端）
	 * @param msg 消息
	 * @return
	 */
	public static void sendMsg(@NonNull String port, @NonNull String key,@NonNull String msg){
		Map<String,ChannelHandlerContext> clients = sclients.get(port);
		if(CollectionUtils.isEmpty(clients)){
			return ;
		}
		ChannelHandlerContext ctx = clients.get(key);
		ctx.writeAndFlush(msg);
	}

	/**
	 * 根据服务器端口和客户端的地址移除ctx
	 * @param port 服务器端口（识别服务器）
	 * @param key 客户端地址（识别客户端）
	 */
	public static void removeClientCtx(@NonNull String port, @NonNull String key){
		Map<String,ChannelHandlerContext> clients = sclients.get(port);
		if(CollectionUtils.isEmpty(clients)){
			return ;
		}
		clients.remove(key);
	}
	
	/**
	 * 根据服务器端口和客户端的地址key设置ctx
	 * @param port 服务器端口（识别服务器）
	 * @param key 客户端地址（识别客户端）
	 * @param ctx
	 */
	public static void addClientCtx(@NonNull String port, @NonNull String key, @NonNull ChannelHandlerContext ctx){
		Map<String,ChannelHandlerContext> clients = sclients.get(port);
		if(CollectionUtils.isEmpty(clients)){
			clients = new ConcurrentHashMap();
		}
		ChannelHandlerContext del = clients.remove(key);
		if(del != null && del.channel() != null && del.channel().isActive()){//之前这个key就有，可能是失败重连,如果还没关闭，则需要关闭
			try {
				del.channel().close();
			} catch (Exception e) {
			}
		}
		clients.put(key, ctx);
		sclients.put(port,clients);
	}
	
	/**
	 * 根据服务器端口获取全部记录的ctx
	 * @param port 服务器端口（识别服务器）
	 * @return
	 */
	public static Collection<ChannelHandlerContext> getAllClientCtxs(@NonNull String port){
		Map<String,ChannelHandlerContext> clients = sclients.get(port);
		if(CollectionUtils.isEmpty(clients)){
			return null;
		}
		return clients.values();
	}

	/**
	 * 根据服务器端口获取连接客户端的个数
	 * @param port 服务器端口（识别服务器）
	 * @return
	 */
	public static int getClientSize(@NonNull String port){
		Map<String,ChannelHandlerContext> clients = sclients.get(port);
		if(CollectionUtils.isEmpty(clients)){
			return 0;
		}
		return clients.size();
	}
	
	/**
	 * 根据服务器端口清空存储的客户端ctx
	 * @param port 服务器端口（识别服务器）
	 */
	public static void clearClientCtxs(@NonNull String port){
		Map<String,ChannelHandlerContext> clients = sclients.get(port);
		if(CollectionUtils.isEmpty(clients)){
			return ;
		}
		clients.clear();
	}
	/**
	 * 清空存储的客户端ctx
	 */
	public static void clearAllClientCtxs(){
		sclients.clear();
	}

}
