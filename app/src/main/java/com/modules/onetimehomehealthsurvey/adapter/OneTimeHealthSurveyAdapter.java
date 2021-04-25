package com.modules.onetimehomehealthsurvey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.modules.onetimehomehealthsurvey.model.OneTimeHealthSurvey;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

public class OneTimeHealthSurveyAdapter extends RecyclerView.Adapter<OneTimeHealthSurveyAdapter.MyViewHolder> {

    ArrayList<OneTimeHealthSurvey> mOneTimeHealthSurveys;
    private Context mContext;
    private int count = 0;
    ArrayList<String> radioOptionsList = new ArrayList<String>();

    public OneTimeHealthSurveyAdapter(Context context, ArrayList<OneTimeHealthSurvey> oneTimeHealthSurveys) {
        this.mContext = context;
        this.mOneTimeHealthSurveys = oneTimeHealthSurveys;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_one_time_health_survey_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        count = position + 1;
        final OneTimeHealthSurvey model = mOneTimeHealthSurveys.get(position);
        holder.mTxtViewQuestionNumber.setText("Q"+ count +".");
        holder.mTxtViewQuestion.setText(model.getQues());

        int size = model.getOptions().size();
        for (int j = 0; j < mOneTimeHealthSurveys.get(position).getOptions().size(); j++) {
            String radioItemName = mOneTimeHealthSurveys.get(position).getOptions().get(j).getAnswer();
            radioOptionsList.add(radioItemName);
        }

        holder.mRadioGroup.removeAllViews();
        int totalCount = mOneTimeHealthSurveys.get(position).getOptions().size();
        final RadioButton[] rb = new RadioButton[totalCount];
        for (int j = 0; j < totalCount; j++) {
            rb[j] = new RadioButton(mContext);
            rb[j].setText(radioOptionsList.get(j));
            rb[j].setId(Integer.parseInt(mOneTimeHealthSurveys.get(position).getOptions().get(j).getId()));
            holder.mRadioGroup.addView(rb[j]); //the RadioButtons are added to the radioGroup instead of the layout
        }

        Log.d("SELECTED", "Position = "+position + " || ID = "+model.getSelectedRadioBtnId()+"");

        // solution 1 - retain check in position
        holder.mRadioGroup.setOnCheckedChangeListener(null);
        holder.mRadioGroup.clearCheck();
        if (model.isSelected()){
            holder.mRadioGroup.check(model.getSelectedRadioBtnId());
        }

        holder.mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == -1){
                    return;
                }

                Log.d("RADIO_ID", "Position = "+position + " || ID = "+i);
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                model.setSelectedRadioBtnId(radioButton.getId());
                model.setAnsId(String.valueOf(radioButton.getId()));
                model.setSelected(true);
                mOneTimeHealthSurveys.set(position, model);
            }
        });

    }

    // solution 2 - return string of ques & ans array comma separated
    public String getAnsValue(){
        ArrayList<String> ansArray = new ArrayList<>();
        for (int i = 0; i < mOneTimeHealthSurveys.size(); i++) {
            OneTimeHealthSurvey item = mOneTimeHealthSurveys.get(i);
            if (item.isSelected()){
                String value = item.getId() + "_" + item.getAnsId();
                ansArray.add(value);
            }
        }

        return TextUtils.join(",", ansArray);
    }

    @Override
    public int getItemCount() {
        return mOneTimeHealthSurveys.size();
    }

//    public int getItemViewType(int position) {
//        return position;
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtViewQuestion, mTxtViewQuestionNumber;
        private RadioGroup mRadioGroup;
        private RadioButton mRadioButton1, mRadioButton2, mRadioButton3, mRadioButton4, mRadioButton5;
        public MyViewHolder(View itemView) {
            super(itemView);

            mTxtViewQuestion = itemView.findViewById(R.id.txt_question_1);
            mTxtViewQuestionNumber = itemView.findViewById(R.id.txt_question_no);
            mRadioGroup = itemView.findViewById(R.id.radioGroup1);
        }
    }
}
