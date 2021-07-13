package com.modules.team;

import android.content.Context;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.CometChatTeamMembers_;
import com.sagesurfer.views.CircleTransform;
import com.utils.AppLog;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 6/14/2018.
 */

public class ContactRecycleViewAdapter extends RecyclerView.Adapter<ContactRecycleViewAdapter.MyViewHolder> {

    private final Context mContext;
    private ArrayList<CometChatTeamMembers_> contactList = new ArrayList<>();
    private final ContactRecycleViewAdapterListener listener;
    private static final String TAG = "ContactRecycleViewAdapt";
    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewUsername;
        final TextView textViewRole;
        final ImageView profileImage;
        final ImageView statusImage;

        MyViewHolder(View view) {
            super(view);
            textViewUsername = (TextView) view.findViewById(R.id.textview_username);
            textViewRole = (TextView) view.findViewById(R.id.textview_role);
            profileImage = (ImageView) view.findViewById(R.id.member_list_item_photo);
            statusImage = (ImageView) view.findViewById(R.id.member_list_item_status_icon);
        }
    }

    ContactRecycleViewAdapter(Context mContext, ArrayList<CometChatTeamMembers_> contactList, ContactRecycleViewAdapterListener listener) {
        this.mContext = mContext;
        this.contactList = contactList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_recycler_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (contactList.get(position).getStatus() == 1) {
            CometChatTeamMembers_ contact = contactList.get(position);
            Glide.with(mContext)
                    .load(contact.getA())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(contact.getA()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(mContext)))
                    .into(holder.profileImage);

            holder.textViewUsername.setText(contact.getName());
            SharedPreferences UserInfoForUIKitPref = mContext.getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
            String DomainCode = UserInfoForUIKitPref.getString(screen.messagelist.General.DOMAIN_CODE, null);
            String UID = ""+contact.getU_member_id()+"_"+DomainCode;
            AppLog.i(TAG, "User details fetched for user id: " + UID);
            CometChat.getUser(UID, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {

                    if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE)) {

                        holder.statusImage.setImageResource(R.drawable.online_sta);
                        //holder.activeUser.setImageResource(R.drawable.online_sta);
                    } else {
                        holder.statusImage.setImageResource(R.drawable.offline_sta);
                    }
                }

                @Override
                public void onError(CometChatException e)
                {
                    AppLog.e(TAG, ""+e.getMessage());
                }
            });
            //holder.statusImage.setImageResource(contact.get);
            if(contact.getRole() != null) {
                holder.textViewRole.setText("(" + contact.getRole() + ")");
            } /*else {
                if (contact.getUserId() == Integer.parseInt(Preferences.get(General.USER_ID))) {
                    holder.textViewRole.setText("(" + Preferences.get(General.ROLE) + ")");
                }
            }*/
            applyClickEvents(holder, position);
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(position, false);
            }
        });
    }

    interface ContactRecycleViewAdapterListener {
        void onItemClicked(int position, boolean isFromCircularView);
    }
}