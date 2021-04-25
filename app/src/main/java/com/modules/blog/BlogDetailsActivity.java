package com.modules.blog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 21-09-2017
 *         Last Modified on 13-12-2017
 */

public class BlogDetailsActivity extends AppCompatActivity {

    private static final String TAG = BlogDetailsActivity.class.getSimpleName();

    private Blog_ blog_;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.blog_details_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText("");

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.GONE);

        Intent data = getIntent();
        if (data.hasExtra(Actions_.GET_BLOG_NEW)) {
            blog_ = (Blog_) data.getSerializableExtra(Actions_.GET_BLOG_NEW);
            setData();
        } else {
            onBackPressed();
        }
    }

    // Set data to respective fields and show blog in details to user
    // Use blog id to show blog details from web page url
    @SuppressLint("SetJavaScriptEnabled")
    private void setData() {
        TextView titleText = (TextView) findViewById(R.id.blog_details_title);
        TextView dateText = (TextView) findViewById(R.id.blog_details_time);
        TextView nameText = (TextView) findViewById(R.id.blog_details_name);
        ImageView profileImage = (ImageView) findViewById(R.id.blog_details_image);
        WebView webView = (WebView) findViewById(R.id.blog_details_web_view);

        titleText.setText(blog_.getTitle());
        nameText.setText(ChangeCase.toTitleCase(blog_.getName()));
        dateText.setText(GetTime.getTodayMm(blog_.getDate()));

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(getApplicationContext()).load(blog_.getPhoto())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(blog_.getPhoto()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(getApplicationContext())))
                .into(profileImage);

        WebSettings webSettings = webView.getSettings();
        Resources res = getResources();
        float fontSize = res.getDimension(R.dimen.blog_font);

        // Use blog id and show blog details using temporary url
        String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "")
                + "/mobile_blog_display.php?id=" + blog_.getId();
        Logger.debug(TAG, "Blog details url: " + url, getApplicationContext());
        webSettings.setDefaultFontSize((int) fontSize);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBuiltInZoomControls(true);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0,0);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
