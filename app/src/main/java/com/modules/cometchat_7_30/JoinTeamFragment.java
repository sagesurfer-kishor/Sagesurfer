package com.modules.cometchat_7_30;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.sagesurfer.adapters.JoinChatExpandableListAdapter;
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

public class JoinTeamFragment extends Fragment implements JoinChatExpandableListAdapter.JoinChatExpandableListAdapterListener {
    private static final String TAG = JoinTeamFragment.class.getSimpleName();

    private static ExpandableListView expandableListView;
    private JoinChatExpandableListAdapter teamsListAdapter;
    public ArrayList<Teams_> primaryList = new ArrayList<>();
    public ArrayList<Teams_> searchList = new ArrayList<>();
    private static LinearLayout errorLayout;
    private EditText editTextSearch;
    private TextView errorText;
    private LinearLayout cardview_actions;
    private AppCompatImageView errorIcon;

    private BroadcastReceiver customReceiver;
    private JoinTeamFragment cometChat;
    private ChatFragment_ parentActivity;

    SharedPreferences sp;

    public JoinTeamFragment() {
        // Required empty public constructor
    }

    public static JoinTeamFragment newInstance(String param1, String param2) {
        JoinTeamFragment fragment = new JoinTeamFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_teams, container, false);

        sp = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);

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

        // search join team from list
        editTextSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                //performSearch();
                searchUser(editTextSearch.getText().toString().trim());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(editTextSearch.getText().toString().trim());
            }
        });

        // fetch all join team from server
        getTeams();

        //push notification
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

    // open chat window join team
    private void openChatActivity(String username, String userId, int status, String gid) {
        Log.e("userName", username);
        Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.NAME, username);
        intent.putExtra(StringContract.IntentStrings.UID, userId);
        intent.putExtra(StringContract.IntentStrings.STATUS, status);
        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
        intent.putExtra(StringContract.IntentStrings.TABS, "4");
        intent.putExtra("teamId", gid);

        startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    // make call to fetch all teams
    private void getTeams() {
        String team_type = "2";
        primaryList = PerformGetTeamsTask.getMyteam(Actions_.ALL_TEAMS_CHAT, team_type, getActivity(), TAG, false, getActivity());
        if (primaryList.size() == 0) {
            errorLayout.setVisibility(View.VISIBLE);
            expandableListView.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.GONE);
            expandableListView.setVisibility(View.VISIBLE);
            cardview_actions.setVisibility(View.VISIBLE);
            searchList.addAll(primaryList);
            teamsListAdapter = new JoinChatExpandableListAdapter(getContext(), primaryList, searchList, this, getActivity());
            expandableListView.setAdapter(teamsListAdapter);
        }
    }

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

        String memberId = String.valueOf(team_.getMembersArrayList().get(memberPosition).getUser_id());
        String teamId = String.valueOf(team_.getId());

        Preferences.save(General.BANNER_IMG, team_.getBanner());
        Preferences.save(General.TEAM_ID, team_.getId());
        Preferences.save(General.TEAM_NAME, team_.getName());

        final String contactId = String.valueOf(team_.getMembersArrayList().get(memberPosition).getComet_chat_id());
        final String username = team_.getMembersArrayList().get(memberPosition).getUsername();
        final int status = team_.getMembersArrayList().get(memberPosition).getStatus();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("teamIds", teamId);
        editor.putString("membersIds", memberId);
        editor.commit();

        openChatActivity(username, contactId, status, String.valueOf(team_.getId()));

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

    // Code By Debopam
    private void checkIntent() {
        if (getActivity() != null && getActivity().getIntent().hasExtra("receiverType")) {
            String receiverType = getActivity().getIntent().getStringExtra("receiverType");
            if (receiverType.equals("user")) {
                // Either tab 1 or 3 or 4
                String team_logs_id = getActivity().getIntent().getStringExtra("team_logs_id");
                if (team_logs_id != null && !team_logs_id.isEmpty()) {
                    if (team_logs_id.endsWith("3")) {
                        String ids[] = team_logs_id.split(Pattern.quote("_"));
                        if (ids.length == 4) {
                            for (int i = 0; i < primaryList.size(); i++) {
                                Teams_ team = primaryList.get(i);
                                if (team.getId() == Integer.parseInt(ids[2].substring(1))) ;
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
