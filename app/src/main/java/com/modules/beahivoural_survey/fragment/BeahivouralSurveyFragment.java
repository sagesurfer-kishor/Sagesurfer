package com.modules.beahivoural_survey.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.modules.beahivoural_survey.adapter.BehaviourHealthServeyAdapter;
import com.modules.beahivoural_survey.model.BehaviouralHealth;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.BehaviouralServey_;
import com.sagesurfer.parser.SelfGoal_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

public class BeahivouralSurveyFragment extends Fragment {
    private static final String TAG = BeahivouralSurveyFragment.class.getSimpleName();
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private ListView listView;
    private Spinner spinnerStudent;
    private ImageButton healthServeyFilter;
    private ArrayList<Choices_> studentArrayList = new ArrayList<>();
    private ArrayList<BehaviouralHealth> behaviouralArrayList = new ArrayList<>();
    private ArrayAdapter<String> studentServeyAdapter;
    private BehaviourHealthServeyAdapter behaviourHealthServeyAdapter;
    private PopupWindow popupWindow = new PopupWindow();
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date = "", end_date = "", lastWeek = "", lastMonth = "";

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.behaviour_health_fragment_layout, null);

        activity = getActivity();

        initUI(view);

        setOnClickListeners();

        return view;
    }

    private void initUI(View view) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(20);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        spinnerStudent = (Spinner) view.findViewById(R.id.spinner_student);
        healthServeyFilter = (ImageButton) view.findViewById(R.id.servey_filter);
    }

    private void setOnClickListeners() {
        healthServeyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                healthServeyDilaog(v);
            }
        });
    }

    private void healthServeyDilaog(View v) {
        try {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.dialog_wall_filter, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            popupWindow.isShowing();

            final LinearLayout dateLayout, dateSelectionLayout;
            final TextView clearDateSelection, title;
            final TextView startDate, endDate;
            final ImageView imageviewSave, imageviewBack;
            final Calendar calendar;
            final CheckBox lastWeekCheckBox, lastMonthCheckBox;

            dateLayout = customView.findViewById(R.id.date_layout);
            dateSelectionLayout = customView.findViewById(R.id.date_selection);
            lastWeekCheckBox = customView.findViewById(R.id.check_box_week);
            lastMonthCheckBox = customView.findViewById(R.id.check_box_month);
            clearDateSelection = customView.findViewById(R.id.clear_selection_date);
            title = customView.findViewById(R.id.journal_header);
            imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
            imageviewBack = customView.findViewById(R.id.imageview_back);
            startDate = customView.findViewById(R.id.start_date_txt);
            endDate = customView.findViewById(R.id.end_date_txt);

            title.setText("Health Survey Filter");

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
                }
            });

            calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);


            startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
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
                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                    try {
                                        endDate.setText(end_date);
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
                    } else {
                        lastMonth = "";
                        Preferences.save("last_month", false);
                    }
                }
            });

            imageviewSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterDateWiseBehaviouralData();
                }
            });

            imageviewBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterDateWiseBehaviouralData() {
        behaviouralArrayList.clear();
        showError(true, 20);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_FROM_LIST);
        requestMap.put(General.STUD_ID, Preferences.get(General.STUD_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

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

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_BEHAVIOURAL_HEALTH_SERVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    behaviouralArrayList = BehaviouralServey_.parseBehaviouralList(response, Actions_.GET_FROM_LIST, activity.getApplicationContext(), TAG);

                    if (behaviouralArrayList.size() > 0) {
                        if (behaviouralArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            behaviourHealthServeyAdapter = new BehaviourHealthServeyAdapter(activity, behaviouralArrayList);
                            listView.setAdapter(behaviourHealthServeyAdapter);
                            popupWindow.dismiss();
                        } else {
                            showError(true, behaviouralArrayList.get(0).getStatus());
                            popupWindow.dismiss();
                        }
                    } else {
                        showError(true, 2);
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }


    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.behavioural_survey));
        mainActivityInterface.setToolbarBackgroundColor();

        fetchStudentList();
    }

    private void fetchStudentList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.STUDENT_UNDER_COACH);
        requestMap.put(General.COACH_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_BEHAVIOURAL_HEALTH_SERVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.v("response-->", response);
                if (response != null) {
                    studentArrayList = new ArrayList<>();
                    Choices_ role = new Choices_();
                    role.setId(0);
                    role.setName("Select Student");
                    role.setStatus(1);
                    studentArrayList.add(role);

                    studentArrayList.addAll(SelfGoal_.parseSchoolList(response, Actions_.STUDENT_UNDER_COACH, activity, TAG));

                    ArrayList<String> studentNameList = new ArrayList<String>();
                    for (int i = 0; i < studentArrayList.size(); i++) {
                        studentNameList.add(studentArrayList.get(i).getName());
                    }

                    if (studentArrayList.size() > 0) {
                        studentServeyAdapter = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, studentNameList);
                        studentServeyAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinnerStudent.setAdapter(studentServeyAdapter);

                        for (int i = 0; i < studentArrayList.size(); i++) {
                            if (Integer.parseInt(Preferences.get(General.NOTIFICATION_BHS_ID)) == studentArrayList.get(i).getId()) {
                                spinnerStudent.setSelection(i);
                                spinnerStudent.setOnItemSelectedListener(onStudentSelected);
                                break;
                            }
                        }

                        showError(true, 2);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final AdapterView.OnItemSelectedListener onStudentSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerStudent.setSelection(position);
            if (position == 0) {
                Preferences.save(General.STUD_ID, "0");
                showError(true, 2);
            } else {
                Preferences.save(General.STUD_ID, studentArrayList.get(position).getId());
                getListFromStudent();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void getListFromStudent() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_FROM_LIST);
        requestMap.put(General.STUD_ID, Preferences.get(General.STUD_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.DATE_TYPE, "0");
        requestMap.put(General.START_DATE, "");
        requestMap.put(General.END_DATE, "");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_BEHAVIOURAL_HEALTH_SERVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.v("response-->", response);
                if (response != null) {
                    behaviouralArrayList = BehaviouralServey_.parseBehaviouralList(response, Actions_.GET_FROM_LIST, activity.getApplicationContext(), TAG);
                    if (behaviouralArrayList.size() > 0) {
                        if (behaviouralArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            behaviourHealthServeyAdapter = new BehaviourHealthServeyAdapter(activity, behaviouralArrayList);
                            listView.setAdapter(behaviourHealthServeyAdapter);
                        } else {
                            showError(true, 2);
                        }
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
            listView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}

