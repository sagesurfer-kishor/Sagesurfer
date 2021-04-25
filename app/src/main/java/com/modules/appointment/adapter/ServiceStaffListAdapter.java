package com.modules.appointment.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.appointment.model.Staff;
import com.sagesurfer.collaborativecares.R;

import java.util.List;

/**
 * Created by Kailash Karankal on 2/13/2020.
 */
public class ServiceStaffListAdapter extends RecyclerView.Adapter<ServiceStaffListAdapter.MyViewHolder> {
    private static final String TAG = ServiceStaffListAdapter.class.getSimpleName();
    private final Context mContext;
    private final List<Staff> staffList;
    private final Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTxt;

        MyViewHolder(View view) {
            super(view);
            nameTxt = (TextView) view.findViewById(R.id.name_txt);
        }
    }

    public ServiceStaffListAdapter(Context mContext, List<Staff> staffList1, Activity activity) {
        this.mContext = mContext;
        this.staffList = staffList1;
        this.activity = activity;
    }


    @Override
    public int getItemCount() {
        return staffList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_staff_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Staff appointment_ = staffList.get(position);
        holder.nameTxt.setText("- " + appointment_.getName());
    }
}