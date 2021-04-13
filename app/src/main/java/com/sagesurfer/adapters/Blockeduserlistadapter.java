package com.sagesurfer.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import com.sagesurfer.collaborativecares.BlockedMembersActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.network.NetworkCall_;
import com.storage.preferences.Preferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import screen.messagelist.General;
import screen.messagelist.Urls_;

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
                String[] array = teams_.getUid().split("_");
                // comet chat unblocked selected  user
                Log.i(TAG, "onClick: block user id "+ array[0]+ "  user_Id  " + Preferences.get(com.sagesurfer.constant.General.USER_ID) );
                CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
                    @Override
                    public void onSuccess(HashMap<String, String> resultMap) {
                        // Handle unblock users success.
                        Log.i(TAG, "onSuccess: teamId "+teams_.getUid());
                        unblockUserIntoDatabase(array[0]);
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


    public void unblockUserIntoDatabase(String blockedUID) {
        String action = "block_user_delete_db";
        //String[] array = blockedUID.split("_");
        String url = Urls_.SAVE_UNBLOCK_USER_TO_THE_SERVER;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.RECEIVER_ID, blockedUID);
        requestMap.put(General.USER_ID, Preferences.get(com.sagesurfer.constant.General.USER_ID));

        Log.i(TAG, "unblockUserIntoDatabase:  receiver_Id  " +blockedUID +"  user_Id  " + Preferences.get(com.sagesurfer.constant.General.USER_ID) + " url " + Preferences.get(com.sagesurfer.constant.General.DOMAIN) + url);
        RequestBody requestBody = NetworkCall_.make(requestMap, Preferences.get(com.sagesurfer.constant.General.DOMAIN) + url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(Preferences.get(com.sagesurfer.constant.General.DOMAIN) + url, requestBody, TAG, mContext);
                if (response != null) {
                    Log.i(TAG, "unblockUserIntoDatabase:  response " + response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

