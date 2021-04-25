package com.modules.postcard;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.CircleTransform;
import com.sagesurfer.views.FlipAnimator;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Kailash Karankal
 */

public class PostcardListAdapter extends RecyclerView.Adapter<PostcardListAdapter.MyViewHolder> {
    private final Context mContext;
    private final List<Postcard_> messages;
    private final MessageAdapterListener listener;
    private final SparseBooleanArray selectedItems;
    // array used to perform multiple animation at once
    private final SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    PostcardListAdapter(Context mContext, List<Postcard_> messages, MessageAdapterListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.postcard_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (messages.get(position).getStatus() == 1) {
            holder.warningLayout.setVisibility(View.GONE);
            holder.mainContainer.setVisibility(View.VISIBLE);

            Postcard_ message = messages.get(position);

            if (message.getAttachmentArrayList().size() == 0) {
                holder.mainContainer.setVisibility(View.VISIBLE);
            }

            // displaying text view data
            holder.message.setText(message.getDescription());
            holder.timestamp.setText(GetTime.wallTime(message.getDate()));

            // displaying the first letter of From in icon text
            if (message.getName().equalsIgnoreCase("n/a") || message.getFolder().equalsIgnoreCase("draft")) {
                holder.iconText.setText(Preferences.get(General.NAME).substring(0, 1).toUpperCase());
                holder.from.setText("Draft");
            } else {
                holder.from.setText(message.getName());
                holder.iconText.setText(message.getName().substring(0, 1).toUpperCase());
            }
            // displaying the first letter of From in icon text
            if (message.getSubject().equalsIgnoreCase("") || message.getFolder().equalsIgnoreCase("draft")) {
                //  holder.subject.setText("No Subject");
                holder.subject.setText(message.getSubject());
            } else {
                holder.from.setText(message.getName());
                holder.subject.setText(message.getSubject());
            }
            // change the row state to activated
            holder.itemView.setActivated(selectedItems.get(position, false));

            // change the font style depending on message read status
            applyReadStatus(holder, message);

            // handle message star
            //applyImportant(holder, message);

            // handle icon animation
            applyIconAnimation(holder, position);

            // display profile image
            applyProfilePicture(holder, message);

            //applyAttachment(holder, message);
            applyDrawable(holder, message);
            // apply click events
            applyClickEvents(holder, position);
        } else {
            holder.warningLayout.setVisibility(View.VISIBLE);
            holder.mainContainer.setVisibility(View.GONE);
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.iconImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconImportantClicked(position);
            }
        });

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    private void applyProfilePicture(MyViewHolder holder, Postcard_ message) {
        if (PostcardManipulations.isValidImage(message.getPhoto())) {
            //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
            Glide.with(mContext).load(message.getPhoto())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(GetThumbnails.userIcon(message.getPhoto()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(mContext)))
                    .into(holder.imgProfile);

            holder.imgProfile.setColorFilter(null);
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.imgProfile.setImageResource(R.drawable.primary_circle);
            holder.imgProfile.setColorFilter(PostcardManipulations.getRandomMaterialColor(mContext));
            holder.iconText.setVisibility(View.VISIBLE);
        }
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }


    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getId();
    }

    private void applyDrawable(MyViewHolder holder, Postcard_ message) {
        if (message.getIsAttachment() == 1 && message.getNew() == 1) {
            holder.from.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_12, 0, R.drawable.ic_attachment_24, 0);
        }
        if (message.getIsAttachment() == 0 && message.getNew() == 1) {
            holder.from.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_12, 0, 0, 0);
        }
        if (message.getIsAttachment() == 1 && message.getNew() == 0) {
            holder.from.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_attachment_24, 0);
        }
        if (message.getIsAttachment() == 0 && message.getNew() == 0) {
            holder.from.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void applyReadStatus(MyViewHolder holder, Postcard_ message) {
        if (message.getIsRead() == 1) {
            holder.from.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_read));

        } else {
            holder.from.setTypeface(null, Typeface.BOLD);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_primary));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        messages.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface MessageAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        final TextView from;
        final TextView subject;
        final TextView message;
        final TextView iconText;
        final TextView timestamp;
        final ImageView iconImp;
        final ImageView imgProfile;
        final LinearLayout messageContainer;
        final RelativeLayout iconContainer;
        final RelativeLayout iconBack;
        final RelativeLayout iconFront;
        final LinearLayout mainContainer;
        final RelativeLayout warningLayout;

        MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.txt_primary);
            message = (TextView) view.findViewById(R.id.txt_secondary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            iconImp = (ImageView) view.findViewById(R.id.icon_star);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);

            messageContainer = (LinearLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            mainContainer = (LinearLayout) view.findViewById(R.id.postcard_content_layout);
            warningLayout = (RelativeLayout) view.findViewById(R.id.postcard_warning_layout);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }
}