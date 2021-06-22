package com.sagesurfer.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.RequestBody;

class AdapterAllGroupMembers extends RecyclerView.Adapter<AdapterAllGroupMembers.MyViewHolder> {

    private final Context mContext;
    private static final String TAG = FragmentCometchatGroupsList.class.getSimpleName();
    String adminUid = "";

    RecyclerView recyclerView;
    public List<GroupMember> groupMemberArrayList = new ArrayList<>();

    AdapterAllGroupMembers(Context mContext, List<GroupMember> groupMemberArrayList) {
        this.mContext = mContext;
        this.groupMemberArrayList = groupMemberArrayList;

        for (GroupMember member : groupMemberArrayList) {
            if (member.getScope().equals("admin")) {
                adminUid = member.getUid().split(Pattern.quote("_"))[0];
                break;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView title;

        ImageView blockuser, deleteUser,friend_list_item_status_icon;
        private Avatar userAvatar;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txtGroupmemberName);
            deleteUser = (ImageView) view.findViewById(R.id.Removemember);
            blockuser = (ImageView) view.findViewById(R.id.blockedMember);
            friend_list_item_status_icon = (ImageView) view.findViewById(R.id.friend_list_item_status_icon);
            userAvatar = view.findViewById(R.id.groupMember_list_item_photo);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_members, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GroupMember member = groupMemberArrayList.get(position);
        String uId = member.getUid().split(Pattern.quote("_"))[0];
        String scope = member.getScope();

        // Code by Debopam
        //Delete button     
        String myUserId = Preferences.get(General.USER_ID);
        if (myUserId.equals(uId)) {
            if (scope.equals("admin")) {
                holder.deleteUser.setVisibility(View.GONE);
            } else
                holder.deleteUser.setVisibility(View.VISIBLE);
        } else {
            holder.deleteUser.setVisibility(View.GONE);
        }

        // check user is online or offline
        String status = member.getStatus();
        if (status.equals("online")) {
            holder.friend_list_item_status_icon.setImageResource(R.drawable.online_sta);
        } else {
            holder.friend_list_item_status_icon.setImageResource(R.drawable.offline_sta);
        }
        // Block button
        holder.blockuser.setVisibility(View.GONE);
        if (myUserId.equals(adminUid) && !adminUid.equals(uId)) {
            holder.blockuser.setVisibility(View.VISIBLE);
        }
        holder.title.setText(member.getName());
        holder.userAvatar.setAvatar(member.getAvatar());
        holder.blockuser.setOnClickListener(view -> {
            Log.i(TAG, "onBindViewHolder: id of the user " + groupMemberArrayList.get(position).getUid());
            Log.i(TAG, "onBindViewHolder: id of the user " + member.getUid());
            String[] arrayId= member.getUid().split("_");
            Log.i(TAG, "onBindViewHolder: id of the user " + groupMemberArrayList.get(position).getUid());
            Log.i(TAG, "onBindViewHolder: id of the blocked user " + arrayId[0]);
            Log.i(TAG, "onBindViewHolder: id of the group id " + Preferences.get("gId"));
            String cometChatUid = groupMemberArrayList.get(position).getUid();
            String GUID = Preferences.get("gId");
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(mContext);
            //Setting message manually and performing action on button click
            builder.setMessage("Do you want to Ban this member?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i(TAG, "onBindViewHolder onClick: userId "+arrayId[0] +" group_id "+GUID +" cometchat Id"+cometChatUid);
                            CometChat.banGroupMember(cometChatUid, GUID, new CometChat.CallbackListener<String>() {
                                @Override
                                public void onSuccess(String successMessage) {
                                    Log.d(TAG, "Group member banned successfully ");
                                    String action = "block_member";
                                    String UserId=arrayId[0];
                                    blockMemberOnServer(action,UserId , GUID, position);
                                    dialog.dismiss();
                                }
                                @Override
                                public void onError(CometChatException e) {
                                    Log.d(TAG, "onBindViewHolder Group member banning failed with exception: " + e.getMessage());
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
        });

        holder.deleteUser.setOnClickListener(view -> {

            String memberIds = groupMemberArrayList.get(position).getUid().split(Pattern.quote("_"))[0];

            String UID = Preferences.get(General.USER_ID);
            String GUID = Preferences.get("gId");

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(mContext);

            //Setting message manually and performing action on button click
            builder.setMessage("Do you want to Delete this member?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            CometChat.leaveGroup(GUID, new CometChat.CallbackListener<String>() {
                                @Override
                                public void onSuccess(String successMessage) {
                                    Log.d(TAG, successMessage);
                                    String action = "delete_member";
                                    DeleteMember(action, UID, GUID, memberIds, position);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onError(CometChatException e) {
                                    Log.d(TAG, "Group leaving failed with exception: " + e.getMessage());
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

        });

    }

    @Override
    public int getItemCount() {
        return groupMemberArrayList.size();
    }

    /*  ban member from group and this update we are also storing on server so that we can get right data when we fetch data from server
    *   first we ban group member from cometchat and then we ban member form server also*/
    private void blockMemberOnServer(String action, String userId, String gId, int position) {
        Log.i(TAG, "blockMemberOnServer: userId "+userId + " groupId "+gId);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.BLOCK_USER_ID, userId);
        requestMap.put(General.GROUP_ID, gId);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext);
                Log.e("blockMember ", response);
                if (response != null) {
                    Toast.makeText(mContext, "Member banned successfully", Toast.LENGTH_LONG).show();
                    groupMemberArrayList.remove(position);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*Delete functionality for the group created by owner and owner can delete that group*/
    private void DeleteMember(String action, String userId, String gId, String memberIds, int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, userId);
        requestMap.put(General.GROUP_ID, gId);
        requestMap.put("member_id", memberIds);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext);
                Log.e("33333", response);
                if (response != null) {
                    Toast.makeText(mContext, "group left successfully", Toast.LENGTH_LONG).show();
                    groupMemberArrayList.remove(position);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
