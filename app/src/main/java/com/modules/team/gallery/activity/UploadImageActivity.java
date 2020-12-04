package com.modules.team.gallery.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.modules.team.gallery.adapter.GalleryMultipleImageAdapter;
import com.modules.team.gallery.model.MultipleImage;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.FileUpload;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Images_;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.TeamDetails_;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class UploadImageActivity extends AppCompatActivity implements GalleryMultipleImageAdapter.SelectedDeleteImageAdapterListener {
    private Toolbar toolbar;
    private static final String TAG = UploadImageActivity.class.getSimpleName();
    private ArrayList<Images_> galleryArrayList;
    private TextView countImg, albumNameTxt;
    private AppCompatImageView uploadIcon;
    private EditText albumName, description;
    private AppCompatImageView createAlbum;
    private RelativeLayout countLayout;
    private int id;
    private String albumTitle, msg;
    private RecyclerView recyclerView;
    private ArrayList<Uri> selectedImages = new ArrayList<>();
    private ArrayList<String> fileID = new ArrayList<>();
    private ArrayList<MultipleImage> imagesArrayList = new ArrayList<>();
    private ArrayList<String> covetImgSelectedId = new ArrayList<String>();
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private GalleryMultipleImageAdapter galleryMultipleImageAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(UploadImageActivity.this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_upload_image);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

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
        titleText.setText("Upload Images");

        Intent data = getIntent();
        if (data.hasExtra(General.ID_UPLOAD)) {
            id = data.getIntExtra(General.ID_UPLOAD, 0);
            albumTitle = data.getStringExtra(General.TITLE);
        } else {
            onBackPressed();
        }

        createAlbum = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        createAlbum.setVisibility(View.VISIBLE);

        createAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryValidation(v)) {
                    uploadGallery();
                    finish();
                }
            }
        });

        galleryArrayList = new ArrayList<>();

        countLayout = findViewById(R.id.count_layout);
        countImg = (TextView) findViewById(R.id.txt_count);
        albumNameTxt = (TextView) findViewById(R.id.album_txt);
        uploadIcon = (AppCompatImageView) findViewById(R.id.upload_img_gallery);
        albumName = findViewById(R.id.album_name);
        description = findViewById(R.id.description);

        recyclerView = (RecyclerView) findViewById(R.id.gallery_upload_multiple_image_list);
        mLayoutManager = new GridLayoutManager(UploadImageActivity.this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        albumNameTxt.setText(albumTitle);

        uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imagesArrayList.size() == 0) {
                    selectedImages.clear();
                } else {
                    selectedImages.clear();
                }

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        try {
            if (imageData.getClipData() != null) {
                ClipData mClipData = imageData.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri tempUri = item.getUri();
                    if (selectedImages.size() < 10) {
                        selectedImages.add(tempUri);
                    } else {
                        Toast.makeText(this, "You can add upto 10 Photos", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (imageData.getData() != null) {
                Uri tempUri = imageData.getData();
                selectedImages.add(tempUri);
            }

            if (selectedImages.size() > 0) {

                if (selectedImages.size() >= 1 && selectedImages.size() < 10) {
                    uploadIcon.setClickable(true);
                } else {
                    uploadIcon.setClickable(false);
                }

                countLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                for (int i = 0; i < selectedImages.size(); i++) {
                    try {
                        new UploadFile(i).execute(getRealPathFromURI(this, selectedImages.get(i)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            // Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, imageData);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadFile extends AsyncTask<String, Void, Integer> {
        ShowLoader showLoader;
        int position;
        String path;

        public UploadFile(int i) {
            position = i;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader = new ShowLoader();
            showLoader.showUploadDialog(UploadImageActivity.this);
        }

        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(String... params) {
            path = params[0];
            int status = 12;
            String file_name = FileOperations.getFileName(path);
            String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER;
            String user_id = Preferences.get(General.USER_ID);
            try {
                String response = FileUpload.uploadFile(path, file_name, user_id, url,
                        Actions_.FMS, getApplicationContext(), UploadImageActivity.this);
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

                                    if (imagesArrayList.size() == 0) {
                                        fileID.add(String.valueOf(object.get(General.ID).getAsLong()));
                                        MultipleImage multipleImage = new MultipleImage();
                                        multipleImage.setFile(path);
                                        multipleImage.setId(object.get(General.ID).getAsLong());
                                        multipleImage.setMsg(object.get(General.MSG).getAsString());
                                        multipleImage.setStatus(object.get(General.STATUS).getAsInt());
                                        multipleImage.setPosition(position);
                                        imagesArrayList.add(multipleImage);
                                    } else if (imagesArrayList.size() >= 1 && imagesArrayList.size() < 10) {
                                        fileID.add(String.valueOf(object.get(General.ID).getAsLong()));
                                        MultipleImage multipleImage = new MultipleImage();
                                        multipleImage.setFile(path);
                                        multipleImage.setId(object.get(General.ID).getAsLong());
                                        multipleImage.setMsg(object.get(General.MSG).getAsString());
                                        multipleImage.setStatus(object.get(General.STATUS).getAsInt());
                                        multipleImage.setPosition(position);
                                        imagesArrayList.add(multipleImage);

                                        uploadIcon.setClickable(false);
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
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return status;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (showLoader != null)
                showLoader.dismissUploadDialog();

            switch (result) {
                case 1:

                    if (imagesArrayList.size() >= 1 && imagesArrayList.size() <= 10) {
                        countImg.setText("(" + imagesArrayList.size() + ")");

                        galleryMultipleImageAdapter = new GalleryMultipleImageAdapter(UploadImageActivity.this, 0, imagesArrayList, UploadImageActivity.this);
                        recyclerView.setAdapter(galleryMultipleImageAdapter);
                    }

                    countImg.setText("(" + imagesArrayList.size() + ")");

                    ShowToast.toast(getApplicationContext().getResources().
                            getString(R.string.upload_successful), getApplicationContext());
                    break;
                case 2:
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.failed), getApplicationContext());
                    break;
                case 11:
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.internal_error_occurred), getApplicationContext());
                    break;
                case 12:
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.network_error_occurred), getApplicationContext());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onItemClicked(int position) {
        new RemoveAttachment(position).execute();
    }

    // background call to remove uploaded file
    @SuppressLint("StaticFieldLeak")
    private class RemoveAttachment extends AsyncTask<Void, Void, Void> {
        final int position;

        RemoveAttachment(int position) {
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... params) {
            RequestBody getBody = new FormBody.Builder()
                    .add(General.ACTION, Actions_.DELETE)
                    .add(General.USER_ID, Preferences.get(General.USER_ID))
                    .add(General.ID, getCoverId())
                    .build();
            try {
                MakeCall.postNormal(Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER, getBody, TAG, UploadImageActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            removeAttachmentId(position);

            ShowToast.toast("Removed", UploadImageActivity.this);
            galleryMultipleImageAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("SetTextI18n")
    private void removeAttachmentId(int position) {
        int i = 0;
        if (imagesArrayList.size() > 0) {
            for (MultipleImage multipleImage : imagesArrayList) {
                if (multipleImage.isSelectImgs()) {
                    imagesArrayList.remove(position);
                    fileID.remove(i);

                    galleryMultipleImageAdapter.notifyDataSetChanged();

                    if (imagesArrayList.size() == 0) {
                        countLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        countImg.setText("(" + imagesArrayList.size() + ")");

                        if (imagesArrayList.size() >= 1 && imagesArrayList.size() < 10) {
                            uploadIcon.setClickable(true);
                        } else {
                            uploadIcon.setClickable(false);
                        }
                    }

                    break;
                }
                i++;
            }
        }
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

    private void uploadGallery() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.UPLOAD_GALLERY);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.TITLE, albumName.getText().toString().trim());
        requestMap.put(General.CAPTION, description.getText().toString().trim());
        requestMap.put(General.FILE_ID, getFileIds());
        requestMap.put(General.ALBUM_ID, String.valueOf(id));

        String url = Preferences.get(General.DOMAIN) + Urls_.UPLOAD_GALLERY_IMAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    galleryArrayList = TeamDetails_.parseUploadAlbumGallery(response, getApplicationContext(), TAG);
                    if (galleryArrayList.size() > 0) {
                        if (galleryArrayList.get(0).getStatus() == 1) {
                            msg = galleryArrayList.get(0).getMessage();
                            Toast.makeText(UploadImageActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getCoverId() {
        covetImgSelectedId.clear();
        if (imagesArrayList != null && imagesArrayList.size() > 0) {
            for (int i = 0; i < imagesArrayList.size(); i++) {
                if (imagesArrayList.get(i).isSelectImgs()) {
                    covetImgSelectedId.add(String.valueOf(imagesArrayList.get(i).getId()));
                }
            }
        }
        return covetImgSelectedId.toString()
                .replace("[", "")
                .replace("]", "").trim();
    }

    private String getFileIds() {
        StringBuilder strFileId = new StringBuilder();
        strFileId.setLength(0);
        if (fileID != null && fileID.size() > 0) {
            for (int i = 0; i < fileID.size(); i++) {
                if (i == fileID.size()) {
                    strFileId.append(fileID.get(i));
                } else {
                    strFileId.append(fileID.get(i));
                    strFileId.append(",");
                }
            }
        }
        return strFileId.toString().replace("[", "")
                .replace("]", "").trim();
    }

    private boolean galleryValidation(View view) {
        String title = albumName.getText().toString().trim();

        if (title == null || title.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Enter title of album", getApplicationContext());
            return false;
        }

        return true;
    }
}

