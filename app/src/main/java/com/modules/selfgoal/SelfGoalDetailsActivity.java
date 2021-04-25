package com.modules.selfgoal;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.modules.selfgoal.werhope_self_goal.activity.GoalLogBookActivity;
import com.modules.selfgoal.werhope_self_goal.activity.HourlyDailyLogBookActivity;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.GoalDetailsInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.tasks.PerformReadTask;
import com.storage.preferences.AddGoalPreferences;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 22/03/2018
 * Last Modified on
 */

public class SelfGoalDetailsActivity extends AppCompatActivity implements GoalDetailsInterface {
    private static final String TAG = SelfGoalDetailsActivity.class.getSimpleName();
    @BindView(R.id.textview_activitytoolbar_title)
    TextView textViewActivityToolbarTitle;
    @BindView(R.id.linearlayout_toolbar_self_goal_details)
    LinearLayout linearLayoutToolbarSelfGoalDetails;
    @BindView(R.id.count_layout)
    LinearLayout linearLayoutCountGoalDetails;
    @BindView(R.id.imagebutton_log_book)
    ImageButton imagebuttonLogBook;
    @BindView(R.id.imagebutton_toolbar_submit)
    ImageButton imageButtonToolbarSubmit;
    @BindView(R.id.imagebutton_toolbar_edit)
    ImageButton imageButtonToolbarEdit;
    @BindView(R.id.imagebutton_toolbar_delete)
    ImageButton imageButtonToolbarDelete;
    @BindView(R.id.imageview_selfgoaldetailsactivity_image)
    ImageView imageViewSelfGoalDetailsActivityImage;
    @BindView(R.id.textview_selfgoaldetailsactivity_sentence)
    TextView textViewSelfGoalDetailsActivitySentence;
    @BindView(R.id.textview_selfgoaldetailsactivity_name)
    TextView textViewSelfGoalDetailsActivityName;
    @BindView(R.id.textview_selfgoaldetailsactivity_start_end_date)
    TextView textViewSelfGoalDetailsActivityStartEndDate;
    @BindView(R.id.textview_selfgoaldetailsactivity_description)
    TextView textViewSelfGoalDetailsActivityescription;
    @BindView(R.id.linear_msg_title)
    LinearLayout mLinearMsgTitle;
    @BindView(R.id.linearlayout_selfgoaldetailsactivity_sub)
    LinearLayout linearLayoutSelfGoalDetailsActivitySub;
    @BindView(R.id.linear_radio_yes_no)
    LinearLayout YesNoAnswer;

    @BindView(R.id.linear_radio_am_pm)
    LinearLayout AmPmAnswer;

    @BindView(R.id.linear_choose_date)
    LinearLayout ChooseDate;
    @BindView(R.id.linear_question)
    LinearLayout QuestionOnly;

    @BindView(R.id.textview_selfgoaldetailsactivity_frequency)
    TextView textViewSelfGoalDetailsActivityFrequency;
    @BindView(R.id.textview_selfgoaldetailsactivity_count)
    TextView textViewSelfGoalDetailsActivityCount;
    @BindView(R.id.textview_selfgoaldetailsactivity_occurrences)
    TextView textViewSelfGoalDetailsActivityOccurrences;
    @BindView(R.id.relativelaypout_selfgoaldetailsactivity_count)
    RelativeLayout relativeLaypoutSelfGoalDetailsActivityCount;
    @BindView(R.id.edittext_selfgoaldetailsactivity_count_text)
    EditText editTextSelfGoalDetailsActivityCountText;
    @BindView(R.id.textview_selfgoaldetailsactivity_count_time)
    TextView textViewSelfGoalDetailsActivityCountTime;
    @BindView(R.id.textview_selfgoaldetailsactivity_count_date)
    TextView textViewSelfGoalDetailsActivityCountDate;
    @BindView(R.id.linearlayout_selfgoaldetailsactivity_create_goal_count_item)
    LinearLayout linearLayoutSelfGoalDetailsActivityCreateGoalCountItem;
    @BindView(R.id.relativelayout_selfgoaldetailsactivity_milestone_footer)
    RelativeLayout relativeLayoutSelfGoalDetailsActivityMilestoneFooter;
    @BindView(R.id.textview_selfgoaldetailsactivity_milestone_count)
    TextView textViewSelfGoalDetailsActivityiMlestoneCount;
    @BindView(R.id.imageview_selfgoaldetailsactivity_milestone_next)
    AppCompatImageView imageViewSelfGoalDetailsActivityMilestoneNext;

    private Goal_ goal_;
    private ArrayList<String> countNameArray, countDateArray, countIdArray, countTimeArray;
    private ArrayList<HashMap<String, String>> milestoneList;

    private String count_date = "";
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String count_time = "00:00:00";
    private String old_date = "";
    private Toolbar activityToolbar;

    //Goal Assignment
    @BindView(R.id.goal_assignment_layout)
    LinearLayout goalAssignmentLayout;
    @BindView(R.id.submit_btn_layout)
    LinearLayout submitBtnLayout;
    @BindView(R.id.button_submit)
    Button submitButton;
    @BindView(R.id.date_selection_txt)
    TextView dateSelection;
    @BindView(R.id.set_question)
    TextView questionName;
    @BindView(R.id.am_pm_radio_group)
    RadioGroup amPmRadioGroup;
    @BindView(R.id.am_radio)
    RadioButton am;
    @BindView(R.id.pm_radio)
    RadioButton pm;
    @BindView(R.id.yes_no_radio_group)
    RadioGroup yesNoRadioGroup;
    @BindView(R.id.yes_radio)
    RadioButton yes;
    @BindView(R.id.no_radio)
    RadioButton no;
    private Boolean yesNo = false;
    private int mGoalCurrentStatus, mGoalStatus, mGoalType;
    private String mCurrentDate, mStartDate;
    private SimpleDateFormat sdfDate;
    private Date CurrentDate, StartDate;
    private String mAnswer, mGoalID, mMainGoalID, mRadioButtonNameTag, mAmPmAnswer;
    private String am_msg, pm_msg;
    private String currentTime;
    private ArrayList<Goal_> goalArrayList = new ArrayList<>();

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity", "NewApi"})
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

        setContentView(R.layout.activity_self_goal_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
        activityToolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(activityToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        activityToolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        activityToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        VectorDrawableCompat countDateDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_angle_arrow_down, textViewSelfGoalDetailsActivityCountDate.getContext().getTheme());
        textViewSelfGoalDetailsActivityCountDate.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, countDateDrawable, null);

        textViewActivityToolbarTitle.setText("");


        Preferences.initialize(getApplicationContext());
        AddGoalPreferences.initialize(getApplicationContext());
        AddGoalPreferences.clear(TAG);
        Intent data = getIntent();
        if (data != null && data.hasExtra(Actions_.MY_GOAL)) {
            goal_ = (Goal_) data.getSerializableExtra(Actions_.MY_GOAL);
//            mGoalCurrentStatus = 1;
            mGoalCurrentStatus = goal_.getGoal_current_status();
            mGoalStatus = goal_.getGoal_status();
            mGoalType = goal_.getGoal_type();
            mGoalID = String.valueOf(goal_.getId());
            am_msg = goal_.getAm_msg();
            pm_msg = goal_.getPm_msg();
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {
                mMainGoalID = goal_.getMain_goal_id();
            }

        } else {
            onBackPressed();
        }
        if (goal_ == null) {
            onBackPressed();
        }

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        count_date = mYear + "-" + (mMonth + 1) + "-" + mDay;

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            dateSelection.setText(GetTime.month_DdYyyy(count_date));
        } else {
            dateSelection.setText("");
        }


        countNameArray = new ArrayList<>();
        countDateArray = new ArrayList<>();
        countTimeArray = new ArrayList<>();
        countIdArray = new ArrayList<>();
        milestoneList = new ArrayList<>();

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {
            imagebuttonLogBook.setVisibility(View.VISIBLE);
        }

        yesNoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                mAnswer = String.valueOf(radioButton.getTag());
//                mRadioButtonNameTag = String.valueOf(radioButton.getText());
            }
        });
        mAnswer = String.valueOf(yes.getTag());

        amPmRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                mAmPmAnswer = String.valueOf(radioButton.getText());
                mRadioButtonNameTag = String.valueOf(radioButton.getText());

                if (mAmPmAnswer.equalsIgnoreCase("AM")) {
                    questionName.setText(am_msg);
                } else {
                    questionName.setText(pm_msg);
                }
                Log.e("AP_PM", mAmPmAnswer);
            }
        });
        mAmPmAnswer = String.valueOf(am.getText());
        Log.e("AP_PM", mAmPmAnswer);

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            AmPmAnswer.setVisibility(View.VISIBLE);
        } else {
            AmPmAnswer.setVisibility(View.GONE);
        }

        setData();


//        LocalTime localTime = LocalTime.now();
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//        System.out.println(localTime.format(dateTimeFormatter));
//        currentTime = localTime.format(dateTimeFormatter);
//        Log.e("currentTime",currentTime);
    }

    private void toggleCountLayout() {
        if (countNameArray.size() == 99) {
            relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
        } else {
            if (goal_.getGoal_status() == 1 || goal_.getGoal_status() == 2) {
                relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
            } else {
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {
                        if (mGoalType != General.GOAL_TYPE_GLOBULE && mGoalStatus == General.GOAL_STATUS_TYPE_RUNNING && mGoalCurrentStatus == General.GOAL_STATUS_TYPE_RUNNING) {
                            relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.VISIBLE);

                        } else {
                            relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                        }
                    } else {
                        if (mGoalStatus == General.GOAL_STATUS_TYPE_RUNNING && mGoalCurrentStatus == General.GOAL_STATUS_TYPE_RUNNING) {
                            relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.VISIBLE);
                        } else {
                            relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                        }
                    }
                } else {
                    relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                }
            }
        }
    }


    // all layout and data will be set in this function
    private void setData() {
        textViewSelfGoalDetailsActivityName.setText(goal_.getName());

        String description = "-NA-";
        if (goal_.getDescription().trim().length() > 0) {
            description = goal_.getDescription();
        }
        textViewSelfGoalDetailsActivityescription.setText(description);


        String start_date = GetTime.month_DdYyyy(goal_.getStart_date());
        String end_date = GetTime.month_DdYyyy(goal_.getEnd_date());
        textViewSelfGoalDetailsActivityStartEndDate.setText(start_date + " to " + end_date);

        sdfDate = new SimpleDateFormat("MMM dd, yyyy");
        Date now = new Date();
        mCurrentDate = sdfDate.format(now);

//        Date str = new Date(start_date);
//        mStartDate = sdfDate.format(str);

        try {
            CurrentDate = sdfDate.parse(mCurrentDate);
            StartDate = sdfDate.parse(start_date);

            Log.e("CurrentDate", "" + CurrentDate);
            Log.e("StartDate", "" + StartDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }


//        Log.e("CurrentDate", "" + mCurrentDate);

        String frequency = goal_.getFrequency();
        if (frequency.length() <= 0) {
            textViewSelfGoalDetailsActivityFrequency.setText("-NA_");
        } else {
            textViewSelfGoalDetailsActivityFrequency.setText(frequency);
        }

        if (frequency.equalsIgnoreCase(this.getResources().getString(R.string.hour)) || frequency.equalsIgnoreCase("hour")) {
            textViewSelfGoalDetailsActivityCountTime.setVisibility(View.VISIBLE);
        } else {
            textViewSelfGoalDetailsActivityCountTime.setVisibility(View.GONE);
        }

        int count = goal_.getQuantity();
        if (frequency.length() <= 0) {
            textViewSelfGoalDetailsActivityCount.setText("-NA-");
        } else {
            textViewSelfGoalDetailsActivityCount.setText(String.valueOf(count));
        }
        textViewSelfGoalDetailsActivityOccurrences.setText(String.valueOf(goal_.getOccurrences()));

//        if (goal_.getGoal_type() == 1 || goal_.getGoal_type() == 2) {

//            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))
//                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025))) {
//                if (goal_.getFrequency().equalsIgnoreCase("Weekly")) {
//                    linearLayoutSelfGoalDetailsActivitySub.setVisibility(View.VISIBLE);
//                } else {
//                    linearLayoutSelfGoalDetailsActivitySub.setVisibility(View.GONE);
//                }
//            } else {
//                linearLayoutSelfGoalDetailsActivitySub.setVisibility(View.VISIBLE);
//                linearLayoutCountGoalDetails.setVisibility(View.GONE);
//            }

//        }

        if (goal_.getGoal_status() == 1 || goal_.getGoal_status() == 2) { //0- running, 1- completed, 2- miss out
            relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
            imageButtonToolbarSubmit.setVisibility(View.GONE);
            imageButtonToolbarEdit.setVisibility(View.GONE);
        } else if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            imageButtonToolbarSubmit.setVisibility(View.GONE);
            imageButtonToolbarEdit.setVisibility(View.GONE);
        }
       /* else {
          relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.VISIBLE);
            if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.VISIBLE);
            } else {
                relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                linearLayoutSelfGoalDetailsActivitySub.setVisibility(View.GONE);
            }
        }*/

        if (goal_.getIs_dashboard() == 1) {
            imageButtonToolbarDelete.setVisibility(View.GONE);
        }

        String[] time = goal_.getStart_time().split(":");
        AddGoalPreferences.save(General.NAME, goal_.getName(), TAG);
        AddGoalPreferences.save(General.START_MINUTE, time[1], TAG);
        AddGoalPreferences.save(General.START_HOUR, time[0], TAG);
        textViewSelfGoalDetailsActivitySentence.setText(CreateSentence_.make_sentence(String.valueOf(goal_.getType()),
                String.valueOf(goal_.getQuantity()), goal_.getUnits(), frequency,
                goal_.getStart_date(), goal_.getEnd_date(),
                goal_.getFrequency_unit(), String.valueOf(goal_.getOccurrences())));

        //if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
        getList();
        //}


        //Goal assignment changes

        //if (goal_.getGoal_type() == 2) {
        /*if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025))) && goal_.getGoal_type() == 2) {
            imageButtonToolbarEdit.setVisibility(View.GONE);
            textViewSelfGoalDetailsActivityescription.setVisibility(View.GONE);
            relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
            // relativeLayoutSelfGoalDetailsActivityMilestoneFooter.setVisibility(View.GONE);
//            goalAssignmentLayout.setVisibility(View.VISIBLE);
//            submitBtnLayout.setVisibility(View.VISIBLE);

            if (goal_.getThumb() != null) {
                Glide.with(getApplicationContext())
                        .load(goal_.getThumb())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_goal_placeholder)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                        .into(imageViewSelfGoalDetailsActivityImage);
            }

            questionName.setText(goal_.getDescription());

        } else {
            // relativeLayoutSelfGoalDetailsActivityMilestoneFooter.setVisibility(View.GONE);

            if (goal_.getImage() != null) {
                Glide.with(getApplicationContext())
                        .load(goal_.getImage())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_goal_placeholder)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                        .into(imageViewSelfGoalDetailsActivityImage);
            }
        }*/

        /* This Below condition is for Count layout for Show and hide */

        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            linearLayoutToolbarSelfGoalDetails.setVisibility(View.VISIBLE);
            relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.VISIBLE);
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {
                if (mGoalType != General.GOAL_TYPE_GLOBULE && mGoalStatus == General.GOAL_STATUS_TYPE_RUNNING && mGoalCurrentStatus == General.GOAL_STATUS_TYPE_RUNNING) {
                    //if (StartDate.before(CurrentDate)) {
                    if (CurrentDate.before(StartDate)) {
                        mLinearMsgTitle.setVisibility(View.VISIBLE);
                        relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                    } else {
                        relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.VISIBLE);
                        mLinearMsgTitle.setVisibility(View.GONE);
                    }
                } else {
                    relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                }
            } else {
                if (mGoalStatus == General.GOAL_STATUS_TYPE_COMPLETE && mGoalCurrentStatus == General.GOAL_STATUS_TYPE_RUNNING) {
                    relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.VISIBLE);
                } else {
                    relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                }
            }

            if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031)))
                    && mGoalType == General.GOAL_TYPE_GLOBULE) {
                goalAssignmentLayout.setVisibility(View.VISIBLE);
                if (mGoalStatus == General.GOAL_STATUS_TYPE_RUNNING && mGoalCurrentStatus == General.GOAL_STATUS_TYPE_RUNNING) {
                    YesNoAnswer.setVisibility(View.VISIBLE);
                    if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                        {
                            goalAssignmentLayout.setVisibility(View.VISIBLE);
                            if (mGoalStatus == General.GOAL_STATUS_TYPE_RUNNING && mGoalCurrentStatus == General.GOAL_STATUS_TYPE_RUNNING) {
                                YesNoAnswer.setVisibility(View.VISIBLE);
                                if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                                    AmPmAnswer.setVisibility(View.VISIBLE);
                                } else {
                                    AmPmAnswer.setVisibility(View.GONE);
                                }
                                ChooseDate.setVisibility(View.VISIBLE);
                                submitBtnLayout.setVisibility(View.VISIBLE);
                            } else {
                                YesNoAnswer.setVisibility(View.GONE);
                                AmPmAnswer.setVisibility(View.GONE);
                                ChooseDate.setVisibility(View.GONE);
                                submitBtnLayout.setVisibility(View.GONE);
                                QuestionOnly.setVisibility(View.VISIBLE);
                            }

                            if (CurrentDate.before(StartDate)) {
//                    ChooseDate.setVisibility(View.GONE);
//                    submitBtnLayout.setVisibility(View.GONE);
//                    mLinearMsgTitle.setVisibility(View.VISIBLE);

//                    ChooseDate.setVisibility(View.VISIBLE);
//                    submitBtnLayout.setVisibility(View.VISIBLE);
                                mLinearMsgTitle.setVisibility(View.VISIBLE);
                            } else {
//                    ChooseDate.setVisibility(View.VISIBLE);
//                    submitBtnLayout.setVisibility(View.VISIBLE);
//                    mLinearMsgTitle.setVisibility(View.GONE);

//                    ChooseDate.setVisibility(View.GONE);
//                    submitBtnLayout.setVisibility(View.GONE);
                                mLinearMsgTitle.setVisibility(View.GONE);

                            }
                            linearLayoutSelfGoalDetailsActivitySub.setVisibility(View.GONE);
                        }
                        AmPmAnswer.setVisibility(View.VISIBLE);
                    } else {
                        AmPmAnswer.setVisibility(View.GONE);
                    }
                    ChooseDate.setVisibility(View.VISIBLE);
                    submitBtnLayout.setVisibility(View.VISIBLE);
                } else {
                    YesNoAnswer.setVisibility(View.GONE);
                    AmPmAnswer.setVisibility(View.GONE);
                    ChooseDate.setVisibility(View.GONE);
                    submitBtnLayout.setVisibility(View.GONE);
                    QuestionOnly.setVisibility(View.VISIBLE);
                }

                if (CurrentDate.before(StartDate)) {
//                    ChooseDate.setVisibility(View.GONE);
//                    submitBtnLayout.setVisibility(View.GONE);
//                    mLinearMsgTitle.setVisibility(View.VISIBLE);

//                    ChooseDate.setVisibility(View.VISIBLE);
//                    submitBtnLayout.setVisibility(View.VISIBLE);
                    mLinearMsgTitle.setVisibility(View.VISIBLE);
                } else {
//                    ChooseDate.setVisibility(View.VISIBLE);
//                    submitBtnLayout.setVisibility(View.VISIBLE);
//                    mLinearMsgTitle.setVisibility(View.GONE);

//                    ChooseDate.setVisibility(View.GONE);
//                    submitBtnLayout.setVisibility(View.GONE);
                    mLinearMsgTitle.setVisibility(View.GONE);

                }
                linearLayoutSelfGoalDetailsActivitySub.setVisibility(View.GONE);
            } else {
                goalAssignmentLayout.setVisibility(View.GONE);
                linearLayoutSelfGoalDetailsActivitySub.setVisibility(View.VISIBLE);
                if (mGoalStatus == General.GOAL_STATUS_TYPE_RUNNING && mGoalCurrentStatus == General.GOAL_STATUS_TYPE_RUNNING) {
                    relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.VISIBLE);
                } else {
                    relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                }

            }
        } else {
            linearLayoutToolbarSelfGoalDetails.setVisibility(View.GONE);
            relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
        }


        /*For question Answer view show hide condition below */

        if (goal_.getThumb() != null) {
            Glide.with(getApplicationContext())
                    .load(goal_.getThumb())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_goal_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(imageViewSelfGoalDetailsActivityImage);
        }

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            if (mAmPmAnswer.equalsIgnoreCase("AM")) {
                questionName.setText(am_msg);
            } else {
                questionName.setText(pm_msg);
            }
        } else {
            questionName.setText(goal_.getDescription());
        }

//        questionName.setText(goal_.getDescription());

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))
                        && mGoalType == General.GOAL_TYPE_GLOBULE) {
            textViewSelfGoalDetailsActivityescription.setVisibility(View.GONE);
        } else {
            textViewSelfGoalDetailsActivityescription.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.imagebutton_log_book, R.id.imagebutton_toolbar_submit, R.id.imagebutton_toolbar_edit, R.id.date_selection_txt, R.id.button_submit,
            R.id.imagebutton_toolbar_delete, R.id.textview_selfgoaldetailsactivity_count_time,
            R.id.textview_selfgoaldetailsactivity_count_date, R.id.imageview_selfgoaldetailsactivity_count_add, R.id.imageview_selfgoaldetailsactivity_milestone_next})

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.imagebutton_log_book:

                if (goal_.getFrequency().equalsIgnoreCase("hour")) {
                    Intent logBook = new Intent(getApplicationContext(), HourlyDailyLogBookActivity.class);
                    logBook.putExtra("map", goal_);
                    startActivity(logBook);
                    finish();
                } else {
                    Intent logBook = new Intent(getApplicationContext(), GoalLogBookActivity.class);
                    logBook.putExtra("map", goal_);
                    startActivity(logBook);
                    finish();
                }
                break;

            case R.id.imagebutton_toolbar_submit:
                answerConfirmation();
                break;

            case R.id.imagebutton_toolbar_edit:
                Bundle bundleAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_one, R.anim.animation_two).toBundle();
                Intent addIntent = new Intent(getApplicationContext(), AddGoalActivity.class);
                addIntent.putExtra("map", goal_);
                addIntent.putExtra(General.TITLE, getApplicationContext().getResources().getString(R.string.edit));
                startActivity(addIntent, bundleAnimation);
                finish();
                break;

            case R.id.imagebutton_toolbar_delete:
                if (goal_.getIs_dashboard() != 1)
                    deleteConfirmation();
                break;

            case R.id.textview_selfgoaldetailsactivity_count_time:
                openDialog();
                break;

            case R.id.textview_selfgoaldetailsactivity_count_date:
                count_date = null;
                textViewSelfGoalDetailsActivityCountDate.setHint(getApplicationContext().getResources().getString(R.string.date));
                DatePickerDialog milestoneDatePicker = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                count_date = sYear + "-" + checkDigit(sMonth) + "-" + checkDigit(sDay);
                                try {
                                    if (Compare.dateInRange(goal_.getStart_date(), goal_.getEnd_date(), count_date)) {
                                        textViewSelfGoalDetailsActivityCountDate.setText(GetTime.month_DdYyyy(count_date));
                                        textViewSelfGoalDetailsActivityCountDate.setTextColor(getApplicationContext()
                                                .getResources().getColor(R.color.text_color_primary));
                                    } else {
                                        count_date = null;
                                        textViewSelfGoalDetailsActivityCountDate.setHint(getApplicationContext().getResources().getString(R.string.date));
                                        ShowToast.toast("Date out of range", getApplicationContext());
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_WEEK, -6);
                milestoneDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                milestoneDatePicker.show();
                break;

            case R.id.imageview_selfgoaldetailsactivity_count_add:
                String count = editTextSelfGoalDetailsActivityCountText.getText().toString().trim();
                if (count.length() <= 0) {
                    editTextSelfGoalDetailsActivityCountText.setError("Enter Valid Count");
                    return;
                }
                if (count_date == null || count_date.length() <= 0) {
                    ShowToast.toast("Enter Valid Date", getApplicationContext());
                    return;
                }
                if (goal_.getFrequency().equalsIgnoreCase(this.getResources().getString(R.string.hour))
                        || goal_.getFrequency().equalsIgnoreCase("hour")) {
                    if (count_time == null || count_time.trim().length() <= 0 ||
                            count_time.trim().equalsIgnoreCase("00:00:00")) {
                        ShowToast.toast("Enter Valid Time", getApplicationContext());
                        return;
                    }
                }

                if (old_date.equals(GetTime.month_DdYyyy(count_date))) {
                    editTextSelfGoalDetailsActivityCountText.setText("");
                    textViewSelfGoalDetailsActivityCountDate.setText("");
                    textViewSelfGoalDetailsActivityCountTime.setText("");
                    ShowToast.toast("Add only one goal for same date", getApplicationContext());
                } else {
                    addLayout(count, count_date, false, "0", "");
                }

                break;

            case R.id.imageview_selfgoaldetailsactivity_milestone_next:
                openMilestoneFragment();
                break;


            case R.id.date_selection_txt:
                count_date = null;
                dateSelection.setHint(getApplicationContext().getResources().getString(R.string.date));
                DatePickerDialog milestoneDatePicker1 = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                count_date = sYear + "-" + checkDigit(sMonth) + "-" + checkDigit(sDay);
                                try {
                                    if (Compare.dateInRange(goal_.getStart_date(), goal_.getEnd_date(), count_date)) {
                                        //show_count_date = checkDigit(sMonth) + "-" + checkDigit(sDay) + "-" + sYear;
                                        dateSelection.setText(GetTime.month_DdYyyy(count_date));
                                        dateSelection.setTextColor(getApplicationContext()
                                                .getResources().getColor(R.color.text_color_primary));
                                    } else {
                                        count_date = null;
                                        dateSelection.setHint(getApplicationContext().getResources().getString(R.string.date));
                                        ShowToast.toast("Date out of range", getApplicationContext());
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
               /* Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_WEEK, -6);
                milestoneDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());*/

                milestoneDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
                if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                    //For senjam only allowed to input for current date
                    milestoneDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis());
                }
                milestoneDatePicker1.show();
                break;


            case R.id.button_submit:
                String count1 = dateSelection.getText().toString().trim();
                if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                    count1 = mAnswer;
                }


                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {

                    dailyDosingAnswerAPICalled(mAnswer, mGoalID, count_date, mMainGoalID, mAmPmAnswer, count1);
//                    addLayout(count1, count_date, false);
//                    yes.setChecked(false);
//                    no.setChecked(false);
//                    am.setChecked(false);
//                    pm.setChecked(false);
//                    relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);

                } else {
                    if (count1.length() <= 0) {
                        dateSelection.setError("Enter Valid Count");
                        return;
                    }
                    if (count_date == null || count_date.length() <= 0) {
                        ShowToast.toast("Enter Valid Date", getApplicationContext());
                        return;
                    }
                    if (goal_.getFrequency().equalsIgnoreCase(this.getResources().getString(R.string.hour))
                            || goal_.getFrequency().equalsIgnoreCase("hour")) {
                        if (count_time == null || count_time.trim().length() <= 0 ||
                                count_time.trim().equalsIgnoreCase("00:00:00")) {
                            ShowToast.toast("Enter Valid Time", getApplicationContext());
                            return;
                        }
                    }


                    addLayout(count1, count_date, false, "0", "");
                    yes.setChecked(true);
                    no.setChecked(false);
                    relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                }

                break;
        }
    }

    /*APi Call for Add Answer for Daily Dosing Compliance*/
    @SuppressLint({"SetTextI18n", "LongLogTag"})
    private void dailyDosingAnswerAPICalled(String answerID, String goalID, String onDate, String mainGoalID, String AM_PM, String count1) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_ANSWER_SENJAM);
        requestMap.put(General.ANSWER, answerID);
        requestMap.put(General.GOAL_ID, goalID);
        requestMap.put(General.ON_DATE, onDate);
        requestMap.put(General.GOAL_AM_PM, AM_PM);
        requestMap.put(General.MAIN_GOAL_ID, mainGoalID);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("PdailyDosingAnswerResponse", "" + response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonDailyDosing = jsonObject.getAsJsonObject(Actions_.ADD_ANSWER_SENJAM);
                    if (jsonDailyDosing.get(General.STATUS).getAsInt() == 1) {
                        countIdArray.add(jsonDailyDosing.get(General.ID).toString());
                        countNameArray.add(answerID);
                        countDateArray.add(onDate);
//                        Date dt = new Date(onDate);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                        String time1 = sdf.format(new Date());
                        countTimeArray.add(time1);
                        addLayout(count1, count_date, true, jsonDailyDosing.get(General.ID).toString(), time1);
                        yes.setChecked(true);
                        no.setChecked(false);
                        am.setChecked(true);
                        pm.setChecked(false);
                        relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
                        Toast.makeText(this, jsonDailyDosing.get(General.MSG).getAsString(), Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, jsonDailyDosing.get(General.ERROR).getAsString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Internal Error Occur..!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
//                Log.e("errorDailyDosingActivity", ""+e.getMessage());
            }
        }
    }

    private void addLayout(String milestone, String date, boolean isAdd, String countId, String time) {
        //This is Layout of Count list (Yes/No Or count)

        final View layout2 = LayoutInflater.from(this).inflate(R.layout.self_goal_details_milestone_item, linearLayoutSelfGoalDetailsActivityCreateGoalCountItem, false);
        TextView nameTag = (TextView) layout2.findViewById(R.id.selfgoaldetails_milestone_item_name);
        TextView dateTag = (TextView) layout2.findViewById(R.id.selfgoaldetails_milestone_item_date); //Date lable in count list
        ImageView cancel = (ImageView) layout2.findViewById(R.id.selfgoaldetails_milestone_item_close); //Close icon in count list
        if (goal_.getGoal_status() == 1 || goal_.getGoal_status() == 2) {
            cancel.setVisibility(View.GONE);
            cancel.setEnabled(false);
        }
/*
        if (goal_.getGoal_status() == 0){
            cancel.setVisibility(View.VISIBLE);
        }else {
            cancel.setVisibility(View.GONE);
        }*/

        int result = 0;
        if (!isAdd) {
            //Add count webservice after loading screen. this will not call on screen load
            result = addCount(milestone, date);
        }
        //if (result == 1 || isAdd) {
        if (result > 0 || isAdd) {
            if (!isAdd) {
                countNameArray.add(milestone);
                countDateArray.add(GetTime.month_DdYyyy(date));
            }
            //cancel.setTag(countDateArray.size() - 1);
            if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                cancel.setTag(Integer.parseInt(countId));
            } else {
                if (result > 0) {
                    cancel.setTag(result);
                } else {
                    cancel.setTag(Integer.parseInt(countId));
                }

            }

            yesNo = yes.isChecked();
//            if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
//                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025)) ||
//                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
//                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031)))
//                    && goal_.getGoal_type() == 2) {

            if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031)))) {
                if (milestone.equalsIgnoreCase("1")) {
                    nameTag.setText("Yes");
                } else {
                    nameTag.setText("No");
                }
            } else {
                nameTag.setText(milestone);
            }

            if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                dateTag.setText(GetTime.month_DdYyyy(date) + " " + getTime(time));
            } else {
                dateTag.setText(GetTime.month_DdYyyy(date));
            }
            old_date = dateTag.getText().toString();
            linearLayoutSelfGoalDetailsActivityCreateGoalCountItem.addView(layout2);
            toggleCountLayout();
            editTextSelfGoalDetailsActivityCountText.setText("");
            textViewSelfGoalDetailsActivityCountDate.setText("");
            textViewSelfGoalDetailsActivityCountTime.setText("");
            count_time = "00:00:00";
            if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {

            } else {
                count_date = "";
                dateSelection.setText("");
            }

            textViewSelfGoalDetailsActivityCountDate.setHint(getApplicationContext().getResources().getString(R.string.date));
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int position = (Integer) v.getTag();
                int IdofInput = (Integer) v.getTag();
                int position = countIdArray.indexOf(String.valueOf(IdofInput));

                int result = removeCount(countIdArray.get(position));
                if (result == 1) {
                    countNameArray.remove(position);
                    countDateArray.remove(position);
                    countIdArray.remove(position);
                    linearLayoutSelfGoalDetailsActivityCreateGoalCountItem.removeViewAt(position);

//                    if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
//                        relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
//                        YesNoAnswer.setVisibility(View.VISIBLE);
//                        AmPmAnswer.setVisibility(View.VISIBLE);
//                        ChooseDate.setVisibility(View.VISIBLE);
//                        submitBtnLayout.setVisibility(View.VISIBLE);
//                        getSelfGoalUpdatedData();
//                    } else {
//                        toggleCountLayout();
//                    }
                    getSelfGoalUpdatedData();
                    toggleCountLayout();

                    old_date = "";
                }
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void getSelfGoalUpdatedData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SELF_GOAL);
        requestMap.put(General.ID, String.valueOf(goal_.getId()));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_DETAILS_CALL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, SelfGoalDetailsActivity.this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, SelfGoalDetailsActivity.this, this);
                Log.e("getSelfGoalUpdatedDataResponse", "" + response);
                if (response != null) {
//                    JSONObject jsonObject = new JSONObject(response);
                    goalArrayList = SelfGoal_.parseSpams(response, Actions_.SELF_GOAL, getApplicationContext(), TAG);
                    if (goalArrayList.size() > 0) {
                        if (goalArrayList.get(0).getStatus() == 1) {

                            mGoalStatus = goalArrayList.get(0).getGoal_status();
                            mGoalCurrentStatus = goalArrayList.get(0).getGoal_current_status();
                            mGoalType = goalArrayList.get(0).getGoal_type();
                            mGoalID = String.valueOf(goalArrayList.get(0).getId());
                            am_msg = goalArrayList.get(0).getAm_msg();
                            pm_msg = goalArrayList.get(0).getPm_msg();
                            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {
                                mMainGoalID = goalArrayList.get(0).getMain_goal_id();
                            }

                            setData();

//                            if (mGoalStatus == General.GOAL_STATUS_TYPE_RUNNING && mGoalCurrentStatus == General.GOAL_STATUS_TYPE_RUNNING) {
//                                relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
//                                YesNoAnswer.setVisibility(View.VISIBLE);
//                                AmPmAnswer.setVisibility(View.VISIBLE);
//                                ChooseDate.setVisibility(View.VISIBLE);
//                                submitBtnLayout.setVisibility(View.VISIBLE);
////                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
////                                String time1 = sdf.format(new Date());
////                                addLayout("0", count_date, true, String.valueOf(goalArrayList.get(0).getId()), getTime(time1));
//                                yes.setChecked(true);
//                                no.setChecked(false);
//                                am.setChecked(true);
//                                pm.setChecked(false);
//                                relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
//                            }else {
//                                relativeLaypoutSelfGoalDetailsActivityCount.setVisibility(View.GONE);
//                                YesNoAnswer.setVisibility(View.GONE);
//                                AmPmAnswer.setVisibility(View.GONE);
//                                ChooseDate.setVisibility(View.GONE);
//                                submitBtnLayout.setVisibility(View.GONE);
//                                QuestionOnly.setVisibility(View.VISIBLE);
//                            }
                            return;
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ShowToast.internalErrorOccurred(getApplicationContext());
    }


    private void getList() {
        boolean isCount = false;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.MILESTONE_LIST);
        requestMap.put(General.ID, String.valueOf(goal_.getId()));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, SelfGoalDetailsActivity.this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, SelfGoalDetailsActivity.this, this);
                Log.e("Response", "" + response);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Actions_.MILESTONE_LIST)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(General.COUNT_LIST);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            if (object.getInt(General.STATUS) == 1) {
                                isCount = true;
                                countIdArray.add(object.getString(General.ID));
//                                if (object.names().equals(General.COUNT)){
//                                    countNameArray.add(object.getString(General.COUNT));
//                                }else {
//                                    countNameArray.add("");
//                                }
                                countNameArray.add(object.getString(General.COUNT));
                                countDateArray.add(object.getString("on_date"));
                                countTimeArray.add(object.getString("on_time"));
                            }
                        }

                        JSONArray milestoneArray = jsonObject.getJSONArray(Actions_.MILESTONE_LIST);
                        for (int i = 0; i < milestoneArray.length(); i++) {
                            JSONObject object = milestoneArray.getJSONObject(i);
                            if (object.getInt(General.STATUS) == 1) {
                                String id = object.getString(General.ID);
                                String name = object.getString(General.NAME);
                                String check = object.getString(General.CHECK);
                                String date = object.getString("date");

                                HashMap<String, String> map = new HashMap<>();
                                map.put(General.ID, id);
                                map.put(General.CHECK, check);
                                if (check.equalsIgnoreCase("1")) {
                                    map.put(General.IS_SELECTED, "1");
                                } else {
                                    map.put(General.IS_SELECTED, "0");
                                }
                                map.put(General.NAME, name);
                                map.put(General.START_DATE, date);

                                milestoneList.add(map);
                            }
                        }
                    }
                    if (milestoneList != null && milestoneList.size() > 0) {
                        relativeLayoutSelfGoalDetailsActivityMilestoneFooter.setVisibility(View.VISIBLE);
                        textViewSelfGoalDetailsActivityiMlestoneCount.setText(String.valueOf(milestoneList.size()));
                        imageViewSelfGoalDetailsActivityMilestoneNext.setEnabled(true);
                    } else {
                        imageViewSelfGoalDetailsActivityMilestoneNext.setEnabled(false);
                        relativeLayoutSelfGoalDetailsActivityMilestoneFooter.setVisibility(View.GONE);
                    }
                    if (isCount && CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                        for (int i = 0; i < countIdArray.size(); i++) {
                            addLayout(countNameArray.get(i), countDateArray.get(i), true, countIdArray.get(i), countTimeArray.get(i));
                        }
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ShowToast.internalErrorOccurred(getApplicationContext());
    }

    public String getTime(String time) {
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _12HourSDF.format(_24HourDt);
    }

    private int addCount(String count, String date) {
        String time = ""; //convert time 12 hrs format to 24 hrs format
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm:ss a");
            time = textViewSelfGoalDetailsActivityCountTime.getText().toString().trim();
            Date date1 = parseFormat.parse(time);
            time = displayFormat.format(date1);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_COUNT);
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ID, String.valueOf(goal_.getId()));

        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025))) && goal_.getGoal_type() == 2) {
            requestMap.put("on_date", date);
            requestMap.put("on_time", "00:00:00");
            requestMap.put("answer", "1");
        } else {
            requestMap.put("on_date", date);
            requestMap.put("on_time", time);
            requestMap.put("answer", count);
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, SelfGoalDetailsActivity.this, this);
        String response;
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, SelfGoalDetailsActivity.this, this);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    return new CountOperation().execute(response).get();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ShowToast.internalErrorOccurred(getApplicationContext());
        return 11;
    }

    private int removeCount(String id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.REMOVE_COUNT);
        requestMap.put(General.ID, id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, SelfGoalDetailsActivity.this, this);
        String response;
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, SelfGoalDetailsActivity.this, this);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    return new CountOperation().execute(response).get();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 11;
    }

    private void deleteConfirmation() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        subTitle.setText(this.getResources().getString(R.string.delete_goal_confirmation));
        title.setText(this.getResources().getString(R.string.action_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteGoal();
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById(R.id.delete_confirmation_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteGoal() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.REMOVE_GOAL);
        requestMap.put(General.ID, String.valueOf(goal_.getId()));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, SelfGoalDetailsActivity.this, this);
        String response;
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, SelfGoalDetailsActivity.this, this);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    int result = new CountOperation().execute(response).get();
                    if (result == 1) {
                        ShowToast.successful("Successful", getApplicationContext());
                        onBackPressed();
                    } else {
                        ShowToast.toast("Action Failed", getApplicationContext());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setCountTime(String time, String unit) {
        count_time = time;
        textViewSelfGoalDetailsActivityCountTime.setText(time.trim() + " " + unit);
    }

    @Override
    public void setDurationTime(String time) {

    }

    private class CountOperation extends AsyncTask<String, Void, Integer> {
        boolean isAdd = false;
        boolean isRemove = false;
        String id = "0";
        String error = "";

        @Override
        protected Integer doInBackground(String... params) {
            try {
                String response = params[0];
                if (response == null) {
                    return 11;
                } else {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Actions_.ADD_COUNT)) {
                        isAdd = true;
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.ADD_COUNT);
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.has(General.ERROR)) {
                            error = object.getString(General.ERROR);
                        }
                        if (object.has(General.ID)) {
                            id = object.getString(General.ID);
                            countIdArray.add(id);
                            //return object.getInt(General.STATUS);
                            return object.getInt(General.ID); //Successfully added & return generated ID
                        } else {
                            //return object.getInt(General.STATUS);
                            return 0; //Error in adding count
                        }
                    } else if (jsonObject.has(Actions_.REMOVE_COUNT)) {
                        isAdd = false;
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.REMOVE_COUNT);
                        JSONObject object = jsonArray.getJSONObject(0);
                        return object.getInt(General.STATUS);
                    } else if (jsonObject.has(Actions_.REMOVE_GOAL)) {
                        isRemove = true;
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.REMOVE_GOAL);
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.has(General.STATUS)) {
                            id = object.getString(General.STATUS);
                            if (object.has(General.ERROR)) {
                                error = object.getString(General.ERROR);
                            }
                            return object.getInt(General.STATUS);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 1:
                    //ShowToast.successful("Successful", getApplicationContext());
                    dateSelection.setText("");
                    if (isAdd) {
                        //countIdArray.add(id);
                    }
                    if (isRemove) {
                        onBackPressed();
                    }
                    break;
                case 2:
                    ShowToast.toast("Action Failed", getApplicationContext());
                    break;
                case 3:
                    ShowToast.toast(error, getApplicationContext());
                    break;
                case 11:
                    ShowToast.internalErrorOccurred(getApplicationContext());
                    break;
                case 12:
                    ShowToast.networkError(getApplicationContext());
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @SuppressLint("CommitTransaction")
    private void openDialog() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment frag = getFragmentManager().findFragmentByTag(General.TIMESTAMP);
        if (frag != null) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.addToBackStack(null);
        TimeHourPickerDialogFragment dialogFrag = new TimeHourPickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(General.FREQUENCY, "details");
        bundle.putString(General.DESCRIPTION, "1");
        bundle.putBoolean(General.IS_NOW_VISIBLE, true);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager(), General.TIMESTAMP);
    }

    private void answerConfirmation() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.self_goal_details_complete_goal_dialog_layout);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageViewSelfGoalDetailsCompleteGoalDialogClose = (ImageView) dialog.findViewById(R.id.imageview_selfgoaldetails_completegoaldialog_close);
        final CheckedTextView checkboxSelfGoalDetailsCompleteGoalDialogReschedule = (CheckedTextView) dialog.findViewById(R.id.checkbox_selfgoaldetails_completegoaldialog_reschedule);
        Button buttonSelfGoalDetailsCompleteGoalDialogYes = (Button) dialog.findViewById(R.id.button_selfgoaldetails_completegoaldialog_yes);
        Button buttonSelfGoalDetailsCompleteGoalDialogNo = (Button) dialog.findViewById(R.id.button_selfgoaldetails_completegoaldialog_no);

        checkboxSelfGoalDetailsCompleteGoalDialogReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkboxSelfGoalDetailsCompleteGoalDialogReschedule.isChecked()) {
                    checkboxSelfGoalDetailsCompleteGoalDialogReschedule.setChecked(false);
                } else {
                    checkboxSelfGoalDetailsCompleteGoalDialogReschedule.setChecked(true);
                }
            }
        });

        imageViewSelfGoalDetailsCompleteGoalDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonSelfGoalDetailsCompleteGoalDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                completeGoal(checkboxSelfGoalDetailsCompleteGoalDialogReschedule.isChecked());
            }
        });

        buttonSelfGoalDetailsCompleteGoalDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void completeGoal(boolean isReschedule) {
        int result = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.COMPLETE_GOAL);
        requestMap.put(General.ID, String.valueOf(goal_.getId()));
        requestMap.put(General.GOAL_TYPE, String.valueOf(goal_.getGoal_type()));
        requestMap.put(General.STATUS, "1");
        requestMap.put("step", "1");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, SelfGoalDetailsActivity.this, this);
        String response;
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, SelfGoalDetailsActivity.this, this);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Actions_.COMPLETE_GOAL)) {
                        //{"complete_goal":[{"msg":"The record has been updated successfully.","status":1}]}
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.COMPLETE_GOAL);
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.has(General.STATUS)) {
                            result = object.getInt(General.STATUS);
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        switch (result) {
            case 1:
                ShowToast.successful(this.getResources().getString(R.string.successful), getApplicationContext());
                if (isReschedule) {
                    Bundle bundleAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_one, R.anim.animation_two).toBundle();
                    Intent addIntent = new Intent(getApplicationContext(), AddGoalActivity.class);
                    addIntent.putExtra("map", goal_);
                    addIntent.putExtra(General.TITLE, getApplicationContext().getResources().getString(R.string.reschedule_goal));
                    startActivity(addIntent, bundleAnimation);
                    finish();
                } else {
                    onBackPressed();
                }
                break;
            case 2:
                ShowToast.toast(this.getResources().getString(R.string.action_failed), getApplicationContext());
                break;
            case 11:
                ShowToast.internalErrorOccurred(getApplicationContext());
                break;
            case 12:
                ShowToast.networkError(getApplicationContext());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Make network call to read entry
        int status = PerformReadTask.readAlert("" + goal_.getId(), General.SELFGOAL, TAG, getApplicationContext(), this);
        activityToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    @SuppressLint("CommitTransaction")
    private void openMilestoneFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment frag = getFragmentManager().findFragmentByTag(General.MILESTONE);
        if (frag != null) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment dialogFrag = new MilestoneDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(General.MILESTONE, milestoneList);
        bundle.putString(General.GOAL_STATUS, String.valueOf(goal_.getStatus()));
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), General.MILESTONE);
    }
}