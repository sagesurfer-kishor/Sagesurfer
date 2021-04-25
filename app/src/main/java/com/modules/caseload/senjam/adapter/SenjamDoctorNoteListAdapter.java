package com.modules.caseload.senjam.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.caseload.senjam.model.SenjamListModel;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SenjamDoctorNoteListAdapter extends RecyclerView.Adapter<SenjamDoctorNoteListAdapter.MyViewHolder> {
    private static final String TAG = SenjamDoctorNoteListAdapter.class.getSimpleName();
    private final ArrayList<SenjamListModel> senjamListModelArrayList;

    private Activity activity;
    private SenjamDoctorNoteListAdapterListener senjamDoctorNoteListAdapterListener;
    private SenjamEditButtonListener senjamEditButtonListener;
    private SenjamDeleteButtonListener senjamDeleteButtonListener;

    public SenjamDoctorNoteListAdapter(Activity activity, ArrayList<SenjamListModel> senjamListModelArrayList
            , SenjamDoctorNoteListAdapterListener senjamDoctorNoteListAdapterListener,SenjamEditButtonListener senjamEditButtonListener, SenjamDeleteButtonListener senjamDeleteButtonListener) {
        this.senjamListModelArrayList = senjamListModelArrayList;
        this.activity = activity;
        this.senjamDoctorNoteListAdapterListener = senjamDoctorNoteListAdapterListener;
        this.senjamEditButtonListener = senjamEditButtonListener;
        this.senjamDeleteButtonListener = senjamDeleteButtonListener;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_senjam_doctor_note_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.mTextTitle.setText(ChangeCase.toTitleCase(senjamListModelArrayList.get(position).getSubject()));
        viewHolder.mTextAddedByValue.setText(ChangeCase.toTitleCase(senjamListModelArrayList.get(position).getAdded_by()));
        viewHolder.mTextPatientNameValue.setText(ChangeCase.toTitleCase(Preferences.get(General.CONSUMER_NAME)));
        viewHolder.mTextCreateDate.setText(getDate(senjamListModelArrayList.get(position).getPosted_date()));
        viewHolder.mTextUpdatedDate.setText(getDate(senjamListModelArrayList.get(position).getUpdated_date()));

        viewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senjamDoctorNoteListAdapterListener.onNoteDetailsLayoutClicked(senjamListModelArrayList.get(position));
            }
        });

        viewHolder.mTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senjamEditButtonListener.onSenjamEditButtonClickedListener(senjamListModelArrayList.get(position),position);
            }
        });

        viewHolder.mTextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senjamDeleteButtonListener.onSenjamDeleteButtonClickedListener(senjamListModelArrayList.get(position),position);
            }
        });
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy hh:mm:ss a", cal).toString();
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
        private final CardView mCardView;
        private TextView mTextEdit, mTextDelete, mTextTitle, mTextDescription;
        private TextView mTextAddedByValue, mTextPatientNameValue, mTextCreateDate, mTextUpdatedDate;

        MyViewHolder(View view) {
            super(view);
            mCardView =  view.findViewById(R.id.cardView);
            mTextTitle = view.findViewById(R.id.txt_title);
            mTextAddedByValue = view.findViewById(R.id.txt_added_by_value);
            mTextPatientNameValue = view.findViewById(R.id.txt_patient_name_value);
            mTextCreateDate = view.findViewById(R.id.txt_create_date);
            mTextUpdatedDate = view.findViewById(R.id.txt_updated_date);
            mTextEdit = view.findViewById(R.id.txt_edit);
            mTextDelete = view.findViewById(R.id.txt_delete);
        }
    }

    public interface SenjamDoctorNoteListAdapterListener {
        void onNoteDetailsLayoutClicked(SenjamListModel senjamListModel);
    }

    public interface SenjamEditButtonListener {
        void onSenjamEditButtonClickedListener(SenjamListModel senjamListModel,int position);
    }

    public interface SenjamDeleteButtonListener {
        void onSenjamDeleteButtonClickedListener(SenjamListModel senjamListModel,int position);
    }
}

