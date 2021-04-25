package com.modules.journaling.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.modules.journaling.model.Journal_;
import com.modules.wall.AttachmentListActivity;
import com.modules.wall.Attachment_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.FileUpload;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.library.PathUtils;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class JournalAddActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = JournalAddActivity.class.getSimpleName();
    private AppCompatImageView postButton;
    private EditText createJournalTitle, createJournalSubject, createJournalDesc, createJournalTag, createJournalLink;
    private ImageView journalTagImg, journalLocationImg, journalLinkImg, journalAttachImg, journalHeartImg;
    private ImageView journalTagImgOne, journalLocationImgOne, journalLinkImgOne, journalAttachImgOne, journalHeartImgOne;
    private LinearLayout journalTagLayout, journalLinkLayout, journalAttachmentLayout;
    private TextView descCounter;
    private boolean setTag = false, setLink = false, setFav = false, editAttachment = false;
    private AppCompatImageView attachmentOne, attachmentTwo, attachmentThree;
    private AppCompatImageButton cancelOne, cancelTwo, cancelThree;
    private ArrayList<Attachment_> attachmentArrayList, deletedAttachmentList;
    private String journalTitle, journalSubject, journalDesc, journalTag, journalLink;
    // edit journal data
    private String journalUpdate, locationName, latitude = "", longitude = "";
    private Journal_ journal;
    private SparseIntArray mErrorString;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_journal_entry);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        attachmentArrayList = new ArrayList<>();
        deletedAttachmentList = new ArrayList<>();
        mErrorString = new SparseIntArray();

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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
        postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        initUI();

        Intent data = getIntent();
        if (data != null && data.hasExtra(General.JOURNAL)) {
            titleText.setPadding(20, 0, 0, 0);
            titleText.setText(getResources().getString(R.string.edit_journal));

            journalUpdate = data.getStringExtra(Actions_.UPDATE_JOURNAL);
            journal = (Journal_) data.getSerializableExtra(General.JOURNAL);

            journalTagLayout.setVisibility(View.VISIBLE);
            journalLinkLayout.setVisibility(View.VISIBLE);
            journalAttachmentLayout.setVisibility(View.VISIBLE);

            createJournalTitle.setText(journal.getTitle());
            createJournalSubject.setText(journal.getSubject());
            createJournalDesc.setText(journal.getDescription());
            createJournalTag.setText(journal.getTags());
            createJournalLink.setText(journal.getLink());

            latitude = journal.getLatitude();
            longitude = journal.getLongitude();

            attachmentArrayList = journal.getAttachmentList();


            if (journal.getTags().equals("")) {
                journalTagLayout.setVisibility(View.GONE);
                journalTagImg.setImageResource(R.drawable.vi_tag_gray);
            } else {
                journalTagImg.setImageResource(R.drawable.vi_tag_blue);
            }

            if (journal.getLink().equals("")) {
                journalLinkLayout.setVisibility(View.GONE);
                journalLinkImg.setImageResource(R.drawable.vi_link_gray);
            } else {
                journalLinkImg.setImageResource(R.drawable.vi_link_blue);
            }

            if (journal.getAttachmentList().size() == 0) {
                journalAttachmentLayout.setVisibility(View.GONE);
                journalAttachImg.setImageResource(R.drawable.vi_attachment_gray);
            } else {
                journalAttachImg.setImageResource(R.drawable.vi_attachment_blue);
            }

            if (journal.getLongitude().equals("0") || journal.getLatitude().equals("0")) {
                journalLocationImg.setImageResource(R.drawable.vi_location_gray);
            } else {
                journalLocationImg.setImageResource(R.drawable.vi_location_blue);
            }

            if (journal.getIs_fav() == 1) {
                journalHeartImg.setImageResource(R.drawable.vi_heart_red);
            } else {
                journalHeartImg.setImageResource(R.drawable.vi_heart_gray);
            }

            setFile();

        } else {
            journalUpdate = "";
            titleText.setPadding(100, 0, 0, 0);
            titleText.setText(this.getResources().getString(R.string.journal_entry));
        }
    }

    private void initUI() {
        createJournalTitle = (EditText) findViewById(R.id.create_journal_title_txt);
        createJournalSubject = (EditText) findViewById(R.id.create_journal_subject_txt);
        createJournalDesc = (EditText) findViewById(R.id.create_journal_desc_txt);
        createJournalTag = (EditText) findViewById(R.id.create_journal_tag_txt);
        createJournalLink = (EditText) findViewById(R.id.create_journal_link_txt);

        journalTagImg = (ImageView) findViewById(R.id.journal_tag_img);
        journalLocationImg = (ImageView) findViewById(R.id.journal_location_img);
        journalLinkImg = (ImageView) findViewById(R.id.journal_link_img);
        journalAttachImg = (ImageView) findViewById(R.id.journal_attachment_img);
        journalHeartImg = (ImageView) findViewById(R.id.vi_heart_img);
        journalHeartImgOne = (ImageView) findViewById(R.id.vi_heart_img_one);

        journalTagImgOne = (ImageView) findViewById(R.id.journal_tag_img_one);
        journalLocationImgOne = (ImageView) findViewById(R.id.journal_location_img_one);
        journalLinkImgOne = (ImageView) findViewById(R.id.journal_link_img_one);
        journalAttachImgOne = (ImageView) findViewById(R.id.journal_attachment_img_one);

        createJournalDesc.addTextChangedListener(mTextEditorWatcher);
        descCounter = (TextView) findViewById(R.id.text_counter);

        journalTagLayout = (LinearLayout) findViewById(R.id.journal_tag_layout);
        journalLinkLayout = (LinearLayout) findViewById(R.id.journal_link_layout);
        journalAttachmentLayout = (LinearLayout) findViewById(R.id.journal_list_item_attachment_layout);

        journalTagImg.setOnClickListener(this);
        journalLocationImg.setOnClickListener(this);
        journalLinkImg.setOnClickListener(this);
        journalAttachImg.setOnClickListener(this);
        journalHeartImg.setOnClickListener(this);
        journalHeartImgOne.setOnClickListener(this);

        journalTagImgOne.setOnClickListener(this);
        journalLocationImgOne.setOnClickListener(this);
        journalLinkImgOne.setOnClickListener(this);
        journalAttachImgOne.setOnClickListener(this);

        attachmentOne = (AppCompatImageView) findViewById(R.id.wall_post_attachment_one);
        attachmentTwo = (AppCompatImageView) findViewById(R.id.wall_post_attachment_two);
        attachmentThree = (AppCompatImageView) findViewById(R.id.wall_post_attachment_three);

        cancelOne = (AppCompatImageButton) findViewById(R.id.wall_post_image_one_cancel);
        cancelOne.setOnClickListener(this);
        cancelTwo = (AppCompatImageButton) findViewById(R.id.wall_post_image_two_cancel);
        cancelTwo.setOnClickListener(this);
        cancelThree = (AppCompatImageButton) findViewById(R.id.wall_post_image_three_cancel);
        cancelThree.setOnClickListener(this);
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            descCounter.setText(String.valueOf(s.length()) + "/500");
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.journal_tag_img:
                journalTagLayout.setVisibility(View.VISIBLE);
                journalTagImg.setVisibility(View.GONE);
                journalTagImgOne.setVisibility(View.VISIBLE);
                setTag = true;
                break;
            case R.id.journal_location_img:
                openLocationMapDialog();
                journalLocationImg.setVisibility(View.GONE);
                journalLocationImgOne.setVisibility(View.VISIBLE);
                break;

            case R.id.journal_link_img:
                journalLinkLayout.setVisibility(View.VISIBLE);
                journalLinkImg.setVisibility(View.GONE);
                journalLinkImgOne.setVisibility(View.VISIBLE);
                setLink = true;
                break;

            case R.id.journal_attachment_img:

                if (journalUpdate.equalsIgnoreCase(Actions_.UPDATE_JOURNAL)) {
                    // attachmentArrayList.clear();
                    editAttachment = true;
                }

                ActivityCompat.requestPermissions(JournalAddActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

//                Intent journalIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                journalIntent.setType("*/*");
//                startActivityForResult(journalIntent, Chat.INTENT_FILE);
//                journalAttachImg.setVisibility(View.GONE);
//                journalAttachImgOne.setVisibility(View.VISIBLE);
                break;

            case R.id.journal_tag_img_one:
                journalTagLayout.setVisibility(View.GONE);
                journalTagImg.setVisibility(View.VISIBLE);
                journalTagImgOne.setVisibility(View.GONE);
                break;

            case R.id.journal_location_img_one:
                journalLocationImg.setVisibility(View.VISIBLE);
                journalLocationImgOne.setVisibility(View.GONE);
                break;
            case R.id.journal_link_img_one:
                journalLinkLayout.setVisibility(View.GONE);
                journalLinkImg.setVisibility(View.VISIBLE);
                journalLinkImgOne.setVisibility(View.GONE);
                setLink = false;
                createJournalLink.setText("");
                break;

            case R.id.journal_attachment_img_one:
                journalAttachmentLayout.setVisibility(View.GONE);
                journalAttachImg.setVisibility(View.VISIBLE);
                journalAttachImgOne.setVisibility(View.GONE);
                break;

            case R.id.vi_heart_img:

                if (journalUpdate.equals(Actions_.UPDATE_JOURNAL)) {
                    journalHeartImg.setVisibility(View.VISIBLE);
                } else {
                    journalHeartImg.setVisibility(View.GONE);
                    journalHeartImgOne.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Journal has been set favourite successfully", Toast.LENGTH_LONG).show();
                }
                setFav = true;
                break;

            case R.id.vi_heart_img_one:

                if (journalUpdate.equals(Actions_.UPDATE_JOURNAL)) {
                    journalHeartImg.setVisibility(View.VISIBLE);
                } else {
                    journalHeartImg.setVisibility(View.VISIBLE);
                    journalHeartImgOne.setVisibility(View.GONE);
                    Toast.makeText(this, "Journal has been set unfavourite successfully", Toast.LENGTH_LONG).show();
                }
                setFav = false;
                break;

            case R.id.wall_post_image_three_cancel:
                new RemoveAttachment(2).execute();
                break;
            case R.id.wall_post_image_two_cancel:
                new RemoveAttachment(1).execute();
                break;
            case R.id.wall_post_image_one_cancel:
                new RemoveAttachment(0).execute();
                break;

            case R.id.imageview_toolbar_save:
                journalTitle = createJournalTitle.getText().toString().trim();
                journalSubject = createJournalSubject.getText().toString().trim();
                journalDesc = createJournalDesc.getText().toString().trim();
                journalTag = createJournalTag.getText().toString().trim();
                journalLink = createJournalLink.getText().toString().trim();

                if (journalUpdate.equalsIgnoreCase(Actions_.UPDATE_JOURNAL)) {
                    if (journalTitle.equalsIgnoreCase(journal.getTitle())
                            && journalSubject.equalsIgnoreCase(journal.getSubject())
                            && journalDesc.equalsIgnoreCase(journal.getDescription())
                            && journalTag.equalsIgnoreCase(journal.getTags())
                            && journalLink.equalsIgnoreCase(journal.getLink())) {

                        if (editAttachment) {
                            updateNewJournalEntry(journalTitle, journalSubject, journalDesc, journalTag, journalLink);
                        } else {
                            showEditNotChangedDialog(getResources().getString(R.string.edit_not_changed_anything_submit_msg));
                        }

                    } else if (journalTitle.equalsIgnoreCase(journal.getTitle())
                            || journalSubject.equalsIgnoreCase(journal.getSubject())
                            || journalDesc.equalsIgnoreCase(journal.getDescription())
                            || journalTag.equalsIgnoreCase(journal.getTags())
                            || journalLink.equalsIgnoreCase(journal.getLink())) {
                        if (JournalValidation(journalTitle, journalSubject, journalDesc, journalTag, journalLink, v)) {
                            updateNewJournalEntry(journalTitle, journalSubject, journalDesc, journalTag, journalLink);
                        }
                    } else {
                        if (JournalValidation(journalTitle, journalSubject, journalDesc, journalTag, journalLink, v)) {
                            updateNewJournalEntry(journalTitle, journalSubject, journalDesc, journalTag, journalLink);
                        }
                    }
                } else {
                    if (JournalValidation(journalTitle, journalSubject, journalDesc, journalTag, journalLink, v)) {
                        createNewJournalEntry(journalTitle, journalSubject, journalDesc, journalTag, journalLink);
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent journalIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    journalIntent.setType("*/*");
                    startActivityForResult(journalIntent, Chat.INTENT_FILE);
                    journalAttachImg.setVisibility(View.GONE);
                    journalAttachImgOne.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), mErrorString.get(requestCode),
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void showEditNotChangedDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_peer_note_edit);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final TextView titleTxt = (TextView) dialog.findViewById(R.id.title_txt);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final ImageButton buttonCancel = (ImageButton) dialog.findViewById(R.id.button_close);

        textViewMsg.setText(message);
        titleTxt.setText("Journal Update");

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNewJournalEntry(journalTitle, journalSubject, journalDesc, journalTag, journalLink);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void openLocationMapDialog() {
        Intent locationIntent = new Intent(this, MapDialogActivity.class);
        /*locationIntent.putExtra(General.LATITUDE, latitude);
        locationIntent.putExtra(General.LONGITUDE, longitude);*/
        startActivityForResult(locationIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Chat.INTENT_FILE:
                    // String file_path = PathUtils.getFilePath(getApplicationContext(), data.getData());

                    String file_path = UriUtils.getPathFromUri(getApplicationContext(), data.getData());

                    if (file_path == null || file_path.trim().length() <= 0) {
                        Toast.makeText(this, this.getResources().getString(R.string.valid_file_error), Toast.LENGTH_LONG).show();
                        return;
                    }
                    double size = FileOperations.getSizeMB(file_path);
                    if (size > 10) {
                        Toast.makeText(this, this.getResources().getString(R.string.max_10_mb_allowed), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (CheckFileType.isDocument(file_path) || CheckFileType.xlsFile(file_path)
                            || CheckFileType.pdfFile(file_path) || CheckFileType.imageFile(file_path)) {
                        new UploadFile().execute(file_path);
                    } else {
                        ShowToast.toast("Please Select Valid File", getApplicationContext());
                    }
                    break;
            }
        } else if (resultCode == 2) {
            locationName = data.getStringExtra("LOCATION_RESULT");
            Geocoder geoCoder = new Geocoder(getApplicationContext());
            try {
                List<Address> addresses = geoCoder.getFromLocationName(locationName, 5);
                latitude = String.valueOf(addresses.get(0).getLatitude());
                longitude = String.valueOf(addresses.get(0).getLongitude());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == 3) {
            journalLocationImg.setVisibility(View.VISIBLE);
            journalLocationImgOne.setVisibility(View.GONE);
        }
    }

    // make background call to upload file/attachment
    @SuppressLint("StaticFieldLeak")
    private class UploadFile extends AsyncTask<String, Void, Integer> {
        ShowLoader showLoader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader = new ShowLoader();
            showLoader.showUploadDialog(JournalAddActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            int status = 12;
            String path = params[0];
            String file_name = FileOperations.getFileName(params[0]);
            String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER;
            String user_id = Preferences.get(General.USER_ID);
            try {
                String response = FileUpload.uploadFile(params[0], file_name, user_id, url,
                        Actions_.FMS, getApplicationContext(), JournalAddActivity.this);
                if (response != null) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                    if (jsonObject.has(Actions_.FMS)) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray(Actions_.FMS);
                        if (jsonArray != null) {
                            JsonObject object = jsonArray.get(0).getAsJsonObject();
                            if (object.has(General.STATUS)) {
                                status = object.get(General.STATUS).getAsInt();
                                if (status == 1) {
                                    Attachment_ attachment_ = new Attachment_();
                                    attachment_.setId(object.get(General.ID).getAsInt());
                                    attachment_.setPath(path);
                                    attachment_.setImage(file_name);
                                    attachment_.setSize(FileOperations.getSize(path));
                                    attachment_.setPost(true);
                                    attachment_.setNewFile(true);
                                    attachmentArrayList.add(attachment_);
                                }
                            } else {
                                status = 11;
                            }
                        } else {
                            status = 11;
                        }
                    }
                } else {
                    status = 11;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (showLoader != null)
                showLoader.dismissUploadDialog();
            switch (result) {
                case 1:
                    ShowToast.toast(getApplicationContext().getResources().
                            getString(R.string.upload_successful), getApplicationContext());
                    setFile();
                    break;
                case 2:
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.failed), getApplicationContext());
                    break;
                case 11:
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.internal_error_occurred), getApplicationContext());
                    break;
                case 12:
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.network_error_occurred), getApplicationContext());
                    break;
                default:
                    break;
            }
        }
    }

    // background call to remove uploaded file
    @SuppressLint("StaticFieldLeak")
    private class RemoveAttachment extends AsyncTask<Void, Void, Void> {
        final int position;

        RemoveAttachment(int position) {
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... params) {
            RequestBody getBody = new FormBody.Builder()
                    .add(General.ACTION, Actions_.DELETE)
                    .add(General.USER_ID, Preferences.get(General.USER_ID))
                    .add(General.ID, GetSelected.wallAttachments(attachmentArrayList))
                    .build();
            try {
                MakeCall.postNormal(Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER, getBody, TAG, JournalAddActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            removeAttachmentId(position);
            ShowToast.toast("Removed", JournalAddActivity.this);
        }
    }

    @SuppressLint("SetTextI18n")
    private void removeAttachmentId(int position) {
        if (attachmentArrayList.size() > 0) {
            if (journalUpdate.equalsIgnoreCase(Actions_.UPDATE_JOURNAL)) {
                deletedAttachmentList.add(attachmentArrayList.get(position));
                editAttachment = true;
            }

            attachmentArrayList.remove(position);

            if (attachmentArrayList.size() == 0) {
                journalAttachImg.setVisibility(View.VISIBLE);
                journalAttachImg.setImageResource(R.drawable.vi_attachment_gray);
                journalAttachImgOne.setVisibility(View.GONE);
            }
            setFile();
        }
    }

    // set file uploaded to appropriate location with appropriate icon/thumbnail
    private void setFile() {
        journalAttachmentLayout.setVisibility(View.VISIBLE);
        TextView count = (TextView) findViewById(R.id.wall_post_attachment_count);
        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attachmentIntent = new Intent(getApplicationContext(), AttachmentListActivity.class);
                attachmentIntent.putExtra(General.ATTACHMENTS, attachmentArrayList);
                startActivity(attachmentIntent);
            }
        });

        if (attachmentArrayList.size() <= 0) {
            attachmentOne.setVisibility(View.GONE);
            attachmentTwo.setVisibility(View.GONE);
            attachmentTwo.setVisibility(View.GONE);

            cancelOne.setVisibility(View.GONE);
            cancelTwo.setVisibility(View.GONE);
            cancelThree.setVisibility(View.GONE);

            count.setVisibility(View.GONE);

            journalAttachmentLayout.setVisibility(View.GONE);

            return;
        }

        attachmentOne.setVisibility(View.VISIBLE);
        attachmentTwo.setVisibility(View.VISIBLE);
        attachmentTwo.setVisibility(View.VISIBLE);

        if (attachmentArrayList.size() > 3) {
            String attachmentCount = "+" + (attachmentArrayList.size() - 3) + " More";
            count.setText(attachmentCount);
            count.setVisibility(View.VISIBLE);

            setAttachmentImage(attachmentArrayList.get(0).getPath(), attachmentOne);
            setAttachmentImage(attachmentArrayList.get(1).getPath(), attachmentTwo);
            setAttachmentImage(attachmentArrayList.get(2).getPath(), attachmentThree);

            cancelOne.setVisibility(View.VISIBLE);
            cancelTwo.setVisibility(View.VISIBLE);
            cancelThree.setVisibility(View.VISIBLE);
            return;
        }
        count.setVisibility(View.GONE);
        if (attachmentArrayList.size() == 3) {
            setAttachmentImage(attachmentArrayList.get(0).getPath(), attachmentOne);
            setAttachmentImage(attachmentArrayList.get(1).getPath(), attachmentTwo);
            setAttachmentImage(attachmentArrayList.get(2).getPath(), attachmentThree);
            cancelOne.setVisibility(View.VISIBLE);
            cancelTwo.setVisibility(View.VISIBLE);
            cancelThree.setVisibility(View.VISIBLE);
            return;
        }
        if (attachmentArrayList.size() == 2) {
            setAttachmentImage(attachmentArrayList.get(0).getPath(), attachmentOne);
            setAttachmentImage(attachmentArrayList.get(1).getPath(), attachmentTwo);
            attachmentThree.setImageResource(0);

            cancelOne.setVisibility(View.VISIBLE);
            cancelTwo.setVisibility(View.VISIBLE);
            cancelThree.setVisibility(View.GONE);
            return;
        }
        if (attachmentArrayList.size() == 1) {
            setAttachmentImage(attachmentArrayList.get(0).getPath(), attachmentOne);
            attachmentThree.setImageResource(0);
            attachmentTwo.setImageResource(0);

            cancelOne.setVisibility(View.VISIBLE);
            cancelTwo.setVisibility(View.GONE);
            cancelThree.setVisibility(View.GONE);
        }
    }

    // set attachment file thumbnail/icon based on file type
    private void setAttachmentImage(String path, ImageView attachmentImage) {
        if (CheckFileType.imageFile(path)) {
            Glide.with(getApplicationContext())
                    .load(path)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(attachmentImage);
        } else {
            attachmentImage.setImageResource(GetThumbnails.attachmentList(path));
        }
    }

    private void createNewJournalEntry(String journalTitle, String journalSubject, String journalDesc, String journalTag, String journalLink) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_JOURNAL);
        requestMap.put(General.TITLE, journalTitle);
        requestMap.put(General.SUBJECT, journalSubject);
        requestMap.put(General.DESC, journalDesc);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.TAGS, journalTag);

        if (latitude.equals("") || longitude.equals("")) {
            requestMap.put(General.LATITUDE, "0");
            requestMap.put(General.LONGITUDE, "0");
        } else {
            requestMap.put(General.LATITUDE, latitude);
            requestMap.put(General.LONGITUDE, longitude);
        }

        requestMap.put(General.LINK, journalLink);
        requestMap.put(General.ATTACHMENTS, GetSelected.wallAttachments(attachmentArrayList));

        if (setFav) {
            requestMap.put(General.IS_FAV, "1");
        } else {
            requestMap.put(General.IS_FAV, "0");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.ADD_JOURNAL);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(JournalAddActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void updateNewJournalEntry(String journalTitle, String journalSubject, String journalDesc, String journalTag, String journalLink) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.UPDATE_JOURNAL);
        requestMap.put(General.TITLE, journalTitle);
        requestMap.put(General.SUBJECT, journalSubject);
        requestMap.put(General.DESC, journalDesc);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.ID, String.valueOf(journal.getId()));
        requestMap.put(General.TAGS, journalTag);
        requestMap.put(General.LATITUDE, latitude);
        requestMap.put(General.LONGITUDE, longitude);
        requestMap.put(General.LINK, journalLink);

        if (deletedAttachmentList.size() > 0) {
            requestMap.put(General.REMOVE_ATTACH_ID, GetSelected.deleteAttachments(deletedAttachmentList));
        } else {
            requestMap.put(General.REMOVE_ATTACH_ID, "0");
        }
        requestMap.put(General.ATTACHMENTS, GetSelected.wallAttachments(attachmentArrayList));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                Log.v("UPDATE_JOURNAL-->", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.UPDATE_JOURNAL);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(JournalAddActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(JournalAddActivity.this, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private boolean JournalValidation(String journalTitle, String journalSubject, String journalDesc, String journalTag, String journalLink, View view) {
        if (journalTitle == null || journalTitle.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Enter valid Journal Title", getApplicationContext());
            return false;
        }
        if (journalTitle.length() < 6 || journalTitle.length() > 140) {
            ShowSnack.viewWarning(view, "Min 6 Char Required\nMax 140 Char allowed", getApplicationContext());
            return false;
        }

        if (journalSubject == null || journalSubject.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Enter valid Journal Subject", getApplicationContext());
            return false;
        }

        if (journalDesc == null || journalDesc.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Enter valid Journal Description", getApplicationContext());
            return false;
        }
        if (journalDesc.length() < 6 || journalDesc.length() > 500) {
            ShowSnack.viewWarning(view, "Min 6 Char Required\nMax 500 Char allowed", getApplicationContext());
            return false;
        }

       /* if (setTag) {
            if (journalTag == null || journalTag.trim().length() <= 0) {
                ShowSnack.viewWarning(view, "Enter valid Journal Tag", getApplicationContext());
                return false;
            }
        }*/

        if (setLink) {
            if (!isValidUrl(journalLink)) {
                ShowSnack.viewWarning(view, "Enter valid Link", getApplicationContext());
                return false;
            }
        }
        return true;
    }

    private boolean isValidUrl(String url) {
        if (url.matches("((http)[s]?(://).*)"))
            return true;
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
