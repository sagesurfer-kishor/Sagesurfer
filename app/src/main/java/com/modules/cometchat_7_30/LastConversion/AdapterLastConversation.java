package com.modules.cometchat_7_30.LastConversion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.modules.cometchat_7_30.ModelUserCount;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.Members_;
import com.storage.preferences.Preferences;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import constant.StringContract;
import screen.messagelist.Call_;
import screen.messagelist.CometChatMessageListActivity;
import screen.messagelist.General;
import utils.Utils;

public class AdapterLastConversation extends RecyclerView.Adapter<AdapterLastConversation.MyViewHolder> implements Filterable {
    private final Context mContext;
    private final List<Conversation> conversationList;
    private List<Conversation> fullConversationList;
    private List<ModelUserCount> al_unreadCountList;
    private FragmentLastConversation fragment;
    private static final String TAG = "AdapterLastConversation";
    private FragmentLastConversation currentFragment;

    public AdapterLastConversation(FragmentLastConversation fragment, List<Conversation> conversationList, Context mContext, FragmentLastConversation fragmentLastConversation) {
        this.mContext = mContext;
        this.conversationList = new ArrayList<>(conversationList);
        this.fullConversationList = new ArrayList<>(conversationList);
        this.al_unreadCountList = al_unreadCountList;
        currentFragment=fragmentLastConversation;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_conversation_item, parent, false);
        return new AdapterLastConversation.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Conversation conversation = conversationList.get(position);
        if (conversation.getConversationType().equals("user")) {
                Log.i(TAG, "onBindViewHolder: metadata " + conversation.getLastMessage().getMetadata());
                    holder.title.setText(((User) conversation.getConversationWith()).getName());
                    if (conversation.getLastMessage().getType().equals("text")) {
                        holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);

                        if (((TextMessage) conversation.getLastMessage()).getText() != null) {
                            holder.friend_list_item_statusmessage.setText(((TextMessage) conversation.getLastMessage()).getText().trim());
                            holder.messageTime.setText(Utils.getLastMessageDate(((TextMessage) conversation.getLastMessage()).getSentAt()));
                        }
                    }else if (conversation.getLastMessage().getType().equalsIgnoreCase("image")
                            || conversation.getLastMessage().getType().equalsIgnoreCase("file")
                            || conversation.getLastMessage().getType().equalsIgnoreCase("audio")
                            || conversation.getLastMessage().getType().equalsIgnoreCase("video")
                            || conversation.getLastMessage().getType().equalsIgnoreCase("extension_whiteboard") ){
                        String type=(conversation.getLastMessage()).getType();
                        holder.friend_list_item_statusmessage.setText(""+type);
                        holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                        holder.messageTime.setText(Utils.getLastMessageDate(( conversation.getLastMessage()).getSentAt()));
                    }else if(conversation.getLastMessage().getType().equals("call")){
                        holder.friend_list_item_statusmessage.setText(""+conversation.getLastMessage().getType());
                        holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                        holder.messageTime.setText(Utils.getLastMessageDate((conversation.getLastMessage()).getSentAt()));
                    }
                    Glide.with(mContext).load(((User) conversation.getConversationWith()).getAvatar()).into(holder.imgBan);
                    String status = ((User) conversation.getConversationWith()).getStatus();
                   // String UID = ((User) conversation.getConversationWith()).getUid();
                    int counter = conversation.getUnreadMessageCount();
                    if (counter != 0) {
                        holder.tv_counter.setVisibility(View.VISIBLE);
                        holder.tv_counter.setText("" + counter);
                    }
                    if (status.equals("online")) {
                        holder.activeUser.setImageResource(R.drawable.online_sta);
                    } else {
                        holder.activeUser.setImageResource(R.drawable.offline_sta);
                    }
        } else if (conversation.getConversationType().equals("group")) {
            holder.title.setText(((Group) conversation.getConversationWith()).getName());
            if (conversation.getLastMessage() != null) {
                Log.i(TAG, "onBindViewHolder: time" + conversation.getLastMessage().getDeliveredToMeAt());
                if (conversation.getLastMessage().getType().equals("text")) {
                    holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                    holder.friend_list_item_statusmessage.setText(((TextMessage) conversation.getLastMessage()).getText());
                    holder.messageTime.setText(Utils.getLastMessageDate(((TextMessage) conversation.getLastMessage()).getSentAt()));
                }else if (conversation.getLastMessage().getType().equalsIgnoreCase("image")
                        || conversation.getLastMessage().getType().equalsIgnoreCase("file")
                        || conversation.getLastMessage().getType().equalsIgnoreCase("audio")
                        || conversation.getLastMessage().getType().equalsIgnoreCase("video")
                        || conversation.getLastMessage().getType().equalsIgnoreCase("extension_whiteboard") ){
                    String type=(conversation.getLastMessage()).getType();
                    holder.friend_list_item_statusmessage.setText(""+type);
                    holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                    holder.messageTime.setText(Utils.getLastMessageDate(( conversation.getLastMessage()).getSentAt()));
                }else if(conversation.getLastMessage().getType().equals("call")){
                    holder.friend_list_item_statusmessage.setText(""+conversation.getLastMessage().getType());
                    holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                    holder.messageTime.setText(Utils.getLastMessageDate((conversation.getLastMessage()).getSentAt()));
                }
            }else{
                holder.friend_list_item_statusmessage.setText("Chat not initiated yet");
                holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
            }
        }

        holder.cl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: conversation item");
                if (conversation.getConversationType().equals("user")) {
                    fragment.progressBar.setVisibility(View.VISIBLE);
                    String allProvidersString = Preferences.get("providers");
                    Log.i(TAG, "onClick: provider array "+allProvidersString);
                    fragment.onUserClickPerform(conversation);
                }else{
                    fragment.onGroupClickedPerform(conversation);
                }
                /*if (conversation.getConversationType().equals("user")) {
                    Intent intent = new Intent(mContext, CometChatMessageListActivity.class);
                    intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                    intent.putExtra(StringContract.IntentStrings.NAME, (((User) conversation.getConversationWith()).getName()));
                    intent.putExtra(StringContract.IntentStrings.SENDER_ID, (((User) conversation.getConversationWith()).getUid()));
                    intent.putExtra(StringContract.IntentStrings.AVATAR, (((User) conversation.getConversationWith()).getAvatar()));
                    intent.putExtra(StringContract.IntentStrings.STATUS, (((User) conversation.getConversationWith()).getStatus()));
                    intent.putExtra(StringContract.IntentStrings.TABS, "1");
                    mContext.startActivity(intent);
                } else {

                    for (int i = 0; i < fragment.primaryGroupList.size(); i++) {
                        if (fragment.primaryGroupList.get(i).getGroupId().equals(((Group) conversation.getConversationWith()).getGuid())) {
                            Log.i(TAG, "onClick: fragment.primaryGroupList.get(i).getGroupId() " + fragment.primaryGroupList.get(i).getGroupId());
                            Log.i(TAG, "onClick: " + ((Group) conversation.getConversationWith()).getGuid());
                            StringBuffer sbGroupMemberIds = new StringBuffer("");
                            ArrayList<Members_> membersArrayList = fragment.primaryGroupList.get(i).getMembersArrayList();
                            int iterator = 0;
                            for (Members_ m : membersArrayList) {
                                iterator = iterator + 1;
                                Log.i(TAG, "onClick: chat id " + m.getComet_chat_id());
                                Log.i(TAG, "onClick: userId" + m.getUser_id());
                                String listGroupMembers = String.valueOf(m.getUser_id());

                                if (sbGroupMemberIds.length() == 0) {
                                    sbGroupMemberIds.append(listGroupMembers);
                                } else {
                                    sbGroupMemberIds.append("," + listGroupMembers);
                                }
                                Log.i(TAG, General.MY_TAG + "onClick: sbGroupMemberIds " + sbGroupMemberIds);
                                Log.i(TAG, General.MY_TAG + "onClick: list size  " + membersArrayList.size() + " iterator " + iterator);
                                if (membersArrayList.size() == iterator) {
                                    Intent intent = new Intent(mContext, CometChatMessageListActivity.class);
                                    intent.putExtra(StringContract.IntentStrings.TYPE, "group");
                                    intent.putExtra(StringContract.IntentStrings.NAME, ((Group) conversation.getConversationWith()).getName());
                                    intent.putExtra(StringContract.IntentStrings.GUID, ((Group) conversation.getConversationWith()).getGuid());
                                    intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, ((Group) conversation.getConversationWith()).getOwner());
                                    intent.putExtra(StringContract.IntentStrings.AVATAR, "https://designstaging.sagesurfer.com/static//avatar/thumb/man.jpg");
                                    intent.putExtra(StringContract.IntentStrings.GROUP_TYPE, ((Group) conversation.getConversationWith()).getGroupType());
                                    intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT, ((Group) conversation.getConversationWith()).getMembersCount());
                                    intent.putExtra(StringContract.IntentStrings.GROUP_DESC, ((Group) conversation.getConversationWith()).getDescription());
                                    intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD, ((Group) conversation.getConversationWith()).getPassword());
                                    intent.putExtra(StringContract.IntentStrings.ALL_MEMBERS_STRING, "" + sbGroupMemberIds);
                                    intent.putExtra(StringContract.IntentStrings.TABS, "2");
                                    mContext.startActivity(intent);
                                }
                            }
                        }
                    }
                }*/
            }
        });
        Log.i(TAG, "onBindViewHolder:  end");
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        final TextView title, friend_list_item_statusmessage;
        ImageView imgBan;
        ImageView activeUser;
        ConstraintLayout cl_main;
        TextView txtView, tv_counter, messageTime;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.friend_list_itemname);
            imgBan = view.findViewById(R.id.friend_list_itemphoto);
            activeUser = view.findViewById(R.id.friend_list_item_statusicon);
            messageTime = view.findViewById(R.id.messageTime);
            cl_main = view.findViewById(R.id.cl_main);
            // txtView = view.findViewById(R.id.txt_errorMsg);
            tv_counter = view.findViewById(R.id.friend_ic_counter);
            friend_list_item_statusmessage = view.findViewById(R.id.friend_list_item_statusmessage);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Conversation> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0 || constraint.equals("")) {
                filteredList.addAll(fullConversationList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Conversation item : fullConversationList) {
                    if (item.getConversationType().equals("group")) {
                        if (((Group) item.getConversationWith()).getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    } else if (item.getConversationType().equals("user"))
                        if (((User) item.getConversationWith()).getName().toLowerCase().contains(filterPattern)) {
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
            conversationList.clear();
            conversationList.addAll((List<Conversation>) results.values);
            if (conversationList.isEmpty()) {
                Toast.makeText(mContext, "No Result Found", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        }
    };
}