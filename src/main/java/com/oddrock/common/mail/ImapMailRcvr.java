package com.oddrock.common.mail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.Logger;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

public class ImapMailRcvr{
	private static Logger logger = Logger.getLogger(ImapMailRcvr.class);
	
	/**
	 * 接收邮件，并支持下载附件到本地
	 * @param imapServer
	 * @param mailAccount
	 * @param mailPasswd
	 * @param folderName
	 * @param readwriteFlag
	 * @param downloadAttachToLocal
	 * @param localAttachFolderPath
	 * @return
	 * @throws Exception 
	 */
	public List<MailRecv> rcvMail(String imapServer, String mailAccount, 
			String mailPasswd, String folderName, boolean readwriteFlag,
			boolean downloadAttachToLocal, String localAttachFolderPath) throws Exception {
		return rcvMail(imapServer, mailAccount, mailPasswd, folderName, readwriteFlag, downloadAttachToLocal, localAttachFolderPath, new FromMailAttachDownloadDirGenerator());
	}
	
	public List<MailRecv> rcvMail(String imapServer, String mailAccount, 
			String mailPasswd, String folderName, boolean readwriteFlag,
			boolean downloadAttachToLocal, String localAttachDirPath, AttachDownloadDirGenerator generator) throws Exception{
		logger.warn("开始接收邮箱【"+mailAccount+"】中的邮件...");
		Properties props = new Properties();
		props.put("mail.imap.host", imapServer);
		props.put("mail.store.protocol", "imap"); 
		Session session = Session.getDefaultInstance(props, getAuthenticator(mailAccount, mailPasswd));	
		IMAPStore store = null;
		IMAPFolder folder = null;
		List<MailRecv> mails = new ArrayList<MailRecv>();	
		try {
			store = (IMAPStore) session.getStore("imap");
			logger.warn("开始远程连接邮箱【"+mailAccount+"】...");
			store.connect();
			logger.warn("已远程连接上邮箱【"+mailAccount+"】...");
			if(folderName==null){
				folderName = "INBOX";
			}
			folder = (IMAPFolder) store.getFolder(folderName);
			if(readwriteFlag){
				folder.open(Folder.READ_WRITE);  
			}else{
				folder.open(Folder.READ_ONLY);    
			}
			logger.warn("已打开【"+folderName+"】邮箱");
			Message[] messages = folder.getMessages();  
			logger.warn("开始读取所有未读邮件...");
			for (Message message : messages) {  
				Flags flags = message.getFlags(); 
				if (!flags.contains(Flags.Flag.SEEN)){
					logger.warn("开始解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
					MimeMessageParser parser = new MimeMessageParser((MimeMessage) message).parse();
					logger.warn("结束解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
					MailRecv mail = new MailRecv();
					mails.add(mail);
					mail.init(parser);
					List<DataSource> attachments = parser.getAttachmentList(); // 获取附件，并写入磁盘
					for (DataSource ds : attachments) {
						MailRecvAttach attachment = new MailRecvAttach();
						mail.getAttachments().add(attachment);
						attachment.setContentType(ds.getContentType());
						attachment.setName(ds.getName());	
						if(downloadAttachToLocal){
							File dir = generator.generateDir(new File(localAttachDirPath), mail);
							dir.mkdirs();
							String filePath =  new File(dir,ds.getName()).getCanonicalPath();
							attachment.setLocalFilePath(filePath);
							logger.warn("开始下载附件【"+ ds.getName() + "】到【"+filePath+"】...");
							downloadAttachToLocal(ds, filePath);	
							logger.warn("结束下载附件【"+ ds.getName() + "】到【"+filePath+"】...");
						}
					}
					logger.warn("邮件内容："+mail.getPlainContent());
					for(MailRecvAttach attach : mail.getAttachments()) {
						logger.warn("附件："+attach.getName());
					}			
					
				}	
			}
			if(mails.size()==0){
				logger.warn("没有新邮件！");
			}
			logger.warn("结束读取所有未读邮件...");
		} finally{
			if (folder != null) {
				folder.close(false);
			}
			if (store != null) {
				store.close();
			}
		}
		return mails;
	}

	/*
	 * 下载附件到本地
	 */
	private void downloadAttachToLocal(DataSource ds, String filePath)
			throws FileNotFoundException, IOException {
		logger.warn("开始写入附件到【"+filePath+"】...");
		BufferedOutputStream outStream = null;
		BufferedInputStream inStream = null;
		try {
			outStream = new BufferedOutputStream(new FileOutputStream(filePath));
			inStream = new BufferedInputStream(ds.getInputStream());
			byte[] data = new byte[2048];
			int length = -1;
			while ((length = inStream.read(data)) != -1) {
				outStream.write(data, 0, length);
			}
			outStream.flush();
		} finally {
			if (inStream != null) {
				inStream.close();
			}
			if (outStream != null) {
				outStream.close();
			}
		}
		logger.warn("结束写入附件到【"+filePath+"】...");
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
}
