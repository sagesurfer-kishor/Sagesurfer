package com.modules.friend_invitation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.views.CircleTransform;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 1/6/2020.
 */
public class FriendInvitationListAdapter extends RecyclerView.Adapter<FriendInvitationListAdapter.MyViewHolder> {
    private final ArrayList<Choices_> friendInvitationList;
    private Activity activity;

    public FriendInvitationListAdapter(Activity activity, ArrayList<Choices_> studAssignDataArrayList1) {
        this.friendInvitationList = studAssignDataArrayList1;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return friendInvitationList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return friendInvitationList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_invitation_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final Choices_ choices = friendInvitationList.get(position);

        Glide.with(activity)
                .load(friendInvitationList.get(position).getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(friendInvitationList.get(position).getImage()))
                        .transform(new CircleTransform(activity)))
                .into(viewHolder.profileImage);

        viewHolder.userName.setText(choices.getName());
        viewHolder.date.setText(getDate(choices.getLast_updated()));
        //viewHolder.status.setText(choices.getLastname());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView userName, date, status;
        ImageView profileImage;

        MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.user_name);
            date = (TextView) view.findViewById(R.id.date_txt);
            status = (TextView) view.findViewById(R.id.status_txt);
            profileImage = (ImageView) view.findViewById(R.id.imageview_profile);
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }
}
