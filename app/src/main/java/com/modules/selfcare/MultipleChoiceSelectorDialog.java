package com.modules.selfcare;

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

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.selectors.MultiUserSelectorDialog;

import java.util.ArrayList;

/**
 * Created by Monika on 12/27/2018.
 */

public class MultipleChoiceSelectorDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = MultiUserSelectorDialog.class.getSimpleName();
    private ArrayList<Choices_> categoryArrayList, oldArray;

    private SelectedAge selectedAge;
    private MultiChoiceChoiceAdapter multiChoiceChoiceListAdapter;
    private RelativeLayout relativeLayoutToolbar;
    private boolean isTeam = false;
    String selfcareContentId = "";

    interface SelectedAge {
        void selectedAge(ArrayList<Choices_> category_arrayList, String action, boolean isSelected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        selectedAge = (SelectedAge) activity;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_choice_team_dialog_layout, null);

        categoryArrayList = new ArrayList<>();
        oldArray = new ArrayList<>();

        Bundle b = getArguments();
        if (b != null) {
            categoryArrayList = (ArrayList<Choices_>) b.getSerializable(Actions_.GET_AGE);
            selfcareContentId = b.getString(General.ID);
            if (categoryArrayList != null) {
                cloneArray();
            }
            if (selfcareContentId == null) {
                selfcareContentId = "";
            }
        } else {
            dismiss();
        }

        final AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.single_choice_team_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView title = (TextView) view.findViewById(R.id.single_choice_team_dialog_title);
        title.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_age));

        ListView listView = (ListView) view.findViewById(R.id.team_list_view_layout_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        multiChoiceChoiceListAdapter = new MultiChoiceChoiceAdapter(getActivity().getApplicationContext(), categoryArrayList);
        listView.setAdapter(multiChoiceChoiceListAdapter);

        EditText searchBox = (EditText) view.findViewById(R.id.team_list_layout_search);
        searchBox.addTextChangedListener(textWatcher);

        TextView submitButton = (TextView) view.findViewById(R.id.single_choice_team_dialog_submit);
        submitButton.setOnClickListener(this);
        submitButton.setVisibility(View.VISIBLE);

        LinearLayout searchLinearLayout = (LinearLayout) view.findViewById(R.id.team_list_search_layout);
        LinearLayout linearLayoutError = (LinearLayout) view.findViewById(R.id.linearlayout_error);
        if(categoryArrayList.size() == 0) {
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        } else if(categoryArrayList.get(0).getStatus() != 1) {
            searchLinearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void cloneArray() {
        for (Choices_ choices : categoryArrayList) {
            Choices_ choice = new Choices_();
            choice.setIs_selected(choices.getIs_selected());
            choice.setId(choices.getId());
            choice.setStatus(choices.getStatus());
            choice.setName(choices.getName());
            oldArray.add(choice);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            multiChoiceChoiceListAdapter.filterChoice(s.toString());
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
                selectedAge.selectedAge(oldArray, selfcareContentId, false);
                break;
            case R.id.single_choice_team_dialog_submit:
                selectedAge.selectedAge(categoryArrayList,selfcareContentId,  true);
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
