package com.modules.assessment;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;


import java.util.List;

/**
 * Created by Kailash Karankal on 6/6/2019.
 */
public class SubmittedFormListAdapter extends ArrayAdapter<Forms_> {

    private final List<Forms_> formsList;

    private final Activity activity;

    public SubmittedFormListAdapter(Activity activity, List<Forms_> formsList) {
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
        SubmittedFormListAdapter.ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new SubmittedFormListAdapter.ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.submitted_form_item_layout, parent, false);

            viewHolder.questionText = (TextView) view.findViewById(R.id.question);
            viewHolder.ansText = (TextView) view.findViewById(R.id.answer);

            view.setTag(viewHolder);
        } else {
            viewHolder = (SubmittedFormListAdapter.ViewHolder) view.getTag();
        }

        if (formsList.get(position).getStatus() == 1) {
            viewHolder.questionText.setText(ChangeCase.toTitleCase(formsList.get(position).getQuestion()));
            viewHolder.ansText.setText(formsList.get(position).getAnswer());

        }

        return view;
    }

    private class ViewHolder {
        TextView questionText, ansText;
    }
}
