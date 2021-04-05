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

class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.MyViewHolder> {

    private final Context mContext;
    private static final String TAG = FragmentCometchatGroupsList.class.getSimpleName();
    String adminUid = "";

    RecyclerView recyclerView;
    public List<GroupMember> groupMemberArrayList = new ArrayList<>();

    GroupMembersAdapter(Context mContext, List<GroupMember> groupMemberArrayList) {
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

        ImageView blockuser, deleteUser;
        private Avatar userAvatar;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txtGroupmemberName);
            deleteUser = (ImageView) view.findViewById(R.id.Removemember);
            blockuser = (ImageView) view.findViewById(R.id.blockedMember);
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
        GroupMember teams_ = groupMemberArrayList.get(position);

        String uId = teams_.getUid().split(Pattern.quote("_"))[0];
        String scope = teams_.getScope();

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

        // Block button
        holder.blockuser.setVisibility(View.GONE);
        if (myUserId.equals(adminUid) && !adminUid.equals(uId)) {
            holder.blockuser.setVisibility(View.VISIBLE);
        }

        holder.title.setText(teams_.getName());
        holder.userAvatar.setAvatar(teams_.getAvatar());

        holder.blockuser.setOnClickListener(view -> {
            String uid = groupMemberArrayList.get(position).getUid();
            String GUID = Preferences.get("gId");
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(mContext);

            //Setting message manually and performing action on button click
            builder.setMessage("Do you want to Ban this member?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CometChat.banGroupMember(uid, GUID, new CometChat.CallbackListener<String>() {
                                @Override
                                public void onSuccess(String successMessage) {
                                    Log.d(TAG, "Group member banned successfully");
                                    String action = "block_member";
                                    String UserId = uid;

                                    blockMember(action, UserId, GUID, position);

                                    dialog.dismiss();
                                }

                                @Override
                                public void onError(CometChatException e) {
                                    Log.d(TAG, "Group member banning failed with exception: " + e.getMessage());
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

    private void blockMember(String action, String userId, String gId, int position) {

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, userId);
        requestMap.put(General.GROUP_ID, gId);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext);
                Log.e("33333", response);
                if (response != null) {
                    Toast.makeText(mContext, "Blocked member successfully", Toast.LENGTH_LONG).show();
                    groupMemberArrayList.remove(position);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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

