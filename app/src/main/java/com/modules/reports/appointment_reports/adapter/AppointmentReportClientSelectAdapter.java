package com.modules.reports.appointment_reports.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.modules.reports.appointment_reports.model.ClientListModel;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;


public class AppointmentReportClientSelectAdapter extends RecyclerView.Adapter<AppointmentReportClientSelectAdapter.MyClientViewHolder> {

    private Context mContext;
    private ArrayList<ClientListModel> mArrayList;
    private AppointmentReportClientSelectListener appointmentReportClientSelectListener;
    private String strSelectedId = "0";

    public AppointmentReportClientSelectAdapter(Context context, ArrayList<ClientListModel> arrayList, AppointmentReportClientSelectListener appointmentReportClientSelectListener, String id) {
        this.strSelectedId = id;
        this.mContext = context;
        this.mArrayList = arrayList;
        this.appointmentReportClientSelectListener = appointmentReportClientSelectListener;
    }

    @NonNull
    @Override
    public MyClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.appointment_report_client_select_item, parent, false);
        return new MyClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClientViewHolder holder, int position) {

        final ClientListModel model = mArrayList.get(position);

        holder.mTxtClientName.setText(model.getName());
        holder.linear_image.setVisibility(View.GONE);

        // old condition change on 07/08/2020 error : app crashes on empty list
        /*if (model.getId().equals(strSelectedId)) {
            holder.linear_image.setVisibility(View.VISIBLE);
        }*/
        if (model.getId() != null) { // added by kishor k on 07/08/2020
            if (model.getId().equals(strSelectedId)) {
                holder.linear_image.setVisibility(View.VISIBLE);
            }
        }

        holder.mRelativeMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentReportClientSelectListener.onAppointmentReportClientSelectLayoutClicked(model);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class MyClientViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtClientName;
        private ImageView mImageViewDone;
        private LinearLayout mLinearMainItem, linear_image;
        private RelativeLayout mRelativeMainItem;

        public MyClientViewHolder(View itemView) {
            super(itemView);

            mTxtClientName = itemView.findViewById(R.id.txt_name);
            mImageViewDone = itemView.findViewById(R.id.img_done);
            mLinearMainItem = itemView.findViewById(R.id.linear_main_item);
            mRelativeMainItem = itemView.findViewById(R.id.relative_main_item);
            linear_image = itemView.findViewById(R.id.linear_image);

        }
    }

    public interface AppointmentReportClientSelectListener {
        void onAppointmentReportClientSelectLayoutClicked(ClientListModel clientListModel);
    }
}
