package com.modules.consent;

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
 **/

class ConsentFileListAdapter extends ArrayAdapter<ConsentFile_> {

    private final List<ConsentFile_> consentList;

    private final Activity activity;

    ConsentFileListAdapter(Activity activity, List<ConsentFile_> consentList) {
        super(activity, 0, consentList);
        this.consentList = consentList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return consentList.size();
    }

    @Override
    public ConsentFile_ getItem(int position) {
        if (consentList != null && consentList.size() > 0) {
            return consentList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return consentList.get(position).getId();
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

            viewHolder.icon = (ImageView) view.findViewById(R.id.consent_list_item_file_icon_bg);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (consentList.get(position).getStatus() == 1) {
            viewHolder.nameText.setText(ChangeCase.toTitleCase(consentList.get(position).getYouth_name()));
            viewHolder.fileText.setText(consentList.get(position).getRealname());
            //viewHolder.dateText.setText(GetTime.wallTime(consentList.get(position).getShared_date()));
            viewHolder.dateText.setText(getDate(consentList.get(position).getShared_date()));

            viewHolder.icon.setImageResource(R.drawable.primary_circle);
            viewHolder.icon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.color_pdf));

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
