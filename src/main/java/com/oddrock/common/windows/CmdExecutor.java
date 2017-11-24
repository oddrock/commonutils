package com.oddrock.common.windows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
			if(line.split("\\s+")[0].equalsIgnoreCase(appname)) {
				return true;
			}
		}
		return false;
	}
}
