package com.modules.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Admin_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 22-08-2017
 * Last Modified on 13-12-2017
 */

public class InvitationsListFragment extends Fragment {

    private static final String TAG = InvitationsListFragment.class.getSimpleName();
    private ArrayList<Invitation_> invitationArrayList;

    private SwipeMenuListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private Activity activity;
    private AdminInviteListAdapter adminInviteListAdapter;

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
        invitationArrayList = new ArrayList<>();

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.listview_fab);
        createButton.setVisibility(View.GONE);

        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight((int) activity.getApplicationContext().getResources().getDimension(R.dimen.list_divider));
        listView.setOnItemClickListener(onItemClickListener);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        return view;
    }

    // Open dialog fragment for invitation details and actions
    @SuppressLint("CommitTransaction")
    private void openDialog(int position) {
        DialogFragment dialogFrag = new AdminInviteDetailsPopUp();
        Bundle bundle = new Bundle();
        bundle.putSerializable(General.ID, invitationArrayList.get(position));
        bundle.putSerializable(General.POSITION, "" + position);
        dialogFrag.setArguments(bundle);
        dialogFrag.setTargetFragment(this, 1);
        dialogFrag.show(getFragmentManager().beginTransaction(), TAG);
    }

    // Handle list item/row click
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (invitationArrayList.get(0).getStatus() == 1) {
                openDialog(position);
            }
        }
    };

    // Get response from dialog fragment when it get closed
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    String position = data.getStringExtra(General.POSITION);
                    boolean isSelected = data.getBooleanExtra(General.IS_SELECTED, false);
                    if (isSelected) {
                        invitationArrayList.remove(Integer.parseInt(position));
                        adminInviteListAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getInvitation();
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

    //Make Network call to fetch invitations
    private void getInvitation() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_INVITATIONS);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CC_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    invitationArrayList = Admin_.parseInvitations(response, activity.getApplicationContext(), TAG);
                    //Log.e(TAG, "" + invitationArrayList.size());
                    if (invitationArrayList.size() > 0) {
                        if (invitationArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            adminInviteListAdapter = new AdminInviteListAdapter(activity, invitationArrayList);
                            listView.setAdapter(adminInviteListAdapter);
                        } else {
                            showError(true, invitationArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 12);
                    }
                    return;
                } else {
                    showError(true, 12);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }
}
