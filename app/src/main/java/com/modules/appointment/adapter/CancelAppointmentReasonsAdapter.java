package com.modules.appointment.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.modules.appointment.model.AppointmentReason_;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 2/6/2020.
 */
public class CancelAppointmentReasonsAdapter extends RecyclerView.Adapter<CancelAppointmentReasonsAdapter.MyViewHolder> {
    private static final String TAG = CancelAppointmentReasonsAdapter.class.getSimpleName();
    public final ArrayList<AppointmentReason_> appointmentReasonArrayList;
    private final Activity activity;
    private final Context mContext;
    public final CancelAppointmentReasonsAdapterListener cancelAppointmentReasonsAdapterListener;
    public int selectedPosition = -1;

    public CancelAppointmentReasonsAdapter(Activity activity, Context mContext, ArrayList<AppointmentReason_> appointmentReasonArrayList1, CancelAppointmentReasonsAdapterListener cancelAppointmentReasonsAdapterListener) {
        this.appointmentReasonArrayList = appointmentReasonArrayList1;
        this.activity = activity;
        this.mContext = mContext;
        this.cancelAppointmentReasonsAdapterListener = cancelAppointmentReasonsAdapterListener;
    }

    @Override
    public long getItemId(int position) {
        return appointmentReasonArrayList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return appointmentReasonArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_select_appointment_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.nameText.setText(appointmentReasonArrayList.get(position).getReason());
        viewHolder.checkbox.setChecked(position == selectedPosition);

        viewHolder.checkbox.setTag(position);
        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                if (position == selectedPosition) {
                    viewHolder.checkbox.setChecked(false);
                    appointmentReasonArrayList.get(position).setSelected(false);
                    selectedPosition = -1;
                } else {
                    viewHolder.checkbox.setChecked(true);
                    appointmentReasonArrayList.get(position).setSelected(true);
                    selectedPosition = position;
                    if (appointmentReasonArrayList.get(position).getId() == 0) { //other
                        cancelAppointmentReasonsAdapterListener.onOtherClicked(false);
                    } else {
                        cancelAppointmentReasonsAdapterListener.onOtherClicked(true);
                    }
                }
                notifyDataSetChanged();
            }
        });

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final RelativeLayout detailsRelativeLayout;
        final TextView nameText;
        final CheckBox checkbox;

        MyViewHolder(View view) {
            super(view);
            detailsRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_details);
            nameText = (TextView) view.findViewById(R.id.multi_select_text);
            checkbox = (CheckBox) view.findViewById(R.id.multi_select_check_box);
        }
    }

    public interface CancelAppointmentReasonsAdapterListener {
        void onOtherClicked(boolean show);
    }
}

