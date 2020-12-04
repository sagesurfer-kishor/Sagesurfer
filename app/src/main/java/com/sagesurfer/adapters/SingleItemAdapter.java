package com.sagesurfer.adapters;

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

import java.util.List;

/**
 * @author Girish Mane <girish@sagesurfer.com>
 * Created on 04-03-2016
 * Last Modified on 15-12-2017
 */

public class SingleItemAdapter extends ArrayAdapter<String> {

    private final List<String> list;
    private final boolean isArrowVisible;
    private final Context context;

    public SingleItemAdapter(Context context, List<String> list, boolean isArrowVisible) {
        super(context, 0, list);
        this.list = list;
        this.isArrowVisible = isArrowVisible;
        this.context = context;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return list.get(position);
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

        holder.titleText.setText(list.get(position));
        if (isArrowVisible) {
            holder.arrow.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private class ViewHolder {
        TextView titleText;
        AppCompatImageView arrow;
    }
}