package com.modules.goal_assignment.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.modules.goal_assignment.model.AssignedGoals;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 12/24/2019.
 */
public class SupervisorGoalAssignmentAdapter extends ArrayAdapter<Choices_> implements UnAssignedGoalAdapter.UnAssignedGoalAdapterListener, AssignedGoalAdapter.AssignedGoalAdapterListener {
    private static final String TAG = SupervisorGoalAssignmentAdapter.class.getSimpleName();
    private List<Choices_> goalArrayList;
    private ArrayList<AssignedGoals> assignGoalArrayList = new ArrayList<>(), unAssignGoalArrayList = new ArrayList<>();
    private final Context mContext;
    private final Activity activity;
    private LinearLayoutManager mLinearLayoutManager, mLinearLayoutManagerOne;
    private AssignedGoalAdapter assignedGoalAdapter;
    private UnAssignedGoalAdapter unAssignedGoalAdapter;
    private ViewHolder mainViewHolder;

    public SupervisorGoalAssignmentAdapter(Activity activity, List<Choices_> goalArrayList) {
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
            view = layoutInflater.inflate(R.layout.goal_assignment_supervisor_layout, parent, false);

            viewHolder.goalNames = (TextView) view.findViewById(R.id.textview_caseload_name);
            viewHolder.grade = (TextView) view.findViewById(R.id.textview_grade);
            viewHolder.noData = (TextView) view.findViewById(R.id.no_data_label);
            viewHolder.noDataOne = (TextView) view.findViewById(R.id.no_data);
            viewHolder.profileImage = (ImageView) view.findViewById(R.id.imageview_profile);
            viewHolder.downArrow = (ImageView) view.findViewById(R.id.down_arrow);
            viewHolder.upArrow = (ImageView) view.findViewById(R.id.up_arrow);

            viewHolder.detailsRelativeLayout = (LinearLayout) view.findViewById(R.id.linearlayout_details);
            viewHolder.assignedNonAssignedGoalLayout = (LinearLayout) view.findViewById(R.id.assigned_non_assigned_goal_layout);
            viewHolder.borderLayout = (LinearLayout) view.findViewById(R.id.border_layout);

            viewHolder.assignedGoalList = (RecyclerView) view.findViewById(R.id.assign_goal_list);
            viewHolder.unAssignedGoalList = (RecyclerView) view.findViewById(R.id.un_assign_goal_list);

            mLinearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            mLinearLayoutManagerOne = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

            viewHolder.assignedGoalList.setLayoutManager(mLinearLayoutManager);
            viewHolder.unAssignedGoalList.setLayoutManager(mLinearLayoutManagerOne);

            mainViewHolder = viewHolder;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            mainViewHolder = viewHolder;
        }

        if (goalArrayList.get(position).getStatus() == 1) {

            viewHolder.goalNames.setText(goalArrayList.get(position).getName());

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

                mainViewHolder = viewHolder;
                // Calling web service for assigned and Unassigned
                toGetSetUnsetGoalsOfStudent(goalArrayList.get(position).getId(), viewHolder.assignedGoalList, viewHolder.unAssignedGoalList, viewHolder.noDataOne, viewHolder.noData);
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
                    toGetSetUnsetGoalsOfStudent(goalArrayList.get(position).getId(), viewHolder.assignedGoalList, viewHolder.unAssignedGoalList, viewHolder.noDataOne, viewHolder.noData);
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

                    mainViewHolder = viewHolder;

                    // Calling web service for assigned and Unassigned
                    toGetSetUnsetGoalsOfStudent(goalArrayList.get(position).getId(), viewHolder.assignedGoalList, viewHolder.unAssignedGoalList, viewHolder.noDataOne, viewHolder.noData);

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

                    viewHolder.goalNames.setTextColor(activity.getResources().getColor(R.color.black));
                    viewHolder.grade.setTextColor(activity.getResources().getColor(R.color.black));
                }
            });
        }

        return view;
    }

    private void toGetSetUnsetGoalsOfStudent(long stud_id, RecyclerView assignedGoalList, RecyclerView unAssignedGoalList, TextView noDataOne, TextView noData) {
        assignGoalArrayList.clear();
        unAssignGoalArrayList.clear();

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
                    JSONArray jsonArrayUnAssigned = jsonGoal.getJSONArray("unassigned");

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
                        }
                    }

                    if (assignGoalArrayList.size() > 0) {
                        if (assignGoalArrayList.get(0).getStatus() == 1) {
                            assignedGoalAdapter = new AssignedGoalAdapter(activity, assignGoalArrayList, this);
                            assignedGoalList.setAdapter(assignedGoalAdapter);
                        }
                    } else {
                        assignedGoalList.setVisibility(View.GONE);
                        noDataOne.setVisibility(View.VISIBLE);
                    }

                    for (int j = 0; j < jsonArrayUnAssigned.length(); j++) {
                        JSONObject object = jsonArrayUnAssigned.getJSONObject(j);
                        AssignedGoals assignedGoals = new AssignedGoals();

                        if (Integer.parseInt(object.getString("status")) == 1) {
                            assignedGoals.setWer_goal_id(Long.parseLong(object.getString("wer_goal_id")));
                            assignedGoals.setGoal_name(object.getString("goal_name"));
                            assignedGoals.setGoal_question(object.getString("goal_question"));
                            assignedGoals.setStatus(Integer.parseInt(object.getString("status")));

                            unAssignGoalArrayList.add(assignedGoals);
                        }
                    }

                    if (unAssignGoalArrayList.size() > 0) {
                        if (unAssignGoalArrayList.get(0).getStatus() == 1) {
                            unAssignedGoalAdapter = new UnAssignedGoalAdapter(activity, unAssignGoalArrayList, this);
                            unAssignedGoalList.setAdapter(unAssignedGoalAdapter);
                        }
                    } else {
                        unAssignedGoalList.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void selectGoalIds(int position, boolean assignedGoal) {
        // Calling web service for assigned and Unassigned
        toGetSetUnsetGoalsOfStudent(Long.parseLong(Preferences.get("assigned_goal_stud_id")), mainViewHolder.assignedGoalList, mainViewHolder.unAssignedGoalList, mainViewHolder.noDataOne, mainViewHolder.noData);
    }

    @Override
    public void refreshGoalData(int position) {
        forRefreshData();
    }

    @Override
    public void DeleteGoalData(int position, boolean unsetGoal) {
        toGetSetUnsetGoalsOfStudent(Long.parseLong(Preferences.get("assigned_goal_stud_id")), mainViewHolder.assignedGoalList, mainViewHolder.unAssignedGoalList, mainViewHolder.noDataOne, mainViewHolder.noData);
    }

    private void forRefreshData() {
        assignGoalArrayList.clear();
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
                    JSONArray jsonArrayAssigned = jsonGoal.getJSONArray("assigned");
                    JSONArray jsonArrayUnAssigned = jsonGoal.getJSONArray("unassigned");

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
        TextView goalNames, grade, noData, noDataOne;
        ImageView profileImage, downArrow, upArrow;
        LinearLayout detailsRelativeLayout, assignedNonAssignedGoalLayout, borderLayout;
        RecyclerView assignedGoalList, unAssignedGoalList;
    }
}

