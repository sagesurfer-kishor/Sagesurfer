package com.modules.appointment.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

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

import com.modules.appointment.adapter.ServiceChoiceChoiceAdapter;
import com.modules.appointment.model.Staff;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 2/10/2020.
 */
public class ServiceSelectorDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = ServiceSelectorDialog.class.getSimpleName();
    private ArrayList<Staff> teamsArrayList, oldArray;

    private SelectedServices selectedServices;
    private ServiceChoiceChoiceAdapter multiChoiceTeamListAdapter;
    private RelativeLayout relativeLayoutToolbar;
    private String action;

    public interface SelectedServices {
        void selectedServices(ArrayList<Staff> teams_arrayList, boolean isSelected, String action);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        selectedServices = (SelectedServices) activity;
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
            action = b.getString(General.ACTION);

            if (action.equals(General.GET_SERVICES)) {
                teamsArrayList = (ArrayList<Staff>) b.getSerializable(General.GET_SERVICES);
            } else if (action.equals(General.GET_STAFF)) {
                teamsArrayList = (ArrayList<Staff>) b.getSerializable(General.GET_STAFF);
            } else {
                teamsArrayList = (ArrayList<Staff>) b.getSerializable(General.GET_OTHER_STAFF_DATA);
            }

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

        if (action.equalsIgnoreCase(General.GET_SERVICES)) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_services));
        } else if (action.equalsIgnoreCase(General.GET_STAFF)) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_members));
        } else {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_other_staff_members));
        }

        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        multiChoiceTeamListAdapter = new ServiceChoiceChoiceAdapter(getActivity().getApplicationContext(), teamsArrayList);
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

    // clone array to keep backup
    private void cloneArray() {
        for (Staff teams_ : teamsArrayList) {
            Staff team = new Staff();
            team.setId(teams_.getId());
            team.setStatus(teams_.getStatus());
            team.setSelected(teams_.isSelected());
            team.setName(teams_.getName());
            oldArray.add(team);
        }
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
                selectedServices.selectedServices(oldArray, false, action);
                break;
            case R.id.single_choice_team_dialog_submit:
                selectedServices.selectedServices(teamsArrayList, true, action);
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
