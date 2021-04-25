package com.modules.goal_assignment.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.modules.goal_assignment.model.AssignedGoals;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 12/27/2019.
 */
public class CoachGoalAssignmentAdapter extends ArrayAdapter<Choices_> implements UnAssignedGoalAdapter.UnAssignedGoalAdapterListener, AssignedGoalAdapter.AssignedGoalAdapterListener {
    private static final String TAG = CoachGoalAssignmentAdapter.class.getSimpleName();
    private List<Choices_> goalArrayList;
    private ArrayList<AssignedGoals> assignGoalArrayList = new ArrayList<>(), unAssignGoalArrayList = new ArrayList<>();
    private final Context mContext;
    private final Activity activity;
    private LinearLayoutManager mLinearLayoutManager, mLinearLayoutManagerOne;
    private AssignedGoalAdapter assignedGoalAdapter;
    private UnAssignedGoalAdapter unAssignedGoalAdapter;

    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String start_date = "", end_date = "";
    private ArrayList<String> goalIds = new ArrayList<String>();
    private ViewHolder mainViewHolder;
    private int currentSelectedSection = 0;// 0= assigned,1=not-assigned

    public CoachGoalAssignmentAdapter(Activity activity, List<Choices_> goalArrayList) {
        super(activity, 0, goalArrayList);
        this.goalArrayList = goalArrayList;
        this.mContext = activity.getApplicationContext();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return goalArrayList.size();
    }

    @Override
    public Choices_ getItem(int position) {
        if (goalArrayList != null && goalArrayList.size() > 0) {
            return goalArrayList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return goalArrayList.get(position).getId();
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
            view = layoutInflater.inflate(R.layout.goal_assignment_coach_layout, parent, false);

            viewHolder.goalNames = (TextView) view.findViewById(R.id.textview_caseload_name);
            viewHolder.grade = (TextView) view.findViewById(R.id.textview_grade);
            viewHolder.profileImage = (ImageView) view.findViewById(R.id.imageview_profile);
            viewHolder.downArrow = (ImageView) view.findViewById(R.id.down_arrow);
            viewHolder.upArrow = (ImageView) view.findViewById(R.id.up_arrow);

            viewHolder.detailsRelativeLayout = (LinearLayout) view.findViewById(R.id.linearlayout_details);
            viewHolder.assignedNonAssignedGoalLayout = (LinearLayout) view.findViewById(R.id.assigned_non_assigned_goal_layout);
            viewHolder.borderLayout = (LinearLayout) view.findViewById(R.id.border_layout);
            viewHolder.notAssignedLayout = (LinearLayout) view.findViewById(R.id.not_assigned_layout);

            viewHolder.assignGoal = (Button) view.findViewById(R.id.assign_goal);
            viewHolder.notAssignGoal = (Button) view.findViewById(R.id.not_assign_goal);

            viewHolder.assignBtn = (Button) view.findViewById(R.id.assign_goal_btn);
            viewHolder.cancelBtn = (Button) view.findViewById(R.id.cancel_goal_btn);

            viewHolder.assignedGoalList = (RecyclerView) view.findViewById(R.id.assign_goal_list);
            viewHolder.unAssignedGoalList = (RecyclerView) view.findViewById(R.id.un_assign_goal_list);

            viewHolder.startDate = (TextView) view.findViewById(R.id.start_date);
            viewHolder.endDate = (TextView) view.findViewById(R.id.end_date);

            viewHolder.noDataLabel = (TextView) view.findViewById(R.id.no_data_label);
            viewHolder.unAssignedLayout = (LinearLayout) view.findViewById(R.id.un_assigned_layout);

            mLinearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            mLinearLayoutManagerOne = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

            viewHolder.assignedGoalList.setLayoutManager(mLinearLayoutManager);
            viewHolder.unAssignedGoalList.setLayoutManager(mLinearLayoutManagerOne);

            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            mainViewHolder = viewHolder;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();

            mainViewHolder = viewHolder;
        }

        if (goalArrayList.get(position).getStatus() == 1) {
            viewHolder.goalNames.setText(ChangeCase.toTitleCase(goalArrayList.get(position).getName()));

            if (goalArrayList.get(position).getGrade() == 1) {
                viewHolder.grade.setText("Grade - " + goalArrayList.get(position).getGrade() + "st");
            } else if (goalArrayList.get(position).getGrade() == 2) {
                viewHolder.grade.setText("Grade - " + goalArrayList.get(position).getGrade() + "nd");
            } else if (goalArrayList.get(position).getGrade() == 3) {
                viewHolder.grade.setText("Grade - " + goalArrayList.get(position).getGrade() + "rd");
            } else {
                viewHolder.grade.setText("Grade - " + goalArrayList.get(position).getGrade() + "th");
            }

            Glide.with(activity)
                    .load(goalArrayList.get(position).getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(goalArrayList.get(position).getImage()))
                            .transform(new CircleTransform(activity)))
                    .into(viewHolder.profileImage);


            viewHolder.assignGoal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSelectedSection = 0;
                    viewHolder.notAssignGoal.setBackgroundColor(activity.getResources().getColor(R.color.colorLightGrey));
                    viewHolder.assignGoal.setBackgroundColor(activity.getResources().getColor(R.color.cg_dashboard_box_two));
                    viewHolder.notAssignGoal.setTextColor(activity.getResources().getColor(R.color.text_color_secondary));
                    viewHolder.assignGoal.setTextColor(activity.getResources().getColor(R.color.colorAccent));

                    viewHolder.assignedGoalList.setVisibility(View.VISIBLE);
                    viewHolder.unAssignedGoalList.setVisibility(View.GONE);
                    viewHolder.notAssignedLayout.setVisibility(View.GONE);

                    viewHolder.noDataLabel.setVisibility(View.GONE);
                    viewHolder.unAssignedLayout.setVisibility(View.GONE);

                    viewHolder.goalNames.setTextColor(activity.getResources().getColor(R.color.white));
                    viewHolder.grade.setTextColor(activity.getResources().getColor(R.color.color_image));
                    viewHolder.detailsRelativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));

                    viewHolder.startDate.setText("");
                    viewHolder.endDate.setText("");

                    // Calling web service for assigned and Unassigned
                    assignedGoalForCoach(goalArrayList.get(position).getId(), viewHolder.assignedGoalList, viewHolder.noDataLabel, viewHolder.unAssignedLayout);
                }
            });

            viewHolder.notAssignGoal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSelectedSection = 1;
                    viewHolder.assignGoal.setBackgroundColor(activity.getResources().getColor(R.color.colorLightGrey));
                    viewHolder.notAssignGoal.setBackgroundColor(activity.getResources().getColor(R.color.cg_dashboard_box_two));
                    viewHolder.assignGoal.setTextColor(activity.getResources().getColor(R.color.text_color_secondary));
                    viewHolder.notAssignGoal.setTextColor(activity.getResources().getColor(R.color.colorAccent));

                    viewHolder.assignedGoalList.setVisibility(View.GONE);
                    viewHolder.unAssignedGoalList.setVisibility(View.VISIBLE);
                    viewHolder.notAssignedLayout.setVisibility(View.VISIBLE);

                    viewHolder.goalNames.setTextColor(activity.getResources().getColor(R.color.white));
                    viewHolder.grade.setTextColor(activity.getResources().getColor(R.color.color_image));
                    viewHolder.detailsRelativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));

                    viewHolder.startDate.setText("");
                    viewHolder.endDate.setText("");

                    // Calling web service for assigned and Unassigned
                    notAssignedGoalForCoach(goalArrayList.get(position).getId(), viewHolder.unAssignedGoalList, viewHolder.noDataLabel, viewHolder.unAssignedLayout);

                }
            });

            if (Preferences.get(General.STUDENT_ASSIGN_NAME).equals(goalArrayList.get(position).getName())) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.assignedNonAssignedGoalLayout.setVisibility(View.VISIBLE);

                viewHolder.goalNames.setTextColor(activity.getResources().getColor(R.color.white));
                viewHolder.grade.setTextColor(activity.getResources().getColor(R.color.color_image));
                viewHolder.detailsRelativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
                viewHolder.borderLayout.setBackgroundResource(R.drawable.blue_rounded_border);

                Preferences.save("assigned_goal_name", goalArrayList.get(position).getName());
                Preferences.save("assigned_goal_image", goalArrayList.get(position).getImage());
                Preferences.save("assigned_goal_stud_id", goalArrayList.get(position).getId());

                // Calling web service for assigned and Unassigned
                assignedGoalForCoach(goalArrayList.get(position).getId(), viewHolder.assignedGoalList, viewHolder.noDataLabel, viewHolder.unAssignedLayout);

                Preferences.save(General.STUDENT_ASSIGN_NAME, "");
            }


            viewHolder.detailsRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.upArrow.setVisibility(View.VISIBLE);
                    viewHolder.downArrow.setVisibility(View.GONE);
                    viewHolder.assignedNonAssignedGoalLayout.setVisibility(View.VISIBLE);

                    viewHolder.goalNames.setTextColor(activity.getResources().getColor(R.color.white));
                    viewHolder.grade.setTextColor(activity.getResources().getColor(R.color.color_image));
                    viewHolder.detailsRelativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
                    viewHolder.borderLayout.setBackgroundResource(R.drawable.blue_rounded_border);

                    Preferences.save("assigned_goal_name", goalArrayList.get(position).getName());
                    Preferences.save("assigned_goal_image", goalArrayList.get(position).getImage());
                    Preferences.save("assigned_goal_stud_id", goalArrayList.get(position).getId());

                    mainViewHolder = viewHolder;

                    // Calling web service for assigned and Unassigned
                    assignedGoalForCoach(goalArrayList.get(position).getId(), viewHolder.assignedGoalList, viewHolder.noDataLabel, viewHolder.unAssignedLayout);
                }
            });

            viewHolder.downArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.upArrow.setVisibility(View.VISIBLE);
                    viewHolder.downArrow.setVisibility(View.GONE);
                    viewHolder.assignedNonAssignedGoalLayout.setVisibility(View.VISIBLE);

                    viewHolder.goalNames.setTextColor(activity.getResources().getColor(R.color.white));
                    viewHolder.grade.setTextColor(activity.getResources().getColor(R.color.color_image));
                    viewHolder.detailsRelativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
                    viewHolder.borderLayout.setBackgroundResource(R.drawable.blue_rounded_border);

                    Preferences.save("assigned_goal_name", goalArrayList.get(position).getName());
                    Preferences.save("assigned_goal_image", goalArrayList.get(position).getImage());
                    Preferences.save("assigned_goal_stud_id", goalArrayList.get(position).getId());

                    Preferences.save("assign_start_date", "");
                    Preferences.save("assign_end_date", "");

                    mainViewHolder = viewHolder;

                    // Calling web service for assigned and Unassigned
                    assignedGoalForCoach(goalArrayList.get(position).getId(), viewHolder.assignedGoalList, viewHolder.noDataLabel, viewHolder.unAssignedLayout);
                }
            });

            viewHolder.upArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.upArrow.setVisibility(View.GONE);
                    viewHolder.downArrow.setVisibility(View.VISIBLE);
                    viewHolder.assignedNonAssignedGoalLayout.setVisibility(View.GONE);
                    viewHolder.detailsRelativeLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));

                    Preferences.save("assigned_goal_name", "");
                    Preferences.save("assigned_goal_image", "");
                    Preferences.save("assigned_goal_stud_id", "");

                    Preferences.save("assign_start_date", "");
                    Preferences.save("assign_end_date", "");

                    viewHolder.goalNames.setTextColor(activity.getResources().getColor(R.color.black));
                    viewHolder.grade.setTextColor(activity.getResources().getColor(R.color.black));
                }
            });

            viewHolder.startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    start_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                    try {
                                        viewHolder.startDate.setText(start_date);
                                        Preferences.save("assign_start_date", start_date);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, mYear, mMonth, mDay);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_WEEK, -6);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                }
            });

            viewHolder.endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog1 = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    monthOfYear = (monthOfYear + 1);
                                    sDay = dayOfMonth;
                                    sMonth = monthOfYear;
                                    sYear = year;

                                    end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                    try {
                                        int result = Compare.validEndDate(end_date, viewHolder.startDate.getText().toString().trim());
                                        if (result == 1) {
                                            viewHolder.endDate.setText(end_date);
                                            Preferences.save("assign_end_date", end_date);
                                        } else {
                                            end_date = null;
                                            viewHolder.endDate.setText(null);
                                            ShowSnack.textViewWarning(viewHolder.endDate, activity.getResources()
                                                    .getString(R.string.invalid_date), activity);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, mYear, mMonth, mDay);
                    Calendar c1 = Calendar.getInstance();
                    c1.add(Calendar.DAY_OF_WEEK, -6);
                    datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog1.show();
                }
            });


            viewHolder.assignBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getNotAssignedGoalIds();
                    if (validate(viewHolder.startDate.getText().toString(), viewHolder.endDate.getText().toString())) {
                        assignGoalAPI();
                        resetFields(viewHolder.startDate, viewHolder.endDate);
                        // Calling web service for assigned and Unassigned
                        notAssignedGoalForCoach(goalArrayList.get(position).getId(), viewHolder.unAssignedGoalList, viewHolder.noDataLabel, viewHolder.unAssignedLayout);
                    }
                }
            });

            viewHolder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetFields(viewHolder.startDate, viewHolder.endDate);
                    int size = unAssignGoalArrayList.size();
                    for (int i = 0; i < size; i++) {
                        AssignedGoals notification = unAssignGoalArrayList.get(i);
                        notification.setSelected(false);
                    }
                    unAssignedGoalAdapter.notifyDataSetChanged();
                }
            });
        }

        return view;
    }

    private void resetFields(TextView startDate, TextView endDate) {
        startDate.setText("");
        endDate.setText("");
        Preferences.save("assign_start_date", "");
        Preferences.save("assign_end_date", "");
        Preferences.save("start_date_one", "");
        Preferences.save("end_date_one", "");
    }

    private void assignGoalAPI() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ASSIGN_GOALS);
        requestMap.put(General.STUD_ID, Preferences.get("assigned_goal_stud_id"));
        requestMap.put(General.GOAL_IDS, getNotAssignedGoalIds());
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject editGoal = jsonObject.getAsJsonObject(Actions_.ASSIGN_GOALS);
                    if (editGoal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, editGoal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        //refreshNotAssignedGoalForCoach();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    private String getNotAssignedGoalIds() {
        goalIds.clear();
        if (unAssignGoalArrayList != null && unAssignGoalArrayList.size() > 0) {
            for (int i = 0; i < unAssignGoalArrayList.size(); i++) {
                if (unAssignGoalArrayList.get(i).getSelected()) {
                    if (!unAssignGoalArrayList.get(i).getStart_date().equalsIgnoreCase("")) {
                        goalIds.add(String.valueOf(unAssignGoalArrayList.get(i).getWer_goal_id() + "_" + unAssignGoalArrayList.get(i).getStart_date() + "_" + unAssignGoalArrayList.get(i).getEnd_date()));
                    } else {
                        goalIds.add(String.valueOf(unAssignGoalArrayList.get(i).getWer_goal_id() + "_" + Preferences.get("assign_start_date") + "_" + Preferences.get("assign_end_date")));
                    }

                }
            }
        }
        return android.text.TextUtils.join(",", goalIds);
    }

    private boolean validate(String startDate, String endDate) {

        if (Preferences.get("assign_start_date").equals("") || Preferences.get("assign_end_date").equals("")) {
            if (goalIds.size() == 0) {
                Toast.makeText(activity, "Please select goals", Toast.LENGTH_LONG).show();
                return false;
            }

        } else {
            if (startDate == null || startDate.trim().length() <= 0) {
                Toast.makeText(activity, "Please select Start Date", Toast.LENGTH_LONG).show();
                return false;
            }

            if (endDate == null || endDate.trim().length() <= 0) {
                Toast.makeText(activity, "Please select End Date", Toast.LENGTH_LONG).show();
                return false;
            }

            if (goalIds.size() == 0) {
                Toast.makeText(activity, "Please select goals", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void assignedGoalForCoach(long stud_id, RecyclerView assignedGoalList, TextView noDataLable, LinearLayout layout) {
        assignGoalArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ASSIGNED_UNASSIGNED_GOALS);
        requestMap.put(General.STUDENT_ID, String.valueOf(stud_id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonGoal = jsonObject.getJSONObject(Actions_.GET_ASSIGNED_UNASSIGNED_GOALS);
                    JSONArray jsonArrayAssigned = jsonGoal.getJSONArray("assigned");


                    for (int i = 0; i < jsonArrayAssigned.length(); i++) {
                        JSONObject object = jsonArrayAssigned.getJSONObject(i);
                        AssignedGoals assignedGoals = new AssignedGoals();

                        if (Integer.parseInt(object.getString("status")) == 1) {
                            assignedGoals.setWer_goal_id(Long.parseLong(object.getString("wer_goal_id")));
                            assignedGoals.setMain_goal_id(Long.parseLong(object.getString("main_goal_id")));
                            assignedGoals.setGoal_name(object.getString("goal_name"));
                            assignedGoals.setGoal_question(object.getString("goal_question"));
                            assignedGoals.setStart_date(object.getString("start_date"));
                            assignedGoals.setEnd_date(object.getString("end_date"));
                            assignedGoals.setProgress(Integer.parseInt(object.getString("progress")));
                            assignedGoals.setGoal_current_status(Integer.parseInt(object.getString("goal_current_status")));
                            assignedGoals.setIsAddedAnyInputs(Integer.parseInt(object.getString("isAddedAnyInputs")));
                            assignedGoals.setDate_diff(object.getString("date_diff"));
                            assignedGoals.setStatus(Integer.parseInt(object.getString("status")));
                            assignedGoals.setSelected(true);

                            assignGoalArrayList.add(assignedGoals);

                        } else {
                            assignedGoals.setDate_diff(object.getString("error"));
                            assignedGoals.setStatus(Integer.parseInt(object.getString("status")));
                        }
                    }


                    if (assignGoalArrayList.size() > 0) {
                        if (assignGoalArrayList.get(0).getStatus() == 1) {
                            assignedGoalAdapter = new AssignedGoalAdapter(activity, assignGoalArrayList, this);
                            assignedGoalList.setAdapter(assignedGoalAdapter);
                        }
                    } else {

                        if (currentSelectedSection == 0) {
                            assignedGoalList.setVisibility(View.GONE);
                            layout.setVisibility(View.GONE);
                            noDataLable.setVisibility(View.VISIBLE);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void notAssignedGoalForCoach(long stud_id, RecyclerView unAssignedGoalList, TextView noDataLable, LinearLayout noAssignedLayout) {
        unAssignGoalArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ASSIGNED_UNASSIGNED_GOALS);
        requestMap.put(General.STUDENT_ID, String.valueOf(stud_id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        Log.e("requestMap",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                Log.e("Response",response);
                if (response != null) {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonGoal = jsonObject.getJSONObject(Actions_.GET_ASSIGNED_UNASSIGNED_GOALS);
                    JSONArray jsonArrayUnAssigned = jsonGoal.getJSONArray("unassigned");


                    for (int j = 0; j < jsonArrayUnAssigned.length(); j++) {
                        JSONObject object = jsonArrayUnAssigned.getJSONObject(j);
                        AssignedGoals assignedGoals = new AssignedGoals();

                        if (Integer.parseInt(object.getString("status")) == 1) {
                            assignedGoals.setWer_goal_id(Long.parseLong(object.getString("wer_goal_id")));
                            assignedGoals.setGoal_name(object.getString("goal_name"));
                            assignedGoals.setGoal_question(object.getString("goal_question"));
                            assignedGoals.setStatus(Integer.parseInt(object.getString("status")));
                            assignedGoals.setStart_date("");
                            assignedGoals.setEnd_date("");
                        } else {
                            assignedGoals.setDate_diff(object.getString("error"));
                            assignedGoals.setStatus(Integer.parseInt(object.getString("status")));
                        }

                        unAssignGoalArrayList.add(assignedGoals);
                    }


                    if (unAssignGoalArrayList.size() > 0) {
                        if (unAssignGoalArrayList.get(0).getStatus() == 1) {
                            if (currentSelectedSection == 1) {
                                unAssignedGoalList.setVisibility(View.VISIBLE);
                                noAssignedLayout.setVisibility(View.VISIBLE);
                                noDataLable.setVisibility(View.GONE);
                            }
                            unAssignedGoalAdapter = new UnAssignedGoalAdapter(activity, unAssignGoalArrayList, this);
                            unAssignedGoalList.setAdapter(unAssignedGoalAdapter);
                        } else {
                            if (currentSelectedSection == 1) {
                                unAssignedGoalList.setVisibility(View.GONE);
                                noAssignedLayout.setVisibility(View.GONE);
                                noDataLable.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void selectGoalIds(int position, boolean assignedGoal) {
        refreshAssignedGoalForCoach();
        assignedGoalAdapter.notifyDataSetChanged();
        refreshNotAssignedGoalForCoach();
        unAssignedGoalAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshGoalData(int position) {
        refreshAssignedGoalForCoach();
        assignedGoalAdapter.notifyDataSetChanged();
        refreshNotAssignedGoalForCoach();
        unAssignedGoalAdapter.notifyDataSetChanged();
    }

    @Override
    public void DeleteGoalData(int position, boolean unsetGoal) {
        // Calling web service for assigned
        assignedGoalForCoach(Long.parseLong(Preferences.get("assigned_goal_stud_id")), mainViewHolder.assignedGoalList, mainViewHolder.noDataLabel, mainViewHolder.unAssignedLayout);
        // Calling web service for Unassigned
        notAssignedGoalForCoach(Long.parseLong(Preferences.get("assigned_goal_stud_id")), mainViewHolder.unAssignedGoalList, mainViewHolder.noDataLabel, mainViewHolder.unAssignedLayout);
    }

    private void refreshAssignedGoalForCoach() {
        assignGoalArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ASSIGNED_UNASSIGNED_GOALS);
        requestMap.put(General.STUD_ID, Preferences.get("assigned_goal_stud_id"));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonGoal = jsonObject.getJSONObject(Actions_.GET_ASSIGNED_UNASSIGNED_GOALS);
                    JSONArray jsonArrayAssigned = jsonGoal.getJSONArray("assigned");

                    for (int i = 0; i < jsonArrayAssigned.length(); i++) {
                        JSONObject object = jsonArrayAssigned.getJSONObject(i);
                        AssignedGoals assignedGoals = new AssignedGoals();

                        if (Integer.parseInt(object.getString("status")) == 1) {
                            assignedGoals.setWer_goal_id(Long.parseLong(object.getString("wer_goal_id")));
                            assignedGoals.setMain_goal_id(Long.parseLong(object.getString("main_goal_id")));
                            assignedGoals.setGoal_name(object.getString("goal_name"));
                            assignedGoals.setGoal_question(object.getString("goal_question"));
                            assignedGoals.setStart_date(object.getString("start_date"));
                            assignedGoals.setEnd_date(object.getString("end_date"));
                            assignedGoals.setProgress(Integer.parseInt(object.getString("progress")));
                            assignedGoals.setGoal_current_status(Integer.parseInt(object.getString("goal_current_status")));
                            assignedGoals.setIsAddedAnyInputs(Integer.parseInt(object.getString("isAddedAnyInputs")));
                            assignedGoals.setDate_diff(object.getString("date_diff"));
                            assignedGoals.setStatus(Integer.parseInt(object.getString("status")));
                            assignedGoals.setSelected(true);
                        } else {
                            assignedGoals.setDate_diff(object.getString("error"));
                            assignedGoals.setStatus(Integer.parseInt(object.getString("status")));
                        }
                        assignGoalArrayList.add(assignedGoals);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshNotAssignedGoalForCoach() {
        unAssignGoalArrayList.clear();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_ASSIGNED_UNASSIGNED_GOALS);
        requestMap.put(General.STUD_ID, Preferences.get("assigned_goal_stud_id"));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonGoal = jsonObject.getJSONObject(Actions_.GET_ASSIGNED_UNASSIGNED_GOALS);
                    JSONArray jsonArrayUnAssigned = jsonGoal.getJSONArray("unassigned");

                    for (int j = 0; j < jsonArrayUnAssigned.length(); j++) {
                        JSONObject object = jsonArrayUnAssigned.getJSONObject(j);
                        AssignedGoals assignedGoals = new AssignedGoals();
                        if (Integer.parseInt(object.getString("status")) == 1) {
                            assignedGoals.setWer_goal_id(Long.parseLong(object.getString("wer_goal_id")));
                            assignedGoals.setGoal_name(object.getString("goal_name"));
                            assignedGoals.setGoal_question(object.getString("goal_question"));
                            assignedGoals.setStatus(Integer.parseInt(object.getString("status")));
                        } else {
                            assignedGoals.setDate_diff(object.getString("error"));
                            assignedGoals.setStatus(Integer.parseInt(object.getString("status")));
                        }
                        unAssignGoalArrayList.add(assignedGoals);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ViewHolder {
        TextView goalNames, grade, startDate, endDate, noDataLabel;
        ImageView profileImage, downArrow, upArrow;
        LinearLayout detailsRelativeLayout, assignedNonAssignedGoalLayout, borderLayout, notAssignedLayout, unAssignedLayout;
        RecyclerView assignedGoalList, unAssignedGoalList;
        Button assignGoal, notAssignGoal, assignBtn, cancelBtn;
    }
}


