package com.liangzhmj.cat.dao.dbconfig;

import com.liangzhmj.cat.dao.dbconfig.vo.ConfigVO;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Log4j2
@NoArgsConstructor
public class DBProperties {

    private static ConfigVO dbConfigVO;


    public static ConfigVO getDbConfigVO() {
        return dbConfigVO;
    }

    @Autowired(required = false)
    @Qualifier("dbConfigVO")//如果没有则说明配置文件没有配置dbconfig.db=[base|service]
    public void setDbConfigVO(ConfigVO dbConfigVO) {
        log.info("初始化DBProperties");
        if(dbConfigVO == null){
            log.warn("没有设置数据库配置信息来源，默认直接返回defaultValue");
            return;
        }
        DBProperties.dbConfigVO = dbConfigVO;
    }


    /**
     * 初始化数据配置表信息
     */
    public static void initProps(){
        log.info("初始化数据库配置表信息");
        DBProperties.dbConfigVO.initProperties();
    }

    /**
     * 获取配置表中的键值对值
     * @param key 键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String getProperty(String key, String defaultValue) {
        if(dbConfigVO == null){
            return defaultValue;
        }
        Object value = dbConfigVO.getProperty(key);
        return (value != null) ? String.valueOf(value) : defaultValue;
    }
    /**
     * 获取t_properties中的键值对值
     * @param key 键
     * @return 值
     */
    public static String getProperty(String key) {
        return String.valueOf(dbConfigVO.getProperty(key));
    }

    /**
     * 获取t_properties中的键值对boolean值
     * @param key 键
     * @param def 默认值
     * @return 值
     */
    public static boolean getBooleanProperty(String key, boolean def) {
        if(dbConfigVO == null){
            return def;
        }
        boolean bol = def;
        Object value = dbConfigVO.getProperty(key);
        if(value != null)
            bol = Boolean.valueOf(String.valueOf(value));
        return bol;
    }
}
