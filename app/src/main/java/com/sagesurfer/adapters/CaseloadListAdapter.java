package com.sagesurfer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.selfcare.ListItem;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Caseload_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash on 5/31/2018.
 */

public class CaseloadListAdapter extends RecyclerView.Adapter<CaseloadListAdapter.MyViewHolder> {
    private static final String TAG = CaseloadListAdapter.class.getSimpleName();
    public final ArrayList<Caseload_> caseloadList;
    private final Context mContext;
    public final CaseloadListAdapterListener caseloadListAdapterListener;
    int color;
    private ArrayList<ListItem> settingArrayList = new ArrayList<>();

    public CaseloadListAdapter(Context mContext, ArrayList<Caseload_> caseloadList, CaseloadListAdapterListener caseloadListAdapterListener) {
        this.caseloadList = caseloadList;
        this.mContext = mContext;
        this.caseloadListAdapterListener = caseloadListAdapterListener;

        String settingListString = Preferences.get(General.SETTING_LIST);
        Gson gson = new Gson();
        Type entityType = new TypeToken<ArrayList<ListItem>>() {
        }.getType();
        settingArrayList = gson.fromJson(settingListString, entityType);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView teamText;
        final TextView statusText;
        final TextView dobText;
        final TextView parentName;
        final TextView contactedText;
        final ImageView profileImage;
        final LinearLayout statusLinearLayout;
        final ImageView statusImage, textviewCaseloaTeamImg;
        final RelativeLayout detailsRelativeLayout;
        final RelativeLayout actionsRelativeLayout;
        final LinearLayout summaryLinearLayout;
        final LinearLayout eventsLinearLayout;
        final LinearLayout planLinearLayout;
        final LinearLayout teamLinearLayout;
        final LinearLayout contactLinearLayout;
        final AppCompatImageView summaryImageView;
        final AppCompatImageView eventsImageView;
        final AppCompatImageView planImageView;
        final AppCompatImageView teamImageView;
        final AppCompatImageView contactImageView;
        final TextView eventTextView;
        final TextView planTextView;

        MyViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.textview_caseload_name);
            teamText = (TextView) view.findViewById(R.id.textview_caseload_team);
            statusText = (TextView) view.findViewById(R.id.textview_caseload_status);
            dobText = (TextView) view.findViewById(R.id.textview_caseload_dob);
            parentName = (TextView) view.findViewById(R.id.textview_caseload_parent_name);
            contactedText = (TextView) view.findViewById(R.id.textview_caseload_contacted_at);
            profileImage = (ImageView) view.findViewById(R.id.imageview_profile);
            statusLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_status);
            statusImage = (ImageView) view.findViewById(R.id.imageview_clear_search_text);
            textviewCaseloaTeamImg = (ImageView) view.findViewById(R.id.textview_caseload_team_img);
            detailsRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_details);
            actionsRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_actions);
            summaryLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_summary);
            eventsLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_events);
            planLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_plan);
            teamLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_team);
            contactLinearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_caseload_contact);
            summaryImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_summary);
            eventsImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_events);
            planImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_plan);
            teamImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_team);
            contactImageView = (AppCompatImageView) view.findViewById(R.id.imageview_caseload_contact);
            eventTextView = (TextView) view.findViewById(R.id.textview_caseload_events);
            planTextView = (TextView) view.findViewById(R.id.textview_caseload_plan);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.caseload_cardview_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        int color1 = Color.parseColor("#000000");
        viewHolder.textviewCaseloaTeamImg.setColorFilter(color1);

        viewHolder.actionsRelativeLayout.setVisibility(View.GONE);
        viewHolder.teamText.setTextColor(color1);
        viewHolder.teamText.setText(ChangeCase.toTitleCase(caseloadList.get(position).getGroup_name()));
        viewHolder.nameText.setText(ChangeCase.toTitleCase(caseloadList.get(position).getUsername()));
        viewHolder.contactedText.setText("Contacted - " + caseloadList.get(position).getContacted_last());

        Glide.with(mContext)
                .load(caseloadList.get(position).getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(caseloadList.get(position).getImage()))
                        .transform(new CircleTransform(mContext)))
                .into(viewHolder.profileImage);

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(mContext.getResources().getString(R.string.sage023))) {
            viewHolder.statusText.setVisibility(View.GONE);
            viewHolder.contactedText.setVisibility(View.GONE);
            viewHolder.dobText.setVisibility(View.VISIBLE);
            viewHolder.parentName.setVisibility(View.VISIBLE);


            if (caseloadList.get(position).getDob().equalsIgnoreCase("")) {
                viewHolder.dobText.setText("N/A");
            } else {
                viewHolder.dobText.setText(GetTime.dateValue(Long.parseLong(caseloadList.get(position).getLast_progress_date())));
            }

            if (caseloadList.get(position).getSchool_name().equals("")) {
                viewHolder.parentName.setText("Parent Name : " + "NA");
            } else {
                viewHolder.parentName.setText("Parent Name : " + ChangeCase.toTitleCase(caseloadList.get(position).getParent_name()));
            }

        } else
        /*Status ids:
        1: in_process;
        2: improving/emerging;
        3: worsening/support_needed;*/
            if (caseloadList.get(position).getType().equalsIgnoreCase(mContext.getResources().getString(R.string.worsening))
                    || caseloadList.get(position).getType().equalsIgnoreCase(mContext.getResources().getString(R.string.support_needed))) {
                int color = Color.parseColor("#fb2021"); //red
                viewHolder.statusImage.setColorFilter(color);
                viewHolder.statusImage.setImageResource(R.drawable.vi_warning);
                viewHolder.statusText.setTextColor(color);
                viewHolder.statusText.setText(caseloadList.get(position).getType());
            } else if (caseloadList.get(position).getType().equalsIgnoreCase(mContext.getResources().getString(R.string.improving))
                    || caseloadList.get(position).getType().equalsIgnoreCase(mContext.getResources().getString(R.string.emerging))) {
                int color = Color.parseColor("#22d369"); //green
                viewHolder.statusImage.setColorFilter(color);
                viewHolder.statusImage.setImageResource(R.drawable.vi_check_green);
                viewHolder.statusText.setTextColor(color);
                viewHolder.statusText.setText(caseloadList.get(position).getType());
            } else {
                int color = Color.parseColor("#f89d59"); //orange
                viewHolder.statusImage.setColorFilter(color);
                viewHolder.statusImage.setImageResource(R.drawable.vi_check_white);
                viewHolder.statusText.setTextColor(color);
                viewHolder.statusText.setText(caseloadList.get(position).getType());
            }

        /*if(caseloadList.get(position).isSelected()) {
            viewHolder.actionsRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.actionsRelativeLayout.setVisibility(View.GONE);
        }

        viewHolder.summaryImageView.setColorFilter(color);
        viewHolder.summaryImageView.setImageResource(R.drawable.vi_caseload_case_summary);

        //to get selected items from settingArrayList and set caseload 2nd(event) and 3rd(plan) action view acc. to caseload setting selection
        String position1 = "", position2 = "";
        viewHolder.eventsImageView.setColorFilter(color);
        for(int i = 0; i < settingArrayList.size(); i++) {
            if (settingArrayList.get(i).getSelected()) {
                if(position1.length() == 0) {
                    position1 = settingArrayList.get(i).getId();
                } else {
                    position2 = settingArrayList.get(i).getId();
                }
            }
        }

        setCaseloadItems(position1,  viewHolder.eventsImageView, viewHolder.eventTextView);
        setCaseloadItems(position2,  viewHolder.planImageView, viewHolder.planTextView);*/
        /*viewHolder.eventsImageView.setColorFilter(color);
        viewHolder.eventsImageView.setImageResource(R.drawable.vi_caseload_events);

        viewHolder.planImageView.setColorFilter(color);
        viewHolder.planImageView.setImageResource(R.drawable.vi_caseload_plan);*/

        /*viewHolder.teamImageView.setColorFilter(color);
        viewHolder.teamImageView.setImageResource(R.drawable.vi_drawer_teams);

        viewHolder.contactImageView.setColorFilter(color);
        viewHolder.contactImageView.setImageResource(R.drawable.vi_caseload_contact);*/

        /*viewHolder.detailsRelativeLayout.setTag(position);
        viewHolder.detailsRelativeLayout.setOnClickListener(onClick);*/

        viewHolder.detailsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caseloadListAdapterListener.onDetailsLayoutClicked(caseloadList.get(position));
            }
        });

        /*viewHolder.statusLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Teams_ team_ = new Teams_();
                team_.setId(caseloadList.get(position).getGroup_id());
                team_.setName(caseloadList.get(position).getGroup_name());
                caseloadListAdapterListener.onStatusButtonClicked(position, team_);
            }
        });

        viewHolder.summaryLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Teams_ team_ = new Teams_();
                team_.setId(caseloadList.get(position).getGroup_id());
                team_.setName(caseloadList.get(position).getGroup_name());
                caseloadListAdapterListener.onSummaryButtonClicked(position, team_);
            }
        });

        viewHolder.eventsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Teams_ team_ = new Teams_();
                team_.setId(caseloadList.get(position).getGroup_id());
                team_.setName(caseloadList.get(position).getGroup_name());
                caseloadListAdapterListener.onEventsButtonClicked(position, team_);
            }
        });

        viewHolder.planLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Teams_ team_ = new Teams_();
                team_.setId(caseloadList.get(position).getGroup_id());
                team_.setName(caseloadList.get(position).getGroup_name());
                caseloadListAdapterListener.onPlanButtonClicked(position, team_);
            }
        });

        viewHolder.teamLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Teams_ team_ = new Teams_();
                team_.setId(caseloadList.get(position).getGroup_id());
                team_.setName(caseloadList.get(position).getGroup_name());
                team_.setBanner(caseloadList.get(position).getBanner_img());
                caseloadListAdapterListener.onTeamButtonClicked(position, team_);
            }
        });

        viewHolder.contactLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Teams_ team_ = new Teams_();
                team_.setId(caseloadList.get(position).getGroup_id());
                team_.setName(caseloadList.get(position).getGroup_name());
                caseloadListAdapterListener.onContactButtonClicked(position, team_);
            }
        });*/
    }

    public interface CaseloadListAdapterListener {
        void onDetailsLayoutClicked(Caseload_ caseload_);

        /*void onStatusButtonClicked(int position, Teams_ team_);

        void onSummaryButtonClicked(int position, Teams_ team_);

        void onEventsButtonClicked(int position, Teams_ team_);

        void onPlanButtonClicked(int position, Teams_ team_);

        void onTeamButtonClicked(int position, Teams_ team_);

        void onContactButtonClicked(int position, Teams_ team_);*/
    }

    /*// on click for list item button for accept/decline
    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.relativelayout_caseload_details:
                    showHideActionsLayout(position, v);
                    break;
            }
        }
    };*/

    //Show/Hide Actions relative layout
    private void showHideActionsLayout(int position, View view) {
        //isShowing flag in caseload arraylist. onClick of position if isShowing false than make isShowing true and actionsRelativeLayout visible
        if (caseloadList.get(position).isSelected()) {
            caseloadList.get(position).setSelected(false);
        } else {
            for (int i = 0; i < caseloadList.size(); i++) {
                //hide all actionsRelativeLayout
                caseloadList.get(i).setSelected(false);
                //viewHolder.actionsRelativeLayout.setVisibility(View.GONE);
            }
            caseloadList.get(position).setSelected(true);
            //viewHolder.actionsRelativeLayout.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return caseloadList.get(position).getUser_id();
    }

    @Override
    public int getItemCount() {
        return caseloadList.size();
    }

    //set caseload 2nd(event) and 3rd(plan) action view acc. to caseload setting selection
    public void setCaseloadItems(String position, AppCompatImageView imageView, TextView textView) {
        if (position.equalsIgnoreCase("0")) {
            imageView.setColorFilter(color);
            imageView.setImageResource(R.drawable.vi_caseload_events);
            textView.setText(mContext.getString(R.string.events));
        } else if (position.equalsIgnoreCase("1")) {
            imageView.setColorFilter(color);
            imageView.setImageResource(R.drawable.vi_caseload_plan);
            textView.setText(mContext.getString(R.string.crisis_plan));
        } else if (position.equalsIgnoreCase("2")) {
            imageView.setColorFilter(color);
            imageView.setImageResource(R.drawable.vi_home_task_list);
            textView.setText(mContext.getString(R.string.task_list));
        } else if (position.equalsIgnoreCase("3")) {
            imageView.setColorFilter(color);
            imageView.setImageResource(R.drawable.vi_home_adherence);
            textView.setText(mContext.getString(R.string.adherence));
        } else if (position.equalsIgnoreCase("4")) {
            imageView.setColorFilter(color);
            imageView.setImageResource(R.drawable.vi_up_solid_arrow_white);//vi_up_solid_arrow_white
            //textView.setText(mContext.getString(R.string.progress_note));
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(mContext.getResources().getString(R.string.sage015))) {
                textView.setText(mContext.getString(R.string.note));
            } else {
                textView.setText(mContext.getString(R.string.progress_note));
            }
        } else if (position.equalsIgnoreCase("5")) {
            imageView.setColorFilter(color);
            imageView.setImageResource(R.drawable.vi_drawer_mood);
            textView.setText(mContext.getString(R.string.mood));
        } else if (position.equalsIgnoreCase("6")) {
            imageView.setColorFilter(color);
            imageView.setImageResource(R.drawable.vi_warning);
            textView.setText(mContext.getString(R.string.status));
        }
    }
}
