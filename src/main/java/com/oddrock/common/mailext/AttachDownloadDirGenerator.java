package com.oddrock.common.mailext;

import java.io.File;

import javax.mail.Message;

// 附件下载地址生成器
public interface AttachDownloadDirGenerator {
	public abstract File generateDir(File baseDir, MailRecv mail);
	
	public abstract String generateDownloadFileName(String origName, Message message);
}
