package com.modules.wall.update;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.modules.wall.AttachmentListActivity;
import com.modules.wall.Attachment_;
import com.modules.wall.WallPostActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.DeviceInfo;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.library.GetThumbnails;
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
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;
import com.utils.AppLog;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class EditWallPost extends AppCompatActivity implements View.OnClickListener, MultiTeamSelectorDialog.SelectedTeams, MultiUserSelectorDialog.SelectedUsers {
    private static final String TAG = "EditWallPost";
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
        setContentView(R.layout.activity_edit_wall_post);

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

        if(getIntent()!= null && getIntent().hasExtra("feedId")){
            getWallDataToUpdate(getIntent().getStringExtra("feedId"));
        }
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

    private void getWallDataToUpdate(String wall_Id) {
        HashMap<String, String> requestMap = new HashMap<>();
        AppLog.i(TAG,"Wall id - "+wall_Id);
        AppLog.i(TAG,"User id - "+Preferences.get(General.USER_ID));
        
        requestMap.put(General.ACTION, Actions_.GET_WALL_FEED);
        requestMap.put(General.SHARE_ID, "" +wall_Id);
        requestMap.put(General.USER_ID, "" + Preferences.get(General.USER_ID));
        //requestMap.put(General.LAST_DATE, databaseRetrieve_.retrieveUpdateLog(General.FEED));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.GET_SINGLE_WALL_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this);
                if (response != null) {
                    AppLog.i(TAG, "getWallDataToUpdate "+response);
                }
                else {
                    AppLog.i(TAG, "getWallDataToUpdate Data is null..");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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

                ActivityCompat.requestPermissions(EditWallPost.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

//                Intent musicIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                musicIntent.setType("*/*");
//                startActivityForResult(musicIntent, Chat.INTENT_FILE);
                break;
            case R.id.wall_post_menu_add_team:
                if (teamsArrayList == null || teamsArrayList.size() <= 0) {
                    ShowSnack.viewWarning(view, this.getResources().getString(
                            R.string.teams_unavailable), getApplicationContext());
                    return;
                }
                openTeamSelector();
                break;
            case R.id.wall_post_menu_add_user:
                if (friendsArrayList == null || friendsArrayList.size() <= 0) {
                    ShowSnack.viewWarning(view, this.getResources().getString(
                            R.string.friends_unavailable), getApplicationContext());
                    return;
                }
                openUsersSelector();
                break;
            case R.id.wall_post_image_three_cancel:
                ShowSnack.viewWarning(view, this.getResources().getString(R.string.removed), getApplicationContext());
                attachmentArrayList.remove(2);
                setFile();
                break;
            case R.id.wall_post_image_two_cancel:
                ShowSnack.viewWarning(view, this.getResources().getString(R.string.removed), getApplicationContext());
                attachmentArrayList.remove(1);
                setFile();
                break;
            case R.id.wall_post_image_one_cancel:
                attachmentArrayList.remove(0);
                ShowSnack.viewWarning(view, this.getResources().getString(R.string.removed), getApplicationContext());
                setFile();
                break;
            case R.id.textview_activitytoolbar_post:
                String message = messageBox.getText().toString();
                if (validate(message)) {
                    postButton.setEnabled(false);
                    showLoader.showPostingDialog(this);
                    postWallFeed(message, view);
                }
                break;
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

    // open users selector dialog fragment
    @SuppressLint("CommitTransaction")
    private void openUsersSelector() {
        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new MultiUserSelectorDialog();
        bundle.putSerializable(Actions_.MY_FRIENDS, friendsArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.MY_FRIENDS);
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
    // open team selector dialog fragment
    @SuppressLint("CommitTransaction")
    private void openTeamSelector() {
        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new MultiTeamSelectorDialog();
        bundle.putSerializable(General.TEAM_LIST, teamsArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), General.TEAM_LIST);
    }

    @Override
    public void selectedTeams(ArrayList<Teams_> teams_arrayList, boolean isSelected) {

    }

    @Override
    public void selectedUsers(ArrayList<Friends_> users_arrayList, String id, boolean isSelected) {

    }
}