package com.liangzhmj.cat.api.service;

import net.sf.json.JSONObject;

/**
 * 接口文档业务接口
 * @author liangzhmj
 */
public interface APIDocService {

    void initDoc() throws Exception;
    JSONObject apiDoc();
}
