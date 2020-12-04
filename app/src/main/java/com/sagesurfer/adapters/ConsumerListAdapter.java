package com.sagesurfer.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.modules.consent.Consumer_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.views.CircleTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 09-08-2017
 *         Last Modified on 15-12-2017
 */

public class ConsumerListAdapter extends ArrayAdapter<Consumer_> {

    private final List<Consumer_> usersList;
    private final List<Consumer_> list;
    private final Context context;

    public ConsumerListAdapter(Context context, List<Consumer_> usersList) {
        super(context, 0, usersList);
        this.usersList = usersList;
        this.context = context;
        this.list = new ArrayList<>();
        this.list.addAll(this.usersList);
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public Consumer_ getItem(int position) {
        if (usersList != null && usersList.size() > 0) {
            return usersList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.team_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.team_list_item_name);
            viewHolder.warningText = (TextView) view.findViewById(R.id.team_list_item_warning);

            viewHolder.image = (ImageView) view.findViewById(R.id.team_list_item_image);
            viewHolder.checkIcon = (AppCompatImageView) view.findViewById(R.id.team_list_item_check);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.team_list_item_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (usersList.get(position).getStatus() == 0) {
            viewHolder.mainLayout.setVisibility(View.GONE);
            viewHolder.warningText.setVisibility(View.VISIBLE);
            view.setBackgroundColor(context.getResources().getColor(R.color.screen_background));
            return view;
        }
        if (usersList.get(position).getStatus() == 1) {
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
            viewHolder.mainLayout.setTag(position);
            viewHolder.mainLayout.setVisibility(View.VISIBLE);
            viewHolder.warningText.setVisibility(View.GONE);
            viewHolder.checkIcon.setVisibility(View.VISIBLE);

            viewHolder.nameText.setText(usersList.get(position).getName());
            Glide.with(context)
                    .load(usersList.get(position).getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_group_default)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(context)))
                    .into(viewHolder.image);

            viewHolder.checkIcon.setVisibility(View.GONE);
        }
        return view;
    }

    private class ViewHolder {
        AppCompatImageView checkIcon;
        TextView nameText, warningText;
        ImageView image;
        RelativeLayout mainLayout;
    }

    // filter users list based on search string
    public void filterUsers(String searchString) {
        usersList.clear();
        if (searchString == null) {
            usersList.addAll(list);
            notifyDataSetChanged();
            return;
        }
        searchString = searchString.toLowerCase(Locale.getDefault());
        if (searchString.length() <= 0) {
            usersList.addAll(list);
        } else {
            for (Consumer_ consumer_ : list) {
                String name = consumer_.getName();
                if (name.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    usersList.add(consumer_);
                }
            }
        }
        if (usersList.size() <= 0) {
            Consumer_ consumer_ = new Consumer_();
            consumer_.setStatus(0);
            usersList.add(consumer_);
        }
        notifyDataSetChanged();
    }
}
