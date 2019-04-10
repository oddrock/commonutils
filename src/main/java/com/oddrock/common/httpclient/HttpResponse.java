package com.oddrock.common.httpclient;

public class HttpResponse {
	private int statusCode = -1;
	private String content = null;
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
