package com.liangzhmj.cat.tools.fel.fun;


import com.liangzhmj.cat.tools.fel.FelEngineUtils;
import com.liangzhmj.cat.tools.fel.MyFunction;

/**
 * fel自定义函数
 * 第一个参数为条件 属性a
 * 第二到n个参数为条件
 * 描述判断 属性a是否等于(equals)n个条件中的一个值
 * @author liangzhmj
 *
 */
public class Include extends MyFunction {

	public Include(){
		//把自己放进函数序列里
		FelEngineUtils.addFun(this);
	}
	
	
	@Override
	public String getName() {//方法名称
		return "in";
	}

	@Override
	public Object call(Object[] params) {//方法体
		int size = params.length;
		for (int i = 1; i < size; i++) {
			if(params[0].equals(params[i])){
				return 1;
			}
		}
		return 0;
	}

}
