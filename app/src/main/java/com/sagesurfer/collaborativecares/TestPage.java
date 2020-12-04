package com.sagesurfer.collaborativecares;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.modules.wall.Attachment_;
import com.modules.wall.WallPostActivity;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.FileUpload;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

public class TestPage extends AppCompatActivity {

    Button Test;
    private ArrayList<Attachment_> attachmentArrayList;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Preferences.initialize(getApplicationContext());

        requestWritePermission();

        Test = findViewById(R.id.testClick);

        Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musicIntent = new Intent(Intent.ACTION_GET_CONTENT);
                musicIntent.setType("*/*");
                startActivityForResult(musicIntent, Chat.INTENT_FILE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Log.d(LOG_TAG, "Write Permission Failed");
                Toast.makeText(this, "You must allow permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestWritePermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Chat.INTENT_FILE:
                    String file_path = UriUtils.getPathFromUri(this, data.getData());

                    if (file_path == null || file_path.trim().length() <= 0) {

                        return;
                    }
                    double size = FileOperations.getSizeMB(file_path);
                    if (size > 10) {

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
            showLoader.showUploadDialog(TestPage.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            int status = 12;
            String path = params[0];
            String file_name = FileOperations.getFileName(params[0]);
            String url = "https://mhaw.sagesurfer.com/mobile_uploader.php";


            String user_id = Preferences.get(General.USER_ID);
            try {
                String response = FileUpload.uploadFile(params[0], file_name, user_id, url,
                        Actions_.WALL, getApplicationContext(), TestPage.this);
                Log.d("FileUpload", response);
                if (response != null) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                    if (jsonObject.has(Actions_.WALL)) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray(Actions_.WALL);
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
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.upload_successful), getApplicationContext());

                    break;
                case 2:

                    break;
                case 11:

                    break;
                case 12:

                    break;
                default:
                    break;
            }
        }
    }
}
