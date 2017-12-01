package com.oddrock.common.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdExecutor {
	private static final CmdExecutor instance = new CmdExecutor();
	private CmdExecutor(){}
	public static CmdExecutor getSingleInstance(){
		return instance;
	}
	
	public CmdResult exeCmd(String cmd){
		CmdResult cmdResult = new CmdResult();
		Runtime rt = Runtime.getRuntime();
		Process p;
		try {
			p = rt.exec(cmd);
			cmdResult.setSuccess(true);
			cmdResult.setMessage(p.toString());
		} catch (IOException e) {
			e.printStackTrace();
			cmdResult.setSuccess(false);
			cmdResult.setMessage(e.getMessage());
		}
		return cmdResult;
	}
	
	/**
	 * 打开目录对应窗口
	 * @param path
	 * @return
	 */
	public CmdResult openDirWindows(String path) {
		return exeCmd("explorer \"" + path + "\"");
	}
	
	/**
	 * 检测指定APP是否已打开
	 * @param appname	
	 * @throws IOException 
	 */
	public boolean isAppAlive(String appname) throws IOException {
		Process p = Runtime.getRuntime().exec("tasklist /svc");  
		InputStream is = p.getInputStream(); 
		InputStreamReader ir = new InputStreamReader(is, "GBK");  
		BufferedReader br = new BufferedReader(ir);
		String line = null;
		while((line=br.readLine())!=null) {
			// 按数字划分才不会让名字中间有空格的appname被错误分割
			if(line.split("\\d+")[0].trim().equalsIgnoreCase(appname.trim())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 将tasklist内容输出到一个文件
	 * @param dstFile
	 * @throws IOException
	 */
	public void exportTasklistToFile(File dstFile) throws IOException {
		CmdExecutor.getSingleInstance().exeCmd("tasklist /svc > \""+dstFile.getCanonicalPath() + "\"");
	}
	
	/**
	 * 获得进程号
	 * @param appname
	 * @return
	 * @throws IOException
	 */
	public String getProcNum(String appname) throws IOException {
		Process p = Runtime.getRuntime().exec("tasklist /svc");  
		InputStream is = p.getInputStream(); 
		InputStreamReader ir = new InputStreamReader(is, "GBK");  
		BufferedReader br = new BufferedReader(ir);
		String line = null;
		Pattern pattern = Pattern.compile("\\d+");   
		while((line=br.readLine())!=null) {
			// 按数字划分才不会让名字中间有空格的appname被错误分割
			if(line.split("\\d+")[0].trim().equalsIgnoreCase(appname.trim())) {
				Matcher mather = pattern.matcher(line); 
				if(mather.find()) {
					return mather.group(0);
				}

			}
		}
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(CmdExecutor.getSingleInstance().getProcNum("FineReader.exe"));
	}
}
