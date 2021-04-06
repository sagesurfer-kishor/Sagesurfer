package com.sagesurfer.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.modules.cometchat_7_30.CometChatFriendsListFragment_;
import com.sagesurfer.collaborativecares.R;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import constant.StringContract;
import screen.messagelist.CometChatMessageListActivity;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder> {
    private final Context mContext;
    private final List<User> friendList;

    private static final String TAG = CometChatFriendsListFragment_.class.getSimpleName();

    public FriendsListAdapter(Context mContext, List<User> friendList) {
        this.mContext = mContext;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends_converison, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = friendList.get(position);

        holder.title.setText(user.getName());
        Glide.with(mContext).load(user.getAvatar()).into(holder.imgBan);
        String status = user.getStatus();

        String UID = friendList.get(position).getUid();

        CometChat.getUnreadMessageCountForUser(UID, new CometChat.CallbackListener<HashMap<String, Integer>>() {
            @Override
            public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                // handle success
                Log.e("0", stringIntegerHashMap.toString());
            }

            @Override
            public void onError(CometChatException e) {
                // handle error
            }
        });

        if (status.equals("online")) {
            holder.activeUser.setImageResource(R.drawable.online_sta);
        } else {
            holder.activeUser.setImageResource(R.drawable.offline_sta);
        }

        holder.BlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(mContext);
                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to block this friend?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                List<String> uids = new ArrayList<>();
                                uids.add(friendList.get(position).getUid());

                                // comet chat call for blocked selected user
                                CometChat.blockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
                                    @Override
                                    public void onSuccess(HashMap<String, String> resultMap) {
                                        // Handle block users success.
                                        friendList.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(CometChatException e) {
                                        // Handle block users failure
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

        String youth = Preferences.get("youths");

        if (!youth.isEmpty()) {
            if (youth.contains(user.getUid())) {
                holder.relativeLayout.setVisibility(View.GONE);
            }
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String providerArray = Preferences.get("providers");
                if (!youth.isEmpty()) {
                    if (providerArray.contains(user.getUid())) {

                        String status = user.getStatus();
                        if (status.equals("online")) {
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(mContext);
                            //Setting message manually and performing action on button click
                            builder.setMessage("Are you sure you want chat with provider?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(mContext, CometChatMessageListActivity.class);
                                            intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                                            intent.putExtra(StringContract.IntentStrings.NAME, (friendList.get(position).getName()));
                                            intent.putExtra(StringContract.IntentStrings.UID, (friendList.get(position).getUid()));
                                            intent.putExtra(StringContract.IntentStrings.AVATAR, (friendList.get(position).getAvatar()));
                                            intent.putExtra(StringContract.IntentStrings.STATUS, (friendList.get(position).getStatus()));
                                            intent.putExtra(StringContract.IntentStrings.TABS, "1");
                                            mContext.startActivity(intent);
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
                        } else {
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(mContext);
                            //Setting message manually and performing action on button click
                            builder.setMessage("Harsh pro provider staff is unavailable at present. He is available from 06:19 pm to 07:19 pm. Please call 911 for emergency issues.\n" +
                                    "\n" + "still you want to chat with this provider?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(mContext, CometChatMessageListActivity.class);
                                            intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                                            intent.putExtra(StringContract.IntentStrings.NAME, (friendList.get(position).getName()));
                                            intent.putExtra(StringContract.IntentStrings.UID, (friendList.get(position).getUid()));
                                            intent.putExtra(StringContract.IntentStrings.AVATAR, (friendList.get(position).getAvatar()));
                                            intent.putExtra(StringContract.IntentStrings.STATUS, (friendList.get(position).getStatus()));
                                            intent.putExtra(StringContract.IntentStrings.TABS, "1");
                                            mContext.startActivity(intent);
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

                    } else {
                        Intent intent = new Intent(mContext, CometChatMessageListActivity.class);
                        intent.putExtra(StringContract.IntentStrings.TYPE, "user");
                        intent.putExtra(StringContract.IntentStrings.NAME, (friendList.get(position).getName()));
                        intent.putExtra(StringContract.IntentStrings.UID, (friendList.get(position).getUid()));
                        intent.putExtra(StringContract.IntentStrings.AVATAR, (friendList.get(position).getAvatar()));
                        intent.putExtra(StringContract.IntentStrings.STATUS, (friendList.get(position).getStatus()));
                        intent.putExtra(StringContract.IntentStrings.TABS, "1");
                        mContext.startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        final TextView title;
        ImageView imgBan;
        ImageView activeUser;
        ImageView BlockUser;
        RelativeLayout relativeLayout;

        TextView txtView;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.friend_list_itemname);
            imgBan = view.findViewById(R.id.friend_list_itemphoto);
            activeUser = view.findViewById(R.id.friend_list_item_statusicon);
            BlockUser = view.findViewById(R.id.friend_block);
            relativeLayout = view.findViewById(R.id.friend_list_item_layout);
            txtView = view.findViewById(R.id.txt_errorMsg);

        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }


    }


}

