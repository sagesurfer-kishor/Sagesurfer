package com.modules.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 22-08-2017
 * Last Modified on 13-12-2017
 */

/*
 * This is master file/fragment for admin approval module with two tab in it,
 * for displaying list of invitation (internal and external) and spammed content
 */

public class AdminApprovalsFragment extends Fragment {

    private static final String TAG = AdminApprovalsFragment.class.getSimpleName();

    private Activity activity;
    private MainActivityInterface mainActivityInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Initialize MainActivityInterface
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.sos_update_layout, null);

        activity = getActivity();

        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.admin_approval));

        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.sos_update_float);
        createButton.setVisibility(View.GONE);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.sos_update_tab_layout);
        tabs.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));

        tabs.setOnTabSelectedListener(tabSelectedListener);
        tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources()
                .getString(R.string.deleting)));
        tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources()
                .getString(R.string.invitations)));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //mainActivityInterface.hideRevealView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.admin_approval));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    // Handle click event of tabs from tab layout
    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            String fragment_name = tab.getText().toString();
            setTabFragment(fragment_name);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    // Set tab fragment respective the names
    private void setTabFragment(String fragment_name) {
        Fragment fragment;
        if (fragment_name.equalsIgnoreCase(activity.getApplicationContext().getResources()
                .getString(R.string.deleting))) {
            fragment = new DeletingListFragment();
        } else {
            fragment = new InvitationsListFragment();
        }
        replaceFragment(fragment);
    }

    // Change/replace fragment as per user input/click
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.DELETE);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fragmentManager.findFragmentByTag(Actions_.DELETE);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.replace(R.id.sos_update_container, fragment, Actions_.DELETE);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }

}
