package com.modules.contacts;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Contacts_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.views.CircleTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 07-08-2017
 * Last Modified on 13-12-2017
 */


class AllContactListAdapter extends ArrayAdapter<Contacts_>/*RecyclerView.Adapter<AllContactListAdapter.MyViewHolder>*/ {

    private final List<Contacts_> contactsList;
    private final List<Contacts_> list;
    private String initial = "";
    ViewHolder viewHolder;
    private final Activity activity;

    AllContactListAdapter(Activity activity, List<Contacts_> contactsList) {
        super(activity, 0, contactsList);
        this.contactsList = contactsList;
        this.activity = activity;
        this.list = new ArrayList<>();
        this.list.addAll(contactsList);
    }

    @Override
    public Contacts_ getItem(int position) {
        return contactsList.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.contact_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.contact_list_item_name);
            viewHolder.warningText = (TextView) view.findViewById(R.id.contact_list_item_warning);
            viewHolder.initialText = (TextView) view.findViewById(R.id.contact_list_item_header);
            viewHolder.image = (ImageView) view.findViewById(R.id.contact_list_item_image);

            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.contact_list_item_layout);

            viewHolder.initialText.setTag(position);
            view.setTag(viewHolder);
        }

        if (contactsList.get(position).getStatus() == 0) {
            viewHolder.mainLayout.setVisibility(View.GONE);
            viewHolder.warningText.setVisibility(View.VISIBLE);
            viewHolder.initialText.setVisibility(View.GONE);
            view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));
            return view;
        }
        if (contactsList.get(position).getStatus() == 1) {
            if (!initial.equalsIgnoreCase(contactsList.get(position).getFirstName().substring(0, 1))) {
                initial = contactsList.get(position).getFirstName().substring(0, 1).toUpperCase();

                viewHolder.mainLayout.setVisibility(View.GONE);
                viewHolder.warningText.setVisibility(View.GONE);
                viewHolder.initialText.setVisibility(View.VISIBLE);
                viewHolder.initialText.setText(initial);
            } else {
                viewHolder.initialText.setVisibility(View.GONE);
            }

            view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.white));
            viewHolder.mainLayout.setVisibility(View.VISIBLE);
            viewHolder.warningText.setVisibility(View.GONE);

            String name = contactsList.get(position).getFirstName() + " " + contactsList.get(position).getLastName();
            viewHolder.nameText.setText(name);
            viewHolder.nameText.setTypeface(null, Typeface.NORMAL);

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

            viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment dialogFrag = new ContactDetailsDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Actions_.TEAM_CONTACT, contactsList.get(position));
                    dialogFrag.setArguments(bundle);
                    dialogFrag.show(activity.getFragmentManager().beginTransaction(), General.COMMENT_COUNT);
                }
            });

            viewHolder.initialText.setTag(position);
            viewHolder.mainLayout.setTag(position);
        }

        return view;
    }

    //Filter list based on search query
    void filterContacts(String searchString) {
        contactsList.clear();
        if (searchString == null) {
            contactsList.addAll(list);
            notifyDataSetChanged();
            return;
        }
        searchString = searchString.toLowerCase(Locale.getDefault());
        if (searchString.length() <= 0) {
            contactsList.addAll(list);
        } else {
            for (Contacts_ contacts_ : list) {
                String firstName = contacts_.getFirstName();
                String lastName = contacts_.getLastName();
                if (firstName.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    contactsList.add(contacts_);
                } else if (lastName.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    contactsList.add(contacts_);
                }
            }
        }
        if (contactsList.size() <= 0) {
            Contacts_ contacts_ = new Contacts_();
            contacts_.setStatus(0);
            contactsList.add(contacts_);
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView nameText, initialText, warningText;
        ImageView image;
        RelativeLayout mainLayout;
    }
}
