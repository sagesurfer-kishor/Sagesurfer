package com.modules.postcard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Counters_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 17-07-2017
 * Last Modified on 14-12-2017
 */

public class PostcardFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = PostcardFragment.class.getSimpleName();
    private TabLayout tabLayout;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private static String inbox, draft, send, trash;
    private static TextView tabOne, tabTwo, tabThree, tabFour;
    private ViewPager viewPager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPagerAdapter adapter;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.postcard_layout, null);

        activity = getActivity();

        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources()
                .getString(R.string.message));

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.postcard_create_button);
        createButton.setOnClickListener(this);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        viewPager.setCurrentItem(0);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setOffscreenPageLimit(0);
                        viewPager.setAdapter(adapter);
                        setupTabIcons();
                        getCounters(activity);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        return view;
    }

    //set tab with icon and counter to it
    @SuppressLint("InflateParams")
    private void setupTabIcons() {
        tabOne = (TextView) LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.postcard_tab_item_layout, null);
        tabOne.setText(inbox);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.xi_postcard_inbox, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        tabTwo = (TextView) LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.postcard_tab_item_layout, null);
        tabTwo.setText(draft);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.xi_postcard_draft, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        tabThree = (TextView) LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.postcard_tab_item_layout, null);
        tabThree.setText(send);
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.xi_postcard_sent, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        tabFour = (TextView) LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.postcard_tab_item_layout, null);
        tabFour.setText(trash);
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.xi_postcard_trash, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    //set view page to set multiple postcard options
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new PostcardListFragment(), "ONE");
        adapter.addFrag(new PostcardListFragment(), "TWO");
        adapter.addFrag(new PostcardListFragment(), "THREE");
        adapter.addFrag(new PostcardListFragment(), "FOUR");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return PostcardListFragment.newInstance(getType(position));
        }

        private String getType(int position) {
            switch (position) {
                case 0:
                    return activity.getApplicationContext().getResources().getString(R.string.inbox);
                case 1:
                    return activity.getApplicationContext().getResources().getString(R.string.draft);
                case 2:
                    return activity.getApplicationContext().getResources().getString(R.string.outbox);
                case 3:
                    return activity.getApplicationContext().getResources().getString(R.string.trash);
            }
            return activity.getApplicationContext().getResources().getString(R.string.inbox);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.message));
        mainActivityInterface.setToolbarBackgroundColor();
        getCounters(activity);
    }

    public static Counters_ getCounters(Context _context) {
        Counters_ counters_ = null;
        Preferences.initialize(_context);

        String url = Preferences.get(General.DOMAIN) + Urls_.COUNTER_URL;

        HashMap<String, String> map = new HashMap<>();
        map.put(General.USER_ID, Preferences.get(General.USER_ID));

        RequestBody requestBody = NetworkCall_.make(map, url, TAG, _context);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, _context);
                if (response != null && Error_.oauth(response, _context) == 0) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                    Gson gson = new Gson();
                    counters_ = gson.fromJson(jsonObject.toString(), Counters_.class);

                    draft = String.valueOf(counters_.getDraft());
                    send = String.valueOf(counters_.getSent());
                    inbox = String.valueOf(counters_.getPostcard());
                    trash = String.valueOf(counters_.getTrash());

                    if (inbox.equalsIgnoreCase("0")) {
                        tabOne.setText("");
                    } else {
                        tabOne.setText(inbox);
                    }

                    if (draft.equalsIgnoreCase("0")) {
                        tabTwo.setText("");
                    } else {
                        tabTwo.setText(draft);
                    }

                    if (send.equalsIgnoreCase("0")) {
                        tabThree.setText("");
                    } else {
                        tabThree.setText(send);
                    }

                    if (trash.equalsIgnoreCase("0")) {
                        tabFour.setText("");
                    } else {
                        tabFour.setText(trash);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return counters_;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.postcard_create_button:
                Intent nextIntent = new Intent(activity.getApplicationContext(), CreateMailActivity.class);
                startActivity(nextIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }
}
