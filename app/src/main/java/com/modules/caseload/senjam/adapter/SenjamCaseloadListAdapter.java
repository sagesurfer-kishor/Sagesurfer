package com.modules.caseload.senjam.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.modules.dailydosing.activity.SenjamDailyDosingActivity;
import com.modules.assessment_screener.AssessmentListActivity;
import com.modules.caseload.CaseloadContactActivity;
import com.modules.caseload.senjam.activity.SenjamDoctorsNoteActivity;
import com.modules.caseload.werhope.activity.ProgressNoteActivity;
import com.modules.mood.CCMoodActivity;
import com.modules.onetime_dailysurvey.activity.OnTimeSurveyListActivity;
import com.modules.sows.activity.SenjamSowsActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Caseload_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class SenjamCaseloadListAdapter extends RecyclerView.Adapter<SenjamCaseloadListAdapter.MyViewHolder> {
    private static final String TAG = SenjamCaseloadListAdapter.class.getSimpleName();
    private final ArrayList<Caseload_> caseloadList;
    private ArrayList<Teams_> teamArrayList;
    private Teams_ team_ = new Teams_();
    private int color;
    private Intent detailsIntent;
    private Activity activity;
    private Dialog dialog;
    private boolean isEventFromCaseload = false;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date = "";
    private String senjam_patient_id;

    public SenjamCaseloadListAdapter(Activity activity, ArrayList<Caseload_> caseloadList, String senjam_patient_id) {
        this.caseloadList = caseloadList;
        this.activity = activity;
        this.senjam_patient_id = senjam_patient_id;
    }

//    @Override
//    public long getItemId(int position) {
//        return caseloadList.get(position).getUser_id();
//    }

    @Override
    public int getItemCount() {
        return caseloadList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.senjam_new_caseload_cardview_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        final Caseload_ caseload_ = caseloadList.get(position);

        viewHolder.nameText.setText(ChangeCase.toTitleCase(caseloadList.get(position).getUsername()));
        viewHolder.contactedText.setText(caseloadList.get(position).getContacted_last());


        if (caseloadList.get(position).getContacted_last().equalsIgnoreCase("")) {
            viewHolder.lastNoteDate.setText("N/A");
        } else {
            viewHolder.lastNoteDate.setText(caseloadList.get(position).getContacted_last());
        }

        if (caseloadList.get(position).getLast_progress_date() == null || caseloadList.get(position).getLast_progress_date().trim().length() <= 0 || caseloadList.get(position).getLast_progress_date().equalsIgnoreCase("N/A")) {
            viewHolder.lastNoteDate.setText(caseloadList.get(position).getLast_progress_date());
        } else {
            viewHolder.lastNoteDate.setText(getDate(Long.parseLong(caseloadList.get(position).getLast_progress_date())));
        }

        if (caseloadList.get(position).getLast_mood().equalsIgnoreCase("")) {
            viewHolder.lastMood.setText("N/A");
        } else {
            viewHolder.lastMood.setText(ChangeCase.toTitleCase(caseloadList.get(position).getLast_mood()));
        }

        Glide.with(activity)
                .load(caseloadList.get(position).getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(caseloadList.get(position).getImage()))
                        .transform(new CircleTransform(activity)))
                .into(viewHolder.profileImage);

        if (caseloadList.get(position).getMood_image().equals("")) {
            viewHolder.moodImg.setVisibility(View.GONE);
        } else {
            Glide.with(activity)
                    .load(caseloadList.get(position).getMood_image())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(caseloadList.get(position).getMood_image())))
                    .into(viewHolder.moodImg);
        }

        // When Server option then Orange border & Orange dot in Survey menu
        //When Moderate option then Red border & Red dot in survey menu
        //else no any border or dot
        if (caseloadList.get(position).getCovid().equalsIgnoreCase(General.COVID_MODERATE)) {
            viewHolder.detailsRelativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.covid_border_moderate));
            viewHolder.mDailySurveyIndicator.setVisibility(View.VISIBLE);
            viewHolder.mDailySurveyIndicator.setBackground(activity.getResources().getDrawable(R.drawable.senjam_status_orange));
        } else if (caseloadList.get(position).getCovid().equalsIgnoreCase(General.COVID_SEVERE)) {
            viewHolder.detailsRelativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.covid_border_severe));
            viewHolder.mDailySurveyIndicator.setVisibility(View.VISIBLE);
            viewHolder.mDailySurveyIndicator.setBackground(activity.getResources().getDrawable(R.drawable.senjam_status_red));
        } else {
            viewHolder.detailsRelativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
            viewHolder.mDailySurveyIndicator.setVisibility(View.GONE);
        }


        // when Daily Dosing is Completed it will indicate Green Dot
        // when Daily Dosing is Partially Completed it will indicate Orange Dot
        // when Daily Dosing is InCompleted it will indicate Red Dot
        if (caseloadList.get(position).getDaily_dosing_status() == 1) {
            viewHolder.mDailyDosingIndicator.setVisibility(View.VISIBLE);
            viewHolder.mDailyDosingIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_green));
        } else if (caseloadList.get(position).getDaily_dosing_status() == 2) {
            viewHolder.mDailyDosingIndicator.setVisibility(View.VISIBLE);
            viewHolder.mDailyDosingIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_orange));
        } else if (caseloadList.get(position).getDaily_dosing_status() == 3) {
            viewHolder.mDailyDosingIndicator.setVisibility(View.VISIBLE);
            viewHolder.mDailyDosingIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.senjam_status_red));
        } else {
            viewHolder.mDailyDosingIndicator.setVisibility(View.GONE);
        }


        //redirect from push notification and open that patient detail
        if (senjam_patient_id.equalsIgnoreCase(String.valueOf(caseloadList.get(position).getUser_id()))) {
            viewHolder.upArrow.setVisibility(View.VISIBLE);
            viewHolder.downArrow.setVisibility(View.GONE);
            viewHolder.caseloadActionsLayout.setVisibility(View.VISIBLE);
            viewHolder.detailsLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.upArrow.setVisibility(View.GONE);
            viewHolder.downArrow.setVisibility(View.VISIBLE);
            viewHolder.caseloadActionsLayout.setVisibility(View.GONE);
            viewHolder.detailsLayout.setVisibility(View.GONE);
        }


        viewHolder.detailsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.caseloadActionsLayout.setVisibility(View.VISIBLE);
                viewHolder.detailsLayout.setVisibility(View.VISIBLE);
            }
        });

        viewHolder.downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.caseloadActionsLayout.setVisibility(View.VISIBLE);
                viewHolder.detailsLayout.setVisibility(View.VISIBLE);
            }
        });

        viewHolder.upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.GONE);
                viewHolder.downArrow.setVisibility(View.VISIBLE);
                viewHolder.caseloadActionsLayout.setVisibility(View.GONE);
                viewHolder.detailsLayout.setVisibility(View.GONE);
            }
        });


        color = R.color.black;
        viewHolder.doctorNoteImageView.setColorFilter(color);
        viewHolder.oneTimeDailySurveyImageView.setColorFilter(color);
        viewHolder.sowsListImageView.setColorFilter(color);
        viewHolder.dailyDosingImageView.setColorFilter(color);
        viewHolder.moodImageView.setColorFilter(color);
        viewHolder.contactImageView.setColorFilter(color);


        // Click Event for DoctorNote Activity
        viewHolder.doctorNoteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.CONSUMER_NAME, caseload_.getUsername());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
//                    Preferences.save(General.NOTE_USER_ID, "" + caseload_.getUser_id());
                detailsIntent = new Intent(activity, SenjamDoctorsNoteActivity.class);
                activity.startActivity(detailsIntent);
            }
        });


        // Click Event For Daily Survey Activity
        viewHolder.oneTimeDailySurveyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEventFromCaseload = true;
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                detailsIntent = new Intent(activity.getApplicationContext(), OnTimeSurveyListActivity.class);
                activity.startActivity(detailsIntent);
            }
        });

        // Click Event For Sow(Notes) List Activity
        viewHolder.sowsListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                detailsIntent = new Intent(activity.getApplicationContext(), SenjamSowsActivity.class);
                activity.startActivity(detailsIntent);
            }
        });

        viewHolder.moodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.CONSUMER_NAME, caseload_.getUsername());
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());
                detailsIntent = new Intent(activity.getApplicationContext(), CCMoodActivity.class);
                activity.startActivity(detailsIntent);
            }
        });

        viewHolder.dailyDosingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.CONSUMER_ID, caseload_.getUser_id());
                Preferences.save(General.CONSUMER_NAME, caseload_.getName());

                // Api call for Status Check
                // if Status is 0 then Open Daily Dosing Dialog
                // if Status is 1 then Open Daily Dosing Activity
                dailyDosingStatusAPICalled();
            }
        });

        viewHolder.contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.GROUP_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_ID, "" + caseload_.getGroup_id());
                Preferences.save(General.TEAM_NAME, caseload_.getGroup_name());

                detailsIntent = new Intent(activity.getApplicationContext(), CaseloadContactActivity.class);
                detailsIntent.putExtra(General.NAME, caseload_.getUsername());
                detailsIntent.putExtra(General.USERNAME, caseload_.getUsername());
                detailsIntent.putExtra(General.ADDRESS, caseload_.getAddress());
                detailsIntent.putExtra(General.URL_IMAGE, caseload_.getImage());
                detailsIntent.putExtra(General.PHONE, caseload_.getPhone());
                activity.startActivity(detailsIntent);
            }
        });
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy", cal).toString();
        return date;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText, parentName, contactedText;
        final ImageView profileImage, moodImg, downArrow, upArrow;
        final LinearLayout taskLinearLayout, eventsLinearLayout, detailsLayout, mCovidLinearLayoutDetail;
        final RelativeLayout OneTimeNoteLinearLayout, detailsRelativeLayout, teamLinearLayout;
        final LinearLayout contactLinearLayout, moodLinearLayout, caseloadActionsLayout;
        final TextView lastNoteDate, lastMood, teamName;
        final AppCompatImageView doctorNoteImageView, oneTimeDailySurveyImageView, sowsListImageView, dailyDosingImageView;
        final AppCompatImageView moodImageView, contactImageView, mDailySurveyIndicator, mDailyDosingIndicator;

        MyViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.textview_caseload_name);
            parentName = (TextView) view.findViewById(R.id.textview_caseload_parent_name);
            contactedText = (TextView) view.findViewById(R.id.textview_caseload_contacted_at);
            profileImage = (ImageView) view.findViewById(R.id.imageview_profile);

            OneTimeNoteLinearLayout = (RelativeLayout) view.findViewById(R.id.linearlayout_caseload_onetime);
            eventsLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_events);
            taskLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_task_list);
            teamLinearLayout = (RelativeLayout) view.findViewById(R.id.linearlayout_caseload_team);
            moodLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_mood);
            contactLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_contact);
            caseloadActionsLayout = (LinearLayout) view.findViewById(R.id.relativelayout_caseload_actions);
            detailsLayout = (LinearLayout) view.findViewById(R.id.details_layout);
            mCovidLinearLayoutDetail = (LinearLayout) view.findViewById(R.id.main_linear_layout);
            mDailySurveyIndicator = view.findViewById(R.id.image_view_daily_survey_indicator);
            mDailyDosingIndicator = view.findViewById(R.id.image_view_daily_dosing_indicator);

            moodImg = (ImageView) view.findViewById(R.id.caseload_mood_img);
            downArrow = (ImageView) view.findViewById(R.id.down_arrow);
            upArrow = (ImageView) view.findViewById(R.id.up_arrow);

            detailsRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_details);

            doctorNoteImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_doctor_note);
            oneTimeDailySurveyImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_one_time_daily_survey);
            sowsListImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_sows);
            dailyDosingImageView = (AppCompatImageView) view.findViewById(R.id.imageview_daily_dosage);
            moodImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_mood);
            contactImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_contact);

            lastNoteDate = (TextView) view.findViewById(R.id.last_note_date);
            lastMood = (TextView) view.findViewById(R.id.mood_type);
            teamName = (TextView) view.findViewById(R.id.team_name_txt);
        }
    }

    @SuppressLint({"SetTextI18n", "LongLogTag"})
    private void dailyDosingStatusAPICalled() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.CHECK_GOAL_IS_ADDED_SENJAM);
        requestMap.put(General.PATIENT_ID, Preferences.get(General.CONSUMER_ID));
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("PNResponse", "" + response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonDailyDosing = jsonObject.getAsJsonObject(Actions_.CHECK_GOAL_IS_ADDED_SENJAM);
                    if (jsonDailyDosing.get(General.STATUS).getAsInt() == 1) {
//                        Toast.makeText(activity, jsonDailyDosing.get(General.MSG).getAsString(), Toast.LENGTH_SHORT).show();

                        /*If status is 1 than we have call DailDosing Activity*/
                        detailsIntent = new Intent(activity.getApplicationContext(), SenjamDailyDosingActivity.class);
                        activity.startActivity(detailsIntent);
                    } else {

                        /*If Status is not 1 then we have to open Dailog Box*/
                        showAddDailyDosingDialog();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//                Log.e("errorDailyDosingActivity", ""+e.getMessage());
            }
        }
    }


    // Daily Dosing Dialog Function
    @SuppressLint("SetTextI18n")
    private void showAddDailyDosingDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_daily_dosing);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        final TextView mTxtPatientName = (TextView) dialog.findViewById(R.id.txt_patient_name_value);
        final TextView mTxtDosingName = (TextView) dialog.findViewById(R.id.txt_dosing_name_value);
        final TextView mTxtSelectDate = (TextView) dialog.findViewById(R.id.txt_select_date);
        final Button mButtonSubmit = (Button) dialog.findViewById(R.id.button_submit);
        final ImageButton buttonClose = (ImageButton) dialog.findViewById(R.id.button_close);
//        textViewMsg.setText(message);

        mTxtPatientName.setText(Preferences.get(General.CONSUMER_NAME));
        mTxtSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    mTxtSelectDate.setText(date);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_WEEK, -14);

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Api Call for Add Daily Dosing

                dailyDosingAddAPICalled(date);
            }
        });

        dialog.show();
    }

    //Api call for Add Daily Dosing
    @SuppressLint({"SetTextI18n", "LongLogTag"})
    private void dailyDosingAddAPICalled(String date) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ADD_START_DATE_DOSAGE_SENJAM);
        requestMap.put(General.PATIENT_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.START_DATE, date);
        Log.e("requestMapDailydosingAdd", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("dailydosingAddResponse", "" + response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonDailyDosing = jsonObject.getAsJsonObject(Actions_.ADD_START_DATE_DOSAGE_SENJAM);
                    if (jsonDailyDosing.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, jsonDailyDosing.get(General.MSG).getAsString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(activity, jsonDailyDosing.get(General.ERROR).getAsString(), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//                Log.e("errorDailyDosingActivity", ""+e.getMessage());
            }
        }
    }

}
