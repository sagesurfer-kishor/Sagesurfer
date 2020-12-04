package com.modules.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 12-07-2017
 * Last Modified on 13-12-2017
 **/

public class MessageFragment extends Fragment implements View.OnClickListener {

    private String consumer_id = "";
    private static final String TAG = MessageFragment.class.getSimpleName();

    private EditText messageBox;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;

    public static MessageFragment newInstance(String _id) {
        MessageFragment myFragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(General.ID, _id);
        myFragment.setArguments(args);
        return myFragment;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivityInterface = (MainActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainActivityInterface");
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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View view = inflater.inflate(R.layout.messages_fragment_layout, null);

        activity = getActivity();

        messageBox = (EditText) view.findViewById(R.id.messages_fragment_box);
        messageBox.setOnFocusChangeListener(onFocusChangeListener);

        LinearLayout messageLayout = (LinearLayout) view.findViewById(R.id.message_fragment_layout);
        messageLayout.setOnClickListener(this);

        Button send = (Button) view.findViewById(R.id.messages_fragment_send);
        send.setOnClickListener(this);

        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.message));

        Bundle data = getArguments();
        if (data.containsKey(General.ID)) {
            consumer_id = data.getString(General.ID);
        }
        return view;
    }

    //hide reveal view from main activity if focus gained
    private final View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            //mainActivityInterface.hideRevealView();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messages_fragment_send:
                //mainActivityInterface.hideRevealView();
                String message = messageBox.getText().toString().trim();
                if (message.isEmpty() || message.length() < 3 || message.length() > 1000) {
                    messageBox.setError("Enter valid message");
                } else {
                    if (consumer_id.trim().length() <= 0) {
                        ShowToast.internalErrorOccurred(activity.getApplicationContext());
                    } else {
                        sendMessage(message, v);
                    }
                }
                break;
        }
    }

    // make network call to send message
    private void sendMessage(String message, View view) {
        int result = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SEND_MESSAGE);
        requestMap.put("youth_id", consumer_id);
        requestMap.put(General.MESSAGE, message);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CARE_GIVER_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.SEND_MESSAGE);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            result = object.get(General.STATUS).getAsInt();
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(result, view);
    }

    private void showResponses(int status, View view) {
        String message;
        if (status == 1) {
            messageBox.setText("");
            message = this.getResources().getString(R.string.successful);
        } else {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, activity.getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        Preferences.initialize(getActivity().getApplicationContext());
        mainActivityInterface.setToolbarBackgroundColor();
    }
}