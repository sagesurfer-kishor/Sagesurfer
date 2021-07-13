package com.modules.announcement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.team.TeamDetailsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Announcement_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.storage.database.operations.DatabaseInsert_;
import com.storage.database.operations.DatabaseRetrieve_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 **/

public class AnnouncementListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = AnnouncementListFragment.class.getSimpleName();
    private ArrayList<Announcement_> announcementList;
    private SwipeMenuListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private DatabaseRetrieve_ databaseRetrieve_;
    private AnnouncementListAdapter announcementListAdapter;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private FloatingActionButton fab;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @Nullable
    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.list_view_layout, null);

        activity = getActivity();

        announcementList = new ArrayList<>();
        databaseRetrieve_ = new DatabaseRetrieve_(activity.getApplicationContext());

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);

        announcementListAdapter = new AnnouncementListAdapter(activity, announcementList);
        listView.setAdapter(announcementListAdapter);
        listView.setOnItemClickListener(onItemClickListener);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //mainActivityInterface.hideRevealView();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.fab_listview);
        fab.setOnClickListener(this);
        fab.setImageResource(R.drawable.ic_add_white);

        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")
                || Preferences.get(General.IS_CC).equalsIgnoreCase("1") ) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }


        // added by mayur
        if (Preferences.get(General.ROLE).equalsIgnoreCase("Lead Peer Support Specialist")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Coach")
                || Preferences.get(General.ROLE).equalsIgnoreCase("System Administrator")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Peer Mentor")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Care Coordinator")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Case Manager")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Consumer-Adult")
                || Preferences.get(General.ROLE).equalsIgnoreCase("Parent/Guardian")) {
            fab.setVisibility(View.VISIBLE);
        }


        getHeight(fab);
        Log.i(TAG, "onCreateView: announcement");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.announcements));
        mainActivityInterface.setToolbarBackgroundColor();
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));
        showError(false, 1);
        getAnnouncement();
        Log.i(TAG, "onCreateView: announcement2");
        try{
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Handle on click for list item/row
    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (announcementListAdapter.getItem(position) != null) {
                Intent detailsIntent = new Intent(activity.getApplicationContext(), AnnouncementDetailsActivity.class);
                detailsIntent.putExtra(Actions_.ANNOUNCEMENT, announcementListAdapter.getItem(position));
                startActivity(detailsIntent);
            }
        }
    };

    private void showError(boolean isError, int status) {
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

    // set padding to float button from bottom of list view to avoid overlap
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

    // Make network call to fetch announcements from server
    private void getAnnouncement() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ANNOUNCEMENT);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        requestMap.put(General.LAST_DATE, "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DATA;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                announcementList = Alerts_.parseAnnouncement(response, Actions_.ANNOUNCEMENT, activity.getApplicationContext(), TAG);
                if (announcementList.size() > 0) {
                    if (announcementList.get(0).getStatus() == 1) {
                        saveAnnouncement(announcementList);
                        announcementListAdapter = new AnnouncementListAdapter(activity, announcementList);
                        listView.setAdapter(announcementListAdapter);
                    } else {
                        showError(true, announcementList.get(0).getStatus());
                    }
                } else {
                    showError(true, 2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (announcementList.size() <= 0) {
            showError(true, 2);
        }
        swipeRefreshLayout.setRefreshing(false);
    }
    // Save newly added records to database
    private void saveAnnouncement(final ArrayList<Announcement_> list) {
        final Handler handler = new Handler();  //Optional. Define as a variable in your activity.
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(activity.getApplicationContext());
                for (int i = 0; i < list.size(); i++) {
                    databaseInsert_.insertAnnouncement(list.get(i));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_listview:
                Intent createIntent = new Intent(activity.getApplicationContext(), PostAnnouncementActivity.class);
                activity.startActivity(createIntent);
                break;
        }
    }
}
