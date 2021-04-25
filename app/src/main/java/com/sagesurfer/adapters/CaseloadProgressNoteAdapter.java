package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.caseload.ProgressNoteDetailsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.CaseloadProgressNote_;

import java.util.ArrayList;

/**
 * Created by Monika on 7/10/2018.
 */

public class CaseloadProgressNoteAdapter extends RecyclerView.Adapter<CaseloadProgressNoteAdapter.MyViewHolder> {
    private static final String TAG = CaseloadListAdapter.class.getSimpleName();
    private final Context mContext;
    private final Activity activity;
    public final ArrayList<CaseloadProgressNote_> caseloadProgressNoteList;
    private int color;

    public CaseloadProgressNoteAdapter(Activity activity, Context mContext, ArrayList<CaseloadProgressNote_> caseloadProgressNoteList) { //, CaseloadProgressNoteAdapterListener caseloadProgressNoteAdapterListener
        this.activity = activity;
        this.caseloadProgressNoteList = caseloadProgressNoteList;
        this.mContext = mContext;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout linearLayoutProgressNote;
        final TextView nameText;
        final TextView dateText;
        final ImageView imageViewDownload;

        MyViewHolder(View view) {
            super(view);
            linearLayoutProgressNote = (LinearLayout) view.findViewById(R.id.linearlayout_progress_note);
            nameText = (TextView) view.findViewById(R.id.textview_progress_note_name);
            dateText = (TextView) view.findViewById(R.id.textview_progress_note_date);
            imageViewDownload = (AppCompatImageView) view.findViewById(R.id.imageview_progress_note_download);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.caseload_progress_note_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        color = GetColor.getHomeIconBackgroundColorColorParse(true);
        viewHolder.nameText.setText(caseloadProgressNoteList.get(position).getBy_user());
        viewHolder.dateText.setText("Posted on " + caseloadProgressNoteList.get(position).getDate());
        viewHolder.linearLayoutProgressNote.setTag(position);
        viewHolder.linearLayoutProgressNote.setOnClickListener(onClick);
    }

    // on click for list item button for accept/decline
    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.linearlayout_progress_note:
                    Intent detailsIntent = new Intent(mContext, ProgressNoteDetailsActivity.class);
                    detailsIntent.putExtra(General.ID, caseloadProgressNoteList.get(position).getId());
                    detailsIntent.putExtra(General.NAME, caseloadProgressNoteList.get(position).getBy_user());

                    activity.startActivity(detailsIntent);
                    activity.overridePendingTransition(0, 0);
                    break;
            }
        }
    };

    @Override
    public long getItemId(int position) {
        return caseloadProgressNoteList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return caseloadProgressNoteList.size();
    }
}
