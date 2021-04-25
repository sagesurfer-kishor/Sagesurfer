package com.modules.team;

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

import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;


public class TeamClientCategoryAdapter extends RecyclerView.Adapter<TeamClientCategoryAdapter.MyClientViewHolder> {

    private Context mContext;
    private ArrayList<TeamTypeModel> mArrayList;
    private TeaamTypeListener appointmentReportClientSelectListener;
    private int strSelectedId=0;

    public TeamClientCategoryAdapter(Context context, ArrayList<TeamTypeModel> arrayList, TeaamTypeListener appointmentReportClientSelectListener, int id){
        this.strSelectedId = id;
        this.mContext = context;
        this.mArrayList = arrayList;
        this.appointmentReportClientSelectListener = appointmentReportClientSelectListener;

    }

    @NonNull
    @Override
    public MyClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.team_type_category_select_item,parent,false);
        return new MyClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClientViewHolder holder, int position) {

        final TeamTypeModel model = mArrayList.get(position);

        holder.mTxtClientName.setText(model.getName());
        holder.linear_image.setVisibility(View.GONE);
        if(model.getId() == strSelectedId) {
            holder.linear_image.setVisibility(View.VISIBLE);
        }

        holder.mRelativeMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentReportClientSelectListener.onTeamTypeLayoutClicked(model);
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
        private LinearLayout mLinearMainItem,linear_image;
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

    public interface TeaamTypeListener {
        void onTeamTypeLayoutClicked(TeamTypeModel clientListModel);
    }
}
