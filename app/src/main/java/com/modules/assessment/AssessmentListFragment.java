package com.modules.assessment;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.google.android.material.tabs.TabLayout;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.storage.preferences.Preferences;

/**
 * @author Kailash Karankal
 * Created on 09-05-2019
 **/


public class AssessmentListFragment extends Fragment implements View.OnClickListener {
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private String fragment_name;
    private FrameLayout frameLayoutFab;

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

        View view = inflater.inflate(R.layout.assessment_fragment_layout, null);

        activity = getActivity();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.assessments));

        frameLayoutFab = view.findViewById(R.id.framelayout_fab);

        TabLayout tabs = view.findViewById(R.id.assessment_tab_layout);
        tabs.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        tabs.setOnTabSelectedListener(tabSelectedListener);

        tabs.addTab(tabs.newTab().setText("Pending Form"));
        tabs.addTab(tabs.newTab().setText("Submitted Form"));

        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isNaturalSupportId(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isParentId(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {

            frameLayoutFab.setVisibility(View.GONE);
        } else {
            frameLayoutFab.setVisibility(View.GONE);
        }

        return view;
    }


    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            assert tab.getText() != null;
            fragment_name = tab.getText().toString();
            setTabFragment(fragment_name);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void setTabFragment(String fragment_name) {
        Fragment fragment;
        if (fragment_name.equalsIgnoreCase("Pending Form")) {
            fragment = new PendingFormFragment();
        } else {
            fragment = new SubmittedFormFragment();
        }
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
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
        ft.replace(R.id.assesement_container, fragment, Actions_.GET_DATA);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }


    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.assessments));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onClick(View v) {
    }
}
