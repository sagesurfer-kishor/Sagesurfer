package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.CaseloadPeerNoteComments_;

import java.util.ArrayList;

/**
 * Created by Monika on 10/29/2018.
 */

public class CaseloadNoteCommentListAdapter extends RecyclerView.Adapter<CaseloadNoteCommentListAdapter.MyViewHolder> {
    private static final String TAG = CaseloadNoteCommentListAdapter.class.getSimpleName();
    public final ArrayList<CaseloadPeerNoteComments_> commentList;
    private final Activity activity;
    private final Context mContext;

    public CaseloadNoteCommentListAdapter(Activity activity, Context mContext, ArrayList<CaseloadPeerNoteComments_> commentList) {
        this.commentList = commentList;
        this.activity = activity;
        this.mContext = mContext;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        //final TextView subjectText;
        final TextView commentedByText;
        final TextView commentDateText;
        final TextView reasonText;
        final TextView commentText;

        MyViewHolder(View view) {
            super(view);
            //subjectText = (TextView) view.findViewById(R.id.textview_peer_note_subject);
            commentedByText = (TextView) view.findViewById(R.id.textview_peer_note_commented_by);
            commentDateText = (TextView) view.findViewById(R.id.textview_peer_note_comment_date);
            reasonText = (TextView) view.findViewById(R.id.textview_peer_note_reason);
            commentText = (TextView) view.findViewById(R.id.textview_peer_note_comment);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.caseload_peer_note_comment_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        //viewHolder.subjectText.setText(ChangeCase.toTitleCase(commentList.get(position).getSubject()));
        viewHolder.commentedByText.setText(" " + commentList.get(position).getCommented_by());
        viewHolder.commentDateText.setText(" " + commentList.get(position).getComment_date());
        if(commentList.get(position).getReasons() != null) {
            if(commentList.get(position).getReasons().length() > 0) {
                viewHolder.reasonText.setText(" " + commentList.get(position).getReasons());
            } else {
                viewHolder.reasonText.setText(" -");
            }
        } else {
            viewHolder.reasonText.setText(" -");
        }
        //viewHolder.reasonText.setText(" " + commentList.get(position).getReasons());
        viewHolder.commentText.setText(" " + commentList.get(position).getComments());
    }

    @Override
    public long getItemId(int position) {
        return commentList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
