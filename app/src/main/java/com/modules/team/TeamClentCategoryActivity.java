package com.modules.team;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TeamClentCategoryActivity extends AppCompatActivity implements TeamClientCategoryAdapter.TeaamTypeListener {
    private static final String TAG = TeamClentCategoryActivity.class.getSimpleName();
    @BindView(R.id.edittext_search)
    EditText editTextSearch;
    @BindView(R.id.imagebutton_filter)
    AppCompatImageButton imageButtonFilter;
    @BindView(R.id.imagebutton_setting)
    AppCompatImageButton imageButtonSetting;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Unbinder unbinder;
    private ArrayList<TeamTypeModel> searchSelectClientArrayList = new ArrayList<>(), ClientArrayList = new ArrayList<>();
    private TeamClientCategoryAdapter appointmentReportClientSelectAdapter;
    private static final String KEY_ID = "key_id";
    private static final String KEY_VALUE = "key_value";
    private int selectedClientID, selectedCategoryID,selectedSubCategoryID;
    private int Mode;

    private int MODE_CLIENT = 1;
    private int MODE_CATEGORY = 2;
    private int MODE_SUB_CATEGORY = 3;

    private int CLIENT_REQUEST_CODE = 11;
    private int CATEGORY_REQUEST_CODE = 12;
    private int SUB_CATEGORY_REQUEST_CODE = 13;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideSoftKeyboard(this);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_team_type_category);

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
        titleText.setPadding(100, 0, 0, 0);


        unbinder = ButterKnife.bind(this);

        Bundle extra = getIntent().getExtras();
        selectedClientID = extra.getInt("selectedClientId");
        selectedCategoryID = extra.getInt("selectedCategoryId");
        selectedSubCategoryID = extra.getInt("selectedSubCategoryId");

        Mode = extra.getInt("Mode");


        /*Above condition is to differentiate ArrayList Using Mode*/
        int strID = 0;
        if (Mode == MODE_CLIENT) {
            titleText.setText(getResources().getString(R.string.select_client));
//            getSelectClientData(Actions_.GET_CLIENT_REPORT);
            ClientArrayList.clear();
            ClientArrayList = (ArrayList<TeamTypeModel>) getIntent().getSerializableExtra("clientArrayList");
            strID = selectedClientID;
        } else if (Mode == MODE_CATEGORY) {
            titleText.setText(getResources().getString(R.string.select_staff));
//            getSelectStaffData(Actions_.GET_STAFFS_REPORT);
            ClientArrayList.clear();
            ClientArrayList = (ArrayList<TeamTypeModel>) getIntent().getSerializableExtra("categoryArrayList");
            strID = selectedCategoryID;
        } else if (Mode == MODE_SUB_CATEGORY) {
//            titleText.setText(getResources().getString(R.string.select_staff));
//            getSelectStaffClientData(Actions_.GET_CLIENT_UNDER_STAFF_REPORT, selectedStaffID);
            ClientArrayList.clear();
            ClientArrayList = (ArrayList<TeamTypeModel>) getIntent().getSerializableExtra("subCategoryArrayList");
            strID = selectedSubCategoryID;
           // selectedStaffID = ClientArrayList.get(0).getId();
        }

        mOnClickListnearFunction();

        /*ArrayList bind Here and set into adapter*/
        appointmentReportClientSelectAdapter = new TeamClientCategoryAdapter(this, ClientArrayList, this, strID);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(appointmentReportClientSelectAdapter);
    }

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

    public void performSearch() {
        searchSelectClientArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }

        for (TeamTypeModel teamTypeModel : ClientArrayList) {
            if (teamTypeModel.getName() != null && teamTypeModel.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchSelectClientArrayList.add(teamTypeModel);
                Preferences.save(General.CASELOAD_LIST, true);
            }
        }
        if (searchSelectClientArrayList.size() > 0) {
            int strID = 0;
            if (Mode == MODE_CLIENT || Mode == MODE_CATEGORY) {
                strID = selectedClientID;
            } else {
                strID = selectedCategoryID;
            }
            appointmentReportClientSelectAdapter = new TeamClientCategoryAdapter(this, searchSelectClientArrayList, this, strID);
            recyclerView.setAdapter(appointmentReportClientSelectAdapter);
        }

    }


    private void showError(boolean isError, int status) {
        if (isError) {
            Toast.makeText(this, GetErrorResources.getMessage(status, this), Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTeamTypeLayoutClicked(TeamTypeModel teamTypeModel) {
//        selectedClientID = clientListModel.getId();
        Intent intent = new Intent();
        intent.putExtra(KEY_ID, teamTypeModel.getId());
        intent.putExtra(KEY_VALUE, teamTypeModel.getName());

        /*Above Condition for pass data with there request code*/
        if (Mode == MODE_CLIENT) {
            setResult(CLIENT_REQUEST_CODE, intent);
        } else if (Mode == MODE_CATEGORY) {
            setResult(CATEGORY_REQUEST_CODE, intent);
        } else if (Mode == MODE_SUB_CATEGORY) {
            setResult(SUB_CATEGORY_REQUEST_CODE, intent);
        }
        finish();

    }


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
