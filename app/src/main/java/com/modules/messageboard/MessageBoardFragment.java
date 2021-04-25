package com.modules.messageboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.team.TeamDetailsActivity;
import com.sagesurfer.adapters.MessageBoardListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.MessageBoard_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.storage.database.operations.DatabaseRetrieve_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 2/26/2019.
 */

public class MessageBoardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MessageBoardListAdapter.OnItemClickListener, View.OnClickListener {
    private static final String TAG = MessageBoardFragment.class.getSimpleName();

    private ArrayList<MessageBoard_> messageBoardList;

    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeMenuListView listView;

    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private DatabaseRetrieve_ databaseRetrieve_;
    private Activity activity;
    private MessageBoardListAdapter messageBoardListAdapter;
    private MainActivityInterface mainActivityInterface;

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
        databaseRetrieve_ = new DatabaseRetrieve_(activity.getApplicationContext());

        messageBoardList = new ArrayList<>();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(this);
        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")
                || Preferences.get(General.IS_CC).equalsIgnoreCase("1")
                || Preferences.get(General.IS_CASE_MANAGER).equalsIgnoreCase("1"))
        {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setPadding(0,10,0,0);
        listView.setDividerHeight(0);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        messageBoardListAdapter = new MessageBoardListAdapter(activity, messageBoardList, this);
        listView.setAdapter(messageBoardListAdapter);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.message_board));
        mainActivityInterface.setToolbarBackgroundColor();
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));

        if (messageBoardList.size()>0){
            messageBoardList.clear();
        }
        /*// fetch home task from local database and add it to list adapter
        messageBoardList.addAll(databaseRetrieve_.retrieveTask(0));
        messageBoardListAdapter.notifyDataSetChanged();*/

        if (messageBoardList.size() <= 0) {
            onRefresh();
        }

        try{
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        //mainActivityInterface.hideRevealView();
        swipeRefreshLayout.setRefreshing(true);
        fetchTask();
    }

    @Override
    public void onItemClickListener(MessageBoard_ messageBoard_) {
        if (messageBoard_ != null) {
            Intent detailsIntent = new Intent(activity.getApplicationContext(), MessageBoardDetailsActivity.class);
            detailsIntent.putExtra(General.MESSAGEBOARD, messageBoard_);
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        }
    }

    // make network call to fetch all home task
    private void fetchTask() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MESSAGEBOARD);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                ArrayList<MessageBoard_> tempMessageBoardArrayList = Alerts_.parseMessageBoard(response, Actions_.GET_MESSAGEBOARD, activity.getApplicationContext(), TAG);
                if (tempMessageBoardArrayList.size() > 0) {
                    if (tempMessageBoardArrayList.get(0).getStatus() == 1) {
                        //saveTask(tempMessageBoardArrayList);
                        messageBoardList.addAll(tempMessageBoardArrayList);
                        messageBoardListAdapter.notifyDataSetChanged();
                        showError(false, 1);
                    } else {
                        showError(true, tempMessageBoardArrayList.get(0).getStatus());
                    }
                } else {
                    showError(true, 12);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
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

    /*// insert newly received task to local database
    private void saveTask(final ArrayList<MessageBoard_> list) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(activity.getApplicationContext());
                for (int i = 0; i < list.size(); i++) {
                    databaseInsert_.insertTask(list.get(i));
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent sosIntent = new Intent(activity.getApplicationContext(), CreateMessageBoardActivity.class);
                startActivity(sosIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }
}
