package com.modules.teamtalk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.teamtalk.adapter.TeamTalkCommentsAdapter;
import com.modules.teamtalk.model.ChildComments;
import com.modules.teamtalk.model.Comments_;
import com.modules.teamtalk.model.TeamTalk_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformDeleteTask;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.database.constants.TableList_;
import com.storage.database.operations.DatabaseDeleteRecord_;
import com.storage.database.operations.DatabaseUpdate_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Kailash Karankal
 */

public class TalkDetailsActivity extends AppCompatActivity implements View.OnClickListener, TeamTalkCommentsAdapter.TeamTalkCommentsAdapterListener {
    private static final String TAG = TalkDetailsActivity.class.getSimpleName();
    private static ArrayList<Comments_> commentsList = new ArrayList<>();
    private ArrayList<Comments_> replyCommentsList;
    private ArrayList<ChildComments> childComments = new ArrayList<>();
    private RecyclerView recyclerView;
    private static TeamTalkCommentsAdapter teamTalkCommentsAdapter;
    private TeamTalk_ teamTalk_;
    private Toolbar toolbar;
    private AppCompatImageButton menuButton;
    private static EditText commentBox;
    private Button postCommentBtn;
    private TextView commentPost;
    private String msg;
    private TextView commentTitle, comment, dateText;
    private TextView nameText, descriptionText, titleText;
    private ImageView icon;
    private LinearLayoutManager mLinearLayoutManager;
    private int commentId;
    private static int CommnentID = 0;
    private static int CurrentParentID = 0;
    private Activity activity;
    private RelativeLayout careDetailsFooter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.talk_details_layout);

        activity = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

        TextView headerTitleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        headerTitleText.setText("");
        menuButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_menu);
        menuButton.setImageResource(R.drawable.vi_delete_white);
        menuButton.setVisibility(View.VISIBLE);
        menuButton.setOnClickListener(this);

        commentBox = findViewById(R.id.comment_message_box);
        postCommentBtn = findViewById(R.id.comment_send);
        commentPost = (TextView) findViewById(R.id.comment);

        careDetailsFooter = (RelativeLayout) findViewById(R.id.care_details_footer);

        recyclerView = (RecyclerView) findViewById(R.id.team_talk_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        Intent data = getIntent();
        if (data.hasExtra(Actions_.TEAM_TALK)) {
            teamTalk_ = (TeamTalk_) data.getSerializableExtra(Actions_.TEAM_TALK);
        } else {
            onBackPressed();
        }

        setHeaderData(teamTalk_);

        if (teamTalk_.getCount() > 0) {
            getTeamTalkComments(teamTalk_.getId());
        }

        if (teamTalk_.getIsDelete() == 1) {
            menuButton.setVisibility(View.VISIBLE);
        } else {
            menuButton.setVisibility(View.GONE);
        }

        /*if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            careDetailsFooter.setVisibility(View.GONE);
        }*/

        /*if(![AppManager isCurruntUserHasPermissionToTeamTalkActions] && self.objSelectedTeam.nModeratorId != [AppManager sharedManager].curruntLoginUser.nUserId && self.objSelectedTeam.nOwnerId != [AppManager sharedManager].curruntLoginUser.nUserId){

            hide botton text field for comment & send button
        }*/

        if (!General.isCurruntUserHasPermissionToTeamTalkActions() &&
                Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("0") &&
                !Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))){
            careDetailsFooter.setVisibility(View.GONE);
            commentPost.setClickable(false);
        }else {
            careDetailsFooter.setVisibility(View.VISIBLE);
            commentPost.setClickable(true);
        }


        setClickListeners();
    }

    // set values to respective fields from received data
    private void setHeaderData(TeamTalk_ teamTalk_) {
        commentTitle = (TextView) findViewById(R.id.comment_title);
        commentTitle.setText(teamTalk_.getTeamName());
        nameText = (TextView) findViewById(R.id.talk_details_name);
        nameText.setText(ChangeCase.toTitleCase(teamTalk_.getFullName()));
        titleText = (TextView) findViewById(R.id.talk_details_title);
        titleText.setText(teamTalk_.getTitle());
        descriptionText = (TextView) findViewById(R.id.talk_details_description);
        descriptionText.setText(teamTalk_.getMessage());
        dateText = (TextView) findViewById(R.id.talk_details_time);
        dateText.setText(GetTime.getTodayEeeMm(teamTalk_.getDate()));
        icon = (ImageView) findViewById(R.id.talk_details_image);
        comment = (TextView) findViewById(R.id.talk_details_comment);

        Glide.with(getApplicationContext()).load(teamTalk_.getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(teamTalk_.getImage()))
                        .transform(new CircleTransform(getApplicationContext())))
                .into(icon);

        String commentMessage = null;
        if (teamTalk_.getCount() > 0) {
            if (teamTalk_.getCount() == 1) {
                commentMessage = getApplicationContext().getResources().getString(R.string.comment) + " " + "(" + teamTalk_.getCount() + ")";
            } else {
                commentMessage = getApplicationContext().getResources().getString(R.string.comments) + " " + "(" + GetCounters.convertCounter(teamTalk_.getCount()) + ")";
            }
        }

        comment.setText(commentMessage);
    }

    private void setClickListeners() {
        commentPost.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                commentBox.requestFocus();
                CommnentID = 0;
            }
        });

        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentTeamTalkValidation(v, commentBox)) {
                    String commentValue = commentBox.getText().toString().trim();
                    if (CommnentID == 0) {
                        commentTeamTalkWebService(commentValue);
                    } else {
                        replyCommentWebService(CommnentID, commentValue);
                    }
                }
            }
        });
    }

    // make network call to fetch comments respective to team talk
    private void getTeamTalkComments(int talk_id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TEAM_TALK);
        requestMap.put("thread_id", "" + talk_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.TEAM_TALK_DETAILS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    commentsList = Alerts_.parseTalkComment(response, getApplicationContext(), TAG);

                    if (commentsList.size() > 0) {
                        if (commentsList.get(0).getStatus() == 1) {

                            teamTalkCommentsAdapter = new TeamTalkCommentsAdapter(this, commentsList, this);
                            recyclerView.setAdapter(teamTalkCommentsAdapter);

                            resetCommentCount(commentsList);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void commentTeamTalkWebService(String commentValue) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.COMMENT_TEAM_TALK);
        requestMap.put(General.MSG_ID, String.valueOf(teamTalk_.getId()));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.COMMENT, commentValue);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.TEAM_TALK_DETAILS_URL;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.COMMENT_TEAM_TALK);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Comments_>>() {
                        }.getType();

                        commentsList = gson.fromJson(GetJson_.getArray(response, Actions_.COMMENT_TEAM_TALK).toString(), listType);

                        if (commentsList.get(0).getStatus() == 1) {

                            if (commentsList.size() == 0) {
                                comment.setText("Comments (" + commentsList.get(0).getComment_cnt() + ")");
                            } else {
                                comment.setText("Comments (" + commentsList.get(0).getComment_cnt() + ")");
                            }

                            getTeamTalkComments(teamTalk_.getId());
                            commentBox.setText("");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void replyCommentWebService(int commentId, String commentValue) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.REPLY_COMMENT);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.COMMENT_ID, String.valueOf(commentId));
        requestMap.put(General.COMMENT, commentValue);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.TEAM_TALK_DETAILS_URL;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.REPLY_COMMENT);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Comments_>>() {
                        }.getType();

                        replyCommentsList = gson.fromJson(GetJson_.getArray(response, Actions_.REPLY_COMMENT).toString(), listType);

                        if (replyCommentsList.get(0).getStatus() == 1) {
                            if (replyCommentsList.size() > 0) {
                                Toast.makeText(TalkDetailsActivity.this, "Reply Successful", Toast.LENGTH_LONG).show();
                                commentBox.setText("");
                                getTeamTalkComments(teamTalk_.getId());
                                replyCommentDataWebService(commentId, 0);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void replyCommentDataWebService(int commentId, int parentID) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TEAM_TALK_COMMENT);
        requestMap.put(General.COMMENT_ID, String.valueOf(commentId));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.TEAM_TALK_DETAILS_URL;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray1 = jsonObject.getJSONArray(Actions_.TEAM_TALK_COMMENT);

                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.TEAM_TALK_COMMENT);

                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<ChildComments>>() {
                        }.getType();

                        for (int i = 0; i < commentsList.size(); i++) {

                            if (CurrentParentID == 0) {
                                if (commentsList.get(i).getId() == commentId) {
                                    commentsList.get(i).setChildComments((ArrayList<ChildComments>) gson.fromJson(GetJson_.getArray(response, Actions_.TEAM_TALK_COMMENT).toString(), listType));

                                    if (commentsList.get(i).getChildComments().size() > 0) {
                                        if (commentsList.get(i).getChildComments().size() >= 0 && commentsList.get(i).getChildComments().get(0).getStatus() == 1) {

                                            teamTalkCommentsAdapter = new TeamTalkCommentsAdapter(this, commentsList, this);
                                            recyclerView.setAdapter(teamTalkCommentsAdapter);
                                            teamTalkCommentsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            } else {
                                if (commentsList.get(i).getId() == CurrentParentID) {
                                    if (commentsList.get(i).getChildComments().size() == 0) {
                                        commentsList.get(i).setChildComments((ArrayList<ChildComments>) gson.fromJson(GetJson_.getArray(response, Actions_.TEAM_TALK_COMMENT).toString(), listType));
                                    } else {
                                        for (int j = 0; j < jsonArray1.length(); j++) {
                                            JSONObject object = jsonArray1.getJSONObject(j);
                                            ChildComments childComments = new ChildComments();
                                            childComments.setId(Integer.parseInt(object.getString("id")));
                                            childComments.setComment_cnt(Integer.parseInt(object.getString("comment_cnt")));
                                            childComments.setMessage(object.getString("message"));
                                            childComments.setUser_id(Long.parseLong(object.getString("user_id")));
                                            childComments.setPosted_on(Long.parseLong(object.getString("posted_on")));
                                            childComments.setFull_name(object.getString("full_name"));
                                            childComments.setImage(object.getString("profile_pic"));
                                            childComments.setChild_cnt(Integer.parseInt(object.getString("child_cnt")));
                                            childComments.setStatus(Integer.parseInt(object.getString("status")));
                                            childComments.setSelectMainLayout(true);
                                            childComments.setHighlight(false);
                                            commentsList.get(i).getChildComments().add(childComments);
                                        }
                                    }

                                    if (commentsList.get(i).getChildComments().size() > 0) {
                                        if (commentsList.get(i).getChildComments().size() >= 0 && commentsList.get(i).getChildComments().get(0).getStatus() == 1) {

                                            teamTalkCommentsAdapter = new TeamTalkCommentsAdapter(this, commentsList, this);
                                            recyclerView.setAdapter(teamTalkCommentsAdapter);
                                            teamTalkCommentsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteComment(int commentId, int parentID) {
        deleteReplyTeamCommentWebService(commentId, parentID);
    }

    public void deleteReplyTeamCommentWebService(int commentId, int currentParentID) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_COMMENT);
        requestMap.put(General.COMMENT_ID, String.valueOf(commentId));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.TEAM_TALK_DETAILS_URL;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.DELETE_COMMENT);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Comments_>>() {
                        }.getType();

                        replyCommentsList = gson.fromJson(GetJson_.getArray(response, Actions_.DELETE_COMMENT).toString(), listType);

                        if (replyCommentsList.get(0).getStatus() == 1) {
                            if (replyCommentsList.size() > 0) {
                                msg = replyCommentsList.get(0).getMsg();

                                if (currentParentID == 0) {
                                    comment.setText("Comments (" + replyCommentsList.get(0).getComment_cnt() + ")");
                                }

                                for (int i = 0; i < commentsList.size(); i++) {
                                    if (currentParentID == 0) {
                                        if (commentsList.size() != 0) {
                                            for (int j = 0; j < commentsList.size(); j++) {
                                                if (commentsList.get(j).getId() == commentId) {
                                                    commentsList.remove(j);
                                                }
                                            }
                                        }
                                        if (commentsList.size() > 0) {
                                            if (commentsList.size() >= 0 && commentsList.get(i).getStatus() == 1) {
                                                teamTalkCommentsAdapter = new TeamTalkCommentsAdapter(this, commentsList, this);
                                                recyclerView.setAdapter(teamTalkCommentsAdapter);
                                            }
                                        }

                                    } else {
                                        if (commentsList.get(i).getId() == currentParentID) {
                                            if (commentsList.get(i).getChildComments().size() != 0) {
                                                for (int j = 0; j < commentsList.get(i).getChildComments().size(); j++) {
                                                    if (commentsList.get(i).getChildComments().get(j).getId() == commentId) {
                                                        commentsList.get(i).getChildComments().remove(j);
                                                    }
                                                }
                                            }
                                            if (commentsList.get(i).getChildComments().size() > 0) {
                                                if (commentsList.get(i).getChildComments().size() >= 0 && commentsList.get(i).getChildComments().get(0).getStatus() == 1) {
                                                    teamTalkCommentsAdapter = new TeamTalkCommentsAdapter(this, commentsList, this);
                                                    recyclerView.setAdapter(teamTalkCommentsAdapter);
                                                }
                                            }
                                        }
                                    }
                                }
                                Toast.makeText(TalkDetailsActivity.this, msg, Toast.LENGTH_LONG).show();
                                teamTalkCommentsAdapter.notifyDataSetChanged();
                                getTeamTalkComments(teamTalk_.getId());
                                CommnentID = 0;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void resetCommentCount(ArrayList<Comments_> commentsList) {
        if (commentsList.size() == 0) {
            comment.setVisibility(View.GONE);
        } else {
            comment.setText("Comments (" + commentsList.size() + ")");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mail_details_recipient_button:
                break;
            case R.id.imagebutton_activitytoolbar_menu:
                if (teamTalk_.getIsDelete() == 1) {
                    deleteConfirmation();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        // Make network call to read entry
        int status = PerformReadTask.readAlert("" + teamTalk_.getId(), General.TEAM_TALK, TAG, getApplicationContext(), this);
        if (status == 1) {
            // Update record for read/unread
            DatabaseUpdate_ databaseUpdate_ = new DatabaseUpdate_(getApplicationContext());
            databaseUpdate_.updateRead(General.IS_READ, TableList_.TABLE_TEAM_TALK, "1", "" + teamTalk_.getId());
        }
    }

    private void showResponses(int status, View view) {
        String message = "";
        if (status == 1) {
            message = getApplicationContext().getResources().getString(R.string.successful);
        } else if (status == 3) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }

    // delete record from local database
    private void deleteRecord(View view) {
        DatabaseDeleteRecord_ databaseDeleteRecord_ = new DatabaseDeleteRecord_(getApplicationContext());
        String id = "" + teamTalk_.getId();
        String is_delete = "" + teamTalk_.getIsDelete();
        String alert_id = "0";
        int response = PerformDeleteTask.deleteAlert(id, Actions_.TEAM_TALK, is_delete,alert_id, TAG, getApplicationContext(), this);
        if (response == 1) {
            databaseDeleteRecord_.deleteRecord(TableList_.TABLE_TEAM_TALK, "" + teamTalk_.getId(), General.ID);
        }
        showResponses(response, view);
    }

    //open delete confirmation dialog box
    private void deleteConfirmation() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
            subTitle.setText(this.getResources().getString(R.string.team_discussion_delete_confirmation));
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
            subTitle.setText(this.getResources().getString(R.string.team_talk_delete_confirmation));
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage006)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage008))) {
            subTitle.setText(this.getResources().getString(R.string.team_talk_delete_confirmation));
        }

        title.setText(this.getResources().getString(R.string.action_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(v);
                dialog.dismiss();
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

    private boolean commentTeamTalkValidation(View view, EditText commentTxt) {
        String comment = commentTxt.getText().toString().trim();

        if (comment == null || comment.trim().length() <= 3) {
            ShowSnack.viewWarning(view, "Comment: Min 3 char required", getApplicationContext());
            return false;
        }
        return true;
    }

    @Override
    public void onReplyItemClicked(int commentId) {
        commentBox.requestFocus();
        CommnentID = commentId;
    }

    public static void childOnItemClicked(int commentId, int currentParentID) {
        commentBox.requestFocus();
        CommnentID = commentId;
        CurrentParentID = currentParentID;
    }


    @Override
    public void repliesDataItemClicked(int commentId, int parentID) {
        CommnentID = commentId;
        CurrentParentID = parentID;
        replyCommentDataWebService(commentId, parentID);
    }

    @Override
    public void highLightReplyLayout(int commentId, int parentID) {
        for (int i = 0; i < commentsList.size(); i++) {

            if (commentsList.size() != 0) {

                for (int j = 0; j < commentsList.get(i).getChildComments().size(); j++) {

                    if (commentsList.get(i).getChildComments().size() != 0) {

                        if (commentId == commentsList.get(i).getChildComments().get(j).getId()) {
                            if (parentID == commentsList.get(i).getChildComments().get(j).getId()) {
                                commentsList.get(i).getChildComments().get(j).setHighlight(true);
                                teamTalkCommentsAdapter.notifyDataSetChanged();
                            }

                            if (commentId == commentsList.get(i).getChildComments().get(j).getId()) {
                                commentsList.get(i).getChildComments().get(j).setHighlight(true);
                                teamTalkCommentsAdapter.notifyDataSetChanged();
                            }

                            if (parentID == commentsList.get(i).getId()) {
                                commentsList.get(i).setHighlight(true);
                                teamTalkCommentsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }
    }
}
