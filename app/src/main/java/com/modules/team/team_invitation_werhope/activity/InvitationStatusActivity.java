package com.modules.team.team_invitation_werhope.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.modules.team.team_invitation_werhope.fragments.AllInvitationFragment;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Teams_;

public class InvitationStatusActivity extends AppCompatActivity {
    private static final String TAG = InvitationStatusActivity.class.getSimpleName();
    private String tab_name;
    private Teams_ team = new Teams_();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_invitation_status);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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
        titleText.setPadding(120, 0, 0, 0);
        titleText.setText(getResources().getString(R.string.invitation_status));

        Intent data = getIntent();
        if (data != null && data.hasExtra(General.TEAM)) {
            team = (Teams_) data.getSerializableExtra(General.TEAM);
        }

        initUI();

    }

    private void initUI() {
        TabLayout tabs = findViewById(R.id.team_invite_tab_layout);
        tabs.setOnTabSelectedListener(tabSelectedListener);
        tabs.addTab(tabs.newTab().setText("All"));
        tabs.addTab(tabs.newTab().setText("Pending"));
        tabs.addTab(tabs.newTab().setText("Declined"));
    }

    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            assert tab.getText() != null;
            tab_name = tab.getText().toString();
            setTabFragment(tab_name);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void setTabFragment(String fragment_name) {
        Fragment fragment = new AllInvitationFragment();
        Bundle bundle = new Bundle();
        if (fragment_name.equalsIgnoreCase("All")) {
            bundle.putSerializable(General.ACTION, Actions_.ALL);
            bundle.putSerializable(General.TEAM, team);
            fragment.setArguments(bundle);
        } else if (fragment_name.equalsIgnoreCase("Pending")) {
            bundle.putSerializable(General.ACTION, Actions_.PENDIN);
            bundle.putSerializable(General.TEAM, team);
            fragment.setArguments(bundle);
        } else {
            bundle.putSerializable(General.ACTION, Actions_.DECLIN);
            bundle.putSerializable(General.TEAM, team);
            fragment.setArguments(bundle);
        }
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.invitation_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
