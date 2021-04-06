package com.modules.wall;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.FileUpload;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.secure._Base64;
import com.sagesurfer.selectors.MultiTeamSelectorDialog;
import com.sagesurfer.selectors.MultiUserSelectorDialog;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.tasks.PerformGetUsersTask;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 12-07-2017
 * Last Modified on 15-12-2017
 */

public class WallPostActivity extends AppCompatActivity implements View.OnClickListener, MultiTeamSelectorDialog.SelectedTeams, MultiUserSelectorDialog.SelectedUsers {
    private static final String TAG = WallPostActivity.class.getSimpleName();
    private ArrayList<Attachment_> attachmentArrayList;
    private ArrayList<Teams_> teamsArrayList;
    private ArrayList<Friends_> friendsArrayList;
    private boolean isKeyboard = false;
    private String team_id = "0", user_id = "0", start_time = "0";
    private LinearLayout iconFooter, footerLayout;
    private AppCompatImageView attachmentOne, attachmentTwo, attachmentThree;
    private AppCompatImageButton cancelOne, cancelTwo, cancelThree;
    private TextView postButton;
    private EditText messageBox;
    private ValueAnimator valueAnimator;
    private ShowLoader showLoader;
    private Toolbar toolbar;
    private SparseIntArray mErrorString;

    private static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.wall_post_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Preferences.initialize(getApplicationContext());
        attachmentArrayList = new ArrayList<>();
        teamsArrayList = new ArrayList<>();
        friendsArrayList = new ArrayList<>();
        mErrorString = new SparseIntArray();
        showLoader = new ShowLoader();
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
        titleText.setText(this.getResources().getString(R.string.wall_post));

        postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.VISIBLE);
        postButton.setOnClickListener(this);

        attachmentOne = (AppCompatImageView) findViewById(R.id.wall_post_attachment_one);
        attachmentTwo = (AppCompatImageView) findViewById(R.id.wall_post_attachment_two);
        attachmentThree = (AppCompatImageView) findViewById(R.id.wall_post_attachment_three);

        cancelOne = (AppCompatImageButton) findViewById(R.id.wall_post_image_one_cancel);
        cancelOne.setOnClickListener(this);
        cancelTwo = (AppCompatImageButton) findViewById(R.id.wall_post_image_two_cancel);
        cancelTwo.setOnClickListener(this);
        cancelThree = (AppCompatImageButton) findViewById(R.id.wall_post_image_three_cancel);
        cancelThree.setOnClickListener(this);

        iconFooter = (LinearLayout) findViewById(R.id.wall_post_menu_footer);
        iconFooter.setVisibility(View.GONE);
        iconFooter.setOnClickListener(this);

        footerLayout = (LinearLayout) findViewById(R.id.wall_post_menu_list_footer);
        footerLayout.setVisibility(View.VISIBLE);
        footerLayout.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        footerLayout.getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        footerLayout.setVisibility(View.VISIBLE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(
                                0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec
                                .makeMeasureSpec(0,
                                        View.MeasureSpec.UNSPECIFIED);
                        footerLayout.measure(widthSpec, heightSpec);

                        valueAnimator = slideAnimator(0,
                                footerLayout.getMeasuredHeight());
                        return true;
                    }
                });
        messageBox = (EditText) findViewById(R.id.wall_post_message_box);
        LinearLayout addFile = (LinearLayout) findViewById(R.id.wall_post_menu_add_file);
        addFile.setOnClickListener(this);
        LinearLayout addUser = (LinearLayout) findViewById(R.id.wall_post_menu_add_user);
        addUser.setOnClickListener(this);
        LinearLayout addTeam = (LinearLayout) findViewById(R.id.wall_post_menu_add_team);
        addTeam.setOnClickListener(this);

        final View activityRootView = findViewById(R.id.wall_post_main_layout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(getApplicationContext(), 200)) {
                    isKeyboard = true;
                    setFooter();
                }
            }
        });

        setFooter();
    }

    // handle footer layout to expand with animation
    private void expand() {
        footerLayout.setVisibility(View.VISIBLE);
        iconFooter.setVisibility(View.GONE);
        valueAnimator.start();
    }

    // hide footer layout and collapse it with animation
    private void collapse() {
        int finalHeight = footerLayout.getHeight();
        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                // Height=0, but it set visibility to GONE
                footerLayout.setVisibility(View.GONE);
                iconFooter.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    // slide animation for footer to open/collapse
    private ValueAnimator slideAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = footerLayout
                        .getLayoutParams();
                layoutParams.height = value;
                footerLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    // set footer based on soft keyboard status (open/closed)
    private void setFooter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isKeyboard) {
                    iconFooter.setVisibility(View.VISIBLE);
                    footerLayout.setVisibility(View.GONE);
                } else {
                    iconFooter.setVisibility(View.GONE);
                    footerLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // set file uploaded to appropriate location with appropriate icon/thumbnail
    private void setFile() {
        TextView count = (TextView) findViewById(R.id.wall_post_attachment_count);
        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attachmentIntent = new Intent(getApplicationContext(), AttachmentListActivity.class);
                attachmentIntent.putExtra(General.ATTACHMENTS, attachmentArrayList);
                startActivity(attachmentIntent);
                overridePendingTransition(0, 0);
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

            cancelOne.setVisibility(View.GONE);
            cancelTwo.setVisibility(View.GONE);
            cancelThree.setVisibility(View.GONE);
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

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
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

    private void hideKeyboard() {
        isKeyboard = false;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // open team selector dialog fragment
    @SuppressLint("CommitTransaction")
    private void openTeamSelector() {
        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new MultiTeamSelectorDialog();
        bundle.putSerializable(General.TEAM_LIST, teamsArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), General.TEAM_LIST);
    }

    // open users selector dialog fragment
    @SuppressLint("CommitTransaction")
    private void openUsersSelector() {
        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new MultiUserSelectorDialog();
        bundle.putSerializable(Actions_.MY_FRIENDS, friendsArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.MY_FRIENDS);
    }

    @Override
    public void selectedTeams(ArrayList<Teams_> teams_arrayList, boolean isSelected) {
        teamsArrayList = teams_arrayList;
        if (isSelected) {
            team_id = GetSelected.wallTeams(teamsArrayList);
        }
    }

    // get list of selected users only from users list
    private void getTeams() {
        if (teamsArrayList == null || teamsArrayList.size() == 0) {
            teamsArrayList = PerformGetTeamsTask.getNormalTeams(Actions_.ALL_TEAMS, getApplicationContext(), TAG, false, this);
        }
    }

    // get list of selected users only from users list
    private void getFriends() {
        if (friendsArrayList == null || friendsArrayList.size() == 0) {
            String shared_to_ids = "";
            friendsArrayList = PerformGetUsersTask.get(Actions_.MY_FRIENDS, shared_to_ids, getApplicationContext(), TAG, this);
        }
    }

    // make network call to post new wall post
    private void postWallFeed(String message, View view) {
        int status = 12;
        String info = DeviceInfo.get(this);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.POST_WALL);
        requestMap.put(General.USER_LIST, user_id);
        requestMap.put(General.TEAM_LIST, team_id);
        requestMap.put(General.ATTACHMENTS, GetSelected.wallAttachments(attachmentArrayList));
        requestMap.put(General.WALL_TEXT, message);
        requestMap.put(General.START_TIME, start_time);
        requestMap.put(General.END_TIME, GetTime.getChatTimestamp());
        requestMap.put(General.INFO, _Base64.encode(info));
        requestMap.put(General.IP, DeviceInfo.getDeviceMAC(this));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.WALL_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.POST_WALL);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            status = object.get(General.STATUS).getAsInt();
                        } else {
                            status = 11;
                        }
                    } else {
                        status = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status, view);
    }

    private void showResponses(int status, View view) {
        showLoader.dismissPostingDialog();
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else {
            message = this.getResources().getString(R.string.action_failed);
        }
        postButton.setEnabled(false);
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    // validate wll post message for min/max length
    private boolean validate(String text) {
        if (text == null) {
            messageBox.setError("Enter valid post\nMin 3 Char required");
            return false;
        }
        if (text.trim().length() <= 0) {
            messageBox.setError("Enter valid post\nMin 2 Char required");
            return false;
        }
        if (text.length() <= 1) {
            messageBox.setError("Min 2 Char required");
            return false;
        }
        if (text.length() > 1000) {
            messageBox.setError("Max 1000 Char allowed");
            return false;
        }
        return true;
    }

    @Override
    public void selectedUsers(ArrayList<Friends_> users_arrayList, String selfCareContentId, boolean isSelected) {
        friendsArrayList = users_arrayList;
        if (isSelected) {
            user_id = GetSelected.wallUsers(users_arrayList);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            isKeyboard = true;
            setFooter();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            isKeyboard = false;
            setFooter();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Chat.INTENT_FILE:
                    String file_path = UriUtils.getPathFromUri(this, data.getData());

                    if (file_path == null || file_path.trim().length() <= 0) {
                        ShowSnack.textViewWarning(postButton, this.getResources()
                                        .getString(R.string.valid_file_error),
                                getApplicationContext());
                        return;
                    }
                    double size = FileOperations.getSizeMB(file_path);
                    if (size > 10) {
                        ShowSnack.textViewWarning(postButton, this.getResources()
                                        .getString(R.string.max_10_mb_allowed),
                                getApplicationContext());
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

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        getTeams();
        getFriends();
    }

    @Override
    protected void onStart() {
        super.onStart();
        start_time = GetTime.getChatTimestamp();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(General.TEAM_LIST, teamsArrayList);
        outState.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            teamsArrayList = (ArrayList<Teams_>) savedInstanceState.getSerializable(General.TEAM_LIST);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wall_post_menu_footer:
                if (isKeyboard) {
                    hideKeyboard();
                }
                if (footerLayout.getVisibility() == View.GONE) {
                    expand();
                } else {
                    collapse();
                }
                break;
            case R.id.wall_post_menu_add_file:

                ActivityCompat.requestPermissions(WallPostActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

//                Intent musicIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                musicIntent.setType("*/*");
//                startActivityForResult(musicIntent, Chat.INTENT_FILE);
                break;
            case R.id.wall_post_menu_add_team:
                if (teamsArrayList == null || teamsArrayList.size() <= 0) {
                    ShowSnack.viewWarning(v, this.getResources().getString(
                            R.string.teams_unavailable), getApplicationContext());
                    return;
                }
                openTeamSelector();
                break;
            case R.id.wall_post_menu_add_user:
                if (friendsArrayList == null || friendsArrayList.size() <= 0) {
                    ShowSnack.viewWarning(v, this.getResources().getString(
                            R.string.friends_unavailable), getApplicationContext());
                    return;
                }
                openUsersSelector();
                break;
            case R.id.wall_post_image_three_cancel:
                ShowSnack.viewWarning(v, this.getResources().getString(R.string.removed), getApplicationContext());
                attachmentArrayList.remove(2);
                setFile();
                break;
            case R.id.wall_post_image_two_cancel:
                ShowSnack.viewWarning(v, this.getResources().getString(R.string.removed), getApplicationContext());
                attachmentArrayList.remove(1);
                setFile();
                break;
            case R.id.wall_post_image_one_cancel:
                attachmentArrayList.remove(0);
                ShowSnack.viewWarning(v, this.getResources().getString(R.string.removed), getApplicationContext());
                setFile();
                break;
            case R.id.textview_activitytoolbar_post:
                String message = messageBox.getText().toString();
                if (validate(message)) {
                    postButton.setEnabled(false);
                    showLoader.showPostingDialog(this);
                    postWallFeed(message, v);
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
            default:
                return;
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
            showLoader.showUploadDialog(WallPostActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            int status = 12;
            String path = params[0];
            String file_name = FileOperations.getFileName(params[0]);
            String file_size = FileOperations.getSize(params[0]);

            String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME,
                    "") + Urls_.MOBILE_UPLOADER;
            String user_id = Preferences.get(General.USER_ID);
            try {
                String response = FileUpload.uploadFile(params[0], file_name, user_id, url,
                        Actions_.WALL, getApplicationContext(), WallPostActivity.this);
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
                    setFile();
                    break;
                case 2:
                    ShowSnack.textViewWarning(postButton, getApplicationContext().getResources()
                            .getString(R.string.failed), getApplicationContext());
                    break;
                case 11:
                    ShowSnack.textViewWarning(postButton, getApplicationContext().getResources()
                            .getString(R.string.internal_error_occurred), getApplicationContext());
                    break;
                case 12:
                    ShowSnack.textViewWarning(postButton, getApplicationContext().getResources()
                            .getString(R.string.network_error_occurred), getApplicationContext());
                    break;
                default:
                    break;
            }
        }
    }

}