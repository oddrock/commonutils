package com.oddrock.common.mail.qqmail;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.oddrock.common.net.WebUtils;


public class QQFileDownloader {
	private static Logger logger = Logger.getLogger(QQFileDownloader.class);
	/**
	 * 根据QQ邮件内容解析出QQ文件中转站下载页面地址
	 * @return
	 */
	public static List<QQFileDownloadPage> parseQQFileDownloadPageFromQQMail(String mailContent){
		logger.warn("开始从邮件解析QQ文件中转站的下载页面地址...");
		List<QQFileDownloadPage> list = new ArrayList<QQFileDownloadPage>();
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
        	QQFileDownloadPage qfhu = new QQFileDownloadPage();
        	qfhu.setPageUrl(m.group(2).trim());
        	qfhu.setFileName(m.group(1).trim());
        	logger.warn("QQ文件中转站下载页面地址："+qfhu.getPageUrl());
        	list.add(qfhu);
        }
        logger.warn("结束从邮件解析QQ文件中转站的下载页面地址...");
		return list;
	}
	/**
	 * 根据QQ文件中转站下载页面地址提取文件下载地址
	 * @param downloadPageUrl
	 * @return
	 */
	public static List<String> parseQQFileDownloadUrlsFromQQFileDownloadPage(String downloadPageUrl){
		logger.warn("开始从QQ文件中转站下载页面解析QQ文件下载地址...");
		String content = WebUtils.getPageContent(downloadPageUrl);
    	String pattern = "<a un=\"down\".*?href=\"(.*?)\"";
    	Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(content);
		List<String> strs = new ArrayList<String>();
        while (m.find()) {
        	String tmp = m.group(1);
            strs.add(tmp);          
            logger.warn("QQ中转站文件下载地址是："+tmp);
        }
        logger.warn("结束从QQ文件中转站下载页面解析QQ文件下载地址...");
        return strs;
	}
}
