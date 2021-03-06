package com.modules.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
 import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.adapters.SingleChoiceUserListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.snack.ShowSnack;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 08-09-2017
 *         Last Modified on 14-12-2017
 */

public class OwnerSelectorDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = OwnerSelectorDialog.class.getSimpleName();
    private ArrayList<Friends_> usersList;

    private GetOwner getChoice;
    private SingleChoiceUserListAdapter singleChoiceUserListAdapter;
    private RelativeLayout relativeLayoutToolbar;

    interface GetOwner {
        void getOwner(Friends_ friends_, boolean isSelected);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getChoice = (GetOwner) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        usersList = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null) {
            usersList = (ArrayList<Friends_>) b.getSerializable(General.USER_LIST);
            if (usersList == null) {
                dismiss();
            }
        } else {
            dismiss();
            getChoice.getOwner(null, false);
        }

        final AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView title = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_owner));


        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);

        singleChoiceUserListAdapter = new SingleChoiceUserListAdapter(getActivity().getApplicationContext(), usersList);
        listView.setAdapter(singleChoiceUserListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0) {
                    ShowSnack.compatImageButtonWarning(backButton, getActivity().getApplicationContext().getResources().getString(
                                    R.string.select_owner), getActivity().getApplicationContext());
                }
                if (usersList.get(position).getStatus() == 1) {
                    getChoice.getOwner(usersList.get(position), true);
                    dismiss();
                }
            }
        });

        EditText searchBox = (EditText) view.findViewById(R.id.team_list_layout_search);
        searchBox.addTextChangedListener(textWatcher);

        LinearLayout searchLinearLayout = (LinearLayout) view.findViewById(R.id.team_list_search_layout);
        LinearLayout linearLayoutError = (LinearLayout) view.findViewById(R.id.linearlayout_error);
        if(usersList.size() == 0) {
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        } else if(usersList.get(0).getStatus() != 1) {
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            singleChoiceUserListAdapter.filterUsers(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @NonNull
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
                dismiss();
                getChoice.getOwner(null, false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        relativeLayoutToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
