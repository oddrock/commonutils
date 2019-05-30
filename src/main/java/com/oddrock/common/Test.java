package com.oddrock.common;

import java.io.IOException;

import com.oddrock.common.pic.PicUtils;

public class Test {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//PicUtils.shrinkImg("C:\\Users\\oddro\\Desktop\\智检查照片\\image-00c08fcb-68f5-413e-95b0-f5e2a1bb0996.jpg", "C:\\_Temp",0.25);
		PicUtils.shrinkImgBatch("C:\\Users\\oddro\\Desktop\\智检查照片", "C:\\_Temp",0.25);
		//Thumbnails.of("C:\\_XPS13_Doc\\Picture\\壁纸\\风景\\e7ed21c28324d513e4dd3b4e.jpg").scale(0.25f).toFile("C:\\_Temp\\image_25%.jpg");
	}
}