package com.liangzhmj.cat.tools.json;

import java.util.List;


/**
 * 数组的包装结果
 * @author liangzhmj
 *
 */
@SuppressWarnings("rawtypes")
public class ArrayWrap extends JSONBase{

	@JSONField
	private List datas;

	public ArrayWrap(){}
	
	public ArrayWrap(List datas) {
		this.datas = datas;
	}

	public List getDatas() {
		return datas;
	}

	public void setDatas(List datas) {
		this.datas = datas;
	}
	
}
