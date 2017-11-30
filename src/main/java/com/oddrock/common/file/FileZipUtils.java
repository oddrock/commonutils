package com.oddrock.common.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class FileZipUtils {
	private FileZipUtils() {}

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
	public static boolean zip(File srcDir, String zipDirPath, String zipFileName, boolean cover) throws IOException {
		if(!srcDir.exists()){
			System.out.println("待压缩的文件目录：" + srcDir.getCanonicalPath() + "不存在.");
		}
		if(!srcDir.isDirectory()){
			System.out.println("待压缩的文件目录：" + srcDir.getCanonicalPath() + "不是目录");
		}
		return zip(srcDir.listFiles(), zipDirPath, zipFileName, cover);
	}
	
	@SuppressWarnings("resource")
	public static boolean zip(File[] srcFiles, String zipDirPath, String zipFileName, boolean cover) {
		boolean flag = false;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		try {
			File zipFile = new File(zipDirPath, zipFileName + ".zip");
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
				}
				flag = true;
			}
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
		return flag;
	}

	public static void main(String[] args) throws IOException {
		String sourceFilePath = "C:\\Users\\oddro\\Desktop\\哈哈儿";
		String zipFilePath = "C:\\Users\\oddro\\Desktop";
		String fileName = "我的压缩包";
		boolean flag = FileZipUtils.zip(new File(sourceFilePath), zipFilePath,fileName, true);
		if (flag) {
			System.out.println("文件打包成功!");
		} else {
			System.out.println("文件打包失败!");
		}
	}

}