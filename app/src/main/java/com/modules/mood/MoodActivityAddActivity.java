package com.modules.mood;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.BaseActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.MoodStats_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MoodParser_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;
import com.utils.MessageUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import utils.Utils;

/**
 * Created by Monika on 12/20/2018.
 */

public class MoodActivityAddActivity extends BaseActivity implements View.OnClickListener, MoodActivityAdapter.moodListActivityAdapterListener {
    private static final String TAG = MoodActivityAddActivity.class.getSimpleName();

    @BindView(R.id.imageViewMood)
    ImageView imageViewMood;

    @BindView(R.id.textview_mood)
    TextView textViewMood;

    @BindView(R.id.linearlayout_activity_cleaning)
    LinearLayout linearLayoutActivityCleaning;

    @BindView(R.id.relativelayout_activity_cleaning)
    RelativeLayout relativeLayoutActivityCleaning;

    @BindView(R.id.imageview_activity_cleaning)
    AppCompatImageView imageViewActivityCleaning;

    @BindView(R.id.linearlayout_activity_date)
    LinearLayout linearLayoutActivityDate;

    @BindView(R.id.relativelayout_activity_date)
    RelativeLayout relativeLayoutActivityDate;

    @BindView(R.id.imageview_activity_date)
    AppCompatImageView imageViewActivityDate;

    @BindView(R.id.linearlayout_activity_friends)
    LinearLayout linearLayoutActivityFriends;

    @BindView(R.id.relativelayout_activity_friends)
    RelativeLayout relativeLayoutActivityFriends;

    @BindView(R.id.imageview_activity_friends)
    AppCompatImageView imageViewActivityFriends;

    @BindView(R.id.linearlayout_activity_gaming)
    LinearLayout linearLayoutActivityGaming;

    @BindView(R.id.relativelayout_activity_gaming)
    RelativeLayout relativeLayoutActivityGaming;

    @BindView(R.id.imageview_activity_gaming)
    AppCompatImageView imageViewActivityGaming;

    @BindView(R.id.linearlayout_activity_meal)
    LinearLayout linearLayoutActivityMeal;

    @BindView(R.id.relativelayout_activity_meal)
    RelativeLayout relativeLayoutActivityMeal;

    @BindView(R.id.imageview_activity_meal)
    AppCompatImageView imageViewActivityMeal;

    @BindView(R.id.linearlayout_activity_movie)
    LinearLayout linearLayoutActivityMovie;

    @BindView(R.id.relativelayout_activity_movie)
    RelativeLayout relativeLayoutActivityMovie;

    @BindView(R.id.imageview_activity_movie)
    AppCompatImageView imageViewActivityMovie;

    @BindView(R.id.linearlayout_activity_music)
    LinearLayout linearLayoutActivityMusic;

    @BindView(R.id.relativelayout_activity_music)
    RelativeLayout relativeLayoutActivityMusic;

    @BindView(R.id.imageview_activity_music)
    AppCompatImageView imageViewActivityMusic;

    @BindView(R.id.linearlayout_activity_reading)
    LinearLayout linearLayoutActivityReading;

    @BindView(R.id.relativelayout_activity_reading)
    RelativeLayout relativeLayoutActivityReading;

    @BindView(R.id.imageview_activity_reading)
    AppCompatImageView imageViewActivityReading;

    @BindView(R.id.linearlayout_activity_shopping)
    LinearLayout linearLayoutActivityShopping;

    @BindView(R.id.relativelayout_activity_shopping)
    RelativeLayout relativeLayoutActivityShopping;

    @BindView(R.id.imageview_activity_shopping)
    AppCompatImageView imageViewActivityShopping;

    @BindView(R.id.linearlayout_activity_sports)
    LinearLayout linearLayoutActivitySports;

    @BindView(R.id.relativelayout_activity_sports)
    RelativeLayout relativeLayoutActivitySports;

    @BindView(R.id.imageview_activity_sports)
    AppCompatImageView imageViewActivitySports;

    @BindView(R.id.linearlayout_activity_travel)
    LinearLayout linearLayoutActivityTravel;

    @BindView(R.id.relativelayout_activity_travel)
    RelativeLayout relativeLayoutActivityTravel;

    @BindView(R.id.imageview_activity_travel)
    AppCompatImageView imageViewActivityTravel;

    @BindView(R.id.linearlayout_activity_work)
    LinearLayout linearLayoutActivityWork;

    @BindView(R.id.relativelayout_activity_work)
    RelativeLayout relativeLayoutActivityWork;

    @BindView(R.id.imageview_activity_work)
    AppCompatImageView imageViewActivityWork;

    @BindView(R.id.linearlayout_activity_relax)
    LinearLayout linearLayoutActivityRelax;

    @BindView(R.id.relativelayout_activity_relax)
    RelativeLayout relativeLayoutActivityRelax;

    @BindView(R.id.imageview_activity_relax)
    AppCompatImageView imageViewActivityRelax;

    @BindView(R.id.linearlayout_activity_other)
    LinearLayout linearLayoutActivityOther;

//    @BindView(R.id.linearlayout_activity_school)
//    LinearLayout linearLayoutActivitySchool;

    @BindView(R.id.relativelayout_activity_other)
    RelativeLayout relativeLayoutActivityOther;

//    @BindView(R.id.relativelayout_activity_school)
//    RelativeLayout relativeLayoutActivitySchool;

    @BindView(R.id.imageview_activity_other)
    AppCompatImageView imageViewActivityOther;

//    @BindView(R.id.imageview_activity_school)
//    AppCompatImageView imageViewActivitySchool;

    @BindView(R.id.edittext_add_other_activity)
    EditText editTextOthetActivity;

    @BindView(R.id.edittext_add_note)
    EditText editTextNote;

    @BindView(R.id.textview_add_location)
    TextView textViewLocation;

    @BindView(R.id.imageview_submit)
    ImageView imageViewSubmit;

    @BindView(R.id.back_img)
    ImageView BackImg;


    private String mood, date, time, moodActivity = "", intensity;

    public ArrayList<MoodStats_> addMoodArrayList = new ArrayList<MoodStats_>();

//    @BindView(R.id.linearlayout_recycler_activity)
//    LinearLayout mLinearLayoutRecycler;

    RecyclerView mRecycleViewMood;

    ArrayList<MoodModel> moodModelArrayList = new ArrayList<>();
    MoodActivityAdapter moodAdapter;
    private GridLayoutManager mLinearLayoutManager;
    private String activityID;
    MoodModel selectedMoodModel;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View view = inflater.inflate(R.layout.activity_mood_activity_add, null);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        requestWindowFeature(Window.FEATURE_NO_TITLE);

       /* if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
            setContentView(R.layout.activity_werhope_mood_activity_add);
        } else {
            setContentView(R.layout.activity_mood_activity_add);
        }*/
        setContentView(R.layout.activity_mood_activity_add);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ButterKnife.bind(this);

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            editTextNote.setVisibility(View.GONE);
        } else {
            editTextNote.setVisibility(View.VISIBLE);
        }


        Preferences.initialize(this);
        Intent data = getIntent();
        if (data.hasExtra(General.MOOD)) {
            intensity = data.getStringExtra(General.INTENSITY);
            mood = data.getStringExtra(General.MOOD);
            date = data.getStringExtra(General.DATE);
            time = data.getStringExtra(General.TIME);
        } else {
            onBackPressed();
        }


        mRecycleViewMood = findViewById(R.id.mood_activity_recycler_view);
        mLinearLayoutManager = new GridLayoutManager(this, 5);
        mRecycleViewMood.setLayoutManager(mLinearLayoutManager);
        moodActivityListAPICalled();

        linearLayoutActivityCleaning.setOnClickListener(this);
        linearLayoutActivityDate.setOnClickListener(this);
        linearLayoutActivityFriends.setOnClickListener(this);
        linearLayoutActivityGaming.setOnClickListener(this);
        linearLayoutActivityMeal.setOnClickListener(this);
        linearLayoutActivityMovie.setOnClickListener(this);
        linearLayoutActivityMusic.setOnClickListener(this);
        linearLayoutActivityReading.setOnClickListener(this);
        linearLayoutActivityShopping.setOnClickListener(this);
        linearLayoutActivitySports.setOnClickListener(this);
        linearLayoutActivityTravel.setOnClickListener(this);
        linearLayoutActivityWork.setOnClickListener(this);
        linearLayoutActivityRelax.setOnClickListener(this);
        linearLayoutActivityOther.setOnClickListener(this);

        //linearLayoutActivitySchool.setOnClickListener(this);

        textViewLocation.setOnClickListener(this);
        imageViewSubmit.setOnClickListener(this);

        setActivityLayouts();
        setMood(Integer.valueOf(mood));
        textViewLocation.setText(getResources().getString(R.string.select_location));


        BackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Preferences.save(General.IS_ADD_MOOD_ACTIVITY_BACK_PRESSED, true);
    }

    private void setMood(int mood) {
        String moodString = "";
        activityID = "" + mood;

        switch (Integer.valueOf(mood)) {
            case 1:
                imageViewMood.setImageResource(R.drawable.mood_happy_low);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.happy);
                textViewMood.setText(moodString);
                break;
            case 2:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.laugh);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_laugh_low);
                break;
            case 3:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.neutral);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_neutral_low);
                break;
            case 4:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                    moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.anxious);
                    textViewMood.setText(moodString);
                } else {
                    moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.worried);
                    textViewMood.setText(moodString);
                }
                imageViewMood.setImageResource(R.drawable.mood_worried_low);
                break;
            case 5:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.cry);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_cry_low);
                break;
            case 6:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.sad);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_sad_low);
                break;
            case 7:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.angry);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_angry_low);
                break;
            case 8:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.excited);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_excited_low);
                break;
            case 9:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.bored);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_bored_low);
                break;
            case 10:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.fearful);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_fearful_low);
                break;
            case 11:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.angry);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_angry_low);
                break;
            case 12:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.frustrated);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_frustrated_low);
                break;
            case 13:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.excited);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_excited_low);
                break;
            case 14:
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.confused);
                textViewMood.setText(moodString);
                imageViewMood.setImageResource(R.drawable.mood_confused_low);
                break;
        }


        if (intensity.equalsIgnoreCase("33")) {
            setMoodLow();
            setMoodOne(Integer.valueOf(mood));
        } else if (intensity.equalsIgnoreCase("66")) {
            setMoodModerate();
            setMoodTwo(Integer.valueOf(mood));
        } else if (intensity.equalsIgnoreCase("100")) {
            setMoodHigh();
            setMoodThree(Integer.valueOf(mood));
        }
    }

    private void setMoodThree(int mood) {
        String moodString = "";
        switch (mood) {
            case 1:
                //imageViewMood.setImageResource(R.drawable.mood_happy);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_happy);
                //textViewMood.setText(moodString);
                break;
            case 2:
                //imageViewMood.setImageResource(R.drawable.mood_laugh);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.laugh);
                //textViewMood.setText(moodString);
                break;
            case 3:
                //imageViewMood.setImageResource(R.drawable.mood_neutral);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.neutral);
                //textViewMood.setText(moodString);
                break;
            case 4:
                //imageViewMood.setImageResource(R.drawable.mood_worried);
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                    moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_anxious);
                    //textViewMood.setText(moodString);
                } else {
                    moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_worried);
                    //textViewMood.setText(moodString);
                }
                break;
            case 5:
                //imageViewMood.setImageResource(R.drawable.mood_cry);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.cry);
                //textViewMood.setText(moodString);
                break;
            case 6:
                //imageViewMood.setImageResource(R.drawable.mood_sad);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_sad);
                // textViewMood.setText(moodString);
                break;
            case 7:
                //imageViewMood.setImageResource(R.drawable.mood_angry);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_angry);
                //textViewMood.setText(moodString);
                break;
            case 8:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_excited);
                //textViewMood.setText(moodString);
                break;

            case 9:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_bored);
                //textViewMood.setText(moodString);
                break;

            case 10:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_fearful);
                //textViewMood.setText(moodString);
                break;

            case 11:
                //imageViewMood.setImageResource(R.drawable.mood_angry);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_angry);
                //textViewMood.setText(moodString);
                break;
            case 13:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_excited);
                //textViewMood.setText(moodString);
                break;

            case 14:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_confused);
                //textViewMood.setText(moodString);
                break;

            case 12:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.extremely_frustrated);
                //textViewMood.setText(moodString);
                break;
        }
    }

    private void setMoodTwo(int mood) {
        String moodString = "";
        switch (mood) {
            case 1:
                //imageViewMood.setImageResource(R.drawable.mood_happy);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_happy);
                //textViewMood.setText(moodString);
                break;
            case 2:
                //imageViewMood.setImageResource(R.drawable.mood_laugh);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.laugh);
                //textViewMood.setText(moodString);
                break;
            case 3:
                //imageViewMood.setImageResource(R.drawable.mood_neutral);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.neutral);
                //textViewMood.setText(moodString);
                break;
            case 4:
                //imageViewMood.setImageResource(R.drawable.mood_worried);
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                    moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_anxious);
                    //textViewMood.setText(moodString);
                } else {
                    moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_worried);
                    //textViewMood.setText(moodString);
                }

                break;
            case 5:
                //imageViewMood.setImageResource(R.drawable.mood_cry);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.cry);
                //textViewMood.setText(moodString);
                break;
            case 6:
                //imageViewMood.setImageResource(R.drawable.mood_sad);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_sad);
                //textViewMood.setText(moodString);
                break;
            case 7:
                //imageViewMood.setImageResource(R.drawable.mood_angry);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_angry);
                //textViewMood.setText(moodString);
                break;
            case 8:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_excited);
                //textViewMood.setText(moodString);
                break;
            case 9:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_bored);
                //textViewMood.setText(moodString);
                break;
            case 10:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_fearful);
                //textViewMood.setText(moodString);
                break;

            case 11:
                //imageViewMood.setImageResource(R.drawable.mood_angry);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_angry);
                //textViewMood.setText(moodString);
                break;
            case 13:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_excited);
                //textViewMood.setText(moodString);
                break;

            case 14:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_confused);
                //textViewMood.setText(moodString);
                break;

            case 12:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.very_frustrated);
                //textViewMood.setText(moodString);
                break;
        }
    }

    private void setMoodOne(int mood) {
        String moodString = "";
        switch (mood) {
            case 1:
                // imageViewMood.setImageResource(R.drawable.mood_happy);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_happy);
                //textViewMood.setText(moodString);
                break;
            case 2:
                //imageViewMood.setImageResource(R.drawable.mood_laugh);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.laugh);
                //textViewMood.setText(moodString);
                break;
            case 3:
                //imageViewMood.setImageResource(R.drawable.mood_neutral);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.neutral);
                //textViewMood.setText(moodString);
                break;
            case 4:
                //imageViewMood.setImageResource(R.drawable.mood_worried);
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                    moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_anxious);
                    //textViewMood.setText(moodString);
                } else {
                    moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_worried);
                    //textViewMood.setText(moodString);
                }
                break;
            case 5:
                //imageViewMood.setImageResource(R.drawable.mood_cry);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.cry);
                //textViewMood.setText(moodString);
                break;
            case 6:
                //imageViewMood.setImageResource(R.drawable.mood_sad);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_sad);
                //textViewMood.setText(moodString);
                break;
            case 7:
                //imageViewMood.setImageResource(R.drawable.mood_angry);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_angry);
                //textViewMood.setText(moodString);
                break;
            case 8:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_excited);
                //textViewMood.setText(moodString);
                break;

            case 9:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_bored);
                //textViewMood.setText(moodString);
                break;
            case 10:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_fearful);
                //textViewMood.setText(moodString);
                break;

            case 11:
                //imageViewMood.setImageResource(R.drawable.mood_angry);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_angry);
                //textViewMood.setText(moodString);
                break;
            case 13:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_excited);
                //textViewMood.setText(moodString);
                break;

            case 14:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_confused);
                //textViewMood.setText(moodString);
                break;

            case 12:
                //imageViewMood.setImageResource(R.drawable.mood_excited);
                moodString = getResources().getString(R.string.which_activity_you_are_doing_for) + getResources().getString(R.string.really_frustrated);
                //textViewMood.setText(moodString);
                break;
        }
    }

    public void setMoodLow() {
        switch (Integer.valueOf(mood)) {
            case 1:
                imageViewMood.setImageResource(R.drawable.mood_happy_low);
                break;
            case 2:
                imageViewMood.setImageResource(R.drawable.mood_laugh_low);
                break;
            case 3:
                imageViewMood.setImageResource(R.drawable.mood_neutral_low);
                break;
            case 4:
                imageViewMood.setImageResource(R.drawable.mood_worried_low);
                break;
            case 5:
                imageViewMood.setImageResource(R.drawable.mood_cry_low);
                break;
            case 6:
                imageViewMood.setImageResource(R.drawable.mood_sad_low);
                break;
            case 7:
                imageViewMood.setImageResource(R.drawable.mood_angry_low);
                break;
            case 8:
                imageViewMood.setImageResource(R.drawable.mood_excited_low);
                break;
            case 9:
                imageViewMood.setImageResource(R.drawable.mood_bored_low);
                break;
            case 10:
                imageViewMood.setImageResource(R.drawable.mood_fearful_low);
                break;
            case 11:
                imageViewMood.setImageResource(R.drawable.mood_angry_low);
                break;
            case 13:
                imageViewMood.setImageResource(R.drawable.mood_excited_low);
                break;
            case 14:
                imageViewMood.setImageResource(R.drawable.mood_confused_low);
                break;
            case 12:
                imageViewMood.setImageResource(R.drawable.mood_frustrated_low);
                break;
        }
    }

    public void setMoodModerate() {
        switch (Integer.valueOf(mood)) {
            case 1:
                imageViewMood.setImageResource(R.drawable.mood_happy_moderate);
                break;
            case 2:
                imageViewMood.setImageResource(R.drawable.mood_laugh_moderate);
                break;
            case 3:
                imageViewMood.setImageResource(R.drawable.mood_neutral_moderate);
                break;
            case 4:
                imageViewMood.setImageResource(R.drawable.mood_worried_moderate);
                break;
            case 5:
                imageViewMood.setImageResource(R.drawable.mood_cry_moderate);
                break;
            case 6:
                imageViewMood.setImageResource(R.drawable.mood_sad_moderate);
                break;
            case 7:
                imageViewMood.setImageResource(R.drawable.mood_angry_moderate);
                break;
            case 8:
                imageViewMood.setImageResource(R.drawable.mood_excited_moderate);
                break;
            case 9:
                imageViewMood.setImageResource(R.drawable.mood_bored_moderate);
                break;
            case 10:
                imageViewMood.setImageResource(R.drawable.mood_fearful_moderate);
                break;
            case 11:
                imageViewMood.setImageResource(R.drawable.mood_angry_moderate);
                break;
            case 13:
                imageViewMood.setImageResource(R.drawable.mood_excited_moderate);
                break;
            case 14:
                imageViewMood.setImageResource(R.drawable.mood_confused_moderate);
                break;
            case 12:
                imageViewMood.setImageResource(R.drawable.mood_frustrated_moderate);
                break;
        }
    }

    public void setMoodHigh() {
        switch (Integer.valueOf(mood)) {
            case 1:
                //imageViewMood.setImageResource(R.drawable.mood_happy_high);
                break;
            case 2:
                //imageViewMood.setImageResource(R.drawable.mood_laugh_high);
                break;
            case 3:
                //imageViewMood.setImageResource(R.drawable.mood_neutral_high);
                break;
            case 4:
                //imageViewMood.setImageResource(R.drawable.mood_worried_high);
                break;
            case 5:
                //imageViewMood.setImageResource(R.drawable.mood_cry_high);
                break;
            case 6:
                //imageViewMood.setImageResource(R.drawable.mood_sad_high);
                break;
            case 7:
                //imageViewMood.setImageResource(R.drawable.mood_angry_high);
                break;
            case 8:
                //imageViewMood.setImageResource(R.drawable.mood_excited_high);
                break;
            case 9:
                //imageViewMood.setImageResource(R.drawable.mood_bored_high);
                break;
            case 10:
                //imageViewMood.setImageResource(R.drawable.mood_fearful_high);
                break;
            case 11:
                //imageViewMood.setImageResource(R.drawable.mood_angry_high);
                break;
            case 13:
                //imageViewMood.setImageResource(R.drawable.mood_excited_high);
                break;
            case 14:
                //imageViewMood.setImageResource(R.drawable.mood_confused_high);
                break;
            case 12:
                //imageViewMood.setImageResource(R.drawable.mood_frustrated_high);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearlayout_activity_cleaning:
                moodActivity = "1";
                setActivityLayouts();
                relativeLayoutActivityCleaning.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_date:
                moodActivity = "2";
                setActivityLayouts();
                relativeLayoutActivityDate.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_friends:
                moodActivity = "3";
                setActivityLayouts();

                relativeLayoutActivityFriends.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_gaming:
                moodActivity = "4";
                setActivityLayouts();

                relativeLayoutActivityGaming.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_meal:
                moodActivity = "5";
                setActivityLayouts();

                relativeLayoutActivityMeal.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_movie:
                moodActivity = "6";
                setActivityLayouts();

                relativeLayoutActivityMovie.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_music:
                moodActivity = "7";
                setActivityLayouts();

                relativeLayoutActivityMusic.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_reading:
                moodActivity = "8";
                setActivityLayouts();

                relativeLayoutActivityReading.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_shopping:
                moodActivity = "9";
                setActivityLayouts();

                relativeLayoutActivityShopping.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_sports:
                moodActivity = "10";
                setActivityLayouts();

                relativeLayoutActivitySports.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_travel:
                moodActivity = "11";
                setActivityLayouts();

                relativeLayoutActivityTravel.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_work:
                moodActivity = "12";
                setActivityLayouts();

                relativeLayoutActivityWork.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_relax:
                moodActivity = "13";
                setActivityLayouts();

                relativeLayoutActivityRelax.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_other:
                moodActivity = "14";
                setActivityLayouts();

                relativeLayoutActivityOther.setBackgroundResource(R.drawable.square_moodselection);
                break;

            case R.id.linearlayout_activity_school:
                moodActivity = "15";
                setActivityLayouts();
                //imageViewActivitySchool.setColorFilter(color);
                // relativeLayoutActivitySchool.setBackgroundResource(R.drawable.circle_mood_activity_selected);
                break;

            case R.id.textview_add_location:
                showLocationPopUp(textViewLocation);
                break;

            case R.id.imageview_submit:
                if (validate()) {
                    addMood();
                }
                break;
        }
    }

    public boolean validate() {
        activityID = moodActivity;

        if (activityID == null || activityID.length() == 0) {
            showAlert("Please Select Activity");
            return false;
        }

        if (textViewLocation == null || textViewLocation.getText().toString().trim().length() <= 0
                || textViewLocation.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.select_location))) {
            showAlert(getResources().getString(R.string.select_location));
            return false;
        }


        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {

        } else {
            if (editTextNote == null || editTextNote.getText().toString().trim().length() <= 0) {
                showAlert("Please provide comment");
                return false;
            }
        }



        return true;
    }

    // make network call to add Mood
    private void addMood() {
        int status = 11;
        String actionName = editTextOthetActivity.getText().toString().trim();
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(General.ACTION, Actions_.ADD_MOOD);

        requestMap.put(General.INTENSITY, intensity);

        requestMap.put(General.LOCATION, textViewLocation.getText().toString());

        requestMap.put(General.MOOD, mood);

//        requestMap.put(General.ACTIVITY, String.valueOf(selectedMoodModel.getId()));

        requestMap.put(General.ACTIVITY, activityID);


//        if (selectedMoodModel.getName().equalsIgnoreCase("Other")) {
//            requestMap.put(General.OTHER_COMMENT, actionName);
//        } else {
//            requestMap.put(General.OTHER_COMMENT, "");
//        }

        if (!actionName.isEmpty()) {
            requestMap.put(General.OTHER_COMMENT, actionName);
        } else {
            requestMap.put(General.OTHER_COMMENT, "");
        }

        requestMap.put(General.COMMENT, editTextNote.getText().toString());
        requestMap.put(General.DATE, date);
        requestMap.put(General.TIME, time);

        Log.e("requestMap", requestMap.toString());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_MOOD_DASHBOARD;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray(Actions_.ADD_MOOD);
                if (response != null) {
                    addMoodArrayList = MoodParser_.parseStatsCount(response, Actions_.ADD_MOOD, getApplicationContext(), TAG);
                    if (addMoodArrayList.size() > 0) {
                        if (addMoodArrayList.get(0).getStatus() == 1) {
                            showError(false, 1, "");
                        } else {
                            if (jsonArray.size() > 0) {
                                JsonObject objectMsg = jsonArray.get(0).getAsJsonObject();
                                showError(true, addMoodArrayList.get(0).getStatus(), objectMsg.get("msg").getAsString());
                            } else {
                                showError(true, addMoodArrayList.get(0).getStatus(), "");
                            }

                        }
                    } else {
                        if (jsonArray.size() > 0) {
                            JsonObject objectMsg = jsonArray.get(0).getAsJsonObject();
                            showError(true, addMoodArrayList.get(0).getStatus(), objectMsg.get("msg").getAsString());
                        }
                    }
                } else {
                    showError(true, 11, "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true, status, "");
        }
    }

    private void showError(boolean isError, int status, String msg) {

        Preferences.save(General.IS_ADD_MOOD_ACTIVITY_BACK_PRESSED, false);
        finish();
        if (isError) {
            if (status == 3) {
                if (msg.equals("")) {
                    ShowToast.successful(getResources().getString(R.string.you_can_add_only_3_mood_per_day), getApplicationContext());
                } else {
                    ShowToast.successful(msg, getApplicationContext());
                }

            } else if (status == 2) {
                if (msg.equals("")) {
                    ShowToast.successful(getResources().getString(R.string.error_while_adding_mood), getApplicationContext());
                } else {
                    ShowToast.successful(msg, getApplicationContext());
                }

            } else {
                ShowToast.successful(GetErrorResources.getMessage(status, getApplicationContext()), getApplicationContext());
            }
        } else {
            ShowToast.successful(getResources().getString(R.string.successful), getApplicationContext());
        }
    }

    private void setActivityLayouts() {


        relativeLayoutActivityCleaning.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityDate.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityFriends.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityGaming.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityMeal.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityMovie.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityMusic.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityReading.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityShopping.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivitySports.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityTravel.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityWork.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityRelax.setBackgroundResource(R.drawable.circle_gray_corners);
        relativeLayoutActivityOther.setBackgroundResource(R.drawable.circle_gray_corners);

        // relativeLayoutActivitySchool.setBackgroundResource(R.drawable.circle_gray_corners);

        editTextOthetActivity.setText("");

        if (moodActivity.equalsIgnoreCase("14")) {
            editTextOthetActivity.setVisibility(View.VISIBLE);
        } else {
            editTextOthetActivity.setVisibility(View.GONE);
        }
    }

    //open task status menu
    private void showLocationPopUp(final View view) {
        PopupMenu popup = new PopupMenu(this, view);

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
            popup.getMenuInflater().inflate(R.menu.werhope_menu_mood_activity_location, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.menu_mood_activity_location, popup.getMenu());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String old_status = textViewLocation.getText().toString();
                if (!old_status.equalsIgnoreCase(item.getTitle().toString())) {
                    textViewLocation.setText(item.getTitle().toString());
                }
                return true;
            }
        });
        popup.show();
    }


    public void moodActivityListAPICalled() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ACTIVITY);

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("PNResponse", response);
                if (response != null) {
                    moodModelArrayList = MoodParser_.parseMoodList(response, Actions_.GET_ACTIVITY, this, TAG);
                    if (moodModelArrayList.size() > 0) {
                        if (moodModelArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
//                            notesCount.setText(progressList.size() + " Notes Found");
                            MoodModel moodModel = new MoodModel();
                            for (int i = 0; i < moodModelArrayList.size(); i++) {
                                if (moodModelArrayList.get(i).getName().equalsIgnoreCase("Other")) {
                                    moodModel = moodModelArrayList.get(i);
                                    moodModelArrayList.remove(i);
                                    break;
                                }
                            }
                            moodModelArrayList.add(moodModel);

                            moodAdapter = new MoodActivityAdapter(this, moodModelArrayList, this);
                            mRecycleViewMood.setAdapter(moodAdapter);
                        } else {
                            Log.e("ErrorMood", "" + moodModelArrayList.get(0).getStatus());
                        }
                    } else {
//                        showError(true, progressList.get(0).getStatus());
                        Log.e("ErrorMood_1", "" + moodModelArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("errorPNListActivity", e.getMessage());
            }
        }
    }

    @Override
    public void moodDetailLayoutClicked(MoodModel moodModel) {
        activityID = String.valueOf(moodModel.getId());
        // activityID = moodModel.getName();
        selectedMoodModel = moodModel;
        editTextOthetActivity.setText("");
        if (moodModel.getName().equalsIgnoreCase("Other")) {
            editTextOthetActivity.setVisibility(View.VISIBLE);
        } else {
            editTextOthetActivity.setVisibility(View.GONE);
        }
    }
}
