package com.modules.consent;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.AttachmentViewer;
import com.sagesurfer.views.ShowWebView;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileOperations;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 19-09-2017
 * Last Modified on 13-12-2017
 **/


public class ConsentDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ConsentDetailsActivity.class.getSimpleName();
    private String url = "";

    private TextView fileText, sizeText, senderText, consumerText, dateText;
    private ProgressBar progressBar;
    private ImageView consentImage, fullScreenButton;
    private WebView webView;

    private ConsentFile_ consentFile_;
    Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.consent_details_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Monika- Added toolbar to avoid download/fullscreen icon overlap to doc's icon
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
        RelativeLayout relativeLayoutToolbarConsentDetails = (RelativeLayout) findViewById(R.id.relativelayout_toolbar_consent_details);
        relativeLayoutToolbarConsentDetails.setVisibility(View.VISIBLE);

        fileText = (TextView) findViewById(R.id.consent_details_file_name);
        sizeText = (TextView) findViewById(R.id.consent_details_file_size);
        senderText = (TextView) findViewById(R.id.consent_details_file_sender);
        consumerText = (TextView) findViewById(R.id.consent_details_file_consumer);
        dateText = (TextView) findViewById(R.id.consent_details_file_date);
        consentImage = (ImageView) findViewById(R.id.consent_details_image);
        fullScreenButton = (ImageView) findViewById(R.id.consent_details_full_screen);
        fullScreenButton.setOnClickListener(this);
        ImageView downloadButton = (ImageView) findViewById(R.id.consent_details_download);
        downloadButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.consent_details_progress);
        progressBar.setVisibility(View.VISIBLE);
        webView = (WebView) findViewById(R.id.consent_details_web_view);

        Intent data = getIntent();
        if (data != null && data.hasExtra(Actions_.GET_SHARED_FILES)) {
            consentFile_ = (ConsentFile_) data.getSerializableExtra(Actions_.GET_SHARED_FILES);
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        url = ConsentOperation.getUrl(Actions_.GET_FILE_URL, "" + consentFile_.getId(), this);
        if (url != null && url.trim().length() > 0) {
            setData(url);
        } else {
            onBackPressed();
        }
    }

    // Show consent details data in respective fields
    // If it's image file then shows image or it shows google document viewer to show file
    private void setData(String url) {
        if (consentFile_ == null) {
            onBackPressed();
        }
        fileText.setText(consentFile_.getRealname());
        senderText.setText(consentFile_.getAuthor_name());
        consumerText.setText(consentFile_.getYouth_name());
        dateText.setText(GetTime.getTodayEeeMm(consentFile_.getShared_date()));
        sizeText.setText(FileOperations.bytes2String(consentFile_.getSize()));

        if (CheckFileType.imageFile(consentFile_.getRealname())) {
            webView.setVisibility(View.GONE);
            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(getApplicationContext())
                    .load(url)
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(url))
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable final GlideException e, final Object model,
                                                    final Target<Drawable> target, final boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Drawable resource, final Object model,
                                                       final Target<Drawable> target, final DataSource dataSource,
                                                       final boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(consentImage);
        } else {
            progressBar.setVisibility(View.GONE);
            fullScreenButton.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            String doc = "<html>\n" +
                    "<body>\n" +
                    "\n" +
                    "<iframe src=\"https://docs.google.com/gview?url=" + url
                    + "&amp;embedded=true\" style=\"border:none;\"\n" +
                    "width = 100% height = 100%></iframe>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>";
            ShowWebView.loadData(webView, doc);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0,0);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.consent_details_download:
                // Download file using global download class
                DownloadFile downloadFile = new DownloadFile();
                downloadFile.download(consentFile_.getId(), url, consentFile_.getRealname(), DirectoryList.DIR_CONSENT_FILES, this);
                break;

            case R.id.consent_details_full_screen:
                DialogFragment dialogFrag = new AttachmentViewer();
                Bundle bundle = new Bundle();
                bundle.putString(General.ID, "" + consentFile_.getId());
                bundle.putString(General.DIRECTORY, DirectoryList.DIR_CONSENT_FILES);
                bundle.putString(General.PATH, url);
                bundle.putString(General.IMAGE, consentFile_.getRealname());
                bundle.putString(General.SIZE, "Image");
                dialogFrag.setArguments(bundle);
                dialogFrag.show(getFragmentManager().beginTransaction(), General.URL_IMAGE);
                break;
        }
    }
}
