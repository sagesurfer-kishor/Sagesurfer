package com.sagesurfer.collaborativecares;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Server_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    @BindView(R.id.textview_activitytoolbar_title)
    TextView textViewActivityToolbarTitle;
    @BindView(R.id.relativelayout_forgot_password_support)
    RelativeLayout relativeLayoutForgotPasswordSupport;
    @BindView(R.id.relativelayout_forgot_password_username)
    RelativeLayout relativeLayoutForgotPasswordUsername;
    @BindView(R.id.relativelayout_forgot_password_passcode)
    RelativeLayout relativeLayoutForgotPasswordPasscode;

    private ArrayList<Server_> instanceList;

    public static ForgotPasswordActivity forgotPasswordActivity;

    Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ButterKnife.bind(this);
        Preferences.initialize(ForgotPasswordActivity.this);
        Preferences.save(General.HOME_ICON_NUMBER, "0");

        forgotPasswordActivity = this;
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

        textViewActivityToolbarTitle.setText(this.getResources().getString(R.string.forgot_password));

        relativeLayoutForgotPasswordSupport.setOnClickListener(this);
        relativeLayoutForgotPasswordUsername.setOnClickListener(this);
        relativeLayoutForgotPasswordPasscode.setOnClickListener(this);

        Intent data = getIntent();
        if (data.hasExtra(General.INSTANCE_KEY)) {
            instanceList = (ArrayList<Server_>) data.getSerializableExtra(General.INSTANCE_KEY);
        }
        if (instanceList == null || instanceList.size() <= 0) {
            onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativelayout_forgot_password_support:
                registerFragment(this.getResources().getString(R.string.contact_to_support_team));
                break;
            case R.id.relativelayout_forgot_password_passcode:
                registerFragment(this.getResources().getString(R.string.forgot_password));
                break;
            case R.id.relativelayout_forgot_password_username:
                registerFragment(this.getResources().getString(R.string.forgot_username));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        this.overridePendingTransition(0,0);
    }

    @SuppressWarnings("static")
    private void registerFragment(String tag) {
        ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
        Bundle data = new Bundle();
        data.putString(General.TITLE, tag);

        data.putSerializable(General.INSTANCE_KEY, instanceList);
        forgotPasswordFragment.setArguments(data);

        Fragment f = getFragmentManager().findFragmentByTag(tag);
        if (f != null) {
            getFragmentManager().popBackStack();
        } else {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.framelayout_forgot_password, forgotPasswordFragment).addToBackStack(null).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}