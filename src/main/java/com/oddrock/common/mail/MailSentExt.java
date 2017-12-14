package com.oddrock.common.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.mail.MessagingException;

import com.oddrock.common.CommonProp;


/**
 * 发出的邮件
 * @author oddrock
 *
 */
public class MailSentExt {
	private String protocol;
	private String smtpHost;
	private boolean smtpAuth;
	private boolean debug;
	private String senderAccount;
	private String senderName;
	private Set<String> recverAccounts;
	private String subject;
	private String content;
	private String contentEncoding;
	private Date sendTime;
	private String senderPasswd;
	private String smtpPort;
	private Set<File> attachFileSet = new HashSet<File>();
	public void addAttach(File file){
		attachFileSet.add(file);
	}
	public Set<File> getAttachFileSet() {
		return attachFileSet;
	}
	public void setAttachFileSet(Set<File> attachFileSet) {
		this.attachFileSet = attachFileSet;
	}
	public String getSmtpPort() {
		return smtpPort;
	}
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String sendProtocol) {
		this.protocol = sendProtocol;
	}
	public String getSmtpHost() {
		if(smtpHost!=null){
			return smtpHost;
		}else if(senderAccount!=null){
			String[] splits = senderAccount.split("@");
			if(splits.length>1){
				return "smtp."+splits[1];
			}
		}
		return null;
	}
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	public boolean isSmtpAuth() {
		return smtpAuth;
	}
	public void setSmtpAuth(boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public String getSenderAccount() {
		return senderAccount;
	}
	public void setSenderAccount(String senderAccount) {
		this.senderAccount = senderAccount;
	}
	public String getSenderName() {
		if(senderName==null){
			senderName = senderAccount;
		}
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public Set<String> getRecverAccounts() {
		return recverAccounts;
	}
	public void setRecverAccounts(Set<String> recverAccounts) {
		this.recverAccounts = recverAccounts;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		if(content==null){
			return subject;
		}else{
			return content;
		}
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContentEncoding() {
		return contentEncoding;
	}
	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public String getSenderPasswd() {
		return senderPasswd;
	}
	public void setSenderPasswd(String senderPaaswd) {
		this.senderPasswd = senderPaaswd;
	}
	public void addrecverAccount(String recverAccount){
		recverAccounts.add(recverAccount);
	}
	public MailSentExt() {
		super();
		recverAccounts = new HashSet<String>();
		protocol = "smtp";
		debug = false;
		smtpAuth = false;
		contentEncoding = "text/html;charset=UTF-8";
		sendTime = new Date();
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, MessagingException {
		String recverAccounts = CommonProp.get("mail.recver.accounts");
		String senderAccount = CommonProp.get("qqmail.account");
		String senderPasswd = CommonProp.get("qqmail.passwd");
		String smtpPort = CommonProp.get("qqmail.smtpport");
		String smtpHost = CommonProp.get("qqmail.smtphost");
		Set<File> attach = new HashSet<File>();
		attach.add(new File("C:\\Users\\oddro\\Desktop\\test.log"));
		attach.add(new File("C:\\Users\\oddro\\Desktop\\落地思路v0.4-qzfeng - 副本.xlsx"));
		attach.add(new File("C:\\Users\\oddro\\Desktop\\2017信息安全体系培训材料.pptx"));
		MailSenderExt.sendEmail(senderAccount, senderPasswd, recverAccounts, "很好就这样", "明晚去吃饭", true, smtpHost, smtpPort, attach);
	}

}
