package com.modules.assignment.werhope.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.assignment.werhope.activity.AssignmentCreateActivity;
import com.modules.assignment.werhope.adapter.StudentlListAdapter;
import com.modules.assignment.werhope.model.School;
import com.modules.assignment.werhope.model.Student;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.parser.School_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

public class AssignmentListingFragment extends Fragment implements View.OnClickListener, StudentlListAdapter.StudentlListAdapterListener {
    private static final String TAG = AssignmentListingFragment.class.getSimpleName();
    private ArrayList<School> schoolArrayList = new ArrayList<>();
    private ArrayList<Student> studentArrayList = new ArrayList<>(), searchStudentArrayList;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLayout, studentListLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private PopupWindow popupWindow = new PopupWindow();
    private StudentlListAdapter studentlListAdapter;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private FloatingActionButton createButton;
    private CardView cardViewActionsSearch;
    private EditText editTextSearch;
    private ImageButton imageButtonSetting, imageButtonFilter;
    private Spinner spinnerStudentList;

    //Assignment listing filter part
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String start_date = "", end_date = "";
    private boolean executiveDirector = false, directorOfOperation = false, registered = false, notRegistered = false;
    private boolean one = false, two = false, three = false, four = false, five = false, six = false, seven = false;
    private boolean eight = false, nine = false, ten = false, eleven = false, twelve = false;
    private String lastWeek = "", lastMonth = "";

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

        View view = inflater.inflate(R.layout.journal_list_layout, null);
        activity = getActivity();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setPadding(20, 0, 20, 20);

        cardViewActionsSearch = (CardView) view.findViewById(R.id.cardview_actions);
        imageButtonSetting = (ImageButton) view.findViewById(R.id.imagebutton_setting);
        imageButtonFilter = (ImageButton) view.findViewById(R.id.notification_filter);
        imageButtonFilter.setVisibility(View.VISIBLE);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        studentListLayout = (LinearLayout) view.findViewById(R.id.spinner_student_layout);
        spinnerStudentList = (Spinner) view.findViewById(R.id.spinner_student_list);

        createButton = (FloatingActionButton) view.findViewById(R.id.fab);
        createButton.setImageResource(R.drawable.assign_icon);
        createButton.setOnClickListener(this);

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            createButton.setVisibility(View.GONE);
            studentListLayout.setVisibility(View.VISIBLE);
        } else {
            createButton.setVisibility(View.VISIBLE);
        }

        subSrearchFunctiaonality(view);

        assignmentFilterData();

        return view;
    }

    private void assignmentFilterData() {
        imageButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View customView = inflater.inflate(R.layout.assignment_filter, null);
                    popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.isShowing();

                    final TextView clearDateSelection, clearDateSelectionOne, clearDateSelectionTwo, clearDateSelectionThree;
                    final TextView startDate, endDate;
                    final ImageView imageviewSave, imageviewBack;
                    final CheckBox EDCheckBox, DOCheckBox;
                    final Calendar calendar;
                    final LinearLayout dateLayout, dateSelectionLayout;
                    final CheckBox lastWeekCheckBox, lastMonthCheckBox, regiStudentCheckBox, notRegiStudentCheckBox;

                    final ImageView oneGray, twoGray, threeGray, fourGray, fiveGray, sixGray;
                    final ImageView sevenGray, eightGray, nineGray, tenGray, elevenGray, twelveGray;

                    EDCheckBox = customView.findViewById(R.id.check_box_ed);
                    DOCheckBox = customView.findViewById(R.id.check_box_do);
                    clearDateSelection = customView.findViewById(R.id.clear_selection);
                    clearDateSelectionOne = customView.findViewById(R.id.clear_selection_date);
                    clearDateSelectionTwo = customView.findViewById(R.id.clear_selection_grade);
                    clearDateSelectionThree = customView.findViewById(R.id.clear_selection_student);

                    imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
                    imageviewBack = customView.findViewById(R.id.imageview_back);
                    startDate = customView.findViewById(R.id.start_date_txt);
                    endDate = customView.findViewById(R.id.end_date_txt);

                    dateLayout = customView.findViewById(R.id.date_layout);
                    dateSelectionLayout = customView.findViewById(R.id.date_selection);
                    lastWeekCheckBox = customView.findViewById(R.id.check_box_week);
                    lastMonthCheckBox = customView.findViewById(R.id.check_box_month);

                    regiStudentCheckBox = customView.findViewById(R.id.registered_students);
                    notRegiStudentCheckBox = customView.findViewById(R.id.not_registered_students);

                    oneGray = customView.findViewById(R.id.one_gray);
                    twoGray = customView.findViewById(R.id.two_gray);
                    threeGray = customView.findViewById(R.id.three_gray);
                    fourGray = customView.findViewById(R.id.four_gray);
                    fiveGray = customView.findViewById(R.id.five_gray);
                    sixGray = customView.findViewById(R.id.six_gray);
                    sevenGray = customView.findViewById(R.id.seven_gray);
                    eightGray = customView.findViewById(R.id.eight_gray);
                    nineGray = customView.findViewById(R.id.nine_gray);
                    tenGray = customView.findViewById(R.id.ten_gray);
                    elevenGray = customView.findViewById(R.id.eleven_gray);
                    twelveGray = customView.findViewById(R.id.twelve_gray);

                    clearDateSelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lastWeekCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                            startDate.setText("");
                            endDate.setText("");
                        }
                    });

                    clearDateSelectionOne.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lastWeekCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                            startDate.setText("");
                            endDate.setText("");
                        }
                    });

                    clearDateSelectionThree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            regiStudentCheckBox.setChecked(false);
                            notRegiStudentCheckBox.setChecked(false);
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

                    oneGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            one = true;
                            oneGray.setImageResource(R.drawable.one_blue);
                        }
                    });
                    twoGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            two = true;
                            twoGray.setImageResource(R.drawable.two_blue);
                        }
                    });

                    threeGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            three = true;
                            threeGray.setImageResource(R.drawable.three_blue);
                        }
                    });

                    fourGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            four = true;
                            fourGray.setImageResource(R.drawable.four_blue);
                        }
                    });

                    fiveGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            five = true;
                            fiveGray.setImageResource(R.drawable.five_blue);
                        }
                    });

                    sixGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            six = true;
                            sixGray.setImageResource(R.drawable.six_blue);
                        }
                    });

                    sevenGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            seven = true;
                            sevenGray.setImageResource(R.drawable.seven_blue);
                        }
                    });

                    eightGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eight = true;
                            eightGray.setImageResource(R.drawable.eight_blue);
                        }
                    });

                    nineGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nine = true;
                            nineGray.setImageResource(R.drawable.nine_blue);
                        }
                    });

                    tenGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ten = true;
                            tenGray.setImageResource(R.drawable.ten_blue);
                        }
                    });

                    elevenGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eleven = true;
                            elevenGray.setImageResource(R.drawable.eleven_blue);
                        }
                    });

                    twelveGray.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            twelve = true;
                            twelveGray.setImageResource(R.drawable.twelve_blue);
                        }
                    });


                    clearDateSelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EDCheckBox.setChecked(false);
                            DOCheckBox.setChecked(false);
                        }
                    });

                    clearDateSelectionTwo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            oneGray.setImageResource(R.drawable.one_gray);
                            twoGray.setImageResource(R.drawable.two_gray);
                            threeGray.setImageResource(R.drawable.three_gray);
                            fourGray.setImageResource(R.drawable.four_gray);
                            fiveGray.setImageResource(R.drawable.five_gray);
                            sixGray.setImageResource(R.drawable.six_gray);
                            sevenGray.setImageResource(R.drawable.seven_gray);
                            eightGray.setImageResource(R.drawable.eight_gray);
                            nineGray.setImageResource(R.drawable.nine_gray);
                            tenGray.setImageResource(R.drawable.ten_gray);
                            elevenGray.setImageResource(R.drawable.eleven_gray);
                            twelveGray.setImageResource(R.drawable.twelve_gray);

                            one = false;
                            two = false;
                            three = false;
                            four = false;
                            five = false;
                            six = false;
                            seven = false;
                            eight = false;
                            nine = false;
                            ten = false;
                            eleven = false;
                            twelve = false;
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

                                            start_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                            try {
                                                startDate.setText(start_date);
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

                    if (Preferences.getBoolean("executive_director")) {
                        EDCheckBox.setChecked(true);
                    } else {
                        EDCheckBox.setChecked(false);
                    }

                    EDCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                executiveDirector = true;
                                DOCheckBox.setChecked(false);
                                Preferences.save("executive_director", true);
                            } else {
                                executiveDirector = false;
                                Preferences.save("executive_director", false);
                            }
                        }
                    });

                    if (Preferences.getBoolean("director_operation")) {
                        DOCheckBox.setChecked(true);
                    } else {
                        DOCheckBox.setChecked(false);
                    }

                    DOCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                directorOfOperation = true;
                                EDCheckBox.setChecked(false);
                                Preferences.save("director_operation", true);
                            } else {
                                directorOfOperation = false;
                                Preferences.save("director_operation", false);
                            }
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


                    if (Preferences.getBoolean("registered")) {
                        regiStudentCheckBox.setChecked(true);
                    } else {
                        regiStudentCheckBox.setChecked(false);
                    }

                    regiStudentCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                registered = true;
                                notRegiStudentCheckBox.setChecked(false);
                                Preferences.save("registered", true);
                            } else {
                                registered = false;
                                Preferences.save("registered", false);
                            }
                        }
                    });

                    if (Preferences.getBoolean("not_registered")) {
                        notRegiStudentCheckBox.setChecked(true);
                    } else {
                        notRegiStudentCheckBox.setChecked(false);
                    }

                    notRegiStudentCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                notRegistered = true;
                                regiStudentCheckBox.setChecked(false);
                                Preferences.save("not_registered", true);
                            } else {
                                notRegistered = false;
                                Preferences.save("not_registered", false);
                            }
                        }
                    });


                    imageviewSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            filterDateWiseAssignmentData();
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
        });
    }

    private void filterDateWiseAssignmentData() {
        studentArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_STUDENT_LIST_ASSIGNED_COACH);
        requestMap.put(General.SCHOOL_ID, Preferences.get(General.SCHOOL_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        if (one) {
            requestMap.put(General.BY_GRADE, "1");
        } else if (two) {
            requestMap.put(General.BY_GRADE, "2");
        } else if (three) {
            requestMap.put(General.BY_GRADE, "3");
        } else if (four) {
            requestMap.put(General.BY_GRADE, "4");
        } else if (five) {
            requestMap.put(General.BY_GRADE, "5");
        } else if (six) {
            requestMap.put(General.BY_GRADE, "6");
        } else if (seven) {
            requestMap.put(General.BY_GRADE, "7");
        } else if (eight) {
            requestMap.put(General.BY_GRADE, "8");
        } else if (nine) {
            requestMap.put(General.BY_GRADE, "9");
        } else if (ten) {
            requestMap.put(General.BY_GRADE, "10");
        } else if (eleven) {
            requestMap.put(General.BY_GRADE, "11");
        } else if (twelve) {
            requestMap.put(General.BY_GRADE, "12");
        } else {
            requestMap.put(General.BY_GRADE, "0");
        }

        if (executiveDirector) {
            requestMap.put(General.ADDED_BY, "1");
        } else if (directorOfOperation) {
            requestMap.put(General.ADDED_BY, "5");
        } else {
            requestMap.put(General.ADDED_BY, "0");
        }

        if (registered) {
            requestMap.put(General.STUDENT_TYPE, "1");
        } else if (notRegistered) {
            requestMap.put(General.STUDENT_TYPE, "2");
        } else {
            requestMap.put(General.STUDENT_TYPE, "0");
        }

        if (start_date.equals("date") && end_date.equals("end_date")) {
            requestMap.put(General.DATE_TYPE, "date");
            requestMap.put(General.START_DATE, start_date);
            requestMap.put(General.END_DATE, end_date);
        } else if (lastWeek.equals("last_week")) {
            requestMap.put(General.DATE_TYPE, lastWeek);
        } else if (lastMonth.equals("last_month")) {
            requestMap.put(General.DATE_TYPE, lastMonth);
        } else {
            requestMap.put(General.DATE_TYPE, "0");
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.v("STUDENT_LIST-->", response);
                if (response != null) {
                    studentArrayList = School_.parseStudentList(response, Actions_.GET_STUDENT_LIST_ASSIGNED_COACH, getActivity().getApplicationContext(), TAG);
                    if (studentArrayList.size() > 0) {
                        if (studentArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            studentlListAdapter = new StudentlListAdapter(activity, studentArrayList, this);
                            listView.setAdapter(studentlListAdapter);
                            popupWindow.dismiss();
                        } else {
                            popupWindow.dismiss();
                            showError(true, studentArrayList.get(0).getStatus());
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
        showError(true, 20);
    }

    private void subSrearchFunctiaonality(View view) {
        cardViewActionsSearch.setVisibility(View.VISIBLE);
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
        imageButtonSetting.setVisibility(View.GONE);
        view = (View) view.findViewById(R.id.view_part);
        view.setVisibility(View.GONE);

        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                performSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    public void performSearch() {
        searchStudentArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (Student student : studentArrayList) {
            if (student.getName() != null && student.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchStudentArrayList.add(student);
            } else if (student.getAdded_by() != null && student.getAdded_by().toLowerCase().contains(searchText.toLowerCase())) {
                searchStudentArrayList.add(student);
            }
        }
        if (searchStudentArrayList.size() > 0) {
            showError(false, 1);
            StudentlListAdapter studentlListAdapter = new StudentlListAdapter(activity, searchStudentArrayList, this);
            listView.setAdapter(studentlListAdapter);
        } else {
            showError(true, 2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.assignment_list));
        mainActivityInterface.setToolbarBackgroundColor();

        if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            studentListLayout.setVisibility(View.VISIBLE);
            fetchSchoolData();
        } else {
            getAssignmentListing(0);
        }
    }

    private void fetchSchoolData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SCHOOL_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.DIST_ID, "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_STUDENT_MODULE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    schoolArrayList = new ArrayList<>();
                    School school = new School();
                    school.setSchool_id(0);
                    school.setName("Select School Name");
                    school.setStatus(1);

                    schoolArrayList.add(school);

                    schoolArrayList.addAll(CaseloadParser_.parseSchoolList(response, Actions_.GET_SCHOOL_LIST, activity.getApplicationContext(), TAG));

                    ArrayList<String> schoolNameList = new ArrayList<String>();
                    for (int i = 0; i < schoolArrayList.size(); i++) {
                        schoolNameList.add(schoolArrayList.get(i).getName());
                    }

                    if (schoolNameList.size() > 0) {
                        ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, schoolNameList);
                        adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinnerStudentList.setAdapter(adapterConsumer);
                        spinnerStudentList.setOnItemSelectedListener(onSchoolSelected);
                        spinnerStudentList.setSelection(0);

                        showError(true, 2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private final AdapterView.OnItemSelectedListener onSchoolSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerStudentList.setSelection(position);
            if (position == 0) {
                Preferences.save(General.SCHOOL_ID, "0");
                showError(true, 2);
            } else {
                //Preferences.save(General.SCHOOL_ID, schoolArrayList.get(position).getSchool_id());
                getAssignmentListing(schoolArrayList.get(position).getId());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    // make network call to fetch all journal listing data
    private void getAssignmentListing(int schoolId) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_STUDENT_LIST_ASSIGNED_COACH);
        requestMap.put(General.SCHOOL_ID, String.valueOf(schoolId));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.DATE_TYPE, "0");
        requestMap.put(General.BY_GRADE, "0");
        requestMap.put(General.ADDED_BY, "0");
        requestMap.put(General.SEARCH, "0");
        requestMap.put(General.STUDENT_TYPE, "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    studentArrayList = School_.parseStudentList(response, Actions_.GET_STUDENT_LIST_ASSIGNED_COACH, getActivity().getApplicationContext(), TAG);
                    if (studentArrayList.size() > 0) {
                        if (studentArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            studentlListAdapter = new StudentlListAdapter(activity, studentArrayList, this);
                            listView.setAdapter(studentlListAdapter);
                        } else {
                            showError(true, studentArrayList.get(0).getStatus());
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
        showError(true, 20);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent assignmentCreate = new Intent(activity.getApplicationContext(), AssignmentCreateActivity.class);
                activity.startActivity(assignmentCreate);
                break;
        }
    }

    @Override
    public void refreshListData() {
        getAssignmentListing(0);
    }
}


