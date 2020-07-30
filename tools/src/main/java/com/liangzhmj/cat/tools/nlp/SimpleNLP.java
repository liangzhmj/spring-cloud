package com.liangzhmj.cat.tools.nlp;

import com.liangzhmj.cat.tools.exception.ToolsException;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * 简单的然语言处理（基本是相似文本比较（包括粤语），不考虑苛刻的情况下）
 * @author liangzhmj
 */
@Log4j2
public class SimpleNLP {

    /** 普通话 **/
    public static int LAN_MANDARIN = 1;
    /** 粤语 **/
    public static int LAN_CANTONESE = 2;

    /**
     * 根据语种获取整句话的拼音
     * @param words
     * @param language
     * @return
     */
    public static String getStrPinyin(String words, int language){
        if(LAN_MANDARIN == language){//普通话
            List<String> res = CnToSpell.getPinyin(words);
            if(CollectionUtils.isEmpty(res)){
                return null;
            }
            return res.get(0);
        }else if(LAN_CANTONESE == language){//粤语
            return HkToSpellNotone.getStrPinyin(words);
        }
        return null;
    }

    /**
     * 获取一个字符的拼音
     * @param word
     * @param language 语种
     * @return
     */
    public static Collection<String> getPinyin(char word, int language){
        if(LAN_MANDARIN == language){//普通话
            Collection<String> res = CnToSpell.getMohu(word);
            if(CollectionUtils.isEmpty(res)){
                return null;
            }
            return res;
        }else if(LAN_CANTONESE == language){//粤语
            return HkToSpellNotone.getPinyin(word);
        }
        return null;
    }


    /**
     * 比较两个字符串相似率(同音也算)
     * @param src 源字符串
     * @param dest 目标字符串
     * @param max 最大处理字符串
     * @param language 语种
     * @return
     */
    public static double compareCnSimilarity(String src,String dest,int max,int language){
        try {
            src = StringUtils.pureText(src);//去除标点,空格之类的
            dest = StringUtils.pureText(dest);//去除标点,空格之类的
            if(StringUtils.isEmpty(src) || StringUtils.isEmpty(dest) || src.length()>max){
                throw new ToolsException("["+src+"]-["+dest+"]参数不合法，对比失败");
            }
            if(src.equals(dest)){//完全相等
                log.info("["+src+"]-["+dest+"]完全相等，相似率为:1");
                return 1;
            }
            int srcSize = src.length();
            int destSize = dest.length();
            if(srcSize == destSize){//长度相等，逐一比较
                //正句子判断一次
                String srcPys = getStrPinyin(src,language);
                String destPys = getStrPinyin(dest,language);
                if(!StringUtils.isEmpty(srcPys) && srcPys.equals(destPys)){
                    log.info("["+src+"]-["+dest+"]的拼音["+srcPys+"]-["+destPys+"]相等，对比完毕，相似率为:1");
                    return 1;
                }
            }
            return compareCnSimilarity(src, dest,language);
        } catch (Exception e) {
            log.error(e);
        }
        return 0;
    }

    /**
     * 字符串相似度匹配算法
     * @param longStr
     * @param shortStr
     * @param language 语种
     * @return
     */
    public static double compareCnSimilarity(String longStr,String shortStr,int language){
        try {
            int longSize = longStr.length();
            int shortSize = shortStr.length();
            if(longSize < shortSize){//交换
                String temp = longStr;
                longStr = shortStr;
                shortStr = temp;
                int tmepSize = longSize;
                longSize = shortSize;
                shortSize = tmepSize;
            }
            int longIndex = 0;
            int match = 0;
            for (int i = 0; i < shortSize; i++) {//用小串去匹配大串
                if(longIndex > (longSize - 1)){//长字符串已经对比完毕
                    double res = (double)match/(double)longSize;
                    log.info("["+longStr+"]-["+shortStr+"]长串遍历完:短串剩余字符数="+(shortSize-i)+",match:"+match+"，index:"+i+"，相似率:"+res);
                    return res;
                }
                char shortChar = shortStr.charAt(i);
                char longChar = longStr.charAt(longIndex);
                if(shortChar == longChar){//如果当前短串的index的值等于长串的，则双方跳到下一个index比较
                    match++;//记录匹配
                    longIndex++;
                    continue;
                }
                Collection<String> spys = getPinyin(shortChar,language);//同音字
                if(CollectionUtils.isEmpty(spys)){//没有同音字，则不相等就是不等
                    continue;
                }
                //短串的index的值有同音字
                boolean isCatch = false;
                int j = longIndex;
                for ( ;j < longSize; j++) {//用短串index的值的同音字去匹配长串
                    Collection<String> lpys = getPinyin(longStr.charAt(j),language);//长串j的同音字
                    if(CollectionUtils.isEmpty(spys)){//长串的index的值没有同音字记录一次失败
                        continue;
                    }
                    for (String lp : lpys) {//如果长串这个索引有同音字
                        for (String sp : spys) {
                            if(lp.equals(sp)){
                                isCatch = true;
                                break;//跳到外层for，该字符匹配
                            }
                        }
                        if(isCatch){
                            break;//跳到外层，该字符匹配
                        }
                    }
                    if(isCatch){
                        break;//跳到外层，该字符匹配
                    }
                }
                if(!isCatch){//没找到,短的匹配长的，没找到，短串当前字符是多余的，故长串不用进位
//                    longIndex++;
                }else{//找到，就是长串在该index之前都是多余的，因此进j+1位
                    match++;
                    //记录位移量,调到找到的index的下一个位置
                    longIndex = j+1;
                }
            }
            //短串匹配完毕，长串剩余的全是不匹配的,匹配率
            double res = (double)match/(double)longSize;
            log.info("["+longStr+"]-["+shortStr+"]全部对比完毕，相似率:"+res);
            return res;
        } catch (Exception e) {
            log.error(e);
        }
        return 0;
    }


    /**
     * 比较两个字符串是否匹配（同音也算匹配）
     * @param src 源字符串
     * @param dest 目标字符串
     * @param accuracy 0-1最低匹配率
     * @param language 语种
     * @return
     */
    public static boolean compareCnMatch(String src,String dest,double accuracy,int language){
        try {
            src = StringUtils.pureText(src);//去除标点,空格之类的
            dest = StringUtils.pureText(dest);//去除标点,空格之类的

            if(StringUtils.isEmpty(src) || StringUtils.isEmpty(dest) || accuracy < 0 || accuracy > 1 || src.length()>100){
                throw new ToolsException("["+src+"]-["+dest+"]参数不合法，匹配失败");
            }
            if(src.equals(dest)){//完全相等
                log.info("["+src+"]-["+dest+"]完全相等，匹配成功");
                return true;
            }
            int size = src.length();
            int size1 = dest.length();
            //计算最大错误字数
            int ecount = size - new Double(Math.ceil(size * accuracy)).intValue();
            log.info("["+src+"]-["+dest+"]允许最大错误字数:"+ecount);
            int interval = size-size1;
            if(Math.abs(interval) > ecount){//超过容错数量
                throw new ToolsException("["+src+"]-["+dest+"]字数差额超过容错数量:"+ecount+"，匹配失败");
            }
            if(interval == 0){//相等，逐一比较
                //正句子判断一次
                String srcPys = getStrPinyin(src,language);
                String destPys = getStrPinyin(dest,language);
                if(!StringUtils.isEmpty(srcPys) && srcPys.equals(destPys)){
                    log.info("["+src+"]-["+dest+"]的拼音["+srcPys+"]-["+destPys+"]相等，匹配成功");
                    return true;
                }
            }
            return compareCnMatch(src, dest, ecount,language);
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }

    /**
     * 字符串相似度匹配算法
     * @param longStr
     * @param shortStr
     * @param ecount 允许错误的字数
     * @param language 语种
     * @return
     */
    public static boolean compareCnMatch(String longStr,String shortStr,int ecount,int language){
        try {
            int eqCount = ecount;//允许错误的字数
            int longSize = longStr.length();
            int shortSize = shortStr.length();
            if(longSize < shortSize){//交换
                String temp = longStr;
                longStr = shortStr;
                shortStr = temp;
                int tmepSize = longSize;
                longSize = shortSize;
                shortSize = tmepSize;
            }
            int longIndex = 0;
            int subEqCount;
            for (int i = 0; i < shortSize; i++) {
                subEqCount = eqCount;
                if((longIndex > (longSize - 1)) && ((shortSize-i) > eqCount)){//长字符串已经对比完毕,剩余的全是错,如果剩余>容错
                    throw new ToolsException("["+longStr+"]-["+shortStr+"]长串遍历完毕并且短字符串剩余字符大于容错数:剩余字符数="+(shortSize-i)+"eqCount:"+eqCount+"，index:"+i+"，匹配失败");
                }
                if(eqCount < 0){
                    throw new ToolsException("["+longStr+"]-["+shortStr+"]错误字数已超过容错数量:ecount="+ecount+"，index:"+i+"，匹配失败");
                }
                //如果剩余<=容错
                if((shortSize-i) <= eqCount && (longSize-longIndex) <= eqCount){
                    log.info("["+longStr+"]-["+shortStr+"]未匹配小于等于容错数量:eqCount="+eqCount+"，短串未匹配:"+(shortSize-i)+"，长串未匹配："+(longSize-longIndex)+"，index:"+i+"，匹配成功");
                    return true;
                }
                char shortChar = shortStr.charAt(i);
                char longChar = longStr.charAt(longIndex);
                if(shortChar == longChar){//如果当前短串的index的值等于长串的，则双方跳到下一个index比较
                    longIndex++;
                    continue;
                }
                Collection<String> spys = getPinyin(shortChar,language);//同音字
                if(CollectionUtils.isEmpty(spys)){//没有同音字，则不相等就是不等，记录一次错误
                    eqCount--;//记录一次失败
                    continue;
                }
                //短串的index的值有同音字
                boolean isCatch = false;
                int j = longIndex;
                for ( ;j < longSize; j++) {//用短串index的值的同音字去匹配长串
                    if(subEqCount < 0){
                        break;
                    }
                    Collection<String> lpys = getPinyin(longStr.charAt(j),language);
                    if(CollectionUtils.isEmpty(spys)){//长串的index的值没有同音字记录一次失败
                        subEqCount--;
                        continue;
                    }
                    for (String lp : lpys) {
                        for (String sp : spys) {
                            if(lp.equals(sp)){
                                isCatch = true;
                                break;//跳到外层for，该字符匹配
                            }
                        }
                        if(isCatch){
                            break;//跳到外层，该字符匹配
                        }
                    }
                    if(isCatch){
                        break;//跳到外层，该字符匹配
                    }
                    subEqCount--;//记录一次失败
                }
                if(!isCatch){//没找到,记录一次失败
                    eqCount--;
//                    longIndex++;//没找到,短的匹配长的，没找到，短串当前字符是多余的，故长串不用进位
                }else{//找到
                    //跳过了几个字
                    eqCount-=(j-longIndex);
                    //记录位移量,调到找到的index的下一个位置
                    longIndex = j+1;//找到，就是长串在该index之前都是多余的，因此进j+1位
                }
            }
            if(eqCount < 0 || (longSize - longIndex) > eqCount){//容错数未零，剩下未遍历的长字符串全是错
                throw new ToolsException("["+longStr+"]-["+shortStr+"]短串遍历完错误字数已超过容错数量:ecount="+ecount+"或长串剩余未处理数大于容错数，未处理数:"+(longSize - longIndex)+"，匹配失败");
            }
            log.info("["+longStr+"]-["+shortStr+"]遍历匹配完成剩余容错数:eqCount="+(eqCount-(longSize - longIndex))+"，匹配成功");
            return true;
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }
}
