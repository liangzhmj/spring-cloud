package com.liangzhmj.cat.ext.wechat.miniprogram.openability.vo;

import com.liangzhmj.cat.tools.security.MD5Util;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Log4j2
@Data
public class Payment {

	/** 小程序的appid **/
	private String appid;
	/** 商户号 **/
    private String mchId;
	/** 商户密钥 **/
    private String mchSecret;
    /** 用户openid **/
    private String openid;
    /** 随机字符串 **/
    private String nonceStr;
    /** 签名类型，默认为MD5 **/
    private String signType = "MD5";
    /** 小程序取值如下：JSAPI **/
    private String tradeType = "JSAPI";
    /** 商品描述 **/
    private String desc;
    /** 商户订单号 **/
    private String orderId;
    /** 上传此参数no_credit--可限制用户不能使用信用卡支付 **/
    private String limitPay;
    /** 客户端ip **/
    private String ip;
    /** 交易金额（分） **/
    private int fee;
	/** 支付同步路径 **/
    private String syncUrl;
    /** 解析统一下单返回的数据，如果trade_type=NATIVE,扫码支付返回url，否则返回prepay_id **/
    private String orderKey;

    /**
     * 统一下单用
     * @param appid
     * @param mchId
     * @param mchSecret
     * @param syncUrl
     * @param openid
     * @param orderId
     * @param desc
     * @param ip
     * @param fee
     */
    public Payment(String appid,String mchId,String mchSecret,String syncUrl,String openid,String orderId,String desc,String ip,int fee){
        this.appid = appid;
        this.mchId = mchId;
        this.mchSecret = mchSecret;
        this.syncUrl = syncUrl;
        this.openid = openid;
        this.orderId = orderId;
        this.desc = StringUtils.substring(25,desc);
        this.ip = ip;
        this.fee = fee;
        this.nonceStr = UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

    /**
     * 统一下单用
     * @param appid
     * @param mchId
     * @param mchSecret
     * @param syncUrl
     * @param openid
     * @param orderId
     * @param desc
     * @param ip
     * @param fee
     * @param limitPay
     */
    public Payment(String appid,String mchId,String mchSecret,String syncUrl,String openid,String orderId,String desc,String ip,int fee,String limitPay){
        this.appid = appid;
        this.mchId = mchId;
        this.mchSecret = mchSecret;
        this.syncUrl = syncUrl;
        this.openid = openid;
        this.orderId = orderId;
        this.desc = StringUtils.substring(25,desc);
        this.ip = ip;
        this.fee = fee;
        this.limitPay = limitPay;
        this.nonceStr = UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

    /**
     * 退款用
     * @param appid
     * @param mchId
     * @param mchSecret
     * @param syncUrl
     * @param orderId
     * @param fee
     */
    public Payment(String appid,String mchId,String mchSecret,String syncUrl,String orderId,int fee){
        this.appid = appid;
        this.mchId = mchId;
        this.mchSecret = mchSecret;
        this.syncUrl = syncUrl;
        this.orderId = orderId;
        this.fee = fee;
        this.nonceStr = UUID.randomUUID().toString().trim().replaceAll("-", "");
    }



    /**
     * 微信支付签名
     * @return
     */
    public String sign(){
        //字典排序
        StringBuilder signStr = new StringBuilder("appid=").append(this.appid);
        signStr.append("&body=").append(desc);
        if(!StringUtils.isEmpty(limitPay)){
            signStr.append("&limit_pay=").append(limitPay);//上传此参数no_credit--可限制用户不能使用信用卡支付
        }
        signStr.append("&mch_id=").append(this.mchId)
                .append("&nonce_str=").append(nonceStr)
                .append("&notify_url=").append(this.syncUrl)
                .append("&openid=").append(this.openid)
                .append("&out_trade_no=").append(orderId)
                .append("&sign_type=").append(getSignType())
                .append("&spbill_create_ip=").append(ip)
                .append("&total_fee=").append(fee)
                .append("&trade_type=").append(getTradeType())
                .append("&key=").append(this.mchSecret);
        String sign = MD5Util.calc(signStr.toString()).toUpperCase();
        log.info("支付单【"+orderId+"】待签名信息:"+signStr+"  签名后-->"+sign);
        return sign;
    }

	
}
