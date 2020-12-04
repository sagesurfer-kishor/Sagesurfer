package com.modules.admin;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.icons.GetNotifyIcons;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.library.NotificationTypeDetector;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 22-08-2017
 * Last Modified on 13-12-2017
 */

class DeletingListAdapter extends ArrayAdapter<SpamItem_> {

    private List<SpamItem_> spamItemList;
    private Activity activity;

    DeletingListAdapter(Activity activity, List<SpamItem_> spamItemList) {
        super(activity, 0, spamItemList);
        this.spamItemList = spamItemList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return spamItemList.size();
    }

    @Override
    public SpamItem_ getItem(int position) {
        if (spamItemList != null && spamItemList.size() > 0) {
            return spamItemList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return spamItemList.get(position).getId();
    }

    public int getItemStatus(int position) {
        return spamItemList.get(position).getStatus();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.deleting_list_item_layout, parent, false);

            viewHolder.titleText = (TextView) view.findViewById(R.id.deleting_list_item_title);
            viewHolder.dateText = (TextView) view.findViewById(R.id.deleting_list_item_time);

            viewHolder.profile = (ImageView) view.findViewById(R.id.deleting_list_item_image);
            viewHolder.icon = (AppCompatImageView) view.findViewById(R.id.deleting_list_item_icon);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (spamItemList.get(position).getStatus() == 1) {
            viewHolder.dateText.setText(GetTime.wallTime(spamItemList.get(position).getTimestamp()));

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(spamItemList.get(position).getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(spamItemList.get(position).getPhoto()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.profile);

            viewHolder.icon.setImageResource(GetNotifyIcons.getNotifyIcon(spamItemList.get(position).getType()));
            setTitle(spamItemList.get(position), viewHolder);
        }
        return view;
    }

    // Set title for list item based on module/type it is representing
    protected void setTitle(SpamItem_ spamItem_, ViewHolder holder) {
        String openMainFont = "<font color=\"#333333\">";
        String openSubFont = "<font color=\"#333333\">";
        String openPrimaryFont = "<font color=\"#0D79C2\">";
        String closeFont = "</font>";
        String openBold = "<b>";
        String closeBold = "</b>";
        String message = "-NA-";
        switch (NotificationTypeDetector.getType(spamItem_.getType())) {
            case 1:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " added new " + closeFont
                        + openPrimaryFont + "Message" + closeFont
                        + openSubFont + " in " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;
                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 2:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " posted " + closeFont
                        + openPrimaryFont + "Announcement" + closeFont
                        + openSubFont + " in " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;

                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 3:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openPrimaryFont + "Uploaded File" + closeFont
                        + openSubFont + " in " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;

                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 6:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " posted " + closeFont
                        + openPrimaryFont + "Blog" + closeFont
                        + openSubFont + " for " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;

                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 7:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " posted " + closeFont
                        + openPrimaryFont + "Team Talk" + closeFont
                        + openSubFont + " for team" + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;

                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 8:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openPrimaryFont + "Uploaded Video" + closeFont
                        + openSubFont + " for " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;

                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 9:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " added " + closeFont
                        + openPrimaryFont + "Poll" + closeFont
                        + openSubFont + " in team " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;

                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 12:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " added " + closeFont
                        + openPrimaryFont + "Task" + closeFont
                        + openSubFont + " in " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;

                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 13:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " added " + closeFont
                        + openPrimaryFont + "Gallery" + closeFont
                        + openSubFont + " for " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;

                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 14:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " added " + closeFont
                        + openPrimaryFont + "Strength" + closeFont
                        + openSubFont + " for " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;
                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 15:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " added " + closeFont
                        + openPrimaryFont + "Success" + closeFont
                        + openSubFont + " for " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;
                holder.titleText.setText(Html.fromHtml(message));
                break;
            case 16:
                message = openMainFont + openBold + (ChangeCase.toTitleCase(spamItem_.getCreated_by())) + closeBold
                        + closeFont + closeBold
                        + openSubFont + " uploaded " + closeFont
                        + openPrimaryFont + "Lived-Stream Video" + closeFont
                        + openSubFont + " in " + closeFont
                        + openMainFont + spamItem_.getTeam() + closeFont;
                holder.titleText.setText(Html.fromHtml(message));
                break;
            default:
                break;

        }
    }

    private class ViewHolder {
        TextView titleText, dateText;
        ImageView profile;
        AppCompatImageView icon;
    }
}
