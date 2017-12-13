package com.oddrock.common.mail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;

import com.oddrock.common.CommonProp;
import com.oddrock.common.file.FileUtils;
import com.sun.mail.pop3.POP3Folder;

// 记录有哪些邮件被接收了
public class PopMailReadRecordManager {
	public static PopMailReadRecordManager instance = new PopMailReadRecordManager();
	private PopMailReadRecordManager(){
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private Map<String, Set<String>> readMailUIDsMap;
	private void init() throws IOException{
		readMailUIDsMap = new HashMap<String, Set<String>>();
		File dir = new File(CommonProp.get("mail.readrecordfile.dirpath"));
		if(!dir.exists()) return;
		for(File file : dir.listFiles()){
			if(file.getName().endsWith(CommonProp.get("mail.readrecordfile.suffix"))){
				String content = FileUtils.readFileContentToStr(file.getCanonicalPath());
				String mail = file.getName().replace(CommonProp.get("mail.readrecordfile.suffix"), "");
				if(content!=null && content.trim().length()>0){
					Set<String> set = new HashSet<String>();
					readMailUIDsMap.put(mail, set);
					for(String UID : content.split(",")){
						if(!StringUtils.isBlank(UID)) set.add(UID);
					}
				}
			}
		}
	}
	
	public void setRead(String account, POP3Folder folder, Message message) throws MessagingException, IOException{
		String UID = folder.getUID(message);
		Set<String> set = readMailUIDsMap.get(account);
		if(set==null){
			set = new HashSet<String>();
			readMailUIDsMap.put(account, set);
		}
		set.add(UID);
		StringBuffer sb = new StringBuffer();
		for(String str : set){
			if(!StringUtils.isBlank(str)) sb.append(str).append(",");
		}
		File file = new File(CommonProp.get("mail.readrecordfile.dirpath"), account+CommonProp.get("mail.readrecordfile.suffix"));
		FileUtils.writeToFile(file.getCanonicalPath(), sb.toString(), false);
	}
	
	public void setUnRead(String account, String UID) throws MessagingException, IOException{
		Set<String> set = readMailUIDsMap.get(account);
		if(set==null){
			return;
		}
		set.remove(UID);
		StringBuffer sb = new StringBuffer();
		for(String str : set){
			if(!StringUtils.isBlank(str)) sb.append(str).append(",");
		}
		File file = new File(CommonProp.get("mail.readrecordfile.dirpath"), account+CommonProp.get("mail.readrecordfile.suffix"));
		FileUtils.writeToFile(file.getCanonicalPath(), sb.toString(), false);
	}
	
	public boolean isRead(String account, POP3Folder folder, Message message) throws MessagingException{
		String UID = folder.getUID(message);
		if(!readMailUIDsMap.containsKey(account)){
			return false;
		}else{
			Set<String> set = readMailUIDsMap.get(account);
			if(set.contains(UID)){
				return true;
			}else{
				return false;
			}
		}
	}

	public static void main(String[] args) {
	
	}
}
