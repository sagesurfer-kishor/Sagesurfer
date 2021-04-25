package com.modules.task;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.calendar.ParticipantsListDialog;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Task_;
import com.sagesurfer.parser.Participant_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformDeleteTask;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.database.constants.TableList_;
import com.storage.database.operations.DatabaseDeleteRecord_;
import com.storage.database.operations.DatabaseUpdate_;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 19-07-2017
 *         Last Modified on 14-12-2017
 */

public class TaskDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = TaskDetailsActivity.class.getSimpleName();

    private Task_ task_;
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
        if (data.hasExtra(General.TASK_LIST)) {
            task_ = (Task_) data.getSerializableExtra(General.TASK_LIST);
            setData();
        } else {
            onBackPressed();
        }
    }

    private void setData() {
        if (task_.getIsDelete() == 1) {
            menuButton.setVisibility(View.VISIBLE);
        } else {
            menuButton.setVisibility(View.GONE);
        }

        TextView nameText = (TextView) findViewById(R.id.announcement_details_name);
        nameText.setText(ChangeCase.toTitleCase(task_.getFullName()));
        TextView titleText = (TextView) findViewById(R.id.announcement_details_title);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(task_.getTitle());
        TextView descriptionText = (TextView) findViewById(R.id.announcement_details_description);
        descriptionText.setText(task_.getDescription());

        TextView postedInText = (TextView) findViewById(R.id.announcement_details_posted_in);

        if (task_.getOwnOrTeam() == 0) {
            postedInText.setVisibility(View.GONE);
        } else {
            String posted_id = this.getResources().getString(R.string.posted_in) + " " + task_.getTeamName();
            postedInText.setText(ChangeCase.toTitleCase(posted_id));
        }

        TextView dateText = (TextView) findViewById(R.id.announcement_details_time);
        dateText.setText(GetTime.dateValue(task_.getDate()));
        ImageView icon = (ImageView) findViewById(R.id.announcement_details_image);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        if(task_.getImage() != null) {
            Glide.with(getApplicationContext()).load(task_.getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(task_.getImage()))
                            .transform(new CircleTransform(getApplicationContext())))
                    .into(icon);
        }

        if(task_.getParticipants() != null && task_.getParticipants().size() > 0) {
            linearLayoutParticipants.setVisibility(View.VISIBLE);
            setDetails();
        }
    }

    // set more event details fetched to respective fields
    private void setDetails() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int size = (int) this.getResources().getDimension(R.dimen.profile_size) + (int) this.getResources().getDimension(R.dimen.activity_horizontal_margin);
        int width = displayMetrics.widthPixels;
        addParticipantPhoto((width / size));
    }

    // Show participants list at bottom with their photos
    private void addParticipantPhoto(int count) {
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.event_details_image_list);

        for (int i = 0; i < task_.getParticipants().size(); i++) {
            if (i <= (count - 1)) {
                final View view = LayoutInflater.from(this).inflate(R.layout.participant_item_layout, viewGroup, false);
                ImageView userPhoto = (ImageView) view.findViewById(R.id.participants_item_image);

                //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                Glide.with(getApplicationContext())
                        .load(task_.getParticipants().get(i).getPhoto())
                        .thumbnail(0.5f)
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                .placeholder(GetThumbnails.userIcon(task_.getParticipants().get(i).getPhoto()))
                                .transform(new CircleTransform(getApplicationContext())))
                        .into(userPhoto);
                if (i == (count - 1)) {
                    TextView countText = (TextView) view.findViewById(R.id.participants_item_count);
                    countText.setVisibility(View.VISIBLE);
                    String counters = "+" + (task_.getParticipants().size() - count) + "";
                    countText.setText(counters);
                }
                viewGroup.addView(view);
            }
        }

        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task_.getParticipants().size() > 0) {
                    openParticipantsList(task_.getParticipants(), getApplicationContext().getResources().getString(R.string.participants));
                } else {
                    ShowSnack.viewWarning(v, getApplicationContext().getResources().getString(R.string.participants_unavailable), getApplicationContext());
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

    // delete record from local database
    private void deleteRecord(View view) {
        DatabaseDeleteRecord_ databaseDeleteRecord_ = new DatabaseDeleteRecord_(getApplicationContext());
        String id = "" + task_.getId();
        String is_delete = "" + task_.getIsDelete();
        String alert_id = "0";
        int response = PerformDeleteTask.deleteAlert(id, General.TASK_LIST, is_delete,alert_id, TAG, getApplicationContext(), this);
        if (response == 1) {
            databaseDeleteRecord_.deleteRecord(TableList_.TABLE_TASK_LIST, "" + task_.getId(), General.ID);
        }
        showResponses(response, view);
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
        subTitle.setText(this.getResources().getString(R.string.task_delete_confirmation));
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
        int status = PerformReadTask.readAlert("" + task_.getId(), General.TASK_LIST_GROUP/*"tasklist"*/, TAG, getApplicationContext(), this);
        if (status == 1) {
            DatabaseUpdate_ databaseUpdate_ = new DatabaseUpdate_(getApplicationContext());
            databaseUpdate_.updateRead(General.IS_READ, TableList_.TABLE_TASK_LIST, "1", "" + task_.getId());
        }
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
                if (task_.getIsDelete() == 1) {
                    deleteConfirmation();
                } /*else {
                    deleteRecord(v);
                }*/
                break;
        }
    }
}
