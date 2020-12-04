package com.modules.calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.parser.Participant_;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 18-08-2017
 * Last Modified on 13-12-2017
 */


public class ParticipantsListDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = ParticipantsListDialog.class.getSimpleName();
    private RelativeLayout relativeLayoutToolbar;

    @SuppressWarnings("unchecked")
    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        ArrayList<Participant_> participantArrayList = new ArrayList<>();

        String title = getActivity().getApplicationContext().getResources().getString(R.string.participants);
        Bundle b = getArguments();
        if (b != null) {
            participantArrayList = (ArrayList<Participant_>) b.getSerializable(General.USER_LIST);
            title = b.getString(General.TITLE);
        } else {
            dismiss();
        }

        AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView titleText = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        titleText.setText(title);

        LinearLayout searchLayout = (LinearLayout) view.findViewById(R.id.team_list_search_layout);
        searchLayout.setVisibility(View.GONE);

        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);

        // Set list adapter for participants list view
        ParticipantsListAdapter participantsListAdapter = new ParticipantsListAdapter(getActivity(), participantArrayList);
        listView.setAdapter(participantsListAdapter);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressLint("WrongConstant")
    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
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
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        relativeLayoutToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
