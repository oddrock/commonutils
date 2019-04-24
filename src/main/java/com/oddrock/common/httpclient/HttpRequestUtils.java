package com.oddrock.common.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

/**
 * HTTP请求的工具类
 * @author oddrock
 *
 */
public class HttpRequestUtils {
	
	/**
     * HTTP DELETE
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResponse delete(String url) throws ClientProtocolException, IOException {
    	HttpResponse httpResponse = new HttpResponse();
    	HttpDelete httpdelete = new HttpDelete(url);
        RequestConfig postConfig = RequestConfig.custom().setSocketTimeout(25000).setConnectTimeout(3000).build();
        httpdelete.setConfig(postConfig);
        CloseableHttpResponse response = HttpClientPool.getHttpClient().execute(httpdelete);
        HttpEntity entity = response.getEntity();
        int statusCode = response.getStatusLine().getStatusCode();
        httpResponse.setStatusCode(statusCode);
        String str = null;
        if (entity != null) {
        	InputStream instreams = entity.getContent();
            str = convertStreamToString(instreams);
            httpResponse.setContent(str);
        	httpdelete.abort();
        }
    	return httpResponse;
    }
    
    /**
     * JSON提交
     * @param url
     * @param jsonContent
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResponse putJson(String url, String jsonContent) throws ClientProtocolException, IOException {
    	return putJson(url, jsonContent, "UTF-8");
    }
    
    /**
     * JSON提交
     * @param url
     * @param jsonContent
     * @param encoding
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResponse putJson(String url, String jsonContent, String encoding) throws ClientProtocolException, IOException {
    	HttpResponse httpResponse = new HttpResponse();
    	HttpPut httpPost = new HttpPut(url);
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
    
	/**
	 * 用POST方式发送FORM数据
	 * @param url
	 * @param strContent
	 * @param requestEncoding
	 * @param responseEncoding
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse post(
			String url, String strContent, String requestEncoding, String responseEncoding) 
			throws ClientProtocolException, IOException {
		HttpResponse httpResponse = new HttpResponse();
    	HttpPost httpPost = new HttpPost(url);
    	if(requestEncoding==null) {
    		requestEncoding = "UTF-8";
    	}
    	StringEntity postContent = new StringEntity(strContent, requestEncoding);
        httpPost.setEntity(postContent);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestConfig postConfig = RequestConfig.custom().setSocketTimeout(25000).setConnectTimeout(3000).build();
        httpPost.setConfig(postConfig);
        CloseableHttpResponse response = HttpClientPool.getHttpClient().execute(httpPost);
        HttpEntity entity = response.getEntity();
        int statusCode = response.getStatusLine().getStatusCode();		
        httpResponse.setStatusCode(statusCode);
        String str = null;
        if (entity != null) {
        	InputStream instreams = entity.getContent();
        	if(responseEncoding!=null) {
        		str = convertStreamToString(instreams, responseEncoding);
        	}else {
        		str = convertStreamToString(instreams);
        	}
            
            httpResponse.setContent(str);
        	httpPost.abort();
        }
        return httpResponse;
	}
	
	public static HttpResponse post(String url, String strContent, String encoding) throws ClientProtocolException, IOException {
		return post(url, strContent, encoding, encoding);
	}
	
	public static HttpResponse post(String url, String strContent) throws ClientProtocolException, IOException {
		return post(url, strContent, null, null);
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
    
    private static String convertStreamToString(InputStream is, String encoding) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
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
