package com.modules.motivation.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.modules.motivation.model.ToolKitData;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 6/22/2019.
 */
public class WellnessSelecteToolkitItemAdapter extends ArrayAdapter<ToolKitData> {
    private static final String TAG = WellnessAddToolkitAdapter.class.getSimpleName();
    private final ArrayList<ToolKitData> toolKitDataArrayList;
    private final Activity activity;

    public WellnessSelecteToolkitItemAdapter(Activity activity, ArrayList<ToolKitData> toolKitData) {
        super(activity, 0, toolKitData);
        this.toolKitDataArrayList = toolKitData;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return toolKitDataArrayList.size();
    }

    @Override
    public ToolKitData getItem(int position) {
        if (toolKitDataArrayList != null && toolKitDataArrayList.size() > 0) {
            return toolKitDataArrayList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return toolKitDataArrayList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final WellnessSelecteToolkitItemAdapter.ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.wellness_selected_toolkit_item, parent, false);
            holder = new WellnessSelecteToolkitItemAdapter.ViewHolder();

            holder.titleText = (TextView) view.findViewById(R.id.textview_pray);

            view.setTag(holder);
        } else {
            holder = (WellnessSelecteToolkitItemAdapter.ViewHolder) view.getTag();
        }

        ToolKitData toolKitData = toolKitDataArrayList.get(position);

        holder.titleText.setText(toolKitData.getName());

        return view;
    }

    private class ViewHolder {
        TextView titleText;
    }
}