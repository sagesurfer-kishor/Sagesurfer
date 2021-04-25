package com.sagesurfer.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/12/2018
 * Last Modified on 4/12/2018
 */

/*
 * This class will act as global video player
 * It will play video in full screen mode
 */

public class VideoViewer extends DialogFragment implements View.OnClickListener, OnPreparedListener {

    @BindView(R.id.imagebutton_videoviewer_back)
    AppCompatImageButton imageButtonVideoViewerBack;
    @BindView(R.id.videoview_videoviewer)
    VideoView videoViewVideoViewer;

    private Unbinder unbinder;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_viewer_layout, null);
        unbinder = ButterKnife.bind(this, view);

        imageButtonVideoViewerBack.setOnClickListener(this);

        Bundle data = getArguments();
        if (data != null && data.containsKey(General.PATH)) {
            String path = data.getString(General.PATH);
            setupVideoView(path);
        } else {
            dismiss();
        }

        return view;
    }

    private void setupVideoView(String path) {
        videoViewVideoViewer.setOnPreparedListener(this);
        videoViewVideoViewer.setVideoURI(Uri.parse(path));
    }

    @Override
    public void onPrepared() {
        videoViewVideoViewer.start();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagebutton_videoviewer_back:
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
