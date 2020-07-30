package com.liangzhmj.cat.ext.wechat.tools.common.vo;


import lombok.Data;

@Data
public class Article {
	
	private String title;
	private String digest;
	private String content;
	private String author;
	private String csurl;
	private int needOpenComment;
	private int onlyFansComment;


}
