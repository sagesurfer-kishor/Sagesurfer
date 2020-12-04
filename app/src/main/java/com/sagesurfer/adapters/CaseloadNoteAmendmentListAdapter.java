package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.CaseloadPeerNoteAmendments_;

import java.util.ArrayList;

/**
 * Created by Monika on 10/29/2018.
 */

public class CaseloadNoteAmendmentListAdapter extends RecyclerView.Adapter<CaseloadNoteAmendmentListAdapter.MyViewHolder> {
    private static final String TAG = CaseloadNoteAmendmentListAdapter.class.getSimpleName();
    public final ArrayList<CaseloadPeerNoteAmendments_> amendmentList;
    private final Activity activity;
    private final Context mContext;

    public CaseloadNoteAmendmentListAdapter(Activity activity, Context mContext, ArrayList<CaseloadPeerNoteAmendments_> amendmentList) {
        this.amendmentList = amendmentList;
        this.activity = activity;
        this.mContext = mContext;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView dateTimeText;
        final TextView amendmentText;

        MyViewHolder(View view) {
            super(view);
            dateTimeText = (TextView) view.findViewById(R.id.textview_peer_note_date_time);
            amendmentText = (TextView) view.findViewById(R.id.textview_peer_note_amendment);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.caseload_peer_note_amendment_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.dateTimeText.setText(amendmentList.get(position).getAdded_date());
        viewHolder.amendmentText.setText(amendmentList.get(position).getAmendment());
    }

    @Override
    public long getItemId(int position) {
        return amendmentList.get(position).getAb_id();
    }

    @Override
    public int getItemCount() {
        return amendmentList.size();
    }
}
