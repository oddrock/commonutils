package com.oddrock.common;

import java.io.File;
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
	
	public static void main(String[] args) throws IOException {
		File file = new File("D:\\_caj2pdf\\cajwait");
		for(File f:file.listFiles()) {
			System.out.println(OddrockStringUtils.deleteSpecCharacters(f.getCanonicalPath()));
		}
	}
}
