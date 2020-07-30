package com.liangzhmj.cat.tools.nlp;

import com.liangzhmj.cat.tools.collection.CollectionUtils;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CnToSpell {

	/**    
	 * 获取拼音集合    
	 * @author wyh    
	 * @param src    
	 * @return Set<String>    
	 */
	public static List<String> getPinyin(String src) {
		if (src != null && !src.trim().equalsIgnoreCase("")) {
			char[] srcChar;
			srcChar = src.toCharArray();
			//汉语拼音格式输出类      
			HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();

			//输出设置，大小写，音标方式等      
			hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

			String[][] temp = new String[src.length()][];
			for (int i = 0; i < srcChar.length; i++) {
				char c = srcChar[i];
				//是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)      
				if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
					try {
						temp[i] = PinyinHelper.toHanyuPinyinStringArray(
								srcChar[i], hanYuPinOutputFormat);
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				} else if (((int) c >= 65 && (int) c <= 90)
						|| ((int) c >= 97 && (int) c <= 122)
						|| ((int) c >= 33 && (int) c <= 46)
						|| ((int) c >= 48 && (int) c <= 57)) {
					temp[i] = new String[] { String.valueOf(srcChar[i]) };
				} else {
					temp[i] = new String[] { "" };
				}
			}
			String[] pingyinArray = Exchange(temp);
			List<String> pinyinSet = new ArrayList<String>();
			for (int i = 0; i < pingyinArray.length; i++) {
				String py = pingyinArray[i];
				if(!pinyinSet.contains(py)){
					pinyinSet.add(py);
				}
			}
			return pinyinSet;
		}
		return null;
	}

	/**    
	 * 递归    
	 * @author wyh    
	 * @param strJaggedArray    
	 * @return    
	 */
	public static String[] Exchange(String[][] strJaggedArray) {
		String[][] temp = DoExchange(strJaggedArray);
		return temp[0];
	}

	/**    
	 * 递归    
	 * @author wyh    
	 * @param strJaggedArray    
	 * @return    
	 */
	private static String[][] DoExchange(String[][] strJaggedArray) {
		int len = strJaggedArray.length;
		if (len >= 2) {
			int len1 = strJaggedArray[0].length;
			int len2 = strJaggedArray[1].length;
			int newlen = len1 * len2;
			String[] temp = new String[newlen];
			int Index = 0;
			for (int i = 0; i < len1; i++) {
				for (int j = 0; j < len2; j++) {
					temp[Index] = strJaggedArray[0][i] + strJaggedArray[1][j];
					Index++;
				}
			}
			String[][] newArray = new String[len - 1][];
			for (int i = 2; i < len; i++) {
				newArray[i - 1] = strJaggedArray[i];
			}
			newArray[0] = temp;
			return DoExchange(newArray);
		} else {
			return strJaggedArray;
		}
	}
	
	/**
	 * 模糊获取一个字符串的拼音集合
	 * @param word
	 * @return
	 */
	public static Set<String> getMohu(char word){
		List<String> pys = CnToSpell.getPinyin(String.valueOf(word));
		Set<String> res = new HashSet<>();
		if(CollectionUtils.isEmpty(pys)){
			return res;
		}
		for (String py : pys) {
			int length = py.length();
			//先保存原样
			res.add(py);
			if(length < 2){
				continue;
			}
			//换头
			String adapterHeader = getPreffix(py);
			res.add(adapterHeader);
			//换脚
			String adapterfooter = getSuffix(py);
			res.add(adapterfooter);
			//换头&换脚
			String adapter = getSuffix(adapterHeader);
			res.add(adapter);
		}
		return res;
	}
	
	private static String getPreffix(String py){
		//z<=>zh
		if(py.charAt(0) == 'z' && py.charAt(1) != 'h'){
			return py.replaceFirst("z", "zh");
		}
		//zh<=>z
		if(py.startsWith("zh")){
			return py.replaceFirst("zh", "z");
		}
		//c<=>ch
		if(py.charAt(0) == 'c' && py.charAt(1) != 'h'){
			return py.replaceFirst("c", "ch");
		}
		//ch<=>c
		if(py.startsWith("ch")){
			return py.replaceFirst("ch", "c");
		}
		//s<=>sh
		if(py.charAt(0) == 's' && py.charAt(1) != 'h'){
			return py.replaceFirst("s", "sh");
		}
		//sh<=>s
		if(py.startsWith("sh")){
			return py.replaceFirst("sh", "s");
		}
		//n<=>l
		if(py.startsWith("n")){
			return py.replaceFirst("n", "l");
		}
		//l<=>n
		if(py.startsWith("l")){
			return py.replaceFirst("l", "n");
		}
		//r<=>y
		if(py.startsWith("r")){
			return py.replaceFirst("r", "y");
		}
		//y<=>r
		if(py.startsWith("y")){
			return py.replaceFirst("y", "r");
		}
		//h<=>f
		if(py.startsWith("h")){
			return py.replaceFirst("h", "f");
		}
		//f<=>h
		if(py.startsWith("f")){
			return py.replaceFirst("f", "h");
		}
		return py;
	}
	
	private static String getSuffix(String py){
		//an<=>ang
		if(py.endsWith("an")){
			return py.replaceAll("an", "ang");
		}
		//ang<=>an
		if(py.endsWith("ang")){
			return py.replaceAll("ang", "an");
		}
		//en<=>eng
		if(py.endsWith("en")){
			return py.replaceAll("en", "eng");
		}
		//eng<=>en
		if(py.endsWith("eng")){
			return py.replaceAll("eng", "en");
		}
		//in<=>ing
		if(py.endsWith("in")){
			return py.replaceAll("in", "ing");
		}
		//ui<=>ei
		if(py.endsWith("ui")){
			return py.replaceAll("ui", "ei");
		}
		//ei<=>ui
		if(py.endsWith("ei")){
			return py.replaceAll("ei", "ui");
		}
		return py;
	}
}
