package com.modules.journaling.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.journaling.activity.JournalDetailsActivity;
import com.modules.journaling.model.Journal_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.tasks.PerformJournalTask;
import com.storage.preferences.Preferences;

import java.util.List;

/**
 * Created by Kailash Karankal on 10/3/2019.
 */
public class JournalListAdapter extends ArrayAdapter<Journal_> {
    private final List<Journal_> journalArrayList;
    private final Context mContext;
    private final Activity activity;

    public JournalListAdapter(Activity activity, List<Journal_> journalArrayList) {
        super(activity, 0, journalArrayList);
        this.journalArrayList = journalArrayList;
        this.mContext = activity.getApplicationContext();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return journalArrayList.size();
    }

    @Override
    public Journal_ getItem(int position) {
        if (journalArrayList != null && journalArrayList.size() > 0) {
            return journalArrayList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return journalArrayList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.journal_list_item, parent, false);

            viewHolder.journalTitle = (TextView) view.findViewById(R.id.journal_title);
            viewHolder.lastUpdateDate = (TextView) view.findViewById(R.id.last_updated_date);
            viewHolder.subjectOfJournal = (TextView) view.findViewById(R.id.subject_of_journal_txt);
            viewHolder.journalDescription = (TextView) view.findViewById(R.id.journal_desc_txt);

            viewHolder.attachmentLayout = (LinearLayout) view.findViewById(R.id.journal_list_item_attachment_layout);

            viewHolder.journalTagImg = (ImageView) view.findViewById(R.id.journal_tag_img);
            viewHolder.journalLocationImg = (ImageView) view.findViewById(R.id.journal_location_img);
            viewHolder.journalPictureImg = (ImageView) view.findViewById(R.id.journal_picture_img);
            viewHolder.journalLinkImg = (ImageView) view.findViewById(R.id.journal_link_img);
            viewHolder.journalAttachImg = (ImageView) view.findViewById(R.id.journal_attachment_img);
            viewHolder.journalHeartImg = (ImageView) view.findViewById(R.id.vi_heart_img);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (journalArrayList.get(position).getStatus() == 1) {
            viewHolder.journalTitle.setText(ChangeCase.toTitleCase(journalArrayList.get(position).getTitle()));
            viewHolder.lastUpdateDate.setText(GetTime.getDateTime(journalArrayList.get(position).getDb_add_date()));
            viewHolder.subjectOfJournal.setText(ChangeCase.toTitleCase(journalArrayList.get(position).getSubject()));
            viewHolder.journalDescription.setText(ChangeCase.toTitleCase(journalArrayList.get(position).getDescription()));

            if (journalArrayList.get(position).getTags().equals("")) {
                viewHolder.journalTagImg.setImageResource(R.drawable.vi_tag_gray);
            } else {
                viewHolder.journalTagImg.setImageResource(R.drawable.vi_tag_blue);
            }

            if (journalArrayList.get(position).getLongitude().equals("0") || journalArrayList.get(position).getLatitude().equals("0")) {
                viewHolder.journalLocationImg.setImageResource(R.drawable.vi_location_gray);
            } else {
                viewHolder.journalLocationImg.setImageResource(R.drawable.vi_location_blue);
            }

            if (journalArrayList.get(position).getLink().equals("")) {
                viewHolder.journalLinkImg.setImageResource(R.drawable.vi_link_gray);
            } else {
                viewHolder.journalLinkImg.setImageResource(R.drawable.vi_link_blue);
            }

            if (journalArrayList.get(position).getAttachmentList().size() == 0) {
                viewHolder.journalAttachImg.setImageResource(R.drawable.vi_attachment_gray);
            } else {
                viewHolder.journalAttachImg.setImageResource(R.drawable.vi_attachment_blue);
            }

            if (journalArrayList.get(position).getIs_fav() == 0) {
                viewHolder.journalHeartImg.setImageResource(R.drawable.vi_heart_gray);
            } else {
                viewHolder.journalHeartImg.setImageResource(R.drawable.vi_heart_red);
            }
        }

        viewHolder.journalTagImg.setTag(position);
        viewHolder.journalLocationImg.setTag(position);
        viewHolder.journalPictureImg.setTag(position);
        viewHolder.journalHeartImg.setTag(position);
        viewHolder.attachmentLayout.setTag(position);

        viewHolder.journalHeartImg.setOnClickListener(onClick);
        viewHolder.attachmentLayout.setOnClickListener(onClick);

        if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            viewHolder.journalHeartImg.setClickable(false);
        }

        return view;
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.vi_heart_img:
                    toggleFaveUnFav(position);
                    PerformJournalTask.favourate(journalArrayList.get(position).getId(), journalArrayList.get(position).getIs_fav(), mContext, activity);
                    notifyDataSetChanged();
                    break;

                case R.id.journal_list_item_attachment_layout:
                    Intent journalDetails = new Intent(mContext, JournalDetailsActivity.class);
                    journalDetails.putExtra(General.JOURNAL, journalArrayList.get(position));
                    journalDetails.putExtra("details", false);
                    journalDetails.putExtra("not_show_map", false);
                    activity.startActivity(journalDetails);
                    break;
            }
        }
    };

    private void toggleFaveUnFav(int position) {
        if (journalArrayList.get(position).getIs_fav() == 1) {
            journalArrayList.get(position).setIs_fav(0);
        } else {
            journalArrayList.get(position).setIs_fav(1);
        }
    }

    private class ViewHolder {
        TextView journalTitle, lastUpdateDate, subjectOfJournal, journalDescription;
        ImageView journalTagImg, journalLocationImg, journalPictureImg, journalLinkImg, journalAttachImg, journalHeartImg;
        LinearLayout attachmentLayout;
    }
}