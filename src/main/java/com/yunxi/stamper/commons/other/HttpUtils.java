package com.yunxi.stamper.commons.other;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.entity.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/***
 * 描述: http请求工具类
 * @author zhf
 */
@Slf4j
public class HttpUtils {

	private static final String UTF_8 = "utf-8";

	/***
	 * 发送POST请求
	 * @param url 地址
	 * @return 响应结果json字符串
	 */
	public static String postRequest(String url) {
		return postRequest(url, null, null);
	}

	/***
	 * 发送POST请求
	 * @param url 地址
	 * @param params 参数
	 * @return 响应结果json字符串
	 */
	public static String postRequest(String url, Map<String, Object> params) {
		return postRequest(url, null, params);
	}

	/***
	 * 发送POST请求
	 * @param url     访问的url
	 * @param headers 请求需要添加的请求头
	 * @param params  请求参数
	 * @return 响应结果json字符串
	 */
	public static String postRequest(String url, Map<String, String> headers, Map<String, Object> params) {
		String result = null;
		HttpPost httpPost = new HttpPost(url);
		if (null != headers && headers.size() > 0) {
			headers.forEach((key, value) -> {
				httpPost.addHeader(new BasicHeader(key, value));
			});
		}

		if (null != params && params.size() > 0) {

			List<NameValuePair> pairList = new ArrayList<>(params.size());
			params.forEach((key, value) -> {
				if (value != null && StringUtils.isNotBlank(value.toString())) {
					pairList.add(new BasicNameValuePair(key, value.toString()));
				}
			});
			httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(UTF_8)));
		}

		try (CloseableHttpClient httpClient = buildHttpClient();
			 CloseableHttpResponse response = httpClient.execute(httpPost)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, Charset.forName(UTF_8));
			}
		} catch (Exception e) {
			log.error("HttpUtils 发送POST请求出现异常", e);
		}
		return result;
	}

	/**
	 * 发送http get请求
	 *
	 * @param url 请求url
	 * @return url返回内容
	 */
	public static String getRequest(String url) {
		return getRequest(url, null);
	}


	/**
	 * 发送http get请求
	 *
	 * @param url    请求的url
	 * @param params 请求的参数
	 * @return 响应结果json字符串
	 */
	public static String getRequest(String url, Map<String, Object> params) {
		return getRequest(url, null, params);
	}

	/**
	 * 发送http get请求
	 *
	 * @param url        请求的url
	 * @param headersMap 请求头
	 * @param params     请求的参数
	 * @return 响应结果json字符串
	 */
	public static String getRequest(String url, Map<String, String> headersMap, Map<String, Object> params) {
		String result = null;
		try (CloseableHttpClient httpClient = buildHttpClient()) {
			String apiUrl = url;

			if (null != params && params.size() > 0) {
				StringBuilder param = new StringBuilder();
				int i = 0;
				for (String key : params.keySet()) {
					if (i == 0) {
						param.append("?");
					} else {
						param.append("&");
					}
					param.append(key).append("=").append(params.get(key));
					i++;
				}
				apiUrl += param;
			}

			HttpGet httpGet = new HttpGet(apiUrl);
			if (null != headersMap && headersMap.size() > 0) {
				headersMap.forEach((key, value) -> httpGet.addHeader(key, value));
			}

			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {

				HttpEntity entity = response.getEntity();
				if (null != entity) {
					result = EntityUtils.toString(entity, UTF_8);
				}

			} catch (Exception e) {
				log.error("httpUtils 出现异常", e);
			}

		} catch (IOException | ParseException e) {
			log.error("httpUtils 出现异常", e);
		}
		return result;
	}

	/**
	 * 创建httpclient
	 *
	 * @return
	 */
	private static CloseableHttpClient buildHttpClient() {
		RegistryBuilder<ConnectionSocketFactory> builder = RegistryBuilder.create();
		ConnectionSocketFactory factory = new PlainConnectionSocketFactory();
		builder.register("http", factory);

		SSLContext context = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			context = SSLContexts.custom().useTLS()
					.loadTrustMaterial(trustStore, new TrustStrategy() {
						@Override
						public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
							return true;
						}
					}).build();
		} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
			log.error("httpUtils  出现异常", e);
		}

		if (context == null) {
			return null;
		}

		LayeredConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(context, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		builder.register("https", sslFactory);
		Registry<ConnectionSocketFactory> registry = builder.build();
		PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);
		ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(Charset.forName(UTF_8)).build();
		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(100000).build();
		manager.setDefaultConnectionConfig(connConfig);
		manager.setDefaultSocketConfig(socketConfig);
		return HttpClientBuilder.create().setConnectionManager(manager).build();
	}
}
