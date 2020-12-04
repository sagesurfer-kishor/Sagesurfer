package com.modules.wall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.AttachmentViewer;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 12-07-2017
 *         Last Modified on 15-12-2017
 */

class AttachmentsListAdapter extends ArrayAdapter<Attachment_> {

    private final List<Attachment_> attachmentList;
    private final Activity activity;

    AttachmentsListAdapter(Activity activity, List<Attachment_> attachmentList) {
        super(activity, 0, attachmentList);
        this.attachmentList = attachmentList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return attachmentList.size();
    }

    @Override
    public Attachment_ getItem(int position) {
        if (attachmentList != null && attachmentList.size() > 0) {
            return attachmentList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.attachment_card_layout, parent, false);

            viewHolder.nameText = (TextView) view.findViewById(R.id.attachment_list_item_name);
            viewHolder.sizeText = (TextView) view.findViewById(R.id.attachment_list_item_size);

            viewHolder.fileName = (TextView) view.findViewById(R.id.attachment_list_item_file_name);
            viewHolder.fileSize = (TextView) view.findViewById(R.id.attachment_list_item_file_size);

            viewHolder.attachmentPort = (ImageView) view.findViewById(R.id.attachment_list_item_image_port);
            viewHolder.attachmentLand = (ImageView) view.findViewById(R.id.attachment_list_item_image_land);
            viewHolder.fileThumbnail = (AppCompatImageView) view.findViewById(R.id.attachment_list_item_file_thumbnail);

            viewHolder.fileDownload = (AppCompatImageButton) view.findViewById(R.id.attachment_list_item_file_download);
            viewHolder.imageDownload = (AppCompatImageButton) view.findViewById(R.id.attachment_list_item_download);

            viewHolder.fileLayout = (LinearLayout) view.findViewById(R.id.attachment_list_item_file_layout);
            viewHolder.imageLayout = (LinearLayout) view.findViewById(R.id.attachment_list_item_image_layout);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (CheckFileType.imageFile(attachmentList.get(position).getImage())) {
            viewHolder.imageLayout.setTag(position);
            viewHolder.fileLayout.setVisibility(View.GONE);
            viewHolder.imageLayout.setVisibility(View.VISIBLE);

            viewHolder.nameText.setText(attachmentList.get(position).getImage());
            viewHolder.sizeText.setText(attachmentList.get(position).getSize());

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(attachmentList.get(position).getPath())
                    .transition(withCrossFade())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                            int width = drawable.getIntrinsicWidth();
                            int height = drawable.getIntrinsicHeight();
                            if (height > (width + 100)) {
                                viewHolder.attachmentLand.setVisibility(View.GONE);
                                viewHolder.attachmentPort.setVisibility(View.VISIBLE);
                                viewHolder.attachmentPort.setImageDrawable(drawable);
                            } else {
                                viewHolder.attachmentLand.setVisibility(View.VISIBLE);
                                viewHolder.attachmentPort.setVisibility(View.GONE);
                                viewHolder.attachmentLand.setImageDrawable(drawable);
                            }

                        }
                    });
        } else {
            viewHolder.fileLayout.setVisibility(View.VISIBLE);
            viewHolder.imageLayout.setVisibility(View.GONE);

            viewHolder.fileName.setText(attachmentList.get(position).getImage());
            viewHolder.fileSize.setText(attachmentList.get(position).getSize());
            viewHolder.fileThumbnail.setImageResource(
                    GetThumbnails.attachmentList(attachmentList.get(position).getImage()));
        }
        viewHolder.imageLayout.setOnClickListener(onClick);
        if (!attachmentList.get(position).isPost()) {
            viewHolder.imageDownload.setVisibility(View.VISIBLE);
            viewHolder.fileDownload.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageDownload.setVisibility(View.GONE);
            viewHolder.fileDownload.setVisibility(View.GONE);
        }
        viewHolder.imageDownload.setTag(position);
        viewHolder.fileDownload.setTag(position);

        viewHolder.imageDownload.setOnClickListener(onClick);
        viewHolder.fileDownload.setOnClickListener(onClick);
        return view;
    }

    private final View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DownloadFile downloadFile = new DownloadFile();
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.attachment_list_item_image_layout:
                    openAttachment(position);
                    break;
                case R.id.attachment_list_item_file_download:
                    downloadFile.download(attachmentList.get(position).getId(), attachmentList
                                    .get(position).getPath(), attachmentList.get(position).getImage(),
                            DirectoryList.WALL_DOWNLOAD, activity);
                    break;
                case R.id.attachment_list_item_download:
                    downloadFile.download(attachmentList.get(position).getId(), attachmentList
                                    .get(position).getPath(), attachmentList.get(position).getImage(),
                            DirectoryList.WALL_DOWNLOAD, activity);
                    break;
            }
        }
    };

    // open attachment file in dialog fragment
    @SuppressLint("CommitTransaction")
    private void openAttachment(int position) {
        DialogFragment dialogFrag = new AttachmentViewer();
        Bundle bundle = new Bundle();
        bundle.putString(General.ID, "" + attachmentList.get(position).getId());
        bundle.putString(General.DIRECTORY, DirectoryList.WALL_DOWNLOAD);
        bundle.putString(General.PATH, attachmentList.get(position).getPath());
        bundle.putString(General.IMAGE, attachmentList.get(position).getImage());
        bundle.putString(General.SIZE, attachmentList.get(position).getSize());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), General.URL_IMAGE);
    }

    private class ViewHolder {
        TextView nameText, sizeText, fileName, fileSize;
        ImageView attachmentPort, attachmentLand;
        AppCompatImageView fileThumbnail;
        AppCompatImageButton fileDownload, imageDownload;
        LinearLayout fileLayout, imageLayout;
    }
}
