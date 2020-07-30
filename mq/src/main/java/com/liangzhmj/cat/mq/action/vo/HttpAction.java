package com.liangzhmj.cat.mq.action.vo;

import com.liangzhmj.cat.tools.http.HttpUtils;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.UnsupportedEncodingException;
import java.util.Map;
/**
 * 发送http请求的一些信息
 * @author liangzhmj
 *
 */
@Log4j2
@Getter
@Setter
public abstract class HttpAction extends AbstractAction{


	/** 请求发送的方法 **/
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String RESULT_EXCEPTION = "EXCEPTION";
	
	/** 目的地址 **/
	protected String url;
	/** 发送的方式(默认post) **/
	protected String method = METHOD_POST;
	protected Map<String,String> headers;
	/** byte数组参数 **/
	protected byte[] params;//还可以扩展更多的参数形式，例如map
	/** 复位发送次数 (默认5次) **/
	protected int retime = 5;
	/** 超时时间(毫秒) **/
	protected int timeout;
	/**访问结果**/
	protected String resp;

	/**
	 * 判断响应是否成功
	 * @return
	 */
	public abstract boolean isSuccess();
	
	@Override
	public boolean doAction() {
		//POST提交
		try {
			if(METHOD_POST.equals(method)){
				resp = HttpUtils.post(this.url,this.headers,this.getParams(),"UTF-8",this.timeout);
			}
			//GET提交
			else{
				resp = HttpUtils.get(this.url,headers,"UTF-8",this.timeout);
			}
		} catch (Exception e) {
			resp =  RESULT_EXCEPTION;
			log.error(this.name+" 发送http请求未知错误:",e);
		}
		return this.isSuccess();
	}
	
	@Override
	public abstract void onSuccess();
	
	@Override
	public abstract void onFail();
	
	@Override
	public void prepareForAction() {
		//调用一次，最大剩余执行次数减一
		this.reduceOnetime();
	}
	/**
	 * 减少一次
	 */
	public void reduceOnetime(){
		this.time -= 1;
	}
	
	/**
	 * 增加一次
	 */
	public void addOnetime(){
		this.time += 1;
	}
	
	/**
	 * 重新回复默认值
	 */
	public void timeRenewDefault(){
		this.time = retime;
	}
	
	/**
	 * 是否还需要队列处理
	 * @return
	 */
	public boolean isValid(){
		return this.time > 0 ? true:false;
	}
	
	@Override
	public String toString() {
		String temp = null;
		try {
			if(params !=null){
				temp = new String(params,"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		temp = StringUtils.substring(1000, temp);;
		return "HttpAction [url=" + url + ", method=" + method + ", headers="
				+ headers + ", params=" + temp + ", time="
				+ time + ",resp="+resp+", timeout=" + timeout + "]";
	}

}
