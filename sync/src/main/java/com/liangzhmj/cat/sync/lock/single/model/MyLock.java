package com.liangzhmj.cat.sync.lock.single.model;

import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 我的同步锁
 * @author liangzhmj
 *
 */
@Setter
@Getter
public class MyLock implements Serializable{

	private List elems = new ArrayList();
	
	public MyLock(){}
	
	public MyLock(Object obj){
		if(obj == null){
			return ;
		}
		if(obj instanceof JSONArray){//json数组
			JSONArray ja = (JSONArray)obj;
			for (Object o : ja) {
				elems.add(o);
			}
			return;
		}
		if(obj instanceof Object[]){//数组
			Object[] infos = (Object[])obj;
			for (Object info : infos) {
				elems.add(info);
			}
			return;
		}
		if(obj instanceof Collection){//集合
			Collection cols = (Collection)obj;
			for (Object col : cols) {
				elems.add(col);
			}
			return ;
		}
		//普通类型
		elems.add(obj);
	}
	
}
