package com.sagesurfer.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.library.FileOperations;
import com.storage.preferences.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/12/2018
 * Last Modified on 4/12/2018
 */

/*
 * This is global file to view images in full screen mode
 * User can view full screen image here and download it also
 */

public class AttachmentViewer extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.imagebutton_attachmentviewer_back)
    AppCompatImageButton imageButtonAttachmentViewerBack;
    @BindView(R.id.imageview_attachmentviewer)
    ImageView imageViewAttachmentViewer;
    @BindView(R.id.linearlayout_attachmentviewer_footer)
    LinearLayout linearLayoutAttachmentViewerFooter;
    @BindView(R.id.textview_attachmentviewer_name)
    TextView textViewAttachmentViewerName;
    @BindView(R.id.textview_attachmentviewer_size)
    TextView textViewAttachmentViewerSize;
    @BindView(R.id.imagebutton_attachmentviewer_download)
    AppCompatImageButton imageButtonAttachmentViewerDownload;

    private String path, name, directory, id;
    private Unbinder unbinder;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attachment_viewer_layout, null);
        unbinder = ButterKnife.bind(this, view);

        imageButtonAttachmentViewerBack.setOnClickListener(this);
        imageButtonAttachmentViewerDownload.setOnClickListener(this);

        Bundle data = getArguments();
        if (data != null && data.containsKey(General.PATH)) {
            path = data.getString(General.PATH);
            id = data.getString(General.ID);
            directory = data.getString(General.DIRECTORY);
            name = data.getString(General.IMAGE);
            String size = data.getString(General.SIZE);

            setData(name, size, path, view);
        } else {
            dismiss();
        }

        //Monika on 25/01/19 for wall post adding attachment, do not download image(as its from local storage)
        if (directory.equalsIgnoreCase(DirectoryList.WALL_DOWNLOAD) && !path.contains("http")) {
            imageButtonAttachmentViewerDownload.setVisibility(View.GONE);
        }

        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")
                ||  !General.isCurruntUserHasPermissionToOnlyViewCantPerformAnyAction()) {
            imageButtonAttachmentViewerDownload.setVisibility(View.VISIBLE);
        } else {
            imageButtonAttachmentViewerDownload.setVisibility(View.GONE);
        }
        return view;
    }

    // set values to respective fields
    private void setData(String name, String size, String url, View view) {
        textViewAttachmentViewerName.setText(name);
        textViewAttachmentViewerSize.setText(size);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(getActivity().getApplicationContext())
                .load(url)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                .into(imageViewAttachmentViewer);
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
            case R.id.imagebutton_attachmentviewer_back:
                dismiss();
                break;
            case R.id.imagebutton_attachmentviewer_download:
                DownloadFile downloadFile = new DownloadFile();
                if (directory.equalsIgnoreCase(DirectoryList.DIR_CONSENT_FILES)) {
                    downloadFile.download(Long.parseLong(id), path, FileOperations.getFileName(path), directory, getActivity());
                } else {
                    downloadFile.download(Long.parseLong(id), path, name, directory, getActivity());
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
