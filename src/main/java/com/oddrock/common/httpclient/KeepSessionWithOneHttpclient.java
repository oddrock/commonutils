package com.oddrock.common.httpclient;

import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
import org.apache.http.NameValuePair;  
import org.apache.http.client.config.RequestConfig;  
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.CloseableHttpResponse;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.impl.client.CloseableHttpClient;  
import org.apache.http.impl.client.HttpClientBuilder;  
import org.apache.http.message.BasicNameValuePair;  
import org.apache.http.util.EntityUtils;  

public class KeepSessionWithOneHttpclient {  
    /** 
     * 如果用的是同一个HttpClient且没去手动连接放掉client.getConnectionManager().shutdown(); 
     * 都不用去设置cookie的ClientPNames.COOKIE_POLICY。httpclient都是会保留cookie的 
     * @param loginUrl 
     * @param loginNameValuePair 
     * @param urlAndNamePairList 
     * @return 
     */  
    public static Map<String,String> doPostWithOneHttpclient(String loginUrl,List<NameValuePair> loginNameValuePair,  
            Map<String,List<NameValuePair>> urlAndNamePairList, String encoding) {  
        //返回每个URL对应的响应信息  
        Map<String,String> map = new HashMap<String,String>();  
        String retStr = "";//每次响应信息  
        int statusCode = 0 ;//每次响应代码  
          
        // 创建HttpClientBuilder  
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
        // HttpClient  
        CloseableHttpClient closeableHttpClient = null;  
        closeableHttpClient = httpClientBuilder.build();  
        HttpPost httpPost = new HttpPost(loginUrl);  
        // 设置请求和传输超时时间  
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();  
        httpPost.setConfig(requestConfig);  
        UrlEncodedFormEntity entity = null;  
        try {  
            if(null!=loginNameValuePair){  
                entity = new UrlEncodedFormEntity(loginNameValuePair, encoding);  
                httpPost.setEntity(entity);  
            }  
            //登录  
            CloseableHttpResponse loginResponse = closeableHttpClient.execute(httpPost);  
            statusCode = loginResponse.getStatusLine().getStatusCode();  
            retStr = EntityUtils.toString(loginResponse.getEntity(), encoding);  
            map.put(loginUrl, retStr);  
              
            //登录后其他操作  
            for(Map.Entry<String,List<NameValuePair>> entry : urlAndNamePairList.entrySet()){  
                String url = entry.getKey();  
                List<NameValuePair> params = urlAndNamePairList.get(url);  
                httpPost = new HttpPost(url);  
                if(null!=params){  
                    entity = new UrlEncodedFormEntity(params, encoding);  
                    httpPost.setEntity(entity);  
                }  
                CloseableHttpResponse operationResponse = closeableHttpClient.execute(httpPost);  
                statusCode = operationResponse.getStatusLine().getStatusCode();  
                retStr = EntityUtils.toString(operationResponse.getEntity(), encoding);  
                map.put(url, retStr);  
                
                if(statusCode == 302){  
                    String redirectUrl = operationResponse.getLastHeader("Location").getValue();  
                    httpPost = new HttpPost(redirectUrl);  
                    CloseableHttpResponse redirectResponse = closeableHttpClient.execute(httpPost);  
                    statusCode = redirectResponse.getStatusLine().getStatusCode();  
                    retStr = EntityUtils.toString(redirectResponse.getEntity(), encoding);  
                    map.put(redirectUrl, retStr);  
                }  
            }  
            // 释放资源  
            closeableHttpClient.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return map;  
    }  

    public static void main(String[] args) {  
        //登录的地址以及登录操作参数  
        String loginUrl = "http://hospital.ustc.edu.cn/yy/member.php";  
        //登录的相关参数以及登录后操作参数  
        List<NameValuePair> loginParams = new ArrayList<NameValuePair>();  
        loginParams.add(new BasicNameValuePair("txtusername", "18056067506"));  
        loginParams.add(new BasicNameValuePair("txtpassword", "wcnmgb123"));  
        loginParams.add(new BasicNameValuePair("send", "login"));  
          
        //登录后操作地址以及登录后操作参数  
        String queryUrl = "http://hospital.ustc.edu.cn/yy/member.php?app=reservation&date=2019-04-25&ampm=AM&depid=70&doctorid=199&key=29acc85097";  
        List<NameValuePair> queryParams = new ArrayList<NameValuePair>();  

        Map<String,List<NameValuePair>> map = new HashMap<String,List<NameValuePair>>();  
        map.put(queryUrl, queryParams);  
        Map<String,String> returnMap = doPostWithOneHttpclient(loginUrl, loginParams, map, "GBK");  
        System.out.println(returnMap.toString());  
    }  
}  