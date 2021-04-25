package com.modules.contacts;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Contacts_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.views.CircleTransform;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 12-08-2017
 * Last Modified on 13-12-2017
 **/

public class ContactDetailsDialogFragment extends DialogFragment {

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_dialog_emergency_contact, null);

        Contacts_ contacts_ = new Contacts_();
        Bundle data = getArguments();
        if (data.containsKey(Actions_.TEAM_CONTACT)) {
            contacts_ = (Contacts_) data.getSerializable(Actions_.TEAM_CONTACT);
        } else {
            dismiss();
        }
        TextView home = (TextView) view.findViewById(R.id.textview_contact_details_home_number);
        TextView work = (TextView) view.findViewById(R.id.textview_contact_details_work_number);
        TextView mobile = (TextView) view.findViewById(R.id.textview_contact_details_mobile_number);
        TextView name = (TextView) view.findViewById(R.id.textview_contact_details_name);
        TextView role = (TextView) view.findViewById(R.id.textview_contact_details_role);
        TextView email = (TextView) view.findViewById(R.id.textview_contact_details_email);
        ImageView profile = (ImageView) view.findViewById(R.id.imageview_contact_details_image);

        if (contacts_ != null) {
            home.setText(contacts_.getHome());
            work.setText(contacts_.getWork());
            mobile.setText(contacts_.getMobile());
            String fullName = contacts_.getFirstName() + " " + contacts_.getLastName();
            name.setText(fullName);
            role.setText(contacts_.getRole());
            email.setText(contacts_.getEmail());

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(getActivity().getApplicationContext())
                    .load(contacts_.getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(contacts_.getImage()))
                            .transform(new CircleTransform(getActivity().getApplicationContext())))
                    .into(profile);
        }
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        assert dialog.getWindow() != null;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d.getWindow() != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setStyle(Window.FEATURE_NO_TITLE, R.style.MY_DIALOG);
        d.setCancelable(true);
        d.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setStyle(android.app.DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }
}
