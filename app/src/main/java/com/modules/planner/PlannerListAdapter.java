package com.modules.planner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.DailyPlanner_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformGetTeamsTask;

import java.util.List;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 31-03-2018
 * Last Modified on
 */

public class PlannerListAdapter extends RecyclerView.Adapter<PlannerListAdapter.MyViewHolder> {
    private static final String TAG = PlannerListAdapter.class.getSimpleName();
    private final Context mContext;
    private final List<DailyPlanner_> dailyPlannerList;
    private final PlannerAdapterListener listener;
    private final Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView time, textViewLocation, textViewTaskListStatus;
        final TextView title;
        //final TextView complete;
        final AppCompatImageView icon/*, completeIcon*/, imageViewLocation;
        final LinearLayout mainContainer;
        final ImageView viewVertical;

        MyViewHolder(View view) {
            super(view);
            imageViewLocation = (AppCompatImageView) view.findViewById(R.id.imageview_dailyplanner_listitem_location);
            textViewLocation = (TextView) view.findViewById(R.id.textview_dailyplanner_listitem_location);
            textViewTaskListStatus = (TextView) view.findViewById(R.id.textview_tasklist_status);
            time = (TextView) view.findViewById(R.id.textview_dailyplanner_listitem_time);
            title = (TextView) view.findViewById(R.id.textview_dailyplanner_listitem_title);
            //completeIcon = (AppCompatImageView) view.findViewById(R.id.imageview_dailyplanner_listitem_complete);
            //complete = (TextView) view.findViewById(R.id.textview_dailyplanner_listitem_complete);

            icon = (AppCompatImageView) view.findViewById(R.id.imageview_dailyplanner_listitem);

            mainContainer = (LinearLayout) view.findViewById(R.id.linearlayout_dailyplanner_listitem);
            viewVertical = (ImageView) view.findViewById(R.id.view);
            imageViewLocation.setVisibility(View.GONE);
            textViewLocation.setVisibility(View.GONE);
        }
    }


    public PlannerListAdapter(Context mContext, List<DailyPlanner_> dailyPlannerList, PlannerAdapterListener listener, Activity activity) {
        this.mContext = mContext;
        this.dailyPlannerList = dailyPlannerList;
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return dailyPlannerList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_planner_list_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (dailyPlannerList.get(position).getStatus() == 1) {
            holder.mainContainer.setVisibility(View.VISIBLE);

            DailyPlanner_ dailyPlanner_ = dailyPlannerList.get(position);
            switch (dailyPlanner_.getType()) {
                case 1:
                    holder.time.setText(GetTime.wallTime(dailyPlanner_.getAnnouncement().getDate()));
                    //holder.title.setText(dailyPlanner_.getAnnouncement().getDescription());
                    holder.title.setText(mContext.getResources().getString(R.string.new_announcement_of_day));
                    break;
                case 2:
                    holder.time.setText(GetTime.wallTime(dailyPlanner_.getTask().getLast_updated()));
                    holder.title.setText(dailyPlanner_.getTask().getTitle());
                    break;
                case 3:
                    holder.time.setText(GetTime.wallTime(dailyPlanner_.getTask().getLast_updated()));
                    holder.title.setText(dailyPlanner_.getTask().getTitle());
                    break;
                case 4:
                    holder.time.setText(GetTime.wallTime(dailyPlanner_.getEvent().getTimestamp()));
                    holder.title.setText(dailyPlanner_.getEvent().getName());
                    if (dailyPlanner_.getEvent().getLocation() != null && dailyPlanner_.getEvent().getLocation().length() > 0) {
                        holder.imageViewLocation.setVisibility(View.VISIBLE);
                        holder.textViewLocation.setVisibility(View.VISIBLE);
                        holder.textViewLocation.setText(dailyPlanner_.getEvent().getLocation());
                    }
                    break;
                case 5:
                    holder.time.setText(GetTime.wallTime(dailyPlanner_.getGoal().getLast_updated()));
                    holder.title.setText(dailyPlanner_.getGoal().getName());
                    break;
            }
            //holder.icon.setImageResource(getIcon(dailyPlanner_.getType()));
            if (dailyPlanner_.getType() == 1) {
                int color = Color.parseColor("#548efa"); //announcement- blue
                holder.mainContainer.setBackground(activity.getResources().getDrawable(R.drawable.planner_announcement_rounded_rectangle));
                holder.viewVertical.setImageResource(R.drawable.planner_announcement_left_rounded_rectangle);
                holder.icon.setColorFilter(color);
                holder.icon.setImageResource(R.drawable.vi_drawer_announcements);
            } else if (dailyPlanner_.getType() == 2 || dailyPlanner_.getType() == 3) {
                int color = Color.parseColor("#5b4c45"); //tasklist- brown
                holder.mainContainer.setBackground(activity.getResources().getDrawable(R.drawable.planner_tasklist_rounded_rectangle));
                holder.viewVertical.setImageResource(R.drawable.planner_tasklist_left_rounded_rectangle);
                holder.icon.setColorFilter(color);
                holder.icon.setImageResource(R.drawable.vi_home_task_list);
            } else if (dailyPlanner_.getType() == 4) {
                int color = Color.parseColor("#51d166"); //event- green
                holder.mainContainer.setBackground(activity.getResources().getDrawable(R.drawable.planner_event_rounded_rectangle));
                holder.viewVertical.setImageResource(R.drawable.planner_event_left_rounded_rectangle);
                holder.icon.setColorFilter(color);
                holder.icon.setImageResource(R.drawable.vi_caseload_events);
            } else if (dailyPlanner_.getType() == 5) {
                int color = Color.parseColor("#e85f9d"); //self goal- pink
                holder.mainContainer.setBackground(activity.getResources().getDrawable(R.drawable.planner_selfgoal_rounded_rectangle));
                holder.viewVertical.setImageResource(R.drawable.planner_selfgoal_left_rounded_rectangle);
                holder.icon.setColorFilter(color);
                holder.icon.setImageResource(R.drawable.vi_home_self_goal);
            } else {
                holder.icon.setImageResource(R.drawable.vi_daily_planner_icon1);
            }
            holder.time.setTextColor(getColor(dailyPlanner_.getType()));
            setActionVisibility(dailyPlanner_, holder);
            applyClickEvents(holder, position);
        } else {
            holder.mainContainer.setVisibility(View.GONE);
        }
    }

    private int getColor(int type) {
        if (type == 1) {
            return mContext.getResources().getColor(R.color.announcement_icon);
        } else if (type == 2 || type == 3) {
            return mContext.getResources().getColor(R.color.tasklist_icon);
        } else if (type == 4) {
            return mContext.getResources().getColor(R.color.events_icon);
        } else if (type == 5) {
            return mContext.getResources().getColor(R.color.selfgoal_icon);
        }
        return mContext.getResources().getColor(R.color.colorPrimary);
    }

    private void setActionVisibility(DailyPlanner_ dailyPlanner_, MyViewHolder holder) {
        if (dailyPlanner_.getType() == 2 || dailyPlanner_.getType() == 3) {
            holder.textViewTaskListStatus.setVisibility(View.VISIBLE);
            holder.textViewTaskListStatus.setText(dailyPlanner_.getTask().getToDoStatus());
            holder.textViewTaskListStatus.setTextColor(GetColor.getTaskStatusColor(dailyPlanner_.getTask().getToDoStatus(), mContext.getApplicationContext()));
            if (dailyPlanner_.getTask().getIsOwner() == 1) {
                holder.textViewTaskListStatus.setBackgroundResource(R.drawable.ic_down_arrow_ternary);
            } else {
                holder.textViewTaskListStatus.setBackgroundResource(0);
            }
        } else {
            holder.textViewTaskListStatus.setVisibility(View.GONE);
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(position, "mainContainer");
            }
        });

        holder.imageViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(position, "location");
            }
        });

        holder.textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(position, "location");
            }
        });

        holder.textViewTaskListStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dailyPlannerList.get(position).getTask().getIsOwner() == 1) {
                    String date = GetTime.dueTime(dailyPlannerList.get(position).getTask().getDate());
                    if (date.contains("Due") || date.contains("due")) {
                        showStatusPopUp(view, position);
                    } else {
                        SubmitSnackResponse.showSnack(1, "Task Expired", view, activity.getApplicationContext());
                    }
                }
            }
        });
    }

    //open task status menu
    private void showStatusPopUp(final View view, final int position) {
        PopupMenu popup = new PopupMenu(mContext, view);
        popup.getMenuInflater().inflate(R.menu.task_list_status_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String old_status = dailyPlannerList.get(position).getTask().getToDoStatus();
                if (!old_status.equalsIgnoreCase(item.getTitle().toString())) {
                    int status = PerformGetTeamsTask.changeStatus(mContext, TAG, item.getTitle().toString(), dailyPlannerList.get(position).getTask().getId(), activity);
                    showResponses(status, view);
                    if (status == 1) {
                        dailyPlannerList.get(position).getTask().setToDoStatus(item.getTitle().toString());
                        notifyDataSetChanged();
                    }
                }
                return true;
            }
        });
        popup.show();
    }

    private void showResponses(int status, View view) {
        String message = "";
        if (status == 1) {
            message = mContext.getResources().getString(R.string.successful);
        } else if (status == 2) {
            message = mContext.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, mContext.getApplicationContext());
    }

    public interface PlannerAdapterListener {
        void onItemClicked(int position, String itemView);
    }

    /*
    private int getIcon(int type) {
        if (type == 1) {
            return R.drawable.vi_drawer_announcements;
        }
        if (type == 2 || type == 3) {
            return R.drawable.vi_home_task_list;
        }
        if (type == 4) {
            return R.drawable.vi_caseload_events;
        }

        if (type == 5) {
            return R.drawable.vi_home_self_goal;
        }

        return R.drawable.vi_daily_planner_icon1;
    }

    // on click for list item button for accept/decline
    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.linearlayout_dailyplanner_listitem:
                    String date = caseloadPeerNoteList.get(position).getVisit_date();
                    String[] splitStr = date.trim().split("\\s+");
                    Intent detailsIntent = new Intent(mContext, PeerNoteDetailsActivity.class);
                    detailsIntent.putExtra(General.ID, caseloadPeerNoteList.get(position).getId());
                    detailsIntent.putExtra(General.TYPE_OF_CONTACT, caseloadPeerNoteList.get(position).getContact_mode());
                    detailsIntent.putExtra(General.DURATION, caseloadPeerNoteList.get(position).getDuration());
                    detailsIntent.putExtra(General.DATE, splitStr[0]);
                    detailsIntent.putExtra(General.TIMESTAMP, splitStr[1]);
                    detailsIntent.putExtra(General.NOTE, caseloadPeerNoteList.get(position).getRemark());
                    activity.startActivity(detailsIntent, ActivityTransition.moveToNextAnimation(mContext));
                    break;
            }
        }
    };*/
}