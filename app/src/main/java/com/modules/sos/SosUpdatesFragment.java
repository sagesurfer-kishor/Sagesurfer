package com.modules.sos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.storage.preferences.Preferences;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 17-07-2017
 *         Last Modified on 14-12-2017
 **/

public class SosUpdatesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SosUpdatesFragment.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    static FloatingActionButton fb_createSOS;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private FrameLayout frameLayoutFab;
    private String fragment_name;
    private EditText editTextSearch;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
        Log.i(TAG, "onAttach: ");
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.sos_update_layout, null);

        activity = getActivity();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources()
                .getString(R.string.sos_updates));

        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
        frameLayoutFab = (FrameLayout) view.findViewById(R.id.framelayout_fab);
        fb_createSOS = (FloatingActionButton) view.findViewById(R.id.sos_update_float);
        fb_createSOS.setOnClickListener(this);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.sos_update_tab_layout);
        tabs.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        tabs.setOnTabSelectedListener(tabSelectedListener);

        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.my_sos)));
            tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.received_sos)));
        } else {
            tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.received_sos)));
            tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.my_sos)));
        }
        Log.e(TAG, "RoleId: "+Integer.parseInt(Preferences.get(General.ROLE_ID)));
        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isNaturalSupportId(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isParentId(Integer.parseInt(Preferences.get(General.ROLE_ID)))
        ) {
            Log.i(TAG, "onCreateView: Fab visible");
            frameLayoutFab.setVisibility(View.VISIBLE);
            //getHeight(createButton);
        } else {
            frameLayoutFab.setVisibility(View.GONE);
            Log.i(TAG, "onCreateView: Fab gone");
            //getHeight(SosUpdatesFragment.createButton);
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
        InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        Fragment fragment;
        if (fragment_name.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.received_sos))) {
            fragment = new ReceivedSosFragment();
        } else {
            fragment = new MySosFragment();
        }
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.SOS);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fragmentManager.findFragmentByTag(Actions_.SOS);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.replace(R.id.sos_update_container, fragment, Actions_.SOS);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        //mainActivityInterface.hideRevealView();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.sos_updates));
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.sos_updates));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sos_update_float:
                Intent sosIntent = new Intent(activity.getApplicationContext(), CreateSosActivity.class);
                startActivity(sosIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }
}
