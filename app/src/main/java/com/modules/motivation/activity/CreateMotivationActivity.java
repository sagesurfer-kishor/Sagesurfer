package com.modules.motivation.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.modules.motivation.fragment.MotivationContentType_;
import com.modules.motivation.model.MotivationLibrary_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.FileUpload;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.PathUtils;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.secure.KeyMaker_;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CreateMotivationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CreateMotivationActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextView selectContentType, fileNameText, textViewUploadFile, websiteBoxLabel, textViewValidateWebsiteLabel, textViewLocation;
    private EditText websiteBox, titleBox, descriptionBox;
    private Button fileButton;
    private AppCompatImageView fileCheck;
    private LinearLayout linearLayoutUploadFile;
    private CheckedTextView checkedTextViewLaugh, checkedTextViewHappy, checkedTextViewCry, checkedTextViewWorried, checkedTextViewSad, checkedTextViewAngry, checkedTextViewNeutral, checkedTextViewExcited;

    private int contentType = 1;
    private long file_id = 0;
    private boolean isEdit = false;
    private long care_id = 0;
    Intent data;
    MotivationLibrary_ motivationLibrary_ = new MotivationLibrary_();
    private String selectedCommaSeperatedMood = "";
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

        setContentView(R.layout.activity_create_motivation);
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

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getResources().getString(R.string.create_motivation));

        mErrorString = new SparseIntArray();

        AppCompatImageView postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        selectContentType = (TextView) findViewById(R.id.textview_content_type);
        selectContentType.setOnClickListener(this);
        selectContentType.setText(this.getResources().getString(R.string.images));
        linearLayoutUploadFile = (LinearLayout) findViewById(R.id.linearlayout_upload_file);
        textViewUploadFile = (TextView) findViewById(R.id.textview_upload_file);
        fileButton = (Button) findViewById(R.id.button_upload_file);
        fileButton.setOnClickListener(this);
        fileCheck = (AppCompatImageView) findViewById(R.id.imageview_upload_file_check);
        fileNameText = (TextView) findViewById(R.id.textview_upload_file_name);
        websiteBoxLabel = (TextView) findViewById(R.id.textview_upload_website_label);
        textViewValidateWebsiteLabel = (TextView) findViewById(R.id.textview_validate_website_label);
        websiteBox = (EditText) findViewById(R.id.edittext_upload_website);
        titleBox = (EditText) findViewById(R.id.edittext_upload_title);
        descriptionBox = (EditText) findViewById(R.id.edittext_upload_description);
        textViewLocation = (TextView) findViewById(R.id.textview_location);
        checkedTextViewLaugh = (CheckedTextView) findViewById(R.id.checkedtextview_laugh);
        checkedTextViewHappy = (CheckedTextView) findViewById(R.id.checkedtextview_happy);
        checkedTextViewCry = (CheckedTextView) findViewById(R.id.checkedtextview_cry);
        checkedTextViewWorried = (CheckedTextView) findViewById(R.id.checkedtextview_worried);
        checkedTextViewSad = (CheckedTextView) findViewById(R.id.checkedtextview_sad);
        checkedTextViewAngry = (CheckedTextView) findViewById(R.id.checkedtextview_angry);
        checkedTextViewNeutral = (CheckedTextView) findViewById(R.id.checkedtextview_neutral);
        checkedTextViewExcited = (CheckedTextView) findViewById(R.id.checkedtextview_excited);

        textViewLocation.setOnClickListener(this);
        checkedTextViewLaugh.setOnClickListener(this);
        checkedTextViewHappy.setOnClickListener(this);
        checkedTextViewCry.setOnClickListener(this);
        checkedTextViewWorried.setOnClickListener(this);
        checkedTextViewSad.setOnClickListener(this);
        checkedTextViewAngry.setOnClickListener(this);
        checkedTextViewNeutral.setOnClickListener(this);
        checkedTextViewExcited.setOnClickListener(this);

        data = getIntent();
        if (data != null && data.hasExtra(Actions_.UPDATE_UPLIFT_DATA)) {
            motivationLibrary_ = (MotivationLibrary_) data.getSerializableExtra(Actions_.UPDATE_UPLIFT_DATA);
            setData(motivationLibrary_);
        } else {
            motivationLibrary_.setContent_path("");
            isMoodCheck(1, true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    private void isMoodCheck(int _id, boolean isReset) {
        int[] dayCheckId = {
                R.id.checkedtextview_laugh,
                R.id.checkedtextview_happy,
                R.id.checkedtextview_cry,
                R.id.checkedtextview_worried,
                R.id.checkedtextview_sad,
                R.id.checkedtextview_angry,
                R.id.checkedtextview_neutral,
                R.id.checkedtextview_excited
        };
        CheckedTextView[] specificDayCheck = {
                checkedTextViewLaugh, checkedTextViewHappy, checkedTextViewCry, checkedTextViewWorried, checkedTextViewSad, checkedTextViewAngry, checkedTextViewNeutral, checkedTextViewExcited};
        if (isReset) {
            for (CheckedTextView aDayCheck : specificDayCheck) {
                aDayCheck.setChecked(false);
            }
            return;
        }
        int i = 0;
        for (int id : dayCheckId) {
            if (id == _id) {
                if (specificDayCheck[i].isChecked()) {
                    specificDayCheck[i].setChecked(false);
                } else {
                    specificDayCheck[i].setChecked(true);
                }
                //return;
            }
            i++;
        }

        ArrayList<String> selectedMood = new ArrayList<>();
        for (int j = 0; j < specificDayCheck.length; j++) {
            if (specificDayCheck[j].isChecked()) {
                if (specificDayCheck[j].getText().toString().equalsIgnoreCase(getResources().getString(R.string.happy))) {
                    selectedMood.add("1");
                }
                if (specificDayCheck[j].getText().toString().equalsIgnoreCase(getResources().getString(R.string.laugh))) {
                    selectedMood.add("2");
                }
                if (specificDayCheck[j].getText().toString().equalsIgnoreCase(getResources().getString(R.string.cry))) {
                    selectedMood.add("5");
                }
                if (specificDayCheck[j].getText().toString().equalsIgnoreCase(getResources().getString(R.string.worried))) {
                    selectedMood.add("4");
                }
                if (specificDayCheck[j].getText().toString().equalsIgnoreCase(getResources().getString(R.string.sad))) {
                    selectedMood.add("6");
                }
                if (specificDayCheck[j].getText().toString().equalsIgnoreCase(getResources().getString(R.string.angry))) {
                    selectedMood.add("7");
                }
                if (specificDayCheck[j].getText().toString().equalsIgnoreCase(getResources().getString(R.string.neutral))) {
                    selectedMood.add("3");
                }
                if (specificDayCheck[j].getText().toString().equalsIgnoreCase(getResources().getString(R.string.excited))) {
                    selectedMood.add("8");
                }
            }
        }
        selectedCommaSeperatedMood = TextUtils.join(",", selectedMood);
    }

    // set respective values to fields from received data
    private void setData(MotivationLibrary_ motivationLibrary_) {
        isEdit = true;
        titleBox.setText(motivationLibrary_.getTitle());
        descriptionBox.setText(makeItEmpty(motivationLibrary_.getDescription()));
        selectContentType.setText(getContentType(Integer.parseInt(motivationLibrary_.getContent_type())));
        contentType = MotivationContentType_.nameToType(motivationLibrary_.getContent_type());
        textViewLocation.setText(motivationLibrary_.getMood_location());

        if (motivationLibrary_.getMood() != null) {
            for (int i = 0; i < motivationLibrary_.getMood().size(); i++) {
                setMoodChecked((int) motivationLibrary_.getMood().get(i).getMood_id());
            }
        }
        isMoodCheck(1, false);

        care_id = motivationLibrary_.getId();
        file_id = 1;

        toggleUploadLayout();
        toggleFileUploadLayout(true, FileOperations.getFileName(motivationLibrary_.getContent_path()));

        selectContentType.setEnabled(false);
    }

    public void setMoodChecked(int mood) {
        switch (mood) {
            case 1:
                checkedTextViewHappy.setChecked(true);
                break;
            case 2:
                checkedTextViewLaugh.setChecked(true);
                break;
            case 3:
                checkedTextViewNeutral.setChecked(true);
                break;
            case 4:
                checkedTextViewWorried.setChecked(true);
                break;
            case 5:
                checkedTextViewCry.setChecked(true);
                break;
            case 6:
                checkedTextViewSad.setChecked(true);
                break;
            case 7:
                checkedTextViewAngry.setChecked(true);
                break;
            case 8:
                checkedTextViewExcited.setChecked(true);
                break;
        }
    }

    private String getContentType(int content_type) {
        String contentTypeString = "";
        switch (content_type) {
            case 1:
                contentTypeString = "Images";
                break;
            case 2:
                contentTypeString = "Videos";
                break;
            case 3:
                contentTypeString = "Audio";
                break;
            default:
                contentTypeString = "Text Articles";
                break;
        }
        return contentTypeString;
    }

    // remove "N/A" from description if present to make it empty
    private String makeItEmpty(String input) {
        if (input.equalsIgnoreCase("N/A") || input.equalsIgnoreCase("n / a")) {
            return "";
        }
        return input;
    }

    @Override
    public void onClick(View v) {
        isMoodCheck(v.getId(), false);

        switch (v.getId()) {
            case R.id.imageview_toolbar_save:
                hideKeyboard();
                String title = titleBox.getText().toString().trim();
                String description = descriptionBox.getText().toString().trim();
                String action = Actions_.ADD_UPLIFT;
                if (isEdit) {
                    action = Actions_.UPDATE_UPLIFT_DATA;
                }
                if (validate(title, description)) {
                    addContent(title, description, action);
                }
                break;
            case R.id.textview_content_type:
                hideKeyboard();
                showsContentType();
                break;
            case R.id.button_upload_file:
                hideKeyboard();

                ActivityCompat.requestPermissions(CreateMotivationActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

//                if (file_id <= 0) {
//                    Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                    fileIntent.setType("*/*");
//                    startActivityForResult(fileIntent, Chat.INTENT_FILE);
//                } else {
//                    removeFiles(1);
//                }
                break;
            case R.id.textview_location:
                hideKeyboard();
                showLocationPopUp(textViewLocation);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (file_id <= 0) {
                        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        fileIntent.setType("*/*");
                        startActivityForResult(fileIntent, Chat.INTENT_FILE);
                    } else {
                        removeFiles(1);
                    }
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

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // check fields validity based on respective content type
    private boolean validate(String title, String description) {
        if (file_id <= 0 && (contentType == 1 || contentType == 2 || contentType == 3 || contentType == 4)) {
            ShowSnack.textViewWarning(textViewUploadFile, "Please select file to upload", getApplicationContext());
            return false;
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
        if (textViewLocation == null || textViewLocation.getText().toString().trim().length() <= 0 || textViewLocation.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.select_location))) {
            textViewLocation.setError("Location is mandatory");
            return false;
        }
        if (selectedCommaSeperatedMood.length() == 0 && selectedCommaSeperatedMood.equalsIgnoreCase("")) {
            ShowToast.toast(getResources().getString(R.string.select_mood), getApplicationContext());
            return false;
        }
        return true;
    }

    // make network call to add new content to self care
    private void addContent(String title, String description, String action) {
        int result = 12;
        if (isEdit) {
            file_id = 0;
        }
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.ID, "" + care_id);
        requestMap.put(General.TITLE, title);
        requestMap.put(General.DESCRIPTION, description);
        requestMap.put(General.CONTENT_TYPE, "" + contentType);
        requestMap.put(General.LOG_ID, "" + file_id);
        requestMap.put(General.LOCATION, textViewLocation.getText().toString());
        requestMap.put(General.MOOD, selectedCommaSeperatedMood);
        requestMap.put(General.DOMAIN_CODE, "" + Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_UPLIFT;
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
        showResponses(result, textViewUploadFile);
        if (result == 1) {
            if (isEdit) {
                Preferences.save(General.IS_EDIT, true);
            }
            onBackPressed();
        }
    }

    // show content type pop up for filter options
    private void showsContentType() {
        final PopupMenu popup = new PopupMenu(this, selectContentType);
        popup.getMenuInflater().inflate(R.menu.menu_motivation_content_type, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                selectContentType.setText(item.getTitle());
                if (contentType != MotivationContentType_.nameToType(item.getTitle().toString())) {
                    contentType = MotivationContentType_.nameToType(item.getTitle().toString());
                    removeFiles(3);
                }
                toggleUploadLayout();

                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    // make network call to remove uploaded file only
    private void removeFiles(int remove_type) {
        //1:file 2:thumb 3:both
        if (file_id > 0 && (remove_type == 1 || remove_type == 3)) {
            new RemoveFile().execute("" + file_id);
            file_id = 0;
            toggleFileUploadLayout(false, null);
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
                MakeCall.postNormal(Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER, getBody, TAG, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    // change name of file upload based on file status (uploaded/not uploaded)
    private void toggleFileUploadLayout(boolean isUploaded, String name) {
        if (isUploaded) {
            fileNameText.setVisibility(View.VISIBLE);
            fileCheck.setVisibility(View.VISIBLE);
            fileButton.setText(this.getResources().getString(R.string.remove));
            fileNameText.setText(name);
        } else {
            fileNameText.setVisibility(View.GONE);
            fileCheck.setVisibility(View.GONE);
            fileButton.setText(this.getResources().getString(R.string.choose_file));
        }
    }

    //Monika
    // toggle file and thumbnail upload buttons based on content type
    // blog, courses, image and video will have only file upload
    // where as  text articles and podcasts have both file and thumbnail upload
    // Blog, Intervention Tools, Webinar will have websites
    private void toggleUploadLayout() {
        if (contentType == 1) {
            textViewUploadFile.setText(getResources().getString(R.string.upload_image));
        } else if (contentType == 2) {
            textViewUploadFile.setText(getResources().getString(R.string.upload_video));
        } else if (contentType == 5) {
            textViewUploadFile.setText(getResources().getString(R.string.upload_audio));
        } else if (contentType == 4) {
            textViewUploadFile.setText(getResources().getString(R.string.upload_document));
        }
    }

    @Override
//Intent { dat=content://com.android.externalstorage.documents/document/primary:Mobile stolen issues.docx flg=0x1 }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //String path = PathUtils.getFilePath(getApplicationContext(), data.getData());
            String path = UriUtils.getPathFromUri(getApplicationContext(), data.getData());

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
                        isValid = CheckFileType.audioFile(path);
                        break;
                    case 4:
                        isValid = CheckFileType.isDocument(path);
                        break;
                    default:
                        isValid = CheckFileType.imageFile(path);
                        break;
                }
                if (isValid) {
                    new Upload(1, path).execute();
                } else {
                    ShowSnack.buttonWarning(fileButton, this.getResources().getString(R.string.valid_file_error), getApplicationContext());
                }
            }
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader = new ShowLoader();
            showLoader.showUploadDialog(CreateMotivationActivity.this);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        @Override
        protected String doInBackground(Void... params) {
            file_name = FileOperations.getFileName(path);
            String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER;
            String user_id = Preferences.get(General.USER_ID);
            try {
                return FileUpload.uploadFile(path, file_name, "" + user_id, url, Actions_.FMS, getApplicationContext(), CreateMotivationActivity.this);
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

    private void showResponses(int status, View view) {
        String message;
        if (status == 1) {
            if (data != null && data.hasExtra(Actions_.SELF_CARE)) {
                Preferences.save(General.FROM_UPLODER_EDIT, "true");
            }
            message = this.getResources().getString(R.string.successful);
        } else {
            status = 2;
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
    }

    //open task status menu
    private void showLocationPopUp(final View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_mood_activity_location, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String old_status = textViewLocation.getText().toString();
                if (!old_status.equalsIgnoreCase(item.getTitle().toString())) {
                    textViewLocation.setText(item.getTitle().toString());
                }
                return true;
            }
        });
        popup.show();
    }
}
