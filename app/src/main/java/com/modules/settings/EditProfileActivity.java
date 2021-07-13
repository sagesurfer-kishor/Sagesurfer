package com.modules.settings;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.modules.journaling.model.Student_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.validator.LoginValidator;
import com.storage.preferences.Preferences;
import com.utils.AppLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 */

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    @BindView(R.id.et_firstname)
    EditText et_firstname;

    @BindView(R.id.et_lastname)
    EditText et_lastname;

    @BindView(R.id.et_username)
    EditText et_username;

    @BindView(R.id.et_email)
    EditText et_email;

    @BindView(R.id.textview_editprofile_dob)
    TextView textViewEditProfileDOB;

    @BindView(R.id.edittext_editprofile_address)
    EditText editTextEditProfileAddress;

    private int mYear = 0, mMonth = 0, mDay = 0;

    private int sYear, sMonth, sDay;

    private String date_of_birth = "";

    private Toolbar toolbar;

    private boolean showMsg = false;

    private ArrayList<Student_> cityArrayList = new ArrayList<>(), stateArrayList = new ArrayList<>(), countryArrayList = new ArrayList<>();

    private Spinner spinnerCityList, spinnerStateList, spinnerCountryList, spinnerStateListOne, spinnerCityListOne;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = findViewById(R.id.activity_toolbar_layout);
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

        TextView textViewActivityToolbarTitle = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        textViewActivityToolbarTitle.setText(this.getResources().getString(R.string.edit_profile));

        TextView textViewActivityToolbarPost = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        textViewActivityToolbarPost.setVisibility(View.VISIBLE);
        textViewActivityToolbarPost.setText(this.getResources().getString(R.string.save));

        if (Preferences.get(General.FIRST_NAME).equalsIgnoreCase("")) {
            et_firstname.setText("");
        } else {
            et_firstname.setText(Preferences.get(General.FIRST_NAME));
        }
        et_lastname.setText(Preferences.get(General.LAST_NAME));
        et_username.setText(Preferences.get(General.USERNAME));
        et_email.setText(Preferences.get(General.EMAIL));
        //textViewEditProfileDOB.setText(Preferences.get(General.BIRTDATE));
        textViewEditProfileDOB.setOnClickListener(this);
        textViewActivityToolbarPost.setOnClickListener(this);

        et_username.setEnabled(false);
        et_username.setClickable(false);
        et_email.setEnabled(false);
        et_email.setClickable(false);

        spinnerCityList = (Spinner) findViewById(R.id.spinner_city_list);
        spinnerStateList = (Spinner) findViewById(R.id.spinner_state_list);
        spinnerCountryList = (Spinner) findViewById(R.id.spinner_country_list);

        spinnerStateListOne = (Spinner) findViewById(R.id.spinner_state_list_one);
        spinnerCityListOne = (Spinner) findViewById(R.id.spinner_city_list_one);

        spinnerCountryList.setOnItemSelectedListener(onCountrySelected);
        spinnerStateList.setOnItemSelectedListener(onStateSelected);
        spinnerCityList.setOnItemSelectedListener(onCitySelected);

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        getUserProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));

        //load the data of country list
        loadCountryList();

        //load the data of state list
        loadStateList(Integer.parseInt(Preferences.get(General.STATE_ID)));

        //load the data of city list
        loadStateList(Integer.parseInt(Preferences.get(General.CITY_ID)));
    }

    private void loadCountryList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_COUNTRY);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    countryArrayList = new ArrayList<>();
                    countryArrayList.addAll(CaseloadParser_.parseStudentList(response, Actions_.GET_COUNTRY, getApplicationContext(), TAG));

                    if (countryArrayList.get(0).getStatus() == 1) {
                        ArrayList<String> countryNameList = new ArrayList<String>();
                        for (int i = 0; i < countryArrayList.size(); i++) {
                            countryNameList.add(countryArrayList.get(i).getName());
                        }

                        if (countryNameList.size() > 0) {
                            ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, countryNameList);
                            adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                            spinnerCountryList.setAdapter(adapterConsumer);

                            for (int i = 0; i < countryArrayList.size(); i++) {
                                if (Integer.parseInt(Preferences.get(General.COUNTRY_ID)) == countryArrayList.get(i).getId()) {
                                    //for default selection of country
                                    spinnerCountryList.setSelection(i);
                                    break;
                                }
                            }
                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final AdapterView.OnItemSelectedListener onCountrySelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerCountryList.setSelection(position);
            showMsg = true;
            loadStateList(countryArrayList.get(spinnerCountryList.getSelectedItemPosition()).getId());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void loadStateList(int counrtyId) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_STATE);
        requestMap.put(General.COUNTRY_ID, String.valueOf(counrtyId));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    stateArrayList = new ArrayList<>();
                    stateArrayList.addAll(CaseloadParser_.parseStudentList(response, Actions_.GET_STATE, getApplicationContext(), TAG));

                    if (stateArrayList.get(0).getStatus() == 1) {
                        ArrayList<String> stateNameList = new ArrayList<String>();
                        for (int i = 0; i < stateArrayList.size(); i++) {
                            stateNameList.add(stateArrayList.get(i).getName());
                        }

                        if (stateNameList.size() > 0) {
                            ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, stateNameList);
                            adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                            spinnerStateList.setAdapter(adapterConsumer);

                            for (int i = 0; i < stateArrayList.size(); i++) {
                                if (Integer.parseInt(Preferences.get(General.STATE_ID)) == stateArrayList.get(i).getId()) {
                                    //for default selection of state
                                    spinnerStateList.setSelection(i);
                                    break;
                                }
                            }

                            spinnerStateListOne.setVisibility(View.GONE);
                            spinnerStateList.setVisibility(View.VISIBLE);
                            spinnerCityListOne.setVisibility(View.GONE);
                            spinnerCityList.setVisibility(View.VISIBLE);
                        }

                    } else {
                        spinnerStateListOne.setVisibility(View.VISIBLE);
                        spinnerStateList.setVisibility(View.GONE);
                        spinnerCityListOne.setVisibility(View.VISIBLE);
                        spinnerCityList.setVisibility(View.GONE);

                        if (showMsg) {
                            stateArrayList.clear();
                            Toast.makeText(EditProfileActivity.this, "State: No Data", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final AdapterView.OnItemSelectedListener onStateSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerStateList.setSelection(position);
            showMsg = true;
            loadCityList(stateArrayList.get(spinnerStateList.getSelectedItemPosition()).getId());

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void loadCityList(int stateId) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_CITY);
        requestMap.put(General.STATE_ID, String.valueOf(stateId));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    cityArrayList = new ArrayList<>();
                    cityArrayList.addAll(CaseloadParser_.parseStudentList(response, Actions_.GET_CITY, getApplicationContext(), TAG));

                    if (cityArrayList.get(0).getStatus() == 1) {

                        ArrayList<String> cityNameList = new ArrayList<String>();
                        for (int i = 0; i < cityArrayList.size(); i++) {
                            cityNameList.add(cityArrayList.get(i).getName());
                        }

                        if (cityNameList.size() > 0) {
                            ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, cityNameList);
                            adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                            spinnerCityList.setAdapter(adapterConsumer);

                            for (int i = 0; i < cityArrayList.size(); i++) {
                                if (Integer.parseInt(Preferences.get(General.CITY_ID)) == cityArrayList.get(i).getId()) {
                                    //for default selection of state
                                    spinnerCityList.setSelection(i);
                                    break;
                                }
                            }
                            spinnerCityListOne.setVisibility(View.GONE);
                            spinnerCityList.setVisibility(View.VISIBLE);
                        }

                    } else {
                        spinnerCityListOne.setVisibility(View.VISIBLE);
                        spinnerCityList.setVisibility(View.GONE);
                        if (showMsg) {
                            cityArrayList.clear();
                            Toast.makeText(EditProfileActivity.this, "City : No Data", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final AdapterView.OnItemSelectedListener onCitySelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerCityList.setSelection(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textview_activitytoolbar_post:
                if (validate()) {
                    submitUserSetting();
                }
                break;

            case R.id.textview_editprofile_dob:
                DatePickerDialog datePicker = new DatePickerDialog(EditProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                date_of_birth = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    int result = compareDate(mYear + "-" + (mMonth + 1) + "-" + mDay, date_of_birth);
                                    if (result == 1) {
                                        textViewEditProfileDOB.setText(GetTime.yy_mm_dd(date_of_birth));
                                        Preferences.save(General.BIRTDATE, GetTime.yy_mm_dd(date_of_birth));
                                    } else {
                                        date_of_birth = null;
                                        textViewEditProfileDOB.setText("");
                                        ShowToast.toast("Invalid Date of Birth", EditProfileActivity.this);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);

                datePicker.show();
                break;
        }
    }

    private void getUserProfile() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROFILE);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_PROFILE;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {

                    Gson gson = new Gson();

                    GetProfileResponse mGetProfileResponse = gson.fromJson(response, GetProfileResponse.class);

                    String dob = "";

                    if (mGetProfileResponse != null) {
                        if (!mGetProfileResponse.getGet_profile().isEmpty()) {
                            for (GetProfile mobj : mGetProfileResponse.getGet_profile()) {
                                dob = mobj.getDob();
                            }
                        }
                    }

                    if (!dob.isEmpty()) {
                        textViewEditProfileDOB.setText(dob);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void submitUserSetting() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.EDIT_PROFILE);
        requestMap.put(General.DOB, textViewEditProfileDOB.getText().toString().trim());
        requestMap.put(General.FIRST_NAME, et_firstname.getText().toString().trim());
        requestMap.put(General.LAST_NAME, et_lastname.getText().toString().trim());

        int posCounrty = spinnerCountryList.getSelectedItemPosition();
        int counrtyId = countryArrayList.get(posCounrty).getId();
        requestMap.put(General.COUNTRY, String.valueOf(counrtyId));

        int stateId = 0;
        if (stateArrayList.size() == 0) {
            requestMap.put(General.STATE, String.valueOf(stateId));
        } else {
            int posState = spinnerStateList.getSelectedItemPosition();
            stateId = stateArrayList.get(posState).getId();
            requestMap.put(General.STATE, String.valueOf(stateId));
        }

        int cityId = 0;
        if (cityArrayList.size() == 0) {
            requestMap.put(General.CITY, String.valueOf(cityId));
        } else {
            int posCity = spinnerCityList.getSelectedItemPosition();
            cityId = cityArrayList.get(posCity).getId();
            requestMap.put(General.CITY, String.valueOf(cityId));
        }
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_USER_SETTING;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.EDIT_PROFILE);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(EditProfileActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                        Preferences.save(General.COUNTRY_ID, counrtyId);
                        Preferences.save(General.STATE_ID, stateId);
                        Preferences.save(General.CITY_ID, cityId);
                    } else {
                        Toast.makeText(EditProfileActivity.this,
                                jsonAddJournal.get(General.ERROR).getAsString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //validate all fields
    private boolean validate() {
        String fist_name = et_firstname.getText().toString().trim();
        String last_name = et_lastname.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String user_name = et_username.getText().toString().trim();
        String address = editTextEditProfileAddress.getText().toString();
        String bob = textViewEditProfileDOB.getText().toString();

        Preferences.save(General.FIRST_NAME, fist_name);
        Preferences.save(General.LAST_NAME, last_name);

        if (!LoginValidator.isName(fist_name)) {
            Toast.makeText(EditProfileActivity.this, "Invalid First Name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!LoginValidator.isName(last_name)) {
            Toast.makeText(EditProfileActivity.this, "Invalid Last Name", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!LoginValidator.isName(bob)) {
            Toast.makeText(EditProfileActivity.this, "Invalid Date of Birth", Toast.LENGTH_LONG).show();
            return false;
        }

        /* if (!LoginValidator.isName(countryName)) {
            Toast.makeText(EditProfileActivity.this, "Please select country", Toast.LENGTH_LONG).show();
            return false;
        }

       if (!LoginValidator.isName(stateName)) {
            Toast.makeText(EditProfileActivity.this, "Please select state", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!LoginValidator.isName(cityName)) {
            Toast.makeText(EditProfileActivity.this, "Please select city", Toast.LENGTH_LONG).show();
            return false;
        }*/

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("SimpleDateFormat")
    private int compareDate(String today, String selected_date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        Date date1 = dateFormat.parse(today);
        Date date2 = dateFormat.parse(selected_date);

        calendar1.setTime(date1);
        calendar2.setTime(date2);

        return calendar1.compareTo(calendar2);
    }
}
