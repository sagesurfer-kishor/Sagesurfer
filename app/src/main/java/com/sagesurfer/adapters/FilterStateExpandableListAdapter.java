package com.sagesurfer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.selfcare.ListItem;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.State_;

import java.util.ArrayList;

public class FilterStateExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;
    private Context context;
    ArrayList<State_> stateList;
    int countryPosition;
    ArrayList<ListItem> countryList;
    public final FilterStateExpandableListAdapterListener filterStateExpandableListAdapterListener;

    public FilterStateExpandableListAdapter(Context context, int groupPosition, ArrayList<State_> stateList,
                                            ArrayList<ListItem> countryList, FilterStateExpandableListAdapterListener filterStateExpandableListAdapterListener) {
        this.context = context;
        this.stateList = stateList;
        this.countryPosition = groupPosition;
        this.countryList = countryList;
        this.filterStateExpandableListAdapterListener = filterStateExpandableListAdapterListener;
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

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        final ViewHolderState viewHolderState;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.multiselect_state_list_item, null);
            viewHolderState = new ViewHolderState();

            viewHolderState.textVieStateName = convertView.findViewById(R.id.multi_select_text);
            viewHolderState.linearLayoutCheckState = convertView.findViewById(R.id.linearlayout_checkbox);
            viewHolderState.checkBoxState = convertView.findViewById(R.id.multi_select_check_box);
            convertView.setTag(viewHolderState);
        } else {
            viewHolderState = (ViewHolderState) convertView.getTag();
        }

        if (stateList.get(groupPosition).isSelected()) {
            viewHolderState.checkBoxState.setChecked(true);
            notifyDataSetChanged();

        } else {
            viewHolderState.checkBoxState.setChecked(false);
            notifyDataSetChanged();
        }

        viewHolderState.linearLayoutCheckState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = stateList.get(groupPosition).isSelected();
                if (checked) {
                    viewHolderState.checkBoxState.setChecked(false);
                    stateList.get(groupPosition).setIs_selected(false);

                    //deselect all the cities(if any), for respective state deselection
                    int citySize = stateList.get(groupPosition).getCity().size();
                    for(int i = 0; i < citySize; i++) {
                        if(stateList.get(groupPosition).getCity().get(i).isSelected()){
                            stateList.get(groupPosition).getCity().get(i).setIs_selected(false);
                        }
                    }
                } else {
                    viewHolderState.checkBoxState.setChecked(true);
                    stateList.get(groupPosition).setIs_selected(true);
                    //countryList.get(countryPosition).setSelected(true);
                }
                filterStateExpandableListAdapterListener.onStateSelected(countryPosition);
                notifyDataSetChanged();
            }
        });

        viewHolderState.checkBoxState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = stateList.get(groupPosition).isSelected();
                if (checked) {
                    viewHolderState.checkBoxState.setChecked(false);
                    stateList.get(groupPosition).setIs_selected(false);

                    //deselect all the cities(if any), for respective state deselection
                    int citySize = stateList.get(groupPosition).getCity().size();
                    for(int i = 0; i < citySize; i++) {
                        if(stateList.get(groupPosition).getCity().get(i).isSelected()){
                            stateList.get(groupPosition).getCity().get(i).setIs_selected(false);
                        }
                    }
                } else {
                    viewHolderState.checkBoxState.setChecked(true);
                    stateList.get(groupPosition).setIs_selected(true);
                    //countryList.get(countryPosition).setSelected(true);
                }
                filterStateExpandableListAdapterListener.onStateSelected(countryPosition);
                notifyDataSetChanged();
            }
        });

        viewHolderState.textVieStateName.setText(stateList.get(groupPosition).getName());

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean b, View convertView, ViewGroup viewGroup) {
        if(stateList.get(groupPosition).getCity().get(childPosition).getStatus() == 1) {
            final ViewHolderCity viewHolderCity;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.multiselect_city_list_item, null);
                viewHolderCity = new ViewHolderCity();

                viewHolderCity.linearLayoutCity = convertView.findViewById(R.id.linearlayout_city);
                viewHolderCity.textVieCityName = convertView.findViewById(R.id.multi_select_text);
                viewHolderCity.linearLayoutCheckCity = convertView.findViewById(R.id.linearlayout_checkbox);
                viewHolderCity.checkBoxCity = convertView.findViewById(R.id.multi_select_check_box);
                convertView.setTag(viewHolderCity);
            } else {
                viewHolderCity = (ViewHolderCity) convertView.getTag();
            }

        //if(stateList.get(groupPosition).getCity().get(childPosition).getStatus() == 1) {
            boolean checked = stateList.get(groupPosition).getCity().get(childPosition).isSelected();
            if (checked) {
                viewHolderCity.checkBoxCity.setChecked(true);
                notifyDataSetChanged();
            } else {
                viewHolderCity.checkBoxCity.setChecked(false);
                notifyDataSetChanged();
            }

            viewHolderCity.linearLayoutCheckCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = stateList.get(groupPosition).getCity().get(childPosition).isSelected();
                    /*if (checked) {
                        viewHolderCity.checkBoxCity.setChecked(false);
                        stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(false);
                    } else {
                        viewHolderCity.checkBoxCity.setChecked(true);
                        stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(true);
                        stateList.get(groupPosition).setIs_selected(true);
                    }*/
                    if (checked) {
                        viewHolderCity.checkBoxCity.setChecked(false);
                        stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(false);
                        //To deselect state of respective city if other city for that state is not selected
                        int citySelectedSize = 0;
                        int citySize = stateList.get(groupPosition).getCity().size();
                        for(int i = 0; i < citySize; i++) {
                            if(stateList.get(groupPosition).getCity().get(i).isSelected()){
                                citySelectedSize++;
                            }
                        }
                        /*if(citySelectedSize == 0) {
                            stateList.get(groupPosition).setIs_selected(false);
                        }*/
                    } else {
                        viewHolderCity.checkBoxCity.setChecked(true);
                        stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(true);
                        stateList.get(groupPosition).setIs_selected(true); //select state for the respective city
                    }
                    filterStateExpandableListAdapterListener.onStateSelected(countryPosition);
                    notifyDataSetChanged();
                }
            });

            viewHolderCity.checkBoxCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = stateList.get(groupPosition).getCity().get(childPosition).isSelected();
                    if (checked) {
                        viewHolderCity.checkBoxCity.setChecked(false);
                        stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(false);
                        //To deselect state of respective city if other city for that state is not selected
                        int citySelectedSize = 0;
                        int citySize = stateList.get(groupPosition).getCity().size();
                        for(int i = 0; i < citySize; i++) {
                            if(stateList.get(groupPosition).getCity().get(i).isSelected()){
                                citySelectedSize++;
                            }
                        }
                        /*if(citySelectedSize == 0) {
                            stateList.get(groupPosition).setIs_selected(false);
                        }*/
                    } else {
                        viewHolderCity.checkBoxCity.setChecked(true);
                        stateList.get(groupPosition).getCity().get(childPosition).setIs_selected(true);
                        stateList.get(groupPosition).setIs_selected(true); //select state for the respective city
                    }
                    filterStateExpandableListAdapterListener.onStateSelected(countryPosition);
                    notifyDataSetChanged();
                }
            });

            viewHolderCity.textVieCityName.setText(stateList.get(groupPosition).getCity().get(childPosition).getName());
        } else {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.multiselect_city_blankrow, null);
            }
        }
        //}
        return convertView;

    }

    public interface FilterStateExpandableListAdapterListener {
        void onStateSelected(int position);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class ViewHolderState {
        TextView textVieStateName;
        LinearLayout linearLayoutCheckState;
        CheckBox checkBoxState;
    }

    private class ViewHolderCity {
        LinearLayout linearLayoutCity;
        TextView textVieCityName;
        LinearLayout linearLayoutCheckCity;
        CheckBox checkBoxCity;
    }
}