package com.modules.cometchat_7_30.Calls;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.uikit.Settings.UISettings;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;

import adapter.TabAdapter;
import screen.call.AllCall;
import screen.call.MissedCall;
import utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMainCallHistory#newInstance} factory method to
 * create an instance of this fragment.
 * created by rahul maske
 */

public class FragmentMainCallHistory extends Fragment {

    private TabAdapter tabAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView phoneAddIv;
    private View view;
    private MessagesRequest messageRequest;    //Uses to fetch Conversations.
    private TextView tvTitle;
    private ShimmerFrameLayout conversationShimmer;

    private static final String TAG = "CallList";
    public FragmentMainCallHistory() {
    }


    public static FragmentMainCallHistory newInstance(String param1, String param2) {
        FragmentMainCallHistory fragment = new FragmentMainCallHistory();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_main_call_history, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        if (getActivity() != null) {
            tabAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
            tabAdapter.addFragment(new AllCall(), getContext().getResources().getString(com.cometchat.pro.uikit.R.string.all));
            tabAdapter.addFragment(new MissedCall(), getContext().getResources().getString(com.cometchat.pro.uikit.R.string.missed));
            viewPager.setAdapter(tabAdapter);
        }
        tabLayout.setupWithViewPager(viewPager);

        if (UISettings.getColor()!=null) {
//            phoneAddIv.setImageTintList(ColorStateList.valueOf(Color.parseColor(UISettings.getColor())));
            Drawable wrappedDrawable = DrawableCompat.wrap(getResources().
                    getDrawable(com.cometchat.pro.uikit.R.drawable.tab_layout_background_active));
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(UISettings.getColor()));
            tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).view.setBackground(wrappedDrawable);
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor(UISettings.getColor()));
        } else {
            tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).
                    view.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.colorPrimary));
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(com.cometchat.pro.uikit.R.color.colorPrimary));
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (UISettings.getColor()!=null) {
                    Drawable wrappedDrawable = DrawableCompat.wrap(getResources().
                            getDrawable(com.cometchat.pro.uikit.R.drawable.tab_layout_background_active));
                    DrawableCompat.setTint(wrappedDrawable, Color.parseColor(UISettings.getColor()));
                    tab.view.setBackground(wrappedDrawable);
                }
                else
                    tab.view.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.colorPrimary));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        checkDarkMode();

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            /*if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                mainActivity.hideLogBookIcon(true);
            } else {
                mainActivity.hideLogBookIcon(false);
            }*/
            mainActivity.showHideBellIcon2(true);
            //mainActivity.showChatIcon(true);
            mainActivity.hidesettingIcon(true);
        }
        return view;
    }

    private void checkDarkMode() {
        if(Utils.isDarkMode(getContext())) {
//            tvTitle.setTextColor(getResources().getColor(com.cometchat.pro.uikit.R.color.textColorWhite));
            tabLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(com.cometchat.pro.uikit.R.color.grey)));
            tabLayout.setTabTextColors(getResources().getColor(com.cometchat.pro.uikit.R.color.textColorWhite),getResources().getColor(com.cometchat.pro.uikit.R.color.light_grey));
        } else {
       //     tvTitle.setTextColor(getResources().getColor(com.cometchat.pro.uikit.R.color.primaryTextColor));
            tabLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(com.cometchat.pro.uikit.R.color.textColorWhite)));
            tabLayout.setTabTextColors(getResources().getColor(com.cometchat.pro.uikit.R.color.primaryTextColor),getResources().getColor(com.cometchat.pro.uikit.R.color.textColorWhite));
        }
    }
    /*private void openUserListScreen() {
        Intent intent = new Intent(getContext(), CometChatNewCallList.class);
        startActivity(intent);
    }*/
}