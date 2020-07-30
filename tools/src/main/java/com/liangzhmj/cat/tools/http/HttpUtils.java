package com.liangzhmj.cat.tools.http;

import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Log4j2
public class HttpUtils {

	public static String BAIDU_UA = "Baiduspider";
	
	private static ConnectionSocketFactory plainsf;
	private static LayeredConnectionSocketFactory sslsf;
	private static Registry<ConnectionSocketFactory> registry;
	private static PoolingHttpClientConnectionManager cm;
	private static CloseableHttpClient httpClient;

	// 初始化连接池
	static {
		plainsf = PlainConnectionSocketFactory.getSocketFactory();
		sslsf = SSLConnectionSocketFactory.getSocketFactory();
		registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", plainsf).register("https", sslsf)
				.build();
		cm = new PoolingHttpClientConnectionManager(registry);
		// 将最大连接数增加到200
		cm.setMaxTotal(200);
		// 将每个路由基础的连接增加到20
		cm.setDefaultMaxPerRoute(20);
		// 请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= 5) {// 如果已经重试了5次，就放弃
					return false;
				}
				if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
					return true;
				}
				if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
					return false;
				}
				if (exception instanceof InterruptedIOException) {// 超时
					return false;
				}
				if (exception instanceof UnknownHostException) {// 目标服务器不可达
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
					return false;
				}
				if (exception instanceof SSLException) {// ssl握手异常
					return false;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
		httpClient = HttpClients.custom().setConnectionManager(cm).setRetryHandler(httpRequestRetryHandler).build();
	}

	public static String get(String url, Map<String, String> headers,String encoding, int timeout) {
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		setHeaders(httpget, headers);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpget.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			String result = getStringResult(response,encoding);
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}
	
	
	public static String get(String url,int timeout) {
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpget.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			String result = getStringResult(response,"UTF-8");
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}

	public static byte[] getByteArray(String url,String contentType,int timeout) throws Exception {
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpget.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			//检查Content-Type
			checkContentType(response, contentType);
			byte[] result = EntityUtils.toByteArray(response.getEntity());
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
	public static byte[] getByteArray(String url,int timeout) throws Exception {
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpget.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			byte[] result = EntityUtils.toByteArray(response.getEntity());
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
	
	public static String get(String url, String ua,String referer, String encoding, int timeout) {
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("User-Agent", ua);
		httpget.setHeader("Referer", referer);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpget.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			String result = getStringResult(response,encoding);
			// 释放
			EntityUtils.consume(response.getEntity());
			if(result != null){
				result = result.replace("\r\n", "");
			}
			return result;
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}

	public static String get(String url, String ua, String encoding, int timeout) {
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("User-Agent", ua);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpget.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			String result = getStringResult(response,encoding);
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}
	
	public static String post(String url, Map<String, String> headers, File data, int timeout) {
		HttpPost httpPost = new HttpPost(url);
		MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
		mEntityBuilder.addBinaryBody("media", data);
        httpPost.setEntity(mEntityBuilder.build());
        setHeaders(httpPost, headers);
		CloseableHttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpPost);
			String result = getStringResult(response,"UTF-8");
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}
	public static String postMedia(String url, Map<String, String> headers, byte[] data,ContentType contentType,String filename, int timeout) {
		HttpPost httpPost = new HttpPost(url);
		MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
		mEntityBuilder.addPart("media", new ByteArrayBody(data, contentType,filename));
        httpPost.setEntity(mEntityBuilder.build());
        setHeaders(httpPost, headers);
		CloseableHttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpPost);
			String result = getStringResult(response,"UTF-8");
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}

	public static String post(String url, Map<String, String> headers, byte[] data, int timeout) {
		HttpPost httpPost = new HttpPost(url);
		setHeaders(httpPost, headers);
		CloseableHttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(requestConfig);
		try {
			httpPost.setEntity(new ByteArrayEntity(data));
			response = httpClient.execute(httpPost);
			String result = getStringResult(response,"UTF-8");
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}

	public static String post(String url, Map<String, String> headers, String data, String encoding, int timeout) {
		HttpPost httpPost = new HttpPost(url);
		setHeaders(httpPost, headers);
		CloseableHttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(requestConfig);
		try {
			httpPost.setEntity(new StringEntity(data, ContentType.create("application/json", "UTF-8")));
			response = httpClient.execute(httpPost);
			String result = getStringResult(response,encoding);
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}

	public static String post(String url, Map<String, String> headers, byte[] data, String encoding, int timeout) {
		HttpPost httpPost = new HttpPost(url);
		setHeaders(httpPost, headers);
		CloseableHttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(requestConfig);
		try {
			httpPost.setEntity(new ByteArrayEntity(data));
			response = httpClient.execute(httpPost);
			String result = getStringResult(response,encoding);
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}

	public static byte[] postByteArray(String url, Map<String, String> headers, byte[] data,String contentType,int timeout) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		setHeaders(httpPost, headers);
		CloseableHttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(requestConfig);
		try {
			httpPost.setEntity(new ByteArrayEntity(data));
			response = httpClient.execute(httpPost);
			//检查Content-Type
			checkContentType(response, contentType);
			byte[] resByte = EntityUtils.toByteArray(response.getEntity());
			// 释放
			EntityUtils.consume(response.getEntity());
			return resByte;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
	public static byte[] postByteArray(String url, Map<String, String> headers, byte[] data,int timeout) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		setHeaders(httpPost, headers);
		CloseableHttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(requestConfig);
		try {
			httpPost.setEntity(new ByteArrayEntity(data));
			response = httpClient.execute(httpPost);
			byte[] resByte = EntityUtils.toByteArray(response.getEntity());
			// 释放
			EntityUtils.consume(response.getEntity());
			return resByte;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
	
	public static void checkContentType(CloseableHttpResponse response,String contentType) throws Exception{
		Header[] respheaders = response.getHeaders("Content-Type");
		boolean match = false;
		for (Header h : respheaders) {
			System.out.println(h.getName()+":"+h.getValue());
			if(h.getValue().toLowerCase().indexOf(contentType.toLowerCase()) != -1){
				match = true;
				break;
			}
		}
		if(!match){
			String result = getStringResult(response,"UTF-8");
			// 释放
			EntityUtils.consume(response.getEntity());
			throw new RuntimeException(result);
		}
	}

	public static String post(String url, Map<String, String> headers, Map<String, String> params, String encoding,
			int timeout) {
		HttpPost httpPost = new HttpPost(url);
		setHeaders(httpPost, headers);
		CloseableHttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(requestConfig);
		try {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (params != null && !params.isEmpty()) {
				Set<Entry<String, String>> entries = params.entrySet();
				for (Entry<String, String> entry : entries) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			if (!nvps.isEmpty()) {
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			}
			response = httpClient.execute(httpPost);
			String result = getStringResult(response,encoding);
			// 释放
			EntityUtils.consume(response.getEntity());
			return result;
		} catch (IOException e) {
			log.error(e);;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
		return null;
	}
	
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
			result = EntityUtils.toString(new GzipDecompressingEntity(response.getEntity()), StringUtils.isEmpty(encoding)?"UTF-8":encoding);
		} else {
			result = EntityUtils.toString(response.getEntity(),StringUtils.isEmpty(encoding)?"UTF-8":encoding);
		}
		return result;
	}

	private static HttpRequest setHeaders(HttpRequest request, Map<String, String> headers) {
		if (headers != null && !headers.isEmpty()) {
			Set<Entry<String, String>> ens = headers.entrySet();
			for (Entry<String, String> en : ens) {
				request.setHeader(en.getKey(), en.getValue());
			}
		}
		return request;
	}

	/**
	 * 复制网络图片保存本地
	 * @param url
	 * @param savePath
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static String copyNetImage(String url,String savePath,int timeout) throws Exception {
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpget.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			String ext = "";
			try {
				Header[] hs = response.getHeaders("Content-type");
				String contentType = hs[0].getValue();
				if(contentType.indexOf("webp") != -1){//转成jpg
					savePath += ".jpg";
					File temp = new File(savePath);
					try {
						BufferedImage im = ImageIO.read(response.getEntity().getContent());
						ImageIO.write(im, "jpg", temp);
					} catch (IOException e) {
						log.error(e);
					}
					return savePath;
				}
				if(contentType.indexOf("/gif") != -1){
					ext = ".gif";
				}else {
					ext = ".jpg";
				}
			} catch (Exception e) {
				log.error("错误的contentType:"+url);
			}
			byte[] result = EntityUtils.toByteArray(response.getEntity());
			savePath += ext;
			FileUtils.writeByteArrayToFile(new File(savePath), result);
			return savePath;
		} finally {
			// 释放
			EntityUtils.consume(response.getEntity());
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

	/**
	 * 复制网络图片返回直接数组
	 * @param url
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static Object[] copyNetImage(String url,int timeout) throws Exception {
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpget.setConfig(requestConfig);
		try {
			response = httpClient.execute(httpget, HttpClientContext.create());
			String ext = "";
			try {
				Header[] hs = response.getHeaders("Content-type");
				String contentType = hs[0].getValue();
				if(contentType.indexOf("webp") != -1){//转成jpg
					@Cleanup ByteArrayOutputStream out = new ByteArrayOutputStream();
					try {
						BufferedImage im = ImageIO.read(response.getEntity().getContent());
						ImageIO.write(im, "jpg", out);
					} catch (IOException e) {
						log.error(e);
					}
					return new Object[]{out.toByteArray(),"jpg"};
				}
				if(contentType.indexOf("/gif") != -1){
					ext = "gif";
				}else {
					ext = "jpg";
				}
			} catch (Exception e) {
				log.error("错误的contentType:"+url);
			}
			byte[] result = EntityUtils.toByteArray(response.getEntity());
			return new Object[]{result,ext};
		} finally {
			// 释放
			EntityUtils.consume(response.getEntity());
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}

}
