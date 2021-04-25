package com.modules.onetime_dailysurvey.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.caseload.senjam.model.SenjamListModel;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

public class OneTimeSurveyDetailsAdapter extends RecyclerView.Adapter<OneTimeSurveyDetailsAdapter.MyViewHolder> {
    private static final String TAG = OneTimeSurveyDetailsAdapter.class.getSimpleName();
    private final ArrayList<SenjamListModel> senjamListModelArrayList;

    private Activity activity;
    private OneTimeDailySurveyDetailsAdapterListener oneTimeDailySurveyDetailsAdapterListener;

    public OneTimeSurveyDetailsAdapter(Activity activity, ArrayList<SenjamListModel> senjamListModelArrayList, OneTimeDailySurveyDetailsAdapterListener oneTimeDailySurveyDetailsAdapterListener) {
        this.senjamListModelArrayList = senjamListModelArrayList;
        this.activity = activity;
        this.oneTimeDailySurveyDetailsAdapterListener = oneTimeDailySurveyDetailsAdapterListener;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(senjamListModelArrayList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return senjamListModelArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_senjam_sows_details_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        int count = position + 1;
        viewHolder.mTextQuestion.setText(senjamListModelArrayList.get(position).getQuestion());
        viewHolder.mTextAnswer.setText(senjamListModelArrayList.get(position).getAns());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mainLinearLayout;
        private TextView mTextQuestion, mTextAnswer;

        MyViewHolder(View view) {
            super(view);
            mainLinearLayout =  view.findViewById(R.id.note_details_layout);
            mTextQuestion = view.findViewById(R.id.txt_question);
            mTextAnswer = view.findViewById(R.id.txt_answer);
        }
    }

    public interface OneTimeDailySurveyDetailsAdapterListener {
        void onOneTimeDailySurveyNoteDetailsLayoutClicked(SenjamListModel senjamListModel);
    }
}

