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
 * @author Girish Mane <girish@sagesurfer.com>
 *         Created on 07-09-2016
 *         Last Modified on 07-09-2016
 */

class SupportPersonListItemAdapter extends ArrayAdapter<SupportPerson_> {

    private final List<SupportPerson_> list;

    private final Context context;

    SupportPersonListItemAdapter(Context context, List<SupportPerson_> list) {
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
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.support_person_item_layout, parent, false);
            holder = new ViewHolder();

            holder.nameText = (TextView) view.findViewById(R.id.support_person_item_name);
            holder.relationText = (TextView) view.findViewById(R.id.support_person_item_relation);
            holder.numberText = (TextView) view.findViewById(R.id.support_person_item_number);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.nameText.setText(list.get(position).getName());
        holder.relationText.setText(list.get(position).getRelationship());
        holder.numberText.setText(list.get(position).getPhone());

        return view;
    }

    private class ViewHolder {
        TextView nameText, relationText, numberText;
    }
}