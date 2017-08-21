package com.oddrock.common.email.bean;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;

public class Email {
	private String from;
	private List<Address> cc = new ArrayList<Address>();
	private List<Address> to = new ArrayList<Address>();
	private String replyTo;
	private String subject;
	private String htmlContent;
	private String plainContent;
	private List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
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
	public List<EmailAttachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<EmailAttachment> attachments) {
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
