package com.modules.cometchat_7_30;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cometchat.pro.uikit.Avatar;
import com.sagesurfer.collaborativecares.R;
import java.util.ArrayList;

public class AdapterMembersList extends RecyclerView.Adapter<AdapterMembersList.ViewHolder> {
    private ArrayList<UserModel> userModelArrayList;
    private FragmentCometchatGroupsList fragmentCometchatGroupsList;
    private Context context;
    private static final String TAG = "AdapterMembersList";
    public AdapterMembersList(ArrayList<UserModel> userModelArrayList,Context context) {
        this.userModelArrayList = userModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_members_item, parent, false);
        return new AdapterMembersList.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserModel userModel = userModelArrayList.get(position);
        Log.i(TAG, "onBindViewHolder: "+userModel.getFirstname());
        holder.tv_name.setText(userModel.getFirstname() +" "+userModel.getLastname());
        holder.tv_start_time.setText("From Time "+userModel.getFrom_time());
        holder.tv_end_time.setText("To Time "+userModel.getTo_time());
        Glide.with(context).load(userModel.getPhoto()).into(holder.friend_list_item_photo);
    }

    @Override
    public int getItemCount() {
        return userModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name,tv_start_time,tv_end_time;
        Avatar friend_list_item_photo;
        public ViewHolder(@NonNull View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_end_time = view.findViewById(R.id.tv_end_time);
            tv_start_time = view.findViewById(R.id.tv_start_time);
            friend_list_item_photo = view.findViewById(R.id.friend_list_item_photo);
        }
    }
}
