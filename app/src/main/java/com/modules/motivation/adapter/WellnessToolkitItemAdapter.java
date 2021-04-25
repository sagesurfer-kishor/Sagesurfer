package com.modules.motivation.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.modules.motivation.model.ToolKitData;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Kailash Karankal on 6/21/2019.
 */
public class WellnessToolkitItemAdapter extends ArrayAdapter<ToolKitData> {
    private static final String TAG = WellnessAddToolkitAdapter.class.getSimpleName();
    private final ArrayList<ToolKitData> motivationLibraryList;
    private final Activity activity;
    public final OnItemClickListener onItemClickListener;

    public WellnessToolkitItemAdapter(Activity activity, ArrayList<ToolKitData> motivationLibraryList, OnItemClickListener onItemClickListener) {
        super(activity, 0, motivationLibraryList);
        this.motivationLibraryList = motivationLibraryList;
        this.activity = activity;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return motivationLibraryList.size();
    }

    @Override
    public ToolKitData getItem(int position) {
        if (motivationLibraryList != null && motivationLibraryList.size() > 0) {
            return motivationLibraryList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return motivationLibraryList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final WellnessToolkitItemAdapter.ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.toolkit_selected_items, parent, false);
            holder = new WellnessToolkitItemAdapter.ViewHolder();

            holder.toolkitItemName = (TextView) view.findViewById(R.id.toolkit_item_name);
            holder.toolkitDate = (TextView) view.findViewById(R.id.date);
            holder.toolkitSelectedImg = (ImageView) view.findViewById(R.id.imageview_next);
            holder.nextLayout = (RelativeLayout) view.findViewById(R.id.next_layout);

            view.setTag(holder);
        } else {
            holder = (WellnessToolkitItemAdapter.ViewHolder) view.getTag();
        }

        ToolKitData toolKitData = motivationLibraryList.get(position);

        int pos = position + 1;
        holder.toolkitItemName.setTag(position);
        holder.toolkitItemName.setText("Toolkit Item" + " " + pos);

        // holder.toolkitDate.setText(getDate(toolKitData.getTimestamp()));
        holder.toolkitDate.setText(toolKitData.getDatetime());
        holder.nextLayout.setTag(position);
        holder.nextLayout.setOnClickListener(onClick);

        return view;
    }

    private class ViewHolder {
        TextView toolkitItemName, toolkitDate;
        ImageView toolkitSelectedImg;
        RelativeLayout nextLayout;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

    public interface OnItemClickListener {
        void onItemClickListener(ToolKitData toolKitData);
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.next_layout:
                    onItemClickListener.onItemClickListener(getItem(position));
                    break;
            }
        }
    };
}