package com.liangzhmj.cat.tools.num;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DecimalUtils {

	private static Map<String,DecimalFormat> dfs = new HashMap<String,DecimalFormat>();
	static {
		DecimalFormat df1= new DecimalFormat("#.#");
		DecimalFormat df2= new DecimalFormat("#.##");
		DecimalFormat df3= new DecimalFormat("#.###");
		DecimalFormat df4= new DecimalFormat("#.####");
		DecimalFormat df5= new DecimalFormat("#.0");
		DecimalFormat df6= new DecimalFormat("#.00");
		DecimalFormat df7= new DecimalFormat("#.000");
		DecimalFormat df8= new DecimalFormat("#.0000");
		dfs.put("#.#",df1);
		dfs.put("#.##",df2);
		dfs.put("#.###",df3);
		dfs.put("#.####",df4);
		dfs.put("#.0",df5);
		dfs.put("#.00",df6);
		dfs.put("#.000",df7);
		dfs.put("#.0000",df8);
	}
	
	public static String getFormatNum(String format,double number){
		DecimalFormat df = dfs.get(format);
		return df.format(number);
	}
	
	public static double clearDecimal(double src,int decimal){
		if(decimal < 0 || decimal > 10){
			return src;
		}
		double temp = 1;
		for (int i = 0; i < decimal; i++) {
			temp *= 10;
		}
		src = ((double)Math.round(src*temp))/temp;
		return src;
	}
	
}
