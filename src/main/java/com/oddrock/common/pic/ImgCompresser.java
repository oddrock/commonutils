package com.oddrock.common.pic;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oddrock.common.file.FileUtils;

public class ImgCompresser {
	private final static Logger logger = LoggerFactory.getLogger(ImgCompresser.class);

	/**
	 * 压缩图片
	 * 
	 * @param srcImg
	 * @param quality
	 *            参数qality是取值0~1范围内 代表压缩的程度
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	public static void compressImg(File srcImg, File dstImg, float quality) throws IOException {
		if(!PicUtils.isImgFile(srcImg)) {
			logger.warn("输入不是图片文件，无法要锁");
			return;
		}
		
		BufferedImage src = null;
		FileOutputStream out = null;
		ImageWriter imgWrier;
		ImageWriteParam imgWriteParams;
		logger.info("开始设定压缩图片参数");
		// 指定写图片的方式为 jpg
		imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
		imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);
		// 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
		imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
		// 这里指定压缩的程度，参数qality是取值0~1范围内，
		imgWriteParams.setCompressionQuality(quality);
		imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
		ColorModel colorModel = ImageIO.read(srcImg).getColorModel();// ColorModel.getRGBdefault();
		imgWriteParams.setDestinationType(
				new javax.imageio.ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(32, 32)));
		logger.info("结束设定压缩图片参数");
		if (!srcImg.exists()) {
			logger.info("Not Found Img File,文件不存在");
			throw new FileNotFoundException("Not Found Img File,文件不存在");
		} else {
			logger.info("图片转换前大小" + srcImg.length() + "字节");
			src = ImageIO.read(srcImg);
			out = new FileOutputStream(dstImg);
			imgWrier.reset();
			// 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何
			// OutputStream构造
			imgWrier.setOutput(ImageIO.createImageOutputStream(out));
			// 调用write方法，就可以向输入流写图片
			imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);
			out.flush();
			out.close();
			logger.info("图片转换后大小" + srcImg.length() + "字节");
		}
	}
	
	public static void compressImg(File srcImg, String addStr, float quality) throws IOException {
		String dstImgPath = FileUtils.renameFileByAdd(srcImg.getCanonicalPath(), addStr);
		File dstImg = new File(dstImgPath);
		compressImg(srcImg, dstImg, quality);
	}
	
	public static void compressImg(File srcImg, String addStr) throws IOException {
		compressImg(srcImg, addStr, 0.618f);
	}
	
	public static void compressImg(File srcImg) throws IOException {
		compressImg(srcImg, "_compress");
	}
	
	public static void compressImg(String srcImgFilePath, String addStr) throws IOException {
		File srcImg = new File(srcImgFilePath);
		compressImg(srcImg, addStr, 0.1f);
	}
	
	public static void compressImg(String srcImgFilePath) throws IOException {
		compressImg(srcImgFilePath, "_compress");
	}
	
	public static void compressImgBatch(File srcDir, String addStr) throws IOException {
		if(!srcDir.exists()) {
			return;
		}
		if(!srcDir.isDirectory()) {
			return;
		}
		for(File file : srcDir.listFiles()) {
			if (file.exists() && file.isFile() && PicUtils.isImgFile(file)) {
				compressImg(file, addStr, 0.618f);
			}
		}
	}
	
	public static void compressImgBatch(File srcDir) throws IOException {
		compressImgBatch(srcDir, "_compress");
	}
	
	public static void compressImgBatch(String srcDirPath) throws IOException {
		compressImgBatch(new File(srcDirPath), "_compress");
	}
	
	
	
	public static void main(String[] args) throws IOException {
		//String srcDirPath = "C:\\Users\\oddro\\Desktop\\新建文件夹";
		String srcImgFilePath = "C:\\_XPS13_DocNotCloud\\个人资料\\04 淘宝\\IMG_20170909_093551.jpg";
		//File srcImg = new File("C:\\Users\\oddro\\Desktop\\新建文件夹\\变形金刚2_009.jpg");
		//compressImgBatch(srcDirPath);
		compressImg(srcImgFilePath);
	}
}