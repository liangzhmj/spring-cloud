**cat-ext-wechat模块**
    简介
        微信公众接口模块
    参考配置

```yml
wechat:
	redisKey:
		#redis的key的前缀，考虑redis多项目共用
		prefix: GLOBAL_	
		#微信第三方平台配置，redis存储预授权码的前缀
		thirdPreCode: THIRD_PRE_CODE_	
		#微信第三方平台配置，redis存储第三方平台token的前缀
		componentToken: COMPONENT_TOKEN_
		#微信第三方平台配置，redis存储15分钟刷新一遍的ticket的前缀
		verifyTicket: VERIFY_TICKET_
		#微信第三方平台配置，redis存储被授权应用的token的前缀
		authToken: AUTH_TOKEN_
		#微信第三方平台配置，redis存储被授权应用的刷新token的前缀
		authRefreshToken: AUTH_REFRESH_TOKEN_
		#redis存储微信公众号的token的前缀
		token: TOKEN_
	#证书路径前缀
	certPathPrefix: /home/www/project/cert/
    #退款单号后缀
	rorderSuffix: _REFUND   
	#微信第三方平台配置，获取被授权号token路径
	ctokenUrl: http://xxx/realSync151?method=m-token
	#微信第三方平台配置，获取第三方平台token的路径
	mtokenUrl: http://xxx/realSync151?method=c-token
```

**建表sql**

这表是配置表，小程序公众号的配置都放这个表，<font color="red">必选</font>

```mysql
CREATE TABLE `t_wechat_subject` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `thirdAppid` varchar(30) DEFAULT NULL COMMENT '第三平台appId',
  `name` varchar(50) DEFAULT NULL COMMENT '小程序名称',
  `gh_id` varchar(50) DEFAULT NULL,
  `appid` varchar(30) NOT NULL COMMENT 'appId',
  `secret` varchar(50) NOT NULL COMMENT '密钥',
  `mchId` varchar(30) DEFAULT NULL COMMENT '商户号',
  `msecret` varchar(50) DEFAULT NULL COMMENT '商户密钥',
  `paySyncUrl` varchar(255) DEFAULT NULL COMMENT '支付同步路径',
  `refunSyncUrl` varchar(255) DEFAULT NULL COMMENT '退款同步路径',
  `extData` varchar(255) DEFAULT NULL COMMENT '扩展字段',
  `isDel` varchar(2) DEFAULT '0' COMMENT '1:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `appId` (`appid`),
  KEY `thirdAppId` (`thirdAppid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8
```

这个表是第三方平台配置，系统中可以允许多个平台共存，<font color="red">使用第三方平台的时候需要用到词表</font>

```mysql
CREATE TABLE `t_wechat_third` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `appid` varchar(20) NOT NULL COMMENT 'appId',
  `name` varchar(20) DEFAULT NULL COMMENT '平台名称',
  `secretKey` varchar(32) NOT NULL COMMENT '密钥',
  `msgToken` varchar(32) NOT NULL COMMENT '消息token',
  `msgAseKey` varchar(50) NOT NULL COMMENT '消息asekey',
  `extData` varchar(255) DEFAULT NULL COMMENT '扩展字段',
  `isDel` int(1) DEFAULT '0' COMMENT '0:禁用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `appId` (`appid`),
  KEY `isDel` (`isDel`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8
```

这个表是第三方平台素材主体授权表，<font color="red">使用第三方平台的时候需要用到词表</font>

```mysql
CREATE TABLE `t_wechat_auth` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `roleId` int(11) DEFAULT '0' COMMENT '角色ID',
  `thirdAppId` varchar(50) DEFAULT NULL COMMENT '第三方平台appId',
  `appId` varchar(50) NOT NULL COMMENT '公众号appId',
  `atype` int(2) DEFAULT '0' COMMENT '1:公众号,2:小程序',
  `username` varchar(50) DEFAULT NULL COMMENT '授权方公众号的原始ID',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `headImg` varchar(255) DEFAULT NULL COMMENT '头像',
  `qrcode` varchar(255) DEFAULT NULL COMMENT '二维码',
  `serviceType` int(2) DEFAULT '0' COMMENT '授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号',
  `verifyType` int(2) DEFAULT '0' COMMENT '授权方认证类型，-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证',
  `refreshToken` varchar(150) DEFAULT NULL COMMENT '刷新令牌',
  `principalName` varchar(80) DEFAULT NULL COMMENT '公众号的主体名称',
  `alias` varchar(100) DEFAULT NULL COMMENT '授权方公众号所设置的微信号，可能为空',
  `authStatus` int(2) DEFAULT '0' COMMENT '0:未授权,1:已授权,2:取消授权',
  `authTime` varchar(25) DEFAULT NULL COMMENT '授权时间',
  `unauthTime` varchar(25) DEFAULT NULL COMMENT '取消授权时间',
  `tags` varchar(100) DEFAULT '1' COMMENT '标签',
  `codeVersion` varchar(100) DEFAULT NULL COMMENT '代码版本(小程序)',
  `auditVersion` varchar(100) DEFAULT NULL COMMENT '审核版本(小程序)',
  `issueVersion` varchar(100) DEFAULT NULL COMMENT '发布版本(小程序)',
  `auditStatus` int(2) DEFAULT '0' COMMENT '-1:审核不通过,0:未提交,1:审核中,2:审核通过,3:发布',
  `auditId` int(11) DEFAULT '0' COMMENT '最新审核ID',
  `isDel` int(2) DEFAULT '0' COMMENT '1:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `appId` (`appId`,`thirdAppId`),
  KEY `atype` (`atype`),
  KEY `authStatus` (`authStatus`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8
```



