package com.sagesurfer.selectors;

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

import com.sagesurfer.adapters.SingleChoiceTeamListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.snack.ShowSnack;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 14-07-2017
 * Last Modified on 15-12-2017
 */

public class SingleTeamSelectorDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = SingleTeamSelectorDialog.class.getSimpleName();
    private ArrayList<Teams_> teamsArrayList;
    private int menu_id = 0;

    private GetChoice getChoice;
    private SingleChoiceTeamListAdapter singleChoiceTeamListAdapter;
    private RelativeLayout relativeLayoutToolbar;
    //private MainActivityInterface mainActivityInterface;

    public interface GetChoice {
        void getChoice(Teams_ teams_, boolean isSelected, int menu_id);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //mainActivityInterface = (MainActivityInterface) activity;
        getChoice = (GetChoice) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        teamsArrayList = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null) {
            teamsArrayList = (ArrayList<Teams_>) b.getSerializable(General.TEAM_LIST);
            if (teamsArrayList == null) {
                dismiss();
            }
            menu_id = b.getInt("menu_id", 0);
        } else {
            dismiss();
            getChoice.getChoice(null, false, menu_id);
        }

        final AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView title = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_team));

        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);

        singleChoiceTeamListAdapter = new SingleChoiceTeamListAdapter(getActivity().getApplicationContext(), teamsArrayList);
        listView.setAdapter(singleChoiceTeamListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0) {
                    ShowSnack.compatImageButtonWarning(backButton, getActivity()
                            .getApplicationContext().getResources().getString(
                                    R.string.please_select_team), getActivity().getApplicationContext());
                }
                if (teamsArrayList.get(position).getStatus() == 1) {
                    getChoice.getChoice(teamsArrayList.get(position), true, menu_id);
                    dismiss();
                }
            }
        });

        EditText searchBox = (EditText) view.findViewById(R.id.team_list_layout_search);
        searchBox.addTextChangedListener(textWatcher);

        LinearLayout searchLinearLayout = (LinearLayout) view.findViewById(R.id.team_list_search_layout);
        LinearLayout linearLayoutError = (LinearLayout) view.findViewById(R.id.linearlayout_error);
        if (teamsArrayList.size() == 0) {
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        } else if (teamsArrayList.get(0).getStatus() != 1) {
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
            singleChoiceTeamListAdapter.filterTeams(s.toString());
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
                dismiss();
                getChoice.getChoice(null, false, menu_id);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        relativeLayoutToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
