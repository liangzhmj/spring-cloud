package com.liangzhmj.cat.tools.fel.fun;

import com.liangzhmj.cat.tools.fel.FelEngineUtils;
import com.liangzhmj.cat.tools.fel.MyFunction;

/**
 * 判断字符串是否为null或者空串
 * @author liangzhmj
 *
 */
public class IsEmpty extends MyFunction {

	public IsEmpty(){
		FelEngineUtils.addFun(this);
	}
	
	@Override
	public String getName() {
		return "isEmpty";
	}

	@Override
	public Object call(Object[] arg0) {
		Object obj = arg0[0];
		if(obj == null){return 1;}
		String str = (String)obj;
		if(str == null || str.equals("")){return 1;}
		return 0;
	}

}
