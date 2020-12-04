package com.modules.fms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.team.TeamDetailsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.database.operations.DatabaseInsert_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 01-08-2017
 * Last Modified on 13-12-2017
 */

public class FileSharingListFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = FileSharingListFragment.class.getSimpleName();
    /*private ArrayList<Teams_> teamsArrayList;*/
    private ArrayList<File_> fileList;
    private ArrayList<Folder_> folderArrayList;
    private int group_id;

    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    //private TextView teamSelected;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private FloatingActionButton fab;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.list_view_layout, null);

        activity = getActivity();

        //teamsArrayList = new ArrayList<>();

        Preferences.initialize(activity.getApplicationContext());

        group_id = Integer.parseInt(Preferences.get(General.TEAM_ID));
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(3);

        //teamSelected = (TextView) view.findViewById(R.id.lis_team_selector_text);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        /*RelativeLayout teamSelector = (RelativeLayout) view.findViewById(R.id.list_team_selector);
        teamSelector.setOnClickListener(this);*/

        fab = (FloatingActionButton) view.findViewById(R.id.listview_fab);
        fab.setImageResource(R.drawable.ic_add_white);
        fab.setOnClickListener(this);

        /*if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }*/

        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1") || General.isCurruntUserHasPermissionToCreateFMS()   ) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        getHeight(fab);
        return view;
    }

    // set padding to float button from bottom toa void overlap
    private void getHeight(final FloatingActionButton createButton) {
        final ViewTreeObserver observer = createButton.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int ht = createButton.getHeight();
                        listView.setPadding(0, 0, 0, ht + 10);
                    }
                });
    }

    private void showError(boolean isError, int status) {
        if (status == 22) {
            errorText.setText(activity.getApplicationContext().getResources().getString(R.string.select_team));
            errorIcon.setImageResource(R.drawable.vi_select_team_error);

            errorLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    // make call to fetch file and folders for root folder
    private void getFileFolders() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET);
        requestMap.put(General.GROUP_ID, "" + group_id);
        requestMap.put(General.LAST_DATE, "0");
        requestMap.put(General.FOLDER_ID, "-1");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    fileList = Alerts_.parseFiles(response, activity.getApplicationContext(), TAG);
                    saveFileList(fileList);
                    folderArrayList = Alerts_.parseFolders(response, activity.getApplicationContext(), TAG);
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
        String directory = "root/";
        FileSharingListAdapter fileSharingListAdapter = new FileSharingListAdapter(activity, fileArrayList, group_id, directory, -1);
        listView.setAdapter(fileSharingListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.file_sharing));
        mainActivityInterface.setToolbarBackgroundColor();
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));
        /*if (teamsArrayList != null) {
            teamsArrayList = PerformGetTeamsTask.get(Actions_.ALL_TEAMS, activity.getApplicationContext(), TAG);
        }*/
        if (group_id == 0) {
            showError(true, 22);
        }
        if (group_id != 0) {
            getFileFolders();
        }

        try{
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*//open team selector dialog
    @SuppressLint("CommitTransaction")
    private void openTeamSelector() {
        Bundle bundle = new Bundle();
        DialogFragment dialogFrag = new SingleTeamSelectorDialog();
        bundle.putSerializable(General.TEAM_LIST, teamsArrayList);
        bundle.putBoolean(General.FOLDER_ID, true);
        bundle.putInt("menu_id", 17);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), General.TEAM_LIST);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listview_fab:
                if (group_id == 0) {
                    ShowSnack.viewWarning(v, activity.getApplicationContext().getResources()
                            .getString(R.string.please_select_team), activity.getApplicationContext());
                } else {
                    Intent createIntent = new Intent(activity.getApplicationContext(), UploadFileActivity.class);
                    createIntent.putExtra(General.GROUP_ID, group_id);
                    startActivity(createIntent);
                    activity.overridePendingTransition(0, 0);
                }
                break;
            /*case R.id.list_team_selector:
                if (teamsArrayList == null || teamsArrayList.size() <= 0) {
                    ShowSnack.viewWarning(v, activity.getApplicationContext().getResources()
                            .getString(R.string.teams_unavailable), activity.getApplicationContext());
                } else {
                    openTeamSelector();
                }
                break;*/
        }
    }

    /*// get selected team and save those details to shared preferences
    public void GetChoice(Teams_ teams_, boolean isSelected) {
        if (isSelected) {
            group_id = teams_.getId();
            //teamSelected.setText(teams_.getName());
            Preferences.save(General.GROUP_ID, "" + group_id);
            Preferences.save(General.GROUP_NAME, teams_.getName());
            Preferences.save(General.OWNER_ID, teams_.getOwnerId());
            Preferences.save(General.MODERATOR, teams_.getModerator());
            Preferences.save(General.PERMISSION, teams_.getPermission());
            getFileFolders();
        }
    }*/

    // Save newly added records to database
    private void saveFileList(final ArrayList<File_> list) {
        final Handler handler = new Handler();  //Optional. Define as a variable in your activity.
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(activity.getApplicationContext());
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
