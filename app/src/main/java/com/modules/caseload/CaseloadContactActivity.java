package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.postcard.CreateMailActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class CaseloadContactActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CaseloadContactActivity.class.getSimpleName();

    @BindView(R.id.imageview_profile_image)
    AppCompatImageView imageViewProfile;
    @BindView(R.id.imageview_contact_call)
    AppCompatImageView imageViewCall;
    @BindView(R.id.imageview_message)
    AppCompatImageView imageViewMessage;
    @BindView(R.id.textview_user_name)
    TextView textViewUserName;
    @BindView(R.id.textview_user_address)
    TextView textViewUserAddress;
    @BindView(R.id.linearlayout_contact)
    LinearLayout linearLayoutContact;
    @BindView(R.id.linearlayout_message)
    LinearLayout linearLayoutMessage;
    @BindView(R.id.textview_contact_number)
    TextView textViewContactNumber;
    private AlertDialog callDialog;
    Toolbar toolbar;
    private String name = "", address = "", profileImageUrl = "", contactNumber = "", userName = "";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_caseload_contact);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.caseload_contact_toolbar);
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

        int color = Color.parseColor("#757575"); //R.color.colorSecondary
        imageViewCall.setColorFilter(color);
        imageViewCall.setImageResource(R.drawable.vi_contact_call);

        imageViewMessage.setColorFilter(color);
        imageViewMessage.setImageResource(R.drawable.vi_message_black);

        linearLayoutContact.setOnClickListener(this);
        linearLayoutMessage.setOnClickListener(this);

        Intent data = getIntent();
        if (data != null) {
            if (data.hasExtra(General.NAME)) {
                name = data.getStringExtra(General.NAME);
            }

            if (data.hasExtra(General.USERNAME)) {
                userName = data.getStringExtra(General.USERNAME);
            }

            if (data.hasExtra(General.ADDRESS)) {
                address = data.getStringExtra(General.ADDRESS);
            }

            if (data.hasExtra(General.URL_IMAGE)) {
                profileImageUrl = data.getStringExtra(General.URL_IMAGE);
            }

            if (data.hasExtra(General.PHONE)) {
                contactNumber = data.getStringExtra(General.PHONE);
            }
        }

        Glide.with(getApplicationContext())
                .load(profileImageUrl)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(profileImageUrl))
                        .transform(new CircleTransform(getApplicationContext())))
                .into(imageViewProfile);

        if (name != null || address != null) {
            textViewUserName.setText(name.trim());
            textViewUserAddress.setText(address);
        }

        if (contactNumber != null) {

            if (contactNumber.length() < 3) {
                textViewContactNumber.setText("NA");
            } else {
                textViewContactNumber.setText(contactNumber);
                callDialog();
            }
        }

    }

    private void callDialog() {
        textViewContactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(CaseloadContactActivity.this).inflate(R.layout.call_popup, viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(CaseloadContactActivity.this);
                builder.setView(dialogView);

                callDialog = builder.create();

                final Button callBtn = dialogView.findViewById(R.id.call_btn);
                final Button cancelBtn = dialogView.findViewById(R.id.cancel_btn);
                final TextView mobileNoTxt = dialogView.findViewById(R.id.mobile_no);

                if (contactNumber.length() < 3) {
                    mobileNoTxt.setText("NA");
                } else {
                    mobileNoTxt.setText(contactNumber);
                }

                callBtn.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(View view) {
                        String phone = mobileNoTxt.getText().toString().trim();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                        callDialog.dismiss();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callDialog.dismiss();
                    }
                });

                callDialog.show();
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearlayout_contact:
                break;

            case R.id.linearlayout_message:
                /*code commented after discussed with sunil here for all instances we are sending username in intent
                 * commented and changed by rahulmsk*/
                Intent intent = new Intent(getApplicationContext(), CreateMailActivity.class);
                 /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                    intent.putExtra(General.NAME, userName);
                } else {
                    intent.putExtra(General.NAME, name);
                }*/
                intent.putExtra(General.NAME, userName);
                startActivity(intent);
                startActivity(intent);
                break;
        }
    }

}
