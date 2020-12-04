package com.modules.team;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Broadcast;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;


/**
 * @author kailash karankal
 */

public class TeamListFragment extends Fragment implements TeamListAdapter.TeamListAdapterListener, View.OnClickListener {
    private static final String TAG = TeamListFragment.class.getSimpleName();
    private ArrayList<Teams_> teamsArrayList, teamsSearchArrayList;
    private CardView cardViewActions;
    private EditText editTextSearch;
    private RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private TeamListAdapter teamListAdapter;
    private BroadcastReceiver receiver;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private FloatingActionButton createTeamButton;
    private ImageButton teamFilterButton;
    private int item_selection = 1;

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
        /*Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));*/

        View view = inflater.inflate(R.layout.swipe_refresh_recycle_view_layout, null);

        activity = getActivity();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.teams));

        cardViewActions = (CardView) view.findViewById(R.id.cardview_actions);
        cardViewActions.setVisibility(View.VISIBLE);
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        errorText = (TextView) view.findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.swipe_refresh_recycler_view__error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(Broadcast.SEARCH_BROADCAST)) {
                    String query = intent.getStringExtra(Broadcast.SEARCH_BROADCAST);
                    if (teamListAdapter != null) {
                        teamListAdapter.filterTeams(query);
                    }
                }
            }
        };

        teamsArrayList = new ArrayList<>();
        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                performSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        createTeamButton = (FloatingActionButton) view.findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        createTeamButton.setImageResource(R.drawable.ic_add_white);
        createTeamButton.setOnClickListener(this);


//        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
////            createTeamButton.setVisibility(View.GONE);
//        }
//        else {
//            if (CheckRole.showFabIconOne(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
//                createTeamButton.setVisibility(View.VISIBLE);
//            } else {
//                createTeamButton.setVisibility(View.GONE);
//            }
//        }


        /*Above condition is to show and hide add button according to there Roll*/
        Boolean isPermission = false;
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage008))) {
//            createTeamButton.setVisibility(View.GONE);
            if (CheckRole.showFabIconOneTrail(Integer.parseInt(Preferences.get(General.ROLE_ID))) || CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                isPermission = true;
            }
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
//            createTeamButton.setVisibility(View.GONE);
            if (CheckRole.showFabIconOneTarzana(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                isPermission = true;
            }
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
//            createTeamButton.setVisibility(View.GONE);
            if (CheckRole.showFabIconOneWerhope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                isPermission = true;
            }
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
//            createTeamButton.setVisibility(View.GONE);
            if (CheckRole.showFabIconOneMhaw(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                isPermission = true;
            }
        }

        if (isPermission){
            createTeamButton.setVisibility(View.VISIBLE);
        }else {
            createTeamButton.setVisibility(View.GONE);
        }

        teamFilterButton = (ImageButton) view.findViewById(R.id.team_filter_icon);
        teamFilterButton.setVisibility(View.VISIBLE);

        teamFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopupMenu(v);
            }
        });

        return view;
    }

    private void showFilterPopupMenu(View view) {
        final PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.menu_team_data_sort, popup.getMenu());
        MenuItem itemAll = popup.getMenu().findItem(R.id.menusort_alphabetical);
        MenuItem itemMyTeam = popup.getMenu().findItem(R.id.menusort_my_team);
        MenuItem itemJoinedTeam = popup.getMenu().findItem(R.id.menusort_joined_team);

        if (item_selection == 1) {
            itemAll.setChecked(true);
        }
        if (item_selection == 2) {
            itemMyTeam.setChecked(true);
        }
        if (item_selection == 3) {
            itemJoinedTeam.setChecked(true);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menusort_alphabetical:
                        item.setChecked(!item.isChecked());
                        item_selection = 1;
                        getTeams("", "0");
                        popup.dismiss();
                        break;
                    case R.id.menusort_my_team:
                        item.setChecked(!item.isChecked());
                        item_selection = 2;
                        getTeams("", "1");
                        popup.dismiss();
                        break;
                    case R.id.menusort_joined_team:
                        item.setChecked(!item.isChecked());
                        item_selection = 3;
                        getTeams("", "2");
                        popup.dismiss();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.teams));
        mainActivityInterface.setToolbarBackgroundColor();
        getActivity().registerReceiver(receiver, new IntentFilter(Broadcast.SEARCH_BROADCAST));
        editTextSearch.setText("");
        String searchText = "";
        getTeams(searchText, "0");
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.unregisterReceiver(receiver);
    }

    // make call to fetch all teams
    private void getTeams(String searchText, String teamType) {
        if (searchText.length() > 0) {
            teamsArrayList = PerformGetTeamsTask.getSearchTeams(Actions_.SEARCH_TEAMS, teamType, activity, TAG, searchText, activity);
        } else {
            teamsArrayList = PerformGetTeamsTask.getTeams(Actions_.ALL_TEAMS, teamType, activity, TAG, false, activity);
        }
        if (teamsArrayList.size() > 0) {
            if (teamsArrayList.get(0).getStatus() == 1) {
                teamListAdapter = new TeamListAdapter(activity.getApplicationContext(), teamsArrayList, this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(teamListAdapter);
                showError(false, 1);
            } else {
                showError(true, teamsArrayList.get(0).getStatus());
            }
        } else {
            showError(true, 2);
        }
    }

    public void performSearch() {
        teamsSearchArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
            getTeams(searchText, "0");
        }
        for (Teams_ teamsItem : teamsArrayList) {
            if ((teamsItem.getName() != null && teamsItem.getName().toLowerCase().contains(searchText.toLowerCase()))) {
                teamsSearchArrayList.add(teamsItem);
            }
        }
        if (teamsSearchArrayList.size() > 0) {
            showError(false, 1);
            teamListAdapter = new TeamListAdapter(activity.getApplicationContext(), teamsSearchArrayList, this);
            recyclerView.setAdapter(teamListAdapter);
            teamsArrayList = teamsSearchArrayList;
        } else {
            showError(true, 2);
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClicked(int position) {
        Teams_ tesmsItem = teamsArrayList.get(position);
        if (teamsArrayList.size() > 0) {
            tesmsItem = teamsArrayList.get(position);
        }
        Preferences.save(General.BANNER_IMG, tesmsItem.getBanner());
        Preferences.save(General.TEAM_ID, tesmsItem.getId());
        Preferences.save(General.TEAM_NAME, tesmsItem.getName());
        Preferences.save("Owner_ID", tesmsItem.getOwnerId());
        Preferences.save(General.TYPE, tesmsItem.getType());

        Intent detailsIntent = new Intent(activity.getApplicationContext(), TeamDetailsActivity.class);
        detailsIntent.putExtra("showIcon", false);
        detailsIntent.putExtra(General.TEAM, tesmsItem);
        startActivity(detailsIntent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_refresh_layout_recycler_view_float:
                Intent attachmentIntent = new Intent(activity.getApplicationContext(), TeamCreateActivity.class);
                activity.startActivity(attachmentIntent);
                break;
        }
    }
}
