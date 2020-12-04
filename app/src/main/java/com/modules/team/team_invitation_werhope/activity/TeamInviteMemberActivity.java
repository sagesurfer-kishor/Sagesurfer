package com.modules.team.team_invitation_werhope.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.modules.team.TeamPeerSupervisorListActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.Invitation_parser;
import com.sagesurfer.parser.Invitations_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.validator.LoginValidator;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

public class TeamInviteMemberActivity extends AppCompatActivity {
    private static final String TAG = TeamInviteMemberActivity.class.getSimpleName();
    private AppCompatImageView postButton;
    private RadioGroup inviteTeamMember; //Radio Group of NewUser & ExistingUser Radiobutton
    private RadioButton newUserRadio, registeredUserRadio;
    private EditText teamName, teamEmail, userName, customerMessage;
    private String teamNameValue = "", teamEmailValue = "", userNameValue = "", teamCustomerValue = "", newUserSelected, regiUserSelected;
    private Boolean newUserState = false, registeredUserState = false, showRole = false;
    private LinearLayout roleIdLayout, emailLayout, userNameLayout, inviteMemberLayout;
    private Spinner spinnerRole;
    private ArrayAdapter<String> teamIdAdapter;
    private String selectedRole = "", tab_name;
    private ArrayList<Choices_> roleArrayList = new ArrayList<>();
    private Teams_ team = new Teams_();

    private LinearLayout mLinearPeerDesc, mLinearBtnAssign, mLinearBtnCancel;
    private Button mBtnAssign;
    private PopupWindow popupWindow = new PopupWindow();
    private ArrayList<Invitations_> invitationsArrayList = new ArrayList<>(); //Array for Supervisor list
    private int SUPERVISOR_REQUEST_CODE = 11;
    private static final String KEY_ID = "key_id";
    private static final String KEY_VALUE = "key_value";
    private String supervisorName;
    private int supervisorId = 0;
    private TextView mTxtSelectSupervisor, mTxtDescProDir, mTxtDescSysAdm, mTxtDescDir;
    private int peerID;
    public Invitations_ selectedSupervisor; // Model object useing when calling assignemnt API. this is selected supervisor for assignment.
    private boolean isUserNameAssigmentPending = false; //Boolen when we detect entered username assignment done or not from webservice.
    private LinearLayout supervisorLayout;

    private String selectedSupervisorName;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_team_invite_member);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(150, 0, 0, 0);
        titleText.setText(getResources().getString(R.string.invitation));

        postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);

        Intent data = getIntent();
        if (data != null && data.hasExtra(General.TEAM)) {
            team = (Teams_) data.getSerializableExtra(General.TEAM);
        }
        Preferences.save(General.INVITE_ROLE, "0");
        initUI();

        setOnClickListeners();

        getSupervisorListData();

        mBtnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameValue = userName.getText().toString().trim();
                selectedSupervisor = null;
                if (userNameValue == null || userNameValue.trim().length() <= 0) {
                    ShowSnack.viewWarning(v, "User Name is compulsory", getApplicationContext());
                } else {
                    getAssignApiFunction(userNameValue);
                }

            }
        });

        // Have to check on entered user name is inviated or not.
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    //Toast.makeText(this, "Focus Lose", Toast.LENGTH_SHORT).show();
                    userNameValue = userName.getText().toString().trim();
                    if (userNameValue == null || userNameValue.trim().length() <= 0) {
                        ShowSnack.viewWarning(userName, "User Name is compulsory", getApplicationContext());
                    } else {
                        /*   getPeerStaffTeamProgramDirectorFunction(userNameValue);*/

                        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") ||
                                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) &&
                                registeredUserRadio.isChecked() && Preferences.get(General.INVITE_ROLE).equals(General.USER_ROLE_MHAW_PEER_STAFF) &&
                                Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ProgramDirector)) {
//                            getPeerStaffTeamProgramDirectorFunction(userNameValue);
                            getAssignApiFunction(userNameValue);
                        }
                    }
                }
            }

        });

    }

    private void initUI() {
        inviteTeamMember = findViewById(R.id.invite_member_radio_group);
        newUserRadio = findViewById(R.id.radio_btn_new_user);
        registeredUserRadio = findViewById(R.id.radio_btn__register_user);

        teamName = (EditText) findViewById(R.id.team_name_txt);
        teamEmail = (EditText) findViewById(R.id.team_email_id_txt);
        userName = (EditText) findViewById(R.id.user_name_txt);
        customerMessage = (EditText) findViewById(R.id.customer_msg_txt);

        spinnerRole = (Spinner) findViewById(R.id.spinner_role_id);

        roleIdLayout = findViewById(R.id.role_id_layout);
        emailLayout = findViewById(R.id.email_layout);
        userNameLayout = findViewById(R.id.user_name_layout);
        inviteMemberLayout = findViewById(R.id.invite_member_layout);

        supervisorLayout = findViewById(R.id.supervisor_layout);
        mTxtSelectSupervisor = findViewById(R.id.txt_select_supervisor);

        mLinearPeerDesc = findViewById(R.id.linear_peer_desc);
        mTxtDescProDir = findViewById(R.id.team_desc_pro_dir);
        mTxtDescSysAdm = findViewById(R.id.team_desc_sys_adm);
        mLinearBtnAssign = findViewById(R.id.linear_btn_assign);
        mTxtDescDir = findViewById(R.id.team_desc_dir);

        mBtnAssign = findViewById(R.id.btn_assign);

        if (team.getType() == 1) {
            roleIdLayout.setVisibility(View.VISIBLE);
            showRole = true;
        } else {
            roleIdLayout.setVisibility(View.GONE);
            showRole = false;
        }
        mTxtSelectSupervisor.setHint("Select Peer Supervisor");

        TabLayout tabs = findViewById(R.id.team_invite_tab_layout);
        tabs.setOnTabSelectedListener(tabSelectedListener);
        tabs.addTab(tabs.newTab().setText("Invite Member"));
        tabs.addTab(tabs.newTab().setText("Invitation Status"));

        getRoleData();

        //setUIFooterUIAsPerConditions();
    }

    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            assert tab.getText() != null;
            tab_name = tab.getText().toString();
            setTabFragment(tab_name);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void setTabFragment(String fragment_name) {
        if (fragment_name.equalsIgnoreCase("Invite Member")) {
            inviteMemberLayout.setVisibility(View.VISIBLE);
        } else {
            Intent invitationStatus = new Intent(TeamInviteMemberActivity.this, InvitationStatusActivity.class);
            invitationStatus.putExtra(General.TEAM, team);
            startActivity(invitationStatus);
            inviteMemberLayout.setVisibility(View.GONE);
        }
    }

    private void getRoleData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ROLE_INVITATION);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                //   Log.e("RollResponse",response);
                if (response != null) {
                    roleArrayList = new ArrayList<>();
                    Choices_ role = new Choices_();
                    role.setId(0);
                    role.setRole("Select Role");
                    role.setStatus(1);
                    roleArrayList.add(role);

                    roleArrayList.addAll(SelfGoal_.parseSchoolList(response, Actions_.ROLE_INVITATION, getApplicationContext(), TAG));

                    ArrayList<String> roleNameList = new ArrayList<String>();
                    for (int i = 0; i < roleArrayList.size(); i++) {
                        roleNameList.add(roleArrayList.get(i).getRole());
                    }

                    if (roleArrayList.size() > 0) {
                        teamIdAdapter = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, roleNameList);
                        teamIdAdapter.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinnerRole.setAdapter(teamIdAdapter);
                        spinnerRole.setOnItemSelectedListener(onRoleSelected);
                        spinnerRole.setSelection(0);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final AdapterView.OnItemSelectedListener onRoleSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedRole = spinnerRole.getSelectedItem().toString();
            long ids = roleArrayList.get(position).getId();

            spinnerRole.setSelection(position);

            if (position == 0) {
                Preferences.save(General.INVITE_ROLE, "0");
            } else {
                Preferences.save(General.INVITE_ROLE, ids);

                //setUIFooterUIAsPerConditions();

                if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) &&
                        registeredUserRadio.isChecked() && Preferences.get(General.INVITE_ROLE).equals(General.USER_ROLE_MHAW_PEER_STAFF) &&
                        userNameValue.trim().length() > 0 && Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ProgramDirector)) {
//                    getPeerStaffTeamProgramDirectorFunction(userNameValue);
                    getAssignApiFunction(userNameValue);
                }
            }

            //newly added by kishor k on 08/80/2020
            if (Preferences.get(General.DOMAIN_CODE).equals("sage027")) {
                if (newUserRadio.isChecked()) {
                    if (position == 2) {
                        supervisorLayout.setVisibility(View.VISIBLE);
                    } else {
                        supervisorLayout.setVisibility(View.GONE);
                    }
                } else {
                    supervisorLayout.setVisibility(View.GONE);
                }
            } else {
                supervisorLayout.setVisibility(View.GONE);
            }

           /* else {
                Preferences.save(General.INVITE_ROLE, roleArrayList.get(position).getId());
                if(Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")){
                    if (roleArrayList.get(position).getId() == 15 && registeredUserRadio.isChecked() && Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ProgramDirector)) {
                        mLinearPeerDesc.setVisibility(View.GONE);
                        mLinearBtnAssign.setVisibility(View.GONE);
                        mTxtDescProDir.setVisibility(View.GONE);
                        mTxtDescSysAdm.setVisibility(View.GONE);
                        if (userNameValue == null || userNameValue.trim().length() <= 0) {
                            ShowSnack.viewWarning(view, "User Name is compulsory", getApplicationContext());
                        }else {
                            getPeerStaffTeamProgramDirectorFunction(userNameValue);
                        }
                    } else if(roleArrayList.get(position).getId() == 15 && registeredUserRadio.isChecked()){
                        mLinearPeerDesc.setVisibility(View.VISIBLE);
                        mLinearBtnAssign.setVisibility(View.VISIBLE);
                        mTxtDescProDir.setVisibility(View.GONE);
                        mTxtDescSysAdm.setVisibility(View.VISIBLE);
                    }else {
                        mTxtDescProDir.setVisibility(View.GONE);
                        mLinearPeerDesc.setVisibility(View.GONE);
                        mLinearBtnAssign.setVisibility(View.GONE);
                        mTxtDescSysAdm.setVisibility(View.GONE);
                    }
                }

            }*/


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

   /* private void setUIFooterUIAsPerConditions() {
        //Show/hide peer assignment lable management for MHAW instance

        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) &&
                (Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_ProgramDirector) ||
                        Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_SystemAdministrator))) {

            if (Preferences.get(General.INVITE_ROLE).equals(General.USER_ROLE_MHAW_PEER_STAFF) && registeredUserRadio.isChecked()) {

                if (Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_SystemAdministrator)) {
                    mLinearPeerDesc.setVisibility(View.VISIBLE);
                    mLinearBtnAssign.setVisibility(View.VISIBLE);
                    mTxtDescProDir.setVisibility(View.GONE);
                    mTxtDescSysAdm.setVisibility(View.VISIBLE);
                } else {
                    if (isUserNameAssigmentPending) {
                        mLinearPeerDesc.setVisibility(View.VISIBLE);
                        mLinearBtnAssign.setVisibility(View.GONE);
                        mTxtDescProDir.setVisibility(View.VISIBLE);
                        mTxtDescSysAdm.setVisibility(View.GONE);
                    } else {
                        mLinearPeerDesc.setVisibility(View.GONE);
                        mLinearBtnAssign.setVisibility(View.GONE);
                        mTxtDescProDir.setVisibility(View.GONE);
                        mTxtDescSysAdm.setVisibility(View.GONE);
                    }
                }

            } else if (Preferences.get(General.INVITE_ROLE).equals(General.USER_ROLE_MHAW_PEER_STAFF) && newUserRadio.isChecked()) {

                if (mTxtSelectSupervisor.getText().toString().trim().equalsIgnoreCase("")) {
                    mLinearPeerDesc.setVisibility(View.VISIBLE);
                    mTxtDescSysAdm.setVisibility(View.VISIBLE);
                    mTxtDescDir.setVisibility(View.VISIBLE);
                    mTxtDescProDir.setVisibility(View.VISIBLE);
                    mTxtDescSysAdm.setText(getResources().getString(R.string.super_title_1));
                    mTxtDescProDir.setText(getResources().getString(R.string.super_title_2));
                    mTxtDescDir.setText(getResources().getString(R.string.super_title_3));
                } else {
                    mLinearPeerDesc.setVisibility(View.VISIBLE);
                    mTxtDescSysAdm.setVisibility(View.VISIBLE);
                    mTxtDescProDir.setVisibility(View.VISIBLE);
                    mTxtDescDir.setVisibility(View.GONE);
                    mTxtDescSysAdm.setText(getResources().getString(R.string.super_selected_title_1));
                    mTxtDescProDir.setText(getResources().getString(R.string.super_selected_title_2));
                }

            } else {
                mLinearPeerDesc.setVisibility(View.GONE);
                mLinearBtnAssign.setVisibility(View.GONE);
                mTxtDescProDir.setVisibility(View.GONE);
                mTxtDescSysAdm.setVisibility(View.GONE);
            }
        } else {
            mLinearPeerDesc.setVisibility(View.GONE);
            mLinearBtnAssign.setVisibility(View.GONE);
            mTxtDescProDir.setVisibility(View.GONE);
            mTxtDescSysAdm.setVisibility(View.GONE);
        }
    }*/

    private void setOnClickListeners() {

        inviteTeamMember.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_btn_new_user) {
                    clearTextField();
                    emailLayout.setVisibility(View.VISIBLE);
                    userNameLayout.setVisibility(View.GONE);
                    mLinearPeerDesc.setVisibility(View.GONE);
                    mLinearBtnAssign.setVisibility(View.GONE);
                    supervisorLayout.setVisibility(View.GONE);
                } else if (checkedId == R.id.radio_btn__register_user) {
                    clearTextField();
                    emailLayout.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                    supervisorLayout.setVisibility(View.GONE);
                }

                //setUIFooterUIAsPerConditions();
            }
        });

        mTxtSelectSupervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Go to Supervisor selection
                Intent intent = new Intent(TeamInviteMemberActivity.this, TeamPeerSupervisorListActivity.class);
                intent.putExtra("invitationsArrayList", invitationsArrayList);
                intent.putExtra("invitation", selectedSupervisor);

                startActivityForResult(intent, SUPERVISOR_REQUEST_CODE);

            }
        });


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamNameValue = teamName.getText().toString().trim();
                teamEmailValue = teamEmail.getText().toString().trim();
                userNameValue = userName.getText().toString().trim();
                teamCustomerValue = customerMessage.getText().toString().trim();
                selectedSupervisorName = mTxtSelectSupervisor.getText().toString().trim();

                newUserState = newUserRadio.isChecked();
                registeredUserState = registeredUserRadio.isChecked();

                if (newUserState) {
                    newUserSelected = "1";
                } else if (registeredUserState) {
                    regiUserSelected = "2";
                }

                if (TeamInviteValidation(teamNameValue, teamEmailValue, userNameValue, v)) {

                    if (team.getType() == 1) {
                        inviteFaciliatedNonFaciliatedTeamMemberAPICalled(teamNameValue, Actions_.TEAM_INVITATION_FACILIATED, teamEmailValue, userNameValue, teamCustomerValue);
                    } else {
                        inviteFaciliatedNonFaciliatedTeamMemberAPICalled(teamNameValue, Actions_.TEAM_INVITATION_NON_FACILIATED, teamEmailValue, userNameValue, teamCustomerValue);
                    }
                }
            }
        });
    }

    public void clearTextField() {
        teamName.setText("");
        teamEmail.setText("");
        userName.setText("");
        customerMessage.setText("");
        spinnerRole.setSelection(0);
//        mTxtSelectSupervisor.setHint("Select Peer Supervisor");
    }

    //API to invite facilitae/Non-Facilitate Team
    private void inviteFaciliatedNonFaciliatedTeamMemberAPICalled(String teamNameValue, String action, String teamEmailValue, String userNameValue, String teamCustomerValue) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.NAME, teamNameValue);
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        requestMap.put(General.CUSTOM_MSG, teamCustomerValue);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        // added by kishor k 10/08/2020
        requestMap.put(General.SUPERVISOR_ID, String.valueOf(supervisorId));

        if (action.equals(Actions_.TEAM_INVITATION_FACILIATED)) {
            requestMap.put(General.ACTION, Actions_.TEAM_INVITATION_FACILIATED);
            requestMap.put(General.ROLE, Preferences.get(General.INVITE_ROLE));
        } else {
            requestMap.put(General.ACTION, Actions_.TEAM_INVITATION_NON_FACILIATED);
        }

        if (newUserState) {
            requestMap.put(General.USER_TYPE, newUserSelected);
            requestMap.put(General.EMAIL, teamEmailValue);
        } else {
            requestMap.put(General.USER_TYPE, regiUserSelected);
            requestMap.put(General.USERNAME, userNameValue);
            requestMap.put(General.EMAIL, "");
        }

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);

                    JsonObject teamInvitation;
                    if (action.equals(Actions_.TEAM_INVITATION_FACILIATED)) {
                        teamInvitation = jsonObject.getAsJsonObject(Actions_.TEAM_INVITATION_FACILIATED);
                    } else {
                        teamInvitation = jsonObject.getAsJsonObject(Actions_.TEAM_INVITATION_NON_FACILIATED);
                    }

                    if (teamInvitation.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(TeamInviteMemberActivity.this, teamInvitation.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else if (teamInvitation.get(General.STATUS).getAsInt() == 3) {
                        Toast.makeText(TeamInviteMemberActivity.this, teamInvitation.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(TeamInviteMemberActivity.this, teamInvitation.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TeamInviteMemberActivity.this, "Internal server error. Please try again.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean TeamInviteValidation(String teamName, String teamEmail, String userNameValue, View view) {
        if (showRole) {
            if (selectedRole.equalsIgnoreCase("") || selectedRole.equalsIgnoreCase("Select Role")) {
                ShowSnack.viewWarning(view, "Role is compulsory", getApplicationContext());
                return false;
            }
        }

        if (teamName == null || teamName.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Name is compulsory", getApplicationContext());
            return false;
        }

        if (teamName.length() < 3 || teamName.length() > 150) {
            ShowSnack.viewWarning(view, "Min 3 character are allowed", getApplicationContext());
            return false;
        }

        if (newUserState) {
            if (teamEmail == null || teamEmail.trim().length() <= 0) {
                ShowToast.toast("Email is compulsory", this);
                return false;
            }

            if (!LoginValidator.isEmail(teamEmail)) {
                ShowToast.toast("Please enter a valid email address", this);
                return false;
            }
        }

        if (registeredUserState) {
            if (userNameValue == null || userNameValue.trim().length() <= 0) {
                ShowSnack.viewWarning(view, "User Name is compulsory", getApplicationContext());
                return false;
            }
        }

        return true;
    }

    //API to check if assignment is done or not.
    private void getAssignApiFunction(String strValue) {
        String action = Actions_.GET_TEAM_INVITATION_FACILITATED_USERNAME_CHECK_NEW;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(General.ACTION, action);
        requestMap.put(General.USERNAME, strValue);
        requestMap.put(General.GROUP_ID, String.valueOf(team.getId()));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("getUserResponse", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddProgressNote;

                    if (action.equals(Actions_.GET_TEAM_INVITATION_FACILITATED_USERNAME_CHECK_NEW)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.GET_TEAM_INVITATION_FACILITATED_USERNAME_CHECK_NEW);

                        if (jsonAddProgressNote.get(General.STATUS).getAsInt() == 1) {
                            //Here is assignment remaining. show assignment popup & set peerID
//                            peerID = jsonAddProgressNote.get(General.USER_ID).getAsInt();
                            // Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
//                            popUpForManageRelationShip(view, strValue);
                            supervisorLayout.setVisibility(View.VISIBLE);

//                            onBackPressed();
                        } else {
                            Toast.makeText(this, jsonAddProgressNote.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                Log.e("AssignError", e.getMessage());
            }
        }
    }

    //API to get supervisor list for assignment
    private void getSupervisorListData() {
        String action = Actions_.GET_SUPERVISOR_LIST_MHAW;
        HashMap<String, String> requestMap = new HashMap<>();


        invitationsArrayList.clear();
        requestMap.put(General.ACTION, action);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("ResponseSupervisor", response);
                if (response != null) {
                    invitationsArrayList = Invitation_parser.parseSupervisorListPopup(response, action, this, TAG);
                    if (invitationsArrayList.size() > 0) {
                        if (invitationsArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
//                            ClientListModel model = new ClientListModel();
//                            model.setId("0");
//                            model.setName("All Client");
//                            invitationsArrayList.add(0, model);

//                            mProgressBar.setVisibility(View.GONE);


                        } else {
//                            showError(true, caseloadArrayList.get(0).getStatus());
                            Log.e("ErrorClientListOne", "" + invitationsArrayList.get(0).getStatus());
                        }
                    } else {
//                        showError(true, caseloadArrayList.get(0).getStatus());
                        Log.e("ErrorClientListTwo", "" + invitationsArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    //Assignment popup
    public void popUpForManageRelationShip(View v, String strName) {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.popup_manage_relationship_layout, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            popupWindow.setOutsideTouchable(false);
//            popupWindow.setBackgroundDrawable(null);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.isShowing();

            TextView mTxtSelectStaff;
            TextView mBtnCancel, mBtnAssignment;
            LinearLayout mLinearBtnCancel, mLinearBtnAssignment, mLinearTxtSupervisor;

            mTxtSelectSupervisor = customView.findViewById(R.id.txt_select_supervisor);
            mLinearTxtSupervisor = customView.findViewById(R.id.linear_txt_title_supervisor);
            mTxtSelectStaff = customView.findViewById(R.id.txt_select_staff);
            mBtnCancel = customView.findViewById(R.id.btn_cancel);
            mLinearBtnCancel = customView.findViewById(R.id.linear_btn_cancel);
            mLinearBtnAssignment = customView.findViewById(R.id.linear_btn_assignment);
            mBtnAssignment = customView.findViewById(R.id.btn_assignment);

            getSupervisorListData();

            mTxtSelectStaff.setText(strName);

            mLinearTxtSupervisor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Go to Supervisor selection
                    Intent intent = new Intent(TeamInviteMemberActivity.this, TeamPeerSupervisorListActivity.class);
                    intent.putExtra("invitationsArrayList", invitationsArrayList);
                    intent.putExtra("invitation", selectedSupervisor);

                    startActivityForResult(intent, SUPERVISOR_REQUEST_CODE);

                }
            });

            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            mBtnAssignment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showAddProgressNoteDialog(getResources().getString(R.string.are_you_sure_you_want_to_add_this_assignment));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showAddProgressNoteDialog(String message) {
        //Assignment confirmation dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_team_peer_note);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final Button buttonNo = (Button) dialog.findViewById(R.id.button_no);
        final ImageButton buttonClose = (ImageButton) dialog.findViewById(R.id.button_close);
        textViewMsg.setText(message);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAssignPeerStaffToPeerSupervisorApiFunction(String.valueOf(peerID), String.valueOf(selectedSupervisor.getId()));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //API for assignment
    private void getAssignPeerStaffToPeerSupervisorApiFunction(String strPeerID, String strSupervisorID) {
        String action = Actions_.GET_ASSIGN_PEER_STAFF_TO_PEER_SUPERVISOR_MHAW;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(General.ACTION, action);
        requestMap.put(General.PEER_ID, strPeerID);
        requestMap.put(General.SUPERVISOR_ID, strSupervisorID);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("Response", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddProgressNote;

                    if (action.equals(Actions_.GET_ASSIGN_PEER_STAFF_TO_PEER_SUPERVISOR_MHAW)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.GET_ASSIGN_PEER_STAFF_TO_PEER_SUPERVISOR_MHAW);

                        if (jsonAddProgressNote.get(General.STATUS).getAsInt() == 1) {
                            //Assignment successfull
                            Toast.makeText(this, jsonAddProgressNote.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
//                            onBackPressed();
                            popupWindow.dismiss();
                        } else {
                            Toast.makeText(this, jsonAddProgressNote.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                Log.e("AssignError", e.getMessage());
            }
        }
    }

    //API tp check if entered username's assignment done or pending.
    private void getPeerStaffTeamProgramDirectorFunction(String strUserName) {
        String action = Actions_.GET_PEER_STAFF_TEAM_PROGRAM_DIRECTOR;
        HashMap<String, String> requestMap = new HashMap<>();

        requestMap.put(General.ACTION, action);
        requestMap.put(General.USERNAME, strUserName);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                Log.e("userResponse", response);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddProgressNote;

                    if (action.equals(Actions_.GET_PEER_STAFF_TEAM_PROGRAM_DIRECTOR)) {
                        jsonAddProgressNote = jsonObject.getAsJsonObject(Actions_.GET_PEER_STAFF_TEAM_PROGRAM_DIRECTOR);
                        isUserNameAssigmentPending = false;
                        if (jsonAddProgressNote.get(General.STATUS).getAsInt() == 1) {
                        } else if (jsonAddProgressNote.get(General.STATUS).getAsInt() == 3) {
                            isUserNameAssigmentPending = true;
                            Toast.makeText(this, jsonAddProgressNote.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(this, jsonAddProgressNote.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                        }
                        //setUIFooterUIAsPerConditions();
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                Log.e("AssignError", e.getMessage());
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("Activity", "The activity has finished");
        if (resultCode == SUPERVISOR_REQUEST_CODE) {
            //Callback after selecting supervisor
            selectedSupervisor = (Invitations_) data.getExtras().getSerializable("invitation");
//            Log.e("ID",""+supervisorId);
            mTxtSelectSupervisor.setText(selectedSupervisor.getUsername());


            supervisorId = (int) selectedSupervisor.getId();

            selectedSupervisorName = mTxtSelectSupervisor.getText().toString().trim();

            mTxtDescSysAdm.setVisibility(View.VISIBLE);
            mTxtDescProDir.setVisibility(View.VISIBLE);
            mTxtDescDir.setVisibility(View.GONE);
            mTxtDescSysAdm.setText(getResources().getString(R.string.super_selected_title_1));
            mTxtDescProDir.setText(getResources().getString(R.string.super_selected_title_2));
        }
    }
}
