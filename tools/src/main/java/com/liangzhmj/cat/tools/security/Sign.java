package com.liangzhmj.cat.tools.security;

import com.liangzhmj.cat.tools.string.StringUtils;

/**
 * 签名
 */
public class Sign {

	public static final String calc(final String[] ss)throws Exception {
		if (ss == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0,len = ss.length; i < len; i++) {
			if(i>0) sb.append("&");
			sb.append(StringUtils.getCleanString(ss[i]));
		}
		return MD5Util.calc(sb.toString());
	}
}
