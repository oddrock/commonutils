package com.oddrock.common.mail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralAttachDownloadDirGenerator implements AttachDownloadDirGenerator {
	
	// 目录的格式是<时间字符串>-==-发件人邮箱地址-==-邮件主题
	public File generateDir(File baseDir, MailRecv mail) {
		String separator = "]--[";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd"); 
		String dateStr = format.format(new Date());
		String lastDirName = dateStr + separator + mail.getFrom() + separator + mail.getSubject();
		return new File(baseDir, lastDirName);
	}

}
