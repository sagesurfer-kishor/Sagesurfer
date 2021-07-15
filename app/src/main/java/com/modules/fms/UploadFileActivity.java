package com.modules.fms;

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

import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ArrayOperations;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.FileUpload;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.Users_;
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

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 12-09-2017
 * Last Modified on 13-12-2017
 */

public class UploadFileActivity extends AppCompatActivity implements View.OnClickListener,
        PermissionSelectorDialog.PermissionList, FolderListDialog.SelectedFolder,
        CreateFolderDialog.FinalFolderList {

    private static final String TAG = UploadFileActivity.class.getSimpleName();
    private ArrayList<AllFolder_> allFolderArrayList;
    private ArrayList<Friends_> friendsArrayList;
    private int group_id = 0;
    private long folder_id = -1, file_id = 0;
    private boolean isEdit = false;
    private String start_time = "0";

    private RadioButton customRadio, defaultRadio;
    private TextView selectFolder, postButton, fileNameText, fileSizeText, removeFileButton;
    private RelativeLayout footerLayout;
    private EditText et_commentBox, et_descriptionBox;
    private ImageView fileThumbnail, backImage;
    private AppCompatImageView iv_addFolder;

    Toolbar toolbar;
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

        setContentView(R.layout.file_upload_activity_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        friendsArrayList = new ArrayList<>();

        mErrorString = new SparseIntArray();

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        //toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.screen_background));
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
        //titleText.setTextColor(getResources().getColor(R.color.white));
        titleText.setText(this.getResources().getString(R.string.upload_file));

        postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        //postButton.setTextColor(getResources().getColor(R.color.white));
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        customRadio = (RadioButton) findViewById(R.id.file_upload_custom_radio);
        customRadio.setOnClickListener(this);
        defaultRadio = (RadioButton) findViewById(R.id.file_upload_default_radio);
        defaultRadio.setOnClickListener(this);

        selectFolder = (TextView) findViewById(R.id.file_upload_select_folder);
        selectFolder.setOnClickListener(this);
        selectFolder.setText("root/");

        iv_addFolder = (AppCompatImageView) findViewById(R.id.iv_add_folder);
        iv_addFolder.setOnClickListener(this);

        footerLayout = (RelativeLayout) findViewById(R.id.file_upload_footer);

        fileNameText = (TextView) findViewById(R.id.file_upload_name);
        fileSizeText = (TextView) findViewById(R.id.file_upload_size);
        fileThumbnail = (ImageView) findViewById(R.id.file_upload_thumbnail);
        backImage = (ImageView) findViewById(R.id.file_upload_thumbnail_back);

        removeFileButton = (TextView) findViewById(R.id.file_upload_remove);
        removeFileButton.setOnClickListener(this);

        et_commentBox = (EditText) findViewById(R.id.file_upload_comment);
        et_descriptionBox = (EditText) findViewById(R.id.file_upload_description);

        toggleAddFolder();

        Intent data = getIntent();
        if (data != null && data.hasExtra(General.GROUP_ID)) {
            group_id = data.getIntExtra(General.GROUP_ID, 0);
            if (data.hasExtra(General.ID)) {
                isEdit = true;
                FileSharing_ file_ = (FileSharing_) data.getSerializableExtra(General.ID);
                group_id = data.getIntExtra(General.GROUP_ID, 0);
                setEditData(file_, data.getStringExtra(General.DIRECTORY));
                titleText.setText(getApplicationContext().getResources().getString(R.string.edit));
            }
        } else {
            onBackPressed();
        }


        if (data != null && data.hasExtra(General.FOLDER_ID)) {
            folder_id = data.getIntExtra(General.FOLDER_ID, 0);
        }

        if (data != null && data.hasExtra(General.DIRECTORY)) {
            String foldername = data.getStringExtra(General.DIRECTORY);
            selectFolder.setText("root/" + foldername);
        }

    }

    // make a call to upload file
    private void uploadFile(String comment, String description) {
        String action = "upload_file";
        if (isEdit) {
            action = "update_file";
        }
        String is_default = "1";
        if (customRadio.isChecked()) {
            is_default = "0";
        }


//        int status = FileSharingOperations.upload("" + file_id,
//                "" + group_id,
//                user_access_array(),
//                Preferences.get(General.PERMISSION),
//                "" + folder_id,
//                description,
//                comment,
//                is_default,
//                action,
//                start_time,
//                this);

        int status = FileSharingOperations.upload("" + file_id,
                "" + group_id,
                user_access_array(),
                "1",
                "" + folder_id,
                description,
                comment,
                is_default,
                action,
                start_time,
                this);


        showResponses(status, postButton);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_activitytoolbar_post:
                hideKeyboard();

                ActivityCompat.requestPermissions(UploadFileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

//                if (file_id <= 0) {
//                    Intent musicIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                    musicIntent.setType("*/*");
//                    startActivityForResult(musicIntent, Chat.INTENT_FILE);
//                } else {
//                    String comment = commentBox.getText().toString().trim();
//                    String description = descriptionBox.getText().toString().trim();
//                    if (validate(comment, description)) {
//                        uploadFile(comment, description);
//                    }
//                }
                break;
            case R.id.file_upload_remove:
                if (file_id != 0) {
                    new RemoveFile().execute();
                }
                break;
            case R.id.file_upload_select_folder:
                openFoldersSelector();
                break;
            case R.id.iv_add_folder:
                createFoldersDialog();
                break;
            case R.id.file_upload_custom_radio:
                defaultRadio.setChecked(false);
                customRadio.setChecked(true);
                openUsersSelector();
                break;
            case R.id.file_upload_default_radio:
                defaultRadio.setChecked(true);
                customRadio.setChecked(false);
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
                        Intent musicIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        musicIntent.setType("*/*");
                        startActivityForResult(musicIntent, Chat.INTENT_FILE);
                    } else {
                        String comment = et_commentBox.getText().toString().trim();
                        String description = et_descriptionBox.getText().toString().trim();
                        if (validate(comment, description)) {
                            uploadFile(comment, description);
                        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        start_time = GetTime.getChatTimestamp();
        Preferences.initialize(getApplicationContext());
        if (group_id == 0) {
            onBackPressed();
            return;
        }
        if (file_id > 0) {
            getAccess();
        }
        if (allFolderArrayList == null) {
            allFolderArrayList = new ArrayList<>();
        }
        if (allFolderArrayList.size() <= 0) {
            getFolders();
        }
        togglePostButton();
    }

    // set data to all fields with available data
    private void setEditData(FileSharing_ file_, String directory) {
        String comment = file_.getComment();
        String description = file_.getDescription();
        file_id = file_.getId();
        togglePostButton();
        footerLayout.setVisibility(View.VISIBLE);
        removeFileButton.setEnabled(false);
        removeFileButton.setVisibility(View.GONE);
        fileSizeText.setText(FileOperations.bytes2String(file_.getSize()));

        fileNameText.setText(file_.getRealName());

        setThumbnail(file_.getRealName());

        et_commentBox.setText(comment);
        et_commentBox.setSelection(comment.length());
        et_descriptionBox.setText(description);
        et_descriptionBox.setSelection(description.length());
        selectFolder.setText(directory);
        //   folderText.setVisibility(View.VISIBLE);
        //    folderName.setVisibility(View.GONE);
        selectFolder.setEnabled(false);

        //iv_addFolder.setVisibility(View.GONE);

        if (file_.getIsDefault() == 1) {
            defaultRadio.setChecked(true);
            customRadio.setChecked(false);
        } else {
            defaultRadio.setChecked(false);
            customRadio.setChecked(true);
        }
    }

    private void showResponses(int status, View view) {
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else {
            status = 2;
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    // generate users list with permission value
    private String user_access_array() {
        ArrayList<String> final_list = new ArrayList<>();
        for (int i = 0; i < friendsArrayList.size(); i++) {
            if (friendsArrayList.get(i).getStatus() == 1)
                final_list.add(friendsArrayList.get(i).getUserId() + "_" + friendsArrayList.get(i).getUser_access());
        }
        return ArrayOperations.stringListToString(final_list);
    }

    // validate file comment and description for appropriate inputs
    private boolean validate(String comment, String description) {
        if (file_id <= 0 && !isEdit) {
            ShowSnack.textViewWarning(postButton, this.getApplicationContext()
                    .getResources().getString(R.string.please_select_file), getApplicationContext());
            return false;
        }
        if (description.length() < 3 && description.length() > 0) {
            et_descriptionBox.setError("Min 3 char required");
            return false;
        }
        if (description.length() > 500) {
            et_descriptionBox.setError("Max 500 char allowed");
            return false;
        }
        if (comment == null || comment.length() <= 0) {
            et_commentBox.setError("Comment is mandatory");
            return false;
        }
        if (comment.length() < 3) {
            et_commentBox.setError("Min 3 char required");
            return false;
        }
        if (comment.length() > 100) {
            et_commentBox.setError("Max 100 char allowed");
            return false;
        }
        return true;
    }

    // toggle post button based on file status (uploaded or not uploaded)
    private void togglePostButton() {
        if (file_id <= 0) {
            postButton.setText(this.getResources().getString(R.string.upload));
        } else {
            postButton.setText(this.getResources().getString(R.string.submit));
        }
    }

    // get file icon/thumbnail based on file type
    private void setThumbnail(String name) {
        fileThumbnail.setImageResource(GetThumbnails.fileSharing(name));
        backImage.setImageResource(R.drawable.primary_rounded_rectangle);
        backImage.setColorFilter(getApplicationContext().getResources().getColor(
                GetColor.getFileIconBackgroundColor(name)));
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadFile extends AsyncTask<String, Void, Integer> {
        ShowLoader showLoader;
        String path;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader = new ShowLoader();
            showLoader.showUploadDialog(UploadFileActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            path = params[0];
            int status = 12;
            String file_name = FileOperations.getFileName(path);
            String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER;

            Log.i(UploadFileActivity.class.getSimpleName(), "File Upload URL: " + url);

            String user_id = Preferences.get(General.USER_ID);
            try {
                String response = FileUpload.uploadFile(path, file_name, user_id, url,
                        Actions_.FMS, getApplicationContext(), UploadFileActivity.this);
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
                                    file_id = object.get(General.ID).getAsLong();
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
                    togglePostButton();
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.upload_successful), getApplicationContext());
                    footerLayout.setVisibility(View.VISIBLE);
                    fileNameText.setText(FileOperations.getFileName(path));
                    fileSizeText.setText(FileOperations.getSize(path));
                    if (CheckFileType.imageFile(path)) {
                        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                        Glide.with(getApplicationContext())
                                .load(path)
                                .transition(withCrossFade())
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.ic_image_thumb)
                                        .fitCenter()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                                .into(backImage);
                    } else {
                        setThumbnail(FileOperations.getFileName(path));
                    }
                    break;
                case 2:
                    ShowSnack.viewWarning(postButton, getApplicationContext().getResources()
                            .getString(R.string.failed), getApplicationContext());
                    break;
                case 11:
                    ShowSnack.viewWarning(postButton, getApplicationContext().getResources()
                            .getString(R.string.internal_error_occurred), getApplicationContext());
                    break;
                case 12:
                    ShowSnack.viewWarning(postButton, getApplicationContext().getResources()
                            .getString(R.string.network_error_occurred), getApplicationContext());
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RemoveFile extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> keyMap = KeyMaker_.getKey();
            RequestBody getBody = new FormBody.Builder()
                    .add(General.KEY, keyMap.get(General.KEY))
                    .add(General.TOKEN, keyMap.get(General.TOKEN))
                    .add(General.ACTION, Actions_.DELETE)
                    .add(General.ID, "" + file_id)
                    .build();
            try {
                MakeCall.postNormal(Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "")
                        + Urls_.MOBILE_UPLOADER, getBody, TAG, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ShowSnack.textViewWarning(postButton, getApplicationContext().getResources()
                    .getString(R.string.removed), getApplicationContext());
            file_id = 0;
            footerLayout.setVisibility(View.GONE);
            togglePostButton();
        }
    }

    // get user access if editing file
    private void getAccess() {
        if (friendsArrayList != null && friendsArrayList.size() > 0) {
            friendsArrayList.clear();
        }
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.EDIT_FILE);
        requestMap.put(General.ID, "" + file_id);
        requestMap.put(General.GROUP_ID, "" + group_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    friendsArrayList = Users_.parse(response, "user_access",
                            getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // hide soft keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // set add folder visibility based on accessibility
    private void toggleAddFolder() {

        if ((Preferences.get(General.GROUP_OWNER_ID) != null
                &&
                Preferences.get(General.GROUP_OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID)))
                || (Preferences.get(General.IS_MODERATOR) != null && Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1"))
                || (Preferences.get(General.IS_CASE_MANAGER) != null && Preferences.get(General.IS_CASE_MANAGER).equalsIgnoreCase("1"))
                || (Preferences.get(General.IS_CC) != null && Preferences.get(General.IS_CC).equalsIgnoreCase("1"))
                || (Preferences.get(General.IS_COACH) != null && Preferences.get(General.IS_COACH).equalsIgnoreCase("1"))
                || General.isCurruntUserHasPermissionToCreateEvent()) {
            iv_addFolder.setVisibility(View.VISIBLE);
        } else {
            iv_addFolder.setVisibility(View.GONE);
        }


        if (Preferences.get(General.ROLE).equalsIgnoreCase("Lead Peer Support Specialist")
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")
                || Preferences.get(General.IS_CASE_MANAGER).equalsIgnoreCase("1")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Coach")
                || Preferences.get(General.ROLE).equalsIgnoreCase("System Administrator")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Peer Mentor")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Care Coordinator")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Case Manager")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Consumer-Adult")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Parent/Guardian")) {
            if ((Preferences.get(General.GROUP_OWNER_ID) != null
                    &&
                    Preferences.get(General.GROUP_OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID)))) {
                iv_addFolder.setVisibility(View.VISIBLE);
            }
        }


    }

    //open users selector dialog
    @SuppressLint("CommitTransaction")
    private void openUsersSelector() {
        hideKeyboard();
        Bundle bundle = new Bundle();
        PermissionSelectorDialog dialogFrag = new PermissionSelectorDialog();
        bundle.putInt(General.GROUP_ID, group_id);
        bundle.putSerializable(Actions_.MY_FRIENDS, friendsArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.MY_FRIENDS);
    }

    //open folder selector dialog
    @SuppressLint("CommitTransaction")
    private void openFoldersSelector() {
        hideKeyboard();
        Bundle bundle = new Bundle();
        FolderListDialog dialogFrag = new FolderListDialog();
        bundle.putSerializable(Actions_.FOLDER_LIST, allFolderArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), Actions_.FOLDER_LIST);
    }

    // open create folder dialog
    @SuppressLint("CommitTransaction")
    private void createFoldersDialog() {
        hideKeyboard();
        Bundle bundle = new Bundle();
        CreateFolderDialog dialogFrag = new CreateFolderDialog();
        bundle.putSerializable(Actions_.FOLDER_LIST, allFolderArrayList);
        bundle.putInt(General.GROUP_ID, group_id);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getSupportFragmentManager().beginTransaction(), Actions_.FOLDER_LIST);
    }

    // initialize folder list with root folder
    private void initFolderList() {
        allFolderArrayList = new ArrayList<>();

        AllFolder_ allFolder_ = new AllFolder_();
        allFolder_.setStatus(1);
        allFolder_.setId(-1);
        allFolder_.setName("Root");
        allFolder_.setSub_folder(new ArrayList<Folder_>());
        allFolderArrayList.add(allFolder_);
    }

    // make call to fetch all folder list
    private void getFolders() {
        allFolderArrayList.clear();

        initFolderList();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "all_folders");
        requestMap.put(General.GROUP_ID, "" + group_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        String response;
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    ArrayList<AllFolder_> list = Alerts_.parseAllFolders(response, getApplicationContext(), TAG);
                    if (list.size() > 0) {
                        if (list.get(0).getStatus() == 1) {
                            allFolderArrayList.addAll(list);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Chat.INTENT_FILE:
                    //String file_path = PathUtils.getFilePath(getApplicationContext(), data.getData());
                    String file_path = UriUtils.getPathFromUri(getApplicationContext(), data.getData());

                    if (file_path == null || file_path.trim().length() <= 0) {
                        ShowSnack.textViewWarning(postButton, this.getResources()
                                .getString(R.string.valid_file_error), getApplicationContext());
                        return;
                    }
                    double size = FileOperations.getSizeMB(file_path);
                    if (size > 10) {
                        ShowSnack.textViewWarning(postButton, this.getResources()
                                .getString(R.string.max_10_mb_allowed), getApplicationContext());
                        return;
                    }
                    new UploadFile().execute(file_path);
                    break;
            }
        }
    }

    @Override
    public void permissionList(ArrayList<Friends_> users_arrayList, boolean isSelected) {
        friendsArrayList = users_arrayList;
    }

    @Override
    public void selectedFolder(long id, String directory, boolean isSelected) {
        if (isSelected) {
            folder_id = id;
            selectFolder.setText(directory);
        }
    }

    @Override
    public void FinalFolder(ArrayList<AllFolder_> folderArrayList) {
        allFolderArrayList = folderArrayList;
    }
}
