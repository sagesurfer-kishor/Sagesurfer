package com.sagesurfer.views;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 04-10-2016
 * Last Modified on 14-09-2017
 */

/*
 * This file load url in web view with necessary setting in given web view
 */

public class ShowWebView {

    @SuppressLint("SetJavaScriptEnabled")
    public static void loadData(WebView webView, String doc) {
        WebSettings webViewSettings = webView.getSettings();
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setBuiltInZoomControls(false);
        webViewSettings.setPluginState(WebSettings.PluginState.ON);
        webView.loadData(doc, "text/html; video/mpeg", "UTF-8");

    }
}
