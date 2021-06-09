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

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.uikit.Avatar;
import com.modules.cometchat_7_30.FragmentCometchatGroupsList;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

public class AdapterAllBanMembersInGroup extends RecyclerView.Adapter<AdapterAllBanMembersInGroup.MyViewHolder> {

    private final Context mContext;
    private final List<GroupMember> teamList;
    private static final String TAG = FragmentCometchatGroupsList.class.getSimpleName();

    AdapterAllBanMembersInGroup(Context mContext, List<GroupMember> teamList) {
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
                String CometchatUid = teams_.getUid();
                String GUID = Preferences.get("gId");
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(mContext);
                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to unban this member?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "onClick: UID " + CometchatUid + " GUID " + GUID);

                                CometChat.unbanGroupMember(CometchatUid, GUID, new CometChat.CallbackListener<String>() {
                                    @Override
                                    public void onSuccess(String successMessage) {
                                        String[] Array = CometchatUid.split("_");
                                        Log.e(TAG, "onSuccess: unbanUser "+Array[0]);
                                        unblockedMember("unblock_member", GUID, Array[0], position,CometchatUid);
                                        //addMemberToGroup("unblock_member", GUID, UID, position);
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

    public void addMemberToGroupFromCometchat(String GUID, String cometchatUid) {
        List<GroupMember> userList = new ArrayList<>();
        userList.add(new GroupMember(cometchatUid, CometChatConstants.SCOPE_PARTICIPANT));
        CometChat.addMembersToGroup(GUID, userList, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Log.e(TAG, "addMemberToGroupFromCometchat onSuccess: " + teamList + "Group" + GUID);
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    private void unblockedMember(String action, String groupId, String uId, int position,String cometchatUid) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.GROUP_ID, groupId);
        requestMap.put(General.BLOCK_USER_ID, uId);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext);
                if (response != null) {
                    Log.i(TAG, "unblockedMember: success");
                    Toast.makeText(mContext, "unblocked user Successfully", Toast.LENGTH_LONG).show();
                    teamList.remove(position);
                    notifyDataSetChanged();
                    addMemberToGroupFromCometchat( groupId, cometchatUid);
                }
            } catch (Exception e) {
                Log.i(TAG, "unblockedMember: onError " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}