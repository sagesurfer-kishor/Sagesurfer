package com.modules.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 27-09-2017
 * Last Modified on 13-12-2017
 */

public class CgDashboardFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CgDashboardFragment.class.getSimpleName();
    private String consumer_id = "0";
    private ArrayList<Goal_> goalArrayList;

    private TextView boxOneText, boxTwoText, boxThreeText, boxThreeTag, boxFourText;
    private TextView goalOne, goalTwo, goalThree, goalFour;
    private TextView warningOne, warningTwo, warningThree, warningFour, goalWarning;
    private ImageView imageOne, imageTwo, imageThree, imageFour;
    private AppCompatImageView arrowTwo;
    private LinearLayout goalGridView;

    private MainActivityInterface mainActivityInterface;
    private Activity activity;

    // Constructor to fragment
    public static CgDashboardFragment newInstance(String _id) {
        CgDashboardFragment myFragment = new CgDashboardFragment();
        Bundle args = new Bundle();
        args.putString(General.ID, _id);
        myFragment.setArguments(args);
        return myFragment;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.cg_dashboard_fragment_layout, null);

        activity = getActivity();
        goalArrayList = new ArrayList<>();

        boxOneText = (TextView) view.findViewById(R.id.cg_dash_box_one_tag_one);
        boxTwoText = (TextView) view.findViewById(R.id.cg_dash_box_two_tag_one);
        boxThreeText = (TextView) view.findViewById(R.id.cg_dash_box_three_tag_one);
        boxThreeTag = (TextView) view.findViewById(R.id.cg_dash_box_three_tag_two);
        boxFourText = (TextView) view.findViewById(R.id.cg_dash_box_four_tag_one);

        arrowTwo = (AppCompatImageView) view.findViewById(R.id.cg_dash_box_two_icon);

        goalOne = (TextView) view.findViewById(R.id.my_goal_item_name_one);
        goalTwo = (TextView) view.findViewById(R.id.my_goal_item_name_two);
        goalThree = (TextView) view.findViewById(R.id.my_goal_item_name_three);
        goalFour = (TextView) view.findViewById(R.id.my_goal_item_name_four);

        warningOne = (TextView) view.findViewById(R.id.my_goal_item_caution_one);
        warningTwo = (TextView) view.findViewById(R.id.my_goal_item_caution_two);
        warningThree = (TextView) view.findViewById(R.id.my_goal_item_caution_three);
        warningFour = (TextView) view.findViewById(R.id.my_goal_item_caution_four);
        goalWarning = (TextView) view.findViewById(R.id.care_giver_dashboard_goal_warning);

        imageOne = (ImageView) view.findViewById(R.id.my_goal_item_icon_one);
        imageTwo = (ImageView) view.findViewById(R.id.my_goal_item_icon_two);
        imageThree = (ImageView) view.findViewById(R.id.my_goal_item_icon_three);
        imageFour = (ImageView) view.findViewById(R.id.my_goal_item_icon_four);

        goalGridView = (LinearLayout) view.findViewById(R.id.my_goal_view_grid);

        Bundle data = getArguments();
        if (data.containsKey(General.ID)) {
            consumer_id = data.getString(General.ID);
            getGoals();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.dashboard));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_listview:
                break;
        }
    }

    // set data to respective field from fetch content
    private void setData(String functional_status, String engagement, String engagement_arrow,
                         long last_contact, String last_contact_with, long platform_last_visit) {
        setArrow(engagement_arrow);
        boxOneText.setText(functional_status);
        boxTwoText.setText(engagement);
        boxThreeText.setText(GetTime.wallTime(last_contact));
        String lastContacted = "Last Contact with " + last_contact_with;
        boxThreeTag.setText(lastContacted);
        boxFourText.setText(GetTime.wallTime(platform_last_visit));
    }

    // set up/down arrow for respective boxes
    private void setArrow(String engagement) {
        if (engagement.equalsIgnoreCase("up") || engagement.equalsIgnoreCase("high") ||
                engagement.equalsIgnoreCase("Increasing")) {
            arrowTwo.setImageResource(R.drawable.vi_up_solid_arrow_white);
        } else {
            arrowTwo.setImageResource(R.drawable.vi_down_solid_arrow_white);
        }
    }

    // Make network call to get goals for selected consumer
    private void getGoals() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("youth_id", consumer_id);
        requestMap.put(General.ACTION, Actions_.PERSONAL_GOAL);
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CARE_GIVER_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getObject(response, "boxes");
                    String functional_status = jsonObject.get("functional_status").getAsString();
                    String engagement = jsonObject.get("engagement").getAsString();
                    String engagement_arrow = jsonObject.get("engagement_arrow").getAsString();
                    long last_contact = jsonObject.get("last_contact_timestamp").getAsLong();
                    String last_contact_with = jsonObject.get("last_contact_with").getAsString();
                    long platform_last_visit = jsonObject.get("platform_last_visit_timestamp").getAsLong();

                    goalArrayList = SelfGoal_.parseSpams(response, "personal_goals",
                            activity.getApplicationContext(), TAG);
                    if (goalArrayList.size() == 4) {
                        goalWarning.setVisibility(View.GONE);
                        goalGridView.setVisibility(View.VISIBLE);
                        setMyGoal();
                    } else {
                        goalGridView.setVisibility(View.GONE);
                        goalWarning.setText(activity.getApplicationContext().getResources()
                                .getString(R.string.cg_dashboard_goal_warning));
                        goalWarning.setVisibility(View.VISIBLE);
                    }
                    setData(functional_status, engagement, engagement_arrow, last_contact,
                            last_contact_with, platform_last_visit);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //set goal data in grid format
    private void setMyGoal() {
        Context context = activity.getApplicationContext();

        goalOne.setText(goalArrayList.get(0).getName());
        goalTwo.setText(goalArrayList.get(1).getName());
        goalThree.setText(goalArrayList.get(2).getName());
        goalFour.setText(goalArrayList.get(3).getName());
        warningOne.setText(goalArrayList.get(0).getText());
        warningTwo.setText(goalArrayList.get(1).getText());
        warningThree.setText(goalArrayList.get(2).getText());
        warningFour.setText(goalArrayList.get(3).getText());
        warningOne.setTextColor(getTextColor(goalArrayList.get(0).getColor()));
        warningTwo.setTextColor(getTextColor(goalArrayList.get(1).getColor()));
        warningThree.setTextColor(getTextColor(goalArrayList.get(2).getColor()));
        warningFour.setTextColor(getTextColor(goalArrayList.get(3).getColor()));
        imageOne.setBackgroundResource(getBackgroundCircle(goalArrayList.get(0).getColor()));
        imageTwo.setBackgroundResource(getBackgroundCircle(goalArrayList.get(1).getColor()));
        imageThree.setBackgroundResource(getBackgroundCircle(goalArrayList.get(2).getColor()));
        imageFour.setBackgroundResource(getBackgroundCircle(goalArrayList.get(3).getColor()));

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(context)
                .load(goalArrayList.get(0).getImage())
                .thumbnail(0.5f)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_goal_placeholder)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL) //.RESULT
                        .transform(new CircleTransform(context)))
                .into(imageOne);
        Glide.with(context)
                .load(goalArrayList.get(1).getImage())
                .thumbnail(0.5f)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_goal_placeholder)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(context)))
                .transition(withCrossFade())
                .into(imageTwo);
        Glide.with(context)
                .load(goalArrayList.get(2).getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_goal_placeholder)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(context)))
                .into(imageThree);
        Glide.with(context)
                .load(goalArrayList.get(3).getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_goal_placeholder)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(context)))
                .into(imageFour);
    }

    // draw background circles to goals based on their status
    private int getBackgroundCircle(String color) {
        if (color.equalsIgnoreCase("red")) {
            return R.drawable.goal_red_circle;
        } else if (color.equalsIgnoreCase("green")) {
            return R.drawable.goal_green_circle;
        }
        return R.drawable.primary_circle;
    }

    // text color to goal name/status based on their status
    private int getTextColor(String color) {
        if (color.equalsIgnoreCase("red")) {
            return activity.getApplicationContext().getResources().getColor(R.color.self_goal_red);
        } else if (color.equalsIgnoreCase("green")) {
            return activity.getApplicationContext().getResources().getColor(R.color.self_goal_green);
        }
        return activity.getApplicationContext().getResources().getColor(R.color.self_goal_orange);
    }
}
