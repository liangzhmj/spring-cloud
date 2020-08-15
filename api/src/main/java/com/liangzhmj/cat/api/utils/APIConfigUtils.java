package com.liangzhmj.cat.api.utils;

import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.dao.mysql.APIDao;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Setter
@Log4j2
public class APIConfigUtils {


    @Value("${api.projectId:0}")
    private int projectId;
    @Value("${api.syncServlet.start:0}")
    private int start;
    @Value("${api.syncServlet.count:0}")
    private int count;
    @Resource(name = "baseDao")
    private APIDao baseDao;

    public void generateSynaServlets() {
        if(projectId < 1){
            throw new APIException("请配置api.projectid > 0");
        }
        if(count < 1){
            throw new APIException("请配置api.syncServlet.count > 0");
        }
        int end = start+count;
        for (int i = start; i < end; i++) {
            String num = String.valueOf(i);
            if(i < 10){
                num = "00"+num;
            }else if(i < 100){
                num = "0"+num;
            }
            String sname = "sync"+num;
            String url = "/realSync"+num;
            String sql = "INSERT IGNORE INTO t_inter_sync(sname,projectId,type,servletClass,url,isUse) VALUES('"+sname+"',"+projectId+",0,'com.liangzhmj.cat.api.servlet.DomainAction','"+url+"',1)";
            log.info(">>>>>"+sql);
            baseDao.insertSQL(sql);
        }

    }

}
