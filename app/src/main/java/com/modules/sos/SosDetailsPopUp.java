package com.modules.sos;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 28-07-2017
 *         Last Modified on 14-12-2017
 **/


public class SosDetailsPopUp extends DialogFragment {

    private static final String TAG = SosDetailsPopUp.class.getSimpleName();
    private int id = 0;

    private Activity activity;

    private ImageView imageOne, imageTwo, imageThree, imageFour;
    private LinearLayout containerOne, containerTwo, containerThree, containerFour;
    private TextView nameOne, nameTwo, nameThree, nameFour;
    private TextView timeOne, timeTwo, timeThree, timeFour;
    private TextView messageOne, messageTwo, messageThree, messageFour;
    private View lineOne, lineTwo, lineThree;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sos_details_popup, container, false);

        activity = getActivity();

        Bundle data = getArguments();
        if (data.containsKey(General.ID)) {
            id = data.getInt(General.ID, 0);
        }

        imageOne = (ImageView) rootView.findViewById(R.id.sos_details_pop_image_one);
        imageTwo = (ImageView) rootView.findViewById(R.id.sos_details_pop_image_two);
        imageThree = (ImageView) rootView.findViewById(R.id.sos_details_pop_image_three);
        imageFour = (ImageView) rootView.findViewById(R.id.sos_details_pop_image_four);

        containerOne = (LinearLayout) rootView.findViewById(R.id.sos_details_pop_container_one);
        containerTwo = (LinearLayout) rootView.findViewById(R.id.sos_details_pop_container_two);
        containerThree = (LinearLayout) rootView.findViewById(R.id.sos_details_pop_container_three);
        containerFour = (LinearLayout) rootView.findViewById(R.id.sos_details_pop_container_four);

        nameOne = (TextView) rootView.findViewById(R.id.sos_details_pop_name_one);
        nameTwo = (TextView) rootView.findViewById(R.id.sos_details_pop_name_two);
        nameThree = (TextView) rootView.findViewById(R.id.sos_details_pop_name_three);
        nameFour = (TextView) rootView.findViewById(R.id.sos_details_pop_name_four);

        messageOne = (TextView) rootView.findViewById(R.id.sos_details_pop_message_one);
        messageTwo = (TextView) rootView.findViewById(R.id.sos_details_pop_message_two);
        messageThree = (TextView) rootView.findViewById(R.id.sos_details_pop_message_three);
        messageFour = (TextView) rootView.findViewById(R.id.sos_details_pop_message_four);

        timeOne = (TextView) rootView.findViewById(R.id.sos_details_pop_time_one);
        timeTwo = (TextView) rootView.findViewById(R.id.sos_details_pop_time_two);
        timeThree = (TextView) rootView.findViewById(R.id.sos_details_pop_time_three);
        timeFour = (TextView) rootView.findViewById(R.id.sos_details_pop_time_four);

        lineOne = rootView.findViewById(R.id.sos_details_pop_vertical_one);
        lineTwo = rootView.findViewById(R.id.sos_details_pop_vertical_two);
        lineThree = rootView.findViewById(R.id.sos_details_pop_vertical_three);

        if (id > 0) {
            getDetails();
        } else {
            dismiss();
        }
        return rootView;
    }

    // get contact name based on it's status
    private String getName(SosComments_ sosComments_) {
        if (sosComments_.getCurrentStatus() == 7) {
            return "Contact not found";
        }
        return sosComments_.getName();
    }

    // get sos message based on it's status
    private String getMessage(SosComments_ sosComments_) {
        if (sosComments_.getCurrentStatus() != 7) {
            return sosComments_.getMessage();
        }
        return "";
    }

    // set contact images to respective position in sos trail
    private void setImages(SosComments_ e1, SosComments_ e2, SosComments_ e3, SosComments_ cc) {
        int count = 0;
        if (e1.getStatus() != 2) {
            nameOne.setText(getName(e1));
            messageOne.setText(getMessage(e1));
            if (e1.getPhoto().trim().length() <= 0) {
                imageOne.setImageResource(R.drawable.ic_user_male);
            } else {
                setImage(e1.getPhoto(), imageOne);
            }
            imageOne.setBackgroundResource(GetDrawable.circle(e1.getCurrentStatus()));
            lineOne.setVisibility(View.GONE);
        } else {
            containerOne.setVisibility(View.GONE);
        }

        if (e2.getStatus() != 2) {
            count++;
            nameTwo.setText(getName(e2));
            messageTwo.setText(getMessage(e2));
            if (e2.getPhoto().trim().length() <= 0) {
                imageTwo.setImageResource(R.drawable.ic_user_male);
            } else {
                setImage(e2.getPhoto(), imageTwo);
            }
            imageTwo.setBackgroundResource(GetDrawable.circle(e2.getCurrentStatus()));
            lineOne.setVisibility(View.VISIBLE);
            lineTwo.setVisibility(View.GONE);
        } else {
            containerTwo.setVisibility(View.GONE);
        }

        if (e3.getStatus() != 2) {
            count++;
            nameThree.setText(getName(e3));
            messageThree.setText(getMessage(e3));
            if (e3.getPhoto().trim().length() <= 0) {
                imageThree.setImageResource(R.drawable.ic_user_male);
            } else {
                setImage(e3.getPhoto(), imageThree);
            }
            imageThree.setBackgroundResource(GetDrawable.circle(e3.getCurrentStatus()));
            lineOne.setVisibility(View.VISIBLE);
            lineTwo.setVisibility(View.VISIBLE);
            lineThree.setVisibility(View.GONE);
        } else {
            containerThree.setVisibility(View.GONE);
        }

        if (cc.getStatus() != 2) {
            count++;
            nameFour.setText(getName(cc));
            messageFour.setText(getMessage(cc));
            if (cc.getPhoto().trim().length() <= 0) {
                imageFour.setImageResource(R.drawable.ic_user_male);
            } else {
                setImage(cc.getPhoto(), imageFour);
            }
            imageFour.setBackgroundResource(GetDrawable.circle(cc.getCurrentStatus()));
            lineOne.setVisibility(View.VISIBLE);
            lineTwo.setVisibility(View.VISIBLE);
            lineThree.setVisibility(View.VISIBLE);
        } else {
            containerFour.setVisibility(View.GONE);
        }

        setTime(count, e1, e2, e3, cc);
    }

    // set time action time for respective contact location
    private void setTime(int position, SosComments_ e1, SosComments_ e2, SosComments_ e3, SosComments_ cc) {
        switch (position) {
            case 0:
                timeOne.setText(GetTime.wallTime(e1.getTimestamp()));
                break;
            case 1:
                timeTwo.setText(GetTime.wallTime(e2.getTimestamp()));
                break;
            case 2:
                timeThree.setText(GetTime.wallTime(e3.getTimestamp()));
                break;
            case 3:
                timeFour.setText(GetTime.wallTime(cc.getTimestamp()));
                break;
        }
    }

    // set profile photo of each involved entity in sos chain
    private void setImage(String path, ImageView userImage) {
        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(activity.getApplicationContext())
                .load(path)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(path))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(userImage);
    }

    // make network call to fetch sos details from sos id
    private void getDetails() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ID, "" + id);
        String response;
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.URL_SOS_DETAILS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                JsonObject jsonObject = GetJson_.getJson(response);
                JsonObject commentObject = jsonObject.get(Actions_.COMMENTS).getAsJsonObject();

                JsonObject e1Object = commentObject.getAsJsonObject("e1");
                JsonObject e2Object = commentObject.getAsJsonObject("e2");
                JsonObject e3Object = commentObject.getAsJsonObject("e3");
                JsonObject ccObject = commentObject.getAsJsonObject("cc");

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                SosComments_ e1 = gson.fromJson(e1Object, SosComments_.class);
                SosComments_ e2 = gson.fromJson(e2Object, SosComments_.class);
                SosComments_ e3 = gson.fromJson(e3Object, SosComments_.class);
                SosComments_ cc = gson.fromJson(ccObject, SosComments_.class);

                setImages(e1, e2, e3, cc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        assert dialog.getWindow() != null;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d.getWindow() != null) {
            int width = ViewGroup.LayoutParams.FILL_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        d.setCancelable(true);
    }

}
