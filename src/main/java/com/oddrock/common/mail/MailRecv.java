package com.oddrock.common.mail;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;

import org.apache.commons.mail.util.MimeMessageParser;

/**
 * 接收到的邮件
 * @author oddrock
 *
 */
public class MailRecv {
	private String mailAccount;
	private String UID;
	private String from;
	private String fromNick;
	private List<Address> cc = new ArrayList<Address>();
	private List<Address> to = new ArrayList<Address>();
	private String replyTo;
	private String subject;
	private String htmlContent;
	private String plainContent;
	private List<MailRecvAttach> attachments = new ArrayList<MailRecvAttach>();
	public void init(MimeMessageParser parser) throws Exception{
		this.setFrom(parser.getFrom());
		this.setCc(parser.getCc());
		this.setTo(parser.getTo());
		this.setReplyTo(parser.getReplyTo());
		this.setSubject(parser.getSubject());
		this.setHtmlContent(parser.getHtmlContent());
		this.setPlainContent(parser.getPlainContent());
	}
	public String getMailAccount() {
		return mailAccount;
	}
	public void setMailAccount(String mailAccount) {
		this.mailAccount = mailAccount;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public String getFromNick() {
		return fromNick;
	}
	public void setFromNick(String fromNick) {
		this.fromNick = fromNick;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public List<Address> getCc() {
		return cc;
	}
	public void setCc(List<Address> cc) {
		this.cc = cc;
	}
	public List<Address> getTo() {
		return to;
	}
	public void setTo(List<Address> to) {
		this.to = to;
	}
	public String getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	public String getPlainContent() {
		return plainContent;
	}
	public void setPlainContent(String plainContent) {
		this.plainContent = plainContent;
	}
	public List<MailRecvAttach> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<MailRecvAttach> attachments) {
		this.attachments = attachments;
	}
	@Override
	public String toString() {
		return "Email [from=" + from + ", cc=" + cc + ", to=" + to
				+ ", replyTo=" + replyTo + ", subject=" + subject
				+ ", htmlContent=" + htmlContent + ", plainContent="
				+ plainContent + ", attachments=" + attachments + "]";
	}
}
