package com.oddrock.common.mailext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import com.oddrock.common.DateUtils;
import com.oddrock.common.file.FileUtils;

public class FileStorageMailRecorder implements MailRecorder {
	private static Logger log = Logger.getLogger(FileStorageMailRecorder.class);
	
	private String storagePath;
	
	private String generateKey(Message message) {
		String seperator = "]-[";
		StringBuffer key = new StringBuffer();
		Date sendDate = null;
		try {
			sendDate = message.getSentDate();
		} catch (MessagingException e) {
			log.warn(ExceptionUtils.getFullStackTrace(e));
			//e.printStackTrace();
		}
		String dateStr = DateUtils.getFormatTime2(sendDate);
		String from = "";
		try {
			from = message.getFrom()[0].toString();
		} catch (MessagingException e) {
			log.warn(ExceptionUtils.getFullStackTrace(e));
			//e.printStackTrace();
		}
		String subject = "";
		try {
			subject = message.getSubject();
		} catch (MessagingException e) {
			log.warn(ExceptionUtils.getFullStackTrace(e));
			//e.printStackTrace();
		}
		key.append(dateStr).append(seperator).append(from).append(seperator).append(subject);
		// 返回使用MD5加密的字符串
		return DigestUtils.md5Hex(key.toString());
	}

	public FileStorageMailRecorder() {
		super();
		//this.storagePath = this.getClass().getResource("").getPath();
		try {
			this.storagePath = new File(System.getProperty("user.dir"), "alimailautodeal-mailrecords.txt").getCanonicalPath();
		} catch (IOException e) {
			log.warn(ExceptionUtils.getFullStackTrace(e));
			//e.printStackTrace();
		}
	}
	
	public FileStorageMailRecorder(String storagePath) {
		super();
		try {
			this.storagePath = new File(storagePath, "alimailautodeal-mailrecords.txt").getCanonicalPath();
		} catch (IOException e) {
			log.warn(ExceptionUtils.getFullStackTrace(e));
			//e.printStackTrace();
		}
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public boolean isMailHasRcv(Message message) {
		String key = generateKey(message);
		if(existsInFileStore(key)) {
			return true;
		}else {
			return false;
		}
		
	}

	private boolean existsInFileStore(String key) {
		Set<String> keySet = readRecordFromFileStore();
		if(keySet.contains(key)) {
			return true;
		}else {
			return false;
		}
	}

	private Set<String> readRecordFromFileStore() {
		Set<String> result = new HashSet<String>();
		File file = new File(storagePath);
		if(file.exists() && file.isFile()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String tempString = null;
				while ((tempString = reader.readLine()) != null) {
					tempString = tempString.trim();
					if(StringUtils.isNotBlank(tempString)) {
						result.add(tempString);
					}
				}
				reader.close();
			} catch (IOException e) {
				log.warn(ExceptionUtils.getFullStackTrace(e));
				//e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
						log.warn(ExceptionUtils.getFullStackTrace(e1));
					}
				}
			}
		}
		return result;
	}

	public void saveRecord(Message message) {
		String key = generateKey(message);
		save2FileStore(key);
	}

	private void save2FileStore(String key) {
		FileUtils.writeLineToFile(storagePath, key, true);
	}

	public void deleteRecord(Message message) {
		String key = generateKey(message);
		deleteFromFileStore(key);
	}
	
	private void deleteFromFileStore(String key) {
		Set<String> keySet = readRecordFromFileStore();
		if(keySet.contains(key)) {
			keySet.remove(key);
			for(String key2 : keySet) {
				FileUtils.writeLineToFile(storagePath, key2, true);
			}
		}
	}

	public static void main(String[] args) {
		FileStorageMailRecorder r = new FileStorageMailRecorder();
		System.out.println(r.getStoragePath());
	}

}
