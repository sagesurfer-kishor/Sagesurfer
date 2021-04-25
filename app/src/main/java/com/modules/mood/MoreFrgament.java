package com.modules.mood;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sagesurfer.collaborativecares.BuildConfig;
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
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * Created by Monika on 11/13/2018.
 */

public class MoreFrgament extends Fragment {
    private static final String TAG = MoreFrgament.class.getSimpleName();

    @BindView(R.id.imageview_reminders)
    ImageView imageViewReminder;
    @BindView(R.id.switch_reminder)
    Switch switchReminder;
    @BindView(R.id.imageview_share)
    ImageView imageViewShare;
    @BindView(R.id.textview_share)
    TextView textViewShare;
    @BindView(R.id.switch_share)
    SwitchCompat switchShare;

    private Unbinder unbinder;
    private Activity activity;
    private RelativeLayout mRelatieLayoutShare;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_mood_more, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
        Preferences.initialize(activity);
        mRelatieLayoutShare = view.findViewById(R.id.relativelayout_share);
        mRelatieLayoutShare.setEnabled(false);
        int color = Color.parseColor("#0D79C2"); //colorPrimary
        imageViewReminder.setColorFilter(color);
        imageViewReminder.setImageResource(R.drawable.vi_mood_reminder);
        imageViewShare.setColorFilter(color);
        imageViewShare.setImageResource(R.drawable.vi_mood_share);
        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setSwitchReminder(1);
                    Preferences.save(General.MOOD_REMINDER_STATUS, 1);
                } else {
                    setSwitchReminder(0);
                    Preferences.save(General.MOOD_REMINDER_STATUS, 0);
                }
            }
        });


        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")){

            // set color to switch thumb
            ColorStateList thumbStates = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_checked},
                    },
                    new int[]{
                            color,
                            Color.WHITE,
                    }
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                switchShare.setThumbTintList(thumbStates);
            }

            if (Build.VERSION.SDK_INT >= 24) {

                // set color to switch track
                ColorStateList trackStates = new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_enabled}
                        },
                        new int[]{
                                Color.parseColor("#85a78c")
                        }
                );
                switchShare.setTrackTintList(trackStates);
//                switchShare.setTrackTintMode(PorterDuff.Mode.OVERLAY);
            }

            switchShare.setEnabled(false);
        }

        switchShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Preferences.save(General.MOBILE_SETTING, 1);
                    setSwitchShare(1);
                } else {
                    Preferences.save(General.MOBILE_SETTING, 0);
                    setSwitchShare(0);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage013))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))) {
            textViewShare.setText(activity.getResources().getString(R.string.share_with_peer_mentor));
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
            textViewShare.setText(activity.getResources().getString(R.string.share_with_wellness_asso));
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
            textViewShare.setText(activity.getResources().getString(R.string.share_with_coach));
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
            textViewShare.setText("Case Manager");
        }else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage008))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage006))) {
            textViewShare.setText(getResources().getString(R.string.share_with_care_coordinator));
        } else if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            textViewShare.setText(activity.getResources().getString(R.string.share_with_clinician));
        } else {
            textViewShare.setText(activity.getResources().getString(R.string.share_with_sys_admin));
        }
        int mood_reminder_status = Integer.valueOf(Preferences.get(General.MOOD_REMINDER_STATUS));
        if (mood_reminder_status == 1) {
            switchReminder.setChecked(true);
        } else {
            switchReminder.setChecked(false);
        }

        int mood_share_status = Integer.valueOf(Preferences.get(General.MOBILE_SETTING));
        if (mood_share_status == 1) {
            switchShare.setChecked(true);
        } else {
            switchShare.setChecked(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //  make network call to change reminder setting
    private void setSwitchReminder(int isChecked) {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.REMINDER_SETTING);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.STATUS, String.valueOf(isChecked));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    ArrayList<Responses_> reminderStatusResponse = MoodParser_.parseReminderStatus(response, Actions_.REMINDER_SETTING, activity.getApplicationContext(), TAG);
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
            GetErrorResources.showError(status, activity);
        }
    }

    //  make network call to change Share setting
    private void setSwitchShare(int isChecked) {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.MOOD_SETTING);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.MOBILE_SETTING, String.valueOf(isChecked));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    ArrayList<Responses_> reminderStatusResponse = MoodParser_.parseReminderStatus(response, Actions_.MOOD_SETTING, activity.getApplicationContext(), TAG);
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
            GetErrorResources.showError(status, activity);
        }
    }
}
