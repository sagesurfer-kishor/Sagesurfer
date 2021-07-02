package com.modules.team.gallery.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
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
import com.sagesurfer.models.Gallery_;
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
/**
 * Created by Kailash Karankal on 7/11/2019.
 */
public class CreateGalleryActivity extends AppCompatActivity implements GalleryMultipleImageAdapter.SelectedDeleteImageAdapterListener {
    private static final int MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION = 21;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private GalleryMultipleImageAdapter galleryMultipleImageAdapter;
    private static final String TAG = CreateGalleryActivity.class.getSimpleName();
    private ArrayList<Gallery_> al_gallery;
    private TextView tv_countImg, tv_hintTxt;
    private AppCompatImageView btn_uploadImage;
    private EditText albumName, description;
    private AppCompatImageView btn_CreateAlbum;
    private RelativeLayout countLayout;
    private String msg;
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private ArrayList<Uri> selectedImages = new ArrayList<>();
    private ArrayList<String> fileID = new ArrayList<>();
    private ArrayList<MultipleImage> al_images = new ArrayList<>();
    private ArrayList<String> covetImgSelectedId = new ArrayList<String>();
    private RecyclerView.LayoutManager mLayoutManager;

    String coverid = "";

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        coverid = "";

        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(CreateGalleryActivity.this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.gallery_create_album);
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
        titleText.setText("Create Gallery");

        btn_CreateAlbum = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        btn_CreateAlbum.setVisibility(View.VISIBLE);

        btn_CreateAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryValidation(v)) {
                    createGallery();
                    finish();
                }
            }
        });

        al_gallery = new ArrayList<>();

        countLayout = findViewById(R.id.count_layout);
        tv_countImg = (TextView) findViewById(R.id.txt_count);
        btn_uploadImage = (AppCompatImageView) findViewById(R.id.btn_upload_img_gallery);
        albumName = findViewById(R.id.et_album_name);
        description = findViewById(R.id.et_description);
        tv_hintTxt = findViewById(R.id.hint_txt);

        recyclerView = (RecyclerView) findViewById(R.id.gallery_upload_multiple_image_list);
        mLayoutManager = new GridLayoutManager(CreateGalleryActivity.this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btn_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!al_images.isEmpty()){
                    al_images.clear();
                }
                /*if (imagesArrayList.size() == 0) {
                    selectedImages.clear();
                } else {
                    selectedImages.clear();
                }

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_MULTIPLE);*/
                checkUserPermissions();
            }
        });
    }

    public void checkUserPermissions() {
        if (ContextCompat.checkSelfPermission(CreateGalleryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateGalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new android.app.AlertDialog.Builder(this)
                        .setTitle("Storage permission required")
                        .setMessage("To upload image need to allow storage permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CreateGalleryActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(CreateGalleryActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION);
            }
        }else{
            selectImage();
        }
    }

    /*here we are creating file chooser
  this method is added by rahul */
    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    selectImage();

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(this, "Need storage permission..", Toast.LENGTH_SHORT).show();
                }
                return;
        }
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
                    btn_uploadImage.setClickable(true);
                } else {
                    btn_uploadImage.setClickable(false);
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
            //Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
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
            showLoader.showUploadDialog(CreateGalleryActivity.this);
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
                        Actions_.FMS, getApplicationContext(), CreateGalleryActivity.this);
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

                                    if (coverid.isEmpty()) {
                                        coverid = object.get(General.ID).getAsLong() + "";
                                    }


                                    if (al_images.size() == 0) {
                                        fileID.add(String.valueOf(object.get(General.ID).getAsLong()));
                                        MultipleImage multipleImage = new MultipleImage();
                                        multipleImage.setFile(path);
                                        multipleImage.setId(object.get(General.ID).getAsLong());
                                        multipleImage.setMsg(object.get(General.MSG).getAsString());
                                        multipleImage.setStatus(object.get(General.STATUS).getAsInt());
                                        multipleImage.setPosition(position);
                                        al_images.add(multipleImage);
                                    } else if (al_images.size() >= 1 && al_images.size() < 10) {
                                        fileID.add(String.valueOf(object.get(General.ID).getAsLong()));
                                        MultipleImage multipleImage = new MultipleImage();
                                        multipleImage.setFile(path);
                                        multipleImage.setId(object.get(General.ID).getAsLong());
                                        multipleImage.setMsg(object.get(General.MSG).getAsString());
                                        multipleImage.setStatus(object.get(General.STATUS).getAsInt());
                                        multipleImage.setPosition(position);
                                        al_images.add(multipleImage);
                                        btn_uploadImage.setClickable(false);
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

                    if (al_images.size() >= 1 && al_images.size() <= 10) {
                        tv_countImg.setText("(" + al_images.size() + ")");

                        galleryMultipleImageAdapter = new GalleryMultipleImageAdapter(CreateGalleryActivity.this, 1, al_images, CreateGalleryActivity.this);
                        recyclerView.setAdapter(galleryMultipleImageAdapter);
                    }

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
        int position;

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
                MakeCall.postNormal(Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER, getBody, TAG, CreateGalleryActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            removeAttachmentId(position);

            ShowToast.toast("Removed", CreateGalleryActivity.this);
            galleryMultipleImageAdapter.notifyDataSetChanged();
        }
    }

    private void removeAttachmentId(int position) {
        int i = 0;
        if (al_images.size() > 0) {
            for (MultipleImage multipleImage : al_images) {
                if (multipleImage.isSelectImgs()) {
                    al_images.remove(position);
                    fileID.remove(i);

                    galleryMultipleImageAdapter.notifyDataSetChanged();

                    if (al_images.size() == 0) {
                        countLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tv_countImg.setText("(" + al_images.size() + ")");

                        if (al_images.size() >= 1 && al_images.size() < 10) {
                            btn_uploadImage.setClickable(true);
                        } else {
                            btn_uploadImage.setClickable(false);
                        }
                    }

                    break;
                }
                i++;
            }
        }
    }

    private void createGallery() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.CREATE_GALLERY);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));
        requestMap.put(General.TITLE, albumName.getText().toString().trim());
        requestMap.put(General.CAPTION, description.getText().toString().trim());
        requestMap.put("file_id", getFileIds());

        if (!getCoverId().isEmpty()) {
            requestMap.put("cover_img_id", getCoverId());
        } else {
            requestMap.put("cover_img_id", coverid);
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.CREATE_GALLERY_IMAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    al_gallery = TeamDetails_.parseCreateAlbumGallery(response, getApplicationContext(), TAG);
                    if (al_gallery.size() > 0) {
                        if (al_gallery.get(0).getStatus() == 1) {
                            msg = al_gallery.get(0).getMessage();
                            Toast.makeText(CreateGalleryActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private String getCoverId() {
        covetImgSelectedId.clear();
        if (al_images != null && al_images.size() > 0) {
            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).isSelectImgs()) {
                    covetImgSelectedId.add(String.valueOf(al_images.get(i).getId()));
                }
            }
        }
        return covetImgSelectedId.toString()
                .replace("[", "")
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
}

