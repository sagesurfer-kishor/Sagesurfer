package com.modules.sows.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.sows.model.SowsNotes;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SenjamSowsNoteListAdapter extends RecyclerView.Adapter<SenjamSowsNoteListAdapter.MyViewHolder> {
    private static final String TAG = SenjamSowsNoteListAdapter.class.getSimpleName();
    private final ArrayList<SowsNotes> senjamListModelArrayList;

    private Activity activity;
    private SenjamSowsNoteListAdapterListener senjamSowsNoteListAdapterListener;

    public SenjamSowsNoteListAdapter(Activity activity, ArrayList<SowsNotes> senjamListModelArrayList
            , SenjamSowsNoteListAdapterListener senjamSowsNoteListAdapterListener) {
        this.senjamListModelArrayList = senjamListModelArrayList;
        this.activity = activity;
        this.senjamSowsNoteListAdapterListener = senjamSowsNoteListAdapterListener;

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_senjam_sows_note_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        //viewHolder.mTextTitle.setText(ChangeCase.toTitleCase(senjamListModelArrayList.get(position).getTitle()));
        viewHolder.mTextTitle.setText(ChangeCase.toTitleCase(senjamListModelArrayList.get(position).getSubject()));
        viewHolder.mTextSubjectValue.setText(ChangeCase.toTitleCase(senjamListModelArrayList.get(position).getSubject()));
        viewHolder.mTextUpdatedDate.setText(getDate(senjamListModelArrayList.get(position).getLast_updated()));
        viewHolder.mTextDescription.setText(ChangeCase.toTitleCase(senjamListModelArrayList.get(position).getDescription()));

        viewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senjamSowsNoteListAdapterListener.onSowsNoteDetailsLayoutClicked(senjamListModelArrayList.get(position));
            }
        });

    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy, hh:mm:ss a", cal).toString();
        return date;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
//        private final RelativeLayout mainRelativeLayout;
        private final CardView mCardView;
        private TextView  mTextSubjectValue, mTextTitle;
        private TextView  mTextUpdatedDate, mTextDescription;

        MyViewHolder(View view) {
            super(view);
            mCardView =  view.findViewById(R.id.cardView);
            mTextTitle = view.findViewById(R.id.txt_title);
            mTextSubjectValue = view.findViewById(R.id.txt_subject_value);
            mTextUpdatedDate = view.findViewById(R.id.txt_updated_date);
            mTextDescription = view.findViewById(R.id.txt_description);
        }
    }

    public interface SenjamSowsNoteListAdapterListener {
        void onSowsNoteDetailsLayoutClicked(SowsNotes senjamListModel);
    }

}

