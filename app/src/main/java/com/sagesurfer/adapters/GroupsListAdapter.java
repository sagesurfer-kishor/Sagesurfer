package com.sagesurfer.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.core.BannedGroupMembersRequest;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.firebase.MessagingService;
import com.modules.cometchat_7_30.FragmentCometchatGroupsList;
import com.modules.cometchat_7_30.ModelUserCount;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.GetAddNewMember;
import com.sagesurfer.models.GetGroupsCometchat;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.secure.GroupTeam_;
import com.storage.preferences.Preferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import constant.StringContract;
import okhttp3.RequestBody;
import screen.messagelist.CometChatMessageListActivity;

/**
 * @author kishor k
 * Created on 13/11/2020
 */

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.MyViewHolder> implements Filterable {
    private final Context mContext;
    private static final String TAG = FragmentCometchatGroupsList.class.getSimpleName();
    private String GID;
    private RecyclerView recyclerView;
    private TextView error;
    private ArrayList<GetAddNewMember> getgroupmemberArrayList = new ArrayList<>();
    private final ArrayList<GetGroupsCometchat> searchGroupList;
    private FragmentCometchatGroupsList fragment;
    private EditText et_search_user;
    private MembersListAdapter memberListAdapter;
    private TextView btnSearch;
    private List<ModelUserCount> al_unreadCountList;
    SharedPreferences preferenOpenActivity;

    public GroupsListAdapter(FragmentCometchatGroupsList fragment_CometchatGroupsList_, Context mContext, ArrayList<GetGroupsCometchat> searchGroupList) {
        this.mContext = mContext;//, ArrayList<ModelUserCount> al_unreadCountList
        this.searchGroupList = searchGroupList;
        //this.al_unreadCountList = al_unreadCountList;
        fragment = fragment_CometchatGroupsList_;
        preferenOpenActivity = mContext.getSharedPreferences("highlighted_group", Context.MODE_PRIVATE);
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        final TextView title;
        ImageView img_group_item_options;
        ImageView img;
        ImageView btnselfinvite;
        TextView group_ic_counter;
        LinearLayout linearLayout;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.list_item_name);
            group_ic_counter = (TextView) view.findViewById(R.id.group_ic_counter);
            img = (ImageView) view.findViewById(R.id.friend_list_item_photo);
            img_group_item_options = view.findViewById(R.id.img_group_item_options);
            btnselfinvite = view.findViewById(R.id.selfInvite);
            linearLayout = view.findViewById(R.id.groupMemberClick);
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groups, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        GetGroupsCometchat group_item = searchGroupList.get(position);
        final List<String> l = new ArrayList<>();
        //final String providerArray = Preferences.get("providers");
        // set group name
        holder.title.setText(group_item.getName());
        if (preferenOpenActivity.contains("receiver")) {
            String highliteGroupId = preferenOpenActivity.getString("receiver", null);
            if (highliteGroupId.equals(group_item.getGroupId())) {
                Log.i(TAG, "onBindViewHolder: bothGroupsAreEqual");
                if (preferenOpenActivity.getBoolean("makeHighlight", false)) {
                    holder.linearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_border_highlighted));
                }
            }
        }
        //Log.e(TAG, "onBindViewHolder: al_unreadCountList size "+al_unreadCountList.size()+" searchGroupList size "+searchGroupList.size());
        /*if (!al_unreadCountList.isEmpty())
        {
            for (ModelUserCount userCount : al_unreadCountList) {
                Log.e(TAG, "onBindViewHolder: userCountGID "+userCount.getUID()+"  searchlistGID "+searchGroupList.get(position).getGroupId() );
                if (userCount.getUID().equalsIgnoreCase(searchGroupList.get(position).getGroupId())) {
                    Log.e(TAG, "onBindViewHolder: match uids" );
                    holder.group_ic_counter.setVisibility(View.VISIBLE);
                    holder.group_ic_counter.setText("" +userCount.getCount());
                }else{
                    Log.e(TAG, "onBindViewHolder: unmatched uids "+userCount.getUID() +" and "+searchGroupList.get(position).getGroupId() );
                    holder.group_ic_counter.setVisibility(View.INVISIBLE);
                }
            }
        }else{
            Log.e(TAG, "onBindViewHolder: empty unread count list found" );
            //holder.group_ic_counter.setVisibility(View.INVISIBLE);
        }*/


        CometChat.getUnreadMessageCountForGroup(group_item.getGroupId(), new CometChat.CallbackListener<HashMap<String, Integer>>() {
            @Override
            public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                Log.e(TAG, "onSuccess: cometchat groupId " + stringIntegerHashMap.get("670270470"));    //getting null values for group

                if (stringIntegerHashMap.get(group_item.getGroupId()) != null) {

                    group_item.setStatus(stringIntegerHashMap.get(group_item.getGroupId()));
                    holder.group_ic_counter.setText("" +group_item.getStatus());
                    holder.group_ic_counter.setVisibility(View.VISIBLE);
                } else {
                    holder.group_ic_counter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.i(TAG, "onError get groups " + e.getMessage());
                holder.group_ic_counter.setVisibility(View.GONE);
            }
        });

        // set group icon for pubilc group
        if (group_item.getType().equals("public")) {
            if (!group_item.getOwner_id().equals(Preferences.get(General.USER_ID))) {
                holder.btnselfinvite.setVisibility(View.VISIBLE);
            }
            holder.img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pub_group));
        } else if (group_item.getType().equals("private")) {
            holder.img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pri_group));
            holder.btnselfinvite.setVisibility(View.INVISIBLE);
        } else {
            holder.img.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pass_group));
            holder.btnselfinvite.setVisibility(View.INVISIBLE);
        }

        if (group_item.getType().equals("private") && group_item.getType().equals("public")) {
            holder.btnselfinvite.setVisibility(View.INVISIBLE);
        }

        String isMember = String.valueOf(group_item.getIs_member());

        if (group_item.getOwner_id().equals(Preferences.get(General.USER_ID))) {
            holder.btnselfinvite.setVisibility(View.INVISIBLE);
        }

        if (isMember.equals("1")) {
            holder.btnselfinvite.setVisibility(View.INVISIBLE);
        }

        // add member
        holder.img_group_item_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent intent = new Intent(mContext, Resourse_Share.class);
                mContext.startActivity(intent);*/

               /* Resourse_Share CallUs = new Resourse_Share();
                CallUs.show((AppCompatActivity)mContext.getFragmentManager(), "SOME_TAG");*/
                //Log.e("GroupListAdapter", "onClick: img_group_item_options isMember" );

                /*if (isMember.equals("0")) {*/

                final String userId = Preferences.get(General.USER_ID);

                if (group_item.getIs_member() != 0 || userId.equals(group_item.getOwner_id())) {
                    GID = group_item.getGroupId();
                    String owner = group_item.getOwner_id();
                    String groupType = group_item.getType();
                    Log.e("GroupListAdapter", "onClick: img_group_item_options isMember if true");
                    Preferences.save("gId", GID);
                    Preferences.save("owner", owner);

                    final Dialog dialog1 = new Dialog(mContext);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog1.setCancelable(false);
                    dialog1.setContentView(R.layout.group_detail);

                    Button btnViewMember = dialog1.findViewById(R.id.btnViewMember);
                    Button btnDeleteGroup = dialog1.findViewById(R.id.btnDeleteGroup);

                    TextView txtmemberCount = dialog1.findViewById(R.id.txtmemberCount);
                    TextView txtcreatedDated = dialog1.findViewById(R.id.txtcreatedDated);
                    TextView txtowner = dialog1.findViewById(R.id.txtowner);

                    txtowner.setText(group_item.getOwner());
                    txtmemberCount.setText(group_item.getMembers_count());

              /*  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(Long.parseLong(teamList.get(position).getCreated())));  */

                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(group_item.getCreated()) * 1000);
                    String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                    txtcreatedDated.setText(date);

                    ImageView btnClose = dialog1.findViewById(R.id.imgDismiss);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                        }
                    });

                    btnViewMember.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogViewMembers(groupType);
                            dialog1.dismiss();
                        }
                    });

                    if (group_item.getOwner_id().equals(Preferences.get(General.USER_ID))) {
                        btnDeleteGroup.setVisibility(View.VISIBLE);
                    } else {
                        btnDeleteGroup.setVisibility(View.GONE);
                    }

                    //delete group call
                    btnDeleteGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GID = group_item.getGroupId();
                            final String GUID = String.valueOf(GID);
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(mContext);
                            //Setting message manually and performing action on button click
                            builder.setMessage("Do you want to delete this group?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            // delete group from comet chat first
                                            CometChat.deleteGroup(GUID, new CometChat.CallbackListener<String>() {
                                                @Override
                                                public void onSuccess(String successMessage) {
                                                    Log.d(TAG, "Group deleted successfully: ");

                                                    MessagingService.unsubscribeGroupNotification(GUID);

                                                    // update deleted group in our db
                                                    DeleteGroup("delete_group", GUID, position);
                                                    // teamList.remove(position);
                                                    dialog1.dismiss();
                                                }

                                                @Override
                                                public void onError(CometChatException e) {
                                                    Log.d(TAG, "Group delete failed with exception: " + e.getMessage());
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //  Action for 'NO' Button
                                            dialog1.cancel();
                                        }
                                    });
                            //Creating dialog box
                            AlertDialog alert = builder.create();
                            //Setting the title manually
                            alert.setTitle("Alert Notification");
                            alert.show();

                        }
                    });

                    dialog1.show();
                /*}else{
                    Log.e("GroupListAdapter", "onClick: img_group_item_options isMember else Part" );
                    Log.e("GroupListAdapter", "onClick: group type = "+teams_.getType());
                    //addMemberingroup(teams_.getType());
                }*/

                } else {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(mContext);
                    //Setting message manually and performing action on button click

                    builder.setMessage("You are not member of this group. please join this group.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    // delete group from comet chat first

                                }

                            });
                    //Creating dialog box
                    //Creating dialog box

                    //Setting the title manually
                    AlertDialog alert = builder.create();
                    alert.setTitle("Alert");
                    alert.show();

                }


            }

        });


        // self invite call
        holder.btnselfinvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GID = group_item.getGroupId();
                Log.e(TAG, "selfInvite started");
                final String action = "self_invite_public";
                final String userId = Preferences.get(General.USER_ID);
                final String groupId = String.valueOf(GID);

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(mContext);

                //Setting message manually and performing action on button click
                //builder.setTitle("Self Invitation");
                builder.setMessage("Do you want to self invite to this group?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.e(TAG, "selfInvite yes button clicked..");
                                Log.e(TAG, "selfInvite groupId" + groupId + " type" + group_item.getType());
                                String GUID = groupId;
                                String groupType = group_item.getType();
                                String password = "";

                                // join group by self
                                CometChat.joinGroup(GUID, groupType, password, new CometChat.CallbackListener<Group>() {
                                    @Override
                                    public void onSuccess(Group joinedGroup) {
                                        Log.e(TAG, "cometChatJoinGroupSuccess " + joinedGroup.toString());
                                        // update self joined group in our db
                                        GetGroupsCometchat item = group_item;
                                        item.setIs_member(1);
                                        searchGroupList.set(position, item);
                                        notifyItemChanged(position);
                                        selfinvite(action, userId, joinedGroup.getGuid(), position);
                                    }

                                    @Override
                                    public void onError(CometChatException e) {
                                        Log.d(TAG, "Group joining failed with exception: " + e.getMessage());
                                    }
                                });

                                dialog.dismiss();
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
                alert.setTitle("Self Invitation");
                alert.show();
            }
        });

        /*holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GID = teams_.getGroupId();
                for (Members_ teamsItem : teams_.getMembersArrayList()) {
                    String s = String.valueOf(teamsItem.getUser_id());
                    l.add(s);
                }
                Log.i(TAG, "onClick: GroupName"+teams_.getName()+" IsMember"+teams_.getIs_member());
                final String gName = teams_.getName();
                final String groupIds = String.valueOf(teams_.getGroupId());
                final String groupType = searchGroupList.get(position).getType();
                final String ownerId = teams_.getOwner_id();
                String Uid = Preferences.get(General.USER_ID);
                final String memberCount = teams_.getMembers_count();
                int isMembers = teams_.getIs_member();

                switch (groupType) {
                    case "public":
                        if (isMembers == 0) {
                            // compare  he is owner of that group or not
                            if (ownerId.equals(Uid)) {
                                Log.i(TAG, "onClick: public 1");
                                openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                            } else {
                                //if not then show this error
                                Log.i(TAG, "onClick: public 2");
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(mContext);
                                builder.setMessage("You are not member of this group. join this group")
                                        .setCancelable(false)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.setTitle("Alert Notification");
                                alert.show();
                            }

                        } else {
                            Log.i(TAG, "onClick: public 3");
                            openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                        }

                        break;

                    case "private":
                        if (ownerId.equals(Uid)) {
                            openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                            Log.i(TAG, "onClick: private 1");
                        } else if (isMembers == 1) {
                            Log.i(TAG, "onClick: private 2");
                            openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                        } else {
                            //if not then show this error
                            Log.i(TAG, "onClick: private 3");
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(mContext);
                            builder.setMessage("You are not member of this group. join this group")
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.setTitle("Alert Notification");
                            alert.show();
                        }

                        break;
                    case "password":

                        if (ownerId.equals(Uid)) {
                            Log.i(TAG, "onClick: password 1");
                            openActivity(gName, groupIds, groupType, ownerId, memberCount, "");
                        } else {
                            Log.i(TAG, "onClick: password 1");
                            final Dialog dialog = new Dialog(mContext);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.dialog_password);

                            final EditText editText = dialog.findViewById(R.id.groupPassword);
                            ImageView imageView = dialog.findViewById(R.id.btnPasswordSubmit);
                            ImageView imageView1 = dialog.findViewById(R.id.btncancelPasswordSubmit);

                            imageView1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String pass = editText.getText().toString().trim();
                                    if (!TextUtils.isEmpty(pass)) {

                                        if (pass.equals(teams_.getPassword())) {
                                            openActivity(gName, groupIds, groupType, ownerId, memberCount, pass);
                                            dialog.dismiss();

                                        } else {
                                            editText.setError("Enter correct password");
                                        }

                                    } else {
                                        editText.setError("Enter password");
                                    }
                                }
                            });
                            dialog.show();

                        }
                        break;

                }
            }
        });*/

        //holder.linearLayout.setOnClickListener(view -> fragment.performAdapterClick(position));
        holder.linearLayout.setOnClickListener(view -> fragment.performAdapterClick(position, searchGroupList));

    }

    //open group chat screen
    private void openActivity(String name, String groupId, String ownerId, String GroupType, String memberCount, String GroupPass) {
        Intent intent = new Intent(mContext, CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.TYPE, "group");
        intent.putExtra(StringContract.IntentStrings.NAME, name);
        intent.putExtra(StringContract.IntentStrings.GUID, groupId);
        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, ownerId);
        intent.putExtra(StringContract.IntentStrings.AVATAR, "https://designstaging.sagesurfer.com/static//avatar/thumb/man.jpg");
        intent.putExtra(StringContract.IntentStrings.GROUP_TYPE, GroupType);
        intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT, memberCount);
        intent.putExtra(StringContract.IntentStrings.GROUP_DESC, "");
        intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD, GroupPass);
        intent.putExtra(StringContract.IntentStrings.TABS, "2");
        mContext.startActivity(intent);
    }

    private void dialogViewMembers(String groupType) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.member_details);

        final TextView tv_allMember = dialog.findViewById(R.id.allMemberList);
        final TextView tv_addMembers = dialog.findViewById(R.id.addNewMemberList);
        final TextView tv_blockedMember = dialog.findViewById(R.id.blockedMemberList);

        recyclerView = dialog.findViewById(R.id.detailMemberList);
        final LinearLayout linearLayout = dialog.findViewById(R.id.lv_search);
        error = dialog.findViewById(R.id.error);
        et_search_user = dialog.findViewById(R.id.search_name);

        tv_allMember.setBackgroundResource(R.color.colorPrimary);
        tv_addMembers.setBackgroundResource(R.color.white);
        tv_blockedMember.setBackgroundResource(R.color.white);
        linearLayout.setVisibility(View.GONE);

        String groupId = String.valueOf(GID);
        Preferences.save("gId", groupId);
        // get all group member
        getTeamsMember(groupId);

        tv_allMember.setBackgroundResource(R.color.colorPrimary);
        tv_addMembers.setBackgroundResource(R.color.white);
        tv_blockedMember.setBackgroundResource(R.color.white);

        ImageView btn_CloseDialog = dialog.findViewById(R.id.btnMemberDismiss);
        btnSearch = dialog.findViewById(R.id.btnsearch);
        btnSearch.setVisibility(View.GONE);

        btn_CloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_allMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String groupId = String.valueOf(GID);
                Preferences.save("gId", groupId);

                //  get all team member for selected group
                getTeamsMember(groupId);

                tv_allMember.setBackgroundResource(R.color.colorPrimary);
                tv_addMembers.setBackgroundResource(R.color.white);
                tv_blockedMember.setBackgroundResource(R.color.white);

                linearLayout.setVisibility(View.GONE);
            }
        });

        String userId = Preferences.get(General.USER_ID);
        groupId = String.valueOf(GID);

        if (!Preferences.get("owner").equals(userId)) {
            tv_addMembers.setVisibility(View.GONE);
            tv_blockedMember.setVisibility(View.GONE);
        } else {
            // add member
            tv_addMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    tv_allMember.setBackgroundResource(R.color.white);
                    tv_addMembers.setBackgroundResource(R.color.colorPrimary);
                    tv_blockedMember.setBackgroundResource(R.color.white);
                    linearLayout.setVisibility(View.VISIBLE);

                    String action = "get_add_new_members";
                    String userId = Preferences.get(General.USER_ID);
                    String groupId = String.valueOf(GID);
                    // get all user list from db
                    getaddMembersTeams(action, userId, groupId, groupType);

                }
            });
        }

        tv_blockedMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.GONE);

                tv_blockedMember.setBackgroundResource(R.color.colorPrimary);
                tv_allMember.setBackgroundResource(R.color.white);
                tv_addMembers.setBackgroundResource(R.color.white);

                // block member list for group
                getBlockedUserList();
            }
        });

        // friend conversion search action
        et_search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchMember(s.toString());

            }
        });

        dialog.show();
    }

    private void getTeamsMember(String groupId) {
        GroupMembersRequest groupMembersRequest = null;
        String GUID = groupId;
        int limit = 30;

        groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(GUID)
                .setLimit(limit).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                Log.e(TAG, "Group Member list fetched successfully : " + list.toString());

                if (list.size() > 0) {
                    GroupMembersAdapter caseloadListAdapter = new GroupMembersAdapter(mContext, list);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(caseloadListAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    error.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Group Member list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    /*fetching all the members to add in the group where we have add member button option on list.
     * (Second tab of dialog view members)*/
    private void getaddMembersTeams(String action, String userId, String groupId, String groupType) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, userId);
        requestMap.put(General.GROUP_ID, groupId);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext);
                if (response != null) {
                    getgroupmemberArrayList = GroupTeam_.parsegroupMember(response, "get_add_new_members", mContext, TAG);
                    memberListAdapter = new MembersListAdapter(mContext, getgroupmemberArrayList, groupType);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(memberListAdapter);

                    recyclerView.setVisibility(View.VISIBLE);
                    error.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.VISIBLE);

                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getBlockedUserList() {
        BannedGroupMembersRequest bannedGroupMembersRequest;
        String GUID = String.valueOf(GID);
        int limit = 50;
        bannedGroupMembersRequest = new BannedGroupMembersRequest.BannedGroupMembersRequestBuilder(GUID).setLimit(limit).build();
        bannedGroupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                if (list.size() > 0) {
                    Banmemberlistadapter banmemberadapter = new Banmemberlistadapter(mContext, list);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(banmemberadapter);

                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Banned Group Member list fetching failed with exception: " + e.getMessage());
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchGroupList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void DeleteGroup(String action, String gId, int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.GROUP_ID, gId);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext);
                Log.e("deleteGroup", response);
                if (response != null) {
                    Toast.makeText(mContext, "Delete group Successfully", Toast.LENGTH_LONG).show();
                    searchGroupList.remove(position);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void selfinvite(String action, String userId, String gId, int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, userId);
        requestMap.put(General.GROUP_ID, gId);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, mContext);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, mContext);
                if (response != null) {
                    Toast.makeText(mContext, "you got self invited in this group", Toast.LENGTH_LONG).show();
                    notifyAll();
                    //MyFirebaseMessagingService.subscribeGroupNotification(userId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void searchMember(String search) {
        memberListAdapter.getFilter().filter(search);
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<GetGroupsCometchat> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fragment.primaryGroupList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (GetGroupsCometchat item : fragment.primaryGroupList) {
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
            searchGroupList.clear();
            searchGroupList.addAll((List) results.values);
            if (searchGroupList.isEmpty()) {
                Toast.makeText(mContext, "No Result Found", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        }
    };

    public void changeUnreadCount(String sender) {
        CometChat.getUnreadMessageCountForGroup(sender, new CometChat.CallbackListener<HashMap<String, Integer>>() {
            @Override
            public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                Log.e(TAG, "onSuccess: cometchat groupId " + stringIntegerHashMap.get("670270470"));    //getting null values for group
                for (GetGroupsCometchat group : searchGroupList ){
                    if(group.getGroupId().equals(""+sender)){
                        Log.i(TAG, "onSuccess: matched user");
                        group.setStatus(stringIntegerHashMap.get(sender));
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onError(CometChatException e) {
                Log.i(TAG, "onError get groups " + e.getMessage());

            }
        });
    }
}

