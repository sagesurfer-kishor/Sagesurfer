package com.modules.sos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.JsonObject;
import com.sagesurfer.adapters.SingleItemAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.KeyboardOperation;
import com.sagesurfer.library.SosInbuiltMessage;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.secure._Base64;
import com.sagesurfer.selectors.SingleTeamSelectorDialog;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 13-07-2017
 * Last Modified on 14-12-2017
 */

public class CreateSosActivity extends AppCompatActivity implements View.OnClickListener, SingleTeamSelectorDialog.GetChoice, LocationListener {
    private static final String TAG = CreateSosActivity.class.getSimpleName();
    private ArrayList<Teams_> teamsArrayList;
    private int group_id = 0;
    private String start_time = "0";
    private TextView teamSelector;
    private EditText messageBox;
    private Spinner spinnerPriority;
    private LinearLayout linearLayoutConsumerName;
    private TextView textViewConsumerNameLabel, textViewConsumerName;
    private int sosPriority = 0;
    LocationManager locationManager;
    Toolbar toolbar;
    AppCompatImageView postButton;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    String locationAddressString = "";
    int status = 12;
    int lat_lon_status = 0, profile_address_status = 0;
    boolean sendSOSClciked = false;
    boolean isGPSOn = false;
    private AlertDialog alertDialog;
    LinearLayout messageLinearLayout;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        setContentView(R.layout.create_sos_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        teamsArrayList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);

        //toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.screen_background));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(120, 0, 0, 0);
        titleText.setText(this.getResources().getString(R.string.create_sos));


        /*int color = Color.parseColor("#a5a5a5"); //text_color_tertiary
        postButton.setColorFilter(color);
        postButton.setImageResource(R.drawable.vi_check_white);*/
        postButton = (AppCompatImageView) toolbar.findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        messageLinearLayout = (LinearLayout) findViewById(R.id.linearlayout_select_message);
        messageLinearLayout.setOnClickListener(this);

        messageBox = (EditText) findViewById(R.id.create_sos_message_box);
        teamSelector = (TextView) findViewById(R.id.create_sos_team_select);
        teamSelector.setOnClickListener(this);

        spinnerPriority = (Spinner) findViewById(R.id.spinner_priority);
        linearLayoutConsumerName = (LinearLayout) findViewById(R.id.linearlayout_consumername);
        textViewConsumerNameLabel = (TextView) findViewById(R.id.textview_consumername_label);
        textViewConsumerName = (TextView) findViewById(R.id.textview_consumername);

        if (CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            linearLayoutConsumerName.setVisibility(View.VISIBLE);
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015")) {
                textViewConsumerNameLabel.setText("Peer Participant");
            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
                textViewConsumerNameLabel.setText("Guest");
            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023")) {
                textViewConsumerNameLabel.setText("Student Name");
            }
        }

        final ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(CreateSosActivity.this, R.array.sos_priority, R.layout.drop_down_text_item_layout);
        priorityAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
        spinnerPriority.setAdapter(priorityAdapter);
        spinnerPriority.setOnItemSelectedListener(onItemSelected);

        lat_lon_status = Integer.valueOf(Preferences.get(General.LATI_LONGI));
        profile_address_status = Integer.valueOf(Preferences.get(General.PROFILE_LOCATION));

        if (lat_lon_status == 1) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

            }

            final LocationManager manager = (LocationManager) CreateSosActivity.this.getSystemService(Context.LOCATION_SERVICE);
            if (!hasGPSDevice(CreateSosActivity.this)) {
                Toast.makeText(CreateSosActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
            }

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(CreateSosActivity.this)) {
                Toast.makeText(CreateSosActivity.this, "Gps not enabled.\nEnable GPS to share your location.", Toast.LENGTH_SHORT).show();
                enableLoc();
            } else {
                //Log.e("TAG","Gps already enabled");
                //Toast.makeText(CreateSosActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            }
        } /*else {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

            }

            final LocationManager manager = (LocationManager) CreateSosActivity.this.getSystemService(Context.LOCATION_SERVICE);
            if (!hasGPSDevice(CreateSosActivity.this)) {
                Toast.makeText(CreateSosActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
            }

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(CreateSosActivity.this)) {
                Toast.makeText(CreateSosActivity.this, "Gps not enabled.\nEnable GPS to share your location.", Toast.LENGTH_SHORT).show();
                enableLoc();
            } else {
                //Log.e("TAG","Gps already enabled");
                //Toast.makeText(CreateSosActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            }
        }*/

        locationShareDialogBox();
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(CreateSosActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(CreateSosActivity.this, REQUEST_LOCATION);
                            //finish();
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }

    private final AdapterView.OnItemSelectedListener onItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            sosPriority = spinnerPriority.getSelectedItemPosition() + 1;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    // check sos message for it's validation
    private boolean sosValidation(String message, View view) {
        if (message == null || message.trim().length() <= 0) {
            //messageBox.setError("Enter valid SOS");
            Log.i(TAG, "sosValidation: not null msg");
            ShowSnack.viewWarning(view, "Enter valid SOS", getApplicationContext());
            return false;
        }

        if (message.length() < 6 || message.length() > 140) {
            Log.i(TAG, "sosValidation: length less than zero msg");
            //messageBox.setError("Min 6 Char Required\nMax 140 Char allowed");
            ShowSnack.viewWarning(view, "Min 6 Char Required\nMax 140 Char allowed", getApplicationContext());
            return false;
        }
        if (group_id == 0) {
            Log.i(TAG, "sosValidation: group id is zero");
            ShowSnack.viewWarning(view, this.getResources().getString(R.string.please_select_team), getApplicationContext());
            return false;
        }
        if (sosPriority == 0) {
            Log.i(TAG, "sosValidation: select priority");
            ShowSnack.viewWarning(view, this.getResources().getString(R.string.please_select_priority), getApplicationContext());
            return false;
        }
        return true;
    }

    // make network call to create sos
    private void sendSos(String message) {
        if (lat_lon_status == 0) {
            locationAddressString = "";
        }
        String info = DeviceInfo.get(this);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.MSG, message);
        requestMap.put(General.START_TIME, start_time);
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.END_TIME, GetTime.getChatTimestamp());
        requestMap.put(General.INFO, _Base64.encode(info));
        requestMap.put(General.IP, DeviceInfo.getDeviceMAC(this));
        requestMap.put("g_id", "" + group_id);
        requestMap.put(General.DEVICE, "a");
        requestMap.put(General.PRIORITY, "" + sosPriority);
        requestMap.put(General.LATI_LONGI, "" + lat_lon_status);
        requestMap.put(General.PROFILE_LOCATION, "" + profile_address_status);
        requestMap.put(General.LOCATION, locationAddressString);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SOS_SEND_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject.has(General.STATUS)) {
                        status = jsonObject.get(General.STATUS).getAsInt();
                    } else {
                        status = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status);
    }

    // make network call to create sos
    private void sendSosNo(String message) {
        if (lat_lon_status == 0) {
            locationAddressString = "";
        }
        String info = DeviceInfo.get(this);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.MSG, message);
        requestMap.put(General.START_TIME, start_time);
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.END_TIME, GetTime.getChatTimestamp());
        requestMap.put(General.INFO, _Base64.encode(info));
        requestMap.put(General.IP, DeviceInfo.getDeviceMAC(this));
        requestMap.put("g_id", "" + group_id);
        requestMap.put(General.DEVICE, "a");
        requestMap.put(General.PRIORITY, "" + sosPriority);
        requestMap.put(General.LATI_LONGI, "" + lat_lon_status);
        requestMap.put(General.PROFILE_LOCATION, "" + profile_address_status);
        requestMap.put(General.LOCATION, locationAddressString);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SOS_SEND_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        Log.i(TAG, "sendSosNo: "+requestBody);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject.has(General.STATUS)) {
                        status = jsonObject.get(General.STATUS).getAsInt();
                    } else {
                        status = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String messageNo;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (status == 1) {
            if (Integer.valueOf(Preferences.get(General.LATI_LONGI)) == 1 && statusOfGPS) {
                messageNo = this.getResources().getString(R.string.successful);
            } else if (Integer.valueOf(Preferences.get(General.PROFILE_LOCATION)) == 1) {
                messageNo = this.getResources().getString(R.string.your_profile_address_location_have_been_shared);
            } else {
                messageNo = this.getResources().getString(R.string.successful);
            }
        } else {
            status = 2;
            messageNo = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, messageNo, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    private void showResponses(int status) {
        String message = null;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (status == 1) {
            if (Integer.valueOf(Preferences.get(General.LATI_LONGI)) == 1 && statusOfGPS) {
                message = this.getResources().getString(R.string.your_current_location_have_been_shared);
            } else if (Integer.valueOf(Preferences.get(General.PROFILE_LOCATION)) == 1) {
                message = this.getResources().getString(R.string.your_current_location_have_been_shared);
            } else if (Integer.valueOf(Preferences.get(General.LATI_LONGI)) == 0 && statusOfGPS) {
                message = this.getResources().getString(R.string.your_current_location_have_been_shared);
            } else {
                message = this.getResources().getString(R.string.successful);
            }
        } else {
            status = 2;
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    // open inbuilt sos message dialog box
    private void showInbuilt() {
        final List<String> messageList = SosInbuiltMessage.getInbuiltMessage();
        final Dialog dialog = new Dialog(this, R.style.MY_DIALOG);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.single_choice_team_dialog_layout);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        assert dialog.getWindow() != null;
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        LinearLayout searchLayout = (LinearLayout) dialog.findViewById(R.id.team_list_search_layout);
        searchLayout.setVisibility(View.GONE);

        TextView title = (TextView) dialog.findViewById(R.id.single_choice_team_dialog_title);
        title.setText(getApplicationContext().getResources().getString(R.string.select_message));
        AppCompatImageButton back = (AppCompatImageButton) dialog.findViewById(R.id.single_choice_team_dialog_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ListView listView = (ListView) dialog.findViewById(R.id.team_list_view_layout_list);
        SingleItemAdapter singleItemAdapter = new SingleItemAdapter(getApplicationContext(), messageList, false);
        listView.setAdapter(singleItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = messageBox.getText().toString();
                if (msg.length() > 0) {
                    String completeMessage = msg + " " + messageList.get(position);
                    messageBox.setText(completeMessage);
                    int pos = messageBox.getText().length();
                    messageBox.setSelection(pos);
                } else {
                    messageBox.setText(messageList.get(position));
                    int pos = messageBox.getText().length();
                    messageBox.setSelection(pos);
                }
                messageBox.setError(null);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //open team selector dialog on click
    @SuppressLint("CommitTransaction")
    private void openTeamSelector() {
        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new SingleTeamSelectorDialog();
        bundle.putSerializable(General.TEAM_LIST, teamsArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.TEAM_LIST);
    }

    @Override
    public void onStart() {
        super.onStart();
        start_time = GetTime.getChatTimestamp();
        if (teamsArrayList != null) {
            teamsArrayList = PerformGetTeamsTask.getNormalTeams(Actions_.SOS_TEAMS, getApplicationContext(), TAG, false, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(General.TEAM_LIST, teamsArrayList);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            teamsArrayList = (ArrayList<Teams_>) savedInstanceState.getSerializable(General.TEAM_LIST);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: hit");
        KeyboardOperation.hide(getApplicationContext(), messageBox.getWindowToken());

        if (postButton.equals(v)) {
            Log.i(TAG, "onClick: sendSOS1");
            String message = messageBox.getText().toString().trim();
            if (sosValidation(message, v)) {
                Log.i(TAG, "onClick: sendSOS2");
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    alertDialog.show();
                    Log.i(TAG, "onClick: sendSOS3");
                } else {
                    lat_lon_status = 1;
                    sendSOSClciked = true;
                    Log.i(TAG, "onClick: sendSOS4");
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if ((lat_lon_status == 0) || (lat_lon_status == 1 && locationAddressString.length() > 0) || (lat_lon_status == 1 && !statusOfGPS)) {
                        sendSOSClciked = false;

                        sendSos(message);
                    }
                }
            } else {
                Log.i(TAG, "onClick: validation failed");
            }
        } else if (teamSelector.equals(v)) {
            if (teamsArrayList == null || teamsArrayList.size() <= 0) {
                ShowSnack.viewWarning(v,
                        this.getResources().getString(R.string.teams_unavailable), getApplicationContext());
                return;
            }
            openTeamSelector();
        } else if (messageLinearLayout.equals(v)) {
            Log.i(TAG, "onClick: message");
            showInbuilt();
        }
    }

    private void locationShareDialogBox() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(CreateSosActivity.this).inflate(R.layout.location_popup, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateSosActivity.this);
        builder.setView(dialogView);

        alertDialog = builder.create();

        final Button yesBtn = dialogView.findViewById(R.id.yes_btn);
        final Button noBtn = dialogView.findViewById(R.id.no_btn);
        final TextView messageTxt = dialogView.findViewById(R.id.message_txt);
        final ImageView closeIcon = dialogView.findViewById(R.id.close_icon);

        if (lat_lon_status == 1) {
            messageTxt.setText("Your location is ON, Do you want to share your location ?");
        } else {
            messageTxt.setText("Your location is OFF, still you want to share your location ?");
        }

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = messageBox.getText().toString().trim();

                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                final boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);


                lat_lon_status = 1;
                sendSOSClciked = true;
                if (statusOfGPS) {
                    if ((lat_lon_status == 0) || (lat_lon_status == 1 && locationAddressString.length() > 0) || (lat_lon_status == 1 && !statusOfGPS)) {
                        sendSOSClciked = false;
                        sendSos(message);
                    }
                }

                if (lat_lon_status == 1) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateSosActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    }

                    if (!hasGPSDevice(CreateSosActivity.this)) {
                        Toast.makeText(CreateSosActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
                    }

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(CreateSosActivity.this)) {
                        Toast.makeText(CreateSosActivity.this, "Gps not enabled.\nEnable GPS to share your location.", Toast.LENGTH_SHORT).show();
                        enableLoc();
                    }
                }
                alertDialog.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageBox.getText().toString().trim();
                lat_lon_status = 0;
                sendSOSClciked = true;
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if ((lat_lon_status == 0) || (lat_lon_status == 1 && locationAddressString.length() > 0) || (lat_lon_status == 1 && !statusOfGPS)) {
                    sendSOSClciked = false;
                    sendSosNo(message);
                }
                alertDialog.dismiss();
            }
        });

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @Override
    public void getChoice(Teams_ teams_, boolean isSelected, int menu_id) {
        if (isSelected) {
            group_id = teams_.getId();
            String group_name = teams_.getName();
            teamSelector.setText(group_name);
            textViewConsumerName.setText(teams_.getConsumer());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // gel locations API
        getLocation();

        if (teamsArrayList.size() == 1) {
            for (Teams_ teams : teamsArrayList) {
                group_id = teams.getId();
                teamSelector.setText(teams.getName());
            }
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String latLonString = "Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationAddressString = addresses.get(0).getAddressLine(0)/*+", "+ addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2)*/;
            //Toast.makeText(CreateSosActivity.this, locationAddressString, Toast.LENGTH_LONG).show();
            isGPSOn = true;
            if (status != 1 && sendSOSClciked == true) {
                sendSOSClciked = false;
                String message = messageBox.getText().toString().trim();
                sendSos(message);
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(CreateSosActivity.this, "Gps not enabled. \n\nEnable GPS to share your location.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
