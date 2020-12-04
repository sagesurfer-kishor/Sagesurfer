package com.modules.fms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 03-08-2017
 *         Last Modified on 13-12-2017
 **/

public class FileViewDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = FileViewDialog.class.getSimpleName();

    private WebView webView;
    private ProgressBar contentLoadingProgressBar;

    private Activity activity;
    int _id = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_view_dialog_layout, null);

        activity = getActivity();

        Preferences.initialize(activity.getApplicationContext());
        AppCompatImageButton backButton = (AppCompatImageButton) view.findViewById(R.id.file_view_dialog_back);
        backButton.setOnClickListener(this);

        TextView titleText = (TextView) view.findViewById(R.id.file_view_dialog_title);
        titleText.setText(activity.getApplicationContext().getResources().getString(R.string.view));

        webView = (WebView) view.findViewById(R.id.file_view_dialog_web_view);
        contentLoadingProgressBar = (ProgressBar) view.findViewById(R.id.file_view_dialog_pb);

        Bundle data = getArguments();
        if (data != null && data.containsKey(General.ID)) {
            _id = data.getInt(General.ID);
            if (_id != 0) {
                //showFile(_id);
            } else {
                dismiss();
            }
        } else {
            dismiss();
        }
        return view;
    }

    class WebViewClients extends WebViewClient {
        private final ProgressBar progress;

        WebViewClients(ProgressBar progress) {
            this.progress = progress;
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progress.setVisibility(View.GONE);
        }
    }

    //show file in web view
    @SuppressLint("SetJavaScriptEnabled")
    private void showFile(int id) {
        String url = FileSharingOperations.getView("" + id, activity, activity);
        if (url.trim().length() > 0) {
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClients(contentLoadingProgressBar));
            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            ShowToast.internalErrorOccurred(activity.getApplicationContext());
            dismiss();
        }
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
            int width = ViewGroup.LayoutParams.FILL_PARENT;
            int height = ViewGroup.LayoutParams.FILL_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setStyle(Window.FEATURE_NO_TITLE, R.style.MY_DIALOG);
        d.setCancelable(false);

        if (_id != 0) {
            showFile(_id);
        } else {
            dismiss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_view_dialog_back:
                dismiss();
                break;
        }
    }

}
