package com.modules.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.storage.preferences.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Kailash Karankal
 */

public class SettingsFragment extends Fragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;

    @BindView(R.id.relativelayout_settings_termsandcondition)
    RelativeLayout relativelayoutSettingsTermsandcondition;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
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

        View view = inflater.inflate(R.layout.fragment_settings, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        /*if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage024))) {
            relativelayoutSettingsTermsandcondition.setVisibility(View.GONE);
        } else {
            relativelayoutSettingsTermsandcondition.setVisibility(View.VISIBLE);
        }*/
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.action_settings));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.relativelayout_settings_editprofile, R.id.relativelayout_settings_changepassword,
            R.id.relativelayout_settings_termsandcondition, R.id.relativelayout_settings_changetheme})
    public void onButtonClick(View view) {
        Intent createIntent = new Intent();
        switch (view.getId()) {
            case R.id.relativelayout_settings_editprofile:
                createIntent = new Intent(activity.getApplicationContext(), EditProfileActivity.class);
                startActivity(createIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.relativelayout_settings_changepassword:
                createIntent = new Intent(activity.getApplicationContext(), ChangePasswordActivity.class);
                startActivity(createIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.relativelayout_settings_termsandcondition:
                /*createIntent = new Intent(activity.getApplicationContext(), TermsAndConditionActivity.class);
                startActivity(createIntent);
                activity.overridePendingTransition(0, 0);*/
                break;

            case R.id.relativelayout_settings_changetheme:
                //replaceFragment(homeMenuList.get(3).getId(), homeMenuList.get(3).getMenu(), null);
                break;

            /*case R.id.linearlayout_home_five:
                //replaceFragment(homeMenuList.get(4).getId(), homeMenuList.get(4).getMenu(), null);
                break;

            case R.id.linearlayout_home_six:
                //replaceFragment(homeMenuList.get(5).getId(), homeMenuList.get(5).getMenu(), null);
                break;*/
        }
    }
}
