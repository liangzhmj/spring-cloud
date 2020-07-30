package com.liangzhmj.cat.api.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

@WebFilter(filterName="EncodingFilter",urlPatterns="/*", initParams=@WebInitParam(name="encoding", value="UTF-8"))
public class EncodingFilter implements Filter {

	private String enc = "UTF-8";
	
	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		req.setCharacterEncoding(enc);
		resp.setCharacterEncoding(enc);
		chain.doFilter(req, resp);
	}

	public void init(FilterConfig arg0) throws ServletException {
		enc = arg0.getInitParameter("encoding");
		if(enc == null || "".equals(enc)){
			enc = "UTF-8";
		}
		System.out.println("初始化字符编码过滤器");
	}

}
