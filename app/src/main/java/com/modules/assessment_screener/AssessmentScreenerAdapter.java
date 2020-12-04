package com.modules.assessment_screener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;

import java.util.List;

public class AssessmentScreenerAdapter extends ArrayAdapter<AssessmentScreener> {
    private List<AssessmentScreener> assessmentScreenerList;
    private Context mContext;

    public AssessmentScreenerAdapter(Activity activity, List<AssessmentScreener> assessmentScreenerList1) {
        super(activity, 0, assessmentScreenerList1);
        this.assessmentScreenerList = assessmentScreenerList1;
        this.mContext = activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return assessmentScreenerList.size();
    }

    @Override
    public AssessmentScreener getItem(int position) {
        if (assessmentScreenerList != null && assessmentScreenerList.size() > 0) {
            return assessmentScreenerList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return assessmentScreenerList.get(position).getRecord_id();
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.assessment_screener_list_item, parent, false);

            viewHolder.formName = (TextView) view.findViewById(R.id.form_name);
            viewHolder.senderName = (TextView) view.findViewById(R.id.sender_name);
            viewHolder.date = (TextView) view.findViewById(R.id.date);
            viewHolder.invitationImage = (ImageView) view.findViewById(R.id.invitation_item_image);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final AssessmentScreener assessmentScreener = assessmentScreenerList.get(position);

        if (assessmentScreener.getStatus() == 1) {
            viewHolder.formName.setText(ChangeCase.toTitleCase(assessmentScreener.getForm_name()));
            viewHolder.date.setText(GetTime.getDateTime(assessmentScreener.getAdded_date()));
            viewHolder.senderName.setText(ChangeCase.toTitleCase(assessmentScreener.getSender()));
        }
        return view;
    }


    private class ViewHolder {
        TextView formName, senderName, date;
        ImageView invitationImage;
    }
}
