package com.modules.fms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.storage.database.operations.DatabaseInsert_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author girish M (girish@sagesurfer.com)
 * Created on 19/07/2017.
 * Last Modified on 13/09/2017.
 **/

public class FileFolderListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FileFolderListActivity.class.getSimpleName();
    private int folder_id = 0;
    private ArrayList<File_> fileList;
    private ArrayList<Folder_> folderArrayList;
    private String directory = "root/";

    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    Toolbar toolbar;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fab;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.file_folder_list_activity_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        Preferences.initialize(getApplicationContext());

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

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

        AppCompatImageButton sendButton = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_send);
        sendButton.setVisibility(View.GONE);

        errorText = (TextView) findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) findViewById(R.id.linealayout_error);
        listView = (ListView) findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(3);
        fab = (FloatingActionButton) findViewById(R.id.fab_listview);
        fab.setOnClickListener(this);
        fab.setImageResource(R.drawable.ic_add_white);
        fab.setVisibility(View.VISIBLE);

        Intent data = getIntent();
        if (data.hasExtra(General.ID)) {
            folder_id = data.getIntExtra(General.ID, 0);
            directory = data.getStringExtra(General.NAME);
            titleText.setText(directory);
            getFileFolders();
        }
        if (folder_id == 0) {
            onBackPressed();
        }
    }

    private void showError(boolean isError, int status) {
        if (status == 22) {
            errorText.setText(getApplicationContext().getResources().getString(R.string.select_team));
            errorIcon.setImageResource(R.drawable.vi_select_team_error);

            errorLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    // make call to fetch file and folders for any folder with folder id
    private void getFileFolders() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET);
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        requestMap.put(General.LAST_DATE, "0");
        requestMap.put(General.FOLDER_ID, "" + folder_id);
        requestMap.put("is_dev", "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FMS;
        RequestBody requestBody = NetworkCall_.make(requestMap,
                url,
                TAG,
                this.getApplicationContext(),
                this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this.getApplicationContext(), this);
                if (response != null) {
                    fileList = Alerts_.parseFiles(response, getApplicationContext(), TAG);
                    saveFileList(fileList);
                    folderArrayList = Alerts_.parseFolders(response, getApplicationContext(), TAG);
                    if (folderArrayList != null && fileList != null) {
                        setFileList();
                    } else {
                        showError(true, 11);
                    }
                } else {
                    showError(true, 12);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // make finale list to display with file and folder
    private void setFileList() {
        ArrayList<FileSharing_> fileArrayList = new ArrayList<>();

        if (fileList.size() > 0 && fileList.get(0).getStatus() == 1) {
            FileSharing_ sectionOne = new FileSharing_();
            sectionOne.setStatus(1);
            sectionOne.setName("Files");
            sectionOne.setSection(true);
            sectionOne.setIsFile(false);
            fileArrayList.add(sectionOne);
            for (File_ file_ : fileList) {
                FileSharing_ fileSharing_ = new FileSharing_();
                fileSharing_.setStatus(file_.getStatus());
                fileSharing_.setId(file_.getId());
                fileSharing_.setGroupId(file_.getGroupId());
                fileSharing_.setUserId(file_.getUserId());
                fileSharing_.setIsRead(file_.getIsRead());
                fileSharing_.setIsDefault(file_.getIsDefault());
                fileSharing_.setCheckIn(file_.getCheckIn());
                fileSharing_.setPermission(file_.getPermission());
                fileSharing_.setDate(file_.getDate());
                fileSharing_.setTeamName(file_.getTeamName());
                fileSharing_.setUserName(file_.getUserName());
                fileSharing_.setFullName(file_.getFullName());
                fileSharing_.setRealName(file_.getRealName());
                fileSharing_.setStatus(file_.getStatus());
                fileSharing_.setComment(file_.getComment());
                fileSharing_.setDescription(file_.getDescription());
                fileSharing_.setSize(file_.getSize());
                fileSharing_.setIsFile(true);
                fileSharing_.setSection(false);
                fileArrayList.add(fileSharing_);
            }
        }
        if (folderArrayList.size() > 0 && folderArrayList.get(0).getStatus() == 1) {
            FileSharing_ sectionTwo = new FileSharing_();
            sectionTwo.setStatus(1);
            sectionTwo.setName("Folders");
            sectionTwo.setSection(true);
            sectionTwo.setIsFile(false);
            fileArrayList.add(sectionTwo);

            for (Folder_ folder_ : folderArrayList) {
                FileSharing_ fileSharing_ = new FileSharing_();
                fileSharing_.setStatus(folder_.getStatus());
                fileSharing_.setId(folder_.getId());
                fileSharing_.setName(folder_.getName());
                fileSharing_.setTotalFiles(folder_.getTotalFiles());
                fileSharing_.setTotalFolders(folder_.getTotalFolders());
                fileSharing_.setIsFile(false);
                fileSharing_.setSection(false);
                fileArrayList.add(fileSharing_);
            }
        }
        if (fileArrayList.size() <= 0) {
            showError(true, 2);
        } else {
            showError(false, 1);
        }
        FileSharingListAdapter fileSharingListAdapter = new FileSharingListAdapter(this, fileArrayList, Integer.parseInt(Preferences.get(General.GROUP_ID)), directory, folder_id);
        listView.setAdapter(fileSharingListAdapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagebutton_activitytoolbar_send:

                break;
            case R.id.fab_listview:
                Intent createIntent = new Intent(getApplicationContext(), UploadFileActivity.class);
                createIntent.putExtra(General.GROUP_ID, Integer.parseInt(Preferences.get(General.GROUP_ID)));
                createIntent.putExtra(General.FOLDER_ID, folder_id);

                createIntent.putExtra(General.DIRECTORY, directory);

                startActivity(createIntent);
                overridePendingTransition(0, 0);
                break;
        }
    }

    // Save newly added records to database
    private void saveFileList(final ArrayList<File_> list) {
        final Handler handler = new Handler();  //Optional. Define as a variable in your activity.
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(getApplicationContext());
                for (int i = 0; i < list.size(); i++) {
                    databaseInsert_.insertFMS(list.get(i));
                }
                handler.post(new Runnable()  //If you want to update the UI, queue the code on the UI thread
                {
                    public void run() {
                        //Code to update the UI
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
}
