package com.modules.reports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Monika on 8/23/2018.
 */

public class CompareGraphDialogFragment extends DialogFragment {
    private static final String TAG = CompareGraphDialogFragment.class.getSimpleName();

    @BindView(R.id.linearlayout_title)
    LinearLayout linearLayoutTitle;
    @BindView(R.id.title)
    TextView textViewTitle;
    @BindView(R.id.webview_comparegraph)
    WebView webViewCompareGraph;

    String url = "", label = "";
    Toolbar toolbar;
    private Activity activity;
    private Unbinder unbinder;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        //DialogFragment.getDialog().setCanceledOnTouchOutside(true);

        View view = inflater.inflate(R.layout.fragment_compare_graph_dialog, null);
        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*toolbar = (Toolbar) view.findViewById(R.id.activity_toolbar_layout);
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);*/

        linearLayoutTitle.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        Bundle data = getArguments();
        if (data.containsKey(General.URL)) {
            url = data.getString(General.URL);
            label = data.getString(General.LABLE);
            textViewTitle.setText(label);
            webViewCompareGraph.getSettings().setLoadWithOverviewMode(true);
            webViewCompareGraph.setInitialScale(100);
            webViewCompareGraph.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            webViewCompareGraph.setScrollbarFadingEnabled(false);
            webViewCompareGraph.getSettings().setJavaScriptEnabled(true);
            webViewCompareGraph.getSettings().setLoadsImagesAutomatically(true);
            webViewCompareGraph.getSettings().setBuiltInZoomControls(true);
            webViewCompareGraph.loadUrl(url);
        } else {
            dismiss();
        }

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
