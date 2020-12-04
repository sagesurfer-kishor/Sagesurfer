package com.modules.assignment.werhope.dialog;

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

import com.modules.assignment.werhope.adapter.SingleChoiceAssignmentChoiceAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.snack.ShowSnack;

import java.util.ArrayList;

public class SingleChoiceAssignmentSelectorDialog extends DialogFragment implements View.OnClickListener {
    private EditText searchBox;
    private static final String TAG = SingleChoiceAssignmentSelectorDialog.class.getSimpleName();
    private ArrayList<Choices_> categoryArrayList;
    private String action;
    private GetCategorySelected getChoice;
    private SingleChoiceAssignmentChoiceAdapter singleChoiceAssignmentChoiceAdapter;
    private RelativeLayout relativeLayoutToolbar;

    public interface GetCategorySelected {
        void GetSelectedCategory(Choices_ choices_, String action, boolean isSelected);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getChoice = (GetCategorySelected) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        categoryArrayList = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null) {
            action = b.getString(General.ACTION);
            if (Actions_.GET_DISTRICT_LIST.equals(action)) {
                categoryArrayList = (ArrayList<Choices_>) b.getSerializable(Actions_.GET_DISTRICT_LIST);
            } else if (Actions_.GET_SCHOOL_LIST.equals(action)) {
                categoryArrayList = (ArrayList<Choices_>) b.getSerializable(Actions_.GET_SCHOOL_LIST);
            } else if (Actions_.GET_STUDENT_UNDER_SCHOOL.equals(action)) {
                categoryArrayList = (ArrayList<Choices_>) b.getSerializable(Actions_.GET_STUDENT_UNDER_SCHOOL);
            } else {
                categoryArrayList = (ArrayList<Choices_>) b.getSerializable(Actions_.GET_COACH_LIST);
            }

        } else {
            getChoice.GetSelectedCategory(null, null, false);
        }

        if (categoryArrayList == null || categoryArrayList.size() <= 0) {
            dismiss();
        }

        final AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView title = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        if (action.equalsIgnoreCase(Actions_.GET_DISTRICT_LIST)) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_district));
        }

        if (action.equalsIgnoreCase(Actions_.GET_SCHOOL_LIST)) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_school));
        }

        if (action.equalsIgnoreCase(Actions_.GET_STUDENT_UNDER_SCHOOL)) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_student));
        }

        if (action.equalsIgnoreCase(Actions_.GET_COACH_LIST)) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_coach));
        }

        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);
        singleChoiceAssignmentChoiceAdapter = new SingleChoiceAssignmentChoiceAdapter(getActivity().getApplicationContext(),action, categoryArrayList);
        listView.setAdapter(singleChoiceAssignmentChoiceAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0) {
                    ShowSnack.compatImageButtonWarning(backButton, getActivity()
                                    .getApplicationContext().getResources().getString(
                                    R.string.select_category),
                            getActivity().getApplicationContext());
                }
                if (singleChoiceAssignmentChoiceAdapter.getItem(position) != null) {
                    if (singleChoiceAssignmentChoiceAdapter.getItem(position).getStatus() == 1) {
                        getChoice.GetSelectedCategory(categoryArrayList.get(position), action, true);
                        dismiss();
                    }
                }
            }
        });

        searchBox = (EditText) view.findViewById(R.id.team_list_layout_search);
        searchBox.addTextChangedListener(textWatcher);

        LinearLayout searchLinearLayout = (LinearLayout) view.findViewById(R.id.team_list_search_layout);
        LinearLayout linearLayoutError = (LinearLayout) view.findViewById(R.id.linearlayout_error);
        if (categoryArrayList.size() == 0) {
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        } else if (categoryArrayList.get(0).getStatus() != 1) {
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        }
        return view;
    }

    // handle search string query
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            singleChoiceAssignmentChoiceAdapter.filter(s.toString());
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
                searchBox.setText("");
                dismiss();
                getChoice.GetSelectedCategory(null, null, false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        relativeLayoutToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}

