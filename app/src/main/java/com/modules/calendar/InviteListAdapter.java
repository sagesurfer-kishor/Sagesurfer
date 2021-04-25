package com.modules.calendar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.datetime.InviteTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.parser.Invitations_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformCalendarTask;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 16-08-2017
 * Last Modified on 13-12-2017
 **/

class InviteListAdapter extends RecyclerView.Adapter<InviteListAdapter.MyViewHolder> {

    private static final String TAG = InviteListAdapter.class.getSimpleName();
    private final List<Invitations_> eventList;

    private final Activity activity;

    InviteListAdapter(Activity activity, List<Invitations_> eventList) {
        this.eventList = eventList;
        this.activity = activity;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView titleText;
        final TextView dateText;
        final TextView dateTag;
        final TextView descriptionText;
        final TextView acceptButton;
        final TextView rejectButton;
        final ImageView userImage;

        MyViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.invite_list_item_name);
            titleText = (TextView) view.findViewById(R.id.invite_list_item_title);
            dateText = (TextView) view.findViewById(R.id.invite_list_item_date);
            dateTag = (TextView) view.findViewById(R.id.invite_list_item_date_tag);
            descriptionText = (TextView) view.findViewById(R.id.invite_list_item_description);

            acceptButton = (TextView) view.findViewById(R.id.invite_list_item_accept);
            rejectButton = (TextView) view.findViewById(R.id.invite_list_item_reject);

            userImage = (ImageView) view.findViewById(R.id.invite_list_item_image);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_list_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        if (eventList.get(position).getStatus() == 1) {
            viewHolder.titleText.setText(ChangeCase.toTitleCase(eventList.get(position).getName()));
            viewHolder.nameText.setText(ChangeCase.toTitleCase(eventList.get(position).getUsername()));
            viewHolder.descriptionText.setText(eventList.get(position).getDescription());
            String[] remain = InviteTime.remainingTime(eventList.get(position).getTimestamp());
            viewHolder.dateText.setText(remain[0]);
            viewHolder.dateTag.setText(remain[1]);

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(eventList.get(position).getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(eventList.get(position).getImage()))
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.userImage);
            viewHolder.acceptButton.setTag(position);
            viewHolder.acceptButton.setOnClickListener(onClick);
            viewHolder.rejectButton.setTag(position);
            viewHolder.rejectButton.setOnClickListener(onClick);
        }
    }

    // on click for list item button for accept/decline
    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.invite_list_item_accept:
                    acceptInvite(position, v);
                    break;
                case R.id.invite_list_item_reject:
                    declineInvite(position, v);
                    break;
            }
        }
    };

    //Make call to accept invitation
    private void acceptInvite(int position, View view) {
        Invitations_ invitations_ = eventList.get(position);
        int status = PerformCalendarTask.inviteAction(Actions_.CAL_ACCEPT, "" + invitations_.getId(), TAG, activity.getApplicationContext(), activity);
        if (status == 1) {
            eventList.remove(position);
            notifyDataSetChanged();
        }
        showResponses(status, view);
    }

    //Make call to decline invitation
    private void declineInvite(int position, View view) {
        Invitations_ invitations_ = eventList.get(position);
        int status = PerformCalendarTask.inviteAction(Actions_.CAL_DECLINE, "" + invitations_.getId(), TAG, activity.getApplicationContext(), activity);
        if (status == 1) {
            eventList.remove(position);
            notifyDataSetChanged();
        }
        showResponses(status, view);
    }

    private void showResponses(int status, View view) {
        String message = "";
        if (status == 1) {
            message = activity.getApplicationContext().getResources().getString(R.string.successful);
        } else if (status == 2) {
            message = activity.getApplicationContext().getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, activity.getApplicationContext());
    }

    @Override
    public long getItemId(int position) {
        return eventList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

}
