package com.sagesurfer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.selfcare.ListItem;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Caseload_;
import com.sagesurfer.models.PURDeviceData_;
import com.sagesurfer.models.PostcardAttachment_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 8/6/2018.
 */

public class DeviceBaseDataListAdapter extends RecyclerView.Adapter<DeviceBaseDataListAdapter.MyViewHolder> {
    private static final String TAG = DeviceBaseDataListAdapter.class.getSimpleName();

    public final ArrayList<PURDeviceData_> deviceDataList;
    private final Context mContext;
    int color;

    public DeviceBaseDataListAdapter(Context mContext, ArrayList<PURDeviceData_> deviceDataList) {
        this.deviceDataList = deviceDataList;
        this.mContext = mContext;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout linearLayoutDeviceData;
        final LinearLayout linearLayoutActiveUsers;
        final LinearLayout linearLayoutActivity;
        final LinearLayout linearLayoutDocumentActivity;
        final TextView nameText;
        final TextView activityText;
        final TextView deviceText;
        final ImageView deviceImageView;
        final TextView percentageText;
        final View bottomView;

        MyViewHolder(View view) {
            super(view);
            linearLayoutDeviceData = (LinearLayout) view.findViewById(R.id.linearlayout_device_data);
            linearLayoutActiveUsers = (LinearLayout) view.findViewById(R.id.linearlayout_active_users);
            linearLayoutActivity = (LinearLayout) view.findViewById(R.id.linearlayout_activity);
            linearLayoutDocumentActivity = (LinearLayout) view.findViewById(R.id.linearlayout_document_activity);
            nameText = (TextView) view.findViewById(R.id.textview_active_user_name);
            activityText = (TextView) view.findViewById(R.id.textview_activity);
            deviceText = (TextView) view.findViewById(R.id.textview_device);
            deviceImageView = (ImageView) view.findViewById(R.id.imageview_device);
            percentageText = (TextView) view.findViewById(R.id.textview_time_spent);
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

        viewHolder.linearLayoutDeviceData.setVisibility(View.VISIBLE);
        viewHolder.linearLayoutActiveUsers.setVisibility(View.GONE);
        viewHolder.linearLayoutActivity.setVisibility(View.GONE);
        viewHolder.linearLayoutDocumentActivity.setVisibility(View.GONE);

        viewHolder.nameText.setVisibility(View.GONE);
        viewHolder.activityText.setText(ChangeCase.toTitleCase(deviceDataList.get(position).getAct_name()));
        if(deviceDataList.get(position).getDevice().equalsIgnoreCase("Mobile")
                || deviceDataList.get(position).getDevice().equalsIgnoreCase("Computer")
                || deviceDataList.get(position).getDevice().equalsIgnoreCase("Tablet")) {
            viewHolder.deviceText.setVisibility(View.GONE);
            viewHolder.deviceImageView.setVisibility(View.VISIBLE);

            if(deviceDataList.get(position).getDevice().equalsIgnoreCase("Mobile")){
                viewHolder.deviceImageView.setImageResource(R.drawable.vi_mobile);
            } else if(deviceDataList.get(position).getDevice().equalsIgnoreCase("Computer")){
                viewHolder.deviceImageView.setImageResource(R.drawable.vi_desktop);
            } else if(deviceDataList.get(position).getDevice().equalsIgnoreCase("Tablet")){
                viewHolder.deviceImageView.setImageResource(R.drawable.vi_tablet);
            } else {
                viewHolder.deviceImageView.setImageResource(R.drawable.vi_mobile);
            }
            viewHolder.percentageText.setText(deviceDataList.get(position).getPercentage() + "%");
        } else {
            viewHolder.deviceText.setVisibility(View.VISIBLE);
            viewHolder.deviceImageView.setVisibility(View.GONE);
            viewHolder.deviceText.setText(ChangeCase.toTitleCase(deviceDataList.get(position).getDevice()));
            viewHolder.percentageText.setText("Page Hits%");
        }

        if(position+1 == deviceDataList.size()){
            viewHolder.bottomView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return deviceDataList.size();
    }
}
