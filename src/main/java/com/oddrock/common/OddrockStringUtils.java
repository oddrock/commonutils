package com.oddrock.common;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OddrockStringUtils {
	public static String txtToHtml(String s) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                if (previousWasASpace) {
                    builder.append(" ");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }
            switch (c) {
                case '<':
                    builder.append("<");
                    break;
                case '>':
                    builder.append(">");
                    break;
                case '&':
                    builder.append("&");
                    break;
                case '"':
                    builder.append("\"");
                    break;
                case '\n':
                    builder.append("<br>");
                    break;
                case ' ':
                    builder.append("&nbsp;");
                    break;
                case '\t':
                    builder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    break;
                default:
                    builder.append(c);

            }
        }
        String converted = builder.toString();
        String str = "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>?«»“”‘’]))";
        Pattern patt = Pattern.compile(str);
        Matcher matcher = patt.matcher(converted);
        converted = matcher.replaceAll("<a href=\"$1\">$1</a>");
        return converted;
    }
	
	/**
	 * 去掉特殊字符
	 * @param string
	 * @return
	 */
	public static String deleteSpecCharacters(String string) {
		String regex = "[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\\\*:*\\[*\\]*@*\\.*%*\\(*\\)*\\s*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			sb.append(m.group());
	          
	        }
		return sb.toString();
	}
	
	/**
	 * 功能：判断一个字符串是否包含特殊字符
	 * 
	 */
	public static boolean containsSpecCharacters(String string) {
		String regex = "[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\\\*:*\\[*\\]*@*\\.*%*\\(*\\)*\\s*";
		if (string.replaceAll(regex, "").length() == 0) {
			// 如果不包含特殊字符
			return false;
		} else {
			return true;
		}

	}
	
	/**
	 * 判断是否是手机号
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		if(mobile==null) {
			return false;
		}
		mobile = mobile.trim();
		Pattern p = null;
		Matcher m = null;
		p = Pattern.compile("^[1][0-9]{10}$"); // 验证手机号
		m = p.matcher(mobile);
		return m.matches();
	}
	
	/**
	 * 遮罩掉URL里的参数
	 * @param url
	 * @param paramName
	 * @return
	 */
	public static String dataMaskUrlParam(String url, String paramName) {
		Pattern pattern = Pattern.compile(paramName+"=(.*?)&");
		Matcher matcher = pattern.matcher(url);
		if(matcher.find()) {
			return url.replaceAll(paramName+"=(.*?)&", paramName+"=***&");
		}else {
			pattern = Pattern.compile(paramName+"=(.*?)$");
			matcher = pattern.matcher(url);
			if(matcher.find()) {
				return url.replaceAll(paramName+"=(.*?)$", paramName+"=***");
			}
		}
		return url;
	}
	
	public static String dataMaskBodyParam(String body, String paramName) {
		Pattern pattern = Pattern.compile("\""+paramName+"\"\\s*:\\s*\".*?\"");		// 匹配"name":"value"这样的格式
		Matcher matcher = pattern.matcher(body);
		if(matcher.find()) {
			return body.replaceAll("\""+paramName+"\"\\s*:\\s*\".*?\"", "\""+paramName+"\":\"***\"");
		}else {
			pattern = Pattern.compile("\""+paramName+"\"\\s*:[^\\}\"]*?,");			// 匹配"name": value, 这样的格式
			matcher = pattern.matcher(body);
			if(matcher.find()) {
				return body.replaceAll("\""+paramName+"\"\\s*:[^\\}\"]*?,", "\""+paramName+"\":,");
			}else {
				pattern = Pattern.compile("\""+paramName+"\"\\s*:.*?}");			// 匹配"name": value} 这样的格式
				matcher = pattern.matcher(body);
				if(matcher.find()) {
					return body.replaceAll("\""+paramName+"\"\\s*:.*?}", "\""+paramName+"\":}");
				}
			}
		}
		return body;
	}
	
	/**
	 * 向字符串左边填充指定字符
	 * @param srcStr
	 * @param totalLength
	 * @param padStr
	 * @return
	 */
	public static String leftPad(String srcStr, int totalLength, char c) {
		String dstStr = srcStr;
		while(dstStr.length()<totalLength) {
			dstStr = c + dstStr;
		}
		return dstStr;
	}
	
	public static void main(String[] args) throws IOException {
		/*File file = new File("D:\\_caj2pdf\\cajwait");
		for(File f:file.listFiles()) {
			System.out.println(OddrockStringUtils.deleteSpecCharacters(f.getCanonicalPath()));
		}*/
		
		/*String url = "http://localhost:19001/bankapisrv/accountidentify?bankcard=6214809701290010328&name=%E5%BC%A0%E4%B8%89&idcard=34012319801008004&mobile=18012345678&appid=test-gK33ide7mPmd05Ma";
		Pattern pattern = Pattern.compile("name=(.*?)&");
		Matcher matcher = pattern.matcher(url);
		System.out.println(matcher.find());//必须要有这句
		System.out.println(matcher.group(0));
		System.out.println(url.replaceAll("name=(.*?)&", "name=xxxxxx&"));*/
		
		String body = "{\"data\":{\"accountType\":\"\",\"bankCard\":\"6214809701290010328\",\"bankName\":\"\",\"hasResult\":\"N\",\"idCard\":\"34012319801008004\",\"mobile\":\"18012345678\",\"name\":\"张三\", \"team\": 123, \"cach\": 123, \"cash\": 123 },\"resp\":{\"error_code\":150104,\"reason\":\"请求参数错误\"}}";
		/*Pattern pattern = Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\"");
		Matcher matcher = pattern.matcher(body);
		System.out.println(matcher.find());
		System.out.println(matcher.group(0));
		System.out.println(body.replaceAll("\"name\"\\s*:\\s*\"(.*?)\"", "\"name\":\"***\""));*/
		System.out.println(dataMaskBodyParam(body, "name"));
		System.out.println(dataMaskBodyParam(body, "team"));
		System.out.println(dataMaskBodyParam(body, "cash"));
	}
}
