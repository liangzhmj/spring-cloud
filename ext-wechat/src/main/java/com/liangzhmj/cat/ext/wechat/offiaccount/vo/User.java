package com.liangzhmj.cat.ext.wechat.offiaccount.vo;

import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.json.JSONField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 公众号用户信息
 */
@Getter
@Setter
@NoArgsConstructor
public class User extends JSONBase {

    /**
     * subscribe : 1
     * openid : o6_bmjrPTlm6_2sgVt7hMZOPfL2M
     * nickname : Band
     * sex : 1
     * language : zh_CN
     * city : 广州
     * province : 广东
     * country : 中国
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0
     * subscribe_time : 1382694957
     * unionid :  o6_bmasdasdsad6_2sgVt7hMZOPfL
     * remark :
     * groupid : 0
     * tagid_list : [128,2]
     * subscribe_scene : ADD_SCENE_QR_CODE
     * qr_scene : 98765
     * qr_scene_str :
     */
    //用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
    @JSONField(clazz = JSONBase.INTEGER)
    private int subscribe;
    @JSONField
    private String openid;
    @JSONField
    private String nickname;
    //	用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
    @JSONField(clazz = JSONBase.INTEGER)
    private int sex;
    @JSONField
    private String language;
    @JSONField
    private String city;
    @JSONField
    private String province;
    @JSONField
    private String country;
    @JSONField
    private String headimgurl;
    //用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
    @JSONField(name = "subscribe_time",clazz = JSONBase.LONG)
    private long subscribeTime;
    @JSONField
    private String unionid;
    @JSONField
    private String remark;
    //用户所在的分组ID（兼容旧的用户分组接口）
    @JSONField(clazz = JSONBase.INTEGER)
    private int groupid;
    @JSONField(name = "subscribe_scene")
    private String subscribeScene;
    @JSONField(name="qr_scene",clazz = JSONBase.INTEGER)
    private int qrScene;
    @JSONField(name="qr_scene_str")
    private String qrSceneStr;
    //用户被打上的标签ID列表
    @JSONField(name="tagid_list")
    private List<Integer> tagids;

    public User(JSONObject data){
        this.fromJSON(data);
    }

}
