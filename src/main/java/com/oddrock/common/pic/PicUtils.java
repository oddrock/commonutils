package com.oddrock.common.pic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.oddrock.common.file.FileUtils;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;


public class PicUtils {
	/**
	 * 将tiff文件转为jpg文件
	 * @param tiffFilePath
	 * @param jpgFilePath
	 * @throws IOException
	 */
	public static boolean transferTiff2Jpg(String tiffFilePath, String jpgFilePath, boolean overwrite) {
		// 源文件不存在不转换
		if(!FileUtils.fileExists(tiffFilePath)) return false;
		// 目标文件已存在且目标文件不覆盖不转换
		if(FileUtils.fileExists(jpgFilePath) && !overwrite) return false;
		File tiffFile = new File(tiffFilePath);
		// 源文件不是tiff不转换
		if(!FileUtils.isSuffix(tiffFile, "tiff") && !FileUtils.isSuffix(tiffFile, "tif")) return false;	
		
		RenderedOp src2 = JAI.create("fileload", tiffFilePath);
		OutputStream os2 = null;
		try {
			os2 = new FileOutputStream(jpgFilePath);
			JPEGEncodeParam param2 = new JPEGEncodeParam();  
	        //指定格式类型，jpg 属于 JPEG 类型  
	        ImageEncoder enc2 = ImageCodec.createImageEncoder("JPEG", os2, param2);  
	        enc2.encode(src2);  
	        return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(os2!=null) os2.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
        return false;
	}
	
	public static String transferTiff2Jpg(String tiffFilePath, boolean overwrite) throws IOException {
		// 源文件不存在不转换
		if(!FileUtils.fileExists(tiffFilePath)) return null;
		File tiffFile = new File(tiffFilePath);
		// 源文件不是tiff不转换
		if(!FileUtils.isSuffix(tiffFile, "tiff") && !FileUtils.isSuffix(tiffFile, "tif")) return null;	
		String fileNameWithoutSuffix = FileUtils.getFileNameWithoutSuffixFromFilePath(tiffFilePath);
		File jpgFile = new File(tiffFile.getParentFile(), fileNameWithoutSuffix+".jpg");
		if(transferTiff2Jpg(tiffFilePath, jpgFile.getCanonicalPath(), overwrite)){
			return jpgFile.getCanonicalPath();
		}else{
			return null;
		}
		
	}
	
	public static String transferTiff2Jpg(String tiffFilePath) throws IOException {
		return transferTiff2Jpg(tiffFilePath, true);
	}

	public static void main(String[] args) throws IOException {
		String input2 = "C:\\Users\\oddro\\Desktop\\PastedGraphic-7.tiff";  
        transferTiff2Jpg(input2);
	}

}
