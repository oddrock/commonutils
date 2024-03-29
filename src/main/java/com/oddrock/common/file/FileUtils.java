package com.oddrock.common.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.oddrock.common.OddrockStringUtils;
import com.oddrock.common.windows.SensitiveStringUtils;

public class FileUtils {
	private static Logger logger = Logger.getLogger(FileUtils.class);

	/**
	 * 从文件路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameFromFilePath(String filePath) {
		File file = new File(filePath);
		return file.getName();
	}

	/**
	 * 从文件路径获取不带后缀的文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameWithoutSuffixFromFilePath(String filePath) {
		String fileName = getFileNameFromFilePath(filePath);
		if (fileName.lastIndexOf(".") > 0) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
		return fileName;
	}

	/**
	 * 从文件路径获取所在目录路径
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getDirPathFromFilePath(String filePath) {
		File file = new File(filePath);
		return file.getParent();
	}

	/**
	 * 获取文件后缀名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileNameSuffix(String fileName) {
		if (fileName != null) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}
	
	public static String getFileNameSuffix(File file) {
		return getFileNameSuffix(file.getName());
	}

	/**
	 * 将文件作为一个字符串整体读取
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFileContentToStr(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return sb.toString();
	}
	
	public static Set<String> readFileContentPerLine(String fileName) {
		Set<String> lines = new HashSet<String>();
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				lines.add(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return lines;
	}
	
	public static Set<String> readFileContentPerLineEncoding(String fileName, String encoding) {
		Set<String> lines = new HashSet<String>();
		BufferedReader reader = null;
		FileInputStream fr = null;
		InputStreamReader is = null;
		try {
			fr = new FileInputStream(fileName); 
			is = new InputStreamReader(fr,encoding); 
			reader = new BufferedReader(is);
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				lines.add(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return lines;
	}
	
	public static Set<String> readFileContentPerLineEncoding(String fileName) {
		return readFileContentPerLineEncoding(fileName, "UTF-8");
	}
	
	public static String readFileContentToStrExt(String fileName) throws IOException {
		File file = new File(fileName);
		String encoding = getEncoding(file);
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 更改文件名称
	 * 
	 * @param srcFilePath
	 *            文件路径
	 * @param newName
	 *            新的文件名，不包括后缀和前面的目录路径
	 * @return
	 */
	public static String renameFile(String srcFilePath, String newName) {
		String dirPath = getDirPathFromFilePath(srcFilePath);
		String suffix = getFileNameSuffix(srcFilePath);
		if (suffix.length() > 0) {
			newName = newName + "." + suffix;
		}
		String separator = java.io.File.separator;
		return dirPath + separator + newName;
	}

	/**
	 * 在文件名后面追加字符形成新文件名
	 * 
	 * @param srcFilePath
	 * @param addStr
	 * @return
	 */
	public static String renameFileByAdd(String srcFilePath, String addStr) {
		String fileName = getFileNameWithoutSuffixFromFilePath(srcFilePath);
		return renameFile(srcFilePath, fileName + addStr);
	}

	/**
	 * 在文件名后面追加字符形成新文件名，且目录路径也发生变化
	 * 
	 * @param srcFilePath
	 * @param destDirPath
	 * @param addStr
	 * @return
	 */
	public static String renameFileByAdd(String srcFilePath, String destDirPath, String addStr) {
		String fileName = getFileNameWithoutSuffixFromFilePath(srcFilePath);
		String suffix = getFileNameSuffix(srcFilePath);
		if (addStr != null && addStr.length() > 0) {
			fileName = fileName + addStr;
		}
		if (suffix.length() > 0) {
			fileName = fileName + "." + suffix;
		}
		return destDirPath + java.io.File.separator + fileName;
	}

	public static void mkdirIfNotExists(String dirPath) {
		File dirname = new File(dirPath);
		if (!dirname.isDirectory()) { // 目录不存在
			dirname.mkdir(); // 创建目录
		}
	}

	public static void writeToFile(String filePath, String content, boolean append) {
		/*BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, append)));
			out.write(conent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		writeToFile(filePath, content, append, "UTF-8");	// 默认采用UTF-8编码
	}
	

	public static void writeToFile(String filePath, String conent, boolean append, String encoding) {
		BufferedWriter out = null;
		File file = new File(filePath);
		if(!file.exists() || !file.isFile()) {
			append = false;
		}
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, append), encoding));
			out.write(conent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将一行数据追加到文件
	 * 
	 * @param filePath
	 * @param conent
	 * @param append
	 */
	public static void writeLineToFile(String filePath, String conent, boolean append) {
		writeToFile(filePath, conent + "\n", append);
	}

	/**
	 * 递归获得一个文件夹下所有的文件的绝对路径 参考：http://www.cnblogs.com/azhqiang/p/4596793.html
	 * 
	 * @param srcDirPath
	 * @return
	 */
	public static List<String> getAbsolutePathRecursively(String srcDirPath) {
		List<String> result = new ArrayList<String>();
		File file = new File(srcDirPath);
		if (file.exists()) {
			LinkedList<File> dirList = new LinkedList<File>();
			File[] files = file.listFiles();
			for (File file2 : files) {
				if (file2.isDirectory()) {
					dirList.add(file2);
				} else {
					result.add(file2.getAbsolutePath());
				}
			}
			File temp_file;
			while (!dirList.isEmpty()) {
				temp_file = dirList.removeFirst();
				files = temp_file.listFiles();
				for (File file2 : files) {
					if (file2.isDirectory()) {
						dirList.add(file2);
					} else {
						result.add(file2.getAbsolutePath());
					}
				}
			}
		}
		return result;
	}
	
	public static List<String> getChildrenFilePathRecursively(String srcDirPath) {
		List<String> result = new ArrayList<String>();
		if(!FileUtils.dirExists(srcDirPath) && !FileUtils.fileExists(srcDirPath)){
			return result;			// 如果不是目录也不是文件，直接返回。
		}
		if(FileUtils.fileExists(srcDirPath)){		// 如果是文件，直接返回该文件
			try {
				result.add(new File(srcDirPath).getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
		Queue<File> queue = new LinkedList<File>();
		queue.add(new File(srcDirPath));
		while (!queue.isEmpty()) {
			File dir = queue.remove();
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					queue.add(file);
				} else if(file.isFile()) {
					result.add(file.getAbsolutePath());
				}
			}
		}
		return result;
	}

	/**
	 * 递归创建目录 参考：http://blog.csdn.net/hephec/article/details/37960617
	 * 
	 * @param dirPath
	 */
	public static void mkDirRecursively(String dirPath) {
		File file = new File(dirPath);
		if (file.getParentFile().exists()) {
			file.mkdir();
		} else {
			mkDirRecursively(file.getParentFile().getAbsolutePath());
			file.mkdir();
		}
	}
	
	public static void copyFileToDir(File srcFile, File dir) throws IOException {
		if(dir==null) return;
		if(!dir.exists()) dir.mkdirs();
		copyFile(srcFile.getCanonicalPath(), new File(dir,srcFile.getName()).getCanonicalPath());
	}
	
	// 将某个文件夹下文件拷贝到另一个文件夹下
	public static void copyFileInSrcDirToDstDir(File srcDir, File dstDir) throws IOException {
		if(srcDir==null || dstDir==null || !srcDir.exists() || !srcDir.isDirectory()) return;
		for(File file : srcDir.listFiles()) {
			if(file.isFile()) copyFileToDir(file, dstDir);
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param srcFilePath
	 * @param dstFilePath
	 */
	public static void copyFile(String srcFilePath, String dstFilePath) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			int byteread = 0;
			File srcFile = new File(srcFilePath);
			if (srcFile.exists() && srcFile.isFile()) { 
				in = new FileInputStream(srcFilePath); 
				out = new FileOutputStream(dstFilePath);
				byte[] buffer = new byte[1024];
				while ((byteread = in.read(buffer)) != -1) {
					out.write(buffer, 0, byteread);
				}
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(in!=null) in.close();
				if(out!=null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 将一个目录下的所有文件汇总到目标目录下 参考：http://www.cnblogs.com/acm-bingzi/p/javaFileMove.html
	 * 
	 * @param srcDirPath
	 * @param dstDirPath
	 * @param remainFlag
	 *            为true则保留源目录下文件，否则不保留
	 */
	public static void gatherAllFiles(String srcDirPath, String dstDirPath, boolean remainFlag) {
		mkDirRecursively(dstDirPath); // 创建目标目录
		for (String filePath : getChildrenFilePathRecursively(srcDirPath)) {
			String newFilePath = null;
			try {
				newFilePath = new File(new File(dstDirPath), new File(filePath).getName()).getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(newFilePath!=null){
				if (remainFlag) {
					copyFile(filePath, newFilePath);
				} else {
					new File(filePath).renameTo(new File(newFilePath));
				}
			}
		}
	}
	
	public static void gatherAllFiles(String srcDirPath, boolean remainFlag) {
		gatherAllFiles(srcDirPath, srcDirPath, remainFlag);
	}
	
	// 将文件夹下的所有文件（递归）集中到父文件夹
	public static void gatherAllFiles(String srcDirPath) {
		gatherAllFiles(srcDirPath, srcDirPath, false);
	}

	/**
	 * 将文件移动到某个目录
	 * 
	 * @param srcFilePath
	 * @param dstDirPath
	 * @param cover
	 *            如果目标文件夹有该文件，且cover为true，则覆盖
	 */
	public static void moveFile(String srcFilePath, String dstDirPath, boolean cover) {
		File srcFile = new File(srcFilePath);
		if (!srcFile.exists()) {
			logger.error(srcFilePath + "：文件不存在，无法移动！");
			return;
		}
		if (!srcFile.isFile()) {
			logger.error(srcFilePath + "：不是文件，无法移动！");
		}
		File dstDir = new File(dstDirPath);
		if (!dstDir.exists()) {
			dstDir.mkdirs();
		}
		File dstFile = new File(dstDir, srcFile.getName());
		if (dstFile.exists() && dstFile.isFile()) {
			if (cover) {
				dstFile.delete();
				srcFile.renameTo(dstFile);
			} else {
				logger.error(dstFile + "：文件已存在，无法移动！");
			}
		} else {
			srcFile.renameTo(dstFile);
		}

	}

	/**
	 * 将文件移动到某个目录
	 * 
	 * @param srcFilePath
	 * @param dstDirPath
	 */
	public static void moveFile(String srcFilePath, String dstDirPath) {
		moveFile(srcFilePath, dstDirPath, true);
	}

	private static String toHex(byte[] byteArray) {
		int i;
		StringBuffer buf = new StringBuffer("");
		int len = byteArray.length;
		for (int offset = 0; offset < len; offset++) {
			i = byteArray[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		return buf.toString().toUpperCase();
	}
	
	/**
	 * 获取文本文件的编码格式
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getEncoding(File file) throws IOException{
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
		byte[] b = new byte[10];
		// 这里可以看到各种编码的前几个字符是什么，gbk编码前面没有多余的
		String code = null;
		try {
			bin.read(b, 0, b.length);
			String first = toHex(b);		
			if (first.startsWith("EFBBBF")) {
				code = "UTF-8";
			} else if (first.startsWith("FEFF00")) {
				code = "UTF-16BE";
			} else if (first.startsWith("FFFE")) {
				code = "Unicode";
			} else if (first.startsWith("FFFE")) {
				code = "Unicode";
			} else {
				code = "GBK";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(bin!=null) bin.close();;
		}
		return code;
	}
	
	// 确保删除该文件
	public static void deleteAndConfirm(File file) throws InterruptedException {
		if(file==null || !file.exists() || !file.isFile()) return;
		while(!file.delete()) {
			Thread.sleep(1000);
		}
	}
	
	// 删除所有隐藏文件
	public static void deleteHiddenFiles(File dir){   
        if(dir==null || !dir.exists() || !dir.isDirectory()) return;
        File[] files = dir.listFiles(); 
        for(File f:files)  {  
        	if(f.isFile() && f.isHidden()){
        		f.delete();
        	}  
        }
	}
	
	// 删除目录及目录下所有文件
	public static void deleteDirAndAllFiles(File dir) throws IOException {
		if(dir==null || !dir.exists()) return;
		if(dir.isFile()) {
			dir.delete();
			return;
		}
		for(File file : dir.listFiles()) {
			if(file==null || !file.exists()) {
				continue;
			}else if(file.isFile()) {
				file.delete();
				continue;
			}else {
				deleteDirAndAllFiles(file);
			}
		}
		dir.delete();
	}
	
	// 清空目录
	public static void clearDir(File dir) {
		if(dir==null || !dir.exists() || !dir.isDirectory()) return;
		for(File file : dir.listFiles()) {
			if(file==null || !file.exists()) continue;
			if(file.isFile()) {
				file.delete();
			}else if(file.isDirectory()) {
				clearDir(file);
				file.delete();
			}
		}
		dir.delete();
	}
	
	public static File copyDirToParentDir(File srcDir, File dstParentDir) throws IOException {
		File dstDir = new File(dstParentDir, srcDir.getName());
		dstDir.mkdirs();
		if(srcDir.exists() && srcDir.isDirectory() && srcDir.listFiles()!=null) {
			for(File file : srcDir.listFiles()) {
				File dstFile = new File(dstDir, file.getName());
				copyFile(file.getCanonicalPath(), dstFile.getCanonicalPath());
			}
		}
		return dstDir;
	}
	
	public static File mvDirToParentDir(File srcDir, File dstParentDir) throws IOException {
		File dstDir = new File(dstParentDir, srcDir.getName());
		dstDir.mkdirs();
		for(File file : srcDir.listFiles()) {
			if(file.isFile()) {
				File dstFile = new File(dstDir, file.getName());
				dstFile.delete();
				file.renameTo(dstFile);
			}else if(file.isDirectory()) {
				mvDirToParentDir(file, dstDir);
			}
		}
		srcDir.delete();
		return dstDir;
	}
	
	// 创建文件的父目录
	public static void mkDirOfParent(File file){
		if(!file.exists()){
			new File(file.getParent()).mkdirs();
		}
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
	}
	
	/**
	 * 根据路径判断文件是否存在（这里的文件不包括目录）
	 * @param filePath
	 * @return
	 */
	public static boolean fileExists(String filePath){
		if(filePath==null) return false;
		File file = new File(filePath);
		if(file.exists() && file.isFile()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断文件是否存在（这里的文件不包括目录）
	 * @param file
	 * @return
	 */
	public static boolean fileExists(File file){
		if(file!=null && file.exists() && file.isFile()){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean dirExists(String dirPath){
		if(dirPath==null) return false;
		File dir = new File(dirPath);
		if(dir.exists() && dir.isDirectory()){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean dirExists(File dir){
		if(dir!=null && dir.exists() && dir.isDirectory()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 文件是否以指定后缀结尾
	 * @param file
	 * @param suffix
	 * @param captitalSensitive		后缀是否大小写敏感
	 * @return
	 */
	public static boolean isSuffix(File file, String suffix, boolean captitalSensitive){
		if(file==null || !file.exists() || !file.isFile() || suffix==null){
			return false;
		}
		suffix = suffix.trim();
		if(captitalSensitive){
			if(suffix.equals(FileUtils.getFileNameSuffix(file.getName()))){
				return true;
			}else{
				return false;
			}
		}else{
			if(suffix.equalsIgnoreCase(FileUtils.getFileNameSuffix(file.getName()))){
				return true;
			}else{
				return false;
			}
		}
		
	}
	
	/**
	 * 文件是否以指定后缀结尾，大小写不敏感
	 * @param file
	 * @param suffix
	 * @return
	 */
	public static boolean isSuffix(File file, String suffix){
		return isSuffix(file, suffix, false);
	}
	
	public static void main(String[] args) throws IOException {
		// mkDirRecursively("C:\\Users\\oddro\\Desktop");
		/*
		 * for(String path :
		 * getAllFilesAbsoultePathRecursively("C:\\Users\\oddro\\Desktop\\熊逸书院" )){
		 * System.out.println(path); }
		 */
		
		/*
		 * gatherAllFiles("C:\\Users\\oddro\\Desktop\\关系攻略",
		 * "C:\\Users\\oddro\\Desktop\\得到\\关系攻略", true);
		 * gatherAllFiles("C:\\Users\\oddro\\Desktop\\熊逸书院",
		 * "C:\\Users\\oddro\\Desktop\\得到\\熊逸书院", true);
		 * gatherAllFiles("C:\\Users\\oddro\\Desktop\\武志红心理学",
		 * "C:\\Users\\oddro\\Desktop\\得到\\武志红心理学", true);
		 * gatherAllFiles("C:\\Users\\oddro\\Desktop\\5分钟商学院",
		 * "C:\\Users\\oddro\\Desktop\\得到\\5分钟商学院", true);
		 */
		/*for(File file : new File("C:\\Users\\qzfeng\\Desktop\\cajwait").listFiles()) {
			System.out.println(getEncoding(file));
		}*/
		
		/*File  file = new File("C:\\_Download");  
		deleteHiddenFiles(file);
        File[] files = file.listFiles(); 
        System.out.println("第一遍");
        for(File f:files)  {  
        	if(f.isFile() && f.isHidden()){
        		System.out.println(f);
        		f.delete();
        	}  
        }  
        System.out.println("第二遍");
        for(File f:files)  {  
        	if(f.isFile() && f.isHidden()){
        		System.out.println(f);
        	}  
        }*/ 
		
		gatherAllFiles("C:\\Users\\oddro\\Desktop\\卫安原\\2016");
		
		/*String result = readFileContentToStr("C:\\Users\\oddro\\Desktop\\新建文本文档 (2).txt");
		String[] arr = result.split("\\n");
		String model = "http://quotes.money.163.com/trade/lsjysj_zhishu_XXXXXX.html";
		for(int i=0;i<arr.length; i++) {
			System.out.println(model.replace("XXXXXX", OddrockStringUtils.leftPad(arr[i], 6, '0')));
		}*/
		
	}
	
	public static String removeInvaildSymbolInFileName(String fileName) {
		fileName = fileName.trim();
		fileName = OddrockStringUtils.deleteSpecCharacters(fileName);
		fileName = SensitiveStringUtils.replaceSensitiveString(fileName);
		Pattern p = Pattern.compile("\n");
        Matcher m = p.matcher(fileName);
        fileName = m.replaceAll("");
        Pattern pattern = Pattern.compile("[\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName);
        fileName= matcher.replaceAll(""); // 将匹配到的非法字符以空替换
        return fileName;
	}
}
