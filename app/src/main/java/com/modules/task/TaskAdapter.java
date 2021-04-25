package com.modules.task;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.Task_;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 08-09-2017
 *         Last Modified on 14-12-2017
 */

class TaskAdapter extends ArrayAdapter<Task_> {

    private final List<Task_> taskList;
    private final List<Task_> list;
    private final Context context;

    TaskAdapter(Context context, List<Task_> taskList) {
        super(context, 0, taskList);
        this.taskList = taskList;
        this.context = context;
        this.list = new ArrayList<>();
        this.list.addAll(this.taskList);
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Task_ getItem(int position) {
        if (taskList != null && taskList.size() > 0) {
            return taskList.get(position);
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
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.simple_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.simple_item_title);
            //viewHolder.warningText = (TextView) view.findViewById(R.id.simple_item_warning);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (taskList.get(position).getStatus() == 1) {
            //viewHolder.warningText.setVisibility(View.GONE);
            viewHolder.nameText.setVisibility(View.VISIBLE);
            viewHolder.nameText.setText(taskList.get(position).getName());
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            //viewHolder.nameText.setVisibility(View.GONE);
            //viewHolder.warningText.setVisibility(View.VISIBLE);
            //view.setBackgroundColor(context.getResources().getColor(R.color.screen_background));
        }
        return view;
    }

    private class ViewHolder {
        TextView nameText, warningText;
    }

    // handle search query to filter content lst
    void filterTask(String searchString) {
        taskList.clear();
        if (searchString == null) {
            taskList.addAll(list);
            notifyDataSetChanged();
            return;
        }
        searchString = searchString.toLowerCase(Locale.getDefault());
        if (searchString.length() <= 0) {
            taskList.addAll(list);
        } else {
            for (Task_ task_ : list) {
                String name = task_.getName();
                if (name.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    taskList.add(task_);
                }
            }
        }
        if (taskList.size() <= 0) {
            Task_ teams_ = new Task_();
            teams_.setStatus(0);
            taskList.add(teams_);
        }
        notifyDataSetChanged();
    }
}
