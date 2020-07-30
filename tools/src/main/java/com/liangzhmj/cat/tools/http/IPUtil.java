package com.liangzhmj.cat.tools.http;

import com.liangzhmj.cat.tools.string.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具类
 */
public class IPUtil {


	public static String getIp(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		String ipaddress = null;
		if (request.getHeader("x-forwarded-for") == null) {
			ipaddress = request.getRemoteAddr();
		} else {
			ipaddress = request.getHeader("x-forwarded-for");
		}
		if (!StringUtils.isEmpty(ipaddress)) {
			String[] s = ipaddress.split(",");
			if (s.length == 1) {
				ipaddress = s[0].trim();
			} else if (s.length > 1) {
				ipaddress = s[s.length - 2].trim();
			}
		}
		return ipaddress;
	}

}
