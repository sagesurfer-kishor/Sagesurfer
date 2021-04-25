package com.modules.selfgoal;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SenjamLogBookAdapter extends RecyclerView.Adapter<SenjamLogBookAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<LogBookModel> logBookModelArrayList;
    private Date createdDate;
    private String createDateString;

    public SenjamLogBookAdapter(Context context, ArrayList<LogBookModel> logBookModelArrayList) {
        this.context = context;
        this.logBookModelArrayList = logBookModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.senjam_item_log_book_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LogBookModel model = logBookModelArrayList.get(position);
        holder.txtHeading.setText(model.getName());
        holder.txtDate.setText(getDateFormmat(model.getStartDate())+" to " + getDateFormmat(model.getEndDate()));

        holder.txtStatus.setText(model.getGoalStatus());
        if (model.getGoalStatusID().equalsIgnoreCase(General.GOAL_STATUS_ID_COMPLETE)){
            //holder.txtStatus.setText("Completed");
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_green));
            holder.imageViewStatus.setImageDrawable(context.getDrawable(R.drawable.complete_green_icon));
        }else if (model.getGoalStatusID().equalsIgnoreCase(General.GOAL_STATUS_ID_PARTIAL_COMPLETE)){
            holder.txtStatus.setText("Partially Complete");
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_yellow));
            holder.imageViewStatus.setImageDrawable(context.getDrawable(R.drawable.par_complete_orange_icon));
        }else if (model.getGoalStatusID().equalsIgnoreCase(General.GOAL_STATUS_ID_INPUT_NEEDED)){
            holder.txtStatus.setText("Input Needed");
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_gray));
            holder.imageViewStatus.setImageDrawable(context.getDrawable(R.drawable.in_needed_gray_icon));
        }else if (model.getGoalStatusID().equalsIgnoreCase(General.GOAL_STATUS_ID_MISSED)){
            holder.txtStatus.setText("Missed");
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.text_color_pink));
            holder.imageViewStatus.setImageDrawable(context.getDrawable(R.drawable.missed_red_icon));
        }else {
            holder.relativeLayoutStatus.setVisibility(View.GONE);
        }

    }

    public String getDateFormmat(String date){
        SimpleDateFormat createDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            createdDate = createDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        createDateString = formatter.format(createdDate);

        return createDateString;
    }

    @Override
    public int getItemCount() {
        return logBookModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtHeading, txtDate, txtStatus;
        private AppCompatImageView imageViewStatus;
        private RelativeLayout relativeLayoutStatus;
        public MyViewHolder(View itemView) {
            super(itemView);

            txtHeading = itemView.findViewById(R.id.txt_heading);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtStatus = itemView.findViewById(R.id.txt_status);
            imageViewStatus = itemView.findViewById(R.id.image_view_status);
//            relativeLayoutStatus = itemView.findViewById(R.id.relative_layout_status);
        }
    }
}
