package com.modules.contacts;

import android.app.Activity;
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
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.EmergencyParent_;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

class EmergencyContactListAdapter extends ArrayAdapter<EmergencyParent_> {

    private final List<EmergencyParent_> emergencyParentList;

    private final Activity activity;

    EmergencyContactListAdapter(Activity activity, List<EmergencyParent_> emergencyParentList) {
        super(activity, 0, emergencyParentList);
        this.emergencyParentList = emergencyParentList;
        this.activity = activity;
    }

    @Override
    public EmergencyParent_ getItem(int position) {
        return emergencyParentList.get(position);
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
            view = layoutInflater.inflate(R.layout.contact_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.contact_list_item_name);
            viewHolder.roleText = (TextView) view.findViewById(R.id.contact_list_item_role);
            viewHolder.warningText = (TextView) view.findViewById(R.id.contact_list_item_warning);
            viewHolder.initialText = (TextView) view.findViewById(R.id.contact_list_item_header);
            viewHolder.image = (ImageView) view.findViewById(R.id.contact_list_item_image);

            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.contact_list_item_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (emergencyParentList.get(position).getStatus() == 0) {
            viewHolder.mainLayout.setVisibility(View.GONE);
            viewHolder.warningText.setVisibility(View.VISIBLE);
            viewHolder.initialText.setVisibility(View.GONE);
            view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));
            return view;
        }
        if (emergencyParentList.get(position).getStatus() == 1) {
            viewHolder.mainLayout.setVisibility(View.VISIBLE);
            viewHolder.roleText.setVisibility(View.VISIBLE);
            viewHolder.warningText.setVisibility(View.GONE);
            viewHolder.initialText.setVisibility(View.GONE);
            viewHolder.mainLayout.setPadding(0, 0, 0, 0);
            view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.white));

            viewHolder.nameText.setText(emergencyParentList.get(position).getTeam());
            viewHolder.roleText.setText(emergencyParentList.get(position).getYouth());

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(emergencyParentList.get(position).getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(emergencyParentList.get(position).getImage()))
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL) //.RESULT
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.image);
        }

        return view;
    }

    private class ViewHolder {
        TextView nameText, initialText, warningText, roleText;
        ImageView image;
        RelativeLayout mainLayout;
    }
}
