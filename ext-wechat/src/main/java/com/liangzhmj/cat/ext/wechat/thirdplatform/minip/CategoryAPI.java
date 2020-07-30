package com.liangzhmj.cat.ext.wechat.thirdplatform.minip;

import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import com.liangzhmj.cat.ext.wechat.thirdplatform.vo.Category;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理小程序类目管理 - 微信文档[第三方平台->代小程序实现业务->类目管理]
 * @author liangzhmj
 *
 */
public class CategoryAPI {

    /**
     * 获取审核时可填写的类目信息
     * @param thirdAppid
     * @param appid
     * @return
     */
    public static List<Category> getCategories(String thirdAppid, String appid){
        JSONObject rjson = ThirdAPIUtils.callAPIGet(thirdAppid, appid, "https://api.weixin.qq.com/wxa/get_category?access_token=");
        if(rjson.getInt("errcode") != 0){
            throw new WechatException("获取小程序["+thirdAppid+"]-["+appid+"]类目列表失败:"+rjson);
        }
        JSONArray ja = rjson.getJSONArray("category_list");
        List<Category> cats = new ArrayList<Category>();
        for (int i = 0; i < ja.size(); i++) {
            cats.add(new Category(ja.getJSONObject(i)));
        }
        return cats;
    }

}
