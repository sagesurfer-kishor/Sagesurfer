package com.sagesurfer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.selfcare.FilterStateExpandableListView;
import com.modules.selfcare.ListItem;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.State_;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by zerones on 04-Oct-17.
 */

public class FilterCountryExpandableListAdapter extends BaseExpandableListAdapter implements FilterStateExpandableListAdapter.FilterStateExpandableListAdapterListener{

    private LayoutInflater inflater;
    ArrayList<ListItem> countryList;
    ArrayList<LinkedHashMap<String, ArrayList<State_>>> stateArrayList;
    //ArrayList<LinkedHashMap<String, List<City_>>> cityArrayList;
    private int lastExpandedPosition = -1;
    private Context context;
    static FilterStateExpandableListView stateExpandableListView;
    static ArrayList<Integer> idsExpand = new ArrayList<Integer>();

    public FilterCountryExpandableListAdapter(Context context, ArrayList<ListItem> countryList,
                                              ArrayList<LinkedHashMap<String, ArrayList<State_>>> stateArrayList
                                              /*ArrayList<LinkedHashMap<String, List<City_>>> cityArrayList*/) {
        this.context = context;
        this.countryList = countryList;
        this.stateArrayList = stateArrayList;
        //this.cityArrayList = cityArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getGroupCount() {
        return countryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return i;
    }

    @Override
    public Object getChild(int i, int i1) {
        return i1;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        final ViewHolderCountry viewHolderCountry;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.multiselect_country_list_item, null);
            viewHolderCountry = new ViewHolderCountry();

            viewHolderCountry.textVieCountryName = convertView.findViewById(R.id.multi_select_text);
            viewHolderCountry.linearLayoutCheckCountry = convertView.findViewById(R.id.linearlayout_checkbox);
            viewHolderCountry.checkBoxCountry = convertView.findViewById(R.id.multi_select_check_box);
            convertView.setTag(viewHolderCountry);
        } else {
            viewHolderCountry = (ViewHolderCountry) convertView.getTag();
        }

        if (countryList.get(groupPosition).getSelected()) {
            viewHolderCountry.checkBoxCountry.setChecked(true);
            notifyDataSetChanged();

        } else {
            viewHolderCountry.checkBoxCountry.setChecked(false);
            notifyDataSetChanged();
        }

        viewHolderCountry.linearLayoutCheckCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = countryList.get(groupPosition).getSelected();
                if (checked) {
                    viewHolderCountry.checkBoxCountry.setChecked(false);
                    countryList.get(groupPosition).setSelected(false);
                    deselectStateCity(groupPosition);
                } else {
                    viewHolderCountry.checkBoxCountry.setChecked(true);
                    countryList.get(groupPosition).setSelected(true);
                }
                //FilterChoiceDialogFragment.setExpandedGroup();
                notifyDataSetChanged();
                //FilterChoiceDialogFragment.getExpandedIds();
            }
        });

        viewHolderCountry.checkBoxCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = countryList.get(groupPosition).getSelected();
                if (checked) {
                    viewHolderCountry.checkBoxCountry.setChecked(false);
                    countryList.get(groupPosition).setSelected(false);
                    deselectStateCity(groupPosition);
                } else {
                    viewHolderCountry.checkBoxCountry.setChecked(true);
                    countryList.get(groupPosition).setSelected(true);
                }
                //FilterChoiceDialogFragment.setExpandedGroup();
                notifyDataSetChanged();
                //FilterChoiceDialogFragment.getExpandedIds();
            }
        });

        viewHolderCountry.textVieCountryName.setText(countryList.get(groupPosition).getName());

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        stateExpandableListView = new FilterStateExpandableListView(context);

        LinkedHashMap<String, ArrayList<State_>> stateLinkedHashMap = stateArrayList.get(groupPosition);

        final ArrayList<State_> stateList = new ArrayList<>();
        for(String key : stateLinkedHashMap.keySet())
        {
            for(int i = 0; i < stateLinkedHashMap.get(key).size(); i++) {
                if(stateLinkedHashMap.get(key).get(i).getStatus() == 1) {
                    stateList.add(stateLinkedHashMap.get(key).get(i));
                }
            }
        }

        if(stateList.size() > 0) {
            stateExpandableListView.setAdapter(new FilterStateExpandableListAdapter(context, groupPosition, stateList, countryList, this));
            stateExpandableListView.setGroupIndicator(null);
        }

        /*secondLevelELV.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
//               if(groupPosition != previousGroup)
//                    secondLevelELV.collapseGroup(previousGroup);
//                previousGroup = groupPosition;
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    secondLevelELV.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });*/
        stateExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    stateExpandableListView.collapseGroup(lastExpandedPosition);
                }
                previousGroup = groupPosition;
            }
        });
        return stateExpandableListView;
    }

    @Override
    public void onStateSelected(int position) {
        boolean checked = countryList.get(position).getSelected();
        if (checked) {
            //To deselect state of respective city if other city for that state is not selected
            LinkedHashMap<String, ArrayList<State_>> stateLinkedHashMap = stateArrayList.get(position);

            final ArrayList<State_> stateList = new ArrayList<>();
            for(String key : stateLinkedHashMap.keySet())
            {
                for(int i = 0; i < stateLinkedHashMap.get(key).size(); i++) {
                    if(stateLinkedHashMap.get(key).get(i).getStatus() == 1) {
                        stateList.add(stateLinkedHashMap.get(key).get(i));
                    }
                }
            }

            int stateSelectedSize = 0;
            int stateSize = stateList.size();
            for(int i = 0; i < stateSize; i++) {
                if(stateList.get(i).isSelected()){
                    stateSelectedSize++;
                }
            }
            if(stateSelectedSize == 0) {
                countryList.get(position).setSelected(false);
            }
        } else {
            countryList.get(position).setSelected(true);
        }
        notifyDataSetChanged();
    }

    public void deselectStateCity(int position) {
        LinkedHashMap<String, ArrayList<State_>> stateLinkedHashMap = stateArrayList.get(position);

        final ArrayList<State_> stateList = new ArrayList<>();
        for(String key : stateLinkedHashMap.keySet())
        {
            for(int i = 0; i < stateLinkedHashMap.get(key).size(); i++) {
                if(stateLinkedHashMap.get(key).get(i).getStatus() == 1) {
                    stateLinkedHashMap.get(key).get(i).setIs_selected(false);
                    for(int j = 0; j < stateLinkedHashMap.get(key).get(i).getCity().size(); j++) {
                        stateLinkedHashMap.get(key).get(i).getCity().get(j).setIs_selected(false);
                    }
                    //stateList.add(stateLinkedHashMap.get(key).get(i));
                }
            }
        }
        notifyDataSetChanged();

        /*int stateSelectedSize = 0;
        int stateSize = stateList.size();
        for(int i = 0; i < stateSize; i++) {
            if(stateList.get(i).isSelected()){
                stateSelectedSize++;
            }
        }*/
    }

    public static void setExpandedGroup() {
        if (stateExpandableListView != null
                && stateExpandableListView.getExpandableListAdapter() != null) {
            final int groupCount = stateExpandableListView.getExpandableListAdapter().getGroupCount();
            for (int i = 0; i < groupCount; i++) {
                if (stateExpandableListView.isGroupExpanded(i)) {
                    idsExpand.add(i);
                }
            }
        }
    }

    public static void getExpandedIds() {
        if (idsExpand != null) {
            final int groupCount = idsExpand.size();
            for (int i = 0; i < groupCount; i++) {
                if (stateExpandableListView != null) {
                    try {
                        stateExpandableListView.expandGroup(idsExpand.get(i));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class ViewHolderCountry {
        TextView textVieCountryName;
        LinearLayout linearLayoutCheckCountry;
        CheckBox checkBoxCountry;
    }
}