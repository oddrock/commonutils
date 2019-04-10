package com.oddrock.common.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

/**
 * HTTP请求的工具类
 * @author oddrock
 *
 */
public class HttpRequestUtils {
	/**
	 * 用POST方式发送JSON数据，默认用UTF-8编码
	 * @param url
	 * @param jsonContent
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse postJson(String url, String jsonContent) throws ClientProtocolException, IOException {
    	return postJson(url, jsonContent, "UTF-8");
    }
    
	/**
	 * 用POST方式发送JSON数据
	 * @param url
	 * @param jsonContent
	 * @param encoding
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse postJson(String url, String jsonContent, String encoding) throws ClientProtocolException, IOException {
    	HttpResponse httpResponse = new HttpResponse();
    	HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity postContent = new StringEntity(jsonContent, encoding);
        httpPost.setEntity(postContent);
        RequestConfig postConfig = RequestConfig.custom().setSocketTimeout(25000).setConnectTimeout(3000).build();
        httpPost.setConfig(postConfig);
        CloseableHttpResponse response = HttpClientPool.getHttpClient().execute(httpPost);
        HttpEntity entity = response.getEntity();
        int statusCode = response.getStatusLine().getStatusCode();
        httpResponse.setStatusCode(statusCode);
        String str = null;
        if (entity != null) {
        	InputStream instreams = entity.getContent();
            str = convertStreamToString(instreams);
            httpResponse.setContent(str);
        	httpPost.abort();
        }
    	return httpResponse;
    }
    
	/*
	 * 将流转为字符串
	 */
    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
