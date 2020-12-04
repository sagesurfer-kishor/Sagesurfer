package com.modules.admin;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.CircleTransform;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 23-08-2017
 * Last Modified on 13-12-2017
 */

class AdminInviteListAdapter extends ArrayAdapter<Invitation_> {

    private List<Invitation_> invitationList;
    private Activity activity;

    AdminInviteListAdapter(Activity activity, List<Invitation_> invitationList) {
        super(activity, 0, invitationList);
        this.invitationList = invitationList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return invitationList.size();
    }

    @Override
    public Invitation_ getItem(int position) {
        if (invitationList != null && invitationList.size() > 0) {
            return invitationList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return invitationList.get(position).getId();
    }

    public int getItemStatus(int position) {
        return invitationList.get(position).getStatus();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.admin_invite_list_item, parent, false);

            viewHolder.titleText = (TextView) view.findViewById(R.id.admin_invite_list_item_title);
            viewHolder.emailText = (TextView) view.findViewById(R.id.admin_invite_list_item_sub_title);

            viewHolder.profile = (ImageView) view.findViewById(R.id.admin_invite_list_item_image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (invitationList.get(position).getStatus() == 1) {
            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            // set Image using glide with creating thumbnail
            Glide.with(activity.getApplicationContext()).load(invitationList.get(position).getReceiver_profile())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(invitationList.get(position).getReceiver_profile()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.profile);
            viewHolder.emailText.setText(invitationList.get(position).getReceiver_email());
            setTitle(position, viewHolder);
        }
        return view;
    }

    // Set title/content of list roq in different color using html text
    private void setTitle(int position, ViewHolder holder) {
        String openMainFont = "<font color=\"#333333\">";
        String closeFont = "</font>";
        String openBold = "<b>";
        String closeBold = "</b>";
        String title = openMainFont + openBold + ChangeCase.toTitleCase(invitationList.get(position).getReceiver_user()) + closeBold +
                " invited in " + invitationList.get(position).getTeam() + closeFont;
        holder.titleText.setText(Html.fromHtml(title));
    }

    // View holder class to keep all view widgets
    private class ViewHolder {
        TextView titleText, emailText;
        ImageView profile;
    }
}
