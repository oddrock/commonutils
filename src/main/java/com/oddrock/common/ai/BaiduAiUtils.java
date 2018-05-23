package com.oddrock.common.ai;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONObject;

import com.baidu.aip.ocr.AipOcr;
import com.oddrock.common.CommonProp;

public class BaiduAiUtils {
	public static final String APP_ID = CommonProp.get("baidu.ai.appid");
    public static final String API_KEY = CommonProp.get("baidu.ai.appkey");
    public static final String SECRET_KEY = CommonProp.get("baidu.ai.secretkey");
    
    public static String ocr(File file) throws IOException{
    	// 初始化一个OcrClient
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用通用识别接口
        JSONObject genRes = client.basicGeneral(file.getCanonicalPath(), new HashMap<String, String>());
        System.out.println(genRes.toString(2));
        return null;
    }
    
	public static void main(String[] args) throws IOException {
		String genFilePath = "C:\\Users\\oddro\\Desktop\\PastedGraphic-7.jpg";
		ocr(new File(genFilePath));
	}

}
