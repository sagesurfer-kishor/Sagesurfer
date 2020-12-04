package com.modules.support;

import android.Manifest;
import android.annotation.SuppressLint;
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

import android.util.SparseIntArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ArrayOperations;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.FileUpload;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.PathUtils;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.models.WallAttachment_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.secure.KeyMaker_;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Kailash Karankal
 * Created on 4/3/2018
 * Last Modified on 4/3/2018
 */

public class FeedbackActivity extends AppCompatActivity {
    private static final String TAG = FeedbackActivity.class.getSimpleName();
    @BindView(R.id.edittext_support_feedback_message)
    EditText editTextSupportFeedbackMessage;
    @BindView(R.id.imageview_support_feedback_attachment_one)
    ImageView imageViewSupportFeedbackAttachmentOne;
    @BindView(R.id.imageview_support_feedback_attachment_one_cancel)
    AppCompatImageView imageViewSupportFeedbackAttachmentOneCancel;
    @BindView(R.id.imageview_support_feedback_attachment_two)
    ImageView imageViewSupportFeedbackAttachmentTwo;
    @BindView(R.id.imageview_support_feedback_attachment_two_cancel)
    AppCompatImageView imageViewSupportFeedbackAttachmentTwoCancel;

    private ArrayList<WallAttachment_> attachmentArrayList;

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

        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        attachmentArrayList = new ArrayList<>();
        mErrorString = new SparseIntArray();

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

        TextView textViewActivityToolbarTitle = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        textViewActivityToolbarTitle.setText(this.getResources().getString(R.string.feedback));

        TextView textViewActivityToolbarPost = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        textViewActivityToolbarPost.setVisibility(View.VISIBLE);
        textViewActivityToolbarPost.setText(this.getResources().getString(R.string.submit));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // String file_path = PathUtils.getFilePath(getApplicationContext(), data.getData());

            String file_path = UriUtils.getPathFromUri(getApplicationContext(), data.getData());

            if (file_path == null || file_path.trim().length() <= 0) {
                ShowSnack.viewWarning(imageViewSupportFeedbackAttachmentOne, this.getResources().getString(R.string.valid_file_error), getApplicationContext());
                return;
            }
            double size = FileOperations.getSizeMB(file_path);
            if (size > 10) {
                ShowSnack.viewWarning(imageViewSupportFeedbackAttachmentOne, this.getResources().getString(R.string.max_10_mb_allowed), getApplicationContext());
                return;
            }
            if (CheckFileType.imageFile(file_path)) {
                new UploadFile(requestCode, file_path).execute();
            } else {
                ShowSnack.viewWarning(imageViewSupportFeedbackAttachmentOne, this.getResources().getString(R.string.valid_file_error), getApplicationContext());
            }
        }
    }

    // add image attachment to feedback
    private void setAttachments(int position, String path) {
        if (position == 1) {
            imageViewSupportFeedbackAttachmentOneCancel.setVisibility(View.VISIBLE);

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(getApplicationContext())
                    .load(path)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .transition(withCrossFade())
                    .into(imageViewSupportFeedbackAttachmentOne);
        } else {
            imageViewSupportFeedbackAttachmentTwoCancel.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(path)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(imageViewSupportFeedbackAttachmentTwo);
        }
    }

    // validate feedback message for it's min/max length
    private boolean validate(String message) {
        if (message == null || message.length() <= 0) {
            editTextSupportFeedbackMessage.setError("Invalid Feedback Message\nMessage is mandatory");
            return false;
        }
        if (message.length() < 3) {
            editTextSupportFeedbackMessage.setError("Minimum 3 char required");
            return false;
        }
        if (message.length() > 250) {
            editTextSupportFeedbackMessage.setError("Maximum 250 char allowed");
            return false;
        }
        return true;
    }

    // get attachment id based on location
    private String getAttachmentId(int position) {
        ArrayList<Integer> idList = new ArrayList<>();
        if (attachmentArrayList.size() > 0) {
            for (WallAttachment_ wallAttachment_ : attachmentArrayList) {
                if (position == 0) {
                    idList.add(wallAttachment_.getId());
                } else {
                    if (wallAttachment_.getPosition() == position) {
                        idList.add(wallAttachment_.getId());
                    }
                }
            }
        }
        return ArrayOperations.listToString(idList);
    }

    // remove attachment from list based on position
    private void removeAttachmentId(int position) {
        int i = 0;
        if (attachmentArrayList.size() > 0) {
            for (WallAttachment_ wallAttachment_ : attachmentArrayList) {
                if (wallAttachment_.getPosition() == position) {
                    attachmentArrayList.remove(i);
                    break;
                }
                i++;
            }
        }
    }

    // make network call to submit feedback
    private void submit(String message) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "add");
        requestMap.put(General.MESSAGE, message);
        if (attachmentArrayList.size() == 1) {
            requestMap.put("img1", "" + attachmentArrayList.get(0).getId());
        } else {
            requestMap.put("img1", "");
        }
        if (attachmentArrayList.size() > 1) {
            requestMap.put("img2", "" + attachmentArrayList.get(1).getId());
        } else {
            requestMap.put("img2", "");
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.FEEDBACK_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    if (Error_.oauth(response, getApplicationContext()) == 13) {
                        showResponses(13);
                        return;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, "feedback");
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            showResponses(object.get(General.STATUS).getAsInt());
                        } else {
                            showResponses(11);
                            return;
                        }
                    } else {
                        showResponses(11);
                        return;
                    }
                } else {
                    showResponses(12);
                    return;
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(11);
    }

    private void showResponses(int status) {
        String message = "";
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
            onBackPressed();
        } else if (status == 2) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, imageViewSupportFeedbackAttachmentOne, getApplicationContext());
    }

    // background call to remove uploaded file
    @SuppressLint("StaticFieldLeak")
    private class RemoveAttachment extends AsyncTask<Void, Void, Void> {
        final int position;
        final String id;

        RemoveAttachment(int position, String id) {
            this.position = position;
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, String> keyMap = KeyMaker_.getKey();
            RequestBody getBody = new FormBody.Builder()
                    .add(General.KEY, keyMap.get(General.KEY))
                    .add(General.TOKEN, keyMap.get(General.TOKEN))
                    .add(General.ACTION, Actions_.DELETE)
                    .add(General.ID, id)
                    .build();
            try {
                MakeCall.postNormal(Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER, getBody, TAG, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (position == 0) {
                attachmentArrayList.clear();
                imageViewSupportFeedbackAttachmentOne.setImageResource(R.drawable.vi_add_gray);
                imageViewSupportFeedbackAttachmentOneCancel.setVisibility(View.GONE);
                imageViewSupportFeedbackAttachmentTwo.setImageResource(R.drawable.vi_add_gray);
                imageViewSupportFeedbackAttachmentTwoCancel.setVisibility(View.GONE);
            }
            if (position == 1) {
                ShowToast.toast(getApplicationContext().getResources().getString(R.string.removed), getApplicationContext());
                imageViewSupportFeedbackAttachmentOne.setImageResource(R.drawable.vi_add_gray);
                imageViewSupportFeedbackAttachmentOneCancel.setVisibility(View.GONE);
                removeAttachmentId(1);
            }
            if (position == 2) {
                ShowToast.toast(getApplicationContext().getResources().getString(R.string.removed), getApplicationContext());
                imageViewSupportFeedbackAttachmentTwo.setImageResource(R.drawable.vi_add_gray);
                imageViewSupportFeedbackAttachmentTwoCancel.setVisibility(View.GONE);
                removeAttachmentId(2);
            }
        }
    }

    // background call to make upload file call
    @SuppressLint("StaticFieldLeak")
    private class UploadFile extends AsyncTask<Void, Void, Integer> {
        ShowLoader showLoader;
        final int position;
        final String path;

        UploadFile(int position, String path) {
            this.position = position;
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader = new ShowLoader();
            showLoader.showUploadDialog(FeedbackActivity.this);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int status = 12;
            String file_name = FileOperations.getFileName(path);
            String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "")
                    + Urls_.MOBILE_UPLOADER;
            String user_id = Preferences.get(General.USER_ID);
            try {
                String response = FileUpload.uploadFile(path, file_name, user_id, url,
                        Actions_.SUPPORT, getApplicationContext(), FeedbackActivity.this);
                if (response != null) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                    if (jsonObject.has(Actions_.SUPPORT)) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray(Actions_.SUPPORT);
                        if (jsonArray != null) {
                            JsonObject object = jsonArray.get(0).getAsJsonObject();
                            if (object.has(General.STATUS)) {
                                status = object.get(General.STATUS).getAsInt();
                                if (status == 1) {
                                    WallAttachment_ wallAttachment_ = new WallAttachment_();
                                    wallAttachment_.setId(object.get(General.ID).getAsInt());
                                    wallAttachment_.setPath(path);
                                    wallAttachment_.setImage(file_name);
                                    wallAttachment_.setSize(FileOperations.getSize(path));
                                    wallAttachment_.setPosition(position);
                                    attachmentArrayList.add(wallAttachment_);
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
                    ShowToast.toast(getApplicationContext().getResources().getString(R.string.upload_successful), getApplicationContext());
                    setAttachments(position, path);
                    break;
                case 2:
                    ShowSnack.viewWarning(imageViewSupportFeedbackAttachmentOne, getApplicationContext().getResources().getString(R.string.failed), getApplicationContext());
                    break;
                case 11:
                    ShowSnack.viewWarning(imageViewSupportFeedbackAttachmentOne, getApplicationContext().getResources().getString(R.string.internal_error_occurred), getApplicationContext());
                    break;
                case 12:
                    ShowSnack.viewWarning(imageViewSupportFeedbackAttachmentOne, getApplicationContext().getResources().getString(R.string.network_error_occurred), getApplicationContext());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (attachmentArrayList.size() > 0) {
            new RemoveAttachment(0, "" + getAttachmentId(0)).execute();
        }
        super.onBackPressed();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @OnClick({R.id.textview_activitytoolbar_post, R.id.button_support_feedback_faq,
            R.id.imageview_support_feedback_attachment_one, R.id.imageview_support_feedback_attachment_two,
            R.id.imageview_support_feedback_attachment_one_cancel, R.id.imageview_support_feedback_attachment_two_cancel})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.textview_activitytoolbar_post:
                String message = editTextSupportFeedbackMessage.getText().toString().trim();
                if (validate(message)) {
                    submit(message);
                }
                break;
            case R.id.button_support_feedback_faq:
                if (attachmentArrayList.size() > 0) {
                    new RemoveAttachment(0, "" + getAttachmentId(0)).execute();
                }
                Intent faqIntent = new Intent(getApplicationContext(), NewFaqActivity.class);
                startActivity(faqIntent);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.imageview_support_feedback_attachment_one:

                ActivityCompat.requestPermissions(FeedbackActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

//                Intent intentOne = new Intent(Intent.ACTION_GET_CONTENT);
//                intentOne.setType("*/*");
//                startActivityForResult(intentOne, 1);
                break;
            case R.id.imageview_support_feedback_attachment_two:

                ActivityCompat.requestPermissions(FeedbackActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

//                Intent intentTwo = new Intent(Intent.ACTION_GET_CONTENT);
//                intentTwo.setType("*/*");
//                startActivityForResult(intentTwo, 2);
                break;
            case R.id.imageview_support_feedback_attachment_one_cancel:
                new RemoveAttachment(1, "" + getAttachmentId(1)).execute();
                break;
            case R.id.imageview_support_feedback_attachment_two_cancel:
                new RemoveAttachment(2, "" + getAttachmentId(2)).execute();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intentOne = new Intent(Intent.ACTION_GET_CONTENT);
                    intentOne.setType("*/*");
                    startActivityForResult(intentOne, 1);
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

            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intentTwo = new Intent(Intent.ACTION_GET_CONTENT);
                    intentTwo.setType("*/*");
                    startActivityForResult(intentTwo, 2);
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
            default:
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
