package com.modules.announcement;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformDeleteTask;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.database.constants.TableList_;
import com.storage.database.operations.DatabaseDeleteRecord_;
import com.storage.database.operations.DatabaseUpdate_;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 19-07-2017
 * Last Modified on 13-12-2017
 **/

public class AnnouncementDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = AnnouncementDetailsActivity.class.getSimpleName();
    private Announcement_ announcement_;
    private Toolbar toolbar;
    private AppCompatImageButton menuButton;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.announcement_details_layout);

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
        titleText.setText("");

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.GONE);

        menuButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_menu);
        menuButton.setVisibility(View.VISIBLE);
        menuButton.setImageResource(R.drawable.vi_delete_white);
        menuButton.setOnClickListener(this);

        Intent data = getIntent();
        if (data.hasExtra(Actions_.ANNOUNCEMENT)) {
            announcement_ = (Announcement_) data.getSerializableExtra(Actions_.ANNOUNCEMENT);
            setData();
        } else {
            onBackPressed();
        }
    }

    //Set data to respective fields
    private void setData() {
        if (announcement_.getIsDelete() == 1) {
            menuButton.setVisibility(View.VISIBLE);
        } else {
            menuButton.setVisibility(View.GONE);
        }

        TextView nameText = (TextView) findViewById(R.id.announcement_details_name);
        nameText.setText(ChangeCase.toTitleCase(announcement_.getCreatedBy()));
        TextView titleText = (TextView) findViewById(R.id.announcement_details_description);
        titleText.setText(announcement_.getDescription());
        TextView postedInText = (TextView) findViewById(R.id.announcement_details_posted_in);
        String posted_id = this.getResources().getString(R.string.posted_in) + " " + announcement_.getTeamName();
        postedInText.setText(ChangeCase.toTitleCase(posted_id));
        TextView dateText = (TextView) findViewById(R.id.announcement_details_time);
        dateText.setText(GetTime.getTodayEeeMm(announcement_.getDate()));
        ImageView icon = (ImageView) findViewById(R.id.announcement_details_image);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        if (announcement_.getImage() != null) {
            Glide.with(getApplicationContext())
                    .load(announcement_.getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(announcement_.getImage()))
                            .transform(new CircleTransform(getApplicationContext())))
                    .into(icon);
        }
    }

    // Get delete confirmation before deleting any records
    private void deleteConfirmation() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        subTitle.setText(this.getResources().getString(R.string.announcement_delete_confirmation));
        title.setText(this.getResources().getString(R.string.action_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(v);
                dialog.dismiss();
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

    // Delete record from local database
    private void deleteRecord(View view) {
        DatabaseDeleteRecord_ databaseDeleteRecord_ = new DatabaseDeleteRecord_(getApplicationContext());
        String id = "" + announcement_.getId();
        String is_delete = "" + announcement_.getIsDelete();
        String alert_id = "0";
        int response = PerformDeleteTask.deleteAlert(id, General.ANNOUNCEMENT, is_delete,alert_id, TAG, getApplicationContext(), this);
        if (response == 1) {
            databaseDeleteRecord_.deleteRecord(TableList_.TABLE_ANNOUNCEMENT, "" + announcement_.getId(), General.ID);
        }
        showResponses(response, view);
    }

    // Show action response using toast or snack bar
    private void showResponses(int status, View view) {
        String message = "";
        if (status == 1) {
            message = getApplicationContext().getResources().getString(R.string.successful);
        } else if (status == 3) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        // Make network call to read entry
        int status = PerformReadTask.readAlert("" + announcement_.getId(), General.ANNOUNCEMENT, TAG, getApplicationContext(), this);
        if (status == 1) {
            // Update record for read/unread
            DatabaseUpdate_ databaseUpdate_ = new DatabaseUpdate_(getApplicationContext());
            databaseUpdate_.updateRead(General.IS_READ, TableList_.TABLE_ANNOUNCEMENT, "1", "" + announcement_.getId());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0,0);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mail_details_recipient_button:
                break;
            case R.id.imagebutton_activitytoolbar_menu:
                if (announcement_.getIsDelete() == 1) {
                    deleteConfirmation();
                } /*else {
                    deleteRecord(v);
                }*/
                break;
        }
    }
}
