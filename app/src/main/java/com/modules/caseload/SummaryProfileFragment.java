package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
 import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.CSProfile_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CSProfileParser_;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 6/04/2018.
 */

public class SummaryProfileFragment extends Fragment {

    private static final String TAG = SummaryProfileFragment.class.getSimpleName();
    @BindView(R.id.imageview_profile_image)
    AppCompatImageView imageViewProfile;
    @BindView(R.id.textview_user_name)
    TextView textViewUserName;
    @BindView(R.id.textview_user_address)
    TextView textViewUserAddress;
    @BindView(R.id.imageview_summary_profile1)
    AppCompatImageView imageViewSummaryProfile1;
    @BindView(R.id.textview_summary_profile1)
    TextView textViewSummaryProfile1;
    @BindView(R.id.imageview_summary_profile2)
    AppCompatImageView imageViewSummaryProfile2;
    @BindView(R.id.textview_summary_profile2)
    TextView textViewSummaryProfile2;
    @BindView(R.id.imageview_summary_profile3)
    AppCompatImageView imageViewSummaryProfile3;
    @BindView(R.id.textview_summary_profile3)
    TextView textViewSummaryProfile3;
    //@BindView(R.id.cardview_strengths)
    //CardView cardViewStrengths;
    @BindView(R.id.relativelayout_strengths)
    RelativeLayout relativeLayoutStrengths;
    @BindView(R.id.textview_strengths)
    TextView textViewStrengths;
    @BindView(R.id.linearlayout_strengths_data)
    LinearLayout linearLayoutStrengthsData;
    //@BindView(R.id.cardview_successes)
    //CardView cardViewSuccesses;
    @BindView(R.id.relativelayout_successes)
    RelativeLayout relativeLayoutSuccesses;
    @BindView(R.id.textview_successses)
    TextView textViewSuccessses;
    @BindView(R.id.linearlayout_success_data)
    LinearLayout linearLayoutSuccessData;

    private ArrayList<CSProfile_> csProfileArrayList;

    private LinearLayout errorLayout, profileLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private Activity activity;
    private Unbinder unbinder;

    String userId = "", userName = "", address = "", profileImageUrl = "";

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

        View view = inflater.inflate(R.layout.fragment_summary_profile, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        //summaryArrayList = new ArrayList<>();
        int  color = GetColor.getHomeIconBackgroundColorColorParse(true);
        textViewStrengths.setTextColor(color);
        textViewStrengths.setText(getResources().getString(R.string.consumer_strengths));
        textViewSuccessses.setTextColor(color);
        textViewSuccessses.setText(getResources().getString(R.string.successes));
        profileLayout = (LinearLayout) view.findViewById(R.id.linearlayout_profile_details);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        return view;
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            profileLayout.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);

            setBannerAndCategoryData();

            if(csProfileArrayList.get(0).getStrength().size() == 0) {
                relativeLayoutStrengths.setVisibility(View.GONE);
            } else {
                setStrengthsData();
            }

            if(csProfileArrayList.get(0).getSuccess().size() == 0) {
                relativeLayoutSuccesses.setVisibility(View.GONE);
            } else {
                setSuccessData();
            }

        }
    }

    //make network call fetch sos from server
    private void fetchCaseloadProfile() {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROFILE_DATA);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    csProfileArrayList = CSProfileParser_.parseCSProfile(response, Actions_.GET_PROFILE_DATA, activity.getApplicationContext(), TAG);
                    if (csProfileArrayList.size() <= 0 || csProfileArrayList.get(0).getStatus() != 1) {
                        if (csProfileArrayList.size() <= 0) {
                            status = 12;
                        } else {
                            status = csProfileArrayList.get(0).getStatus();
                        }
                        showError(true, status);
                        return;
                    }
                    showError(false, status);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchCaseloadProfile();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setBannerAndCategoryData() {
        Glide.with(activity.getApplicationContext())
                .load(csProfileArrayList.get(0).getImage())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(csProfileArrayList.get(0).getImage()))
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(imageViewProfile);

        textViewUserName.setText(csProfileArrayList.get(0).getName());
        textViewUserAddress.setText(csProfileArrayList.get(0).getAddress());

        Glide.with(activity.getApplicationContext())
                .load(csProfileArrayList.get(0).getCategory().get(0).getPath())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(csProfileArrayList.get(0).getCategory().get(0).getPath()))
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(imageViewSummaryProfile1);

        Glide.with(activity.getApplicationContext())
                .load(csProfileArrayList.get(0).getCategory().get(1).getPath())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(csProfileArrayList.get(0).getCategory().get(1).getPath()))
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(imageViewSummaryProfile2);

        Glide.with(activity.getApplicationContext())
                .load(csProfileArrayList.get(0).getCategory().get(2).getPath())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(csProfileArrayList.get(0).getCategory().get(2).getPath()))
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(imageViewSummaryProfile3);

        textViewSummaryProfile1.setText(csProfileArrayList.get(0).getCategory().get(0).getName());
        textViewSummaryProfile2.setText(csProfileArrayList.get(0).getCategory().get(1).getName());
        textViewSummaryProfile3.setText(csProfileArrayList.get(0).getCategory().get(2).getName());
    }

    private void setStrengthsData() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < csProfileArrayList.get(0).getStrength().size(); i++) {
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(activity.getApplicationContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);

            AppCompatImageView image = new AppCompatImageView(activity.getApplicationContext());
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(20, 20);
            layoutParams.gravity=Gravity.CENTER;
            image.setLayoutParams(layoutParams);
            //image.setLayoutParams(new android.view.ViewGroup.LayoutParams(20,20));
            image.setMaxHeight(20);
            image.setMaxWidth(20);

            if(csProfileArrayList.get(0).getStrength().get(i).getPath().equalsIgnoreCase(getResources().getString(R.string.tree))) {
                image.setImageResource(R.drawable.vi_caseload_summary_tree);
            } else if(csProfileArrayList.get(0).getStrength().get(i).getPath().equalsIgnoreCase(getResources().getString(R.string.seed))) {
                image.setImageResource(R.drawable.vi_caseload_summary_seed);
            } else {
                image.setImageResource(R.drawable.vi_caseload_summary_plant);
            }

            // Adds the view to the layout
            ll.addView(image);

            // Create TextView
            TextView textViewStrength = new TextView(activity.getApplicationContext());
            textViewStrength.setText(csProfileArrayList.get(0).getStrength().get(i).getDetails().trim());
            textViewStrength.setLayoutParams(new android.view.ViewGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT));
            textViewStrength.setGravity(Gravity.LEFT);
            textViewStrength.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
            textViewStrength.setTextColor(getResources().getColor(R.color.text_color_primary));
            textViewStrength.setPadding(6,6,6,6);
            ll.addView(textViewStrength);

            linearLayoutStrengthsData.addView(ll);
        }
    }

    private void setSuccessData() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < csProfileArrayList.get(0).getSuccess().size(); i++) {
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(activity.getApplicationContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);

            AppCompatImageView image = new AppCompatImageView(activity.getApplicationContext());
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(20, 20);
            layoutParams.gravity=Gravity.CENTER;
            image.setLayoutParams(layoutParams);
            image.setMaxHeight(20);
            image.setMaxWidth(20);

            if(csProfileArrayList.get(0).getSuccess().get(i).getPath().equalsIgnoreCase(getResources().getString(R.string.tree))) {
                image.setImageResource(R.drawable.vi_caseload_summary_tree);
            } else if(csProfileArrayList.get(0).getSuccess().get(i).getPath().equalsIgnoreCase(getResources().getString(R.string.seed))) {
                image.setImageResource(R.drawable.vi_caseload_summary_seed);
            } else {
                image.setImageResource(R.drawable.vi_caseload_summary_plant);
            }
            //image.setImageResource(R.drawable.seedling);
            // Adds the view to the layout
            ll.addView(image);

            // Create TextView
            TextView textViewSuccess = new TextView(activity.getApplicationContext());
            textViewSuccess.setText(csProfileArrayList.get(0).getSuccess().get(i).getDetails());
            textViewSuccess.setLayoutParams(new android.view.ViewGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT));
            textViewSuccess.setGravity(Gravity.LEFT);
            textViewSuccess.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
            textViewSuccess.setTextColor(getResources().getColor(R.color.text_color_primary));
            textViewSuccess.setPadding(6,6,6,6);
            ll.addView(textViewSuccess);

            linearLayoutSuccessData.addView(ll);
        }
    }
}
