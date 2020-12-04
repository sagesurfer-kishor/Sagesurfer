package com.modules.caseload.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modules.caseload.PeerAddNoteActivity;
import com.modules.caseload.PeerNoteDetailsAmendmentActivity;
import com.modules.caseload.PeerNoteDetailsCommentActivity;
import com.modules.caseload.PeerNoteDetailsViewLogActivity;
import com.sagesurfer.adapters.CaseloadPeerNoteRejectReasonsAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.models.CaseloadPeerNoteAmendments_;
import com.sagesurfer.models.CaseloadPeerNoteComments_;
import com.sagesurfer.models.CaseloadPeerNoteReject_;
import com.sagesurfer.models.CaseloadPeerNoteViewLog_;
import com.sagesurfer.models.CaseloadPeerNote_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

public class PeerNoteAllDetailsActivity extends AppCompatActivity implements View.OnClickListener, CaseloadPeerNoteRejectReasonsAdapter.CaseloadListAdapterListener {
    private static final String TAG = PeerNoteAllDetailsActivity.class.getSimpleName();
    @BindView(R.id.linearlayout_peer_note_details)
    LinearLayout linearLayoutPeerNoteDetails;
    @BindView(R.id.caseload_peer_note_title)
    TextView textViewTitle;
    @BindView(R.id.imageview_edit)
    ImageView imageViewEdit;
    @BindView(R.id.imageview_delete)
    ImageView imageViewDelete;
    @BindView(R.id.imageview_add_amendment)
    ImageView imageViewAddAmendment;
    @BindView(R.id.textview_amendment_count)
    TextView textViewAmendmentCount;
    @BindView(R.id.imageview_comment)
    ImageView imageViewComment;
    @BindView(R.id.textview_comment_count)
    TextView textViewCommentCount;
    @BindView(R.id.linearlayout_leave_header)
    LinearLayout linearLayoutLeaveHeader;
    @BindView(R.id.textview_leave_header)
    TextView textViewLeaveHeader;
    @BindView(R.id.textview_peer_note_subject)
    TextView subjectTextView;
    @BindView(R.id.textview_peer_note_date)
    TextView dateTextView;
    @BindView(R.id.textview_peer_time)
    TextView timeTextView;
    @BindView(R.id.textview_peer_note_type)
    TextView typeTextView;
    @BindView(R.id.button_view_log)
    Button buttonViewLog;
    @BindView(R.id.textview_peer_duration)
    TextView durationTextView;
    @BindView(R.id.textview_peer_next_steps)
    TextView nextStepsTextView;
    @BindView(R.id.textview_peer_resources_needed)
    TextView resourcesNeededTextView;
    @BindView(R.id.textview_peer_note)
    TextView noteTextView;
    @BindView(R.id.linear_layout_button)
    LinearLayout linearLayoutButton;
    @BindView(R.id.button_approve)
    Button buttonApprove;
    @BindView(R.id.button_reject)
    Button buttonReject;
    @BindView(R.id.linealayout_error)
    LinearLayout lineaLayoutError;
    @BindView(R.id.textview_error_message)
    TextView textViewErrorMessage;
    @BindView(R.id.imageview_error_icon)
    ImageView imageViewErrorIcon;
    private Long note_id, cust_id, getAdded_by_Id;
    public String subject = "", date = "", time = "", note = "", type_of_contact = "", duration = "", next_steps = "", resources_needed = "", type = "";
    Toolbar toolbar;
    public ArrayList<CaseloadPeerNote_> caseloadPeerNoteArrayList = new ArrayList<>();
    private String action = "";
    ArrayList<CaseloadPeerNoteReject_> rejectReasonArrayList = new ArrayList<>();
    ArrayList<CaseloadPeerNoteViewLog_> viewLogArrayList = new ArrayList<>();
    ArrayList<CaseloadPeerNoteAmendments_> amendmentsArrayList = new ArrayList<>();
    ArrayList<CaseloadPeerNoteComments_> commentsArrayList = new ArrayList<>();
    private String reject_reason_id = "0";
    EditText editTextOther;
    private boolean isFromNotification = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_caseload_peer_note_details);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.caseload_peer_note_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent data = getIntent();
        if (data != null) {
            if (data.hasExtra(Actions_.NOTES)) {
                isFromNotification = true;
                caseloadPeerNoteArrayList = (ArrayList<CaseloadPeerNote_>) data.getSerializableExtra(Actions_.NOTES);
                note_id = caseloadPeerNoteArrayList.get(0).getVisit_id();
                action = caseloadPeerNoteArrayList.get(0).getNote_status();
            }
            if (data.hasExtra(General.ID)) {
                note_id = data.getLongExtra(General.ID, 0);
                action = data.getStringExtra(General.ACTION);
                cust_id = data.getLongExtra(General.CONSUMER_ID, 0);
                getAdded_by_Id = data.getLongExtra(General.LOGIN_USER_ID, 0);
                type = data.getStringExtra(General.TYPE);
            }
        } else {
            onBackPressed();
        }

        buttonViewLog.setOnClickListener(this);
        imageViewEdit.setOnClickListener(this);
        imageViewAddAmendment.setOnClickListener(this);
        imageViewComment.setOnClickListener(this);
        buttonApprove.setOnClickListener(this);
        buttonReject.setOnClickListener(this);
        nextStepsTextView.setMovementMethod(new ScrollingMovementMethod());
        resourcesNeededTextView.setMovementMethod(new ScrollingMovementMethod());
        noteTextView.setMovementMethod(new ScrollingMovementMethod());

        if (CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            linearLayoutButton.setVisibility(View.GONE);
            setViewLogButtonBackground();

            if (action.equalsIgnoreCase(Actions_.PENDING_ALL)) {
                buttonViewLog.setVisibility(View.VISIBLE);
            } else if (action.equalsIgnoreCase(Actions_.APPROVED_All)) {
                buttonViewLog.setVisibility(View.VISIBLE);
                imageViewAddAmendment.setVisibility(View.VISIBLE);
            } else if (action.equalsIgnoreCase(Actions_.REJECTED_All)) {
                buttonViewLog.setVisibility(View.VISIBLE);
                imageViewEdit.setVisibility(View.VISIBLE);
                imageViewComment.setVisibility(View.VISIBLE);
                textViewCommentCount.setVisibility(View.VISIBLE);
            } else {
                buttonViewLog.setVisibility(View.VISIBLE);
                imageViewEdit.setVisibility(View.VISIBLE);
            }
        } else {
            setViewLogButtonBackground();
            if (action.equalsIgnoreCase(Actions_.APPROVED_All)) {
                buttonViewLog.setVisibility(View.VISIBLE);
            }

        }
    }

    private void setViewLogButtonBackground() {
        int iconNumber = 0;
        if (iconNumber == 0) {
            buttonViewLog.setBackground(getResources().getDrawable(R.drawable.blue_rounded_rectangle)); //R.color.colorPrimary
        } else if (iconNumber == 1) {
            buttonViewLog.setBackground(getResources().getDrawable(R.drawable.background_home_menu_one)); //R.color.bg_home_menu_one
        } else if (iconNumber == 2) {
            buttonViewLog.setBackground(getResources().getDrawable(R.drawable.background_home_menu_two)); //R.color.bg_home_menu_two
        } else if (iconNumber == 3) {
            buttonViewLog.setBackground(getResources().getDrawable(R.drawable.background_home_menu_three)); //R.color.bg_home_menu_three
        } else if (iconNumber == 4) {
            buttonViewLog.setBackground(getResources().getDrawable(R.drawable.background_home_menu_four)); //R.color.bg_home_menu_four
        } else if (iconNumber == 5) {
            buttonViewLog.setBackground(getResources().getDrawable(R.drawable.background_home_menu_five)); //R.color.bg_home_menu_five
        } else if (iconNumber == 6) {
            buttonViewLog.setBackground(getResources().getDrawable(R.drawable.background_home_menu_six)); //R.color.bg_home_menu_six
        } else {
            buttonViewLog.setBackground(getResources().getDrawable(R.drawable.blue_rounded_rectangle)); //R.color.colorPrimary
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage013)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))) {
            //textViewTitle.setText(getResources().getString(R.string.note));
            textViewTitle.setText("Note Status");
        } else {
            textViewTitle.setText(getResources().getString(R.string.progress_note));
        }
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        if (!isFromNotification) {
            getPeerCaseloadNoteDetails();
        } else {
            subjectTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getSubject());
            dateTextView.setText(" " + GetTime.month_DdYyyy(caseloadPeerNoteArrayList.get(0).getVisit_date()));
            timeTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getAt_time().substring(0, 5) + " " + caseloadPeerNoteArrayList.get(0).getAt_time().substring(9, 11));
            typeTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getContact_type());
            durationTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getDuration().substring(0, 5));
            nextStepsTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getNext_step_notes());
            resourcesNeededTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getResources_needed());
            noteTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getNotes());

            if (CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                if ((action.equalsIgnoreCase(Actions_.PENDING_ALL) || action.equalsIgnoreCase(Actions_.APPROVED_All)) && caseloadPeerNoteArrayList.get(0).getReject_cnt() > 0) {
                    imageViewComment.setVisibility(View.VISIBLE);
                    textViewCommentCount.setVisibility(View.VISIBLE);
                }
            }
            if (buttonViewLog.getVisibility() == View.VISIBLE) {
                String viewLogCount = "" + caseloadPeerNoteArrayList.get(0).getLog_cnt();
                String viewLog = getResources().getString(R.string.view_log) + " (" + viewLogCount + ")";
                buttonViewLog.setText(viewLog);
            }
            if (imageViewComment.getVisibility() == View.VISIBLE) {
                String commentCount = "(" + caseloadPeerNoteArrayList.get(0).getReject_cnt() + ")";
                textViewCommentCount.setText(commentCount);
            }
            if (imageViewAddAmendment.getVisibility() == View.VISIBLE) {
                if (caseloadPeerNoteArrayList.get(0).getAmendment_cnt() > 0) {
                    textViewAmendmentCount.setVisibility(View.VISIBLE);
                    String amendmentCount = "(" + caseloadPeerNoteArrayList.get(0).getAmendment_cnt() + ")";
                    textViewAmendmentCount.setText(amendmentCount);
                }
            }

            if (CheckRole.isSupervisor(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                if (action.equalsIgnoreCase(Actions_.PENDING_ALL) && caseloadPeerNoteArrayList.get(0).getIs_on_leave() == 0) { //isOnLeave Condition
                    linearLayoutButton.setVisibility(View.VISIBLE);
                    //textViewApprove.setVisibility(View.VISIBLE);
                    if (caseloadPeerNoteArrayList.get(0).getReject_cnt() > 2) {
                        buttonReject.setVisibility(View.GONE);
                    } else {
                        buttonReject.setVisibility(View.VISIBLE);
                    }
                } else if (action.equalsIgnoreCase(Actions_.PENDING_ALL) && caseloadPeerNoteArrayList.get(0).getIs_on_leave() == 1) {
                    linearLayoutLeaveHeader.setVisibility(View.VISIBLE);
                    textViewLeaveHeader.setText(getResources().getString(R.string.you_are_on_leave));
                }
            } else if (CheckRole.isInstanceAdmin(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                if (action.equalsIgnoreCase(Actions_.PENDING_ALL) && caseloadPeerNoteArrayList.get(0).getIs_on_leave() == 1) { //isOnLeave instanceAdmin
                    linearLayoutLeaveHeader.setVisibility(View.VISIBLE);
                    textViewLeaveHeader.setText(getResources().getString(R.string.mentor_coordinator_is_on_leave));
                    linearLayoutButton.setVisibility(View.VISIBLE);
                    if (caseloadPeerNoteArrayList.get(0).getReject_cnt() > 2) {
                        buttonReject.setVisibility(View.GONE);
                    } else {
                        buttonReject.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_view_log:
                if (caseloadPeerNoteArrayList.size() > 0) {
                    Intent detailsIntent = new Intent(getApplicationContext(), PeerNoteDetailsViewLogActivity.class);
                    detailsIntent.putExtra(General.NOTE_ID, caseloadPeerNoteArrayList.get(0).getVisit_id());
                    detailsIntent.putExtra(General.NOTE_SUBJECT, caseloadPeerNoteArrayList.get(0).getSubject());
                    startActivity(detailsIntent);
                    overridePendingTransition(0, 0);
                }
                break;

            case R.id.imageview_edit:
                if (caseloadPeerNoteArrayList.size() > 0) {
                    Intent detailsIntent = new Intent(getApplicationContext(), PeerAddNoteActivity.class);
                    detailsIntent.putExtra(Actions_.EDIT, action);
                    detailsIntent.putExtra(General.NOTE_ID, caseloadPeerNoteArrayList.get(0).getVisit_id());
                    detailsIntent.putExtra(General.SUBJECT, caseloadPeerNoteArrayList.get(0).getSubject());
                    detailsIntent.putExtra(General.DATE, caseloadPeerNoteArrayList.get(0).getVisit_date());
                    detailsIntent.putExtra(General.TIME, caseloadPeerNoteArrayList.get(0).getAt_time());
                    detailsIntent.putExtra(General.CONTACT_TYPE, caseloadPeerNoteArrayList.get(0).getContact_type());
                    detailsIntent.putExtra(General.DURATION, caseloadPeerNoteArrayList.get(0).getDuration());
                    detailsIntent.putExtra(General.NEXT_STEP_NOTES, caseloadPeerNoteArrayList.get(0).getNext_step_notes());
                    detailsIntent.putExtra(General.RESOURCES_NEEDED, caseloadPeerNoteArrayList.get(0).getResources_needed());
                    detailsIntent.putExtra(General.NOTES, caseloadPeerNoteArrayList.get(0).getNotes());
                    startActivity(detailsIntent);
                    overridePendingTransition(0, 0);
                }
                break;

            case R.id.imageview_add_amendment:
                if (caseloadPeerNoteArrayList.size() > 0) {
                    Intent detailsIntent = new Intent(getApplicationContext(), PeerNoteDetailsAmendmentActivity.class);
                    detailsIntent.putExtra(General.NOTE_ID, caseloadPeerNoteArrayList.get(0).getVisit_id());
                    startActivity(detailsIntent);
                    overridePendingTransition(0, 0);
                }
                break;

            case R.id.imageview_comment:
                if (caseloadPeerNoteArrayList.size() > 0) {
                    Intent detailsIntent = new Intent(getApplicationContext(), PeerNoteDetailsCommentActivity.class);
                    detailsIntent.putExtra(General.NOTE_ID, caseloadPeerNoteArrayList.get(0).getVisit_id());
                    startActivity(detailsIntent);
                    overridePendingTransition(0, 0);
                }
                break;

            case R.id.button_approve:
                showApproveRejectConfirmationDialog(getResources().getString(R.string.are_you_sure_you_want_to_approve_this_note), Actions_.SUPERVISOR_APPROVE);
                break;

            case R.id.button_reject:
                showApproveRejectConfirmationDialog(getResources().getString(R.string.are_you_sure_you_want_to_reject_this_note), "");
                break;
        }
    }

    //make network call to fetch all Peer Notes
    private void getPeerCaseloadNoteDetails() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_NOTES_DETAILS);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.CUST_ID, String.valueOf(cust_id));
        requestMap.put(General.NOTE_ID, String.valueOf(note_id));

        if (Preferences.get(General.MENTOR_ID).equalsIgnoreCase("0")) {
            requestMap.put(General.LOGIN_USER_ID, String.valueOf(getAdded_by_Id));
        } else {
            requestMap.put(General.LOGIN_USER_ID, Preferences.get(General.MENTOR_ID));
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    caseloadPeerNoteArrayList = CaseloadParser_.parsePeerNote(response, General.RESULT, getApplicationContext(), TAG);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 1) {
                        if (caseloadPeerNoteArrayList.size() > 0) {

                            showError(false, 1);

                            subjectTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getSubject());
                            dateTextView.setText(" " + GetTime.month_DdYyyy(caseloadPeerNoteArrayList.get(0).getVisit_date()));
                            timeTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getAt_time().substring(0, 5) + " " + caseloadPeerNoteArrayList.get(0).getAt_time().substring(9, 11));
                            typeTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getContact_type());
                            durationTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getDuration().substring(0, 5));
                            nextStepsTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getNext_step_notes());
                            resourcesNeededTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getResources_needed());
                            noteTextView.setText(" " + caseloadPeerNoteArrayList.get(0).getNotes());

                            if (CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                                if ((action.equalsIgnoreCase(Actions_.PENDING_ALL) || action.equalsIgnoreCase(Actions_.APPROVED_All)) && caseloadPeerNoteArrayList.get(0).getReject_cnt() > 0) {
                                    imageViewComment.setVisibility(View.VISIBLE);
                                    textViewCommentCount.setVisibility(View.VISIBLE);
                                }
                            }
                            if (buttonViewLog.getVisibility() == View.VISIBLE) {
                                String viewLogCount = "" + caseloadPeerNoteArrayList.get(0).getLog_cnt();
                                String viewLog = getResources().getString(R.string.view_log) + " (" + viewLogCount + ")";
                                buttonViewLog.setText(viewLog);
                            }
                            if (imageViewComment.getVisibility() == View.VISIBLE) {
                                String commentCount = "(" + caseloadPeerNoteArrayList.get(0).getReject_cnt() + ")";
                                textViewCommentCount.setText(commentCount);
                            }
                            if (imageViewAddAmendment.getVisibility() == View.VISIBLE) {
                                if (caseloadPeerNoteArrayList.get(0).getAmendment_cnt() > 0) {
                                    textViewAmendmentCount.setVisibility(View.VISIBLE);
                                    String amendmentCount = "(" + caseloadPeerNoteArrayList.get(0).getAmendment_cnt() + ")";
                                    textViewAmendmentCount.setText(amendmentCount);
                                }
                            }
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

    //open Approve/Reject Confirmation Dialog
    private void showApproveRejectConfirmationDialog(String message, final String action) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_peer_note_edit);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView textViewMsg = (TextView) dialog.findViewById(R.id.textview_msg);
        final Button buttonYes = (Button) dialog.findViewById(R.id.button_yes);
        final ImageButton buttonCancel = (ImageButton) dialog.findViewById(R.id.button_close);

        textViewMsg.setText(message);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (action.length() > 0) {
                    approveRejectNote(Actions_.SUPERVISOR_APPROVE, "");
                } else {
                    showRejectCommentDialog();
                }
            }
        });

        dialog.show();
    }

    //make network call to Approve/Reject Peer Notes
    private void approveRejectNote(String action, String comment) { //approve or reject note based on note id, mentor id is not required
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.LOGIN_USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.NOTE_ID, String.valueOf(note_id));
        if (action.equalsIgnoreCase(Actions_.SUPERVISOR_REJECT_COMMENTS)) {
            requestMap.put(General.COMMENT, comment);
            requestMap.put(General.REASON_ID, reject_reason_id);
            requestMap.put(General.OTHER_TEXT, editTextOther.getText().toString().trim());
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    caseloadPeerNoteArrayList = CaseloadParser_.parsePeerNote(response, General.RESULT, getApplicationContext(), TAG);
                    JSONObject jsonObject = new JSONObject(response);
                    String message;
                    if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 1) {
                        message = this.getResources().getString(R.string.successful);
                    } else {
                        message = this.getResources().getString(R.string.action_failed);
                    }
                    SubmitSnackResponse.showSnack(jsonObject.getInt(General.STATUS), message, getApplicationContext());
                }
                onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //open Reject Comment Dialog with available reasons to a reject note
    private void showRejectCommentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_peer_note_reject);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText editTextComment = (EditText) dialog.findViewById(R.id.edittext_action_dialod);
        final RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
        editTextOther = (EditText) dialog.findViewById(R.id.editview_other);
        final Button buttonSubmit = (Button) dialog.findViewById(R.id.button_submit);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);

        CaseloadPeerNoteRejectReasonsAdapter caseloadPeerNoteRejectReasons = new CaseloadPeerNoteRejectReasonsAdapter(this, getApplicationContext(), rejectReasonArrayList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(caseloadPeerNoteRejectReasons);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.REJECT_REASON);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    rejectReasonArrayList = CaseloadParser_.parseRejectReasons(response, General.DATA, getApplicationContext(), TAG);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 1) {
                        if (rejectReasonArrayList.size() > 0) {
                            caseloadPeerNoteRejectReasons = new CaseloadPeerNoteRejectReasonsAdapter(this, getApplicationContext(), rejectReasonArrayList, this);
                            recyclerView.setAdapter(caseloadPeerNoteRejectReasons);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = editTextComment.getText().toString().trim();
                if (comment.length() > 0) {
                    dialog.dismiss();
                    reject_reason_id = GetSelected.rejectReasons(rejectReasonArrayList);
                    approveRejectNote(Actions_.SUPERVISOR_REJECT_COMMENTS, comment);
                } else {
                    ShowToast.toast("Please Enter Comment", getApplicationContext());
                }
            }
        });

        dialog.show();
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            lineaLayoutError.setVisibility(View.VISIBLE);
            linearLayoutPeerNoteDetails.setVisibility(View.GONE);
            textViewErrorMessage.setText(GetErrorResources.getMessage(status, getApplicationContext()));
            imageViewErrorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            lineaLayoutError.setVisibility(View.GONE);
            linearLayoutPeerNoteDetails.setVisibility(View.VISIBLE);
            if (CheckRole.isSupervisor(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                if (action.equalsIgnoreCase(Actions_.GET_All) || action.equalsIgnoreCase(Actions_.PENDING_ALL)) { //isOnLeave Condition

                    if (caseloadPeerNoteArrayList.get(0).getIs_on_leave() == 0) {
                        if (type.equalsIgnoreCase(Actions_.PENDING_ALL)) {
                            linearLayoutButton.setVisibility(View.VISIBLE);
                        } else if (type.equalsIgnoreCase(Actions_.APPROVED_All)) {
                            buttonViewLog.setVisibility(View.VISIBLE);
                            linearLayoutLeaveHeader.setVisibility(View.GONE);
                            linearLayoutButton.setVisibility(View.GONE);
                        } else if (type.equalsIgnoreCase(Actions_.REJECTED_All)) {
                            buttonViewLog.setVisibility(View.GONE);
                            linearLayoutLeaveHeader.setVisibility(View.GONE);
                            linearLayoutButton.setVisibility(View.GONE);
                        }
                        if (caseloadPeerNoteArrayList.get(0).getReject_cnt() > 2) {
                            buttonReject.setVisibility(View.GONE);
                        } else {
                            buttonReject.setVisibility(View.VISIBLE);
                        }

                        if (type.equalsIgnoreCase(Actions_.PENDING_ALL)) {
                            textViewLeaveHeader.setText(getResources().getString(R.string.you_are_on_leave));
                        }

                    } else if (caseloadPeerNoteArrayList.get(0).getIs_on_leave() == 1) {
                        if (type.equalsIgnoreCase(Actions_.PENDING_ALL)) {
                            // linearLayoutButton.setVisibility(View.VISIBLE);
                            linearLayoutLeaveHeader.setVisibility(View.VISIBLE);
                        } else if (type.equalsIgnoreCase(Actions_.APPROVED_All)) {
                            buttonViewLog.setVisibility(View.VISIBLE);
                            linearLayoutLeaveHeader.setVisibility(View.GONE);
                            linearLayoutButton.setVisibility(View.GONE);
                        } else if (type.equalsIgnoreCase(Actions_.REJECTED_All)) {
                            buttonViewLog.setVisibility(View.GONE);
                            linearLayoutLeaveHeader.setVisibility(View.GONE);
                            linearLayoutButton.setVisibility(View.GONE);
                        }
                        if (caseloadPeerNoteArrayList.get(0).getReject_cnt() > 2) {
                            buttonReject.setVisibility(View.GONE);
                        } else {
                            buttonReject.setVisibility(View.VISIBLE);
                        }

                        if (type.equalsIgnoreCase(Actions_.PENDING_ALL)) {
                            textViewLeaveHeader.setText(getResources().getString(R.string.you_are_on_leave));
                        }
                    }
                }
            } else if (CheckRole.isInstanceAdmin(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                if (action.equalsIgnoreCase(Actions_.GET_All) || action.equalsIgnoreCase(Actions_.PENDING_ALL)) { //isOnLeave instanceAdmin

                    if (caseloadPeerNoteArrayList.get(0).getIs_on_leave() == 0) {
                        if (type.equalsIgnoreCase(Actions_.PENDING_ALL)) {
                            //linearLayoutLeaveHeader.setVisibility(View.VISIBLE);
                            //linearLayoutButton.setVisibility(View.VISIBLE);
                        } else if (type.equalsIgnoreCase(Actions_.APPROVED_All)) {
                            buttonViewLog.setVisibility(View.VISIBLE);
                            linearLayoutLeaveHeader.setVisibility(View.GONE);
                            linearLayoutButton.setVisibility(View.GONE);
                        } else if (type.equalsIgnoreCase(Actions_.REJECTED_All)) {
                            buttonViewLog.setVisibility(View.GONE);
                            linearLayoutLeaveHeader.setVisibility(View.GONE);
                            linearLayoutButton.setVisibility(View.GONE);
                        }

                        if (caseloadPeerNoteArrayList.get(0).getReject_cnt() > 2) {
                            buttonReject.setVisibility(View.GONE);
                        } else {
                            buttonReject.setVisibility(View.VISIBLE);
                        }

                        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))) {
                            textViewLeaveHeader.setText(getResources().getString(R.string.supervisor__is_on_leave));
                        } else {
                            textViewLeaveHeader.setText(getResources().getString(R.string.mentor_coordinator_is_on_leave));
                        }
                    } else if (caseloadPeerNoteArrayList.get(0).getIs_on_leave() == 1) {
                        if (type.equalsIgnoreCase(Actions_.PENDING_ALL)) {
                            linearLayoutLeaveHeader.setVisibility(View.VISIBLE);
                            linearLayoutButton.setVisibility(View.VISIBLE);
                        } else if (type.equalsIgnoreCase(Actions_.APPROVED_All)) {
                            buttonViewLog.setVisibility(View.VISIBLE);
                            linearLayoutLeaveHeader.setVisibility(View.GONE);
                            linearLayoutButton.setVisibility(View.GONE);
                        } else if (type.equalsIgnoreCase(Actions_.REJECTED_All)) {
                            buttonViewLog.setVisibility(View.GONE);
                            linearLayoutLeaveHeader.setVisibility(View.GONE);
                            linearLayoutButton.setVisibility(View.GONE);
                        }

                        if (caseloadPeerNoteArrayList.get(0).getReject_cnt() > 2) {
                            buttonReject.setVisibility(View.GONE);
                        } else {
                            buttonReject.setVisibility(View.VISIBLE);
                        }

                        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))) {
                            textViewLeaveHeader.setText(getResources().getString(R.string.supervisor__is_on_leave));
                        } else {
                            textViewLeaveHeader.setText(getResources().getString(R.string.mentor_coordinator_is_on_leave));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onOtherClicked() {
        editTextOther.setVisibility(View.VISIBLE);
    }
}

