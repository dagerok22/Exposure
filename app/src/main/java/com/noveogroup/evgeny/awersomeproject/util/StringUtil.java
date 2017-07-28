package com.noveogroup.evgeny.awersomeproject.util;

import java.util.List;


public class StringUtil {
    public static String getTagsString(List<String> tagsList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String tag :
                tagsList) {
            stringBuilder.append("#")
                    .append(tag)
                    .append(" ");
        }
        return stringBuilder.toString();
    }
}
