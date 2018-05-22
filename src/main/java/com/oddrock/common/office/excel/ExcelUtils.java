package com.oddrock.common.office.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlException;

import com.oddrock.common.file.FileUtils;

public class ExcelUtils {
	public static String parseTxtFromExcel(File file) throws IOException, XmlException, OpenXML4JException{
		String content = null;
		if(file==null || !file.exists() || !file.isFile()){
			return content;
		}
		if(FileUtils.isSuffix(file, "xlsx")){
			content = parseTxtFromXlsx(file);
		}else if(FileUtils.isSuffix(file, "xls")){
			content = parseTxtFromXls(file);
		}
		return content;
	}
 
    @SuppressWarnings("deprecation")
	public static String parseTxtFromXls(File file) {  
        StringBuffer buff = new StringBuffer();  
        try {  
            // 创建对Excel工作簿文件的引用  
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));  
            // 创建对工作表的引用。  
            for (int numSheets = 0; numSheets < wb  
                    .getNumberOfSheets(); numSheets++) {  
                if (null != wb.getSheetAt(numSheets)) {  
                    HSSFSheet aSheet = wb.getSheetAt(numSheets);// 获得一个sheet  
                    for (int rowNumOfSheet = 0; rowNumOfSheet <= aSheet  
                            .getLastRowNum(); rowNumOfSheet++) {  
                        if (null != aSheet.getRow(rowNumOfSheet)) {  
                            HSSFRow aRow = aSheet.getRow(rowNumOfSheet); // 获得一个行  
                            for (int cellNumOfRow = 0; cellNumOfRow <= aRow  
                                    .getLastCellNum(); cellNumOfRow++) {  
                                if (null != aRow.getCell(cellNumOfRow)) {  
                                    HSSFCell aCell = aRow.getCell(cellNumOfRow);// 获得列值  
                                    switch (aCell.getCellType()) {  
                                        case HSSFCell.CELL_TYPE_FORMULA :  
                                            break;  
                                        case HSSFCell.CELL_TYPE_NUMERIC :  
                                            buff.append(  
                                                    aCell.getNumericCellValue())  
                                                    .append('\t');  
                                            break;  
									case HSSFCell.CELL_TYPE_STRING :  
                                            buff.append(  
                                                    aCell.getStringCellValue())  
                                                    .append('\t');  
                                            break;  
                                    }  
                                }  
                            }  
                            buff.append('\n');  
                        }  
                    }  
                }  
            }  
            wb.close();
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        return buff.toString();  
    }  

    @SuppressWarnings("deprecation")
	public static String parseTxtFromXlsx(File file) {  
        StringBuffer buff = new StringBuffer();  
        try {  
            // 创建对Excel工作簿文件的引用  
            @SuppressWarnings("resource")  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));  
            // 创建对工作表的引用。  
            for (int numSheets = 0; numSheets < wb  
                    .getNumberOfSheets(); numSheets++) {  
                if (null != wb.getSheetAt(numSheets)) {  
                    XSSFSheet aSheet = wb.getSheetAt(numSheets);// 获得一个sheet  
                    for (int rowNumOfSheet = 0; rowNumOfSheet <= aSheet  
                            .getLastRowNum(); rowNumOfSheet++) {  
                        if (null != aSheet.getRow(rowNumOfSheet)) {  
                            XSSFRow aRow = aSheet.getRow(rowNumOfSheet); // 获得一个行  
                            for (int cellNumOfRow = 0; cellNumOfRow <= aRow  
                                    .getLastCellNum(); cellNumOfRow++) {  
                                if (null != aRow.getCell(cellNumOfRow)) {  
                                    XSSFCell aCell = aRow.getCell(cellNumOfRow);// 获得列值  
                                    switch (aCell.getCellType()) {  
                                        case HSSFCell.CELL_TYPE_FORMULA :  
                                            break;  
                                        case HSSFCell.CELL_TYPE_NUMERIC :  
                                            buff.append(  
                                                    aCell.getNumericCellValue())  
                                                    .append('\t');  
                                            break;  
                                        case HSSFCell.CELL_TYPE_STRING :  
                                            buff.append(  
                                                    aCell.getStringCellValue())  
                                                    .append('\t');  
                                            break;  
                                    }  
                                }  
                            }  
                            buff.append('\n');  
                        }  
                    }  
                }  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buff.toString();  
    }  
    
    public static void createTxtFileFromExcel(File excelFile, File dir, boolean overwrite) throws IOException, XmlException, OpenXML4JException{
		if(excelFile==null || !excelFile.exists() || !excelFile.isFile()){
			return;
		}
		if(dir==null || !dir.exists() || !dir.isDirectory()){
			return;
		}
		
		File txtFile = new File(dir, excelFile.getName()+".txt");
		if(!overwrite && txtFile.exists()) {
			return;
		}
		String txtContent = parseTxtFromExcel(excelFile);
		if(StringUtils.isBlank(txtContent)){
			return;
		}
		FileUtils.writeToFile(txtFile.getCanonicalPath(), txtContent, false);
	}
    
    public static void createTxtFileFromExcel(File excelFile, boolean overwrite) throws IOException, XmlException, OpenXML4JException{
		if(!FileUtils.fileExists(excelFile)){
			return;
		}
		File dir = excelFile.getParentFile();
		createTxtFileFromExcel(excelFile, dir, overwrite);
	}
    
    public static void createTxtFileFromExcel(File excelFile) throws IOException, XmlException, OpenXML4JException{
    	createTxtFileFromExcel(excelFile, true);
	}
    
	public static void main(String[] args) throws IOException, XmlException, OpenXML4JException {
		String filePath = "X:\\邮件\\qzfeng@ustcinfo.com\\2018年05月22日07时40分]-[li.lingyue@ustcinfo.com]-[li.lingyue@ustcinfo.com]-[运营商招标中标信息统计报表\\5月份请假.xlsx";
		createTxtFileFromExcel(new File(filePath));
	}

}
