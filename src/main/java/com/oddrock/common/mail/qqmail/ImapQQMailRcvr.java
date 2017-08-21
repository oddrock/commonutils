package com.oddrock.common.mail.qqmail;

import java.util.List;

import org.apache.log4j.Logger;

import com.oddrock.common.mail.ImapMailRcvr;
import com.oddrock.common.mail.MailRecv;

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
	public MailRecv[] downloadQQMailAttach(String imapServer, String emailAccount, String emailPasswd, 
			String folderName, boolean readwriteFlag, String localAttachFolderPath) throws Exception{
		List<MailRecv> mailArr = imr.rcvMail(imapServer, emailAccount, emailPasswd, folderName, readwriteFlag, true, localAttachFolderPath);
		/*String qqFileUrl = null;
		MailRecvAttach ea = null;
		String dirpath = null;
		for(MailRecv mail : mailArr){
			List<QQFileHtmlUrls> list = QQFileDownloader.parseQQFileHtmlUrlsFromQQMail(mail.getPlainContent());
			for(QQFileHtmlUrls e : list){	
				try {
					qqFileUrl = QQFileDownloader.parseQQFileUrlsFromQQFileHtmlUrl(e.getQqFileHtmlUrl()).get(0);
					logger.warn("开始下载：" + e.getQqFileName() + " | " + qqFileUrl);
					dirpath = localAttachFolderPath+File.separator+mail.getFrom();
					FileUtils.mkdirIfNotExists(dirpath);
					UrlFileDownloader.downLoadFromUrl(qqFileUrl, e.getQqFileName(), dirpath);
					ea = new MailRecvAttach();
					ea.setName(e.getQqFileName());
					ea.setLocalFilePath(localAttachFolderPath + File.separator + e.getQqFileName());
					mail.getAttachments().add(ea);
					logger.warn("结束下载：" + e.getQqFileName() + " | " + qqFileUrl);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}*/
		return null;
	}
}
