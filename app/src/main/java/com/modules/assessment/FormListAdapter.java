package com.modules.assessment;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 09-08-2017
 * Last Modified on 13-12-2017
 */

public class FormListAdapter extends ArrayAdapter<Forms_> {

    private final List<Forms_> formsList;

    private final Activity activity;

    public FormListAdapter(Activity activity, List<Forms_> formsList) {
        super(activity, 0, formsList);
        this.formsList = formsList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return formsList.size();
    }

    @Override
    public Forms_ getItem(int position) {
        if (formsList != null && formsList.size() > 0) {
            return formsList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return formsList.get(position).getForm_id();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.consent_list_item_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.consent_list_item_name);
            viewHolder.dateText = (TextView) view.findViewById(R.id.consent_list_item_time);
            viewHolder.fileText = (TextView) view.findViewById(R.id.consent_list_item_file_name);

            viewHolder.icon = (ImageView) view.findViewById(R.id.consent_list_item_file_icon);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (formsList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(formsList.get(position).getSender()));
            viewHolder.fileText.setText(formsList.get(position).getForm_name());
            viewHolder.dateText.setText(getDate(formsList.get(position).getTimestamp()));

            viewHolder.icon.setImageResource(R.drawable.vi_sm_assesments);
            viewHolder.icon.setPadding(0, 0, 0, 0);
        }
        return view;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }


    private class ViewHolder {
        TextView nameText, dateText, fileText;
        ImageView icon;
    }
}
