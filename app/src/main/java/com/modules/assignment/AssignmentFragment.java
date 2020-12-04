package com.modules.assignment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.adapters.MessageBoardListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Consumers_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.ReportsParser_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * Created by Kailash on 4/17/2019.
 */

public class AssignmentFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = AssignmentFragment.class.getSimpleName();
    @BindView(R.id.spinner_peer_participant)
    Spinner spinnerPeerParticipant;
    @BindView(R.id.spinner_peer_mentor)
    Spinner spinnerPeerMentor;
    @BindView(R.id.button_assign)
    Button buttonAssign;
    @BindView(R.id.peer_participant)
    TextView peerParticipant;
    @BindView(R.id.peer_txt)
    TextView peerTxt;

    private Activity activity;
    private MessageBoardListAdapter messageBoardListAdapter;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;

    ArrayList<Consumers_> consumerArrayList = new ArrayList<>();
    ArrayList<Consumers_> peerMentorArrayList = new ArrayList<>();

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

        View view = inflater.inflate(R.layout.fragment_assignment, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        buttonAssign.setOnClickListener(this);

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
            peerParticipant.setText("Guest");
            peerTxt.setText("Wellness Associates");
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015")){
            peerParticipant.setText("Peer Participant");
            peerTxt.setText("Peer Mentor");
        }else {
            peerParticipant.setText("Client");
            peerTxt.setText("Case Manager");
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.assignment));
        mainActivityInterface.setToolbarBackgroundColor();

        fetchPeerParticipant();
        fetchPeerMentor();
    }

    private final AdapterView.OnItemSelectedListener onPeerParticipantSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerPeerParticipant.setSelection(position);
            if (position == 0) {
                Preferences.save(General.YOUTH_ID, "");
            } else {
                Preferences.save(General.YOUTH_ID, consumerArrayList.get(position).getUser_id());
                Preferences.save(General.YOUTH_NAME, consumerArrayList.get(position).getUsername());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener onPeerMentorSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerPeerMentor.setSelection(position);
            if (position == 0) {
                Preferences.save(General.MENTOR_ID, "");
            } else {
                Preferences.save(General.MENTOR_ID, peerMentorArrayList.get(position).getUser_id());
                Preferences.save(General.ACTUALMENTOR_NAME, peerMentorArrayList.get(position).getUsername());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void fetchPeerParticipant() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.PEERPARTICIPANT_LIST);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_USER_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    consumerArrayList = new ArrayList<>();
                    Consumers_ consumers = new Consumers_();
                    consumers.setUser_id(0);

                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
                        consumers.setUsername("Select Guest");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage024")) {
                        consumers.setUsername("Select Client");
                    } else {
                        consumers.setUsername("Select Peer Participant");
                    }

                    consumers.setStatus(1);
                    consumerArrayList.add(consumers);
                    ArrayList<Consumers_> tempConsumerArrayList = ReportsParser_.parseConsumers(response, Actions_.PEERPARTICIPANT_LIST, activity.getApplicationContext(), TAG);
                    if (tempConsumerArrayList.size() > 0 && tempConsumerArrayList.get(0).getStatus() == 1) {
                        consumerArrayList.addAll(tempConsumerArrayList);
                    }
                    ArrayList<String> consumerNameList = new ArrayList<String>();
                    for (int i = 0; i < consumerArrayList.size(); i++) {
                        consumerNameList.add(consumerArrayList.get(i).getUsername());
                    }
                    if (consumerArrayList.size() > 0) {
                        ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, consumerNameList);
                        adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinnerPeerParticipant.setAdapter(adapterConsumer);
                        spinnerPeerParticipant.setOnItemSelectedListener(onPeerParticipantSelected);
                        spinnerPeerParticipant.setSelection(0);

                        Preferences.save(General.YOUTH_ID, consumerArrayList.get(0).getUser_id());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchPeerMentor() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.PEER_MENTOR_LIST);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_USER_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    peerMentorArrayList = new ArrayList<>();
                    Consumers_ consumers = new Consumers_();
                    consumers.setUser_id(0);
                    consumers.setPeerparticipant_count("");

                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
                        consumers.setUsername("Select Wellness Associates");
                    } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage024")) {
                        consumers.setUsername("Select Case Manager");
                    } else {
                        consumers.setUsername("Select Peer Mentor");
                    }

                    consumers.setStatus(1);
                    peerMentorArrayList.add(consumers);
                    ArrayList<Consumers_> tempPeerMentorArrayList = ReportsParser_.parseConsumers(response, Actions_.PEER_MENTOR_LIST, activity.getApplicationContext(), TAG);
                    if (tempPeerMentorArrayList.size() > 0 && tempPeerMentorArrayList.get(0).getStatus() == 1) {
                        peerMentorArrayList.addAll(tempPeerMentorArrayList);
                    }
                    ArrayList<String> peerMentorNameList = new ArrayList<String>();
                    for (int i = 0; i < peerMentorArrayList.size(); i++) {
                        if (peerMentorArrayList.get(i).getPeerparticipant_count().equalsIgnoreCase("")) {
                            peerMentorNameList.add(peerMentorArrayList.get(i).getUsername());
                        } else {
                            peerMentorNameList.add(peerMentorArrayList.get(i).getUsername() + " (" + peerMentorArrayList.get(i).getPeerparticipant_count() + ")");
                        }
                    }
                    if (peerMentorArrayList.size() > 0) {
                        ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, peerMentorNameList);
                        adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinnerPeerMentor.setAdapter(adapterConsumer);
                        spinnerPeerMentor.setOnItemSelectedListener(onPeerMentorSelected);
                        spinnerPeerMentor.setSelection(0);

                        Preferences.save(General.MENTOR_ID, peerMentorArrayList.get(0).getUser_id());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_assign:
                if (validate()) {
                    showStatusCommentDialog();
                }
                break;
        }
    }

    //validate all fields
    private boolean validate() {
        String consumer_id = Preferences.get(General.YOUTH_ID);
        String peer_mentor_id = Preferences.get(General.MENTOR_ID);
        if (consumer_id.length() <= 0) {

            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
                SubmitSnackResponse.showSnack(2, "Please select guest.", activity.getApplicationContext());
            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage024")) {
                SubmitSnackResponse.showSnack(2, "Please select client.", activity.getApplicationContext());
            } else {
                SubmitSnackResponse.showSnack(2, "Please select peer participant.", activity.getApplicationContext());
            }


            return false;
        }
        if (peer_mentor_id.length() <= 0) {

            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")) {
                SubmitSnackResponse.showSnack(2, "Please select wellness associates.", activity.getApplicationContext());
            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage024")) {
                SubmitSnackResponse.showSnack(2, "Please select case manager.", activity.getApplicationContext());
            } else {
                SubmitSnackResponse.showSnack(2, "Please select peer mentor.", activity.getApplicationContext());
            }

            return false;
        }
        return true;
    }

    //open Status Comment Dialog
    private void showStatusCommentDialog() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_caseload_peer_status);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView textViewMessage = (TextView) dialog.findViewById(R.id.textview_message);
        textViewMessage.setText(activity.getResources().getString(R.string.are_you_sure_you_want_to_add_this_assignment));
        TextView textViewReason = (TextView) dialog.findViewById(R.id.textview_reason);
        textViewReason.setVisibility(View.GONE);
        EditText editTextStatusMessage = (EditText) dialog.findViewById(R.id.edittext_status_message);
        editTextStatusMessage.setVisibility(View.GONE);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final Button buttonNo = (Button) dialog.findViewById(R.id.button_no);

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                assign();
            }
        });

        dialog.show();
    }

    // make network call to submit feedback
    private void assign() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ASSIGN_PARTICIPSNTH);
        requestMap.put(General.YOUTH_ID, Preferences.get(General.YOUTH_ID));
        requestMap.put(General.YOUTH_NAME, Preferences.get(General.YOUTH_NAME));
        requestMap.put(General.MENTOR_ID, Preferences.get(General.MENTOR_ID));
        requestMap.put(General.ACTUALMENTOR_NAME, Preferences.get(General.ACTUALMENTOR_NAME));
        requestMap.put(General.YOUTH_ROLE, "31");
        requestMap.put(General.TEAN_NAME, Preferences.get(General.YOUTH_NAME));
        requestMap.put(General.ADMINVARIBALFORHS, "1");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_USER_ASSIGNMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    if (Error_.oauth(response, activity.getApplicationContext()) == 13) {
                        showResponses(13);
                        return;
                    }
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.ASSIGN_PARTICIPSNTH);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            showResponses(object.get(General.STATUS).getAsInt());
                        } else {
                            showResponses(11);
                            return;
                        }
                    } else {
                        showResponses(11);
                        return;
                    }
                } else {
                    showResponses(12);
                    return;
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(11);
    }

    private void showResponses(int status) {
        String message = "";
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
            onResume();
        } else if (status == 2) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, activity.getApplicationContext());
    }
}
