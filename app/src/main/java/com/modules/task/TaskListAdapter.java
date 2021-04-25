package com.modules.task;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Task_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 21-07-2017
 *         Last Modified on 14-12-2017
 */

class TaskListAdapter extends ArrayAdapter<Task_> {

    private static final String TAG = TaskListAdapter.class.getSimpleName();
    private final List<Task_> taskList;

    private final OnItemClickListener onItemClickListener;
    private final Activity activity;
    private final Context mContext;

    TaskListAdapter(Activity activity, List<Task_> taskList, OnItemClickListener onItemClickListener) {
        super(activity, 0, taskList);
        this.taskList = taskList;
        this.onItemClickListener = onItemClickListener;
        this.activity = activity;
        mContext = this.activity.getApplicationContext();
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

    @Override
    public long getItemId(int position) {
        return taskList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.task_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.task_list_item_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.task_list_item_date);
            viewHolder.teamText = (TextView) view.findViewById(R.id.task_list_item_team);
            viewHolder.titleText = (TextView) view.findViewById(R.id.task_list_item_title);
            viewHolder.statusText = (TextView) view.findViewById(R.id.task_list_item_status);
            viewHolder.descriptionText = (TextView) view.findViewById(R.id.task_list_item_description);
            viewHolder.tagText = (TextView) view.findViewById(R.id.task_list_item_tag);

            viewHolder.profile = (ImageView) view.findViewById(R.id.task_list_item_image);

            viewHolder.mainLayout = (LinearLayout) view.findViewById(R.id.task_list_item_main_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.statusText.setTag(position);
        viewHolder.mainLayout.setTag(position);

        if (taskList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(taskList.get(position).getFullName()));
            if (taskList.get(position).getOwnOrTeam() == 0) {
                viewHolder.tagText.setText(activity.getApplicationContext().getResources()
                        .getString(R.string.posted_by));
                viewHolder.teamText.setText(taskList.get(position).getFullName());
            } else {
                viewHolder.tagText.setText(activity.getApplicationContext().getResources()
                        .getString(R.string.posted_in));
                viewHolder.teamText.setText(taskList.get(position).getTeamName());
            }
            viewHolder.titleText.setText(taskList.get(position).getTitle());
            viewHolder.statusText.setText(taskList.get(position).getToDoStatus());
            viewHolder.statusText.setTextColor(GetColor.getTaskStatusColor(taskList.get(position).getToDoStatus(), activity.getApplicationContext()));
            viewHolder.descriptionText.setText(taskList.get(position).getDescription());
            viewHolder.dateText.setText(GetTime.dateValue(taskList.get(position).getDate()));

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(taskList.get(position).getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(taskList.get(position).getImage()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.profile);

            if (taskList.get(position).getIsOwner() == 1) {
                viewHolder.statusText.setBackgroundResource(R.drawable.ic_down_arrow_ternary);
            } else {
                viewHolder.statusText.setBackgroundResource(0);
            }
            applyReadStatus(viewHolder, taskList.get(position));
        }
        viewHolder.statusText.setOnClickListener(onClick);
        viewHolder.mainLayout.setOnClickListener(onClick);
        return view;
    }

    interface OnItemClickListener {
        void onItemClickListener(Task_ task_);
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.task_list_item_status:
                    if (taskList.get(position).getIsOwner() == 1) {
                        String date = GetTime.dateValue(taskList.get(position).getDate());
                        if (date != null && !date.isEmpty()) {
                            showStatusPopUp(v, position);
                        } else {
                            SubmitSnackResponse.showSnack(1, "Task Expired", v, activity.getApplicationContext());
                        }
                    }
                    break;
                case R.id.task_list_item_main_layout:
                    onItemClickListener.onItemClickListener(getItem(position));
                    break;
            }
        }
    };

    //open task status menu
    private void showStatusPopUp(final View view, final int position) {
        PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.task_list_status_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String old_status = taskList.get(position).getToDoStatus();
                if (!old_status.equalsIgnoreCase(item.getTitle().toString())) {
                    //changeStatus(item.getTitle().toString(), position, view);
                    int status = PerformGetTeamsTask.changeStatus(mContext, TAG, item.getTitle().toString(), taskList.get(position).getId(), activity);
                    showResponses(status, view);
                    if (status == 1) {
                        taskList.get(position).setToDoStatus(item.getTitle().toString());
                        notifyDataSetChanged();
                    }
                }
                return true;
            }
        });
        popup.show();
    }

    // make network call to change task status
    private void changeStatus(String _status, int _position, View view) {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TASK_LIST_STATUS);
        requestMap.put(General.TASK_ID, "" + taskList.get(_position).getId());
        requestMap.put(General.TASK_STATUS, _status);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.OTHER_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    if (Error_.oauth(response, activity.getApplicationContext()) == 0) {
                        JsonArray jsonArray = GetJson_.getArray(response, Actions_.DETAILS);
                        if (jsonArray != null) {
                            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                            status = jsonObject.get(General.STATUS).getAsInt();
                        } else {
                            status = 11;
                        }
                    } else {
                        status = 13;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(status, view);
        if (status == 1) {
            taskList.get(_position).setToDoStatus(_status);
            notifyDataSetChanged();
        }
    }

    private void showResponses(int status, View view) {
        String message = "";
        if (status == 1) {
            message = mContext.getResources().getString(R.string.successful);
        } else if (status == 2) {
            message = mContext.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, activity.getApplicationContext());
    }

    private void applyReadStatus(ViewHolder holder, Task_ task_) {
        if (task_.getIsRead() == 1) {
            holder.nameText.setTypeface(null, Typeface.NORMAL);
            holder.teamText.setTypeface(null, Typeface.NORMAL);
            holder.titleText.setTypeface(null, Typeface.NORMAL);

            holder.titleText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
            holder.nameText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
            holder.teamText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
            holder.tagText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
        } else {
            holder.nameText.setTypeface(null, Typeface.BOLD);
            holder.teamText.setTypeface(null, Typeface.BOLD);
            holder.titleText.setTypeface(null, Typeface.BOLD);

            holder.titleText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
            holder.nameText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
            holder.teamText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
            holder.tagText.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));

            if(Preferences.get(General.TASKLIST_ID) != null && !Preferences.get(General.TASKLIST_ID).equalsIgnoreCase("")) {
                if(Preferences.get(General.TASKLIST_ID).equalsIgnoreCase(String.valueOf(task_.getId()))) {
                    holder.mainLayout.setBackground(activity.getResources().getDrawable(R.drawable.white_rounded_rectangle_blue_border));
                    Preferences.save(General.TASKLIST_ID, "");
                }
            }
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy ", cal).toString();
        return date;
    }

    private class ViewHolder {
        TextView nameText, dateText, teamText, titleText, descriptionText, statusText, tagText;
        ImageView profile;
        LinearLayout mainLayout;
    }
}
