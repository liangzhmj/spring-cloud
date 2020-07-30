package com.liangzhmj.cat.tools.zookeeper.annotation;


import com.liangzhmj.cat.tools.zookeeper.config.ZookeeperConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ZookeeperConfig.class)
public @interface EnableZookeeper {

}
