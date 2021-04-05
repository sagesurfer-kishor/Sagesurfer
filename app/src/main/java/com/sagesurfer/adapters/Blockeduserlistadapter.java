package com.sagesurfer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.modules.cometchat_7_30.CometChatMainFragment;
import com.sagesurfer.collaborativecares.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Blockeduserlistadapter extends RecyclerView.Adapter<Blockeduserlistadapter.MyViewHolder> {
    private final Context mContext;
    private final List<User> userList;
    private static final String TAG = CometChatMainFragment.class.getSimpleName();


    public Blockeduserlistadapter(Context mContext, List<User> userList) {
        this.mContext = mContext;
        this.userList = userList;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        final TextView title;
        TextView imgBan;
        ImageView banuserProfile;
        RelativeLayout relativeLayout1, relativeLayout2;


        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.list);
            imgBan = view.findViewById(R.id.unblockedId);
            banuserProfile = view.findViewById(R.id.ban_item_photo);
            relativeLayout1 = view.findViewById(R.id.lv);
            relativeLayout2 = view.findViewById(R.id.lv_error2);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ban_group_member, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        User teams_ = userList.get(position);

        holder.title.setText(teams_.getName());
        holder.imgBan.setText("Unblock");
        // unblocked user form list
        holder.imgBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> uids = new ArrayList<>();
                uids.add(teams_.getUid());
                // comet chat unblocked selected  user
                CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
                    @Override
                    public void onSuccess(HashMap<String, String> resultMap) {
                        // Handle unblock users success.
                        userList.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        // Handle unblock users failure
                    }
                });
            }
        });

        Glide.with(mContext).load(teams_.getAvatar()).into(holder.banuserProfile);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}

