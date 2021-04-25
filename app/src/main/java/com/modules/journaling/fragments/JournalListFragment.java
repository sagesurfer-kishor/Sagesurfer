package com.modules.journaling.fragments;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.assignment.werhope.model.School;
import com.modules.journaling.activity.JournalAddActivity;
import com.modules.journaling.adapter.JournalListAdapter;
import com.modules.journaling.model.Journal_;
import com.modules.journaling.model.Student_;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.parser.Journaling_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 10/3/2019.
 */
public class JournalListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = JournalListFragment.class.getSimpleName();
    private ArrayList<Journal_> journalArrayList = new ArrayList<>(), searchJournalArrayList;
    private ArrayList<Student_> studentArrayList = new ArrayList<>();
    private ArrayList<School> schoolArrayList = new ArrayList<>();
    private ArrayList<Choices_> studentFromSchoolArrayList = new ArrayList<>();
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLayout, studentListLayout, schoolListLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private PopupWindow popupWindow = new PopupWindow();
    private JournalListAdapter journalListAdapter;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private FloatingActionButton createButton;
    private CardView cardViewActionsSearch;
    private EditText editTextSearch;
    private ImageButton imageButtonSetting, imageButtonFilter;
    private Spinner spinnerStudentList, spinnerSchoolList;
    //journal listing filter part
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String start_date = "", end_date = "";
    private String startSelectedDate = "", endSelectedDate = "", lastWeek = "", lastMonth = "", all = "", favourite = "", unFavourite = "";

    private boolean showStudent = false;

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

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.notes_title));
        } else {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.journal_list));
        }

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

        schoolListLayout = (LinearLayout) view.findViewById(R.id.spinner_school_layout);
        spinnerSchoolList = (Spinner) view.findViewById(R.id.spinner_school_list);

        createButton = (FloatingActionButton) view.findViewById(R.id.fab);
        createButton.setOnClickListener(this);

        if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            createButton.setVisibility(View.GONE);
        } else {
            createButton.setVisibility(View.VISIBLE);
        }

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            studentListLayout.setVisibility(View.VISIBLE);
            schoolListLayout.setVisibility(View.VISIBLE);
        }

        subSrearchFunctiaonality(view);

        journalFilterData();

        return view;
    }

    private void journalFilterData() {
        imageButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View customView = inflater.inflate(R.layout.dialog_wall_filter, null);
                    popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.isShowing();

                    final LinearLayout dateLayout, dateSelectionLayout, moreFilterLayout;
                    final TextView clearDateSelection, clearDateSelectionMore, jornalHeader;
                    final TextView startDate, endDate;
                    final ImageView imageviewSave, imageviewBack, dateIcon;
                    final Calendar calendar;
                    final CheckBox lastWeekCheckBox, lastMonthCheckBox, allCheckBox, favouriteCheckBox, unFavourateCheckBox;

                    dateLayout = customView.findViewById(R.id.date_layout);
                    dateSelectionLayout = customView.findViewById(R.id.date_selection);
                    moreFilterLayout = customView.findViewById(R.id.more_filter_layout);
                    moreFilterLayout.setVisibility(View.VISIBLE);
                    lastWeekCheckBox = customView.findViewById(R.id.check_box_week);
                    lastMonthCheckBox = customView.findViewById(R.id.check_box_month);
                    allCheckBox = customView.findViewById(R.id.check_box_all);
                    favouriteCheckBox = customView.findViewById(R.id.check_box_favourite);
                    unFavourateCheckBox = customView.findViewById(R.id.check_box_unfavourite);
                    clearDateSelection = customView.findViewById(R.id.clear_selection_date);
                    clearDateSelectionMore = customView.findViewById(R.id.clear_selection_more);
                    jornalHeader = customView.findViewById(R.id.journal_header);
                    imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
                    imageviewBack = customView.findViewById(R.id.imageview_back);
                    dateIcon = customView.findViewById(R.id.date_icon);
                    startDate = customView.findViewById(R.id.start_date_txt);
                    endDate = customView.findViewById(R.id.end_date_txt);

                    jornalHeader.setText("Journal Filter");

                    clearDateSelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lastWeekCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                            startDate.setText("");
                            endDate.setText("");
                        }
                    });

                    clearDateSelectionMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            allCheckBox.setChecked(false);
                            favouriteCheckBox.setChecked(false);
                            unFavourateCheckBox.setChecked(false);
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

                    dateIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dateLayout.setVisibility(View.GONE);
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
                                                startSelectedDate = "start_date";
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

                                                int result = Compare.validEndDate(end_date, start_date);
                                                if (result == 1) {
                                                    endDate.setText(end_date);
                                                    endSelectedDate = "end_date";
                                                } else {
                                                    end_date = null;
                                                    endDate.setText(null);
                                                    ShowSnack.textViewWarning(endDate, activity.getResources()
                                                            .getString(R.string.invalid_date), activity);

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


                    if (Preferences.getBoolean("all")) {
                        allCheckBox.setChecked(true);
                    } else {
                        allCheckBox.setChecked(false);
                    }

                    allCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                all = "all";
                                favouriteCheckBox.setChecked(false);
                                unFavourateCheckBox.setChecked(false);
                                Preferences.save("all", true);
                            } else {
                                all = "";
                                Preferences.save("all", false);
                            }
                        }
                    });

                    if (Preferences.getBoolean("favourite")) {
                        favouriteCheckBox.setChecked(true);
                    } else {
                        favouriteCheckBox.setChecked(false);
                    }

                    favouriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                favourite = "favourite";
                                allCheckBox.setChecked(false);
                                unFavourateCheckBox.setChecked(false);
                                Preferences.save("favourite", true);
                            } else {
                                favourite = "";
                                Preferences.save("favourite", false);
                            }
                        }
                    });

                    if (Preferences.getBoolean("un-favourite")) {
                        unFavourateCheckBox.setChecked(true);
                    } else {
                        unFavourateCheckBox.setChecked(false);
                    }

                    unFavourateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                unFavourite = "un-favourite";
                                allCheckBox.setChecked(false);
                                favouriteCheckBox.setChecked(false);
                                Preferences.save("un-favourite", true);
                            } else {
                                unFavourite = "";
                                Preferences.save("un-favourite", false);
                            }
                        }
                    });


                    imageviewSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            filterDateWiseJornalData();
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

    private void filterDateWiseJornalData() {
        journalArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LIST);

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            requestMap.put(General.STUD_ID, Preferences.get(General.STUD_ID));
        } else {
            requestMap.put(General.STUD_ID, Preferences.get(General.USER_ID));
        }
        requestMap.put(General.SEARCH, "");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        if (all.equals("all")) {
            requestMap.put(General.FILTER, "");
        } else if (favourite.equals("favourite")) {
            requestMap.put(General.FILTER, "1");
        } else if (unFavourite.equals("un-favourite")) {
            requestMap.put(General.FILTER, "0");
        }

        if (startSelectedDate.equals("start_date") && endSelectedDate.equals("end_date")) {
            requestMap.put(General.DATE_TYPE, "date");
            requestMap.put(General.START_DATE, start_date);
            requestMap.put(General.END_DATE, end_date);
        } else if (lastWeek.equals("last_week")) {
            requestMap.put(General.DATE_TYPE, lastWeek);
        } else if (lastMonth.equals("last_month")) {
            requestMap.put(General.DATE_TYPE, lastMonth);
        } else {
            requestMap.put(General.DATE_TYPE, "");
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    journalArrayList = Journaling_.parseJournaling(response, getActivity().getApplicationContext(), TAG);

                    if (journalArrayList.size() > 0) {
                        if (journalArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            journalListAdapter = new JournalListAdapter(activity, journalArrayList);
                            listView.setAdapter(journalListAdapter);
                            popupWindow.dismiss();
                        } else {
                            showError(true, journalArrayList.get(0).getStatus());
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
        searchJournalArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (Journal_ journal_ : journalArrayList) {
            if (journal_.getSubject() != null && journal_.getSubject().toLowerCase().contains(searchText.toLowerCase())) {
                searchJournalArrayList.add(journal_);
            } else if (journal_.getTitle() != null && journal_.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                searchJournalArrayList.add(journal_);
            }
        }
        if (searchJournalArrayList.size() > 0) {
            showError(false, 1);
            JournalListAdapter journalListAdapter = new JournalListAdapter(getActivity(), searchJournalArrayList);
            listView.setAdapter(journalListAdapter);
        } else {
            showError(true, 2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.journal_list));
        mainActivityInterface.setToolbarBackgroundColor();


       /* if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            studentListLayout.setVisibility(View.VISIBLE);
            schoolListLayout.setVisibility(View.VISIBLE);
//            fetchStudentData();
            fetchSchoolData();
        }
        *//*else if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))){
        }*//*
        else {
            getJournalData();
        }*/


        if (Preferences.get(General.ROLE_ID).equals(General.USER_ROLE_EXECUTIVE_DIRECTOR)
                || Preferences.get(General.ROLE_ID).equals(General.USER_ROLE_DIRECTOR_OF_OPERATIONS)) {
            studentListLayout.setVisibility(View.VISIBLE);
            schoolListLayout.setVisibility(View.VISIBLE);
            fetchSchoolData();

            studentFromSchoolArrayList = new ArrayList<>();
            Choices_ choices_ = new Choices_();
            choices_.setId(0);
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
                choices_.setName("Select Client");
            } else {
                choices_.setName("Select Student");
            }

            choices_.setStatus(1);
            studentFromSchoolArrayList.add(choices_);
            ArrayList<String> studentNameList = new ArrayList<String>();
            for (int i = 0; i < studentFromSchoolArrayList.size(); i++) {
                studentNameList.add(studentFromSchoolArrayList.get(i).getName());
            }

            if (studentNameList.size() > 0) {
                ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, studentNameList);
                adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                spinnerStudentList.setAdapter(adapterConsumer);

            }
        } else if (Preferences.get(General.ROLE_ID).equals(General.USER_ROLE_COACH)) {
            studentListLayout.setVisibility(View.VISIBLE);
            schoolListLayout.setVisibility(View.GONE);
            fetchStudentData();
        } else {
            getJournalData();
        }
    }


    private void fetchStudentData() {
        studentArrayList.clear();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_STUDENT_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("fetchStudentData", response);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonJournal = jsonObject.getJSONArray(Actions_.GET_STUDENT_LIST);

                    for (int j = 0; j < jsonJournal.length(); j++) {
                        JSONObject object = jsonJournal.getJSONObject(j);

                        if (Integer.parseInt(object.getString("status")) == 2) {
                            Toast.makeText(activity, object.getString("error"), Toast.LENGTH_LONG).show();
                        } else {
                            studentArrayList = new ArrayList<>();

                            Student_ student_ = new Student_();
                            student_.setId(0);
                            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
                                student_.setName("Select Client");
                            } else {
                                student_.setName("Select Student");
                            }
                            student_.setStatus(1);

                            studentArrayList.add(student_);

                            studentArrayList.addAll(CaseloadParser_.parseStudentList(response, General.GET_STUDENT_LIST, activity.getApplicationContext(), TAG));

                            ArrayList<String> studentNameList = new ArrayList<String>();
                            for (int i = 0; i < studentArrayList.size(); i++) {
                                studentNameList.add(studentArrayList.get(i).getName());
                            }

                            if (studentNameList.size() > 0) {
                                ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, studentNameList);
                                adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                                spinnerStudentList.setAdapter(adapterConsumer);
                                spinnerStudentList.setOnItemSelectedListener(onStudentSelected);
                                //spinnerStudentList.setSelection(Integer.parseInt(Preferences.get("spinnerSelection")));

                                /*for (int k = 0; k < studentArrayList.size(); k++) {
                                    spinnerStudentList.setSelection(k);
                                    spinnerStudentList.setOnItemSelectedListener(onStudentSelected);
                                }*/

                                Preferences.save(General.STUD_ID, studentArrayList.get(0).getId());
                                showError(true, 2);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchSchoolData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_SCHOOL_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_SCHOOL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
//                Log.e("ResponseSchoolData",response);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonJournal = jsonObject.getJSONArray(Actions_.GET_SCHOOL_LIST);

                    for (int j = 0; j < jsonJournal.length(); j++) {
                        JSONObject object = jsonJournal.getJSONObject(j);

                        if (Integer.parseInt(object.getString("status")) == 2) {
                            Toast.makeText(activity, object.getString("error"), Toast.LENGTH_LONG).show();
                        } else {
                            schoolArrayList = new ArrayList<>();

                            School school = new School();
                            school.setId(0);
                            school.setName("Select School");
                            school.setStatus(1);

                            schoolArrayList.add(school);

                            schoolArrayList.addAll(CaseloadParser_.parseSchoolList(response, General.GET_SCHOOL_LIST, activity.getApplicationContext(), TAG));

                            ArrayList<String> schoolNameList = new ArrayList<String>();
                            for (int i = 0; i < schoolArrayList.size(); i++) {
                                schoolNameList.add(schoolArrayList.get(i).getName());
                            }

                            if (schoolNameList.size() > 0) {
                                ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, schoolNameList);
                                adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                                spinnerSchoolList.setAdapter(adapterConsumer);
                                spinnerSchoolList.setOnItemSelectedListener(onSchoolSelected);
                                //spinnerStudentList.setSelection(Integer.parseInt(Preferences.get("spinnerSelection")));

                                /*for (int k = 0; k < schoolArrayList.size(); k++) {
                                    spinnerSchoolList.setSelection(k);
                                    spinnerSchoolList.setOnItemSelectedListener(onSchoolSelected);
                                }*/

                                //Preferences.save(General.SCHOOL_ID, schoolArrayList.get(0).getId());
                                showError(true, 2);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getStudentList(String ID) {
        studentFromSchoolArrayList.clear();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.STUDENT_LIST_OF_COACH);
        requestMap.put(General.SCHOOL_ID, ID);
        requestMap.put(General.STD_ID, "0");
       /* if (showStudent) {
            requestMap.put(General.STD_ID, "0");
        } else {
            requestMap.put(General.STD_ID, String.valueOf(grade_id));
        }*/

        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("ResponseStudent", response);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonJournal = jsonObject.getJSONArray(Actions_.STUDENT_LIST_OF_COACH);

                    for (int j = 0; j < jsonJournal.length(); j++) {
                        JSONObject object = jsonJournal.getJSONObject(j);

                        if (Integer.parseInt(object.getString("status")) == 2) {
                            Toast.makeText(activity, object.getString("error"), Toast.LENGTH_LONG).show();
                        } else {
                            studentFromSchoolArrayList = new ArrayList<>();

                            Choices_ choices_ = new Choices_();
                            choices_.setId(0);
                            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
                                choices_.setName("Select Client");
                            } else {
                                choices_.setName("Select Student");
                            }
                            choices_.setStatus(1);

                            studentFromSchoolArrayList.add(choices_);

                            studentFromSchoolArrayList.addAll(SelfGoal_.parseSchoolList(response, Actions_.STUDENT_LIST_OF_COACH, activity, TAG));

                            ArrayList<String> studentNameList = new ArrayList<String>();
                            for (int i = 0; i < studentFromSchoolArrayList.size(); i++) {
                                studentNameList.add(studentFromSchoolArrayList.get(i).getName());
                            }

                            if (studentNameList.size() > 0) {
                                ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, studentNameList);
                                adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                                spinnerStudentList.setAdapter(adapterConsumer);
                                spinnerStudentList.setOnItemSelectedListener(onSchoolStudentSelected);
                                //spinnerStudentList.setSelection(Integer.parseInt(Preferences.get("spinnerSelection")));

                                /*for (int k = 0; k < studentFromSchoolArrayList.size(); k++) {
                                    spinnerStudentList.setSelection(k);
                                    spinnerStudentList.setOnItemSelectedListener(onSchoolStudentSelected);
                                }*/

                                Preferences.save(General.STUD_ID, studentFromSchoolArrayList.get(0).getId());
                                showError(true, 2);
                            }
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
//            spinnerStudentList.setSelection(0);
            //Preferences.save("spinnerSelection", spinnerStudentList.getSelectedItemPosition());
            if (position == 0) {
                Preferences.save(General.STUD_ID, studentArrayList.get(position).getId());
                showError(true, 2);
            } else {
                Preferences.save(General.STUD_ID, studentArrayList.get(position).getId());
                getJournalData();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private final AdapterView.OnItemSelectedListener onSchoolSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerSchoolList.setSelection(position);
//            spinnerSchoolList.setSelection(0);
            //Preferences.save("spinnerSelection", spinnerStudentList.getSelectedItemPosition());
            if (position == 0) {
                Preferences.save(General.SCHOOL_ID, schoolArrayList.get(position).getSchool_id());
                showError(true, 2);
            } else {
                Preferences.save(General.SCHOOL_ID, schoolArrayList.get(position).getSchool_id());
                getStudentList(String.valueOf(schoolArrayList.get(position).getSchool_id()));
//                getJournalData();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private final AdapterView.OnItemSelectedListener onSchoolStudentSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerStudentList.setSelection(position);
//            spinnerStudentList.setSelection(0);
            //Preferences.save("spinnerSelection", spinnerStudentList.getSelectedItemPosition());
            if (position == 0) {
                Preferences.save(General.STUD_ID, studentFromSchoolArrayList.get(position).getId());
                showError(true, 2);
            } else {
                Preferences.save(General.STUD_ID, studentFromSchoolArrayList.get(position).getId());
                getJournalData();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    // make network call to fetch all journal listing data
    private void getJournalData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LIST);

        if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            requestMap.put(General.STUD_ID, Preferences.get(General.STUD_ID));
        } else {
            requestMap.put(General.STUD_ID, Preferences.get(General.USER_ID));
        }
        requestMap.put(General.SEARCH, "");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.FILTER, "");
        requestMap.put(General.DATE_TYPE, "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.v("JOURNAL_LIST-->", response);
                if (response != null) {
                    journalArrayList = Journaling_.parseJournaling(response, getActivity().getApplicationContext(), TAG);
                    if (journalArrayList.size() > 0) {
                        if (journalArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            journalListAdapter = new JournalListAdapter(activity, journalArrayList);
                            listView.setAdapter(journalListAdapter);
                        } else {
                            showError(true, journalArrayList.get(0).getStatus());
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
                Intent attachmentIntent = new Intent(activity.getApplicationContext(), JournalAddActivity.class);
                activity.startActivity(attachmentIntent);
                break;
        }
    }
}

