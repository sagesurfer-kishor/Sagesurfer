package com.modules.team.gallery.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Images_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.TeamDetails_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 7/11/2019.
 */

public class CommentGalleryActivity extends AppCompatActivity {
    private static final String TAG = CommentGalleryActivity.class.getSimpleName();
    private Toolbar toolbar;
    private String imageIds, imageURL, imageTitle, imageDesc;
    private ImageView commentGalleryImg;
    private TextView commentTitle, loginUserName, commentAlbumName, commentAlbumDesc, commentCount;
    private EditText commentBox;
    private Button commentBtn;
    private ArrayList<Images_> commentArrayList = new ArrayList<>();
    private String msg;
    private ViewGroup viewGroup;
    private RelativeLayout mCareDetailsFooter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(CommentGalleryActivity.this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_comment_gallery);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        hideKeyBoard();

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
        titleText.setPadding(130, 0, 0, 0);
        titleText.setText("Comment");

        commentGalleryImg = findViewById(R.id.care_details_image_view);
        commentTitle = findViewById(R.id.comment_title);
        loginUserName = findViewById(R.id.login_user_name);
        commentAlbumName = findViewById(R.id.album_name);
        commentAlbumDesc = findViewById(R.id.album_description);
        commentCount = findViewById(R.id.comments_count_txt);
        commentBox = findViewById(R.id.comment_message_box);
        commentBtn = findViewById(R.id.comment_send);
        viewGroup = (ViewGroup) findViewById(R.id.comment_layout);

        Intent data = getIntent();
        if (data.hasExtra("image_ids")) {
            imageIds = data.getStringExtra("image_ids");
            imageURL = data.getStringExtra("image_url");
            imageTitle = data.getStringExtra("image_name");
            imageDesc = data.getStringExtra("image_description");
        } else {
            onBackPressed();
        }

        if (General.isCurruntUserHasPermissionToOnlyViewCantPerformAnyAction()) {

            if (!Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                    && !Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")) {
                mCareDetailsFooter.setVisibility(View.GONE);
            } else {
                mCareDetailsFooter.setVisibility(View.VISIBLE);
            }
        }

        applyCommentImage(imageURL, imageTitle);

        setClickListener();
    }

    private void hideKeyBoard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void applyCommentImage(final String imageUrl, String imageTitle) {
        Glide.with(getApplicationContext())
                .load(imageUrl)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop())
                .into(commentGalleryImg);

        commentTitle.setText(imageTitle);
        loginUserName.setText(Preferences.get(General.FIRST_NAME) + " " + Preferences.get(General.LAST_NAME));
        commentAlbumName.setText(imageTitle);
        commentAlbumDesc.setText(imageDesc);
    }

    private void setClickListener() {
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentGalleryValidation(v, commentBox)) {
                    String commentValue = commentBox.getText().toString().trim();
                    // calling web service comment gallery image
                    commentImageGalleryWebService(commentValue);
                    hideKeyBoard();
                }
            }
        });
    }

    private void commentImageGalleryWebService(String commentValue) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.COMMENT);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.IMAGE_ID, imageIds);
        requestMap.put(General.COMMENT, commentValue);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.COMMENT_GALLERY_IMAGE;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.COMMENT);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Images_>>() {
                        }.getType();

                        commentArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.COMMENT).toString(), listType);

                        if (commentArrayList.get(0).getStatus() == 1) {
                            if (commentArrayList.size() > 0) {
                                msg = commentArrayList.get(0).getMessage();
                                Toast toast = Toast.makeText(CommentGalleryActivity.this, msg, Toast.LENGTH_LONG);
                                toast.show();
                            }

                            getCommentWebServiceData();
                            commentBox.setText("");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        getCommentWebServiceData();
    }

    private void getCommentWebServiceData() {
        commentArrayList.clear();

        viewGroup.removeAllViews();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_COMMENTS);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.IMAGE_ID, imageIds);
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.COMMENT_GALLERY_IMAGE;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    commentArrayList = TeamDetails_.parseImagesComments(response, Actions_.GET_COMMENTS, getApplicationContext(), TAG);
                    int i = 0;
                    if (commentArrayList.size() > 0) {
                        if (commentArrayList.get(0).getStatus() == 1) {
                            for (Images_ comment : commentArrayList) {
                                int commentId = commentArrayList.get(i).getId();
                                addCommentLayout(comment, i, commentId);
                                i++;
                            }

                            resetCommentCount(commentArrayList);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // add dynamic comment layout row to layout
    private void addCommentLayout(Images_ comments_, int position, final int commentId) {
        View view = LayoutInflater.from(this).inflate(R.layout.gallery_comment_item, viewGroup, false);
        TextView name = (TextView) view.findViewById(R.id.textview_wallcommentitem_name);
        TextView time = (TextView) view.findViewById(R.id.textview_wallcommentitem_time);
        TextView message = (TextView) view.findViewById(R.id.textview_wallcommentitem_text);
        ImageView icon = (ImageView) view.findViewById(R.id.imageview_wallcommentitem_image);
        final TextView deleteComment = (TextView) view.findViewById(R.id.delete_comment);

        deleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCommentWebService(commentId);
            }
        });

        Glide.with(getApplicationContext()).load(comments_.getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(comments_.getImage()))
                        .transform(new CircleTransform(getApplicationContext())))
                .into(icon);
        message.setText(comments_.getComment());
        name.setText(comments_.getComment_by());
        time.setText(GetTime.wallTime(comments_.getComment_on()));
        viewGroup.addView(view);
    }

    private void deleteCommentWebService(int commentId) {
        commentArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_COMMENT);
        requestMap.put(General.COMMENT_ID, String.valueOf(commentId));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.DELETE_COMMENT;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.DELETE_COMMENT);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Images_>>() {
                        }.getType();

                        commentArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.DELETE_COMMENT).toString(), listType);

                        if (commentArrayList.get(0).getStatus() == 1) {
                            if (commentArrayList.size() > 0) {
                                msg = commentArrayList.get(0).getMsg();
                                Toast.makeText(CommentGalleryActivity.this, msg, Toast.LENGTH_LONG).show();
                            }

                            commentCount.setText("Comments (" + commentArrayList.get(0).getCount() + ")");

                            getCommentWebServiceData();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void resetCommentCount(ArrayList<Images_> commentArrayList) {
        if (commentArrayList.size() == 0) {
            commentCount.setText("Comments (" + commentArrayList.size() + ")");
        } else {
            commentCount.setText("Comments (" + commentArrayList.size() + ")");
        }
    }

    private boolean commentGalleryValidation(View view, EditText commentTxt) {
        String comment = commentTxt.getText().toString().trim();

        if (comment == null || comment.trim().length() <= 3) {
            ShowSnack.viewWarning(view, "Comment: Min 3 char required", getApplicationContext());
            return false;
        }
        return true;
    }

}
