package com.oddrock.common.mailext;

import java.io.File;
import java.text.SimpleDateFormat;
import javax.mail.Message;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.oddrock.common.OddrockStringUtils;
import com.oddrock.common.file.FileUtils;


public class EmailNameAttachDownloadDirGenerator implements AttachDownloadDirGenerator {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(EmailNameAttachDownloadDirGenerator.class);
	
	// 目录的格式是<时间字符串>-==-发件人邮箱地址-==-邮件主题
	public File generateDir(File baseDir, MailRecv mail) {
		String separator = "]-[";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd"); 
		String dateStr = format.format(mail.getSentDate());
		String lastDirName = dateStr + separator + mail.getFromNick() + separator + mail.getFrom() + separator + mail.getSubject();
		//lastDirName = lastDirName.trim();
		//lastDirName = OddrockStringUtils.deleteSpecCharacters(lastDirName);
		lastDirName = FileUtils.removeInvaildSymbolInFileName(lastDirName);
		return new File(baseDir, lastDirName);
	}

	public String generateDownloadFileName(String origName, Message message) {
		String origFileName = FileUtils.getFileNameWithoutSuffixFromFilePath(origName);
		String suffix = FileUtils.getFileNameSuffix(origName);
		String fileName = origFileName + "(<oddrEmailSubject> <oddrEmailSender> <oddrEmailSendDate>)";
		
		String emailSubject = "";
		String emailSender = "";
		String emailSendDate = "";
		try {
			emailSubject = message.getSubject();
			if(message.getFrom().length>0) {
				emailSender = message.getFrom()[0].toString();
			}
			if(message.getSentDate()!=null) {
				SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
				emailSendDate = format1.format(message.getSentDate());
			}
		} catch (Exception e) {
			log.warn(ExceptionUtils.getFullStackTrace(e));
			//e.printStackTrace();
		}
		//emailSubject = emailSubject.substring(beginIndex);
		int maxLen = 20;	
		emailSubject = OddrockStringUtils.interceptFirstXSubStr(emailSubject, maxLen);
		fileName = fileName.replace("<oddrEmailSubject>", emailSubject);
		emailSender = OddrockStringUtils.interceptFirstXSubStr(emailSender, maxLen);
		fileName = fileName.replace("<oddrEmailSender>", emailSender);
		fileName = fileName.replace("<oddrEmailSendDate>", emailSendDate);
		/*System.out.println("oddrEmailSubject："+emailSubject);
		System.out.println("oddrEmailSender："+emailSender);
		System.out.println("oddrEmailSendDate："+emailSendDate);*/
		fileName = fileName + "." + suffix;
		fileName = FileUtils.removeInvaildSymbolInFileName(fileName);
		//System.out.println("fileName："+fileName);
		return fileName;
	}
	
	public static void main(String[] args) {
		String s = "123";
		String a = OddrockStringUtils.interceptFirstXSubStr(s, 2);
		System.out.println(a);
	}
}
