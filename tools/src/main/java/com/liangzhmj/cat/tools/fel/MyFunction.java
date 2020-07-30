package com.liangzhmj.cat.tools.fel;

import com.greenpineyu.fel.function.CommonFunction;
import lombok.extern.log4j.Log4j2;


/**
 * fel自定义函数抽象类
 * @author liangzhmj
 *
 */
@Log4j2
public abstract class MyFunction extends CommonFunction{

	
	@Override
	public int hashCode() {
		return ("FEL_FUN_"+getName()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null){return false;}
		if(!(obj instanceof MyFunction)){return false;}
		MyFunction o = (MyFunction)obj;
		//函数名相同
		if(getName().equals(o.getName())){
			return true;
		}
		return false;
	}
}
