package com.oddrock.common.file;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class UrlWebGetter {
	public static String getUrlContent(String urlStr){
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