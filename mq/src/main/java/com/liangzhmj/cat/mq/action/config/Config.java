package com.liangzhmj.cat.mq.action.config;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    @NonNull
    private String name;
    private int pool = 2;
    private int interval = 500;

}
