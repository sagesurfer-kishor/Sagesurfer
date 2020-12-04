package com.modules.caseload.werhope.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.caseload.werhope.adapter.MoodFilterAdapter;
import com.modules.caseload.werhope.adapter.ProgressListAdapter;
import com.modules.caseload.werhope.model.ProgressList;
import com.modules.mood.MoodModel;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MoodParser_;
import com.sagesurfer.parser.ProgressList_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

public class ProgressNoteActivity extends AppCompatActivity implements View.OnClickListener, ProgressListAdapter.ProgressListAdapterListener {
    private static final String TAG = ProgressNoteActivity.class.getSimpleName();
    public ArrayList<ProgressList> progressList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView errorText, notesCount;
    private AppCompatImageView errorIcon;
    private FloatingActionButton createProgressNote;
    private ImageButton noteFilterButton, noteDescending, noteAscending;
    private LinearLayoutManager mLinearLayoutManager;
    private List<String> moodIds = new ArrayList<String>();

    //Progress Note filter part
    private PopupWindow popupWindow = new PopupWindow();
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date = "", end_date = "";
    private String lastWeek = "", lastMonth = "";
    private String happy = "", anxious = "", sad = "", bored = "", angry = "";
    private String excited = "", confused = "", frustrated = "";

    ArrayList<MoodModel> moodModelArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MoodFilterAdapter moodFilterAdapter;


    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_progress_note);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(100, 0, 0, 0);
        titleText.setText(getResources().getString(R.string.progress_note));

        initUI();

        setOnClickListeners();
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.setBackgroundColor(this.getResources().getColor(R.color.screen_background));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        notesCount = (TextView) findViewById(R.id.notes_count);
        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        createProgressNote = (FloatingActionButton) findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        createProgressNote.setImageResource(R.drawable.ic_add_white);
        createProgressNote.setOnClickListener(this);
        createProgressNote.setVisibility(View.VISIBLE);

        noteFilterButton = (ImageButton) findViewById(R.id.note_filter);
        noteDescending = (ImageButton) findViewById(R.id.imagebutton_desc);
        noteAscending = (ImageButton) findViewById(R.id.imagebutton_asc);
    }

    private void setOnClickListeners() {
        noteDescending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear previous data
                progressList.clear();

                noteAscending.setVisibility(View.VISIBLE);
                noteDescending.setVisibility(View.GONE);
                noteProgressListAPICalled("ASC");
            }
        });

        noteAscending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear previous data
                progressList.clear();

                noteAscending.setVisibility(View.GONE);
                noteDescending.setVisibility(View.VISIBLE);
                noteProgressListAPICalled("DESC");
            }
        });

        noteFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterProgressNoteData(v);
            }
        });
    }

    private void filterProgressNoteData(View v) {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.progress_note_filter, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            popupWindow.isShowing();

            final LinearLayout dateLayout, dateSelectionLayout, mLinearLayoutRecyclerView;
            final TextView clearSelection, clearDateSelection;
            final TextView startDate, endDate;
            final ImageView imageviewSave, closeNotificationFilterDialog;
            final Calendar calendar;
            final CheckBox lastWeekCheckBox, lastMonthCheckBox;
            final CheckBox happyCheckBox, anxiousCheckBox;
            final CheckBox sadCheckBox, boredCheckBox, angryCheckBox;
            final CheckBox excitedCheckBox, confusedCheckBox, frustratedCheckBox;


            dateLayout = customView.findViewById(R.id.date_layout);
            dateSelectionLayout = customView.findViewById(R.id.date_selection);
            lastWeekCheckBox = customView.findViewById(R.id.check_box_week);
            lastMonthCheckBox = customView.findViewById(R.id.check_box_month);
            clearSelection = customView.findViewById(R.id.clear_selection);
            clearDateSelection = customView.findViewById(R.id.clear_selection_date);
            imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
            closeNotificationFilterDialog = customView.findViewById(R.id.imageview_back);
            startDate = customView.findViewById(R.id.start_date_txt);
            endDate = customView.findViewById(R.id.end_date_txt);

            happyCheckBox = customView.findViewById(R.id.happy_check_box);
            anxiousCheckBox = customView.findViewById(R.id.anxious_check_box);
            sadCheckBox = customView.findViewById(R.id.sad_check_box);
            boredCheckBox = customView.findViewById(R.id.bored_check_box);
            angryCheckBox = customView.findViewById(R.id.angry_check_box);
            excitedCheckBox = customView.findViewById(R.id.excited_check_box);
            confusedCheckBox = customView.findViewById(R.id.confused_check_box);
            frustratedCheckBox = customView.findViewById(R.id.frustrated_check_box);

            mLinearLayoutRecyclerView = customView.findViewById(R.id.linear_recycler_progress_filter);
            mRecyclerView = customView.findViewById(R.id.progress_filter_recycler_view);

            mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            if (moodModelArrayList.size() <= 0) {

                moodListAPICalled();
            } else {
                moodFilterAdapter = new MoodFilterAdapter(this, moodModelArrayList);
                mRecyclerView.setAdapter(moodFilterAdapter);
            }

            moodFilterAdapter.setOnItemClickListener(new MoodFilterAdapter.onItemClickListener() {
                @Override
                public void onItemClickListener(View view, int position, final MoodModel moodModel, CheckBox checkBox) {

                    moodModel.setSelected(!moodModel.isSelected());
                    checkBox.setChecked(moodModel.isSelected());
                }
            });

            moodFilterAdapter.setOnItemClickListenerCheckBox(new MoodFilterAdapter.onItemClickListenerCheckBox() {
                @Override
                public void onItemClickListenerCheckBox(View view, int position, final MoodModel moodModel, CheckBox checkBox) {

                    moodModel.setSelected(!moodModel.isSelected());
                    checkBox.setChecked(moodModel.isSelected());
                }
            });


            excitedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        excited = "excited";
                        moodIds.add("13");
                    } else {
                        moodIds.remove("");
                        excited = "";
                    }
                }
            });


            confusedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        confused = "confused";
                        moodIds.add("14");
                    } else {
                        moodIds.remove("");
                        confused = "";
                    }
                }
            });


            frustratedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        frustrated = "frustrated";
                        moodIds.add("12");
                    } else {
                        moodIds.remove("");
                        frustrated = "";
                    }
                }
            });


            happyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        happy = "happy";
                        moodIds.add("1");
                    } else {
                        moodIds.remove("");
                        happy = "";
                    }
                }
            });

            anxiousCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        anxious = "anxious";
                        moodIds.add("4");
                    } else {
                        anxious = "";
                        moodIds.remove("");
                    }
                }
            });

            sadCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        sad = "sad";
                        moodIds.add("6");
                    } else {
                        sad = "";
                        moodIds.remove("");
                    }
                }
            });
            boredCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        bored = "bored";
                        moodIds.add("9");
                    } else {
                        bored = "";
                        moodIds.remove("");
                    }
                }
            });

            angryCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        angry = "angry";
                        moodIds.add("11");
                    } else {
                        angry = "";
                        moodIds.remove("");
                    }
                }
            });


            clearSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    happyCheckBox.setChecked(false);
                    anxiousCheckBox.setChecked(false);
                    sadCheckBox.setChecked(false);
                    boredCheckBox.setChecked(false);
                    angryCheckBox.setChecked(false);
                    excitedCheckBox.setChecked(false);
                    confusedCheckBox.setChecked(false);
                    frustratedCheckBox.setChecked(false);

                    startDate.setText("");
                    endDate.setText("");

                    for (MoodModel n : moodModelArrayList) {
                        n.setSelected(false);
                    }
                    moodFilterAdapter.notifyDataSetChanged();
                }
            });

            clearDateSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastWeekCheckBox.setChecked(false);
                    lastMonthCheckBox.setChecked(false);
                    startDate.setText("");
                    endDate.setText("");
                }
            });

            dateSelectionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateLayout.setVisibility(View.VISIBLE);
                    lastWeekCheckBox.setChecked(false);
                    lastMonthCheckBox.setChecked(false);
                }
            });

            calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ProgressNoteActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                    try {
                                        startDate.setText(date);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, mYear, mMonth, mDay);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_WEEK, -6);
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }
            });

            endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(ProgressNoteActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                    try {
                                        int result = Compare.validEndDate(end_date, date);
                                        if (result == 1) {
                                            endDate.setText(end_date);
                                        } else {
                                            end_date = null;
                                            endDate.setText(null);
                                            ShowSnack.textViewWarning(endDate, ProgressNoteActivity.this.getResources()
                                                    .getString(R.string.invalid_date), ProgressNoteActivity.this);

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, mYear, mMonth, mDay);
                    Calendar c1 = Calendar.getInstance();
                    c1.add(Calendar.DAY_OF_WEEK, -6);
                    datePickerDialog1.getDatePicker().setMaxDate(System.currentTimeMillis());
                    datePickerDialog1.show();
                }
            });

            if (Preferences.getBoolean("last_week")) {
                lastWeekCheckBox.setChecked(true);
            } else {
                lastWeekCheckBox.setChecked(false);
            }

            lastWeekCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        lastWeek = "last_week";
                        lastMonthCheckBox.setChecked(false);
                        Preferences.save("last_week", true);
                        startDate.setText("");
                        endDate.setText("");
                    } else {
                        lastWeek = "";
                        Preferences.save("last_week", false);
                    }
                }
            });

            if (Preferences.getBoolean("last_month")) {
                lastMonthCheckBox.setChecked(true);
            } else {
                lastMonthCheckBox.setChecked(false);
            }

            lastMonthCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        lastMonth = "last_month";
                        lastWeekCheckBox.setChecked(false);
                        Preferences.save("last_month", true);
                        startDate.setText("");
                        endDate.setText("");
                    } else {
                        lastMonth = "";
                        Preferences.save("last_month", false);
                    }
                }
            });

            imageviewSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterMoodWise("");
                }
            });

            closeNotificationFilterDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterMoodWise(String sort) {
        showError(true, 20);
        progressList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROGRESS_NOTE_LIST_NEW);
        requestMap.put(General.STUD_ID, Preferences.get(General.NOTE_USER_ID));

        /*if (moodIds.size() == 1) {
            if (happy.equals("happy")) {
                requestMap.put(General.MOOD, "1");
            } else if (anxious.equals("anxious")) {
                requestMap.put(General.MOOD, "4");
            } else if (sad.equals("sad")) {
                requestMap.put(General.MOOD, "6");
            } else if (bored.equals("bored")) {
                requestMap.put(General.MOOD, "9");
            } else if (angry.equals("angry")) {
                requestMap.put(General.MOOD, "11");
            } else if (excited.equals("excited")) {
                requestMap.put(General.MOOD, "13");
            } else if (confused.equals("confused")) {
                requestMap.put(General.MOOD, "14");
            } else if (frustrated.equals("frustrated")) {
                requestMap.put(General.MOOD, "12");
            } else {
                requestMap.put(General.MOOD, "0");
            }
        } else {
            requestMap.put(General.MOOD, getIds());
        }*/
        requestMap.put(General.MOOD, getIds());
        if (!date.equals("") && !end_date.equals("")) {
            requestMap.put(General.DATE_TYPE, "date");
            requestMap.put(General.START_DATE, date);
            requestMap.put(General.END_DATE, end_date);
        } else if (lastWeek.equals("last_week")) {
            requestMap.put(General.DATE_TYPE, lastWeek);
        } else if (lastMonth.equals("last_month")) {
            requestMap.put(General.DATE_TYPE, lastMonth);
        } else {
            requestMap.put(General.DATE_TYPE, "0");
        }

        if (sort.equalsIgnoreCase("ASC")) {
            requestMap.put(General.SORT, "ASC");
        } else {
            requestMap.put(General.SORT, "DESC");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    progressList = ProgressList_.parseProgressList(response, Actions_.GET_PROGRESS_NOTE_LIST_NEW, this, TAG);
                    if (progressList.size() > 0) {
                        if (progressList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            notesCount.setText(progressList.size() + " Notes Found");
                            ProgressListAdapter progressListAdapter = new ProgressListAdapter(this, progressList, this);
                            recyclerView.setAdapter(progressListAdapter);
                            popupWindow.dismiss();
                            clearData();
                        } else {
                            notesCount.setText("");
                            showError(true, progressList.get(0).getStatus());
                            popupWindow.dismiss();
                            moodIds.clear();
                            clearData();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void moodListAPICalled() {

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MOODS);

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MOOD_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("moodListResponse", response);
                if (response != null) {
                    moodModelArrayList = MoodParser_.parseMoodList(response, Actions_.GET_MOODS, this, TAG);
                    if (moodModelArrayList.size() > 0) {
                        if (moodModelArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
//                            notesCount.setText(progressList.size() + " Notes Found");
                            moodFilterAdapter = new MoodFilterAdapter(this, moodModelArrayList);
                            mRecyclerView.setAdapter(moodFilterAdapter);
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

    private void clearData() {
        moodIds.clear();
        happy = "";
        anxious = "";
        sad = "";
        bored = "";
        angry = "";
        confused = "";
        excited = "";
        frustrated = "";
    }

    @SuppressLint("NewApi")
    private String getIds() {

/*
        if (moodIds.size() > 0) {
            StringBuilder nameBuilder = new StringBuilder();

            for (String n : moodIds) {
                nameBuilder.append("'").append(n.replace("'", "\\'")).append("',");
            }
            nameBuilder.deleteCharAt(nameBuilder.length() - 1);
            return nameBuilder.toString();
        } else {
            return "";
        }*/
        if (moodModelArrayList.size() > 0) {
            StringBuilder nameBuilder = new StringBuilder();

            int nSelectCount = 0;
            for (MoodModel n : moodModelArrayList) {
                if (n.isSelected()) {
                    if (nSelectCount == 0) {
                        nameBuilder.append(n.getId());
                    } else {
                        nameBuilder.append(",").append(n.getId());
                    }
                    nSelectCount++;
                }
            }
            return nameBuilder.toString();
        } else {
            return "";
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Called API for display list of progress note
        noteProgressListAPICalled("");
    }

    @SuppressLint("SetTextI18n")
    private void noteProgressListAPICalled(String sort) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROGRESS_NOTE_LIST_NEW);
        requestMap.put(General.STUD_ID, Preferences.get(General.NOTE_USER_ID));
        requestMap.put(General.DATE_TYPE, "0");
        requestMap.put(General.MOOD, "0");

        if (sort.equalsIgnoreCase("ASC")) {
            requestMap.put(General.SORT, "ASC");
        } else {
            requestMap.put(General.SORT, "DESC");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    progressList = ProgressList_.parseProgressList(response, Actions_.GET_PROGRESS_NOTE_LIST_NEW, this, TAG);
                    if (progressList.size() > 0) {
                        if (progressList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            notesCount.setText(progressList.size() + " Notes Found");
                            ProgressListAdapter progressListAdapter = new ProgressListAdapter(this, progressList, this);
                            recyclerView.setAdapter(progressListAdapter);
                        } else {
                            notesCount.setText("0 Note Found");
                            showError(true, progressList.get(0).getStatus());
                        }
                    } else {
                        showError(true, progressList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_refresh_layout_recycler_view_float:
                Intent addProgressNoteIntent = new Intent(this, AddProgressNoteActivity.class);
                startActivity(addProgressNoteIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onNoteDetailsLayoutClicked(ProgressList progressNote) {
        Intent progressNoteDetails = new Intent(this, ProgressNoteDetailsActivity.class);
        progressNoteDetails.putExtra(General.NOTE_DETAILS, progressNote);
        startActivity(progressNoteDetails);
    }
}
