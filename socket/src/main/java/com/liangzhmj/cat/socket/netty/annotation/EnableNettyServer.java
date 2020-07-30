package com.liangzhmj.cat.socket.netty.annotation;


import com.liangzhmj.cat.socket.netty.config.NettyServerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NettyServerConfig.class)
public @interface EnableNettyServer {
}
