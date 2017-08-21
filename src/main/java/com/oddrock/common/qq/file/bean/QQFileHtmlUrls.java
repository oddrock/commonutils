package com.oddrock.common.qq.file.bean;
public class QQFileHtmlUrls {
	private String qqFileName;
	private String qqFileHtmlUrl;
	public String getQqFileName() {
		return qqFileName;
	}
	public void setQqFileName(String qqFileName) {
		this.qqFileName = qqFileName;
	}
	public String getQqFileHtmlUrl() {
		return qqFileHtmlUrl;
	}
	public void setQqFileHtmlUrl(String qqFileHtmlUrl) {
		this.qqFileHtmlUrl = qqFileHtmlUrl;
	}
	@Override
	public String toString() {
		return "QQFileHtmlUrls [qqFileName=" + qqFileName + ", qqFileHtmlUrl="
				+ qqFileHtmlUrl + "]";
	}
}
