package com.modules.admin;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.KeyboardOperation;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Admin_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 22-08-2017
 *         Last Modified on 20-12-2017
 */

public class AdminActivityFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AdminActivityFragment.class.getSimpleName();
    private ArrayList<Friends_> usersList;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;

    private LinearLayout errorLayout;
    private EditText searchBox;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private ListView listView;


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.admin_activity_fragment_layout, null);

        activity = getActivity();
        usersList = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight((int) activity.getApplicationContext().getResources().getDimension(R.dimen.list_divider));
        listView.setOnItemClickListener(onItemClick);
        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        AppCompatImageView searchButton = (AppCompatImageView)
                view.findViewById(R.id.admin_activity_fragment_search_icon);
        searchButton.setOnClickListener(this);

        searchBox = (EditText) view.findViewById(R.id.admin_activity_fragment_search_box);

        return view;
    }

    // Handle click on search result list item
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (usersList.get(0).getStatus() == 1) {
                showActionPopup(position, view);
            }
        }
    };

    // Make network call to reset password
    private void resetPassword(long user_id, View view) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.RESET_PASSWORD);
        requestMap.put(General.USER_ID, "" + user_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SUPERVISOR_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    if (Error_.oauth(response, activity.getApplicationContext()) == 13) {
                        showResponses(13, view);
                        return;
                    }
                    JsonObject object = GetJson_.getJson(response);
                    if (object.has(General.STATUS)) {
                        showResponses(object.get(General.STATUS).getAsInt(), view);
                        return;
                    } else {
                        showResponses(12, view);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(11, view);
    }

    // Show action result from response message in toast or snack bar
    private void showResponses(int status, View view) {
        String message = "";
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else if (status == 2) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, activity.getApplicationContext());
    }

    //Show pop-up menu when clicked on particular user from search result list
    private void showActionPopup(final int position, final View view) {
        final PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.admin_activity_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.admin_activity_menu_message:
                        Intent sendMessage = new Intent(activity.getApplicationContext(),
                                SendMessageActivity.class);
                        sendMessage.putExtra(General.USERNAME, usersList.get(position));
                        startActivity(sendMessage);
                        break;

                    case R.id.admin_activity_menu_reset:
                        resetPassword(usersList.get(position).getUserId(), view);
                        break;
                }
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    // Show respective error on screen
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

    // Make network call to search user
    private void search(String query) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.FETCH_USER_DETAILS);
        requestMap.put(General.SEARCH, query);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SUPERVISOR_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    usersList = Admin_.parseUsers(response, activity.getApplicationContext(), TAG);
                    if (usersList.get(0).getStatus() == 1) {
                        showError(false, usersList.get(0).getStatus());
                        AdminUsersListAdapter adminUsersListAdapter =
                                new AdminUsersListAdapter(activity, usersList);
                        listView.setAdapter(adminUsersListAdapter);
                    } else {
                        showError(true, usersList.get(0).getStatus());
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }

    // Validate search query entered
    private boolean validQuery(String query) {
        if (query.length() <= 0) {
            searchBox.setError("Enter Valid Name / Username");
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        //mainActivityInterface.hideRevealView();
        //mainActivityInterface.toggleSearch(false);
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources()
                .getString(R.string.admin_activity));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.admin_activity_fragment_search_icon:
                KeyboardOperation.hide(activity.getApplicationContext(), searchBox.getWindowToken());
                String query = searchBox.getText().toString().trim();
                if (validQuery(query)) {
                    search(query);
                }
                break;
        }
    }
}
