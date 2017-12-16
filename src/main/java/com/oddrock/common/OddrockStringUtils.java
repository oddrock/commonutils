package com.oddrock.common;

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
}
