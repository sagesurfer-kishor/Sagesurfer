package com.modules.caseload.werhope.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.modules.caseload.werhope.model.ProgressList;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UpdateCountAdapter extends RecyclerView.Adapter<UpdateCountAdapter.MyViewHolder>{

    private Context mContext;
    private final ArrayList<ProgressList> mProgressList;

    public UpdateCountAdapter(Context activity, ArrayList<ProgressList> progressList) {
        mContext = activity;
        mProgressList = progressList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_count_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTxtName.setText(mProgressList.get(position).getAdded_by());
        holder.mTxtNameDate.setText(getDate(mProgressList.get(position).getPosted_date()));
    }

    @Override
    public int getItemCount() {
        return mProgressList.size();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy '|' hh:mm a", cal).toString();
        return date;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtName, mTxtNameDate;

        public MyViewHolder(View itemView) {
            super(itemView);

            mTxtName = itemView.findViewById(R.id.txt_name);
            mTxtNameDate = itemView.findViewById(R.id.txt_name_date);
        }
    }
}
