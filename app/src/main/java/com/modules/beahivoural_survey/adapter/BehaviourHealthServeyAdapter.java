package com.modules.beahivoural_survey.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.modules.beahivoural_survey.model.BehaviouralHealth;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.BehaviouralServey_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;

public class BehaviourHealthServeyAdapter extends ArrayAdapter<BehaviouralHealth> {
    private static final String TAG = BehaviourHealthServeyAdapter.class.getSimpleName();
    private List<BehaviouralHealth> behaviouralHealthList, questionAnsHealthList = new ArrayList<>();
    private Context mContext;
    private Activity activity;

    public BehaviourHealthServeyAdapter(Activity activity, List<BehaviouralHealth> behaviouralHealthList1) {
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
            view = layoutInflater.inflate(R.layout.behaviroural_list_item, parent, false);

            viewHolder.submittedDate = (TextView) view.findViewById(R.id.submitted_date);
            viewHolder.downArrow = (ImageView) view.findViewById(R.id.down_arrow);
            viewHolder.upArrow = (ImageView) view.findViewById(R.id.up_arrow);
            viewHolder.behaviouralLayout = (RelativeLayout) view.findViewById(R.id.behavioural_layout);
            viewHolder.questionLayout = (LinearLayout) view.findViewById(R.id.questions_layout);
            viewHolder.viewGroup = (ViewGroup) view.findViewById(R.id.questions_list_layout);
            viewHolder.questionsAnsListView = (ListView) view.findViewById(R.id.questions__listview);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (behaviouralHealthList.get(position).getStatus() == 1) {
            viewHolder.submittedDate.setText(getDate(behaviouralHealthList.get(position).getSubmitted_date()));
        }


        if (Integer.parseInt(Preferences.get(General.NOTIFICATION_BHS_REF_ID)) == behaviouralHealthList.get(position).getId()) {
            viewHolder.upArrow.setVisibility(View.VISIBLE);
            viewHolder.downArrow.setVisibility(View.GONE);
            viewHolder.questionLayout.setVisibility(View.VISIBLE);
            viewHolder.behaviouralLayout.setBackgroundResource(R.drawable.background_home_menu_blue);
            viewHolder.submittedDate.setTextColor(ContextCompat.getColor(activity, R.color.white));
            callQuestionsListAPI(viewHolder, behaviouralHealthList.get(position).getId());
            Preferences.save(General.NOTIFICATION_BHS_ID, 0);
            Preferences.save(General.NOTIFICATION_BHS_REF_ID, 0);
        }

        viewHolder.downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.questionLayout.setVisibility(View.VISIBLE);
                viewHolder.behaviouralLayout.setBackgroundResource(R.drawable.background_home_menu_blue);
                viewHolder.submittedDate.setTextColor(ContextCompat.getColor(activity, R.color.white));
                callQuestionsListAPI(viewHolder, behaviouralHealthList.get(position).getId());
            }
        });

        viewHolder.upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.GONE);
                viewHolder.downArrow.setVisibility(View.VISIBLE);
                viewHolder.questionLayout.setVisibility(View.GONE);

                viewHolder.behaviouralLayout.setBackgroundResource(R.color.white);
                viewHolder.submittedDate.setTextColor(ContextCompat.getColor(activity, R.color.black));
            }
        });

        return view;
    }

    private void callQuestionsListAPI(ViewHolder viewHolder, long id) {
        questionAnsHealthList.clear();

        viewHolder.viewGroup.removeAllViews();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_FROM_DETAILS);
        requestMap.put(General.ID, String.valueOf(id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_BEHAVIOURAL_HEALTH_SERVEY;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.v("response question-->", response);
                if (response != null) {
                    questionAnsHealthList = BehaviouralServey_.parseBehaviouralList(response, Actions_.GET_FROM_DETAILS, activity, TAG);
                    int i = 0;
                    if (questionAnsHealthList.size() > 0) {
                        if (questionAnsHealthList.get(0).getStatus() == 1) {
                            /*  QuestionsAnswersAdapter questionsAnswersAdapter = new QuestionsAnswersAdapter(activity, questionAnsHealthList);
                            viewHolder.questionsAnsListView.setAdapter(questionsAnswersAdapter);*/

                            for (BehaviouralHealth behaviouralHealth : questionAnsHealthList) {
                                long id_ = questionAnsHealthList.get(i).getId();
                                addQuestionLayout(behaviouralHealth, i, id_, viewHolder);
                                i++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // add dynamic comment layout row to layout
    private void addQuestionLayout(BehaviouralHealth behaviouralHealth, int position, final long id, ViewHolder viewHolder) {
        View view = LayoutInflater.from(activity).inflate(R.layout.questions_item, viewHolder.viewGroup, false);
        TextView srNo = (TextView) view.findViewById(R.id.sr_no);
        TextView question = (TextView) view.findViewById(R.id.question_txt);
        TextView answer = (TextView) view.findViewById(R.id.ans_txt);

        TextView srNo1 = (TextView) view.findViewById(R.id.sr1_no);
        TextView question1 = (TextView) view.findViewById(R.id.question1_txt);
        TextView answer1 = (TextView) view.findViewById(R.id.ans1_txt);

        TextView srNo2 = (TextView) view.findViewById(R.id.sr2_no);
        TextView question2 = (TextView) view.findViewById(R.id.question2_txt);
        TextView answer2 = (TextView) view.findViewById(R.id.ans2_txt);

        RelativeLayout headerLayout = view.findViewById(R.id.header_layout);
        RelativeLayout headerLayout1 = view.findViewById(R.id.header_layout1);
        RelativeLayout headerLayout2 = view.findViewById(R.id.header_layout2);

        if (behaviouralHealth.getQues1() != null) {
            if (behaviouralHealth.getQues1().length() > 0) {
                srNo.setText("1.");
                question.setText(behaviouralHealth.getQues1());
                answer.setText(behaviouralHealth.getAns1());
            } else {
                headerLayout.setVisibility(View.GONE);
            }
        } else {
            headerLayout.setVisibility(View.GONE);
        }

        if (behaviouralHealth.getQues2() != null) {
            if (behaviouralHealth.getQues2().length() > 0) {
                srNo1.setText("2.");
                question1.setText(behaviouralHealth.getQues2());
                answer1.setText(behaviouralHealth.getAns2());
            } else {
                headerLayout1.setVisibility(View.GONE);
            }
        } else {
            headerLayout1.setVisibility(View.GONE);
        }

        if (behaviouralHealth.getQues3() != null) {
            if (behaviouralHealth.getQues3().length() > 0) {
                srNo2.setText("3.");
                question2.setText(behaviouralHealth.getQues3());
                answer2.setText(behaviouralHealth.getAns3());
            } else {
                headerLayout2.setVisibility(View.GONE);
            }
        } else {
            headerLayout2.setVisibility(View.GONE);
        }

        viewHolder.viewGroup.addView(view);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

    private class ViewHolder {
        TextView submittedDate;
        ImageView downArrow, upArrow;
        RelativeLayout behaviouralLayout;
        LinearLayout questionLayout;
        private ViewGroup viewGroup;
        private ListView questionsAnsListView;
    }
}