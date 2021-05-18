package com.sagesurfer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.widget.AppCompatImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.icons.GetDrawerIcon;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.DrawerMenu_;
import com.sagesurfer.models.HomeMenu_;

import java.util.HashMap;
import java.util.List;

/**
 * @author Kailash Karankal
 */

/*
 *Adapter class for drawer items
 */

public class DrawerListAdapter extends BaseExpandableListAdapter {
    private final Context _context;
    private final List<DrawerMenu_> headerList;
    private final HashMap<String, List<HomeMenu_>> childList;
    private static final String TAG = "DrawerListAdapter";

    public DrawerListAdapter(Context context,
                             List<DrawerMenu_> headerList,
                             HashMap<String, List<HomeMenu_>> childList) {
        this._context = context;
        this.headerList = headerList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return headerList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return headerList.get(groupPosition).getSubMenu().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headerList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return headerList.get(groupPosition).getSubMenu().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return headerList.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return headerList.get(groupPosition).getSubMenu().get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;
        View view = convertView;
        if (view == null) {
            headerViewHolder = new HeaderViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.drawer_list_group_layout, null);
            headerViewHolder.titleText = view.findViewById(R.id.drawer_list_group_title);
            headerViewHolder.counterText = view.findViewById(R.id.drawer_list_group_counter);
            headerViewHolder.icon = view.findViewById(R.id.drawer_list_group_icon);
            headerViewHolder.indicator = view.findViewById(R.id.drawer_list_group_indicator);
            headerViewHolder.view_separator = view.findViewById(R.id.view_separator);
            view.setTag(headerViewHolder);
            String menu[] = headerList.get(groupPosition).getMenu().split(" ");
            if (!menu[0].equalsIgnoreCase("Line")) {

                Log.i(TAG, "getGroupView: hide seperator... menu -> " + headerList.get(groupPosition).getMenu());
                headerViewHolder.view_separator.setVisibility(View.GONE);
                headerViewHolder.titleText.setText(ChangeCase.toTitleCase(headerList.get(groupPosition).getMenu()));
                //overriding icon color
                int color = Color.parseColor("#333333");
                headerViewHolder.icon.setColorFilter(color);
                headerViewHolder.icon.setImageResource(GetDrawerIcon.get(headerList.get(groupPosition).getId()));

                /* if (headerList.size() > 2) {*/
                if (headerList.get(groupPosition).getSubMenu().size() <= 0 && headerList != null) {
                    headerViewHolder.indicator.setVisibility(View.GONE);
                } else {
                    headerViewHolder.indicator.setVisibility(View.VISIBLE);
                }

                if (headerList.get(groupPosition).isSelected()) {
                    headerViewHolder.indicator.setImageResource(R.drawable.ic_solid_arrow_d);
                } else {
                    headerViewHolder.indicator.setImageResource(R.drawable.ic_solid_arrow_r);
                }
                Log.i(TAG, "getGroupView: id" + headerList.get(groupPosition).getId());
                Log.i(TAG, "getGroupView: Menu" + headerList.get(groupPosition).getMenu());
                if (headerList.get(groupPosition).getCounter() > 0) {
                    headerViewHolder.counterText.setVisibility(View.VISIBLE);
                    headerViewHolder.counterText.setText(GetCounters.convertCounter(headerList.get(groupPosition).getCounter()));
                } else {
                    headerViewHolder.counterText.setVisibility(View.GONE);
                }
            } else {
                Log.i(TAG, "getGroupView: Show seperator...");
                headerViewHolder.view_separator.setVisibility(View.VISIBLE);
                headerViewHolder.titleText.setVisibility(View.GONE);
                headerViewHolder.icon.setVisibility(View.GONE);
                headerViewHolder.counterText.setVisibility(View.GONE);
                headerViewHolder.indicator.setVisibility(View.GONE);
            }
        } else {
            headerViewHolder = (HeaderViewHolder) view.getTag();
            String menu[] = headerList.get(groupPosition).getMenu().split(" ");
            if (!menu[0].equalsIgnoreCase("Line")) {
                Log.i(TAG, "getGroupView: hide seperator... menu -> " + headerList.get(groupPosition).getMenu());
                headerViewHolder.view_separator.setVisibility(View.GONE);
                headerViewHolder.titleText.setText(ChangeCase.toTitleCase(headerList.get(groupPosition).getMenu()));
                //overriding icon color
                int color = Color.parseColor("#333333");
                headerViewHolder.icon.setColorFilter(color);
                headerViewHolder.icon.setImageResource(GetDrawerIcon.get(headerList.get(groupPosition).getId()));

                /* if (headerList.size() > 2) {*/
                if (headerList.get(groupPosition).getSubMenu().size() <= 0 && headerList != null) {
                    headerViewHolder.indicator.setVisibility(View.GONE);
                } else {
                    headerViewHolder.indicator.setVisibility(View.VISIBLE);
                }

                if (headerList.get(groupPosition).isSelected()) {
                    headerViewHolder.indicator.setImageResource(R.drawable.ic_solid_arrow_d);
                } else {
                    headerViewHolder.indicator.setImageResource(R.drawable.ic_solid_arrow_r);
                }
                Log.i(TAG, "getGroupView: id" + headerList.get(groupPosition).getId());
                Log.i(TAG, "getGroupView: Menu" + headerList.get(groupPosition).getMenu());
                if (headerList.get(groupPosition).getCounter() > 0) {
                    headerViewHolder.counterText.setVisibility(View.VISIBLE);
                    headerViewHolder.counterText.setText(GetCounters.convertCounter(headerList.get(groupPosition).getCounter()));
                } else {
                    headerViewHolder.counterText.setVisibility(View.GONE);
                }

            } else {
                Log.i(TAG, "getGroupView: Show seperator...");
                headerViewHolder.view_separator.setVisibility(View.VISIBLE);
                headerViewHolder.titleText.setVisibility(View.GONE);
                headerViewHolder.icon.setVisibility(View.GONE);
                headerViewHolder.counterText.setVisibility(View.GONE);
                headerViewHolder.indicator.setVisibility(View.GONE);
            }
        }
        return view;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        View view = convertView;
        if (view == null) {
            childViewHolder = new ChildViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.drawer_list_child_layout, null);
            childViewHolder.titleText = (TextView) view.findViewById(R.id.drawer_list_child_title);
            childViewHolder.counterText = (TextView) view.findViewById(R.id.drawer_list_child_counter);
            childViewHolder.view_separator = (View) view.findViewById(R.id.view_separator);

            view.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) view.getTag();
        }
        Log.i("DrawerListAdapter TAG", "getChildView: ");

        childViewHolder.titleText.setText(headerList.get(groupPosition).getSubMenu().get(childPosition).getMenu());
        int count = childList.get(headerList.get(groupPosition).getMenu()).get(childPosition).getCounter();
        if (count > 0) {
            childViewHolder.counterText.setVisibility(View.VISIBLE);
            childViewHolder.counterText.setText(GetCounters.convertCounter(count));
        } else {
            childViewHolder.counterText.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class HeaderViewHolder {
        TextView titleText, counterText;
        AppCompatImageView icon;
        ImageView indicator;
        View view_separator;
    }

    private class ChildViewHolder {
        TextView titleText, counterText;
        View view_separator;
    }
}
