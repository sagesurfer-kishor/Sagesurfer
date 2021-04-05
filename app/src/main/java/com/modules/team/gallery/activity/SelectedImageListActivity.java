package com.modules.team.gallery.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
 import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.team.gallery.adapter.SelectedImageAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Images_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.TeamDetails_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

public class SelectedImageListActivity extends AppCompatActivity implements SelectedImageAdapter.SelectedImageAdapterListener {
    private static final String TAG = SelectedImageListActivity.class.getSimpleName();
    private PopupWindow popupWindow = new PopupWindow();
    private int id;
    private String albumTitle;
    private static ArrayList<Images_> imagesArrayList = new ArrayList<>(), isCoverImgArray;
    private static ArrayList<Images_> likeUnlikeArrayList = new ArrayList<>();
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private RecyclerView recyclerViewAlbumImages;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView listTitle;
    private static TextView selectedCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private static ImageView commentAlbunImg, shareAlbumImg, coverAlbumImg, editAlbumImg, selectAlbumImg, unSelectAlbumImg, deleteAlbumImg;
    private String msg;
    private TextView titleText;
    private ArrayList<String> imageSelectedIds = new ArrayList<String>();
    private ArrayList<String> imageSelectedUrls = new ArrayList<String>();
    private ArrayList<String> imageSelectedName = new ArrayList<String>();
    private ArrayList<String> imageSelectedDesc = new ArrayList<String>();
    private AlertDialog alertDialog;
    private SelectedImageAdapter imageListAdapter;
    private String imageIds, imagePath;
    private RelativeLayout headerLayout;
    private LinearLayout functionsLayout;
    private int isModerator;
    private int isCC;
    private static long addedBy;
    private FloatingActionButton fb_uplodeImagesForAlbum;

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.selected_gallery_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        Preferences.initialize(getApplicationContext());

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

        titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        recyclerViewAlbumImages = (RecyclerView) findViewById(R.id.swipe_menu_listview);
        mLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewAlbumImages.setLayoutManager(mLayoutManager);
        recyclerViewAlbumImages.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerViewAlbumImages.setItemAnimator(new DefaultItemAnimator());

        errorText = (TextView) findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) findViewById(R.id.swipe_refresh_recycler_view_error_layout);
        listTitle = findViewById(R.id.list_view_title);
        selectedCount = findViewById(R.id.selected_count_txt);

        commentAlbunImg = findViewById(R.id.comment_album_img);
        shareAlbumImg = findViewById(R.id.share_album_img);
        coverAlbumImg = findViewById(R.id.cover_album_img);
        editAlbumImg = findViewById(R.id.edit_album_img);
        selectAlbumImg = findViewById(R.id.select_album_img);
        deleteAlbumImg = findViewById(R.id.delete_album_img);
        unSelectAlbumImg = findViewById(R.id.unselect_album_img);

        headerLayout = findViewById(R.id.header_layout);
        functionsLayout = findViewById(R.id.functional_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        Intent data = getIntent();
        if (data.hasExtra(General.ID)) {
            id = data.getIntExtra(General.ID, 0);
            albumTitle = data.getStringExtra(General.TITLE);
            addedBy = data.getLongExtra(General.ADDED_BY, 0);
            isModerator = data.getIntExtra(General.IS_MODERATOR, 0);
            isCC = data.getIntExtra(General.IS_CC, 0);
            titleText.setText(data.getStringExtra(General.TITLE));
            listTitle.setText(data.getStringExtra(General.TITLE));
        } else {
            onBackPressed();
        }

        fb_uplodeImagesForAlbum = findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        fb_uplodeImagesForAlbum.setImageResource(R.drawable.ic_add_white);

        fb_uplodeImagesForAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadAlbumIntent = new Intent(getApplicationContext(), AlbumImagesUplodeActivity.class);
                uploadAlbumIntent.putExtra(General.ID_UPLOAD, id);
                uploadAlbumIntent.putExtra(General.TITLE, albumTitle);
                startActivity(uploadAlbumIntent);
            }
        });

        setClickListeners();

        commentAlbunImg.setClickable(false);
        shareAlbumImg.setClickable(false);
        coverAlbumImg.setClickable(false);
        editAlbumImg.setClickable(false);
        deleteAlbumImg.setClickable(false);

        /*if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            commentAlbunImg.setEnabled(false);
            commentAlbunImg.setClickable(false);
            shareAlbumImg.setEnabled(false);
            shareAlbumImg.setClickable(false);
            createAlbum.setVisibility(View.GONE);
        } else {
            createAlbum.setVisibility(View.VISIBLE);
        }*/

        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")
                ||  !General.isCurruntUserHasPermissionToOnlyViewCantPerformAnyAction()) {

            fb_uplodeImagesForAlbum.setVisibility(View.VISIBLE);
        } else {
            commentAlbunImg.setEnabled(false);
            commentAlbunImg.setClickable(false);
            shareAlbumImg.setEnabled(false);
            shareAlbumImg.setClickable(false);
            fb_uplodeImagesForAlbum.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        // call gallery  web service
        getImages();
        // all images are un-clickable mode
        setClickableFalse();
    }

    private void setClickListeners() {
        commentAlbunImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentAlbumIntent = new Intent(getApplicationContext(), CommentGalleryActivity.class);
                imageIds = getImageIds();
                imagePath = getImageUrls();
                String imageName = getImageTitle();
                String imageDescription = getImageDescription();
                commentAlbumIntent.putExtra("image_ids", imageIds);
                commentAlbumIntent.putExtra("image_url", imagePath);
                commentAlbumIntent.putExtra("image_name", imageName);
                commentAlbumIntent.putExtra("image_description", imageDescription);
                startActivity(commentAlbumIntent);
                showSelectCount();
            }
        });

        shareAlbumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
                showSelectCount();
            }
        });

        coverAlbumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCoverImageGalleryWebService();
            }
        });

        editAlbumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater inflater = (LayoutInflater) SelectedImageListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View customView = inflater.inflate(R.layout.edit_image_dialog, null);
                    popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.isShowing();

                    String imageName = getImageTitle();
                    String imageDescription = getImageDescription();

                    final TextView albumName = customView.findViewById(R.id.edit_album_name);
                    final ImageView imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
                    final ImageView imageviewBack = customView.findViewById(R.id.imageview_back);
                    final EditText title = customView.findViewById(R.id.et_album_name);
                    final EditText description = customView.findViewById(R.id.et_description);

                    albumName.setText(imageName);
                    title.setText(imageName);
                    description.setText(imageDescription);

                    imageviewSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editGalleryValidation(v, title, description)) {
                                String titleValue = title.getText().toString().trim();
                                String descValue = description.getText().toString().trim();

                                // calling web service edit gallery image
                                editImageGalleryWebService(titleValue, descValue);

                                setClickableFalse();

                                showSelectCount();

                                popupWindow.dismiss();
                            }
                        }
                    });

                    imageviewBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        selectAlbumImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                selectAlbumImg.setVisibility(View.GONE);
                unSelectAlbumImg.setVisibility(View.VISIBLE);
                deleteAlbumImg.setImageResource(R.drawable.delete_img_blue);

                imagesArrayList = getSelectedAlbumImages(true);

                selectedCount.setText("Selected (" + imagesArrayList.size() + ")");

                deleteAlbumImg.setClickable(true);

                commentAlbunImg.setImageResource(R.drawable.comment_gray);
                commentAlbunImg.setClickable(false);
                shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                shareAlbumImg.setClickable(false);
                coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
                coverAlbumImg.setClickable(false);
                editAlbumImg.setImageResource(R.drawable.edit_img_gray);
                editAlbumImg.setClickable(false);
            }
        });

        unSelectAlbumImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                selectAlbumImg.setVisibility(View.VISIBLE);
                unSelectAlbumImg.setVisibility(View.GONE);
                deleteAlbumImg.setImageResource(R.drawable.delete_img_gray);

                imagesArrayList = getSelectedAlbumImages(false);

                showSelectCount();

                deleteAlbumImg.setClickable(false);
            }
        });


        deleteAlbumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup viewGroup = v.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(SelectedImageListActivity.this).inflate(R.layout.delete_gallery_popup, viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectedImageListActivity.this);
                builder.setView(dialogView);

                alertDialog = builder.create();

                final Button okBtn = dialogView.findViewById(R.id.ok_btn);
                final ImageView closeIcon = dialogView.findViewById(R.id.close_icon);
                final TextView message = dialogView.findViewById(R.id.message_txt);
                message.setText("Image(s) will be deleted from platform!");

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteGalleryImageWebService();
                        alertDialog.dismiss();
                    }
                });

                closeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }

    private ArrayList<Images_> getSelectedAlbumImages(boolean isSelect) {
        ArrayList<Images_> list = new ArrayList<>();
        for (int i = 0; i < imagesArrayList.size(); i++) {
            Images_ images_ = new Images_();
            images_.setSelectImgs(isSelect);
            images_.setComment(imagesArrayList.get(i).getComment());
            images_.setComment_by(imagesArrayList.get(i).getComment_by());
            images_.setComment_by_userid(imagesArrayList.get(i).getComment_by_userid());
            images_.setComment_on(imagesArrayList.get(i).getComment_on());
            images_.setCount(imagesArrayList.get(i).getCount());
            images_.setDescription(imagesArrayList.get(i).getDescription());
            images_.setFull_path(imagesArrayList.get(i).getFull_path());
            images_.setId(imagesArrayList.get(i).getId());
            images_.setImage(imagesArrayList.get(i).getImage());
            images_.setIs_cover(imagesArrayList.get(i).getIs_cover());
            images_.setIs_delete(imagesArrayList.get(i).getIs_delete());
            images_.setIs_like(imagesArrayList.get(i).getIs_like());
            images_.setMessage(imagesArrayList.get(i).getMessage());
            images_.setUsername(imagesArrayList.get(i).getUsername());
            images_.setMsg(imagesArrayList.get(i).getMsg());
            images_.setStatus(imagesArrayList.get(i).getStatus());
            images_.setTitle(imagesArrayList.get(i).getTitle());
            images_.setThumb_path(imagesArrayList.get(i).getThumb_path());
            images_.setTotal_like(imagesArrayList.get(i).getTotal_like());
            list.add(images_);
        }

        imageListAdapter = new SelectedImageAdapter(this, list, this);
        recyclerViewAlbumImages.setAdapter(imageListAdapter);

        return list;
    }

    public void showSelectCount() {
        selectedCount.setText("Selected (" + 0 + ")");
    }

    private boolean editGalleryValidation(View view, EditText titleTxt, EditText description) {
        String title = titleTxt.getText().toString().trim();

        if (title == null || title.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Enter title of album", getApplicationContext());
            return false;
        }
        return true;
    }

    private void shareImage() {
        imagePath = getImageUrls();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, imagePath);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

    // make network call to fetch images from server
    @SuppressLint("SetTextI18n")
    private void getImages() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_GALLERY_PHOTOS);
        requestMap.put(General.ID, "" + id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.GET_GALLERY_IMAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    imagesArrayList = TeamDetails_.parseImages(response, getApplicationContext(), TAG);
                    if (imagesArrayList.size() > 0) {
                        if (imagesArrayList.get(0).getStatus() == 1) {
                            imageListAdapter = new SelectedImageAdapter(this, imagesArrayList, this);
                            recyclerViewAlbumImages.setAdapter(imageListAdapter);
                            showError(false, imagesArrayList.get(0).getStatus());
                        } else {
                            showError(true, imagesArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 12);
                    }
                } else {
                    showError(true, 2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public static void showGalleryImages() {
        int selectedCountImg = 0, isCover = 0;
        long imgAddedBy = 0;
        for (int i = 0; i < imagesArrayList.size(); i++) {
            if (imagesArrayList.get(i).isSelectImgs()) {
                imgAddedBy = imagesArrayList.get(i).getAdded_by_id();
                isCover = imagesArrayList.get(i).getIs_cover();
                isCoverImgArray = imagesArrayList;
                selectedCountImg++;
            }
        }

        selectedCount.setText("Selected (" + selectedCountImg + ")");

        if (selectedCountImg == imagesArrayList.size()) {
            unSelectAlbumImg.setVisibility(View.GONE);
            selectAlbumImg.setVisibility(View.GONE);
        } else {
            unSelectAlbumImg.setVisibility(View.GONE);
            selectAlbumImg.setVisibility(View.VISIBLE);
        }

        if (selectedCountImg == 1) {

            if (Preferences.get(General.USER_ID).equalsIgnoreCase(Preferences.get(General.GROUP_OWNER_ID) )) {
                commentAlbunImg.setVisibility(View.VISIBLE);
                shareAlbumImg.setVisibility(View.VISIBLE);
                coverAlbumImg.setVisibility(View.VISIBLE);
                editAlbumImg.setVisibility(View.VISIBLE);
                selectAlbumImg.setVisibility(View.VISIBLE);
                deleteAlbumImg.setVisibility(View.VISIBLE);
                selectAlbumImg.setVisibility(View.VISIBLE);

                /*if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    commentAlbunImg.setImageResource(R.drawable.comment_gray);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                } else {
                    commentAlbunImg.setImageResource(R.drawable.comment_blue);
                    commentAlbunImg.setClickable(true);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_blue);
                    shareAlbumImg.setClickable(true);
                }*/

                if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                        || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")
                        ||  !General.isCurruntUserHasPermissionToOnlyViewCantPerformAnyAction()) {
                    commentAlbunImg.setImageResource(R.drawable.comment_blue);
                    commentAlbunImg.setClickable(true);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_blue);
                    shareAlbumImg.setClickable(true);
                }else{
                    commentAlbunImg.setImageResource(R.drawable.comment_gray);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                }

                if (isCover == 1) {
                    coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
                    coverAlbumImg.setClickable(false);
                } else {
                    coverAlbumImg.setImageResource(R.drawable.cover_img_blue);
                    coverAlbumImg.setClickable(true);
                }

                editAlbumImg.setImageResource(R.drawable.edit_img_blue);
                editAlbumImg.setClickable(true);
                selectAlbumImg.setImageResource(R.drawable.select_all_img_blue);
                deleteAlbumImg.setImageResource(R.drawable.delete_img_blue);
                deleteAlbumImg.setClickable(true);
            } else if (Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1") && imgAddedBy != Integer.parseInt(Preferences.get(General.GROUP_OWNER_ID))) {
                commentAlbunImg.setVisibility(View.VISIBLE);
                shareAlbumImg.setVisibility(View.VISIBLE);
                coverAlbumImg.setVisibility(View.VISIBLE);
                editAlbumImg.setVisibility(View.VISIBLE);
                selectAlbumImg.setVisibility(View.VISIBLE);
                deleteAlbumImg.setVisibility(View.VISIBLE);
                selectAlbumImg.setVisibility(View.VISIBLE);

                if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    commentAlbunImg.setImageResource(R.drawable.comment_gray);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                } else {
                    commentAlbunImg.setImageResource(R.drawable.comment_blue);
                    commentAlbunImg.setClickable(true);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_blue);
                    shareAlbumImg.setClickable(true);
                }

                if (isCover == 1) {
                    coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
                    coverAlbumImg.setClickable(false);
                } else {
                    coverAlbumImg.setImageResource(R.drawable.cover_img_blue);
                    coverAlbumImg.setClickable(true);
                }

                editAlbumImg.setImageResource(R.drawable.edit_img_blue);
                editAlbumImg.setClickable(true);
                selectAlbumImg.setImageResource(R.drawable.select_all_img_blue);
                deleteAlbumImg.setImageResource(R.drawable.delete_img_blue);
                deleteAlbumImg.setClickable(true);
            } else if (imgAddedBy == Integer.parseInt(Preferences.get(General.USER_ID))) {
                commentAlbunImg.setVisibility(View.VISIBLE);
                shareAlbumImg.setVisibility(View.VISIBLE);
                coverAlbumImg.setVisibility(View.VISIBLE);
                editAlbumImg.setVisibility(View.VISIBLE);
                selectAlbumImg.setVisibility(View.VISIBLE);
                deleteAlbumImg.setVisibility(View.VISIBLE);
                selectAlbumImg.setVisibility(View.VISIBLE);

                if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    commentAlbunImg.setImageResource(R.drawable.comment_gray);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                } else {
                    commentAlbunImg.setImageResource(R.drawable.comment_blue);
                    commentAlbunImg.setClickable(true);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_blue);
                    shareAlbumImg.setClickable(true);
                }


                if (isCover == 1) {
                    coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
                    coverAlbumImg.setClickable(false);
                } else {
                    coverAlbumImg.setImageResource(R.drawable.cover_img_blue);
                    coverAlbumImg.setClickable(true);
                }

                editAlbumImg.setImageResource(R.drawable.edit_img_blue);
                editAlbumImg.setClickable(true);
                selectAlbumImg.setImageResource(R.drawable.select_all_img_blue);
                deleteAlbumImg.setImageResource(R.drawable.delete_img_blue);
                deleteAlbumImg.setClickable(true);

            } else {
                commentAlbunImg.setVisibility(View.VISIBLE);
                shareAlbumImg.setVisibility(View.VISIBLE);
                selectAlbumImg.setVisibility(View.VISIBLE);

                if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    commentAlbunImg.setImageResource(R.drawable.comment_gray);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                } else {
                    commentAlbunImg.setImageResource(R.drawable.comment_blue);
                    commentAlbunImg.setClickable(true);
                    shareAlbumImg.setImageResource(R.drawable.sharing_img_blue);
                    shareAlbumImg.setClickable(true);
                }

                selectAlbumImg.setImageResource(R.drawable.select_all_img_blue);
                unSelectAlbumImg.setVisibility(View.GONE);
            }
        } else if (selectedCountImg >= 2) {
            if ((Integer.parseInt(Preferences.get(General.USER_ID)) == (Integer.parseInt(Preferences.get(General.GROUP_OWNER_ID))))) {
                commentAlbunImg.setImageResource(R.drawable.comment_gray);
                commentAlbunImg.setClickable(false);
                shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                shareAlbumImg.setClickable(false);
                coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
                coverAlbumImg.setClickable(false);
                editAlbumImg.setImageResource(R.drawable.edit_img_gray);
                editAlbumImg.setClickable(false);
                selectAlbumImg.setImageResource(R.drawable.select_all_img_blue);
                deleteAlbumImg.setImageResource(R.drawable.delete_img_blue);
                deleteAlbumImg.setClickable(true);
            } else if ((Integer.parseInt(Preferences.get(General.IS_MODERATOR)) == 1 && imgAddedBy != Integer.parseInt(Preferences.get(General.GROUP_OWNER_ID)))) {
                commentAlbunImg.setImageResource(R.drawable.comment_gray);
                commentAlbunImg.setClickable(false);
                shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                shareAlbumImg.setClickable(false);
                coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
                coverAlbumImg.setClickable(false);
                editAlbumImg.setImageResource(R.drawable.edit_img_gray);
                editAlbumImg.setClickable(false);
                selectAlbumImg.setImageResource(R.drawable.select_all_img_blue);
                deleteAlbumImg.setImageResource(R.drawable.delete_img_gray);
                deleteAlbumImg.setClickable(false);
            } else if (imgAddedBy == Integer.parseInt(Preferences.get(General.USER_ID))) {
                commentAlbunImg.setImageResource(R.drawable.comment_gray);
                commentAlbunImg.setClickable(false);
                shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                shareAlbumImg.setClickable(false);
                coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
                coverAlbumImg.setClickable(false);
                editAlbumImg.setImageResource(R.drawable.edit_img_gray);
                editAlbumImg.setClickable(false);
                selectAlbumImg.setImageResource(R.drawable.select_all_img_blue);
                deleteAlbumImg.setImageResource(R.drawable.delete_img_gray);
                deleteAlbumImg.setClickable(false);
            } else {
                commentAlbunImg.setImageResource(R.drawable.comment_gray);
                commentAlbunImg.setClickable(false);
                shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
                shareAlbumImg.setClickable(false);
                coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
                coverAlbumImg.setClickable(false);
                editAlbumImg.setImageResource(R.drawable.edit_img_gray);
                editAlbumImg.setClickable(false);
                selectAlbumImg.setImageResource(R.drawable.select_all_img_blue);
                deleteAlbumImg.setImageResource(R.drawable.delete_img_gray);
                deleteAlbumImg.setClickable(false);
            }
        } else if (selectedCountImg == 0) {
            setClickableFalse();
        }
    }

    private static void setClickableFalse() {
        commentAlbunImg.setImageResource(R.drawable.comment_gray);
        commentAlbunImg.setClickable(false);
        shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
        shareAlbumImg.setClickable(false);
        coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
        coverAlbumImg.setClickable(false);
        editAlbumImg.setImageResource(R.drawable.edit_img_gray);
        editAlbumImg.setClickable(false);
        deleteAlbumImg.setImageResource(R.drawable.delete_img_gray);
        deleteAlbumImg.setClickable(false);
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerViewAlbumImages.setVisibility(View.GONE);
            headerLayout.setVisibility(View.GONE);
            functionsLayout.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getGalleryMessage(status, getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerViewAlbumImages.setVisibility(View.VISIBLE);
            headerLayout.setVisibility(View.VISIBLE);
            functionsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(int position) {
        callingLikeUnlikeWebService();
    }


    private void callingLikeUnlikeWebService() {
        likeUnlikeArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GALLERY_LIKE);
        imageIds = getImageIds();
        requestMap.put(General.IMAGE_ID, imageIds);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.LIKE_GALLERY_IMAGE;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.GALLERY_LIKE);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Images_>>() {
                        }.getType();

                        likeUnlikeArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GALLERY_LIKE).toString(), listType);

                        if (likeUnlikeArrayList.size() > 0) {
                            msg = likeUnlikeArrayList.get(0).getMessage();
                            Toast.makeText(SelectedImageListActivity.this, msg, Toast.LENGTH_LONG).show();
                            getImages();
                            imageListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteGalleryImageWebService() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));
        imageIds = getImageIds();
        requestMap.put(General.IMAGE_ID, imageIds);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.DELETE_GALLERY_IMAGE;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.DELETE);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Images_>>() {
                        }.getType();

                        likeUnlikeArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.DELETE).toString(), listType);

                        if (likeUnlikeArrayList.size() > 0) {
                            msg = likeUnlikeArrayList.get(0).getMessage();
                            getImages();
                            setGrayColorImages();
                            showSelectCount();
                            Toast.makeText(SelectedImageListActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setCoverImageGalleryWebService() {
        likeUnlikeArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GALLERY_SET_COVER);
        requestMap.put(General.ALBUM_ID, String.valueOf(id));
        imageIds = getImageCoverIds();
        requestMap.put(General.IMAGE_ID, imageIds);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.COVER_GALLERY_IMAGE;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.GALLERY_SET_COVER);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Images_>>() {
                        }.getType();

                        likeUnlikeArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GALLERY_SET_COVER).toString(), listType);

                        if (likeUnlikeArrayList.size() > 0) {
                            if (likeUnlikeArrayList.get(0).getStatus() == 1) {
                                msg = likeUnlikeArrayList.get(0).getMessage();
                                setGrayColorImages();
                                showSelectCount();
                                imagesArrayList = getSelectedAlbumImages(false);
                                imageListAdapter.notifyDataSetChanged();
                                Toast.makeText(SelectedImageListActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void setGrayColorImages() {
        commentAlbunImg.setImageResource(R.drawable.comment_gray);
        shareAlbumImg.setImageResource(R.drawable.sharing_img_gray);
        coverAlbumImg.setImageResource(R.drawable.cover_img_gray);
        editAlbumImg.setImageResource(R.drawable.edit_img_gray);
        deleteAlbumImg.setImageResource(R.drawable.delete_img_gray);
    }

    private void editImageGalleryWebService(String titleValue, String descValue) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.EDIT);
        requestMap.put(General.TITLE, titleValue);
        requestMap.put(General.CAPTION, descValue);
        imageIds = getImageIds();
        requestMap.put(General.IMAGE_ID, imageIds);
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.EDIT_GALLERY_IMAGE;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.EDIT);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Images_>>() {
                        }.getType();

                        likeUnlikeArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.EDIT).toString(), listType);

                        if (likeUnlikeArrayList.size() > 0) {
                            msg = likeUnlikeArrayList.get(0).getMessage();
                            getImages();
                            imageListAdapter.notifyDataSetChanged();
                            Toast.makeText(SelectedImageListActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getImageCoverIds() {
        imageSelectedIds.clear();
        if (isCoverImgArray != null && isCoverImgArray.size() > 0) {
            for (int i = 0; i < isCoverImgArray.size(); i++) {
                if (isCoverImgArray.get(i).isSelectImgs()) {
                    imageSelectedIds.add(String.valueOf(isCoverImgArray.get(i).getId()));
                }
            }
        }
        return imageSelectedIds.toString()
                .replace("[", "")
                .replace("]", "").trim();
    }

    private String getImageIds() {
        imageSelectedIds.clear();
        if (imagesArrayList != null && imagesArrayList.size() > 0) {
            for (int i = 0; i < imagesArrayList.size(); i++) {
                if (imagesArrayList.get(i).isSelectImgs()) {
                    imageSelectedIds.add(String.valueOf(imagesArrayList.get(i).getId()));
                }
            }
        }
        return imageSelectedIds.toString()
                .replace("[", "")
                .replace("]", "").trim();
    }

    private String getImageUrls() {
        imageSelectedUrls.clear();
        if (imagesArrayList != null && imagesArrayList.size() > 0) {
            for (int i = 0; i < imagesArrayList.size(); i++) {
                if (imagesArrayList.get(i).isSelectImgs()) {
                    imageSelectedUrls.add(String.valueOf(imagesArrayList.get(i).getImage()));
                }
            }
        }
        return imageSelectedUrls.toString()
                .replace("[", "")
                .replace("]", "").trim();
    }

    private String getImageTitle() {
        imageSelectedName.clear();
        if (imagesArrayList != null && imagesArrayList.size() > 0) {
            for (int i = 0; i < imagesArrayList.size(); i++) {
                if (imagesArrayList.get(i).isSelectImgs()) {
                    imageSelectedName.add(String.valueOf(imagesArrayList.get(i).getTitle()));
                }
            }
        }
        return imageSelectedName.toString()
                .replace("[", "")
                .replace("]", "").trim();
    }

    private String getImageDescription() {
        imageSelectedDesc.clear();
        if (imagesArrayList != null && imagesArrayList.size() > 0) {
            for (int i = 0; i < imagesArrayList.size(); i++) {
                if (imagesArrayList.get(i).isSelectImgs()) {
                    imageSelectedDesc.add(String.valueOf(imagesArrayList.get(i).getDescription()));
                }
            }
        }
        return imageSelectedDesc.toString()
                .replace("[", "")
                .replace("]", "").trim();
    }

    // handle spacing and edge curves for card view
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    // convert dp to pixel(px)
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }
}
