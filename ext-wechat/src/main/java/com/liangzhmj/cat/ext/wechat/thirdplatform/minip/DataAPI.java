package com.liangzhmj.cat.ext.wechat.thirdplatform.minip;

import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

/**
 * 小程序数据
 * @author liangzhmj
 *
 */
@Log4j2
public class DataAPI {


	/**
	 * 获取用户访问小程序数据概况
	 * @param thirdAppid
	 * @param appid
	 * @param starttime
	 * @param endtime
	 */
	public static void dailySummary(String thirdAppid,String appid,String starttime,String endtime){
		try {
			JSONObject params = new JSONObject();
			params.put("begin_date", starttime);
			params.put("end_date", endtime);
			JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/datacube/getweanalysisappiddailysummarytrend?access_token=", params);
			/*
			{
				  "list": [
					{
					  "ref_date": "20170313",
					  "visit_total": 391,
					  "share_pv": 572,
					  "share_uv": 383
					}
				  ]
				}
			 */
			if(rjson.getInt("errcode") != 0){
				throw new WechatException("为小程序["+appid+"]常规分析失败:"+rjson);
			}
			log.info(rjson);
		} catch (Exception e) {
			log.error(e);
		}
	}

}
