package com.liangzhmj.cat.socket.netty.config.vo;

import lombok.Data;

@Data
public class Config {
    private String host;
    private int port;
    private int pgSize;
    private int cgSize;
    //虽然这个字段是非空的，但是还是不能加lombok的@NonNull，因为加了的话，默认构造方法就没有了，生成一个@NonNull String handler的带参构造方法
    //导致application注入的时候没有默认构造方法，注入失败
    private String handler;
    private String type;//区别是普通socket，还是websocket
}
