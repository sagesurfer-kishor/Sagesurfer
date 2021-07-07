package com.modules.mood;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.modules.calendar.CustomCalendar;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.storage.preferences.Preferences;

/**
 * Created by Monika on 11/2/2018.
 */

public class MoodFragment_ extends Fragment implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MoodFragment_.class.getSimpleName();

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private TabLayout tabs;
    private BottomNavigationView bottomNavigationViewLeft, bottomNavigationViewRight;
    private ImageView imageViewAdd;
    private Fragment fragment;
    private ColorStateList colorStateListText, colorStateListIcon;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    //Only (-.,*|?:/<>=,#() are allowed.)
    @SuppressLint({"InflateParams"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_mood_, null);
        activity = getActivity();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.mood));
        Preferences.save(General.CONSUMER_ID, ""); //consumer_id blank when logged in from consumer, using logged in user_id when logged in from consumer to show journal, stats, calendar data
        CustomCalendar.getInitialCalendarInfo();

        setBottomNavigationTextColor();
        setBottomNavigationIconColor();

        imageViewAdd = (ImageView) view.findViewById(R.id.imageview_add);
        int color = Color.parseColor("#0D79C2"); //colorPrimary
        imageViewAdd.setColorFilter(color);
        imageViewAdd.setImageResource(R.drawable.vi_mood_add);
        imageViewAdd.setOnClickListener(this);

        bottomNavigationViewLeft = view.findViewById(R.id.bottomnav_left);
        try {
            BottomNavigationViewHelper.disableShiftMode(bottomNavigationViewLeft);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bottomNavigationViewLeft.setOnNavigationItemSelectedListener(this);
        bottomNavigationViewLeft.setItemTextColor(colorStateListText);
        bottomNavigationViewLeft.setItemIconTintList(colorStateListIcon);

        bottomNavigationViewRight = view.findViewById(R.id.bottomnav_right);

        try {
            BottomNavigationViewHelper.disableShiftMode(bottomNavigationViewRight);
        } catch (Exception e) {
            e.printStackTrace();
        }


        bottomNavigationViewRight.setItemTextColor(colorStateListText);
        bottomNavigationViewRight.setItemIconTintList(colorStateListIcon);
        bottomNavigationViewRight.setOnNavigationItemSelectedListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.mood));
        mainActivityInterface.setToolbarBackgroundColor();

        Preferences.save(General.SELECTED_MOOD_FRAGMENT, activity.getResources().getString(R.string.journal));
        fragment = new JournalFragment();
        replaceFragment(fragment);
    }

    public void setBottomNavigationTextColor() {
        int[][] state = new int[][]{
                new int[]{android.R.attr.state_checked}, // checked
                new int[]{-android.R.attr.state_checked}
        };
        int[] color = new int[]{
                getResources().getColor(R.color.colorPrimary), //colorPrimary
                getResources().getColor(R.color.colorPrimary) //sos_grey
        };
        colorStateListText = new ColorStateList(state, color);
    }

    public void setBottomNavigationIconColor() {
        int[][] state2 = new int[][]{
                new int[]{android.R.attr.state_checked}, // checked
                new int[]{-android.R.attr.state_checked}
        };
        int[] color2 = new int[]{
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimary)
        };
        colorStateListIcon = new ColorStateList(state2, color2);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_journal:
                Preferences.save(General.SELECTED_MOOD_FRAGMENT, activity.getResources().getString(R.string.journal));
                mainActivityInterface.setMoodToolbar(51);
                fragment = new JournalFragment();
                break;
            case R.id.menu_stats:
                Preferences.save(General.SELECTED_MOOD_FRAGMENT, activity.getResources().getString(R.string.stats));
                mainActivityInterface.setMoodToolbar(51);
                fragment = new StatsFragment();
                break;
            case R.id.menu_calender:
                Preferences.save(General.SELECTED_MOOD_FRAGMENT, activity.getResources().getString(R.string.calendar));
                mainActivityInterface.setMoodToolbar(51);
                fragment = new MoodCalendarFragment();
                break;
            case R.id.menu_more:
                Preferences.save(General.SELECTED_MOOD_FRAGMENT, activity.getResources().getString(R.string.more));
                mainActivityInterface.setMoodToolbar(51);
                fragment = new MoreFrgament();
                break;
            default:
                return false;
        }
        updateNavigationBarState(item.getItemId());
        replaceFragment(fragment);
        return true;
    }

    private void updateNavigationBarState(int actionId) {
        for (int i = 0; i < bottomNavigationViewLeft.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationViewLeft.getMenu().getItem(i);
            boolean isChecked = menuItem.getItemId() == actionId;
            menuItem.setChecked(isChecked);
        }
        for (int i = 0; i < bottomNavigationViewRight.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationViewRight.getMenu().getItem(i);
            boolean isChecked = menuItem.getItemId() == actionId;
            menuItem.setChecked(isChecked);
        }
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
        ft.replace(R.id.mood_container, fragment, Actions_.SOS);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_add:
                Preferences.save(General.SELECTED_MOOD_FRAGMENT, activity.getResources().getString(R.string.add_mood));
                //fragment = new MoodAddActivity();
                updateNavigationBarState(0);
                //replaceFragment(fragment);

                /*android.app.DialogFragment dialogFrag = new MoodAddActivity();
                dialogFrag.show(activity.getFragmentManager().beginTransaction(), Actions_.ADD_MOOD);*/
                Preferences.save(General.IS_ADD_MOOD_ACTIVITY_BACK_PRESSED, true);
                Intent createIntent = new Intent(activity.getApplicationContext(), MoodAddActivity.class);
                startActivity(createIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }

    /*//set tab with icon and counter to it
    @SuppressLint("InflateParams")
    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.postcard_tab_item_layout, null);
        tabOne.setText("Journal");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.xi_postcard_inbox, 0, 0);
        tabs.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.postcard_tab_item_layout, null);
        tabTwo.setText("Stats");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.xi_postcard_draft, 0, 0);
        tabs.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.postcard_tab_item_layout, null);
        tabThree.setText("Add");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.xi_postcard_sent, 0, 0);
        tabs.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.postcard_tab_item_layout, null);
        tabFour.setText("");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.xi_postcard_trash, 0, 0);
        tabs.getTabAt(3).setCustomView(tabFour);
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
        if (fragment_name.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.received_sos))) {
            fragment = new ReceivedSosFragment();
        } else {
            fragment = new MySosFragment();
        }
        replaceFragment(fragment);
    }*/


}
