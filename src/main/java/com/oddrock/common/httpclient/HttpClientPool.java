package com.oddrock.common.httpclient;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * HttpClient池
 */
public class HttpClientPool {
	/**
	 * 获取池实例
	 * @return
	 */
    public static HttpClientPool getInstance() {
        return HttpClientPoolInstance.INSTANCE;
    }
    
    /**
     * 从池中获取一个httpClient
     * @return
     */
    public static CloseableHttpClient getHttpClient() {
    	return HttpClientPoolInstance.INSTANCE._getHttpClient();
    }
    
    /* 通过内部静态类实现单例模式 start */
    private PoolingHttpClientConnectionManager cm; 
    private HttpClientPool() {
		cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(500);
        cm.setDefaultMaxPerRoute(100);
	}
    private static class HttpClientPoolInstance {
        private static final HttpClientPool INSTANCE = new HttpClientPool();
    }
    /* 通过内部静态类实现单例模式 end */
    
    private CloseableHttpClient _getHttpClient(){
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();  
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(globalConfig).build();  
        return client;
    }
}