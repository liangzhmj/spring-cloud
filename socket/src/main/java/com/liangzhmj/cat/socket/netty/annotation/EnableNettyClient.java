package com.liangzhmj.cat.socket.netty.annotation;


import com.liangzhmj.cat.socket.netty.config.NettyClientConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NettyClientConfig.class)
public @interface EnableNettyClient {
}
