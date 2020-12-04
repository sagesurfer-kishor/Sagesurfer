package com.modules.goal_assignment.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

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
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 12/26/2019.
 */
public class UnAssignedGoalAdapter extends RecyclerView.Adapter<UnAssignedGoalAdapter.MyViewHolder> {
    private static final String TAG = UnAssignedGoalAdapter.class.getSimpleName();
    private final ArrayList<AssignedGoals> assignedGoalsArrayList;
    private UnAssignedGoalAdapterListener unAssignedGoalAdapterListener;
    private Activity activity;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String start_date = "", end_date = "";
    private Dialog dialog;
    private boolean fromCoach = false;
    private ArrayList<String> goalIds = new ArrayList<String>();

    public UnAssignedGoalAdapter(Activity activity, ArrayList<AssignedGoals> assignedGoalsArrayList1, UnAssignedGoalAdapterListener unAssignedGoalAdapterListener) {
        this.assignedGoalsArrayList = assignedGoalsArrayList1;
        this.activity = activity;
        this.unAssignedGoalAdapterListener = unAssignedGoalAdapterListener;
    }

    @Override
    public long getItemId(int position) {
        return assignedGoalsArrayList.get(position).getWer_goal_id();
    }

    @Override
    public int getItemCount() {
        return assignedGoalsArrayList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.un_assigned_goal_coach_layout, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.un_assigned_goal_layout, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final AssignedGoals assignedGoals = assignedGoalsArrayList.get(position);
        if (assignedGoals.getStatus() == 1) {
            viewHolder.goalName.setText(ChangeCase.toTitleCase(assignedGoals.getGoal_name()));
            // for coach actions
            if (Preferences.get(General.ROLE_ID).equals("6")) {
                boolean checked = assignedGoals.getSelected();
                if (checked) {
                    viewHolder.multiSelectCheckBox.setChecked(true);
                } else {
                    viewHolder.multiSelectCheckBox.setChecked(false);
                }

                viewHolder.multiSelectCheckBox.setTag(position);
                viewHolder.multiSelectCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        boolean checked = assignedGoalsArrayList.get(position).isSelected();
                        if (checked) {
                            viewHolder.multiSelectCheckBox.setChecked(false);
                            assignedGoalsArrayList.get(position).setSelected(false);
                        } else {
                            viewHolder.multiSelectCheckBox.setChecked(true);
                            assignedGoalsArrayList.get(position).setSelected(true);
                        }
                        notifyDataSetChanged();
                    }
                });

                viewHolder.calendarIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fromCoach = true;
                        assignGoalDialogBox(assignedGoals, position, viewHolder.multiSelectCheckBox);
                    }
                });

            } else { // for supervisor actions
                boolean checked = assignedGoals.getSelected();
                if (checked) {
                    viewHolder.multiSelectCheckBox.setChecked(true);
                } else {
                    viewHolder.multiSelectCheckBox.setChecked(false);
                }

                viewHolder.multiSelectCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.multiSelectCheckBox.setChecked(false);
                        assignGoalDialogBox(assignedGoals, position, viewHolder.multiSelectCheckBox);
                    }
                });
            }
        }
    }

    private void assignGoalDialogBox(final AssignedGoals assignedGoals, final int position, final CheckBox checkBox) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.assigned_goal_dialog);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
        Button buttonAssign = (Button) dialog.findViewById(R.id.button_assign);
        ImageView profileImage = (ImageView) dialog.findViewById(R.id.imageview_profile);
        TextView userName = (TextView) dialog.findViewById(R.id.user_name);
        TextView goalName = (TextView) dialog.findViewById(R.id.goal_name);
        final TextView startDate = (TextView) dialog.findViewById(R.id.start_date);
        final TextView endDate = (TextView) dialog.findViewById(R.id.end_date);

        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        Glide.with(activity)
                .load(Preferences.get("assigned_goal_image"))
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(Preferences.get("assigned_goal_image")))
                        .transform(new CircleTransform(activity)))
                .into(profileImage);

        userName.setText(ChangeCase.toTitleCase(Preferences.get("assigned_goal_name")));
        goalName.setText(ChangeCase.toTitleCase(assignedGoals.getGoal_name()));

        if (Preferences.get(General.ROLE_ID).equals("6")) {
            if (Preferences.get("start_date_one"+assignedGoals.getWer_goal_id()).equals("") || Preferences.get("end_date_one"+assignedGoals.getWer_goal_id()).equals("")) {
                if (Preferences.get("assign_start_date").equals("") || Preferences.get("assign_end_date").equals("")) {
                    startDate.setText("");
                    endDate.setText("");
                } else {
                    startDate.setText(Preferences.get("assign_start_date"));
                    endDate.setText(Preferences.get("assign_end_date"));
                }
            } else {
                startDate.setText(Preferences.get("start_date_one"+assignedGoals.getWer_goal_id()));
                endDate.setText(Preferences.get("end_date_one"+assignedGoals.getWer_goal_id()));
            }
        } else {
            startDate.setText("");
            endDate.setText("");
        }

        startDate.setOnClickListener(new View.OnClickListener() {
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
                                    startDate.setText(start_date);
                                    Preferences.save("start_date_one"+assignedGoals.getWer_goal_id(), start_date);
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


        endDate.setOnClickListener(new View.OnClickListener() {
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
                                    int result = Compare.validEndDate(end_date, startDate.getText().toString());
                                    if (result == 1) {
                                        endDate.setText(end_date);
                                        Preferences.save("end_date_one"+assignedGoals.getWer_goal_id(), end_date);
                                    } else {
                                        end_date = null;
                                        endDate.setText(null);
                                        ShowSnack.textViewWarning(endDate, activity.getResources()
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

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation(startDate.getText().toString().trim(), endDate.getText().toString().trim())) {
                    if (fromCoach) {
                        checkBox.setChecked(true);
                        assignedGoalsArrayList.get(position).setSelected(true);
                        assignedGoalsArrayList.get(position).setStart_date(startDate.getText().toString());
                        assignedGoalsArrayList.get(position).setEnd_date(endDate.getText().toString());
                        notifyDataSetChanged();
//                        assignGoalAPI(startDate.getText().toString().trim(), endDate.getText().toString().trim(), position);
                        dialog.dismiss();
                    } else {
                        assignedGoalsArrayList.get(position).setSelected(true);
                        assignedGoalsArrayList.get(position).setStart_date(startDate.getText().toString());
                        assignedGoalsArrayList.get(position).setEnd_date(endDate.getText().toString());
                        notifyDataSetChanged();
                        assignGoalAPI(startDate.getText().toString().trim(), endDate.getText().toString().trim(), position);
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.show();
    }

    private boolean validation(String startDate, String endDate) {
        if (startDate == null || startDate.trim().length() <= 0) {
            Toast.makeText(activity, "Please select Start Date", Toast.LENGTH_LONG).show();
            return false;
        }

        if (endDate == null || endDate.trim().length() <= 0) {
            Toast.makeText(activity, "Please select End Date", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void assignGoalAPI(String startDate, String endDate, final int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ASSIGN_GOALS);
        requestMap.put(General.STUD_ID, Preferences.get("assigned_goal_stud_id"));
        requestMap.put(General.GOAL_IDS, getNotAssignedGoalIds());
        requestMap.put(General.START_DATE, startDate);
        requestMap.put(General.END_DATE, endDate);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        Log.e("requestMap",requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("ResponseAP",""+response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject editGoal = jsonObject.getAsJsonObject(Actions_.ASSIGN_GOALS);
                    if (editGoal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, ""+editGoal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        unAssignedGoalAdapterListener.selectGoalIds(position, true);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(activity, ""+editGoal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(activity, "Error Null response", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    private String getNotAssignedGoalIds() {
        goalIds.clear();
        if (assignedGoalsArrayList != null && assignedGoalsArrayList.size() > 0) {
            for (int i = 0; i < assignedGoalsArrayList.size(); i++) {
                if (assignedGoalsArrayList.get(i).getSelected()) {
                    goalIds.add(String.valueOf(assignedGoalsArrayList.get(i).getWer_goal_id() + "_" + assignedGoalsArrayList.get(i).getStart_date() + "_" + assignedGoalsArrayList.get(i).getEnd_date()));
                }
            }
        }
        return android.text.TextUtils.join(",", goalIds);
    }

    public interface UnAssignedGoalAdapterListener {
        void selectGoalIds(int position, boolean assignedGoal);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView goalName;
        CheckBox multiSelectCheckBox;
        ImageView calendarIcon;

        MyViewHolder(View view) {
            super(view);
            multiSelectCheckBox = (CheckBox) view.findViewById(R.id.multi_select_check_box);
            goalName = (TextView) view.findViewById(R.id.textview_goal_name);
            calendarIcon = (ImageView) view.findViewById(R.id.calender_icon);
        }
    }
}
