package com.oddrock.common.mail;

import java.io.File;

// 用发信人邮箱作为文件夹名
public class FromMailAttachDownloadDirGenerator implements AttachDownloadDirGenerator {
	public File generateDir(File baseDir, MailRecv mail) {
		return new File(baseDir, mail.getFrom());
	}
}
