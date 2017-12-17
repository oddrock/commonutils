package com.oddrock.common.mail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.Logger;

import com.oddrock.common.CommonProp;
import com.oddrock.common.DateUtils;
import com.oddrock.common.windows.SensitiveStringUtils;
import com.sun.mail.pop3.POP3Folder;

public class PopMailRcvr{
	private static Logger logger = Logger.getLogger(PopMailRcvr.class);
	
	/**
	 * 接收邮件，并支持下载附件到本地
	 * @param server
	 * @param account
	 * @param passwd
	 * @param folderName
	 * @param readwriteFlag
	 * @param downloadAttachToLocal
	 * @param localAttachFolderPath
	 * @return
	 * @throws Exception 
	 */
	public List<MailRecv> rcvMail(String server, String account, String passwd, String folderName, 
			boolean downloadAttachToLocal, String localAttachFolderPath) throws Exception {
		return rcvMail(server, account, passwd, folderName, downloadAttachToLocal, localAttachFolderPath, new FromMailAttachDownloadDirGenerator());
	}
	
	public List<MailRecv> rcvMail(String server, String account, String passwd, 
			String folderName, boolean downloadAttachToLocal, String localAttachDirPath, 
			AttachDownloadDirGenerator generator) throws Exception{
		Session session = createSession(server, account, passwd);	
		Store store = null;
		Folder folder = null;
		List<MailRecv> mails = new ArrayList<MailRecv>();	
		try {
			store = session.getStore("pop3");
			logger.warn("开始远程连接邮箱【"+account+"】...");
			store.connect(server, account, passwd);
			logger.warn("已远程连接上邮箱【"+account+"】...");
			if(folderName==null) folderName = "INBOX";
			folder = store.getFolder(folderName);
			folder.open(Folder.READ_WRITE);
			logger.warn("已打开【"+folderName+"】邮箱");
			Message[] messages = folder.getMessages(); 
			logger.warn("共有"+messages.length+"封邮件");
			logger.warn("开始读取所有未读邮件...");
			int i = 1;
			for (Message message : messages) {  
				if(PopMailReadRecordManager.instance.isReadInAllDays(account, (POP3Folder)folder, message)) {
					logger.info("之前已阅读，本次不再下载："+((POP3Folder)folder).getUID(message));
					continue;
				}
				logger.warn("第"+i+"封未读邮件：");
				i++;
				MailRecv mail = parseMail(message, account, folder, downloadAttachToLocal, localAttachDirPath, generator);
				mails.add(mail);
				PopMailReadRecordManager.instance.isReadInAllDays(account, (POP3Folder)folder, message);
			}
			if(mails.size()==0){
				logger.warn("没有新邮件！");
			}
			logger.warn("结束读取所有未读邮件...");
		} finally{
			if (folder != null) folder.close(false);
			if (store != null) store.close();
		}
		return mails;
	}

	private Session createSession(String server, String account, String passwd) {
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
		return session;
	}
	
	public MailRecv rcvOneUnreadMail(String server, String account, 
			String passwd, String folderName, 
			boolean downloadAttachToLocal, String localAttachDirPath, 
			AttachDownloadDirGenerator generator) throws Exception{
		Session session = createSession(server, account, passwd);	
		Store store = null;
		Folder folder = null;
		MailRecv mail = null;
		try {
			store = session.getStore("pop3");
			logger.warn("开始远程连接邮箱【"+account+"】...");
			store.connect(server, account, passwd);
			logger.warn("已远程连接上邮箱【"+account+"】...");
			if(folderName==null) folderName = "INBOX";
			folder = store.getFolder(folderName);
			folder.open(Folder.READ_WRITE);
			logger.warn("已打开【"+folderName+"】邮箱");
			Message[] messages = folder.getMessages(); 
			logger.warn("共有"+messages.length+"封邮件");
			logger.warn("开始读取所有未读邮件...");
			int i = 1;
			for (Message message : messages) {  
				if(PopMailReadRecordManager.instance.isReadInAllDays(account, (POP3Folder)folder, message)) {
					logger.info("之前已阅读，本次不再下载："+((POP3Folder)folder).getUID(message));
					continue;
				}
				logger.warn("第"+i+"封未读邮件：");
				i++;
				mail = parseMail(message, account, folder, downloadAttachToLocal, localAttachDirPath, generator);
				PopMailReadRecordManager.instance.setReadInAllDays(account, (POP3Folder)folder, message);
				break;
			}
			if(mail==null){
				logger.warn("没有新邮件！");
			}
			logger.warn("结束读取所有未读邮件...");
		} finally{
			if (folder != null) folder.close(false);
			if (store != null) store.close();
		}
		return mail;
	}
	
	// 优先接收未收到的邮件，如果都接收完了，就重复接收从今天开始指定天数内的邮件
	public MailRecv rcvOneMailCylclyInSpecDays(String server, String account, String passwd, 
			String folderName, boolean downloadAttachToLocal, String localAttachDirPath, 
			AttachDownloadDirGenerator generator, int days) throws Exception{
		Session session = createSession(server, account, passwd);	
		Store store = null;
		Folder folder = null;
		MailRecv mail = null;
		try {
			store = session.getStore("pop3");
			logger.warn("开始远程连接邮箱【"+account+"】...");
			store.connect(server, account, passwd);
			logger.warn("已远程连接上邮箱【"+account+"】...");
			if(folderName==null) folderName = "INBOX";
			folder = store.getFolder(folderName);
			folder.open(Folder.READ_WRITE);
			logger.warn("已打开【"+folderName+"】邮箱");
			Message[] messages = folder.getMessages(); 
			mail = getUnreadMailInAllDays(account, downloadAttachToLocal, 
					localAttachDirPath, generator, folder, mail, messages);
			if(mail==null){
				logger.warn("没有未读邮件！");
				logger.warn("开始循环读取"+days+"天内邮件...");
				mail = getUnreadMailInRecentDays(account, downloadAttachToLocal, 
						localAttachDirPath, generator, folder, mail, messages, days);
				logger.warn("结束循环读取"+days+"天内邮件...");
				if(mail==null){
					if(PopMailReadRecordManager.instance.countInRecentDays(account, days)>0) {
						PopMailReadRecordManager.instance.clearReadInRecentDays(account, days);
						mail = getUnreadMailInRecentDays(account, downloadAttachToLocal, 
								localAttachDirPath, generator, folder, mail, messages, days);
					}
				}
			}
		} finally{
			if (folder != null) folder.close(false);
			if (store != null) store.close();
		}
		return mail;
	}

	// 获得近几天内一封未阅读邮件
	private MailRecv getUnreadMailInRecentDays(String account, boolean downloadAttachToLocal, String localAttachDirPath,
			AttachDownloadDirGenerator generator, Folder folder, MailRecv mail, Message[] messages, int days)
			throws ParseException, MessagingException, Exception, IOException {
		for (Message message : messages) {  
			// 超过指定天数的邮件不考虑
			if(DateUtils.daysBetween(message.getSentDate(), new Date())>=days) {
				continue;
			}
			if(PopMailReadRecordManager.instance.isReadInRecentDays(account, (POP3Folder)folder, message, days)) {
				continue;
			}
			mail = parseMail(message, account, folder, downloadAttachToLocal, localAttachDirPath, generator);
			PopMailReadRecordManager.instance.setReadInRecentDays(account, (POP3Folder)folder, message, days);
			break;
		}
		return mail;
	}

	// 获得一封未阅读邮件
	private MailRecv getUnreadMailInAllDays(String account, boolean downloadAttachToLocal, String localAttachDirPath,
			AttachDownloadDirGenerator generator, Folder folder, MailRecv mail, Message[] messages)
			throws MessagingException, Exception, IOException {
		logger.warn("开始读取所有未读邮件...");
		for (Message message : messages) {  
			if(PopMailReadRecordManager.instance.isReadInAllDays(account, (POP3Folder)folder, message)) {
				continue;
			}
			mail = parseMail(message, account, folder, downloadAttachToLocal, localAttachDirPath, generator);
			PopMailReadRecordManager.instance.setReadInAllDays(account, (POP3Folder)folder, message);
			break;
		}
		logger.warn("结束读取所有未读邮件...");
		return mail;
	}
	
	// 解析邮件，并下载其中附件
	private MailRecv parseMail(Message message, String account, Folder folder, 
			boolean downloadAttachToLocal, String localAttachDirPath, 
			AttachDownloadDirGenerator generator) throws Exception {
		logger.warn("开始解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
		MimeMessageParser parser = null;
		parser = new MimeMessageParser((MimeMessage) message).parse();
		logger.warn("结束解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
		MailRecv mail = new MailRecv();
		mail.init(parser, message);
		mail.setMailAccount(account);
		mail.setUID(((POP3Folder)folder).getUID(message));
		List<DataSource> attachments = parser.getAttachmentList(); // 获取附件，并写入磁盘
		for (DataSource ds : attachments) {
			MailRecvAttach attachment = new MailRecvAttach();
			mail.getAttachments().add(attachment);
			attachment.setContentType(ds.getContentType());
			attachment.setName(ds.getName());	
			if(downloadAttachToLocal){
				File dir = generator.generateDir(new File(localAttachDirPath), mail);
				dir.mkdirs();
				if(!StringUtils.isBlank(ds.getName())){
					String filePath =  new File(dir,SensitiveStringUtils.replaceSensitiveString(ds.getName().trim())).getCanonicalPath();
					attachment.setLocalFilePath(filePath);
					logger.warn("开始下载附件【"+ ds.getName() + "】到【"+filePath+"】...");
					downloadAttachToLocal(ds, filePath);	
					logger.warn("结束下载附件【"+ ds.getName() + "】到【"+filePath+"】...");
				}
			}
		}
		if(CommonProp.getBool("mail.contentshow")){
			logger.warn("开始显示邮件内容");
			logger.warn(mail.getPlainContent());
			logger.warn("结束显示邮件内容");	
		}
		return mail;
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
		boolean downloadAttachToLocal= true;
		String localAttachDirPath = CommonProp.get("mail.savefolder");
		AttachDownloadDirGenerator generator = new GeneralAttachDownloadDirGenerator();
		new PopMailRcvr().rcvMail(server,account,passwd,folderName,downloadAttachToLocal,localAttachDirPath,generator);
		/*String str = "2017-12-14]--[ypt]--[wcycmx@sohu.com]--[全国各地数据-做市场必看数据；q q nu：174－918－2004 ； ＭＯＢ：１８２１－３８９４－６８０   ";
		File file = new File(localAttachDirPath, str);
		System.out.println(file.getCanonicalPath());*/
	}
}
