package com.modules.blog;

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
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 21-09-2017
 * Last Modified on 13-12-2017
 */

class BlogListAdapter extends ArrayAdapter<Blog_> {

    private static final String TAG = BlogListAdapter.class.getSimpleName();
    private final List<Blog_> blogList;

    private final OnItemClickListener onItemClickListener;
    private final Activity activity;

    BlogListAdapter(Activity activity, List<Blog_> blogList, OnItemClickListener onItemClickListener) {
        super(activity, 0, blogList);
        this.blogList = blogList;
        this.onItemClickListener = onItemClickListener;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return blogList.size();
    }

    @Override
    public Blog_ getItem(int position) {
        if (blogList != null && blogList.size() > 0) {
            return blogList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return blogList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.blog_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.blog_list_item_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.blog_list_item_date);
            viewHolder.titleText = (TextView) view.findViewById(R.id.blog_list_item_title);
            viewHolder.descriptionText = (TextView) view.findViewById(R.id.blog_list_item_description);

            viewHolder.profile = (ImageView) view.findViewById(R.id.blog_list_item_image);

            viewHolder.mainLayout = (LinearLayout) view.findViewById(R.id.blog_list_item_main_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mainLayout.setTag(position);

        if (blogList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(blogList.get(position).getName()));
            viewHolder.titleText.setText(blogList.get(position).getTitle());
            viewHolder.descriptionText.setText(blogList.get(position).getDescription());
            viewHolder.dateText.setText(GetTime.wallTime(blogList.get(position).getDate()));

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(blogList.get(position).getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(blogList.get(position).getPhoto()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.profile);
        }

        viewHolder.mainLayout.setOnClickListener(onClick);
        return view;
    }

    // On click interface
    interface OnItemClickListener {
        void onItemClickListener(Blog_ blog_);
    }

    // Handle on click for row/item of blog list view
    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.blog_list_item_main_layout:
                    onItemClickListener.onItemClickListener(getItem(position));
                    break;
            }
        }
    };

    private class ViewHolder {
        TextView nameText, dateText, titleText, descriptionText;
        ImageView profile;
        LinearLayout mainLayout;
    }
}
