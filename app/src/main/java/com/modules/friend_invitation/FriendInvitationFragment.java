package com.modules.friend_invitation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.validator.LoginValidator;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 1/3/2020.
 */
public class FriendInvitationFragment extends Fragment {
    private static final String TAG = FriendInvitationFragment.class.getSimpleName();
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private RadioGroup inviteTeamMember;
    private RadioButton newUserRadio, registeredUserRadio;
    private EditText teamName, teamEmail, userName;
    private String teamNameValue = "", teamEmailValue = "", userNameValue = "";
    private Boolean newUserState = false, registeredUserState = false;
    private LinearLayout nameEmailLayout, userNameLayout;
    private Button pendingBtn, cancelBtn, inviteUserBtn;

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

        View view = inflater.inflate(R.layout.friend_inviatation_layout, null);
        activity = getActivity();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.friend_invitation));

        initUI(view);

        setOnClickListeners();

        return view;
    }

    private void initUI(View view) {
        inviteTeamMember = view.findViewById(R.id.invite_member_radio_group);
        newUserRadio = view.findViewById(R.id.wrhope_radio_btn);
        registeredUserRadio = view.findViewById(R.id.new_user_radio_btn);

        teamName = (EditText) view.findViewById(R.id.team_name_txt);
        teamEmail = (EditText) view.findViewById(R.id.team_email_id_txt);
        userName = (EditText) view.findViewById(R.id.user_txt);

        nameEmailLayout = view.findViewById(R.id.name_email_layout);
        userNameLayout = view.findViewById(R.id.user_layout);

        pendingBtn = view.findViewById(R.id.pending_btn);
        cancelBtn = view.findViewById(R.id.cancel_btn);
        inviteUserBtn = view.findViewById(R.id.invite_user_btn);

        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027") )
                && Preferences.get(General.ROLE_ID).equals(General.USER_ROLE_MHAW_CLIENT)){
            registeredUserRadio.setVisibility(View.GONE);
        }else {
            registeredUserRadio.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListeners() {
        inviteTeamMember.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.wrhope_radio_btn) {
                    nameEmailLayout.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.new_user_radio_btn) {
                    nameEmailLayout.setVisibility(View.VISIBLE);
                    userNameLayout.setVisibility(View.GONE);
                }
            }
        });

        inviteUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameValue = userName.getText().toString().trim();
                teamNameValue = teamName.getText().toString().trim();
                teamEmailValue = teamEmail.getText().toString().trim();

                newUserState = newUserRadio.isChecked();
                registeredUserState = registeredUserRadio.isChecked();

                if (InviteFriendValidation(userNameValue, teamNameValue, teamEmailValue, v)) {
                    if (newUserState) {
                        sendFriendInvitation(userNameValue, teamNameValue, teamEmailValue, Actions_.SEND_INVITATION_PLATFORM);
                    } else {
                        sendFriendInvitation(userNameValue, teamNameValue, teamEmailValue, Actions_.SEND_INVITATION_NONPLATFORM);
                    }
                }
            }
        });

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent invitationStatus = new Intent(activity, FriendInvitationStatusList.class);
                startActivity(invitationStatus);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamName.setText("");
                teamEmail.setText("");
                userName.setText("");
            }
        });

        teamName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        } else {
                            Toast.makeText(activity, "Only alphabet are allowed", Toast.LENGTH_LONG).show();
                        }
                        return "";
                    }
                }
        });
    }

    private void sendFriendInvitation(String userNameValue, String newUserValue, String teamEmailValue, String action) {
        HashMap<String, String> requestMap = new HashMap<>();

        if (action.equals(Actions_.SEND_INVITATION_PLATFORM)) {
            requestMap.put(General.ACTION, Actions_.SEND_INVITATION_PLATFORM);
            requestMap.put(General.USERNAME, userNameValue);
        } else {
            requestMap.put(General.ACTION, Actions_.SEND_INVITATION_NONPLATFORM);
            requestMap.put(General.NAME, newUserValue);
            requestMap.put(General.EMAIL, teamEmailValue);
        }
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_FRIEND_INVITATION;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);

                    JsonObject teamInvitation;
                    if (action.equals(Actions_.SEND_INVITATION_PLATFORM)) {
                        teamInvitation = jsonObject.getAsJsonObject(Actions_.SEND_INVITATION_PLATFORM);
                    } else {
                        teamInvitation = jsonObject.getAsJsonObject(Actions_.SEND_INVITATION_NONPLATFORM);
                    }

                    if (teamInvitation.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, teamInvitation.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        teamName.setText("");
                        teamEmail.setText("");
                        userName.setText("");
                    } else if (teamInvitation.get(General.STATUS).getAsInt() == 3) {
                        /*Toast.makeText(activity, "User is already invited.", Toast.LENGTH_LONG).show();*/
                        Toast.makeText(activity, teamInvitation.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity, teamInvitation.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        /*Toast.makeText(activity, "User is already invited.", Toast.LENGTH_LONG).show();*/
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean InviteFriendValidation(String userNameValue, String newUser, String teamEmail, View view) {

        if (newUserState) {
            if (userNameValue == null || userNameValue.trim().length() <= 0) {
                ShowSnack.viewWarning(view, "Username is compulsory", activity);
                return false;
            }

            if (userNameValue.length() < 3 || userNameValue.length() > 150) {
                ShowSnack.viewWarning(view, "Reason: Min 3 character are allowed", activity);
                return false;
            }
        }

        if (registeredUserState) {
            if (newUser == null || newUser.trim().length() <= 0) {
                ShowSnack.viewWarning(view, "Name is compulsory", activity);
                return false;
            }

            if (newUser.length() < 3 || newUser.length() > 150) {
                ShowSnack.viewWarning(view, "Reason: Min 3 character are allowed", activity);
                return false;
            }


            if (teamEmail == null || teamEmail.trim().length() <= 0) {
                ShowToast.toast("Email is compulsory", activity);
                return false;
            }

            if (!LoginValidator.isEmail(teamEmail)) {
                ShowToast.toast("Please enter a valid email address", activity);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.friend_invitation));
        mainActivityInterface.setToolbarBackgroundColor();
    }
}


