package com.sagesurfer.adapters;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.Avatar;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.modules.cometchat_7_30.ModelUserCount;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.CometChatTeamMembers_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.tasks.PerformGetUsersTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MyTeamsChatExpandableListAdapter extends BaseExpandableListAdapter implements Filterable {

    private static final String TAG = MyTeamsChatExpandableListAdapter.class.getSimpleName();
    Handler handler;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Teams_> primaryList = new ArrayList<>();
    private ArrayList<Teams_> searchList = new ArrayList<>();
    public final TeamsChatExpandableListAdapterListener teamsChatExpandableListAdapterListener;
    public final Activity activity;
    private int lastExpandedPosition = -1;
    int number;

    public MyTeamsChatExpandableListAdapter(Context context, ArrayList<Teams_> primaryList,
                                            ArrayList<Teams_> searchList,
                                            TeamsChatExpandableListAdapterListener teamsChatExpandableListAdapterListener, Activity activity) {
        this.context = context;
        this.primaryList = primaryList;
        this.searchList = searchList;

        this.teamsChatExpandableListAdapterListener = teamsChatExpandableListAdapterListener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return searchList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return searchList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return searchList.get(groupPosition).getMembersArrayList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return searchList.get(groupPosition).getMembersArrayList().size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        final ViewHolderTeam viewHolderTeam;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.friend_list_item_layout, null);
            viewHolderTeam = new ViewHolderTeam();
            /*viewHolderTeam.teamRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.team_list_item_layout);
            viewHolderTeam.teamNameText = (TextView) convertView.findViewById(R.id.team_list_item_name);
            viewHolderTeam.image = (ImageView) convertView.findViewById(R.id.team_list_item_image);*/
            viewHolderTeam.linearLayoutFriendListItem = (LinearLayout) convertView.findViewById(R.id.linearlayout_friendlistitem);
            viewHolderTeam.teamRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.team_list_item_layout);
            viewHolderTeam.avatar = convertView.findViewById(R.id.friend_list_item_photo);
            viewHolderTeam.userName = (TextView) convertView.findViewById(R.id.friend_list_item_name);
            viewHolderTeam.userStatus = (TextView) convertView.findViewById(R.id.friend_list_item_status_message);
            viewHolderTeam.userStatus.setVisibility(View.GONE);
            viewHolderTeam.statusImage = (ImageView) convertView.findViewById(R.id.friend_list_item_status_icon);
            viewHolderTeam.statusImage.setVisibility(View.GONE);
            viewHolderTeam.unreadCount = (TextView) convertView.findViewById(R.id.ic_counter);
            viewHolderTeam.unreadCount.setVisibility(View.GONE);
            viewHolderTeam.timeText = (RelativeTimeTextView) convertView.findViewById(R.id.friend_list_item_last);
            viewHolderTeam.timeText.setVisibility(View.GONE);
            viewHolderTeam.typing = (TextView) convertView.findViewById(R.id.friend_list_item_typing);
            viewHolderTeam.memberCount = (TextView) convertView.findViewById(R.id.memberCount);

            convertView.setTag(viewHolderTeam);
        } else {
            viewHolderTeam = (ViewHolderTeam) convertView.getTag();
        }

        Teams_ item = searchList.get(groupPosition);
        Log.i(TAG, "getGroupView: "+item.getMembersArrayList());

        Glide.with(context)
                .load(item.getBanner())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_account)
                        .transform(new CircleTransform(context)))
                .into(viewHolderTeam.avatar);

        viewHolderTeam.userName.setText(item.getName());

        if (item.getMembersArrayList().size() > 0) {
            number = item.getMembers();
            viewHolderTeam.memberCount.setText(String.valueOf(number));
        } else {
            viewHolderTeam.memberCount.setText("0");
        }

        final ExpandableListView mExpandableListView = (ExpandableListView) viewGroup;
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    mExpandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
                Log.i(TAG, "onGroupExpand: team name"+item.getName());
                teamsChatExpandableListAdapterListener.onTeamClickFetchTeamData( item);
            }
        });

        /*viewHolderTeam.linearLayoutFriendListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.setBackgroundColor(context.getResources().getColor(R.color.list_divider));
                //int background = android.R.color.black;
                //view.setBackgroundResource(background);
                //mExpandableListView.setBackgroundColor(Color.parseColor("#9e9e9e"));
                if(mExpandableListView.isGroupExpanded(groupPosition)) {
                    viewHolderTeam.teamRelativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                    mExpandableListView.collapseGroup(groupPosition);
                } else {
                    viewHolderTeam.teamRelativeLayout.setBackgroundColor(Color.parseColor("#d6d6d6"));
                    mExpandableListView.expandGroup(groupPosition);
                }
            }
        });*/

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        Teams_ item = searchList.get(groupPosition);
        Log.e(TAG, "team id: " + item.getId() + " team name: " + item.getName());
        Preferences.save(General.BANNER_IMG, item.getBanner());
        Preferences.save(General.TEAM_ID, item.getId());
        Preferences.save(General.TEAM_NAME, item.getName());
        ArrayList<CometChatTeamMembers_> teamMemberList = PerformGetUsersTask.getCometChatTeamMembers(Actions_.COMETCHAT, context, TAG, activity);

        try {
            final ViewHolderMember viewHolderMember;
            convertView = inflater.inflate(R.layout.friend_list_item_layout, null);
            viewHolderMember = new ViewHolderMember();

            viewHolderMember.linearLayoutFriendListItem = (LinearLayout) convertView.findViewById(R.id.linearlayout_friendlistitem);
            viewHolderMember.memberRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.team_list_item_layout);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearParams.setMargins(30, 0, 0, 0);
            viewHolderMember.memberRelativeLayout.setLayoutParams(linearParams);
            viewHolderMember.memberRelativeLayout.requestLayout();
            viewHolderMember.avatar = convertView.findViewById(R.id.friend_list_item_photo);
            viewHolderMember.userName = (TextView) convertView.findViewById(R.id.friend_list_item_name);
            viewHolderMember.userStatus = (TextView) convertView.findViewById(R.id.friend_list_item_status_message);
            viewHolderMember.statusImage = (ImageView) convertView.findViewById(R.id.friend_list_item_status_icon);
            viewHolderMember.unreadCount = (TextView) convertView.findViewById(R.id.ic_counter);
            viewHolderMember.unreadCount.setVisibility(View.GONE);
            viewHolderMember.timeText = (RelativeTimeTextView) convertView.findViewById(R.id.friend_list_item_last);
            viewHolderMember.typing = (TextView) convertView.findViewById(R.id.friend_list_item_typing);
            //viewHolderMember.memberCount = (TextView) convertView.findViewById(R.id.memberCount);
            convertView.setTag(viewHolderMember);

            viewHolderMember.userName.setText(item.getMembersArrayList().get(childPosition).getUsername());
            Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    CometChat.getUnreadMessageCountForUser(""+item.getMembersArrayList().get(childPosition).getComet_chat_id(), new CometChat.CallbackListener<HashMap<String, Integer>>() {
                        @Override
                        public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                            handler=new Handler();
                             handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolderMember.unreadCount.setVisibility(View.VISIBLE);
                                    //Log.i(TAG, "onSuccess: unread messages   "+stringIntegerHashMap.get(item.getMembersArrayList().get(childPosition).getComet_chat_id()));
                                    String counter= String.valueOf(stringIntegerHashMap.get(item.getMembersArrayList().get(childPosition).getComet_chat_id()));
                                    if (!counter.equalsIgnoreCase("null")){
                                        viewHolderMember.unreadCount.setText(counter);
                                    }else{
                                        viewHolderMember.unreadCount.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(CometChatException e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolderMember.unreadCount.setVisibility(View.GONE);
                                }
                            });
                        }

                    });
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();


           //viewHolderMember.timeText.setText(timeStamp);

            viewHolderMember.userStatus.setText(teamMemberList.get(childPosition).getM());

           /* CometChat.getUnreadMessageCountForUser(item.getMembersArrayList().get(childPosition).getComet_chat_id(), new CometChat.CallbackListener<HashMap<String, Integer>>() {
                @Override
                public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                    // handle success
                    Log.e("unreadcount", String.valueOf(stringIntegerHashMap.get(item.getMembersArrayList().get(childPosition).getComet_chat_id())));

                   // viewHolderMember.unreadCount.setText(stringIntegerHashMap.get(item.getMembersArrayList().get(childPosition).getComet_chat_id()));
                }

                @Override
                public void onError(CometChatException e) {
                    // handle error
                }
            });*/

            Glide.with(context)
                    .load(item.getMembersArrayList().get(childPosition).getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_account)
                            .transform(new CircleTransform(context)))
                    .into(viewHolderMember.avatar);

            Log.i(TAG, "getChildView: status of user ---> "+teamMemberList.get(childPosition).getStatus());

            viewHolderMember.linearLayoutFriendListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    teamsChatExpandableListAdapterListener.onMemberRelativeLayoutClicked(childPosition, item);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public interface TeamsChatExpandableListAdapterListener {
        void onMemberRelativeLayoutClicked(int childPosition, Teams_ team_);

        void onTeamClickFetchTeamData(Teams_ item);

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class ViewHolderTeam {
        /*RelativeLayout teamRelativeLayout;
        TextView teamNameText;
        ImageView image;*/

        LinearLayout linearLayoutFriendListItem;
        RelativeLayout teamRelativeLayout;
        TextView userName;
        TextView userStatus;
        TextView unreadCount;
        Avatar avatar;
        ImageView avtar_image;
        ImageView statusImage;
        View view;
        RelativeTimeTextView timeText;
        TextView typing;
        TextView memberCount;

    }

    private class ViewHolderMember {
       /* RelativeLayout memberRelativeLayout;
        ImageView image;
        RelativeLayout relativeLayoutImage;
        RoundedImageView memberImage;
        ImageView statusImage;
        TextView memberNameText;
        TextView statusMessageText;*/

        LinearLayout linearLayoutFriendListItem;
        RelativeLayout memberRelativeLayout;
        TextView userName;
        TextView userStatus;
        TextView unreadCount;
        Avatar avatar;
        ImageView avtar_image;
        ImageView statusImage;
        View view;
        RelativeTimeTextView timeText;
        TextView typing;
        TextView memberCount;
    }

    @Override
    public Filter getFilter() {
        Log.e("Seach Functionality", TAG+" getFilter : ");
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Teams_> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(primaryList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Teams_ item : primaryList) {
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
            searchList.clear();
            searchList.addAll((List) results.values);
            if (searchList.isEmpty()) {
                Toast.makeText(context, "No Result Found", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        }
    };
}