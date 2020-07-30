**cat-socket模块**
    简介
        该模块暂时包括netty模块，通过注解引入
        注解包括
        **@EnableNettyServer**   #在配置文件中指定相关参数（特别是handler），通过handler中的回调方法进行业务处理,ServerChannelContexts工具类对对应客户端ctx进程存储
        **@EnableNettyClient**   #在配置文件中指定相关参数（特别是handler），通过handler中的回调方法进行业务处理,ClientChannelContexts工具类对对应服务端ctx进程存储

```yml
	socket:
      netty:
        server:
          configs:
            - port: 9888  #端口
              pgSize: 5  #父组线程池大小
              cgSize: 5  #子组线程池大小
              handler: com.api.netty.server.InboundHandler9888  #handler实现类
              type: websocket
            - port: 9889
              pgSize: 6
              cgSize: 6
              handler: com.api.netty.server.InboundHandler9889
        client:
          configs:
            - host: 127.0.0.1
              port: 9888
              handler: com.api.netty.client.InboundHandler9888
            - host: 127.0.0.1
              port: 9889
              handler: com.api.netty.client.InboundHandler9889
```
**附录**

服务器

```java
/**
 * 服务器端，数据进站事件(logstash)
 * @author liangzhmj
 *
 */
@Log4j2
public class InboundHandler9889 extends ServerInboundHandler {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
			throws Exception {//触发读事件时调用的方法
		log.info("9889收到客户端信息："+msg);
		ctx.writeAndFlush("9889复读:"+msg);
	}
}
```

客户端

```java
/**
 * 客户端端，数据进站事件
 * @author liangzhmj
 *
 */
@Log4j2
public class InboundHandler9888 extends ClientInboundHandler {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)//注意这个有可能出现数据不完整(粘包，切包)的情况，要处理这种情况
			throws Exception {//触发读事件时调用的方法
		log.info("服务器9888:"+msg);
	}
}
```

