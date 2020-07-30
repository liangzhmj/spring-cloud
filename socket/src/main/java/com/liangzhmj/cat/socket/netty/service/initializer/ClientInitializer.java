package com.liangzhmj.cat.socket.netty.service.initializer;

import com.liangzhmj.cat.socket.netty.service.handler.ClientInboundHandler;
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
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

	//这里不能直接传一个对象过来，因为channel不是share的，要在ServerInitializer每个都new一个
	private String handler;

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline pipeline = sc.pipeline();
//		pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//		pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
//		pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));//编码和解码必须设定，不然读不到信息
//		pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
		pipeline.addLast(new StringEncoder());
		pipeline.addLast(new StringDecoder());//编码和解码必须设定，不然读不到信息
		ClientInboundHandler myHandler = (ClientInboundHandler)Class.forName(handler).newInstance();
		pipeline.addLast(myHandler);
	}

	
	
}
