package com.modules.selfgoal;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author girish M (girish@sagesurfer.com)
 *         Created on 26/4/16.
 *         Last Modified on 26/4/16.
 */

public class MilestoneDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = MilestoneDialogFragment.class.getSimpleName();

    private String goal_status = "0";

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.multiselect_list_view_layout, null);

        Activity activity = getActivity();

        ArrayList<HashMap<String, String>> milestoneList = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null && b.containsKey(General.MILESTONE)) {
            milestoneList = (ArrayList<HashMap<String, String>>) b.getSerializable(General.MILESTONE);
            if (b.containsKey(General.GOAL_STATUS)) {
                goal_status = b.getString(General.GOAL_STATUS);
            }
        } else {
            dismiss();
        }

        ImageView backButton = (ImageView) view.findViewById(R.id.multi_select_list_dialog_back);
        backButton.setOnClickListener(this);

        TextView titleText = (TextView) view.findViewById(R.id.multi_select_list_dialog_title);
        titleText.setText(activity.getApplicationContext().getResources().getString(R.string.milestone));

        ListView listView = (ListView) view.findViewById(R.id.multi_select_list_dialog_list_view);
        MilestoneListAdapter milestoneListAdapter = new MilestoneListAdapter(activity, milestoneList);
        listView.setAdapter(milestoneListAdapter);

        if (!goal_status.equalsIgnoreCase("0")) {
            listView.setEnabled(false);
        }
        TextView submitButton = (TextView) view.findViewById(R.id.multi_select_list_dialog_submit);
        submitButton.setVisibility(View.GONE);

        return view;
    }

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
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        setRetainInstance(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.multi_select_list_dialog_back:
                dismiss();
                break;
        }
    }
}
