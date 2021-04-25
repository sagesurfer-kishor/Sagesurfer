package com.modules.calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
 import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Event_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Participant_;
import com.sagesurfer.parser.Users_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformDeleteTask;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.database.constants.TableList_;
import com.storage.database.operations.DatabaseDeleteRecord_;
import com.storage.database.operations.DatabaseUpdate_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 17-08-2017
 * Last Modified on 13-12-2017
 */


public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EventDetailsActivity.class.getSimpleName();
    private ArrayList<Participant_> friendsArrayList;

    private Event_ event_;
    AppCompatImageButton menuButton;
    Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        setContentView(R.layout.event_details_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        setSupportActionBar(toolbar);
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
        titleText.setText(this.getResources().getString(R.string.event));

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.GONE);

        menuButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_menu);
        menuButton.setVisibility(View.VISIBLE);
        menuButton.setImageResource(R.drawable.vi_delete_white);
        menuButton.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        Intent data = getIntent();
        if (data.hasExtra(Actions_.GET_EVENTS)) {
            event_ = (Event_) data.getSerializableExtra(Actions_.GET_EVENTS);
            setData();

            // Make network call to read entry
            int status = PerformReadTask.readAlert("" + event_.getId(), General.CALENDAR, TAG, getApplicationContext(), this);
            if (status == 1) {
                // Update record for read/unread
                DatabaseUpdate_ databaseUpdate_ = new DatabaseUpdate_(getApplicationContext());
                databaseUpdate_.updateRead(General.IS_READ, TableList_.TABLE_EVENTS, "1", "" + event_.getId());
            }
        } else {
            onBackPressed();
        }
    }

    // Set data to respective fields from event model
    private void setData() {
        if (event_.getIs_delete() == 1) {
            menuButton.setVisibility(View.VISIBLE);
        } else {
            menuButton.setVisibility(View.GONE);
        }

        TextView nameText = (TextView) findViewById(R.id.event_details_name);
        nameText.setText(event_.getUsername());

        TextView descriptionText = (TextView) findViewById(R.id.event_details_description);
        descriptionText.setText(event_.getDescription());

        TextView titleText = (TextView) findViewById(R.id.event_details_title);
        titleText.setText(event_.getName());

        TextView timeText = (TextView) findViewById(R.id.event_details_time);
        timeText.setText(GetTime.getTodayEeeMm(event_.getTimestamp()));

        ImageView userPhoto = (ImageView) findViewById(R.id.event_details_image);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        if(event_.getImage() != null) {
            Glide.with(getApplicationContext())
                    .load(event_.getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(event_.getImage()))
                            .transform(new CircleTransform(getApplicationContext())))
                    .into(userPhoto);
        }
        getDetails();
    }

    // Make network call to get event details from event id
    private void getDetails() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("event_id", "" + event_.getId());
        requestMap.put(General.ACTION, Actions_.GET_DETAILS);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                    JsonArray jsonArray = jsonObject.get(Actions_.GET_DETAILS).getAsJsonArray();
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        JsonArray users = object.get("participants").getAsJsonArray();
                        friendsArrayList = Users_.parseParticipants(users.toString(), getApplicationContext(), TAG);
                        setDetails(object.get("invitation_accepted").getAsString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Get list of confirmed participants only
    private ArrayList<Participant_> sortList(ArrayList<Participant_> friendsArrayList) {
        ArrayList<Participant_> list = new ArrayList<>();
        for (int i = 0; i < friendsArrayList.size(); i++) {
            if (friendsArrayList.get(i).getIs_accepted() == 1) {
                list.add(friendsArrayList.get(i));
            }
        }
        return list;
    }

    // set more event details fetched to respective fields
    private void setDetails(String participants_confirmed) {
        TextView participants = (TextView) findViewById(R.id.event_details_participants_confirmed);
        String peopleCount = participants_confirmed + " People " + "Confirmed";
        participants.setText(peopleCount);
        participants.setOnClickListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int size = (int) this.getResources().getDimension(R.dimen.profile_size) +
                (int) this.getResources().getDimension(R.dimen.activity_horizontal_margin);
        int width = displayMetrics.widthPixels;
        addParticipantPhoto((width / size));
    }

    // Show participants list at bottom with their photos
    private void addParticipantPhoto(int count) {
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.event_details_image_list);

        for (int i = 0; i < friendsArrayList.size(); i++) {
            if (i <= (count - 1)) {
                final View view = LayoutInflater.from(this).inflate(R.layout.participant_item_layout, viewGroup, false);
                ImageView userPhoto = (ImageView) view.findViewById(R.id.participants_item_image);

                //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                Glide.with(getApplicationContext())
                        .load(friendsArrayList.get(i).getPhoto())
                        .thumbnail(0.5f)
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                .placeholder(GetThumbnails.userIcon(friendsArrayList.get(i).getPhoto()))
                                .transform(new CircleTransform(getApplicationContext())))
                        .into(userPhoto);
                if (i == (count - 1)) {
                    TextView countText = (TextView) view.findViewById(R.id.participants_item_count);
                    countText.setVisibility(View.VISIBLE);
                    String counters = "+" + (friendsArrayList.size() - count) + "";
                    countText.setText(counters);
                }
                viewGroup.addView(view);
            }
        }

        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendsArrayList.size() > 0) {
                    openParticipantsList(friendsArrayList, getApplicationContext().getResources().getString(R.string.participants));
                } else {
                    ShowSnack.viewWarning(v, getApplicationContext().getResources()
                                    .getString(R.string.participants_unavailable), getApplicationContext());
                }
            }
        });
    }

    // Open user/participants list selector pop up
    @SuppressLint("CommitTransaction")
    private void openParticipantsList(ArrayList<Participant_> participantArrayList, String title) {
        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new ParticipantsListDialog();
        bundle.putSerializable(General.USER_LIST, participantArrayList);
        bundle.putSerializable(General.TITLE, title);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.USER_LIST);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.event_details_participants_confirmed:
                if (sortList(friendsArrayList).size() > 0) {
                    openParticipantsList(sortList(friendsArrayList), this.getResources().getString(R.string.participants_confirmed));
                } else {
                    ShowSnack.viewWarning(v, "0" + " People " + "Confirmed", getApplicationContext());
                }
                break;

            case R.id.imagebutton_activitytoolbar_menu:
                if (event_.getIs_delete() == 1) {
                    deleteConfirmation();
                } /*else {
                    deleteRecord(v);
                }*/
                break;
        }
    }

    private void showResponses(int status) {
        String message = "";
        if (status == 1) {
            message = getApplicationContext().getResources().getString(R.string.successful);
        } else if (status == 3) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    // delete record from local database
    private int deleteRecord(View view) {
        String message = "";
        DatabaseDeleteRecord_ databaseDeleteRecord_ = new DatabaseDeleteRecord_(getApplicationContext());
        String id = "" + event_.getId();
        String is_delete = "" + event_.getIs_delete();
        String alert_id = "0";
        int response = PerformDeleteTask.deleteAlert(id, General.CALENDAR, is_delete,alert_id, TAG, getApplicationContext(), this);
        if (response == 1) {
            databaseDeleteRecord_.deleteRecord(TableList_.TABLE_EVENTS, "" + event_.getId(), General.ID);
            message = getApplicationContext().getResources().getString(R.string.successful);
        } else if (response == 3) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(response, message, getApplicationContext());
        return response;
    }

    //open delete confirmation dialog box
    private void deleteConfirmation() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        subTitle.setText(this.getResources().getString(R.string.event_delete_confirmation));
        title.setText(this.getResources().getString(R.string.action_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                int response = deleteRecord(v);
                if (response == 1) {
                    onBackPressed();
                }
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById(R.id.delete_confirmation_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
