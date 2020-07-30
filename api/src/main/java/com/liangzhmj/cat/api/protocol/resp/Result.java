package com.liangzhmj.cat.api.protocol.resp;

import com.liangzhmj.cat.api.enums.APIExceptionEnum;
import com.liangzhmj.cat.api.enums.ResultEnum;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.dao.dbconfig.DBProperties;
import com.liangzhmj.cat.tools.exception.CatExceptionEnum;
import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.json.JSONField;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * api请求响应结果
 * @liangzhmj
 */
@Getter
@Setter
public class Result extends JSONBase {

    @JSONField
    private String code;//返回的状态码
    @JSONField
    private String debugMsg;//包含真实的调试信息
    @JSONField
    private String resultMsg;//仅提供给用户看的，友好的信息
    @JSONField
    private Object data;//业务数据
    @JSONField
    private Object extData;//返回扩展字段

    /**
     * 返回成功Result
     * @param data
     * @return
     */
    public static Result success(Object data){
        Result res = new Result();
        res.setCode(ResultEnum.SUCCESS.getCode());
        res.setData(data);
        return res;
    }

    /**
     * 返回成功Result
     * @return
     */
    public static Result success(){
        return success(null);
    }

    /**
     * 返回失败Result
     * @param code
     * @param resultMsg
     * @return
     */
    public static Result fail(String code,String resultMsg){
        Result res = new Result();
        res.setCode(code);
        res.setResultMsg(resultMsg);
        return res;
    }

    /**
     * 返回失败Result
     * @param e
     * @return
     */
    public static Result fail(APIException e){
        Result res = new Result();
        if(StringUtils.isEmpty(e.getCode())){//只有一段message
            res.setCode(APIExceptionEnum.FAIL_SERVICE_OPERATION.getCode());
        }else{
            res.setCode(e.getCode());
        }
        if(StringUtils.isEmpty(e.getHint())){
            res.setResultMsg(CatExceptionEnum.SERVER_BUSY);
        }else{
            res.setResultMsg(e.getHint());
        }
        if(!StringUtils.isEmpty(e.getMessage()) && Boolean.valueOf(DBProperties.getProperty("api.returnDebug","false"))){
            res.setDebugMsg(e.getMessage());
        }
        return res;
    }

    /**
     * 返回失败Result
     * @param code
     * @param resultMsg
     * @param debugMsg
     * @return
     */
    public static Result fail(String code,String resultMsg,String debugMsg){
        Result res = new Result();
        res.setCode(code);
        res.setResultMsg(resultMsg);
        if(!StringUtils.isEmpty(debugMsg) && Boolean.valueOf(DBProperties.getProperty("api.returnDebug","false"))){
            res.setDebugMsg(debugMsg);
        }
        return res;
    }

}
