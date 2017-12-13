package com.oddrock.common.mail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.oddrock.common.windows.SensitiveStringUtils;

public class GeneralAttachDownloadDirGenerator implements AttachDownloadDirGenerator {
	
	// 目录的格式是<时间字符串>-==-发件人邮箱地址-==-邮件主题
	public File generateDir(File baseDir, MailRecv mail) {
		String separator = "]--[";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		String dateStr = format.format(new Date());
		String lastDirName = dateStr + separator + SensitiveStringUtils.replaceSensitiveString(mail.getFromNick()) + separator + mail.getFrom() + separator + SensitiveStringUtils.replaceSensitiveString(mail.getSubject());
		lastDirName = lastDirName.trim();
		return new File(baseDir, lastDirName);
	}

}
