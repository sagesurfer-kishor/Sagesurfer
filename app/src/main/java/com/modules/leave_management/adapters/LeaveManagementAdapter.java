package com.modules.leave_management.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.leave_management.activity.LeaveApplicationActivity;
import com.modules.leave_management.models.LeaveManagement;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 12/18/2019.
 */
public class LeaveManagementAdapter extends RecyclerView.Adapter<LeaveManagementAdapter.MyViewHolder> {
    private final ArrayList<LeaveManagement> leaveManagementArrayList;
    private Activity activity;
    private LinearLayoutManager mLinearLayoutManager;
    private LeaveListAdapter leaveListAdapter;
    private final LeaveManagementAdapterListener leaveManagementAdapterListener;

    public LeaveManagementAdapter(Activity activity, ArrayList<LeaveManagement> leaveManagementArrayList1, LeaveManagementAdapterListener leaveManagementAdapterListener) {
        this.leaveManagementArrayList = leaveManagementArrayList1;
        this.activity = activity;
        this.leaveManagementAdapterListener = leaveManagementAdapterListener;
    }

    @Override
    public long getItemId(int position) {
        return leaveManagementArrayList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return leaveManagementArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (Preferences.get(General.ROLE_ID).equals("6")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.coach_leave_management_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.supervisor_leave_management_item, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final LeaveManagement leaveManagement = leaveManagementArrayList.get(position);

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            viewHolder.fromDate.setText(GetTime.dateCaps(leaveManagement.getFrom_date()));
            viewHolder.toDate.setText(GetTime.dateCaps(leaveManagement.getTo_date()));
            viewHolder.reasonTxt.setText(leaveManagement.getReason());

            viewHolder.editLeave.setColorFilter(Color.parseColor("#333333"));
            viewHolder.deleteLeave.setColorFilter(Color.parseColor("#333333"));

            viewHolder.deleteLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteLeaveDialog(leaveManagement.getId());
                }
            });

            viewHolder.editLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent leaveApplicationDetails = new Intent(activity, LeaveApplicationActivity.class);
                    leaveApplicationDetails.putExtra(General.LEAVE_UPDATE, General.LEAVE_UPDATE);
                    leaveApplicationDetails.putExtra(General.LEAVE_UPDATE_SUPERVISOR, false);
                    leaveApplicationDetails.putExtra(General.LEAVE_COACH_DATA, leaveManagementArrayList.get(position));
                    activity.startActivity(leaveApplicationDetails);
                }
            });

        } else {

            if (leaveManagement.getLeaveArrayList().size() == 0) {
                viewHolder.leaveList.setVisibility(View.GONE);
            } else {
                leaveListAdapter = new LeaveListAdapter(activity, leaveManagement.getLeaveArrayList());
                viewHolder.leaveList.setAdapter(leaveListAdapter);
            }

            viewHolder.nameText.setText(leaveManagement.getUsername());
            viewHolder.statusText.setText("(" + ChangeCase.toTitleCase(leaveManagement.getAvailable()) + ")");
            viewHolder.supervisorColorLayout.setBackgroundColor(Color.parseColor(leaveManagement.getColor()));

            if (Integer.parseInt(Preferences.get(General.NOTIFICATION_COACH_ID)) == leaveManagement.getId()) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.leaveLinearLayout.setVisibility(View.VISIBLE);
                Preferences.save(General.NOTIFICATION_COACH_ID, 0);
            }

            viewHolder.leaveExpandLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.upArrow.setVisibility(View.VISIBLE);
                    viewHolder.downArrow.setVisibility(View.GONE);
                    viewHolder.leaveLinearLayout.setVisibility(View.VISIBLE);
                }
            });

            viewHolder.downArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.upArrow.setVisibility(View.VISIBLE);
                    viewHolder.downArrow.setVisibility(View.GONE);
                    viewHolder.leaveLinearLayout.setVisibility(View.VISIBLE);
                }
            });

            viewHolder.upArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.upArrow.setVisibility(View.GONE);
                    viewHolder.downArrow.setVisibility(View.VISIBLE);
                    viewHolder.leaveLinearLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void showDeleteLeaveDialog(final int id) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_peer_note_edit);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final TextView titleTxt = (TextView) dialog.findViewById(R.id.title_txt);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final ImageButton buttonCancel = (ImageButton) dialog.findViewById(R.id.button_close);

        titleTxt.setText("Action Confirmation");
        textViewMsg.setText("Leave application will be deleted from platform!");

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveManagementAdapterListener.deleteLeave(id);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface LeaveManagementAdapterListener {
        void deleteLeave(int id);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText, statusText;
        final TextView fromDate, toDate, reasonTxt;
        final ImageView profileImage, downArrow, upArrow;
        final LinearLayout leaveLinearLayout, leaveExpandLayout;
        final RelativeLayout supervisorColorLayout;
        RecyclerView leaveList;
        final ImageView deleteLeave, editLeave;

        MyViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.textview_leave_name);
            statusText = (TextView) view.findViewById(R.id.textview_leave_status);
            profileImage = (ImageView) view.findViewById(R.id.imageview_profile);
            leaveLinearLayout = (LinearLayout) view.findViewById(R.id.leave_details);
            leaveExpandLayout = (LinearLayout) view.findViewById(R.id.linearlayout_details);
            supervisorColorLayout = (RelativeLayout) view.findViewById(R.id.supervisor_color_layout);

            downArrow = (ImageView) view.findViewById(R.id.down_arrow);
            upArrow = (ImageView) view.findViewById(R.id.up_arrow);

            fromDate = (TextView) view.findViewById(R.id.from_date_txt);
            toDate = (TextView) view.findViewById(R.id.to_date_txt);
            reasonTxt = (TextView) view.findViewById(R.id.reason);

            deleteLeave = (ImageView) view.findViewById(R.id.delete_leave);
            editLeave = (ImageView) view.findViewById(R.id.edit_leave);

            leaveList = (RecyclerView) view.findViewById(R.id.leave_list_recycler_view);
            mLinearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            leaveList.setLayoutManager(mLinearLayoutManager);
        }
    }

}