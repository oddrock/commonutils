package com.oddrock.common.mail;

import java.io.File;

// 附件下载地址生成器
public interface AttachDownloadDirGenerator {
	public abstract File generateDir(File baseDir, MailRecv mail);
}
