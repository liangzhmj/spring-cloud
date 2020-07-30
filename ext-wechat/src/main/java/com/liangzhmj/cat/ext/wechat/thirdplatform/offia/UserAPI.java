package com.liangzhmj.cat.ext.wechat.thirdplatform.offia;


import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.offiaccount.vo.User;
import com.liangzhmj.cat.ext.wechat.thirdplatform.ThirdAPIUtils;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 公众号用户api - 微信文档[公众号->用户管理]
 * @author liangzhmj
 *
 */
@Log4j2
public class UserAPI {

    /**
     * 获取用户信息
     * @param thirdAppid
     * @param appid
     * @param openid
     * @return
     */
    public static User getUserInfo(String thirdAppid, String appid, String openid){
        String url ="https://api.weixin.qq.com/cgi-bin/user/info?openid="+openid+"&lang=zh_CN&access_token=";
        JSONObject res = ThirdAPIUtils.callAPIGet(thirdAppid,appid,url);
        return new User(res);
    }

    /**
     * 判断用户是否关注该公众号
     * @param thirdAppid
     * @param appid
     * @param openid
     * @return
     */
    public static boolean isSubscribe(String thirdAppid,String appid,String openid){
        try {
            User user = getUserInfo(thirdAppid,appid, openid);
            return (user.getSubscribe() == 0)?false:true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }


    /**
     * 获取曾经关注过的用户信息列表
     * @param thirdAppid
     * @param appid
     * @param openids
     * @return
     */
    public static List<User> getUsers(String thirdAppid, String appid, List<String> openids){
        String url = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=";
        JSONObject params = new JSONObject();
        JSONArray ops = new JSONArray();
        for (String openId : openids) {
            JSONObject temp = new JSONObject();
            temp.put("openid", openId);
            temp.put("lang", "zh_CN");
            ops.add(temp);
        }
        params.put("user_list", ops);
        JSONObject json = ThirdAPIUtils.callAPI(thirdAppid,appid,url,params);
        if(!json.has("user_info_list")){
            throw new WechatException("获取不到用户信息列表:"+json);
        }
        JSONArray ulist = json.getJSONArray("user_info_list");
        List<User> users = new ArrayList<>();
        for (Object o : ulist) {
            JSONObject u = (JSONObject)o;
            User user = new User(u);
            users.add(user);
        }
        return users;
    }

    /**
     * 获取曾经关注过的用户信息列表
     * @param thirdAppid
     * @param appid
     * @param openids 一次最多100条
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<User> getSubUsers(String thirdAppid, String appid, List<String> openids){
        if(CollectionUtils.isEmpty(openids)){
            return null;
        }
        List<User> users = getUsers(thirdAppid,appid,openids);
        for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
            User user = iterator.next();
            if(user.getSubscribe() == 0){
                log.warn("用户为取消关注用户，过滤:"+user);
                iterator.remove();
            }
        }
        return users;
    }

    /**
     * 获取公众号所有的粉丝(在关注的)
     * @param thirdAppid
     * @param appid
     * @param openids
     * @return
     */
    public static List<User> getAllSubUsers(String thirdAppid, String appid, List<String> openids){
        List<User> res = new ArrayList<>();
        try {
            log.info("公众号["+appid+"]开始执行获取粉丝");
            if(!CollectionUtils.isEmpty(openids)){
                int size = openids.size();
                log.info("wx-openid-detail:有"+size+"个待获取粉丝");
                List<String> temp = new ArrayList<>();
                int count = 0;
                for (Object obj: openids) {
                    String openid = StringUtils.getCleanString(obj);
                    if(temp.size() > 99){//处理
                        log.info("第"+(++count)+"次像微信接口请求获取用户信息列表，获取条数:"+temp.size());
                        try {
                            List<User> sub = getSubUsers(thirdAppid,appid,temp);
                            res.addAll(sub);
                        } catch (Exception e) {
                            log.error("获取用户信息部分异常:"+temp);
                        }
                        temp.clear();
                        continue;
                    }
                    temp.add(openid);
                }
                if(!temp.isEmpty()){//有剩余
                    try {
                        List<User> sub = getSubUsers(thirdAppid,appid,temp);
                        log.info("第"+(++count)+"次像微信接口请求获取用户信息列表，获取条数:"+temp.size());
                        res.addAll(sub);
                    } catch (Exception e) {
                        log.error("获取用户信息部分异常:"+temp);
                    }
                    temp.clear();
                }
                log.info("公众号["+appid+"]获取用户信息"+size+"条");
            }else{
                log.info("数据库没有待获取的粉丝数");
            }
        } catch (Exception e) {
            log.error(e);
        }
        log.info("公众号["+appid+"]获取粉丝完毕，共获取到粉丝数:"+res.size());
        return res;
    }




}
