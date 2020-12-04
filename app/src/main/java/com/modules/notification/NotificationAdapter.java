package com.modules.notification;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;

import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.icons.GetNotifyIcons;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.library.NotificationTypeDetector;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformDeleteTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash Karankal on 6/12/2019.
 */

class NotificationAdapter extends ArrayAdapter<Notification> {
    private final ArrayList<Notification> notificationList;
    private final ArrayList<Notification> list;
    private final Activity activity;
    private int pos = 0;
    private static final String TAG = NotificationAdapter.class.getSimpleName();

    NotificationAdapter(Activity activity, ArrayList<Notification> notificationList) {
        super(activity, 0, notificationList);
        this.notificationList = notificationList;
        this.activity = activity;
        list = new ArrayList<>();
        list.addAll(notificationList);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.notification_item_layout, parent, false);
            holder = new ViewHolder();

            holder.titleText = (TextView) view.findViewById(R.id.notification_item_title);
            holder.timeText = (TextView) view.findViewById(R.id.notification_item_time);
            holder.descriptionText = (TextView) view.findViewById(R.id.notification_item_description);
            holder.typeText = (TextView) view.findViewById(R.id.notification_item_type);
            holder.notificationLayout = view.findViewById(R.id.notication_layout);

            holder.icon = (ImageView) view.findViewById(R.id.notification_item_icon);
            holder.photo = (ImageView) view.findViewById(R.id.notification_item_photo);
            holder.deleteIcon = (ImageView) view.findViewById(R.id.imageview_delete);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        pos = position;
        Notification notification = notificationList.get(position);
        //String time = GetTime.wallTime(notification.getTimestamp());
        final String id = "" + notification.getRef_id();
        final String is_delete = "" + notification.getIs_delete();
        final String alert_id = "" + notification.getId();
        final String type = notification.getType();
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirmation(id, is_delete, alert_id, type, position);
            }
        });

        String time = getDate(notification.getTimestamp());

        holder.timeText.setText(time);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(activity.getApplicationContext())
                .load(notification.getProfile())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(notification.getProfile()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(holder.photo);

        setText(notification, holder);

        setBackgroundData(notification, holder);

        int color = Color.parseColor("#333333"); //The color u want
        holder.icon.setColorFilter(color);
        holder.icon.setImageResource(GetNotifyIcons.getNotifyIcon(notification.getType()));
//        holder.icon.setImageResource(GetNotifyIcons.getNotifyIcon(notification.getRef_type()));

        return view;
    }

    private void setBackgroundData(Notification notification, ViewHolder holder) {
        if (notification.getType().equalsIgnoreCase("mood")) {
            if (notification.getIs_read() == 1) {
                if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.happy)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_happy)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_happy)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_happy))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.happy_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.laugh))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.laugh_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.cry))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.cry_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.worried)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_worried)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_worried)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_worried))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.worried_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.sad)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_sad)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_sad)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_sad))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.sad_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.angry)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_angry)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_angry)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_angry))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.mood_light_rounded_shadow));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.neutral))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.mood_neutral_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.excited)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_excited)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_excited)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_excited))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.mood_excited_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.anxious)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_anxious)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_anxious)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_anxious))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.worried_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.bored)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_bored)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_bored)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_bored))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.bored_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.fearful))
                        || notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_fearful)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_fearful)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_fearful))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.fearful_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.confused))
                        || notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_confused)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_confused)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_confused))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.confused_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.frustrated))
                        || notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_frustrated)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_frustrated)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_frustrated))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.frustrated_light_rounded_border));
                }

                holder.titleText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.text_color_read));

            } else {
                if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.happy)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_happy)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_happy)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_happy))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.happy_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.laugh))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.laugh_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.cry))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.cry_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.worried)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_worried)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_worried)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_worried))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.worried_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.sad)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_sad)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_sad)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_sad))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.sad_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.angry)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_angry)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_angry)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_angry))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.mood_rounded_shadow));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.neutral))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.mood_neutral_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.excited)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_excited)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_excited)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_excited))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.mood_excited_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.anxious)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_anxious)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_anxious)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_anxious))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.worried_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.bored)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_bored)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_bored)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_bored))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.bored_dark_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.fearful)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_fearful)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_fearful)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_fearful))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.fearful_dark_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.confused))
                        || notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_confused)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_confused)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_confused))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.confused_light_rounded_border));
                } else if (notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.frustrated))
                        || notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.really_frustrated)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.very_frustrated)) ||
                        notification.getMood_name().equalsIgnoreCase(activity.getResources().getString(R.string.extremely_frustrated))) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.frustrated_light_rounded_border));
                }


                holder.titleText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.text_color_primary));
            }

        } else {
            if (notification.getHigh_priority() == 1) {

                if (notification.getIs_read() == 1) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.mood_rounded_shadow));
                    holder.titleText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.text_color_read));
                } else {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.mood_rounded_shadow));
                    holder.titleText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.text_color_primary));
                }
            } else {

                if (notification.getIs_read() == 1) {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.edittext_rounded_border));
                    holder.titleText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.text_color_read));
                } else {
                    holder.notificationLayout.setBackground(activity.getApplicationContext().getResources().getDrawable(R.drawable.edittext_rounded_border));
                    holder.titleText.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.text_color_primary));
                }
            }
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

    private void setText(Notification notification, ViewHolder holder) {
        holder.descriptionText.setVisibility(View.VISIBLE);
        String message;
        switch (NotificationTypeDetector.getType(notification.getType())) {
            case 1:
                message = notification.getAdded_by() + " added new message in " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 2:
                message = notification.getAdded_by() + " posted announcement in " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 3:
                message = notification.getAdded_by() + " uploaded file in " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 4:
                message = "New Member " + " join team " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;
            case 5:
                message = "Member " + " left team " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;
            case 6:
                message = notification.getAdded_by() + " posted blog for " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 7:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015")) {
                    message = notification.getAdded_by() + " posted team discussion in " + notification.getGroup_name();
                } else {
                    message = notification.getAdded_by() + " posted team talk in " + notification.getGroup_name();
                }
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 8:
                message = notification.getAdded_by() + " uploaded new video in " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 9:
                message = notification.getAdded_by() + " added poll in team " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 10:
                message = notification.getAdded_by() + " invited to join team " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;
            case 11:
                message = notification.getAdded_by() + " sent friend request";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;
            case 12: //tasklist
                if (notification.getGroup_name() == null || notification.getGroup_name().length() == 0) {
                    message = notification.getAdded_by() + " new task added ";
                } else {
                    message = notification.getAdded_by() + " added new task in " + notification.getGroup_name();
                }
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 17://upload_selfcare
                message = notification.getTitle(); //title with uploaded by name
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 18://comment_selfcare
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;

            case 19://decline_selfcare
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;

            case 20://approve_selfcare
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 21://event or calendar
                if (notification.getGroup_name() == null || notification.getGroup_name().length() == 0) {
                    message = notification.getAdded_by() + " added new event";
                } else {
                    message = notification.getAdded_by() + " added new event in " + notification.getGroup_name();
                }
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 22://selfgoal
                message = notification.getAdded_by() + " added new selfgoal ";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 23://notes
                message = notification.getAdded_by() + " submitted note for approval ";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 24://mood
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.typeText.setVisibility(View.VISIBLE);
                if (notification.getMood_name() != null) {
                    if (notification.getMood_name().length() == 0) {
                        holder.typeText.setText("");
                    } else {
                        holder.typeText.setText("Mood: " + Html.fromHtml(notification.getMood_name()));
                    }
                }

                holder.descriptionText.setVisibility(View.GONE);
                break;
            case 25://accept event or calendar
                if (notification.getGroup_name() == null || notification.getGroup_name().length() == 0) {
                    message = notification.getAdded_by() + " accepted an event ";
                } else {
                    message = notification.getAdded_by() + " accepted an event in " + notification.getGroup_name();
                }
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 26://decline event or calendar
                if (notification.getGroup_name() == null || notification.getGroup_name().length() == 0) {
                    message = notification.getAdded_by() + " rejected an event ";
                } else {
                    message = notification.getAdded_by() + " rejected an event in " + notification.getGroup_name();
                }
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 27://approved notes
                message = notification.getAdded_by() + " approved a note ";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 28://rejected notes
                message = notification.getAdded_by() + " rejected a note ";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 29://updated notes
                message = notification.getAdded_by() + " updated note for approval ";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 34://Assessment
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;
            case 30://team invitation
                message = notification.getAdded_by() + " invited to join team " + notification.getGroup_name();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;
            case 37://team_request_decline
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.VISIBLE);
                if (notification.getGroup_name().length() == 0) {
                    holder.typeText.setText("");
                } else {
                    holder.typeText.setText("Team: " + Html.fromHtml(notification.getGroup_name()));
                }
                break;

            case 38://team_request_accept
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.VISIBLE);
                if (notification.getGroup_name().length() == 0) {
                    holder.typeText.setText("");
                } else {
                    holder.typeText.setText("Team: " + Html.fromHtml(notification.getGroup_name()));
                }
                break;

            case 39://friend_request_decline
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.VISIBLE);
                if (notification.getGroup_name().length() == 0) {
                    holder.typeText.setText("");
                } else {
                    holder.typeText.setText("Friend: " + Html.fromHtml(notification.getGroup_name()));
                }
                break;

            case 40://friend_request_accept
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.VISIBLE);
                if (notification.getGroup_name().length() == 0) {
                    holder.typeText.setText("");
                } else {
                    holder.typeText.setText("Friend: " + Html.fromHtml(notification.getGroup_name()));
                }
                break;

            case 41://share_selfcare
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 42://add_journal
//                message = notification.getAdded_by() + " added new journal ";
                message = "Note has been added by " + notification.getAdded_by();
                holder.titleText.setText(Html.fromHtml(message));
                if (notification.getIs_delete() == 0) {
                    holder.descriptionText.setVisibility(View.VISIBLE);
                    // holder.descriptionText.setText("Title: "+ notification.getDescription());
                    holder.descriptionText.setText(notification.getDescription());
                } else {
                    holder.descriptionText.setVisibility(View.GONE);
                }
                holder.typeText.setVisibility(View.GONE);
                break;
            case 43://update_journal
//                message = notification.getAdded_by() + " update journal ";
                message = "Note has been update by " + notification.getAdded_by();
                holder.titleText.setText(Html.fromHtml(message));
                if (notification.getIs_delete() == 0) {
                    holder.descriptionText.setVisibility(View.VISIBLE);
                    holder.descriptionText.setText("Title: " + notification.getDescription());
                } else {
                    holder.descriptionText.setVisibility(View.GONE);
                }

                holder.typeText.setVisibility(View.GONE);
                break;
            case 44://delete_journal
//                message = notification.getAdded_by() + " delete journal ";
                message = "Note has been deleted by " + notification.getAdded_by();
                holder.titleText.setText(Html.fromHtml(message));
                if (notification.getIs_delete() == 0) {
                    holder.descriptionText.setVisibility(View.VISIBLE);
                    holder.descriptionText.setText("Title: " + notification.getDescription());
                } else {
                    holder.descriptionText.setVisibility(View.GONE);
                }
                holder.typeText.setVisibility(View.GONE);
                break;

            case 45://assign_goal
                message = notification.getAdded_by() + " has been added/Updated the goal(s)";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText("Goal title: " + notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;

            case 46://bhs
                message = notification.getAdded_by() + " has been responded behavioral tracking survey.";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 47://assign_student
                holder.titleText.setText(Html.fromHtml(notification.getTitle()));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 48://student_reassignment
                holder.titleText.setText(Html.fromHtml(notification.getTitle()));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 49://add_leave
                message = notification.getAdded_by() + " has been added leave.";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 50://unset_goal
                message = notification.getTitle() + " by " + notification.getAdded_by();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;

            case 51://progress_note
                message = notification.getAdded_by() + " has been added progress note.";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 52://edit_progress_note
                message = notification.getAdded_by() + " has been updated  progress note.";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 53://delete_progress_note
                message = notification.getAdded_by() + " has been deleted progress note.";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 54://delete_goal
                message = "Goal has been deleted by " + notification.getAdded_by();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getTitle());
                holder.typeText.setVisibility(View.GONE);
                break;

            case 55://edit_leave
                message = notification.getTitle();
                //message = notification.getAdded_by() + " has been edit leave.";
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 56://delete_leave
                //message = notification.getAdded_by() + " has been delete leave.";
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 57://platform_youth_message
                message = notification.getTitle1();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText(notification.getDescription1());
                holder.typeText.setVisibility(View.GONE);
                break;

            case 58://add_appointment
                message = notification.getTitle1();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 59://updated_appointment
                message = notification.getTitle1();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 60://cancel_appointment
                message = notification.getTitle1();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 61://rescheduled_appointment
                message = notification.getTitle1();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 62://delete_appointment
                message = notification.getTitle1();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 63://peer_supervisor_notification
                message = notification.getTitle1();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText("Team: " + notification.getDescription1());
                holder.typeText.setVisibility(View.GONE);
                break;

            case 64://Sows_notification
                message = notification.getTitle1();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText("Sows: " + notification.getDescription1());
                holder.typeText.setVisibility(View.GONE);
                break;

            case 65://immunity_survey
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;
            case 66://submit_one_time_survey
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 67://Selfgoal_due
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
                holder.descriptionText.setText("Goal title: " + notification.getDescription());
                holder.typeText.setVisibility(View.GONE);
                break;

            case 68://dailysurvey_due
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
//                holder.descriptionText.setText("Goal title: " + notification.getDescription());
                holder.descriptionText.setVisibility(View.INVISIBLE);
                holder.typeText.setVisibility(View.GONE);
                break;

// case added by kishor k 06/05/2020
            case 69:// caseload page
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
//                holder.descriptionText.setText("Goal title: " + notification.getDescription());
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            case 70:// new Cometchat-pro group invitation  // 13-11-2020
                message = notification.getTitle();
                holder.titleText.setText(Html.fromHtml(message));
//                holder.descriptionText.setText("Goal title: " + notification.getDescription());
                holder.descriptionText.setVisibility(View.GONE);
                holder.typeText.setVisibility(View.GONE);
                break;

            default:

                break;
        }
    }

    public void filterNotification(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        notificationList.clear();
        if (charText.length() == 0) {
            notificationList.addAll(list);
        } else {
            for (Notification item : list) {
                String name = item.getAdded_by();
                String team_name = item.getGroup_name();
                String type = item.getType();

                if (type.toLowerCase().contains(charText.toLowerCase())) {
                    notificationList.add(item);
                } else if (team_name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    notificationList.add(item);
                } else if (name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    notificationList.add(item);
                }
            }
        }
        if (notificationList.size() <= 0) {
            ShowToast.toast("No Data", activity.getApplicationContext());
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView titleText, timeText, typeText, descriptionText;
        ImageView icon, photo, deleteIcon;
        LinearLayout notificationLayout;
    }

    // delete record from local database
    private void deleteRecord(View view, String id, String is_delete, String alert_id, String type, int position) {
        is_delete = "0"; //Always send 0 from notification. so it will only delete notification.
        int response = PerformDeleteTask.deleteAlert(id, type, is_delete, alert_id, TAG, activity, activity);
        showResponses(response, view, position);
    }

    //open delete confirmation dialog box
    private void deleteConfirmation(final String id, final String is_delete, final String alert_id, final String type, final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        subTitle.setText(activity.getResources().getString(R.string.notification_delete_confirmation));
        title.setText(activity.getResources().getString(R.string.action_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(v, id, is_delete, alert_id, type, position);
                dialog.dismiss();
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById(R.id.delete_confirmation_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showResponses(int status, View view, int position) {
        String message = "";
        if (status == 1) {
            message = activity.getResources().getString(R.string.successful);
            notificationList.remove(position);
            notifyDataSetChanged();
        } else if (status == 3) {
            message = activity.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, activity);
//        if (status == 1) {
//           onBackPressed();
////        }
    }
}