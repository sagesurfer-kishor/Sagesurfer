package com.modules.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/3/2018
 *         Last Modified on 4/3/2018
 */

class FaqListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final FaqInterface faqInterface;

    private final ArrayList<Question> groups;
    private final ArrayList<Question> groupsList;

    FaqListAdapter(Context context, ArrayList<Question> groups, FaqInterface faqInterface) {
        this.context = context;
        this.groups = groups;
        this.faqInterface = faqInterface;
        groupsList = new ArrayList<>();
        groupsList.addAll(groups);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Answer> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Answer child = (Answer) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.support_faq_list_item_layout,null);
        }

        TextView childText = (TextView) convertView.findViewById(R.id.faq_list_item_child);
        childText.setVisibility(View.VISIBLE);
        TextView parentText = (TextView) convertView.findViewById(R.id.faq_list_item_head);
        parentText.setVisibility(View.GONE);
        childText.setText(child.getName());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Answer> chList = groups.get(groupPosition).getItems();
        return chList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Question group = (Question) getGroup(groupPosition);
        if (convertView == null) {
            @SuppressWarnings("static-access")
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.support_faq_list_item_layout, null);
        }
        TextView childText = (TextView) convertView.findViewById(R.id.faq_list_item_child);
        childText.setVisibility(View.GONE);
        TextView parentText = (TextView) convertView.findViewById(R.id.faq_list_item_head);
        parentText.setVisibility(View.VISIBLE);
        parentText.setText(group.getName());

        if (isExpanded) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.screen_background));
            parentText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
            parentText.setTextColor(context.getResources().getColor(R.color.text_color_primary));
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // filter list content based on search query
    void filterList(String searchString) {
        groups.clear();
        if (searchString == null) {
            groups.addAll(groupsList);
            notifyDataSetChanged();
            return;
        }
        searchString = searchString.toLowerCase(Locale.getDefault());
        if (searchString.length() <= 0) {
            groups.addAll(groupsList);
        } else {
            for (Question question : groupsList) {
                String question_string = question.getName();
                if (question_string.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    groups.add(question);
                }
            }
        }
        if (groups.size() <= 0) {
            faqInterface.noData(true);
        } else {
            faqInterface.noData(false);
        }
        notifyDataSetChanged();
    }

    interface FaqInterface {
        void noData(boolean isData);
    }
}