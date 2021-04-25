package com.modules.mood;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.modules.calendar.CustomCalendar;
import com.modules.calendar.CustomCalendarAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.storage.preferences.Preferences;

import java.util.Calendar;
import java.util.Locale;

public class CCMoodActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = CCMoodActivity.class.getSimpleName();

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private ColorStateList colorStateListText, colorStateListIcon;
    private Toolbar toolbar;
    private ImageView imageViewToolbarLeftArrow, imageViewToolbarRightArrow;
    private TextView moodTitleText;
    private CustomCalendarAdapter mMaterialCalendarAdapter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View view = inflater.inflate(R.layout.activity_mood_activity_add, null);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_cc_mood);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Preferences.initialize(getApplicationContext());

        CustomCalendar.getInitialCalendarInfo();
        imageViewToolbarLeftArrow = (ImageView) toolbar.findViewById(R.id.imageview_toolbar_left_arrorw);
        moodTitleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        imageViewToolbarRightArrow = (ImageView) toolbar.findViewById(R.id.imageview_toolbar_right_arrorw);
        int color = Color.parseColor("#ffffff"); //green
        imageViewToolbarLeftArrow.setColorFilter(color);
        imageViewToolbarRightArrow.setColorFilter(color);

        mMaterialCalendarAdapter = new CustomCalendarAdapter(this);

        if (imageViewToolbarLeftArrow != null) {
            imageViewToolbarLeftArrow.setOnClickListener(this);
        }
        if (moodTitleText != null) {
            Calendar cal = Calendar.getInstance();
            if (cal != null) {
                String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + cal.get(Calendar.YEAR);
                moodTitleText.setText(monthName);
            }
        }
        if (imageViewToolbarRightArrow != null) {
            imageViewToolbarRightArrow.setOnClickListener(this);
        }

        setBottomNavigationTextColor();
        setBottomNavigationIconColor();

        //color = Color.parseColor("#0D79C2"); //colorPrimary
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setItemTextColor(colorStateListText);
        bottomNavigationView.setItemIconTintList(colorStateListIcon);
    }

    @Override
    public void onResume() {
        super.onResume();

        Preferences.save(General.SELECTED_MOOD_FRAGMENT, getResources().getString(R.string.journal));
        fragment = new JournalFragment();
        replaceFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    public void setBottomNavigationTextColor() {
        int[][] state = new int[][] {
                new int[] {android.R.attr.state_checked}, // checked
                new int[] {-android.R.attr.state_checked}
        };
        int[] color = new int[] {
                getResources().getColor(R.color.colorPrimary), //colorPrimary
                getResources().getColor(R.color.colorPrimary) //sos_grey
        };
        colorStateListText = new ColorStateList(state, color);
    }

    public void setBottomNavigationIconColor() {
        int[][] state2 = new int[][] {
                new int[] {android.R.attr.state_checked}, // checked
                new int[] {-android.R.attr.state_checked}
        };
        int[] color2 = new int[] {
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimary)
        };
        colorStateListIcon = new ColorStateList(state2, color2);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_journal:
                Preferences.save(General.SELECTED_MOOD_FRAGMENT, getResources().getString(R.string.journal));
                fragment = new JournalFragment();
                break;
            case R.id.menu_stats:
                Preferences.save(General.SELECTED_MOOD_FRAGMENT, getResources().getString(R.string.stats));
                fragment = new StatsFragment();
                break;
            case R.id.menu_calender:
                Preferences.save(General.SELECTED_MOOD_FRAGMENT, getResources().getString(R.string.calendar));
                fragment = new MoodCalendarFragment();
                break;
            default:
                return false;
        }
        updateNavigationBarState(item.getItemId());
        replaceFragment(fragment);
        return true;
    }

    private void updateNavigationBarState(int actionId){
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
            boolean isChecked = menuItem.getItemId() == actionId;
            menuItem.setChecked(isChecked);
        }
    }

    private void replaceFragment(Fragment fragment) {
        /*FragmentManager fragmentManager = getSupportFragmentManager();
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
        ft.commit();*/
        FragmentTransaction ft;
        if (Preferences.get(General.SELECTED_MOOD_FRAGMENT).equalsIgnoreCase(getResources().getString(R.string.journal))) {
            fragment = new JournalFragment();
        } else if (Preferences.get(General.SELECTED_MOOD_FRAGMENT).equalsIgnoreCase(getResources().getString(R.string.stats))) {
            fragment = new StatsFragment();
        } else if (Preferences.get(General.SELECTED_MOOD_FRAGMENT).equalsIgnoreCase(getResources().getString(R.string.calendar))) {
            fragment = new MoodCalendarFragment();
        } else {
            fragment = new MoreFrgament();
        }
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mood_container, fragment, TAG);
        ft.commit();
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.imageview_toolbar_left_arrorw:
                    CustomCalendar.previousOnClick(imageViewToolbarLeftArrow, moodTitleText);
                    replaceFragment(fragment);
                    break;
                case R.id.imageview_toolbar_right_arrorw:
                    CustomCalendar.nextOnClick(imageViewToolbarRightArrow, moodTitleText);
                    replaceFragment(fragment);
                    break;
            }
        }
    }
}
