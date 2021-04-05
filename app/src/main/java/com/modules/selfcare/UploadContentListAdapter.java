package com.modules.selfcare;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.CareUploaded_;
import com.storage.preferences.Preferences;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 21-07-2017
 *         Last Modified on 14-12-2017
 */

class UploadContentListAdapter extends ArrayAdapter<CareUploaded_> {

    private final List<CareUploaded_> careUploadedList;
    private final String action;
    ViewHolder viewHolder;
    private final Activity activity;

    UploadContentListAdapter(Activity activity, List<CareUploaded_> careUploadedList, String action) {
        super(activity, 0, careUploadedList);
        this.careUploadedList = careUploadedList;
        this.activity = activity;
        this.action = action;
    }

    @Override
    public int getCount() {
        return careUploadedList.size();
    }

    @Override
    public CareUploaded_ getItem(int position) {
        if (careUploadedList != null && careUploadedList.size() > 0) {
            return careUploadedList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return careUploadedList.get(position).getId();
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.self_care_content_list_item, parent, false);

            viewHolder.relativeLayoutCaseloadDetails = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_details);
            viewHolder.title = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_title);
            viewHolder.description = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_description);
            viewHolder.category = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_category);
            viewHolder.likeCount = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_like_count);
            viewHolder.commentCount = (TextView) view.findViewById(R.id.tetxtview_selfcarecontentlistitem_comment_count);
            viewHolder.shareCount = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_share_count);

            viewHolder.narrationTag = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_narration_tag);
            viewHolder.narrationName = (TextView) view.findViewById(R.id.textview_selfcarecontentlistitem_narration_name);

            viewHolder.thumbnail = (ImageView) view.findViewById(R.id.imageview_selfcarecontentlistitem_icon);

            viewHolder.icon = (AppCompatImageView) view.findViewById(R.id.imageview_selfcarecontentlistitem_type_icon);
            viewHolder.likeIcon = (AppCompatImageView) view.findViewById(R.id.btn_like);
            viewHolder.commentIcon = (AppCompatImageView) view.findViewById(R.id.imageview_selfcarecontentlistitem_comment);

            viewHolder.imageLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_selfcarecontentlistitem_icon);
            viewHolder.countLayout = (LinearLayout) view.findViewById(R.id.linearlayout_selfcarecontentlistitem_count);
            viewHolder.narrationLayout = (LinearLayout) view.findViewById(R.id.linearlayout_selfcarecontentlistitem_narration);

            view.setTag(viewHolder);
        } /*else {
            viewHolder = (ViewHolder) view.getTag();
        }*/

        if (careUploadedList.get(position).getStatus() == 1) {
            viewHolder.countLayout.setVisibility(View.GONE);
            if(Preferences.get(General.SELFCARE_ID) != null && !Preferences.get(General.SELFCARE_ID).equalsIgnoreCase("")) {
                if (Preferences.get(General.SELFCARE_ID).equalsIgnoreCase(String.valueOf(careUploadedList.get(position).getId()))) {
                    viewHolder.relativeLayoutCaseloadDetails.setBackground(activity.getResources().getDrawable(R.drawable.white_rounded_rectangle_blue_border));
                    Preferences.save(General.SELFCARE_ID, "");
                }
            }

            viewHolder.title.setText(careUploadedList.get(position).getTitle());
            viewHolder.description.setText(careUploadedList.get(position).getDescription());
            viewHolder.category.setText(careUploadedList.get(position).getType());

            String thumbnail_url = careUploadedList.get(position).getThumb_path();
            int content_type = SelfCareContentType_.nameToType(careUploadedList.get(position).getType());
            if(content_type == 8) {
                try {
                    String videoId = getYoutubeVideoIdFromUrl(careUploadedList.get(position).getContent());
                    Log.e("VideoId is->", "" + videoId);
                    thumbnail_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (toggleThumbnail(careUploadedList.get(position).getType(), position, viewHolder)) {
                //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                Glide.with(activity.getApplicationContext())
                        .load(thumbnail_url)
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                .placeholder(GetThumbnails.userIcon(thumbnail_url))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(getThumbnailSize()[0], getThumbnailSize()[1])
                                .centerCrop())
                        .into(viewHolder.thumbnail);
                applyIcon(viewHolder, position);
            }

            setNarration(viewHolder, position);

            viewHolder.relativeLayoutCaseloadDetails.setTag(position);
        }
        return view;
    }

    /*
    public String extractYoutubeId(String url) throws MalformedURLException {
        String query = new URL(url).getQuery();
        String[] param = query.split("&");
        String id = null;
        for (String row : param) {
            String[] param1 = row.split("=");
            if (param1[0].equals("v")) {
                id = param1[1];
            }
        }
        return id;
    }*/

    public static String getYoutubeVideoIdFromUrl(String inUrl) {
        if (inUrl.toLowerCase().contains("youtu.be")) {
            return inUrl.substring(inUrl.lastIndexOf("/") + 1);
        }
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(inUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    // set narration of Approved by with names if applicable
    private void setNarration(ViewHolder viewHolder, int position) {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")){
            viewHolder.narrationLayout.setVisibility(View.GONE);
        }else if (action.equalsIgnoreCase(Actions_.PENDING)) {
            viewHolder.narrationLayout.setVisibility(View.GONE);
        } else {
            viewHolder.narrationLayout.setVisibility(View.VISIBLE);
            viewHolder.narrationName.setText(careUploadedList.get(position).getShared_to());
            viewHolder.narrationTag.setText(getTag());
            viewHolder.narrationTag.setTextColor(getTagColor());
        }
    }

    // set different colors based on action to tag of shared by
    private int getTagColor() {
        if (action.equalsIgnoreCase(Actions_.APPROVAL) || action.equalsIgnoreCase(Actions_.RE_APPROVAL)) {
            return activity.getApplicationContext().getResources().getColor(R.color.sos_attending);
        }

        if (action.equalsIgnoreCase(Actions_.REJECTED) || action.equalsIgnoreCase(Actions_.RE_REJECTED)) {
            return activity.getApplicationContext().getResources().getColor(R.color.sos_delivered);
        }
        if (action.equalsIgnoreCase(Actions_.SHARED)) {
            return activity.getApplicationContext().getResources().getColor(R.color.sos_not_attending);
        }
        return activity.getApplicationContext().getResources().getColor(R.color.sos_grey);
    }

    private String getTag() {
        if (action.equalsIgnoreCase(Actions_.APPROVAL) || action.equalsIgnoreCase(Actions_.RE_APPROVAL)) {
            return activity.getApplicationContext().getResources().getString(R.string.shared_to);
        }
        if (action.equalsIgnoreCase(Actions_.REJECTED) || action.equalsIgnoreCase(Actions_.RE_REJECTED)) {
            return activity.getApplicationContext().getResources().getString(R.string.shared_to);
        }
        if (action.equalsIgnoreCase(Actions_.SHARED)) {
            return activity.getApplicationContext().getResources().getString(R.string.shared_to);
        }
        return "";
    }

    private int[] getThumbnailSize() {
        int width = (int) activity.getApplicationContext().getResources().getDimension(R.dimen.thumbnail_size);
        int height = (int) activity.getApplicationContext().getResources().getDimension(R.dimen.thumbnail_size);
        int[] dimension = new int[2];
        dimension[0] = width;
        dimension[1] = height;
        return dimension;
    }

    private void applyIcon(ViewHolder holder, int position) {
        switch (SelfCareContentType_.nameToType(careUploadedList.get(position).getType())) {
            case 1:
                holder.icon.setImageResource(R.drawable.vi_care_image_white);
                break;
            case 2:
                holder.icon.setImageResource(R.drawable.vi_care_video_white);
                break;
            case 3:
                holder.icon.setImageResource(R.drawable.vi_care_image_white);
                break;
            case 4:
                holder.icon.setImageResource(R.drawable.vi_care_image_white);
                break;
            case 5:
                holder.icon.setImageResource(R.drawable.vi_care_audio_white);
                break;
            case 6:
                holder.icon.setImageResource(R.drawable.vi_care_video_white);
                break;
            case 8:
                holder.icon.setImageResource(R.drawable.vi_care_video_white);
                break;
            default:
                holder.icon.setVisibility(View.GONE);
                break;
        }
    }

    private boolean toggleThumbnail(String type, int position, ViewHolder holder) {
        boolean isVisible;
        int content_type = SelfCareContentType_.nameToType(type);
        if (content_type == 1 || content_type == 2 || content_type == 3 || content_type == 5 || content_type == 6 || content_type == 8) {
            isVisible = true;
            holder.imageLayout.setVisibility(View.VISIBLE);
        } else if(content_type == 4) {
            if(careUploadedList.get(position).getThumb_path().length() > 0) {
                isVisible = true;
                holder.imageLayout.setVisibility(View.VISIBLE);
            } else {
                isVisible = false;
                holder.imageLayout.setVisibility(View.GONE);
            }
        } else {
            isVisible = false;
            holder.imageLayout.setVisibility(View.GONE);
        }
        return isVisible;
    }

    private class ViewHolder {
        TextView title, description, category, likeCount, commentCount, shareCount, narrationTag, narrationName;
        ImageView thumbnail;
        AppCompatImageView icon, likeIcon, commentIcon;
        RelativeLayout imageLayout, relativeLayoutCaseloadDetails;
        LinearLayout countLayout, narrationLayout;
    }
}
