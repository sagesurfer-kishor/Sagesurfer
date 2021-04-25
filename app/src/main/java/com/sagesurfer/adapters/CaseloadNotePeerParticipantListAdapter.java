package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.caseload.CaseloadProgressNoteActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Members_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 10/25/2018.
 */

public class CaseloadNotePeerParticipantListAdapter extends RecyclerView.Adapter<CaseloadNotePeerParticipantListAdapter.MyViewHolder> {
    public final ArrayList<Members_> memberList;
    private final Activity activity;
    private final Context mContext;

    public CaseloadNotePeerParticipantListAdapter(Activity activity, Context mContext, ArrayList<Members_> memberList) {
        this.memberList = memberList;
        this.activity = activity;
        this.mContext = mContext;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final RelativeLayout detailsRelativeLayout;
        final TextView peerNameText;
        final TextView peerTeamText;
        final ImageView profileImage;

        MyViewHolder(View view) {
            super(view);
            detailsRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_details);
            peerNameText = (TextView) view.findViewById(R.id.textview_peer_name);
            peerTeamText = (TextView) view.findViewById(R.id.textview_peer_team_name);
            profileImage = (ImageView) view.findViewById(R.id.imageview_profile);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.caseload_note_peer_participant_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.peerTeamText.setText(ChangeCase.toTitleCase(memberList.get(position).getTeam_name()));
        viewHolder.peerNameText.setText(ChangeCase.toTitleCase(memberList.get(position).getFullname()));

        Glide.with(mContext)
                .load(memberList.get(position).getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(memberList.get(position).getImage()))
                        .transform(new CircleTransform(mContext)))
                .into(viewHolder.profileImage);

        viewHolder.detailsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.CONSUMER_ID, memberList.get(position).getUser_id());
                Intent detailsIntent = new Intent(activity.getApplicationContext(), CaseloadProgressNoteActivity.class);
                activity.startActivity(detailsIntent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return memberList.get(position).getUser_id();
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}
