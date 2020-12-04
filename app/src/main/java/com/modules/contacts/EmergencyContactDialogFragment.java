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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class EmergencyContactDialogFragment extends DialogFragment {

    private static final String TAG = EmergencyContactDialogFragment.class.getSimpleName();

    @BindView(R.id.textview_contact_details_home_number)
    TextView textViewContactDetailsHomeNumber;
    @BindView(R.id.textview_contact_details_mobile_number)
    TextView textViewContactDetailsMobileNumber;
    @BindView(R.id.textview_contact_details_name)
    TextView textViewContactDetailsName;
    @BindView(R.id.textview_contact_details_role)
    TextView textViewContactDetailsRole;
    @BindView(R.id.imageview_contact_details_image)
    ImageView imageViewContactDetailsImage;
    @BindView(R.id.linearlayout_contact_details_email)
    LinearLayout linearLayoutContactDetailsEmail;
    @BindView(R.id.linearlayout_contact_details_work)
    LinearLayout linearLayoutContactDetailsWork;

    private boolean isFetch = true;
    private Unbinder unbinder;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_dialog_emergency_contact, null);
        unbinder = ButterKnife.bind(this, view);

        linearLayoutContactDetailsEmail.setVisibility(View.GONE);
        linearLayoutContactDetailsWork.setVisibility(View.GONE);

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
        if (d != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            assert d.getWindow() != null;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setStyle(Window.FEATURE_NO_TITLE, R.style.MY_DIALOG);
        if (d != null) {
            d.setCancelable(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle data = getArguments();
        if (data != null && data.containsKey(General.NAME)) {
            isFetch = false;
            String name = data.getString(General.NAME);
            String home = data.getString("home");
            String mobile = data.getString("mobile");
            String image = data.getString(General.IMAGE);
            String role = data.getString(General.ROLE);

            setData(name, home, mobile, image, role);
        }
        if (isFetch) {
            getNumbers();
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Make network call to fetch emergency contacts from server
    private void getNumbers() {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_OWNER_NUMBER);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_YOUTH_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getActivity().getApplicationContext(), getActivity());
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getActivity().getApplicationContext(), getActivity());
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, "phone_number");
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        if (object.has(General.STATUS)) {
                            status = object.get(General.STATUS).getAsInt();
                            if (status == 1) {
                                String name = object.get(General.NAME).getAsString();
                                String home = object.get("home").getAsString();
                                String mobile = object.get("mobile").getAsString();
                                String image = object.get(General.IMAGE).getAsString();
                                String role = object.get(General.ROLE).getAsString();

                                setData(name, home, mobile, image, role);
                                return;
                            } else {
                                ShowToast.toast("Contact Not Found", getActivity().getApplicationContext());
                            }
                        } else {
                            status = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (status != 1) {
            dismiss();
        }
    }

    // Set data to respective fields
    private void setData(String name, String home, String mobile, String image, String role) {
        textViewContactDetailsHomeNumber.setText(home);
        textViewContactDetailsMobileNumber.setText(mobile);
        textViewContactDetailsName.setText(name);
        textViewContactDetailsRole.setText(role);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(getActivity().getApplicationContext())
                .load(image)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(image))
                        .transform(new CircleTransform(getActivity().getApplicationContext())))
                .into(imageViewContactDetailsImage);
    }
}
