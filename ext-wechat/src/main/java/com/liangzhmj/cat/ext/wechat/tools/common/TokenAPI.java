package com.liangzhmj.cat.ext.wechat.tools.common;

import com.liangzhmj.cat.dao.redis.RedisDao;
import com.liangzhmj.cat.dao.utils.DaoBeanUtils;
import com.liangzhmj.cat.ext.wechat.config.ConfigContext;
import com.liangzhmj.cat.ext.wechat.config.DBConfigUtils;
import com.liangzhmj.cat.ext.wechat.config.vo.RedisKey;
import com.liangzhmj.cat.sync.lock.single.MySyncLockUtils;
import com.liangzhmj.cat.sync.lock.single.model.MyLock;
import com.liangzhmj.cat.tools.http.HttpUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;

/**
 * 小程序和公众号的共用api
 * @author liangzhmj
 */
@Log4j2
public class TokenAPI {

    private static RedisKey keys = ConfigContext.getWechatConfig().getRedisKey();
    private static RedisDao redisDao = DaoBeanUtils.getRedisDao();


    /**
     * 获取素材[公众号|小程序]的访问token
     * @param appid
     * @param srcToken
     * @param refresh
     * @return
     */
    public static String getToken(String appid,String srcToken,boolean refresh){
        //0:appid,1:app密钥,2:商户号,3:商户密钥,4:支付通知路径
        String[] config = DBConfigUtils.getSubjectConfig(appid);
        String key = keys.token + appid;
        String token = null;
        if(!refresh){//不是刷新
            token = redisDao.getT(key);
            if(!StringUtils.isEmpty(token)){
                return token;
            }
        }
        String lockKey = "/mylock/atoken/single/"+appid;
        MyLock lock = MySyncLockUtils.getSimpleLock(lockKey);
        synchronized (lock) {
            //再次获取
            token = redisDao.getT(key);
            if(!StringUtils.isEmpty(token) && !token.equals(srcToken)){//之前已经有获取的,防止重复刷新
                log.info("素材["+appid+"]的访问token已刷新["+srcToken+"],被刷新token["+token+"]");
                return token;
            }
            String res = null;
            try {
                String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+config[1];
                //{"access_token":"ACCESS_TOKEN","expires_in":7200}
                res = HttpUtils.get(url,10000);
                JSONObject json = JSONObject.fromObject(res);
                token = json.getString("access_token");
                int expiresIn = json.getInt("expires_in");
                //提前一分钟失效
                int temp = expiresIn-60;
                expiresIn = temp>60?temp:60;
                //保存token
                redisDao.add(key, token,expiresIn);
                return token;
            } catch (Exception e) {
                log.error(e.getMessage()+"-->"+res);
            }
        }
        return null;
    }
}
