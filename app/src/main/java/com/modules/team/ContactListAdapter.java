package com.modules.team;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
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
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Contacts_;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 07-08-2017
 * Last Modified on 15-12-2017
 */

class ContactListAdapter extends ArrayAdapter<Contacts_> {
    private final List<Contacts_> contactsList;
    private final Activity activity;

    ContactListAdapter(Activity activity, List<Contacts_> contactsList) {
        super(activity, 0, contactsList);
        this.contactsList = contactsList;
        this.activity = activity;
    }

    @Override
    public Contacts_ getItem(int position) {
        return contactsList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.team_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.team_list_item_name);
            viewHolder.roleText = (TextView) view.findViewById(R.id.team_list_item_role);
            viewHolder.warningText = (TextView) view.findViewById(R.id.team_list_item_warning);
            viewHolder.image = (ImageView) view.findViewById(R.id.team_list_item_image);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.team_list_item_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (contactsList.get(position).getStatus() == 0) {
            viewHolder.mainLayout.setVisibility(View.GONE);
            viewHolder.warningText.setVisibility(View.VISIBLE);
            view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));
            return view;
        }
        if (contactsList.get(position).getStatus() == 1) {
            view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.white));
            viewHolder.mainLayout.setVisibility(View.VISIBLE);
            viewHolder.roleText.setVisibility(View.VISIBLE);
            viewHolder.warningText.setVisibility(View.GONE);

            String fullName = contactsList.get(position).getFirstName() + " " + contactsList.get(position).getLastName();
            viewHolder.nameText.setText(fullName);
            viewHolder.nameText.setTypeface(null, Typeface.NORMAL);

            viewHolder.roleText.setText(contactsList.get(position).getRole());

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(contactsList.get(position).getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(contactsList.get(position).getImage()))
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL) //.RESULT
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.image);
        }
        return view;
    }

    private class ViewHolder {
        TextView nameText, roleText, warningText;
        ImageView image;
        RelativeLayout mainLayout;
    }
}
