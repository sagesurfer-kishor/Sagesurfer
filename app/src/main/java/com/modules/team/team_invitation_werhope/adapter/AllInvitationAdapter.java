package com.modules.team.team_invitation_werhope.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.modules.team.team_invitation_werhope.model.Invitation;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class AllInvitationAdapter extends ArrayAdapter<Invitation> {
    private static final String TAG = AllInvitationAdapter.class.getSimpleName();
    private List<Invitation> invitationList;
    private Context mContext;
    private Activity activity;
    private AllInvitationAdapterListener allInvitationAdapterListener;

    public AllInvitationAdapter(Activity activity, List<Invitation> invitationList1, AllInvitationAdapterListener allInvitationAdapterListener) {
        super(activity, 0, invitationList1);
        this.invitationList = invitationList1;
        this.mContext = activity.getApplicationContext();
        this.allInvitationAdapterListener = allInvitationAdapterListener;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return invitationList.size();
    }

    @Override
    public Invitation getItem(int position) {
        if (invitationList != null && invitationList.size() > 0) {
            return invitationList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return invitationList.get(position).getId();
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.invitation_list_item, parent, false);

            viewHolder.invitationName = (TextView) view.findViewById(R.id.invitation_item_name);
            viewHolder.invitationDate = (TextView) view.findViewById(R.id.invitation_date);
            viewHolder.invitationRole = (TextView) view.findViewById(R.id.invitation_item_role);
            viewHolder.invitationUsername = (TextView) view.findViewById(R.id.invitation_username);
            viewHolder.invitationStatus = (TextView) view.findViewById(R.id.invitation_status);
            viewHolder.pendingDeclinedBtn = (TextView) view.findViewById(R.id.pending_acceptance_button);

            viewHolder.invitationImage = (ImageView) view.findViewById(R.id.invitation_item_image);
            viewHolder.invitationShareImage = (ImageView) view.findViewById(R.id.invitation_share_image);
            viewHolder.invitationDeleteImage = (ImageView) view.findViewById(R.id.invitation_delete_image);

            viewHolder.invitationButtonLayout = (RelativeLayout) view.findViewById(R.id.inviation_button_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Invitation invitation = invitationList.get(position);

        if (invitation.getStatus() == 1) {
            viewHolder.invitationName.setText(ChangeCase.toTitleCase(invitation.getName()));
            viewHolder.invitationDate.setText(getDate(invitation.getTimestamp()));
            viewHolder.invitationRole.setText(" (" + ChangeCase.toTitleCase(invitation.getRole()) + ") ");
            viewHolder.invitationUsername.setText(invitation.getEmail());
            viewHolder.invitationStatus.setText(ChangeCase.toTitleCase(invitation.getRequest_status()));

            if (invitation.getRequest_status().equalsIgnoreCase("Declined")) {
                viewHolder.pendingDeclinedBtn.setText("Request Declined");
            } else {
                viewHolder.pendingDeclinedBtn.setText("Pending for Acceptance");
            }

            Glide.with(activity.getApplicationContext())
                    .load(invitation.getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(invitation.getImage()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.invitationImage);

            if (invitation.getRequest_status().equals("Pending")) {
                viewHolder.invitationButtonLayout.setVisibility(View.VISIBLE);
            }

            viewHolder.invitationShareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendReminderDailog(v, invitation);
                }
            });

            viewHolder.invitationDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteInvitation(invitation.getId());
                    allInvitationAdapterListener.onDeletedInvitationClicked();
                }
            });

        }
        return view;
    }

    private void sendReminderDailog(View v, final Invitation invitation) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_positive_quote);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final Button buttonSubmit = (Button) dialog.findViewById(R.id.button_submit);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
        final EditText invitationComment = (EditText) dialog.findViewById(R.id.edittext_comment_quote);
        final TextView invitationTitle = (TextView) dialog.findViewById(R.id.text);
        invitationTitle.setText("Send reminder");
        invitationComment.setHint("Message");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String invitationMsg = invitationComment.getText().toString().trim();
                if (inviValidation(v, invitationMsg)) {
                    sendReminderInvitation(invitation, invitationMsg);
                    dialog.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendReminderInvitation(Invitation invitation, String invitationMsg) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SEND_REMINDER);
        requestMap.put(General.MSG, invitationMsg);
        requestMap.put(General.ID, String.valueOf(invitation.getId()));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonInvitation = jsonObject.getAsJsonObject(Actions_.SEND_REMINDER);
                    if (jsonInvitation.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, jsonInvitation.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity, jsonInvitation.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean inviValidation(View view, String invitationMsg) {
        if (invitationMsg == null || invitationMsg.trim().length() <= 0) {
            Toast.makeText(view.getContext(), "Comment: Min 2 char required", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void deleteInvitation(long invitation_id) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_INVITATION);
        requestMap.put(General.ID, String.valueOf(invitation_id));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonInvitation = jsonObject.getAsJsonObject(Actions_.DELETE_INVITATION);
                    if (jsonInvitation.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(activity, "Invitation will be deleted from platform.", Toast.LENGTH_LONG).show();
                        notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ViewHolder {
        TextView invitationName, invitationDate, invitationRole, invitationUsername, invitationStatus, pendingDeclinedBtn;
        ImageView invitationImage, invitationShareImage, invitationDeleteImage;
        RelativeLayout invitationButtonLayout;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

    public interface AllInvitationAdapterListener {
        void onDeletedInvitationClicked();
    }
}
