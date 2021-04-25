package com.modules.caseload.mhaw.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
 import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.caseload.mhaw.activity.MhawAmendmentListActivity;
import com.modules.caseload.mhaw.model.MhawProgressList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MhawProgressListAdapter extends RecyclerView.Adapter<MhawProgressListAdapter.MyViewHolder> {
    private static final String TAG = MhawProgressListAdapter.class.getSimpleName();
    private final ArrayList<MhawProgressList> progressList;

    private Activity activity;
    private MhawProgressListAdapterListener progressListAdapterListener;

    public MhawProgressListAdapter(Activity activity, ArrayList<MhawProgressList> progressList, MhawProgressListAdapterListener progressListAdapterListener) {
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_mhaw_progress_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.titleName.setText(ChangeCase.toTitleCase(progressList.get(position).getTitle()));
//        viewHolder.postedDate.setText(getDate(progressList.get(position).getPosted_date()));
        viewHolder.postedDate.setText(ChangeCase.toTitleCase(progressList.get(position).getAdded_by_name()));
        viewHolder.typeValue.setText(ChangeCase.toTitleCase(progressList.get(position).getService()));
//        viewHolder.addedBy.setText(ChangeCase.toTitleCase(progressList.get(position).getAdded_by_name()) + " (" + ChangeCase.toTitleCase(progressList.get(position).getAdded_by_role()) + ")");
//        viewHolder.addedBy.setText(""+progressList.get(position).getFinalize_note());
        if (progressList.get(position).getFinalize_note() == 1) {
            viewHolder.addedBy.setText("Finalize");
        } else {
            viewHolder.addedBy.setText("Open");
        }
//        viewHolder.date.setText(dateCaps(progressList.get(position).getDate()));
//        viewHolder.duration.setText(progressList.get(position).getTotal_time().substring(0, 5));
//        viewHolder.attendance.setText(ChangeCase.toTitleCase(progressList.get(position).getAttendance()));
        //viewHolder.moodType.setTextColor(Color.parseColor(progressList.get(position).getMood_color()));

//        Glide.with(activity)
//                .load(progressList.get(position).getMood_image())
//                .thumbnail(0.5f)
//                .transition(withCrossFade())
//                .apply(new RequestOptions()
//                        .placeholder(GetThumbnails.userIcon(progressList.get(position).getMood_image()))
//                )
//                .into(viewHolder.moodImage);
        viewHolder.btnAmendments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int progressId = progressList.get(position).getId();
                Intent intent = new Intent(activity, MhawAmendmentListActivity.class);
                intent.putExtra("progressNoteId", progressId);
                intent.putExtra("AddedById", progressList.get(position).getAdded_by_id());
                intent.putExtra("FinalizeNoteId", progressList.get(position).getFinalize_note());
                activity.startActivity(intent);
                Log.e("ProgressNoteId", "" + progressList.get(position).getId());
            }
        });

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
        //        final TextView moodType, postedDate, addedBy;
        final TextView postedDate, addedBy, titleName, typeValue;
        //        final ImageView moodImage;
//        final TextView date, duration, attendance;
        final TextView attendance;
        final LinearLayout noteDetails;
        private final Button btnAmendments;

        MyViewHolder(View view) {
            super(view);
//            moodType = (TextView) view.findViewById(R.id.mood_type);
            postedDate = (TextView) view.findViewById(R.id.posted_date_txt);
            addedBy = (TextView) view.findViewById(R.id.added_by_txt);
            titleName = (TextView) view.findViewById(R.id.txt_title_name);
            typeValue = (TextView) view.findViewById(R.id.txt_type_value);
//            date = (TextView) view.findViewById(R.id.date_txt);
//            duration = (TextView) view.findViewById(R.id.durartion_txt);
            attendance = (TextView) view.findViewById(R.id.attendance_txt);
//            moodImage = (ImageView) view.findViewById(R.id.mood_image);
            noteDetails = (LinearLayout) view.findViewById(R.id.note_details_layout);
            btnAmendments = (Button) view.findViewById(R.id.btn_view_amendments);
        }
    }

    public interface MhawProgressListAdapterListener {
        void onNoteDetailsLayoutClicked(MhawProgressList progressNote);
    }
}

