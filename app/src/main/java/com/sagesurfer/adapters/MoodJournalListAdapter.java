package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.TimeSet;
import com.sagesurfer.models.MoodJournalDataMood_;
import com.sagesurfer.models.MoodJournalData_;
import com.sagesurfer.views.RoundedCornersTransformation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 11/13/2018.
 */

public class MoodJournalListAdapter extends ArrayAdapter<MoodJournalData_> {
    private static final String TAG = MoodJournalListAdapter.class.getSimpleName();

    public List<MoodJournalData_> journalList = new ArrayList<MoodJournalData_>();
    private final Activity activity;

    public MoodJournalListAdapter(Activity activity, List<MoodJournalData_> journalList) {
        super(activity, 0, journalList);
        this.journalList = journalList;
        this.activity = activity;
        Collections.reverse(journalList);
    }

    @Override
    public int getCount() {
        return journalList.size();
    }

    @Override
    public MoodJournalData_ getItem(int position) {
        if (journalList != null && journalList.size() > 0) {
            return journalList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return journalList.get(position).getMood().get(0).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.mood_journal_item, parent, false);

            viewHolder.cardViewActions = (CardView) view.findViewById(R.id.cardview_actions);
            viewHolder.journalLayout = (LinearLayout) view.findViewById(R.id.linearlayout_journal);
            viewHolder.linearLayoutDate = (LinearLayout) view.findViewById(R.id.linearlayout_date);
            //viewHolder.dateImageView = (ImageView) view.findViewById(R.id.imageview_date);
            viewHolder.dateText = (TextView) view.findViewById(R.id.textview_mood_date);
            viewHolder.moodOneLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_mood_one);
            viewHolder.moodOneImageView = (ImageView) view.findViewById(R.id.imageview_mood_one);
            viewHolder.moodOneView = (View) view.findViewById(R.id.view_mood_one);
            viewHolder.moodOneText = (TextView) view.findViewById(R.id.textview_mood_one);
            viewHolder.timeOneText = (TextView) view.findViewById(R.id.textview_time_one);
            viewHolder.activityOneImageView = (ImageView) view.findViewById(R.id.imageview_activity_one);
            viewHolder.activityOneText = (TextView) view.findViewById(R.id.textview_activity_one);
            viewHolder.messageOneText = (TextView) view.findViewById(R.id.textview_message_one);
            viewHolder.locationOneText = (TextView) view.findViewById(R.id.textview_location_one);

            viewHolder.moodTwoLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_mood_two);
            viewHolder.moodTwoImageView = (ImageView) view.findViewById(R.id.imageview_mood_two);
            viewHolder.moodTwoView = (View) view.findViewById(R.id.view_mood_two);
            viewHolder.moodTwoText = (TextView) view.findViewById(R.id.textview_mood_two);
            viewHolder.timeTwoText = (TextView) view.findViewById(R.id.textview_time_two);
            viewHolder.activityTwoImageView = (ImageView) view.findViewById(R.id.imageview_activity_two);
            viewHolder.activityTwoText = (TextView) view.findViewById(R.id.textview_activity_two);
            viewHolder.messageTwoText = (TextView) view.findViewById(R.id.textview_message_two);
            viewHolder.locationTwoText = (TextView) view.findViewById(R.id.textview_location_two);

            viewHolder.moodThreeLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_mood_three);
            viewHolder.moodThreeImageView = (ImageView) view.findViewById(R.id.imageview_mood_three);
            viewHolder.moodThreeText = (TextView) view.findViewById(R.id.textview_mood_three);
            viewHolder.timeThreeText = (TextView) view.findViewById(R.id.textview_time_three);
            viewHolder.activityThreeImageView = (ImageView) view.findViewById(R.id.imageview_activity_three);
            viewHolder.activityThreeText = (TextView) view.findViewById(R.id.textview_activity_three);
            viewHolder.messageThreeText = (TextView) view.findViewById(R.id.textview_message_three);
            viewHolder.locationThreeText = (TextView) view.findViewById(R.id.textview_location_three);

            viewHolder.setByOneText = (TextView) view.findViewById(R.id.set_by_one);
            viewHolder.setByTwoText = (TextView) view.findViewById(R.id.set_by_two);
            viewHolder.setByThreeText = (TextView) view.findViewById(R.id.set_by_three);

            viewHolder.moodAdded = (TextView) view.findViewById(R.id.added_mood_date);
            viewHolder.moodAddedOne = (TextView) view.findViewById(R.id.added_mood_date_one);
            viewHolder.moodAddedTwo = (TextView) view.findViewById(R.id.added_mood_date_two);

            viewHolder.linearLayoutDescription1 = (LinearLayout) view.findViewById(R.id.linear_layout_desc_1);
            viewHolder.linearLayoutDescription2 = (LinearLayout) view.findViewById(R.id.linear_layout_desc_2);
            viewHolder.linearLayoutDescription3 = (LinearLayout) view.findViewById(R.id.linear_layout_desc_3);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")){
            viewHolder.linearLayoutDescription1.setVisibility(View.GONE);
            viewHolder.linearLayoutDescription2.setVisibility(View.GONE);
            viewHolder.linearLayoutDescription3.setVisibility(View.GONE);
        }else {
            viewHolder.linearLayoutDescription1.setVisibility(View.VISIBLE);
            viewHolder.linearLayoutDescription2.setVisibility(View.VISIBLE);
            viewHolder.linearLayoutDescription3.setVisibility(View.VISIBLE);
        }

        if (journalList.get(position).getMood().get(0).getStatus() == 1) {
            int size = journalList.get(position).getMood().size();

            MoodJournalDataMood_ moodOne = journalList.get(position).getMood().get(0);
            try {
                if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.happy)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_happy)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_happy)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_happy))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_happy));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.laugh))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_laugh));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.cry))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_cry));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.worried)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_worried)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_worried)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_worried))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_worried));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.sad)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_sad)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_sad)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_sad))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_sad));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.angry)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_angry)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_angry)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_angry))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_angry));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.neutral))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_neutral));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.excited)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_excited)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_excited)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_excited))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_excited));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.anxious)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_anxious)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_anxious)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_anxious))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_worried));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.bored)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_bored)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_bored)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_bored))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_bored));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.fearful)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_fearful)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_fearful)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_fearful))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_fearful));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.confused)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_confused)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_confused)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_confused))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_confused));
                } else if (journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.frustrated)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_frustrated)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_frustrated)) ||
                        journalList.get(position).getMood().get(0).getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_frustrated))) {
                    viewHolder.linearLayoutDate.setBackgroundColor(activity.getResources().getColor(R.color.mood_frustrated));
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String todaysDate = sdf.format(new Date());
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                String inputDateStr = moodOne.getDate();
                Date date = inputFormat.parse(inputDateStr);
                String outputDateStr = outputFormat.format(date);
                if (todaysDate.equalsIgnoreCase(inputDateStr)) {
                    viewHolder.dateText.setText("Today, " + outputDateStr);
                } else {
                    String yesterday = TimeSet.getYesterdayDateString();
                    Date yesterdayDate = inputFormat.parse(yesterday);
                    if (yesterdayDate.after(date)) {
                        viewHolder.dateText.setText(outputDateStr);
                    } else {
                        viewHolder.dateText.setText("Yesterday, " + outputDateStr);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (size == 1) {
                viewHolder.moodOneLinearLayout.setVisibility(View.VISIBLE);
                viewHolder.moodTwoLinearLayout.setVisibility(View.GONE);
                viewHolder.moodThreeLinearLayout.setVisibility(View.GONE);
                viewHolder.moodOneView.setVisibility(View.GONE);
                viewHolder.moodTwoView.setVisibility(View.GONE);
                viewHolder.moodOneText.setText(moodOne.getMood_name());
                viewHolder.timeOneText.setText(moodOne.getTime());

                if (moodOne.getActivity_name().equalsIgnoreCase("Other")) {
                    if (moodOne.getOther_activity_name().equalsIgnoreCase("")) {
                        viewHolder.activityOneText.setText(moodOne.getActivity_name());
                    } else {
                        viewHolder.activityOneText.setText(moodOne.getActivity_name() + "(" + moodOne.getOther_activity_name() + ")");
                    }
                } else {
                    viewHolder.activityOneText.setText(moodOne.getActivity_name());
                }
                viewHolder.messageOneText.setText(moodOne.getMood_note());
                viewHolder.setByOneText.setText(moodOne.getSet_by() + "(" + moodOne.getSet_by_roll_name() + ")");
                viewHolder.moodAdded.setText(getDate(moodOne.getAdded_date()));
                if (moodOne.getLocation().length() > 0) {
                    viewHolder.locationOneText.setText(" " + moodOne.getLocation());
                } else {
                    viewHolder.locationOneText.setText("N/A");
                }

                setTextColor(moodOne.getMood_name(), viewHolder.moodOneText, viewHolder.timeOneText, viewHolder.activityOneText);
                setImage(viewHolder.moodOneImageView, moodOne.getMood_url());
                setImage(viewHolder.activityOneImageView, moodOne.getActivity_url());
            } else if (size == 2) {
                viewHolder.moodOneLinearLayout.setVisibility(View.VISIBLE);
                viewHolder.moodThreeLinearLayout.setVisibility(View.GONE);
                viewHolder.moodTwoView.setVisibility(View.GONE);
                viewHolder.moodOneText.setText(moodOne.getMood_name());
                viewHolder.timeOneText.setText(moodOne.getTime());

                if (moodOne.getActivity_name().equalsIgnoreCase("Other")) {
                    if (moodOne.getOther_activity_name().equalsIgnoreCase("")) {
                        viewHolder.activityOneText.setText(moodOne.getActivity_name());
                    } else {
                        viewHolder.activityOneText.setText(moodOne.getActivity_name() + "(" + moodOne.getOther_activity_name() + ")");
                    }
                } else {
                    viewHolder.activityOneText.setText(moodOne.getActivity_name());
                }
                viewHolder.setByOneText.setText(moodOne.getSet_by() + "(" + moodOne.getSet_by_roll_name() + ")");
                viewHolder.moodAdded.setText(getDate(moodOne.getAdded_date()));
                viewHolder.messageOneText.setText(moodOne.getMood_note());
                if (moodOne.getLocation().length() > 0) {
                    viewHolder.locationOneText.setText(" " + moodOne.getLocation());
                } else {
                    viewHolder.locationOneText.setText("N/A");
                }
                setTextColor(moodOne.getMood_name(), viewHolder.moodOneText, viewHolder.timeOneText, viewHolder.activityOneText);
                setImage(viewHolder.moodOneImageView, moodOne.getMood_url());
                setImage(viewHolder.activityOneImageView, moodOne.getActivity_url());

                if (journalList.get(position).getMood().get(1).getStatus() == 1) {
                    MoodJournalDataMood_ moodTwo = journalList.get(position).getMood().get(1);
                    viewHolder.moodTwoLinearLayout.setVisibility(View.VISIBLE);
                    //viewHolder.moodOneView.setVisibility(View.VISIBLE);
                    viewHolder.moodTwoText.setText(moodTwo.getMood_name());
                    viewHolder.timeTwoText.setText(moodTwo.getTime());

                    if (moodTwo.getActivity_name().equalsIgnoreCase("Other")) {
                        if (moodTwo.getOther_activity_name().equalsIgnoreCase("")) {
                            viewHolder.activityTwoText.setText(moodTwo.getActivity_name());
                        } else {
                            viewHolder.activityTwoText.setText(moodTwo.getActivity_name() + "(" + moodTwo.getOther_activity_name() + ")");
                        }
                    } else {
                        viewHolder.activityTwoText.setText(moodTwo.getActivity_name());
                    }

                    viewHolder.setByTwoText.setText(moodOne.getSet_by() + "(" + moodOne.getSet_by_roll_name() + ")");
                    viewHolder.moodAddedOne.setText(getDate(moodTwo.getAdded_date()));
                    viewHolder.messageTwoText.setText(moodTwo.getMood_note());
                    if (moodTwo.getLocation().length() > 0) {
                        viewHolder.locationTwoText.setText(" " + moodTwo.getLocation());
                    } else {
                        viewHolder.locationTwoText.setText("N/A");
                    }
                    setTextColor(moodTwo.getMood_name(), viewHolder.moodTwoText, viewHolder.timeTwoText, viewHolder.activityTwoText);
                    setImage(viewHolder.moodTwoImageView, moodTwo.getMood_url());
                    setImage(viewHolder.activityTwoImageView, moodTwo.getActivity_url());
                } else {
                    viewHolder.moodTwoLinearLayout.setVisibility(View.GONE);
                    viewHolder.moodOneView.setVisibility(View.GONE);
                }
            } else {
                viewHolder.moodOneLinearLayout.setVisibility(View.VISIBLE);
                viewHolder.moodOneText.setText(moodOne.getMood_name());
                viewHolder.timeOneText.setText(moodOne.getTime());
                if (moodOne.getActivity_name().equalsIgnoreCase("Other")) {
                    if (moodOne.getOther_activity_name().equalsIgnoreCase("")) {
                        viewHolder.activityOneText.setText(moodOne.getActivity_name());
                    } else {
                        viewHolder.activityOneText.setText(moodOne.getActivity_name() + "(" + moodOne.getOther_activity_name() + ")");
                    }
                } else {
                    viewHolder.activityOneText.setText(moodOne.getActivity_name());
                }
                viewHolder.setByOneText.setText(moodOne.getSet_by() + "(" + moodOne.getSet_by_roll_name() + ")");
                viewHolder.moodAdded.setText(getDate(moodOne.getAdded_date()));
                viewHolder.messageOneText.setText(moodOne.getMood_note());
                if (moodOne.getLocation().length() > 0) {
                    viewHolder.locationOneText.setText(" " + moodOne.getLocation());
                } else {
                    viewHolder.locationOneText.setText("N/A");
                }
                setTextColor(moodOne.getMood_name(), viewHolder.moodOneText, viewHolder.timeOneText, viewHolder.activityOneText);
                setImage(viewHolder.moodOneImageView, moodOne.getMood_url());
                setImage(viewHolder.activityOneImageView, moodOne.getActivity_url());

                if (journalList.get(position).getMood().get(1).getStatus() == 1) {
                    MoodJournalDataMood_ moodTwo = journalList.get(position).getMood().get(1);
                    viewHolder.moodTwoLinearLayout.setVisibility(View.VISIBLE);
                    // viewHolder.moodOneView.setVisibility(View.VISIBLE);
                    viewHolder.moodTwoText.setText(moodTwo.getMood_name());
                    viewHolder.timeTwoText.setText(moodTwo.getTime());

                    if (moodTwo.getActivity_name().equalsIgnoreCase("Other")) {
                        if (moodTwo.getOther_activity_name().equalsIgnoreCase("")) {
                            viewHolder.activityTwoText.setText(moodTwo.getActivity_name());
                        } else {
                            viewHolder.activityTwoText.setText(moodTwo.getActivity_name() + "(" + moodTwo.getOther_activity_name() + ")");
                        }
                    } else {
                        viewHolder.activityTwoText.setText(moodTwo.getActivity_name());
                    }
                    viewHolder.setByTwoText.setText(moodOne.getSet_by() + "(" + moodOne.getSet_by_roll_name() + ")");
                    viewHolder.moodAddedOne.setText(getDate(moodTwo.getAdded_date()));
                    viewHolder.messageTwoText.setText(moodTwo.getMood_note());
                    if (moodTwo.getLocation().length() > 0) {
                        viewHolder.locationTwoText.setText(moodTwo.getLocation());
                    } else {
                        viewHolder.locationTwoText.setText("N/A");
                    }
                    setTextColor(moodTwo.getMood_name(), viewHolder.moodTwoText, viewHolder.timeTwoText, viewHolder.activityTwoText);
                    setImage(viewHolder.moodTwoImageView, moodTwo.getMood_url());
                    setImage(viewHolder.activityTwoImageView, moodTwo.getActivity_url());
                } else {
                    viewHolder.moodTwoLinearLayout.setVisibility(View.GONE);
                    viewHolder.moodOneView.setVisibility(View.GONE);
                }
                if (journalList.get(position).getMood().get(2).getStatus() == 1) {
                    MoodJournalDataMood_ moodThree = journalList.get(position).getMood().get(2);
                    viewHolder.moodThreeLinearLayout.setVisibility(View.VISIBLE);
                    //viewHolder.moodTwoView.setVisibility(View.VISIBLE);
                    viewHolder.moodThreeText.setText(moodThree.getMood_name());
                    viewHolder.timeThreeText.setText(moodThree.getTime());

                    if (moodThree.getActivity_name().equalsIgnoreCase("Other")) {
                        if (moodThree.getOther_activity_name().equalsIgnoreCase("")) {
                            viewHolder.activityThreeText.setText(moodThree.getActivity_name());
                        } else {
                            viewHolder.activityThreeText.setText(moodThree.getActivity_name() + "(" + moodThree.getOther_activity_name() + ")");
                        }
                    } else {
                        viewHolder.activityThreeText.setText(moodThree.getActivity_name());
                    }


                    viewHolder.setByThreeText.setText(moodOne.getSet_by() + "(" + moodOne.getSet_by_roll_name() + ")");
                    viewHolder.moodAddedTwo.setText(getDate(moodThree.getAdded_date()));
                    viewHolder.messageThreeText.setText(moodThree.getMood_note());
                    if (moodThree.getLocation().length() > 0) {
                        viewHolder.locationThreeText.setText(moodThree.getLocation());
                    } else {
                        viewHolder.locationThreeText.setText("N/A");
                    }
                    setTextColor(moodThree.getMood_name(), viewHolder.moodThreeText, viewHolder.timeThreeText, viewHolder.activityThreeText);
                    setImage(viewHolder.moodThreeImageView, moodThree.getMood_url());
                    setImage(viewHolder.activityThreeImageView, moodThree.getActivity_url());
                } else {
                    viewHolder.moodThreeLinearLayout.setVisibility(View.GONE);
                    viewHolder.moodTwoView.setVisibility(View.GONE);
                }
            }
        } else {
            viewHolder.journalLayout.setVisibility(View.GONE);
        }
        return view;
    }

    private void setImage(ImageView userImage, String path) {
        Glide.with(activity)
                .load(path)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        //.placeholder(R.drawable.ic_team)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new RoundedCornersTransformation(activity.getApplicationContext(), 10, 0)))
                .into(userImage);
    }

    private void setTextColor(String mood, TextView moodText, TextView timeText, TextView activityText) {
        if (mood.contains(activity.getResources().getString(R.string.happy)) ||
                mood.contains(activity.getResources().getString(R.string.really_happy)) ||
                mood.contains(activity.getResources().getString(R.string.very_happy)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_happy))) {
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_happy));
            // timeText.setTextColor(activity.getResources().getColor(R.color.mood_happy));
            // activityText.setTextColor(activity.getResources().getColor(R.color.mood_happy));
        } else if (mood.contains(activity.getResources().getString(R.string.laugh))) {
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_laugh));
            // timeText.setTextColor(activity.getResources().getColor(R.color.mood_laugh));
            //activityText.setTextColor(activity.getResources().getColor(R.color.mood_laugh));
        } else if (mood.contains(activity.getResources().getString(R.string.cry))) {
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_cry));
            //  timeText.setTextColor(activity.getResources().getColor(R.color.mood_cry));
            // activityText.setTextColor(activity.getResources().getColor(R.color.mood_cry));
        } else if (mood.contains(activity.getResources().getString(R.string.angry)) ||
                mood.contains(activity.getResources().getString(R.string.really_angry)) ||
                mood.contains(activity.getResources().getString(R.string.very_angry)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_angry))) {
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_angry));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_angry));
            //activityText.setTextColor(activity.getResources().getColor(R.color.mood_angry));
        } else if (mood.contains(activity.getResources().getString(R.string.sad)) ||
                mood.contains(activity.getResources().getString(R.string.really_sad)) ||
                mood.contains(activity.getResources().getString(R.string.very_sad)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_sad))) {
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_sad));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_sad));
            //activityText.setTextColor(activity.getResources().getColor(R.color.mood_sad));
        } else if (mood.contains(activity.getResources().getString(R.string.worried)) ||
                mood.contains(activity.getResources().getString(R.string.really_worried)) ||
                mood.contains(activity.getResources().getString(R.string.very_worried)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_worried))) {
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
            // activityText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
        } else if (mood.contains(activity.getResources().getString(R.string.neutral))) {
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_neutral));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_neutral));
            // activityText.setTextColor(activity.getResources().getColor(R.color.mood_neutral));
        } else if (mood.contains(activity.getResources().getString(R.string.excited)) ||
                mood.contains(activity.getResources().getString(R.string.really_excited)) ||
                mood.contains(activity.getResources().getString(R.string.very_excited)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_excited))) {
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_excited));
            // timeText.setTextColor(activity.getResources().getColor(R.color.mood_excited));
            // activityText.setTextColor(activity.getResources().getColor(R.color.mood_excited));
        } else if (mood.contains(activity.getResources().getString(R.string.anxious)) ||
                mood.contains(activity.getResources().getString(R.string.really_anxious)) ||
                mood.contains(activity.getResources().getString(R.string.very_anxious)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_anxious))) { //Not used for now
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_cry));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_cry));
            //activityText.setTextColor(activity.getResources().getColor(R.color.mood_cry));
        } else if (mood.contains(activity.getResources().getString(R.string.good))) { //Not used for now
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
            //activityText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
        } else if (mood.contains(activity.getResources().getString(R.string.bored)) ||
                mood.contains(activity.getResources().getString(R.string.really_bored)) ||
                mood.contains(activity.getResources().getString(R.string.very_bored)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_bored))) { //Not used for now
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_bored));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
            //activityText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
        } else if (mood.contains(activity.getResources().getString(R.string.fearful)) ||
                mood.contains(activity.getResources().getString(R.string.really_fearful)) ||
                mood.contains(activity.getResources().getString(R.string.very_fearful)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_fearful))) { //Not used for now
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_fearful));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
            //activityText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
        } else if (mood.contains(activity.getResources().getString(R.string.confused)) ||
                mood.contains(activity.getResources().getString(R.string.really_confused)) ||
                mood.contains(activity.getResources().getString(R.string.very_confused)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_confused))) { //Not used for now
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_confused));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
            //activityText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
        } else if (mood.contains(activity.getResources().getString(R.string.frustrated)) ||
                mood.contains(activity.getResources().getString(R.string.really_frustrated)) ||
                mood.contains(activity.getResources().getString(R.string.very_frustrated)) ||
                mood.contains(activity.getResources().getString(R.string.extremely_frustrated))) { //Not used for now
            moodText.setTextColor(activity.getResources().getColor(R.color.mood_frustrated));
            //timeText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
            //activityText.setTextColor(activity.getResources().getColor(R.color.mood_worried));
        }
    }

    private class ViewHolder {
        TextView dateText, moodOneText, timeOneText, activityOneText, messageOneText, locationOneText;
        TextView setByOneText, setByTwoText, setByThreeText;
        TextView moodTwoText, timeTwoText, activityTwoText, messageTwoText, locationTwoText;
        TextView moodThreeText, timeThreeText, activityThreeText, messageThreeText, locationThreeText;
        ImageView moodOneImageView, activityOneImageView, moodTwoImageView, activityTwoImageView, moodThreeImageView, activityThreeImageView;
        LinearLayout journalLayout, linearLayoutDate, moodOneLinearLayout, moodTwoLinearLayout, moodThreeLinearLayout;
        View moodOneView, moodTwoView;
        CardView cardViewActions;
        TextView moodAdded, moodAddedOne, moodAddedTwo;
        LinearLayout linearLayoutDescription1,linearLayoutDescription2,linearLayoutDescription3;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = android.text.format.DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }
}
