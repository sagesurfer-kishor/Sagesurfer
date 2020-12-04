
package com.modules.selfcare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.FileUpload;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.library.PathUtils;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.models.CareUploaded_;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.SelfCare_;
import com.sagesurfer.parser.Users_;
import com.sagesurfer.secure.KeyMaker_;
import com.sagesurfer.selectors.MultiUserSelectorDialog;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.validator.Care;
import com.storage.preferences.Preferences;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 11-09-2017
 * Last Modified on 14-12-2017
 */

public class CareUploadActivity extends AppCompatActivity implements View.OnClickListener,
        SingleChoiceSelectorDialog.GetCategorySelected, MultiUserSelectorDialog.SelectedUsers, MultipleChoiceSelectorDialog.SelectedAge {

    private static final String TAG = CareUploadActivity.class.getSimpleName();
    private ArrayList<Choices_> categoryArrayList, languageArrayList, ageArrayList;
    private int contentType = 1;
    private long file_id = 0;
    private long thumbnail_id = 0;
    private long category_id = 0;
    private String publish_date = "0000-00-00";
    private int mYear = 0, mMonth = 0, mDay = 0;
    private boolean isEdit = false;
    private long care_id = 0;
    private String action = "";
    private ArrayList<Friends_> usersArrayList;
    private String shared_with = "0";
    private String age_ids = "";
    private long language_id = 1;
    private TextView selectContentType, fileNameText, thumbnailNameText, publishDateText,
            selectCategory, selectLanguage, selectAge, selectFriends, selectFriendsTitle, textViewUploadFile, textViewUploadThumbnail, websiteBoxLabel, textViewValidateWebsiteLabel;
    private EditText titleBox, descriptionBox, tagBox, websiteBox;
    private Button fileButton, thumbnailButton;
    private AppCompatImageView fileCheck, thumbnailCheck;
    private ImageView addCategory;
    private LinearLayout thumbnailLayout, fileLayout;
    private Toolbar toolbar;
    private Intent data;
    private CareUploaded_ careUploaded_ = new CareUploaded_();
    private SparseIntArray mErrorString;
    private CoordinatorLayout coordinatorLayout;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.care_upload_activity_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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
        mErrorString = new SparseIntArray();

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getResources().getString(R.string.upload));

        AppCompatImageView postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        fileCheck = (AppCompatImageView) findViewById(R.id.care_upload_file_check);
        thumbnailCheck = (AppCompatImageView) findViewById(R.id.care_upload_thumbnail_check);

        addCategory = (AppCompatImageView) findViewById(R.id.care_upload_add_category);
        addCategory.setOnClickListener(this);

        fileNameText = (TextView) findViewById(R.id.care_upload_file_name);
        thumbnailNameText = (TextView) findViewById(R.id.care_upload_thumbnail_name);
        publishDateText = (TextView) findViewById(R.id.care_upload_publish_date);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        publishDateText.setOnClickListener(this);
        selectContentType = (TextView) findViewById(R.id.care_upload_content_type);
        selectContentType.setOnClickListener(this);
        selectContentType.setText(this.getResources().getString(R.string.images));

        thumbnailLayout = (LinearLayout) findViewById(R.id.care_upload_thumbnail_layout);
        fileLayout = (LinearLayout) findViewById(R.id.care_upload_file_layout);
        textViewUploadFile = (TextView) findViewById(R.id.textview_upload_file);
        textViewUploadThumbnail = (TextView) findViewById(R.id.textview_upload_thumbnail);

        fileButton = (Button) findViewById(R.id.care_upload_file_button);
        fileButton.setOnClickListener(this);
        thumbnailButton = (Button) findViewById(R.id.care_upload_thumbnail_button);
        thumbnailButton.setOnClickListener(this);
        selectCategory = (TextView) findViewById(R.id.care_upload_category);
        selectCategory.setOnClickListener(this);
        selectLanguage = (TextView) findViewById(R.id.care_upload_language);
        selectLanguage.setOnClickListener(this);
        selectAge = (TextView) findViewById(R.id.care_upload_age);
        selectAge.setOnClickListener(this);
        selectFriends = (TextView) findViewById(R.id.care_upload_friends);
        selectFriends.setOnClickListener(this);
        selectFriendsTitle = (TextView) findViewById(R.id.care_upload_friends_title);

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            selectFriends.setVisibility(View.GONE);
            selectFriendsTitle.setVisibility(View.GONE);
        }
        else {
            selectFriends.setVisibility(View.GONE);
            selectFriendsTitle.setVisibility(View.GONE);
        }

        titleBox = (EditText) findViewById(R.id.care_upload_title);
        descriptionBox = (EditText) findViewById(R.id.care_upload_description);
        tagBox = (EditText) findViewById(R.id.care_upload_tag);
        websiteBoxLabel = (TextView) findViewById(R.id.care_upload_website_label);
        textViewValidateWebsiteLabel = (TextView) findViewById(R.id.textview_validate_website_label);
        websiteBox = (EditText) findViewById(R.id.care_upload_website);

        data = getIntent();
        if (data != null && data.hasExtra(Actions_.SELF_CARE)) {
            careUploaded_ = (CareUploaded_) data.getSerializableExtra(Actions_.SELF_CARE);
            action = data.getStringExtra(General.ACTION);
            setData(careUploaded_);
        } else {
            careUploaded_.setContent("");
            careUploaded_.setThumb_path("");
            careUploaded_.setWebsite("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        if (usersArrayList == null || usersArrayList.size() <= 0) {
            getMembers();
            if (data != null && data.hasExtra(Actions_.SELF_CARE)) {
                GetSelected.setSelectedUserList(usersArrayList, shared_with);
            }
        }
        if (categoryArrayList == null || categoryArrayList.size() <= 0) {
            getCategory();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                hideKeyboard();
                String title = titleBox.getText().toString().trim();
                String description = descriptionBox.getText().toString().trim();
                String tag = tagBox.getText().toString().trim();
                String action = Actions_.UPLOAD;
                if (isEdit) {
                    action = Actions_.EDIT;
                }
                if (validate(title, tag, description)) {
                    addContent(title, tag, description, action);
                }
                break;
            case R.id.care_upload_add_category:
                hideKeyboard();
                createCategoryDialog();
                break;
            case R.id.care_upload_publish_date:
                hideKeyboard();
                Calendar c = Calendar.getInstance();

                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                publish_date = "0000-00-00";
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                publish_date = year + "-"
                                        + GetCounters.checkDigit(monthOfYear) + "-"
                                        + GetCounters.checkDigit(dayOfMonth);
                                if (year > (mYear + 3)) {
                                    publish_date = "0000-00-00";
                                    publishDateText.setText("");
                                    publishDateText.setHint(getApplicationContext()
                                            .getResources().getString(R.string.publish_date));
                                    ShowSnack.textViewWarning(publishDateText,
                                            getApplicationContext().getResources()
                                                    .getString(R.string.invalid_date), getApplicationContext());
                                    return;
                                }
                                try {
                                    int result = Compare.startDate(mYear + "-" + (mMonth + 1) + "-" + mDay, publish_date);
                                    if (result == 1) {
                                        publish_date = "0000-00-00";
                                        publishDateText.setText("");
                                        publishDateText.setHint(getApplicationContext()
                                                .getResources().getString(R.string.publish_date));
                                        ShowSnack.textViewWarning(publishDateText,
                                                getApplicationContext().getResources()
                                                        .getString(R.string.invalid_date), getApplicationContext());
                                    } else {
                                        publishDateText.setText(publish_date);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.care_upload_content_type:
                hideKeyboard();
                showsContentType();
                break;
            case R.id.care_upload_file_button:
                hideKeyboard();

                ActivityCompat.requestPermissions(CareUploadActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
//
//                if (file_id <= 0) {
//                    Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                    fileIntent.setType("*/*");
//                    startActivityForResult(fileIntent, Chat.INTENT_FILE);
//                } else {
//                    removeFiles(1);
//                }
                break;
            case R.id.care_upload_thumbnail_button:
                hideKeyboard();

                ActivityCompat.requestPermissions(CareUploadActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);

//                if (thumbnail_id <= 0) {
//                    if (Build.VERSION.SDK_INT < 19) {
//                        Intent intent = new Intent();
//                        intent.setType("image/jpeg");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        startActivityForResult(intent, Chat.INTENT_IMAGE);
//                    } else {
//                        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        imageIntent.setType("image/*");
//                        startActivityForResult(imageIntent, Chat.INTENT_IMAGE);
//                    }
//                } else {
//                    removeFiles(2);
//                }
                break;
            case R.id.care_upload_category:
                hideKeyboard();
                if (categoryArrayList != null && categoryArrayList.size() > 0) {
                    openCategorySelector(Actions_.GET_CATEGORY, categoryArrayList);
                } else {
                    ShowSnack.textViewWarning(publishDateText, "Category list unavailable", getApplicationContext());
                }
                break;
            case R.id.care_upload_language:
                hideKeyboard();
                if (languageArrayList != null && languageArrayList.size() > 0) {
                    openCategorySelector(Actions_.GET_LANGUAGES, languageArrayList);
                } else {
                    ShowSnack.textViewWarning(publishDateText, "Language list unavailable", getApplicationContext());
                }
                break;
            case R.id.care_upload_age:
                hideKeyboard();
                if (ageArrayList != null && ageArrayList.size() > 0) {
                    //openCategorySelector(Actions_.GET_AGE, ageArrayList);
                    openAgeSelector();
                } else {
                    ShowSnack.textViewWarning(publishDateText, "Age list unavailable", getApplicationContext());
                }
                break;
            case R.id.care_upload_friends:
                hideKeyboard();
                if (usersArrayList != null && usersArrayList.size() > 0) {
                    openUsersSelector();
                } else {
                    ShowSnack.textViewWarning(publishDateText, "Friends list unavailable", getApplicationContext());
                }
                break;
        }
    }

    //Intent { dat=content://com.android.providers.downloads.documents/document/352 flg=0x1 }
    @Override
//Intent { dat=content://com.android.externalstorage.documents/document/primary:Mobile stolen issues.docx flg=0x1 }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            Uri uri = data.getData();
            String path = UriUtils.getPathFromUri(getApplicationContext(),uri);

            if (requestCode == Chat.INTENT_FILE) {
                boolean isValid;
                switch (contentType) {
                    case 1:
                        isValid = CheckFileType.imageFile(path);
                        break;
                    case 2:
                        isValid = CheckFileType.videoFile(path);
                        break;
                    case 3:
                        isValid = CheckFileType.isDocument(path);
                        break;
                    case 5:
                        isValid = CheckFileType.audioFile(path);
                        break;
                    case 6:
                        isValid = CheckFileType.videoFile(path);
                        break;
                    default:
                        isValid = CheckFileType.imageFile(path);
                        break;
                }
                if (isValid) {
                    new Upload(1, path).execute();
                } else {
                    ShowSnack.buttonWarning(fileButton, this.getResources()
                            .getString(R.string.valid_file_error), getApplicationContext());
                }
            }
            if (requestCode == Chat.INTENT_IMAGE) {
                if (CheckFileType.imageFile(path)) {
                    new Upload(2, path).execute();
                } else {
                    ShowSnack.buttonWarning(thumbnailButton, this.getResources()
                            .getString(R.string.valid_image_error), getApplicationContext());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    // disable fields which are not their for edit action
    private void setFieldStatus() {
        titleBox.setEnabled(false);
        descriptionBox.setEnabled(false);
        tagBox.setEnabled(false);
        selectContentType.setEnabled(false);
        selectCategory.setEnabled(false);
        publishDateText.setEnabled(false);
        selectLanguage.setEnabled(false);
        selectAge.setEnabled(false);
        fileButton.setEnabled(false);
        thumbnailButton.setEnabled(false);
    }

    // set respective values to fields from received data
    private void setData(CareUploaded_ careUploaded_) {
        isEdit = true;
        titleBox.setText(careUploaded_.getTitle());
        descriptionBox.setText(makeItEmpty(careUploaded_.getDescription()));
        tagBox.setText(careUploaded_.getTags());
        selectContentType.setText(careUploaded_.getType());
        selectCategory.setText(careUploaded_.getCategory_name());
        contentType = SelfCareContentType_.nameToType(careUploaded_.getType());
        publish_date = GetTime.getMmDdYyyy(careUploaded_.getAdded_date());
        publishDateText.setText(publish_date);
        selectFriends.setText(careUploaded_.getShared_to());
        selectLanguage.setText(careUploaded_.getLanguage());
        selectAge.setText(careUploaded_.getAge());

        care_id = careUploaded_.getId();
        category_id = careUploaded_.getCategory_id();
        if (careUploaded_.getShared_to_ids() != null) {
            shared_with = careUploaded_.getShared_to_ids();
        }
        if (careUploaded_.getLanguage_id() == 0) {
            language_id = 1;
        } else {
            language_id = careUploaded_.getLanguage_id();
        }
        if (careUploaded_.getAge_id() == null) {
            age_ids = "";
        } else {
            age_ids = careUploaded_.getAge_id();
        }
        file_id = 1;

        toggleUploadLayout(); //3,4,7 getTumb and 1,5 getContent
        if (contentType == 1 || contentType == 2 || contentType == 6) { //File upload
            if (careUploaded_.getContent().length() > 0) {
                toggleFileUploadLayout(true, FileOperations.getFileName(careUploaded_.getContent()));
            }
        }
        if (contentType == 4) { //website and image
            if (careUploaded_.getContent().length() > 0) {
                toggleFileUploadLayout(false, FileOperations.getFileName(careUploaded_.getContent()));
            }
            if (careUploaded_.getThumb_path().length() > 0) {
                toggleFileUploadLayout(true, FileOperations.getFileName(careUploaded_.getThumb_path()));
            }
        }
        if (contentType == 3 || contentType == 5) { //DocORAudio and image
            if (careUploaded_.getContent().length() > 0) {
                toggleFileUploadLayout(true, FileOperations.getFileName(careUploaded_.getContent()));
            }
        }
        if (contentType == 7 || contentType == 8) { //website
            if (careUploaded_.getContent().length() > 0) {
                toggleFileUploadLayout(false, FileOperations.getFileName(careUploaded_.getContent()));
            }
        }
        selectContentType.setEnabled(false);
        addCategory.setVisibility(View.GONE);
        //selectCategory.setEnabled(false); //changed for edit- issue reported on 10/08/18 by Nirmal

        if (action.equalsIgnoreCase(Actions_.SHARED)) {
            setFieldStatus();
        }
    }

    // remove "N/A" from description if present to make it empty
    private String makeItEmpty(String input) {
        if (input.equalsIgnoreCase("N/A") || input.equalsIgnoreCase("n / a")) {
            return "";
        }
        return input;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //open user selector dialog box
    @SuppressLint("CommitTransaction")
    private void openUsersSelector() {
        Bundle bundle = new Bundle();
        android.app.DialogFragment dialogFrag = new MultiUserSelectorDialog();
        bundle.putSerializable(Actions_.MY_FRIENDS, usersArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.MY_FRIENDS);
    }

    //open user selector dialog box
    @SuppressLint("CommitTransaction")
    private void openAgeSelector() {
        Bundle bundle = new Bundle();
        android.app.DialogFragment dialogFrag = new MultipleChoiceSelectorDialog();
        bundle.putSerializable(Actions_.GET_AGE, ageArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.GET_AGE);
    }

    // make network call to add new content to self care
    private void addContent(String title, String tag, String description, String action) {
        int result = 12;
        String website;
        if (contentType == 4 || contentType == 7 || contentType == 8) {
            website = websiteBox.getText().toString().trim();
        } else {
            website = "";
        }
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.ID, "" + care_id);
        requestMap.put(General.TITLE, title);
        requestMap.put(General.TYPE, "" + contentType);
        requestMap.put(General.CATEGORY, "" + category_id);
        requestMap.put("tags", tag);
        requestMap.put("website", website);
        requestMap.put("friend_id", shared_with);
        requestMap.put("publish_date", publish_date);
        requestMap.put(General.DESCRIPTION, description);
        requestMap.put("log_id", "" + file_id);
        requestMap.put("thumb_id", "" + thumbnail_id);
        requestMap.put(General.LANGUAGE, "" + language_id);
        requestMap.put(General.AGE, "" + age_ids);
        requestMap.put(General.DOMAIN_CODE, "" + Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, action);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            result = object.get(General.STATUS).getAsInt();
                        } else {
                            result = 11;
                        }
                    } else {
                        result = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(result, publishDateText);
        if (result == 1) {
            if (isEdit) {
                Preferences.save(General.IS_EDIT, true);
            }
            onBackPressed();
        }
    }

    // make network call to fetch users to share content with
    private void getMembers() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SELF_CARE);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_USERS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    usersArrayList = Users_.parse(response, Actions_.SELF_CARE, getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // make network call to remove uploaded file only
    private void removeFiles(int remove_type) {
        //1:file 2:thumb 3:both
        if (file_id > 0 && (remove_type == 1 || remove_type == 3)) {
            new RemoveFile().execute("" + file_id);
            file_id = 0;
            toggleFileUploadLayout(false, null);
        }
        if (thumbnail_id > 0 && (remove_type == 2 || remove_type == 3)) {
            new RemoveFile().execute("" + thumbnail_id);
            thumbnail_id = 0;
            toggleThumbnailUploadLayout(false, null);
        }
    }

    @Override
    public void selectedUsers(ArrayList<Friends_> users_arrayList, String selfCareContentId, boolean isSelected) {
        usersArrayList = users_arrayList;
        if (isSelected) {
            shared_with = GetSelected.wallUsers(users_arrayList);
            selectFriends.setText(GetSelected.wallUsersName(users_arrayList));
        }
    }

    // background call to execute remove file action
    @SuppressLint("StaticFieldLeak")
    private class RemoveFile extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            HashMap<String, String> keyMap = KeyMaker_.getKey();
            RequestBody getBody = new FormBody.Builder()
                    .add(General.KEY, keyMap.get(General.KEY))
                    .add(General.TOKEN, keyMap.get(General.TOKEN))
                    .add(General.ACTION, Actions_.DELETE)
                    .add(General.ID, params[0])
                    .add(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE))
                    .build();
            try {
                MakeCall.postNormal(Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "")
                        + Urls_.MOBILE_UPLOADER, getBody, TAG, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    // background call to execute upload file action
    @SuppressLint("StaticFieldLeak")
    private class Upload extends AsyncTask<Void, Void, String> {
        String file_name = "";
        final String path;
        final int type;
        ShowLoader showLoader;

        Upload(int type, String path) {
            this.path = path;
            this.type = type;
        }

        @SuppressLint("SourceLockedOrientationActivity")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader = new ShowLoader();
            showLoader.showUploadDialog(CareUploadActivity.this);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        @Override
        protected String doInBackground(Void... params) {
            file_name = FileOperations.getFileName(path);
            String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER;
            String user_id = Preferences.get(General.USER_ID);
            try {
                return FileUpload.uploadFile(path, file_name, "" + user_id, url, Actions_.FMS, getApplicationContext(), CareUploadActivity.this);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (showLoader != null) {
                showLoader.dismissUploadDialog();
            }
            parseUploadResponse(response, file_name, type);
        }
    }

    // parse json of upload file response
    private void parseUploadResponse(String response, String file_name, int type) {
        int status = 12;
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
                            if (type == 1) {
                                file_id = object.get(General.ID).getAsLong();
                                toggleFileUploadLayout(true, file_name);
                            } else {
                                thumbnail_id = object.get(General.ID).getAsLong();
                                toggleThumbnailUploadLayout(true, file_name);
                            }
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
        showResponses(status, fileButton);
    }

    // open dialog to select category
    @SuppressLint("CommitTransaction")
    private void openCategorySelector(String action, ArrayList<Choices_> list) {
        Bundle bundle = new Bundle();
        SingleChoiceSelectorDialog dialogFrag = new SingleChoiceSelectorDialog();
        bundle.putSerializable(General.CATEGORY, list);
        bundle.putString(General.ACTION, action);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), General.CATEGORY);
    }

    // change name of file upload based on file status (uploaded/not uploaded)
    private void toggleFileUploadLayout(boolean isUploaded, String name) {
        if (isUploaded) {
            fileNameText.setVisibility(View.VISIBLE);
            fileCheck.setVisibility(View.VISIBLE);
            fileButton.setText(this.getResources().getString(R.string.remove));
            fileNameText.setText(name);

            if (contentType == 3 || contentType == 5) {
                if (careUploaded_.getContent().length() > 0) {
                    fileNameText.setVisibility(View.VISIBLE);
                    fileCheck.setVisibility(View.VISIBLE);
                    fileButton.setText(this.getResources().getString(R.string.remove));
                    fileNameText.setText(FileOperations.getFileName(careUploaded_.getContent()));
                }
                if (careUploaded_.getThumb_path().length() > 0) {
                    thumbnailLayout.setVisibility(View.VISIBLE);
                    thumbnailNameText.setVisibility(View.VISIBLE);
                    thumbnailCheck.setVisibility(View.VISIBLE);
                    thumbnailButton.setText(this.getResources().getString(R.string.remove));
                    thumbnailNameText.setText(FileOperations.getFileName(careUploaded_.getThumb_path()));
                }
            }
        } else {
            fileNameText.setVisibility(View.GONE);
            fileCheck.setVisibility(View.GONE);
            fileButton.setText(this.getResources().getString(R.string.choose_file));
        }
    }

    // change name of thumbnail file upload based on file status (uploaded/not uploaded)
    private void toggleThumbnailUploadLayout(boolean isUploaded, String name) {
        if (isUploaded) {
            thumbnailNameText.setVisibility(View.VISIBLE);
            thumbnailCheck.setVisibility(View.VISIBLE);
            thumbnailButton.setText(this.getResources().getString(R.string.remove));
            thumbnailNameText.setText(name);
        } else {
            thumbnailNameText.setVisibility(View.GONE);
            thumbnailCheck.setVisibility(View.GONE);
            thumbnailButton.setText(this.getResources().getString(R.string.choose_file));
        }
    }

    //Girish
    // toggle file and thumbnail upload buttons based on content type
    // blog, courses, image and video will have only file upload
    // where as  text articles and podcasts have both file and thumbnail upload
    // Intervention Tools don't have both
    //Monika
    // toggle file and thumbnail upload buttons based on content type
    // blog, courses, image and video will have only file upload
    // where as  text articles and podcasts have both file and thumbnail upload
    // Blog, Intervention Tools, Webinar will have websites
    private void toggleUploadLayout() {
        if (contentType == 1 || contentType == 2 || contentType == 4 || contentType == 6) {
            thumbnailLayout.setVisibility(View.GONE);
            fileLayout.setVisibility(View.VISIBLE);
            if (contentType == 1) {
                textViewUploadFile.setText(getResources().getString(R.string.upload_image));
            } else if (contentType == 2 || contentType == 6) {
                textViewUploadFile.setText(getResources().getString(R.string.upload_video));
            } else if (contentType == 4) {
                textViewUploadFile.setText(getResources().getString(R.string.upload_image));
            }
        } else if (contentType == 3 || contentType == 5) {
            thumbnailLayout.setVisibility(View.VISIBLE);
            fileLayout.setVisibility(View.VISIBLE);
            if (contentType == 3) {
                textViewUploadFile.setText(getResources().getString(R.string.upload_document));
            } else if (contentType == 5) {
                textViewUploadFile.setText(getResources().getString(R.string.upload_audio));
            }
        } else {
            thumbnailLayout.setVisibility(View.GONE);
            fileLayout.setVisibility(View.GONE);
        }

        if (contentType == 4 || contentType == 7 || contentType == 8) {
            websiteBoxLabel.setVisibility(View.VISIBLE);
            textViewValidateWebsiteLabel.setVisibility(View.VISIBLE);
            websiteBox.setVisibility(View.VISIBLE);
            websiteBox.setText(careUploaded_.getContent());
        } else {
            websiteBoxLabel.setVisibility(View.GONE);
            textViewValidateWebsiteLabel.setVisibility(View.GONE);
            websiteBox.setVisibility(View.GONE);
        }
    }

    // check fields validity based on respective content type
    private boolean validate(String title, String tag, String description) {
        if (file_id <= 0 && (contentType == 1 || contentType == 2 || contentType == 3 || contentType == 5 || contentType == 6)) {
            ShowSnack.textViewWarning(publishDateText, "Please select file to upload", getApplicationContext());
            return false;
        }

        if (contentType == 4 || contentType == 7 || contentType == 8) {
            String website = websiteBox.getText().toString().trim();
            if (!Care.isUrl(website)) {
                websiteBox.setError("Enter valid url");
                return false;
            }
        }

        if (contentType == 3 || contentType == 5) {
            if (thumbnail_id == 0) {
                ShowSnack.textViewWarning(publishDateText, "Please select thumbnail to upload", getApplicationContext());
                return false;
            }
        }
        if (title == null || title.length() <= 0) {
            titleBox.setError("Title is mandatory");
            return false;
        }
        if (title.length() > 0 && title.length() < 3) {
            titleBox.setError("Min 3 char required");
            return false;
        }
        if (title.length() > 150) {
            titleBox.setError("Max 150 char required");
            return false;
        }
        if (description.length() > 0 && description.length() < 3) {
            descriptionBox.setError("Min 3 char required");
            return false;
        }
        if (description.length() > 2000) {
            descriptionBox.setError("Max 2000 char required");
            return false;
        }
        if (tag == null || tag.length() <= 0) {
            tagBox.setError("Tag is mandatory");
            return false;
        }
        if (tag.length() > 0 && tag.length() < 3) {
            tagBox.setError("Min 3 char required");
            return false;
        }
        if (tag.length() > 150) {
            tagBox.setError("Max 150 char required");
            return false;
        }
        if (publish_date.equalsIgnoreCase("0000-00-00") || publish_date.trim().length() <= 0) {
            publish_date = "0000-00-00";
            publishDateText.setText("");
            publishDateText.setHint(getApplicationContext()
                    .getResources().getString(R.string.publish_date));
            ShowSnack.textViewWarning(publishDateText, getApplicationContext().getResources().getString(R.string.invalid_date), getApplicationContext());
            return false;
        }
        if (category_id <= 0) {
            ShowSnack.textViewWarning(publishDateText, "Please select category", getApplicationContext());
            return false;
        }
        return true;
    }

    // validate category name for min and max length
    private boolean validate(String category_name, EditText inputBox) {
        if (category_name == null || category_name.length() < 3) {
            inputBox.setError("Min 3 char required");
            return false;
        }
        if (category_name.length() > 50) {
            inputBox.setError("Max 50 char allowed");
            return false;
        }
        return true;
    }

    // make network call to add new category
    private void addCategory(String category_name) {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_CATEGORY);
        requestMap.put(General.NAME, category_name);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.ADD_CATEGORY);
                    if (jsonArray != null) {
                        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                        if (jsonObject.has(General.STATUS)) {
                            if (jsonObject.get(General.STATUS).getAsInt() == 1) {
                                Choices_ choices_ = new Choices_();
                                choices_.setId(jsonObject.get(General.ID).getAsLong());
                                choices_.setName(jsonObject.get(General.NAME).getAsString());
                                choices_.setStatus(1);
                                categoryArrayList.add(choices_);
                                status = 1;
                            } else {
                                status = jsonObject.get(General.STATUS).getAsInt();
                            }
                        }
                    } else {
                        status = 12;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status, addCategory);
    }

    private void showResponses(int status, View view) {
        String message;
        if (status == 1) {
            if (data != null && data.hasExtra(Actions_.SELF_CARE)) {
                Preferences.save(General.FROM_UPLODER_EDIT, "true");
                Preferences.save(General.UPLOADER_CATEGORY_NAME, selectCategory.getText().toString().trim());
                Preferences.save(General.UPLOADER_CATEGORY_ID, category_id);
            }
            message = this.getResources().getString(R.string.successful);
        } else {
            status = 2;
            message = this.getResources().getString(R.string.category_present);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
    }

    // show content type pop up for filter options
    private void showsContentType() {
        final PopupMenu popup = new PopupMenu(this, selectContentType);
        popup.getMenuInflater().inflate(R.menu.care_menu_content_type, popup.getMenu());
        MenuItem itemWebinar = popup.getMenu().findItem(R.id.care_content_type_webinar);
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage013))) {
            itemWebinar.setVisible(true);
        } else {
            itemWebinar.setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                selectContentType.setText(item.getTitle());
                if (contentType != SelfCareContentType_.nameToType(item.getTitle().toString())) {
                    contentType = SelfCareContentType_.nameToType(item.getTitle().toString());
                    removeFiles(3);
                }
                toggleUploadLayout();

                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    // make network call to fetch category list
    private void getCategory() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_CATEGORY);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    categoryArrayList = SelfCare_.parseCategory(response, Actions_.GET_CATEGORY, getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getLanguage();
    }

    // make network call to fetch language list
    private void getLanguage() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LANGUAGES);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    languageArrayList = SelfCare_.parseCategory(response, Actions_.GET_LANGUAGES,
                            getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getAge();
    }

    // make network call to fetch age
    private void getAge() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_AGE);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    ageArrayList = SelfCare_.parseCategory(response, Actions_.GET_AGE, getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // open dialog box to create new category
    private void createCategoryDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        subTitle.setVisibility(View.GONE);
        final EditText inputBox = (EditText) dialog.findViewById(R.id.delete_confirmation_input_box);
        inputBox.setVisibility(View.VISIBLE);
        title.setText(this.getResources().getString(R.string.create_category));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category_name = inputBox.getText().toString().trim();
                if (validate(category_name, inputBox)) {
                    addCategory(category_name);
                    dialog.dismiss();
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

    @Override
    public void GetSelectedCategory(Choices_ choices_, String action, boolean isSelected) {
        if (isSelected) {
            if (action.equalsIgnoreCase(Actions_.GET_CATEGORY)) {
                selectCategory.setText(choices_.getName());
                category_id = choices_.getId();
            }
            if (action.equalsIgnoreCase(Actions_.GET_LANGUAGES)) {
                selectLanguage.setText(choices_.getName());
                language_id = choices_.getId();
            }
            if (action.equalsIgnoreCase(Actions_.GET_AGE)) {
                selectAge.setText(choices_.getName());
                //long age_id = choices_.getId();
            }
        }
    }

    @Override
    public void selectedAge(ArrayList<Choices_> age_arrayList, String selfCareContentId, boolean isSelected) {
        ageArrayList = age_arrayList;
        if (isSelected) {
            age_ids = GetSelected.selectedAge(age_arrayList);
            selectAge.setText(GetSelected.selectedAgeName(age_arrayList));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    if (file_id <= 0) {
                        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        fileIntent.setType("*/*");
                        startActivityForResult(fileIntent, Chat.INTENT_FILE);
                    } else {
                        removeFiles(1);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(CareUploadActivity.this, "Permission denied to read and write your External storage", Toast.LENGTH_SHORT).show();
                    Snackbar.make(coordinatorLayout, "Permission denied to read and write your External storage",
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

            case 2: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (thumbnail_id <= 0) {
                        if (Build.VERSION.SDK_INT < 19) {
                            Intent intent = new Intent();
                            intent.setType("image/jpeg");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, Chat.INTENT_IMAGE);
                        } else {
                            Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            imageIntent.setType("image/*");
                            startActivityForResult(imageIntent, Chat.INTENT_IMAGE);
                        }
                    } else {
                        removeFiles(2);
                    }
                } else {
//                    Toast.makeText(CareUploadActivity.this, "Permission denied to read and write your External storage", Toast.LENGTH_SHORT).show();
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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
