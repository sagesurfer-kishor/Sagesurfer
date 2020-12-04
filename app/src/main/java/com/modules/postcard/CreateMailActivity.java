package com.modules.postcard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.LoginActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.PathUtils;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.logger.Logger;
import com.sagesurfer.models.PostcardAttachment_;
import com.sagesurfer.models.Responses_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Mail_;
import com.sagesurfer.parser.Uploader_;
import com.sagesurfer.secure.KeyMaker_;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformComposeMailTask;
import com.sagesurfer.views.TagsEditText;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Kailash Karankal
 */

public class CreateMailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CreateMailActivity.class.getSimpleName();
    private ArrayList<PostcardAttachment_> attachmentArrayList;
    private String action = Actions_.COMPOSE_MAIL;

    private AppCompatImageButton attachButton;
    private TextView attachmentTag, warningText;
    //    private RecipientBubbleTextView toText;
    private EditText subjectText, descriptionText;
    private Postcard_ postcard_;
    private ShowLoader showLoader;
    private Toolbar toolbar;
    private String userName = "";
    private SparseIntArray mErrorString;
    private TagsEditText toText, ccText;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Preferences.initialize(this);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.create_mail_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Preferences.initialize(getApplicationContext());
        showLoader = new ShowLoader();
        mErrorString = new SparseIntArray();

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog(2);
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getResources().getString(R.string.compose));

        AppCompatImageButton sendButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_send);
        sendButton.setVisibility(View.VISIBLE);
        sendButton.setOnClickListener(this);

        attachButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activity_toolbar_attach);
        attachButton.setVisibility(View.VISIBLE);
        attachButton.setOnClickListener(this);

        toText = (TagsEditText) findViewById(R.id.create_mail_to);
//        toText.addTextChangedListener(textWatcher);
        ccText = (TagsEditText) findViewById(R.id.create_mail_cc);
//        ccText.addTextChangedListener(textWatcher);

        subjectText = (EditText) findViewById(R.id.create_mail_subject);
        descriptionText = (EditText) findViewById(R.id.create_mail_message);

        attachmentTag = (TextView) findViewById(R.id.create_mail_attachments_tag);
        warningText = (TextView) findViewById(R.id.create_mail_warning);

        attachmentArrayList = new ArrayList<>();

        Intent data = getIntent();

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {

            if (Preferences.get(General.CLINICIAN_USER_NAME).equalsIgnoreCase("")) {

            } else {
                //Default Clinician name only for patient
//                toText.setText(Preferences.get(General.CLINICIAN_USER_NAME)+",");
                toText.setText(Preferences.get(General.CLINICIAN_USER_NAME) + ",");
//                toText.setChips();
            }

        }

        if (data != null) {
            if (data.hasExtra(General.POSTCARD) && data.hasExtra(General.ACTION)) {
                action = data.getStringExtra(General.ACTION);
                if (action.equals("draft")) {
                    action = "compose_mail";
                }
                postcard_ = (Postcard_) data.getSerializableExtra(General.POSTCARD);
                //set data
                setData();

                if (postcard_ != null) {
                    String toString = postcard_.getToAll();

                    if (toString.length() > 0) {
//                        toText.setText(toString + ",");
                        toText.setText(toString + ",");
                        ccText.setText(postcard_.getCcAll());
                    }

                }
            }

            if (data.hasExtra(General.NAME)) {
                userName = data.getStringExtra(General.NAME);
                String toString = userName + ",";
//                toText.setText(toString);
                toText.setText(toString);
            }
        }
    }

    // set data to respective field if we are using old main to compose new mail from draft or any other mode
    private void setData() {
        toText = (TagsEditText) findViewById(R.id.create_mail_to);
        String to = getToString();
        // toText.setText(to);
        if (to.trim().length() > 0) {
            // toText.setChips();
        }

        ccText = (TagsEditText) findViewById(R.id.create_mail_cc);
        String cc = getCcString();
        //ccText.setText(cc);
        if (cc.trim().length() > 0) {
            //ccText.setChips();
        }

        subjectText.setText(postcard_.getSubject());
        descriptionText.setText(postcard_.getDescription());

        setAttachments();
    }

    // set attachments from attachments list
    private void setAttachments() {
        ViewGroup layout = (ViewGroup) findViewById(R.id.create_mail_attachments);
        if (postcard_.getIsAttachment() == 1) {
            attachmentTag.setVisibility(View.VISIBLE);
            attachmentArrayList = postcard_.getAttachmentArrayList();
            for (int i = 0; i < attachmentArrayList.size(); i++) {
                addLayout(layout, attachmentArrayList.get(i));
            }
        }
    }

    // get final receivers in "TO" field based on action
    private String getToString() {
        String toString = "";
        if (action.equalsIgnoreCase(Actions_.REPLY)) {
            if (postcard_.getUserName().length() > 0) {
                toString = postcard_.getUserName() + ",";
            }
        }

        if (action.equalsIgnoreCase(Actions_.FORWARD)) {
            if (postcard_.getUserName().length() > 0) {
                toString = postcard_.getUserName() + ",";
            }
        }

        if (action.equalsIgnoreCase(Actions_.DRAFT)) {
            if (postcard_.getToAll().length() > 0) {
                toString = postcard_.getToAll() + ",";
            }
        }
        if (action.equalsIgnoreCase(Actions_.REPLY_ALL)) {
            if (postcard_.getToAll().trim().length() > 0) {
                toString = postcard_.getUserName() + postcard_.getToAll() + ",";
            } else {
                if (postcard_.getUserName().length() > 0) {
                    toString = postcard_.getUserName() + ",";
                }
            }

        }
        return toString;
    }

    // get final receivers in "CC" field based on action
    private String getCcString() {
        String toString = "";
        if (action.equalsIgnoreCase(Actions_.REPLY_ALL) || action.equalsIgnoreCase(Actions_.DRAFT)) {
            if (postcard_.getCcAll().trim().length() > 0) {
                toString = postcard_.getCcAll() + ",";
            }
        }
        return toString;
    }

    // get attachment list based on mail status
    // if mail is new then new attachment will be added only
    // if mail is old then new attachments will be added with old one
    private String getAttachmentList(boolean isNew) {
        String attachment = "";
        ArrayList<String> attachList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (PostcardAttachment_ attachment_ : attachmentArrayList) {
            if (isNew) {
                if (attachment_.isNewFile()) {
                    attachList.add("" + attachment_.getId());
                }
            } else {
                stringBuilder = stringBuilder.append(FileOperations.getFileName(attachment_.getFile()) + "|");
                //stringBuilder = stringBuilder.append(attachment_.getFile() + "|");
            }
        }

        if (isNew) {
            attachment = attachList.toString();
            return attachment.replaceAll("\\[", "").replaceAll("]", "");
        } else {
            int index = stringBuilder.lastIndexOf("|");
            stringBuilder.replace(index, stringBuilder.length() + index, "");
            attachment = stringBuilder.toString();
            if (attachment.length() > 1) {
                attachment = attachment.substring(0, attachment.length());
            }
            return attachment;
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            warningText.setVisibility(View.GONE);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // make network call to compose mail
    private void compose() {
        int status = 12;
//        String to = toText.getText().toString().trim();
        String to = toText.getText().toString().trim();
        String cc = ccText.getText().toString().trim();
        String subject = subjectText.getText().toString();
        String description = descriptionText.getText().toString();

        String attachment = getAttachmentList(true);

        String old_attachment = "";
        if ((action.equalsIgnoreCase(Actions_.FORWARD) || action.equalsIgnoreCase(Actions_.DRAFT) || action.equalsIgnoreCase(Actions_.COMPOSE_MAIL))) {
            if (postcard_ != null && postcard_.getIsAttachment() == 1) {
                old_attachment = getAttachmentList(false);
            }
        }
        if (description.length() > 0) {
            description = Html.toHtml(descriptionText.getText());
        }

        String response = null;
        if (action.equalsIgnoreCase(Actions_.COMPOSE_MAIL)) {
            if (postcard_ != null) {
                response = PerformComposeMailTask.compose(to, cc, subject, description,
                        attachment, old_attachment, "" + postcard_.getMessageId(), this);
            } else {
                response = PerformComposeMailTask.compose(to, cc, subject, description,
                        attachment, old_attachment, "0", this);
            }

        } else if (action.equalsIgnoreCase(Actions_.REPLY)) {
            response = PerformComposeMailTask.reply(to, cc, subject, description, attachment,
                    "" + postcard_.getMessageId(), this);
        } else if (action.equalsIgnoreCase(Actions_.REPLY_ALL)) {
            response = PerformComposeMailTask.reply_all(to, cc, subject, description, attachment, "" + postcard_.getMessageId(), this);
        } else if (action.equalsIgnoreCase(Actions_.FORWARD)) {
            response = PerformComposeMailTask.forward(to, cc, subject, description, attachment, "" + postcard_.getMessageId(), old_attachment, this);
        } else if (action.equalsIgnoreCase(Actions_.DRAFT)) {
            if (postcard_ == null) {
                response = PerformComposeMailTask.draft(to, cc, subject, description, attachment,
                        "0", old_attachment, this);
            } else {
                response = PerformComposeMailTask.draft(to, cc, subject, description, attachment,
                        "" + postcard_.getMessageId(), old_attachment, this);
            }
        }

        Responses_ response_ = Mail_.parseActions(response, action, getApplicationContext(), TAG);
        if (response_ != null) {
            status = response_.getStatus();
        }
        showResponses(status, response_);
    }

    private void showResponses(int status, Responses_ response_) {
        String message;
        if (status == 1) {
            if (action.equalsIgnoreCase("draft")) {
                message = this.getResources().getString(R.string.draft_saved_successfully);
            } else {
                message = this.getResources().getString(R.string.successful);
            }
            SubmitSnackResponse.showSnack(status, message, attachButton, getApplicationContext());
            onBackPressed();
        } else if (status == 3) {
            String warningMessage = this.getResources().getString(R.string.invalid_username) + " " + response_.getUsername();
            warningText.setText(warningMessage);
            warningText.setVisibility(View.VISIBLE);
        } else {
            message = this.getResources().getString(R.string.action_failed);
            SubmitSnackResponse.showSnack(status, message, attachButton, getApplicationContext());
        }
    }

    // add attachment layout/row
    private void addLayout(final ViewGroup viewGroup, PostcardAttachment_ attachment_) {
        final View view = LayoutInflater.from(this).inflate(R.layout.postcard_attachment_item_layout, viewGroup, false);

        TextView name = (TextView) view.findViewById(R.id.attachment_list_item_name);
        TextView size = (TextView) view.findViewById(R.id.attachment_list_item_size);
        ImageView icon = (ImageView) view.findViewById(R.id.attachment_list_item_thumbnail);
        TextView extension = (TextView) view.findViewById(R.id.attachment_list_item_thumbnail_text);
        TextView remove = (TextView) view.findViewById(R.id.attachment_list_item_remove);
        remove.setVisibility(View.VISIBLE);
        remove.setTag(attachment_.getId());

        if (attachment_.getName() != null) {
            if (CheckFileType.imageFile(attachment_.getName())) {
                extension.setVisibility(View.GONE);

                //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                Glide.with(getApplicationContext()).load(attachment_.getFile())
                        .thumbnail(0.5f)
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_image_thumb)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(icon);
            } else {
                extension.setVisibility(View.VISIBLE);
                icon.setImageResource(R.drawable.primary_rounded_rectangle);
                icon.setColorFilter(this.getResources().getColor(GetColor.getFileIconBackgroundColor(attachment_.getName())));
                extension.setText(FileOperations.getFileExtension(attachment_.getName()).toUpperCase());
            }
        }
        name.setText(attachment_.getName());
        size.setText(FileOperations.bytes2String(attachment_.getSize()));
        viewGroup.addView(view);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (Integer) v.getTag();
                removeAttachment(id);
                viewGroup.removeView(view);
                toggleAttachmentTag();
            }
        });
    }

    // toggle add attachment tag based on max attachment number/size
    private void toggleAttachmentTag() {
        if (attachmentArrayList.size() > 0) {
            attachmentTag.setVisibility(View.VISIBLE);
        } else {
            attachmentTag.setVisibility(View.GONE);
        }
    }

    // remove attachment from list displayed
    private void removeAttachment(int id) {
        for (Iterator<PostcardAttachment_> it = attachmentArrayList.iterator(); it.hasNext(); ) {
            PostcardAttachment_ s = it.next();
            if (s.getId() == id) {
                it.remove();
            }
        }

        int i = 0;
        for (PostcardAttachment_ attachment_ : attachmentArrayList) {
            if (id == attachment_.getId()) {
                attachmentArrayList.remove(i);
            }
            i++;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Chat.INTENT_FILE:
                    String file_path = UriUtils.getPathFromUri(getApplicationContext(), data.getData());
                    if (file_path == null || file_path.trim().length() <= 0) {
                        ShowSnack.compatImageButtonWarning(attachButton, this.getResources()
                                .getString(R.string.valid_file_error), getApplicationContext());
                        return;
                    }
                    double size = FileOperations.getSizeMB(file_path);
                    if (size > 10) {
                        ShowSnack.compatImageButtonWarning(attachButton, this.getResources()
                                .getString(R.string.max_10_mb_allowed), getApplicationContext());
                        return;
                    }
                    long size_total = 0;
                    if (attachmentArrayList.size() > 0) {
                        size_total = (FileOperations.getCumulativeSize(attachmentArrayList)
                                + new File(file_path).length());
                    }

                    if (size_total > 10240000) {
                        ShowSnack.compatImageButtonWarning(attachButton, this.getResources().getString(R.string.max_attachment_size_10_mb_allowed), getApplicationContext());
                        return;
                    }
                    if (CheckFileType.executableFile(file_path)) {
                        ShowSnack.compatImageButtonWarning(attachButton, this.getResources()
                                .getString(R.string.valid_file_error), getApplicationContext());
                    } else {
                        new Upload().execute(file_path);
                    }
                    break;
            }
        }
    }

    // upload attachment file on server to temporary location
    // after successful upload it returns new location,name and id of file uploaded
    @SuppressWarnings("deprecation")
    private void execMultipartPost(final String file_path) throws Exception {
        final File file = new File(file_path);
        String contentType = file.toURL().openConnection().getContentType();
        RequestBody fileBody = RequestBody.create(MediaType.parse(contentType), file);
        final String filename = FileOperations.getFileName(file_path);
        HashMap<String, String> keyMap = KeyMaker_.getKey();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(General.USER_ID, Preferences.get(General.USER_ID))
                .addFormDataPart(General.ACTION, Actions_.POSTCARD)
                .addFormDataPart(General.KEY, keyMap.get(General.KEY))
                .addFormDataPart(General.TOKEN, keyMap.get(General.TOKEN))
                .addFormDataPart("userfile", filename, fileBody)
                .addFormDataPart("userfile", filename, fileBody)
                .addFormDataPart(General.IMEI, DeviceInfo.getDeviceId(this))
                .build();
        Request request = new Request.Builder()
                .url(Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + "/" + Urls_.MOBILE_UPLOADER)
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowSnack.compatImageButtonWarning(attachButton, getApplicationContext()
                                .getResources().getString(R.string.action_failed), getApplicationContext());
                    }
                });
            }


            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String result = response.body().string();
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(result).getAsJsonObject();
                try {
                    if (jsonObject.has("details")) {
                        JsonObject object = jsonObject.get("details").getAsJsonObject();
                        if (object.has(General.STATUS) && object.get(General.STATUS).getAsInt() == 21) {
                            //PerformLogoutTask.logout(activity);
                            HashMap<String, String> keyMap = KeyMaker_.getKey();
                            RequestBody logoutBody = new FormBody.Builder()
                                    .add(General.USER_ID, Preferences.get(General.USER_ID))
                                    .add(General.KEY, keyMap.get(General.KEY))
                                    .add(General.TOKEN, keyMap.get(General.TOKEN))
                                    .build();
                            String responseLogout = MakeCall.post(Preferences.get(General.DOMAIN) + "/" + Urls_.LOGOUT_URL, logoutBody, TAG, getApplicationContext(), CreateMailActivity.this);
                            if (responseLogout != null) {
                              /*  try {
                                    CometChat cometChat = CometChat.getInstance(getApplicationContext());
                                    cometChat.logout(new Callbacks() {
                                        @Override
                                        public void successCallback(JSONObject response) {
                                            Log.e(TAG, "CometChat Logout Successful");
                                        }

                                        @Override
                                        public void failCallback(JSONObject response) {
                                            Log.e(TAG, "CometChat Logout Failed");
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/

                                Preferences.save(General.IS_LOGIN, 0);
                                Preferences.removeKey("regId");
                                Preferences.removeKey(General.GROUP_NAME);
                                Preferences.removeKey(General.GROUP_ID);
                                Preferences.removeKey(General.OWNER_ID);
                                Preferences.save(General.IS_COMETCHAT_LOGIN_SUCCESS, false);

                             /*   SugarRecord.deleteAll(OneOnOneMessage.class);
                                SugarRecord.deleteAll(Groups.class);
                                SugarRecord.deleteAll(Conversation.class);
                                SugarRecord.deleteAll(GroupMessage.class);
                                SugarRecord.deleteAll(models.Status.class);
                                SugarRecord.deleteAll(Contact.class);
                                SugarRecord.deleteAll(Bot.class);*/

                                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            }
                        } else if (object.has(General.STATUS) && object.get(General.STATUS).getAsInt() == 20) {
                            final String appPackageName = getApplicationContext().getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    } else {
                        final ArrayList<PostcardAttachment_> attachmentList = Uploader_.parse(result, Actions_.POSTCARD, getApplicationContext(), TAG);
                        Logger.debug(TAG, "result: " + result + "\nid:" + attachmentList.get(0).getId(), getApplicationContext());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (attachmentList.get(0).getStatus() == 1) {
                                    PostcardAttachment_ attachment_ = new PostcardAttachment_();
                                    attachment_.setId(attachmentList.get(0).getId());
                                    attachment_.setName(attachmentList.get(0).getFile());
                                    attachment_.setSize(file.length());
                                    attachment_.setFile(file_path);
                                    attachment_.setNewFile(true);

                                    attachmentArrayList.add(attachment_);

                                    ShowSnack.compatImageButtonSuccess(attachButton, getApplicationContext().getResources().getString(R.string.upload_successful), getApplicationContext());
                                    ViewGroup layout = (ViewGroup) findViewById(R.id.create_mail_attachments);

                                    attachmentTag.setVisibility(View.VISIBLE);
                                    addLayout(layout, attachmentArrayList.get(attachmentArrayList.size() - 1));

                                } else if (attachmentList.get(0).getStatus() == 2) {
                                    ShowSnack.compatImageButtonWarning(attachButton, getApplicationContext().getResources().getString(R.string.action_failed), getApplicationContext());
                                } else if (attachmentList.get(0).getStatus() == 13) {
                                    ShowSnack.compatImageButtonWarning(attachButton, getApplicationContext().getResources().getString(R.string.internal_error_occurred), getApplicationContext());
                                } else if (attachmentList.get(0).getStatus() == 12) {
                                    ShowSnack.compatImageButtonWarning(attachButton, getApplicationContext().getResources().getString(R.string.network_error_occurred), getApplicationContext());
                                }
                                if (attachmentArrayList.size() == 5) {
                                    ShowSnack.compatImageButtonWarning(attachButton, getApplicationContext().getResources().getString(R.string.max_5_attachments_allowed), getApplicationContext());
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // make background call to upload file
    @SuppressLint("StaticFieldLeak")
    class Upload extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader.showUploadDialog(CreateMailActivity.this);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                execMultipartPost(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showLoader.dismissUploadDialog();
        }
    }

    // validate all field for appropriate data
    private boolean validate() {
//        String to = toText.getText().toString().trim();
        String to = toText.getText().toString().trim();
        String cc = ccText.getText().toString().trim();
        String subject = subjectText.getText().toString();
        String description = descriptionText.getText().toString();

        if (to.length() <= 0 && cc.length() <= 0) {
            toText.setError("Please add recipient");
            return false;
        }
        if (subject.length() <= 0) {
            confirmationDialog(1);
            return false;
        }
        if (subject.length() > 1000 && !action.equalsIgnoreCase(Actions_.DRAFT)) {
            subjectText.setError("Max 1000 char allowed");
            return false;
        }
        if (description.length() > 3000 && !action.equalsIgnoreCase(Actions_.DRAFT)) {
            descriptionText.setError("Max 3000 char allowed");
            return false;
        }

        return true;
    }

    // confirmation dialog to take confirmation before sending mail without subject
    // and save mail to draft on back pressed
    private void confirmationDialog(final int type) {
        //type 1: mail without subject
        //type 2: save draft on back pressed

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);

        if (type == 1) {
            subTitle.setText(this.getResources().getString(R.string.yor_message_dont_have_subject));
        }
        if (type == 2) {
            subTitle.setText(this.getResources().getString(R.string.do_you_want_save_message_as_draft));
        }
        title.setText(this.getResources().getString(R.string.action_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (type == 2) {
                    action = Actions_.DRAFT;
                }
                compose();
                if (type == 2) {
                    onBackPressed();
                }
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById(R.id.delete_confirmation_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (type == 2) {
                    onBackPressed();
                }
            }
        });
        dialog.show();
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
            case R.id.imagebutton_activitytoolbar_send:
                if (validate()) {
                    compose();
                }
                break;
            case R.id.imagebutton_activity_toolbar_attach:
                hideKeyboard();
                if (attachmentArrayList.size() >= 5) {
                    ShowSnack.compatImageButtonWarning(attachButton, this.getResources()
                            .getString(R.string.max_5_attachments_allowed), getApplicationContext());
                    return;
                }

                ActivityCompat.requestPermissions(CreateMailActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
//                Intent musicIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                musicIntent.setType("*/*");
//                startActivityForResult(musicIntent, Chat.INTENT_FILE);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent musicIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    musicIntent.setType("*/*");
                    startActivityForResult(musicIntent, Chat.INTENT_FILE);
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
    protected void onResume() {
        super.onResume();
    }
}
