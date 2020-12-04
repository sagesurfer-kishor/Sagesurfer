package com.modules.sows.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
 import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.caseload.senjam.model.SenjamListModel;
import com.sagesurfer.collaborativecares.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SenjamSowsDetailsAdapter extends RecyclerView.Adapter<SenjamSowsDetailsAdapter.MyViewHolder> {
    private static final String TAG = SenjamSowsDetailsAdapter.class.getSimpleName();
    private final ArrayList<SenjamListModel> senjamListModelArrayList;

    private Activity activity;
    private SenjamSowsDetailsAdapterListener senjamSowsDetailsAdapterListener;

    public SenjamSowsDetailsAdapter(Activity activity, ArrayList<SenjamListModel> senjamListModelArrayList, SenjamSowsDetailsAdapterListener senjamSowsDetailsAdapterListener) {
        this.senjamListModelArrayList = senjamListModelArrayList;
        this.activity = activity;
        this.senjamSowsDetailsAdapterListener = senjamSowsDetailsAdapterListener;
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
//        viewHolder.mTextNumber.setText(ChangeCase.toTitleCase(senjamListModelArrayList.get(position).getSubject()));
//        viewHolder.mTextDateTime.setText(getDate(senjamListModelArrayList.get(position).getPosted_date()));
        int count = position + 1;
        viewHolder.mTextQuestion.setText(senjamListModelArrayList.get(position).getQuestion());
        viewHolder.mTextAnswer.setText(senjamListModelArrayList.get(position).getAnswer_name());
//        viewHolder.mainLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                senjamSowsDetailsAdapterListener.onNoteDetailsLayoutClicked(senjamListModelArrayList.get(position));
//            }
//        });
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy", cal).toString();
        return date;
    }

    private String getTime(String time) {
//        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date newdate = null;
        try {
            newdate = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String formattedDate = sdf.format(newdate);
        return formattedDate;
    }

    @SuppressLint("SimpleDateFormat")
    private String dateCaps(String dateValue) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("MMM dd, yyyy");
        String date = spf.format(newDate);
        return date;
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

    public interface SenjamSowsDetailsAdapterListener {
        void onNoteDetailsLayoutClicked(SenjamListModel senjamListModel);
    }
}

