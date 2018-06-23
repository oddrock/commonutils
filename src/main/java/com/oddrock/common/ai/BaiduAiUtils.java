package com.oddrock.common.ai;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.aip.ocr.AipOcr;
import com.oddrock.common.CommonProp;
import com.oddrock.common.file.FileUtils;

public class BaiduAiUtils {
	private static Logger logger = Logger.getLogger(BaiduAiUtils.class);
	
	public static final String APP_ID = CommonProp.get("baidu.ai.appid");
    public static final String API_KEY = CommonProp.get("baidu.ai.appkey");
    public static final String SECRET_KEY = CommonProp.get("baidu.ai.secretkey");
    
    /**
     * 将图片中文字识别出来
     * @param file
     * @return
     * @throws IOException
     */
    public static String ocr(File file) throws IOException{
    	int maxsize = CommonProp.getInt("baidu.ai.ocr.filemaxsize");
    	if(file.length()>maxsize){
    		logger.warn("文件大小为"+file.length()+"字节，超过了限制大小"+maxsize+"字节");
    		return null;
    	}
    	String suffix = FileUtils.getFileNameSuffix(file);
    	if(!suffix.equalsIgnoreCase("jpg") && !suffix.equalsIgnoreCase("jpeg") 
    			&& !suffix.equalsIgnoreCase("png") && !suffix.equalsIgnoreCase("bmp") ){
    		logger.warn("文件后缀名不对，可支持PNG、JPG、JPEG、BMP");
    		return null;
    	}
    	// 初始化一个OcrClient
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 调用通用识别接口
        JSONObject genRes = client.basicGeneral(file.getCanonicalPath(), new HashMap<String, String>());
        JSONArray words_result = null;
        try{
        	words_result = genRes.getJSONArray("words_result");
        }catch(Exception e){
        	return "";
        }
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < words_result.length(); i++) {
        	JSONObject obj = (JSONObject)words_result.get(i);
        	sb.append(obj.get("words")).append("\n");
        }
        return sb.toString();
    }
    
	public static void main(String[] args) throws IOException {
		String genFilePath = "C:\\Users\\oddro\\Desktop\\PastedGraphic-7.jpg";
		System.out.println(new File(genFilePath).length());
		System.out.println(ocr(new File(genFilePath)));
	}

}
