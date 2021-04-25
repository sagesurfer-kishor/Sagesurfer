package com.modules.settings;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.storage.preferences.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Kailash Karankal
 */

public class TermsAndConditionActivity extends AppCompatActivity {

    Toolbar toolbar;
    @BindView(R.id.textview_terms_of_use_agreement)
    TextView textViewTermsOfUseAgreement;

    String terms_conditions_url;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_terms_and_condition);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView textViewActivityToolbarTitle = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        textViewActivityToolbarTitle.setText(this.getResources().getString(R.string.terms_and_conditions));

        WebView web = (WebView) this.findViewById(R.id.webview);

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage013))) {
            textViewTermsOfUseAgreement.setText(getResources().getString(R.string.hope_terms_of_use_agreement));
            terms_conditions_url = Preferences.get(General.TERMS_URL);
            Log.e("url", terms_conditions_url);

            web.loadUrl(terms_conditions_url);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
            textViewTermsOfUseAgreement.setText(getResources().getString(R.string.cspnj_terms_of_use_agreement));
            terms_conditions_url = Preferences.get(General.TERMS_URL);
            Log.e("url", terms_conditions_url);

            web.loadUrl(terms_conditions_url);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
            textViewTermsOfUseAgreement.setVisibility(View.GONE);
            terms_conditions_url = Preferences.get(General.TERMS_URL);
            Log.e("url", terms_conditions_url);

            web.loadUrl(terms_conditions_url);
        } else {
            textViewTermsOfUseAgreement.setText(getResources().getString(R.string.terms_of_use_agreement));
            terms_conditions_url = Preferences.get(General.TERMS_URL);
            Log.e("url", terms_conditions_url);
            web.loadUrl(terms_conditions_url);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
