package com.modules.caseload.mhaw.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
 import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView; ;
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
import com.modules.caseload.mhaw.adapter.MhawProgressListAdapter;
import com.modules.caseload.mhaw.model.MhawProgressList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MhawProgressList_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

public class MhawProgressNoteActivity extends AppCompatActivity implements View.OnClickListener, MhawProgressListAdapter.MhawProgressListAdapterListener {
    private static final String TAG = MhawProgressNoteActivity.class.getSimpleName();
    public ArrayList<MhawProgressList> progressList = new ArrayList<>();
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

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_mhaw_progress_note);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Action bar theme setup
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
        //Recycler view to bind progress note data
        recyclerView = (RecyclerView) findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.setBackgroundColor(this.getResources().getColor(R.color.screen_background));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        //Elements when no data from response
        notesCount = (TextView) findViewById(R.id.notes_count);
        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        //Floaitng add progress note button
        createProgressNote = (FloatingActionButton) findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        createProgressNote.setImageResource(R.drawable.ic_add_white);
        createProgressNote.setOnClickListener(this);


        //Filter & Sort elements
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

        //Common function to show/hide add progress note option
        AddProgressNoteFabButtonVisibility();
    }

    private void AddProgressNoteFabButtonVisibility() {

        /*This condition is for show hide AddProgressNote Button according to Roll*/
        if (Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_SystemAdministrator)
                || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ComplianceAdministrator)
                || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ClinicalApplicationsAdministrator)
                || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ProgramDirector)
                || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_PeerSupervisor)
                || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_QACoordinator)
                || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_AdminAssistant)
                || Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_CPST_Clinician)
                || Preferences.get(General.ROLE_ID).equals(General.USER_ROLE_MHAW_PEER_STAFF)) {
            createProgressNote.setVisibility(View.VISIBLE);
        } else {
            createProgressNote.setVisibility(View.GONE);
        }

    }

    private void filterProgressNoteData(View v) {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.progress_mhaw_note_filter, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            popupWindow.isShowing();

            final LinearLayout dateLayout, dateSelectionLayout;
            final TextView clearSelection, clearDateSelection;
            final TextView startDate, endDate;
            final ImageView imageviewSave, closeNotificationFilterDialog;
            final Calendar calendar;
            final CheckBox lastWeekCheckBox, lastMonthCheckBox;

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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MhawProgressNoteActivity.this,
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
                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(MhawProgressNoteActivity.this,
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
                                            ShowSnack.textViewWarning(endDate, MhawProgressNoteActivity.this.getResources()
                                                    .getString(R.string.invalid_date), MhawProgressNoteActivity.this);

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
        requestMap.put(General.ACTION, Actions_.GET_PROGRESS_NOTE_MHAW);
        requestMap.put(General.USER_ID, Preferences.get(General.NOTE_USER_ID));
        requestMap.put(General.SEARCH, "");
        requestMap.put(General.YOUTH_ID, Preferences.get(General.CONSUMER_ID));

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
                    progressList = MhawProgressList_.parseProgressList(response, Actions_.GET_PROGRESS_NOTE_MHAW, this, TAG);
                    if (progressList.size() > 0) {
                        if (progressList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            notesCount.setText(progressList.size() + " Notes Found");
                            MhawProgressListAdapter progressListAdapter = new MhawProgressListAdapter(this, progressList, this);
                            recyclerView.setAdapter(progressListAdapter);
                            popupWindow.dismiss();
//                            clearData();
                        } else {
                            notesCount.setText("0 Notes Found");
                            showError(true, progressList.get(0).getStatus());
                            popupWindow.dismiss();
//                            moodIds.clear();
//                            clearData();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        requestMap.put(General.ACTION, Actions_.GET_PROGRESS_NOTE_MHAW);
        requestMap.put(General.USER_ID, Preferences.get(General.NOTE_USER_ID));
        requestMap.put(General.YOUTH_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.SEARCH, "");
        requestMap.put(General.DATE_TYPE, "0");
//        requestMap.put(General.MOOD, "0");

        if (sort.equalsIgnoreCase("ASC")) {
            requestMap.put(General.SORT, "ASC");
        } else {
            requestMap.put(General.SORT, "DESC");
        }
        Log.e("requestMap",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("PNResponse", response);
                if (response != null) {
                    progressList = MhawProgressList_.parseProgressList(response, Actions_.GET_PROGRESS_NOTE_MHAW, this, TAG);
                    if (progressList.size() > 0) {
                        if (progressList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            notesCount.setText(progressList.size() + " Notes Found");
                            /*All Data will bind here and set in adapter*/
                            MhawProgressListAdapter progressListAdapter = new MhawProgressListAdapter(this, progressList, this);
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
                Log.e("errorPNListActivity", e.getMessage());
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
                Intent addProgressNoteIntent = new Intent(this, MhawAddProgressNoteActivity.class);
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
    public void onNoteDetailsLayoutClicked(MhawProgressList progressNote) {
        Intent progressNoteDetails = new Intent(this, MhawProgressNoteDetailsActivity.class);
        progressNoteDetails.putExtra(General.NOTE_DETAILS, progressNote);
        startActivity(progressNoteDetails);
    }
}
