package com.sagesurfer.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
 * Created on 08-09-2017
 * Last Modified on 15-12-2017
 */

public class SingleChoiceUserListAdapter extends ArrayAdapter<Friends_> {

    private final List<Friends_> usersList;
    private final List<Friends_> list;

    private final Context context;

    public SingleChoiceUserListAdapter(Context context, List<Friends_> usersList) {
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
            viewHolder.mainLayout.setVisibility(View.VISIBLE);
            viewHolder.warningText.setVisibility(View.GONE);

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
        }
        return view;
    }

    private class ViewHolder {
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
