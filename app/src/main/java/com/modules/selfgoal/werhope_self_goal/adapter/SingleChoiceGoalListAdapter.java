package com.modules.selfgoal.werhope_self_goal.adapter;

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
import com.sagesurfer.models.Goal_;

import java.util.ArrayList;
import java.util.Locale;

public class SingleChoiceGoalListAdapter extends ArrayAdapter<Goal_> {
    private final ArrayList<Goal_> categoryArrayList, list;
    private final Context context;

    public SingleChoiceGoalListAdapter(Context context, ArrayList<Goal_> categoryArrayList) {
        super(context, 0, categoryArrayList);
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.list = new ArrayList<>();
        this.list.addAll(categoryArrayList);
    }

    @Nullable
    @Override
    public Goal_ getItem(int position) {
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
            for (Goal_ choices_ : list) {
                String name = choices_.getName();
                if (name.toLowerCase(Locale.getDefault()).contains(query)) {
                    categoryArrayList.add(choices_);
                }
            }
        }
        if (categoryArrayList.size() <= 0) {
            Goal_ choices_ = new Goal_();
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
