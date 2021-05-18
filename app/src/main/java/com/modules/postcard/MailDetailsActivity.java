package com.modules.postcard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.modules.selfcare.CareUploadActivity;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.PostcardAttachment_;
import com.sagesurfer.models.Responses_;
import com.sagesurfer.parser.Mail_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformComposeMailTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 19-07-2017
 * Last Modified on 14-12-2017
 */

public class MailDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MailDetailsActivity.class.getSimpleName();
    private AppCompatImageButton menuButton;
    private PopupWindow popupWindow;
    private Postcard_ postcard_;
    private Toolbar toolbar;
    private String action = "";
    private Boolean show = true;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        setContentView(R.layout.mail_details_layout);
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
                if (show) {
                    Intent messageDetails = new Intent(MailDetailsActivity.this, MainActivity.class);
                    startActivity(messageDetails);
                } else {
                    onBackPressed();
                }
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText("");

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.GONE);

        menuButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_menu);
        menuButton.setVisibility(View.VISIBLE);
        menuButton.setOnClickListener(this);

        final LinearLayout openReceipts = (LinearLayout) findViewById(R.id.mail_details_recipient_button);
        openReceipts.setOnClickListener(this);

        Intent data = getIntent();
        if (data.hasExtra(General.POSTCARD)) {
            action = data.getStringExtra(General.ACTION);
            postcard_ = (Postcard_) data.getSerializableExtra(General.POSTCARD);
            show = data.getBooleanExtra("main_screen", true);
            setData();
        } else {
            onBackPressed();
        }
    }


    // set data of mail to appropriate fields
    private void setData() {

        TextView attachmentTag = (TextView) findViewById(R.id.mail_details_attachment_tag);
        TextView nameText = (TextView) findViewById(R.id.mail_details_name);
        TextView toText = (TextView) findViewById(R.id.mail_details_to_list);
        if (action.equalsIgnoreCase("outbox")) {
            nameText.setText(Preferences.get(General.NAME));
        } else {
            nameText.setText(postcard_.getName());
        }

        toText.setText(getToOrCc());

        TextView timeText = (TextView) findViewById(R.id.mail_details_time);
        timeText.setText(GetTime.wallTime(postcard_.getDate()));

        TextView initialText = (TextView) findViewById(R.id.mail_details_user_initial_text);
        if (postcard_.getName().equalsIgnoreCase("n/a") && postcard_.getFolder().equalsIgnoreCase("draft")) {
            initialText.setText(Preferences.get(General.NAME).substring(0, 1).toUpperCase());
        } else {
            initialText.setText(postcard_.getName().substring(0, 1).toUpperCase());
        }

        TextView subjectText = (TextView) findViewById(R.id.mail_details_subject);
        subjectText.setText(postcard_.getSubject());

        final TextView descriptionText = (TextView) findViewById(R.id.mail_details_description);
        descriptionText.setText(postcard_.getDescription());

        ImageView imgProfile = (ImageView) findViewById(R.id.mail_details_user_image);
        ImageView circleImage = (ImageView) findViewById(R.id.mail_details_user_initial_circle);

        if (PostcardManipulations.isValidImage(postcard_.getPhoto())) {

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(getApplicationContext())
                    .load(postcard_.getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(postcard_.getPhoto()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(getApplicationContext())))
                    .into(imgProfile);

            initialText.setVisibility(View.GONE);
        } else {
            circleImage.setImageResource(R.drawable.primary_circle);
            circleImage.setColorFilter(PostcardManipulations.getRandomMaterialColor(getApplicationContext()));
            initialText.setVisibility(View.VISIBLE);
        }
        if (postcard_.getIsAttachment() == 1) {
            attachmentTag.setVisibility(View.VISIBLE);
        }
        setAttachments();
    }

    private void setAttachments() {
        ViewGroup layout = (ViewGroup) findViewById(R.id.mail_details_attachments);
        if (postcard_.getIsAttachment() == 1) {
            ArrayList<PostcardAttachment_> attachmentArrayList = postcard_.getAttachmentArrayList();
            for (int i = 0; i < attachmentArrayList.size(); i++) {
                addLayout(layout, attachmentArrayList.get(i), i);
            }
        }
    }

    private String getToString() {
        String toString = "- NA -";
        // if (postcard_.getIsToCc() == 1 || postcard_.getIsToCc() == 3 ) {
        if (action.equalsIgnoreCase("outbox")) {
            toString = postcard_.getToAll();
        } else {
            if (postcard_.getToAll().length() > 0) {
                toString = "me, " + postcard_.getToAll();
            } else {
                toString = "me";
            }
        }
        //  }

        if (toString.trim().length() <= 0) {
            toString = "- NA -";
        }
        return toString;
    }

    private String getCcString() {
        String toString = postcard_.getCcAll();
        if (postcard_.getIsToCc() == 2 || postcard_.getIsToCc() == 3) {
            if (postcard_.getCcAll().length() > 0) {
                toString = "me, " + postcard_.getCcAll();
            } else {
                toString = "me";
            }
        }
        if (toString.trim().length() <= 0) {
            toString = "- NA -";
        }
        return toString;
    }

    // open mail details such as sender, receivers, timestamp, etc. in pop window
    @SuppressLint("InflateParams")
    private void initiatePopupWindow(View v) {
        //1:to;2:cc;3:both
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            try {
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                View customView = inflater.inflate(R.layout.recipient_pop_up_dialog, null);
                popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.getContentView().setFocusableInTouchMode(false);
                popupWindow.setFocusable(false);

                TextView from = (TextView) customView.findViewById(R.id.recipient_pop_up_from);
                TextView to = (TextView) customView.findViewById(R.id.recipient_pop_up_to);
                TextView cc = (TextView) customView.findViewById(R.id.recipient_pop_up_cc);
                TextView time = (TextView) customView.findViewById(R.id.recipient_pop_up_time);

                if (action.equalsIgnoreCase("outbox")) {
                    from.setText(Preferences.get(General.USERNAME));
                } else {
                    from.setText(postcard_.getUserName());
                }
                to.setText(getToString());
                cc.setText(getCcString());
                time.setText(GetTime.getTodayEeeMm(postcard_.getDate()));

                Rect location = locateView(v);
                assert location != null;
                popupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, location.left, location.bottom + 20);
                customView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // locate view and give exact below coordinate to it
    private static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    // add attachment item/row to screen
    private void addLayout(ViewGroup viewGroup, final PostcardAttachment_ attachment_, int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.postcard_attachment_item_layout, viewGroup, false);
        view.setTag(position);
        RelativeLayout relativeLayoutItem = (RelativeLayout) view.findViewById(R.id.relativelayout_postcard_attachment_item);
        TextView name = (TextView) view.findViewById(R.id.attachment_list_item_name);
        TextView size = (TextView) view.findViewById(R.id.attachment_list_item_size);
        ImageView icon = (ImageView) view.findViewById(R.id.attachment_list_item_thumbnail);
        TextView extension = (TextView) view.findViewById(R.id.attachment_list_item_thumbnail_text);
        relativeLayoutItem.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission()) {
                    try {
                        DownloadFile downloadFile = new DownloadFile();
                        downloadFile.download(attachment_.getId(), attachment_.getFile(), attachment_.getName(), DirectoryList.ATTACHMENTS_DIR, MailDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        if (attachment_.getName() != null) {
            if (CheckFileType.imageFile(attachment_.getName())) {
                extension.setVisibility(View.GONE);
            } else {
                extension.setVisibility(View.VISIBLE);
                icon.setImageResource(R.drawable.primary_rounded_rectangle);
                if (attachment_.getName() != null) {
                    icon.setColorFilter(this.getResources().getColor(GetColor.getFileIconBackgroundColor(attachment_.getName())));
                    extension.setText(FileOperations.getFileExtension(attachment_.getName()).toUpperCase());
                }
            }
        }

        Glide.with(getApplicationContext()).load(attachment_.getFile())
                .thumbnail(0.5f)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_thumb)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .transition(withCrossFade())
                .into(icon);

        if (attachment_.getName() != null) {
            name.setText(attachment_.getName());
        } else {
            name.setText("");
        }

        size.setText(FileOperations.bytes2String(attachment_.getSize()));
        viewGroup.addView(view);
    }

    private String getToOrCc() {
        String to = getToString();
        String cc = getCcString();
        if (to.trim().length() <= 0) {
            return "cc " + cc;
        } else {
            return "to " + to;
        }
    }

    // call compose mail activity with action of user's choice
    private void actionIntent(String action) {
        Intent nextIntent = new Intent(getApplicationContext(), CreateMailActivity.class);
        nextIntent.putExtra(General.ACTION, action);
        nextIntent.putExtra(General.POSTCARD, postcard_);
        startActivity(nextIntent);
        overridePendingTransition(0, 0);
    }

    private void showResponses(int status) {
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
            SubmitSnackResponse.showSnack(status, message, menuButton, getApplicationContext());
            onBackPressed();
        } else {
            message = this.getResources().getString(R.string.action_failed);
            SubmitSnackResponse.showSnack(status, message, menuButton, getApplicationContext());
        }
    }

    // show postcard/mail action menu on click
    // perform respective operations to that menu
    private void showMenuPopUp(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.inbox_mail_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.inbox_mail_reply:
                        actionIntent(Actions_.REPLY);
                        break;
                    case R.id.inbox_mail_reply_all:
                        actionIntent(Actions_.REPLY_ALL);
                        break;
                    case R.id.inbox_mail_forward:
                        actionIntent(Actions_.FORWARD);
                        break;
                    case R.id.inbox_mail_delete:

                        String response = PerformComposeMailTask.delete("" + postcard_.getMessageId(),
                                "" + postcard_.getId(), postcard_.getFolder(), MailDetailsActivity.this);
                        Responses_ response_ = Mail_.parseActions(response, Actions_.DELETE,
                                getApplicationContext(), TAG);
                        showResponses(response_.getStatus());

                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
                super.onBackPressed();
                this.overridePendingTransition(0, 0);
                finish();
            }
        } else {
            super.onBackPressed();
            this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mail_details_recipient_button:
                initiatePopupWindow(v);
                break;
            case R.id.imagebutton_activitytoolbar_menu:
                showMenuPopUp(v);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }


    //  Check if permissions was granted
    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  Permissions was granted, open the gallery
                ShowToast.toast("permission for " + Manifest.permission.WRITE_EXTERNAL_STORAGE, this);
            }
            //  Permissions was not granted
            else {
                ShowToast.toast("No permission for " + Manifest.permission.WRITE_EXTERNAL_STORAGE, this);
            }
        }
    }

}
