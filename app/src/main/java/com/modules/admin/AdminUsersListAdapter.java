package com.modules.admin;

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
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 24-08-2017
 * Last Modified on 13-12-2017
 */

class AdminUsersListAdapter extends ArrayAdapter<Friends_> {
    private List<Friends_> usersList;
    private Context context;

    AdminUsersListAdapter(Context context, List<Friends_> usersList) {
        super(context, 0, usersList);
        this.usersList = usersList;
        this.context = context;
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
            view = layoutInflater.inflate(R.layout.admin_users_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.admin_user_list_item_name);
            viewHolder.roleText = (TextView) view.findViewById(R.id.admin_user_list_item_role);

            viewHolder.image = (ImageView) view.findViewById(R.id.admin_user_list_item_photo);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (usersList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(usersList.get(position).getName()));
            viewHolder.roleText.setText(usersList.get(position).getRole());

            //Monika M 2/6/18- Glide upgradation v3.7.0 to v4.1.1
            Glide.with(context)
                    .load(usersList.get(position).getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_group_default)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//.RESULT
                            .transform(new CircleTransform(context)))
                    .into(viewHolder.image);
        }
        return view;
    }

    private class ViewHolder {
        TextView nameText, roleText;
        ImageView image;
    }
}