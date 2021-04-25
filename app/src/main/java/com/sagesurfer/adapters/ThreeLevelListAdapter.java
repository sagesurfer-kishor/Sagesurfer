package com.sagesurfer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.State_;

import java.util.List;

public class ThreeLevelListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;
    private Context context;
    List<State_> stateList;
    int groupPosition;

    public ThreeLevelListAdapter(Context context, int groupPosition, List<State_> stateList) {
        this.context = context;
        this.stateList = stateList;
        this.groupPosition = groupPosition;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getGroup(int groupPosition) {

        return stateList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return stateList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return stateList.get(groupPosition).getCity().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return stateList.get(groupPosition).getCity().size();
    }

    /*@Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }*/

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        final ViewHolderCountry viewHolderCountry;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.multiselect_state_list_item, null);
            viewHolderCountry = new ViewHolderCountry();

            viewHolderCountry.textVieCountryName = convertView.findViewById(R.id.multi_select_text);
            viewHolderCountry.linearLayoutCheckCountry = convertView.findViewById(R.id.linearlayout_checkbox);
            viewHolderCountry.checkBoxCountry = convertView.findViewById(R.id.multi_select_check_box);
            convertView.setTag(viewHolderCountry);
        } else {
            viewHolderCountry = (ViewHolderCountry) convertView.getTag();
        }

        if (stateList.get(groupPosition).isSelected()) {
            viewHolderCountry.checkBoxCountry.setChecked(true);
            notifyDataSetChanged();

        } else {
            viewHolderCountry.checkBoxCountry.setChecked(false);
            notifyDataSetChanged();
        }

        viewHolderCountry.linearLayoutCheckCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = stateList.get(groupPosition).isSelected();
                if (checked) {
                    viewHolderCountry.checkBoxCountry.setChecked(false);
                    //check.setImageResource(R.drawable.vi_self_goal_checkbox_deselect);
                    stateList.get(groupPosition).setIs_selected(false);
                } else {
                    viewHolderCountry.checkBoxCountry.setChecked(true);
                    //check.setImageResource(R.drawable.vi_self_goal_checkbox_select);
                    stateList.get(groupPosition).setIs_selected(true);
                }
                notifyDataSetChanged();
            }
        });

        viewHolderCountry.checkBoxCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = stateList.get(groupPosition).isSelected();
                if (checked) {
                    viewHolderCountry.checkBoxCountry.setChecked(false);
                    //check.setImageResource(R.drawable.vi_self_goal_checkbox_deselect);
                    stateList.get(groupPosition).setIs_selected(false);
                } else {
                    viewHolderCountry.checkBoxCountry.setChecked(true);
                    //check.setImageResource(R.drawable.vi_self_goal_checkbox_select);
                    stateList.get(groupPosition).setIs_selected(true);
                }
                notifyDataSetChanged();
            }
        });

        /*ConstantManager.childItems = childItems;
        ConstantManager.parentItems = parentItems;*/

        viewHolderCountry.textVieCountryName.setText(stateList.get(groupPosition).getName());

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        final ViewHolderCity viewHolderCity;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.multiselect_city_list_item, null);
            viewHolderCity = new ViewHolderCity();

            viewHolderCity.textVieCityName = convertView.findViewById(R.id.multi_select_text);
            viewHolderCity.linearLayoutCheckCity = convertView.findViewById(R.id.linearlayout_checkbox);
            viewHolderCity.checkBoxCity = convertView.findViewById(R.id.multi_select_check_box);
            //viewHolderState.ivCategory = convertView.findViewById(R.id.ivCategory);
            convertView.setTag(viewHolderCity);
        } else {
            viewHolderCity = (ViewHolderCity) convertView.getTag();
        }

        boolean checked = stateList.get(groupPosition).getCity().get(childPosition).isSelected();
        if (checked) {
            viewHolderCity.checkBoxCity.setChecked(true);
            //viewHolderCity.checkBoxCity.setImageResource(R.drawable.vi_self_goal_checkbox_select);
            notifyDataSetChanged();
        } else {
            viewHolderCity.checkBoxCity.setChecked(false);
            //viewHolderCity.checkBoxCity.setImageResource(R.drawable.vi_self_goal_checkbox_deselect);
            notifyDataSetChanged();
        }

        viewHolderCity.linearLayoutCheckCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = stateList.get(groupPosition).getCity().get(childPosition).isSelected();
                if (checked) {
                    viewHolderCity.checkBoxCity.setChecked(false);
                    //viewHolderCity.checkBoxCity.setImageResource(R.drawable.vi_self_goal_checkbox_deselect);
                    stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(false);
                } else {
                    viewHolderCity.checkBoxCity.setChecked(true);
                    //viewHolderCity.checkBoxCity.setImageResource(R.drawable.vi_self_goal_checkbox_select);
                    stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(true);
                }
                notifyDataSetChanged();
            }
        });

        viewHolderCity.checkBoxCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = stateList.get(groupPosition).getCity().get(childPosition).isSelected();
                if (checked) {
                    viewHolderCity.checkBoxCity.setChecked(false);
                    //viewHolderCity.checkBoxCity.setImageResource(R.drawable.vi_self_goal_checkbox_deselect);
                    stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(false);
                } else {
                    viewHolderCity.checkBoxCity.setChecked(true);
                    //viewHolderCity.checkBoxCity.setImageResource(R.drawable.vi_self_goal_checkbox_select);
                    stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(true);
                }
                notifyDataSetChanged();
            }
        });

        /*ConstantManager.childItems = childItems;
        ConstantManager.parentItems = parentItems;*/

        viewHolderCity.textVieCityName.setText(stateList.get(groupPosition).getCity().get(childPosition).getName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    /*@Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }*/

    private class ViewHolderCountry {
        TextView textVieCountryName;
        LinearLayout linearLayoutCheckCountry;
        CheckBox checkBoxCountry;
        //ImageView ivCategory;
    }

    private class ViewHolderCity {
        TextView textVieCityName;
        LinearLayout linearLayoutCheckCity;
        CheckBox checkBoxCity;
        //ImageView checkBoxCity;
        View viewDivider;
    }
}