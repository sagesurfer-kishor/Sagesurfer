package com.modules.fms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Users_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 14-07-2017
 * Last Modified on 13-12-2017
 */

public class PermissionSelectorDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = PermissionSelectorDialog.class.getSimpleName();
    private int group_id = 0;
    private ArrayList<Friends_> usersArrayList, oldList;

    private ListView listView;

    private PermissionList permissionList;
    private Activity activity;
    private UserPermissionListAdapter userPermissionListAdapter;
    private RelativeLayout relativeLayoutToolbar;

    interface PermissionList {
        void permissionList(ArrayList<Friends_> users_arrayList, boolean isSelected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        permissionList = (PermissionList) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        activity = getActivity();
        oldList = new ArrayList<>();

        AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView title = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        title.setText(getActivity().getApplicationContext().getResources().getString(R.string.custom_permission));

        listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        EditText searchBox = (EditText) view.findViewById(R.id.team_list_layout_search);
        searchBox.addTextChangedListener(textWatcher);

        TextView submitButton = (TextView) view.findViewById(R.id.single_choice_team_dialog_submit);
        submitButton.setOnClickListener(this);
        submitButton.setVisibility(View.VISIBLE);

        Bundle data = getArguments();
        if (data != null && data.containsKey(General.GROUP_ID)) {
            group_id = data.getInt(General.GROUP_ID, 0);
            usersArrayList = (ArrayList<Friends_>) data.getSerializable(Actions_.MY_FRIENDS);
            if (usersArrayList != null) {
                cloneArray();
            }
            if (group_id != 0) {
                if (usersArrayList == null || usersArrayList.size() <= 0) {
                    getMembers();
                } else {
                    setList();
                }
            } else {
                dismiss();
            }
        }

        LinearLayout searchLinearLayout = (LinearLayout) view.findViewById(R.id.team_list_search_layout);
        LinearLayout linearLayoutError = (LinearLayout) view.findViewById(R.id.linearlayout_error);
        if (usersArrayList.size() == 0) {
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        } else if (usersArrayList.get(0).getStatus() != 1) {
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        }

        return view;
    }

    // clone users list
    private void cloneArray() {
        for (Friends_ friends : usersArrayList) {
            Friends_ friend = new Friends_();
            friend.setSelected(friends.getSelected());
            friend.setUserId(friends.getUserId());
            friend.setStatus(friends.getStatus());
            friend.setName(friends.getName());
            friend.setPhoto(friends.getPhoto());
            friend.setUserName(friends.getUsername());
            oldList.add(friend);
        }
    }

    // text watcher to filter users list based on search query
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            userPermissionListAdapter.filterUsers(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        assert dialog.getWindow() != null;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d.getWindow() != null) {
            int width = ViewGroup.LayoutParams.FILL_PARENT;
            int height = ViewGroup.LayoutParams.FILL_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setStyle(Window.FEATURE_NO_TITLE, R.style.MY_DIALOG);
        d.setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_choice_team_dialog_back:
                permissionList.permissionList(oldList, false);
                break;
            case R.id.single_choice_team_dialog_submit:
                permissionList.permissionList(usersArrayList, true);
                break;
        }
        dismiss();
    }

    //open permission list and set list adapter
    private void setList() {
        userPermissionListAdapter = new UserPermissionListAdapter(activity, usersArrayList);
        listView.setAdapter(userPermissionListAdapter);
    }

    // make call to fetch users
    private void getMembers() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.FMS);
        requestMap.put(General.GROUP_ID, "" + group_id);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_USERS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    usersArrayList = Users_.parse(response, Actions_.FMS, activity.getApplicationContext(), TAG);
                    setList();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        relativeLayoutToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
