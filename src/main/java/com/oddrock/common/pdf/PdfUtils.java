package com.oddrock.common.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

import com.oddrock.common.file.FileUtils;

public class PdfUtils {
	public static String parseTxtFromPdf(File file) {  
		String result = null;  
		if(!FileUtils.fileExists(file) || !FileUtils.isSuffix(file, "pdf")){
			return result;
		}
		RandomAccessBufferedFileInputStream is = null;  
	    PDDocument document = null;  
	    try {  
	        is = new RandomAccessBufferedFileInputStream(file);  
	        PDFParser parser = new PDFParser(is);  
	        parser.parse();  
	        document = parser.getPDDocument();  
	        PDFTextStripper stripper = new PDFTextStripper();  
	        result = stripper.getText(document);  
	    } catch (FileNotFoundException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (is != null) {  
	            try {  
	                is.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	        if (document != null) {  
	            try {  
	                document.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }  
	    return result;  
	}  
	
	public static void createTxtFileFromPdf(File file, File dir, boolean overwrite) throws IOException, XmlException, OpenXML4JException{
		if(!FileUtils.fileExists(file) || !FileUtils.dirExists(dir)){
			return;
		}
		File txtFile = new File(dir, file.getName()+".txt");
		if(!overwrite && txtFile.exists()) {
			return;
		}
		String txtContent = parseTxtFromPdf(file);
		if(StringUtils.isBlank(txtContent)){
			return;
		}
		FileUtils.writeToFile(txtFile.getCanonicalPath(), txtContent, false);
	}
	

	public static void createTxtFileFromPdf(File file, boolean overwrite) throws IOException, XmlException, OpenXML4JException{
		if(!FileUtils.fileExists(file)){
			return;
		}
		File dir = file.getParentFile();
		createTxtFileFromPdf(file, dir, overwrite);
	}
	

	public static void createTxtFileFromPdf(File file) throws IOException, XmlException, OpenXML4JException{
		createTxtFileFromPdf(file, true);
	}
	public static void main(String[] args) throws IOException, XmlException, OpenXML4JException {
		String filePath = "X:\\邮件\\qzfeng@ustcinfo.com\\2018年05月21日20时27分]-[冯强中]-[oddrock@qq.com]-[2018年中国联通智能运维创新应用新建工程天津单项工程作业计划巡检管理统一平台项目采购公开招标综合分析子平台\\企业IT架构转型之道  阿里巴巴中台战略思想与架构实战_钟华.pdf";
		createTxtFileFromPdf(new File(filePath));
		filePath = "X:\\邮件\\qzfeng@ustcinfo.com\\2018年05月21日20时27分]-[冯强中]-[oddrock@qq.com]-[2018年中国联通智能运维创新应用新建工程天津单项工程作业计划巡检管理统一平台项目采购公开招标综合分析子平台\\010_CTGMBOSS OSS 2.5_集成及技术分册(终审稿).pdf";
		createTxtFileFromPdf(new File(filePath));
	}

}
