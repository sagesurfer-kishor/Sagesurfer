package com.modules.caseload.werhope.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

 import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.caseload.werhope.activity.UpdateCountActivity;
import com.modules.caseload.werhope.model.ProgressList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ProgressListAdapter extends RecyclerView.Adapter<ProgressListAdapter.MyViewHolder> {
    private static final String TAG = ProgressListAdapter.class.getSimpleName();
    private final ArrayList<ProgressList> progressList;
    private Activity activity;
    private ProgressListAdapterListener progressListAdapterListener;

    public ProgressListAdapter(Activity activity, ArrayList<ProgressList> progressList, ProgressListAdapterListener progressListAdapterListener) {
        this.progressList = progressList;
        this.activity = activity;
        this.progressListAdapterListener = progressListAdapterListener;
    }

    @Override
    public long getItemId(int position) {
        return progressList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_progress_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.moodType.setText(ChangeCase.toTitleCase(progressList.get(position).getMood()));
        viewHolder.postedDate.setText(getDate(progressList.get(position).getPosted_date()));
        viewHolder.addedBy.setText(ChangeCase.toTitleCase(progressList.get(position).getAdded_by_name()) + " (" + ChangeCase.toTitleCase(progressList.get(position).getAdded_by_role()) + ")");
        viewHolder.date.setText(dateCaps(progressList.get(position).getDate()));
        viewHolder.duration.setText(progressList.get(position).getTotal_time().substring(0, 5));
        viewHolder.attendance.setText(ChangeCase.toTitleCase(progressList.get(position).getAttendance()));

        if (progressList.get(position).getCount() > 0){
            viewHolder.update_count.setVisibility(View.VISIBLE);
            viewHolder.update_count.setText(""+progressList.get(position).getCount());
        }else {
            viewHolder.update_count.setVisibility(View.GONE);
            viewHolder.update_count.setText(""+progressList.get(position).getCount());
        }

        viewHolder.update_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, UpdateCountActivity.class);
                intent.putExtra("progressNoteId",progressList.get(position).getId());
                activity.startActivity(intent);
            }
        });
        //viewHolder.moodType.setTextColor(Color.parseColor(progressList.get(position).getMood_color()));

        Glide.with(activity)
                .load(progressList.get(position).getMood_image())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(progressList.get(position).getMood_image()))
                )
                .into(viewHolder.moodImage);

        viewHolder.noteDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressListAdapterListener.onNoteDetailsLayoutClicked(progressList.get(position));
            }
        });
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy", cal).toString();
        return date;
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
        final TextView moodType, postedDate, addedBy,update_count;
        final ImageView moodImage;
        final TextView date, duration, attendance;
        final LinearLayout noteDetails;

        MyViewHolder(View view) {
            super(view);
            moodType = (TextView) view.findViewById(R.id.mood_type);
            update_count = (TextView) view.findViewById(R.id.update_count);
            postedDate = (TextView) view.findViewById(R.id.posted_date_txt);
            addedBy = (TextView) view.findViewById(R.id.added_by_txt);
            date = (TextView) view.findViewById(R.id.date_txt);
            duration = (TextView) view.findViewById(R.id.durartion_txt);
            attendance = (TextView) view.findViewById(R.id.attendance_txt);
            moodImage = (ImageView) view.findViewById(R.id.mood_image);
            noteDetails = (LinearLayout) view.findViewById(R.id.note_details_layout);
        }
    }

    public interface ProgressListAdapterListener {
        void onNoteDetailsLayoutClicked(ProgressList progressNote);
    }
}

