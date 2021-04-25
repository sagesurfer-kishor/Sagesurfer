package com.modules.sos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Responses_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MoodParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * Created by Monika on 4/25/2019.
 */

public class SosSettingActivity extends AppCompatActivity {
    private static final String TAG = SosSettingActivity.class.getSimpleName();

    @BindView(R.id.imageview_current_location)
    ImageView imageViewCurrentLocation;
    @BindView(R.id.textview_current_location)
    TextView textViewCurrentLocation;
    @BindView(R.id.switch_current_location)
    Switch switchCurrentLocation;
    @BindView(R.id.relativelayout_profile_address)
    RelativeLayout relativeLayoutProfileAddress;
    @BindView(R.id.imageview_profile_address)
    ImageView imageViewProfileAddress;
    @BindView(R.id.textview_profile_address)
    TextView textViewProfileAddress;
    @BindView(R.id.switch_profile_address)
    Switch switchProfileAddress;
    private Toolbar toolbar;
    private AlertDialog alertDialog;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_sos_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        //toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.screen_background));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getResources().getString(R.string.sos_setting));
        titleText.setPadding(120, 0, 0, 0);

        ButterKnife.bind(this);
        Preferences.initialize(this);

        int color = Color.parseColor("#0D79C2"); //colorPrimary
        imageViewCurrentLocation.setColorFilter(color);
        imageViewCurrentLocation.setImageResource(R.drawable.vi_daily_planner_location);
        imageViewProfileAddress.setColorFilter(color);
        imageViewProfileAddress.setImageResource(R.drawable.vi_profile_location);


        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(SosSettingActivity.this).inflate(R.layout.location_popup, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(SosSettingActivity.this);
        builder.setView(dialogView);
        alertDialog = builder.create();

        final Button yesBtn = dialogView.findViewById(R.id.yes_btn);
        final Button noBtn = dialogView.findViewById(R.id.no_btn);
        final ImageView closeImg = dialogView.findViewById(R.id.close_icon);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitchCurrentLocation(1);
                Preferences.save(General.LATI_LONGI, 1);
                relativeLayoutProfileAddress.setVisibility(View.GONE);
                setSwitchProfileAddress(0);
                Preferences.save(General.PROFILE_LOCATION, 0);
                switchProfileAddress.setChecked(false);
                alertDialog.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwitchCurrentLocation(0);
                switchCurrentLocation.setChecked(false);
                Preferences.save(General.LATI_LONGI, 0);
                relativeLayoutProfileAddress.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
            }
        });

        switchCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alertDialog.show();
                } else {
                    setSwitchCurrentLocation(0);
                    Preferences.save(General.LATI_LONGI, 0);
                    relativeLayoutProfileAddress.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();
                }
            }
        });

        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        /*switchCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setSwitchCurrentLocation(1);
                    Preferences.save(General.LATI_LONGI, 1);

                    relativeLayoutProfileAddress.setVisibility(View.GONE);
                    setSwitchProfileAddress(0);
                    Preferences.save(General.PROFILE_LOCATION, 0);
                    switchProfileAddress.setChecked(false);
                } else {
                    setSwitchCurrentLocation(0);
                    Preferences.save(General.LATI_LONGI, 0);

                    relativeLayoutProfileAddress.setVisibility(View.VISIBLE);
                    //setSwitchProfileAddress(0);
                    //Preferences.save(General.PROFILE_LOCATION, 0);
                    //switchProfileAddress.setChecked(false);
                }
            }
        });*/

        switchProfileAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setSwitchProfileAddress(1);
                    Preferences.save(General.PROFILE_LOCATION, 1);
                } else {
                    setSwitchProfileAddress(0);
                    Preferences.save(General.PROFILE_LOCATION, 0);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        int lat_lon_status = Integer.valueOf(Preferences.get(General.LATI_LONGI));
        if (lat_lon_status == 1) {
            switchCurrentLocation.setChecked(true);

            relativeLayoutProfileAddress.setVisibility(View.GONE);
            setSwitchProfileAddress(0);
            Preferences.save(General.PROFILE_LOCATION, 0);
            switchProfileAddress.setChecked(false);
        } else {
            switchCurrentLocation.setChecked(false);

            relativeLayoutProfileAddress.setVisibility(View.VISIBLE);
            int profile_address_status = Integer.valueOf(Preferences.get(General.PROFILE_LOCATION));
            if (profile_address_status == 1) {
                switchProfileAddress.setChecked(true);
            } else {
                switchProfileAddress.setChecked(false);
            }
        }

        /*int profile_address_status = Integer.valueOf(Preferences.get(General.PROFILE_LOCATION));
        if(profile_address_status == 1) {
            switchProfileAddress.setChecked(true);
        } else {
            switchProfileAddress.setChecked(false);
        }*/
    }

    //  make network call to change reminder setting
    private void setSwitchCurrentLocation(int isChecked) {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.LATI_LONGI_SETTING);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.SETTING, String.valueOf(isChecked));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_YOUTH_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    ArrayList<Responses_> reminderStatusResponse = MoodParser_.parseReminderStatus(response, Actions_.LATI_LONGI_SETTING, getApplicationContext(), TAG);
                    if (reminderStatusResponse.size() > 0) {
                        status = reminderStatusResponse.get(0).getStatus();
                    } else {
                        status = 2;
                    }
                } else {
                    status = 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (status != 1) {
            GetErrorResources.showError(status, this);
        }
    }

    //  make network call to change Share setting
    private void setSwitchProfileAddress(int isChecked) {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.PROFILE_SETTING);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.SETTING, String.valueOf(isChecked));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_YOUTH_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    ArrayList<Responses_> reminderStatusResponse = MoodParser_.parseReminderStatus(response, Actions_.PROFILE_SETTING, getApplicationContext(), TAG);
                    if (reminderStatusResponse.size() > 0) {
                        status = reminderStatusResponse.get(0).getStatus();
                    } else {
                        status = 2;
                    }
                } else {
                    status = 12;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (status != 1) {
            GetErrorResources.showError(status, this);
        }
    }
}
