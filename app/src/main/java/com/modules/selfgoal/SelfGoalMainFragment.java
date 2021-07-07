package com.modules.selfgoal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.SelfGoal_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 */

public class SelfGoalMainFragment extends Fragment implements SelfGoalListAdapter.GoalListAdapterListener {
    private static final String TAG = SelfGoalMainFragment.class.getSimpleName();

    @BindView(R.id.textview_fragmentselfgoalmain_total)
    TextView textViewFragmentSelfGoalMainTotal;

    @BindView(R.id.textview_fragmentselfgoalmain_missed)
    TextView textViewFragmentSelfGoalMainMissed;

    @BindView(R.id.textview_fragmentselfgoalmain_active)
    TextView textViewFragmentSelfGoalMainActive;

    @BindView(R.id.textview_fragmentselfgoalmain_completed)
    TextView textViewFragmentSelfGoalMainCompleted;

    @BindView(R.id.recycleview_fragmentselfgoalmain)
    RecyclerView recycleViewFragmentSelfGoalMain;

    @BindView(R.id.fab_fragmentselfgoalmain)
    FloatingActionButton fabFragmentSelfGoalMain;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;
    private ArrayList<Goal_> goalArrayList = new ArrayList<>();
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.hideHelpIcon(true);
            /*if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                mainActivity.hideLogBookIcon(true);
            } else {
                mainActivity.hideLogBookIcon(false);
            }*/
            mainActivity.hideLogBookIcon(true);

        }
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

        View view = inflater.inflate(R.layout.fragment_self_goal_main_layout, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

//        fabFragmentSelfGoalMain.setVisibility(View.GONE);
        fabFragmentSelfGoalMain.setImageResource(R.drawable.fab_add);

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            fabFragmentSelfGoalMain.setVisibility(View.GONE);
        } else {
            fabFragmentSelfGoalMain.setVisibility(View.VISIBLE);
        }

        // set a LinearLayoutManager with default vertical orientaion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recycleViewFragmentSelfGoalMain.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        // call the constructor of CustomAdapter to send the reference and data to Adapter
        SelfGoalListAdapter goalListAdapter = new SelfGoalListAdapter(activity, goalArrayList, this);
        recycleViewFragmentSelfGoalMain.setAdapter(goalListAdapter); // set the Adapter to RecyclerView

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getGoals();
                        getAllGoal();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage031))) {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.daily_dosing));

        } else {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.self_goal));
        }

        mainActivityInterface.setToolbarBackgroundColor();
        getGoals();
        getAllGoal();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fab_fragmentselfgoalmain)
    public void onButtonClick(View view) {
        Intent createIntent = new Intent(activity.getApplicationContext(), AddGoalActivity.class);
        startActivity(createIntent);
    }

    // set goal counters to respective text fields
    private void setGoalCounter(String completed, String miss_out, String running, String total) {
        textViewFragmentSelfGoalMainCompleted.setText(completed);
        textViewFragmentSelfGoalMainMissed.setText(miss_out);
        textViewFragmentSelfGoalMainActive.setText(running);
        textViewFragmentSelfGoalMainTotal.setText(total);
    }

    // make network call to fetch goal counters based on goal status
    private void getGoals() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.MY_GOAL);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        String response;
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("getGoals", response);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, "counter");
                    if (jsonArray != null) {
                        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                        if (jsonObject.has(General.STATUS) && jsonObject.get(General.STATUS).getAsInt() == 1) {
                            String total = jsonObject.get("total").getAsString();
                            String running = jsonObject.get("running").getAsString();
                            String completed = jsonObject.get("completed").getAsString();
                            String miss_out = jsonObject.get("miss_out").getAsString();

                            setGoalCounter(completed, miss_out, running, total);
                        } else {
                            setGoalCounter("0", "0", "0", "0");
                        }
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
                Log.e("ResponseError", "" + e.getMessage());
            }
        }
//        getAllGoal();
    }

    // make network call to fetch all goals
    private void getAllGoal() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ALL_GOAL);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("ResponseGetAllGoal", "" + response);
                if (response != null) {
                    goalArrayList = SelfGoal_.parseSpams(response, Actions_.ALL_GOAL, activity.getApplicationContext(), TAG);
                    if (goalArrayList.size() > 0) {
                        if (goalArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            SelfGoalListAdapter goalListAdapter = new SelfGoalListAdapter(activity, goalArrayList, this);
                            recycleViewFragmentSelfGoalMain.setAdapter(goalListAdapter);
                        } else {
                            showError(true, 2);
                        }
                    }
                } else {
                    showError(true, 2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recycleViewFragmentSelfGoalMain.setVisibility(View.GONE);
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
                errorText.setText(GetErrorResources.getMessage(status, activity));
            } else {
                errorText.setText("Default self goal's are not set by Coach yet. ");
            }
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recycleViewFragmentSelfGoalMain.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGoalItemClicked(int position, Goal_ goal_) {
        Log.e(TAG, "position: " + position + " name: " + goal_.getName());
        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
            Intent addIntent = new Intent(activity.getApplicationContext(), SenjamSelfGoalDetailActivity.class);
            addIntent.putExtra(Actions_.MY_GOAL, goal_);
            activity.startActivity(addIntent);
        } else {
            Intent addIntent = new Intent(activity.getApplicationContext(), SageSelfGoalDetailActivity.class);
            addIntent.putExtra(Actions_.MY_GOAL, goal_);
            activity.startActivity(addIntent);
        }

    }
}
