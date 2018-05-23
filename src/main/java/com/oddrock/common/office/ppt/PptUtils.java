package com.oddrock.common.office.ppt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.xmlbeans.XmlException;

import com.oddrock.common.file.FileUtils;



public class PptUtils {
	private static Logger logger = Logger.getLogger(PptUtils.class);
	public static String parseTxtFromPpt(File file) throws IOException, XmlException, OpenXML4JException{
		String result = null;
		if(!FileUtils.fileExists(file))	return result;
		if(FileUtils.isSuffix(file, "pptx")){
			result = parseTxtFromPptx(file);
		}else if(FileUtils.isSuffix(file, "ppt")){
			result = parseTxtFromPpt2003(file);
		}
		return result;
	}
	/**
	 * 直接抽取幻灯片的全部内容，只支持后缀为ppt的文件
	 * @param file
	 * @return
	 * @throws IOException
	 */
    @SuppressWarnings("resource")
	public static String parseTxtFromPpt2003(File file) throws IOException{
    	FileInputStream is = new FileInputStream(file);
        PowerPointExtractor extractor=new PowerPointExtractor(is);
        return extractor.getText();
    }
    
    @SuppressWarnings("resource")
	public static String parseTxtFromPptx(File file) throws IOException, XmlException, OpenXML4JException {
        return new XSLFPowerPointExtractor(POIXMLDocument.openPackage(file.getCanonicalPath())).getText();   
   }
    
    public static void createTxtFileFromPpt(File file, File dir, boolean overwrite) throws IOException, XmlException, OpenXML4JException{
		if(!FileUtils.fileExists(file) || !FileUtils.dirExists(dir)){
			return;
		}
		File txtFile = new File(dir, file.getName()+".txt");
		if(!overwrite && txtFile.exists()) {
			return;
		}
		String txtContent = parseTxtFromPpt(file);
		if(StringUtils.isBlank(txtContent)){
			return;
		}
		logger.warn("开始将ppt内容写入："+txtFile.getName());
		FileUtils.writeToFile(txtFile.getCanonicalPath(), txtContent, false);
		logger.warn("结束将ppt内容写入："+txtFile.getName());
	}
	

	public static void createTxtFileFromPpt(File file, boolean overwrite) throws IOException, XmlException, OpenXML4JException{
		if(!FileUtils.fileExists(file)){
			return;
		}
		File dir = file.getParentFile();
		createTxtFileFromPpt(file, dir, overwrite);
	}
	

	public static void createTxtFileFromPpt(File file) throws IOException, XmlException, OpenXML4JException{
		createTxtFileFromPpt(file, true);
	}
	
	public static void main(String[] args) throws IOException, XmlException, OpenXML4JException {
		String filePath = "X:\\邮件\\qzfeng@ustcinfo.com\\2018年05月21日20时27分]-[冯强中]-[oddrock@qq.com]-[2018年中国联通智能运维创新应用新建工程天津单项工程作业计划巡检管理统一平台项目采购公开招标综合分析子平台\\对话聊天机器人解决方案汇报.ppt";
		createTxtFileFromPpt(new File(filePath));
		filePath = "X:\\邮件\\qzfeng@ustcinfo.com\\2018年05月21日20时27分]-[冯强中]-[oddrock@qq.com]-[2018年中国联通智能运维创新应用新建工程天津单项工程作业计划巡检管理统一平台项目采购公开招标综合分析子平台\\对话聊天机器人解决方案汇报.pptx";
		createTxtFileFromPpt(new File(filePath));
	}
}
