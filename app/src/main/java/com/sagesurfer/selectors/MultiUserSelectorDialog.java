package com.sagesurfer.selectors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.adapters.MultiChoiceUsersListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.views.TextWatcherExtended;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 14-07-2017
 * Last Modified on 15-12-2017
 */

public class MultiUserSelectorDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = MultiUserSelectorDialog.class.getSimpleName();
    private ArrayList<Friends_> usersArrayList, oldArray, searchUserList;

    private SelectedUsers selectedUsers;
    private MultiChoiceUsersListAdapter multiChoiceUsersListAdapter;
    private RelativeLayout relativeLayoutToolbar;
    private boolean isTeam = false;
    String selfcareContentId = "";
    private EditText searchBox;
    private ListView listView;

    public interface SelectedUsers {
        void selectedUsers(ArrayList<Friends_> users_arrayList, String id, boolean isSelected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        selectedUsers = (SelectedUsers) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        usersArrayList = new ArrayList<>();
        oldArray = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null) {
            usersArrayList = (ArrayList<Friends_>) b.getSerializable(Actions_.MY_FRIENDS);
            selfcareContentId = b.getString(General.ID);
            if (usersArrayList != null) {
                cloneArray();
            }
            if (selfcareContentId == null) {
                selfcareContentId = "";
            }
            isTeam = b.getBoolean(General.IS_FROM_TEAM_TASK);
        } else {
            dismiss();
        }

        final AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView title = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        if (isTeam) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_members));
        } else {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_friends));
        }

        listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        multiChoiceUsersListAdapter = new MultiChoiceUsersListAdapter(getActivity().getApplicationContext(), usersArrayList);
        listView.setAdapter(multiChoiceUsersListAdapter);

        searchBox = (EditText) view.findViewById(R.id.team_list_layout_search);
        //searchBox.addTextChangedListener(textWatcher);

        TextView submitButton = (TextView) view.findViewById(R.id.single_choice_team_dialog_submit);
        submitButton.setOnClickListener(this);
        submitButton.setVisibility(View.VISIBLE);

        LinearLayout searchLinearLayout = (LinearLayout) view.findViewById(R.id.team_list_search_layout);
        LinearLayout linearLayoutError = (LinearLayout) view.findViewById(R.id.linearlayout_error);
        if (usersArrayList.size() == 0) {
            submitButton.setVisibility(View.GONE);
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        } else if (usersArrayList.get(0).getStatus() != 1) {
            submitButton.setVisibility(View.GONE);
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        }

        searchBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchBox.clearFocus();
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        searchBox.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                performSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        return view;
    }


    public void performSearch() {
        searchUserList = new ArrayList<>();
        String searchText = searchBox.getText().toString().trim();
        if (searchText.length() == 0) {
            searchBox.clearFocus();
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        }
        for (Friends_ friends_ : usersArrayList) {
            if (friends_.getName() != null && friends_.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchUserList.add(friends_);
            }
        }
        if (searchUserList.size() > 0) {
            multiChoiceUsersListAdapter = new MultiChoiceUsersListAdapter(getActivity().getApplicationContext(), searchUserList);
            listView.setAdapter(multiChoiceUsersListAdapter);

        }
    }


    private void cloneArray() {
        for (Friends_ friends : usersArrayList) {
            Friends_ friend = new Friends_();
            friend.setSelected(friends.getSelected());
            friend.setUserId(friends.getUserId());
            friend.setStatus(friends.getStatus());
            friend.setName(friends.getName());
            friend.setPhoto(friends.getPhoto());
            friend.setUserName(friends.getUsername());
            oldArray.add(friend);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            multiChoiceUsersListAdapter.filterUsers(s.toString());
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
                selectedUsers.selectedUsers(oldArray, selfcareContentId, false);
                break;
            case R.id.single_choice_team_dialog_submit:
                selectedUsers.selectedUsers(usersArrayList, selfcareContentId, true);
                break;
        }
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        relativeLayoutToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
