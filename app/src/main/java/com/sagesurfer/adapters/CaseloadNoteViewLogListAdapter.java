package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.CaseloadPeerNoteViewLog_;

import java.util.ArrayList;

/**
 * Created by Monika on 10/29/2018.
 */

public class CaseloadNoteViewLogListAdapter extends RecyclerView.Adapter<CaseloadNoteViewLogListAdapter.MyViewHolder> {
    private static final String TAG = CaseloadNoteViewLogListAdapter.class.getSimpleName();
    public final ArrayList<CaseloadPeerNoteViewLog_> viewLogList;
    private final Activity activity;
    private final Context mContext;

    public CaseloadNoteViewLogListAdapter(Activity activity, Context mContext, ArrayList<CaseloadPeerNoteViewLog_> viewLogList) {
        this.viewLogList = viewLogList;
        this.activity = activity;
        this.mContext = mContext;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView statusText;
        final TextView userText;
        final TextView processText;
        final TextView commentText;
        final TextView dateText;

        MyViewHolder(View view) {
            super(view);
            statusText = (TextView) view.findViewById(R.id.textview_peer_note_status);
            userText = (TextView) view.findViewById(R.id.textview_peer_note_user);
            processText = (TextView) view.findViewById(R.id.textview_peer_note_process);
            commentText = (TextView) view.findViewById(R.id.textview_peer_note_comment);
            dateText = (TextView) view.findViewById(R.id.textview_peer_note_date);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.caseload_peer_note_view_log_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        //color = GetColor.getHomeIconBackgroundColorColorParse(true);
        //viewHolder.teamText.setTextColor(color);
        //viewHolder.teamText.setText(ChangeCase.toTitleCase(caseloadList.get(position).getGroup_name()));
        viewHolder.statusText.setText(viewLogList.get(position).getNote_status());
        if(viewLogList.get(position).getName() != null) {
            if(viewLogList.get(position).getName().length() > 0) {
                viewHolder.userText.setText(" " + viewLogList.get(position).getName());
            } else {
                viewHolder.userText.setText(" -");
            }
        } else {
            viewHolder.userText.setText(" -");
        }
        if(viewLogList.get(position).getProcess() != null) {
            if(viewLogList.get(position).getProcess().length() > 0) {
                viewHolder.processText.setText(" " + viewLogList.get(position).getProcess());
            } else {
                viewHolder.processText.setText(" -");
            }
        } else {
            viewHolder.processText.setText(" -");
        }
        if(viewLogList.get(position).getComment() != null) {
            if(viewLogList.get(position).getComment().length() > 0) {
                viewHolder.commentText.setText(" " + viewLogList.get(position).getComment());
            } else {
                viewHolder.commentText.setText(" -");
            }
        } else {
            viewHolder.commentText.setText(" -");
        }
        if(viewLogList.get(position).getDate() != null) {
            if(viewLogList.get(position).getDate().length() > 0) {
                viewHolder.dateText.setText(" " + viewLogList.get(position).getDate());
            } else {
                viewHolder.dateText.setText(" -");
            }
        } else {
            viewHolder.dateText.setText(" -");
        }
    }

    @Override
    public long getItemId(int position) {
        return viewLogList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return viewLogList.size();
    }
}
