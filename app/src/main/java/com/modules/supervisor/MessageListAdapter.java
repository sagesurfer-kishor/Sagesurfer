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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 28-07-2017
 * Last Modified on 14-12-2017
 */

class MessageListAdapter extends ArrayAdapter<Message_> {
    private final List<Message_> messageList;
    private final Activity activity;

    MessageListAdapter(Activity activity, List<Message_> messageList) {
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
            view = layoutInflater.inflate(R.layout.message_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.message_list_item_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.message_list_item_date);
            viewHolder.descriptionText = (TextView) view.findViewById(R.id.message_list_item_description);

            viewHolder.profile = (ImageView) view.findViewById(R.id.message_list_item_image);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (messageList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(messageList.get(position).getName())
                    .replaceAll("\\s+", " "));
            viewHolder.descriptionText.setText(messageList.get(position).getDescription());
            viewHolder.dateText.setText(GetTime.wallTime(messageList.get(position).getDate()));

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(messageList.get(position).getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(messageList.get(position).getPhoto()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.profile);
        }
        return view;
    }

    private class ViewHolder {
        TextView nameText, dateText, descriptionText;
        ImageView profile;
    }
}
