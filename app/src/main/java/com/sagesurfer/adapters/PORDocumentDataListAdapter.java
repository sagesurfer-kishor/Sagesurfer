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
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.PORDocumentData_;

import java.util.ArrayList;

/**
 * Created by Monika on 8/21/2018.
 */

public class PORDocumentDataListAdapter extends RecyclerView.Adapter<PORDocumentDataListAdapter.MyViewHolder> {
    private static final String TAG = DeviceBaseDataListAdapter.class.getSimpleName();

    public final ArrayList<PORDocumentData_> documentDataList;
    private final Context mContext;
    int color;
    public final PORDocumentDataListAdapterListener documentDataListAdapterrListener;

    public PORDocumentDataListAdapter(Context mContext, ArrayList<PORDocumentData_> documentDataList, PORDocumentDataListAdapterListener documentDataListAdapterrListener) {
        this.documentDataList = documentDataList;
        this.mContext = mContext;
        this.documentDataListAdapterrListener = documentDataListAdapterrListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout linearLayoutDeviceData;
        final LinearLayout linearLayoutActiveUsers;
        final LinearLayout linearLayoutActivity;
        final LinearLayout linearLayoutDocumentActivity;
        final TextView countText;
        final TextView titleText;
        final TextView compareText;
        final ImageView compareImageView;
        final View bottomView;

        MyViewHolder(View view) {
            super(view);
            linearLayoutDeviceData = (LinearLayout) view.findViewById(R.id.linearlayout_device_data);
            linearLayoutActiveUsers = (LinearLayout) view.findViewById(R.id.linearlayout_active_users);
            linearLayoutActivity = (LinearLayout) view.findViewById(R.id.linearlayout_activity);
            linearLayoutDocumentActivity = (LinearLayout) view.findViewById(R.id.linearlayout_document_activity);
            countText = (TextView) view.findViewById(R.id.textview_count);
            titleText = (TextView) view.findViewById(R.id.textview_title);
            compareText = (TextView) view.findViewById(R.id.textview_compare);
            compareImageView = (ImageView) view.findViewById(R.id.imageview_compare);
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
        viewHolder.linearLayoutActivity.setVisibility(View.GONE);
        viewHolder.linearLayoutDocumentActivity.setVisibility(View.VISIBLE);

        viewHolder.compareText.setVisibility(View.GONE);
        viewHolder.compareImageView.setVisibility(View.VISIBLE);
        viewHolder.countText.setText("" + documentDataList.get(position).getCnt());
        viewHolder.titleText.setText(documentDataList.get(position).getLable());
        if(documentDataList.get(position).getLable().equalsIgnoreCase("Title")) {
            viewHolder.compareText.setVisibility(View.VISIBLE);
            viewHolder.compareImageView.setVisibility(View.GONE);
            viewHolder.countText.setText(mContext.getResources().getString(R.string.count));
            viewHolder.compareText.setText(mContext.getResources().getString(R.string.compare));
        }

        if(position+1 == documentDataList.size()){
            viewHolder.bottomView.setVisibility(View.VISIBLE);
        }

        viewHolder.compareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PORDocumentData_ documentData_ = new PORDocumentData_();
                documentData_ = documentDataList.get(position);
                documentDataListAdapterrListener.onCompareImageClicked(position, documentData_);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documentDataList.size();
    }

    public interface PORDocumentDataListAdapterListener {
        void onCompareImageClicked(int position, PORDocumentData_ documentData_);
    }
}