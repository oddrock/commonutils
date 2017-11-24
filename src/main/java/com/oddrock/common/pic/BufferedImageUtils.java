package com.oddrock.common.pic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.oddrock.common.awt.RobotManager;

public class BufferedImageUtils {
	/**
	 * 将BufferedImage写入文件，默认写为bmp文件
	 * @param image			
	 * @param parentDirPath				父目录路径
	 * @param fileNameWithoutSuffix		文件名（不带后缀）
	 * @throws IOException
	 */
	public static File write(BufferedImage image, String parentDirPath, 
			String fileNameWithoutSuffix) throws IOException{
		File parentDir = new File(parentDirPath);
		// 如果父目录不存在，则级联创建父目录
		parentDir.mkdirs();
		File bmpFile = new File(parentDir, fileNameWithoutSuffix+".bmp");
		ImageIO.write(image,"bmp",bmpFile); 
		return bmpFile;	 
	}
	
	/**
	 * 将BufferedImage写入文件，默认写为bmp文件，用UUID做文件名
	 * @param image
	 * @param parentDirPath
	 * @return
	 * @throws IOException
	 */
	public static File write(BufferedImage image, String parentDirPath) throws IOException{
		return write(image, parentDirPath, UUID.randomUUID().toString());
	}
	
	/**
	 * 从文件中读取出BufferedImage
	 * @param imgFile
	 * @return
	 * @throws IOException 
	 */
	public static BufferedImage read(File imgFile) throws IOException{
		return ImageIO.read(imgFile);
	}
	
	/**
	 * 从文件中读取出BufferedImage
	 * @param imgFilePath
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage read(String imgFilePath) throws IOException{
		return read(new File(imgFilePath));
	}
	
	/**
	 * 截图并保存为文件
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param parentDirPath
	 * @throws IOException
	 */
	public static void captureImageAndSave(RobotManager robotMngr, int x, int y, int width, int height, String parentDirPath, String fileNameWithoutSuffix) throws IOException {
		BufferedImage image = robotMngr.createScreenCapture(x, y ,width, height);
		write(image, parentDirPath, fileNameWithoutSuffix);
	}
}
