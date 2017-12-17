package com.oddrock.common.mail;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;

import com.oddrock.common.CommonProp;
import com.oddrock.common.file.FileUtils;
import com.sun.mail.pop3.POP3Folder;

// 记录有哪些邮件被接收了
public class PopMailReadRecordManager {
	public static PopMailReadRecordManager instance = new PopMailReadRecordManager();
	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyyMMdd");
	private PopMailReadRecordManager(){
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private Map<String, Set<String>> readMailUIDsMap;
	private String combineKey(String mail, String dateStr, String days) {
		String key;
		if(dateStr.equals("all")) {
    		key = mail+"-all-";
    	}else if(dateStr.equals(DATEFORMAT.format(new Date()))) {		
    		key = mail+"-"+dateStr+"-"+days;
    	}else {
    		key = null;
    	}
		return key;
	}
	
	private String combineFilename(String key) {
		return key+CommonProp.get("mail.readrecordfile.suffix");
	}
	
	private void init() throws IOException{
		readMailUIDsMap = new HashMap<String, Set<String>>();
		File dir = new File(CommonProp.get("mail.readrecordfile.dirpath"));
		if(!dir.exists()) return;
		for(File file : dir.listFiles()){
			Pattern pattern1 = Pattern.compile("(.+@.+)-(all|\\d{8})-(\\d*)"+CommonProp.get("mail.readrecordfile.suffix"));
	        Matcher matcher = pattern1.matcher(file.getName());
	        if(matcher.matches()) {
	        	String mail = matcher.group(1);
	        	String dateStr = matcher.group(2);
	        	String days = matcher.group(3);
	        	String key = combineKey(mail, dateStr, days);
	        	if(key!=null) {
	        		String content = FileUtils.readFileContentToStrExt(file.getCanonicalPath());
	        		if(content!=null && content.trim().length()>0){
						Set<String> set = new HashSet<String>();
						readMailUIDsMap.put(key, set);
						for(String UID : content.split(",")){
							if(!StringUtils.isBlank(UID)) set.add(UID);
						}
					}
	        	}else {
	        		file.delete();		// 如果文件名里的时间字符串和今天的不符，就删除本文件
	        	}
	        }
		}
	}
	
	public void clearReadInAllDays(String account) {
		String key = combineKey(account, "all", null);
		clearRead(key);
	}
	
	public void clearReadInRecentDays(String account, int recentDays) {
		String key = combineKey(account, DATEFORMAT.format(new Date()), String.valueOf(recentDays));
		clearRead(key);
	}
	
	// 清理掉所有已读记录
	public void clearRead(String key) {
		Set<String> set = readMailUIDsMap.get(key);
		if(set==null){
			return;
		}
		readMailUIDsMap.remove(key);
		File file = new File(CommonProp.get("mail.readrecordfile.dirpath"),combineFilename(key));
		file.delete();
	}
	
	public void setUnReadInAllDays(String account, String UID) throws MessagingException, IOException {
		String key = combineKey(account, "all", null);
		setUnRead(key, UID);
	}
	
	public void setUnReadInAllDays(String account, POP3Folder folder, Message message) throws MessagingException, IOException {
		String key = combineKey(account, "all", null);
		String UID = folder.getUID(message);
		setUnRead(key, UID);
	}
	
	public void setUnReadInRecentDays(String account, String UID, int recentDays) throws MessagingException, IOException {
		String key = combineKey(account, DATEFORMAT.format(new Date()), String.valueOf(recentDays));
		setUnRead(key, UID);
	}
	
	public void setUnReadInRecentDays(String account, POP3Folder folder, Message message, int recentDays) throws MessagingException, IOException {
		String key = combineKey(account, DATEFORMAT.format(new Date()), String.valueOf(recentDays));
		String UID = folder.getUID(message);
		setUnRead(key, UID);
	}
	
	// 设置某条邮件为未读
	private void setUnRead(String key, String UID) throws MessagingException, IOException{
		Set<String> set = readMailUIDsMap.get(key);
		if(set==null){
			return;
		}
		set.remove(UID);
		StringBuffer sb = new StringBuffer();
		for(String str : set){
			if(!StringUtils.isBlank(str)) sb.append(str).append(",");
		}
		File file = new File(CommonProp.get("mail.readrecordfile.dirpath"), combineFilename(key));
		FileUtils.writeToFile(file.getCanonicalPath(), sb.toString(), false);
	}
	
	public void setReadInAllDays(String account, POP3Folder folder, Message message) throws MessagingException, IOException{
		String UID = folder.getUID(message);
		String key = combineKey(account, "all", null);
		setRead(key, UID);
	}
	
	public void setReadInRecentDays(String account, POP3Folder folder, Message message, int recentDays) throws MessagingException, IOException{
		String UID = folder.getUID(message);
		String key = combineKey(account, DATEFORMAT.format(new Date()), String.valueOf(recentDays));
		setRead(key, UID);
	}
	
	private void setRead(String key, String UID) throws MessagingException, IOException{
		Set<String> set = readMailUIDsMap.get(key);
		if(set==null){
			set = new HashSet<String>();
			readMailUIDsMap.put(key, set);
		}
		set.add(UID);
		StringBuffer sb = new StringBuffer();
		for(String str : set){
			if(!StringUtils.isBlank(str)) sb.append(str).append(",");
		}
		File file = new File(CommonProp.get("mail.readrecordfile.dirpath"), combineFilename(key));
		FileUtils.writeToFile(file.getCanonicalPath(), sb.toString(), false);
	}
	
	
	
	public boolean isReadInAllDays(String account, POP3Folder folder, Message message) throws MessagingException{
		String UID = folder.getUID(message);
		String key = combineKey(account, "all", null);
		return isRead(key, UID);
	}
	
	public boolean isReadInRecentDays(String account, POP3Folder folder, Message message, int recentDays) throws MessagingException{
		String UID = folder.getUID(message);	
		String key = combineKey(account, DATEFORMAT.format(new Date()), String.valueOf(recentDays));
		return isRead(key, UID);
	}
	
	private boolean isRead(String key, String UID) {
		if(!readMailUIDsMap.containsKey(key)){
			return false;
		}else{
			Set<String> set = readMailUIDsMap.get(key);
			if(set.contains(UID)){
				return true;
			}else{
				return false;
			}
		}
	}
	
	// 获得所有邮件的数量
	public int countInAllDays(String account) {
		String key = combineKey(account, "all", null);
		return countByKey(key);
	}
	
	// 获得指定近几天的邮件数量
	public int countInRecentDays(String account, int recentDays) {
		String key = combineKey(account, DATEFORMAT.format(new Date()), String.valueOf(recentDays));
		return countByKey(key);
	}
	
	private int countByKey(String key) {
		if(!readMailUIDsMap.containsKey(key)){
			return 0;
		}else{
			return readMailUIDsMap.get(key).size();
		}
	}
}
