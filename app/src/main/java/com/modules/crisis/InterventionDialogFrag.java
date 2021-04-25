package com.modules.crisis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.adapters.SingleItemAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.secure._Base64;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 07-09-2017
 * Last Modified on 13-12-2017
 **/

public class InterventionDialogFrag extends DialogFragment {

    private static final String TAG = InterventionDialogFrag.class.getSimpleName();
    private String id = "";

    private Activity activity;

    private TextView messageText;
    private ListView listView;
    private RelativeLayout relativelayoutTitle;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.potential_crisis_dialog, null);

        activity = getActivity();

        Bundle data = getArguments();
        if (data != null) {
            id = data.getString(General.ID);
        } else {
            dismiss();
        }

        relativelayoutTitle = (RelativeLayout) view.findViewById(R.id.relativelayout_title);
        TextView title = (TextView) view.findViewById(R.id.potential_crisis_dialog_title);
        title.setText(data.getString(General.TITLE));

        AppCompatImageView cancelButton = (AppCompatImageView) view.findViewById(R.id.potential_crisis_dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        messageText = (TextView) view.findViewById(R.id.potential_crisis_dialog_message);
        listView = (SwipeMenuListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight((int) activity.getApplicationContext().getResources().getDimension(R.dimen.list_divider));

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        return view;
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Override
    public void onResume() {
        super.onResume();
        relativelayoutTitle.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        if (id.trim().length() <= 0) {
            dismiss();
            return;
        }
        getInterventions();
    }

    // Make network call to fetch interventions
    private void getInterventions() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_POTENTIAL_CRISIS_DETAILS);
        requestMap.put(General.ID, id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CRISIS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, "potential_crisis");
                    if (jsonArray != null) {
                        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                        String message = jsonObject.get(General.MSG).getAsString();
                        if (message.trim().length() <= 0) {
                            message = "-- NA --";
                        }
                        String all_interventions = jsonObject.get("all_interventions").getAsString();
                        setData(message, all_interventions);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // set list view and message data
    private void setData(String message, String all_interventions) {
        messageText.setText(message);
        List<String> list = Arrays.asList(all_interventions.split(","));
        ArrayList<String> interventionList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).equalsIgnoreCase("0")) {
                interventionList.add(_Base64.decode(list.get(i)));
            }
        }
        if (interventionList.size() > 0) {
            listView.setVisibility(View.VISIBLE);
            SingleItemAdapter singleItemAdapter = new SingleItemAdapter(
                    activity.getApplicationContext(), interventionList, false);
            listView.setAdapter(singleItemAdapter);
        }
    }
}