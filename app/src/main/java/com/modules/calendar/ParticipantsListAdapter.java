package com.modules.calendar;

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
import com.sagesurfer.parser.Participant_;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 18-08-2017
 * Last Modified on 13-12-2017
 */

class ParticipantsListAdapter extends ArrayAdapter<Participant_> {

    private final List<Participant_> usersList;

    private final Context context;

    ParticipantsListAdapter(Context context, List<Participant_> usersList) {
        super(context, 0, usersList);
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public Participant_ getItem(int position) {
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
            viewHolder.roleText = (TextView) view.findViewById(R.id.team_list_item_role);
            viewHolder.image = (ImageView) view.findViewById(R.id.team_list_item_image);
            viewHolder.checkIcon = (AppCompatImageView) view.findViewById(R.id.team_list_item_name_check);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.team_list_item_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        view.setBackgroundColor(context.getResources().getColor(R.color.white));
        viewHolder.mainLayout.setTag(position);
        viewHolder.mainLayout.setVisibility(View.VISIBLE);
        viewHolder.checkIcon.setVisibility(View.VISIBLE);

        viewHolder.nameText.setText(usersList.get(position).getName());
        viewHolder.roleText.setText(usersList.get(position).getRole());
        viewHolder.roleText.setVisibility(View.VISIBLE);
        if (usersList.get(position).getIs_accepted() == 1) {
            viewHolder.checkIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkIcon.setVisibility(View.GONE);
        }

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

        return view;
    }


    private class ViewHolder {
        AppCompatImageView checkIcon;
        TextView nameText, roleText;
        ImageView image;
        RelativeLayout mainLayout;
    }
}
