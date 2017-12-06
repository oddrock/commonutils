package com.oddrock.common.mail.qqmail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.oddrock.common.Prop;
import com.oddrock.common.file.FileUtils;
import com.oddrock.common.mail.ImapMailRcvr;
import com.oddrock.common.mail.MailRecv;
import com.oddrock.common.mail.MailRecvAttach;
import com.oddrock.common.net.UrlFileDownloader;

public class ImapQQMailRcvr {
	private static Logger logger = Logger.getLogger(ImapQQMailRcvr.class);
	private ImapMailRcvr imr;
	public ImapQQMailRcvr(){
		imr = new ImapMailRcvr();
	}

	/**
	 * 下载QQ邮件附件
	 * @param imapServer
	 * @param emailAccount
	 * @param emailPasswd
	 * @param folderName
	 * @param readwriteFlag
	 * @param localAttachFolderPath
	 * @return
	 * @throws Exception 
	 */
	public List<MailRecv> rcvMail(String imapServer, String emailAccount, 
			String emailPasswd, String folderName, boolean readwriteFlag, 
			boolean downloadAttachToLocal, String localAttachFolderPath) throws Exception{
		List<MailRecv> mails = imr.rcvMail(imapServer, emailAccount, emailPasswd, folderName, readwriteFlag, true, localAttachFolderPath);
		for(MailRecv mail : mails){
			downloadQQFileInMail(mail, localAttachFolderPath);
		}
		return mails;
	}

	/*
	 * 下载QQ邮件中的QQ中转站文件
	 */
	private void downloadQQFileInMail(MailRecv mail, String localAttachFolderPath) {
		List<QQFileDownloadPage> list = QQFileDownloader.parseQQFileDownloadPageFromQQMail(mail.getPlainContent());
		for(QQFileDownloadPage url : list){	
			try {
				String qqFileUrl = QQFileDownloader.parseQQFileDownloadUrlsFromQQFileDownloadPage(url.getPageUrl()).get(0);
				logger.warn("开始下载【"+mail.getFrom()+"】主题为【"+mail.getSubject()+"】的邮件中的QQ中转站文件：【" + url.getFileName() + " | " + qqFileUrl+"】");
				String dirpath = localAttachFolderPath+File.separator+mail.getFrom();
				FileUtils.mkdirIfNotExists(dirpath);
				UrlFileDownloader.downLoadFromUrl(qqFileUrl, url.getFileName(), dirpath);
				MailRecvAttach ea = new MailRecvAttach();
				ea.setName(url.getFileName());
				ea.setLocalFilePath(localAttachFolderPath + File.separator + url.getFileName());
				mail.getAttachments().add(ea);
				logger.warn("结束下载【"+mail.getFrom()+"】主题为【"+mail.getSubject()+"】的邮件中的QQ中转站文件：【" + url.getFileName() + " | " + qqFileUrl+"】");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws Exception{
		String imapserver = Prop.get("qqmail.imapserver");
		String account = Prop.get("qqmail.account"); 
		String passwd = Prop.get("qqmail.passwd"); 
		String foldername = Prop.get("qqmail.foldername"); 
		boolean readwrite = Prop.getBool("qqmail.readwrite");
		String savefolder = Prop.get("qqmail.savefolder"); 
		new ImapQQMailRcvr().rcvMail(imapserver, account, passwd, foldername, readwrite, true, savefolder);
	}
}
