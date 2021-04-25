package com.modules.dailydosing.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
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

import com.modules.dailydosing.model.DailyDosing;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.views.TextWatcherExtended;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DailyDosingPatientSelectActivity extends AppCompatActivity implements DailyDosingPatientSelectAdapter.DailyDosingPatientSelectListener {


    private static final String TAG = DailyDosingPatientSelectActivity.class.getSimpleName();
    @BindView(R.id.edittext_search)
    EditText editTextSearch;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    public Unbinder unbinder;


    public ArrayList<DailyDosing> dailyDosingArrayList = new ArrayList<>();


    private DailyDosingPatientSelectAdapter appointmentReportClientSelectAdapter;

    private static final String KEY_ID = "key_id";
    private static final String KEY_VALUE = "key_value";
    public String patientID = "0";

    private int PATIENT_REQUEST_CODE = 1;
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

        setContentView(R.layout.activity_daily_dosing_patient_select);

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
        titleText.setText(getResources().getString(R.string.select_patient));


        unbinder = ButterKnife.bind(this);

        // get ArrayList using Intent
        Bundle extra = getIntent().getExtras();
        assert extra != null;
        patientID = extra.getString("selectedPatientID");
        dailyDosingArrayList = (ArrayList<DailyDosing>) getIntent().getSerializableExtra("dailyDosingArrayList");

        mOnClickListnearFunction();

        // This Code is for set or binding Data for Client or Staff in adapter
        appointmentReportClientSelectAdapter = new DailyDosingPatientSelectAdapter(this, dailyDosingArrayList, this, patientID);
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
//                performSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    // this function is for hiding keyboard
    public static void hideSoftKeyboard(Activity activity) {
        try {
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

    // This Method is For displaying Name of Client or Staff which is selected
    @Override
    public void onDailyDosingPatientSelectLayoutClicked(DailyDosing dailyDosingModel) {
        Intent intent = new Intent();
        intent.putExtra(KEY_ID, dailyDosingModel.getId());
        intent.putExtra(KEY_VALUE, dailyDosingModel.getName());
        setResult(PATIENT_REQUEST_CODE,intent);

        finish();
    }
}