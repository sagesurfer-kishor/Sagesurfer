package com.modules.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 24-08-2017
 * Last Modified on 13-12-2017
 */

/*
 * This file create dialog box to perform operations on invitation sent to external/internal users.
 */

@SuppressWarnings("ConstantConditions")
public class AdminInviteDetailsPopUp extends DialogFragment implements View.OnClickListener {

    private static final String TAG = AdminInviteDetailsPopUp.class.getSimpleName();
    private boolean isSuccess = false;
    private String position;

    private Activity activity;
    private Context _context;
    private Invitation_ invitation_;

    private EditText inputBox;
    private CheckBox reminderCheck;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_invite_dialog_layout, null);

        activity = getActivity();
        _context = activity.getApplicationContext();

        Bundle data = getArguments();
        if (data.containsKey(General.ID)) {
            invitation_ = (Invitation_) data.getSerializable(General.ID);
            position = data.getString(General.POSITION);
            setData(view);
        } else {
            dismiss();
        }
        AppCompatImageView close = (AppCompatImageView) view.findViewById(R.id.admin_invite_dialog_close);
        close.setOnClickListener(this);

        Button deleteButton = (Button) view.findViewById(R.id.admin_invite_dialog_button_delete);
        deleteButton.setOnClickListener(this);

        Button resendButton = (Button) view.findViewById(R.id.admin_invite_dialog_button_resend);
        resendButton.setOnClickListener(this);

        reminderCheck = (CheckBox) view.findViewById(R.id.admin_invite_dialog_checkbox);
        reminderCheck.setOnClickListener(this);

        return view;
    }

    // Method to set data for respective fields in dialog box
    private void setData(View view) {
        inputBox = (EditText) view.findViewById(R.id.admin_invite_dialog_input_box);
        ImageView senderImage = (ImageView) view.findViewById(R.id.admin_invite_dialog_photo_one);
        ImageView authImage = (ImageView) view.findViewById(R.id.admin_invite_dialog_photo_two);
        ImageView receiverImage = (ImageView) view.findViewById(R.id.admin_invite_dialog_photo_three);
        TextView authName = (TextView) view.findViewById(R.id.admin_invite_dialog_auth_name);
        TextView authStatus = (TextView) view.findViewById(R.id.admin_invite_dialog_status_two);
        TextView receiverName = (TextView) view.findViewById(R.id.admin_invite_dialog_receiver_name);
        TextView receiverStatus = (TextView) view.findViewById(R.id.admin_invite_dialog_status_three);
        TextView senderName = (TextView) view.findViewById(R.id.admin_invite_dialog_sender_name);
        TextView senderStatus = (TextView) view.findViewById(R.id.admin_invite_dialog_status_one);
        LinearLayout middleLayout = (LinearLayout) view.findViewById(R.id.admin_invite_dialog_middle_layout);
        LinearLayout checkLayout = (LinearLayout) view.findViewById(R.id.admin_invite_dialog_checkbox_layout);
        Button resendButton = (Button) view.findViewById(R.id.admin_invite_dialog_button_resend);
        resendButton.setOnClickListener(this);
        Button deleteButton = (Button) view.findViewById(R.id.admin_invite_dialog_button_delete);
        deleteButton.setOnClickListener(this);

        if (invitation_.getAction().length() <= 0) {
            resendButton.setVisibility(View.GONE);
        } else {
            resendButton.setVisibility(View.VISIBLE);
        }

        View lineOne = view.findViewById(R.id.admin_invite_dialog_view_one);
        View lineTwo = view.findViewById(R.id.admin_invite_dialog_view_two);
        View lineThree = view.findViewById(R.id.admin_invite_dialog_view_three);
        View lineFour = view.findViewById(R.id.admin_invite_dialog_view_four);

        senderStatus.setText(_context.getResources().getString(R.string.request_sent));
        receiverStatus.setText(invitation_.getReceiver_status());
        middleLayout.setVisibility(View.GONE);

        senderName.setText(ChangeCase.toTitleCase(invitation_.getRequest_send()));
        receiverName.setText(ChangeCase.toTitleCase(invitation_.getReceiver_user()));

        String message = invitation_.getVarmsg();
        if (message != null && message.trim().length() > 0) {
            inputBox.setText(message);
            inputBox.setSelection(message.length());
        }

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(_context).load(invitation_.getSend_profile())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(invitation_.getSend_profile()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(_context)))
                .into(senderImage);

        senderImage.setBackground(InviteAnalysis.getCircle("green_circle", _context));

        Glide.with(_context).load(invitation_.getReceiver_profile())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(invitation_.getReceiver_profile()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(_context)))
                .into(receiverImage);

        receiverImage.setBackground(InviteAnalysis.getCircle(invitation_.getReceiver_circle(),
                _context));

        if (invitation_.getIs_popup() == 1) {
            inputBox.setVisibility(View.VISIBLE);
            checkLayout.setVisibility(View.VISIBLE);
        } else {
            inputBox.setVisibility(View.GONE);
            checkLayout.setVisibility(View.GONE);
        }
        if (invitation_.getIs_auth() == 1) {
            middleLayout.setVisibility(View.VISIBLE);
            authStatus.setText(invitation_.getAuth_status());
            authName.setText(ChangeCase.toTitleCase(invitation_.getAuth_user()));

            Glide.with(_context).load(invitation_.getAuth_profile())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(invitation_.getAuth_profile()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(_context)))
                    .into(authImage);

            authImage.setBackground(InviteAnalysis.getCircle(invitation_.getAuth_circle(), _context));
            lineOne.setBackgroundColor(InviteAnalysis.getLine(invitation_.getAuth_circle(), _context));
            lineThree.setBackgroundColor(InviteAnalysis.getLine(invitation_.getAuth_circle(), _context));
            lineTwo.setBackgroundColor(InviteAnalysis.getLine(invitation_.getAuth_circle(), _context));
        } else {
            lineOne.setBackgroundColor(InviteAnalysis.getLine(invitation_.getReceiver_circle(), _context));
        }
        lineFour.setBackgroundColor(InviteAnalysis.getLine(invitation_.getReceiver_circle(), _context));
    }

    // Make network call to delete invitation
    private void delete(View view) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_INVITATION);
        requestMap.put(General.ID, "" + invitation_.getId());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CC_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject != null) {
                        if (jsonObject.has(General.STATUS)) {
                            int status = jsonObject.get(General.STATUS).getAsInt();
                            showResponses(status, view);
                            if (status == 1) {
                                isSuccess = true;
                                dismiss();
                            }
                        } else {
                            showResponses(11, view);
                        }
                    } else {
                        showResponses(12, view);
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(11, view);
    }

    // Make network call to resend invitation
    private void operation(String message, String req, String uid, View view) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, invitation_.getAction());
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.MSG, message);
        requestMap.put("req", req);
        requestMap.put(General.ID, "" + invitation_.getId());
        requestMap.put(General.UID, uid);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CC_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    if (jsonObject != null) {
                        if (jsonObject.has(General.STATUS)) {
                            int status = jsonObject.get(General.STATUS).getAsInt();
                            showResponses(status, view);
                            if (status == 1) {
                                isSuccess = false;
                                dismiss();
                            }
                        } else {
                            showResponses(11, view);
                        }
                    } else {
                        showResponses(12, view);
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(11, view);
    }

    // Show action response
    private void showResponses(int status, View view) {
        String message = "";
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else if (status == 2) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, _context);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.FILL_PARENT;
            int height = ViewGroup.LayoutParams.FILL_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setStyle(Window.FEATURE_NO_TITLE, R.style.MY_DIALOG);
        d.setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Intent intent = new Intent();
        intent.putExtra(General.POSITION, position);
        intent.putExtra(General.IS_SELECTED, isSuccess);
        getTargetFragment().onActivityResult(getTargetRequestCode(), 1, intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.admin_invite_dialog_close:
                dismiss();
                break;
            case R.id.admin_invite_dialog_button_delete:
                delete(v);
                break;
            case R.id.admin_invite_dialog_button_resend:
                String message = inputBox.getText().toString().trim();
                boolean checked = reminderCheck.isChecked();
                String req = "0";
                if (checked) {
                    req = "1";
                }
                String uid = "";
                if (invitation_.getType().equalsIgnoreCase("external")) {
                    uid = "" + invitation_.getU_id();
                }
                operation(message, req, uid, v);
                break;
        }
    }
}
