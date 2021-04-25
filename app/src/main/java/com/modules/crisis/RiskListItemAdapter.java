package com.modules.crisis;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;

import java.util.List;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 07-09-2017
 *         Last Modified on 13-12-2017
 **/

class RiskListItemAdapter extends ArrayAdapter<Risk_> {

    private final List<Risk_> list;

    private final Context context;

    RiskListItemAdapter(Context context, List<Risk_> list) {
        super(context, 0, list);
        this.list = list;
        this.context = context;
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
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.titleText.setText(list.get(position).getDescription());

        return view;
    }

    private class ViewHolder {
        TextView titleText;
    }
}