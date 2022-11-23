package com.oddrock.common.mailext;

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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.Logger;
import com.oddrock.common.CommonProp;
import com.oddrock.common.DateUtils;
import com.oddrock.common.file.FileUtils;
import com.oddrock.common.windows.SensitiveStringUtils;
import com.sun.mail.pop3.POP3Folder;

public class PopMailRcvr{
	private static Logger log = Logger.getLogger(PopMailRcvr.class);
	
	public List<MailRecv> rcvMail(String server, String account, String passwd, String localBaseDirPath, Integer onceMaxCount, Boolean showMailRcvDetail) {
		boolean downloadAttachToLocal = true;
		AttachDownloadDirGenerator generator = new GeneralAttachDownloadDirGenerator();
		String folderName = "INBOX";
		MailRecorder mailRecorder = new FileStorageMailRecorder();
		return rcvMail(server, account, passwd, folderName, downloadAttachToLocal, localBaseDirPath, generator, mailRecorder, onceMaxCount, showMailRcvDetail);
	}
	
	public List<MailRecv> rcvMail(String server, String account, String passwd, String localBaseDirPath, Integer onceMaxCount, Boolean showMailRcvDetail, AttachDownloadDirGenerator generator) {
		boolean downloadAttachToLocal = true;
		String folderName = "INBOX";
		MailRecorder mailRecorder = new FileStorageMailRecorder();
		return rcvMail(server, account, passwd, folderName, downloadAttachToLocal, localBaseDirPath, generator, mailRecorder, onceMaxCount, showMailRcvDetail);
	}
	
	/**
	 * 接收邮件，并支持下载附件到本地
	 * @param server	POP服务器地址
	 * @param account	邮箱账号
	 * @param passwd	邮箱密码	
	 * @param folderName	邮箱
	 * @param downloadAttachToLocal	是否把附件下载到本地
	 * @param onceMaxCount 
	 * @param showMailRcvDetail 
	 * @param localAttachFolderPath	本地附件下载路径
	 * @param AttachDownloadDirGenerator	下载路径生成器
	 * @return
	 * @throws Exception 
	 */
	public List<MailRecv> rcvMail(String server, String account, String passwd, String folderName, 
			boolean downloadAttachToLocal, String localAttachDirPath, 
			AttachDownloadDirGenerator generator, MailRecorder recorder, Integer onceMaxCount, Boolean showMailRcvDetail) {
		Session session = createSession(server, account, passwd);	
		//log.warn("111111");
		Store store = null;
		Folder folder = null;
		List<MailRecv> mails = new ArrayList<MailRecv>();	
		try {
			store = session.getStore("pop3");
			//log.warn("server："+server+"，account："+account+"，passwd："+passwd);
			store.connect(server, account, passwd);
			//log.warn("333333");
			if(folderName==null) {
				folderName = "INBOX";
			}
			folder = store.getFolder(folderName);
			folder.open(Folder.READ_WRITE);
			//log.warn("444444");
			try {
				log.warn("已打开【"+folderName+"】邮箱");
				Message[] messages = folder.getMessages(); 
				int count=0;
				for (Message message : messages) { 
					if(showMailRcvDetail) {
						try {
							if(message!=null && message.getSentDate()!=null && message.getFrom()!=null && message.getFrom().length>0) {
								if(recorder.isMailHasRcv(message) ) {
									log.warn("已收到老邮件：["+DateUtils.getFormatDate(message.getSentDate())+"，"+message.getFrom()[0] +"，"+ message.getSubject()+"]");
								}else {
									log.warn("已收到新邮件：["+DateUtils.getFormatDate(message.getSentDate())+"，"+message.getFrom()[0] +"，"+ message.getSubject()+"]");
								}
							}
						}catch(Exception e) {
							log.warn(ExceptionUtils.getFullStackTrace(e));
						}
						
					}
					if(recorder.isMailHasRcv(message)) {
						continue;
					}
					MailRecv mail = parseMail(message, account, folder, downloadAttachToLocal, localAttachDirPath, generator);
					mails.add(mail);
					recorder.saveRecord(message);
					count++;
					log.warn("***第["+count+"]封邮件完成收取");
					if(onceMaxCount>0) {
						if(count>=onceMaxCount) {
							break;
						}
					}
				}
				log.warn("本次共收取了"+count+"封新邮件");
			}catch(Exception e){
				log.warn(ExceptionUtils.getFullStackTrace(e));
				//e.printStackTrace();
				// 如果出现异常，则回滚已记录的邮件UID，便于重新下载。
				if(mails!=null) {
					for(MailRecv mail: mails) {
						if(mail.getMessage()!=null) {
							recorder.deleteRecord(mail.getMessage());
						}
						FileUtils.deleteDirAndAllFiles(new File(mail.getAttachments().get(0).getLocalFilePath()).getParentFile());
					}
				}
				log.warn("出现异常，将本次所有已收到的邮件删除，并将记录设为未读");
			}
		}catch(Exception e){
			log.warn(ExceptionUtils.getFullStackTrace(e));
			//e.printStackTrace();
		}finally {
			try {
				if (folder != null) folder.close(false);
				if (store != null) store.close();
			}catch(Exception e){
				log.warn(ExceptionUtils.getFullStackTrace(e));
				//e.printStackTrace();
			}
		}
		return mails;
	}

	private Session createSession(String server, String account, String passwd) {
		log.warn("开始接收邮箱【"+account+"】中的邮件...");
		Properties props = new Properties();
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
		props.put("mail.pop3.auth.plain.disable","true");        
		props.put("mail.pop3.ssl.enable", "true"); 
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
			AttachDownloadDirGenerator generator) throws Exception {
		log.warn("开始解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
		MimeMessageParser parser = null;
		parser = new MimeMessageParser((MimeMessage) message).parse();
		log.warn("结束解析来自【"+message.getFrom()[0]+"】主题为【"+message.getSubject()+"】的邮件...");
		MailRecv mail = new MailRecv();
		mail.init(parser, message);
		mail.setMailAccount(account);
		mail.setUID(((POP3Folder)folder).getUID(message));
		File dir = generator.generateDir(new File(localAttachDirPath), mail);
		if(downloadAttachToLocal){
			dir.mkdirs();
			String plainContent = mail.getPlainContent();
			if(StringUtils.isNotBlank(plainContent)) {
				//log.warn(generator.generateDownloadFileName("00_邮件正文.txt", message));
				String plainContentFilePath = null;
				try {
					//log.warn("dir："+dir.getCanonicalPath());
					//log.warn("DownloadFileName："+generator.generateDownloadFileName("00_邮件正文.txt", message));
					File tmpFile = new File(dir,generator.generateDownloadFileName("00_邮件正文.txt", message));
					//log.warn("file："+tmpFile);
					plainContentFilePath = tmpFile.getCanonicalPath();
					//log.warn("plainContentFilePath："+plainContentFilePath);
				}catch(Exception e) {
					log.warn(ExceptionUtils.getFullStackTrace(e));
					try {
						plainContentFilePath = new File(dir,"00_邮件正文.txt").getCanonicalPath();
					}catch(Exception e1) {
						log.warn(ExceptionUtils.getFullStackTrace(e1));
					}
				}
				//log.warn("plainContentFilePath："+plainContentFilePath);
				if(plainContentFilePath!=null) {
					FileUtils.writeToFile(plainContentFilePath, plainContent, false);
					log.warn("已将邮件正文内容保存到到【"+plainContentFilePath+"】...");
				}
			}	
		}
		List<DataSource> attachments = parser.getAttachmentList(); // 获取附件，并写入磁盘
		for (DataSource ds : attachments) {
			MailRecvAttach attachment = new MailRecvAttach();
			mail.getAttachments().add(attachment);
			attachment.setContentType(ds.getContentType());
			attachment.setName(ds.getName());	
			if(downloadAttachToLocal){
				dir.mkdirs();
				if(!StringUtils.isBlank(ds.getName())){
					String fileName = generator.generateDownloadFileName(ds.getName(), message);
					String filePath =  new File(dir,SensitiveStringUtils.replaceSensitiveString(fileName.trim())).getCanonicalPath();
					
					attachment.setLocalFilePath(filePath);
					log.warn("开始下载附件【"+ ds.getName() + "】到【"+filePath+"】...");
					downloadAttachToLocal(ds, filePath);	
					log.warn("结束下载附件【"+ ds.getName() + "】到【"+filePath+"】...");
				}
			}
		}
		if(CommonProp.getBool("mail.contentshow")){
			log.warn("开始显示邮件内容");
			log.warn(mail.getPlainContent());
			log.warn("结束显示邮件内容");	
		}
		return mail;
	}

	/*
	 * 下载附件到本地
	 */
	private void downloadAttachToLocal(DataSource ds, String filePath)
			throws FileNotFoundException, IOException {
		log.warn("开始写入附件到【"+filePath+"】...");
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
		log.warn("结束写入附件到【"+filePath+"】...");
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
