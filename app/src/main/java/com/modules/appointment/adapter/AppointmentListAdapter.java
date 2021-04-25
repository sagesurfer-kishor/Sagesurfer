package com.modules.appointment.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.appointment.model.Appointment_;
import com.modules.planner.PlannerListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetCounters;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Kailash Karankal on 2/4/2020.
 */
public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.MyViewHolder> {
    private static final String TAG = PlannerListAdapter.class.getSimpleName();
    private final Context mContext;
    private final List<Appointment_> appointmentList;
    private final AppointmentAdapterListener appointmentAdapterListener;
    private final Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView textviewProgramDirector, clientName, appointmentBy, appointmentDate, appointmentTime, appointmentStatus, txtreschedule;
        AppCompatImageView dateIcon;
        LinearLayout appointLayout;

        MyViewHolder(View view) {
            super(view);
            textviewProgramDirector = (TextView) view.findViewById(R.id.textview_program_director);
            clientName = (TextView) view.findViewById(R.id.textview_appointment_for_client);
            appointmentBy = (TextView) view.findViewById(R.id.textview_appointment_by);
            appointmentDate = (TextView) view.findViewById(R.id.textview_appoinment_date);
            appointmentTime = (TextView) view.findViewById(R.id.textview_appointment_time);
            appointmentStatus = (TextView) view.findViewById(R.id.textview_appointment_status);
            txtreschedule = view.findViewById(R.id.txt_appointment_responsechange_status);

            dateIcon = (AppCompatImageView) view.findViewById(R.id.date_icon);
            appointLayout = (LinearLayout) view.findViewById(R.id.appoint_header_layout);
        }
    }

    public AppointmentListAdapter(Context mContext, List<Appointment_> appointmentList1, AppointmentAdapterListener appointmentAdapterListener, Activity activity) {
        this.mContext = mContext;
        this.appointmentList = appointmentList1;
        this.appointmentAdapterListener = appointmentAdapterListener;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_list_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (appointmentList.get(position).getStatus() == 1) {
            Appointment_ appointment_ = appointmentList.get(position);

            holder.dateIcon.setColorFilter(Color.parseColor("#757575"));

            holder.textviewProgramDirector.setText(appointment_.getDescription());
            holder.appointmentBy.setText("Appointment by - " + appointment_.getAdded_by());
            holder.clientName.setText("Appointment with - " + appointment_.getClient_name());
            holder.appointmentDate.setText(GetTime.dateCaps(appointment_.getDate()));

            setAppointmentTime(holder.appointmentTime, appointment_.getStart_time().substring(0, 5), appointment_.getEnd_time().substring(0, 5));

            if (appointment_.getApp_status() == 1) {
                holder.appointmentStatus.setText("Confirmed");
                holder.appointmentStatus.setTextColor(activity.getResources().getColor(R.color.self_goal_green));
                String roleId = Preferences.get(General.ROLE_ID);

                Log.e("roleId", roleId);
                if (appointment_.getYouth_yes_no().equals("1")) {
                    holder.txtreschedule.setVisibility(View.VISIBLE);
                } else {
                    holder.txtreschedule.setVisibility(View.GONE);
                }

            } else if (appointment_.getApp_status() == 2) {
                holder.appointmentStatus.setText("Rescheduled");
                holder.appointmentStatus.setTextColor(activity.getResources().getColor(R.color.busy));
            } else if (appointment_.getApp_status() == 3) {
                holder.appointmentStatus.setText("Canceled");
                holder.appointmentStatus.setTextColor(activity.getResources().getColor(R.color.busy));
            } else if(appointment_.getApp_status() == 4){
                holder.appointmentStatus.setText("Completed");
                holder.appointmentStatus.setTextColor(activity.getResources().getColor(R.color.self_goal_green));
            }


            holder.appointLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appointmentAdapterListener.onItemClicked(position, "1");
                }
            });

            holder.txtreschedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appointmentAdapterListener.onItemClicked(position, "2");
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAppointmentTime(TextView appointmentTime, String start_time, String end_time) {
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");

        Date start = null;
        try {
            start = _24HourSDF.parse(start_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date end = null;
        try {
            end = _24HourSDF.parse(end_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String hours = GetCounters.checkDigit(Math.toIntExact(Math.abs((start.getTime() - end.getTime()) / (60 * 60 * 1000) % 24)));
        String minutes = GetCounters.checkDigit(Math.toIntExact(Math.abs((start.getTime() - end.getTime()) / (60 * 1000) % 60)));

        appointmentTime.setText(_12HourSDF.format(start) + " to " + _12HourSDF.format(end) + " ( " + hours + ":" + minutes + " Hrs. )");
    }

    public interface AppointmentAdapterListener {
        void onItemClicked(int position, String value);
    }
}
