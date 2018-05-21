package com.oddrock.common.mail.pop3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.Logger;

import com.oddrock.common.CommonProp;
import com.oddrock.common.file.FileUtils;
import com.oddrock.common.mail.AttachDownloadDirGenerator;
import com.oddrock.common.mail.MailRecv;
import com.oddrock.common.mail.MailRecvAttach;
import com.oddrock.common.mail.PopMailReadRecordManager;
import com.oddrock.common.windows.SensitiveStringUtils;
import com.sun.mail.pop3.POP3Folder;

public class Pop3MailRcvr{
	private static Logger logger = Logger.getLogger(Pop3MailRcvr.class);
	
	/**
	 * 接收邮件，并支持下载附件到本地
	 * @param pop3Config
	 * @return
	 * @throws Exception
	 */
	public List<MailRecv> rcvMail(Pop3Config pop3Config) throws Exception{
		Session session = createSession(pop3Config.getServer(), pop3Config.getAccount(), pop3Config.getPasswd(),pop3Config.isUseSsl());	
		Store store = null;
		Folder folder = null;
		List<MailRecv> mails = new ArrayList<MailRecv>();	
		try {
			store = session.getStore("pop3");
			store.connect(pop3Config.getServer(), pop3Config.getPort(), pop3Config.getAccount(), pop3Config.getPasswd());
			folder = store.getFolder(pop3Config.getFolderName());
			folder.open(Folder.READ_WRITE);
			logger.warn("已打开【"+pop3Config.getFolderName()+"】邮箱");
			Message[] messages = folder.getMessages(); 
			
			Set<String> rejectAddressMap = new HashSet<String>();
			if(!StringUtils.isBlank(pop3Config.getRejectAddresses())){
				String[] addresses = pop3Config.getRejectAddresses().split(",");
				for(String address : addresses){
					rejectAddressMap.add(address);
				}
			}
			
			for (Message message : messages) {  
				if(PopMailReadRecordManager.instance.isReadInAllDays(pop3Config.getAccount(), (POP3Folder)folder, message)) {
					continue;
				}
				MailRecv mail = parseMail(message, pop3Config.getAccount(), folder, 
						pop3Config.isDownloadAttachToLocal(), pop3Config.getLocalAttachDirPath(), 
						pop3Config.getAttachDownloadDirGenerator(), pop3Config.isSavemailcontent2file(), rejectAddressMap);
				if(mail!=null){
					mails.add(mail);
				}
				
				PopMailReadRecordManager.instance.setReadInAllDays(pop3Config.getAccount(), (POP3Folder)folder, message);
			}
			logger.warn("有"+mails.size()+"封新邮件");
		}catch(Exception exception){
			// 如果出现异常，则回滚已记录的邮件UID，便于重新下载。
			if(mails!=null) {
				for(MailRecv mail: mails) {
					PopMailReadRecordManager.instance.setUnReadInAllDays(pop3Config.getAccount(), mail.getUID());
					if(mail.getAttachments()!=null) {
						FileUtils.deleteDirAndAllFiles(new File(mail.getAttachments().get(0).getLocalFilePath()).getParentFile());
					}
				}
			}
			logger.warn("出现异常，将本次所有已收到的邮件删除，并将记录设为未读");
			throw exception;
		}finally{
			if (folder != null) folder.close(false);
			if (store != null) store.close();
		}
		return mails;
	}

	

	private Session createSession(String server, String account, String passwd, boolean useSsl) {
		logger.warn("开始接收邮箱【"+account+"】中的邮件...");
		Properties props = new Properties();
		if(useSsl){
			final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
			props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
	        props.setProperty("mail.pop3.socketFactory.fallback", "false");
			props.put("mail.pop3.auth.plain.disable","true");        
			props.put("mail.pop3.ssl.enable", "true"); 
		}
		props.put("mail.pop3.host", server);
		props.put("mail.transport.protocol", "pop3"); 
		// 防止抛出DecodingException
		System.setProperty("mail.mime.base64.ignoreerrors", "true");
		Session session = Session.getDefaultInstance(props, getAuthenticator(account, passwd));
		return session;
	}
	
	// 解析邮件，并下载其中附件
	private MailRecv parseMail(Message message, String account, Folder folder, 
			boolean downloadAttachToLocal, String localAttachDirPath, 
			AttachDownloadDirGenerator generator, boolean savemailcontent2file, Set<String> rejectAddressMap) throws Exception {
		logger.warn("开始解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
		MimeMessageParser parser = null;
		parser = new MimeMessageParser((MimeMessage) message).parse();
		logger.warn("结束解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
		MailRecv mail = new MailRecv();
		mail.init(parser, message);
		if(rejectAddressMap.contains(mail.getFrom())){
			return null;
		}
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
		if(savemailcontent2file){
			File dir = generator.generateDir(new File(localAttachDirPath), mail);
			dir.mkdirs();
			String filePath =  new File(dir,SensitiveStringUtils.replaceSensitiveString(mail.getSubject().trim())+".txt").getCanonicalPath();
			FileUtils.writeToFile(filePath, mail.getPlainContent(), false);
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
		String server = CommonProp.get("mail2.popserver");
		String account = CommonProp.get("mail2.account");
		String passwd = CommonProp.get("mail2.passwd");
		String localAttachDirPath = CommonProp.get("mail2.savefolder");
		Pop3Config pop3Config = new Pop3Config(server, account, passwd, localAttachDirPath, null);
		new Pop3MailRcvr().rcvMail(pop3Config);
	}
}
