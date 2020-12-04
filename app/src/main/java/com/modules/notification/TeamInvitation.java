package com.modules.notification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.Invitations_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 6/13/2019.
 */
public class TeamInvitation extends AppCompatActivity {
    private static final String TAG = TeamInvitation.class.getSimpleName();
    private Toolbar toolbar;
    private ArrayList<Invitations_> invitationsArrayList;
    private Button declineBtn, acceptBtn;
    private String send_by = "0", group_name = "0", profile_img = "0";
    private Long time_stamp, ref_id, group_id;
    private int added_by_id;
    private TextView userName, addedByName, groupName, dateTime;
    private ImageView profileImg;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_team_invitation);

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

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(100, 0, 0, 0);
        titleText.setText(this.getResources().getString(R.string.team_invitation));

        Intent data = getIntent();
        if (data != null) {
            send_by = data.getStringExtra(General.TEAM_ID);
            group_name = data.getStringExtra("group_name");
            time_stamp = data.getLongExtra("time_stamp", 0);
            profile_img = data.getStringExtra("profile_img");
            added_by_id = data.getIntExtra("added_by_id", 0);
            ref_id = data.getLongExtra("ref_id", 0);
            group_id = data.getLongExtra("group_id", 0);
        }

        initUi();
        setClickListeners();

    }

    private void initUi() {
        declineBtn = findViewById(R.id.decline_btn);
        acceptBtn = findViewById(R.id.accept_btn);

        userName = findViewById(R.id.user_name);
        addedByName = findViewById(R.id.added_name);
        groupName = findViewById(R.id.group_name);
        dateTime = findViewById(R.id.date_time);
        profileImg = findViewById(R.id.profile_img);

        userName.setText("Hi, " + Preferences.get(General.FIRST_NAME)+" "+Preferences.get(General.LAST_NAME));
        addedByName.setText(send_by);
        groupName.setText(group_name);
        dateTime.setText(getDate(time_stamp));

        Glide.with(this)
                .load(profile_img)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(profile_img))
                        .transform(new CircleTransform(this)))
                .into(profileImg);


    }

    private void setClickListeners() {
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptInvitation();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineInvitation();
            }
        });
    }

    private void acceptInvitation() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ACCEPT);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put("send_by", String.valueOf(added_by_id));
        requestMap.put("ref_id", String.valueOf(ref_id));
        requestMap.put("group_id", String.valueOf(group_id));
        Log.e("requestMap",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_INVITATION_OPERATION;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("response",response);
                if (response != null) {
                    invitationsArrayList = Alerts_.friendAccept(response, this, TAG);
                    if (invitationsArrayList.size() > 0) {
                        if (invitationsArrayList.get(0).getStatus() == 1) {
                            ShowToast.toast(invitationsArrayList.get(0).getMsg(), this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000);

                        } else {
                            ShowToast.toast(invitationsArrayList.get(0).getMsg(), this);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void declineInvitation() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DECLINE);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put("send_by", String.valueOf(added_by_id));
        requestMap.put("ref_id", String.valueOf(ref_id));
        requestMap.put("group_id", String.valueOf(group_id));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_INVITATION_OPERATION;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    invitationsArrayList = Alerts_.friendDecline(response, this, TAG);
                    if (invitationsArrayList.size() > 0) {
                        if (invitationsArrayList.get(0).getStatus() == 1) {
                            ShowToast.toast(invitationsArrayList.get(0).getMsg(), getApplicationContext());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000);

                        } else {
                            ShowToast.toast(invitationsArrayList.get(0).getMsg(), getApplicationContext());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

}
