package com.modules.assignment.werhope.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.modules.assignment.werhope.model.Student;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

public class StudentlListAdapter extends ArrayAdapter<Student> {
    private static final String TAG = StudentlListAdapter.class.getSimpleName();
    private List<Student> studentList;
    private Context mContext;
    private Activity activity;
    private StudentlListAdapterListener studentlListAdapterListener;

    public StudentlListAdapter(Activity activity, List<Student> studentList1, StudentlListAdapterListener studentlListAdapterListener) {
        super(activity, 0, studentList1);
        this.studentList = studentList1;
        this.mContext = activity.getApplicationContext();
        this.activity = activity;
        this.studentlListAdapterListener = studentlListAdapterListener;
    }

    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Student getItem(int position) {
        if (studentList != null && studentList.size() > 0) {
            return studentList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return studentList.get(position).getSchool_id();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.assignment_list_item, parent, false);

            viewHolder.student_name = (TextView) view.findViewById(R.id.student_name);
            viewHolder.downArrow = (ImageView) view.findViewById(R.id.down_arrow);
            viewHolder.upArrow = (ImageView) view.findViewById(R.id.up_arrow);
            viewHolder.assignmentLayout = (RelativeLayout) view.findViewById(R.id.assignment_layout);
            viewHolder.subAssignmentLayout = (LinearLayout) view.findViewById(R.id.sub_assignment_layout);
            viewHolder.iconLayout = (LinearLayout) view.findViewById(R.id.icon_layout);
            viewHolder.mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            viewHolder.invitationLayout = (LinearLayout) view.findViewById(R.id.send_invitation_layout);

            viewHolder.grade = (TextView) view.findViewById(R.id.grade_txt);
            viewHolder.assignmentDate = (TextView) view.findViewById(R.id.assign_date);
            viewHolder.addedBy = (TextView) view.findViewById(R.id.added_by_txt);
            viewHolder.dob = (TextView) view.findViewById(R.id.dob_txt);
            viewHolder.viewDetails = (TextView) view.findViewById(R.id.view_details);
            viewHolder.role = (TextView) view.findViewById(R.id.role_txt);
            viewHolder.emailTxt = (TextView) view.findViewById(R.id.email_txt);
            viewHolder.statusTxt = (TextView) view.findViewById(R.id.status_txt);
            viewHolder.status = (TextView) view.findViewById(R.id.status);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Student student = studentList.get(position);

        if (student.getStatus() == 1) {
            viewHolder.student_name.setText(student.getName());
            viewHolder.grade.setText(String.valueOf(student.getGrade()));
            viewHolder.assignmentDate.setText(GetTime.getDateTime(student.getAssignment_date()));
            viewHolder.addedBy.setText(student.getAdded_by());
            viewHolder.dob.setText(GetTime.dateCaps(student.getDob()));
            viewHolder.role.setText("(" + ChangeCase.toTitleCase(student.getAdded_by_role()) + ")");

            if (student.getEmail().equalsIgnoreCase("")) {
                viewHolder.emailTxt.setText("N/A");
            } else {
                viewHolder.emailTxt.setText(student.getEmail());
            }

            if (student.getSend_invite_student() == 1 && student.getStudent_signup() == 0) {
                viewHolder.statusTxt.setVisibility(View.GONE);
                viewHolder.status.setVisibility(View.GONE);
            } else if (student.getSend_invite_student() == 0 && student.getStudent_signup() == 1) {
                viewHolder.statusTxt.setVisibility(View.GONE);
                viewHolder.status.setVisibility(View.GONE);
            } else if (student.getSend_invite_student() == 0 && student.getStudent_signup() == 0) {
                if (student.getEmail().equalsIgnoreCase("")) {
                    viewHolder.statusTxt.setVisibility(View.GONE);
                    viewHolder.status.setVisibility(View.GONE);
                } else {
                    viewHolder.statusTxt.setVisibility(View.VISIBLE);
                    viewHolder.status.setVisibility(View.VISIBLE);
                }
            }
        }

        if (Integer.parseInt(Preferences.get(General.NOTIFICATION_BHS_ID)) == student.getStud_id()) {
            viewHolder.upArrow.setVisibility(View.VISIBLE);
            viewHolder.downArrow.setVisibility(View.GONE);
            viewHolder.subAssignmentLayout.setVisibility(View.VISIBLE);

            showInviteBtn(viewHolder.invitationLayout, student);

            viewHolder.assignmentLayout.setBackgroundResource(R.color.white);
            viewHolder.mainLayout.setBackgroundResource(R.drawable.blue_rounded_border);
            viewHolder.iconLayout.setBackgroundResource(R.drawable.background_home_menu_blue);
            viewHolder.student_name.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));

            Preferences.save(General.NOTIFICATION_BHS_ID, 0);
        }

        viewHolder.assignmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.subAssignmentLayout.setVisibility(View.VISIBLE);

                showInviteBtn(viewHolder.invitationLayout, student);

                viewHolder.assignmentLayout.setBackgroundResource(R.color.white);
                viewHolder.mainLayout.setBackgroundResource(R.drawable.blue_rounded_border);
                viewHolder.iconLayout.setBackgroundResource(R.drawable.background_home_menu_blue);
                viewHolder.student_name.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
            }
        });

        viewHolder.downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.VISIBLE);
                viewHolder.downArrow.setVisibility(View.GONE);
                viewHolder.subAssignmentLayout.setVisibility(View.VISIBLE);

                showInviteBtn(viewHolder.invitationLayout, student);

                viewHolder.assignmentLayout.setBackgroundResource(R.color.white);
                viewHolder.mainLayout.setBackgroundResource(R.drawable.blue_rounded_border);
                viewHolder.iconLayout.setBackgroundResource(R.drawable.background_home_menu_blue);
                viewHolder.student_name.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));

            }
        });

        viewHolder.upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.upArrow.setVisibility(View.GONE);
                viewHolder.downArrow.setVisibility(View.VISIBLE);
                viewHolder.subAssignmentLayout.setVisibility(View.GONE);
                viewHolder.invitationLayout.setVisibility(View.GONE);

                viewHolder.assignmentLayout.setBackgroundResource(R.color.white);
                viewHolder.mainLayout.setBackgroundResource(R.color.white);
                viewHolder.iconLayout.setBackgroundResource(R.color.text_color_tertiary);
                viewHolder.student_name.setTextColor(ContextCompat.getColor(activity, R.color.black));
            }
        });

        viewHolder.viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showParentDetails(student);
            }
        });

        viewHolder.invitationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.save(General.NOTIFICATION_BHS_ID, student.getStud_id());
                callParentStudentInviationAPI(student, General.STUDENT_INVITATION);
            }
        });

        return view;
    }

    private void showInviteBtn(LinearLayout invitationLayout, Student student) {
        if (student.getSend_invite_student() == 1 && student.getStudent_signup() == 0) {
            invitationLayout.setVisibility(View.VISIBLE);
        } else {
            invitationLayout.setVisibility(View.GONE);
        }

    }

    private void callParentStudentInviationAPI(Student student, String action) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.ID, String.valueOf(student.getStud_id()));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_ASSIGNMENT;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    final JsonObject jsonAddJournal = jsonObject.getAsJsonObject(action);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        studentlListAdapterListener.refreshListData();
                    } else {
                        Toast.makeText(activity, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }

                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showParentDetails(final Student student) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.parent_details_dialog);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView fullName = (TextView) dialog.findViewById(R.id.full_name_txt);
        final TextView email = (TextView) dialog.findViewById(R.id.email_txt);
        final TextView dob = (TextView) dialog.findViewById(R.id.dob_txt);
        final TextView sendInvitation = (TextView) dialog.findViewById(R.id.send_invitation);
        final TextView statusInvitation = (TextView) dialog.findViewById(R.id.invitation_status);
        final LinearLayout statusLayout = (LinearLayout) dialog.findViewById(R.id.status);
        sendInvitation.setVisibility(View.GONE);
        statusLayout.setVisibility(View.GONE);

        if (student.getParent_email().length() > 0 && student.getParent_signup() == 0) {
            if (student.getSend_invite_parent() == 1) {
                sendInvitation.setVisibility(View.VISIBLE);
                statusLayout.setVisibility(View.GONE);
            } else {
                sendInvitation.setVisibility(View.GONE);
                statusLayout.setVisibility(View.VISIBLE);
            }
        }

        if (student.getParent_name().length() > 0) {
            fullName.setText(student.getParent_name());
        } else {
            fullName.setText("N/A");
        }

        if (student.getParent_email().length() > 0) {
            email.setText(student.getParent_email());
        } else {
            email.setText("N/A");
        }

        if (student.getParent_dob().length() > 0) {
            dob.setText(GetTime.dateCaps(student.getParent_dob()));
        } else {
            dob.setText("N/A");
        }

        sendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put(General.ACTION, General.PARENT_INVITATION);
                requestMap.put(General.ID, String.valueOf(student.getStud_id()));

                String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_ASSIGNMENT;

                RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
                if (requestBody != null) {
                    try {
                        String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                        if (response != null) {
                            JsonObject jsonObject = GetJson_.getJson(response);
                            final JsonObject jsonAddJournal = jsonObject.getAsJsonObject(General.PARENT_INVITATION);
                            if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                                Toast.makeText(activity, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                                statusInvitation.setVisibility(View.VISIBLE);
                                sendInvitation.setVisibility(View.GONE);
                                studentlListAdapterListener.refreshListData();
                            } else {
                                Toast.makeText(activity, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                            }

                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dialog.show();
    }

    public interface StudentlListAdapterListener {
        void refreshListData();
    }

    private class ViewHolder {
        TextView student_name, grade, statusTxt, status, assignmentDate, addedBy, emailTxt, dob, role, viewDetails;
        ImageView downArrow, upArrow;
        RelativeLayout assignmentLayout;
        LinearLayout subAssignmentLayout, mainLayout, iconLayout, invitationLayout;
    }
}