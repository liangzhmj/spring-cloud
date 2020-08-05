package com.liangzhmj.cat.api.protocol.req;

import com.liangzhmj.cat.api.enums.APIExceptionEnum;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.model.InterInfo;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.json.JSONField;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求的协议的包装类
 */
@SuppressWarnings("serial")
@Log4j2
@Setter
@Getter
public class APIReq extends JSONBase implements Serializable{


	@JSONField
	private int version;
	@JSONField
	private String operation;
	/** 是否跨域0:否,1:jsonp跨域，2:cors跨域 **/
	@JSONField(clazz=JSONBase.INTEGER)
	private int crossDomain;
	@JSONField
	private String sessionId;
	@JSONField
	private String authKey;
	@JSONField
	private String interId;

	//-----------------设置一些基本的常用字段，复杂的放params----------------------start
	@JSONField
	private String method;
	@JSONField(clazz=JSONBase.INTEGER)
	private int id;
	@JSONField
	private String sid;
	@JSONField
	private String strParam;
	@JSONField(clazz=JSONBase.DOUBLE)
	private double douParam;
	//-----------------设置一些基本的常用字段，复杂的放params----------------------end

	@JSONField(clazz=JSONBase.JSONOBJECT)
	private JSONObject params;
	//内部载体
	private InterInfo interInfo;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private int dataType;
	private Session session;
	@JSONField
	private String sequence;
	
	public APIReq(){}
	public APIReq(HttpServletRequest request , HttpServletResponse response){
		this.request = request;
		this.response = response;
	}
	
	public void reciveRequest() throws Exception{
		InputStream in = null;
		String requestStr = null;
		try {
//			String a = request.getParameter("a");//协议，a==1-->protobuf
			in = request.getInputStream();
			//json数据
			requestStr = IOUtils.toString(in,"UTF-8");
			if(StringUtils.isEmpty(requestStr)){
				this.crossDomain = StringUtils.getCleanInteger(request.getParameter("crossDomain"));
				if(this.crossDomain == 2){//跨域post
					initParams(getFirst(request.getParameterMap()));
					return ;
				}
				//get方式拼接url
				requestStr = URLDecoder.decode(request.getParameter("params"),"UTF-8");
			}
			JSONObject json = JSONObject.fromObject(requestStr);
			//参数赋值
			this.fromJSON(json);
		}catch (Exception e) {
			log.error(("协议解析出错:"+e.getMessage()+"-->"+StringUtils.substring(1000,requestStr)));
			throw new APIException(APIExceptionEnum.FAIL_PROTOCOL_ERROR);
		} finally{
			log.info("请求码【"+this.interId+"】-->请求数据:"+requestStr);
		}
	}

	private Map<String,String> getFirst(Map<String,String[]> src){
		Map<String,String> params = new HashMap<>();
		if(src == null){
			return params;
		}
		for (String s : src.keySet()) {
			String[] values = src.get(s);
			if(!CollectionUtils.isEmpty(values)){
				params.put(s,values[0]);
			}
		}
		return params;
	}

	private void initParams(Map<String,String> params){
		if(CollectionUtils.isEmpty(params)){
			return ;
		}
		//因为map里面的value，全是字符串类型，直接放fromObject，可能类型对不上，这里笨一点，手动设置
		this.version = StringUtils.getCleanInteger(params.get("version"));
		this.operation = StringUtils.getCleanString(params.get("operation"));
		this.sessionId = StringUtils.getCleanString(params.get("sessionId"));
		this.authKey = StringUtils.getCleanString(params.get("authKey"));
		this.interId = StringUtils.getCleanString(params.get("interId"));
		this.sequence = StringUtils.getCleanString(params.get("sequence"));
		//-----------------设置一些基本的常用字段，复杂的放params----------------------start
		this.method = StringUtils.getCleanString(params.get("method"));
		this.id = StringUtils.getCleanInteger(params.get("id"));
		this.sid = StringUtils.getCleanString(params.get("sid"));
		this.strParam = StringUtils.getCleanString(params.get("strParam"));
		this.douParam = StringUtils.getCleanDouble(params.get("douParam"));
		String p = params.get("params");
		if(p != null){
			this.params = JSONObject.fromObject(p);
		}
	}
	
}
