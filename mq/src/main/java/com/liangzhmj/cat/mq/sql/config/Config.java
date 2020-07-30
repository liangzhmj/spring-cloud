package com.liangzhmj.cat.mq.sql.config;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    @NonNull
    private String name;//队列名称
    @NonNull
    private String db;
    private int max = 100;//一次性最大处理sql语句数
    private int pool1 = 2;
    private int pool2 = 1;
    private int interval = 300;//等待队列获取sql最大时间间隔
    private String failFilePath = "/";//失败语句保存路径

}
