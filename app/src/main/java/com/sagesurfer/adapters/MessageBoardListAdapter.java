package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.format.DateFormat;
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
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.MessageBoard_;
import com.sagesurfer.views.CircleTransform;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 2/26/2019.
 */

public class MessageBoardListAdapter extends ArrayAdapter<MessageBoard_> {

    private static final String TAG = MessageBoardListAdapter.class.getSimpleName();
    private final List<MessageBoard_> messageBoardList;

    public final OnItemClickListener onItemClickListener;
    private final Activity activity;
    private final Context mContext;

    public MessageBoardListAdapter(Activity activity, List<MessageBoard_> messageBoardList, OnItemClickListener onItemClickListener) {
        super(activity, 0, messageBoardList);
        this.messageBoardList = messageBoardList;
        this.onItemClickListener = onItemClickListener;
        this.activity = activity;
        mContext = this.activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return messageBoardList.size();
    }

    @Override
    public MessageBoard_ getItem(int position) {
        if (messageBoardList != null && messageBoardList.size() > 0) {
            return messageBoardList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return messageBoardList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.message_board_list_item, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.textview_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.textview_date);
            viewHolder.titleText = (TextView) view.findViewById(R.id.textview_title);
            viewHolder.profile = (ImageView) view.findViewById(R.id.imageview_profile);
            viewHolder.mainLayout = (LinearLayout) view.findViewById(R.id.lineralayout_messageboard);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mainLayout.setTag(position);

        if (messageBoardList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(messageBoardList.get(position).getFull_name()));
            viewHolder.titleText.setText(messageBoardList.get(position).getTitle());
            viewHolder.dateText.setText(getDate(messageBoardList.get(position).getLast_updated()));

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(messageBoardList.get(position).getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(messageBoardList.get(position).getImage()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.profile);

            applyReadStatus(viewHolder, messageBoardList.get(position));
        }
        viewHolder.mainLayout.setOnClickListener(onClick);
        return view;
    }

    public interface OnItemClickListener {
        void onItemClickListener(MessageBoard_ messageBoard_);
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.lineralayout_messageboard:
                    onItemClickListener.onItemClickListener(getItem(position));
                    break;
            }
        }
    };

    private void applyReadStatus(ViewHolder holder, MessageBoard_ messageBoard_) {
        if (messageBoard_.getIs_read() == 1) {
            holder.nameText.setTypeface(null, Typeface.NORMAL);
            holder.titleText.setTypeface(null, Typeface.NORMAL);

            holder.titleText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
            holder.nameText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
        } else {
            holder.nameText.setTypeface(null, Typeface.BOLD);
            holder.titleText.setTypeface(null, Typeface.BOLD);

            holder.titleText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
            holder.nameText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
        }
    }

    private class ViewHolder {
        TextView nameText, dateText, titleText;
        ImageView profile;
        LinearLayout mainLayout;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }
}
