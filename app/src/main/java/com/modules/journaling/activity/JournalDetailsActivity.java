package com.modules.journaling.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.modules.journaling.model.Journal_;
import com.modules.wall.Attachment_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class JournalDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private static final String TAG = JournalDetailsActivity.class.getSimpleName();
    private Journal_ journal;
    private Toolbar toolbar;
    private ImageView imageViewEdit, imageViewDelete, upArrow, downArrow;
    private AppCompatImageView attachmentOne, attachmentTwo, attachmentThree;
    private LinearLayout tagLayout, linkLayout, attachmentLayout, locationLayout, expandLayout, dropDownLayout, juornalDetailsLayout;
    private TextView journalSubjectTxt, journalTitleTxt, journalDescTxt, journalDateTxt, journalTagsTxt, journalLinkTxt;
    private SupportMapFragment mapFragment;
    private boolean detailsLayout = false,notShowMap = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_journal_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        toolbar = (Toolbar) findViewById(R.id.journal_toolbar);
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

        TextView titleText = (TextView) findViewById(R.id.caseload_peer_note_title);
        titleText.setText(this.getResources().getString(R.string.journal_details));

        Intent data = getIntent();
        if (data.hasExtra(General.JOURNAL)) {
            journal = (Journal_) data.getSerializableExtra(General.JOURNAL);
            detailsLayout = data.getBooleanExtra("details", true);
            notShowMap = data.getBooleanExtra("not_show_map", true);
        } else {
            onBackPressed();
        }

        initUI();
        // set journal details data
        setJournalDetailsData();
    }

    private void initUI() {
        imageViewEdit = (ImageView) findViewById(R.id.imageview_edit);
        imageViewDelete = (ImageView) findViewById(R.id.imageview_delete);
        imageViewEdit.setOnClickListener(this);
        imageViewDelete.setOnClickListener(this);

        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            imageViewEdit.setVisibility(View.VISIBLE);
            imageViewDelete.setVisibility(View.VISIBLE);
        }

        journalTitleTxt = (TextView) findViewById(R.id.journal_title_label);
        journalSubjectTxt = (TextView) findViewById(R.id.journal_subject_label);
        journalDescTxt = (TextView) findViewById(R.id.journal_desc_label);
        journalTagsTxt = (TextView) findViewById(R.id.journal_tag_label);
        journalLinkTxt = (TextView) findViewById(R.id.journal_link_label);
        journalLinkTxt.setOnClickListener(this);
        journalDateTxt = (TextView) findViewById(R.id.journal_date_label);

        tagLayout = (LinearLayout) findViewById(R.id.journal_list_item_tag_layout);
        linkLayout = (LinearLayout) findViewById(R.id.journal_list_item_link_layout);
        locationLayout = (LinearLayout) findViewById(R.id.journal_list_item_location_layout);
        juornalDetailsLayout = (LinearLayout) findViewById(R.id.linearlayout_peer_note_details);

        attachmentLayout = (LinearLayout) findViewById(R.id.journal_list_item_attachment_layout);
        attachmentLayout.setOnClickListener(this);
        attachmentOne = (AppCompatImageView) findViewById(R.id.feed_list_item_attachment_one);
        attachmentTwo = (AppCompatImageView) findViewById(R.id.feed_list_item_attachment_two);
        attachmentThree = (AppCompatImageView) findViewById(R.id.feed_list_item_attachment_three);

        upArrow = (ImageView) findViewById(R.id.up_icon);
        downArrow = (ImageView) findViewById(R.id.down_icon);
        upArrow.setOnClickListener(this);
        downArrow.setOnClickListener(this);
        expandLayout = (LinearLayout) findViewById(R.id.wrap_layout);
        dropDownLayout = (LinearLayout) findViewById(R.id.drop_down_layout);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.journal_map);
        mapFragment.getMapAsync(this);
    }

    private void setJournalDetailsData() {
        journalTitleTxt.setText(ChangeCase.toTitleCase(journal.getTitle()));
        journalSubjectTxt.setText(ChangeCase.toTitleCase(journal.getSubject()));
        journalDescTxt.setText(ChangeCase.toTitleCase(journal.getDescription()));

        if (journal.getTags().equals("")) {
            tagLayout.setVisibility(View.GONE);
            dropDownLayout.setVisibility(View.GONE);
        } else {
            journalTagsTxt.setText(ChangeCase.toTitleCase(journal.getTags()));
            dropDownLayout.setVisibility(View.VISIBLE);
        }

        if (journal.getLink().equals("")) {
            linkLayout.setVisibility(View.GONE);
        } else {
            journalLinkTxt.setText(journal.getLink());
            dropDownLayout.setVisibility(View.VISIBLE);
        }
        journalDateTxt.setText(GetTime.getDateTime(journal.getDb_add_date()));

        if (journal.getAttachmentList().size() == 0) {
            attachmentLayout.setVisibility(View.GONE);
        } else {
            dropDownLayout.setVisibility(View.VISIBLE);
        }

        if (journal.getLongitude().equals("0") || journal.getLatitude().equals("0")) {
            locationLayout.setVisibility(View.GONE);
        } else {
            locationLayout.setVisibility(View.VISIBLE);
            dropDownLayout.setVisibility(View.VISIBLE);
        }

        setAttachment(journal.getAttachmentList());

        if (detailsLayout) {
            juornalDetailsLayout.setBackground(this.getResources().getDrawable(R.drawable.white_rounded_rectangle_blue_border));
        }

        if(notShowMap){
            dropDownLayout.setVisibility(View.GONE);
            locationLayout.setVisibility(View.GONE);
        }
    }

    // show list of files uploaded with wall post
    private void setAttachment(ArrayList<Attachment_> attachmentArrayList) {
        AppCompatImageView[] imageViews = {attachmentOne, attachmentTwo, attachmentThree};
        TextView count = (TextView) findViewById(R.id.feed_list_item_attachment_count);

        if (attachmentArrayList.size() > 3) {
            String attachmentCount = "+" + (attachmentArrayList.size() - 3) + " More";
            count.setText(attachmentCount);
            count.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < attachmentArrayList.size(); i++) {
            if (i < imageViews.length) {
                setAttachmentImage(attachmentArrayList.get(i).getPath(), imageViews[i]);
            }
        }
    }

    private void setAttachmentImage(String path, ImageView attachmentImage) {
        if (CheckFileType.imageFile(path)) {
            Glide.with(this)
                    .load(path)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(attachmentImage);
        } else {
            attachmentImage.setImageResource(GetThumbnails.attachmentList(path));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_edit:
                Intent journalDetails = new Intent(getApplicationContext(), JournalAddActivity.class);
                journalDetails.putExtra(Actions_.UPDATE_JOURNAL, Actions_.UPDATE_JOURNAL);
                journalDetails.putExtra(General.JOURNAL, journal);
                startActivity(journalDetails);
                finish();
                break;

            case R.id.imageview_delete:
                deleteJournalDetailsAPI();
                break;

            case R.id.journal_link_label:
                Uri uri = Uri.parse(journalLinkTxt.getText().toString().trim());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;

            case R.id.journal_list_item_attachment_layout:
                Intent attachmentIntent = new Intent(this, JournalAttachmentActivity.class);
                attachmentIntent.putExtra(General.ATTACHMENTS, journal.getAttachmentList());
                startActivity(attachmentIntent);
                break;

            case R.id.up_icon:
                expandLayout.setVisibility(View.GONE);
                upArrow.setVisibility(View.GONE);
                downArrow.setVisibility(View.VISIBLE);
                break;

            case R.id.down_icon:
                expandLayout.setVisibility(View.VISIBLE);
                upArrow.setVisibility(View.VISIBLE);
                downArrow.setVisibility(View.GONE);
                break;
        }
    }

    private void deleteJournalDetailsAPI() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_JOURNAL);
        requestMap.put(General.ID, String.valueOf(journal.getId()));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.DELETE_JOURNAL);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(JournalDetailsActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            double lat = 0;
            double lng = 0;
            if (journal.getLatitude().length() > 0 && journal.getLongitude().length() > 0) {
                lat = Double.parseDouble(journal.getLatitude());
                lng = Double.parseDouble(journal.getLongitude());
            }
            Geocoder geoCoder = new Geocoder(getApplicationContext());
            List<Address> addresses = geoCoder.getFromLocation(lat, lng, 5);
            if (addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getLocality()
                        + "," + addresses.get(0).getAdminArea() + "," + addresses.get(0).getCountryName();

                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
