package com.liangzhmj.cat.api.protocol.req;

import com.liangzhmj.cat.api.enums.APIExceptionEnum;
import com.liangzhmj.cat.api.exception.APIException;
import com.liangzhmj.cat.api.model.InterInfo;
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
	/** 是否跨域0:否,1:是 **/
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

	public void initServlet(HttpServletRequest request , HttpServletResponse response){
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
				requestStr = URLDecoder.decode(request.getParameter("params"),"UTF-8");
			}
			if(requestStr.matches("^params=.+$")){
				requestStr = URLDecoder.decode(requestStr,"UTF-8").substring(7);
			}
			JSONObject json = JSONObject.fromObject(requestStr);
			//参数赋值
			this.fromJSON(json);
			//解析协议Bean
			log.info("请求码【"+this.interId+"】-->请求数据:"+requestStr);
		}catch (Exception e) {
			log.error(("协议解析出错:"+e.getMessage()+"-->"+StringUtils.substring(1000,requestStr)));
			throw new APIException(APIExceptionEnum.FAIL_PROTOCOL_ERROR);
		}
	}
	
}
