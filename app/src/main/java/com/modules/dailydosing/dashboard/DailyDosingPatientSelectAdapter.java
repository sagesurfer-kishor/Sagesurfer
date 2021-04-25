package com.modules.dailydosing.dashboard;

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

import com.modules.dailydosing.model.DailyDosing;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;


public class DailyDosingPatientSelectAdapter extends RecyclerView.Adapter<DailyDosingPatientSelectAdapter.MyClientViewHolder> {

    private Context mContext;
    private ArrayList<DailyDosing> mArrayList;
    private DailyDosingPatientSelectListener dailyDosingPatientSelectListener;
    private String strSelectedId = "0";

    public DailyDosingPatientSelectAdapter(Context context, ArrayList<DailyDosing> arrayList, DailyDosingPatientSelectListener dailyDosingPatientSelectListener, String id) {
        this.strSelectedId = id;
        this.mContext = context;
        this.mArrayList = arrayList;
        this.dailyDosingPatientSelectListener = dailyDosingPatientSelectListener;

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

        final DailyDosing model = mArrayList.get(position);

        holder.mTxtClientName.setText(model.getName());
        holder.linear_image.setVisibility(View.GONE);
        if (model.getId().equals(strSelectedId)) {
            holder.linear_image.setVisibility(View.VISIBLE);
        }

        holder.mRelativeMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyDosingPatientSelectListener.onDailyDosingPatientSelectLayoutClicked(model);
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

    public interface DailyDosingPatientSelectListener {
        void onDailyDosingPatientSelectLayoutClicked(DailyDosing dailyDosingModel);
    }
}
