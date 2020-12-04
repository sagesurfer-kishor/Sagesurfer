package com.modules.support;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.secure.KeyMaker_;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/3/2018
 * Last Modified on 4/3/2018
 */

public class AboutUsActivity extends AppCompatActivity {

    private static final String TAG = AboutUsActivity.class.getSimpleName();

    private WebView mWebView;
    Toolbar toolbar;
    private String aboutus_url;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_about_us);

        String aboutus_url = Preferences.get(General.URL_ABOUTS);


        initMethod();
    }

    @SuppressLint("RestrictedApi")
    private void initMethod() {

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
//        setSearchToolbar();
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mWebView = findViewById(R.id.aboutus_web);

        TextView textViewActivityToolbarTitle = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        textViewActivityToolbarTitle.setText(this.getResources().getString(R.string.about_us));

        HashMap<String, String> keyMap = KeyMaker_.getKey();

        aboutus_url = Preferences.get(General.URL_ABOUTS);
        mWebView.loadUrl(aboutus_url + "?" + "access_token=" + keyMap.get(General.TOKEN));
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

}
