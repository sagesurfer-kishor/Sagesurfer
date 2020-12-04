package com.modules.selfgoal.werhope_self_goal.dialog;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import com.modules.selfgoal.werhope_self_goal.adapter.SingleChoiceSchoolListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.snack.ShowSnack;

import java.util.ArrayList;

public class SingleSchoolSelectorDialog extends DialogFragment implements View.OnClickListener {
    private EditText searchBox;
    private ArrayList<Choices_> categoryArrayList;
    private String action;
    private SingleChoiceSchoolListAdapter singleCategoryChoiceAdapter;
    private RelativeLayout relativeLayoutToolbar;

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        categoryArrayList = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null) {
            action = b.getString(General.ACTION);

            if (Actions_.STUDENT_LIST_OF_COACH.equals(action)) {
                categoryArrayList = (ArrayList<Choices_>) b.getSerializable(General.STUDENT_LIST_OF_COACH);
            } else if (Actions_.SCHOOL_LIST_OF_GRADE.equals(action)) {
                categoryArrayList = (ArrayList<Choices_>) b.getSerializable(General.SCHOOL_LIST_OF_GRADE);
            } else {
                categoryArrayList = (ArrayList<Choices_>) b.getSerializable(General.SCHOOL_LIST_OF_COACH);
            }

        } else {
            dismiss();
        }

        final AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
       backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView title = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);

        if (action.equalsIgnoreCase(General.SCHOOL_LIST_OF_COACH)) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_school));
        } else if (action.equalsIgnoreCase(General.STUDENT_LIST_OF_COACH)) {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_student));
        } else {
            title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_standard));
        }

        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);
        singleCategoryChoiceAdapter = new SingleChoiceSchoolListAdapter(getActivity().getApplicationContext(), categoryArrayList);
        listView.setAdapter(singleCategoryChoiceAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0) {
                    ShowSnack.compatImageButtonWarning(backButton, getActivity()
                                    .getApplicationContext().getResources().getString(
                                    R.string.select_category),
                            getActivity().getApplicationContext());
                }

                Intent intent = new Intent();
                intent.putExtra("position", "" + position);

                if (Actions_.STUDENT_LIST_OF_COACH.equals(action)) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), 2, intent);
                } else if (Actions_.SCHOOL_LIST_OF_GRADE.equals(action)) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), 3, intent);
                } else {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
                }

                dismiss();
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
        } else if (categoryArrayList.get(0).getStatus() == 1) {
            searchLinearLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            linearLayoutError.setVisibility(View.GONE);
        } else if (categoryArrayList.get(0).getStatus() == 0) {
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
            singleCategoryChoiceAdapter.filter(s.toString());
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
                searchBox.setText("");
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
