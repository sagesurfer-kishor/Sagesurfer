package com.modules.assignment.werhope.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.assignment.werhope.dialog.SingleChoiceAssignmentSelectorDialog;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class AssignmentCreateActivity extends AppCompatActivity implements View.OnClickListener, SingleChoiceAssignmentSelectorDialog.GetCategorySelected {
    private static final String TAG = AssignmentCreateActivity.class.getSimpleName();
    private ArrayList<Choices_> districtArrayList, schoolArrayList, studentArrayList, coachArrayList;
    private TextView selectDistrict, selectSchool, selectStudent, selectCoach;
    private Button assignButton;
    private long district_id = 0, school_id = 0, student_id = 0, coach_id = 0;
    private boolean selectDist = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_assignment_create);
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
        titleText.setPadding(150, 0, 0, 0);
        titleText.setText(getResources().getString(R.string.ASSIGNMENT));

        initUI();
    }

    private void initUI() {
        selectDistrict = (TextView) findViewById(R.id.district_dropdown);
        selectSchool = (TextView) findViewById(R.id.school_dropdown);
        selectStudent = (TextView) findViewById(R.id.student_dropdown);
        selectCoach = (TextView) findViewById(R.id.coach_dropdown);
        assignButton = (Button) findViewById(R.id.button_assign);

        selectDistrict.setOnClickListener(this);
        selectSchool.setOnClickListener(this);
        selectStudent.setOnClickListener(this);
        selectCoach.setOnClickListener(this);
        assignButton.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (districtArrayList == null || districtArrayList.size() <= 0) {
            getDistrictList();
        }

        //Calling web service fetching coach list
        getCoachList();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.district_dropdown:
                selectDist = true;
                hideKeyboard();
                if (districtArrayList != null && districtArrayList.size() > 0) {
                    openDistrictSelector(Actions_.GET_DISTRICT_LIST, districtArrayList);
                } else {
                    ShowToast.toast("Please select school name", this);
                }
                break;
            case R.id.school_dropdown:
                hideKeyboard();
                if (schoolArrayList != null && schoolArrayList.size() > 0) {
                    openDistrictSelector(Actions_.GET_SCHOOL_LIST, schoolArrayList);
                } else {
                    ShowToast.toast("Please select district", this);
                }
                break;
            case R.id.student_dropdown:
                hideKeyboard();
                if (studentArrayList != null && studentArrayList.size() > 0) {
                    openDistrictSelector(Actions_.GET_STUDENT_UNDER_SCHOOL, studentArrayList);
                } else {
                    if (selectDist) {
                        ShowToast.toast("Please select school name", this);
                    } else {
                        ShowToast.toast("Please select district", this);
                    }
                }
                break;
            case R.id.coach_dropdown:
                hideKeyboard();
                openDistrictSelector(Actions_.GET_COACH_LIST, coachArrayList);
                break;
            case R.id.button_assign:
                if (assignStudentValidation(v)) {
                    assignmentDilaog();
                }
                break;
        }
    }

    private void assignmentDilaog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_assignment);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final Button buttonNo = (Button) dialog.findViewById(R.id.button_no);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignedStudentToCoach();
                dialog.dismiss();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean assignStudentValidation(View v) {

        if (district_id <= 0) {
            ShowToast.toast("Please select district", this);
            return false;
        }

        if (school_id <= 0) {
            ShowToast.toast("Please select school", this);
            return false;
        }

        if (student_id <= 0) {
            ShowToast.toast("Please select student", this);
            return false;
        }

        if (coach_id <= 0) {
            ShowToast.toast("Please select coach", this);
            return false;
        }

        return true;
    }

    private void assignedStudentToCoach() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ASSIGN);
        requestMap.put(General.STUD_ID, String.valueOf(student_id));
        requestMap.put(General.COACH_ID, String.valueOf(coach_id));
        requestMap.put(General.SCHOOL_ID, String.valueOf(school_id));
        requestMap.put(General.DISTRICT_ID, String.valueOf(district_id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.ASSIGN);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(AssignmentCreateActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(AssignmentCreateActivity.this, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void openDistrictSelector(String action, ArrayList<Choices_> districtArrayList) {
        Bundle bundle = new Bundle();
        SingleChoiceAssignmentSelectorDialog dialogFrag = new SingleChoiceAssignmentSelectorDialog();
        bundle.putSerializable(action, districtArrayList);
        bundle.putString(General.ACTION, action);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), action);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void GetSelectedCategory(Choices_ choices_, String action, boolean isSelected) {
        if (isSelected) {
            if (action.equalsIgnoreCase(Actions_.GET_DISTRICT_LIST)) {
                selectDistrict.setText(choices_.getName());
                district_id = choices_.getId();
                //Calling web service fetching school list
                getSchoolsList(district_id);
            }

            if (action.equalsIgnoreCase(Actions_.GET_SCHOOL_LIST)) {
                selectSchool.setText(choices_.getName());
                school_id = choices_.getId();
                //Calling web service fetching student list
                getStudentList(school_id);
            }

            if (action.equalsIgnoreCase(Actions_.GET_STUDENT_UNDER_SCHOOL)) {
                selectStudent.setText(choices_.getFirstname() + " " + choices_.getLastname());
                student_id = choices_.getId();
            }

            if (action.equalsIgnoreCase(Actions_.GET_COACH_LIST)) {
                selectCoach.setText(choices_.getName() + " (" + choices_.getTotal_assigned() + ") ");
                coach_id = choices_.getId();
            }
        }
    }

    private void getDistrictList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_DISTRICT_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_STUDENT_MODULE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    districtArrayList = SelfGoal_.parseSchoolList(response, Actions_.GET_DISTRICT_LIST, this, TAG);
                    if (districtArrayList.size() > 0) {
                        if (districtArrayList.get(0).getStatus() == 2) {
                            ShowToast.toast("No District", this);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getSchoolsList(long district_id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SCHOOL_LIST);
        requestMap.put(General.DIST_ID, String.valueOf(district_id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_STUDENT_MODULE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    schoolArrayList = SelfGoal_.parseSchoolList(response, Actions_.GET_SCHOOL_LIST, this, TAG);
                    if (schoolArrayList.size() > 0) {
                        if (schoolArrayList.get(0).getStatus() == 2) {
                            ShowToast.toast("No School", this);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getStudentList(long school_id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_STUDENT_UNDER_SCHOOL);
        requestMap.put(General.SCHOOL_ID, String.valueOf(school_id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    studentArrayList = SelfGoal_.parseSchoolList(response, Actions_.GET_STUDENT_UNDER_SCHOOL, this, TAG);
                    if (studentArrayList.size() > 0) {
                        if (studentArrayList.get(0).getStatus() == 2) {
                            ShowToast.toast("No Student", this);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getCoachList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_COACH_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    coachArrayList = SelfGoal_.parseSchoolList(response, Actions_.GET_COACH_LIST, this, TAG);
                    if (coachArrayList.size() > 0) {
                        if (coachArrayList.get(0).getStatus() == 2) {
                            ShowToast.toast("No Coach", this);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
