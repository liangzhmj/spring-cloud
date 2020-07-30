package com.liangzhmj.cat.ext.wechat.miniprogram.openability.protocol;

import com.liangzhmj.cat.ext.wechat.config.ConfigContext;
import com.liangzhmj.cat.ext.wechat.config.DBConfigUtils;
import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.miniprogram.openability.vo.Payment;
import com.liangzhmj.cat.tools.security.MD5Util;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * 支付协议
 * @author liangzhmj
 */
@Log4j2
public class PaymentProtocol {

    /**
     * 统一下单
     * @return
     */
    public static String unifiedOrder(Payment payment){
        /**
         * <xml>
         <appid>wx2421b1c4370ec43b</appid>
         <body>JSAPI支付测试</body>
         <limit_pay>no_credit</limit_pay>
         <mch_id>10000100</mch_id>
         <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
         <notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>
         <openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid>
         <out_trade_no>1415659990</out_trade_no>
         <spbill_create_ip>14.23.150.211</spbill_create_ip>
         <total_fee>1</total_fee>
         <trade_type>JSAPI</trade_type>
         <sign>0CB01533B8C1EF103065174F50BCA001</sign>
         </xml>
         */
        StringBuilder message = new StringBuilder();
        message.append("<xml>")
                .append("<appid>").append(payment.getAppid()).append("</appid>")
                .append("<body><![CDATA[").append(payment.getDesc()).append("]]></body>");
        if(!StringUtils.isEmpty(payment.getLimitPay())){
            message.append("<limit_pay>").append(payment.getLimitPay()).append("</limit_pay>");
        }
        message.append("<mch_id>").append(payment.getMchId()).append("</mch_id>")
                .append("<nonce_str>").append(payment.getNonceStr()).append("</nonce_str>")
                .append("<notify_url>").append(payment.getSyncUrl()).append("</notify_url>")
                .append("<openid>").append(payment.getOpenid()).append("</openid>")
                .append("<out_trade_no>").append(payment.getOrderId()).append("</out_trade_no>")
                .append("<sign_type>").append(payment.getSignType()).append("</sign_type>")
                .append("<spbill_create_ip>").append(payment.getIp()).append("</spbill_create_ip>")
                .append("<total_fee>").append(payment.getFee()).append("</total_fee>")
                .append("<trade_type>").append(payment.getTradeType()).append("</trade_type>")
                .append("<sign>").append(payment.sign()).append("</sign>")
                .append("</xml>");
        log.info("支付单【"+payment.getOrderId()+"】 统一下单:\n"+message);
        return message.toString();
    }

    /**
     * 解析统一下单支付返回结果
     * @param result
     * @return 如果trade_type=NATIVE,扫码支付返回url，否则返回prepay_id
     */
    public static String unifiedOrderRes(String result) throws Exception{
        /**
         <xml>
         <return_code><![CDATA[SUCCESS]]></return_code>
         <return_msg><![CDATA[OK]]></return_msg>
         <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
         <mch_id><![CDATA[10000100]]></mch_id>
         <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>
         <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>
         <result_code><![CDATA[SUCCESS]]></result_code>
         <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>
         <trade_type><![CDATA[JSAPI]]></trade_type>
         </xml>
         */
        if(StringUtils.isEmpty(result)){
            throw new WechatException("微信支付异常");
        }
        log.info("统一下单返回结果:"+result);
        Document document = DocumentHelper.parseText(result);
        Element rq = document.getRootElement();
        String returnCode = rq.element("return_code").getText();
        String returnMsg = rq.element("return_msg").getText();
        if(!"SUCCESS".equals(returnCode)){//通讯是否成功
            throw new WechatException("请求微信-通信标识失败："+returnMsg);
        }
        String resultCode = rq.element("result_code").getText();
        if(!"SUCCESS".equals(resultCode)){
            throw new WechatException("请求微信-交易失败："+returnMsg);
        }
        //验证签名
        String sign = rq.elementText("sign");
        String tradeType = rq.elementText("trade_type");
        String appid = rq.elementText("appid");
        String[] configs = DBConfigUtils.getSubjectConfig(appid);
        //验签
        PaymentProtocol.checkSign(rq,configs[3], sign);
        String res = null;
        if("NATIVE".equals(tradeType)){//扫描支付
            res = rq.element("code_url").getText();//返回url生成二维码让用户扫描支付
        }else{
            res = rq.element("prepay_id").getText();
        }
        if(StringUtils.isEmpty(res)){
            throw new WechatException("统一下单失败：找不到prepay_id或者code_url->");
        }
        return res;
    }



    /**
     * 拼装主动查询支付结果xml
     * @param appid
     * @param mchId
     * @param mchSecret
     * @param orderId
     * @return
     */
    public static String orderQuery(String appid,String mchId,String mchSecret,String orderId){
        /**
         *
         <xml>
         <appid>wx2421b1c4370ec43b</appid>
         <mch_id>10000100</mch_id>
         <nonce_str>ec2316275641faa3aacf3cc599e8730f</nonce_str>
         <out_trade_no>1008450740201411110005820873</out_trade_no>
         <sign>FDD167FAA73459FD921B144BAF4F4CA2</sign>
         </xml>
         */
        String nonceStr = UUID.randomUUID().toString().trim().replaceAll("-", "");
        //字典排序
        StringBuilder signStr = new StringBuilder("appid=").append(appid);
        signStr.append("&mch_id=").append(mchId)
                .append("&nonce_str=").append(nonceStr)
                .append("&out_trade_no=").append(orderId)
                .append("&key=").append(mchSecret);
        String sign = MD5Util.calc(signStr.toString()).toUpperCase();
        StringBuilder message = new StringBuilder("<xml>")
                .append("<appid>").append(appid).append("</appid>")
                .append("<mch_id>").append(mchId).append("</mch_id>")
                .append("<nonce_str>").append(nonceStr).append("</nonce_str>")
                .append("<out_trade_no>").append(orderId).append("</out_trade_no>")
                .append("<sign>").append(sign).append("</sign>")
                .append("</xml>");
        log.info("支付单【"+orderId+"】 主动查询:\n"+message);
        return message.toString();
    }
    /**
     * 解析拼装主动查询支付结果返回的结果
     * @param result
     * @return
     *   trade_state:SUCCESS—支付成功，REFUND—转入退款，NOTPAY—未支付，CLOSED—已关闭，REVOKED—已撤销（刷卡支付），USERPAYING--用户支付中，PAYERROR--支付失败(其他原因，如银行返回失败)
     *   transaction_id：微信单号
     *   out_trade_no：商户单号
     */
    public static Map<String,String> orderQueryRes(String result) throws Exception{
        /**
         *
         <xml>
         <return_code><![CDATA[SUCCESS]]></return_code>
         <return_msg><![CDATA[OK]]></return_msg>
         <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
         <mch_id><![CDATA[10000100]]></mch_id>
         <device_info><![CDATA[1000]]></device_info>
         <nonce_str><![CDATA[TN55wO9Pba5yENl8]]></nonce_str>
         <sign><![CDATA[BDF0099C15FF7BC6B1585FBB110AB635]]></sign>
         <result_code><![CDATA[SUCCESS]]></result_code>
         <openid><![CDATA[oUpF8uN95-Ptaags6E_roPHg7AG0]]></openid>
         <is_subscribe><![CDATA[Y]]></is_subscribe>
         <trade_type><![CDATA[MICROPAY]]></trade_type>
         <bank_type><![CDATA[CCB_DEBIT]]></bank_type>
         <total_fee>1</total_fee>
         <fee_type><![CDATA[CNY]]></fee_type>
         <transaction_id><![CDATA[1008450740201411110005820873]]></transaction_id>
         <out_trade_no><![CDATA[1415757673]]></out_trade_no>
         <attach><![CDATA[订单额外描述]]></attach>
         <time_end><![CDATA[20141111170043]]></time_end>
         <trade_state><![CDATA[SUCCESS]]></trade_state>
         </xml>
         */
        Document document = DocumentHelper.parseText(result);
        Element root = document.getRootElement();
        String returnCode = root.element("return_code").getText();
        if(!"SUCCESS".equals(returnCode)){
            throw new WechatException("微信支付主动查询-通信失败："+result);
        }
        /** 签名 **/
        String sign = root.element("sign").getText();
        String appid = root.element("appid").getText();
        String[] configs = DBConfigUtils.getSubjectConfig(appid);
        //解签=================================================
        return PaymentProtocol.checkSign(root,configs[3], sign);
    }

    /**
     * 申请退款
     * @param payment
     * @param rfee
     * @return
     */
    public static String refund(Payment payment,int rfee){
        /**
         *
         <xml>
         <appid>wx2421b1c4370ec43b</appid>
         <mch_id>10000100</mch_id>
         <nonce_str>6cefdb308e1e2e8aabd48cf79e546a02</nonce_str>
         <notify_url>6cefdb308e1e2e8aabd48cf79e546a02</notify_url>
         <out_refund_no>2018032618173718328R</out_refund_no>
         <out_trade_no>2018032618173718328</out_trade_no>
         <refund_fee>1</refund_fee>
         <total_fee>1</total_fee>
         <sign>FE56DD4AA85C0EECA82C35595A69E153</sign>
         </xml>
         */
        //0:appid,1:app密钥,2:商户号,3:商户密钥,4:支付通知路径,5:退款通知路径
        String rorderId = payment.getOrderId() + ConfigContext.getWechatConfig().getRorderSuffix();
        //字典排序
        StringBuilder signStr = new StringBuilder("appid=").append(payment.getAppid());
        signStr.append("&mch_id=").append(payment.getMchId())
                .append("&nonce_str=").append(payment.getNonceStr())
                .append("&notify_url=").append(payment.getSyncUrl())
                .append("&out_refund_no=").append(rorderId)
                .append("&out_trade_no=").append(payment.getOrderId())
                .append("&refund_fee=").append(payment.getFee())
                .append("&total_fee=").append(rfee)
                .append("&key=").append(payment.getMchSecret());
        String sign = MD5Util.calc(signStr.toString()).toUpperCase();
        StringBuilder message = new StringBuilder("<xml>")
                .append("<appid>").append(payment.getAppid()).append("</appid>")
                .append("<mch_id>").append(payment.getMchSecret()).append("</mch_id>")
                .append("<nonce_str>").append(payment.getNonceStr()).append("</nonce_str>")
                .append("<notify_url>").append(payment.getSyncUrl()).append("</notify_url>")
                .append("<out_refund_no>").append(rorderId).append("</out_refund_no>")
                .append("<out_trade_no>").append(payment.getOrderId()).append("</out_trade_no>")
                .append("<refund_fee>").append(payment.getFee()).append("</refund_fee>")
                .append("<total_fee>").append(rfee).append("</total_fee>")
                .append("<sign>").append(sign).append("</sign>")
                .append("</xml>");
        log.info("支付单【"+payment.getOrderId()+"】 退款:\n"+message);
        return message.toString();
    }

    /**
     * 申请退款返回结果
     * @param result
     * @return result_code SUCCESS退款申请接收成功，结果通过退款查询接口查询,FAIL 提交业务失败
     *  refund_id 微信退款单号
     *  refund_fee 应结退款金额
     */
    public static Map<String,String> refundRes(String result) throws Exception {
        /**
         <xml>
         <return_code><![CDATA[SUCCESS]]></return_code>
         <return_msg><![CDATA[OK]]></return_msg>
         <appid><![CDATA[wx6ca3648cfc22b423]]></appid>
         <mch_id><![CDATA[1494446022]]></mch_id>
         <nonce_str><![CDATA[wbRwaQVKco9dW1dj]]></nonce_str>
         <sign><![CDATA[9E2E0ED2338209D0A6F8E056BAB041C3]]></sign>
         <result_code><![CDATA[SUCCESS]]></result_code>
         <transaction_id><![CDATA[4200000064201803266007038809]]></transaction_id>
         <out_trade_no><![CDATA[2018032618173718328]]></out_trade_no>
         <out_refund_no><![CDATA[2018032618173718328GDR]]></out_refund_no>
         <refund_id><![CDATA[50000106162018032703936790864]]></refund_id>
         <refund_channel><![CDATA[]]></refund_channel>
         <refund_fee>1</refund_fee>
         <coupon_refund_fee>0</coupon_refund_fee>
         <total_fee>1</total_fee>
         <cash_fee>1</cash_fee>
         <coupon_refund_count>0</coupon_refund_count>
         <cash_refund_fee>1</cash_refund_fee>
         </xml>
         */
        if(StringUtils.isEmpty(result)){
            throw new WechatException("微信申请退款异常");
        }
        log.info("微信申请退款返回结果:"+result);
        Document document = DocumentHelper.parseText(result);
        Element root = document.getRootElement();
        String returnCode = root.element("return_code").getText();
        String returnMsg = root.elementText("return_msg");
        if(!"SUCCESS".equals(returnCode)){
            throw new WechatException("申请退款-通信失败："+returnMsg);
        }
        String resultCode = root.element("result_code").getText();
        if(!"SUCCESS".equals(resultCode)){
            String errCode = root.elementText("err_code");
            throw new WechatException("申请退款-退款失败："+errCode);
        }
        //验证签名
        String sign = root.elementText("sign");
        String appid = root.elementText("appid");
        String[] configs = DBConfigUtils.getSubjectConfig(appid);
        Map<String,String> datas = PaymentProtocol.checkSign(root,configs[3], sign);
        return datas;
    }

    /**
     * 支付同步
     * @param result
     * @return result_code：SUCCESS，FAIL
     * 必须校验金额total_fee
     * @throws Exception
     */
    public static Map<String,String> paySync(String result) throws Exception{
        /**
         * 例子
         <xml>
         <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
         <attach><![CDATA[支付测试]]></attach>
         <bank_type><![CDATA[CFT]]></bank_type>
         <fee_type><![CDATA[CNY]]></fee_type>
         <is_subscribe><![CDATA[Y]]></is_subscribe>
         <mch_id><![CDATA[10000100]]></mch_id>
         <nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str>
         <openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid>
         <out_trade_no><![CDATA[1409811653]]></out_trade_no>
         <result_code><![CDATA[SUCCESS]]></result_code>
         <return_code><![CDATA[SUCCESS]]></return_code>
         <sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign>
         <sub_mch_id><![CDATA[10000100]]></sub_mch_id>
         <time_end><![CDATA[20140903131540]]></time_end>
         <total_fee>1</total_fee>
         <coupon_fee><![CDATA[10]]></coupon_fee>
         <coupon_count><![CDATA[1]]></coupon_count>
         <coupon_type><![CDATA[CASH]]></coupon_type>
         <coupon_id><![CDATA[10000]]></coupon_id>
         <coupon_fee><![CDATA[100]]></coupon_fee>
         <trade_type><![CDATA[JSAPI]]></trade_type>
         <transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id>
         </xml>
         */
        Document document = DocumentHelper.parseText(result);
        Element root = document.getRootElement();
        String returnCode = root.element("return_code").getText();
        if(!"SUCCESS".equals(returnCode)){
            throw new WechatException("微信支付同步-通信失败："+result);
        }
        /** 签名 **/
        String sign = root.element("sign").getText();
        String appid = root.element("appid").getText();
        String[] configs = DBConfigUtils.getSubjectConfig(appid);
        //解签=================================================
        return PaymentProtocol.checkSign(root,configs[3], sign);
    }

    /**
     * 退款通知
     * @param result
     * @return
     *  transaction_id：微信订单号
     *  out_trade_no：商户订单号
     *  out_refund_no：商户退款号
     *  total_fee：订单金额
     *  refund_fee：申请退款金额
     *  settlement_refund_fee：退款金额
     *  refund_status：退款状态 SUCCESS-退款成功,CHANGE-退款异常,REFUNDCLOSE—退款关闭
     * @throws Exception
     */
    public static Map<String,String> refundSync(String result) throws Exception{
        /**
         * 例子
         <xml>
         <return_code>SUCCESS</return_code>
         <appid><![CDATA[wx6ca3648cfc22b423]]></appid>
         <mch_id><![CDATA[1494446022]]></mch_id>
         <nonce_str><![CDATA[480e8dc42cc7dea6b9ccf7f51a05e06b]]></nonce_str>
         <req_info><![CDATA[OlC+KeDftRBRCag57ifk+eSInIwvuP4F4PqCVfE0RSZ/05/OItaKO2Md/56rbo+eSHcpotzg8wMKDntDKG3S19le57uhFqeyujNZ5tYgTE7L55r4PE0mvTz4e2XOSh489lNTLlVeHdnTozbXorGtxqDzrC/FwqnexWhr3LXDseCLw/UXhCrQLDK7aRJRfVTb5quDPoj1kQ0c2pZPOCPObHghkMHmpg0o3XeGhMuFtII3rmAlaBZIK7o3atANWkrihrJYxqZhBuPCvnsFFLWThARe/qESeU7BkzJBRnpxTfEdqajwu8OKPUh7+blGgK1Zn/joBnY8GSE9IpKA6Ju9bmomg/WovGumZ++ApVvs22DAR1My+ZBul4L/hh5dNZbpj75f+TsfEVZdyZHiMu7iNdymOnYw5/dpMQMQeO9/B5t5QDUGTREksF75HGZds9j5sEueE18rhNIVIfqWvptS5l+G9O1pjbeIoq6PXY2TelOC4rvK8MbQTbOLsZ3FOvuLztMgriNHnE5ZB3YiA77XsadZBaQHCEs7iENHbiqBXImrHfETq8ZiMmJMVkX3qhLGlqIs27npv/O1imMBAb2eqlPaqPhRQ2Y4UVqBtE4hJJbotPo69S2gG+p0OslkxSt6h4QOhPpWEd5ghdfujFLh8j9NTpxik1VCqownRoUOeeSs8PXDAnbMhW8O8+hLkLbgEDkMr5TtUh2kJ7HKgMGwjZsXICbfXTk3cv8tTFgry6Xk6Az5qjU4niTROBwj847TYxDgLTqY3YGQQ71ToOzdCK5/czKX5bQlCrVmELyMVs7b8g994GsOhYNUalMV3XMfEvD9P6OXNbFBolF7h0wXRoujfontnVAvh7yw/sHRmis/phK4bB/N5V6eBFoPJ1oi8s8dC3hnsYdCwQhD2Ep0qcShPh3j9ZXmr3mTPorbK9UJvz2wdV5CgVfmg7iKjFbTrDBCUwN0vEfQnA5XLxkv1Vt6UGXrjtQSEd6RGHKf/4D2ZLn2Lqt+eT/ns8SejnYYhSsKZ3bQoGEi84OMVFufsJPzCajMFcN72g/3vmXmd6s=]]></req_info>
         </xml>
         */
        Document document = DocumentHelper.parseText(result);
        Element root = document.getRootElement();
        String returnCode = root.element("return_code").getText();
        if(!"SUCCESS".equals(returnCode)){
            throw new WechatException("申请退款-通信失败："+result);
        }
        String appid = root.elementText("appid");
        //0:appid,1:app密钥,2:商户号,3:商户密钥,4:支付通知路径,5:退款通知路径
        String[] configs = DBConfigUtils.getSubjectConfig(appid);
        /** 获取加密串 **/
        String info = root.elementText("req_info");
        //解密
        result = decryptData(info, configs[3]);
        /**
         * req_info解密后的示例：
         * <root>
         * <out_refund_no><![CDATA[131811191610442717309]]></out_refund_no>
         * <out_trade_no><![CDATA[71106718111915575302817]]></out_trade_no>
         * <refund_account><![CDATA[REFUND_SOURCE_RECHARGE_FUNDS]]></refund_account>
         * <refund_fee><![CDATA[3960]]></refund_fee>
         * <refund_id><![CDATA[50000408942018111907145868882]]></refund_id>
         * <refund_recv_accout><![CDATA[支付用户零钱]]></refund_recv_accout>
         * <refund_request_source><![CDATA[API]]></refund_request_source>
         * <refund_status><![CDATA[SUCCESS]]></refund_status>
         * <settlement_refund_fee><![CDATA[3960]]></settlement_refund_fee>
         * <settlement_total_fee><![CDATA[3960]]></settlement_total_fee>
         * <success_time><![CDATA[2018-11-19 16:24:13]]></success_time>
         * <total_fee><![CDATA[3960]]></total_fee>
         * <transaction_id><![CDATA[4200000215201811190261405420]]></transaction_id>
         * </root>
         */
        document = DocumentHelper.parseText(result);
        root = document.getRootElement();
        List<Element> els = root.elements();
        Map<String,String> temp = new HashMap<>();
        for (Element el : els) {
            String tag = el.getName();
            String value = el.getText();
            if(!"sign".equals(tag) && !StringUtils.isEmpty(value)){//签名
                temp.put(tag, value);
            }
        }
        return temp;
    }

    /**
     * 退款主动查询
     * @param appid
     * @param refundId
     * @return
     */
    public static String refundQuery(String appid,String refundId){
        /**
         *
             <xml>
             <appid>wx2421b1c4370ec43b</appid>
             <mch_id>10000100</mch_id>
             <nonce_str>0b9f35f484df17a732e537c37708d1d0</nonce_str>
             <out_refund_no></out_refund_no>
             <out_trade_no>1415757673</out_trade_no>
             <refund_id></refund_id>
             <transaction_id></transaction_id>
             <sign>66FFB727015F450D167EF38CCC549521</sign>
             </xml>
         */
        String[] configs = DBConfigUtils.getSubjectConfig(appid);
        String nonceStr = UUID.randomUUID().toString().trim().replaceAll("-", "");
        //字典排序
        StringBuilder signStr = new StringBuilder("appid=").append(appid);
        signStr.append("&mch_id=").append(configs[2])
                .append("&nonce_str=").append(nonceStr)
                .append("&refund_id=").append(refundId)
                .append("&key=").append(configs[3]);
        String sign = MD5Util.calc(signStr.toString()).toUpperCase();
        StringBuilder message = new StringBuilder("<xml>")
                .append("<appid>").append(appid).append("</appid>")
                .append("<mch_id>").append(configs[2]).append("</mch_id>")
                .append("<nonce_str>").append(nonceStr).append("</nonce_str>")
                .append("<refund_id>").append(refundId).append("</refund_id>")
                .append("<sign>").append(sign).append("</sign>")
                .append("</xml>");
        log.info("退款单【"+refundId+"】 主动查询:\n"+message);
        return message.toString();
    }

    /**
     * 解析主动查询退款结果返回的结果
     * @param result
     * @return
     *   result_code:SUCCESS退款申请接收成功，结果通过退款查询接口查询，FAIL 提交业务失败
     *   transaction_id：微信单号
     *   out_trade_no：商户单号
     *   out_refund_no：商户退款单号
     *   refund_id：微信退款单号
     *   refund_fee：退款金额
     */
    public static Map<String,String> refundQueryRes(String result) throws Exception{
        /**
         *
         <xml>
         <return_code><![CDATA[SUCCESS]]></return_code>
         <return_msg><![CDATA[OK]]></return_msg>
         <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
         <mch_id><![CDATA[10000100]]></mch_id>
         <nonce_str><![CDATA[NfsMFbUFpdbEhPXP]]></nonce_str>
         <sign><![CDATA[B7274EB9F8925EB93100DD2085FA56C0]]></sign>
         <result_code><![CDATA[SUCCESS]]></result_code>
         <transaction_id><![CDATA[1008450740201411110005820873]]></transaction_id>
         <out_trade_no><![CDATA[1415757673]]></out_trade_no>
         <out_refund_no><![CDATA[1415701182]]></out_refund_no>
         <refund_id><![CDATA[2008450740201411110000174436]]></refund_id>
         <refund_channel><![CDATA[]]></refund_channel>
         <refund_fee>1</refund_fee>
         </xml>
         */
        Document document = DocumentHelper.parseText(result);
        Element root = document.getRootElement();
        String returnCode = root.element("return_code").getText();
        if(!"SUCCESS".equals(returnCode)){
            throw new WechatException("微信退款主动查询-通信失败："+result);
        }
        /** 签名 **/
        String sign = root.element("sign").getText();
        String appid = root.element("appid").getText();
        String[] configs = DBConfigUtils.getSubjectConfig(appid);
        //解签=================================================
        return PaymentProtocol.checkSign(root,configs[3], sign);
    }



    public static String buildRes(String code,String msg){
        return "<xml>"+
                "<return_code><![CDATA["+code+"]]></return_code>"+
                "<return_msg><![CDATA["+msg+"]]></return_msg>"+
                "</xml>";
    }
    public static String buildSuccessRes(){
        return "<xml>"+
                "<return_code><![CDATA[SUCCESS]]></return_code>"+
                "<return_msg><![CDATA[OK]]></return_msg>"+
                "</xml>";
    }

    /**
     * 验签不抛异常为通过
     * @param root
     * @param mchSecret
     * @param destSign
     * @return
     */
    public static Map<String,String> checkSign(Element root, String mchSecret, String destSign){
        try {
            List<Element> els = root.elements();
            TreeMap<String,String> temp = new TreeMap<>();
            for (Element el : els) {
                String tag = el.getName();
                String value = el.getText();
                if(!"sign".equals(tag) && !StringUtils.isEmpty(value)){//签名
                    temp.put(tag, value);
                }
            }
            Iterator<String> it = temp.keySet().iterator();
            StringBuilder signStr = new StringBuilder();
            while(it.hasNext()){
                String key = it.next();
                signStr.append(key).append("=").append(temp.get(key)).append("&");
            }
            //加上密钥
            signStr.append("key=").append(mchSecret);
            String sign = MD5Util.calc(signStr.toString()).toUpperCase();
            log.info("微信支付 目标签名【"+destSign+"】 生成签名【"+sign+"】  待签字符串:"+signStr);
            //不相等就签名错误
            if(!destSign.equals(sign)){
                throw new WechatException("验签不通过");
            }
            return temp;
        } catch (Exception e) {
            throw new WechatException("签名错误");
        }
    }

    /**
     * AES解密 (需要替换%JDK_HOME%\jre\lib\security的US_export_policy.jar和local_policy.jar)
     * @param base64Data
     * @param secret
     * @return
     * @throws Exception
     */
    private static String decryptData(String base64Data,String secret) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(MD5Util.calc(secret).toLowerCase().getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(base64Data)));
    }
}
