package com.modules.team.gallery.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.team.gallery.activity.SelectedImageListActivity;
import com.modules.teamtalk.model.Comments_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.models.Images_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.views.AttachmentViewer;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 7/19/2019.
 */
public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.MyViewHolder> {
    private static final String TAG = SelectedImageAdapter.class.getSimpleName();
    private final List<Images_> attachmentList;
    private List<Images_> downloadImageList;
    private final Activity activity;
    private final SelectedImageAdapterListener selectedImageAdapterListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, sizeText, heartCount;
        ImageView attachmentPort, galleryTick, galleryTickSelected, heartImg;
        LinearLayout fileLayout;

        MyViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.gallery_card_title);
            sizeText = (TextView) view.findViewById(R.id.gallery_card_name);
            heartCount = (TextView) view.findViewById(R.id.heart_count);
            attachmentPort = (ImageView) view.findViewById(R.id.gallery_card_thumbnail);
            galleryTick = (ImageView) view.findViewById(R.id.gallery_tick);
            galleryTickSelected = (ImageView) view.findViewById(R.id.gallery_tick_selected);
            heartImg = (ImageView) view.findViewById(R.id.heart_img);
            fileLayout = (LinearLayout) view.findViewById(R.id.gallery_list_item_card_main_container);
        }
    }

    public SelectedImageAdapter(Activity activity, List<Images_> attachmentList, SelectedImageAdapterListener imageAdapterListener) {
        this.activity = activity;
        this.attachmentList = attachmentList;
        this.selectedImageAdapterListener = imageAdapterListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_gallery_image_layout, parent, false);

        return new SelectedImageAdapter.MyViewHolder(itemView);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final SelectedImageAdapter.MyViewHolder holder, final int position) {
        final Images_ images_ = attachmentList.get(position);
        holder.nameText.setText(attachmentList.get(position).getTitle());
        holder.sizeText.setText(attachmentList.get(position).getUsername());

        Glide.with(activity.getApplicationContext())
                .load(attachmentList.get(position).getFullPath())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_thumb)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.attachmentPort);


        holder.galleryTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.galleryTick.setVisibility(View.GONE);
                holder.galleryTickSelected.setVisibility(View.VISIBLE);
                images_.setSelectImgs(true);
                notifyDataSetChanged();
                SelectedImageListActivity.showGalleryImages();
            }
        });

        holder.galleryTickSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.galleryTick.setVisibility(View.VISIBLE);
                holder.galleryTickSelected.setVisibility(View.GONE);
                images_.setSelectImgs(false);
                notifyDataSetChanged();
                SelectedImageListActivity.showGalleryImages();
            }
        });

        if (images_.isSelectImgs()) {
            holder.galleryTickSelected.setVisibility(View.VISIBLE);
            holder.galleryTick.setVisibility(View.GONE);
        } else {
            holder.galleryTick.setVisibility(View.VISIBLE);
            holder.galleryTickSelected.setVisibility(View.GONE);
        }

      /*  if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            holder.heartImg.setVisibility(View.GONE);
            holder.heartCount.setVisibility(View.GONE);
        }*/

        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")
                ||  !General.isCurruntUserHasPermissionToOnlyViewCantPerformAnyAction()) {
            holder.heartImg.setVisibility(View.GONE);
            holder.heartCount.setVisibility(View.GONE);
        }

        holder.heartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images_.setSelectImgs(true);
                selectedImageAdapterListener.onItemClicked(position);
            }
        });

        if (images_.getIs_like() > 0) {
            holder.heartImg.setImageResource(R.drawable.heart_red);
            holder.heartCount.setTextColor(activity.getResources().getColor(R.color.self_goal_red));
        } else {
            holder.heartImg.setImageResource(R.drawable.heart_img);
            holder.heartCount.setTextColor(activity.getResources().getColor(R.color.text_color_primary));
        }

        if (images_.getTotal_like() == 0) {
            holder.heartCount.setVisibility(View.GONE);
        } else {
            holder.heartCount.setText("" + images_.getTotal_like());
        }

        holder.fileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // downloadGalleryImageWebService(attachmentList.get(position).getId());
                DialogFragment dialogFrag = new AttachmentViewer();
                Bundle bundle = new Bundle();
                bundle.putString(General.ID, "" + attachmentList.get(position).getId());
                bundle.putString(General.DIRECTORY, DirectoryList.DIR_SHARED_FILES);
                bundle.putString(General.PATH, attachmentList.get(position).getFullPath());
                bundle.putString(General.IMAGE, FileOperations.getFileName(attachmentList.get(position).getFullPath()));
                bundle.putString(General.SIZE, attachmentList.get(position).getDescription());
                dialogFrag.setArguments(bundle);
                dialogFrag.show(activity.getFragmentManager().beginTransaction(), General.URL_IMAGE);
            }
        });
    }

    public void downloadGalleryImageWebService(int imageID) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DOWNLOAD_IMAGE);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.IMAGE_ID, String.valueOf(imageID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.DOWNLOAD_IMAGE_GALLERY;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.DOWNLOAD_IMAGE);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Comments_>>() {
                        }.getType();

                        downloadImageList = gson.fromJson(GetJson_.getArray(response, Actions_.DOWNLOAD_IMAGE).toString(), listType);

                        if (downloadImageList.get(0).getStatus() == 1) {
                            if (downloadImageList.size() > 0) {

                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface SelectedImageAdapterListener {
        void onItemClicked(int position);
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }
}