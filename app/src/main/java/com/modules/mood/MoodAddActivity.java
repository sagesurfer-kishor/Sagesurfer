package com.modules.mood;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.GoalDetailsInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.TimeConverter;
import com.sagesurfer.models.MoodStats_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MoodParser_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.views.SeekBarHint;
import com.storage.preferences.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by kailash karankal
 */

public class MoodAddActivity extends AppCompatActivity implements View.OnClickListener, GoalDetailsInterface, MoodAdapter.moodListAdapterListener {
    @BindView(R.id.imageview_mood)
    ImageView imageViewMood;

    @BindView(R.id.textview_mood)
    TextView moodText;

    @BindView(R.id.seekBar)
    SeekBarHint seekBar;

    @BindView(R.id.textview_date)
    TextView dateText;

    @BindView(R.id.textview_time)
    TextView timeText;

    @BindView(R.id.linearlayout_mood_happy)
    LinearLayout linearLayoutMoodHappy;

    @BindView(R.id.linearlayout_mood_good)
    LinearLayout linearLayoutMoodGood;

    @BindView(R.id.linearlayout_mood_cry)
    LinearLayout linearLayoutMoodCry;

    @BindView(R.id.linearlayout_mood_worried)
    LinearLayout linearLayoutMoodWorried;

    @BindView(R.id.linearlayout_mood_sad)
    LinearLayout linearLayoutMoodSad;

    @BindView(R.id.linearlayout_mood_angry)
    LinearLayout linearLayoutMoodAngry;

    @BindView(R.id.linearlayout_mood_neutral)
    LinearLayout linearLayoutMoodNeutral;

    @BindView(R.id.linearlayout_mood_excited)
    LinearLayout linearLayoutMoodExcited;

    @BindView(R.id.imageview_next)
    ImageView imageViewNext;

    @BindView(R.id.linearlayout_mood)
    LinearLayout moodStatusLayout;

    @BindView(R.id.back_img)
    ImageView BackImg;

    @BindView(R.id.linearlayout_mood_happy_one)
    LinearLayout linearLayoutMoodHappyOne;
    @BindView(R.id.linearlayout_mood_anxious)
    LinearLayout linearlayoutMoodAnxious;
    @BindView(R.id.linearlayout_mood_sad_one)
    LinearLayout linearlayoutMoodSadOne;
    @BindView(R.id.linearlayout_mood_bored)
    LinearLayout linearlayoutMoodBored;
    @BindView(R.id.linearlayout_mood_fearful)
    LinearLayout linearlayoutMoodFearful;

    @BindView(R.id.linearlayout_mood_frustrated)
    LinearLayout linearlayoutMoodFrustrated;
    @BindView(R.id.linearlayout_mood_confused)
    LinearLayout linearlayoutMoodConfused;

    @BindView(R.id.linearlayout_recycler)
    LinearLayout mLinearLayoutRecycler;

    @BindView(R.id.mood_recycler_view)
    RecyclerView mRecycleViewMood;

    private Calendar calendar;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    public String date = "";
    private String hour = "01", minute = "00", unit = "AM";
    private String mood = "0", intensity = "0";
    public static MoodAddActivity moodAddFragment;
    private MoodStats_ moodStats_;

    public static final String TAG = MoodAddActivity.class.getSimpleName();
    private GridLayoutManager mLinearLayoutManager;

    ArrayList<MoodModel> moodModelArrayList = new ArrayList<>();
    MoodAdapter moodAdapter;
    private String ID;
    private MoodModel selectedMood;

    @SuppressLint({"SetTextI18n", "SourceLockedOrientationActivity"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mood_add);

        ButterKnife.bind(this);
        moodAddFragment = this;
        Preferences.initialize(this);
        Preferences.save(General.INTENSITY, "0");

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        dateText.setText(mYear + "-" + GetCounters.checkDigit(mMonth + 1) + "-" + GetCounters.checkDigit(mDay));

        Intent data = getIntent();
        if (data.hasExtra(Actions_.PENDING_MOOD)) {
            moodStats_ = (MoodStats_) data.getSerializableExtra(Actions_.PENDING_MOOD);
            dateText.setText(moodStats_.getDate());
        }

        timeText.setText(TimeConverter.getCurrentTime12Hrs());

        int color = Color.parseColor("#ffffff"); //white
        imageViewNext.setColorFilter(color);
        imageViewNext.setImageResource(R.drawable.vi_calendar_next_arrow);

        seekBar.setOnSeekBarChangeListener(onSeek);
        dateText.setOnClickListener(this);
        timeText.setOnClickListener(this);
        linearLayoutMoodHappy.setOnClickListener(this);
        linearLayoutMoodGood.setOnClickListener(this);
        linearLayoutMoodCry.setOnClickListener(this);
        linearLayoutMoodWorried.setOnClickListener(this);
        linearLayoutMoodSad.setOnClickListener(this);
        linearLayoutMoodAngry.setOnClickListener(this);
        linearLayoutMoodNeutral.setOnClickListener(this);
        linearLayoutMoodExcited.setOnClickListener(this);
        imageViewNext.setOnClickListener(this);


        linearLayoutMoodHappyOne.setOnClickListener(this);
        linearlayoutMoodAnxious.setOnClickListener(this);
        linearlayoutMoodSadOne.setOnClickListener(this);
        linearlayoutMoodBored.setOnClickListener(this);
        linearlayoutMoodFearful.setOnClickListener(this);

        linearlayoutMoodFrustrated.setOnClickListener(this);
        linearlayoutMoodConfused.setOnClickListener(this);

        BackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoodAddActivity.this.finish();
            }
        });

        seekBar.setMax(3);
        seekbarProgress(seekBar);

//        mRecycleViewMood.setBackgroundColor(this.getResources().getColor(R.color.screen_background));
        mLinearLayoutManager = new GridLayoutManager(this, 4);
        mRecycleViewMood.setLayoutManager(mLinearLayoutManager);
        moodListAPICalled();
    }

    private void seekbarProgress(SeekBarHint seekBar) {
        seekBar.setProgress(0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Preferences.getBoolean(General.IS_ADD_MOOD_ACTIVITY_BACK_PRESSED)) { //on mood activity submit click dismiss Mood Add dialog screen
            finish();
        }
    }

    // get values from seek bar positions
    //0-33 --> Low; 34-66 --> Moderate; 67-100 --> High;
    private final SeekBar.OnSeekBarChangeListener onSeek = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.seekBar:
                    if (progress == 1) {
                        seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.mood_add_progress1), PorterDuff.Mode.MULTIPLY));
                        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.mood_add_progress1), PorterDuff.Mode.SRC_IN);
                        intensity = "33";
                        setMoodLow();
                        /*setMood(mood, intensity);*/
                        setMood(intensity);
                    } else if (progress == 2) {
                        seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.mood_add_progress2), PorterDuff.Mode.MULTIPLY));
                        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.mood_add_progress2), PorterDuff.Mode.SRC_IN);
                        intensity = "66";
                        setMoodModerate();
                        setMood(intensity);
                    } else if (progress == 3) {
                        seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.mood_add_progress3), PorterDuff.Mode.MULTIPLY));
                        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.mood_add_progress3), PorterDuff.Mode.SRC_IN);
                        intensity = "100";
                        setMoodHigh();
                        setMood(intensity);
                    } else {
                        seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY));
                        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                        intensity = "0";
                        setMoodModerate();
                    }
                    break;
            }
        }

        private void setMoodText(String really_happy) {
            moodText.setText(really_happy);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private void setMood(String intensity) {

        if (selectedMood != null) {
            if (selectedMood.getIs_intensity() == 1) {
                if (intensity.equalsIgnoreCase("33")) {
                    moodText.setText("Really " + selectedMood.getName());
                } else if (intensity.equalsIgnoreCase("66")) {
                    moodText.setText("Very " + selectedMood.getName());
                } else if (intensity.equalsIgnoreCase("100")) {
                    moodText.setText("Extremely " + selectedMood.getName());
                }
            } else {
                moodText.setText(selectedMood.getName());
            }
        }


       /* if (mood == "1") {
            if (intensity.equalsIgnoreCase("33")) {
                moodText.setText("Really Happy");
            } else if (intensity.equalsIgnoreCase("66")) {
                moodText.setText("Very Happy");
            } else if (intensity.equalsIgnoreCase("100")) {
                moodText.setText("Extremely Happy");
            }

        } else if (mood == "4") {

            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                if (intensity.equalsIgnoreCase("33")) {
                    moodText.setText("Really Anxious");
                } else if (intensity.equalsIgnoreCase("66")) {
                    moodText.setText("Very Anxious");
                } else if (intensity.equalsIgnoreCase("100")) {
                    moodText.setText("Extremely Anxious");
                }
            } else {
                if (intensity.equalsIgnoreCase("33")) {
                    moodText.setText("Really Worried");
                } else if (intensity.equalsIgnoreCase("66")) {
                    moodText.setText("Very Worried");
                } else if (intensity.equalsIgnoreCase("100")) {
                    moodText.setText("Extremely Worried");
                }
            }
        } else if (mood == "6") {
            if (intensity.equalsIgnoreCase("33")) {
                moodText.setText("Really Sad");
            } else if (intensity.equalsIgnoreCase("66")) {
                moodText.setText("Very Sad");
            } else if (intensity.equalsIgnoreCase("100")) {
                moodText.setText("Extremely Sad");
            }
        } else if (mood == "7" || mood == "11") {
            if (intensity.equalsIgnoreCase("33")) {
                moodText.setText("Really Angry");
            } else if (intensity.equalsIgnoreCase("66")) {
                moodText.setText("Very Angry");
            } else if (intensity.equalsIgnoreCase("100")) {
                moodText.setText("Extremely Angry");
            }
        } else if (mood == "8" || mood == "13") {
            if (intensity.equalsIgnoreCase("33")) {
                moodText.setText("Really Excited");
            } else if (intensity.equalsIgnoreCase("66")) {
                moodText.setText("Very Excited");
            } else if (intensity.equalsIgnoreCase("100")) {
                moodText.setText("Extremely Excited");
            }
        } else if (mood == "9") {
            if (intensity.equalsIgnoreCase("33")) {
                moodText.setText("Really Bored");
            } else if (intensity.equalsIgnoreCase("66")) {
                moodText.setText("Very Bored");
            } else if (intensity.equalsIgnoreCase("100")) {
                moodText.setText("Extremely Bored");
            }
        } else if (mood == "10") {
            if (intensity.equalsIgnoreCase("33")) {
                moodText.setText("Really Fearful");
            } else if (intensity.equalsIgnoreCase("66")) {
                moodText.setText("Very Fearful");
            } else if (intensity.equalsIgnoreCase("100")) {
                moodText.setText("Extremely Fearful");
            }
        } else if (mood == "12") {
            if (intensity.equalsIgnoreCase("33")) {
                moodText.setText("Really Frustrated");
            } else if (intensity.equalsIgnoreCase("66")) {
                moodText.setText("Very Frustrated");
            } else if (intensity.equalsIgnoreCase("100")) {
                moodText.setText("Extremely Frustrated");
            }
        } else if (mood == "14") {
            if (intensity.equalsIgnoreCase("33")) {
                moodText.setText("Really Confused");
            } else if (intensity.equalsIgnoreCase("66")) {
                moodText.setText("Very Confused");
            } else if (intensity.equalsIgnoreCase("100")) {
                moodText.setText("Extremely Confused");
            }
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_date:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))
                ) {

                    DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                    try {
                                        dateText.setText(date);
                                        checkTime();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, mYear, mMonth, mDay);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_WEEK, 0); // subtract 6 days from now
                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                    datePickerDialog.show();

                } else {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                    try {
                                        dateText.setText(date);
                                        checkTime();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, mYear, mMonth, mDay);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_WEEK, -6); // subtract 6 days from now
                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }

                break;

            case R.id.textview_time:
                openChoiceDialog();
                break;

            case R.id.linearlayout_mood_happy:
                imageViewMood.setImageResource(R.drawable.mood_happy_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.happy));
                mood = "1";
                seekbarProgress(seekBar);
                break;

            case R.id.linearlayout_mood_happy_one:
                imageViewMood.setImageResource(R.drawable.mood_happy_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.happy));
                mood = "1";
                seekbarProgress(seekBar);
                break;

            case R.id.linearlayout_mood_good: //laugh
                imageViewMood.setImageResource(R.drawable.mood_laugh_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.laugh));
                seekbarProgress(seekBar);
                mood = "2";
                break;

            case R.id.linearlayout_mood_cry:
                imageViewMood.setImageResource(R.drawable.mood_cry_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.cry));
                seekbarProgress(seekBar);
                mood = "5";
                break;

            case R.id.linearlayout_mood_worried:
                imageViewMood.setImageResource(R.drawable.mood_worried_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.worried));
                seekbarProgress(seekBar);
                mood = "4";
                break;

            case R.id.linearlayout_mood_anxious:
                imageViewMood.setImageResource(R.drawable.mood_worried_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.anxious));
                seekbarProgress(seekBar);
                mood = "4";
                break;

            case R.id.linearlayout_mood_sad:
                imageViewMood.setImageResource(R.drawable.mood_sad_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.sad));
                seekbarProgress(seekBar);
                mood = "6";
                break;

            case R.id.linearlayout_mood_sad_one:
                imageViewMood.setImageResource(R.drawable.mood_sad_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.sad));
                seekbarProgress(seekBar);
                mood = "6";
                break;

            case R.id.linearlayout_mood_angry:
                imageViewMood.setImageResource(R.drawable.mood_angry_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.angry));
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                    mood = "11";
                } else {
                    mood = "7";
                }

                seekbarProgress(seekBar);
                break;

            case R.id.linearlayout_mood_neutral:
                imageViewMood.setImageResource(R.drawable.mood_neutral_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.neutral));
                mood = "3";
                seekbarProgress(seekBar);
                break;

            case R.id.linearlayout_mood_excited:
                imageViewMood.setImageResource(R.drawable.mood_excited_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.excited));
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
                    mood = "13";
                } else {
                    mood = "8";
                }
                seekbarProgress(seekBar);
                break;

            case R.id.linearlayout_mood_bored:
                imageViewMood.setImageResource(R.drawable.mood_bored_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.bored));
                mood = "9";
                seekbarProgress(seekBar);
                break;

            case R.id.linearlayout_mood_fearful:
                imageViewMood.setImageResource(R.drawable.mood_fearful_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.fearful));
                mood = "10";
                seekbarProgress(seekBar);
                break;

            case R.id.linearlayout_mood_confused:
                imageViewMood.setImageResource(R.drawable.mood_confused_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.confused));
                mood = "14";
                seekbarProgress(seekBar);
                break;

            case R.id.linearlayout_mood_frustrated:
                imageViewMood.setImageResource(R.drawable.mood_frustrated_moderate);
                moodText.setVisibility(View.VISIBLE);
                moodText.setText(getResources().getString(R.string.frustrated));
                mood = "12";
                seekbarProgress(seekBar);
                break;


            case R.id.imageview_next:
                if (validate()) {
                    Intent createIntent = new Intent(getApplicationContext(), MoodActivityAddActivity.class);
                    createIntent.putExtra(General.INTENSITY, intensity);
                    createIntent.putExtra(General.MOOD, ID);
                    createIntent.putExtra(General.DATE, dateText.getText().toString().trim());
                    createIntent.putExtra(General.TIME, timeText.getText().toString().trim());
                    startActivity(createIntent);
                }
                break;
        }

        if (intensity.equalsIgnoreCase("33")) {
            setMoodLow();
        } else if (intensity.equalsIgnoreCase("66")) {
            setMoodModerate();
        } else if (intensity.equalsIgnoreCase("100")) {
            setMoodHigh();
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
            case 12:
                imageViewMood.setImageResource(R.drawable.mood_frustrated_low);
                break;
            case 13:
                imageViewMood.setImageResource(R.drawable.mood_excited_low);
                break;
            case 14:
                imageViewMood.setImageResource(R.drawable.mood_confused_low);
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
            case 12:
                imageViewMood.setImageResource(R.drawable.mood_frustrated_moderate);
                break;
            case 13:
                imageViewMood.setImageResource(R.drawable.mood_excited_moderate);
                break;
            case 14:
                imageViewMood.setImageResource(R.drawable.mood_confused_moderate);
                break;
        }
    }

    public void setMoodHigh() {
        switch (Integer.valueOf(mood)) {
            case 1:
                imageViewMood.setImageResource(R.drawable.mood_happy_high);
                break;
            case 2:
                imageViewMood.setImageResource(R.drawable.mood_laugh_high);
                break;
            case 3:
                imageViewMood.setImageResource(R.drawable.mood_neutral_high);
                break;
            case 4:
                imageViewMood.setImageResource(R.drawable.mood_worried_high);
                break;
            case 5:
                imageViewMood.setImageResource(R.drawable.mood_cry_high);
                break;
            case 6:
                imageViewMood.setImageResource(R.drawable.mood_sad_high);
                break;
            case 7:
                imageViewMood.setImageResource(R.drawable.mood_angry_high);
                break;
            case 8:
                imageViewMood.setImageResource(R.drawable.mood_excited_high);
                break;
            case 9:
                imageViewMood.setImageResource(R.drawable.mood_bored_high);
                break;
            case 10:
                imageViewMood.setImageResource(R.drawable.mood_fearful_high);
                break;
            case 11:
                imageViewMood.setImageResource(R.drawable.mood_angry_high);
                break;
            case 12:
                imageViewMood.setImageResource(R.drawable.mood_frustrated_high);
                break;
            case 13:
                imageViewMood.setImageResource(R.drawable.mood_excited_high);
                break;
            case 14:
                imageViewMood.setImageResource(R.drawable.mood_confused_high);
                break;
        }
    }

    public void setTime(String _hour, String _minute, String _unit) {
        String time = _hour + ":" + _minute;
        time = time + ":" + "00" + " " + _unit;
        timeText.setText(time);
        checkTime();
    }

    public void checkTime() {
        String currentUnit;
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        int currentYear = c.get(Calendar.YEAR);
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMin = c.get(Calendar.MINUTE);
        int ds = c.get(Calendar.AM_PM);
        if (ds == 0)
            currentUnit = "AM";
        else
            currentUnit = "PM";
        String currentDateTime = currentYear + "-" + currentMonth + "-" + currentDay + " " + currentHour + ":" + currentMin + ":" + "00" + " " + currentUnit;
        if (dateText.getText().toString().length() > 0) {
            try {
                if (timeText.getText().toString().length() > 0) {
                    String dateTime = dateText.getText().toString().trim() + " " + timeText.getText().toString().trim();
                    Date time1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa").parse(dateTime); //startTime
                    Date time2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(currentDateTime); //endTime
                    if (time1.after(time2)) {
                        timeText.setFocusable(true);
//                        timeText.setError(getResources().getString(R.string.please_do_not_add_future_time));
                    } else {
                        timeText.setFocusable(true);
                        timeText.setError(null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {//2018-11-1 12:11:00 AM,2018-11-1 06:11:00 PM  2018-11-01 12:11:00 PM
                String dateTime = currentYear + "-" + currentMonth + "-" + currentDay + " " + timeText.getText().toString().trim();
                Date time1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa").parse(dateTime); //startTime
                Date time2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(currentDateTime); //endTime
                if (time1.after(time2)) {
                    timeText.setFocusable(true);
                    timeText.setError(this.getResources().getString(R.string.please_do_not_add_future_time));
                } else {
                    timeText.setFocusable(true);
                    timeText.setError(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private void openChoiceDialog() {
        Bundle bundle = new Bundle();
        TimeHourMoodPickerDialogFragment dialogFrag = new TimeHourMoodPickerDialogFragment();
        bundle.putString(General.FREQUENCY, "details");
        bundle.putString(General.DESCRIPTION, "1");
        bundle.putBoolean(General.IS_FROM_MOOD, true);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.TIMESTAMP);
    }


    public boolean validate() {

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {

        } else {
            if (intensity == null || intensity.length() == 0 || intensity.equalsIgnoreCase("0")) {
                ShowToast.toast("Select Intensity", getApplicationContext());
                return false;
            }
        }


        if (dateText == null || dateText.getText().toString().trim().length() <= 0) {
            ShowSnack.textViewWarning(dateText, "Enter Valid Date", getApplicationContext());
            return false;
        }

        if (timeText == null || timeText.getText().toString().trim().length() <= 0 || timeText.getError() != null) {
            ShowSnack.textViewWarning(timeText, "Enter Valid Time", getApplicationContext());
            return false;
        }

        if (ID == null || ID.length() == 0 || ID.equalsIgnoreCase("0")) {
            ShowToast.toast("Select Mood", getApplicationContext());
            return false;
        }

        String currentUnit;
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        int currentYear = c.get(Calendar.YEAR);
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMin = c.get(Calendar.MINUTE);
        int currentSecond = c.get(Calendar.SECOND);
        int ds = c.get(Calendar.AM_PM);
        if (ds == 0)
            currentUnit = "AM";
        else
            currentUnit = "PM";


        String currentDateTime = currentYear + "-" + currentMonth + "-" + currentDay + " " + currentHour + ":" + currentMin + ":" + currentSecond + " " + currentUnit;
        String strDate = dateText.getText().toString();
        try {//2018-11-1 12:11:00 AM,2018-11-1 06:11:00 PM  2018-11-01 12:11:00 PM
            //String dateTime = currentYear + "-" + currentMonth + "-" + currentDay + " " + timeText.getText().toString().trim();
            String dateTime = strDate + " " + timeText.getText().toString().trim();
            Date time1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa").parse(dateTime); //startTime
            Date time2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(currentDateTime); //endTime
            if (time1.after(time2)) {
                timeText.setFocusable(true);
                ShowToast.toast("Please do not add future time Enter Valid time", getApplicationContext());
                return false;
            } else {
                timeText.setFocusable(true);
                timeText.setError(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        /*Date dateCurrent = new Date();
        Date dateOfEndAppointment = null;

        String strAppDateTime = dateText.getText().toString().trim().length() + " " + timeText.getText().toString().trim().length();
        SimpleDateFormat formatold = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            dateOfEndAppointment = formatold.parse(strAppDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(dateCurrent.before(dateOfEndAppointment)) {
            ShowToast.toast("Please do not add future time Enter Valid time", getApplicationContext());
            return false;
        }*/

        return true;
    }

    @Override
    public void setCountTime(String time, String unit) {
    }

    @Override
    public void setDurationTime(String time) {
    }

    public void moodListAPICalled() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MOODS);

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("PNResponse", response);
                if (response != null) {
                    moodModelArrayList = MoodParser_.parseMoodList(response, Actions_.GET_MOODS, this, TAG);
                    if (moodModelArrayList.size() > 0) {
                        if (moodModelArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
//                            notesCount.setText(progressList.size() + " Notes Found");
                            moodAdapter = new MoodAdapter(this, moodModelArrayList, this);
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
        selectedMood = moodModel;
        Glide.with(this)
                .load(moodModel.getUrl())
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.mood_happy_high)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                .into(imageViewMood);
//        imageViewMood.setImageResource(moodModel.getUrl());
        moodText.setVisibility(View.VISIBLE);
        moodText.setText(moodModel.getName());
        setMood(intensity);
        ID = String.valueOf(moodModel.getId());
//        seekbarProgress(seekBar);
    }
}
