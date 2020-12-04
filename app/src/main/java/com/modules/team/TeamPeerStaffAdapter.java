package com.modules.team;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.parser.Invitations_;

import java.util.ArrayList;


public class TeamPeerStaffAdapter extends RecyclerView.Adapter<TeamPeerStaffAdapter.MyClientViewHolder> {
    private Context mContext;
    private ArrayList<Invitations_> mArrayList;
    private TeamPeerStaffListener teamPeerStaffListener;

    public TeamPeerStaffAdapter(Context context, ArrayList<Invitations_> arrayList, TeamPeerStaffListener teamPeerStaffListener) {
        this.mContext = context;
        this.mArrayList = arrayList;
        this.teamPeerStaffListener = teamPeerStaffListener;
    }

    @NonNull
    @Override
    public MyClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.team_peer_supervisor_list_item, parent, false);
        return new MyClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyClientViewHolder holder, final int position) {

        final Invitations_ model = mArrayList.get(position);

        holder.mTxtClientName.setText(model.getUsername());

        if (model.getSelected()) {
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }

        holder.mRelativeMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Invitations_ modelObj = mArrayList.get(holder.getPosition());
                modelObj.setSelected(!modelObj.getSelected());
                notifyDataSetChanged();

            }
        });

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Invitations_ modelObj = mArrayList.get(holder.getPosition());
                modelObj.setSelected(!modelObj.getSelected());
                notifyDataSetChanged();
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

    public interface TeamPeerStaffListener {
        void onTeamPeerStaffLayoutClicked(ArrayList<Invitations_> invitations_arrayList);
    }
}
