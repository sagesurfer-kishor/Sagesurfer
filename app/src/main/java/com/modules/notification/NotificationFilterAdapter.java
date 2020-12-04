package com.modules.notification;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 6/12/2019.
 */
public class NotificationFilterAdapter extends ArrayAdapter<Notification> {
    private final ArrayList<Notification> notificationList;
    private final Activity activity;

    public NotificationFilterAdapter(Activity activity, ArrayList<Notification> notificationList) {
        super(activity, 0, notificationList);
        this.notificationList = notificationList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public long getItemId(int position) {
        return notificationList.get(position).getId();
    }

    @Override
    public Notification getItem(int position) {
        if (notificationList != null && notificationList.size() > 0) {
            return notificationList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.notification_filter_item_layout, parent, false);
            holder = new ViewHolder();

            holder.multiSelectText = (TextView) view.findViewById(R.id.multi_select_text);
            holder.multiSelectCheckBox = (CheckBox) view.findViewById(R.id.multi_select_check_box);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Notification notification = notificationList.get(position);
        holder.multiSelectCheckBox.setTag(position);
        holder.multiSelectText.setText(notification.getName());

        boolean checked = notificationList.get(position).getSelected();
        if (checked) {
            holder.multiSelectCheckBox.setChecked(true);
        } else {
            holder.multiSelectCheckBox.setChecked(false);
        }

        holder.multiSelectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (Integer) holder.multiSelectCheckBox.getTag();
                if (isChecked) {
                    holder.multiSelectCheckBox.setChecked(true);
                    notificationList.get(position).setSelected(true);
                } else {
                    holder.multiSelectCheckBox.setChecked(false);
                    notificationList.get(position).setSelected(false);
                }
                notifyDataSetChanged();
            }
        });

        return view;
    }

    static class ViewHolder {
        TextView multiSelectText;
        CheckBox multiSelectCheckBox;
    }
}
