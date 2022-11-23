package com.oddrock.common.mailext;

import java.io.File;
import java.text.SimpleDateFormat;

import javax.mail.Message;

import com.oddrock.common.OddrockStringUtils;
import com.oddrock.common.windows.SensitiveStringUtils;

public class GeneralAttachDownloadDirGenerator implements AttachDownloadDirGenerator {
	
	// 目录的格式是<时间字符串>-==-发件人邮箱地址-==-邮件主题
	public File generateDir(File baseDir, MailRecv mail) {
		String separator = "]-[";
		SimpleDateFormat format = new SimpleDateFormat("yy年MM月dd日HH时"); 
		String dateStr = format.format(mail.getSentDate());
		String lastDirName = dateStr + separator + mail.getSubject() + separator + mail.getFromNick() + separator + mail.getFrom();
		lastDirName = lastDirName.trim();
		lastDirName = OddrockStringUtils.deleteSpecCharacters(lastDirName);
		lastDirName = SensitiveStringUtils.replaceSensitiveString(lastDirName);
		return new File(baseDir, lastDirName);
	}

	public String generateDownloadFileName(String origName, Message message) {
		return origName;
	}

}
