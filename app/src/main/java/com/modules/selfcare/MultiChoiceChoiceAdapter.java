package com.modules.selfcare;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.Choices_;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Monika on 12/27/2018.
 */

public class MultiChoiceChoiceAdapter extends ArrayAdapter<Choices_> {

    private final List<Choices_> categoryArrayList;
    private final List<Choices_> list;
    private final Context context;

    public MultiChoiceChoiceAdapter(Context context, List<Choices_> categoryArrayList) {
        super(context, 0, categoryArrayList);
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.list = new ArrayList<>();
        this.list.addAll(this.categoryArrayList);
    }

    @Override
    public int getCount() {
        return categoryArrayList.size();
    }

    @Override
    public Choices_ getItem(int position) {
        if (categoryArrayList != null && categoryArrayList.size() > 0) {
            return categoryArrayList.get(position);
        }
        return null;
    }

    private int getStatus(int position) {
        if (categoryArrayList != null && categoryArrayList.size() > 0) {
            return categoryArrayList.get(position).getStatus();
        }
        return 0;
    }

    private boolean getSelected(int position) {
        return categoryArrayList != null && categoryArrayList.size() > 0 && categoryArrayList.get(position).getIs_selected().equalsIgnoreCase("1");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        //if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.simple_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.simple_item_title);
            viewHolder.checkIcon = (AppCompatImageView) view.findViewById(R.id.simple_item_check);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_item);

            view.setTag(viewHolder);
        /*} else {
            viewHolder = (ViewHolder) view.getTag();
        }*/
        if (categoryArrayList.get(position).getStatus() == 0) {
            view.setBackgroundColor(context.getResources().getColor(R.color.screen_background));
            viewHolder.nameText.setText(context.getResources().getString(R.string.no_record_found));
            return view;
        }
        if (categoryArrayList.get(position).getStatus() == 1) {
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
            viewHolder.mainLayout.setTag(position);
            viewHolder.checkIcon.setVisibility(View.VISIBLE);

            viewHolder.nameText.setText(categoryArrayList.get(position).getName());

            if(categoryArrayList.get(position).getIs_selected() == null) {
                categoryArrayList.get(position).setIs_selected("0");
            }
            if (categoryArrayList.get(position).getIs_selected().equalsIgnoreCase("1")) {
                viewHolder.checkIcon.setImageResource(R.drawable.vi_check_blue);
            } else {
                viewHolder.checkIcon.setImageResource(0);
            }
        }
        //viewHolder.mainLayout.setOnClickListener(onClick);

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                if (getStatus(position) == 1) {
                    if (getSelected(position)) {
                        categoryArrayList.get(position).setIs_selected("0");
                        viewHolder.checkIcon.setImageResource(0);
                    } else {
                        categoryArrayList.get(position).setIs_selected("1");
                        viewHolder.checkIcon.setImageResource(R.drawable.vi_check_blue);
                    }
                }
            }
        });
        return view;
    }

    private class ViewHolder {
        AppCompatImageView checkIcon;
        TextView nameText;
        RelativeLayout mainLayout;
    }

    public void filterChoice(String searchString) {
        categoryArrayList.clear();
        if (searchString == null) {
            categoryArrayList.addAll(list);
            notifyDataSetChanged();
            return;
        }
        searchString = searchString.toLowerCase(Locale.getDefault());
        if (searchString.length() <= 0) {
            categoryArrayList.addAll(list);
        } else {
            for (Choices_ choice_ : list) {
                String name = choice_.getName();
                if (name.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    categoryArrayList.add(choice_);
                }
            }
        }
        if (categoryArrayList.size() <= 0) {
            Choices_ choice_ = new Choices_();
            choice_.setStatus(0);
            categoryArrayList.add(choice_);
        }
        notifyDataSetChanged();
    }
}
