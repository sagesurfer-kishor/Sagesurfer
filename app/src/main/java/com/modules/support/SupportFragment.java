package com.modules.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.modules.settings.TermsAndConditionActivity;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/3/2018
 * Last Modified on 4/3/2018
 */

public class SupportFragment extends Fragment {

    @BindView(R.id.relativelayout_support_faq)
    RelativeLayout relativeLayoutSupportFAQ;
    @BindView(R.id.relativelayout_support_feedback)
    RelativeLayout relativeLayoutSupportFeedback;
    @BindView(R.id.relativelayout_support_aboutus)
    RelativeLayout relativeLayoutSupportAboutUs;
    @BindView(R.id.relativelayout_support_termsandcondition)
    RelativeLayout relativeLayoutSupportTermsandCondition;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        //For setting status bar color
                Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_support, null);
        unbinder = ButterKnife.bind(this, view);

        activity = getActivity();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.support));
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.relativelayout_support_faq, R.id.relativelayout_support_feedback, R.id.relativelayout_support_aboutus, R.id.relativelayout_support_termsandcondition})
    public void onButtonClick(View view) {
        Intent createIntent = new Intent();
        switch (view.getId()) {
            case R.id.relativelayout_support_faq:
                Intent faqIntent = new Intent(activity.getApplicationContext(), NewFaqActivity.class);
                startActivity(faqIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.relativelayout_support_feedback:
                Intent feedbackIntent = new Intent(activity.getApplicationContext(), FeedbackActivity.class);
                startActivity(feedbackIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.relativelayout_support_aboutus:
                Intent aboutIntent = new Intent(activity.getApplicationContext(), AboutUsActivity.class);
                startActivity(aboutIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.relativelayout_support_termsandcondition:
                createIntent = new Intent(activity.getApplicationContext(), TermsAndConditionActivity.class);
                startActivity(createIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }
}
