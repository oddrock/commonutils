package com.oddrock.common.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public final class ZipUtils {
	private static Logger logger = Logger.getLogger(ZipUtils.class);
			
	private ZipUtils() {}

	/**
	 * 将存放在srcDir目录下的源文件，打包成zipFileName名称的zip文件，并存放到zipDirPath路径下
	 * 
	 * @param srcDirPath	待压缩的文件所在目录路径
	 * @param zipDirPath	压缩后存放目录路径
	 * @param zipFileName	压缩后文件的名称（不含zip后缀）
	 * @param cover			是否覆盖已有的zip文件，是则覆盖
	 * @return
	 * @throws IOException 
	 */
	public static ZipResult zip(File srcDir, String zipDirPath, String zipFileName, boolean cover) throws IOException {
		ZipResult result = new ZipResult();
		if(srcDir==null || !srcDir.exists()){
			System.out.println("待压缩的文件目录：" + srcDir.getCanonicalPath() + "不存在.");
			result.setSuccess(false);
			return result;
		}
		if(!srcDir.isDirectory()){
			System.out.println("待压缩的文件目录：" + srcDir.getCanonicalPath() + "不是目录");
			result.setSuccess(false);
			return result;
		}
		return zip(srcDir.listFiles(), zipDirPath, zipFileName, cover);
	}
	
	@SuppressWarnings("resource")
	public static ZipResult zip(File[] srcFiles, String zipDirPath, String zipFileName, boolean cover) {
		ZipResult result = new ZipResult();
		boolean flag = false;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		File zipFile = null;
		try {
			zipFile = new File(zipDirPath, zipFileName + ".zip");
			logger.warn("开始压缩文件到："+zipFile.getCanonicalPath());
			if (zipFile.exists() && cover) {
				zipFile.delete();
			}
			if (zipFile.exists()) {
				System.out.println(zipDirPath + "目录下存在名字为:" + zipFileName + ".zip" + "打包文件.");
			} else {
				fos = new FileOutputStream(zipFile);
				zos = new ZipOutputStream(new BufferedOutputStream(fos));
				byte[] bufs = new byte[1024 * 10];
				for (File srcFile : srcFiles) {
					logger.warn("开始将文件：【"+srcFile.getCanonicalPath()+"】压缩到："+zipFile.getCanonicalPath());
					// 创建ZIP实体，并添加进压缩包
					ZipEntry zipEntry = new ZipEntry(srcFile.getName());
					zos.putNextEntry(zipEntry);
					// 读取待压缩的文件并写进压缩包里
					fis = new FileInputStream(srcFile);
					bis = new BufferedInputStream(fis, 1024 * 10);
					int read = 0;
					while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
						zos.write(bufs, 0, read);
					}
					logger.warn("结束将文件：【"+srcFile.getCanonicalPath()+"】压缩到："+zipFile.getCanonicalPath());
				}
				flag = true;
			}
			logger.warn("结束压缩文件到："+zipFile.getCanonicalPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != bis) bis.close();
				if (null != zos) zos.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		if(flag){
			result.setZipFile(zipFile);
		}
		result.setSuccess(flag);
		return result;
	}
	
	/**
	 * 将文件打包，每个打包文件不超过zipFileMaxSize大小，除非单个文件已经超过这个大小
	 * @param srcDir
	 * @param zipDirPath
	 * @param zipFileName
	 * @param cover
	 * @param zipFileMaxSize	zip文件最大大小，单位Byte
	 * @return
	 * @throws IOException
	 */
	public static Set<ZipResult> zip(File[] srcFiles, String zipDirPath, String zipFileName, boolean cover, long zipFileMaxSize) throws IOException {
		Set<ZipResult> result = new HashSet<ZipResult>();
		// 如果zipFileMaxSize参数小于等于0，说明不需要控制压缩包的大小
		if(zipFileMaxSize<=0){
			result.add(zip(srcFiles, zipDirPath, zipFileName, cover));
		}
		Set<File> srcFileSet = new HashSet<File>(Arrays.asList(srcFiles));
		// 记录要压缩的文件的总大小
		Long totalFileSize = 0L;
		// 生成的压缩文件名中的序号
		int index = 1;
		// 临时存放本批次要压缩的文件的Set
		Set<File> waitZipFileSet = new HashSet<File>();
		Iterator<File> iterator = srcFileSet.iterator();
		
		while(iterator.hasNext()){
			File file = iterator.next();
			// 如果一个文件已经超过最大闲置，就单独打包
			if(file.length()>=zipFileMaxSize){
				result.add(zip(new File[]{file}, zipDirPath, zipFileName+StringUtils.leftPad(String.valueOf(index), 2, "0"), cover));
				index++;
				iterator.remove();
			}
			// 如果本文件和tmpFileSet中文件的大小加在一起小于zipFileMaxSize，就把本文件加入本批次
			else if((totalFileSize+file.length())<zipFileMaxSize){
				waitZipFileSet.add(file);
				iterator.remove();
				totalFileSize += file.length();
			}else{
				File[] tmpFileArray = new File[waitZipFileSet.size()];
				int i = 0;
				for(File f:waitZipFileSet){
					tmpFileArray[i] =f;
					i++;
				}
				// 如果本文件和tmpFileSet中文件的大小加在一起大于等于zipFileMaxSize，就将本批次压缩
				result.add(zip(tmpFileArray, zipDirPath, zipFileName+StringUtils.leftPad(String.valueOf(index), 2, "0"), cover));
				index++;
				waitZipFileSet.clear();
				waitZipFileSet.add(file);
				iterator.remove();
				totalFileSize = file.length();
			}
		}
		// 如果还剩下文件
		if(waitZipFileSet.size()>0){
			File[] tmpFileArray = new File[waitZipFileSet.size()];
			int i = 0;
			for(File file:waitZipFileSet){
				tmpFileArray[i] =file;
				i++;
			}
			result.add(zip(tmpFileArray, zipDirPath, zipFileName+StringUtils.leftPad(String.valueOf(index), 2, "0"), cover));
		}
		return result;
	}
	
	public static Set<ZipResult> zip(File srcDir, String zipDirPath, String zipFileName, boolean cover, long zipFileMaxSize) throws IOException {
		Set<ZipResult> result = new HashSet<ZipResult>();
		if(srcDir==null || !srcDir.exists()){
			System.out.println("待压缩的文件目录：" + srcDir.getCanonicalPath() + "不存在.");
			return result;
		}
		if(!srcDir.isDirectory()){
			System.out.println("待压缩的文件目录：" + srcDir.getCanonicalPath() + "不是目录");
			return result;
		}
		return zip(srcDir.listFiles(), zipDirPath, zipFileName, cover, zipFileMaxSize);
	}

	public static void main(String[] args) throws IOException {
		String sourceFilePath = "C:\\Users\\oddro\\Desktop\\哈哈儿";
		String zipFilePath = "C:\\Users\\oddro\\Desktop";
		String fileName = "我的压缩包";
		ZipUtils.zip(new File(sourceFilePath), zipFilePath,fileName, true, 15*1024*1024);
		/*if (flag) {
			System.out.println("文件打包成功!");
		} else {
			System.out.println("文件打包失败!");
		}*/
		/*for (File file: new File("C:\\Users\\oddro\\Desktop\\哈哈儿\\").listFiles()){
			System.out.println(file.getName() + "："+(double)file.length()/(double)1024);
		}*/
	}

}