package com.sagesurfer.adapters;

import android.content.Context;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.PORActivityData_;

import java.util.ArrayList;

/**
 * Created by Monika on 8/21/2018.
 */

public class PORActivityDataListAdapter extends RecyclerView.Adapter<PORActivityDataListAdapter.MyViewHolder> {
    private static final String TAG = DeviceBaseDataListAdapter.class.getSimpleName();

    public final ArrayList<PORActivityData_> activityDataList;
    private final Context mContext;
    int color;

    public PORActivityDataListAdapter(Context mContext, ArrayList<PORActivityData_> activityDataList) {
        this.activityDataList = activityDataList;
        this.mContext = mContext;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout linearLayoutDeviceData;
        final LinearLayout linearLayoutActiveUsers;
        final LinearLayout linearLayoutActivity;
        final LinearLayout linearLayoutDocumentActivity;
        final TextView teamText;
        final TextView membersText;
        final TextView strengthIdentifiedText;
        final View bottomView;

        MyViewHolder(View view) {
            super(view);
            linearLayoutDeviceData = (LinearLayout) view.findViewById(R.id.linearlayout_device_data);
            linearLayoutActiveUsers = (LinearLayout) view.findViewById(R.id.linearlayout_active_users);
            linearLayoutActivity = (LinearLayout) view.findViewById(R.id.linearlayout_activity);
            linearLayoutDocumentActivity = (LinearLayout) view.findViewById(R.id.linearlayout_document_activity);
            teamText = (TextView) view.findViewById(R.id.textview_team);
            membersText = (TextView) view.findViewById(R.id.textview_members);
            strengthIdentifiedText = (TextView) view.findViewById(R.id.textview_strength_identified);
            bottomView = (View) view.findViewById(R.id.view_bottom);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_base_data_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        color = GetColor.getHomeIconBackgroundColorColorParse(true);

        viewHolder.linearLayoutDeviceData.setVisibility(View.GONE);
        viewHolder.linearLayoutActiveUsers.setVisibility(View.GONE);
        viewHolder.linearLayoutActivity.setVisibility(View.VISIBLE);
        viewHolder.linearLayoutDocumentActivity.setVisibility(View.GONE);

        viewHolder.teamText.setText(activityDataList.get(position).getGroup_name());
        viewHolder.membersText.setText("" + activityDataList.get(position).getMember_count());
        viewHolder.strengthIdentifiedText.setText("" + activityDataList.get(position).getCount());
        if(activityDataList.get(position).getGroup_name().equalsIgnoreCase("Team")) {
            viewHolder.membersText.setText(mContext.getResources().getString(R.string.members));
            viewHolder.strengthIdentifiedText.setText(mContext.getResources().getString(R.string.strength_identified));
        }

        if(position+1 == activityDataList.size()){
            viewHolder.bottomView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return activityDataList.size();
    }
}