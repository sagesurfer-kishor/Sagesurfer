package com.modules.onetime_dailysurvey.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.caseload.senjam.model.SenjamListModel;
import com.modules.onetime_dailysurvey.model.DailySurveyModel;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

public class DailySurveyDetailsAdapter extends RecyclerView.Adapter<DailySurveyDetailsAdapter.MyViewHolder> {
    private static final String TAG = DailySurveyDetailsAdapter.class.getSimpleName();
    private final ArrayList<DailySurveyModel> dailySurveyModelArrayList;

    private Activity activity;
    private DailySurveyDetailsAdapterListener dailySurveyDetailsAdapterListener;

    public DailySurveyDetailsAdapter(Activity activity, ArrayList<DailySurveyModel> dailySurveyModelArrayList, DailySurveyDetailsAdapterListener dailySurveyDetailsAdapterListener) {
        this.dailySurveyModelArrayList = dailySurveyModelArrayList;
        this.activity = activity;
        this.dailySurveyDetailsAdapterListener = dailySurveyDetailsAdapterListener;
    }

//    @Override
//    public long getItemId(int position) {
//        return Long.parseLong(dailySurveyModelArrayList.get(position).getQuestions().get(position).getId());
//    }

    @Override
    public int getItemCount() {
        return dailySurveyModelArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_senjam_daily_survey_details_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
//        viewHolder.mTextNumber.setText(ChangeCase.toTitleCase(senjamListModelArrayList.get(position).getSubject()));
//        viewHolder.mTextDateTime.setText(getDate(senjamListModelArrayList.get(position).getPosted_date()));
        int count = position + 1;
        viewHolder.mTextTitle.setText(dailySurveyModelArrayList.get(position).getTitle());

        String Description = dailySurveyModelArrayList.get(position).getDescription();

        // this condition is for show and hide description field
        // if Description is empty then we have hide that field else show that field
        if (TextUtils.isEmpty(Description)){
            viewHolder.mTextDesc.setVisibility(View.GONE);
        }else {
            viewHolder.mTextDesc.setVisibility(View.VISIBLE);
        }
        viewHolder.mTextDesc.setText(dailySurveyModelArrayList.get(position).getDescription());

        for (int i = 0; i < dailySurveyModelArrayList.get(position).getQuestions().size(); i++) {

            // Add TextView dynamically and set data in TextView

            TextView question=new TextView(activity);
            TextView answer=new TextView(activity);

            viewHolder.mainLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            viewHolder.mainLinearLayout.setPadding(10,10,10,10);

            question.setText(dailySurveyModelArrayList.get(position).getQuestions().get(i).getQues());
            answer.setText(dailySurveyModelArrayList.get(position).getQuestions().get(i).getAnswer());

            question.setPadding(12,12,12,12);
            answer.setPadding(12,12,12,12);

            question.setGravity(Gravity.START);
            answer.setGravity(Gravity.END);

            question.setTextColor(activity.getResources().getColor(R.color.black));
            question.setTextSize(16);

            answer.setTextColor(activity.getResources().getColor(R.color.black));
            answer.setTextSize(16);

            viewHolder.mainLinearLayoutQuestion.addView(question);
            viewHolder.mainLinearLayoutAnswer.addView(answer);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mainLinearLayout, mainLinearLayoutQuestion, mainLinearLayoutAnswer;
        private TextView mTextQuestion, mTextAnswer, mTextTitle, mTextDesc;

        MyViewHolder(View view) {
            super(view);
            mTextQuestion = view.findViewById(R.id.txt_question);
            mTextAnswer = view.findViewById(R.id.txt_answer);
            mTextTitle = view.findViewById(R.id.txt_title);
            mTextDesc = view.findViewById(R.id.txt_description);
            mainLinearLayout = view.findViewById(R.id.details_layout);
            mainLinearLayoutQuestion = view.findViewById(R.id.linear_question);
            mainLinearLayoutAnswer = view.findViewById(R.id.linear_answer);
        }
    }

    public interface DailySurveyDetailsAdapterListener {
        void onDailySurveyNoteDetailsLayoutClicked(SenjamListModel senjamListModel);
    }
}

