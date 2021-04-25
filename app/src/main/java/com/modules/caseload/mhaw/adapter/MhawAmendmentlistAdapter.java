package com.modules.caseload.mhaw.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.modules.caseload.mhaw.model.MhawAmendmentList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MhawAmendmentlistAdapter extends RecyclerView.Adapter<MhawAmendmentlistAdapter.MyViewHolder> {

    private ArrayList<MhawAmendmentList> mhawAmendmentLists;
    private MhawAmendmentAdapterListener progressListAdapterListener;
    private MhawAmendmentAdapterListenerEdit EditAmendmentAdapterListener;
    private Activity activity;


    public MhawAmendmentlistAdapter(Activity activity, ArrayList<MhawAmendmentList> progressList, MhawAmendmentAdapterListener progressListAdapterListener, MhawAmendmentAdapterListenerEdit editAmendmentAdapterListener) {
        this.mhawAmendmentLists = progressList;
        this.activity = activity;
        this.progressListAdapterListener = progressListAdapterListener;
        this.EditAmendmentAdapterListener = editAmendmentAdapterListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mhaw_amendment_list_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.mTxtDate.setText(getDate(mhawAmendmentLists.get(position).getPosted_date()));
        Log.e("date", getDate(mhawAmendmentLists.get(position).getPosted_date()));
        holder.mTxtTime.setText(getTime(mhawAmendmentLists.get(position).getPosted_date()));
        holder.mTxtDescription.setText(ChangeCase.toSentenceCase(mhawAmendmentLists.get(position).getDescription()));
        holder.mTxtAddedByName.setText(ChangeCase.toTitleCase(mhawAmendmentLists.get(position).getAdded_by_name()));

        holder.mImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressListAdapterListener.onNoteDetailsLayoutClicked(position, mhawAmendmentLists.get(position));
            }
        });

        holder.mImgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAmendmentAdapterListener.onNoteDetailsLayoutClickedEdit(position, mhawAmendmentLists.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mhawAmendmentLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtTime, mTxtDate, mTxtDescription, mTxtAddedByName;
        private AppCompatImageView mImgEdit, mImgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);

            mTxtTime = itemView.findViewById(R.id.time_txt);
            mTxtDate = itemView.findViewById(R.id.date_txt);
            mTxtDescription = itemView.findViewById(R.id.description_txt);
            mTxtAddedByName = itemView.findViewById(R.id.added_by_name_txt);
            mImgEdit = itemView.findViewById(R.id.imageview_edit);
            mImgDelete = itemView.findViewById(R.id.imageview_delete);
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy", cal).toString();
        return date;
    }

    private String getTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time * 1000);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String date = DateFormat.format("MMM dd, yyyy hh:mm a", c).toString();
        Date newdate = null;
        try {
//            newdate = df.parse(String.valueOf(time));
            newdate = df.parse(date);
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

    public interface MhawAmendmentAdapterListener {
        void onNoteDetailsLayoutClicked(int position, MhawAmendmentList progressNote);
    }

    public interface MhawAmendmentAdapterListenerEdit {
        void onNoteDetailsLayoutClickedEdit(int position, MhawAmendmentList progressNote);
    }
}
