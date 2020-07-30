package com.liangzhmj.cat.tools.fel;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.function.Function;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Set;

/**
 * fel引擎包装类
 * @author liangzhmj
 *
 */
@Log4j2
public class FelEngineUtils {


	public static Set<Function> myFuns = new HashSet<Function>();
	
	/**
	 * 创建一个fel引擎实例，并把自定义函数加到引擎中
	 * @return
	 */
	public static FelEngine newFelEngine(){
		long start = System.currentTimeMillis();
		FelEngine fel = new FelEngineImpl();
		//加载自定义参数
		if(!myFuns.isEmpty()){
			for (Function fun : myFuns) {
				fel.addFun(fun);
			}
		}
		long end = System.currentTimeMillis();
		log.info("创建FelEngine加载自定义函数耗时:"+(end - start)+"ms");
		return fel;
	}
	
	/**
	 * 添加自定义参数
	 * @param fun
	 * @return
	 */
	public static boolean addFun(Function fun){
		if(fun != null ){
			if(myFuns.contains(fun)){
				//已经存在，先移除，再更新
				myFuns.remove(fun);
			}
			return myFuns.add(fun);
		}
		return false;
	}
}
