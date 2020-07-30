package com.liangzhmj.cat.ext.wechat.offiaccount;

import com.liangzhmj.cat.dao.vo.Pager;
import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.ext.wechat.tools.common.APIUtils;
import com.liangzhmj.cat.ext.wechat.tools.common.vo.Article;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.date.DateUtils;
import com.liangzhmj.cat.tools.http.HttpUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.entity.ContentType;

import java.io.File;
import java.util.*;

/**
 * 素材api - 微信文档[公众号->素材管理]
 * @author liangzhmj
 *
 */
@Log4j2
public class MaterialAPI {//获取素材就是下载，先不实现

    /**
     * 上传临时素材(本地文件)
     * @param appid
     * @param type 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb，主要用于视频与音乐格式的缩略图）
     * @param data
     * @return
     */
    public static String uploadTMaterial(String appid,String type,File data){
        String url = "https://api.weixin.qq.com/cgi-bin/media/upload?type="+type+"&access_token=";
        return uploadMaterial(url,appid,type,data);
    }

    /**
     * 上传临时素材(byte数组)
     * @param appid
     * @param type
     * @param data
     * @param ext
     * @return
     */
    public static String uploadTMaterial(String appid,String type,byte[] data,String ext){
        String url = "https://api.weixin.qq.com/cgi-bin/media/upload?type="+type+"&access_token=";
        return uploadMaterialBytes(url,appid,type,data,ext);
    }

    /**
     * 上传临时素材(网络资源)
     * @param appid
     * @param type
     * @param dataUrl
     * @return
     */
    public static String uploadTMaterial(String appid,String type,String dataUrl){
        String url = "https://api.weixin.qq.com/cgi-bin/media/upload?type="+type+"&access_token=";
        return uploadMaterialUrl(url,appid,type,dataUrl);
    }

    /**
     * 新增永久素材(本地文件)
     * @param appid
     * @param type
     * @param data
     * @return
     */
    public static String uploadPMaterial(String appid,String type,File data){
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?type="+type+"&access_token=";
        return uploadMaterial(url,appid,type,data);
    }

    /**
     * 上传永久素材(byte数组)
     * @param appid
     * @param type
     * @param data
     * @param ext
     * @return
     */
    public static String uploadPMaterial(String appid,String type,byte[] data,String ext){
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?type="+type+"&access_token=";
        return uploadMaterialBytes(url,appid,type,data,ext);
    }

    /**
     * 上传永久素材(网络资源)
     * @param appid
     * @param type
     * @param dataUrl
     * @return
     */
    public static String uploadPMaterial(String appid,String type,String dataUrl){
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?type="+type+"&access_token=";
        return uploadMaterialUrl(url,appid,type,dataUrl);
    }


    /**
     * 上传素材(本地文件)
     * @param url
     * @param appid
     * @param type
     * @param data
     * @return
     */
    public static String uploadMaterial(String url,String appid,String type,File data){
        String resp = null;
        try {
            Map<String,String> header = getHeaders();
            JSONObject rjson = APIUtils.callAPI(url,appid,header,data);
            return rjson.getString("media_id");
        } catch (Exception e) {
            log.error(e.getMessage()+"--"+resp);
        }
        return null;
    }

    /**
     * 上传素材(byte数组)
     * @param url
     * @param appid
     * @param type
     * @param data
     * @param ext
     * @return
     */
    public static String uploadMaterialBytes(String url,String appid,String type,byte[] data,String ext){
        try {
            Map<String,String> header = getHeaders();
            ContentType contentType = ContentType.MULTIPART_FORM_DATA;
            if("image".equals(type)){
                contentType = ContentType.IMAGE_JPEG;
            }
            JSONObject json = APIUtils.uploadMedia(url,appid,header,contentType,data,ext);
            return json.getString("media_id");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
    /**
     * 上传素材(网络资源)
     * @param url
     * @param appid
     * @param type
     * @param dataUrl
     * @return
     */
    public static String uploadMaterialUrl(String url,String appid,String type,String dataUrl){
        try {
            //获取网络资源
            Object[] info = HttpUtils.copyNetImage(dataUrl,10000);
            if(CollectionUtils.isEmpty(info)){
                throw new WechatException("获取网络资源失败:"+dataUrl);
            }
            byte[] data = (byte[])info[0];
            String ext = (String)info[1];
            return uploadMaterialBytes(url,appid,type,data,ext);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


    /**
     * 按类型分页获取永久素材列表
     * @param pager
     * @param appid
     * @param type 素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
     * @return
     */
    public static Pager getPMaterial(Pager pager,String appid,String type){
        try {
            JSONObject params = new JSONObject();
            params.put("type", type);
            params.put("offset", pager.getOffset());
            params.put("count", pager.getPageSize());
            JSONObject rjson = APIUtils.callAPI( "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=",appid, params);
            pager.setRecords(StringUtils.getCleanLong(rjson.getInt("total_count")));
            JSONArray items = rjson.optJSONArray("item");
            pager.setResults(items);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pager;
    }

    /**
     * 获取公众号图文素材列表
     * @param appid
     * @return
     */
    public static Pager getArticles(Pager pager, String appid){
        try {
            pager = getPMaterial(pager,appid,"news");
            JSONArray items = (JSONArray)pager.getResults();
            List<JSONObject> ress = new ArrayList<>();
            if(items != null && !items.isEmpty()){
                for (int i = 0; i < items.size(); i++) {//处理多图文，多图文同一个mediaId
                    JSONObject item = items.getJSONObject(i);
                    JSONArray newsItems = item.getJSONObject("content").getJSONArray("news_item");
                    JSONObject first = newsItems.getJSONObject(0);
                    String mediaId = item.getString("media_id");
                    first.put("media_id", mediaId);
                    String updateTime = DateUtils.dateToString("yyyy-MM-dd HH:mm:ss",new Date(item.getLong("update_time")*1000));
                    first.put("update_time", updateTime);
                    first.put("idx", 1);
                    JSONArray temps = new JSONArray();
                    for (int j = 1; j < newsItems.size(); j++) {
                        JSONObject temp = newsItems.getJSONObject(j);
                        temp.put("media_id", mediaId);
                        temp.put("update_time", updateTime);
                        temp.put("idx", j+1);
                        temps.add(temp);
                    }
                    first.put("subs", temps);
                    ress.add(first);
                }
                pager.setResults(ress);
                return pager;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return pager;
    }

    /**
     * 获取单个图文素材(可能包含多个文章)消息
     * @param appid
     * @param mediaId
     * @return
     */
    public static JSONArray getArticle(String appid,String mediaId){
        try {
            JSONObject params = new JSONObject();
            params.put("media_id", mediaId);
            JSONObject rjson = APIUtils.callAPI("https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=",appid, params);
            JSONArray items = rjson.optJSONArray("news_item");
            return items;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }



    /**
     * 获取素材库总量
     * @param appid
     * @return
     */
    public static JSONObject getMaterSize(String appid){
        try {
            JSONObject params = new JSONObject();
            JSONObject rjson = APIUtils.callAPI( "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=",appid, params);
            log.info(rjson);
            return rjson;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


    /**
     * 获取素材总数
     * @param appid
     * @return
     */
    public static JSONObject getMaterialSize(String appid){
        JSONObject params = new JSONObject();
        /*
        {
          "voice_count":COUNT,
          "video_count":COUNT,
          "image_count":COUNT,
          "news_count":COUNT
        }
         */
        return APIUtils.callAPIGet("https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=",appid);
    }

    /**
     * 上传微信服务器替换资源路径
     * @param appid
     * @param data
     * @return
     */
    public static String getWServerUrl(String appid,File data){
        try {
            Map<String,String> header = new HashMap<String,String>();
            header.put("Connection", "keep-alive");
            header.put("Accept", "*/*");
            header.put("Content-Type", "multipart/form-data");
            header.put("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
            String url = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=";
            JSONObject resp = APIUtils.callAPI(url,appid,header,data);
            return resp.getString("url");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 发布文章
     * @param appid
     * @param article
     * @param coverMediaId
     * @return
     */
    public static String issueMaterial(String appid, Article article, String coverMediaId){
        /*
        {
            "articles": [{
                             "title": TITLE,
                            "thumb_media_id": THUMB_MEDIA_ID,
                            "author": AUTHOR,
                            "digest": DIGEST,
                            "show_cover_pic": SHOW_COVER_PIC(0 / 1),
                            "content": CONTENT,
                            "content_source_url": CONTENT_SOURCE_URL,
                            "need_open_comment":1,
                            "only_fans_can_comment":1
                        },
                        //若新增的是多图文素材，则此处应还有几段articles结构
                    ]
        }
         */
        JSONObject params = new JSONObject();
        JSONArray articles = new JSONArray();
        JSONObject jarticle = new JSONObject();
        jarticle.put("title", article.getTitle());
        jarticle.put("thumb_media_id", coverMediaId);
        jarticle.put("show_cover_pic", 0);
        if(!StringUtils.isEmpty(article.getAuthor())){
            jarticle.put("author", article.getAuthor());
        }
        if(!StringUtils.isEmpty(article.getDigest())){
            jarticle.put("digest", article.getDigest());
        }
        if(!StringUtils.isEmpty(article.getCsurl())){
            jarticle.put("content_source_url", article.getCsurl());
        }
        jarticle.put("content", article.getContent());
        jarticle.put("need_open_comment", article.getNeedOpenComment());
        jarticle.put("only_fans_can_comment", article.getOnlyFansComment());
        articles.add(jarticle);
        params.put("articles", articles);
//			log.info("内容文本--->"+content);
        JSONObject rjson = APIUtils.callAPI("https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=", appid,  params);
        if(!rjson.containsKey("media_id")){
            throw new WechatException(rjson.toString());
        }
        return rjson.getString("media_id");
    }

    /**
     * 删除素材
     * @param appid
     * @return
     */
    public static int delMaterial(String appid,String mediaId){
        JSONObject params = new JSONObject();
        params.put("media_id", mediaId);
        JSONObject rjson = APIUtils.callAPI("https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=",appid,  params);
        return rjson.getInt("errcode");
    }

    /**
     * 上传文件头部信息
     * @return
     */
    private static Map<String,String> getHeaders(){
        Map<String,String> header = new HashMap<>();
        header.put("Connection", "keep-alive");
        header.put("Accept", "*/*");
        header.put("Content-Type", "multipart/form-data");
        header.put("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        return header;
    }

}
