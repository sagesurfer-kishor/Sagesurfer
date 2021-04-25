package com.modules.selfcare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.storage.preferences.Preferences;

/**
 * Created by Kailash Karankal on 10/17/2019.
 */
public class UploaderFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = UploaderFragment.class.getSimpleName();
    private Activity activity;
    private MainActivityInterface mainActivityInterface;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.sos_update_layout, null);
        activity = getActivity();
        Preferences.initialize(activity);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.sos_update_tab_layout);
        tabs.setOnTabSelectedListener(tabSelectedListener);
        tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.auto_approved)));
        tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.shared)));

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            //Hide tabs for senjam
            tabs.setVisibility(View.GONE);
        }
        FrameLayout frameLayoutFab = (FrameLayout) view.findViewById(R.id.framelayout_fab);
        frameLayoutFab.setVisibility(View.GONE);
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(this);

        return view;
    }

    // get action value in integer from string
    private String getAction(int position) {
        switch (position) {
            case 0:
                return Actions_.APPROVAL;
            case 1:
                return Actions_.SHARED;
            default:
                return Actions_.APPROVAL;
        }
    }

    private void replaceFragment(String action) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_DATA);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_DATA);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.replace(R.id.sos_update_container, UploadedContentListFragment.newInstance(action), Actions_.GET_DATA);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }

    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            replaceFragment(getAction(tab.getPosition()));
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.uploader));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Preferences.save(General.IS_EDIT, false);
                Intent uploadIntent = new Intent(activity.getApplicationContext(), CareUploadActivity.class);
                startActivity(uploadIntent, ActivityTransition.moveToNextAnimation(activity.getApplicationContext()));
                break;
        }
    }
}
