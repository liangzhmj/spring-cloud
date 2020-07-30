package com.liangzhmj.cat.ext.wechat.offiaccount.protocol;

import com.liangzhmj.cat.tools.string.StringUtils;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 用户标签协议
 * @author liangzhmj
 */
public class UserTagProtocol {

    /**
     * 创建标签
     * @param name
     * @return
     */
    public static JSONObject createTag(String name){
        //{   "tag" : {     "name" : "广东"//标签名   } }
        JSONObject params = new JSONObject();
        JSONObject tag = new JSONObject();
        tag.put("name", name);
        params.put("tag", tag);
        return params;
    }

    /**
     * 获取标签的用户列表
     * @param id
     * @param nextOpenid
     * @return
     */
    public static JSONObject tagUsers(int id,String nextOpenid){
        JSONObject params = new JSONObject();
        params.put("tagid", id);
        if(!StringUtils.isEmpty(nextOpenid)){
            params.put("next_openid", nextOpenid);
        }
        return params;
    }

    /**
     * 批量打标签
     * @param id
     * @param openids
     * @return
     */
    public static JSONObject batchTagging(int id, List<String> openids){
        JSONObject params = new JSONObject();
        params.put("tagid", id);
        params.put("openid_list", openids);
        return params;
    }
    /**
     * 批量取消标签
     * @param id
     * @param openids
     * @return
     */
    public static JSONObject batchUntagging(int id, List<String> openids){
        JSONObject params = new JSONObject();
        params.put("tagid", id);
        params.put("openid_list", openids);
        return params;
    }

}
