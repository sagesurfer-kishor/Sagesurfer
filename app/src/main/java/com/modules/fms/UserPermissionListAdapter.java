package com.modules.fms;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 31-07-2017
 * Last Modified on 13-12-2017
 */

class UserPermissionListAdapter extends ArrayAdapter<Friends_> {
    private final List<Friends_> usersList;
    private final List<Friends_> list;
    private final Activity activity;
    private ViewHolder viewHolder;

    UserPermissionListAdapter(Activity activity, List<Friends_> usersList) {
        super(activity, 0, usersList);
        this.usersList = usersList;
        this.activity = activity;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.fms_user_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.fms_user_list_item_name);
            viewHolder.permissionText = (TextView) view.findViewById(R.id.fms_user_list_item_permission);
            viewHolder.image = (ImageView) view.findViewById(R.id.fms_user_list_item_image);

            viewHolder.viewText = (TextView) view.findViewById(R.id.fms_user_list_item_view);
            viewHolder.adminText = (TextView) view.findViewById(R.id.fms_user_list_item_admin);
            viewHolder.modifyText = (TextView) view.findViewById(R.id.fms_user_list_item_modify);
            viewHolder.readText = (TextView) view.findViewById(R.id.fms_user_list_item_read);

            viewHolder.warningText = (TextView) view.findViewById(R.id.fms_user_list_item_warning);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.fms_user_list_item_main_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (usersList.get(position).getStatus() == 0) {
            viewHolder.mainLayout.setVisibility(View.GONE);
            viewHolder.warningText.setVisibility(View.VISIBLE);
            view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));
            return view;
        }
        if (usersList.get(position).getStatus() == 1) {

            if (usersList.get(position).getUserId() == Integer.parseInt(Preferences.get(General.USER_ID))) {
                viewHolder.mainLayout.setVisibility(View.GONE);
                view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));
                return view;
            } else {
                view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.white));
                viewHolder.mainLayout.setTag(position);
                viewHolder.mainLayout.setVisibility(View.VISIBLE);
                viewHolder.warningText.setVisibility(View.GONE);

                viewHolder.nameText.setText(usersList.get(position).getName());
                viewHolder.permissionText.setText(getPermission(usersList.get(position).getUser_access()));

                //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                Glide.with(activity.getApplicationContext())
                        .load(usersList.get(position).getPhoto())
                        .thumbnail(0.5f)
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_user_male)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.ALL) //.RESULT
                                .transform(new CircleTransform(activity.getApplicationContext())))
                        .into(viewHolder.image);

                viewHolder.permissionText.setTag(position);
                viewHolder.permissionText.setOnClickListener(onClick);
            }
        }
        return view;
    }

    //get permission string based on integer
    private String getPermission(int permission) {
        //-1 forbidden; 1 view; 2 read;3 modify; 4 admin
        switch (permission) {
            case 1:
                viewHolder.viewText.setVisibility(View.VISIBLE);
                viewHolder.readText.setVisibility(View.GONE);
                viewHolder.modifyText.setVisibility(View.GONE);
                viewHolder.adminText.setVisibility(View.GONE);
                return activity.getApplicationContext().getResources().getString(R.string.view);
            case 2:
                viewHolder.viewText.setVisibility(View.GONE);
                viewHolder.readText.setVisibility(View.VISIBLE);
                viewHolder.modifyText.setVisibility(View.GONE);
                viewHolder.adminText.setVisibility(View.GONE);
                return activity.getApplicationContext().getResources().getString(R.string.read);
            case 3:
                viewHolder.viewText.setVisibility(View.GONE);
                viewHolder.readText.setVisibility(View.VISIBLE);
                viewHolder.modifyText.setVisibility(View.VISIBLE);
                viewHolder.adminText.setVisibility(View.GONE);
                return activity.getApplicationContext().getResources().getString(R.string.modify);
            case 4:
                viewHolder.viewText.setVisibility(View.GONE);
                viewHolder.readText.setVisibility(View.VISIBLE);
                viewHolder.modifyText.setVisibility(View.VISIBLE);
                viewHolder.adminText.setVisibility(View.VISIBLE);
                return activity.getApplicationContext().getResources().getString(R.string.admin);
            default:
                viewHolder.viewText.setVisibility(View.GONE);
                viewHolder.readText.setVisibility(View.GONE);
                viewHolder.modifyText.setVisibility(View.GONE);
                viewHolder.adminText.setVisibility(View.GONE);
                return activity.getApplicationContext().getResources().getString(R.string.forbidden);
        }
    }

    // get permission value in integer based on string value
    private int getPermission(String permission) {
        //-1 forbidden; 1 view; 2 read;3 modify; 4 admin
        if (permission.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.view))) {
            viewHolder.viewText.setVisibility(View.VISIBLE);
            viewHolder.readText.setVisibility(View.GONE);
            viewHolder.modifyText.setVisibility(View.GONE);
            viewHolder.adminText.setVisibility(View.GONE);
            return 1;
        }
        if (permission.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.read))) {
            viewHolder.viewText.setVisibility(View.GONE);
            viewHolder.readText.setVisibility(View.VISIBLE);
            viewHolder.modifyText.setVisibility(View.GONE);
            viewHolder.adminText.setVisibility(View.GONE);
            return 2;
        }
        if (permission.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.modify))) {
            viewHolder.viewText.setVisibility(View.GONE);
            viewHolder.readText.setVisibility(View.VISIBLE);
            viewHolder.modifyText.setVisibility(View.VISIBLE);
            viewHolder.adminText.setVisibility(View.GONE);
            return 3;
        }
        if (permission.equalsIgnoreCase(activity.getApplicationContext().getResources().getString(R.string.admin))) {
            viewHolder.viewText.setVisibility(View.GONE);
            viewHolder.readText.setVisibility(View.VISIBLE);
            viewHolder.modifyText.setVisibility(View.VISIBLE);
            viewHolder.adminText.setVisibility(View.VISIBLE);
            return 4;
        }
        viewHolder.viewText.setVisibility(View.GONE);
        viewHolder.readText.setVisibility(View.GONE);
        viewHolder.modifyText.setVisibility(View.GONE);
        viewHolder.adminText.setVisibility(View.GONE);
        return -1;
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            if (getStatus(position) == 1) {
                showStatusPopup(position, v);
            }
        }
    };

    private void showStatusPopup(final int position, final View view) {
        final PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.file_menu_permission, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Friends_ friends_ = usersList.get(position);
                usersList.remove(position);
                friends_.setUser_access(getPermission(item.getTitle().toString()));
                usersList.add(position, friends_);
                notifyDataSetChanged();
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    private class ViewHolder {
        TextView nameText, permissionText, viewText, readText, modifyText, adminText, warningText;
        ImageView image;
        RelativeLayout mainLayout;
    }

    // filter user based on search query
    void filterUsers(String searchString) {
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
