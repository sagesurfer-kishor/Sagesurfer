package com.modules.supervisor;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 29-07-2017
 * Last Modified on 14-12-2017
 */

class ConversationListAdapter extends ArrayAdapter<Message_> {

    private final List<Message_> messageList;

    private final Activity activity;

    ConversationListAdapter(Activity activity, List<Message_> messageList) {
        super(activity, 0, messageList);
        this.messageList = messageList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Message_ getItem(int position) {
        if (messageList != null && messageList.size() > 0) {
            return messageList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return messageList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.supervisor_message_list_item, parent, false);

            viewHolder.messageText = (TextView) view.findViewById(R.id.supervisor_message_list_item_message);
            viewHolder.dateText = (TextView) view.findViewById(R.id.supervisor_message_list_item_time);
            viewHolder.profile = (ImageView) view.findViewById(R.id.supervisor_message_list_item_image);

            viewHolder.myMessageText = (TextView) view.findViewById(R.id.supervisor_message_list_item_my_message);
            viewHolder.myTimeText = (TextView) view.findViewById(R.id.supervisor_message_list_item_my_time);

            viewHolder.fromLayout = (LinearLayout) view.findViewById(R.id.supervisor_message_list_item_from_layout);
            viewHolder.myLayout = (LinearLayout) view.findViewById(R.id.supervisor_message_list_item_my_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (messageList.get(position).getStatus() == 1) {
            if (isMe(messageList.get(position).getUserId())) {
                viewHolder.myLayout.setVisibility(View.VISIBLE);
                viewHolder.fromLayout.setVisibility(View.GONE);

                viewHolder.myTimeText.setText(GetTime.wallTime(messageList.get(position).getDate()));
                viewHolder.myMessageText.setText(messageList.get(position).getDescription());

            } else {
                viewHolder.myLayout.setVisibility(View.GONE);
                viewHolder.fromLayout.setVisibility(View.VISIBLE);
                viewHolder.messageText.setText(messageList.get(position).getDescription());
                viewHolder.dateText.setText(GetTime.wallTime(messageList.get(position).getDate()));

                //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                Glide.with(activity.getApplicationContext()).load(messageList.get(position).getImage())
                        .thumbnail(0.5f)
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                .placeholder(GetThumbnails.userIcon(messageList.get(position).getImage()))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .transform(new CircleTransform(activity.getApplicationContext())))
                        .into(viewHolder.profile);
            }
        }
        return view;
    }

    // check user id with own user id to decide message box style
    private boolean isMe(int user_id) {
        return user_id == (Integer.parseInt(Preferences.get(General.USER_ID)));
    }

    private class ViewHolder {
        TextView messageText, dateText, myMessageText, myTimeText;
        ImageView profile;
        LinearLayout fromLayout, myLayout;
    }
}
