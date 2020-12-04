package com.modules.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 17-07-2017
 *         Last Modified on 14-12-2017
 */

public class TaskMainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = TaskMainFragment.class.getSimpleName();

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

        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.task_list));

        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.sos_update_float);
        createButton.setOnClickListener(this);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.sos_update_tab_layout);

        tabs.setOnTabSelectedListener(tabSelectedListener);
        tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.team_task)));
        tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.my_task)));

        return view;
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
        if (fragment_name.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.my_task))) {
            fragment = new MyTaskListFragment();
        } else {
            fragment = new TeamTaskListFragment();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.task_list));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sos_update_float:
                Intent sosIntent = new Intent(activity.getApplicationContext(), CreateTaskActivity.class);
              //  startActivity(sosIntent, ActivityTransition.moveToNextAnimation(activity.getApplicationContext()));
                activity.startActivity(sosIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }
}
