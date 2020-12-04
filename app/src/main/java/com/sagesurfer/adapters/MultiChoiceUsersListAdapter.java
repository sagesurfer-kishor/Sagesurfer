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
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.views.CircleTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 31-07-2017
 * Last Modified on 15-12-2017
 */

public class MultiChoiceUsersListAdapter extends ArrayAdapter<Friends_> {

    private final List<Friends_> usersList;
    private final List<Friends_> list;
    private final Context context;

    public MultiChoiceUsersListAdapter(Context context, List<Friends_> usersList) {
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
    public Friends_ getItem(int position) {
        if (usersList != null && usersList.size() > 0) {
            return usersList.get(position);
        }
        return null;
    }

    private int getStatus(int position) {
        if (usersList != null && usersList.size() > 0) {
            return usersList.get(position).getStatus();
        }
        return 0;
    }

    private boolean getSelected(int position) {
        return usersList != null && usersList.size() > 0 && usersList.get(position).getSelected();
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
            viewHolder.addIcon = (AppCompatImageView) view.findViewById(R.id.team_list_item_add);
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
            viewHolder.addIcon.setVisibility(View.GONE);
            viewHolder.checkIcon.setVisibility(View.VISIBLE);

            viewHolder.nameText.setText(usersList.get(position).getName());

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(context)
                    .load(usersList.get(position).getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_group_default)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL) //.RESULT
                            .transform(new CircleTransform(context)))
                    .into(viewHolder.image);

            if (usersList.get(position).getSelected()) {
                viewHolder.addIcon.setVisibility(View.GONE);
                viewHolder.checkIcon.setImageResource(R.drawable.vi_check_blue);
                viewHolder.addIcon.setImageResource(0);
            } else {
                viewHolder.addIcon.setVisibility(View.VISIBLE);
                viewHolder.addIcon.setImageResource(R.drawable.vi_add_task_list_p);
                viewHolder.checkIcon.setImageResource(0);
            }
        }
        //viewHolder.mainLayout.setOnClickListener(onClick);

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                if (getStatus(position) == 1) {
                    if (getSelected(position)) {
                        usersList.get(position).setSelected(false);
                        viewHolder.addIcon.setVisibility(View.VISIBLE);
                        viewHolder.addIcon.setImageResource(R.drawable.vi_add_task_list_p);
                        viewHolder.checkIcon.setImageResource(0);
                    } else {
                        usersList.get(position).setSelected(true);
                        viewHolder.addIcon.setVisibility(View.GONE);
                        viewHolder.addIcon.setImageResource(0);
                        viewHolder.checkIcon.setImageResource(R.drawable.vi_check_blue);
                    }
                }
            }
        });
        return view;
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            if (getStatus(position) == 1) {
                if (getSelected(position)) {
                    usersList.get(position).setSelected(false);
                } else {
                    usersList.get(position).setSelected(true);
                }
                notifyDataSetChanged();
            }
        }
    };

    private class ViewHolder {
        AppCompatImageView addIcon, checkIcon;
        TextView nameText, warningText;
        ImageView image;
        RelativeLayout mainLayout;
    }

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
            for (Friends_ teams_ : list) {
                String name = teams_.getName();
                if (name.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    usersList.add(teams_);
                }
            }
        }
        if (usersList.size() <= 0) {
            Friends_ teams_ = new Friends_();
            teams_.setStatus(0);
            usersList.add(teams_);
        }
        notifyDataSetChanged();
    }
}
