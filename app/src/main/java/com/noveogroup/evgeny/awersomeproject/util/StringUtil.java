package com.noveogroup.evgeny.awersomeproject.util;

import com.noveogroup.evgeny.awersomeproject.db.model.Task;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sergey on 28.07.17.
 */

public class StringUtil {
    public static String getTagsString(List<String> tagsList){
        List<String> tags = Arrays.asList(tagsList.get(0).replaceAll("\\s+", "").split(","));
        StringBuilder stringBuilder = new StringBuilder();
        for (String tag :
                tags) {
            stringBuilder.append("#")
                    .append(tag)
                    .append(" ");
        }
        return stringBuilder.toString();
    }
}
