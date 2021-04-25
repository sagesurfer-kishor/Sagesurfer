package com.modules.goal_assignment.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 12/26/2019.
 */
public class AssignedGoalAdapter extends RecyclerView.Adapter<AssignedGoalAdapter.MyViewHolder> {
    private static final String TAG = AssignedGoalAdapter.class.getSimpleName();
    private final ArrayList<AssignedGoals> assignedGoalsArrayList;
    private Activity activity;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String start_date = "", end_date = "";
    private String selectedReason = "";
    private Dialog dialog;
    private ArrayAdapter<String> reasonAdapter;
    private AssignedGoalAdapterListener assignedGoalAdapterListener;

    public AssignedGoalAdapter(Activity activity, ArrayList<AssignedGoals> assignedGoalsArrayList1, AssignedGoalAdapterListener assignedGoalAdapterListener) {
        this.assignedGoalsArrayList = assignedGoalsArrayList1;
        this.activity = activity;
        this.assignedGoalAdapterListener = assignedGoalAdapterListener;
    }

    @Override
    public long getItemId(int position) {
        return assignedGoalsArrayList.get(position).getWer_goal_id();
    }

    @Override
    public int getItemCount() {
        return assignedGoalsArrayList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (Preferences.get(General.ROLE_ID).equals("6")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.assigned_goal_coach_layout, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.assigned_goal_layout, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final AssignedGoals assignedGoals = assignedGoalsArrayList.get(position);

        if (assignedGoals.getStatus() == 1) {
            viewHolder.goalName.setText(ChangeCase.toTitleCase(assignedGoals.getGoal_name()));
            if (Preferences.get(General.ROLE_ID).equals("6")) {
                viewHolder.startDate.setText(dateCaps(assignedGoals.getStart_date()));
                viewHolder.endDate.setText(dateCaps(assignedGoals.getEnd_date()));
                viewHolder.editGoal.setColorFilter(Color.parseColor("#757575"));
                viewHolder.deleteGoal.setColorFilter(Color.parseColor("#757575"));

                viewHolder.editGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assignedGoalEditDialog(v, assignedGoals, position);
                    }
                });

                goalStatusColor(assignedGoals, viewHolder.goalStatus);

                viewHolder.deleteGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteGoalDialog(assignedGoals, position);
                    }
                });
            } else {
                boolean checked = assignedGoals.getSelected();
                if (checked) {
                    viewHolder.multiSelectCheckBox.setChecked(true);
                } else {
                    viewHolder.multiSelectCheckBox.setChecked(false);
                }

                viewHolder.multiSelectCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.multiSelectCheckBox.setChecked(true);
                        unSetGoalDialogBox(assignedGoals, position);
                    }
                });

                goalStatusColor(assignedGoals, viewHolder.goalStatus);

                viewHolder.editGoal.setVisibility(View.GONE);
                viewHolder.editGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assignedGoalEditDialog(v, assignedGoals, position);
                    }
                });
            }
        }
    }

    private void goalStatusColor(AssignedGoals assignedGoals, TextView goalStatus) {
        if (assignedGoals.getGoal_current_status() == 1) {
            goalStatus.setText("Completed");
            goalStatus.setTextColor(activity.getResources().getColor(R.color.self_goal_green));
        } else if (assignedGoals.getGoal_current_status() == 2) {
            goalStatus.setText("Missed");
            goalStatus.setTextColor(activity.getResources().getColor(R.color.busy));
        } else {
            goalStatus.setText("Running (" + assignedGoals.getDate_diff() + " remaining)");
            goalStatus.setTextColor(activity.getResources().getColor(R.color.self_goal_active));
        }
    }

    private void showDeleteGoalDialog(final AssignedGoals assignedGoals, final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_peer_note_edit);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final TextView titleTxt = (TextView) dialog.findViewById(R.id.title_txt);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final ImageButton buttonCancel = (ImageButton) dialog.findViewById(R.id.button_close);

        titleTxt.setText("Action Confirmation");
        buttonYes.setText("OK");
        textViewMsg.setText(assignedGoals.getGoal_name() + " goal will be deleted from platform!");

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGoalAPI(assignedGoals, position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deleteGoalAPI(AssignedGoals assignedGoals, int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_GOAL);
        requestMap.put(General.STUD_ID, Preferences.get("assigned_goal_stud_id"));
        requestMap.put(General.GOAL_ID, String.valueOf(assignedGoals.getWer_goal_id()));
        requestMap.put(General.MAIN_GOAL_ID, String.valueOf(assignedGoals.getMain_goal_id()));
        requestMap.put(General.START_DATE, assignedGoals.getStart_date());
        requestMap.put(General.END_DATE, assignedGoals.getEnd_date());
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject editGoal = jsonObject.getAsJsonObject(Actions_.DELETE_GOAL);
                    if (editGoal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, editGoal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        assignedGoalAdapterListener.DeleteGoalData(position, false);
                        notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validate(String reason, String comment) {
        if (reason.equalsIgnoreCase("") || reason.equalsIgnoreCase("Select reason")) {
            Toast.makeText(activity, "Please select reason", Toast.LENGTH_LONG).show();
            return false;
        }

        if (comment == null || comment.trim().length() <= 0) {
            Toast.makeText(activity, "Please enter comment", Toast.LENGTH_LONG).show();
            return false;
        }

        if (comment.length() < 4) {
            Toast.makeText(activity, "Comment: Min 4 Char required", Toast.LENGTH_LONG).show();
            return false;
        }

        if (comment.length() > 100) {
            Toast.makeText(activity, "Comment: Max 100 char allowed", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void unSetGoalDialogBox(final AssignedGoals assignedGoals, final int position) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unset_assigned_goal);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
        Button buttonSubmit = (Button) dialog.findViewById(R.id.button_submit);
        ImageView profileImage = (ImageView) dialog.findViewById(R.id.imageview_profile);
        TextView userName = (TextView) dialog.findViewById(R.id.user_name);
        TextView goalName = (TextView) dialog.findViewById(R.id.goal_name);
        final Spinner reasonSpinner = (Spinner) dialog.findViewById(R.id.reason_goal_spinner);
        final EditText comment = (EditText) dialog.findViewById(R.id.comment_goal);

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

        ArrayList<String> reason = new ArrayList<>();
        reason.add("Select reason");
        reason.add("Student no longer");
        reason.add("Start date need to be modified");
        reason.add("Student don't want to work on goal");

        reasonAdapter = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, reason);
        reasonAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
        reasonSpinner.setAdapter(reasonAdapter);

        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedReason = reasonSpinner.getSelectedItem().toString().trim();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentValue = comment.getText().toString().trim();
                if (validate(selectedReason, commentValue)) {
                    unSetGoalAPI(assignedGoals, commentValue, selectedReason, position);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void unSetGoalAPI(AssignedGoals assignedGoals, String comment, String reason, int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.UNSET_GOAL);
        requestMap.put(General.STUD_ID, Preferences.get("assigned_goal_stud_id"));
        requestMap.put(General.GOAL_ID, String.valueOf(assignedGoals.getWer_goal_id()));
        requestMap.put(General.COMMENT, comment);
        requestMap.put(General.REASON, reason);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject editGoal = jsonObject.getAsJsonObject(Actions_.UNSET_GOAL);
                    if (editGoal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, editGoal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        assignedGoalAdapterListener.DeleteGoalData(position, true);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(activity, editGoal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void assignedGoalEditDialog(View view, final AssignedGoals assignedGoals, final int position) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_assigned_goal);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
        Button buttonUpdate = (Button) dialog.findViewById(R.id.button_update);
        ImageView profileImage = (ImageView) dialog.findViewById(R.id.imageview_profile);
        TextView userName = (TextView) dialog.findViewById(R.id.user_name);
        TextView goalName = (TextView) dialog.findViewById(R.id.goal_name);
        TextView status = (TextView) dialog.findViewById(R.id.status_txt);
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

        if (assignedGoals.getGoal_current_status() == 1) {
            status.setText("Completed");
            status.setTextColor(activity.getResources().getColor(R.color.self_goal_green));
        } else if (assignedGoals.getGoal_current_status() == 2) {
            status.setText("Missed");
            status.setTextColor(activity.getResources().getColor(R.color.busy));
        } else {
            status.setText("Running (" + assignedGoals.getDate_diff() + " remaining )");
            status.setTextColor(activity.getResources().getColor(R.color.self_goal_active));
        }

        startDate.setText(assignedGoals.getStart_date());
        endDate.setText(assignedGoals.getEnd_date());


        if ((Preferences.get(General.ROLE_ID).equals("6"))) {
            if (startDate.getText().toString().equalsIgnoreCase("") || endDate.getText().toString().equalsIgnoreCase("")) {
                startDate.setEnabled(true);
                startDate.setClickable(true);
            } else {
                startDate.setEnabled(false);
                startDate.setClickable(false);
            }
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

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editGoalAPI(assignedGoals, startDate.getText().toString().trim(), endDate.getText().toString().trim(), position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void editGoalAPI(AssignedGoals assignedGoals, String startDate, String endDate,
                             final int position) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.EDIT_GOAL);
        requestMap.put(General.STUD_ID, Preferences.get("assigned_goal_stud_id"));
        requestMap.put(General.GOAL_ID, String.valueOf(assignedGoals.getWer_goal_id()));
        requestMap.put(General.START_DATE, startDate);
        requestMap.put(General.END_DATE, endDate);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject editGoal = jsonObject.getAsJsonObject(Actions_.EDIT_GOAL);
                    if (editGoal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, editGoal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        assignedGoalAdapterListener.refreshGoalData(position);
                        notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView goalName, goalStatus, startDate, endDate;
        ImageView editGoal, deleteGoal;
        CheckBox multiSelectCheckBox;

        MyViewHolder(View view) {
            super(view);
            multiSelectCheckBox = (CheckBox) view.findViewById(R.id.multi_select_check_box);
            goalName = (TextView) view.findViewById(R.id.textview_goal_name);
            startDate = (TextView) view.findViewById(R.id.start_date_txt);
            endDate = (TextView) view.findViewById(R.id.end_date_txt);
            editGoal = (ImageView) view.findViewById(R.id.edit_goal);
            deleteGoal = (ImageView) view.findViewById(R.id.delete_goal);
            goalStatus = (TextView) view.findViewById(R.id.textview_goal_status);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String dateCaps(String dateValue) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("MMM dd, yyyy");
        String date = spf.format(newDate);
        return date;
    }

    public interface AssignedGoalAdapterListener {
        void refreshGoalData(int position);

        void DeleteGoalData(int position, boolean unsetGoal);
    }
}
