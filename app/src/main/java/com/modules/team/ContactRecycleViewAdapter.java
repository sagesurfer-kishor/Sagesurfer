package com.modules.team;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.CometChatTeamMembers_;
import com.sagesurfer.views.CircleTransform;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 6/14/2018.
 */

public class ContactRecycleViewAdapter extends RecyclerView.Adapter<ContactRecycleViewAdapter.MyViewHolder> {

    private final Context mContext;
    private ArrayList<CometChatTeamMembers_> contactList = new ArrayList<>();
    private final ContactRecycleViewAdapterListener listener;

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