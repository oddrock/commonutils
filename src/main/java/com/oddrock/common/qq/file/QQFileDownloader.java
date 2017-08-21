package com.oddrock.common.qq.file;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.oddrock.common.file.FileUtils;
import com.oddrock.common.file.UrlWebGetter;
import com.oddrock.common.qq.file.bean.QQFileHtmlUrls;


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
	
	
	
	public static void main(String[] args){
		/*String htmlUrlStr = "https://mail.qq.com/cgi-bin/ftnExs_download?t=exs_ftn_download&k=09623361863a8fcf26b905794261041d505451575c030154185a0203554c0206535a1e595259531f0151060754580f04045157526466367004521d1100073644&code=5b3ada62";
		for (String s : parseQQFileUrlsFromQQFileHtmlUrl(htmlUrlStr)){
            System.out.println(s);
        } */
		String content = FileUtils.readFileContentToStr("examples/qqmail.txt");
		List<QQFileHtmlUrls> list = parseQQFileHtmlUrlsFromQQMail(content);
		for(QQFileHtmlUrls e : list){
			logger.warn(e.getQqFileName() + " | " + e.getQqFileHtmlUrl());
		}
	}
}
