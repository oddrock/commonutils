package com.oddrock.common.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.log4j.Logger;

public class MailSenderExt {
	private static Logger logger = Logger.getLogger(MailSenderExt.class);
	
	public static void sendEmail(MailSentExt emailSent) 
			throws UnsupportedEncodingException, MessagingException{
		logger.warn("开始邮件准备工作...");
		Properties props = new Properties();                    
	    props.setProperty("mail.transport.protocol", emailSent.getProtocol());   
	    props.setProperty("mail.smtp.host", emailSent.getSmtpHost());   
	    props.setProperty("mail.smtp.auth", String.valueOf(emailSent.isSmtpAuth())); 
	    if(emailSent.getSmtpPort()!=null){
	    	props.setProperty("mail.smtp.port", emailSent.getSmtpPort()); 
	    }
	    Session session = null;
	    if(emailSent.isSmtpAuth()){
	    	final String userName = emailSent.getSenderAccount();
            final String password = emailSent.getSenderPasswd();
	    	Authenticator authenticator = new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(userName, password);
	            }
	        };
	        session = Session.getInstance(props, authenticator);
	    }else{
	    	session = Session.getDefaultInstance(props); 
	    } 
	    session.setDebug(emailSent.isDebug());
	    MimeMessage message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(emailSent.getSenderAccount(),emailSent.getSenderName()));
	    InternetAddress[] addresses = new InternetAddress[emailSent.getRecverAccounts().size()];
	    int i = 0;
	    for (String recv: emailSent.getRecverAccounts()){
	    	addresses[i++] = new InternetAddress(recv);	
	    }
	    message.setRecipients(MimeMessage.RecipientType.TO, addresses);
	    message.setSubject(emailSent.getSubject());	
	    message.setSentDate(emailSent.getSendTime());	
	    
	    // 邮件内容部分
	    MimeMultipart msgMultipart = new MimeMultipart("related");
	    message.setContent(msgMultipart);
	    
	    // 写入邮件正文
	    MimeBodyPart content = new MimeBodyPart();
	    content.setContent(emailSent.getContent(), "text/html; charset=utf-8");  
	    msgMultipart.addBodyPart(content);
	    
	    // 写入邮件附件
	    for (File file : emailSent.getAttachFileSet()) {
	    	MimeBodyPart attch = new MimeBodyPart();
	    	DataSource ds = new FileDataSource(file);
	 	    DataHandler dh = new DataHandler(ds);
	 	    attch.setDataHandler(dh);
	 	    //BASE64Encoder enc = new BASE64Encoder(); 
	 	    // 防止附件名有中文乱码
	 	    //attch.setFileName("=?GBK?B?" + enc.encode(file.getName().getBytes("GBK")) + "?=");
	 	    attch.setFileName(MimeUtility.encodeText(file.getName()));
	    	msgMultipart.addBodyPart(attch);
	    }
	    
	    message.saveChanges();	 
	    logger.warn("完成邮件准备工作...");
	    logger.warn("开始发送邮件...");
	    Transport transport = session.getTransport();
	    transport.connect(emailSent.getSenderAccount(), emailSent.getSenderPasswd());
	    transport.sendMessage(message, message.getAllRecipients());
	    transport.close();
	    logger.warn("完成发送邮件...");
	}
	
	public static void sendEmail(String senderAccount, String senderName, String senderPasswd, 
			String recverAccounts, String subject, String content, boolean smtpAuth, String smtpHost, String smtpPort, Collection<File> attach) 
			throws UnsupportedEncodingException, MessagingException{
		String[] recvers = recverAccounts.split(",");
		MailSentExt emailSent = new MailSentExt();
		for(String recver : recvers){
			if(recver!=null && recver.trim().length()>0){
				emailSent.addrecverAccount(recver.trim());
			}
		}
		emailSent.setSmtpHost(smtpHost);
		emailSent.setSubject(subject);
		emailSent.setContent(content);
		emailSent.setSenderAccount(senderAccount);
		emailSent.setSenderPasswd(senderPasswd);
		emailSent.setSmtpAuth(smtpAuth);
		emailSent.setSenderName(senderName);
		if(smtpPort!=null){
			emailSent.setSmtpPort(smtpPort);
		}
		for(File file : attach){
			if(file.exists() && file.isFile()){
				emailSent.addAttach(file);
			}
		}
		sendEmail(emailSent);
	}
}