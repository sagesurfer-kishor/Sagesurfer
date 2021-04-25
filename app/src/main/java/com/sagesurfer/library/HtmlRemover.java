package com.sagesurfer.library;

import android.text.Html;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 25-07-2017
 *         Last Modified on 15-12-2017
 */

public class HtmlRemover {

    @SuppressWarnings("deprecation")
    public static String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return String.valueOf(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            return String.valueOf(Html.fromHtml(html));
        }
    }

    public static String clearHtml(String sentence) {
        sentence = Html.fromHtml(sentence).toString();
        return sentence;
    }
}
