package com.modules.messageboard;

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
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.MessageBoard_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformDeleteTask;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.database.operations.DatabaseDeleteRecord_;

import java.util.Calendar;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 2/26/2019.
 */

public class MessageBoardDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MessageBoardDetailsActivity.class.getSimpleName();

    private MessageBoard_ messageBoard_;
    Toolbar toolbar;
    AppCompatImageButton menuButton;
    LinearLayout linearLayoutParticipants;

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
        titleText.setText("");

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.GONE);

        linearLayoutParticipants = (LinearLayout) findViewById(R.id.linearlayout_participants);
        menuButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_menu);
        menuButton.setVisibility(View.VISIBLE);
        menuButton.setImageResource(R.drawable.vi_delete_white);
        menuButton.setOnClickListener(this);

        Intent data = getIntent();
        if (data.hasExtra(General.MESSAGEBOARD)) {
            messageBoard_ = (MessageBoard_) data.getSerializableExtra(General.MESSAGEBOARD);
            setData();
        } else {
            onBackPressed();
        }
    }

    private void setData() {
        if (messageBoard_.getIs_delete() == 1) {
            menuButton.setVisibility(View.VISIBLE);
        } else {
            menuButton.setVisibility(View.GONE);
        }

        TextView nameText = (TextView) findViewById(R.id.announcement_details_name);
        nameText.setText(ChangeCase.toTitleCase(messageBoard_.getFull_name()));
        TextView titleText = (TextView) findViewById(R.id.announcement_details_title);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(messageBoard_.getTitle());

        TextView postedInText = (TextView) findViewById(R.id.announcement_details_posted_in);
        TextView descriptionText = (TextView) findViewById(R.id.announcement_details_description);
        postedInText.setVisibility(View.GONE);
        descriptionText.setVisibility(View.GONE);

        TextView dateText = (TextView) findViewById(R.id.announcement_details_time);
        dateText.setText(getDate(messageBoard_.getLast_updated()));
        ImageView icon = (ImageView) findViewById(R.id.announcement_details_image);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        if (messageBoard_.getImage() != null) {
            Glide.with(getApplicationContext()).load(messageBoard_.getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(messageBoard_.getImage()))
                            .transform(new CircleTransform(getApplicationContext())))
                    .into(icon);
        }
    }

    // delete record from local database
    private void deleteRecord(View view) {
        DatabaseDeleteRecord_ databaseDeleteRecord_ = new DatabaseDeleteRecord_(getApplicationContext());
        String id = "" + messageBoard_.getId();
        String is_delete = "" + messageBoard_.getIs_delete();
        int response = PerformDeleteTask.deleteMessageBoard(id, Actions_.DELETE_MESSAGEBOARD, TAG, getApplicationContext(), this);
        showResponses(response, view);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
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
        subTitle.setText(this.getResources().getString(R.string.messageboard_delete_confirmation));
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
        int status = PerformReadTask.readAlert("" + messageBoard_.getId(), General.MESSAGEBOARD, TAG, getApplicationContext(), this);
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
            case R.id.mail_details_recipient_button:
                break;
            case R.id.imagebutton_activitytoolbar_menu:
                if (messageBoard_.getIs_delete() == 1) {
                    deleteConfirmation();
                }
                break;
        }
    }
}
