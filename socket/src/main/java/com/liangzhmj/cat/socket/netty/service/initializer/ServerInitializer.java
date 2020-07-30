package com.liangzhmj.cat.socket.netty.service.initializer;

import com.liangzhmj.cat.socket.netty.service.handler.ServerInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.AllArgsConstructor;

/**
 * 服务器初始化设置
 * @author liangzhmj
 *
 */
@AllArgsConstructor
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

	private String port;
	//这里不能直接传一个对象过来，因为channel不是share的，要在ServerInitializer每个都new一个
	private String handler;

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline pipeline = sc.pipeline();
//		pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));//编码和解码必须设定，不然读不到信息
//		pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
//		pipeline.addLast(new ServerInboundHandler());//inbound要放在outbound之后，执行信息是配置顺序的顺序
//		pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
//		pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
		pipeline.addLast(new StringEncoder());
		pipeline.addLast(new StringDecoder());//增加解码器
		ServerInboundHandler myHandler = (ServerInboundHandler)Class.forName(handler).newInstance();
		//设置端口
		myHandler.setPort(port);
		pipeline.addLast(myHandler);
	}

	
	
}
