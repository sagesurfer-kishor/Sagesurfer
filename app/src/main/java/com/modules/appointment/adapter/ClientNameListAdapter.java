package com.modules.appointment.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.modules.appointment.model.Staff;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 2/27/2020.
 */
public class ClientNameListAdapter extends RecyclerView.Adapter<ClientNameListAdapter.MyClientViewHolder> {
    private Context mContext;
    private ArrayList<Staff> mArrayList;
    private ClientNameListener clientNameListener;
    private int selectedPosition = -1;
    private Staff selectedSupervisor;
    private int mtype;

    public ClientNameListAdapter(Context context, ArrayList<Staff> arrayList, ClientNameListener clientNameListener1, Staff supervisor, int type) {
        this.mContext = context;
        this.mArrayList = arrayList;
        this.clientNameListener = clientNameListener1;
        this.selectedSupervisor = supervisor;
        this.mtype = type;

    }

    @NonNull
    @Override
    public MyClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.team_peer_supervisor_list_item, parent, false);
        return new MyClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyClientViewHolder holder, int position) {

        final Staff model = mArrayList.get(position);

        /*holder.mTxtClientName.setText(model.getName() + " (" +model.getUsername()+")");*/

        if (mtype == 1){
            holder.mTxtClientName.setText(model.getName() + " (" +model.getUsername()+")");
        }else {
            holder.mTxtClientName.setText(model.getName());
        }

        if (selectedSupervisor != null && selectedSupervisor.getId() == model.getId()) {
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedPosition = holder.getAdapterPosition();
            }
        });

        holder.mRelativeMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientNameListener.onClientNameLayoutClicked(model);
            }
        });

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientNameListener.onClientNameLayoutClicked(model);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class MyClientViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxtClientName;
        private LinearLayout mLinearMainItem, linear_image;
        private RelativeLayout mRelativeMainItem;
        private CheckBox mCheckBox;

        public MyClientViewHolder(View itemView) {
            super(itemView);

            mTxtClientName = itemView.findViewById(R.id.txt_name);
            mCheckBox = itemView.findViewById(R.id.checkBox);
            mLinearMainItem = itemView.findViewById(R.id.linear_main_item);
            mRelativeMainItem = itemView.findViewById(R.id.relative_main_item);
            linear_image = itemView.findViewById(R.id.linear_image);


        }
    }

    public interface ClientNameListener {
        void onClientNameLayoutClicked(Staff staffListModel);
    }
}
