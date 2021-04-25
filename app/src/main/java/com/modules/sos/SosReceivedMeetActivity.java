package com.modules.sos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.modules.crisis.CrisisCount;
import com.modules.crisis.CrisisRiskListActivity;
import com.modules.crisis.InterventionActivity;
import com.modules.crisis.SupportContactListActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.KeyboardOperation;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Crisis_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 6/22/2018.
 */

public class SosReceivedMeetActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = ForwardSosActivity.class.getSimpleName();
    private int sos_id = 0, role_id = 0, group_id = 0;
    private String selection = "", group_name = "";

    SupportMapFragment mapFragment;
    Toolbar toolbar;
    private CrisisCount crisisCount;
    private TextView latestCrisis, resolvedCount, activeCount, episodeCount, frequentIntervention, dateText;
    LinearLayout linearLayoutCrisis;

    GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_sos_received_meet);

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
        titleText.setText(this.getResources().getString(R.string.sos_meet));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setVisibility(View.VISIBLE);
        submitButton.setText(this.getResources().getString(R.string.send));
        submitButton.setOnClickListener(this);

        RelativeLayout crisisRiskLayout = (RelativeLayout) findViewById(R.id.crisis_risk_layout);
        crisisRiskLayout.setOnClickListener(this);
        RelativeLayout supportPersonLayout = (RelativeLayout) findViewById(R.id.crisis_support_person_layout);
        supportPersonLayout.setOnClickListener(this);
        RelativeLayout triggerLayout = (RelativeLayout) findViewById(R.id.crisis_trigger_layout);
        triggerLayout.setOnClickListener(this);
        RelativeLayout interventionLayout = (RelativeLayout) findViewById(R.id.crisis_intervention_layout);
        interventionLayout.setOnClickListener(this);

        latestCrisis = (TextView) findViewById(R.id.crisis_fragment_latest_crisis);
        resolvedCount = (TextView) findViewById(R.id.crisis_fragment_crisis_resolved);
        activeCount = (TextView) findViewById(R.id.crisis_fragment_active_crisis);
        episodeCount = (TextView) findViewById(R.id.crisis_fragment_crisis_episode);
        frequentIntervention = (TextView) findViewById(R.id.crisis_fragment_frequent_intervention);
        dateText = (TextView) findViewById(R.id.crisis_fragment_last_date);
        linearLayoutCrisis = (LinearLayout) findViewById(R.id.linearlayout_crisis);

        Intent data = getIntent();
        if (data != null) {
            if (data.hasExtra(General.ID)) {
                sos_id = data.getIntExtra(General.ID, 0);
                selection = String.valueOf(data.getIntExtra(General.SELECTION, 0));
                group_id = data.getIntExtra(General.GROUP_ID, 0);
                group_name = data.getStringExtra(General.GROUP_NAME);
                role_id = data.getIntExtra(General.ROLE_ID, 0);
            }
        }
        if (sos_id == 0) {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015") && CheckRole.isCoordinator(role_id)) {
            linearLayoutCrisis.setVisibility(View.GONE);
            Preferences.save(General.GROUP_ID, "" + group_id);
            Preferences.save(General.GROUP_NAME, group_name);
            //Preferences.save(General.OWNER_ID, team_.getOwnerId());
            getCount();
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
            linearLayoutCrisis.setVisibility(View.GONE);
        } else {
            linearLayoutCrisis.setVisibility(View.VISIBLE);
            Preferences.save(General.GROUP_ID, "" + group_id);
            Preferences.save(General.GROUP_NAME, group_name);
            //Preferences.save(General.OWNER_ID, team_.getOwnerId());
            getCount();
        }

        //registerLocationUpdates();
    }

    private boolean validate(String message) {
        return !(message == null || message.length() < 6 || message.length() > 140);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_submit:
                EditText feedbackBox = (EditText) findViewById(R.id.edit_text_feedback);
                String message = feedbackBox.getText().toString().trim();
                KeyboardOperation.hide(getApplicationContext(), feedbackBox.getWindowToken());
                if (validate(message)) {
                    sosAttending(message);
                } else {
                    feedbackBox.setError("Invalid message\nMin 6 char required\nMax 140 char allowed");
                }
                break;
            case R.id.crisis_risk_layout:
                intent = new Intent(getApplicationContext(), CrisisRiskListActivity.class);
                intent.putExtra(General.ACTION, Actions_.CRISIS_RISK);
                //startActivity(intent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.crisis_support_person_layout:
                intent = new Intent(getApplicationContext(), SupportContactListActivity.class);
                //startActivity(intent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.crisis_trigger_layout:
                intent = new Intent(getApplicationContext(), CrisisRiskListActivity.class);
                intent.putExtra(General.ACTION, Actions_.TRIGGERS);
                //startActivity(intent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.crisis_intervention_layout:
                intent = new Intent(getApplicationContext(), InterventionActivity.class);
                //startActivity(intent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }

    // make network call for attending sos
    private void sosAttending(String message) {
        int status = SosOperations_.attending(sos_id, Integer.parseInt(selection), GetMessages_.getAttendingMethods(Integer.parseInt(selection), message), getApplicationContext(), this);
        if (status == 1) {
            /*sosList.get(position).setCurrentStatus(2);
            notifyDataSetChanged();*/
            //sosAttended(message);
            onBackPressed();
        }
    }

    // make network call to sos attended
    private void sosAttended(String message) {
        int status = SosOperations_.attended(sos_id, selection, message, getApplicationContext(), this);
        if (status == 1) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Country_ Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Country_ Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, SosReceivedMeetActivity.this);
            //manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Country_ Permission Needed")
                        .setMessage("This app needs the Country_ permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SosReceivedMeetActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // set data to respective fields
    private void setData() {
        String dateString = "Last Crisis " + GetTime.getTodayMm(crisisCount.getTimestamp());
        dateText.setText(dateString);
        resolvedCount.setText(crisisCount.getSuccessful_count());
        activeCount.setText(crisisCount.getActive_count());
        episodeCount.setText(crisisCount.getTotal_count());
        latestCrisis.setText(crisisCount.getLatest_crisis());
        frequentIntervention.setText(crisisCount.getMost_frequent_used());
    }

    // Make network call to  get crisis counter
    private void getCount() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.CRISIS_COUNT);
        requestMap.put("gid", "" + group_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CRISIS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    ArrayList<CrisisCount> crisisCountArrayList = Crisis_.parseCounts(response, TAG, getApplicationContext());
                    if (crisisCountArrayList != null && crisisCountArrayList.size() > 0) {
                        crisisCount = crisisCountArrayList.get(0);
                        if (crisisCount.getStatus() == 1) {
                            setData();
                        } else {
                            activeCount.setText("0");
                            episodeCount.setText("0");
                            resolvedCount.setText("0");
                            latestCrisis.setText(getApplicationContext().getResources().getString(R.string.na));
                            frequentIntervention.setText(getApplicationContext().getResources().getString(R.string.na));
                            dateText.setText(getApplicationContext().getResources().getString(R.string.na));
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*protected android.location.LocationListener locationListener;
    protected LocationManager locationManager;
    protected Context context;

    void registerLocationUpdates() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String provider = locationManager.getBestProvider(criteria, true);

        // Cant get a hold of provider
        if (provider == null) {
            Log.v(TAG, "Provider is null");
            //showNoProvider();
            return;
        } else {
            Log.v(TAG, "Provider: " + provider);
        }

        Location oldLocation = new Location(provider);
        // connect to the GPS location service
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(provider, 1L, 1f, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                oldLocation = locationManager.getLastKnownLocation(provider);
            } else {
                //Request Country_ Permission
                checkLocationPermission();
            }
        }
        else {
            locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(provider, 1L, 1f, locationListener);
            oldLocation = locationManager.getLastKnownLocation(provider);
        }

        if (oldLocation != null)  {
            Log.v(TAG, "Got Old location");
            *//*latitude = Double.toString(oldLocation.getLatitude());
            longitude = Double.toString(oldLocation.getLongitude());
            waitingForLocationUpdate = false;
            getNearbyStores();*//*


            mLastLocation = oldLocation;
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            //Place current location marker
            LatLng latLng = new LatLng(oldLocation.getLatitude(), oldLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
        } else {
            Log.v(TAG, "NO Last Location found");
        }
    }

    public class MyLocationListener implements android.location.LocationListener {
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

            locationManager.removeUpdates(this);
        }

        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.v(TAG, "Status changed: " + s);
        }

        public void onProviderEnabled(String s) {
            Log.e(TAG, "PROVIDER DISABLED: " + s);
        }

        public void onProviderDisabled(String s) {
            Log.e(TAG, "PROVIDER DISABLED: " + s);
        }
    }*/

}




