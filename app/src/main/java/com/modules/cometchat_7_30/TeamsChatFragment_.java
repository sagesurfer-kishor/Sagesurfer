package com.modules.cometchat_7_30;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.sagesurfer.adapters.TeamsChatExpandableListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.regex.Pattern;

import constant.StringContract;
import screen.messagelist.CometChatMessageListActivity;


/**
 * Created by Monika on 7/16/2018.
 */

public class TeamsChatFragment_ extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TeamsChatExpandableListAdapter.TeamsChatExpandableListAdapterListener {
    private static final String TAG = TeamsChatFragment_.class.getSimpleName();

    private final int CHATS_LOADER = 1;
    private final int CHATS_SEARCH_LOADER = 2;
    private static ExpandableListView expandableListView;
    private TeamsChatExpandableListAdapter teamsListAdapter;
    public ArrayList<Teams_> primaryList = new ArrayList<>();
    public ArrayList<Teams_> searchList = new ArrayList<>();
    private BroadcastReceiver customReceiver;
    private static LinearLayout errorLayout;
    private EditText editTextSearch;
    private LinearLayout cardview_actions;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private final int CONTACTS_LOADER = 1, CONTACTS_SEARCH_LOADER = 2;
    private boolean isSearching = false;

    private TeamsChatFragment_ cometChat;
    private final int GROUPS_LOADER = 1, GROUPS_SEARCH_LOADER = 2;

    public TeamsChatFragment_() {
        // Required empty public constructor
    }

    public static TeamsChatFragment_ newInstance(String param1, String param2) {
        TeamsChatFragment_ fragment = new TeamsChatFragment_();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_teams, container, false);

        //SubcribeCometChat_.SubcribeToCometChat(getActivity());
        editTextSearch = view.findViewById(R.id.ed_search_friend);
        errorLayout = view.findViewById(R.id.linealayout_error);
        errorText = view.findViewById(R.id.textview_error_message);
        errorIcon = view.findViewById(R.id.imageview_error_icon);
        expandableListView = view.findViewById(R.id.expandablelistview_teams);
        cardview_actions = view.findViewById(R.id.cardview_actions);

        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // serach myteam from list
        editTextSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                //performSearch();

                String searchText = editTextSearch.getText().toString().trim();
                if (searchText.isEmpty()) {
                    getTeams();
                }
                searchUser(editTextSearch.getText().toString().trim());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //fetch all myteam
        getTeams();

        //push notification open my team chat screen
        checkIntent();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        //refreshFragment();
    }

    private void searchUser(String search) {
        teamsListAdapter.getFilter().filter(search);
    }

    private void openChatActivity(String username, String userId, int status) {
        Log.e("userName", username);
        Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.NAME, username);
        intent.putExtra(StringContract.IntentStrings.UID, userId);
        intent.putExtra(StringContract.IntentStrings.STATUS, status);
        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
        intent.putExtra("GID", Preferences.get(General.GROUP_ID));
        intent.putExtra(StringContract.IntentStrings.TABS, "3");
        startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    // make call to fetch all teams
    private void getTeams() {
        String team_type = "1";
        primaryList = PerformGetTeamsTask.getMyteam(Actions_.ALL_TEAMS_CHAT, team_type, getActivity(), TAG, false, getActivity());
        if (primaryList.size() == 0) {
            errorLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.GONE);
            cardview_actions.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.VISIBLE);
            searchList.addAll(primaryList);
            teamsListAdapter = new TeamsChatExpandableListAdapter(getContext(), primaryList, searchList,
                    this, getActivity());
            expandableListView.setAdapter(teamsListAdapter);
        }
    }

    /*public void performSearch() {
        searchTeamsArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }

        for (Teams_ teamsItem : teamsArrayList) {
            if (teamsItem.getName() != null && teamsItem.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchTeamsArrayList.add(teamsItem);
            }
        }

        if (searchTeamsArrayList.size() > 0) {
            showError(false, 1);
            TeamsChatExpandableListAdapter teamsListAdapter = new TeamsChatExpandableListAdapter(getContext(), searchTeamsArrayList, this, getActivity());
            expandableListView.setAdapter(teamsListAdapter);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        try {
            super.onStop();
            if (null != customReceiver) {
                getActivity().unregisterReceiver(customReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMemberRelativeLayoutClicked(int memberPosition, Teams_ team_) {
        Log.e(TAG, "position: " + memberPosition + " user name: " + team_.getMembersArrayList().get(memberPosition).getUsername());
        Log.e(TAG, "team id: " + team_.getId() + " team name: " + team_.getName());

        Preferences.save(General.BANNER_IMG, team_.getBanner());
        Preferences.save(General.TEAM_ID, team_.getId());
        Preferences.save(General.TEAM_NAME, team_.getName());

        final String contactId = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
        final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
        final int status = team_.getMembersArrayList().get(memberPosition).getStatus();

        openChatActivity(username, contactId, status);
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, getContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);
        }
    }

    public void refreshFragment() {

        try {
            if (getLoaderManager().getLoader(CHATS_LOADER) != null) {

                getLoaderManager().restartLoader(CHATS_LOADER, null, this);
            } else {
                getLoaderManager().initLoader(CHATS_LOADER, null, this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String selection;
        String[] selectionArgs;

        switch (id) {
            case CHATS_LOADER:


            case CHATS_SEARCH_LOADER:


            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    // Code By Debopam
    private void checkIntent() {
        if (getActivity() != null && getActivity().getIntent().hasExtra("receiverType")) {
            String receiverType = getActivity().getIntent().getStringExtra("receiverType");
            if (receiverType.equals("user")) {
                // Either tab 1 or 3 or 4
                String team_logs_id = getActivity().getIntent().getStringExtra("team_logs_id");
                if (team_logs_id != null && !team_logs_id.isEmpty()) {
                    if (team_logs_id.endsWith("4")) {
                        String ids[] = team_logs_id.split(Pattern.quote("_"));
                        if (ids.length == 3) {
                            for (int i = 0; i < primaryList.size(); i++) {
                                Teams_ team = primaryList.get(i);
                                if (team.getId() == Integer.parseInt(ids[1].substring(1))) ;
                                {
                                    for (int j = 0; j < team.getMembersArrayList().size(); j++) {
                                        if (team.getMembersArrayList().get(j).getUser_id() ==
                                                Integer.parseInt(getActivity().getIntent().getStringExtra("sender").split(Pattern.quote("_"))[0])) {
                                            onMemberRelativeLayoutClicked(j, team);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }


    }

}

