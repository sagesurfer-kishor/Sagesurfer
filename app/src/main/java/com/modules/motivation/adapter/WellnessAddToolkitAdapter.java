package com.modules.motivation.adapter;

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

import com.modules.motivation.model.ToolKitData;
import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;

/**
 * Created by Kailash Karankal on 5/25/2019.
 */
public class WellnessAddToolkitAdapter extends ArrayAdapter<ToolKitData> {
    private static final String TAG = WellnessAddToolkitAdapter.class.getSimpleName();
    private final ArrayList<ToolKitData> toolKitDataArrayList;
    private final Activity activity;

    public WellnessAddToolkitAdapter(Activity activity, ArrayList<ToolKitData> toolKitData) {
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

        final WellnessAddToolkitAdapter.ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.wellness_toolkit_item, parent, false);
            holder = new WellnessAddToolkitAdapter.ViewHolder();

            holder.titleText = (TextView) view.findViewById(R.id.textview_pray);
            holder.toolkitCheckbox = (CheckBox) view.findViewById(R.id.checkbox_toolkit);

            view.setTag(holder);
        } else {
            holder = (WellnessAddToolkitAdapter.ViewHolder) view.getTag();
        }

        ToolKitData toolKitData = toolKitDataArrayList.get(position);
        holder.toolkitCheckbox.setTag(position);
        holder.titleText.setText(toolKitData.getName());

        boolean checked = toolKitDataArrayList.get(position).getSelected();
        if (checked) {
            holder.toolkitCheckbox.setChecked(true);
        } else {
            holder.toolkitCheckbox.setChecked(false);
        }

        holder.toolkitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (Integer) holder.toolkitCheckbox.getTag();
                if (isChecked) {
                    holder.toolkitCheckbox.setChecked(true);
                    toolKitDataArrayList.get(position).setSelected(true);
                } else {
                    holder.toolkitCheckbox.setChecked(false);
                    toolKitDataArrayList.get(position).setSelected(false);
                }
                notifyDataSetChanged();
            }
        });
        return view;
    }

    private class ViewHolder {
        TextView titleText;
        CheckBox toolkitCheckbox;
    }
}