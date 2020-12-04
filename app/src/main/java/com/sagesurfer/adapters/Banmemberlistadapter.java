package com.sagesurfer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.uikit.Avatar;
import com.modules.cometchat_7_30.ChatroomFragment_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

public class Banmemberlistadapter extends RecyclerView.Adapter<Banmemberlistadapter.MyViewHolder> {

    private final Context mContext;
    private final List<GroupMember> teamList;
    private static final String TAG = ChatroomFragment_.class.getSimpleName();

    Banmemberlistadapter(Context mContext, List<GroupMember> teamList) {
        this.mContext = mContext;
        this.teamList = teamList;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        final TextView title;
        TextView imgBan;
        Avatar userAvatar;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.list);
            imgBan = view.findViewById(R.id.unblockedId);
            userAvatar = view.findViewById(R.id.ban_item_photo);
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
        GroupMember teams_ = teamList.get(position);

        holder.title.setText(teams_.getName());
        holder.userAvatar.setAvatar(teams_.getAvatar());

        // unban user
        holder.imgBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String UID = teams_.getUid();
                String GUID = Preferences.get("gId");
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(mContext);

                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to unblock this member?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CometChat.unbanGroupMember(UID, GUID, new CometChat.CallbackListener<String>() {
                                    @Override
                                    public void onSuccess(String successMessage) {
                                        unblockedMember("unblock_member", GUID, UID, position);
                                    }

                                    @Override
                                    public void onError(CometChatException e) {
                                        Log.d(TAG, "Group Member unbanning failed with exception: " + e.getMessage());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert Notification");
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    private void unblockedMember(String action, String gId, String uId, int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.GROUP_ID, gId);
        requestMap.put(General.USER_ID, uId);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext);
                if (response != null) {
                    Toast.makeText(mContext, "unblocked user Successfully", Toast.LENGTH_LONG).show();
                    teamList.remove(position);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
