package com.modules.selfcare;

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
 *         Created on 12-09-2017
 *         Last Modified on 14-12-2017
 */

public class ReviewerListFragment extends Fragment {

    private static final String TAG = ReviewerListFragment.class.getSimpleName();

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

        TabLayout tabs = (TabLayout) view.findViewById(R.id.sos_update_tab_layout);

        tabs.setOnTabSelectedListener(tabSelectedListener);

        tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.pending)));
        tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.approved)));
        tabs.addTab(tabs.newTab().setText(activity.getApplicationContext().getResources().getString(R.string.rejected)));

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.sos_update_float);
        floatingActionButton.setVisibility(View.GONE);
        return view;
    }

    // get action based on selected tab position
    private String getAction(int position) {
        switch (position) {
            case 0:
                return Actions_.RE_PENDING;
            case 1:
                return Actions_.RE_APPROVAL;
            case 2:
                return Actions_.RE_REJECTED;
            default:
                return Actions_.RE_PENDING;
        }
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

    @Override
    public void onResume() {
        super.onResume();
        //mainActivityInterface.hideRevealView();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.reviewer));
        mainActivityInterface.setToolbarBackgroundColor();
    }
}
