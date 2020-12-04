package com.modules.re_assignment.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.journaling.model.Student_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class CreateReAssignmentActivity extends AppCompatActivity {
    private static final String TAG = CreateReAssignmentActivity.class.getSimpleName();
    private Toolbar toolbar;
    private AppCompatImageView postButton;
    private Spinner spinnerStudentList, spinnerCoachList;
    private RadioGroup RadioGroup;
    private RadioButton coachRadioBtn, studentRadioBtn, otherRadioBtn;
    private Boolean coachState = false, studentState = false, otherState = false, otherSelected = false;
    private EditText otherEditText;
    private RelativeLayout otherLayout;
    private ArrayList<Student_> studentList = new ArrayList<>(), coachList = new ArrayList<>();
    private String blockCharacterSet = "/*!@#$%^&*\\\"{}[]|\\\\?/<>:;§£¥+÷×=~?";
    private String otherReason = "";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_create_re_assignment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(100, 0, 0, 0);
        titleText.setText(this.getResources().getString(R.string.re_assignment));
        postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);

        initUI();

        setClickListeners();
    }

    private void initUI() {
        spinnerStudentList = (Spinner) findViewById(R.id.spinner_student_list);
        spinnerCoachList = (Spinner) findViewById(R.id.spinner_coach_list);
        spinnerStudentList.setOnItemSelectedListener(onStudentSelected);
        spinnerCoachList.setOnItemSelectedListener(onCoachSelected);

        RadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        coachRadioBtn = (RadioButton) findViewById(R.id.coach_radio_btn);
        studentRadioBtn = (RadioButton) findViewById(R.id.student_radio_btn);
        otherRadioBtn = (RadioButton) findViewById(R.id.other_radio_btn);
        otherEditText = (EditText) findViewById(R.id.other_edit_txt);
        otherLayout = (RelativeLayout) findViewById(R.id.other_layout);

        otherEditText.setFilters(new InputFilter[]{filter});
    }

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    private void setClickListeners() {
        RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.coach_radio_btn) {
                    otherLayout.setVisibility(View.GONE);
                    otherSelected = false;
                } else if (checkedId == R.id.student_radio_btn) {
                    otherLayout.setVisibility(View.GONE);
                    otherSelected = false;
                } else if (checkedId == R.id.other_radio_btn) {
                    otherLayout.setVisibility(View.VISIBLE);
                    otherSelected = true;
                }
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otherReasonTxt = otherEditText.getText().toString().trim();

                if (Validation(otherReasonTxt, v)) {

                    coachState = coachRadioBtn.isChecked();
                    studentState = studentRadioBtn.isChecked();
                    otherState = otherRadioBtn.isChecked();

                    if (coachState) {
                        otherReason = "1";
                    } else if (studentState) {
                        otherReason = "2";
                    } else if (otherState) {
                        otherReason = "3";
                    }
                    //Calling API RE-ASSIGNMENT
                    CreateReassignmentAPI(otherReasonTxt);
                }

            }
        });
    }

    private void CreateReassignmentAPI(String reason) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, General.REASSIGMENT);
        requestMap.put(General.STUDENT, Preferences.get(General.STUD_ID));
        requestMap.put(General.COACH, Preferences.get(General.COACH_ID));
        requestMap.put(General.REASON, otherReason);

        if (otherReason.equals("3")) {
            requestMap.put(General.OTHER, reason);
        } else {
            requestMap.put(General.OTHER, "");
        }
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    final JsonObject jsonAddJournal = jsonObject.getAsJsonObject(General.REASSIGMENT);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CreateReAssignmentActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        }, 2000);
                    } else {
                        Toast.makeText(CreateReAssignmentActivity.this, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
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
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));

        //load the data of student list
        loadStudentList();

        //load the data of coach list
        loadCoachList();
    }

    private void loadStudentList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ASSIGNED_STUDENT);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    studentList = new ArrayList<>();

                    Student_ student_ = new Student_();
                    student_.setId(0);
                    student_.setName("Select Student");
                    student_.setStatus(1);

                    studentList.add(student_);

                    studentList.addAll(CaseloadParser_.parseStudentList(response, Actions_.GET_ASSIGNED_STUDENT, getApplicationContext(), TAG));

                    if (studentList.get(0).getStatus() == 1) {

                        ArrayList<String> studentNameList = new ArrayList<String>();
                        for (int i = 0; i < studentList.size(); i++) {
                            studentNameList.add(studentList.get(i).getName());
                        }

                        if (studentNameList.size() > 0) {
                            ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, studentNameList);
                            adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                            spinnerStudentList.setAdapter(adapterConsumer);
                        }

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
            spinnerStudentList.setSelection(position);
            Preferences.save(General.STUD_ID, studentList.get(position).getId());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void loadCoachList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_COACH_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    coachList = new ArrayList<>();

                    Student_ student_ = new Student_();
                    student_.setId(0);
                    student_.setName("Select Coach");
                    student_.setStatus(1);

                    coachList.add(student_);

                    coachList.addAll(CaseloadParser_.parseStudentList(response, Actions_.GET_COACH_LIST, getApplicationContext(), TAG));

                    if (coachList.get(0).getStatus() == 1) {

                        ArrayList<String> coachNameList = new ArrayList<String>();
                        for (int i = 0; i < coachList.size(); i++) {
                            if (i == 0) {
                                coachNameList.add(coachList.get(i).getName());
                            } else {
                                coachNameList.add(coachList.get(i).getName() + " (" + coachList.get(i).getTotal_assigned() + ")");
                            }
                        }

                        if (coachNameList.size() > 0) {
                            ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, coachNameList);
                            adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                            spinnerCoachList.setAdapter(adapterConsumer);
                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final AdapterView.OnItemSelectedListener onCoachSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerCoachList.setSelection(position);
            Preferences.save(General.COACH_ID, coachList.get(position).getId());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private boolean Validation(String otherReason, View view) {

        if (spinnerStudentList.getSelectedItem().toString().equalsIgnoreCase("Select Student")) {
            ShowSnack.viewWarning(view, "Please select student", getApplicationContext());
            return false;
        }

        if (spinnerCoachList.getSelectedItem().toString().equalsIgnoreCase("Select Coach")) {
            ShowSnack.viewWarning(view, "Please select coach", getApplicationContext());
            return false;
        }

        if (otherSelected) {
            if (otherReason == null || otherReason.trim().length() <= 0) {
                ShowSnack.viewWarning(view, "Please enter other reason", getApplicationContext());
                return false;
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
