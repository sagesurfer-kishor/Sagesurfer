package com.modules.team;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.views.RoundedCornersTransformation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Kailash Karankal
 */

class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.MyViewHolder> {
    private final Context mContext;
    private final TeamListAdapterListener listener;
    private final ArrayList<Teams_> teamList, list;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        final TextView title, teamType;
        final TextView count, timestamp;
        final TextView warning, postedDate, visitedDate;
        final ImageView thumbnail;
        final RelativeLayout relativeLayoutItem;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.team_list_item_card_name);
            count = (TextView) view.findViewById(R.id.team_list_item_card_count);
            teamType = (TextView) view.findViewById(R.id.team_type);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            warning = (TextView) view.findViewById(R.id.team_list_item_warning);
            postedDate = (TextView) view.findViewById(R.id.posted_date);
            visitedDate = (TextView) view.findViewById(R.id.visited_date);
            thumbnail = (ImageView) view.findViewById(R.id.team_list_item_card_banner);
            relativeLayoutItem = (RelativeLayout) view.findViewById(R.id.relative_layout_team_list_item);
        }

        @Override
        public boolean onLongClick(View view) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    TeamListAdapter(Context mContext, ArrayList<Teams_> teamList, TeamListAdapterListener listener) {
        this.mContext = mContext;
        this.teamList = teamList;
        this.listener = listener;
        list = new ArrayList<>();
        list.addAll(teamList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_list_item_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Teams_ teams_ = teamList.get(position);
        if (teams_.getStatus() == 1) {
            holder.relativeLayoutItem.setVisibility(View.VISIBLE);
            holder.warning.setVisibility(View.GONE);

            if (teams_.getIs_joined_team() == 1) {
                holder.teamType.setText("Joined Team");
            } else {
                holder.teamType.setText("My Team");
            }

            if (teams_.getType() == 1) {
                holder.title.setText(ChangeCase.toTitleCase(teams_.getName()) + "(Facilitated)");
            } else {
                holder.title.setText(ChangeCase.toTitleCase(teams_.getName()) + "(Non-Facilitated)");
            }


            holder.count.setText(teams_.getMembers() + " Members" + "");

            if (teams_.getLastActivity().equalsIgnoreCase("N/A") || teams_.getLastVisit().equalsIgnoreCase("N/A")) {
                holder.postedDate.setText("N/A");
                holder.visitedDate.setText("N/A");
            } else {
                holder.postedDate.setText(getDate(Long.parseLong(teams_.getLastActivity())));
                holder.visitedDate.setText(getDate(Long.parseLong(teams_.getLastVisit())));
            }

            Glide.with(mContext)
                    .load(teams_.getBanner())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new RoundedCornersTransformation(mContext.getApplicationContext(), 10, 0)))
                    .into(holder.thumbnail);

            applyClickEvents(holder, position);
        } else {
            holder.relativeLayoutItem.setVisibility(View.GONE);
            holder.warning.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teamList.get(position).getStatus() == 1) {
                    listener.onItemClicked(position);
                }
            }
        });
    }

    interface TeamListAdapterListener {
        void onItemClicked(int position);
    }

    // filter team list based on search query
    void filterTeams(String searchString) {
        teamList.clear();
        if (searchString == null) {
            teamList.addAll(list);
            notifyDataSetChanged();
            return;
        }
        searchString = searchString.toLowerCase(Locale.getDefault());
        if (searchString.trim().length() <= 0 || searchString.equals("~")) {
            teamList.addAll(list);
        } else {
            for (Teams_ teams_ : list) {
                String firstName = teams_.getName();
                if (firstName.toLowerCase(Locale.getDefault()).contains(searchString)) {
                    teamList.add(teams_);
                }
            }
        }
        if (teamList.size() <= 0) {
            Teams_ teams_ = new Teams_();
            teams_.setStatus(2);
            teamList.add(teams_);
        }
        notifyDataSetChanged();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }
}