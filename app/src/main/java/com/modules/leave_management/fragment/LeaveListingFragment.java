package com.modules.leave_management.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.modules.leave_management.activity.LeaveApplicationActivity;
import com.modules.leave_management.adapters.LeaveManagementAdapter;
import com.modules.leave_management.models.LeaveManagement;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.LeaveMangement_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 12/18/2019.
 */
public class LeaveListingFragment extends Fragment implements View.OnClickListener, LeaveManagementAdapter.LeaveManagementAdapterListener {
    private static final String TAG = LeaveListingFragment.class.getSimpleName();
    private ArrayList<LeaveManagement> coachLeaveArrayList = new ArrayList<>(), searchLeaveArrayList;
    RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private ImageButton imageButtonFilter;
    private FloatingActionButton createButton, createLeave;
    private CardView cardViewActionsSearch;
    private EditText editTextSearch;
    private LeaveManagementAdapter leaveManagementAdapter;
    private PopupWindow popupWindow = new PopupWindow();
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String start_date = "", end_date = "";

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (Preferences.get(General.ROLE_ID).equals("6")) {
                mainActivity.showFilterIcon(true);
            } else {
                mainActivity.showFilterIcon(false);
            }
        }
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

        View view = null;

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            view = inflater.inflate(R.layout.coach_management_list_layout, null);
        } else {
            view = inflater.inflate(R.layout.leave_management_list_layout, null);
        }

        activity = getActivity();

        recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        cardViewActionsSearch = (CardView) view.findViewById(R.id.cardview_actions);
        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        imageButtonFilter = (ImageButton) view.findViewById(R.id.notification_filter);
        createButton = (FloatingActionButton) view.findViewById(R.id.fab);
        createLeave = (FloatingActionButton) view.findViewById(R.id.fab_listview);
        createButton.setOnClickListener(this);
        createLeave.setOnClickListener(this);

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            createButton.setVisibility(View.VISIBLE);
        } else {
            createButton.setVisibility(View.GONE);
        }

        subSrearchFunctiaonality(view);

        filterLeaveData();

        return view;
    }

    private void subSrearchFunctiaonality(View view) {
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
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
        searchLeaveArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (LeaveManagement leaveManagement : coachLeaveArrayList) {
            if (leaveManagement.getName() != null && leaveManagement.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchLeaveArrayList.add(leaveManagement);
            }
        }

        if (searchLeaveArrayList.size() > 0) {
            showError(false, 1);

            if (Preferences.get(General.ROLE_ID).equals("6")) {
                leaveManagementAdapter = new LeaveManagementAdapter(activity, searchLeaveArrayList, this);
                recyclerView.setAdapter(leaveManagementAdapter);
            } else {
                leaveManagementAdapter = new LeaveManagementAdapter(activity, searchLeaveArrayList, this);
                recyclerView.setAdapter(leaveManagementAdapter);
            }

        } else {
            showError(true, 2);
        }
    }

    public void filterLeaveData() {
        imageButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View customView = inflater.inflate(R.layout.dialog_leave_filter, null);
                    popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.isShowing();

                    final TextView clearDateSelection;
                    final TextView startDate, endDate;
                    final ImageView imageviewSave, imageviewBack;
                    final Calendar calendar;

                    clearDateSelection = customView.findViewById(R.id.clear_selection_date);
                    imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
                    imageviewBack = customView.findViewById(R.id.imageview_back);
                    startDate = customView.findViewById(R.id.start_date_txt);
                    endDate = customView.findViewById(R.id.end_date_txt);

                    if (Preferences.get("start_date").equals("") || Preferences.get("end_date").equals("")) {
                        startDate.setText(Preferences.get(""));
                        endDate.setText(Preferences.get(""));
                    } else {
                        startDate.setText(Preferences.get("start_date"));
                        endDate.setText(Preferences.get("end_date"));
                    }

                    clearDateSelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startDate.setText("");
                            endDate.setText("");
                            Preferences.save("start_date", "");
                            Preferences.save("end_date", "");
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
                                                Preferences.save("start_date", start_date);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
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
                                                    Preferences.save("end_date", end_date);
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
                            datePickerDialog1.show();
                        }
                    });

                    imageviewSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (LeaveValidation(startDate.getText().toString().trim(), endDate.getText().toString().trim(), v)) {
                                filterSupervisorDateWiseLeave();
                            }
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

    public void filterCoachDateWiseLeave(Activity activity, PopupWindow popupWindow) {
        coachLeaveArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LEAVE_COACH);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        if (!Preferences.get("start_date").equals("") && !Preferences.get("end_date").equals("")) {
            requestMap.put(General.DATE_TYPE, "date");
            requestMap.put(General.START_DATE, Preferences.get("start_date"));
            requestMap.put(General.END_DATE, Preferences.get("end_date"));
        } else {
            requestMap.put(General.DATE_TYPE, "0");
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_LEAVE_MANAGEMENT;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    coachLeaveArrayList = LeaveMangement_.parseLeaveCoachList(response, Actions_.GET_LEAVE_COACH, getActivity(), TAG);
                    if (coachLeaveArrayList.size() > 0) {
                        if (coachLeaveArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            leaveManagementAdapter = new LeaveManagementAdapter(activity, coachLeaveArrayList, this);
                            recyclerView.setAdapter(leaveManagementAdapter);
                            popupWindow.dismiss();
                        } else {
                            showError(true, coachLeaveArrayList.get(0).getStatus());
                            popupWindow.dismiss();
                        }
                    } else {
                        showError(true, 2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void filterSupervisorDateWiseLeave() {
        coachLeaveArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LEAVE_SUPERVISOR);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.SEARCH, "");

        if (!start_date.equals("") && !end_date.equals("")) {
            requestMap.put(General.DATE_TYPE, "date");
            requestMap.put(General.START_DATE, start_date);
            requestMap.put(General.END_DATE, end_date);
        } else {
            requestMap.put(General.DATE_TYPE, "0");
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_LEAVE_MANAGEMENT;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    coachLeaveArrayList = LeaveMangement_.parseLeaveCoachList(response, Actions_.GET_LEAVE_SUPERVISOR, getActivity().getApplicationContext(), TAG);
                    if (coachLeaveArrayList.size() > 0) {
                        if (coachLeaveArrayList.get(0).getStatus() == 1) {
                            popupWindow.dismiss();
                            showError(false, 1);
                            leaveManagementAdapter = new LeaveManagementAdapter(activity, coachLeaveArrayList, this);
                            recyclerView.setAdapter(leaveManagementAdapter);
                        } else {
                            popupWindow.dismiss();
                            showError(true, coachLeaveArrayList.get(0).getStatus());
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
    }

    private boolean LeaveValidation(String startDate, String endDate, View view) {
        if (startDate == null || startDate.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select Start Date", activity);
            return false;
        }

        if (endDate == null || endDate.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select End Date", activity);
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.leave_list));
        mainActivityInterface.setToolbarBackgroundColor();

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            fetchLeaveCoachList();
        } else {
            getLeaveSuperVisorList();
        }
    }

    private void fetchLeaveCoachList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LEAVE_COACH);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_LEAVE_MANAGEMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    coachLeaveArrayList = LeaveMangement_.parseLeaveCoachList(response, Actions_.GET_LEAVE_COACH, activity, TAG);
                    if (coachLeaveArrayList.size() > 0) {
                        if (coachLeaveArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            leaveManagementAdapter = new LeaveManagementAdapter(activity, coachLeaveArrayList, this);
                            recyclerView.setAdapter(leaveManagementAdapter);
                        } else {
                            showError(true, coachLeaveArrayList.get(0).getStatus());
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
    }

    private void getLeaveSuperVisorList() {
        coachLeaveArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LEAVE_SUPERVISOR);
        requestMap.put(General.SEARCH, "");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_LEAVE_MANAGEMENT;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {

                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.GET_LEAVE_SUPERVISOR);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<LeaveManagement>>() {
                        }.getType();

                        coachLeaveArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GET_LEAVE_SUPERVISOR).toString(), listType);

                        if (coachLeaveArrayList.size() > 0) {
                            if (coachLeaveArrayList.get(0).getStatus() == 1) {
                                showError(false, 1);
                                leaveManagementAdapter = new LeaveManagementAdapter(activity, coachLeaveArrayList, this);
                                recyclerView.setAdapter(leaveManagementAdapter);
                            } else {
                                showError(true, coachLeaveArrayList.get(0).getStatus());
                            }
                        } else {
                            showError(true, 2);
                        }
                        return;
                    }
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
            recyclerView.setVisibility(View.GONE);

            if (Preferences.get(General.ROLE_ID).equals("6")) {
                createButton.setVisibility(View.GONE);
                createLeave.setVisibility(View.VISIBLE);
            } else {
                createButton.setVisibility(View.GONE);
                createLeave.setVisibility(View.GONE);
            }

            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (Preferences.get(General.ROLE_ID).equals("6")) {
                createButton.setVisibility(View.VISIBLE);
                createLeave.setVisibility(View.GONE);
            } else {
                createButton.setVisibility(View.GONE);
                createLeave.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent leaveIntent = new Intent(activity.getApplicationContext(), LeaveApplicationActivity.class);
                activity.startActivity(leaveIntent);
                break;

            case R.id.fab_listview:
                Intent leaveIntent1 = new Intent(activity.getApplicationContext(), LeaveApplicationActivity.class);
                activity.startActivity(leaveIntent1);
                break;
        }
    }

    @Override
    public void deleteLeave(int id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_LEAVE);
        requestMap.put("id", "" + id);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_LEAVE_MANAGEMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonFavUnFav = jsonObject.getAsJsonObject(Actions_.DELETE_LEAVE);
                    if (jsonFavUnFav.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, jsonFavUnFav.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        leaveManagementAdapter.notifyDataSetChanged();
                        fetchLeaveCoachList();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

