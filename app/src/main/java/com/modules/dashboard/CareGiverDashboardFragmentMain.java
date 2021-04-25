package com.modules.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.modules.consent.Consumer_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Consent_;
import com.sagesurfer.selectors.ConsumerSelectorDialog;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 12-07-2017
 * Last Modified on 13-12-2017
 */

/*
* This is main file of care giver dashboard from which rest of the sub modules get called
*/

public class CareGiverDashboardFragmentMain extends Fragment implements View.OnClickListener {

    private static final String TAG = CareGiverDashboardFragmentMain.class.getSimpleName();
    private String consumer_name = "";
    private int position = 0;
    private long consumer_id = 0;
    private ArrayList<Consumer_> consumerArrayList;

    private static FloatingActionMenu floatingActionsMenu;
    private TextView selectConsumer;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivityInterface = (MainActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " " + e.toString());
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.care_giver_dashboard_main, null);

        activity = getActivity();

        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.dashboard));
        //mainActivityInterface.hideRevealView();

        Preferences.initialize(activity.getApplicationContext());

        floatingActionsMenu = (FloatingActionMenu) view.findViewById(R.id.care_giver_dashboard_float_menu);
        floatingActionsMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mainActivityInterface.hideRevealView();
                if (floatingActionsMenu.isOpened()) {
                    floatingActionsMenu.close(true);
                } else {
                    floatingActionsMenu.open(true);
                }
            }
        });

        FloatingActionButton messageButton = (FloatingActionButton) view.findViewById(R.id.care_giver_dashboard_float_message);
        messageButton.setOnClickListener(this);
        FloatingActionButton noteButton = (FloatingActionButton) view.findViewById(R.id.care_giver_dashboard_float_note);
        noteButton.setOnClickListener(this);
        FloatingActionButton reportsButton = (FloatingActionButton) view.findViewById(R.id.care_giver_dashboard_float_report);
        reportsButton.setOnClickListener(this);
        FloatingActionButton dashboardButton = (FloatingActionButton) view.findViewById(R.id.care_giver_dashboard_float_dashboard);
        dashboardButton.setOnClickListener(this);

        selectConsumer = (TextView) view.findViewById(R.id.cg_dash_select_consumer_text);

        RelativeLayout consumerSelector = (RelativeLayout) view.findViewById(R.id.cg_dash_select_consumer);
        consumerSelector.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.care_giver_dashboard_float_dashboard:
                floatingActionsMenu.close(true);
                replaceFragment(getFragment(0));
                break;
            case R.id.care_giver_dashboard_float_report:
                floatingActionsMenu.close(true);
                replaceFragment(getFragment(1));
                break;
            case R.id.care_giver_dashboard_float_note:
                floatingActionsMenu.close(true);
                replaceFragment(getFragment(2));
                break;
            case R.id.care_giver_dashboard_float_message:
                floatingActionsMenu.close(true);
                replaceFragment(getFragment(3));
                break;
            case R.id.cg_dash_select_consumer:
                if (consumerArrayList == null || consumerArrayList.size() <= 0) {
                    ShowSnack.viewWarning(v, activity.getApplicationContext().getResources()
                            .getString(R.string.consumer_not_available), activity.getApplicationContext());
                } else {
                    showDialog();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (consumerArrayList == null || consumerArrayList.size() <= 0) {
            getConsumer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.dashboard));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getFragmentManager();
        Fragment old_frag = fm.findFragmentByTag(General.DASHBOARD);
        FragmentTransaction ft = fm.beginTransaction();
        if (old_frag != null) {
            ft.remove(old_frag);
        }
    }

    // get fragment based on menu button from float button
    private Fragment getFragment(int position) {
        this.position = position;
        if (consumer_id <= 0) {
            ShowToast.toast(activity.getApplicationContext().getResources()
                    .getString(R.string.select_consumer), activity.getApplicationContext());
            return null;
        }
        switch (position) {
            case 0:
                return CgDashboardFragment.newInstance("" + consumer_id);
            case 1:
                return ReportsFragment.newInstance("" + consumer_id);
            case 2:
                return NoteListFragment.newInstance("" + consumer_id);
            case 3:
                return MessageFragment.newInstance("" + consumer_id);
            default:
                return CgDashboardFragment.newInstance("" + consumer_id);
        }
    }

    // set consumer details
    private void setConsumer(int pos) {
        consumer_id = consumerArrayList.get(pos).getUser_id();
        consumer_name = consumerArrayList.get(pos).getUser_name();
        selectConsumer.setText(consumer_name);
        replaceFragment(getFragment(position));
    }

    // open consumer selector dialog that will return value
    @SuppressLint("CommitTransaction")
    private void showDialog() {
        //mainActivityInterface.hideRevealView();
        android.app.FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        android.app.Fragment frag = getActivity().getFragmentManager().findFragmentByTag(General.USER_LIST);
        if (frag != null) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment dialogFrag = new ConsumerSelectorDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(General.USER_LIST, consumerArrayList);
        dialogFrag.setArguments(bundle);
        dialogFrag.setTargetFragment(this, 1);
        dialogFrag.show(getFragmentManager().beginTransaction(), General.USER_LIST);
    }

    // change fragment
    private void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        //mainActivityInterface.hideRevealView();
        FragmentManager fm = getFragmentManager();
        Fragment old_frag = fm.findFragmentByTag(Actions_.PERSONAL_GOAL);
        FragmentTransaction ft = fm.beginTransaction();
        if (old_frag != null) {
            ft.remove(old_frag);
        }
        ft.replace(R.id.cg_dashboard_container, fragment, Actions_.PERSONAL_GOAL);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }

    // make call to fetch consumers list
    private void getConsumer() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.CONSUMER_LIST);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CARE_GIVER_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    consumerArrayList = Consent_.parseConsumer(response, Actions_.CONSUMER_LIST,
                            activity.getApplicationContext(), TAG);
                    if (consumerArrayList.size() > 0) {
                        setConsumer(0);
                    } else {
                        ShowToast.toast("Consumer not found", activity.getApplicationContext());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Get value return from consumer dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    String position = data.getStringExtra("position");
                    setConsumer(Integer.parseInt(position));
                }
                break;
        }
    }

    // save variable values when activity paused
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(General.ID, consumer_id);
        outState.putString(General.NAME, consumer_name);
        outState.putSerializable(General.USER_LIST, consumerArrayList);
        outState.putInt(General.POSITION, position);
    }

    // re-assign variable values when activity is resumed
    @SuppressWarnings("unchecked")
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            consumerArrayList = (ArrayList<Consumer_>) savedInstanceState.getSerializable(General.USER_LIST);
            consumer_id = savedInstanceState.getLong(General.ID);
            consumer_name = savedInstanceState.getString(General.NAME);
            position = savedInstanceState.getInt(General.POSITION);
            selectConsumer.setText(consumer_name);
            replaceFragment(getFragment(position));
        }
    }
}
