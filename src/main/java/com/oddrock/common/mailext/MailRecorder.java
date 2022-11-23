package com.oddrock.common.mailext;

import javax.mail.Message;

public interface MailRecorder {
	/**
	 * 该封mail是否已经收取
	 * @param message
	 * @return
	 */
	boolean isMailHasRcv(Message message);
	
	/**
	 * 记录下这封Mail已经收到
	 * @param message
	 */
	void saveRecord(Message message);
	
	/**
	 * 删除这封Mail的记录
	 * @param message
	 */
	void deleteRecord(Message message);
}
