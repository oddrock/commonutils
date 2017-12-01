package com.oddrock.common.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

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

	public static void writeToFile(String filePath, String conent, boolean append) {
		BufferedWriter out = null;
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
		}
	}

	public static void writeToFile(String filePath, String conent, boolean append, String encoding) {
		BufferedWriter out = null;
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
		for (String filePath : getAbsolutePathRecursively(srcDirPath)) {
			String newFilePath = renameFileByAdd(filePath, dstDirPath, null);
			if (remainFlag) {
				copyFile(filePath, newFilePath);
			} else {
				new File(filePath).renameTo(new File(newFilePath));
			}
		}
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
        File[] files = dir.listFiles(); 
        for(File f:files)  {  
        	if(f.isFile() && f.isHidden()){
        		f.delete();
        	}  
        }
	}
	
	 

	public static void main(String[] args) throws IOException {
		// mkDirRecursively("C:\\Users\\oddro\\Desktop");
		/*
		 * for(String path :
		 * getAllFilesAbsoultePathRecursively("C:\\Users\\oddro\\Desktop\\熊逸书院" )){
		 * System.out.println(path); }
		 */
		//gatherAllFiles("C:\\_Download\\薛兆丰经济学课", "C:\\_Download\\得到\\薛兆丰经济学课", true);
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
		
		File  file = new File("C:\\_Download");  
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
        } 
	}

}
