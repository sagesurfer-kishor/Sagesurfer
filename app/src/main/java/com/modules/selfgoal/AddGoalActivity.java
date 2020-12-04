package com.modules.selfgoal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.AddGoalActivityInterface;
import com.sagesurfer.interfaces.GoalDetailsInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.views.NonSwipeableViewPager;
import com.storage.preferences.AddGoalPreferences;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 23/03/2018
 * Last Modified on
 */

public class AddGoalActivity extends AppCompatActivity implements View.OnClickListener, AddGoalActivityInterface, GoalDetailsInterface {

    private static final String TAG = AddGoalActivity.class.getSimpleName();

    @BindView(R.id.textview_addgoalactivity_bubble_one)
    TextView textViewAddGoalActivityBubbleOne;
    @BindView(R.id.view_addgoalactivity_line_zero)
    View viewAddGoalActivityLineZero;
    @BindView(R.id.view_addgoalactivity_line_one)
    View viewAddGoalActivityLineOne;
    @BindView(R.id.view_addgoalactivity_line_two)
    View viewAddGoalActivityLineTwo;
    @BindView(R.id.textview_addgoalactivity_bubble_two)
    TextView textViewAddGoalActivityBubbleTwo;
    @BindView(R.id.view_addgoalactivity_line_three)
    View viewAddGoalActivityLineThree;
    @BindView(R.id.textview_addgoalactivity_bubble_three)
    TextView textViewAddGoalActivityBubbleThree;
    @BindView(R.id.nonswipeableviewpager_addgoalactivity_pager)
    NonSwipeableViewPager nonSwipeableViewPagerAddGoalActivityPager;

    private Goal_ goal;
    TextView textViewActivityToolbarPost;

    Toolbar activityToolbar;

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_add_goal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ButterKnife.bind(this);

        Preferences.initialize(getApplicationContext());
        AddGoalPreferences.initialize(getApplicationContext());

        activityToolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        //activityToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.screen_background));
        setSupportActionBar(activityToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        activityToolbar.setNavigationIcon(R.drawable.vi_cancel_white);
        activityToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView textViewActivityToolbarTitle = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        //textViewActivityToolbarTitle.setTextColor(getResources().getColor(R.color.text_color_primary));

        textViewActivityToolbarPost = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        //textViewActivityToolbarPost.setTextColor(getResources().getColor(R.color.text_color_tertiary));

        Intent data = getIntent();
        if (data.hasExtra(General.TITLE)) {
            textViewActivityToolbarTitle.setText(data.getStringExtra(General.TITLE));
            if (data.getStringExtra(General.TITLE).equalsIgnoreCase("edit")) {
                AddGoalPreferences.save(General.ACTION, data.getStringExtra(General.TITLE), TAG);
            } else if (data.getStringExtra(General.TITLE).equalsIgnoreCase(getApplicationContext()
                    .getResources().getString(R.string.reschedule_goal))) {
                AddGoalPreferences.save(General.ACTION, "reschedule", TAG);
            }
        } else {
            AddGoalPreferences.clear(TAG);
            textViewActivityToolbarTitle.setText(this.getResources().getString(R.string.add_goal));
            AddGoalPreferences.save(General.ACTION, getApplicationContext().getResources().getString(R.string.add_goal), TAG);
        }
        if (data.hasExtra("map")) {
            goal = (Goal_) data.getSerializableExtra("map");
            if (goal != null) {
                setData();
            }
        }

        PagerAdapter mPagerAdapter = new AddGoalPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        nonSwipeableViewPagerAddGoalActivityPager.setAdapter(mPagerAdapter);
        nonSwipeableViewPagerAddGoalActivityPager.addOnPageChangeListener(onPageChange);

        textViewAddGoalActivityBubbleOne.setOnClickListener(this);
        textViewAddGoalActivityBubbleTwo.setOnClickListener(this);
        textViewAddGoalActivityBubbleThree.setOnClickListener(this);

        textViewActivityToolbarPost.setVisibility(View.VISIBLE);
        textViewActivityToolbarPost.setText(this.getResources().getString(R.string.next));
        textViewActivityToolbarPost.setOnClickListener(this);


    }

    // handle page scroll to set scroll bubble
    private final ViewPager.OnPageChangeListener onPageChange = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setBubble(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    // set page scroll bubble and number as per page location
    private void setBubble(int position) {
        switch (position) {
            case 0:
                textViewAddGoalActivityBubbleOne.setBackgroundResource(R.drawable.primary_circle);
                textViewAddGoalActivityBubbleTwo.setBackgroundResource(R.drawable.disable_color_rounded_rectangle);
                textViewAddGoalActivityBubbleThree.setBackgroundResource(R.drawable.disable_color_rounded_rectangle);

                viewAddGoalActivityLineZero.setBackgroundColor(this.getResources().getColor(R.color.disable_button_color));
                viewAddGoalActivityLineOne.setBackgroundColor(this.getResources().getColor(R.color.disable_button_color));
                viewAddGoalActivityLineTwo.setBackgroundColor(this.getResources().getColor(R.color.disable_button_color));
                viewAddGoalActivityLineThree.setBackgroundColor(this.getResources().getColor(R.color.disable_button_color));

                textViewActivityToolbarPost.setText(this.getResources().getString(R.string.next));
                break;
            case 1:
                textViewAddGoalActivityBubbleOne.setBackgroundResource(R.drawable.primary_circle);
                textViewAddGoalActivityBubbleTwo.setBackgroundResource(R.drawable.primary_circle);
                textViewAddGoalActivityBubbleThree.setBackgroundResource(R.drawable.disable_color_rounded_rectangle);

                viewAddGoalActivityLineZero.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
                viewAddGoalActivityLineOne.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
                viewAddGoalActivityLineTwo.setBackgroundColor(this.getResources().getColor(R.color.disable_button_color));
                viewAddGoalActivityLineThree.setBackgroundColor(this.getResources().getColor(R.color.disable_button_color));
                textViewActivityToolbarPost.setText(this.getResources().getString(R.string.next));
                break;
            case 2:
                textViewAddGoalActivityBubbleOne.setBackgroundResource(R.drawable.primary_circle);
                textViewAddGoalActivityBubbleTwo.setBackgroundResource(R.drawable.primary_circle);
                textViewAddGoalActivityBubbleThree.setBackgroundResource(R.drawable.primary_circle);

                viewAddGoalActivityLineZero.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
                viewAddGoalActivityLineOne.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
                viewAddGoalActivityLineTwo.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
                viewAddGoalActivityLineThree.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));

                textViewActivityToolbarPost.setText(this.getResources().getString(R.string.done));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int position = nonSwipeableViewPagerAddGoalActivityPager.getCurrentItem();
        switch (v.getId()) {
            case R.id.textview_addgoalactivity_bubble_one:
                sendMoveBroadcast(0);
                break;
            case R.id.textview_addgoalactivity_bubble_two:
                sendMoveBroadcast(1);
                break;
            case R.id.textview_addgoalactivity_bubble_three:
                if (position == 1) {
                    sendMoveBroadcast(2);
                }
                break;
            case R.id.textview_activitytoolbar_post:
                sendMoveBroadcast(position);
                break;
        }
    }

    // make a network call to create goal
    // 0: Counting goal
    // 1: Yes/no Goal
    // 2: Global goal (Tread this goal same as Counting goal)
    private void createGoal(String action_type) {

        String image_id = AddGoalPreferences.get(General.IMAGE);
        String name = AddGoalPreferences.get(General.NAME);
        String description = AddGoalPreferences.get(General.DESCRIPTION);
        String impact = AddGoalPreferences.get(General.IMPACT);

        boolean goal_type = AddGoalPreferences.getBoolean(General.GOAL_TYPE);
        int goalType = 1;
        if (goal_type) {
            goalType = 0;
        }

        String count = AddGoalPreferences.get(General.COUNT);
        String occurrences = AddGoalPreferences.get("occurrences");
        String unit = AddGoalPreferences.get(General.UNITS);
        String frequency = AddGoalPreferences.get(General.FREQUENCY).toLowerCase();
        String time = getTime(frequency);
        String milestone_names = AddGoalPreferences.get(General.MILESTONE);
        String milestone_dates = AddGoalPreferences.get(General.MILESTONE_DATE);
        String del_mile_id = AddGoalPreferences.get("del_mile_id");
        String mile_id = AddGoalPreferences.get(General.MILESTONE_ID);
        String notify = AddGoalPreferences.get(General.NOTE);
        String notify_time = convertTime(AddGoalPreferences.get(General.NOTIFY_HOUR), AddGoalPreferences.get(General.NOTIFY_UNIT)) + ":" + AddGoalPreferences.get(General.NOTIFY_MINUTE);
        if (notify_time.equalsIgnoreCase("null") || notify_time.equalsIgnoreCase(":null")) {
            notify_time = "0";
        }
        String checked_noti = AddGoalPreferences.get("checked_noti");
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.GOAL_ACTION);
        requestMap.put("action_type", action_type);
        requestMap.put(General.NAME, name);
        requestMap.put(General.GOAL_TYPE, "" + goalType);
        requestMap.put(General.UNITS, unit);
        requestMap.put(General.FREQUENCY, frequency);
        requestMap.put(General.START_DATE, AddGoalPreferences.get(General.START_DATE));
        requestMap.put(General.END_DATE, AddGoalPreferences.get(General.END_DATE));
        requestMap.put(General.DESCRIPTION, description);
        requestMap.put(General.IMPACT, impact);
        if (AddGoalPreferences.contains(General.ID) && (!AddGoalPreferences.get(General.ID).equalsIgnoreCase("0") || AddGoalPreferences.get(General.ID).trim().length() > 0)) {
            requestMap.put(General.ID, AddGoalPreferences.get(General.ID));
        } else {
            requestMap.put(General.ID, "0");
        }
        requestMap.put(General.MILESTONE_ID, mile_id);
        requestMap.put("del_mile_id", del_mile_id);
        requestMap.put(General.MILESTONE, milestone_names);
        requestMap.put(General.MILESTONE_DATE, milestone_dates);
        requestMap.put("notification", notify);
        requestMap.put("notify_at", notify_time);
        requestMap.put("frequency_unit", FrequencyUnit_.getFrequencyUnit(frequency, getTime(frequency), getApplicationContext()));
        requestMap.put("occurrences", occurrences);
        requestMap.put("quantity", count);
        requestMap.put("checked_noti", checked_noti);
        requestMap.put("img_gallery_id", image_id);
        if (!frequency.equalsIgnoreCase("minute") || !frequency.equalsIgnoreCase("hour")) {
            requestMap.put(General.START_TIME, time);
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("goal_action")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("goal_action");
                        JSONObject object = jsonArray.getJSONObject(0);
                        int status = object.getInt(General.STATUS);
                        if (status == 1) {
                            ShowToast.toast(this.getResources().getString(R.string.successful), getApplicationContext());
                            AddGoalPreferences.clear(TAG);
                        } else {
                            ShowToast.toast(this.getResources().getString(R.string.action_failed), getApplicationContext());
                        }
                    }
                    onBackPressed();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ShowToast.toast(this.getResources().getString(R.string.action_failed), getApplicationContext());
    }

    // get time in desired format
    private String getTime(String frequency) {
        String time;
        if (frequency.equalsIgnoreCase(getApplicationContext().getResources().getString(R.string.hour))) {
            time = AddGoalPreferences.get(General.START_HOUR) + ":" + AddGoalPreferences.get(General.START_MINUTE);
        } else {
            time = convertTime(AddGoalPreferences.get(General.START_HOUR), AddGoalPreferences.get(General.TIME_UNIT)) + ":" + AddGoalPreferences.get(General.START_MINUTE);
        }
        return time;
    }

    // convert time to desired format
    private String convertTime(String hour_, String unit) {
        if (hour_ == null || unit == null) {
            return "";
        }
        if (unit.equalsIgnoreCase("pm")) {
            int _hour = Integer.parseInt(hour_);
            if (_hour != 12) {
                return "" + GetCounters.checkDigit((_hour + 12));
            }
        } else {
            int _hour = Integer.parseInt(hour_);
            if (_hour == 12) {
                return "00";
            }
        }
        return hour_;
    }

    // send page move broadcast to next page with current page location
    public void sendMoveBroadcast(int position) {
        int current_position = nonSwipeableViewPagerAddGoalActivityPager.getCurrentItem();
        if (current_position > position) {
            nonSwipeableViewPagerAddGoalActivityPager.setCurrentItem(position, true);
            return;
        }
        Intent intent = new Intent();
        intent.setAction("" + current_position);
        intent.putExtra(General.CHECK_IN, "1");
        intent.putExtra(General.POSITION, "" + current_position);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @Override
    public void moveToNext(boolean isMove, int position) {
        if (isMove) {
            nonSwipeableViewPagerAddGoalActivityPager.setCurrentItem(position, true);
        }
    }

    @Override
    public void createGoal(boolean isCreate) {
        String action = "add";
        if (AddGoalPreferences.contains(General.ACTION)) {
            if (AddGoalPreferences.get(General.ACTION).equalsIgnoreCase(this.getResources().getString(R.string.edit))) {
                action = "edit";
            }
        }
        if (isCreate)
            createGoal(action);
    }

    // set data to save in shared preferences
    private void setData() {
        //Page One
        AddGoalPreferences.save(General.ID, String.valueOf(goal.getId()), TAG);
        AddGoalPreferences.save(General.NAME, goal.getName(), TAG);
        AddGoalPreferences.save(General.DESCRIPTION, goal.getDescription(), TAG);
        AddGoalPreferences.save(General.IMPACT, goal.getImpact(), TAG);
        AddGoalPreferences.save(General.IMAGE, String.valueOf(goal.getImage_id()), TAG);
        AddGoalPreferences.save(General.URL_IMAGE, goal.getImage(), TAG);
        //Page two
        AddGoalPreferences.save(General.GOAL_TYPE, String.valueOf(goal.getGoal_type()), TAG);
        AddGoalPreferences.save(General.COUNT, String.valueOf(goal.getQuantity()), TAG);
        AddGoalPreferences.save(General.UNITS, goal.getUnits(), TAG);
        AddGoalPreferences.save("frequency_sub_unit_days", goal.getFrequency_sub_unit_days(), TAG);
        AddGoalPreferences.save(General.FREQUENCY, goal.getFrequency(), TAG);
        AddGoalPreferences.save(General.START_DATE, goal.getStart_date(), TAG);
        AddGoalPreferences.save(General.END_DATE, goal.getEnd_date(), TAG);
        String[] time = goal.getStart_time().split(":");

        AddGoalPreferences.save(General.START_HOUR, "" + hrTime(Integer.parseInt(time[0]), goal.getFrequency()), TAG);
        AddGoalPreferences.save(General.START_MINUTE, time[1], TAG);

        AddGoalPreferences.save(General.START_TIME, hrTime(Integer.parseInt(time[0]), goal.getFrequency()) + ":" + time[1], TAG);
        AddGoalPreferences.save(General.FREQUENCY_UNIT, goal.getFrequency_unit(), TAG);
        AddGoalPreferences.save(General.FREQUENCY_SUB_UNIT_DAYS, goal.getFrequency_sub_unit_days(), TAG);
        //Page three
        AddGoalPreferences.save("notify_at", goal.getNotify_at(), TAG);
        AddGoalPreferences.save("notify_frequency", goal.getNotify_frequency(), TAG);
        AddGoalPreferences.save("occurrences", String.valueOf(goal.getOccurrences()), TAG);

    }

    private int hrTime(int time, String frequency) {
        if (frequency.equalsIgnoreCase(this.getResources().getString(R.string.hour))) {
            return time;
        }
        if (time > 12) {
            AddGoalPreferences.save(General.TIME_UNIT, "PM", TAG);
            return time - 12;
        } else {
            AddGoalPreferences.save(General.TIME_UNIT, "AM", TAG);
            return time;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //activityToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        if (nonSwipeableViewPagerAddGoalActivityPager != null)
            setBubble(nonSwipeableViewPagerAddGoalActivityPager.getCurrentItem());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(General.POSITION, "" + nonSwipeableViewPagerAddGoalActivityPager.getCurrentItem());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(General.POSITION)) {
                setBubble(Integer.parseInt(savedInstanceState.getString(General.POSITION)));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setBubble(0);
    }

    @Override
    public void setCountTime(String time, String unit) {
    }

    @Override
    public void setDurationTime(String time) {

    }
}
