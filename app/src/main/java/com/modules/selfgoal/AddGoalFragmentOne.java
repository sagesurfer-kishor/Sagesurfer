package com.modules.selfgoal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.AddGoalActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.snack.ShowSnack;
import com.storage.preferences.AddGoalPreferences;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 24/03/2018
 *         Last Modified on
 */

public class AddGoalFragmentOne extends Fragment implements View.OnClickListener {

    private static final String TAG = AddGoalFragmentOne.class.getSimpleName();

    @BindView(R.id.textview_addgoalactivity_bubble_two)
    TextView textViewAddGoalActivityBubbleTwo;
    @BindView(R.id.relativelayout_fragmentaddgoalone_image)
    RelativeLayout relativeLayoutFragmentAddGoalOneImage;
    @BindView(R.id.imageview_fragmentaddgoalone_uploadimage)
    ImageView imageViewFragmentAddGoalOneUploadImage;
    @BindView(R.id.imageview_fragmentaddgoalone_uploadicon)
    ImageView imageViewFragmentAddGoalOneUploadIcon;
    @BindView(R.id.textview_fragmentaddgoalone_uploadtext)
    TextView textViewFragmentAddGoalOneUploadText;
    @BindView(R.id.imageview_fragmentaddgoalone_uploadremove)
    ImageView imageViewFragmentAddGoalOneUploadRemove;
    @BindView(R.id.progressbar_fragmentaddgoalone_upload)
    ProgressBar progressBarFragmentAddGoalOneUpload;
    @BindView(R.id.edittext_fragmentaddgoalone_name)
    EditText editTextFragmentAddGoalOneName;
    @BindView(R.id.edittext_fragmentaddgoalone_description)
    EditText editTextFragmentAddGoalOneDescription;
    @BindView(R.id.edittext_fragmentaddgoalone_impact)
    EditText editTextFragmentAddGoalOneImpact;

    private String thumb_id = "0", thumb_path = "", name = "", impact = "", description = "";
    private ArrayList<HashMap<String, String>> imageList;

    private Activity activity;
    private AddGoalActivityInterface addGoalActivityInterface;
    private BroadcastReceiver receiver;
    private Unbinder unbinder;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            addGoalActivityInterface = (AddGoalActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddGoalActivityInterface");
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_add_goal_one, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        editTextFragmentAddGoalOneImpact.setVisibility(View.GONE);
        textViewAddGoalActivityBubbleTwo.setOnClickListener(this);
        imageViewFragmentAddGoalOneUploadImage.setOnClickListener(this);
        imageViewFragmentAddGoalOneUploadIcon.setOnClickListener(this);
        imageViewFragmentAddGoalOneUploadRemove.setOnClickListener(this);
        progressBarFragmentAddGoalOneUpload.setVisibility(View.GONE);

        activity.registerReceiver(receiver, new IntentFilter("0"));
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(General.CHECK_IN)) {
                    if (intent.getStringExtra(General.CHECK_IN).equalsIgnoreCase("1")) {
                        if (validToMove()) {
                            addGoalActivityInterface.moveToNext(true, 1);
                        }
                    }
                }
                if (intent.hasExtra(General.IMAGE)) {
                    setImage();
                }
            }
        };
        setData();
        return view;
    }

    // set data to respective fields from shared preferences
    private void setData() {
        if (AddGoalPreferences.contains(General.NAME) && AddGoalPreferences.get(General.NAME) != null) {
            editTextFragmentAddGoalOneName.setText(AddGoalPreferences.get(General.NAME));
        }
        if (AddGoalPreferences.contains(General.DESCRIPTION) && AddGoalPreferences.get(General.DESCRIPTION) != null) {
            editTextFragmentAddGoalOneDescription.setText(AddGoalPreferences.get(General.DESCRIPTION));
        }
        if (AddGoalPreferences.contains(General.IMPACT) && AddGoalPreferences.get(General.IMPACT) != null) {
            editTextFragmentAddGoalOneImpact.setText(AddGoalPreferences.get(General.IMPACT));
        }
        setImage();
    }

    // check fields for valid input data
    private boolean validToMove() {
        name = editTextFragmentAddGoalOneName.getText().toString().trim();
        impact = editTextFragmentAddGoalOneImpact.getText().toString().trim();
        description = editTextFragmentAddGoalOneDescription.getText().toString().trim();
        if (name.length() <= 0) {
            editTextFragmentAddGoalOneName.setError("Goal Name Compulsory");
            return false;
        }
        if (name.length() > 0 && name.length() < 3) {
            editTextFragmentAddGoalOneName.setError("Goal Name Compulsory\nMin 3 char required\nMax 25 char allowed");
            return false;
        }
        if (thumb_id.length() < 0 || thumb_id.equalsIgnoreCase("0")) {
            ShowSnack.viewWarning(textViewFragmentAddGoalOneUploadText, "Please select image", activity.getApplicationContext());
            return false;
        }
        if (impact.length() > 0 && impact.length() < 3) {
            editTextFragmentAddGoalOneImpact.setError("Min 3 char required");
            return false;
        }
        if (description.length() > 0 && description.length() < 3) {
            editTextFragmentAddGoalOneDescription.setError("Min 3 char required");
            return false;
        }
        return true;
    }

    // make network call to fetch images from server
    private void fetchImages() {
        imageList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_IMAGES);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_SELF_GOAL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Actions_.GET_IMAGES)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.GET_IMAGES);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            if (!object.has(General.STATUS)) {
                                return;
                            }
                            if (object.getInt(General.STATUS) == 1) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(General.STATUS, object.getString(General.STATUS));
                                map.put(General.ID, object.getString(General.ID));
                                map.put(General.NAME, object.getString(General.NAME));

                                imageList.add(map);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // set image to self goal
    private void setImage() {
        if (AddGoalPreferences.contains(General.URL_IMAGE) && AddGoalPreferences.contains(General.IMAGE)) {
            thumb_path = AddGoalPreferences.get(General.URL_IMAGE);
            thumb_id = AddGoalPreferences.get(General.IMAGE);
            if (thumb_path != null && thumb_path.length() > 0) {
                //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                /*relativeLayoutFragmentAddGoalOneImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = relativeLayoutFragmentAddGoalOneImage.getMeasuredWidth();
                int height = relativeLayoutFragmentAddGoalOneImage.getMeasuredHeight();*/
                Glide.with(activity.getApplicationContext())
                        .load(thumb_path)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(imageViewFragmentAddGoalOneUploadImage);

                toggleClose(true);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.registerReceiver(receiver, new IntentFilter("0"));
        setImage();
        if (imageList == null || imageList.size() < 0) {
            fetchImages();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        activity.unregisterReceiver(receiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(General.IMAGES, imageList);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            imageList = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable(General.IMAGES);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if (activity != null) {
                AddGoalPreferences.initialize(activity.getApplicationContext());
                AddGoalPreferences.save(General.NAME, name, TAG);
                AddGoalPreferences.save(General.DESCRIPTION, description, TAG);
                AddGoalPreferences.save(General.IMPACT, impact, TAG);
                AddGoalPreferences.save(General.IMAGE, thumb_id, TAG);
                AddGoalPreferences.save(General.URL_IMAGE, thumb_path, TAG);
            }
        }
    }

    // Open self goal image options dialog
    private void openImageDialog() {
        FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(General.IMAGES);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        AddDialogFragment detailsFragment = new AddDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(General.IMAGES, imageList);
        detailsFragment.setArguments(bundle);
        detailsFragment.show(fragmentManager, General.IMAGES);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_addgoalactivity_bubble_two:
                if (validToMove()) {
                    addGoalActivityInterface.moveToNext(true, 1);
                }
                break;
            case R.id.imageview_fragmentaddgoalone_uploadimage:
                openImageDialog();
                break;
            case R.id.imageview_fragmentaddgoalone_uploadicon:
                openImageDialog();
                break;
            case R.id.imageview_fragmentaddgoalone_uploadremove:
                thumb_id = "0";
                AddGoalPreferences.removeKey(General.IMAGE, TAG);
                AddGoalPreferences.removeKey(General.URL_IMAGE, TAG);
                toggleClose(false);
                break;
        }
    }

    private void toggleClose(boolean isVisible) {
        if (isVisible) {
            imageViewFragmentAddGoalOneUploadRemove.setVisibility(View.VISIBLE);
            imageViewFragmentAddGoalOneUploadImage.setVisibility(View.VISIBLE);
            imageViewFragmentAddGoalOneUploadIcon.setVisibility(View.GONE);
            textViewFragmentAddGoalOneUploadText.setVisibility(View.GONE);
        } else {
            //imageViewFragmentAddGoalOneUploadImage.setImageResource(R.drawable.vi_self_goal_upload_image);
            imageViewFragmentAddGoalOneUploadRemove.setVisibility(View.GONE);
            imageViewFragmentAddGoalOneUploadImage.setVisibility(View.GONE);
            imageViewFragmentAddGoalOneUploadIcon.setVisibility(View.VISIBLE);
            textViewFragmentAddGoalOneUploadText.setVisibility(View.VISIBLE);
        }
    }

}
