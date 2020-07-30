package com.liangzhmj.cat.ext.wechat.thirdplatform.offia;

import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 接口分析 - 微信文档[公众号->数据统计->接口分析]
 * @author liangzhmj
 */
@Log4j2
public class InterStatAPI {

    /**
     * 获取群发天统计
     * @param thirdAppid
     * @param appid
     * @param date yyyy-MM-dd
     * @return
     */
    public static JSONArray getArticleDayStat(String thirdAppid, String appid, String date){
        try {
            JSONObject params = new JSONObject();
            params.put("begin_date", date);
            params.put("end_date", date);
            JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/datacube/getarticlesummary?access_token=", params);
            JSONArray items = rjson.optJSONArray("list");
            /*
            {
               "list": [
                   {
                       "ref_date": "2014-12-07",
                       "callback_count": 36974,
                       "fail_count": 67,
                       "total_time_cost": 14994291,
                       "max_time_cost": 5044
                   }//后续还有不同ref_date（在begin_date和end_date之间）的数据
               ]
            }
             */
            return items;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 获取群发总统计
     * @param thirdAppid
     * @param appid
     * @param date yyyy-MM-dd
     * @return
     */
    public static JSONArray getArticleTotalStat(String thirdAppid,String appid,String date){
        try {
            JSONObject params = new JSONObject();
            params.put("begin_date", date);
            params.put("end_date", date);
            JSONObject rjson = ThirdAPIUtils.callAPI(thirdAppid, appid, "https://api.weixin.qq.com/datacube/getarticletotal?access_token=", params);
            JSONArray items = rjson.optJSONArray("list");
            return items;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
