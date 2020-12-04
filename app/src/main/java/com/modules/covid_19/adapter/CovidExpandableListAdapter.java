package com.modules.covid_19.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.modules.covid_19.model.CovidModel;
import com.modules.covid_19.model.CovidTitleModel;
import com.modules.covid_19.model.OptionModel;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CovidExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<CovidTitleModel> headerList;
    private HashMap<String, List<CovidModel>> childList;
    ArrayList<String> radioOptionsList = new ArrayList<String>();
    private int count;

    public CovidExpandableListAdapter(Context context, List<CovidTitleModel> headerList,
                                      HashMap<String, List<CovidModel>> childList) {
        this.context = context;
        this.headerList = headerList;
        this.childList = childList;
    }


    @Override
    public int getGroupCount() {
        return headerList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.headerList.get(i).getQuestion().size();
//        return childList.get(headerList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return headerList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return headerList.get(i).getQuestion().get(i1);
//        return childList.get(headerList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return Long.parseLong(headerList.get(i).getQuestion().get(i1).getId());
//        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean b, View view, ViewGroup viewGroup) {

        ChildViewHolder childViewHolder;

        count = childPosition + 1;
        final CovidTitleModel model = headerList.get(groupPosition);

        final CovidTitleModel mainSections = headerList.get(groupPosition);
        final List<CovidModel> arrQuestions = mainSections.getQuestion();
        CovidModel objQuestion = arrQuestions.get(childPosition);
        if (view == null) {
            childViewHolder = new ChildViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.popup_list_item_layout, null);

            childViewHolder.mTxtQuestion = view.findViewById(R.id.txt_question_1);
            childViewHolder.mTxtQuestionNumber = view.findViewById(R.id.txt_question_no);
            childViewHolder.mRadioGroup = view.findViewById(R.id.radioGroup1);

            view.setTag(childViewHolder);
        }else {
            childViewHolder = (ChildViewHolder) view.getTag();
        }

        //int questionSize = headerList.get(groupPosition).getQuestion().size();
        final List<OptionModel> optionModels = headerList.get(groupPosition).getQuestion().get(childPosition).getOption();
        //int optionSize = headerList.get(groupPosition).getQuestion().get(childPosition).getOption().size();

        //for (int i = 0; i < questionSize; i++) {
            childViewHolder.mTxtQuestion.setText(objQuestion.getQues());
            childViewHolder.mRadioGroup.removeAllViews();
            int nSelectedCheckBoxPosition = 0;
            for (int j = 0; j <optionModels.size(); j++) {
                childViewHolder.radioButton = new RadioButton(context);
                //childViewHolder.radioButton.setId(Integer.parseInt(optionModels.get(j).getId()));
                childViewHolder.radioButton.setId(j);
                childViewHolder.radioButton.setText(optionModels.get(j).getAnswer());
                if(objQuestion.getSelectedRadioBtnId() == Integer.parseInt(optionModels.get(j).getId())){
                    nSelectedCheckBoxPosition = j;
                }
                childViewHolder.mRadioGroup.addView(childViewHolder.radioButton);
            }
        //}

        childViewHolder.mRadioGroup.setOnCheckedChangeListener(null);
        childViewHolder.mRadioGroup.clearCheck();
        if (objQuestion.isSelected()){
            //childViewHolder.mRadioGroup.check(objQuestion.getSelectedRadioBtnId());
            childViewHolder.mRadioGroup.check(nSelectedCheckBoxPosition);
        }

        childViewHolder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == -1) {
                    return;
                }
                OptionModel optMd = optionModels.get(i);
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                /*model.getQuestion().get(childPosition).setSelectedRadioBtnId(radioButton.getId());
                model.getQuestion().get(childPosition).setAnsId(String.valueOf(radioButton.getId()));*/

                model.getQuestion().get(childPosition).setSelectedRadioBtnId(Integer.parseInt(optMd.getId()));
                model.getQuestion().get(childPosition).setAnsId(String.valueOf(optMd.getId()));
                model.getQuestion().get(childPosition).setSelected(true);
                headerList.set(groupPosition, model);
            }
        });

        childViewHolder.mTxtQuestionNumber.setText("Q" + count +".");
        return view;
    }

    public String getAnsValue() {
        ArrayList<String> ansArray = new ArrayList<>();
        for (int i = 0; i < headerList.size(); i++) {
            CovidTitleModel item = headerList.get(i);
            for (int j = 0; j <item.getQuestion().size() ; j++) {
                CovidModel questionId = item.getQuestion().get(j);
                if (questionId.isSelected()) {
                    String questionID = questionId.getId() + "_" + questionId.getAnsId();
                    ansArray.add(questionID);
                }
            }

        }

        return TextUtils.join(",", ansArray);
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        HeaderViewHolder headerViewHolder;
//        String listTitle = (String) getGroup(i);
        if (view == null) {
            headerViewHolder = new HeaderViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.popup_list_group_layout, null);

            headerViewHolder.listTitleTextView = (TextView) view.findViewById(R.id.listTitle);
            headerViewHolder.listDetailTextView = (TextView) view.findViewById(R.id.listDetails);
            view.setTag(headerViewHolder);
        }else {
            headerViewHolder = (HeaderViewHolder) view.getTag();
        }

        headerViewHolder.listTitleTextView.setTypeface(null, Typeface.BOLD);
        headerViewHolder.listTitleTextView.setText(headerList.get(i).getTitle());
        String Description = headerList.get(i).getDescription();
        if (TextUtils.isEmpty(Description)) {
            headerViewHolder.listDetailTextView.setVisibility(View.GONE);
        } else {
            headerViewHolder.listDetailTextView.setVisibility(View.VISIBLE);
        }
        headerViewHolder.listDetailTextView.setText(Description);
        return view;
    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class HeaderViewHolder {
        TextView listTitleTextView , listDetailTextView;
    }

    private class ChildViewHolder {
        TextView mTxtQuestion, mTxtQuestionNumber;
        RadioGroup mRadioGroup;
        RadioButton radioButton;
    }

    public void scrolltoSection(int index) {
        this.scrolltoSection(index);


    }
}
