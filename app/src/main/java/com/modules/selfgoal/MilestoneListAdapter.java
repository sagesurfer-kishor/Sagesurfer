package com.modules.selfgoal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane <girish@sagesurfer.com>
 *         Created on 04-03-2016
 *         Last Modified on 27-12-2017
 */

public class MilestoneListAdapter extends ArrayAdapter<HashMap<String, String>> {

    private final ArrayList<HashMap<String, String>> milestoneList;
    private final static String TAG = MilestoneListAdapter.class.getSimpleName();

    private final Activity activity;

    public MilestoneListAdapter(Activity activity, ArrayList<HashMap<String, String>> milestoneList) {
        super(activity, 0, milestoneList);
        this.milestoneList = milestoneList;
        this.activity = activity;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        HashMap<String, String> currentMap;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.goal_milestone_list_item_layout, parent, false);
            holder = new ViewHolder();

            holder.nameText = (TextView) view.findViewById(R.id.goal_milestone_item_name);
            holder.statusText = (TextView) view.findViewById(R.id.goal_milestone_item_status);
            holder.checkBox = (CheckBox) view.findViewById(R.id.goal_milestone_item_check);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        currentMap = milestoneList.get(position);

        holder.nameText.setText(currentMap.get(General.NAME));
        if (currentMap.get(General.CHECK).equalsIgnoreCase("1")) {
            holder.checkBox.setChecked(true);
            holder.statusText.setText(activity.getApplicationContext().getResources().getString(R.string.achieved));
            holder.statusText.setTextColor(activity.getApplicationContext().getResources()
                    .getColor(R.color.self_goal_green));
        } else {
            holder.checkBox.setChecked(false);
            holder.statusText.setText(currentMap.get(General.START_DATE));
            holder.statusText.setTextColor(activity.getApplicationContext().getResources()
                    .getColor(R.color.text_color_secondary));
        }

        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(onClick);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (milestoneList.get(position).get(General.IS_SELECTED).equalsIgnoreCase("1")) {
                    buttonView.setChecked(true);
                }
            }
        });
        return view;
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String check;
            int position = (Integer) v.getTag();
            if (!milestoneList.get(position).get(General.IS_SELECTED).equalsIgnoreCase("1")) {
                if (milestoneList.get(position).get(General.CHECK).equalsIgnoreCase("1")) {
                    check = "0";
                } else {
                    check = "1";
                }
                updateMilestone(check, position);
                notifyDataSetChanged();
            } else {
                ShowToast.toast("You can not change milestone status", activity.getApplicationContext());
            }
        }
    };

    private void updateMilestone(String check, int position) {
        int result = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.UPDATE_MILESTONE);
        requestMap.put(General.ID, milestoneList.get(position).get(General.ID));
        requestMap.put(General.CHECK, check);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        String response;
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Actions_.UPDATE_MILESTONE)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.UPDATE_MILESTONE);
                        JSONObject object = jsonArray.getJSONObject(0);
                        result = object.getInt(General.STATUS);
                    }
                } else {
                    result = 11;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        switch (result) {
            case 1:
                ShowToast.successful(activity.getApplicationContext().getResources()
                        .getString(R.string.successful), activity.getApplicationContext());
                milestoneList.get(position).put(General.CHECK, check);
                notifyDataSetChanged();
                break;
            case 2:
                ShowToast.toast(activity.getApplicationContext().getResources()
                        .getString(R.string.action_failed), activity.getApplicationContext());
                break;
            case 3:
                ShowToast.toast("You can not change milestone status", activity.getApplicationContext());
                break;
            case 11:
                ShowToast.internalErrorOccurred(activity.getApplicationContext());
                break;
            case 12:
                ShowToast.networkError(activity.getApplicationContext());
                break;

        }
    }

    private class ViewHolder {
        TextView nameText, statusText;
        CheckBox checkBox;
    }
}