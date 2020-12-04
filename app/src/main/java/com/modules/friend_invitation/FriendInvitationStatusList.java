package com.modules.friend_invitation;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.FriendInvitation_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

public class FriendInvitationStatusList extends AppCompatActivity {
    private static final String TAG = FriendInvitationStatusList.class.getSimpleName();
    private Toolbar toolbar;
    private RecyclerView studentRecyclerView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private FriendInvitationListAdapter friendInvitationListAdapter;
    private ArrayList<Choices_> friendInvitationList = new ArrayList<>(), searchFriendInvitationList;
    private EditText editTextSearch;
    private ImageButton imageButtonFilter;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String start_date = "", end_date = "";
    private PopupWindow popupWindow = new PopupWindow();
    private Boolean showPopUp = false;

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_friend_invitation_status_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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
        titleText.setText("Invitation Status");

        imageButtonFilter = findViewById(R.id.notification_filter);

        errorLayout = (LinearLayout) findViewById(R.id.linealayout_error);
        errorText = (TextView) findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.imageview_error_icon);

        studentRecyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FriendInvitationStatusList.this);
        studentRecyclerView.setLayoutManager(mLayoutManager);
        studentRecyclerView.setItemAnimator(new DefaultItemAnimator());

        searchFunctionality();

        filterInvitationData();
    }

    private void filterInvitationData() {
        imageButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater inflater = (LayoutInflater) FriendInvitationStatusList.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View customView = inflater.inflate(R.layout.dialog_leave_filter, null);
                    popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.isShowing();

                    final TextView clearDateSelection, header;
                    final TextView startDate, endDate;
                    final ImageView imageviewSave, imageviewBack;
                    final Calendar calendar;

                    header = customView.findViewById(R.id.journal_header);
                    clearDateSelection = customView.findViewById(R.id.clear_selection_date);
                    imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
                    imageviewBack = customView.findViewById(R.id.imageview_back);
                    startDate = customView.findViewById(R.id.start_date_txt);
                    endDate = customView.findViewById(R.id.end_date_txt);

                    header.setText("Invitation Status Filter");

                    clearDateSelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startDate.setText("");
                            endDate.setText("");
                        }
                    });


                    calendar = Calendar.getInstance();
                    mYear = calendar.get(Calendar.YEAR);
                    mMonth = calendar.get(Calendar.MONTH);
                    mDay = calendar.get(Calendar.DAY_OF_MONTH);


                    startDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(FriendInvitationStatusList.this,
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
                            DatePickerDialog datePickerDialog1 = new DatePickerDialog(FriendInvitationStatusList.this,
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
                                                } else {
                                                    end_date = null;
                                                    endDate.setText(null);
                                                    ShowSnack.textViewWarning(endDate, FriendInvitationStatusList.this.getResources()
                                                            .getString(R.string.invalid_date), FriendInvitationStatusList.this);

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

                    imageviewSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Validation(startDate.getText().toString().trim(), endDate.getText().toString().trim(), v)) {
                                String dateOne = startDate.getText().toString().trim();
                                String dateTwo = endDate.getText().toString().trim();
                                showPopUp = true;
                                invitationStatusList(dateOne, dateTwo);
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

    private boolean Validation(String startDate, String endDate, View view) {
        if (startDate == null || startDate.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select Start Date", FriendInvitationStatusList.this);
            return false;
        }

        if (endDate == null || endDate.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Please select End Date", FriendInvitationStatusList.this);
            return false;
        }
        return true;
    }

    private void searchFunctionality() {
        editTextSearch = (EditText) findViewById(R.id.edittext_search);
        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) FriendInvitationStatusList.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        searchFriendInvitationList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) FriendInvitationStatusList.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (Choices_ choices_ : friendInvitationList) {
            if (choices_.getName() != null && choices_.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchFriendInvitationList.add(choices_);
            }
        }
        if (searchFriendInvitationList.size() > 0) {
            showError(false, 1);
            friendInvitationListAdapter = new FriendInvitationListAdapter(this, searchFriendInvitationList);
            studentRecyclerView.setAdapter(friendInvitationListAdapter);
        } else {
            showError(true, 2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        //Call API Friend Invitation
        invitationStatusList("", "");
    }

    private void invitationStatusList(String startDate, String endDate) {
        friendInvitationList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, General.PENDING_INVITATION);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.SEARCH, "");

        if (startDate.equalsIgnoreCase("") || endDate.equalsIgnoreCase("")) {
            requestMap.put(General.DATE_TYPE, "0");
            requestMap.put(General.START_DATE, "");
            requestMap.put(General.END_DATE, "");
        } else {
            requestMap.put(General.DATE_TYPE, "date");
            requestMap.put(General.START_DATE, startDate);
            requestMap.put(General.END_DATE, endDate);
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_FRIEND_INVITATION;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    friendInvitationList = FriendInvitation_.friendInvitationList(response, this, TAG);
                    if (friendInvitationList.size() > 0) {
                        if (friendInvitationList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            hideDialog(showPopUp);
                            friendInvitationListAdapter = new FriendInvitationListAdapter(this, friendInvitationList);
                            studentRecyclerView.setAdapter(friendInvitationListAdapter);
                        } else {
                            hideDialog(showPopUp);
                            showError(true, 2);
                        }
                    } else {
                        hideDialog(showPopUp);
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

    private void hideDialog(Boolean showPopUp) {
        if (showPopUp) {
            popupWindow.dismiss();
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            studentRecyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            studentRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

