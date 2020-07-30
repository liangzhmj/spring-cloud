package com.liangzhmj.cat.tools.http;


import com.liangzhmj.cat.tools.json.JSONBase;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;


/**
 * 接收外部数据的工具类
 * @author liangzh
 *
 */
@Log4j2
public class ReceiveDataUtils {

	

	/**
	 * 根据clazz中的属性名从HttpServletRequest中获取参数,并填充到clazz实例的属性中
	 * @param <T>
	 * @param request
	 * @return
	 */
	public static <T> T getReceiveData(HttpServletRequest request, Class<T> clazz)
			throws Exception {
		
		Field[] fs = clazz.getDeclaredFields();
		if(fs == null || fs.length == 0){
			log.error(clazz+"没有属性");
			throw new Exception(clazz+"没有属性");
		}
		T voData = clazz.newInstance();
		for (Field field : fs) {
			String fname = field.getName();
			if(StringUtils.isEmpty(fname)){
				continue;
			}
			String value = request.getParameter(fname);
			if(value != null){//获取到value的值
				try {
					BeanUtils.copyProperty(voData, fname, value);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		log.info("组装vo对象成功,voData = " + voData);
		return voData;
	}

	/**
	 * 根据clazz中的属性名从HttpServletRequest中获取参数,并填充到clazz实例的属性中
	 * @param <T>
	 * @param request
	 * @param clazz
	 * @param alias 别名<属性名,参数名>
	 * @return
	 */
	public static <T> T getReceiveData(HttpServletRequest request, Class<T> clazz,Map<String,String> alias)
			throws Exception {
		if(alias == null || alias.isEmpty()){
			return ReceiveDataUtils.getReceiveData(request, clazz);
		}
		Field[] fs = clazz.getDeclaredFields();
		if(fs == null || fs.length == 0){
			log.error(clazz+"没有属性");
			throw new Exception(clazz+"没有属性");
		}
		T voData = clazz.newInstance();
		for (Field field : fs) {
			String fname = field.getName();
			if(StringUtils.isEmpty(fname)){
				continue;
			}
			String paramName = fname;
			if(alias.containsKey(fname)){
				paramName = alias.get(fname);//用别名去获取参数
			}
			String value = request.getParameter(paramName);
			if(value != null){//获取到参数
				try {
					BeanUtils.copyProperty(voData, fname, value);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		log.info("组装vo对象成功,voData = " + voData);
		return voData;
	}
	
	/**
	 * 根据clazz中的属性名从HttpServletRequest中获取参数,并填充到clazz实例的属性中(只支持一层的xml)
	 * @param <T>
	 * @param request
	 * @param clazz
	 * @return
	 */
	public static <T> T getReceiveXMLData(HttpServletRequest request, Class<T> clazz)
			throws Exception {
		
		Field[] fs = clazz.getDeclaredFields();
		if(fs == null || fs.length == 0){
			log.error(clazz+"没有属性");
			throw new Exception(clazz+"没有属性");
		}
		String dataString = "";
		//通过流获取POST的xml数据
		InputStream in = request.getInputStream();
		dataString =  IOUtils.toString(in, "UTF-8");
		if(StringUtils.isEmpty(dataString)){
			throw new RuntimeException("接收到数据为空");
		}	
		Document document = DocumentHelper.parseText(dataString);
		Element rq = document.getRootElement();
		T voData = clazz.newInstance();
		for (Field field : fs) {
			String fname = field.getName();
			if(StringUtils.isEmpty(fname)){
				continue;
			}
			String value = rq.elementText(fname);
			if(value != null){
				try {
					BeanUtils.copyProperty(voData, fname, value);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		log.info("组装vo对象成功,voData = " + voData);
		return voData;
	}

	/**
	 * 根据clazz中的属性名从HttpServletRequest中获取参数,并填充到clazz实例的属性中
	 * @param <T>
	 * @param request
	 * @param clazz(继承JSONBase)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getReceiveJSONData(HttpServletRequest request, Class<T> clazz)
			throws Exception {
		Field[] fs = clazz.getDeclaredFields();
		if(fs == null || fs.length == 0){
			log.error(clazz+"没有属性");
			throw new Exception(clazz+"没有属性");
		}
		String dataString = "";
		//通过流获取POST的xml数据
		InputStream in = request.getInputStream();
		dataString =  IOUtils.toString(in, "UTF-8");
		if(StringUtils.isEmpty(dataString)){
			throw new RuntimeException("接收到数据为空");
		}
		JSONBase voData = (JSONBase)clazz.newInstance();
		voData.fromJSON(JSONObject.fromObject(dataString));
		log.info("组装vo对象成功,voData = " + voData);
		return (T)voData;
	}
}
