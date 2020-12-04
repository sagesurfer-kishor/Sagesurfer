package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.adapters.CaseloadNotePeerParticipantListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Members_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * Created by Monika on 10/17/2018.
 */

public class CaseloadPeerNoteFragment extends Fragment {
    private static final String TAG = CaseloadPeerNoteFragment.class.getSimpleName();
    @BindView(R.id.linearlayout_peer_mentor_spinner)
    LinearLayout linearLayoutPeerMentorSpinner;
    @BindView(R.id.spinner_peer_mentor)
    Spinner spinnerPeerMentor;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.linearlayout_error)
    LinearLayout linearLayoutError;
    @BindView(R.id.imageview_error)
    AppCompatImageView imageViewError;
    @BindView(R.id.textview_error_message)
    TextView textViewErrorMessage;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;
    private FragmentActivity mContext;

    ArrayList<Members_> peerMentorArrayList = new ArrayList<>();
    ArrayList<Members_> peerParticipantArrayList = new ArrayList<>();
    public CaseloadNotePeerParticipantListAdapter caseloadNotePeerParticipantListAdapter;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mContext = (FragmentActivity) activity;
            mainActivityInterface = (MainActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " " + e.toString());
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

        View view = inflater.inflate(R.layout.fragment_caseload_peer_note, container, false);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        caseloadNotePeerParticipantListAdapter = new CaseloadNotePeerParticipantListAdapter(activity, activity.getApplicationContext(), peerParticipantArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(caseloadNotePeerParticipantListAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))) {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.note));
        } else {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.note));
        }
        mainActivityInterface.setToolbarBackgroundColor();

        fetchPeerMentor();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void fetchPeerMentor() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MENTOR_LIST);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    peerMentorArrayList = new ArrayList<>();
                    Members_ peerMentor = new Members_();
                    peerMentor.setUser_id(0);

                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
                        peerMentor.setUsername("Select Wellness Associates");
                    } else {
                        peerMentor.setUsername("Select Mentor");
                    }

                    peerMentor.setStatus(1);
                    peerMentorArrayList.add(peerMentor);
                    peerMentorArrayList.addAll(CaseloadParser_.parsePeerMentor(response, General.RESULT, activity.getApplicationContext(), TAG));
                    ArrayList<String> peerMentorNameList = new ArrayList<String>();
                    for (int i = 0; i < peerMentorArrayList.size(); i++) {
                        peerMentorNameList.add(peerMentorArrayList.get(i).getUsername());
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

    private final AdapterView.OnItemSelectedListener onPeerMentorSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerPeerMentor.setSelection(position);
            if (position == 0) {
                Preferences.save(General.MENTOR_ID, "");
                recyclerView.setVisibility(View.GONE);
                linearLayoutError.setVisibility(View.VISIBLE);
            } else {
                Preferences.save(General.MENTOR_ID, peerMentorArrayList.get(position).getUser_id());
                recyclerView.setVisibility(View.VISIBLE);
                linearLayoutError.setVisibility(View.GONE);


                fetchPeerParticipant();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void fetchPeerParticipant() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PEER_LIST);
        requestMap.put(General.MENTOR_ID, Preferences.get(General.MENTOR_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    peerParticipantArrayList = CaseloadParser_.parsePeerMentor(response, General.RESULT, activity.getApplicationContext(), TAG);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 1) {
                        if (peerParticipantArrayList.size() > 0) {
                            showError(false, 1);
                            caseloadNotePeerParticipantListAdapter = new CaseloadNotePeerParticipantListAdapter(activity, activity.getApplicationContext(), peerParticipantArrayList);
                            recyclerView.setAdapter(caseloadNotePeerParticipantListAdapter);
                        } else {
                            showError(true, 2);
                        }
                    } else {
                        showError(true, jsonObject.getInt(General.STATUS));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            linearLayoutError.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            textViewErrorMessage.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            imageViewError.setImageResource(GetErrorResources.getIcon(status));
        } else {
            linearLayoutError.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}