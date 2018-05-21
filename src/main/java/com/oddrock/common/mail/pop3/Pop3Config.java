package com.oddrock.common.mail.pop3;

import org.apache.commons.lang.StringUtils;

import com.oddrock.common.mail.AttachDownloadDirGenerator;
import com.oddrock.common.mail.GeneralAttachDownloadDirGenerator;

public class Pop3Config {
	String server;			
	int port;				// 端口，默认为110
	boolean useSsl;			// 是否使用SSL，默认不使用
	String account;
	String passwd;
	String folderName;		// 默认为INBOX
	boolean downloadAttachToLocal; 	// 是否下载到本地，默认下载
	boolean savemailcontent2file;	// 是否将邮件内容保存到本地，默认保存
	String localAttachDirPath; 
	AttachDownloadDirGenerator attachDownloadDirGenerator;		// 如何生成本地文件夹
	String rejectAddresses;			// 拒收邮箱地址，用英文逗号隔开
	public boolean isRejectAddress(String senderMail){
		if(!StringUtils.isBlank(rejectAddresses)){
			String[] addresses = rejectAddresses.split(",");
			for(String address : addresses){
				if(senderMail.equals(address)){
					return true;
				}
			}
		}
		return false;
	}
	public String getRejectAddresses() {
		return rejectAddresses;
	}
	public void setRejectAddresses(String rejectAddresses) {
		this.rejectAddresses = rejectAddresses;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isUseSsl() {
		return useSsl;
	}
	public void setUseSsl(boolean useSsl) {
		this.useSsl = useSsl;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public boolean isDownloadAttachToLocal() {
		return downloadAttachToLocal;
	}
	public void setDownloadAttachToLocal(boolean downloadAttachToLocal) {
		this.downloadAttachToLocal = downloadAttachToLocal;
	}
	public String getLocalAttachDirPath() {
		return localAttachDirPath;
	}
	public void setLocalAttachDirPath(String localAttachDirPath) {
		this.localAttachDirPath = localAttachDirPath;
	}
	public AttachDownloadDirGenerator getAttachDownloadDirGenerator() {
		return attachDownloadDirGenerator;
	}
	public void setAttachDownloadDirGenerator(
			AttachDownloadDirGenerator attachDownloadDirGenerator) {
		this.attachDownloadDirGenerator = attachDownloadDirGenerator;
	}
	
	public boolean isSavemailcontent2file() {
		return savemailcontent2file;
	}
	public void setSavemailcontent2file(boolean savemailcontent2file) {
		this.savemailcontent2file = savemailcontent2file;
	}
	public Pop3Config() {
		super();
		this.useSsl = false;
		this.downloadAttachToLocal = true;
		this.port = 110;
		this.folderName = "INBOX";
		this.attachDownloadDirGenerator = new GeneralAttachDownloadDirGenerator();
		this.savemailcontent2file = true;
	}
	public Pop3Config(String server, String account, String passwd, String localAttachDirPath, String rejectAddresses) {
		this();
		this.server = server;
		this.account = account;
		this.passwd = passwd;
		this.localAttachDirPath = localAttachDirPath;
		this.rejectAddresses = rejectAddresses;
	}
	
	
}
