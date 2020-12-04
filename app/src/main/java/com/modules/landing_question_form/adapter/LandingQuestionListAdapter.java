package com.modules.landing_question_form.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.modules.landing_question_form.module.LandingQuestion_;
import com.sagesurfer.adapters.MessageBoardListAdapter;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 7/16/2019.
 */
public class LandingQuestionListAdapter extends ArrayAdapter<LandingQuestion_> {

    private static final String TAG = MessageBoardListAdapter.class.getSimpleName();
    private final ArrayList<LandingQuestion_> landingQuestionList;

    private final Activity activity;
    int optionValue = 0;

    public LandingQuestionListAdapter(Activity activity, ArrayList<LandingQuestion_> landingQuestionList, int optionValue) {
        super(activity, 0, landingQuestionList);
        this.landingQuestionList = landingQuestionList;
        this.activity = activity;
        this.optionValue = optionValue;
    }

    @Override
    public int getCount() {
        return landingQuestionList.size();
    }

    @Override
    public LandingQuestion_ getItem(int position) {
        if (landingQuestionList != null && landingQuestionList.size() > 0) {
            return landingQuestionList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        final ViewHolder viewHolder = new ViewHolder();

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        view = layoutInflater.inflate(R.layout.landing_question_list_item, parent, false);

        viewHolder.textViewSerialNumber = (TextView) view.findViewById(R.id.textview_serial_number);
        viewHolder.textViewQuestion = (TextView) view.findViewById(R.id.textview_question);
        viewHolder.linearLayoutTwoQuestions = (LinearLayout) view.findViewById(R.id.linearlayout_two_questions);
        viewHolder.linearLayoutFiveQuestions = (LinearLayout) view.findViewById(R.id.linearlayout_five_questions);
        viewHolder.radioButtonYes = (RadioButton) view.findViewById(R.id.radiobutton_yes);
        viewHolder.radioButtonNo = (RadioButton) view.findViewById(R.id.radiobutton_no);
        viewHolder.radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        viewHolder.view = (View) view.findViewById(R.id.view);
        viewHolder.linearLayoutYesOptions = (LinearLayout) view.findViewById(R.id.linearlayout_yes_options);
        viewHolder.linearLayoutMultiple = (LinearLayout) view.findViewById(R.id.linearlayout_multiple);
        viewHolder.radioButtonMultiple = (RadioButton) view.findViewById(R.id.radiobutton_multiple);
        viewHolder.textViewMultiple = (TextView) view.findViewById(R.id.textview_multiple);
        viewHolder.linearLayoutOnce = (LinearLayout) view.findViewById(R.id.linearlayout_once);
        viewHolder.radioButtonOnce = (RadioButton) view.findViewById(R.id.radiobutton_once);
        viewHolder.textViewOnce = (TextView) view.findViewById(R.id.textview_once);
        viewHolder.linearLayoutNever = (LinearLayout) view.findViewById(R.id.linearlayout_never);
        viewHolder.radioButtonNever = (RadioButton) view.findViewById(R.id.radiobutton_never);
        viewHolder.textViewNever = (TextView) view.findViewById(R.id.textview_never);

        view.setTag(viewHolder);

        if (landingQuestionList.get(position).getStatus() == 1) {
            int serialNumber = position + 1;
            viewHolder.textViewSerialNumber.setText(serialNumber + ".");
            viewHolder.textViewQuestion.setText(landingQuestionList.get(position).getQuestion().get(0).getQues());
            if (optionValue == 2) {
                viewHolder.linearLayoutTwoQuestions.setVisibility(View.VISIBLE);
                viewHolder.linearLayoutFiveQuestions.setVisibility(View.GONE);
                viewHolder.textViewMultiple.setText(landingQuestionList.get(position).getAnswer().get(0).getAnswer());
                viewHolder.textViewOnce.setText(landingQuestionList.get(position).getAnswer().get(1).getAnswer());
                viewHolder.textViewNever.setText(landingQuestionList.get(position).getAnswer().get(2).getAnswer());

                if (viewHolder.radioButtonYes.isChecked()) {
                    landingQuestionList.get(position).setSelected(true);
                } else {
                    landingQuestionList.get(position).setSelected(false);

                }

                if (viewHolder.radioButtonMultiple.isChecked()) {
                    landingQuestionList.get(position).getAnswer().get(0).setSelected(true);
                    landingQuestionList.get(position).getAnswer().get(1).setSelected(false);
                    landingQuestionList.get(position).getAnswer().get(2).setSelected(false);
                } else if (viewHolder.radioButtonMultiple.isChecked()) {
                    landingQuestionList.get(position).getAnswer().get(0).setSelected(false);
                    landingQuestionList.get(position).getAnswer().get(1).setSelected(true);
                    landingQuestionList.get(position).getAnswer().get(2).setSelected(false);
                } else {
                    landingQuestionList.get(position).getAnswer().get(0).setSelected(false);
                    landingQuestionList.get(position).getAnswer().get(1).setSelected(false);
                    landingQuestionList.get(position).getAnswer().get(2).setSelected(true);
                }
            } else {
                viewHolder.linearLayoutTwoQuestions.setVisibility(View.GONE);
                viewHolder.linearLayoutFiveQuestions.setVisibility(View.VISIBLE);

                for (int row = 0; row < 1; row++) {
                    RadioGroup ll = new RadioGroup(activity);
                    for (int i = 0; i < landingQuestionList.get(position).getAnswer().size(); i++) {
                        final RadioButton rdbtn = new RadioButton(activity);
                        if (i == 0) {
                            rdbtn.setChecked(true);
                            landingQuestionList.get(position).getAnswer().get(0).setSelected(true);
                        }
                        rdbtn.setId(View.generateViewId());
                        rdbtn.setText(landingQuestionList.get(position).getAnswer().get(i).getAnswer());
                        ll.addView(rdbtn);
                        rdbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                for (int i = 0; i < landingQuestionList.get(position).getAnswer().size(); i++) {
                                    boolean isChecked = rdbtn.isChecked();
                                    // If the radiobutton that has changed in check state is now checked...
                                    if (isChecked && landingQuestionList.get(position).getAnswer().get(i).getAnswer().equalsIgnoreCase(rdbtn.getText().toString())) {
                                        landingQuestionList.get(position).getAnswer().get(i).setSelected(true);
                                    } else {
                                        landingQuestionList.get(position).getAnswer().get(i).setSelected(false);
                                    }
                                }
                            }
                        });
                    }
                    viewHolder.radioGroup.addView(ll);
                }
            }

            viewHolder.radioButtonYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.linearLayoutYesOptions.setVisibility(View.VISIBLE);
                    viewHolder.view.setVisibility(View.VISIBLE);
                    landingQuestionList.get(position).setSelected(true);
                    viewHolder.radioButtonNo.setChecked(false);
                }
            });

            viewHolder.radioButtonNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.linearLayoutYesOptions.setVisibility(View.GONE);
                    viewHolder.view.setVisibility(View.GONE);
                    landingQuestionList.get(position).setSelected(false);
                    viewHolder.radioButtonYes.setChecked(false);
                }
            });

            viewHolder.radioButtonMultiple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    landingQuestionList.get(position).getAnswer().get(0).setSelected(true);
                    landingQuestionList.get(position).getAnswer().get(1).setSelected(false);
                    landingQuestionList.get(position).getAnswer().get(2).setSelected(false);
                    viewHolder.radioButtonOnce.setChecked(false);
                    viewHolder.radioButtonNever.setChecked(false);
                }
            });

            viewHolder.radioButtonOnce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    landingQuestionList.get(position).getAnswer().get(0).setSelected(false);
                    landingQuestionList.get(position).getAnswer().get(1).setSelected(true);
                    landingQuestionList.get(position).getAnswer().get(2).setSelected(false);
                    viewHolder.radioButtonMultiple.setChecked(false);
                    viewHolder.radioButtonNever.setChecked(false);
                }
            });

            viewHolder.radioButtonNever.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    landingQuestionList.get(position).getAnswer().get(0).setSelected(false);
                    landingQuestionList.get(position).getAnswer().get(1).setSelected(false);
                    landingQuestionList.get(position).getAnswer().get(2).setSelected(true);
                    viewHolder.radioButtonMultiple.setChecked(false);
                    viewHolder.radioButtonOnce.setChecked(false);
                }
            });

        }
        return view;
    }


    private class ViewHolder {
        TextView textViewSerialNumber, textViewQuestion, textViewMultiple, textViewOnce, textViewNever;
        RadioButton radioButtonYes, radioButtonNo, radioButtonMultiple, radioButtonOnce, radioButtonNever;
        RadioGroup radioGroup;
        LinearLayout linearLayoutTwoQuestions, linearLayoutFiveQuestions, linearLayoutYesOptions, linearLayoutMultiple, linearLayoutOnce, linearLayoutNever;
        View view;
    }
}
