package com.modules.sos;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Kailash Karankal
 * Created on 17-07-2019
 */

class MySosListAdapter extends ArrayAdapter<MySos_> {
    private final List<MySos_> sosList;
    private final Activity activity;

    MySosListAdapter(Activity activity, List<MySos_> sosList) {
        super(activity, 0, sosList);
        this.sosList = sosList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return sosList.size();
    }

    @Override
    public MySos_ getItem(int position) {
        if (sosList != null && sosList.size() > 0) {
            return sosList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return sosList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        View view = convertView;
        if (view == null) {
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.received_sos_item_layout, parent, false);

            viewHolder.sosLayout = (LinearLayout) view.findViewById(R.id.linearlayout_received_sos);
            viewHolder.messageText = (TextView) view.findViewById(R.id.received_sos_item_message);
            viewHolder.timeText = (TextView) view.findViewById(R.id.received_sos_item_time);
            viewHolder.teamText = (TextView) view.findViewById(R.id.received_sos_item_posted_team);
            viewHolder.nameText = (TextView) view.findViewById(R.id.received_sos_item_name);

            viewHolder.statusLayout = (LinearLayout) view.findViewById(R.id.linearlayout_status);
            viewHolder.buttonLayout = (LinearLayout) view.findViewById(R.id.received_sos_item_button_layout);
            viewHolder.linearLayoutContacts = (LinearLayout) view.findViewById(R.id.linearlayout_contacts);

            viewHolder.imageOne = (ImageView) view.findViewById(R.id.my_sos_item_image_one);
            viewHolder.imageTwo = (ImageView) view.findViewById(R.id.my_sos_item_image_two);
            viewHolder.imageThree = (ImageView) view.findViewById(R.id.my_sos_item_image_three);
            viewHolder.imageFour = (ImageView) view.findViewById(R.id.my_sos_item_image_four);

            viewHolder.userNameOne = (TextView) view.findViewById(R.id.textview_username_one);
            viewHolder.userNameTwo = (TextView) view.findViewById(R.id.textview_username_two);
            viewHolder.userNameThree = (TextView) view.findViewById(R.id.textview_username_three);
            viewHolder.userNameFour = (TextView) view.findViewById(R.id.textview_username_four);

            viewHolder.lineOne = view.findViewById(R.id.my_sos_item_line_one);
            viewHolder.lineTwo = view.findViewById(R.id.my_sos_item_line_two);
            viewHolder.lineThree = view.findViewById(R.id.my_sos_item_line_three);

            viewHolder.linearlayoutSos = (LinearLayout) view.findViewById(R.id.linearlayout_sos);
            viewHolder.imageviewSos = (ImageView) view.findViewById(R.id.imageview_sos);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.linearLayoutContacts.setTag(position);
        viewHolder.linearLayoutContacts.setOnClickListener(onClickListener);


        if (sosList.get(position).getType() == 2) {
            viewHolder.linearlayoutSos.setVisibility(View.VISIBLE);
            viewHolder.imageviewSos.setImageResource(R.drawable.sos_web);
        } else {
            viewHolder.linearlayoutSos.setVisibility(View.VISIBLE);
            viewHolder.imageviewSos.setImageResource(R.drawable.sos_mobile);
        }


        if (Preferences.get(General.SOS_ID) != null && !Preferences.get(General.SOS_ID).equalsIgnoreCase("")) {
            if (Preferences.get(General.SOS_ID).equalsIgnoreCase(String.valueOf(sosList.get(position).getId()))) {
                viewHolder.sosLayout.setBackground(activity.getResources().getDrawable(R.drawable.white_rounded_rectangle_blue_border));
                Preferences.save(General.SOS_ID, "");
            }
        }

        viewHolder.statusLayout.setVisibility(View.GONE);
        viewHolder.buttonLayout.setVisibility(View.GONE);
        viewHolder.linearLayoutContacts.setVisibility(View.VISIBLE);
        if (sosList.get(position).getStatus() == 1) {
            viewHolder.messageText.setText(sosList.get(position).getMessage());
            viewHolder.nameText.setText(ChangeCase.toTitleCase(sosList.get(position).getConsumer_name()));
            viewHolder.teamText.setText(sosList.get(position).getGroup_name());
            viewHolder.timeText.setText(getDate(sosList.get(position).getTime()));

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
        Glide.with(activity)
                .load(path)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(path))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(activity)))
                .into(userImage);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.linearlayout_contacts:
                    sosDetailsPoopUp(position, v);
                    break;
            }
        }
    };

    private void sosDetailsPoopUp(int position, final View view) {
        DialogFragment dialogFrag = new SosDetailsPopUp();
        Bundle bundle = new Bundle();
        bundle.putInt(General.ID, sosList.get(position).getId());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), Actions_.SOS);
    }

    private class ViewHolder {
        TextView messageText, timeText, teamText, nameText;
        LinearLayout sosLayout, buttonLayout, statusLayout, linearLayoutContacts, linearlayoutSos;
        ImageView imageOne, imageTwo, imageThree, imageFour, imageviewSos;
        TextView userNameOne, userNameTwo, userNameThree, userNameFour;
        View lineOne, lineTwo, lineThree;
    }
}
