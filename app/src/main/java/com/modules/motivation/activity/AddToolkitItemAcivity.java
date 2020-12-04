package com.modules.motivation.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.modules.motivation.adapter.WellnessAddToolkitAdapter;
import com.modules.motivation.model.ToolKitData;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.ToolkitParser_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.RequestBody;

public class AddToolkitItemAcivity extends AppCompatActivity {
    private static final String TAG = AddToolkitItemAcivity.class.getSimpleName();
    private Toolbar toolbar;
    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private ArrayList<ToolKitData> toolkitArrayList, fetchToolkitArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout toolkitItemLayout;
    private ImageView selectDatePickerDate, selectTimePickerDate;
    private static TextView setSelectedDate, setSelectedTime;
    private Button submitBtn;
    private static String format = "18", time = "", date = "";
    private Boolean yourBool = false;
    private RelativeLayout firstRelativeLayout, secondRelativeLayout;
    private ArrayList<String> toolkitIdList;
    private String toolkitIds;
    private int selectId = 0;
    private Long timeStamp;
    private String dateTime;
    private ArrayList<String> idsArrayList;
    private WellnessAddToolkitAdapter wellnessToolkitAdapter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_selected_toolkit_item_acivity);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(90, 0, 0, 0);
        titleText.setText(this.getResources().getString(R.string.toolkit_items));

        listView = (ListView) findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(3);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        errorText = (TextView) findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.linealayout_error);
        toolkitItemLayout = findViewById(R.id.add_toolkit_layout);

        selectDatePickerDate = findViewById(R.id.toolkit_date_picker);
        setSelectedDate = findViewById(R.id.toolkit_date_time);
        selectTimePickerDate = findViewById(R.id.toolkit_time_picker);
        setSelectedTime = findViewById(R.id.toolkit_time);
        submitBtn = findViewById(R.id.submit_button);
        submitBtn.setVisibility(View.GONE);
        firstRelativeLayout = findViewById(R.id.rl_one);
        secondRelativeLayout = findViewById(R.id.rl_two);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        AppCompatImageView postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);

        date = "";
        time = "";

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yourBool) {
                    fetchSelectedToolkitItemsList(Actions_.EDIT_TOOLKIT);
                } else {
                    getIds();
                    if (validate()) {
                        fetchSelectedToolkitItemsList(Actions_.ADD_TOOLKIT);
                    }
                }
            }
        });


        selectDatePickerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(AddToolkitItemAcivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String _year = String.valueOf(year);
                        String _month = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
                        String _date = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

                        date = _year + "-" + GetCounters.checkDigit(Integer.parseInt(_month)) + "-" + GetCounters.checkDigit(Integer.parseInt(_date));
                        setSelectedDate.setText(date);

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });

        selectTimePickerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                assert getFragmentManager() != null;
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        toolkitIdList = new ArrayList<String>();
        idsArrayList = new ArrayList<>();
        toolkitArrayList = new ArrayList<ToolKitData>();

        Intent data = getIntent();
        if (data != null) {
            yourBool = Objects.requireNonNull(data.getExtras()).getBoolean("editToolkit");
            if (yourBool) {
                toolkitIds = data.getStringExtra("toolkit_ids");
                selectId = data.getIntExtra("id", 0);
                timeStamp = data.getLongExtra("date_time", 0);
                toolkitArrayList = data.getParcelableArrayListExtra("edit_toolkit_list");

                wellnessToolkitAdapter = new WellnessAddToolkitAdapter(this, toolkitArrayList);
                listView.setAdapter(wellnessToolkitAdapter);

                idsArrayList.clear();
                String[] ids = toolkitIds.split(","); //1,4,7,5
                for (int i = 0; i < ids.length; i++) {
                    idsArrayList.add(ids[i].replace(" ", ""));
                }

                for (int i = 0; i < toolkitArrayList.size(); i++) {
                    for (int j = 0; j < idsArrayList.size(); j++) {
                        if (idsArrayList.get(j).equals(String.valueOf(toolkitArrayList.get(i).getId()))) {
                            toolkitArrayList.get(i).setSelected(true);
                            wellnessToolkitAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                titleText.setText(this.getResources().getString(R.string.edit_toolkit_items));
            }
        }

        if (yourBool) {
            firstRelativeLayout.setVisibility(View.GONE);
            secondRelativeLayout.setVisibility(View.GONE);
        } else {
            firstRelativeLayout.setVisibility(View.VISIBLE);
            secondRelativeLayout.setVisibility(View.VISIBLE);
        }

    }

    private boolean validate() {
        if (toolkitIdList.size() == 0) {
            Toast.makeText(AddToolkitItemAcivity.this, "Please select toolkit item", Toast.LENGTH_LONG).show();
            return false;
        }

        if (date == null || date.length() <= 0) {
            ShowToast.toast("Please select future date", AddToolkitItemAcivity.this);
            return false;
        }

        if (time == null || time.length() <= 0) {
            ShowToast.toast("Please select future time", AddToolkitItemAcivity.this);
            return false;
        }

        return true;
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            int second = c.get(Calendar.SECOND);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar datetime = Calendar.getInstance();
            Calendar c = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);
            if (datetime.getTimeInMillis() >= c.getTimeInMillis()) {
                int hour = hourOfDay % 12;
                time = hourOfDay + ":" + minute + ":" + format;
                setSelectedTime.setText(String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, hourOfDay < 12 ? "AM" : "PM"));
            } else {
                setSelectedTime.setText("");
                Toast.makeText(view.getContext(), "Invalid Time", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!yourBool) {
            getToolkitData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    //  make network call to fetch toolkit data
    private void getToolkitData() {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_TOOLKIT_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.GET_TOOLKIT_LIST;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    toolkitArrayList = ToolkitParser_.parseGetToolkit(response, Actions_.GET_TOOLKIT_LIST, this, TAG);
                    if (toolkitArrayList.size() > 0) {
                        if (toolkitArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            wellnessToolkitAdapter = new WellnessAddToolkitAdapter(this, toolkitArrayList);
                            listView.setAdapter(wellnessToolkitAdapter);
                        } else {
                            showError(true, toolkitArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                } else {
                    showError(true, 11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true, status);
        }
    }


    private void fetchSelectedToolkitItemsList(String action) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.ACTION, action);

        if (yourBool) {
            requestMap.put("id", String.valueOf(selectId));
        } else {
            requestMap.put("date", date);
            requestMap.put("time", time);
        }
        requestMap.put("toolkit_ids", getIds());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.GET_TOOLKIT_LIST;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {

                    fetchToolkitArrayList = ToolkitParser_.parseGetToolkit(response, action, this, TAG);

                    if (fetchToolkitArrayList.size() > 0) {

                        if (fetchToolkitArrayList.get(0).getStatus() == 1) {

                            ShowToast.toast(fetchToolkitArrayList.get(0).getMsg(), this);

                            finish();

                           /* if (yourBool) {
                                onBackPressed();
                                Intent editToolkitIntent = new Intent(this, WellnessSelectedItemsActivity.class);
                                editToolkitIntent.putExtra("time_stamp", timeStamp);
                                editToolkitIntent.putExtra("toolkit_ids", getIds());
                                startActivity(editToolkitIntent);
                            } else {
                                finish();
                            }*/
                        } else {
                            final Dialog dialog = new Dialog(this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.toolkit_info_dialog);
                            assert dialog.getWindow() != null;

                            TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
                            okButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });

                            dialog.show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getIds() {
        toolkitIdList.clear();

        if (toolkitArrayList != null && toolkitArrayList.size() > 0) {
            for (int i = 0; i < toolkitArrayList.size(); i++) {
                if (toolkitArrayList.get(i).getSelected()) {
                    toolkitIdList.add(String.valueOf(toolkitArrayList.get(i).getId()));
                }
            }
        }

        return toolkitIdList.toString()
                .replace("[", "")
                .replace("]", "")
                .trim();
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            toolkitItemLayout.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, this));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            toolkitItemLayout.setVisibility(View.VISIBLE);
        }
    }
}
