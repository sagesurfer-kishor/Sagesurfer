package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.caseload.PeerNoteDetailsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.CaseloadPeerNote_;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 7/23/2018.
 */

public class CaseloadPeerNoteAdapter extends ArrayAdapter<CaseloadPeerNote_> {

    private static final String TAG = CaseloadListAdapter.class.getSimpleName();

    private final Context mContext;
    private final Activity activity;
    public final ArrayList<CaseloadPeerNote_> caseloadPeerNoteList;
    //public final CaseloadPeerNoteAdapterListener caseloadPeerNoteAdapterListener;
    int color;
    private String action = "";

    public CaseloadPeerNoteAdapter(Activity activity, Context mContext, ArrayList<CaseloadPeerNote_> caseloadPeerNoteList, String action/*, CaseloadPeersNoteAdapterListener caseloadPeerNoteAdapterListener*/) {
        super(activity, 0, caseloadPeerNoteList);
        this.activity = activity;
        this.caseloadPeerNoteList = caseloadPeerNoteList;
        this.mContext = mContext;
        this.action = action;
        //this.caseloadPeerNoteAdapterListener = caseloadPeerNoteAdapterListener;
    }

    @Override
    public int getCount() {
        return caseloadPeerNoteList.size();
    }

    @Override
    public CaseloadPeerNote_ getItem(int position) {
        if (caseloadPeerNoteList != null && caseloadPeerNoteList.size() > 0) {
            return caseloadPeerNoteList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return caseloadPeerNoteList.get(position).getVisit_id();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.caseload_peer_note_item, parent, false);

            viewHolder.linearLayoutPeerNote = (LinearLayout) view.findViewById(R.id.linearlayout_peer_note);
            viewHolder.subjectText = (TextView) view.findViewById(R.id.textview_peer_note_subject);
            viewHolder.dateTimeText = (TextView) view.findViewById(R.id.textview_peer_note_datetime);
            viewHolder.durationText = (TextView) view.findViewById(R.id.textview_peer_note_duration);
            viewHolder.typeText = (TextView) view.findViewById(R.id.textview_peer_note_type);
            //viewHolder.noteText = (TextView) view.findViewById(R.id.textview_peer_note);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //String date = caseloadPeerNoteList.get(position).getVisit_date();
        //String[] splitStr = date.trim().split("\\s+");
        viewHolder.subjectText.setText(mContext.getResources().getString(R.string.subject) + ": " + caseloadPeerNoteList.get(position).getSubject());
        viewHolder.dateTimeText.setText(mContext.getResources().getString(R.string.time) + ": " + caseloadPeerNoteList.get(position).getAt_time().substring(0,5)+" "+caseloadPeerNoteList.get(position).getAt_time().substring(9,11));
        viewHolder.durationText.setText(mContext.getResources().getString(R.string.duration) + ": " + caseloadPeerNoteList.get(position).getDuration().substring(0,5));
        viewHolder.typeText.setText(mContext.getResources().getString(R.string.type_of_contact) + ": " +caseloadPeerNoteList.get(position).getContact_type());

        viewHolder.linearLayoutPeerNote.setTag(position);
        viewHolder.linearLayoutPeerNote.setOnClickListener(onClick);

        return view;
    }

    // on click for list item button for accept/decline
    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.linearlayout_peer_note:
                    Intent detailsIntent = new Intent(mContext, PeerNoteDetailsActivity.class);
                    detailsIntent.putExtra(General.ID, caseloadPeerNoteList.get(position).getVisit_id());
                    detailsIntent.putExtra(General.SUBJECT, caseloadPeerNoteList.get(position).getSubject());
                    //detailsIntent.putExtra(General.DATE, caseloadPeerNoteList.get(position).getVisit_date());
                    detailsIntent.putExtra(General.TIME, caseloadPeerNoteList.get(position).getAt_time());
                    detailsIntent.putExtra(General.TYPE_OF_CONTACT, caseloadPeerNoteList.get(position).getContact_type());
                    detailsIntent.putExtra(General.DURATION, caseloadPeerNoteList.get(position).getDuration());
                    detailsIntent.putExtra(General.ACTION, action);
                    //detailsIntent.putExtra(General.NEXT_STEP_NOTES, caseloadPeerNoteList.get(position).getNext_step_notes());
                    //detailsIntent.putExtra(General.RESOURCES_NEEDED, caseloadPeerNoteList.get(position).getResources_needed());
                    //detailsIntent.putExtra(General.NOTE, caseloadPeerNoteList.get(position).getRemark());
                 //   activity.startActivity(detailsIntent, ActivityTransition.moveToNextAnimation(mContext));
                    activity.startActivity(detailsIntent);
                    activity.overridePendingTransition(0, 0);
                    break;
            }
        }
    };

    private class ViewHolder {
        LinearLayout linearLayoutPeerNote;
        TextView subjectText;
        TextView dateTimeText;
        TextView durationText;
        TextView typeText;
        //TextView noteText;
    }
}
