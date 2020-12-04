package com.sagesurfer.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.modules.selfcare.ListItem;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

public class SimpleMultiSelectAdapter extends ArrayAdapter<ListItem> {
    private final Activity activity;
    private final ArrayList<ListItem> dataList;

    public SimpleMultiSelectAdapter(Activity activity, ArrayList<ListItem> dataList) {
        super(activity, 0, dataList);
        this.activity = activity;
        this.dataList = dataList;
    }

    private static class ViewHolder {
        TextView name;
        CheckBox check;
        ImageView icon;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public ListItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("NullableProblems")
    @SuppressLint("InflateParams")
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.multi_select_item, null);

            holder.name = (TextView) view.findViewById(R.id.multi_select_text);
            holder.check = (CheckBox) view.findViewById(R.id.multi_select_check_box);
            holder.icon = (ImageView) view.findViewById(R.id.multi_select_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.icon.setVisibility(View.GONE);

        holder.check.setTag(position);
        holder.name.setText(dataList.get(position).getName());

        boolean checked = dataList.get(position).getSelected();
        if (checked) {
            holder.check.setChecked(true);
        } else {
            holder.check.setChecked(false);
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) holder.check.getTag();
                boolean checked = holder.check.isChecked();
                if (checked) {
                    holder.check.setChecked(false);
                    dataList.get(position).setSelected(false);
                } else {
                    holder.check.setChecked(true);
                    dataList.get(position).setSelected(true);
                }
                notifyDataSetChanged();
            }
        });
        return view;
    }
}