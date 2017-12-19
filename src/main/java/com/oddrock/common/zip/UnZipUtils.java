package com.oddrock.common.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

/**
 * 解压文件公用类
 *
 */
public class UnZipUtils {	
	// 检验文件是否能被解压
	public static boolean canDeCompress(String sourceFile){
		String type = sourceFile.substring(sourceFile.lastIndexOf(".") + 1);
		if(type.equalsIgnoreCase("zip") || type.equalsIgnoreCase("rar")){
			return true;
		}else{
			return false;
		}
	}
	
	// 这里用到了synchronized ，也就是防止出现并发问题
	public static synchronized void deCompress(String srcFilePath, String dstDirPath)throws Exception {
		// 保证文件夹路径最后是"/"或者"\"
		char lastChar = dstDirPath.charAt(dstDirPath.length() - 1);
		if (lastChar != '/' && lastChar != '\\') {
			dstDirPath += File.separator;
		}
		// 根据类型，进行相应的解压缩
		String type = srcFilePath.substring(srcFilePath.lastIndexOf(".") + 1);
		if (type.equalsIgnoreCase("zip")) {
			unZip(srcFilePath, dstDirPath);
		} else if (type.equalsIgnoreCase("rar")) {
			unRar(srcFilePath, dstDirPath);
		} else {
			throw new Exception("只支持zip和rar格式的压缩包！");
		}
	}

	/**
	 * 解压zip格式的压缩文件到指定位置
	 * 
	 * @param zipFilePath	压缩文件
	 * @param dstDirPath		解压目录
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static boolean unZip(String zipFilePath, String dstDirPath)
			throws Exception {
		System.setProperty("sun.zip.encoding",
				System.getProperty("sun.jnu.encoding"));
		try {
			(new File(dstDirPath)).mkdirs();
			File f = new File(zipFilePath);
			ZipFile zipFile = new ZipFile(zipFilePath, "GBK"); // 处理中文文件名乱码的问题
			if ((!f.exists()) && (f.length() <= 0)) {
				throw new Exception("要解压的文件不存在!");
			}
			String strPath, gbkPath, strtemp;
			File tempFile = new File(dstDirPath);
			strPath = tempFile.getAbsolutePath();
			Enumeration<?> e = zipFile.getEntries();
			while (e.hasMoreElements()) {
				ZipEntry zipEnt = (ZipEntry) e.nextElement();
				gbkPath = zipEnt.getName();
				if (zipEnt.isDirectory()) {
					strtemp = strPath + File.separator + gbkPath;
					File dir = new File(strtemp);
					dir.mkdirs();
					continue;
				} else {
					// 读写文件
					InputStream is = zipFile.getInputStream(zipEnt);
					BufferedInputStream bis = new BufferedInputStream(is);
					gbkPath = zipEnt.getName();
					strtemp = strPath + File.separator + gbkPath;

					// 建目录
					String strsubdir = gbkPath;
					for (int i = 0; i < strsubdir.length(); i++) {
						if (strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
							String temp = strPath + File.separator
									+ strsubdir.substring(0, i);
							File subdir = new File(temp);
							if (!subdir.exists())
								subdir.mkdir();
						}
					}
					FileOutputStream fos = new FileOutputStream(strtemp);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					int c;
					while ((c = bis.read()) != -1) {
						bos.write((byte) c);
					}
					bos.close();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据原始rar路径，解压到指定文件夹下.
	 * 
	 * @param rarFilePath		原始rar路径
	 * @param dstDirPath	解压到的文件夹
	 */
	public static void unRar(String rarFilePath, String dstDirPath) {
		if (!rarFilePath.toLowerCase().endsWith(".rar")) {
			System.out.println("非rar文件！");
			return;
		}
		File dstDiretory = new File(dstDirPath);
		if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
			dstDiretory.mkdirs();
		}
		Archive a = null;
		try {
			a = new Archive(new File(rarFilePath));
			if (a != null) {
				// a.getMainHeader().print(); // 打印文件信息.
				FileHeader fh = a.nextFileHeader();

				while (fh != null) {

					// 防止文件名中文乱码问题的处理

					String fileName = fh.getFileNameW().isEmpty() ? fh
							.getFileNameString() : fh.getFileNameW();
					if (fh.isDirectory()) { // 文件夹
						File fol = new File(dstDirPath + File.separator
								+ fileName);
						fol.mkdirs();
					} else { // 文件
						File out = new File(dstDirPath + File.separator
								+ fileName.trim());
						try {
							if (!out.exists()) {
								if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录.
									out.getParentFile().mkdirs();
								}
								out.createNewFile();
							}
							FileOutputStream os = new FileOutputStream(out);
							a.extractFile(fh, os);
							os.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					fh = a.nextFileHeader();
				}
				a.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String filePath = "C:\\Users\\oddro\\Desktop\\哈哈儿.rar";
		String dstDirPath = "C:\\Users\\oddro\\Desktop\\test";
		deCompress(filePath, dstDirPath);
	}
}