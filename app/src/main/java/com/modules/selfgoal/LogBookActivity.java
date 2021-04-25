package com.modules.selfgoal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.calendar.CustomCalendar;
import com.modules.calendar.CustomCalendarAdapter;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.views.ExpandableHeightGridView;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

public class LogBookActivity extends AppCompatActivity implements GridView.OnItemClickListener {
    private static final String TAG = LogBookActivity.class.getSimpleName();
    private TextView mTxtDate, mTxtCalendar;
    private TextView mTxtCompleteCount, mTxtMissedCount, mTxtActivatedCount, mTxtPartialCompletedCount, mTxtSortACS;
    ExpandableHeightGridView mCalendar;
    private CustomCalendarAdapter mMaterialCalendarAdapter;
    protected static int mNumEventsOnDay = 0;
    RelativeLayout lineraLayoutMonth;
    TextView mMonthName;
    AppCompatImageView mNext;
    AppCompatImageView mPrevious;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private static String date;
    private Dialog dialog;
    private LinearLayout mLinearLayoutFilter, mLinearLayoutFilterBlue, mLinearLayoutSortASC, mLinearLayoutSearchImage, mLinearLayoutSortDESC;
    private RelativeLayout mLinearLayoutSort, mLinearLayoutFilterMain;
    private ArrayList<LogBookModel> logBookList;
    private ArrayList<LogBookModel> logBookFilterList;
    private ArrayList<LogBookDotsModel> logBookDotsModelArrayList;
    private RecyclerView recyclerViewSenjamLogBook, recyclerViewLogBook;
    private RelativeLayout relativeLayoutEditTextSearch, relativeTextViewSearch;
    private EditText editTextSearch;
    private String searchText = "";
    private TextView textViewSearch;
    private AppCompatImageView imageViewClearSearchText;
    String answerTag = "0";
    private LogBookAdapter logBookAdapter;
    private String orignalDateFormate;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private boolean isSortASC = true;


    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // if user login with senjam than display senjam logbook design
        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            setContentView(R.layout.senjam_logbook_activity);
        } else {
            setContentView(R.layout.activity_log_book);
        }
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
        titleText.setText(getResources().getString(R.string.log_book));
        titleText.setPadding(140, 0, 0, 0);


        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        date = mYear + "-" + (mMonth + 1) + "-" + mDay;

        intiFunction();

        // if user login with senjam than there is no filter , sort and search icon so all this click listener will be disable for senjam
        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {

        } else {
            mClickListenerFunction();
        }

    }

    // All Variable Declaration Function
    private void intiFunction() {

        // this condition is for declare layout Id for senjam and sage
        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {

            mMonthName = findViewById(R.id.planner_fragment_month_name);
            mNext = findViewById(R.id.planner_fragment_month_next);
            mPrevious = findViewById(R.id.planner_fragment_month_back);
            lineraLayoutMonth = findViewById(R.id.lineralayout_month);
            mCalendar = findViewById(R.id.planner_fragment_grid_view);
            recyclerViewSenjamLogBook = findViewById(R.id.recycler_log_book); // Recycler View For Sanjam

            mCalendar.setOnItemClickListener(this);
            setCalendar();

            if ((CustomCalendar.mMonth + 1) < 10) {
                String month = "0" + (CustomCalendar.mMonth + 1);
                getLogBookDotsAPICalled(month, CustomCalendar.mYear);
            } else {
                getLogBookDotsAPICalled(String.valueOf((CustomCalendar.mMonth + 1)), CustomCalendar.mYear);
            }

        } else {
            mTxtDate = (TextView) findViewById(R.id.txt_log_date);
            mTxtCalendar = findViewById(R.id.txt_calendar);
            mTxtCompleteCount = findViewById(R.id.txt_complete_count);
            mTxtMissedCount = findViewById(R.id.txt_missed_count);
            mTxtActivatedCount = findViewById(R.id.txt_activated_count);
            mTxtPartialCompletedCount = findViewById(R.id.txt_partial_count);

            mLinearLayoutFilter = findViewById(R.id.linear_txt_filter);  // without apply filter layout
            mLinearLayoutFilterBlue = findViewById(R.id.linear_txt_filter_blue); // apply filter layout
            mLinearLayoutFilterMain = findViewById(R.id.linear_filter); // filter main layout

            mLinearLayoutSortASC = findViewById(R.id.linear_txt_sort_asc); // sort asc layout
            mLinearLayoutSortDESC = findViewById(R.id.linear_txt_sort_desc); // sort desc layout
            mLinearLayoutSort = findViewById(R.id.linear_sort); // sort main layout
            mLinearLayoutSearchImage = findViewById(R.id.linear_search_image);
            recyclerViewLogBook = findViewById(R.id.recycler_view); // recycler view for sageSurfer
            relativeLayoutEditTextSearch = findViewById(R.id.relative_edit_text_search);
            editTextSearch = findViewById(R.id.edittext_search);
            relativeTextViewSearch = findViewById(R.id.relative_layout_search);
            textViewSearch = findViewById(R.id.txt_search);
            imageViewClearSearchText = findViewById(R.id.imageview_clear_search_text);
            mTxtSortACS = findViewById(R.id.txt_sort_asc);

            mTxtDate.setText(getDate(date));
        }

        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        getLogBookDataAPICalled(orinalDate(date), "");
    }

    // All Click Event Function
    private void mClickListenerFunction() {

        mTxtCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when user click on show calendar icon it will show Calendar Popup
                popUpShowCalendar();
            }
        });

        mLinearLayoutFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when user Click on Filter Icon it will show Filter Dialog
                dailogShowFilter();
            }
        });

        mLinearLayoutFilterBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when user Click on Filter Icon it will show Filter Dialog
                dailogShowFilter();
            }
        });

        mLinearLayoutSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // when user click on search icon filter and sort list icon will be hide.
                mLinearLayoutFilterMain.setVisibility(View.GONE);
                mLinearLayoutSort.setVisibility(View.GONE);
                relativeLayoutEditTextSearch.setVisibility(View.VISIBLE);
            }
        });

        textViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLinearLayoutFilterMain.setVisibility(View.GONE);
                mLinearLayoutSort.setVisibility(View.GONE);
                relativeLayoutEditTextSearch.setVisibility(View.VISIBLE);
            }
        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                    searchText = editTextSearch.getText().toString().trim();

                    // called API for Log
                    getLogBookDataAPICalled(orinalDate(date), searchText);

                    // when user click on search icon on keyboard search image and editText will be hide and filter and sort icon will be display
                    relativeTextViewSearch.setVisibility(View.VISIBLE);
                    mLinearLayoutFilterMain.setVisibility(View.VISIBLE);
                    mLinearLayoutSort.setVisibility(View.VISIBLE);
                    mLinearLayoutSearchImage.setVisibility(View.GONE);
                    relativeLayoutEditTextSearch.setVisibility(View.GONE);
                    textViewSearch.setText(searchText);
                    return true;
                }
                return false;
            }
        });


        // This close button will be display when user search and when user remove search this click will be called
        imageViewClearSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mLinearLayoutFilterMain.setVisibility(View.VISIBLE);
                mLinearLayoutSort.setVisibility(View.VISIBLE);
                mLinearLayoutSearchImage.setVisibility(View.VISIBLE);
                relativeLayoutEditTextSearch.setVisibility(View.GONE);
                relativeTextViewSearch.setVisibility(View.GONE);
                getLogBookDataAPICalled(orinalDate(date), "");
            }
        });

        mLinearLayoutSortASC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sort icon clicked then desc icon will be visible and the list be in desc order

//                getLogBookDataAPICalled(orinalDate(date),"");
                mLinearLayoutSortDESC.setVisibility(View.VISIBLE);
                mLinearLayoutSortASC.setVisibility(View.GONE);
                isSortASC = false;
                getLogBookDataAPICalled(orinalDate(date), "");
            }
        });

        mLinearLayoutSortDESC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if sort desc icon will be shown and user click on it it will visible asc order icon and the list will be updated by asc order
                mLinearLayoutSortASC.setVisibility(View.VISIBLE);
                mLinearLayoutSortDESC.setVisibility(View.GONE);
                isSortASC = true;
                getLogBookDataAPICalled(orinalDate(date), "");
            }
        });


    }


    //Dialog calendar
    @SuppressLint("SetJavaScriptEnabled")
    public void popUpShowCalendar() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_calendar_layout);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageButton close;
        LinearLayout mLinearLayoutOk;
        mMonthName = dialog.findViewById(R.id.planner_fragment_month_name);
        mNext = dialog.findViewById(R.id.planner_fragment_month_next);
        mPrevious = dialog.findViewById(R.id.planner_fragment_month_back);
        lineraLayoutMonth = dialog.findViewById(R.id.lineralayout_month);
        mCalendar = dialog.findViewById(R.id.planner_fragment_grid_view);
        close = dialog.findViewById(R.id.button_close);
        mLinearLayoutOk = dialog.findViewById(R.id.linear_layout_ok);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        mCalendar.setExpanded(true);
        mCalendar.setOnItemClickListener(this);

        setCalendar();

//        mLinearLayoutOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mTxtDate.setText(getDate(date));
//                getLogBookDataAPICalled(orinalDate(date),"");
//                dialog.dismiss();
//            }
//        });

        dialog.show();

    }

    // set Calendar data into Adapter
    private void setCalendar() {
        mCalendar.setVisibility(View.VISIBLE);

        CustomCalendar.getInitialCalendarInfo();
        lineraLayoutMonth.setVisibility(View.VISIBLE);
        if (mPrevious != null) {
            mPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomCalendar.previousOnClick(mPrevious, mMonthName, mCalendar, mMaterialCalendarAdapter);
                    date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + mDay;
                    // this condition will check if month is in 2 digit because we have pass month parameter in api into 2 digit
                    if ((CustomCalendar.mMonth + 1) < 10) {
                        String month = "0" + (CustomCalendar.mMonth + 1);
                        getLogBookDotsAPICalled(month, CustomCalendar.mYear);
                    } else {
                        getLogBookDotsAPICalled(String.valueOf((CustomCalendar.mMonth + 1)), CustomCalendar.mYear);
                    }
//                    getLogBookDotsAPICalled((CustomCalendar.mMonth + 1),CustomCalendar.mYear);
                }
            });
        }
        if (mMonthName != null) {
            Calendar cal = Calendar.getInstance();
            if (cal != null) {
                String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + cal.get(Calendar.YEAR);
                mMonthName.setText(monthName);
            }
        }
        if (mNext != null) {
            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomCalendar.nextOnClick(mNext, mMonthName, mCalendar, mMaterialCalendarAdapter);
                    date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + mDay;

                    // this condition will check if month is in 2 digit because we have pass month parameter in api into 2 digit
                    if ((CustomCalendar.mMonth + 1) < 10) {
                        String month = "0" + (CustomCalendar.mMonth + 1);
                        getLogBookDotsAPICalled(month, CustomCalendar.mYear);
                    } else {
                        getLogBookDotsAPICalled(String.valueOf((CustomCalendar.mMonth + 1)), CustomCalendar.mYear);
                    }

                }
            });
        }
        if (mCalendar != null) {
            mMaterialCalendarAdapter = new CustomCalendarAdapter(this);
            mCalendar.setAdapter(mMaterialCalendarAdapter);
            if (CustomCalendar.mCurrentDay != -1 && CustomCalendar.mFirstDay != -1) {
                int startingPosition = 6 + CustomCalendar.mFirstDay;
                int currentDayPosition = startingPosition + CustomCalendar.mCurrentDay;
                mCalendar.setItemChecked(currentDayPosition, true);
                if (mMaterialCalendarAdapter != null) {
                    mMaterialCalendarAdapter.notifyDataSetChanged();
                }
            }
        }

        date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + mDay;

        // this condition will check if month is in 2 digit because we have pass month parameter in api into 2 digit
        if ((CustomCalendar.mMonth + 1) < 10) {
            String month = "0" + (CustomCalendar.mMonth + 1);
            getLogBookDotsAPICalled(month, CustomCalendar.mYear);
        } else {
            getLogBookDotsAPICalled(String.valueOf((CustomCalendar.mMonth + 1)), CustomCalendar.mYear);
        }
    }

    //Dialog filter Function
    @SuppressLint("SetJavaScriptEnabled")
    public void dailogShowFilter() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter_layout);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);


        ImageView imageViewClose;
        Button buttonSubmit;
        TextView textViewClear;
        final RadioGroup radioGroupFilter;
        final RadioButton radio_all;

        textViewClear = dialog.findViewById(R.id.txt_clear);
        imageViewClose = dialog.findViewById(R.id.image_close);
        buttonSubmit = dialog.findViewById(R.id.button_submit);
        radioGroupFilter = dialog.findViewById(R.id.radioGroup);
        radio_all = dialog.findViewById(R.id.radio_all);
        //radioGroupFilter.check(Integer.parseInt(answerTag));

        // here we have to show selected radio button
        int count = radioGroupFilter.getChildCount();
        ArrayList<RadioButton> listOfRadioButtons = new ArrayList<RadioButton>();
        for (int i = 0; i < count; i++) {
            View o = radioGroupFilter.getChildAt(i);
            if (o instanceof RadioButton) {
                if (Integer.parseInt(String.valueOf(o.getTag())) == Integer.parseInt(answerTag)) {
                    ((RadioButton) o).setChecked(true);
                    break;
                }
            }
        }


        radioGroupFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == -1) {
                    return;
                }
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                answerTag = String.valueOf(radioButton.getTag());
//                Log.e("Tag", answerTag);
            }
        });


        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        textViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroupFilter.clearCheck();
                radio_all.setChecked(true);
//                getfilterFunction(String.valueOf(radio_all.getTag()));

            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here we have to pass answerTag then we get flter data into list
                getFilterFunction(answerTag);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    // get filter data into list
    private void getFilterFunction(String answerTag) {
        if (answerTag.equalsIgnoreCase("0")) {
            logBookFilterList = new ArrayList<LogBookModel>(logBookList);
            mLinearLayoutFilter.setVisibility(View.VISIBLE);
            mLinearLayoutFilterBlue.setVisibility(View.GONE);
        } else {
            logBookFilterList.clear();
            for (int j = 0; j < logBookList.size(); j++) {
                String goalStatusId = logBookList.get(j).getGoalStatusID();
                if (goalStatusId.equalsIgnoreCase(answerTag)) {
                    logBookFilterList.add(logBookList.get(j));
                }
            }
            mLinearLayoutFilter.setVisibility(View.GONE);
            mLinearLayoutFilterBlue.setVisibility(View.VISIBLE);
        }

        // arraylist will be bind and set into the adapter
        if (logBookFilterList.size() != 0) {
            logBookAdapter = new LogBookAdapter(LogBookActivity.this, logBookFilterList);
            recyclerViewLogBook.setAdapter(logBookAdapter);
            errorLayout.setVisibility(View.GONE);
            recyclerViewLogBook.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerViewLogBook.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Preferences.save(Actions_.APPOINTMENT_COUNTER, false);
        Preferences.save(Actions_.GET_LOGBOOK_DOTS, true);
        Preferences.save(Actions_.GET_LOG, false);
        Preferences.save(General.IS_FROM_CASELOAD, false);
        Preferences.save(General.IS_FROM_DAILYPLANNER, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences.save(Actions_.GET_LOGBOOK_DOTS, false);
    }


    //clander data click event
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.planner_fragment_grid_view:
                CustomCalendar.selectCalendarDay(mMaterialCalendarAdapter, position);
                mNumEventsOnDay = -1;

                int selectedDate = position - (6 + CustomCalendar.mFirstDay);
                date = CustomCalendar.mYear + "-" + (CustomCalendar.mMonth + 1) + "-" + selectedDate;

                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
                Date newDate = null;
                try {
                    newDate = spf.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spf = new SimpleDateFormat("yyyy-MM-dd");
                orignalDateFormate = spf.format(newDate);

                getLogBookDataAPICalled(orinalDate(date), "");


                // kishor k add this code due to error

                if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {

                } else {
                    mTxtDate.setText(getDate(date));
                    dialog.dismiss();
                }

                //dialog.dismiss();

                break;
            default:
                break;
        }

    }

    public String orinalDate(String Date) {
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("yyyy-MM-dd");
        orignalDateFormate = spf.format(newDate);

        return orignalDateFormate;
    }

    public String getDate(String Date) {
        java.text.DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(date);

        return outputDateStr;
    }

    // Api function for getting Log Book Data
    private void getLogBookDataAPICalled(String date, String searchText) {
        logBookList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LOGBOOK_DATA);
        requestMap.put(General.PATIENT_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.SEARCH, searchText);
        if (isSortASC) {
            requestMap.put(General.SORT, "ASC");
        } else {
            requestMap.put(General.SORT, "DESC");
        }
        requestMap.put(General.FETCH_DATE, date);

        Log.e("requestMap", "" + requestMap);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("response", response);

                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray getLogBookData = null;
                    LogBookModel model = new LogBookModel();
                    int status = Integer.parseInt(jsonObject.getString("status"));
                    if (status == 1) {
                        showError(false, 1);
                        String completed = String.valueOf(jsonObject.getInt(General.COMPLETED));
                        String missed = String.valueOf(jsonObject.getInt(General.MISSED));
                        String partiallyCompleted = String.valueOf(jsonObject.getInt(General.PARTIALCOMPLETED));
                        String inputNeeded = String.valueOf(jsonObject.getInt(General.INPUTNEEDED));

                        getLogBookData = jsonObject.getJSONArray(Actions_.GET_LOGBOOK_DATA);
                        for (int i = 0; i < getLogBookData.length(); i++) {
                            JSONObject getLogBookObject = getLogBookData.getJSONObject(i);
                            LogBookModel getLogBookModel = new LogBookModel();
                            getLogBookModel.setName(getLogBookObject.getString(General.NAME));
                            getLogBookModel.setStartDate(getLogBookObject.getString(General.START_DATE));
                            getLogBookModel.setEndDate(getLogBookObject.getString(General.END_DATE));
                            getLogBookModel.setDate(getLogBookObject.getString(General.DATE));
                            getLogBookModel.setYes(getLogBookObject.getString(General.YES));
                            getLogBookModel.setNo(getLogBookObject.getString(General.NO));
                            getLogBookModel.setGoalStatus(getLogBookObject.getString(General.GOAL_STATUS));
                            getLogBookModel.setGoalStatusID(getLogBookObject.getString(General.GOAL_STATUS_ID));
                            logBookList.add(getLogBookModel);
                        }
                        logBookFilterList = new ArrayList<LogBookModel>(logBookList);

                        // here if user login with senjam than data will be bind and set senjam adapter other wise set logbook adapter for sage
                        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                            SenjamLogBookAdapter senjamLogBookAdapter = new SenjamLogBookAdapter(this, logBookList);
                            recyclerViewSenjamLogBook.setAdapter(senjamLogBookAdapter);
                        } else {
                            logBookAdapter = new LogBookAdapter(this, logBookList);
                            recyclerViewLogBook.setAdapter(logBookAdapter);

                            mTxtCompleteCount.setText(completed);
                            mTxtMissedCount.setText(missed);
                            mTxtPartialCompletedCount.setText(partiallyCompleted);
                            mTxtActivatedCount.setText(inputNeeded);
                        }
                    } else {
                        Toast.makeText(this, jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
                        showError(true, status);
                    }
//                    logBookList = SelfGoal_.senjamLogBook(response, Actions_.GET_LOGBOOK_DATA, this, TAG);


                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error", "" + e.getMessage());
            }
        }
    }

    // Api Called for getting Look Book Dots dipsplay in clander
    private void getLogBookDotsAPICalled(String month, int year) {
        logBookDotsModelArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LOGBOOK_DOTS);
        requestMap.put(General.PATIENT_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.MONTH, String.valueOf(month));
        requestMap.put(General.YEAR, String.valueOf(year));

        Log.e("requestMap", "" + requestMap);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("response", response);

                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray getLogBookDots = null;
                    int status = Integer.parseInt(jsonObject.getString("status"));
                    getLogBookDots = jsonObject.getJSONArray(Actions_.GET_LOGBOOK_DOTS);
                    if (status == 1) {
                        for (int i = 0; i < getLogBookDots.length(); i++) {
                            JSONObject getLogBookDotsObject = getLogBookDots.getJSONObject(i);
                            LogBookDotsModel logBookDotsModel = new LogBookDotsModel();
                            logBookDotsModel.setDate(getLogBookDotsObject.getString(General.DATE));
                            logBookDotsModel.setColor(getLogBookDotsObject.getString(General.COLOR));
                            logBookDotsModelArrayList.add(logBookDotsModel);
                        }

                        // arraylist will be bind here and set into the adapter
                        mMaterialCalendarAdapter = new CustomCalendarAdapter(this, logBookDotsModelArrayList);
                        mCalendar.setAdapter(mMaterialCalendarAdapter);
                        if (CustomCalendar.mCurrentDay != -1 && CustomCalendar.mFirstDay != -1) {
                            int startingPosition = 6 + CustomCalendar.mFirstDay;
                            int currentDayPosition = startingPosition + CustomCalendar.mCurrentDay;
                            mCalendar.setItemChecked(currentDayPosition, true);
                            if (mMaterialCalendarAdapter != null) {
                                mMaterialCalendarAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Toast.makeText(this, jsonObject.optString("error"), Toast.LENGTH_SHORT).show();

                        // if calendar do not null then set the adapter
                        if (mCalendar != null) {
                            mMaterialCalendarAdapter = new CustomCalendarAdapter(this);
                            mCalendar.setAdapter(mMaterialCalendarAdapter);
                            if (CustomCalendar.mCurrentDay != -1 && CustomCalendar.mFirstDay != -1) {
                                int startingPosition = 6 + CustomCalendar.mFirstDay;
                                int currentDayPosition = startingPosition + CustomCalendar.mCurrentDay;
                                mCalendar.setItemChecked(currentDayPosition, true);
                                if (mMaterialCalendarAdapter != null) {
                                    mMaterialCalendarAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error", "" + e.getMessage());
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                recyclerViewSenjamLogBook.setVisibility(View.GONE);
            } else {
                recyclerViewLogBook.setVisibility(View.GONE);
            }
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                recyclerViewSenjamLogBook.setVisibility(View.VISIBLE);
            } else {
                recyclerViewLogBook.setVisibility(View.VISIBLE);
            }
        }
    }

}
