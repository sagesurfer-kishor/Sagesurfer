package com.modules.contacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.EmergencyParent_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.TeamDetails_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class ParentEmergencyDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = ParentEmergencyDialogFragment.class.getSimpleName();

    @BindView(R.id.imageview_parent_emergency_list_back)
    ImageView imageViewParentEmergencyListBack;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.swipe_menu_listview)
    SwipeMenuListView swipeMenuListView;

    private ArrayList<EmergencyParent_> emergencyParentArrayList;
    private Activity activity;
    private Unbinder unbinder;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_dialog_parent_emergency_contact, null);
        unbinder = ButterKnife.bind(this, view);

        Preferences.initialize(getActivity().getApplicationContext());
        activity = getActivity();

        imageViewParentEmergencyListBack.setOnClickListener(this);

        //SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        //swipeMenuListView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_list_listview);
        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EmergencyParent_ emergencyParent = emergencyParentArrayList.get(position);
                if (emergencyParent.getStatus() == 1) {
                    EmergencyContactDialogFragment dialogFrag = new EmergencyContactDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(General.ACTION, Actions_.GET_OWNER_NUMBER);
                    bundle.putString(General.NAME, emergencyParent.getName());
                    bundle.putString(General.IMAGE, emergencyParent.getOwner_image());
                    bundle.putString(General.ROLE, emergencyParent.getRole());
                    bundle.putString("home", emergencyParent.getHome());
                    bundle.putString("mobile", emergencyParent.getMobile());
                    dialogFrag.setArguments(bundle);
                    dialogFrag.show(getFragmentManager().beginTransaction(), Actions_.USER_CONTACTS);
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        d.setCancelable(false);

        getNumbers();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_parent_emergency_list_back:
                dismiss();
                break;
        }
    }

    // Make call to get emergency contact numbers
    private void getNumbers() {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_OWNER_NUMBER);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    emergencyParentArrayList = TeamDetails_.parseEmergencyContacts(response, "phone_number",
                            TAG, activity.getApplicationContext());
                    if (emergencyParentArrayList != null && emergencyParentArrayList.size() > 0) {
                        status = emergencyParentArrayList.get(0).getStatus();
                    } else {
                        status = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (status == 1) {
            EmergencyContactListAdapter emergencyContactListAdapter =
                    new EmergencyContactListAdapter(activity, emergencyParentArrayList);
            swipeMenuListView.setAdapter(emergencyContactListAdapter);
        } else {
            dismiss();
        }
    }
}
