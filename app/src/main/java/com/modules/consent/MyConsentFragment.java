package com.modules.consent;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 09-08-2017
 * Last Modified on 13-12-2017
 **/

public class MyConsentFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MyConsentFragment.class.getSimpleName();
    private long consumer_id = 0;
    private ArrayList<Consumer_> consumerArrayList;

    private TextView consumerName;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;

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
        View view = inflater.inflate(R.layout.container_with_team_selector_layout, null);
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        activity = getActivity();

        RelativeLayout selectorLayout = (RelativeLayout) view.findViewById(R.id.container_team_selector);
        selectorLayout.setVisibility(View.VISIBLE);
        selectorLayout.setOnClickListener(this);

        consumerName = (TextView) view.findViewById(R.id.container_team_selector_text);
        //consumerName.setText(activity.getApplicationContext().getResources().getString(R.string.select_consumer));
        if(Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))){
            consumerName.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_peer));
        } else {
            consumerName.setText(getActivity().getApplicationContext().getResources().getString(R.string.select_consumer));
        }
        return view;
    }

    // Make network call to fetch all consumers list
    private void getConsumers() {
        consumerArrayList = new ArrayList<>();
        Consumer_ consumer_ = new Consumer_();
        consumer_.setName("All");
        consumer_.setStatus(1);
        consumer_.setId(0);
        consumerArrayList.add(consumer_);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_CONSUMER_LIST);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CONSENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    consumerArrayList.addAll(Consent_.parseConsumer(response, Actions_.GET_CONSUMER_LIST,
                            activity.getApplicationContext(), TAG));
                    setConsumer(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // set selected consumer name and fetch respective consent list
    private void setConsumer(int position) {
        consumer_id = consumerArrayList.get(position).getId();
        consumerName.setText(consumerArrayList.get(position).getName());

        replaceFragment();
    }

    // Change/replace fragment to load consent list for selected consumer
    private void replaceFragment() {
        FragmentManager fm = getFragmentManager();
       FragmentTransaction ft = fm.beginTransaction();
        Fragment oldFragment = fm.findFragmentByTag(Actions_.GET_CONSUMER_LIST);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.replace(R.id.container_with_team_selector_main, ConsentListFragment.newInstance(consumer_id), Actions_.GET_CONSUMER_LIST);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    // Show/open consumer selector dialog
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

    // Get selected item position from result
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

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.my_consent));
        mainActivityInterface.setToolbarBackgroundColor();
        getConsumers();
    }

    @Override
    public void onClick(View v) {
        if (consumerArrayList == null || consumerArrayList.size() <= 0) {
            ShowSnack.viewWarning(v, activity.getApplicationContext().getResources()
                    .getString(R.string.consumer_not_available), activity.getApplicationContext());
        } else {
            showDialog();
        }
    }
}
