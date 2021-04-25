package com.modules.assignment.werhope.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.models.Choices_;

import java.util.ArrayList;
import java.util.Locale;

public class SingleChoiceAssignmentChoiceAdapter extends ArrayAdapter<Choices_> {

    private final ArrayList<Choices_> categoryArrayList, list;
    private final Context context;
    private String action;

    public SingleChoiceAssignmentChoiceAdapter(Context context, String action, ArrayList<Choices_> categoryArrayList) {
        super(context, 0, categoryArrayList);
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.action = action;
        this.list = new ArrayList<>();
        this.list.addAll(categoryArrayList);
    }

    @Nullable
    @Override
    public Choices_ getItem(int position) {
        return categoryArrayList.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.simple_item_layout, parent, false);
            holder = new ViewHolder();

            holder.titleText = (TextView) view.findViewById(R.id.simple_item_title);
            holder.arrow = (AppCompatImageView) view.findViewById(R.id.simple_item_arrow);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (categoryArrayList.get(position).getStatus() == 1) {
            holder.titleText.setText(categoryArrayList.get(position).getName());

            if (action.equalsIgnoreCase(Actions_.GET_STUDENT_UNDER_SCHOOL)) {
                holder.titleText.setText(categoryArrayList.get(position).getFirstname() + " " + categoryArrayList.get(position).getLastname());
            } else if (action.equalsIgnoreCase(Actions_.GET_COACH_LIST)) {
                holder.titleText.setText(categoryArrayList.get(position).getName() + " (" + categoryArrayList.get(position).getTotal_assigned()+") ");
            } else {
                holder.titleText.setText(categoryArrayList.get(position).getName());
            }

            view.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.screen_background));
            holder.titleText.setText(context.getResources().getString(R.string.no_record_found));
        }
        return view;
    }

    // filter list content as per search query entered by users
    public void filter(String query) {
        categoryArrayList.clear();
        if (query == null) {
            categoryArrayList.addAll(list);
            notifyDataSetChanged();
            return;
        }
        query = query.toLowerCase(Locale.getDefault());
        if (query.length() <= 0) {
            categoryArrayList.addAll(list);
        } else {
            for (Choices_ choices_ : list) {
                String name = choices_.getName();
                if (name.toLowerCase(Locale.getDefault()).contains(query)) {
                    categoryArrayList.add(choices_);
                }
            }
        }
        if (categoryArrayList.size() <= 0) {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(0);
            categoryArrayList.add(choices_);
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView titleText;
        AppCompatImageView arrow;
    }
}
