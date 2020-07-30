package com.liangzhmj.cat.tools.nlp;

import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.exception.ToolsException;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 粤语转拼音(无声调)
 * @author liangzhmj
 */
@Log4j2
public class HkToSpellNotone {

    //有声调
    @Getter
    public Map<String, List<String>> cache = new HashMap<>();
    public static HkToSpellNotone my;

    private HkToSpellNotone(){
        InputStream inputStream = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource("cantonese/data.txt");
            inputStream =classPathResource.getInputStream();
            List<String> lines = IOUtils.readLines(inputStream,"UTF-8");
            Pattern p = Pattern.compile("(\\w+)\\d$");
            for (String line : lines) {
                try {
                    String[] infos = line.trim().split("\t");
                    String word = infos[0].trim();
                    String pinyin = infos[1].trim();
                    Matcher m = p.matcher(pinyin);
                    if(m.matches()){
                        pinyin = m.group(1);
                    }else{
                        throw new ToolsException("拼音格式不匹配:"+line);
                    }
                    List<String> val = cache.get(word);
                    if(val == null){
                        val = new ArrayList<>();
                    }
                    if(!val.contains(pinyin)){
                        val.add(pinyin);
                    }

                    cache.put(word,val);
                } catch (Exception e) {
                    log.error("加载部分粤语库异常:"+line);
                }
            }
        } catch (IOException e) {
            log.error("加载粤语库异常",e);
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
    }

    /**
     * 单例
     * @return
     */
    public static HkToSpellNotone getInstance(){
        if(my != null){
            return my;
        }
        synchronized (HkToSpellNotone.class){
            if(my != null){
                return my;
            }
            my = new HkToSpellNotone();
        }
        return my;
    }

    /**
     * 获取拼音列表
     * @param word
     * @return
     */
    public static List<String> getPinyin(char word){
        return HkToSpellNotone.getPinyin(String.valueOf(word));
    }

    /**
     * 获取拼音列表
     * @param word
     * @return
     */
    public static List<String> getPinyin(String word){
        return HkToSpellNotone.getInstance().getCache().get(word);
    }

    /**
     * 获取一句话的拼音，多音字默认取首个
     * @param str
     * @param blank 分隔符
     * @return
     */
    public static String getStrPinyin(String str,String blank){
        if(StringUtils.isEmpty(str)){
            return str;
        }
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            List<String> pys = HkToSpellNotone.getPinyin(str.charAt(i));
            String sub = CollectionUtils.isEmpty(pys)?"null":pys.get(0);
            if(i == 0){
                res.append(sub);
                continue;
            }
            res.append(blank + sub);
        }
        return res.toString();
    }

    /**
     * 获取一句话的拼音，多音字默认取首个
     * @param str
     * @return
     */
    public static String getStrPinyin(String str){
        return getStrPinyin(str,"");
    }


    public static void main(String[] args) {
        String str = StringUtils.pureText("唔好意思，阻你一阵");
        System.out.println(getStrPinyin(str));
    }

}
