package com.liangzhmj.cat.api.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SyncAdapter {

	/**
	 * 分析同步数据（做异常处理，保证返回数据）
	 * @param request
	 * @param response
	 * @return
	 */
	String doAnalyse(HttpServletRequest request, HttpServletResponse response);
}
