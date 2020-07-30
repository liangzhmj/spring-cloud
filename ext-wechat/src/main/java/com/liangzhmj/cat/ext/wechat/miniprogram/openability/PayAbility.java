package com.liangzhmj.cat.ext.wechat.miniprogram.openability;

import com.liangzhmj.cat.ext.wechat.config.DBConfigUtils;
import com.liangzhmj.cat.ext.wechat.miniprogram.openability.protocol.PaymentProtocol;
import com.liangzhmj.cat.ext.wechat.miniprogram.openability.vo.Payment;
import com.liangzhmj.cat.ext.wechat.tools.common.WXSSLUtils;
import com.liangzhmj.cat.tools.http.HttpUtils;
import com.liangzhmj.cat.tools.security.MD5Util;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

import java.util.Map;


/**
 * 小程序开放能力 - 微信文档[小程序->开放接口->支付]
 *  -用户开放能力
 * @author liangzhmj
 * @Date 2019-09-26 16:45:12
 */
@Log4j2
public class PayAbility {

    /** 统一下单url **/
    private static String UNIFIE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    /** 订单查询url **/
    private static String ORDER_QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    /** 退款url **/
    private static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    /** 查询退款url **/
    private static String QUERY_REFUND_URL = "https://api.mch.weixin.qq.com/pay/refundquery";


    /**
     * 微信统一下单接口
     * @param appid 小程序appid
     * @param openid 用户openid
     * @param orderId 商户订单ID,要求32个字符内
     * @param desc 商品描述
     * @param fee 支付金额(分)
     * @param ip 客户端ip
     * @param payLimit 上传此参数no_credit--可限制用户不能使用信用卡支付
     * @return payment,其中orderKey是统一下单返回的参数，如果trade_type=NATIVE,扫码支付返回url，否则返回prepay_id
     */
    public static Payment unifiedOrder(String appid,String openid, String orderId, String desc, int fee, String ip,String payLimit) throws Exception{
        //0:appid,1:app密钥,2:商户号,3:商户密钥,4:支付通知路径,5:退款通知路径
        String[] config = DBConfigUtils.getSubjectConfig(appid);
        String mchSecret = StringUtils.getCleanString(config[3]);
        //组装参数
        Payment payment = new Payment(appid, StringUtils.getCleanString(config[2]),mchSecret,
                        StringUtils.getCleanString(config[4]),openid,orderId,desc,ip,fee,payLimit);
        //组装协议xml
        String xml = PaymentProtocol.unifiedOrder(payment);
        //请求微信统一下单
        String resp = HttpUtils.post(UNIFIE_ORDER_URL,null, xml.getBytes("UTF-8"), 10000);
        String orderKey = PaymentProtocol.unifiedOrderRes(resp);
        payment.setOrderKey(orderKey);//把结果加到payment中
        return payment;
    }

    /**
     * 返回给小程序调起收银台的参数
     * @param payment
     * @return {appid:'wx6bd8c0493bf422fb',tempStamp:'1570847219',nonceStr:'eadad2a492daf38f91c4361d3cffba32',package:'prepay_id=wx2017033010242291fcfe0db70013231072',signType:'MD5',paySign:'22D9B4E54AB1950F51E0649E8810ACD6'}
     */
    public static JSONObject requestPayment(Payment payment){
        return requestPayment(payment.getAppid(),payment.getMchSecret(),payment.getSignType(),payment.getNonceStr(),payment.getOrderKey());
    }

    /**
     * 返回给小程序调起收银台的参数
     * @param appid
     * @param mchSecret
     * @param signType
     * @param nonceStr
     * @param prepayId
     * @return {appid:'wx6bd8c0493bf422fb',tempStamp:'1570847219',nonceStr:'eadad2a492daf38f91c4361d3cffba32',package:'prepay_id=wx2017033010242291fcfe0db70013231072',signType:'MD5',paySign:'22D9B4E54AB1950F51E0649E8810ACD6'}
     */
    public static JSONObject requestPayment(String appid,String mchSecret,String signType,String nonceStr,String prepayId){
        //时间戳
        String timeStamp = String.valueOf((System.currentTimeMillis()/1000));
        StringBuilder beSign = new StringBuilder("appId=").append(appid)
                .append("&nonceStr=").append(nonceStr)
                .append("&package=prepay_id=").append(prepayId)
                .append("&signType=MD5&timeStamp=").append(timeStamp)
                .append("&key=").append(mchSecret);
        String sign = MD5Util.calc(beSign.toString());//签名
        JSONObject pay = new JSONObject();
        pay.put("appid", appid);
        pay.put("timeStamp", timeStamp);
        pay.put("nonceStr", nonceStr);
        pay.put("package", "prepay_id="+prepayId);
        pay.put("signType", signType);
        pay.put("paySign", sign);
        return pay;
    }

    /**
     * 主动查询订单
     * @param appid
     * @param orderId
     * @return
     *   trade_state:SUCCESS—支付成功，REFUND—转入退款，NOTPAY—未支付，CLOSED—已关闭，REVOKED—已撤销（刷卡支付），USERPAYING--用户支付中，PAYERROR--支付失败(其他原因，如银行返回失败)
     *   transaction_id：微信单号
     *   out_trade_no：商户单号
     * @throws Exception
     */
    public static Map<String,String> orderQuery(String appid, String orderId) throws Exception{
        //0:appid,1:app密钥,2:商户号,3:商户密钥,4:支付通知路径,5:退款通知路径
        String[] config = DBConfigUtils.getSubjectConfig(appid);
        String mchSecret = StringUtils.getCleanString(config[3]);
        //组装协议xml
        String xml = PaymentProtocol.orderQuery(appid,StringUtils.getCleanString(config[2]),mchSecret,orderId);
        //请求微信统一下单
        String resp = HttpUtils.post(ORDER_QUERY_URL,null, xml.getBytes("UTF-8"), 10000);
        //解析返回结果
        return PaymentProtocol.orderQueryRes(resp);
    }

    /**
     * 申请退款
     * @param appid
     * @param orderId
     * @param rfee
     * @return
     * @throws Exception
     */
    public static Map<String,String> refund(String appid,String orderId,int fee,int rfee) throws Exception{
        //0:appid,1:app密钥,2:商户号,3:商户密钥,4:支付通知路径,5:退款通知路径
        String[] config = DBConfigUtils.getSubjectConfig(appid);
        String mchId = StringUtils.getCleanString(config[2]);
        String mchSecret = StringUtils.getCleanString(config[3]);
        //组装参数
        Payment payment = new Payment(appid, StringUtils.getCleanString(config[2]),mchSecret,
                StringUtils.getCleanString(config[5]),orderId,fee);
        String xml = PaymentProtocol.refund(payment,rfee);
        String resp = WXSSLUtils.request(REFUND_URL,mchId,xml.getBytes("UTF-8"));
        return PaymentProtocol.refundRes(resp);
    }

    /**
     * 主动查询退款详情
     * @param appid
     * @param refundId
     * @return
     *   result_code:SUCCESS退款申请接收成功，结果通过退款查询接口查询，FAIL 提交业务失败
     *   transaction_id：微信单号
     *   out_trade_no：商户单号
     *   out_refund_no：商户退款单号
     *   refund_id：微信退款单号
     *   refund_fee：退款金额
     * @throws Exception
     */
    public static Map<String,String> refundQuery(String appid, String refundId) throws Exception{
        //组装协议xml
        String xml = PaymentProtocol.refundQuery(appid,refundId);
        //请求微信查询退款详情
        String resp = HttpUtils.post(QUERY_REFUND_URL,null, xml.getBytes("UTF-8"), 10000);
        //解析返回结果
        return PaymentProtocol.refundRes(resp);
    }


}
