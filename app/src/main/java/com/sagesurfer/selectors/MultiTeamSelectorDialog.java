package com.sagesurfer.selectors;

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

import com.sagesurfer.adapters.MultiChoiceTeamListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Teams_;

import java.util.ArrayList;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 14-07-2017
 * Last Modified on 15-12-2017
 */

public class MultiTeamSelectorDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = MultiTeamSelectorDialog.class.getSimpleName();
    private ArrayList<Teams_> teamsArrayList, oldArray;

    private SelectedTeams selectedTeams;
    private MultiChoiceTeamListAdapter multiChoiceTeamListAdapter;
    private RelativeLayout relativeLayoutToolbar;

    public interface SelectedTeams {
        void selectedTeams(ArrayList<Teams_> teams_arrayList, boolean isSelected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        selectedTeams = (SelectedTeams) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        teamsArrayList = new ArrayList<>();
        oldArray = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null) {
            teamsArrayList = (ArrayList<Teams_>) b.getSerializable(General.TEAM_LIST);
            if (teamsArrayList != null) {
                cloneArray();
            }
        } else {
            dismiss();
        }

        final AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView title = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_teams));

        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        multiChoiceTeamListAdapter = new MultiChoiceTeamListAdapter(getActivity().getApplicationContext(), teamsArrayList);
        listView.setAdapter(multiChoiceTeamListAdapter);

        EditText searchBox = (EditText) view.findViewById(R.id.team_list_layout_search);
        searchBox.addTextChangedListener(textWatcher);

        TextView submitButton = (TextView) view.findViewById(R.id.single_choice_team_dialog_submit);
        submitButton.setOnClickListener(this);
        submitButton.setVisibility(View.VISIBLE);

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

    // clone array to keep backup
    private void cloneArray() {
        for (Teams_ teams_ : teamsArrayList) {
            Teams_ team = new Teams_();
            team.setId(teams_.getId());
            team.setStatus(teams_.getStatus());
            team.setSelected(teams_.getSelected());
            team.setPhoto(teams_.getPhoto());
            team.setMembers(teams_.getMembers());
            team.setBanner(teams_.getBanner());
            team.setName(teams_.getName());
            team.setOwnerId(teams_.getOwnerId());
            team.setPermission(teams_.getPermission());

            oldArray.add(team);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            multiChoiceTeamListAdapter.filterTeams(s.toString());
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
                selectedTeams.selectedTeams(oldArray, false);
                break;
            case R.id.single_choice_team_dialog_submit:
                selectedTeams.selectedTeams(teamsArrayList, true);
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
