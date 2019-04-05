package com.zeling.commons.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http工具类
 * 
 * @author chenbd 2018年10月30日
 */
public class HttpClientUtils {
	
	public static final Logger LOG = LoggerFactory.getLogger(HttpClientUtils.class);
	
	/**
	 * 发送get请求
	 * 
	 * @param url 请求地址
	 * @param paramKeyValue 请求的参数
	 * @param headerKeyValue 请求的header键值对
	 * @param responseHandler 请求结果处理器
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static <T> T sendGet(String url, Map<String, String> paramKeyValue, Map<String, String> headerKeyValue,
			final ResponseHandler<? extends T> responseHandler) throws ClientProtocolException, IOException {
		if (StringUtils.isBlank(url) || responseHandler == null) {
			throw new IllegalArgumentException("url|responseHandler参数不能为空");
		}
		try (CloseableHttpClient httpclient = HttpClients.createDefault();) {
			HttpGet httpGet = new HttpGet(addParams2Url(url, paramKeyValue));
			addHeader2Request(httpGet, headerKeyValue);
			return httpclient.execute(httpGet, responseHandler);
		}
	}
	
	/**
	 * 发送get请求
	 * 
	 * @param url
	 *            请求地址
	 * @param paramKeyValue
	 *            请求的参数
	 * @param headerKeyValue
	 *            header的键值对
	 * @return 请求的返回结果，字符串形式
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String sendGet(String url, Map<String, String> paramKeyValue, Map<String, String> headerKeyValue)
			throws ClientProtocolException, IOException {
		return sendGet(url, paramKeyValue, headerKeyValue, new StringResponseHandler());
	}
	
	/**
	 * 发送post请求
	 * 
	 * @param url 请求地址
	 * @param formKeyVlaue post表单键值对
	 * @param headerKeyValue 请求的header键值对
	 * @param responseHandler 请求结果处理器
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static <T> T sendPost(String url, Map<String, String> formKeyVlaue, Map<String, String> headerKeyValue,
			final ResponseHandler<? extends T> responseHandler) throws ClientProtocolException, IOException {
		if (StringUtils.isBlank(url) || responseHandler == null) {
			throw new IllegalArgumentException("url|responseHandler参数不能为空");
		}
		try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
			HttpPost httpPost = new HttpPost(url);
			addHeader2Request(httpPost, headerKeyValue);
			if (!CollectionUtils.sizeIsEmpty(formKeyVlaue)) {
				List<NameValuePair> parameters = new ArrayList<>();
				for (Entry<String, String> entry : formKeyVlaue.entrySet()) {
					parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, Consts.UTF_8);
				httpPost.setEntity(formEntity);
			}
			return httpClient.execute(httpPost, responseHandler);
		}
	}
	
	/**
	 * 发送post请求
	 * 
	 * @param url 请求地址
	 * @param formKeyVlaue post表单键值对
	 * @param headerKeyValue 请求的header键值对
	 * @return 请求结果
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String sendPost(String url, Map<String, String> formKeyVlaue, Map<String, String> headerKeyValue)
			throws ClientProtocolException, IOException {
		return sendPost(url, formKeyVlaue, headerKeyValue, new StringResponseHandler());
	}
	
	/**
	 * 添加参数到url
	 * 
	 * @param url url地址
	 * @param paramKeyValue 将要添加到url的参数键值对
	 * @return
	 */
	public static String addParams2Url(String url, Map<String, String> paramKeyValue) {
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("url参数不能为空");
		}
		if (CollectionUtils.sizeIsEmpty(paramKeyValue)) {
			return url;
		}
		StringBuilder sb = new StringBuilder(url);
		if (!url.contains("?")) {
			sb.append("?");
		}
		for (Entry<String, String> entry : paramKeyValue.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue() + "&");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private static class StringResponseHandler implements ResponseHandler<String> {

		@Override
		public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity httpEntity = response.getEntity();
				return httpEntity != null ? EntityUtils.toString(httpEntity) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status);
			}
		}

	}
	
	private static void addHeader2Request(HttpRequestBase request, Map<String, String> headerKeyValue) {
		if (headerKeyValue == null) {
			return;
		}
		for (Entry<String, String> entry : headerKeyValue.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	}

	private HttpClientUtils() {
		throw new AssertionError(HttpClientUtils.class.getName() + ": 禁止实例化");
	}
}
