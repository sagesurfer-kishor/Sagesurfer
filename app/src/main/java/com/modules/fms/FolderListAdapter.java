package com.modules.fms;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 13-09-2017
 *         Last Modified on 13-12-2017
 */

class FolderListAdapter extends ArrayAdapter<AllFolder_> {

    private final List<AllFolder_> allFolderList;

    private final Activity activity;
    private final OnItemClick onItemClick;

    FolderListAdapter(Activity activity, List<AllFolder_> allFolderList, OnItemClick onItemClick) {
        super(activity, 0, allFolderList);
        this.allFolderList = allFolderList;
        this.activity = activity;
        this.onItemClick = onItemClick;
    }

    @Override
    public int getCount() {
        return allFolderList.size();
    }

    @Override
    public AllFolder_ getItem(int position) {
        if (allFolderList != null && allFolderList.size() > 0) {
            return allFolderList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.folder_list_item, parent, false);

            viewHolder.folderNameText = (TextView) view.findViewById(R.id.folder_list_main_name);
            viewHolder.subFolderNameText = (TextView) view.findViewById(R.id.folder_list_sub_name);

            viewHolder.mainLayout = (LinearLayout) view.findViewById(R.id.folder_list_main_layout);
            viewHolder.subLayout = (LinearLayout) view.findViewById(R.id.folder_list_sub_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (allFolderList.get(position).getStatus() == 0) {
            viewHolder.mainLayout.setVisibility(View.GONE);
            view.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));
            return view;
        }
        if (allFolderList.get(position).getStatus() == 1) {
            viewHolder.mainLayout.setVisibility(View.VISIBLE);
            viewHolder.folderNameText.setText(allFolderList.get(position).getName());
            if (allFolderList.get(position).getSub_folder().size() > 0) {
                viewHolder.subLayout.setVisibility(View.VISIBLE);
                setSubFolders(viewHolder, allFolderList.get(position).getSub_folder(), position);
            } else {
                viewHolder.subLayout.setVisibility(View.GONE);
            }
            viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(0, "", allFolderList.get(position).getId(), allFolderList.get(position).getName());
                }
            });
        }
        return view;
    }

    // add sub folder view below folder in list view
    private void setSubFolders(ViewHolder viewHolder, final ArrayList<Folder_> folderArrayList, final int post) {
        viewHolder.subLayout.removeAllViews();
        Log.e(FolderListAdapter.class.getSimpleName(), "" + folderArrayList.size());
        for (int i = 0; i < folderArrayList.size(); i++) {
            View view = LayoutInflater.from(activity).inflate(R.layout.sub_folder_layout,
                    viewHolder.subLayout, false);
            TextView name = (TextView) view.findViewById(R.id.folder_list_sub_name);
            name.setText(folderArrayList.get(i).getName());
            view.setTag(i);
            viewHolder.subLayout.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    onItemClick.onItemClick(folderArrayList.get(position).getId(),
                            folderArrayList.get(position).getName(), allFolderList.get(post).getId(), allFolderList.get(post).getName());
                }
            });
        }
    }

    interface OnItemClick {
        void onItemClick(long folder_id, String folder_name, long main_folder_id, String main_folder_name);
    }

    private class ViewHolder {
        TextView folderNameText, subFolderNameText;
        LinearLayout mainLayout, subLayout;
    }

}
