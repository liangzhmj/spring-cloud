package com.liangzhmj.cat.dao.dbconfig.vo;

import com.liangzhmj.cat.dao.mysql.APIDao;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Properties;

@Log4j2
public class ConfigVO {


    private APIDao apiDao;
    private Properties props;
    private String table;

    public ConfigVO(@NonNull APIDao apiDao,@NonNull String table){
        this.apiDao = apiDao;
        this.table = table;
        props = new Properties();
    }

    public void initProperties() {
        try {
            List<Object[]> infos = apiDao.getObjectList("SELECT key_,value_,remark FROM "+table);
            if(CollectionUtils.isEmpty(infos)){
                log.debug(table+"中没有配置数据");
                return ;
            }
            props.clear();
            for (Object[] info : infos) {
                String key = String.valueOf(info[0]);
                String value = String.valueOf(info[1]);
                String remark = String.valueOf(info[2]);
                log.debug(key+"["+remark+"]:"+value);
                props.put(key,value);
            }
        } catch (Exception e) {
            log.error("初始化数据库["+table+"]配置表失败",e);
        }
    }

    public Object getProperty(@NonNull String key){
        return props.get(key);
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
