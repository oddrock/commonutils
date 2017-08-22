package com.oddrock.common.net;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class WebUtils {
	/**
	 * 根据URL获取网页内容为一个字符串
	 * @param urlStr
	 * @return
	 */
	public static String getPageContent(String urlStr){
		String str = null;
		StringBuffer sb = new StringBuffer();
		try {
			URL url = new URL(urlStr);
	        InputStream in =url.openStream();
	        InputStreamReader isr = new InputStreamReader(in,"utf-8");
	        BufferedReader bufr = new BufferedReader(isr);
	        while ((str = bufr.readLine()) != null) {
	        	sb.append(str);
	        }
	        bufr.close();
	        isr.close();
	        in.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
		return sb.toString();
	}
}