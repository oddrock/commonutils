package com.oddrock.common.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import com.oddrock.common.file.FileUtils;

public class WordUtils {	
	/**
	 * 从word文档中解析出文字
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws XmlException
	 * @throws OpenXML4JException
	 */
	public static String parseTxtFromWord(File file) throws IOException, XmlException, OpenXML4JException{
		String content = null;
		if(file==null || !file.exists() || !file.isFile()){
			return content;
		}
		if(FileUtils.isSuffix(file, "docx")){
			content = parseTxtFromDocx(file);
		}else if(FileUtils.isSuffix(file, "doc")){
			content = parseTxtFromDoc(file);
		}
		return content;
	}
	
	/**
	 * 从doc文档中解析出文字
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws XmlException
	 * @throws OpenXML4JException
	 */
	public static String parseTxtFromDocx(File file) throws IOException, XmlException, OpenXML4JException{
		OPCPackage opcPackage = POIXMLDocument.openPackage(file.getCanonicalPath());
		POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
		String content = extractor.getText();
		extractor.close();
		return content;
	}
	
	/**
	 * 从docx文档中解析出文字
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws XmlException
	 * @throws OpenXML4JException
	 */
	public static String parseTxtFromDoc(File file) throws IOException, XmlException, OpenXML4JException{
		String result = null;  
        FileInputStream fis = null;  
        try {  
            fis = new FileInputStream(file);  
            @SuppressWarnings("resource")  
            WordExtractor wordExtractor = new WordExtractor(fis);  
            result = wordExtractor.getText();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (fis != null) {  
                try {  
                    fis.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return result;  
	}
	
	/**
	 * 根据word文档，将内容写入一个txt文件
	 * @param wordFile
	 * @param dir
	 * @param overwrite			是否覆盖同名文件
	 * @throws IOException
	 * @throws XmlException
	 * @throws OpenXML4JException
	 */
	public static void createTxtFileFromWord(File wordFile, File dir, boolean overwrite) throws IOException, XmlException, OpenXML4JException{
		if(wordFile==null || !wordFile.exists() || !wordFile.isFile()){
			return;
		}
		if(dir==null || !dir.exists() || !dir.isDirectory()){
			return;
		}
		File txtFile = new File(dir, wordFile.getName()+".txt");
		if(!overwrite && txtFile.exists()) {
			return;
		}
		String txtContent = parseTxtFromWord(wordFile);
		if(txtContent==null){
			return;
		}
		FileUtils.writeToFile(txtFile.getCanonicalPath(), txtContent, false);
	}
	
	/**
	 * 根据word文档，将内容写入一个txt文件，txt文件名为word文件名加.txt后缀
	 * @param wordFile
	 * @param overwrite
	 * @throws IOException
	 * @throws XmlException
	 * @throws OpenXML4JException
	 */
	public static void createTxtFileFromWord(File wordFile, boolean overwrite) throws IOException, XmlException, OpenXML4JException{
		if(!FileUtils.fileExists(wordFile)){
			return;
		}
		File dir = wordFile.getParentFile();
		createTxtFileFromWord(wordFile, dir, overwrite);
	}
	
	/**
	 * 根据word文档，将内容写入一个txt文件，txt文件名为word文件名加.txt后缀，如果文件存在，则覆盖
	 * @param wordFile
	 * @throws IOException
	 * @throws XmlException
	 * @throws OpenXML4JException
	 */
	public static void createTxtFileFromWord(File wordFile) throws IOException, XmlException, OpenXML4JException{
		createTxtFileFromWord(wordFile, true);
	}
	
	public static void main(String[] args) throws IOException, XmlException, OpenXML4JException {
		String filePath = "X:\\邮件\\qzfeng@ustcinfo.com\\2018年05月21日20时27分]-[冯强中]-[oddrock@qq.com]-[2018年中国联通智能运维创新应用新建工程天津单项工程作业计划巡检管理统一平台项目采购公开招标综合分析子平台\\2018年中国联通智能运维创新应用新建工程天津单项工程作业计划巡检管理统一平台项目采购公开招标综合分析子平台.docx";
		createTxtFileFromWord(new File(filePath));
	}

}
