package com.modules.sos;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.modules.planner.PlannerEventLocationActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ArrayOperations;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 17-07-2017
 * Last Modified on 14-12-2017
 **/

class ReceivedSosListAdapter extends ArrayAdapter<ReceivedSos_> {
    private static final String TAG = ReceivedSosListAdapter.class.getSimpleName();

    private final List<ReceivedSos_> sosList;
    ViewHolder viewHolder;
    private final Activity activity;

    ReceivedSosListAdapter(Activity activity, List<ReceivedSos_> sosList) {
        super(activity, 0, sosList);
        this.sosList = sosList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return sosList.size();
    }

    @Override
    public ReceivedSos_ getItem(int position) {
        if (sosList != null && sosList.size() > 0) {
            return sosList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return sosList.get(position).getId();
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
            view = layoutInflater.inflate(R.layout.received_sos_item_layout, parent, false);
            view.setFocusable(false);

            viewHolder.sosLayout = (LinearLayout) view.findViewById(R.id.linearlayout_received_sos);
            viewHolder.messageText = (TextView) view.findViewById(R.id.received_sos_item_message);
            viewHolder.timeText = (TextView) view.findViewById(R.id.received_sos_item_time);
            viewHolder.teamText = (TextView) view.findViewById(R.id.received_sos_item_posted_team);
            viewHolder.nameText = (TextView) view.findViewById(R.id.received_sos_item_name);

            viewHolder.userPhoto = (ImageView) view.findViewById(R.id.received_sos_item_image);
            viewHolder.imageViewStatus = (AppCompatImageView) view.findViewById(R.id.imageview_clear_search_text);
            viewHolder.locationLayout = (LinearLayout) view.findViewById(R.id.linearlayout_location);
            viewHolder.linearlayoutSos = (LinearLayout) view.findViewById(R.id.linearlayout_sos);
            viewHolder.imageViewLocation = (ImageView) view.findViewById(R.id.imageview_location);
            viewHolder.statusLayout = (LinearLayout) view.findViewById(R.id.linearlayout_status);
            viewHolder.buttonLayout = (LinearLayout) view.findViewById(R.id.received_sos_item_button_layout);

            viewHolder.notAttendingButton = (TextView) view.findViewById(R.id.received_sos_item_not_attending_button);
            viewHolder.attendingButton = (TextView) view.findViewById(R.id.received_sos_item_attending_button);
            viewHolder.completedButton = (TextView) view.findViewById(R.id.received_sos_item_completed_button);

            viewHolder.linearLayoutContacts = (LinearLayout) view.findViewById(R.id.linearlayout_contacts);
            viewHolder.imageOne = (ImageView) view.findViewById(R.id.my_sos_item_image_one);
            viewHolder.imageTwo = (ImageView) view.findViewById(R.id.my_sos_item_image_two);
            viewHolder.imageThree = (ImageView) view.findViewById(R.id.my_sos_item_image_three);
            viewHolder.imageFour = (ImageView) view.findViewById(R.id.my_sos_item_image_four);
            viewHolder.imageviewSos = (ImageView) view.findViewById(R.id.imageview_sos);

            viewHolder.userNameOne = (TextView) view.findViewById(R.id.textview_username_one);
            viewHolder.userNameTwo = (TextView) view.findViewById(R.id.textview_username_two);
            viewHolder.userNameThree = (TextView) view.findViewById(R.id.textview_username_three);
            viewHolder.userNameFour = (TextView) view.findViewById(R.id.textview_username_four);

            viewHolder.lineOne = view.findViewById(R.id.my_sos_item_line_one);
            viewHolder.lineTwo = view.findViewById(R.id.my_sos_item_line_two);
            viewHolder.lineThree = view.findViewById(R.id.my_sos_item_line_three);

            view.setTag(viewHolder);
        } /*else {
            viewHolder = (ViewHolder) view.getTag();
        }*/
        applyReadStatus(viewHolder, sosList.get(position));
        viewHolder.sosLayout.setTag(position);
        viewHolder.locationLayout.setTag(position);
        viewHolder.imageViewLocation.setTag(position);
        viewHolder.notAttendingButton.setTag(position);
        viewHolder.attendingButton.setTag(position);
        viewHolder.completedButton.setTag(position);
        viewHolder.linearLayoutContacts.setTag(position);

        //viewHolder.sosLayout.setOnClickListener(onClickListener);
        viewHolder.locationLayout.setOnClickListener(onClickListener);
        viewHolder.imageViewLocation.setOnClickListener(onClickListener);
        viewHolder.notAttendingButton.setOnClickListener(onClickListener);
        viewHolder.attendingButton.setOnClickListener(onClickListener);
        viewHolder.completedButton.setOnClickListener(onClickListener);
        viewHolder.linearLayoutContacts.setOnClickListener(onClickListener);

        //viewHolder.sosLayout.setBackgroundResource(R.drawable.layout_bottom_shadow);
        if (sosList.get(position).getStatus() == 1) {
            viewHolder.messageText.setText(sosList.get(position).getMessage());
            viewHolder.nameText.setText(ChangeCase.toTitleCase(sosList.get(position).getFullName()));
            viewHolder.teamText.setText(sosList.get(position).getGroupName());
            String time = getDate(sosList.get(position).getTime());
            viewHolder.timeText.setText(time);
            viewHolder.linearLayoutContacts.setVisibility(View.VISIBLE);

            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(activity.getApplicationContext())
                    .load(sosList.get(position).getImage())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(sosList.get(position).getImage()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(activity.getApplicationContext())))
                    .into(viewHolder.userPhoto);

            if (sosList.get(position).getLocation().length() > 0) {
                viewHolder.locationLayout.setVisibility(View.VISIBLE);
            }

            String trackWords = sosList.get(position).getTrack_words();
            try {
                if (trackWords.length() > 0) {
                    List<String> trackWordsList = Arrays.asList(trackWords.split(","));
                    //Get the text from text view and create a spannable string
                    SpannableString spannableString = new SpannableString(viewHolder.messageText.getText());
                    for (int i = 0; i < trackWordsList.size(); i++) {
                        BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
                        for (BackgroundColorSpan span : backgroundSpans) {
                            spannableString.removeSpan(span);
                        }
                        //Search for all occurrences of the keyword in the string
                        int indexOfKeyword = spannableString.toString().indexOf(trackWordsList.get(i));
                        while (indexOfKeyword >= 0) {
                            //Create a background color span on the keyword
                            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), indexOfKeyword, indexOfKeyword + trackWordsList.get(i).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            //Get the next index of the keyword
                            indexOfKeyword = spannableString.toString().indexOf(trackWordsList.get(0), indexOfKeyword + trackWordsList.get(i).length());
                        }
                    }

                    //Set the final text on TextView
                    viewHolder.messageText.setText(spannableString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sosList.get(position).getType() == 2) {
                viewHolder.linearlayoutSos.setVisibility(View.VISIBLE);
                viewHolder.imageviewSos.setImageResource(R.drawable.sos_web);
            } else {
                viewHolder.linearlayoutSos.setVisibility(View.VISIBLE);
                viewHolder.imageviewSos.setImageResource(R.drawable.sos_mobile);
            }


            if (sosList.get(position).getIsActive() == 1) {
                applyStatus(viewHolder, position);
            } else {
                applyStatus(viewHolder, position);
                viewHolder.buttonLayout.setVisibility(View.GONE);
            }

            if (sosList.get(position).getCc().getStatus() > 0) {
                if (sosList.get(position).getE3().getStatus() != 0 && sosList.get(position).getE2().getStatus() != 0 && sosList.get(position).getE1().getStatus() != 0) {
                    ate4(position, viewHolder);
                    return view;
                }

                if (sosList.get(position).getE3().getStatus() == 0 && sosList.get(position).getE2().getStatus() != 0 && sosList.get(position).getE1().getStatus() != 0) {
                    ate3CC(position, viewHolder);
                    return view;
                }

                if (sosList.get(position).getE3().getStatus() == 0 && sosList.get(position).getE2().getStatus() == 0 && sosList.get(position).getE1().getStatus() != 0) {
                    ate2CC(position, viewHolder);
                    return view;
                }

                if (sosList.get(position).getE3().getStatus() == 0 && sosList.get(position).getE2().getStatus() == 0 && sosList.get(position).getE1().getStatus() == 0) {
                    ate1CC(position, viewHolder);
                    return view;
                }
            }

            if (sosList.get(position).getE3().getStatus() != 0 && sosList.get(position).getCc().getStatus() == 0) {
                ate3(position, sosList.get(position).getE3(), viewHolder);
                return view;
            }

            if (sosList.get(position).getE2().getStatus() != 0 && sosList.get(position).getE3().getStatus() == 0) {
                ate2(position, sosList.get(position).getE2(), viewHolder);
                return view;
            }

            if (sosList.get(position).getE1().getStatus() != 0 && sosList.get(position).getE2().getStatus() == 0) {
                ate1(position, sosList.get(position).getE1(), viewHolder);
                return view;
            }
        }

        viewHolder.imageOne.setBackgroundResource(GetDrawable.circle(sosList.get(position).getE1().getStatus()));
        viewHolder.imageTwo.setBackgroundResource(GetDrawable.circle(sosList.get(position).getE2().getStatus()));
        viewHolder.imageThree.setBackgroundResource(GetDrawable.circle(sosList.get(position).getE3().getStatus()));
        viewHolder.imageFour.setBackgroundResource(GetDrawable.circle(sosList.get(position).getCc().getStatus()));

        viewHolder.lineOne.setBackgroundResource(GetDrawable.line(sosList.get(position).getE2().getStatus()));
        viewHolder.lineTwo.setBackgroundResource(GetDrawable.line(sosList.get(position).getE3().getStatus()));
        viewHolder.lineThree.setBackgroundResource(GetDrawable.line(sosList.get(position).getCc().getStatus()));

        return view;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MMM dd, yyyy | hh:mm a", cal).toString();
        return date;
    }

    private void applyReadStatus(ViewHolder holder, ReceivedSos_ receivedSos_) {
        if (receivedSos_.getIs_read() == 1) {
            holder.nameText.setTextColor(ContextCompat.getColor(activity, R.color.white));
            holder.messageText.setTextColor(ContextCompat.getColor(activity, R.color.text_color_read));
            holder.userNameOne.setTextColor(ContextCompat.getColor(activity, R.color.text_color_read));
            holder.userNameTwo.setTextColor(ContextCompat.getColor(activity, R.color.text_color_read));
            holder.userNameThree.setTextColor(ContextCompat.getColor(activity, R.color.text_color_read));
            holder.userNameFour.setTextColor(ContextCompat.getColor(activity, R.color.text_color_read));
        } else {
            holder.nameText.setTextColor(ContextCompat.getColor(activity, R.color.white));
            holder.messageText.setTextColor(ContextCompat.getColor(activity, R.color.text_color_primary));
            holder.userNameOne.setTextColor(ContextCompat.getColor(activity, R.color.text_color_primary));
            holder.userNameTwo.setTextColor(ContextCompat.getColor(activity, R.color.text_color_primary));
            holder.userNameThree.setTextColor(ContextCompat.getColor(activity, R.color.text_color_primary));
            holder.userNameFour.setTextColor(ContextCompat.getColor(activity, R.color.text_color_primary));
        }
    }

    private void applyStatus(ViewHolder holder, int position) {
        /*
         * CurrentStatus >
         * 1:delivered => attending and forward enabled for emergency contacts and for cc only attending
         * 2:attending => show not attending for emergency contacts other than cc and show attended button
         * 3- Attended
         * 4- Forward by user
         * 5- System Forward
         * 6- Not attending
         * */
        ReceivedSos_ receivedSos_ = sosList.get(position);
        if (receivedSos_.getUser_status() < 3) {
            holder.buttonLayout.setVisibility(View.VISIBLE);
            if (CheckRole.isCoordinator(receivedSos_.getGroupRoleId())) {
                applyForCC(holder, receivedSos_);
                //return;
            } else {
                applyForEmergencyContact(holder, receivedSos_, position);
            }
            holder.sosLayout.setBackgroundResource(R.drawable.white_rounded_rectangle_gray_border);

            if (Preferences.get(General.SOS_ID) != null && !Preferences.get(General.SOS_ID).equalsIgnoreCase("")) {
                if (Preferences.get(General.SOS_ID).equalsIgnoreCase(String.valueOf(receivedSos_.getId()))) {
                    holder.sosLayout.setBackground(activity.getResources().getDrawable(R.drawable.white_rounded_rectangle_blue_border));
                    Preferences.save(General.SOS_ID, "");
                }
            }
            holder.statusLayout.setVisibility(View.GONE);
        } else {
            holder.buttonLayout.setVisibility(View.GONE);
            if (receivedSos_.getCurrentStatus() == 3) {
                holder.buttonLayout.setVisibility(View.GONE);
                holder.completedButton.setVisibility(View.GONE);
                holder.sosLayout.setBackgroundResource(R.drawable.sos_complete_rounded_rectangle_gray_border);
                holder.statusLayout.setVisibility(View.VISIBLE);
                int color = Color.parseColor("#529839"); //green
                holder.imageViewStatus.setColorFilter(color);
                holder.imageViewStatus.setImageResource(R.drawable.vi_check_green);
            } else {
                holder.buttonLayout.setVisibility(View.GONE);
                holder.completedButton.setVisibility(View.GONE);
                holder.sosLayout.setBackgroundResource(R.drawable.sos_forward_rounded_rectangle_gray_border);
                holder.statusLayout.setVisibility(View.VISIBLE);
                int color = Color.parseColor("#ed6014"); //orange
                holder.imageViewStatus.setColorFilter(color);
                holder.imageViewStatus.setImageResource(R.drawable.vi_sos_forward);
            }
        }

        if (sosList.get(position).getPriority() == 3) {
            if (receivedSos_.getCurrentStatus() == 1 || receivedSos_.getCurrentStatus() == 2) {
                holder.statusLayout.setVisibility(View.VISIBLE);
                int color = Color.parseColor("#e63333"); //red
                holder.imageViewStatus.setColorFilter(color);
                holder.imageViewStatus.setImageResource(R.drawable.vi_warning);
            }
        }
    }

    // toggle actions button based on user role "care coordinator"
    private void applyForCC(ViewHolder holder, ReceivedSos_ receivedSos_) {
        holder.notAttendingButton.setVisibility(View.GONE);
        if (receivedSos_.getCurrentStatus() == 1) {
            holder.attendingButton.setVisibility(View.VISIBLE);
            holder.completedButton.setVisibility(View.GONE);
            holder.statusLayout.setVisibility(View.GONE);
        } else if (receivedSos_.getCurrentStatus() == 2) {
            holder.buttonLayout.setVisibility(View.GONE);
            holder.completedButton.setVisibility(View.VISIBLE);
        } else {
            holder.buttonLayout.setVisibility(View.GONE);
            holder.completedButton.setVisibility(View.GONE);
        }
    }

    // toggle actions button based on user position "emergency contact 1/2/3"
    private void applyForEmergencyContact(ViewHolder holder, ReceivedSos_ receivedSos_, int position) {
        if (receivedSos_.getCurrentStatus() == 1) {
            holder.notAttendingButton.setVisibility(View.VISIBLE);
            holder.attendingButton.setVisibility(View.VISIBLE);
            holder.completedButton.setVisibility(View.GONE);
            holder.statusLayout.setVisibility(View.GONE);
        } else if (receivedSos_.getCurrentStatus() == 2) {
            sosList.get(position).setIsNotAttending(true);
            //holder.notAttendingButton.setVisibility(View.VISIBLE);
            holder.notAttendingButton.setVisibility(View.GONE);
            holder.attendingButton.setVisibility(View.GONE);
            holder.completedButton.setVisibility(View.VISIBLE);
        } else {
            holder.buttonLayout.setVisibility(View.GONE);
            holder.completedButton.setVisibility(View.GONE);
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            int status;
            switch (v.getId()) {
                case R.id.received_sos_item_attending_button:
                    status = PerformReadTask.readAlert("" + sosList.get(position).getId(), General.SOS, TAG, activity.getApplicationContext(), activity);
                    showAttendingDialog(position);
                    notifyDataSetChanged();
                    break;
                /*case R.id.linearlayout_received_sos:
                    if(sosList.get(position).getCurrentStatus() == 2) {
                        showAttendedDialog(position);
                        notifyDataSetChanged();
                    }
                    break;*/
                case R.id.received_sos_item_completed_button:
                    status = PerformReadTask.readAlert("" + sosList.get(position).getId(), General.SOS, TAG, activity.getApplicationContext(), activity);
                    showAttendedDialog(position);
                    break;
                case R.id.received_sos_item_not_attending_button:
                    status = PerformReadTask.readAlert("" + sosList.get(position).getId(), General.SOS, TAG, activity.getApplicationContext(), activity);
                    confirmationDialog(position, v);
                    notifyDataSetChanged();
                    break;
                case R.id.linearlayout_contacts:
                    status = PerformReadTask.readAlert("" + sosList.get(position).getId(), General.SOS, TAG, activity.getApplicationContext(), activity);
                    sosDetailsPoopUp(position, v);
                    break;
                case R.id.linearlayout_location:
                    status = PerformReadTask.readAlert("" + sosList.get(position).getId(), General.SOS, TAG, activity.getApplicationContext(), activity);
                    Intent locationIntent = new Intent(activity.getApplicationContext(), PlannerEventLocationActivity.class);
                    locationIntent.putExtra(General.LOCATION, sosList.get(position).getLocation());
                    activity.startActivity(locationIntent);
                    activity.overridePendingTransition(0, 0);
                    notifyDataSetChanged();
                    break;
                case R.id.imageview_location:
                    status = PerformReadTask.readAlert("" + sosList.get(position).getId(), General.SOS, TAG, activity.getApplicationContext(), activity);
                    Intent intent = new Intent(activity.getApplicationContext(), PlannerEventLocationActivity.class);
                    intent.putExtra(General.LOCATION, sosList.get(position).getLocation());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                    notifyDataSetChanged();
                    break;
            }
        }
    };

    // open action confirmation for not attending message
    private void confirmationDialog(final int position, final View view) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        title.setText(activity.getApplicationContext().getResources().getString(R.string.action_confirmation));
        subTitle.setText(GetMessages_.actionMessage("", 7));
        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosNotAttending(position);
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

    // make network call to sos attended
    private void sosAttended(int position, String method, String message) {
        int status = SosOperations_.attended(sosList.get(position).getId(), method, message, activity.getApplicationContext(), activity);
        if (status == 1) {
            sosList.get(position).setUser_status(3);
            sosList.get(position).setCurrentStatus(3);
            notifyDataSetChanged();
        }
    }

    // make network call to sos not attended
    private void sosNotAttending(int position) {
        sosList.get(position).setCurrentStatus(4);
        notifyDataSetChanged();

        Intent forwardIntent = new Intent(activity.getApplicationContext(), ForwardSosActivity.class);
        forwardIntent.putExtra(General.ID, sosList.get(position).getId());
        activity.startActivity(forwardIntent);
        activity.overridePendingTransition(0, 0);
    }

    //open sos attending dialog with available modes of attending
    private void showAttendingDialog(final int position) {
        final int[] selection = {0};
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sos_action_method_dialog);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final ImageView callIcon = (ImageView) dialog.findViewById(R.id.sos_action_dialog_call_circle);
        final ImageView chatIcon = (ImageView) dialog.findViewById(R.id.sos_action_dialog_chat_circle);
        final ImageView meetIcon = (ImageView) dialog.findViewById(R.id.sos_action_dialog_meet_circle);
        final ImageView otherIcon = (ImageView) dialog.findViewById(R.id.sos_action_dialog_other_circle);
        final EditText inputBox = (EditText) dialog.findViewById(R.id.sos_action_dialog_other_box);
        final TextView messageText = (TextView) dialog.findViewById(R.id.sos_action_dialog_method);
        messageText.setVisibility(View.GONE);

        AppCompatImageButton close = (AppCompatImageButton) dialog.findViewById(R.id.sos_action_dialog_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView okButton = (TextView) dialog.findViewById(R.id.sos_action_dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputBox.getText().toString().trim();
                if (selection[0] == 0) {
                    ShowToast.toast("Please Select Method", activity.getApplicationContext());
                } else if (selection[0] == 4 && message.length() <= 0) {
                    ShowToast.toast("Please Enter Valid Method", activity.getApplicationContext());
                } else {
                    if (selection[0] == 1) {
                        sosAttending(position, selection[0], GetMessages_.getAttendingMethods(selection[0], message));
                    } else if (selection[0] == 2) {
                        int selectionIntValue = selection[0];
                        Intent meetIntent = new Intent(activity.getApplicationContext(), SosReceivedMeetActivity.class);
                        meetIntent.putExtra(General.ID, sosList.get(position).getId());
                        meetIntent.putExtra(General.SELECTION, selectionIntValue);
                        meetIntent.putExtra(General.GROUP_ID, sosList.get(position).getGroupId());
                        meetIntent.putExtra(General.GROUP_NAME, sosList.get(position).getGroupName());
                        meetIntent.putExtra(General.ROLE_ID, sosList.get(position).getGroupRoleId());
                        activity.startActivity(meetIntent);
                        activity.overridePendingTransition(0, 0);
                    } else if (selection[0] == 4) {
                        dialog.dismiss();
                    } else if (selection[0] == 3) {
                        sosAttending(position, selection[0], GetMessages_.getAttendingMethods(selection[0], message));
                    } else {
                        sosAttending(position, selection[0], GetMessages_.getAttendingMethods(selection[0], message));
                    }
                    dialog.dismiss();
                }
            }
        });

        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Chat
                messageText.setVisibility(View.VISIBLE);
                selection[0] = 1;
                messageText.setText(GetMessages_.actionMessage(ChangeCase.toTitleCase(sosList.get(position).getFullName()), selection[0]));
                inputBox.setVisibility(View.GONE);
                callIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_call));
                chatIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                meetIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                otherIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
            }
        });

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Phone
                messageText.setVisibility(View.VISIBLE);
                selection[0] = 3;
                messageText.setText(GetMessages_.actionMessage(ChangeCase.toTitleCase(sosList.get(position).getFullName()), selection[0]));
                inputBox.setVisibility(View.GONE);
                callIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                chatIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_chat));
                meetIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                otherIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
            }
        });

        meetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Meet
                messageText.setVisibility(View.VISIBLE);
                selection[0] = 2;
                messageText.setText(GetMessages_.actionMessage(ChangeCase.toTitleCase(sosList.get(position).getFullName()), selection[0]));
                inputBox.setVisibility(View.GONE);
                callIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                chatIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                meetIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_meet));
                otherIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
            }
        });

        otherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Other
                messageText.setVisibility(View.VISIBLE);
                selection[0] = 4;
                messageText.setText(GetMessages_.actionMessage(ChangeCase.toTitleCase(sosList.get(position).getFullName()), selection[0]));
                inputBox.setVisibility(View.VISIBLE);
                inputBox.setTag("");
                callIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                chatIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                meetIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                otherIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_other));
            }
        });

        dialog.show();
    }

    // make network call for attending sos
    private void sosAttending(int position, int method, String method_name) {
        int status = SosOperations_.attending(sosList.get(position).getId(), method, method_name, activity.getApplicationContext(), activity);
        if (status == 1) {
            sosList.get(position).setCurrentStatus(2);
            notifyDataSetChanged();
        }
    }


    private String getMethod(boolean[] actionStatus) {
        ArrayList<Integer> method = new ArrayList<>();
        for (int i = 0; i < actionStatus.length; i++) {
            if (actionStatus[i]) {
                method.add(i + 1);
            }
        }
        if (method.size() > 0) {
            return ArrayOperations.listToString(method);
        }
        return "";
    }

    private void showAttendedDialog(final int position) {

        final boolean[] actionStatus = {false, false, false, false};
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sos_action_method_dialog);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final ImageView callIcon = (ImageView) dialog.findViewById(R.id.sos_action_dialog_call_circle);
        final ImageView chatIcon = (ImageView) dialog.findViewById(R.id.sos_action_dialog_chat_circle);
        final ImageView meetIcon = (ImageView) dialog.findViewById(R.id.sos_action_dialog_meet_circle);
        final ImageView otherIcon = (ImageView) dialog.findViewById(R.id.sos_action_dialog_other_circle);
        final EditText inputBox = (EditText) dialog.findViewById(R.id.sos_action_dialog_other_box);
        inputBox.setVisibility(View.VISIBLE);
        final TextView messageText = (TextView) dialog.findViewById(R.id.sos_action_dialog_method);
        messageText.setVisibility(View.GONE);

        AppCompatImageButton close = (AppCompatImageButton) dialog.findViewById(R.id.sos_action_dialog_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView okButton = (TextView) dialog.findViewById(R.id.sos_action_dialog_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputBox.getText().toString().trim();
                if (message.length() <= 0) {
                    inputBox.setError("Please Enter Message");
                } else {
                    sosAttended(position, getMethod(actionStatus), message);
                    dialog.dismiss();
                }
            }
        });

        //isCall , isMeet , isChat , isOther ;
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sos_action_dialog_call_circle:
                        if (!actionStatus[0]) {
                            actionStatus[0] = true;
                            callIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_call));
                        } else {
                            actionStatus[0] = false;
                            callIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                        }
                        break;
                    case R.id.sos_action_dialog_meet_circle:
                        if (!actionStatus[1]) {
                            actionStatus[1] = true;
                            meetIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_meet));
                        } else {
                            actionStatus[1] = false;
                            meetIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                        }
                        break;
                    case R.id.sos_action_dialog_chat_circle:
                        if (!actionStatus[2]) {
                            actionStatus[2] = true;
                            chatIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_chat));
                        } else {
                            actionStatus[2] = false;
                            chatIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                        }
                        break;
                    case R.id.sos_action_dialog_other_circle:
                        if (!actionStatus[3]) {
                            actionStatus[3] = true;
                            otherIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_other));
                        } else {
                            actionStatus[3] = false;
                            otherIcon.setColorFilter(activity.getApplicationContext().getResources().getColor(R.color.sos_grey));
                        }
                        break;
                }
            }
        };
        callIcon.setOnClickListener(onClick);
        chatIcon.setOnClickListener(onClick);
        meetIcon.setOnClickListener(onClick);
        otherIcon.setOnClickListener(onClick);

        dialog.show();
    }

    private void ate4(int position, ViewHolder holder) {
        holder.imageOne.setBackgroundResource(GetDrawable.circle(1));
        setImage(sosList.get(position).getE1().getImage(), holder.imageOne);
        holder.userNameOne.setText(sosList.get(position).getE1().getUsername());
        holder.imageTwo.setBackgroundResource(GetDrawable.circle(1));
        setImage(sosList.get(position).getE2().getImage(), holder.imageTwo);
        holder.userNameTwo.setText(sosList.get(position).getE2().getUsername());
        holder.imageThree.setBackgroundResource(GetDrawable.circle(1));
        setImage(sosList.get(position).getE3().getImage(), holder.imageThree);
        holder.userNameThree.setText(sosList.get(position).getE3().getUsername());
        holder.imageFour.setBackgroundResource(GetDrawable.circle(sosList.get(position).getCc().getStatus()));
        setImage(sosList.get(position).getCc().getImage(), holder.imageFour);
        holder.userNameFour.setText(sosList.get(position).getCc().getUsername());

        holder.imageOne.setVisibility(View.VISIBLE);
        holder.imageTwo.setVisibility(View.VISIBLE);
        holder.imageThree.setVisibility(View.VISIBLE);
        holder.imageFour.setVisibility(View.VISIBLE);

        holder.userNameOne.setVisibility(View.VISIBLE);
        holder.userNameTwo.setVisibility(View.VISIBLE);
        holder.userNameThree.setVisibility(View.VISIBLE);
        holder.userNameFour.setVisibility(View.VISIBLE);

        holder.lineOne.setBackgroundResource(GetDrawable.line(1));
        holder.lineTwo.setBackgroundResource(GetDrawable.line(1));
        holder.lineThree.setBackgroundResource(GetDrawable.line(1));

        holder.lineOne.setVisibility(View.VISIBLE);
        holder.lineTwo.setVisibility(View.VISIBLE);
        holder.lineThree.setVisibility(View.VISIBLE);

        if (sosList.get(position).getE3().getStatus() != 0) {
            holder.imageThree.setVisibility(View.VISIBLE);
            holder.userNameThree.setVisibility(View.VISIBLE);
            holder.lineThree.setBackgroundResource(GetDrawable.line(1));
            holder.lineThree.setVisibility(View.VISIBLE);
        } else {
            holder.imageThree.setVisibility(View.INVISIBLE);
            holder.userNameThree.setVisibility(View.INVISIBLE);
            holder.lineThree.setVisibility(View.INVISIBLE);
        }
        if (sosList.get(position).getE2().getStatus() != 0) {
            holder.imageTwo.setVisibility(View.VISIBLE);
            holder.userNameTwo.setVisibility(View.VISIBLE);
            holder.lineTwo.setBackgroundResource(GetDrawable.line(1));
            holder.lineTwo.setVisibility(View.VISIBLE);
        } else {
            holder.imageTwo.setVisibility(View.INVISIBLE);
            holder.userNameTwo.setVisibility(View.INVISIBLE);
            holder.lineTwo.setVisibility(View.INVISIBLE);
        }
        if (sosList.get(position).getE1().getStatus() != 0) {
            holder.imageOne.setVisibility(View.VISIBLE);
            holder.userNameOne.setVisibility(View.VISIBLE);
            holder.lineOne.setBackgroundResource(GetDrawable.line(1));
            holder.lineOne.setVisibility(View.VISIBLE);
        } else {
            holder.imageOne.setVisibility(View.INVISIBLE);
            holder.userNameOne.setVisibility(View.INVISIBLE);
            holder.lineOne.setVisibility(View.INVISIBLE);
        }
    }

    private void ate3CC(int position, ViewHolder holder) {
        holder.imageOne.setBackgroundResource(GetDrawable.circle(1));
        setImage(sosList.get(position).getE1().getImage(), holder.imageOne);
        holder.userNameOne.setText(sosList.get(position).getE1().getUsername());
        holder.imageTwo.setBackgroundResource(GetDrawable.circle(1));
        setImage(sosList.get(position).getE2().getImage(), holder.imageTwo);
        holder.userNameTwo.setText(sosList.get(position).getE2().getUsername());
        holder.imageThree.setBackgroundResource(GetDrawable.circle(sosList.get(position).getCc().getStatus()));
        setImage(sosList.get(position).getCc().getImage(), holder.imageThree);
        holder.userNameThree.setText(sosList.get(position).getCc().getUsername());

        holder.imageOne.setVisibility(View.VISIBLE);
        holder.imageTwo.setVisibility(View.VISIBLE);
        holder.imageThree.setVisibility(View.VISIBLE);
        holder.imageFour.setVisibility(View.INVISIBLE);

        holder.userNameOne.setVisibility(View.VISIBLE);
        holder.userNameTwo.setVisibility(View.VISIBLE);
        holder.userNameThree.setVisibility(View.VISIBLE);
        holder.userNameFour.setVisibility(View.INVISIBLE);

        holder.lineOne.setBackgroundResource(GetDrawable.line(1));
        holder.lineTwo.setBackgroundResource(GetDrawable.line(1));
        holder.lineThree.setBackgroundResource(GetDrawable.line(1));

        holder.lineOne.setVisibility(View.VISIBLE);
        holder.lineTwo.setVisibility(View.VISIBLE);
        holder.lineThree.setVisibility(View.INVISIBLE);
    }

    private void ate2CC(int position, ViewHolder holder) {
        holder.imageOne.setBackgroundResource(GetDrawable.circle(1));
        setImage(sosList.get(position).getE1().getImage(), holder.imageOne);
        holder.userNameOne.setText(sosList.get(position).getE1().getUsername());
        holder.imageTwo.setBackgroundResource(GetDrawable.circle(sosList.get(position).getCc().getStatus()));
        setImage(sosList.get(position).getCc().getImage(), holder.imageTwo);
        holder.userNameTwo.setText(sosList.get(position).getCc().getUsername());

        holder.imageOne.setVisibility(View.VISIBLE);
        holder.imageTwo.setVisibility(View.VISIBLE);
        holder.imageThree.setVisibility(View.INVISIBLE);
        holder.imageFour.setVisibility(View.INVISIBLE);

        holder.userNameOne.setVisibility(View.VISIBLE);
        holder.userNameTwo.setVisibility(View.VISIBLE);
        holder.userNameThree.setVisibility(View.INVISIBLE);
        holder.userNameFour.setVisibility(View.INVISIBLE);

        holder.lineOne.setBackgroundResource(GetDrawable.line(1));
        holder.lineTwo.setBackgroundResource(GetDrawable.line(1));
        holder.lineThree.setBackgroundResource(GetDrawable.line(1));

        holder.lineOne.setVisibility(View.VISIBLE);
        holder.lineTwo.setVisibility(View.INVISIBLE);
        holder.lineThree.setVisibility(View.INVISIBLE);
    }

    private void ate1CC(int position, ViewHolder holder) {
        holder.imageOne.setBackgroundResource(GetDrawable.circle(sosList.get(position).getCc().getStatus()));
        setImage(sosList.get(position).getCc().getImage(), holder.imageOne);
        holder.userNameOne.setText(sosList.get(position).getCc().getUsername());

        holder.imageOne.setVisibility(View.VISIBLE);
        holder.imageTwo.setVisibility(View.INVISIBLE);
        holder.imageThree.setVisibility(View.INVISIBLE);
        holder.imageFour.setVisibility(View.INVISIBLE);

        holder.userNameOne.setVisibility(View.VISIBLE);
        holder.userNameTwo.setVisibility(View.INVISIBLE);
        holder.userNameThree.setVisibility(View.INVISIBLE);
        holder.userNameFour.setVisibility(View.INVISIBLE);

        holder.lineOne.setBackgroundResource(GetDrawable.line(1));
        holder.lineTwo.setBackgroundResource(GetDrawable.line(1));
        holder.lineThree.setBackgroundResource(GetDrawable.line(1));

        holder.lineOne.setVisibility(View.INVISIBLE);
        holder.lineTwo.setVisibility(View.INVISIBLE);
        holder.lineThree.setVisibility(View.INVISIBLE);
    }

    private void ate3(int position, SosStatus_ sosStatus_, ViewHolder holder) {
        holder.imageOne.setBackgroundResource(GetDrawable.circle(1));
        setImage(sosList.get(position).getE1().getImage(), holder.imageOne);
        holder.userNameOne.setText(sosList.get(position).getE1().getUsername());
        holder.imageTwo.setBackgroundResource(GetDrawable.circle(1));
        setImage(sosList.get(position).getE2().getImage(), holder.imageTwo);
        holder.userNameTwo.setText(sosList.get(position).getE2().getUsername());
        holder.imageThree.setBackgroundResource(GetDrawable.circle(sosStatus_.getStatus()));
        setImage(sosList.get(position).getE3().getImage(), holder.imageThree);
        holder.userNameThree.setText(sosList.get(position).getE3().getUsername());

        holder.imageOne.setVisibility(View.VISIBLE);
        holder.imageTwo.setVisibility(View.VISIBLE);
        holder.imageThree.setVisibility(View.VISIBLE);
        holder.imageFour.setVisibility(View.INVISIBLE);

        holder.userNameOne.setVisibility(View.VISIBLE);
        holder.userNameTwo.setVisibility(View.VISIBLE);
        holder.userNameThree.setVisibility(View.VISIBLE);
        holder.userNameFour.setVisibility(View.INVISIBLE);

        holder.lineOne.setBackgroundResource(GetDrawable.line(1));
        holder.lineTwo.setBackgroundResource(GetDrawable.line(1));
        holder.lineOne.setVisibility(View.VISIBLE);
        holder.lineTwo.setVisibility(View.VISIBLE);
        holder.lineThree.setVisibility(View.INVISIBLE);

        if (sosList.get(position).getE2().getStatus() != 0) {
            holder.imageTwo.setVisibility(View.VISIBLE);
            holder.userNameTwo.setVisibility(View.VISIBLE);
            holder.lineTwo.setBackgroundResource(GetDrawable.line(1));
            holder.lineTwo.setVisibility(View.VISIBLE);
        } else {
            holder.imageTwo.setVisibility(View.INVISIBLE);
            holder.userNameTwo.setVisibility(View.INVISIBLE);
            holder.lineTwo.setVisibility(View.INVISIBLE);
        }
        if (sosList.get(position).getE1().getStatus() != 0) {
            holder.imageOne.setVisibility(View.VISIBLE);
            holder.userNameOne.setVisibility(View.VISIBLE);
            holder.lineOne.setBackgroundResource(GetDrawable.line(1));
            holder.lineOne.setVisibility(View.VISIBLE);
        } else {
            holder.imageOne.setVisibility(View.INVISIBLE);
            holder.userNameOne.setVisibility(View.INVISIBLE);
            holder.lineOne.setVisibility(View.INVISIBLE);
        }
    }

    private void ate2(int position, SosStatus_ sosStatus_, ViewHolder holder) {
        holder.imageOne.setBackgroundResource(GetDrawable.circle(1));
        setImage(sosList.get(position).getE1().getImage(), holder.imageOne);
        holder.userNameOne.setText(sosList.get(position).getE1().getUsername());
        holder.imageTwo.setBackgroundResource(GetDrawable.circle(sosStatus_.getStatus()));
        setImage(sosList.get(position).getE2().getImage(), holder.imageTwo);
        holder.userNameTwo.setText(sosList.get(position).getE2().getUsername());

        holder.imageOne.setVisibility(View.VISIBLE);
        holder.imageTwo.setVisibility(View.VISIBLE);
        holder.imageThree.setVisibility(View.INVISIBLE);
        holder.imageFour.setVisibility(View.INVISIBLE);

        holder.userNameOne.setVisibility(View.VISIBLE);
        holder.userNameTwo.setVisibility(View.VISIBLE);
        holder.userNameThree.setVisibility(View.INVISIBLE);
        holder.userNameFour.setVisibility(View.INVISIBLE);

        holder.lineOne.setBackgroundResource(GetDrawable.line(1));
        holder.lineOne.setVisibility(View.VISIBLE);
        holder.lineTwo.setVisibility(View.INVISIBLE);
        holder.lineThree.setVisibility(View.INVISIBLE);

        if (sosList.get(position).getE1().getStatus() != 0) {
            holder.imageOne.setVisibility(View.VISIBLE);
            holder.userNameOne.setVisibility(View.VISIBLE);
            holder.lineOne.setBackgroundResource(GetDrawable.line(1));
            holder.lineOne.setVisibility(View.VISIBLE);
        } else {
            holder.imageOne.setVisibility(View.INVISIBLE);
            holder.userNameOne.setVisibility(View.INVISIBLE);
            holder.lineOne.setVisibility(View.INVISIBLE);
        }
    }

    private void ate1(int position, SosStatus_ sosStatus_, ViewHolder holder) {
        holder.imageOne.setBackgroundResource(GetDrawable.circle(sosStatus_.getStatus()));
        setImage(sosList.get(position).getE1().getImage(), holder.imageOne);
        holder.userNameOne.setText(sosList.get(position).getE1().getUsername());

        holder.imageOne.setVisibility(View.VISIBLE);
        holder.imageTwo.setVisibility(View.INVISIBLE);
        holder.imageThree.setVisibility(View.INVISIBLE);
        holder.imageFour.setVisibility(View.INVISIBLE);

        holder.userNameOne.setVisibility(View.VISIBLE);
        holder.userNameTwo.setVisibility(View.INVISIBLE);
        holder.userNameThree.setVisibility(View.INVISIBLE);
        holder.userNameFour.setVisibility(View.INVISIBLE);

        holder.lineOne.setVisibility(View.INVISIBLE);
        holder.lineTwo.setVisibility(View.INVISIBLE);
        holder.lineThree.setVisibility(View.INVISIBLE);
    }

    private void setImage(String path, ImageView userImage) {
        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(activity.getApplicationContext())
                .load(path)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(path))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(userImage);
    }

    private void sosDetailsPoopUp(int position, final View view) {
        DialogFragment dialogFrag = new SosDetailsPopUp();
        Bundle bundle = new Bundle();
        bundle.putInt(General.ID, sosList.get(position).getId());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), Actions_.SOS);
    }

    private class ViewHolder {
        TextView messageText, timeText, teamText, nameText;
        ImageView userPhoto;
        AppCompatImageView imageViewStatus;
        LinearLayout sosLayout, locationLayout, statusLayout, buttonLayout, linearLayoutContacts, linearlayoutSos;
        TextView notAttendingButton, attendingButton, completedButton;
        ImageView imageViewLocation, imageOne, imageTwo, imageThree, imageFour, imageviewSos;
        TextView userNameOne, userNameTwo, userNameThree, userNameFour;
        View lineOne, lineTwo, lineThree;
        //View divider;
    }
}
