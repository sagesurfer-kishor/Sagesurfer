package com.sagesurfer.selectors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import com.modules.consent.Consumer_;
import com.sagesurfer.adapters.ConsumerListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 14-07-2017
 * Last Modified on 15-12-2017
 */

public class ConsumerSelectorDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = ConsumerSelectorDialog.class.getSimpleName();
    private RelativeLayout relativeLayoutToolbar;

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        ArrayList<Consumer_> consumerArrayList = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null) {
            consumerArrayList = (ArrayList<Consumer_>) b.getSerializable(General.USER_LIST);
        } else {
            dismiss();
        }

        final AppCompatImageButton backButton = (AppCompatImageButton)
                view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView title = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_peer));
        } else {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_consumer));
        }

        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);
        final ConsumerListAdapter consumerListAdapter = new ConsumerListAdapter(getActivity()
                .getApplicationContext(), consumerArrayList);
        listView.setAdapter(consumerListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0) {
                    ShowSnack.compatImageButtonWarning(backButton, getActivity()
                                    .getApplicationContext().getResources().getString(
                                    R.string.please_select_team),
                            getActivity().getApplicationContext());
                }
                Intent intent = new Intent();
                intent.putExtra("position", "" + position);
                getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
                dismiss();
            }
        });

        EditText searchBox = (EditText) view.findViewById(R.id.team_list_layout_search);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                consumerListAdapter.filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        assert dialog.getWindow() != null;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressLint("WrongConstant")
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
                Intent intent = new Intent();
                intent.putExtra("position", "-1");
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, intent);
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
