package com.sagesurfer.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 20-09-2017
 * Last Modified on 15-12-2017
 */

public class Care {

    public static boolean isUrl(String url) {
        String urlPattern = "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/&?=\\-.~%]*";
        Pattern pattern_ = Pattern.compile(urlPattern);
        Matcher matcher = pattern_.matcher(url);
        return !(url.length() <= 2 && url.length() > 2000) && matcher.matches();
    }
}
