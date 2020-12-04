package com.modules.caseload;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.library.GetColor;

/**
 * Created by Monika on 5/31/2018.
 */

public class CaseloadSummaryActivity extends AppCompatActivity {

    private static final String TAG = CaseloadSummaryActivity.class.getSimpleName();

    Toolbar toolbar;
    private TabLayout tabLayout;
    String userId = "";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_caseload_summary);

        toolbar = (Toolbar) findViewById(R.id.caseload_summary_toolbar);
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

        tabLayout = (TabLayout) findViewById(R.id.caseload_summary_tab_layout);
        tabLayout.setOnTabSelectedListener(tabSelectedListener);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.profile)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.overview)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.goals_and_graph)));

        /*Intent data = getIntent();
        if (data != null) {
            if (data.hasExtra(General.USER_ID)) {
                userId = data.getStringExtra(General.USER_ID);
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0,0);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        tabLayout.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            assert tab.getText() != null;
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


    private void setTabFragment(String fragment_name) {
        Fragment fragment;
        if (fragment_name.equalsIgnoreCase(getResources().getString(R.string.profile))) {
            fragment = new SummaryProfileFragment();
        } else if (fragment_name.equalsIgnoreCase(getResources().getString(R.string.overview))) {
            fragment = new SummaryOverviewFragment();
        } else {
            fragment = new SummaryGoalsGraphFragment();
        }
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PROFILE_DATA);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PROFILE_DATA);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.replace(R.id.caseload_summary_container, fragment, Actions_.GET_PROFILE_DATA);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }
}
