package com.modules.teamtalk.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.modules.teamtalk.activity.CreateTeamTalkActivity;
import com.modules.teamtalk.activity.TalkDetailsActivity;
import com.modules.teamtalk.adapter.TeamTalkListAdapter;
import com.modules.teamtalk.model.TeamTalk_;
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
import com.sagesurfer.views.DeleteSwipeMenu;
import com.storage.database.operations.DatabaseInsert_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 */

public class TeamTalkListFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = TeamTalkListFragment.class.getSimpleName();
    private ArrayList<TeamTalk_> talkList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeMenuListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private TeamTalkListAdapter teamTalkListAdapter;
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
        talkList = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        DeleteSwipeMenu deleteSwipeMenu = new DeleteSwipeMenu(activity);

        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setPadding(0, 10, 0, 0);
        listView.setDividerHeight(0);

        teamTalkListAdapter = new TeamTalkListAdapter(activity, talkList);
        listView.setAdapter(teamTalkListAdapter);
        listView.setOnItemClickListener(onItemClick);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.listview_fab);
        fab.setImageResource(R.drawable.ic_add_white);
        fab.setOnClickListener(this);

       /* if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }*/

        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1") || General.isCurruntUserHasPermissionToTeamTalkActions()) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }



        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.team_discussion));
        } else {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.team_talk));
        }
        mainActivityInterface.setToolbarBackgroundColor();
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));

        showError(false, 1);

        getList();

        try{
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent detailsIntent = new Intent(activity.getApplicationContext(), TalkDetailsActivity.class);
            detailsIntent.putExtra(Actions_.TEAM_TALK, talkList.get(position));
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        }
    };

    //make network call to fetch team talk list from server
    private void getList() {
        talkList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TEAMTALK);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DATA;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                talkList = Alerts_.parseTalk(response, Actions_.TEAMTALK, activity.getApplicationContext(), TAG);
                if (talkList.size() > 0) {
                    if (talkList.get(0).getStatus() == 1) {
                        saveTalkList(talkList);
                        teamTalkListAdapter = new TeamTalkListAdapter(activity, talkList);
                        listView.setAdapter(teamTalkListAdapter);
                    } else {
                        showError(true, talkList.get(0).getStatus());
                    }
                } else {
                    showError(true, 12);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listview_fab:
                Intent createIntent = new Intent(activity.getApplicationContext(), CreateTeamTalkActivity.class);
                activity.startActivity(createIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }

    // Save newly added records to database
    private void saveTalkList(final ArrayList<TeamTalk_> list) {
        final Handler handler = new Handler();  //Optional. Define as a variable in your activity.
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(activity.getApplicationContext());
                for (int i = 0; i < list.size(); i++) {
                    databaseInsert_.insertTeamTalk(list.get(i));
                }
                //If you want to update the UI, queue the code on the UI thread
                handler.post(new Runnable() {
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
