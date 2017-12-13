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
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.Logger;

import com.oddrock.common.CommonProp;
import com.oddrock.common.windows.SensitiveStringUtils;
import com.sun.mail.pop3.POP3Folder;

public class PopMailRcvr{
	private static Logger logger = Logger.getLogger(PopMailRcvr.class);
	
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
	
	public List<MailRecv> rcvMail(String server, String account, 
			String passwd, String folderName, boolean readwriteFlag,
			boolean downloadAttachToLocal, String localAttachDirPath, AttachDownloadDirGenerator generator) throws Exception{
		logger.warn("开始接收邮箱【"+account+"】中的邮件...");
		   
		           
		
		        
		           
		
		Properties props = new Properties();
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
		props.put("mail.pop3.auth.plain.disable","true");        
		props.put("mail.pop3.ssl.enable", "true"); 
		props.put("mail.pop3.host", server);
		props.put("mail.transport.protocol", "pop3"); 
		Session session = Session.getDefaultInstance(props, getAuthenticator(account, passwd));	
		Store store = null;
		Folder folder = null;
		List<MailRecv> mails = new ArrayList<MailRecv>();	
		try {
			store = session.getStore("pop3");
			logger.warn("开始远程连接邮箱【"+account+"】...");
			store.connect(server, account, passwd);
			logger.warn("已远程连接上邮箱【"+account+"】...");
			if(folderName==null){
				folderName = "INBOX";
			}
			folder = store.getFolder(folderName);
			if(readwriteFlag){
				folder.open(Folder.READ_WRITE);  
			}else{
				folder.open(Folder.READ_ONLY);    
			}
			logger.warn("已打开【"+folderName+"】邮箱");
			Message[] messages = folder.getMessages();  
			logger.error("共有"+messages.length+"封邮件");
			logger.error("开始读取所有未读邮件...");
			int i = 1;
			for (Message message : messages) {  
				if(PopMailReadRecordManager.instance.isRead(account, (POP3Folder)folder, message)) {
					logger.error("之前已阅读，本次不再下载："+((POP3Folder)folder).getUID(message));
					continue;
				}
				logger.error("第"+i+"封未读邮件：");
				i++;
				logger.warn("开始解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
				MimeMessageParser parser = new MimeMessageParser((MimeMessage) message).parse();
				logger.warn("结束解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
				MailRecv mail = new MailRecv();
				mails.add(mail);
				mail.init(parser);
				String fromDecode = MimeUtility.decodeText(message.getFrom()[0].toString());
				String fromNick = fromDecode.replaceAll("<"+mail.getFrom()+">", "").trim();
				mail.setFromNick(fromNick);
				List<DataSource> attachments = parser.getAttachmentList(); // 获取附件，并写入磁盘
				for (DataSource ds : attachments) {
					MailRecvAttach attachment = new MailRecvAttach();
					mail.getAttachments().add(attachment);
					attachment.setContentType(ds.getContentType());
					attachment.setName(ds.getName());	
					if(downloadAttachToLocal){
						File dir = generator.generateDir(new File(localAttachDirPath), mail);
						dir.mkdirs();
						if(ds.getName()!=null){
							String filePath =  new File(dir,SensitiveStringUtils.replaceSensitiveString(ds.getName())).getCanonicalPath();
							attachment.setLocalFilePath(filePath);
							logger.warn("开始下载附件【"+ ds.getName() + "】到【"+filePath+"】...");
							downloadAttachToLocal(ds, filePath);	
							logger.warn("结束下载附件【"+ ds.getName() + "】到【"+filePath+"】...");
						}
					}
				}
				PopMailReadRecordManager.instance.setRead(account, (POP3Folder)folder, message);
				if(CommonProp.getBool("mail.contentshow")){
					logger.warn("开始显示邮件内容");
					logger.warn(mail.getPlainContent());
					logger.warn("结束显示邮件内容");	
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
	
	public static void main(String[] args) throws Exception{
		String server = CommonProp.get("mail.popserver");
		String account = CommonProp.get("mail.account");
		String passwd = CommonProp.get("mail.passwd");
		String folderName= CommonProp.get("mail.foldername");
		boolean readwriteFlag = CommonProp.getBool("mail.readwrite");
		boolean downloadAttachToLocal= true;
		String localAttachDirPath = CommonProp.get("mail.savefolder");
		AttachDownloadDirGenerator generator = new GeneralAttachDownloadDirGenerator();
		new PopMailRcvr().rcvMail(server,account,passwd,folderName,readwriteFlag,downloadAttachToLocal,localAttachDirPath,generator);
	}
}
