package com.modules.reports.appointment_reports.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.modules.reports.appointment_reports.adapter.AppointmentReportClientSelectAdapter;
import com.modules.reports.appointment_reports.model.ClientListModel;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AppointmentReportSelectActivity extends AppCompatActivity implements AppointmentReportClientSelectAdapter.AppointmentReportClientSelectListener {


    private static final String TAG = AppointmentReportSelectActivity.class.getSimpleName();
    @BindView(R.id.edittext_search)
    EditText editTextSearch;
    @BindView(R.id.imagebutton_filter)
    AppCompatImageButton imageButtonFilter;
    @BindView(R.id.imagebutton_setting)
    AppCompatImageButton imageButtonSetting;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    public Unbinder unbinder;


    public ArrayList<ClientListModel> searchSelectStaffClientArrayList = new ArrayList<>(), staffClientArrayList = new ArrayList<>();


    private AppointmentReportClientSelectAdapter appointmentReportClientSelectAdapter;

    private static final String KEY_ID = "key_id";
    private static final String KEY_VALUE = "key_value";
    public String selectedClientID, selectedStaffID, selectedSatisSurProg;
    private int Mode;

    private int MODE_CLIENT = 1;
    private int MODE_STAFF = 2;
    private int MODE_STAFF_CLIENT = 3;
    private int MODE_SATIS_SUR_PRO = 4;

    public int CLIENT_REQUEST_CODE = 13;
    public int STAFF_REQUEST_CODE = 14;
    public int CLIENT_STAFF_REQUEST_CODE = 15;
    public int SATIS_SUR_PROG_REQUEST_CODE = 16;
    public Window window;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        hideSoftKeyboard(this);

        window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_appointment_report_select);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

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
        titleText.setPadding(100, 0, 0, 0);


        unbinder = ButterKnife.bind(this);

        Bundle extra = getIntent().getExtras();
        selectedClientID = extra.getString("selectedClientId");
        selectedStaffID = extra.getString("selectedStaffId");
        selectedSatisSurProg = extra.getString("selectedSatisSurProg");

        Mode = extra.getInt("Mode");


        // This All Condition for retrieving Satisfation Survery, Client And Staff ArrayList
        String strID = "0";
        if (Mode == MODE_CLIENT) {
            titleText.setText(getResources().getString(R.string.select_client));
            staffClientArrayList.clear();
            staffClientArrayList = (ArrayList<ClientListModel>) getIntent().getSerializableExtra("clientArrayList");
            strID = selectedClientID;
        } else if (Mode == MODE_STAFF) {
            titleText.setText(getResources().getString(R.string.select_staff));
            staffClientArrayList.clear();
            staffClientArrayList = (ArrayList<ClientListModel>) getIntent().getSerializableExtra("staffArrayList");
            strID = selectedStaffID;
        } else if (Mode == MODE_STAFF_CLIENT) {
            staffClientArrayList.clear();
            staffClientArrayList = (ArrayList<ClientListModel>) getIntent().getSerializableExtra("staffclientArrayList");
            strID = selectedClientID;
        } else if (Mode == MODE_SATIS_SUR_PRO) {
            titleText.setText("Select Services");
            staffClientArrayList.clear();
            staffClientArrayList = (ArrayList<ClientListModel>) getIntent().getSerializableExtra("satisfactionProgressArrayList");
            strID = selectedSatisSurProg;
        }

        mOnClickListnearFunction();

        // This Code is for set or binding Data for Client or Staff in adapter
        appointmentReportClientSelectAdapter = new AppointmentReportClientSelectAdapter(this, staffClientArrayList, this, strID);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(appointmentReportClientSelectAdapter);
    }

    // This Function is a click event for Staff and Client Spinner
    private void mOnClickListnearFunction() {

        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

    // This Function for search client and staff from list
    public void performSearch() {
        searchSelectStaffClientArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }

        for (ClientListModel caseLoadItem : staffClientArrayList) {
            if (caseLoadItem.getName() != null && caseLoadItem.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchSelectStaffClientArrayList.add(caseLoadItem);
                Preferences.save(General.CASELOAD_LIST, true);
            }
        }
        if (searchSelectStaffClientArrayList.size() > 0) {
            String strID = "0";
            if (Mode == MODE_CLIENT || Mode == MODE_STAFF_CLIENT) {
                strID = selectedClientID;
            } else {
                strID = selectedStaffID;
            }
            appointmentReportClientSelectAdapter = new AppointmentReportClientSelectAdapter(this, searchSelectStaffClientArrayList, this, strID);
            recyclerView.setAdapter(appointmentReportClientSelectAdapter);
        }

    }


//    private void showError(boolean isError, int status) {
//        if (isError) {
//            Toast.makeText(this, GetErrorResources.getMessage(status, this), Toast.LENGTH_SHORT).show();
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//        }
//    }

    // This Method is For displaying Name of Client or Staff which is selected
    @Override
    public void onAppointmentReportClientSelectLayoutClicked(ClientListModel clientListModel) {
        selectedClientID = clientListModel.getId();
        Intent intent = new Intent();
        intent.putExtra(KEY_ID, clientListModel.getId());
        intent.putExtra(KEY_VALUE, clientListModel.getName());

        if (Mode == MODE_CLIENT) {
            setResult(CLIENT_REQUEST_CODE, intent);
        } else if (Mode == MODE_STAFF) {
            setResult(STAFF_REQUEST_CODE, intent);
        } else if (Mode == MODE_STAFF_CLIENT) {
            setResult(CLIENT_STAFF_REQUEST_CODE, intent);
        } else if (Mode == MODE_SATIS_SUR_PRO) {
            setResult(SATIS_SUR_PROG_REQUEST_CODE, intent);
        }
        finish();

    }

    // this function is for hiding keyboard
    public static void hideSoftKeyboard(Activity activity) {
        try {
            /*InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);*/

            // Check if no view has focus:
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (NullPointerException e) {

        } catch (RuntimeException e) {

        }
    }
}