package com.modules.goal_assignment.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.modules.goal_assignment.adapters.CoachGoalAssignmentAdapter;
import com.modules.goal_assignment.adapters.SupervisorGoalAssignmentAdapter;
import com.modules.goal_assignment.dialog.SingleChoiceSelectorDialog;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 12/18/2019.
 */
public class GoalAssignmentFragment extends Fragment {
    private static final String TAG = GoalAssignmentFragment.class.getSimpleName();
    private ArrayList<Choices_> goalArrayList = new ArrayList<>();
    private ArrayList<Choices_> districtArrayList = new ArrayList<>(), schoolArrayList = new ArrayList<>(), gradeArrayList = new ArrayList<>(),
            studentArrayList = new ArrayList<>();
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeMenuListView listView;
    private LinearLayout errorLayout, linearLayoutInfo, mainLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private ImageView downArrow, upArrow;
    private MainActivityInterface mainActivityInterface;
    private TextView districtDropdown, schoolDropdown, gradeDropdown, studentDropdown;
    private long district_id = 0, school_id = 0, grade_id = 0, stud_id = 0;
    private boolean showStudent = false, selectDist = false;
    private SupervisorGoalAssignmentAdapter supervisorGoalAssignmentAdapter;
    private CoachGoalAssignmentAdapter coachGoalAssignmentAdapter;
    private ScrollView mNestedScrollView;

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

        View view = inflater.inflate(R.layout.goal_assignment_layout, null);
        activity = getActivity();

        initUI(view);

        setClickListeners();

        return view;
    }

    private void initUI(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        mNestedScrollView = view.findViewById(R.id.nestedScrollView);
//        ViewCompat.setNestedScrollingEnabled(mNestedScrollView,true);
        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setNestedScrollingEnabled(true);
        listView.setDividerHeight(10);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        linearLayoutInfo = (LinearLayout) view.findViewById(R.id.linearlayout_info);
        mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        downArrow = (ImageView) view.findViewById(R.id.down_arrow);
        upArrow = (ImageView) view.findViewById(R.id.up_arrow);
        districtDropdown = (TextView) view.findViewById(R.id.district_name_dropdown);
        schoolDropdown = (TextView) view.findViewById(R.id.school_name_dropdown);
        gradeDropdown = (TextView) view.findViewById(R.id.grade_dropdown);
        studentDropdown = (TextView) view.findViewById(R.id.student_name_dropdown);
    }

    private void setClickListeners() {
        linearLayoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (goalArrayList.size() == 0) {
                    mainLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                } else {
                    mainLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }

                downArrow.setVisibility(View.GONE);
                upArrow.setVisibility(View.VISIBLE);
            }
        });

        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goalArrayList.size() == 0) {
                    mainLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                } else {
                    mainLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
                downArrow.setVisibility(View.GONE);
                upArrow.setVisibility(View.VISIBLE);
            }
        });

        upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                downArrow.setVisibility(View.VISIBLE);
                upArrow.setVisibility(View.GONE);
            }
        });

        districtDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                selectDist = true;

                schoolArrayList.clear();
                gradeArrayList.clear();
                studentArrayList.clear();
                goalArrayList.clear();

                openDistrictSelector(Actions_.GET_DISTRICT_LIST, districtArrayList);

                if (schoolArrayList.size() == 0) {
                    schoolDropdown.setText("Select School");
                    gradeDropdown.setText("All");
                    studentDropdown.setText("All");
                }
            }
        });

        schoolDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                showStudent = true;

                gradeArrayList.clear();
                studentArrayList.clear();
                goalArrayList.clear();

                if (schoolArrayList != null && schoolArrayList.size() > 0) {
                    openDistrictSelector(Actions_.GET_SCHOOL_LIST, schoolArrayList);
                } else {
                    ShowToast.toast("Please select district", activity);
                }
            }
        });

        gradeDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                showStudent = false;

                studentArrayList.clear();
                goalArrayList.clear();

                if (gradeArrayList != null && gradeArrayList.size() > 0) {
                    openDistrictSelector(Actions_.SCHOOL_LIST_OF_GRADE, gradeArrayList);
                } else {
                    ShowToast.toast("Please select school name", activity);
                }
            }
        });

        studentDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (studentArrayList != null && studentArrayList.size() > 0) {
                    openDistrictSelector(Actions_.GET_STUDENT_UNDER_SCHOOL, studentArrayList);
                } else {
                    if (selectDist) {
                        ShowToast.toast("Please select school", activity);
                    } else {
                        ShowToast.toast("Please select district", activity);
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Choices_ choices = goalArrayList.get(position);
                studentDropdown.setText(choices.getName());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.goal_assignment));
        mainActivityInterface.setToolbarBackgroundColor();
        // calling web service fetching district list
        getDistrictList();
    }

    private void getDistrictList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_DISTRICT_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_STUDENT_MODULE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    districtArrayList = SelfGoal_.parseSchoolList(response, Actions_.GET_DISTRICT_LIST, activity.getApplicationContext(), TAG);
                    if (districtArrayList.size() > 0) {
                        if (districtArrayList.get(0).getStatus() == 2) {
                            ShowToast.toast("No District", activity);
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

    private void openDistrictSelector(String action, ArrayList<Choices_> schoolArrayList) {
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        android.app.Fragment frag;

        if (Actions_.GET_DISTRICT_LIST.equals(action)) {
            frag = getActivity().getFragmentManager().findFragmentByTag(Actions_.GET_DISTRICT_LIST);
        } else if (Actions_.GET_SCHOOL_LIST.equals(action)) {
            frag = getActivity().getFragmentManager().findFragmentByTag(Actions_.GET_SCHOOL_LIST);
        } else if (Actions_.SCHOOL_LIST_OF_GRADE.equals(action)) {
            frag = getActivity().getFragmentManager().findFragmentByTag(General.SCHOOL_LIST_OF_GRADE);
        } else {
            frag = getActivity().getFragmentManager().findFragmentByTag(Actions_.GET_STUDENT_UNDER_SCHOOL);
        }

        if (frag != null) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.addToBackStack(null);

         DialogFragment dialogFrag = new SingleChoiceSelectorDialog();
        Bundle bundle = new Bundle();

        if (Actions_.GET_DISTRICT_LIST.equals(action)) {
            bundle.putSerializable(Actions_.GET_DISTRICT_LIST, schoolArrayList);
        } else if (Actions_.GET_SCHOOL_LIST.equals(action)) {
            bundle.putSerializable(Actions_.GET_SCHOOL_LIST, schoolArrayList);
        } else if (Actions_.SCHOOL_LIST_OF_GRADE.equals(action)) {
            bundle.putSerializable(General.SCHOOL_LIST_OF_GRADE, schoolArrayList);
        } else {
            bundle.putSerializable(Actions_.GET_STUDENT_UNDER_SCHOOL, schoolArrayList);
        }

        bundle.putString(General.ACTION, action);
        dialogFrag.setArguments(bundle);
        dialogFrag.setTargetFragment(this, 1);

        if (Actions_.GET_DISTRICT_LIST.equals(action)) {
            dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.GET_DISTRICT_LIST);
        } else if (Actions_.GET_SCHOOL_LIST.equals(action)) {
            dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.GET_SCHOOL_LIST);
        } else if (Actions_.SCHOOL_LIST_OF_GRADE.equals(action)) {
            dialogFrag.show(getFragmentManager().beginTransaction(), General.SCHOOL_LIST_OF_GRADE);
        } else {
            dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.GET_STUDENT_UNDER_SCHOOL);
        }
    }

    // Get selected item position from result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    String position = data.getStringExtra("position");
                    setDistrict(Integer.parseInt(position));
                }

                if (resultCode == 2) {
                    String position = data.getStringExtra("position");
                    setSchool(Integer.parseInt(position));
                }

                if (resultCode == 3) {
                    String position = data.getStringExtra("position");
                    setGrades(Integer.parseInt(position));
                }

                if (resultCode == 4) {
                    String position = data.getStringExtra("position");
                    setStudent(Integer.parseInt(position));
                }
                break;
        }
    }

    private void setDistrict(int position) {
        district_id = districtArrayList.get(position).getId();
        districtDropdown.setText(districtArrayList.get(position).getName());
        //Set school Data
        getSchoolList(district_id);
    }

    private void setSchool(int position) {
        school_id = schoolArrayList.get(position).getId();
        schoolDropdown.setText(schoolArrayList.get(position).getName());
        //set grades data
        setGradesListData();

        //list of student
        getStudentList(school_id, 0);

        getStudentGoalList(school_id, 0);
    }

    private void setGrades(int position) {
        grade_id = gradeArrayList.get(position).getId();
        gradeDropdown.setText(gradeArrayList.get(position).getName());

        //list of student
        getStudentList(school_id, grade_id);

        getStudentGoalList(school_id, grade_id);
    }

    private void setStudent(int position) {
        stud_id = studentArrayList.get(position).getId();
        studentDropdown.setText(studentArrayList.get(position).getName());

        Preferences.save(General.STUDENT_ASSIGN_NAME, studentArrayList.get(position).getName());
        getStudentGoalList(school_id, grade_id);
    }

    private void getSchoolList(long district_id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SCHOOL_LIST);
        requestMap.put(General.DIST_ID, String.valueOf(district_id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_STUDENT_MODULE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    schoolArrayList = SelfGoal_.parseSchoolList(response, Actions_.GET_SCHOOL_LIST, activity.getApplicationContext(), TAG);
                    if (schoolArrayList.size() > 0) {
                        if (schoolArrayList.get(0).getStatus() == 2) {
                            ShowToast.toast("No School", activity);
                        } else {
                            supervisorGoalAssignmentAdapter.notifyDataSetChanged();
                            coachGoalAssignmentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private void getStudentList(long school_id, long grade_id) {
        studentArrayList.clear();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.STUDENT_LIST_OF_COACH);
        requestMap.put(General.SCHOOL_ID, String.valueOf(school_id));

        if (showStudent) {
            requestMap.put(General.STD_ID, "0");
        } else {
            requestMap.put(General.STD_ID, String.valueOf(grade_id));
        }

        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    studentArrayList = SelfGoal_.parseSchoolList(response, Actions_.STUDENT_LIST_OF_COACH, activity, TAG);
                    if (studentArrayList.size() > 0) {
                        if (studentArrayList.get(0).getStatus() == 2) {
                            ShowToast.toast("No Data", activity);
                            supervisorGoalAssignmentAdapter.notifyDataSetChanged();
                            coachGoalAssignmentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getStudentGoalList(long school_id, long grade_id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.STUDENT_LIST_OF_COACH);
        requestMap.put(General.SCHOOL_ID, String.valueOf(school_id));

        if (showStudent) {
            requestMap.put(General.STD_ID, "0");
        } else {
            requestMap.put(General.STD_ID, String.valueOf(grade_id));
        }

        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    goalArrayList = SelfGoal_.parseSchoolList(response, Actions_.STUDENT_LIST_OF_COACH, activity.getApplicationContext(), TAG);
                    if (goalArrayList.size() > 0) {
                        if (goalArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);

                            if (Preferences.get(General.ROLE_ID).equals("6")) {
                                coachGoalAssignmentAdapter = new CoachGoalAssignmentAdapter(activity, goalArrayList);
                                listView.setAdapter(coachGoalAssignmentAdapter);
                            } else {
                                supervisorGoalAssignmentAdapter = new SupervisorGoalAssignmentAdapter(activity, goalArrayList);
                                listView.setAdapter(supervisorGoalAssignmentAdapter);
                            }

                        } else {
                            showError(true, 2);
                            supervisorGoalAssignmentAdapter.notifyDataSetChanged();
                            coachGoalAssignmentAdapter.notifyDataSetChanged();
                        }
                    } else {
                        showError(true, 12);
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
