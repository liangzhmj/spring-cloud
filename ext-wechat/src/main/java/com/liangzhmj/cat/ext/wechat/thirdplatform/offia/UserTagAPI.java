package com.liangzhmj.cat.ext.wechat.thirdplatform.offia;

import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.offiaccount.protocol.UserTagProtocol;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 用户标签 - 微信文档[公众号->用户管理->用户标签管理]
 * @author
 */
@Log4j2
public class UserTagAPI {


    /**
     * 创建用户标签
     * @param thirdAppid
     * @param appid
     * @param name
     * @return
     */
    public static int createTag(String thirdAppid,String appid,String name){
        try {
            JSONObject params = UserTagProtocol.createTag(name);
            JSONObject resp = ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/tags/create?access_token=",params);
            return resp.getJSONObject("tag").getInt("id");
        } catch (Exception e) {
            log.error(e);
        }
        return 0;
    }

    /**
     * 获取标签用户列表
     * @param thirdAppid
     * @param appid
     * @param id
     * @param nextOpenid
     * @return
     */
    public static JSONObject getTagUsers(String thirdAppid,String appid,int id,String nextOpenid){
        try {
            if(id < 1){
                throw new WechatException("用户标签ID小于1");
            }
            JSONObject params = UserTagProtocol.tagUsers(id,nextOpenid);
            return ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=",params);
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 批量为用户打标签
     * @param thirdAppid
     * @param appid
     * @param id
     * @param openids
     * @return
     */
    public static int batchTagging(String thirdAppid, String appid, int id, List<String> openids){
        try {
            if(CollectionUtils.isEmpty(openids)){
                throw new WechatException("用户标签待打标签的用户列表为null");
            }
            JSONObject params = UserTagProtocol.batchTagging(id,openids);
            JSONObject resp = ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=", params);
            return resp.getInt("errcode");
        } catch (Exception e) {
            log.error(e);
        }
        return -1;
    }

    /**
     * 批量取消标签
     * @param thirdAppid
     * @param appid
     * @param id
     * @param openids
     * @return
     */
    public static int batchUntagging(String thirdAppid, String appid, int id, List<String> openids){
        try {
            if(CollectionUtils.isEmpty(openids)){
                throw new WechatException("用户标签待打标签的用户列表为null");
            }
            JSONObject params = UserTagProtocol.batchUntagging(id,openids);
            JSONObject resp = ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=", params);
            return resp.getInt("errcode");
        } catch (Exception e) {
            log.error(e);
        }
        return -1;
    }

    /**
     * 获取公众号标签
     * @param thirdAppid
     * @param appid
     * @return
     */
    public static JSONArray getTags(String thirdAppid, String appid){
        /*
        {
            "tags":[{
                "id":1,
                "name":"每天一罐可乐星人",
                "count":0 //此标签下粉丝数
            },
            {
                "id":2,
                "name":"星标组",
                "count":0
            },
            {
                "id":127,
                "name":"广东",
                "count":5
             }
            ] }
         */
        String url = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=";
        JSONObject res = ThirdAPIUtils.callAPIGet(thirdAppid,appid,url);
        if(!res.containsKey("tags")){
            throw new WechatException("获取不到["+thirdAppid+"]-["+appid+"]的标签");
        }
        return res.getJSONArray("tags");
    }

    /**
     * 编辑标签
     * @param thirdAppid
     * @param appid
     * @param id
     * @param name
     * @return
     */
    public static int updateTag(String thirdAppid, String appid, int id, String name){
        try {
            //{   "tag" : {     "id" : 134,     "name" : "广东人"   } }
            JSONObject params = new JSONObject();
            JSONObject tag = new JSONObject();
            tag.put("id",id);
            tag.put("name",name);
            params.put("tag",tag);
            JSONObject resp = ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/tags/update?access_token=", params);
            return resp.getInt("errcode");
        } catch (Exception e) {
            log.error(e);
        }
        return -1;
    }

    /**
     * 删除标签
     * @param thirdAppid
     * @param appid
     * @param id
     * @return
     */
    public static int delTag(String thirdAppid, String appid, int id){
        try {
            //{   "tag":{        "id" : 134   } }
            JSONObject params = new JSONObject();
            JSONObject tag = new JSONObject();
            tag.put("id",id);
            params.put("tag",tag);
            JSONObject resp = ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=", params);
            return resp.getInt("errcode");
        } catch (Exception e) {
            log.error(e);
        }
        return -1;
    }

    /**
     * 获取用户的标签列表
     * @param thirdAppid
     * @param appid
     * @param openid
     * @return
     */
    public static JSONArray getTagByUser(String thirdAppid, String appid, String openid){
        try {
            //{   "openid" : "ocYxcuBt0mRugKZ7tGAHPnUaOW7Y" }
            JSONObject params = new JSONObject();
            params.put("openid",openid);
            JSONObject resp = ThirdAPIUtils.callAPI(thirdAppid,appid,"https://api.weixin.qq.com/cgi-bin/tags/getidlist?access_token=", params);
            if(!resp.containsKey("tagid_list")){
                throw new WechatException("平台["+thirdAppid+"]-["+appid+"]-用户["+openid+"]获取不到标签");
            }
            return resp.getJSONArray("tagid_list");
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

}
