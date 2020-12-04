package com.modules.beahivoural_survey.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.modules.beahivoural_survey.model.BehaviouralHealth;
import com.sagesurfer.collaborativecares.R;

import java.util.List;

public class QuestionsAnswersAdapter extends ArrayAdapter<BehaviouralHealth> {
    private static final String TAG = QuestionsAnswersAdapter.class.getSimpleName();
    private List<BehaviouralHealth> behaviouralHealthList;
    private Context mContext;
    private Activity activity;

    public QuestionsAnswersAdapter(Activity activity, List<BehaviouralHealth> behaviouralHealthList1) {
        super(activity, 0, behaviouralHealthList1);
        this.behaviouralHealthList = behaviouralHealthList1;
        this.mContext = activity.getApplicationContext();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return behaviouralHealthList.size();
    }

    @Override
    public BehaviouralHealth getItem(int position) {
        if (behaviouralHealthList != null && behaviouralHealthList.size() > 0) {
            return behaviouralHealthList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return behaviouralHealthList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.questions_item, parent, false);

            viewHolder.srNo = (TextView) view.findViewById(R.id.sr_no);
            viewHolder.question = (TextView) view.findViewById(R.id.question_txt);
            viewHolder.answer = (TextView) view.findViewById(R.id.ans_txt);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (behaviouralHealthList.get(position).getStatus() == 1) {
            BehaviouralHealth behaviouralHealth= behaviouralHealthList.get(position);
            viewHolder.srNo.setText("1.");
           // viewHolder.question.setText(behaviouralHealth.getQuestionAnsArrayList().get(position).getClass().getName());
            viewHolder.answer.setText("1.");

        }


        return view;
    }

    private class ViewHolder {
        TextView srNo,question,answer;
    }
}