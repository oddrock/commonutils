package com.oddrock.common.mail.qqmail;

/**
 * QQ文件中转站下载页面
 * @author oddrock
 *
 */
public class QQFileDownloadPage {
	private String fileName;
	private String pageUrl;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	@Override
	public String toString() {
		return "QQFileDownloadPage [fileName=" + fileName + ", pageUrl="
				+ pageUrl + "]";
	}
}
