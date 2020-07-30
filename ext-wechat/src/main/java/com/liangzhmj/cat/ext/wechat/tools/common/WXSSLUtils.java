package com.liangzhmj.cat.ext.wechat.tools.common;

import com.liangzhmj.cat.ext.wechat.config.ConfigContext;
import com.liangzhmj.cat.ext.wechat.exception.WechatException;
import com.liangzhmj.cat.tools.string.StringUtils;
import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信带证书的请求工具
 * @author liangzhmj
 */
public class WXSSLUtils {

	private static Logger log = Logger.getLogger(WXSSLUtils.class);
	public static Map<String,CloseableHttpClient> clients = new HashMap<>();
	
	public static CloseableHttpClient getHttpClient(String mchid) throws Exception{
		if(StringUtils.isEmpty(mchid)){
			throw new WechatException("mchid不能为空");
		}
		CloseableHttpClient httpClient = clients.get(mchid);
		if(httpClient != null){
			return httpClient;
		}
		synchronized (clients) {
			httpClient = clients.get(mchid);
			if(httpClient == null){
				KeyStore keyStore  = KeyStore.getInstance("PKCS12");
				FileInputStream instream = new FileInputStream(new File(ConfigContext.getWechatConfig().getCertPathPrefix()+mchid+".p12"));//这里约定证书以商户号命名
				try {
					keyStore.load(instream, mchid.toCharArray());
				} finally {
					instream.close();
				}
				SSLContext sslcontext = SSLContexts.custom()
						.loadKeyMaterial(keyStore, mchid.toCharArray())
						.build();
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslcontext,
						new String[] {"TLSv1"},
						null,
						SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
				httpClient = HttpClients.custom()
						.setSSLSocketFactory(sslsf)
						.build();
				clients.put(mchid, httpClient);
			}
		}
        return httpClient;
	}


	/**
	 * 带证书的请求
	 * @param url
	 * @param mchId
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String request(String url,String mchId,byte[] data) throws Exception{
		CloseableHttpClient httpClient = getHttpClient(mchId);
		HttpPost post = new HttpPost(url);
		post.setEntity(new ByteArrayEntity(data));
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(post);
			String result = getStringResult(response,"UTF-8");
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} finally {
			if(response != null){
				response.close();
			}
		}
	}

	/**
	 * 分析结果
	 * @param response
	 * @param encoding
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	private static String getStringResult(CloseableHttpResponse response,String encoding) throws ParseException, IOException{
		if(response == null){
			return null;
		}
		Header[] respheaders = response.getHeaders("Content-Encoding");
		boolean isGzip = false;
		for (Header h : respheaders) {
			if (h.getValue().equals("gzip")) {
				// 返回头中含有gzip
				isGzip = true;
			}
		}
		String result = null;
		if (isGzip) {
			// 需要进行gzip解压处理
			result = EntityUtils.toString(new GzipDecompressingEntity(response.getEntity()),StringUtils.isEmpty(encoding)?"UTF-8":encoding);
		} else {
			result = EntityUtils.toString(response.getEntity(),StringUtils.isEmpty(encoding)?"UTF-8":encoding);
		}
		return result;
	}
}
