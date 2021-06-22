package com.modules.cometchat_7_30.LastConversion;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.modules.cometchat_7_30.ModelUserCount;
import com.sagesurfer.collaborativecares.R;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import utils.Utils;

import static android.content.Context.MODE_PRIVATE;

public class AdapterLastConversation extends RecyclerView.Adapter<AdapterLastConversation.MyViewHolder> implements Filterable {
    private final Context mContext;
    private final List<Conversation> conversationList;
    private List<Conversation> fullConversationList;
    private List<ModelUserCount> al_unreadCountList;
    private FragmentLastConversation fragment;
    private static final String TAG = "AdapterLastConversation";
    private FragmentLastConversation currentFragment;
    SharedPreferences sp;

    public AdapterLastConversation(FragmentLastConversation fragment, List<Conversation> conversationList, Context mContext, FragmentLastConversation fragmentLastConversation) {
        this.mContext = mContext;
        this.conversationList = new ArrayList<>(conversationList);
        this.fullConversationList = new ArrayList<>(conversationList);
        this.al_unreadCountList = al_unreadCountList;
        currentFragment = fragmentLastConversation;
        this.fragment = fragment;
        sp = mContext.getSharedPreferences("login", MODE_PRIVATE);
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
            String name = ((User) conversation.getConversationWith()).getName();

            if (name.length() > 20) {
                Log.i(TAG, "onBindViewHolder: name " + name.substring(0, 19));
                holder.title.setText(name.substring(0, 19));
            } else {
                holder.title.setText(((User) conversation.getConversationWith()).getName());
            }
            if (conversation.getLastMessage().getType().equals("text")) {
                holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                if (((TextMessage) conversation.getLastMessage()).getText() != null) {
                    if (((TextMessage) conversation.getLastMessage()).getMetadata().has("deleted_one_to_one")) {
                        holder.friend_list_item_statusmessage.setText(mContext.getResources().getString(R.string.delete_message));
                        holder.messageTime.setVisibility(View.GONE);
                    } else {
                        try {
                            JSONObject body = new JSONObject();
                            JSONArray languages = new JSONArray();
                            languages.put(sp.getString("currentLang", "en"));
                            body.put("msgId", ((TextMessage) conversation.getLastMessage()).getId());
                            body.put("languages", languages);
                            body.put("text", ((TextMessage) conversation.getLastMessage()).getText());
                            Log.i(TAG, "filterBaseMessages: edited at block 2");
                            // Do something after 5s = 5000ms
                            CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                    new CometChat.CallbackListener<JSONObject>() {
                                        @Override
                                        public void onSuccess(JSONObject jsonObject) {
                                            try {
                                                String messageTranslatedString = null;
                                                messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                                Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                holder.friend_list_item_statusmessage.setText(messageTranslatedString);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(CometChatException e) {
                                            // Some error occured
                                            Log.i(TAG, "onError: " + e.getMessage());
                                            Log.i(TAG, "filterBaseMessages: edited at block 4");
                                        }
                                    });
                            holder.messageTime.setText(Utils.getLastMessageDate(((TextMessage) conversation.getLastMessage()).getSentAt()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (conversation.getLastMessage().getType().equalsIgnoreCase("image")
                    || conversation.getLastMessage().getType().equalsIgnoreCase("file")
                    || conversation.getLastMessage().getType().equalsIgnoreCase("audio")
                    || conversation.getLastMessage().getType().equalsIgnoreCase("video")
                    || conversation.getLastMessage().getType().equalsIgnoreCase("extension_whiteboard")) {
                String deleteOneToOne = null;
                try {
                    if ((conversation.getLastMessage()).getMetadata().has("deleted_one_to_one")) {
                        deleteOneToOne = (conversation.getLastMessage()).getMetadata().getString("deleted_one_to_one");
                        if (deleteOneToOne.equalsIgnoreCase("0") || deleteOneToOne.equalsIgnoreCase("1")
                                || deleteOneToOne.equalsIgnoreCase("2") || (conversation.getLastMessage()).getDeletedAt() != 0)
                            holder.friend_list_item_statusmessage.setText(mContext.getResources().getString(R.string.delete_message));
                        holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                        holder.messageTime.setText(Utils.getLastMessageDate((conversation.getLastMessage()).getSentAt()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (conversation.getLastMessage().getType().equalsIgnoreCase("image")
                    || conversation.getLastMessage().getType().equalsIgnoreCase("file")
                    || conversation.getLastMessage().getType().equalsIgnoreCase("audio")
                    || conversation.getLastMessage().getType().equalsIgnoreCase("video")
                    || conversation.getLastMessage().getType().equalsIgnoreCase("extension_whiteboard")) {
                String type = (conversation.getLastMessage()).getType();
                holder.friend_list_item_statusmessage.setText("" + type);
                holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                holder.messageTime.setText(Utils.getLastMessageDate((conversation.getLastMessage()).getSentAt()));
            } else if (conversation.getLastMessage().getType().equals("call")) {
                holder.friend_list_item_statusmessage.setText("" + conversation.getLastMessage().getType());
                holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                holder.messageTime.setText(Utils.getLastMessageDate((conversation.getLastMessage()).getSentAt()));
            }

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

            if (conversation.getLastMessage().getMetadata() != null) {
                if (conversation.getLastMessage().getMetadata().has("team_logs_id")) {
                    try {
                        if (conversation.getLastMessage().getMetadata().getString("team_logs_id").equalsIgnoreCase("0")
                            /* || conversation.getLastMessage().getMetadata().getString("team_logs_id") != null*/) {
                            Glide.with(mContext).load(((User) conversation.getConversationWith()).getAvatar()).into(holder.iv_profile);
                        } else {
                            String team_logs_id = conversation.getLastMessage().getMetadata().getString("team_logs_id");
                            String[] array = team_logs_id.split("_-");
                            if (array[2].equalsIgnoreCase("3")) {
                                holder.iv_profile.setImageResource(R.drawable.team_iconn);
                            } else if (array[2].equalsIgnoreCase("4")) {
                                holder.iv_profile.setImageResource(R.drawable.team_iconn);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else if (conversation.getConversationType().equals("group")) {
            String name= ((Group) conversation.getConversationWith()).getName();
            if (name.length() > 20) {
                Log.i(TAG, "onBindViewHolder: name " + name.substring(0, 19));
                holder.title.setText(name.substring(0, 19)+"...");
            } else {
                holder.title.setText(((Group) conversation.getConversationWith()).getName());
            }
            //
            if (((Group) conversation.getConversationWith()).getGroupType().equals("public")) {
                holder.iv_profile.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pub_group));
            } else if (((Group) conversation.getConversationWith()).getGroupType().equals("private")) {
                holder.iv_profile.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pri_group));
            } else {
                holder.iv_profile.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pass_group));
            }

            if (conversation.getLastMessage() != null) {
                Log.i(TAG, "group onBindViewHolder: group name " + ((Group) conversation.getConversationWith()).getName());
                if (conversation.getLastMessage().getType().equals("text")) {
                    Log.i(TAG, "group onBindViewHolder: is text == true");
                    if (((TextMessage) conversation.getLastMessage()).getMetadata() != null) {
                        Log.i(TAG, "group onBindViewHolder: metadata is != null");
                        if (((TextMessage) conversation.getLastMessage()).getMetadata().has("deleted_one_to_one")) {
                            Log.i(TAG, "group onBindViewHolder: has one-to-one");
                            holder.friend_list_item_statusmessage.setText(mContext.getResources().getString(R.string.delete_message));
                            holder.messageTime.setVisibility(View.GONE);
                        } else {
                            Log.i(TAG, "group onBindViewHolder: has no one-to-one");
                            holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                            try {
                                JSONObject body = new JSONObject();
                                JSONArray languages = new JSONArray();
                                languages.put(sp.getString("currentLang", "en"));
                                body.put("msgId", ((TextMessage) conversation.getLastMessage()).getId());
                                body.put("languages", languages);
                                body.put("text", ((TextMessage) conversation.getLastMessage()).getText());
                                Log.i(TAG, "filterBaseMessages: edited at block 2");
                                // Do something after 5s = 5000ms
                                CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                        new CometChat.CallbackListener<JSONObject>() {
                                            @Override
                                            public void onSuccess(JSONObject jsonObject) {
                                                try {
                                                    String messageTranslatedString = null;
                                                    messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                                    Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                    holder.friend_list_item_statusmessage.setText(messageTranslatedString);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(CometChatException e) {
                                                // Some error occured
                                                Log.i(TAG, "onError: " + e.getMessage());
                                                Log.i(TAG, "filterBaseMessages: edited at block 4");
                                            }
                                        });
                                holder.messageTime.setText(Utils.getLastMessageDate(((TextMessage) conversation.getLastMessage()).getSentAt()));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            holder.messageTime.setText(Utils.getLastMessageDate(((TextMessage) conversation.getLastMessage()).getSentAt()));
                        }
                    } /*else {
                        holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                        holder.friend_list_item_statusmessage.setText(((TextMessage) conversation.getLastMessage()).getText());
                        holder.messageTime.setText(Utils.getLastMessageDate(((TextMessage) conversation.getLastMessage()).getSentAt()));
                    }*/
                    if (conversation.getLastMessage().getDeletedAt() != 0) {
                        holder.friend_list_item_statusmessage.setText(mContext.getResources().getString(R.string.delete_message));
                        holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                        holder.messageTime.setText(Utils.getLastMessageDate((conversation.getLastMessage()).getSentAt()));
                    }
                } else if (conversation.getLastMessage().getType().equalsIgnoreCase("image")
                        || conversation.getLastMessage().getType().equalsIgnoreCase("file")
                        || conversation.getLastMessage().getType().equalsIgnoreCase("audio")
                        || conversation.getLastMessage().getType().equalsIgnoreCase("video")
                        || conversation.getLastMessage().getType().equalsIgnoreCase("extension_whiteboard")) {
                    String type = (conversation.getLastMessage()).getType();
                    holder.friend_list_item_statusmessage.setText("" + type);
                    holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                    holder.messageTime.setText(Utils.getLastMessageDate((conversation.getLastMessage()).getSentAt()));
                } else if (conversation.getLastMessage().getType().equals("call")) {
                    holder.friend_list_item_statusmessage.setText("" + conversation.getLastMessage().getType());
                    holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
                    holder.messageTime.setText(Utils.getLastMessageDate((conversation.getLastMessage()).getSentAt()));
                }
            } else {
                holder.friend_list_item_statusmessage.setText("Chat not initiated yet");
                holder.friend_list_item_statusmessage.setVisibility(View.VISIBLE);
            }
        }

        holder.cl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: conversation item");
                try {
                    if (conversation.getConversationType().equals("user")) {
                        fragment.progressBar.setVisibility(View.VISIBLE);
                        String allProvidersString = Preferences.get("providers");
                        Log.i(TAG, "onClick: provider array " + allProvidersString);
                        fragment.onUserClickPerform(conversation);
                    } else {
                        fragment.onGroupClickedPerform(conversation);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
        ImageView iv_profile;
        ImageView activeUser;
        ConstraintLayout cl_main;
        TextView txtView, tv_counter, messageTime;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.friend_list_itemname);
            iv_profile = view.findViewById(R.id.friend_list_itemphoto);
            activeUser = view.findViewById(R.id.friend_list_item_statusicon);
            messageTime = view.findViewById(R.id.messageTime);
            cl_main = view.findViewById(R.id.cl_main);
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