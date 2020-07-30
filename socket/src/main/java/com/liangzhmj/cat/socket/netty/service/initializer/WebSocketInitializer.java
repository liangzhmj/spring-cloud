package com.liangzhmj.cat.socket.netty.service.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.AllArgsConstructor;

/**
 * websocket的初始化设置
 * @author liangzhmj
 *
 */
@AllArgsConstructor
public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {

	private String port;
	//这里不能直接传一个对象过来，因为channel不是share的，要在ServerInitializer每个都new一个
	private String handler;

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline pipeline = sc.pipeline();
		// HttpRequestDecoder和HttpResponseEncoder的一个组合，针对http协议进行编解码
		pipeline.addLast(new HttpServerCodec());
		//ChunkedWriteHandler分块写处理，文件过大会将内存撑爆
		pipeline.addLast(new ChunkedWriteHandler());
		/**
		 * 作用是将一个Http的消息组装成一个完成的HttpRequest或者HttpResponse，那么具体的是什么
		 * 取决于是请求还是响应, 该Handler必须放在HttpServerCodec后的后面
		 */
		pipeline.addLast(new HttpObjectAggregator(8192));
		SimpleChannelInboundHandler myHandler = (SimpleChannelInboundHandler)Class.forName(handler).newInstance();
//		pipeline.addLast(myHandler);//自定义的业务handler
		pipeline.addLast(myHandler);
		//用于处理websocket, /ws为访问websocket时的uri
		pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
	}

	
	
}
