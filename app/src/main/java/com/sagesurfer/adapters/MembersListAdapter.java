package com.sagesurfer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.sagesurfer.models.GetAddNewMember;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.storage.preferences.Preferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.MyViewHolder> implements Filterable {
    private final Context mContext;
    private final ArrayList<GetAddNewMember> groupmemberList;
    private ArrayList<GetAddNewMember> primaryGroupList = new ArrayList<>();
    private static final String TAG = FragmentCometchatGroupsList.class.getSimpleName();
    private String groupType;

    MembersListAdapter(Context mContext, ArrayList<GetAddNewMember> groupmemberList, String groupType) {
        this.mContext = mContext;
        this.groupmemberList = groupmemberList;
        this.groupType = groupType;
        this.primaryGroupList.addAll(groupmemberList);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView title, txtInviteMember;
        Avatar userAvatar;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txtmemberName);
            txtInviteMember = view.findViewById(R.id.txtInviteMember);
            userAvatar = view.findViewById(R.id.groupMember_list_item_photo);
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_members, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        GetAddNewMember teams_ = groupmemberList.get(position);

        holder.title.setText(teams_.getName());
        holder.userAvatar.setAvatar(teams_.getPhoto());
        holder.txtInviteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(mContext);
                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to send request to user?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // send invition to user for add into group
                                if (groupType.equals("private")) {
                                    Log.i(TAG, "onClick: invite user in password group");
                                    String action = "invite_public_friend";
                                    String groupid = Preferences.get("gId");
                                    String UserId = Preferences.get(General.USER_ID);
                                    String reciverId = teams_.getComet_chat_id();
                                    String rev = teams_.getUserId();
                                    addMemberTogroup(UserId, groupid, action, reciverId, rev, position);
                                    //invite(action, UserId, groupid, reciverId, position);
                                    dialog.cancel();
                                } else {
                                    if (groupmemberList.get(position).getIs_friend().equals("0")) {
                                        /*if the user is not already a friend of the sender then that user will get invitation to accept or decline  add in to the group
                                         * so this block will add user in group  */
                                        Log.i(TAG, "onClick: invite user public or password block 1");
                                        String action = "add_member_invite";
                                        String groupid = Preferences.get("gId");
                                        String UserId = Preferences.get(General.USER_ID);
                                        String reciverId = teams_.getComet_chat_id();
                                        String rev = teams_.getUserId();
                                        invite(action, UserId, groupid, reciverId, rev, position);
                                        Log.e("Yesy", "TEST");
                                        dialog.cancel();

                                    } else {
                                        Log.i(TAG, "onClick: invite user public or password block 2");
                                        /*if the user is already a friend of the sender then that user will directly add in to the group
                                        * so this block will add user in group*/
                                        String action = "invite_public_friend";
                                        String groupid = Preferences.get("gId");
                                        String UserId = Preferences.get(General.USER_ID);
                                        String reciverId = teams_.getComet_chat_id();
                                        String rev = teams_.getUserId();
                                        Log.e("No", "TEST");
                                        addMemberTogroup(UserId, groupid, action, reciverId, rev, position);
                                        Log.e("cometId", reciverId);
                                        dialog.cancel();
                                    }
                                }
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

    public void addMemberTogroup(String uid, String GUID, String action, String reciveverId, String rec, int position) {
        List<GroupMember> userList = new ArrayList<>();
        userList.add(new GroupMember(reciveverId, CometChatConstants.SCOPE_PARTICIPANT));

        CometChat.addMembersToGroup(GUID, userList, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Log.e(TAG, "onSuccess: addMemberTogroup cometchat" + userList + "Group" + GUID);
                invite(action, uid, GUID, reciveverId, rec, position);
            }
            @Override
            public void onError(CometChatException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupmemberList.size();
    }

    private void invite(String action, String userId, String gId, String reciver, String rec, int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, reciver);
        requestMap.put(General.GROUP_ID, gId);
        requestMap.put("receiver", rec);
        Log.i(TAG, "invite: userId "+userId +" receiverId "+reciver);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        Log.e("invite", requestMap.toString());

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext);
                //AddMemberInGroupInCometchat(reciver,gId);
                Log.e("response q", response);

                if (response != null) {
                    Toast.makeText(mContext, "Request send successfully", Toast.LENGTH_LONG).show();
                    groupmemberList.remove(position);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<GetAddNewMember> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(primaryGroupList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (GetAddNewMember item : primaryGroupList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            groupmemberList.clear();
            groupmemberList.addAll((List) results.values);
            if (groupmemberList.isEmpty()) {
                Toast.makeText(mContext, "No Result Found",
                        Toast.LENGTH_LONG).show();
            }
            notifyDataSetChanged();
        }
    };

    public void AddMemberInGroupInCometchat(String reciver, String gId){
        List<GroupMember> members = new ArrayList<>();
        members.add(new GroupMember(reciver,CometChatConstants.SCOPE_PARTICIPANT));
        //members.add(new GroupMember("uid2", CometChatConstants.SCOPE_ADMIN));

        CometChat.addMembersToGroup(gId, members, null, new CometChat.CallbackListener<HashMap<String, String>>(){
            @Override
            public void onSuccess(HashMap<String, String> successMap) {
                Log.d("CometChatActivity","user added in group "+ successMap.toString());
            }

            @Override
            public void onError(CometChatException e) {
                Log.i(TAG, "onError: "+e.getMessage());
            }
        });
    }
}
