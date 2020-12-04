package com.modules.caseload.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.caseload.activity.PeerNoteAllDetailsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.CaseloadPeerNote_;
import com.sagesurfer.views.CircleTransform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 9/10/2019.
 */

public class PeerNoteAllAdapter extends BaseAdapter {
    private final Context mContext;
    private final Activity activity;
    public final ArrayList<CaseloadPeerNote_> caseloadPeerNoteList;
    private String action = "";

    public PeerNoteAllAdapter(Activity activity, Context mContext, ArrayList<CaseloadPeerNote_> caseloadPeerNoteList, String action) {
        this.activity = activity;
        this.caseloadPeerNoteList = caseloadPeerNoteList;
        this.mContext = mContext;
        this.action = action;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return caseloadPeerNoteList.size();
    }

    @Override
    public CaseloadPeerNote_ getItem(int position) {
        return caseloadPeerNoteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        PeerNoteAllAdapter.ViewHolder viewHolder;
        LayoutInflater layoutInflater;
        if (view == null) {
            viewHolder = new PeerNoteAllAdapter.ViewHolder();
            layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.peer_note_all_item, null, true);
            viewHolder.relativeLayoutPeerNote = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_details);
            viewHolder.addNoteBgColorLayout = (LinearLayout) view.findViewById(R.id.add_note_bg_color_layout);
            viewHolder.peerParticipantName = (TextView) view.findViewById(R.id.textview_pp_name);
            viewHolder.addedByName = (TextView) view.findViewById(R.id.added_by_name);
            viewHolder.contactDate = (TextView) view.findViewById(R.id.visited_date);
            viewHolder.submitteddate = (TextView) view.findViewById(R.id.submitted_date);
            viewHolder.approvedDate = (TextView) view.findViewById(R.id.approved_date);
            viewHolder.statusType = (TextView) view.findViewById(R.id.type_status);
            viewHolder.profileImage = (ImageView) view.findViewById(R.id.imageview_profile);
            viewHolder.headerLabel = (TextView) view.findViewById(R.id.header_label);

            view.setTag(viewHolder);
        } else {
            viewHolder = (PeerNoteAllAdapter.ViewHolder) view.getTag();
        }

        if (caseloadPeerNoteList.get(position).getHeader_title().equalsIgnoreCase("Today")) {
            setChildPeerNoteData(caseloadPeerNoteList, viewHolder, position);
            viewHolder.addNoteBgColorLayout.setBackground(activity.getResources().getDrawable(R.color.light_grren));
        } else if (caseloadPeerNoteList.get(position).getHeader_title().equalsIgnoreCase("Yesterday")) {
            setChildPeerNoteData(caseloadPeerNoteList, viewHolder, position);
            viewHolder.addNoteBgColorLayout.setBackground(activity.getResources().getDrawable(R.color.light_yello));
        } else if (caseloadPeerNoteList.get(position).getHeader_title().equalsIgnoreCase("Last Week")) {
            setChildPeerNoteData(caseloadPeerNoteList, viewHolder, position);
            viewHolder.addNoteBgColorLayout.setBackground(activity.getResources().getDrawable(R.color.light_gray));
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setChildPeerNoteData(ArrayList<CaseloadPeerNote_> caseloadPeerNoteList, ViewHolder viewHolder, int position) {
        Glide.with(mContext)
                .load(caseloadPeerNoteList.get(position).getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(caseloadPeerNoteList.get(position).getImage()))
                        .transform(new CircleTransform(mContext)))
                .into(viewHolder.profileImage);

        String ppName = caseloadPeerNoteList.get(position).getPp_name();
        viewHolder.peerParticipantName.setText(ChangeCase.toTitleCase(ppName));
        viewHolder.addedByName.setText("Submitted By - " + ChangeCase.toTitleCase(caseloadPeerNoteList.get(position).getAdded_by()));
        viewHolder.contactDate.setText(dateCaps(caseloadPeerNoteList.get(position).getVisit_date()));
        viewHolder.submitteddate.setText(getDate(caseloadPeerNoteList.get(position).getSubmitted_date()));

        String visitDate = caseloadPeerNoteList.get(position).getVisit_date();
        if (position == 0) {
            viewHolder.headerLabel.setText(caseloadPeerNoteList.get(position).getHeader_title());
        } else {
            String visitDatePrevious = caseloadPeerNoteList.get(position - 1).getVisit_date();

            if (visitDatePrevious.equals(visitDate)) {
                viewHolder.headerLabel.setText("");
            } else {
                viewHolder.headerLabel.setText(caseloadPeerNoteList.get(position).getHeader_title());
            }
        }

        String type = caseloadPeerNoteList.get(position).getType();

        if (caseloadPeerNoteList.get(position).getType().equalsIgnoreCase("pending")) {
            viewHolder.statusType.setText(ChangeCase.toTitleCase(type));
            viewHolder.statusType.setTextColor(mContext.getResources().getColor(R.color.color_html));

            if (caseloadPeerNoteList.get(position).getApproved_date() == 0) {
                viewHolder.approvedDate.setText("---");
            } else {
                viewHolder.approvedDate.setText(getDate(caseloadPeerNoteList.get(position).getApproved_date()));
            }

        } else if (caseloadPeerNoteList.get(position).getType().equalsIgnoreCase("approved") || caseloadPeerNoteList.get(position).getType().equalsIgnoreCase("auto approved")) {
            viewHolder.statusType.setText(ChangeCase.toTitleCase(type));
            viewHolder.statusType.setTextColor(mContext.getResources().getColor(R.color.sos_attending));

            if (caseloadPeerNoteList.get(position).getApproved_date() == 0) {
                viewHolder.approvedDate.setText("---");
            } else {
                viewHolder.approvedDate.setText(getDate(caseloadPeerNoteList.get(position).getApproved_date()));
            }
        } else if (caseloadPeerNoteList.get(position).getType().equalsIgnoreCase("rejected")) {
            viewHolder.statusType.setText(ChangeCase.toTitleCase(type) + " (" + caseloadPeerNoteList.get(position).getReject_cnt() + ")");
            viewHolder.statusType.setTextColor(mContext.getResources().getColor(R.color.warning_text_color));

            if (caseloadPeerNoteList.get(position).getApproved_date() == 0) {
                viewHolder.approvedDate.setText("---");
            } else {
                viewHolder.approvedDate.setText(getDate(caseloadPeerNoteList.get(position).getApproved_date()));
            }
        }

        viewHolder.relativeLayoutPeerNote.setTag(position);
        viewHolder.relativeLayoutPeerNote.setOnClickListener(onClick);
    }

    // on click for list item button for accept/decline
    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.relativelayout_caseload_details:
                    Intent detailsIntent = new Intent(mContext, PeerNoteAllDetailsActivity.class);
                    detailsIntent.putExtra(General.ID, caseloadPeerNoteList.get(position).getVisit_id());
                    detailsIntent.putExtra(General.SUBJECT, caseloadPeerNoteList.get(position).getSubject());
                    detailsIntent.putExtra(General.TIME, caseloadPeerNoteList.get(position).getAt_time());
                    detailsIntent.putExtra(General.TYPE_OF_CONTACT, caseloadPeerNoteList.get(position).getContact_type());
                    detailsIntent.putExtra(General.DURATION, caseloadPeerNoteList.get(position).getDuration());
                    detailsIntent.putExtra(General.CONSUMER_ID, caseloadPeerNoteList.get(position).getConsumer_id());
                    detailsIntent.putExtra(General.LOGIN_USER_ID, caseloadPeerNoteList.get(position).getAdded_by_id());
                    detailsIntent.putExtra(General.TYPE, caseloadPeerNoteList.get(position).getType());
                    detailsIntent.putExtra(General.ACTION, action);
                    activity.startActivity(detailsIntent);
                    break;
            }
        }
    };

    private class ViewHolder {
        RelativeLayout relativeLayoutPeerNote;
        LinearLayout addNoteBgColorLayout;
        ImageView profileImage;
        TextView peerParticipantName, addedByName, contactDate;
        TextView submitteddate, approvedDate, statusType, headerLabel;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
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
}
