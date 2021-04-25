package com.modules.team.team_invitation_werhope.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
 import androidx.core.content.ContextCompat;
 import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.modules.team.team_invitation_werhope.adapter.AllInvitationAdapter;
import com.modules.team.team_invitation_werhope.model.Invitation;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.School_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

public class AllInvitationFragment extends Fragment implements AllInvitationAdapter.AllInvitationAdapterListener {
    private static final String TAG = AllInvitationFragment.class.getSimpleName();
    ArrayList<Invitation> invitationArrayList = new ArrayList<>(), searchInvitationList;
    private Activity activity;
    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private String action = "";
    private EditText editTextSearch;
    private ImageButton imageButtonFilter;

    //Invitation filter part
    private PopupWindow popupWindow = new PopupWindow();
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date = "", end_date = "";
    private String lastWeek = "", lastMonth = "";
    private Teams_ team = new Teams_();
    private String groupID = "";


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.invitation_status_layout, null);
        activity = getActivity();

        Bundle data = getArguments();
        if (data.containsKey(General.ACTION) && data.containsKey(General.TEAM)) {
            action = data.getString(General.ACTION);
            team = (Teams_) data.getSerializable(General.TEAM);
        }
        groupID = String.valueOf(team.getId());

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(15);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
        imageButtonFilter = (ImageButton) view.findViewById(R.id.invitation_filter);

        subSrearchFunctiaonality(view);

        invitationFilterData();

        return view;
    }

    private void subSrearchFunctiaonality(View view) {
        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert in != null;
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
        searchInvitationList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (Invitation invitation : invitationArrayList) {
            if (invitation.getName() != null && invitation.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchInvitationList.add(invitation);
            }
        }
        if (searchInvitationList.size() > 0) {
            showError(false, 1);
            AllInvitationAdapter allInvitationAdapter = new AllInvitationAdapter(activity, searchInvitationList, this);
            listView.setAdapter(allInvitationAdapter);
        } else {
            showError(true, 2);
        }
    }

    private void invitationFilterData() {
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

                    final LinearLayout dateLayout, dateSelectionLayout;
                    final TextView clearDateSelection, titleHeader;
                    final TextView startDate, endDate;
                    final ImageView imageviewSave, imageviewBack;
                    final Calendar calendar;
                    final CheckBox lastWeekCheckBox, lastMonthCheckBox;

                    dateLayout = customView.findViewById(R.id.date_layout);
                    dateSelectionLayout = customView.findViewById(R.id.date_selection);
                    lastWeekCheckBox = customView.findViewById(R.id.check_box_week);
                    lastMonthCheckBox = customView.findViewById(R.id.check_box_month);
                    clearDateSelection = customView.findViewById(R.id.clear_selection_date);
                    titleHeader = customView.findViewById(R.id.journal_header);
                    imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
                    imageviewBack = customView.findViewById(R.id.imageview_back);
                    startDate = customView.findViewById(R.id.start_date_txt);
                    endDate = customView.findViewById(R.id.end_date_txt);

                    titleHeader.setText("Invitation Filter");

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
                                                int result = Compare.validEndDate(end_date, date);
                                                if (result == 1) {
                                                    endDate.setText(end_date);
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

                    imageviewSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            filterDateInviatationData();
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

    private void filterDateInviatationData() {
        invitationArrayList.clear();

        showError(true, 20);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ALL_INVITATION_STATUS);
        requestMap.put(General.GROUP_ID, groupID);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.SEARCH, "");

        if (action.equals(Actions_.ALL)) {
            requestMap.put(General.TYPE, "0");
        } else if (action.equals(Actions_.PENDIN)) {
            requestMap.put(General.TYPE, "1");
        } else {
            requestMap.put(General.TYPE, "2");
        }

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
        Log.e("filterInviatationData", String.valueOf(requestMap));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    invitationArrayList = School_.parseInvitationList(response, Actions_.GET_ALL_INVITATION_STATUS, getActivity().getApplicationContext(), TAG);

                    if (invitationArrayList.size() > 0) {
                        if (invitationArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            AllInvitationAdapter allInvitationAdapter = new AllInvitationAdapter(activity, invitationArrayList, this);
                            listView.setAdapter(allInvitationAdapter);
                            popupWindow.dismiss();
                        } else {
                            showError(true, invitationArrayList.get(0).getStatus());
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
        if (action.trim().length() > 0) {
            getInvitationList();
        }
    }

    // Make network call to fetch all invitation list
    private void getInvitationList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ALL_INVITATION_STATUS);
        requestMap.put(General.GROUP_ID, groupID);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        if (action.equals(Actions_.ALL)) {
            requestMap.put(General.TYPE, "0");
        } else if (action.equals(Actions_.PENDIN)) {
            requestMap.put(General.TYPE, "1");
        } else {
            requestMap.put(General.TYPE, "2");
        }

        requestMap.put(General.DATE_TYPE, "0");
        requestMap.put(General.START_DATE, "");
        requestMap.put(General.END_DATE, "");
        requestMap.put(General.SEARCH, "");
        Log.e("getInvitationList", String.valueOf(requestMap));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.v("LIST-->", response);
                if (response != null) {
                    invitationArrayList = School_.parseInvitationList(response, Actions_.GET_ALL_INVITATION_STATUS, getActivity().getApplicationContext(), TAG);
                    if (invitationArrayList.size() > 0) {
                        if (invitationArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            AllInvitationAdapter allInvitationAdapter = new AllInvitationAdapter(activity, invitationArrayList, this);
                            listView.setAdapter(allInvitationAdapter);
                        } else {
                            showError(true, invitationArrayList.get(0).getStatus());
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
    public void onDeletedInvitationClicked() {
        if (action.trim().length() > 0) {
            getInvitationList();
        }
    }
}

