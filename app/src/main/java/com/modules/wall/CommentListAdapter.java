package com.modules.wall;

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
 * Created on 11-07-2017
 * Last Modified on 15-12-2017
 */

class CommentListAdapter extends ArrayAdapter<Comment_> {
    private final List<Comment_> commentList;
    private final Context context;

    CommentListAdapter(Context context, List<Comment_> commentList) {
        super(context, 0, commentList);
        this.commentList = commentList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.wall_comment_item, parent, false);

            viewHolder.commentText = (TextView) view.findViewById(R.id.textview_wallcommentitem_text);
            viewHolder.nameText = (TextView) view.findViewById(R.id.textview_wallcommentitem_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.textview_wallcommentitem_time);

            viewHolder.profile = (ImageView) view.findViewById(R.id.imageview_wallcommentitem_image);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (commentList.get(position).getStatus() == 1) {
            viewHolder.commentText.setText(commentList.get(position).getComment());
            viewHolder.nameText.setText(ChangeCase.toTitleCase(commentList.get(position).getName()));
            viewHolder.dateText.setText(GetTime.wallTime(commentList.get(position).getDate()));

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(context.getApplicationContext())
                    .load(commentList.get(position).getProfilePhoto())
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .placeholder(GetThumbnails.userIcon(commentList.get(position).getProfilePhoto()))
                            .transform(new CircleTransform(context.getApplicationContext())))
                    .into(viewHolder.profile);
        }
        return view;
    }

    private class ViewHolder {
        TextView commentText, nameText, dateText;
        ImageView profile;
    }
}
