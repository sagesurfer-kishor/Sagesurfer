package com.modules.selfgoal.werhope_self_goal.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.modules.selfgoal.werhope_self_goal.adapter.DefaultGoalsAdapter;
import com.modules.selfgoal.werhope_self_goal.dialog.SingleGoalSelectorDialog;
import com.modules.selfgoal.werhope_self_goal.dialog.SingleSchoolSelectorDialog;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class StudentReportFragment extends Fragment {
    private static final String TAG = StudentReportFragment.class.getSimpleName();
    private ArrayList<Goal_> goalArrayList = new ArrayList<>();
    private ArrayList<Choices_> schoolArrayList = new ArrayList<>(), studentArrayList = new ArrayList<>(), gradeArrayList = new ArrayList<>();
    private Activity activity;
    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText, noDataTxt;
    private AppCompatImageView errorIcon;
    private MainActivityInterface mainActivityInterface;
    private TextView schoolDropdown, standardDropdown, studentDropdown, goalDropdown;
    private long school_id = 0, std_id = 0, stud_id = 0;

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

        View view = inflater.inflate(R.layout.student_report_layout, null);
        activity = getActivity();

        initUI(view);

        setClickListeners();

        return view;
    }

    private void initUI(View view) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(15);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        noDataTxt = (TextView) view.findViewById(R.id.no_data_label);

        schoolDropdown = (TextView) view.findViewById(R.id.school_name_dropdown);
        standardDropdown = (TextView) view.findViewById(R.id.standard_dropdown);
        studentDropdown = (TextView) view.findViewById(R.id.student_dropdown);
        goalDropdown = (TextView) view.findViewById(R.id.goal_dropdown);
    }

    private void setClickListeners() {
        schoolDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (schoolArrayList != null && schoolArrayList.size() > 0) {
                    openStudentSelector(Actions_.SCHOOL_LIST_OF_COACH, schoolArrayList);
                } else {
                    ShowToast.toast("Please select school name", activity);
                }
            }
        });

        standardDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (gradeArrayList != null && gradeArrayList.size() > 0) {
                    openStudentSelector(Actions_.SCHOOL_LIST_OF_GRADE, gradeArrayList);
                } else {
                    ShowToast.toast("Please select grade", activity);
                }
            }
        });

        studentDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (studentArrayList != null && studentArrayList.size() > 0) {
                    openStudentSelector(Actions_.STUDENT_LIST_OF_COACH, studentArrayList);
                } else {
                    ShowToast.toast("Please select student name", activity);
                }
            }
        });

        goalDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (goalArrayList != null && goalArrayList.size() > 0) {
                    openGoalSelector(Actions_.STUDENT_GOAL_FOR_COACH, goalArrayList);
                } else {
                    ShowToast.toast("Please select goal name", activity);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.student_report));
        mainActivityInterface.setToolbarBackgroundColor();
        // calling web service fetching school list

        if (Preferences.get(General.ROLE_ID).equals(General.USER_ROLE_WERHOPE_EXECUTIVE_DIRECTOR) ||
                Preferences.get(General.ROLE_ID).equals(General.USER_ROLE_WERHOPE_DIRECTOR_OF_OPERATIONS)){
            getSchoolsList(Actions_.GET_SCHOOL_LIST);

        }else {
            getSchoolsList(Actions_.SCHOOL_LIST_OF_COACH);
        }

    }

    private void getSchoolsList(String actions) {
        String url = "";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, actions);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        if (Preferences.get(General.ROLE_ID).equals(General.USER_ROLE_WERHOPE_EXECUTIVE_DIRECTOR) ||
                Preferences.get(General.ROLE_ID).equals(General.USER_ROLE_WERHOPE_DIRECTOR_OF_OPERATIONS)){
            url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_SCHOOL;
        }else {
            url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        }


        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                Log.e("SchoolResponse",response);
                if (response != null) {
                    if (actions.equals(Actions_.GET_SCHOOL_LIST)){
                        schoolArrayList = SelfGoal_.parseSchoolList(response, Actions_.GET_SCHOOL_LIST, activity.getApplicationContext(), TAG);
                    }else {
                        schoolArrayList = SelfGoal_.parseSchoolList(response, Actions_.SCHOOL_LIST_OF_COACH, activity.getApplicationContext(), TAG);
                    }

                    if (schoolArrayList.size() > 0) {
                        if (schoolArrayList.get(0).getStatus() == 2) {
                            ShowToast.toast("No School", activity);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void hideKeyboard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void openStudentSelector(String action, ArrayList<Choices_> schoolArrayList) {
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        android.app.Fragment frag;

        if (Actions_.STUDENT_LIST_OF_COACH.equals(action)) {
            frag = getActivity().getFragmentManager().findFragmentByTag(General.STUDENT_LIST_OF_COACH);
        } else if (Actions_.SCHOOL_LIST_OF_GRADE.equals(action)) {
            frag = getActivity().getFragmentManager().findFragmentByTag(General.SCHOOL_LIST_OF_GRADE);
        } else {
            frag = getActivity().getFragmentManager().findFragmentByTag(General.SCHOOL_LIST_OF_COACH);
        }

        if (frag != null) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.addToBackStack(null);

         DialogFragment dialogFrag = new SingleSchoolSelectorDialog();
        Bundle bundle = new Bundle();

        if (Actions_.STUDENT_LIST_OF_COACH.equals(action)) {
            bundle.putSerializable(General.STUDENT_LIST_OF_COACH, schoolArrayList);
        } else if (Actions_.SCHOOL_LIST_OF_GRADE.equals(action)) {
            bundle.putSerializable(General.SCHOOL_LIST_OF_GRADE, schoolArrayList);
        } else {
            bundle.putSerializable(General.SCHOOL_LIST_OF_COACH, schoolArrayList);
        }

        bundle.putString(General.ACTION, action);
        dialogFrag.setArguments(bundle);
        dialogFrag.setTargetFragment(this, 1);

        if (Actions_.STUDENT_LIST_OF_COACH.equals(action)) {
            dialogFrag.show(getFragmentManager().beginTransaction(), General.STUDENT_LIST_OF_COACH);
        } else if (Actions_.SCHOOL_LIST_OF_GRADE.equals(action)) {
            dialogFrag.show(getFragmentManager().beginTransaction(), General.SCHOOL_LIST_OF_GRADE);
        } else {
            dialogFrag.show(getFragmentManager().beginTransaction(), General.SCHOOL_LIST_OF_COACH);
        }
    }

    private void openGoalSelector(String action, ArrayList<Goal_> goalArrayList) {
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        android.app.Fragment frag = getActivity().getFragmentManager().findFragmentByTag(Actions_.STUDENT_GOAL_FOR_COACH);

        if (frag != null) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment dialogFrag = new SingleGoalSelectorDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Actions_.STUDENT_GOAL_FOR_COACH, goalArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.setTargetFragment(this, 1);
        dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.STUDENT_GOAL_FOR_COACH);
    }

    // Get selected item position from result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    String position = data.getStringExtra("position");
                    setSchool(Integer.parseInt(position));
                }

                if (resultCode == 2) {
                    String position = data.getStringExtra("position");
                    setStudent(Integer.parseInt(position));
                }

                if (resultCode == 3) {
                    String position = data.getStringExtra("position");
                    setGrades(Integer.parseInt(position));
                }

                if (resultCode == 4) {
                    String position = data.getStringExtra("position");
                    setGoal(Integer.parseInt(position));
                }

                break;
        }
    }

    private void setGoal(int position) {
        goalDropdown.setText(goalArrayList.get(position).getName());
        Preferences.save(General.GOAL_NAME, goalArrayList.get(position).getName());
        DefaultGoalsAdapter defaultGoalsAdapter = new DefaultGoalsAdapter(activity, goalArrayList);
        listView.setAdapter(defaultGoalsAdapter);
        listView.setSelection(position);
    }

    private void setSchool(int position) {
        school_id = schoolArrayList.get(position).getSchool_id();
        schoolDropdown.setText(schoolArrayList.get(position).getName());
        standardDropdown.setText("All");

        //Set Grade Data
        setGradesListData();

        //Calling web service fetching student list
        getStudentsList(0);
    }

    private void setGradesListData() {
        for (int i = 0; i < 13; i++) {
            Choices_ choices_ = new Choices_();
            choices_.setId(i);
            if (i == 0) {
                choices_.setName("All");
            } else if (i == 1) {
                choices_.setName("1st");
            } else if (i == 2) {
                choices_.setName("2nd");
            } else if (i == 3) {
                choices_.setName("3rd");
            } else if (i == 4) {
                choices_.setName("4th");
            } else if (i == 5) {
                choices_.setName("5th");
            } else if (i == 6) {
                choices_.setName("6th");
            } else if (i == 7) {
                choices_.setName("7th");
            } else if (i == 8) {
                choices_.setName("8th");
            } else if (i == 9) {
                choices_.setName("9th");
            } else if (i == 10) {
                choices_.setName("10th");
            } else if (i == 11) {
                choices_.setName("11th");
            } else {
                choices_.setName("12th");
            }
            choices_.setStatus(1);
            gradeArrayList.add(choices_);
        }
    }

    private void setGrades(int position) {
        std_id = gradeArrayList.get(position).getId();
        standardDropdown.setText(gradeArrayList.get(position).getName());

        //Calling web service fetching student list
        getStudentsList(std_id);
    }

    private void getStudentsList(long std_id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.STUDENT_LIST_OF_COACH);
        requestMap.put(General.SCHOOL_ID, String.valueOf(school_id));
        requestMap.put(General.STD_ID, String.valueOf(std_id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                Log.e("StudentResponse",response);
                if (response != null) {
                    studentArrayList = SelfGoal_.parseSchoolList(response, Actions_.STUDENT_LIST_OF_COACH, activity.getApplicationContext(), TAG);
                    if (studentArrayList.size() > 0) {
                        if (studentArrayList.get(0).getStatus() == 2) {
                            ShowToast.toast("No Students", activity);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setStudent(int position) {
        stud_id = studentArrayList.get(position).getId();
        studentDropdown.setText(studentArrayList.get(position).getName());

        Preferences.save(General.CONSUMER_ID, stud_id);

        // calling web service fetching student goals
        getStudentGoalList(stud_id);
    }

    private void getStudentGoalList(long stud_id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.STUDENT_GOAL_FOR_COACH);
        requestMap.put(General.STUDENT_ID, String.valueOf(stud_id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    goalArrayList = SelfGoal_.parseDefaultsSelfGoal(response, Actions_.STUDENT_GOAL_FOR_COACH, activity.getApplicationContext(), TAG);
                    if (goalArrayList.size() > 0) {
                        if (goalArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            noDataTxt.setVisibility(View.GONE);
                            DefaultGoalsAdapter defaultGoalsAdapter = new DefaultGoalsAdapter(activity, goalArrayList);
                            listView.setAdapter(defaultGoalsAdapter);
                        } else {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has(Actions_.STUDENT_GOAL_FOR_COACH)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Actions_.STUDENT_GOAL_FOR_COACH);
                                JSONObject object = jsonArray.getJSONObject(0);
                                int status = object.getInt(General.STATUS);
                                if (status == 2) {
                                    showError(true, status);
                                    ShowToast.toast(object.getString(General.ERROR), activity);
                                    noDataTxt.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        showError(true, 12);
                    }
                    return;
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