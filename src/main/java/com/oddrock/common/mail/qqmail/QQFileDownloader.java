package com.oddrock.common.mail.qqmail;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.oddrock.common.file.FileUtils;
import com.oddrock.common.file.UrlWebGetter;


public class QQFileDownloader {
	private static Logger logger = Logger.getLogger(QQFileDownloader.class);
	/**
	 * 根据QQ邮件内容解析出QQ文件中转站下载页面地址
	 * @return
	 */
	public static List<QQFileHtmlUrls> parseQQFileHtmlUrlsFromQQMail(String mailContent){
		List<QQFileHtmlUrls> list = new ArrayList<QQFileHtmlUrls>();
		if(mailContent==null || !mailContent.contains("从QQ邮箱发来的超大附件")) return list;
		String pattern = ".*从QQ邮箱发来的超大附件([\\s\\S]*)";
    	Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(mailContent);
        if (!m.find()) return list;
        String[] lines = m.group(1).split("\n");
        for(String line : lines){
        	line = line.trim();
        	if(line.length()==0) continue;
        	pattern = "(.*?)\\(.*?进入下载页面：([https://|http://].*)";
        	p = Pattern.compile(pattern);
        	m = p.matcher(line);
        	if (!m.find()) continue;
        	QQFileHtmlUrls qfhu = new QQFileHtmlUrls();
        	qfhu.setQqFileHtmlUrl(m.group(2).trim());
        	qfhu.setQqFileName(m.group(1).trim());
        	list.add(qfhu);
        }
		return list;
	}
	/**
	 * 根据QQ文件中转站下载页面地址提取文件下载地址
	 * @param htmlUrlStr
	 * @return
	 */
	public static List<String> parseQQFileUrlsFromQQFileHtmlUrl(String htmlUrlStr){
		String content = UrlWebGetter.getUrlContent(htmlUrlStr);
    	String pattern = "<a un=\"down\".*?href=\"(.*?)\"";
    	Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(content);
		List<String> strs = new ArrayList<String>();
        while (m.find()) {
            strs.add(m.group(1));            
        }
        return strs;
	}
}
