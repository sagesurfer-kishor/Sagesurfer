package com.modules.appointment.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.appointment.adapter.ClientNameListAdapter;
import com.modules.appointment.model.Staff;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.views.TextWatcherExtended;

import java.util.ArrayList;

public class ClientNameListActivity extends AppCompatActivity implements ClientNameListAdapter.ClientNameListener {
    private static final String TAG = ClientNameListActivity.class.getSimpleName();
    private EditText editTextSearch;
    private RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private Staff selectedStaff;
    private ClientNameListAdapter adapter;
    private ArrayList<Staff> staffArrayList = new ArrayList<>(), searchSelectClientArrayList = new ArrayList<>();
    private int STAFF_REQUEST_CODE = 11;
    private int OTHER_STAFF_REQUEST_CODE = 12;
    private int MODE_STAFF = 1;
    private int MODE_OTHER_STAFF = 2;
    private int Mode;
    private int TYPE ;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_client_name_list);

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
        titleText.setPadding(50, 0, 0, 0);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle extra = getIntent().getExtras();
        Mode = extra.getInt("Mode");

        // This Condition For Retrieving data from ArrayList
        if (Mode == MODE_STAFF) {
            TYPE = 1;
            titleText.setText(getResources().getString(R.string.select_client));
            staffArrayList.clear();
            selectedStaff = (Staff) extra.getSerializable("staff");
            staffArrayList = (ArrayList<Staff>) getIntent().getSerializableExtra("staffsArrayList");
        } else if (Mode == MODE_OTHER_STAFF) {
            TYPE = 2;
            titleText.setText(getResources().getString(R.string.select_other_staff_members));
            staffArrayList.clear();
            selectedStaff = (Staff) extra.getSerializable("otherStaff");
            staffArrayList = (ArrayList<Staff>) getIntent().getSerializableExtra("otherStaffArrayList");
        }

        recyclerView = findViewById(R.id.recycler_view);
        editTextSearch = findViewById(R.id.edittext_search);
        errorLayout = findViewById(R.id.linealayout_error);
        errorText = findViewById(R.id.textview_error_message);
        errorIcon = findViewById(R.id.imageview_error_icon);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClientNameListAdapter(this, staffArrayList, this, selectedStaff, TYPE);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        mOnClickListnearFunction();
    }

    // Called when Clicked on Search Field
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

    // Perform Search Function
    private void performSearch() {
        searchSelectClientArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }

        for (Staff staff : staffArrayList) {
            if (staff.getName() != null && staff.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchSelectClientArrayList.add(staff);
            }
        }
        if (searchSelectClientArrayList.size() > 0) {
            showError(false, 1);
            adapter = new ClientNameListAdapter(this, searchSelectClientArrayList, this, selectedStaff,TYPE);
            recyclerView.setAdapter(adapter);
        } else {
            showError(true, 2);
        }
    }

    // Called For Displaying Error
    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Called when Selected Client needs to be Display
    @Override
    public void onClientNameLayoutClicked(Staff staffListModel) {
        Intent intent = new Intent();

        if (Mode == MODE_STAFF) {
            intent.putExtra("staff", staffListModel);
            setResult(STAFF_REQUEST_CODE, intent);
        } else if (Mode == MODE_OTHER_STAFF) {
            intent.putExtra("otherStaff", staffListModel);
            setResult(OTHER_STAFF_REQUEST_CODE, intent);
        }

        finish();
    }
}
