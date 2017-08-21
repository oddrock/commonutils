package com.oddrock.common.email;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.Logger;

import com.oddrock.common.email.bean.Email;
import com.oddrock.common.email.bean.EmailAttachment;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

public class ImapEmailRcvr{
	private static Logger logger = Logger.getLogger(ImapEmailRcvr.class);
	public Message[] recvNewMail(String imapServer, String emailAccount, 
			String emailPasswd, String folderName, boolean readwriteFlag) {
		Message[] messages = null;
		Properties props = new Properties();
		props.put("mail.imap.host", imapServer);
		props.put("mail.store.protocol", "imap"); 
		Session session = Session.getDefaultInstance(props, 
				getAuthenticator(emailAccount, emailPasswd));
		
		IMAPStore store;
		try {
			store = (IMAPStore) session.getStore("imap");
			store.connect();
			if(folderName==null){
				folderName = "INBOX";
			}
			IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
			if(readwriteFlag){
				folder.open(Folder.READ_WRITE);  
			}else{
				folder.open(Folder.READ_ONLY);    
			}
			messages = folder.getMessages(); 
			Flags flags;  
			List<Message> list = new ArrayList<Message>();
			for (Message message : messages) {  
				flags = message.getFlags(); 
				if (!flags.contains(Flags.Flag.SEEN)){
					list.add(message);
				}	
			}
			messages = new Message[list.size()];
			int i = 0;
			for(Message message:list){
				messages[i++]=message;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return messages;
	}
	
	/**
	 * 解析收到的邮件，并将附件下载到本地
	 * @param messages
	 * @param localAttachmentFolderPath
	 * @return
	 */
	public Email[] parseEmail(Message[] messages, String localAttachmentFolderPath){
		if (messages == null || messages.length == 0) {
			return new Email[]{};
		} 
		Email[] emails = new Email[messages.length];
		Email email;
		int i = 0;
		EmailAttachment attachment;
		for (Message m : messages) {
			try {
				MimeMessageParser parser = new MimeMessageParser((MimeMessage) m).parse();
				email = new Email();
				emails[i++] = email;
				email.setFrom(parser.getFrom());
				email.setCc(parser.getCc());
				email.setTo(parser.getTo());
				email.setReplyTo(parser.getReplyTo());
				email.setSubject(parser.getSubject());
				email.setHtmlContent(parser.getHtmlContent());
				email.setPlainContent(parser.getPlainContent());
				List<DataSource> attachments = parser.getAttachmentList(); // 获取附件，并写入磁盘
				for (DataSource ds : attachments) {
					BufferedOutputStream outStream = null;
					BufferedInputStream ins = null;
					try {
						String fileName = localAttachmentFolderPath + File.separator + ds.getName();
						outStream = new BufferedOutputStream(new FileOutputStream(fileName));
						ins = new BufferedInputStream(ds.getInputStream());
						byte[] data = new byte[2048];
						int length = -1;
						while ((length = ins.read(data)) != -1) {
							outStream.write(data, 0, length);
						}
						outStream.flush();
						attachment = new EmailAttachment();
						attachment.setContentType(ds.getContentType());
						attachment.setName(ds.getName());
						attachment.setLocalFilePath(fileName);
						email.getAttachments().add(attachment);
					} finally {
						if (ins != null) {
							ins.close();
						}
						if (outStream != null) {
							outStream.close();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		// 最后关闭连接
		if (messages[0] != null) {
			Folder folder = messages[0].getFolder();
			if (folder != null) {
				try {
					Store store = folder.getStore();
					folder.close(false);// 参数false表示对邮件的修改不传送到服务器上
					if (store != null) {
						store.close();
					}
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
		return emails;
	}
	
	/*
	 * 根据用户名和密码，生成Authenticator
	 */
	private Authenticator getAuthenticator(final String userName, final String password) {
		return new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
	}
	
	/**
	 * 显示接收到的邮件
	 * @param emails
	 */
	public void showEmails(Email[] emails, boolean showContent){
		if (emails.length==0) return;
		logger.warn("-------- 收到邮件 start -------");
		int i = 1;
		for(Email e : emails){
			logger.warn("【第"+i+"封邮件 start】");
			logger.warn(e.getFrom() + " | " + e.getSubject());
			if(showContent){
				logger.warn(e.getPlainContent());
			}
			for(EmailAttachment ea : e.getAttachments()){
				logger.warn(ea.getLocalFilePath());
			}
			logger.warn("【第"+i+"封邮件 end】");
			i++;
		}
		logger.warn("-------- 收到邮件 end   -------");
	}
}
