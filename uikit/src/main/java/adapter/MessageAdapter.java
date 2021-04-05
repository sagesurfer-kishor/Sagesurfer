package adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.EmojiSpan;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.Avatar;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.Settings.UISettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import constant.StringContract;
import listeners.StickyHeaderAdapter;
import screen.CometChatMediaViewActivity;
import screen.messagelist.CometChatMessageListActivity;
import screen.threadconversation.CometChatThreadMessageActivity;
import utils.Extensions;
import utils.FontUtils;
import utils.MediaUtils;
import utils.Utils;
import utils.ZoomIv;

/**
 * Purpose - MessageAdapter is a subclass of RecyclerView Adapter which is used to display
 * the list of messages. It helps to organize the messages based on its type i.e TextMessage,
 * MediaMessage, Actions. It also helps to manage whether message is sent or recieved and organizes
 * view based on it. It is single adapter used to manage group messages and user messages.
 * <p>
 * Created on - 20th December 2019
 * <p>
 * Modified on  - 23rd March 2020
 */


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderAdapter<MessageAdapter.DateItemHolder> {

    private static final int RIGHT_IMAGE_MESSAGE = 56;

    private static final int LEFT_IMAGE_MESSAGE = 89;

    private static final int RIGHT_STICKER_MESSAGE = 21;

    private static final int LEFT_STICKER_MESSAGE = 22;


    private static final int RIGHT_VIDEO_MESSAGE = 78;

    private static final int LEFT_VIDEO_MESSAGE = 87;

    private static final int RIGHT_AUDIO_MESSAGE = 39;

    private static final int LEFT_AUDIO_MESSAGE = 93;

    private static final int CALL_MESSAGE = 234;

    private List<BaseMessage> messageList = new ArrayList<>();

    private static final int LEFT_TEXT_MESSAGE = 1;

    private static final int RIGHT_TEXT_MESSAGE = 2;

    private static final int RIGHT_REPLY_TEXT_MESSAGE = 987;

    private static final int LEFT_REPLY_TEXT_MESSAGE = 789;

    private static final int RIGHT_FILE_MESSAGE = 23;

    private static final int LEFT_FILE_MESSAGE = 25;

    private static final int ACTION_MESSAGE = 99;

    private static final int RIGHT_LINK_MESSAGE = 12;

    private static final int LEFT_LINK_MESSAGE = 13;

    private static final int LEFT_DELETE_MESSAGE = 551;

    private static final int RIGHT_DELETE_MESSAGE = 552;

    private static final int RIGHT_CUSTOM_MESSAGE = 432;

    private static final int LEFT_CUSTOM_MESSAGE = 431;

    public Context context;

    private User loggedInUser = CometChat.getLoggedInUser();

    private boolean isLongClickEnabled;

    private List<Integer> selectedItemList = new ArrayList<>();

    private List<BaseMessage> longselectedItemList = new ArrayList<>();

    private FontUtils fontUtils;

    private MediaPlayer mediaPlayer;

    private int messagePosition = 0;

    private OnMessageLongClick messageLongClick;

    private boolean isUserDetailVisible;

    private String TAG = "MessageAdapter";

    private boolean isSent;

    private boolean isTextMessageClick;

    private boolean isImageMessageClick;
    String translatedText = null;
    private SharedPreferences sp;
    private String currentLang;

    /**
     * It is used to initialize the adapter wherever we needed. It has parameter like messageList
     * which contains list of messages and it will be us
     * <p>
     * ed in adapter and paramter type is a String
     * whose values will be either CometChatConstants.RECEIVER_TYPE_USER
     * CometChatConstants.RECEIVER_TYPE_GROUP.
     *
     * @param context     is a object of Context.
     * @param messageList is a list of messages used in this adapter.
     * @param type        is a String which identifies whether it is a user messages or a group messages.
     */
    public MessageAdapter(Context context, List<BaseMessage> messageList, String type) {
        Log.e("Lists", messageList.toString());

        setMessageList(messageList);
        this.context = context;
        try {
            messageLongClick = (CometChatMessageListActivity) context;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
        }

        fontUtils = FontUtils.getInstance(context);
    }

    /**
     * This method is used to return the different view types to adapter based on item position.
     * It uses getItemViewTypes() method to identify the view type of item.
     *
     * @param position is a position of item in recyclerView.
     * @return It returns int which is value of view type of item.
     * @see MessageAdapter#getItemViewTypes(int)
     * *
     * @see MessageAdapter#onCreateViewHolder(ViewGroup, int)
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewTypes(position);
    }

    private void setMessageList(List<BaseMessage> messageList) {
        this.messageList.addAll(0, messageList);
        notifyItemRangeInserted(0, messageList.size());
    }

    /**
     * This method is used to inflate the view for item based on its viewtype.
     * It helps to differentiate view for different type of messages.
     * Based on view type it returns various ViewHolder
     * Ex :- For MediaMessage it will return ImageMessageViewHolder,
     * For TextMessage it will return TextMessageViewHolder,etc.
     *
     * @param parent is a object of ViewGroup.
     * @param i      is viewType based on it various view will be inflated by adapter for various type
     *               of messages.
     * @return It return different ViewHolder for different viewType.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view;
        switch (i) {
            case LEFT_DELETE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message_item, parent, false);
                view.setTag(LEFT_DELETE_MESSAGE);
                return new DeleteMessageViewHolder(view);

            case RIGHT_DELETE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_item, parent, false);
                view.setTag(RIGHT_DELETE_MESSAGE);
                return new DeleteMessageViewHolder(view);

            case LEFT_TEXT_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message_item, parent, false);
                view.setTag(LEFT_TEXT_MESSAGE);
                return new TextMessageViewHolder(view);

            case RIGHT_TEXT_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_item, parent, false);
                view.setTag(RIGHT_TEXT_MESSAGE);
                return new TextMessageViewHolder(view);

            case LEFT_REPLY_TEXT_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message_item, parent, false);
                view.setTag(LEFT_REPLY_TEXT_MESSAGE);
                return new TextMessageViewHolder(view);

            case RIGHT_REPLY_TEXT_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_item, parent, false);
                view.setTag(RIGHT_REPLY_TEXT_MESSAGE);
                return new TextMessageViewHolder(view);

            case RIGHT_LINK_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right_link_item, parent, false);
                view.setTag(RIGHT_LINK_MESSAGE);
                return new LinkMessageViewHolder(view);

            case LEFT_LINK_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left_link_item, parent, false);
                view.setTag(LEFT_LINK_MESSAGE);
                return new LinkMessageViewHolder(view);

            case RIGHT_AUDIO_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cometchat_audio_layout_right, parent, false);
                view.setTag(RIGHT_AUDIO_MESSAGE);
                return new AudioMessageViewHolder(view);

            case LEFT_AUDIO_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cometchat_audio_layout_left, parent, false);
                view.setTag(LEFT_AUDIO_MESSAGE);
                return new AudioMessageViewHolder(view);

            case LEFT_IMAGE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left_list_image_item, parent, false);
                view.setTag(LEFT_IMAGE_MESSAGE);
                return new ImageMessageViewHolder(view);


            case RIGHT_IMAGE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right_list_image_item, parent, false);
                view.setTag(RIGHT_IMAGE_MESSAGE);
                return new ImageMessageViewHolder(view);


            case LEFT_VIDEO_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left_list_video_item, parent, false);
                view.setTag(LEFT_VIDEO_MESSAGE);
                return new VideoMessageViewHolder(view);

            case RIGHT_VIDEO_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right_list_video_item, parent, false);
                view.setTag(RIGHT_VIDEO_MESSAGE);
                return new VideoMessageViewHolder(view);

            case LEFT_STICKER_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left_sticker_item, parent, false);
                view.setTag(LEFT_STICKER_MESSAGE);
                return new StickerMessageViewHolder(view);

            case RIGHT_STICKER_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_right_sticker_item, parent, false);
                view.setTag(RIGHT_STICKER_MESSAGE);
                return new StickerMessageViewHolder(view);


            case RIGHT_FILE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cometchat_right_file_message, parent, false);
                view.setTag(RIGHT_FILE_MESSAGE);
                return new FileMessageViewHolder(view);

            case LEFT_FILE_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cometchat_left_file_message, parent, false);
                view.setTag(LEFT_FILE_MESSAGE);
                return new FileMessageViewHolder(view);

            case ACTION_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cometchat_action_message, parent, false);
                view.setTag(ACTION_MESSAGE);
                return new ActionMessageViewHolder(view);

            case CALL_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cometchat_action_message, parent, false);
                view.setTag(CALL_MESSAGE);
                return new ActionMessageViewHolder(view);

            case RIGHT_CUSTOM_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_item, parent, false);
                view.setTag(RIGHT_TEXT_MESSAGE);
                return new CustomMessageViewHolder(view);

            case LEFT_CUSTOM_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message_item, parent, false);
                view.setTag(LEFT_CUSTOM_MESSAGE);
                return new CustomMessageViewHolder(view);

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_item, parent, false);
                view.setTag(-1);
                return new TextMessageViewHolder(view);
        }
    }


    /**
     * This method is used to bind the various ViewHolder content with their respective view types.
     * Here different methods are being called for different view type and in each method different
     * ViewHolder are been passed as parameter along with position of item.
     * <p>
     * Ex :- For TextMessage <code>setTextData((TextMessageViewHolder)viewHolder,i)</code> is been called.
     * where <b>viewHolder</b> is casted as <b>TextMessageViewHolder</b> and <b>i</b> is position of item.
     *
     * @param viewHolder is a object of RecyclerViewHolder.
     * @param i          is position of item in recyclerView.
     * @see MessageAdapter#setTextData(TextMessageViewHolder, int)
     * @see MessageAdapter#setImageData(ImageMessageViewHolder, int)
     * @see MessageAdapter#setFileData(FileMessageViewHolder, int)
     * @see MessageAdapter#setActionData(ActionMessageViewHolder, int)
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        BaseMessage baseMessage = messageList.get(i);
        BaseMessage nextMessage = null, prevMessage = null;
        boolean isNextMessage = false, isPreviousMessage = false, isPrevActionMessage = false;
        if ((i + 1) < messageList.size()) {
            if (messageList.get(i + 1).getSender() != null)
                nextMessage = messageList.get(i + 1);
        }

        if ((i - 1) >= 0) {
            if (messageList.get(i - 1).getSender() != null)
                prevMessage = messageList.get(i - 1);
        }

        isPrevActionMessage = (prevMessage != null && (prevMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION) || prevMessage.getCategory().equals(CometChatConstants.CATEGORY_CALL)));
        isNextMessage = (nextMessage != null && baseMessage.getSender().getUid().equals(nextMessage.getSender().getUid()));
        isPreviousMessage = (prevMessage != null && baseMessage.getSender().getUid().equals(prevMessage.getSender().getUid()));

        if (!isPreviousMessage && isNextMessage) {
            isUserDetailVisible = true;
        }

        if (isPreviousMessage && isNextMessage) {
            isUserDetailVisible = false;
        } else if (!isNextMessage && !isPreviousMessage) {
            isUserDetailVisible = true;
        } else if (!isNextMessage) {
            isUserDetailVisible = false;
        }
        if (isPrevActionMessage) {
            isUserDetailVisible = true;
        }


        switch (viewHolder.getItemViewType()) {
            case LEFT_DELETE_MESSAGE:
                ((DeleteMessageViewHolder) viewHolder).ivUser.setVisibility(View.GONE);
            case RIGHT_DELETE_MESSAGE:
                setDeleteData((DeleteMessageViewHolder) viewHolder, i);
                break;
            case LEFT_TEXT_MESSAGE:
                Log.i(TAG, "onBindViewHolder: leftText "+messageList.get(i).toString());

            case LEFT_REPLY_TEXT_MESSAGE:
                Log.i(TAG, "onBindViewHolder: RIGHT_REPLY_TEXT_MESSAGE");
                ((TextMessageViewHolder) viewHolder).ivUser.setVisibility(View.GONE);

            case RIGHT_TEXT_MESSAGE:
                Log.i(TAG, "onBindViewHolder: rightText "+messageList.get(i).toString());

            case RIGHT_REPLY_TEXT_MESSAGE:
                Log.i(TAG, "onBindViewHolder: RIGHT_REPLY_TEXT_MESSAGE");
                setTextData((TextMessageViewHolder) viewHolder, i);
                break;
            case LEFT_LINK_MESSAGE:
            case RIGHT_LINK_MESSAGE:
                setLinkData((LinkMessageViewHolder) viewHolder, i);
                break;
            case LEFT_IMAGE_MESSAGE:
            case RIGHT_IMAGE_MESSAGE:
                setImageData((ImageMessageViewHolder) viewHolder, i);
                break;
            case LEFT_AUDIO_MESSAGE:
            case RIGHT_AUDIO_MESSAGE:
                setAudioData((AudioMessageViewHolder) viewHolder, i);
                break;
            case LEFT_VIDEO_MESSAGE:
            case RIGHT_VIDEO_MESSAGE:
                setVideoData((VideoMessageViewHolder) viewHolder, i);
                break;
            case LEFT_FILE_MESSAGE:
                ((FileMessageViewHolder) viewHolder).ivUser.setVisibility(View.GONE);
            case RIGHT_FILE_MESSAGE:
                setFileData((FileMessageViewHolder) viewHolder, i);
                break;
            case ACTION_MESSAGE:
            case CALL_MESSAGE:
                setActionData((ActionMessageViewHolder) viewHolder, i);
                break;
            case LEFT_CUSTOM_MESSAGE:
                ((CustomMessageViewHolder) viewHolder).ivUser.setVisibility(View.GONE);
            case RIGHT_CUSTOM_MESSAGE:
                try {
                    setCustomData((CustomMessageViewHolder) viewHolder, i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setAudioData(AudioMessageViewHolder viewHolder, int i) {
        BaseMessage baseMessage = messageList.get(i);
        if (baseMessage != null && baseMessage.getDeletedAt() == 0) {
            if (!baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                viewHolder.playBtn.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.textColorWhite)));
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    viewHolder.tvUser.setVisibility(View.GONE);
                    viewHolder.ivUser.setVisibility(View.GONE);
                } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                    if (isUserDetailVisible) {
                        viewHolder.tvUser.setVisibility(View.VISIBLE);
                        viewHolder.ivUser.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvUser.setVisibility(View.GONE);
                        viewHolder.ivUser.setVisibility(View.INVISIBLE);
                    }
                    setAvatar(viewHolder.ivUser, baseMessage.getSender().getAvatar(), baseMessage.getSender().getName());
                    viewHolder.tvUser.setText(baseMessage.getSender().getName());
                }
            }

            if (baseMessage.getReplyCount() != 0) {
                viewHolder.tvThreadReplyCount.setVisibility(View.VISIBLE);
                viewHolder.tvThreadReplyCount.setText(baseMessage.getReplyCount() + " Replies");
            } else {
                viewHolder.lvReplyAvatar.setVisibility(View.GONE);
                viewHolder.tvThreadReplyCount.setVisibility(View.GONE);
            }
            viewHolder.tvThreadReplyCount.setOnClickListener(view -> {
                Intent intent = new Intent(context, CometChatThreadMessageActivity.class);
//                intent.putExtra(StringContract.IntentStrings.PARENT_BASEMESSAGE,baseMessage.toString());
                intent.putExtra(StringContract.IntentStrings.NAME, baseMessage.getSender().getName());
                intent.putExtra(StringContract.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
                intent.putExtra(StringContract.IntentStrings.REPLY_COUNT, baseMessage.getReplyCount());
                intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getName());
                intent.putExtra(StringContract.IntentStrings.PARENT_ID, baseMessage.getId());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, baseMessage.getType());
                intent.putExtra(StringContract.IntentStrings.SENTAT, baseMessage.getSentAt());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((MediaMessage) baseMessage).getAttachment().getFileName());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, ((MediaMessage) baseMessage).getAttachment().getFileExtension());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((MediaMessage) baseMessage).getAttachment().getFileUrl());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, ((MediaMessage) baseMessage).getAttachment().getFileSize());
                intent.putExtra(StringContract.IntentStrings.TYPE, baseMessage.getReceiverType());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_CATEGORY, baseMessage.getCategory());
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                    intent.putExtra(StringContract.IntentStrings.GUID, baseMessage.getReceiverUid());
                } else {
                    if (baseMessage.getReceiverUid().equals(loggedInUser.getUid()))
                        intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getUid());
                    else
                        intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getReceiverUid());
                }
                context.startActivity(intent);
            });


            showMessageTime(viewHolder, baseMessage);
//            if (selectedItemList.contains(baseMessage.getId()))
            viewHolder.txtTime.setVisibility(View.VISIBLE);
//            else
//                viewHolder.txtTime.setVisibility(View.GONE);

            viewHolder.length.setText(Utils.getFileSize(((MediaMessage) baseMessage).getAttachment().getFileSize()));
            viewHolder.playBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            viewHolder.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CometChatMediaViewActivity.class);
                    intent.putExtra(StringContract.IntentStrings.MEDIA_SIZE,
                            ((MediaMessage) baseMessage).getAttachment().getFileSize());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, CometChatConstants.MESSAGE_TYPE_AUDIO);
                    intent.putExtra(StringContract.IntentStrings.INTENT_MEDIA_MESSAGE,
                            ((MediaMessage) baseMessage).getAttachment().getFileUrl());
                    intent.putExtra(StringContract.IntentStrings.NAME, baseMessage.getSender().getName());
                    intent.putExtra(StringContract.IntentStrings.SENTAT, baseMessage.getSentAt());
                    context.startActivity(intent);
                }
            });
            viewHolder.rlMessageBubble.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isLongClickEnabled && !isTextMessageClick) {
                        isImageMessageClick = true;
                        setLongClickSelectedItem(baseMessage);
                        messageLongClick.setLongMessageClick(longselectedItemList);
                        notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }
    }

    public void stopPlayingAudio() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    /**
     * This method is called whenever viewType of item is file. It is used to bind FileMessageViewHolder
     * contents with MediaMessage at a given position.
     * It shows FileName, FileType, FileSize.
     *
     * @param viewHolder is a object of FileMessageViewHolder.
     * @param i          is a position of item in recyclerView.
     * @see MediaMessage
     * @see BaseMessage
     */
    private void setFileData(FileMessageViewHolder viewHolder, int i) {
        BaseMessage baseMessage = messageList.get(i);

        if (baseMessage != null && baseMessage.getDeletedAt() == 0) {
            if (!baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    viewHolder.tvUser.setVisibility(View.GONE);
                    viewHolder.ivUser.setVisibility(View.GONE);
                } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                    if (isUserDetailVisible) {
                        viewHolder.tvUser.setVisibility(View.VISIBLE);
                        viewHolder.ivUser.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvUser.setVisibility(View.GONE);
                        viewHolder.ivUser.setVisibility(View.INVISIBLE);
                    }
                    setAvatar(viewHolder.ivUser, baseMessage.getSender().getAvatar(), baseMessage.getSender().getName());
                    viewHolder.tvUser.setText(baseMessage.getSender().getName());
                }
            }
            viewHolder.fileName.setText(((MediaMessage) baseMessage).getAttachment().getFileName());
            viewHolder.fileExt.setText(((MediaMessage) baseMessage).getAttachment().getFileExtension());
            int fileSize = ((MediaMessage) baseMessage).getAttachment().getFileSize();

            viewHolder.fileSize.setText(Utils.getFileSize(fileSize));

            viewHolder.lvReplyAvatar.setVisibility(View.GONE);
            if (baseMessage.getReplyCount() != 0) {
                try {
                    if (baseMessage.getMetadata() != null && baseMessage.getMetadata().has("replyAvatars")) {
                        JSONArray jsonArray = baseMessage.getMetadata().getJSONArray("replyAvatars");
                        viewHolder.lvReplyAvatar.setVisibility(View.VISIBLE);
                        for (int m = 0; m < jsonArray.length(); m++) {
                            if (viewHolder.lvReplyAvatar.getChildCount() < 4) {
                                Avatar avatarId = viewHolder.lvReplyAvatar.findViewById(m);
                                if (avatarId == null) {
                                    Avatar avatar = new Avatar(context.getApplicationContext());
                                    avatar.setId(m);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout
                                            .LayoutParams((int) Utils.dpToPx(context, 16), (int) Utils.dpToPx(context, 16));
                                    layoutParams.leftMargin = 8;
                                    layoutParams.rightMargin = 8;
                                    avatar.setLayoutParams(layoutParams);
                                    avatar.setAvatar(jsonArray.getString(m));
                                    viewHolder.lvReplyAvatar.addView(avatar, m);
                                }
                            }
                        }
                    } else {
                        viewHolder.lvReplyAvatar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "setImageData: " + e.getMessage());
                }
                viewHolder.tvThreadReplyCount.setVisibility(View.VISIBLE);
                viewHolder.tvThreadReplyCount.setText(baseMessage.getReplyCount() + " Replies");
            } else {
                viewHolder.lvReplyAvatar.setVisibility(View.GONE);
                viewHolder.tvThreadReplyCount.setVisibility(View.GONE);
            }
            viewHolder.tvThreadReplyCount.setOnClickListener(view -> {
                Intent intent = new Intent(context, CometChatThreadMessageActivity.class);
                intent.putExtra(StringContract.IntentStrings.NAME, baseMessage.getSender().getName());
                intent.putExtra(StringContract.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
                intent.putExtra(StringContract.IntentStrings.REPLY_COUNT, baseMessage.getReplyCount());
                intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getName());
                intent.putExtra(StringContract.IntentStrings.PARENT_ID, baseMessage.getId());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, baseMessage.getType());
                intent.putExtra(StringContract.IntentStrings.SENTAT, baseMessage.getSentAt());
                if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT))
                    intent.putExtra(StringContract.IntentStrings.TEXTMESSAGE, ((TextMessage) baseMessage).getText());
                else {
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((MediaMessage) baseMessage).getAttachment().getFileName());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, ((MediaMessage) baseMessage).getAttachment().getFileExtension());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((MediaMessage) baseMessage).getAttachment().getFileUrl());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, ((MediaMessage) baseMessage).getAttachment().getFileSize());
                }
                intent.putExtra(StringContract.IntentStrings.TYPE, baseMessage.getReceiverType());
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                    intent.putExtra(StringContract.IntentStrings.GUID, baseMessage.getReceiverUid());
                } else {
                    if (baseMessage.getReceiverUid().equals(loggedInUser.getUid()))
                        intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getUid());
                    else
                        intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getReceiverUid());
                }
                context.startActivity(intent);
            });


            showMessageTime(viewHolder, baseMessage);

            if (selectedItemList.contains(baseMessage.getId()))
                viewHolder.txtTime.setVisibility(View.VISIBLE);
            else
                viewHolder.txtTime.setVisibility(View.GONE);


            viewHolder.rlMessageBubble.setOnClickListener(view -> {
//                  if (isLongClickEnabled && !isTextMessageClick) {
//                          setLongClickSelectedItem(baseMessage);
//                  }
//                  else {
                setSelectedMessage(baseMessage.getId());
//                  }
                notifyDataSetChanged();
            });
            viewHolder.fileName.setOnClickListener(view -> MediaUtils.openFile(((MediaMessage) baseMessage).getAttachment().getFileUrl(), context));
            viewHolder.rlMessageBubble.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isLongClickEnabled && !isTextMessageClick) {
                        isImageMessageClick = true;
                        setLongClickSelectedItem(baseMessage);
                        messageLongClick.setLongMessageClick(longselectedItemList);
                        notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }
    }


    /**
     * This method is called whenever viewType of item is media. It is used to bind ImageMessageViewHolder
     * contents with MediaMessage at a given position.
     * It loads image of MediaMessage using its url.
     *
     * @param viewHolder is a object of ImageMessageViewHolder.
     * @param i          is a position of item in recyclerView.
     * @see MediaMessage
     * @see BaseMessage
     */
    private void setImageData(ImageMessageViewHolder viewHolder, int i) {

        BaseMessage baseMessage = messageList.get(i);
        if (!baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
            if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                viewHolder.tvUser.setVisibility(View.GONE);
                viewHolder.ivUser.setVisibility(View.GONE);
            } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                if (isUserDetailVisible) {
                    viewHolder.tvUser.setVisibility(View.VISIBLE);
                    viewHolder.ivUser.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tvUser.setVisibility(View.GONE);
                    viewHolder.ivUser.setVisibility(View.INVISIBLE);
                }
                setAvatar(viewHolder.ivUser, baseMessage.getSender().getAvatar(), baseMessage.getSender().getName());
                viewHolder.tvUser.setText(baseMessage.getSender().getName());
            }
        }

        boolean isImageNotSafe = Extensions.getImageModeration(context, baseMessage);
        String smallUrl = Extensions.getThumbnailGeneration(context, baseMessage);
        viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_defaulf_image));
        if (smallUrl != null) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).load(smallUrl).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    if (isImageNotSafe)
                        viewHolder.imageView.setImageBitmap(Utils.blur(context, resource));
                    else
                        viewHolder.imageView.setImageBitmap(resource);
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    if (isImageNotSafe)
                        viewHolder.imageView.setImageBitmap(Utils.blur(context, resource));

                    else
                        viewHolder.imageView.setImageBitmap(resource);
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            });
        }
        if (isImageNotSafe) {
            viewHolder.sensitiveLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.sensitiveLayout.setVisibility(View.GONE);
        }

        if (baseMessage.getReplyCount() != 0) {
            try {
                if (baseMessage.getMetadata() != null && baseMessage.getMetadata().has("replyAvatars")) {
                    JSONArray jsonArray = baseMessage.getMetadata().getJSONArray("replyAvatars");
                    viewHolder.lvReplyAvatar.setVisibility(View.VISIBLE);
                    for (int m = 0; m < jsonArray.length(); m++) {
                        if (viewHolder.lvReplyAvatar.getChildCount() < 4) {
                            Avatar avatarId = viewHolder.lvReplyAvatar.findViewById(m);
                            if (avatarId == null) {
                                Avatar avatar = new Avatar(context.getApplicationContext());
                                avatar.setId(m);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout
                                        .LayoutParams((int) Utils.dpToPx(context, 16), (int) Utils.dpToPx(context, 16));
                                layoutParams.leftMargin = 8;
                                layoutParams.rightMargin = 8;
                                avatar.setLayoutParams(layoutParams);
                                avatar.setAvatar(jsonArray.getString(m));
                                viewHolder.lvReplyAvatar.addView(avatar, m);
                            }
                        }
                    }
                } else {
                    viewHolder.lvReplyAvatar.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e(TAG, "setImageData: " + e.getMessage());
            }
            viewHolder.tvThreadReplyCount.setVisibility(View.VISIBLE);
            viewHolder.tvThreadReplyCount.setText(baseMessage.getReplyCount() + " Replies");
        } else {
            viewHolder.lvReplyAvatar.setVisibility(View.GONE);
            viewHolder.tvThreadReplyCount.setVisibility(View.GONE);
        }
        viewHolder.tvThreadReplyCount.setOnClickListener(view -> {
            Intent intent = new Intent(context, CometChatThreadMessageActivity.class);
            intent.putExtra(StringContract.IntentStrings.NAME, baseMessage.getSender().getName());
            intent.putExtra(StringContract.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
            intent.putExtra(StringContract.IntentStrings.REPLY_COUNT, baseMessage.getReplyCount());
            intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getName());
            intent.putExtra(StringContract.IntentStrings.PARENT_ID, baseMessage.getId());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, baseMessage.getType());
            intent.putExtra(StringContract.IntentStrings.SENTAT, baseMessage.getSentAt());
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT))
                intent.putExtra(StringContract.IntentStrings.TEXTMESSAGE, ((TextMessage) baseMessage).getText());
            else {
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((MediaMessage) baseMessage).getAttachment().getFileName());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, ((MediaMessage) baseMessage).getAttachment().getFileExtension());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((MediaMessage) baseMessage).getAttachment().getFileUrl());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, ((MediaMessage) baseMessage).getAttachment().getFileSize());
            }
            intent.putExtra(StringContract.IntentStrings.TYPE, baseMessage.getReceiverType());
            if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                intent.putExtra(StringContract.IntentStrings.GUID, baseMessage.getReceiverUid());
            } else {
                if (baseMessage.getReceiverUid().equals(loggedInUser.getUid()))
                    intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getUid());
                else
                    intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getReceiverUid());
            }
            context.startActivity(intent);
        });



        showMessageTime(viewHolder, baseMessage);
        if (selectedItemList.contains(baseMessage.getId()))
            viewHolder.txtTime.setVisibility(View.VISIBLE);
        else
            viewHolder.txtTime.setVisibility(View.GONE);


        viewHolder.rlMessageBubble.setOnClickListener(view -> {
            if (isImageNotSafe) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Unsafe Content");
                alert.setIcon(R.drawable.ic_hand);
                alert.setMessage("Are you surely want to see this unsafe content");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MediaUtils.openFile(((MediaMessage) baseMessage).getAttachment().getFileUrl(), context);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create().show();
            } else {
                setSelectedMessage(baseMessage.getId());
                notifyDataSetChanged();
                MediaUtils.openFile(((MediaMessage) baseMessage).getAttachment().getFileUrl(), context);
            }

        });
        viewHolder.rlMessageBubble.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isLongClickEnabled && !isTextMessageClick) {
                    isImageMessageClick = true;
                    setLongClickSelectedItem(baseMessage);
                    messageLongClick.setLongMessageClick(longselectedItemList);
                    notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    private void displayImage(BaseMessage baseMessage) {
        Dialog imageDialog = new Dialog(context);
        View messageVw = LayoutInflater.from(context).inflate(R.layout.image_dialog_view, null);
        ZoomIv imageView = messageVw.findViewById(R.id.imageView);
        Glide.with(context).asBitmap().load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                imageView.setImageBitmap(resource);
            }
        });
        imageDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageDialog.setContentView(messageVw);
        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        imageDialog.show();
    }

    private void setVideoData(VideoMessageViewHolder viewHolder, int i) {


        BaseMessage baseMessage = messageList.get(i);
        if (!baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
            if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                viewHolder.tvUser.setVisibility(View.GONE);
                viewHolder.ivUser.setVisibility(View.GONE);
            } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                if (isUserDetailVisible) {
                    viewHolder.tvUser.setVisibility(View.VISIBLE);
                    viewHolder.ivUser.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tvUser.setVisibility(View.GONE);
                    viewHolder.ivUser.setVisibility(View.INVISIBLE);
                }
                setAvatar(viewHolder.ivUser, baseMessage.getSender().getAvatar(), baseMessage.getSender().getName());
                viewHolder.tvUser.setText(baseMessage.getSender().getName());
            }
        }
        if (((MediaMessage) baseMessage).getAttachment() != null)
            Glide.with(context).load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(viewHolder.imageView);
        if (baseMessage.getReplyCount() != 0) {
            try {
                if (baseMessage.getMetadata() != null && baseMessage.getMetadata().has("replyAvatars")) {
                    JSONArray jsonArray = baseMessage.getMetadata().getJSONArray("replyAvatars");
                    viewHolder.lvReplyAvatar.setVisibility(View.VISIBLE);
                    for (int m = 0; m < jsonArray.length(); m++) {
                        if (viewHolder.lvReplyAvatar.getChildCount() < 4) {
                            Avatar avatarId = viewHolder.lvReplyAvatar.findViewById(m);
                            if (avatarId == null) {
                                Avatar avatar = new Avatar(context.getApplicationContext());
                                avatar.setId(m);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout
                                        .LayoutParams((int) Utils.dpToPx(context, 16), (int) Utils.dpToPx(context, 16));
                                layoutParams.leftMargin = 8;
                                layoutParams.rightMargin = 8;
                                avatar.setLayoutParams(layoutParams);
                                avatar.setAvatar(jsonArray.getString(m));
                                viewHolder.lvReplyAvatar.addView(avatar, m);
                            }
                        }
                    }
                } else {
                    viewHolder.lvReplyAvatar.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e(TAG, "setImageData: " + e.getMessage());
            }
            viewHolder.tvThreadReplyCount.setVisibility(View.VISIBLE);
            viewHolder.tvThreadReplyCount.setText(baseMessage.getReplyCount() + " Replies");
        } else {
            viewHolder.lvReplyAvatar.setVisibility(View.GONE);
            viewHolder.tvThreadReplyCount.setVisibility(View.GONE);
        }
        viewHolder.tvThreadReplyCount.setOnClickListener(view -> {
            Intent intent = new Intent(context, CometChatThreadMessageActivity.class);
            intent.putExtra(StringContract.IntentStrings.NAME, baseMessage.getSender().getName());
            intent.putExtra(StringContract.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
            intent.putExtra(StringContract.IntentStrings.REPLY_COUNT, baseMessage.getReplyCount());
            intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getName());
            intent.putExtra(StringContract.IntentStrings.PARENT_ID, baseMessage.getId());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, baseMessage.getType());
            intent.putExtra(StringContract.IntentStrings.SENTAT, baseMessage.getSentAt());
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT))
                intent.putExtra(StringContract.IntentStrings.TEXTMESSAGE, ((TextMessage) baseMessage).getText());
            else {
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((MediaMessage) baseMessage).getAttachment().getFileName());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, ((MediaMessage) baseMessage).getAttachment().getFileExtension());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((MediaMessage) baseMessage).getAttachment().getFileUrl());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, ((MediaMessage) baseMessage).getAttachment().getFileSize());
            }
            intent.putExtra(StringContract.IntentStrings.TYPE, baseMessage.getReceiverType());
            if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                intent.putExtra(StringContract.IntentStrings.GUID, baseMessage.getReceiverUid());
            } else {
                if (baseMessage.getReceiverUid().equals(loggedInUser.getUid()))
                    intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getUid());
                else
                    intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getReceiverUid());
            }
            context.startActivity(intent);
        });


        showMessageTime(viewHolder, baseMessage);
        if (selectedItemList.contains(baseMessage.getId()))
            viewHolder.txtTime.setVisibility(View.VISIBLE);
        else
            viewHolder.txtTime.setVisibility(View.GONE);

        viewHolder.rlMessageBubble.setOnClickListener(view -> {
            setSelectedMessage(baseMessage.getId());
            notifyDataSetChanged();

        });
        viewHolder.rlMessageBubble.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isLongClickEnabled && !isTextMessageClick) {
                    isImageMessageClick = true;
                    setLongClickSelectedItem(baseMessage);
                    messageLongClick.setLongMessageClick(longselectedItemList);
                    notifyDataSetChanged();
                }
                return true;
            }
        });
        viewHolder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaUtils.openFile(((MediaMessage) baseMessage).getAttachment().getFileUrl(), context);
            }
        });
    }

    private void setDeleteData(DeleteMessageViewHolder viewHolder, int i) {


        BaseMessage baseMessage = messageList.get(i);
        if (!baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
            if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                viewHolder.tvUser.setVisibility(View.GONE);
                viewHolder.ivUser.setVisibility(View.GONE);
            } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                if (isUserDetailVisible) {
                    viewHolder.tvUser.setVisibility(View.VISIBLE);
                    viewHolder.ivUser.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tvUser.setVisibility(View.GONE);
                    viewHolder.ivUser.setVisibility(View.INVISIBLE);
                }
                setAvatar(viewHolder.ivUser, baseMessage.getSender().getAvatar(), baseMessage.getSender().getName());
                viewHolder.tvUser.setText(baseMessage.getSender().getName());
            }
        }
        if (baseMessage.getDeletedAt() != 0) {
            viewHolder.tvThreadReplyCount.setVisibility(View.GONE);
            viewHolder.lvReplyAvatar.setVisibility(View.GONE);
            viewHolder.txtMessage.setText(R.string.message_deleted);
            viewHolder.txtMessage.setTextColor(context.getResources().getColor(R.color.secondaryTextColor));
            viewHolder.txtMessage.setTypeface(null, Typeface.ITALIC);
        }
        showMessageTime(viewHolder, baseMessage);

        if (selectedItemList.contains(baseMessage.getId()))
            viewHolder.txtTime.setVisibility(View.VISIBLE);
        else
            viewHolder.txtTime.setVisibility(View.GONE);
//
    }


    /**
     * This method is called whenever viewType of item is Action. It is used to bind
     * ActionMessageViewHolder contents with Action at a given position. It shows action message
     * or call status based on message category
     *
     * @param viewHolder is a object of ActionMessageViewHolder.
     * @param i          is a position of item in recyclerView.
     * @see Action
     * @see Call
     * @see BaseMessage
     */
    private void setActionData(ActionMessageViewHolder viewHolder, int i) {
        BaseMessage baseMessage = messageList.get(i);
        if (Utils.isDarkMode(context))
            viewHolder.textView.setTextColor(context.getResources().getColor(R.color.textColorWhite));
        else
            viewHolder.textView.setTextColor(context.getResources().getColor(R.color.primaryTextColor));

        viewHolder.textView.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        if (baseMessage instanceof Action)
            viewHolder.textView.setText(((Action) baseMessage).getMessage());
        else if (baseMessage instanceof Call) {
            Call call = ((Call) baseMessage);
            if (call.getCallStatus().equals(CometChatConstants.CALL_STATUS_INITIATED)) {
                // viewHolder.textView.setText(call.getSender().getName() + " " + call.getCallStatus() + " " + call.getType() + " " + context.getResources().getString(R.string.call).toLowerCase());
                viewHolder.textView.setText(call.getSender().getName() + " " + call.getCallStatus() + " " + call.getType() + " " + context.getResources().getString(R.string.call).toLowerCase());

                viewHolder.txtTime.setText(Utils.getHeaderDate(call.getSentAt() * 1000));
                Log.i(TAG, "setActionData: initited callSentAt"+(call.getSentAt()+1000));
            }else if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING)) {
                // viewHolder.textView.setText(call.getSender().getName() + " " + call.getCallStatus() + " " + call.getType() + " " + context.getResources().getString(R.string.call).toLowerCase());
                viewHolder.textView.setText(call.getSender().getName() + " " + call.getCallStatus() + " " + call.getType() + " " + context.getResources().getString(R.string.call).toLowerCase());
                Log.i(TAG, "setActionData: ongoing  callSentAt"+(call.getSentAt()+1000));
                viewHolder.txtTime.setText(Utils.getHeaderDate(call.getSentAt() * 1000));
            } else
                Log.i(TAG, "setActionData: ended at callSentAt"+(call.getSentAt()+1000));
                viewHolder.textView.setText(call.getSender().getName() + " " + call.getCallStatus() + " " + call.getType() + " " + context.getResources().getString(R.string.call).toLowerCase());
                viewHolder.txtTime.setText(Utils.getHeaderDate(call.getSentAt() * 1000));
        }
    }

    /**
     * This method is used to show message time below message whenever we click on message.
     * Since we have different ViewHolder, we have to pass <b>txtTime</b> of each viewHolder to
     * <code>setStatusIcon(RecyclerView.ViewHolder viewHolder,BaseMessage baseMessage)</code>
     * along with baseMessage.
     *
     * @param viewHolder  is object of ViewHolder.
     * @param baseMessage is a object of BaseMessage.
     * @see MessageAdapter#setStatusIcon(TextView, BaseMessage)
     * *
     * @see BaseMessage
     */
    private void showMessageTime(RecyclerView.ViewHolder viewHolder, BaseMessage baseMessage) {

        if (viewHolder instanceof TextMessageViewHolder) {
            setStatusIcon(((TextMessageViewHolder) viewHolder).txtTime, baseMessage);
        } else if (viewHolder instanceof LinkMessageViewHolder) {
            setStatusIcon(((LinkMessageViewHolder) viewHolder).txtTime, baseMessage);
        } else if (viewHolder instanceof ImageMessageViewHolder) {
            setStatusIcon(((ImageMessageViewHolder) viewHolder).txtTime, baseMessage);
        } else if (viewHolder instanceof FileMessageViewHolder) {
            setStatusIcon(((FileMessageViewHolder) viewHolder).txtTime, baseMessage);
        }

    }

    /**
     * This method is used set message time i.e sentAt, deliveredAt & readAt in <b>txtTime</b>.
     * If sender of baseMessage is user then for user side messages it will show readAt, deliveredAt
     * time along with respective icon. For reciever side message it will show only deliveredAt time
     *
     * @param txtTime     is a object of TextView which will show time.
     * @param baseMessage is a object of BaseMessage used to identify baseMessage sender.
     * @see BaseMessage
     */
    private void setStatusIcon(TextView txtTime, BaseMessage baseMessage) {
        if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
            if (baseMessage.getReadAt() != 0) {
                txtTime.setText(Utils.getHeaderDate(baseMessage.getReadAt() * 1000));
                txtTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick, 0);
                txtTime.setCompoundDrawablePadding(10);
            } else if (baseMessage.getDeliveredAt() != 0) {
                txtTime.setText(Utils.getHeaderDate(baseMessage.getDeliveredAt() * 1000));
                txtTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_all_black_24dp, 0);
                txtTime.setCompoundDrawablePadding(10);
            } else {
                txtTime.setText(Utils.getHeaderDate(baseMessage.getSentAt() * 1000));
                txtTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_black_24dp, 0);
                txtTime.setCompoundDrawablePadding(10);
            }
        } else {
            txtTime.setText(Utils.getHeaderDate(baseMessage.getSentAt() * 1000));
        }
    }

    /**
     * This method is called whenever viewType of item is TextMessage. It is used to bind
     * TextMessageViewHolder content with TextMessage at given position.
     * It shows text of a message if deletedAt = 0 else it shows "message deleted"
     *
     * @param viewHolder is a object of TextMessageViewHolder.
     * @param i          is postion of item in recyclerView.
     * @see TextMessage
     * @see BaseMessage
     */
    private void setTextData(TextMessageViewHolder viewHolder, int i) {

        BaseMessage baseMessage = messageList.get(i);
        if (baseMessage != null) {
            if (!baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    viewHolder.tvUser.setVisibility(View.GONE);
                    viewHolder.ivUser.setVisibility(View.GONE);
                } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                    if (isUserDetailVisible) {
                        viewHolder.tvUser.setVisibility(View.VISIBLE);
                        viewHolder.ivUser.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvUser.setVisibility(View.GONE);
                        viewHolder.ivUser.setVisibility(View.INVISIBLE);
                    }
                    setAvatar(viewHolder.ivUser, baseMessage.getSender().getAvatar(), baseMessage.getSender().getName());
                    viewHolder.tvUser.setText(baseMessage.getSender().getName());
                }
                boolean isSentimentNegative = Extensions.checkSentiment(baseMessage);
                if (isSentimentNegative) {
                    viewHolder.txtMessage.setVisibility(View.GONE);
                    viewHolder.sentimentVw.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.txtMessage.setVisibility(View.VISIBLE);
                    viewHolder.sentimentVw.setVisibility(View.GONE);
                }
                viewHolder.viewSentimentMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder sentimentAlert = new AlertDialog.Builder(context)
                                .setTitle(context.getResources().getString(R.string.sentiment_alert))
                                .setMessage(context.getResources().getString(R.string.sentiment_alert_message))
                                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        viewHolder.txtMessage.setVisibility(View.VISIBLE);
                                        viewHolder.sentimentVw.setVisibility(View.GONE);
                                    }
                                })
                                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        sentimentAlert.create().show();
                    }
                });
            }
            if (baseMessage.getMetadata() != null && baseMessage.getMetadata().has("reply")) {
                try {
                    JSONObject metaData = baseMessage.getMetadata().getJSONObject("reply");
                    String messageType = metaData.getString("type");
                    String message = metaData.getString("message");
                    viewHolder.replyLayout.setVisibility(View.VISIBLE);
                    viewHolder.replyUser.setText(metaData.getString("name"));
                    if (messageType.equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                        viewHolder.replyMessage.setText(message);
                        viewHolder.replyMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else if (messageType.equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
                        viewHolder.replyMessage.setText(context.getResources().getString(R.string.shared_a_image));
                        viewHolder.replyMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo, 0, 0, 0);
                    } else if (messageType.equals(CometChatConstants.MESSAGE_TYPE_AUDIO)) {
                        viewHolder.replyMessage.setText(String.format(context.getResources().getString(R.string.shared_a_audio), ""));
                        viewHolder.replyMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_library_music_24dp, 0, 0, 0);
                    } else if (messageType.equals(CometChatConstants.MESSAGE_TYPE_VIDEO)) {
                        viewHolder.replyMessage.setText(context.getResources().getString(R.string.shared_a_video));
                        viewHolder.replyMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_videocam_24dp, 0, 0, 0);
                    } else if (messageType.equals(CometChatConstants.MESSAGE_TYPE_FILE)) {
                        viewHolder.replyMessage.setText(String.format(context.getResources().getString(R.string.shared_a_file), ""));
                        viewHolder.replyMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_insert_drive_file_black_24dp, 0, 0, 0);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "setTextData: " + e.getMessage());
                }
            }

            if (baseMessage.getReplyCount() != 0) {
                try {
                    if (baseMessage.getMetadata() != null && baseMessage.getMetadata().has("replyAvatars")) {
                        JSONArray jsonArray = baseMessage.getMetadata().getJSONArray("replyAvatars");
                        viewHolder.lvReplyAvatar.setVisibility(View.VISIBLE);
                        for (int m = 0; m < jsonArray.length(); m++) {
                            if (viewHolder.lvReplyAvatar.getChildCount() < 4) {
                                Avatar avatarId = viewHolder.lvReplyAvatar.findViewById(m);
                                if (avatarId == null) {
                                    Avatar avatar = new Avatar(context.getApplicationContext());
                                    avatar.setId(m);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout
                                            .LayoutParams((int) Utils.dpToPx(context, 16), (int) Utils.dpToPx(context, 16));
                                    layoutParams.leftMargin = 8;
                                    layoutParams.rightMargin = 8;
                                    avatar.setLayoutParams(layoutParams);
                                    avatar.setAvatar(jsonArray.getString(m));
                                    viewHolder.lvReplyAvatar.addView(avatar, m);
                                }
                            }
                        }
                    } else {
                        viewHolder.lvReplyAvatar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "setImageData: " + e.getMessage());
                }
                viewHolder.tvThreadReplyCount.setVisibility(View.VISIBLE);
                viewHolder.tvThreadReplyCount.setText(baseMessage.getReplyCount() + " Replies");
            } else {
                viewHolder.lvReplyAvatar.setVisibility(View.GONE);
                viewHolder.tvThreadReplyCount.setVisibility(View.GONE);
            }
            viewHolder.tvThreadReplyCount.setOnClickListener(view -> {
                Intent intent = new Intent(context, CometChatThreadMessageActivity.class);
                intent.putExtra(StringContract.IntentStrings.NAME, baseMessage.getSender().getName());
                intent.putExtra(StringContract.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
                intent.putExtra(StringContract.IntentStrings.REPLY_COUNT, baseMessage.getReplyCount());
                intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getName());
                intent.putExtra(StringContract.IntentStrings.PARENT_ID, baseMessage.getId());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, baseMessage.getType());
                intent.putExtra(StringContract.IntentStrings.SENTAT, baseMessage.getSentAt());
                if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT))
                    intent.putExtra(StringContract.IntentStrings.TEXTMESSAGE, ((TextMessage) baseMessage).getText());
                else {
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((MediaMessage) baseMessage).getAttachment().getFileName());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, ((MediaMessage) baseMessage).getAttachment().getFileExtension());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((MediaMessage) baseMessage).getAttachment().getFileUrl());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, ((MediaMessage) baseMessage).getAttachment().getFileSize());
                }
                intent.putExtra(StringContract.IntentStrings.TYPE, baseMessage.getReceiverType());
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                    intent.putExtra(StringContract.IntentStrings.GUID, baseMessage.getReceiverUid());
                } else {
                    if (baseMessage.getReceiverUid().equals(loggedInUser.getUid()))
                        intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getUid());
                    else
                        intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getReceiverUid());
                }
                context.startActivity(intent);
            });


            String txtMessage = ((TextMessage) baseMessage).getText().trim();
            viewHolder.txtMessage.setTextSize(16f);
            int count = 0;
            CharSequence processed = EmojiCompat.get().process(txtMessage, 0,
                    txtMessage.length() - 1, Integer.MAX_VALUE, EmojiCompat.REPLACE_STRATEGY_ALL);
            if (processed instanceof Spannable) {
                Spannable spannable = (Spannable) processed;
                count = spannable.getSpans(0, spannable.length() - 1, EmojiSpan.class).length;
                if (Utils.removeEmojiAndSymbol(txtMessage).trim().length() == 0) {
                    if (count == 1) {
                        viewHolder.txtMessage.setTextSize((int) Utils.dpToPx(context, 32));
                    } else if (count == 2) {
                        viewHolder.txtMessage.setTextSize((int) Utils.dpToPx(context, 24));
                    }
                }
            }

            viewHolder.txtMessage.setText(txtMessage);

            String profanityFilter = Extensions.checkProfanityMessage(baseMessage);
            String[] parts = profanityFilter.split(" ");
            String part1 = parts[0];
            if (part1.equals("<img")) {
                String str = profanityFilter.replaceAll("<img src=\"", "");
                String s = str.replaceAll("\">", "");
                viewHolder.imageView.setVisibility(View.VISIBLE);
                viewHolder.txtMessage.setVisibility(View.GONE);
                Glide.with(context).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).load(s).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        viewHolder.imageView.setImageBitmap(resource);
                    }
                });


            } else {
                //viewHolder.txtMessage.setText(profanityFilter);
                /*code changed for fetching data from received data from cometchat API
                * changed by rahul*/
                JSONObject metadata = baseMessage.getMetadata();
                try {
                    //JSONObject injectedObject = metadata.getJSONObject("@injected");
                    //if (injectedObject.has("extensions")) {
                        //JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                        if (metadata.has("message-translation_languages")) {
                            //JSONObject messageTranslationObject = metadata.getJSONObject("message-translation_languages");
                            JSONArray translations = metadata.getJSONArray("message-translation_languages");
                            HashMap<String, String> translationsMap = new HashMap<String, String>();

                            for (int ii = 0; ii < translations.length(); ii++) {
                                JSONObject translation = translations.getJSONObject(ii);
                                String translatedText = translation.getString("message_translated");
                                String translatedLanguage = translation.getString("language_translated");
                                translationsMap.put(translatedLanguage, translatedText);
                                if (translatedLanguage.equals(currentLang)) {
                                    viewHolder.txtMessage.setText(translatedText);
                                }
                            }
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            viewHolder.txtMessage.setTypeface(fontUtils.getTypeFace(FontUtils.robotoRegular));

            if (baseMessage.getSender().getUid().equals(loggedInUser.getUid()))
                viewHolder.txtMessage.setTextColor(context.getResources().getColor(R.color.textColorWhite));
            else
                viewHolder.txtMessage.setTextColor(context.getResources().getColor(R.color.primaryTextColor));

            showMessageTime(viewHolder, baseMessage);
            if (messageList.get(messageList.size() - 1).equals(baseMessage)) {
                selectedItemList.add(baseMessage.getId());
            }
            if (selectedItemList.contains(baseMessage.getId()))
                viewHolder.txtTime.setVisibility(View.VISIBLE);
            else
                viewHolder.txtTime.setVisibility(View.GONE);

            setColorFilter(baseMessage, viewHolder.cardView);

            viewHolder.rlMessageBubble.setOnClickListener(view -> {
                if (isLongClickEnabled && !isImageMessageClick) {
                    setLongClickSelectedItem(baseMessage);
                    messageLongClick.setLongMessageClick(longselectedItemList);
                } else {
                    setSelectedMessage(baseMessage.getId());
                }
                notifyDataSetChanged();

            });

            viewHolder.rlMessageBubble.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!isImageMessageClick) {
                        isLongClickEnabled = true;
                        isTextMessageClick = true;
                        setLongClickSelectedItem(baseMessage);
                        messageLongClick.setLongMessageClick(longselectedItemList);
                        notifyDataSetChanged();
                    }
                    return true;
                }
            });
            viewHolder.itemView.setTag(R.string.message, baseMessage);
        }
    }


    private void setCustomData(CustomMessageViewHolder viewHolder, int i) throws JSONException {
        BaseMessage baseMessage = messageList.get(i);
        if (baseMessage != null) {
            if (!baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    viewHolder.tvUser.setVisibility(View.GONE);
                    viewHolder.ivUser.setVisibility(View.GONE);
                } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                    if (isUserDetailVisible) {
                        viewHolder.tvUser.setVisibility(View.VISIBLE);
                        viewHolder.ivUser.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvUser.setVisibility(View.GONE);
                        viewHolder.ivUser.setVisibility(View.INVISIBLE);
                    }
                    setAvatar(viewHolder.ivUser, baseMessage.getSender().getAvatar(), baseMessage.getSender().getName());
                    viewHolder.tvUser.setText(baseMessage.getSender().getName());
                }
            }

            String s;

            if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {

                String txtMessages = "whiteboard invitation send successfully  <font color=#cc0029>click here</font> to open";
                viewHolder.txtMessage.setTypeface(fontUtils.getTypeFace(FontUtils.robotoLight));

                viewHolder.txtMessage.setText(Html.fromHtml(txtMessages));
                viewHolder.txtTime.setText(Utils.getHeaderDate(baseMessage.getSentAt() * 1000));

                try {
                    s = baseMessage.getMetadata().getString("whiteboard_URL_one");
                    if (!s.isEmpty()) {
                        translatedText = s;
                    }
                } catch (JSONException e) {
                    try {
                        String ss = baseMessage.getMetadata().getString("whiteboard_URL");
                        if (!ss.isEmpty()) {
                            translatedText = ss;
                        }
                    } catch (Exception ee) {
                        String ss = baseMessage.getMetadata().getString("whiteboard_URL_group");
                        if (!ss.isEmpty()) {
                            translatedText = ss;
                        }
                    }
                }

            } else {

                String txtMessages = "Hello! I would like to invite you to join the whiteboard <font color=#cc0029>click here</font> to open";
                viewHolder.txtMessage.setTypeface(fontUtils.getTypeFace(FontUtils.robotoLight));
                if (baseMessage.getSender().getUid().equals(loggedInUser.getUid()))
                    viewHolder.txtMessage.setTextColor(context.getResources().getColor(R.color.textColorWhite));
                else
                    viewHolder.txtMessage.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
                viewHolder.txtMessage.setText(Html.fromHtml(txtMessages));
                viewHolder.txtTime.setText(Utils.getHeaderDate(baseMessage.getReadAt() * 1000));

                try {
                    try {
                        s = baseMessage.getMetadata().getString("whiteboard_URL_one");
                        if (!s.isEmpty()) {
                            translatedText = s;
                        }
                    } catch (Exception ee) {
                        try {
                            String ss = baseMessage.getMetadata().getString("whiteboard_URL");
                            if (!ss.isEmpty()) {
                                translatedText = ss;
                            }
                        } catch (Exception eee) {
                            String whiteboard_URL_group="";
                            whiteboard_URL_group = baseMessage.getMetadata().getString("whiteboard_URL_group");
                            if (!whiteboard_URL_group.isEmpty()) {
                                translatedText="";
                                translatedText = whiteboard_URL_group;
                                Log.i(TAG, "setCustomData: ss "+whiteboard_URL_group);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "setCustomData: exception ");
                    try {
                        String ss = baseMessage.getMetadata().getString("whiteboard_URL");
                        if (!ss.isEmpty()) {
                            translatedText = ss;
                        }
                    } catch (Exception ee) {
                        String ss = baseMessage.getMetadata().getString("whiteboard_URL_group");
                        if (!ss.isEmpty()) {
                            translatedText = ss;
                        }
                    }

                }

            }

          /*  JSONObject metadata = baseMessage.getMetadata();
                try {

                    if (metadata != null) {
                        String s = null;
                        String groups = null;
                        try {
                            s = metadata.getString("whiteboard_URL_one");
                            if (!s.isEmpty()) {
                                translatedText = s;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            viewHolder.txtMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WhiteboardActivity.class);
                    intent.putExtra("whiteBoardUrl", translatedText);
                    Log.i(TAG, "onClick: sessionId"+translatedText);
                    context.startActivity(intent);

                }
            });

            viewHolder.txtMessage.setTypeface(fontUtils.getTypeFace(FontUtils.robotoLight));
            if (baseMessage.getSender().getUid().equals(loggedInUser.getUid()))
                viewHolder.txtMessage.setTextColor(context.getResources().getColor(R.color.textColorWhite));
            else
                viewHolder.txtMessage.setTextColor(context.getResources().getColor(R.color.primaryTextColor));

            showMessageTime(viewHolder, baseMessage);
            if (messageList.get(messageList.size() - 1).equals(baseMessage)) {
                selectedItemList.add(baseMessage.getId());
            }
//            if (selectedItemList.contains(baseMessage.getId()))
            viewHolder.txtTime.setVisibility(View.VISIBLE);
//            else
//                viewHolder.txtTime.setVisibility(View.GONE);

            viewHolder.rlMessageBubble.setOnClickListener(view -> {
                setSelectedMessage(baseMessage.getId());
                notifyDataSetChanged();

            });
            viewHolder.itemView.setTag(R.string.message, baseMessage);
        }

    }

    private void setColorFilter(BaseMessage baseMessage, View view) {

        if (!longselectedItemList.contains(baseMessage)) {
            if (baseMessage.getSender().equals(CometChat.getLoggedInUser()))
                view.getBackground().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            else
                view.getBackground().setColorFilter(context.getResources().getColor(R.color.message_bubble_grey), PorterDuff.Mode.SRC_ATOP);
        } else {
            if (baseMessage.getSender().equals(CometChat.getLoggedInUser()))
                view.getBackground().setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
            else
                view.getBackground().setColorFilter(context.getResources().getColor(R.color.secondaryTextColor), PorterDuff.Mode.SRC_ATOP);
        }

    }

    private void setStickerData(StickerMessageViewHolder viewHolder, int i) {


        BaseMessage baseMessage = messageList.get(i);
        if (!baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
            if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                viewHolder.tvUser.setVisibility(View.GONE);
                viewHolder.ivUser.setVisibility(View.GONE);
            } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                if (isUserDetailVisible) {
                    viewHolder.tvUser.setVisibility(View.VISIBLE);
                    viewHolder.ivUser.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tvUser.setVisibility(View.GONE);
                    viewHolder.ivUser.setVisibility(View.INVISIBLE);
                }
                setAvatar(viewHolder.ivUser, baseMessage.getSender().getAvatar(), baseMessage.getSender().getName());
                viewHolder.tvUser.setText(baseMessage.getSender().getName());
            }
        }


        viewHolder.stickerView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_defaulf_image));
        try {
            Glide.with(context).load(((CustomMessage) baseMessage).getCustomData().getString("url")).into(viewHolder.stickerView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (baseMessage.getReplyCount() != 0 && UISettings.isEnableThreadedReplies()) {
            viewHolder.tvThreadReplyCount.setVisibility(View.VISIBLE);
            viewHolder.tvThreadReplyCount.setText(baseMessage.getReplyCount() + " Replies");
        } else {
            viewHolder.tvThreadReplyCount.setVisibility(View.GONE);
        }
        viewHolder.tvThreadReplyCount.setOnClickListener(view -> {
            Intent intent = new Intent(context, CometChatThreadMessageActivity.class);
//            intent.putExtra(StringContract.IntentStrings.PARENT_BASEMESSAGE,baseMessage.toString());
            intent.putExtra(StringContract.IntentStrings.NAME, baseMessage.getSender().getName());
            intent.putExtra(StringContract.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
            intent.putExtra(StringContract.IntentStrings.REPLY_COUNT, baseMessage.getReplyCount());
            intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getName());
            intent.putExtra(StringContract.IntentStrings.PARENT_ID, baseMessage.getId());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, baseMessage.getType());
            intent.putExtra(StringContract.IntentStrings.SENTAT, baseMessage.getSentAt());
            try {
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((CustomMessage) baseMessage).getCustomData().getString("name"));
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((CustomMessage) baseMessage).getCustomData().getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra(StringContract.IntentStrings.TYPE, baseMessage.getReceiverType());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_CATEGORY, baseMessage.getCategory());
            if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                intent.putExtra(StringContract.IntentStrings.GUID, baseMessage.getReceiverUid());
            } else {
                if (baseMessage.getReceiverUid().equals(loggedInUser.getUid()))
                    intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getUid());
                else
                    intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getReceiverUid());
            }
            context.startActivity(intent);
        });


        showMessageTime(viewHolder, baseMessage);
//        if (selectedItemList.contains(baseMessage.getId()))
        viewHolder.txtTime.setVisibility(View.VISIBLE);
//        else
//            viewHolder.txtTime.setVisibility(View.GONE);


        viewHolder.stickerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isLongClickEnabled && !isTextMessageClick) {
                    isImageMessageClick = true;
                    setLongClickSelectedItem(baseMessage);
                    messageLongClick.setLongMessageClick(longselectedItemList);
                    notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    private void setLinkData(LinkMessageViewHolder viewHolder, int i) {

        BaseMessage baseMessage = messageList.get(i);

        String url = null;

        if (baseMessage != null) {
            if (!baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    viewHolder.tvUser.setVisibility(View.GONE);
                    viewHolder.ivUser.setVisibility(View.GONE);
                } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                    if (isUserDetailVisible) {
                        viewHolder.tvUser.setVisibility(View.VISIBLE);
                        viewHolder.ivUser.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvUser.setVisibility(View.GONE);
                        viewHolder.ivUser.setVisibility(View.INVISIBLE);
                    }
                    setAvatar(viewHolder.ivUser, baseMessage.getSender().getAvatar(), baseMessage.getSender().getName());
                    viewHolder.tvUser.setText(baseMessage.getSender().getName());
                }
            }
            if (baseMessage.getDeletedAt() == 0) {
                HashMap<String, JSONObject> extensionList = Extensions.extensionCheck(baseMessage);
                if (extensionList != null) {
                    if (extensionList.containsKey("linkPreview")) {
                        JSONObject linkPreviewJsonObject = extensionList.get("linkPreview");
                        try {
                            String description = linkPreviewJsonObject.getString("description");
                            String image = linkPreviewJsonObject.getString("image");
                            String title = linkPreviewJsonObject.getString("title");
                            url = linkPreviewJsonObject.getString("url");
                            Log.e("setLinkData: ", baseMessage.toString() + "\n\n" + url + "\n" + description + "\n" + image);
                            viewHolder.linkTitle.setText(title);
                            viewHolder.linkSubtitle.setText(description);
                            Glide.with(context).load(Uri.parse(image)).timeout(1000).into(viewHolder.linkImg);
                            if (url.contains("youtu.be") || url.contains("youtube")) {
                                viewHolder.videoLink.setVisibility(View.VISIBLE);
                                viewHolder.linkVisit.setText(context.getResources().getString(R.string.view_on_youtube));
                            } else {
                                viewHolder.videoLink.setVisibility(View.GONE);
                                viewHolder.linkVisit.setText(context.getResources().getString(R.string.visit));
                            }
                            String messageStr = ((TextMessage) baseMessage).getText();
                            if (((TextMessage) baseMessage).getText().equals(url) || ((TextMessage) baseMessage).getText().equals(url + "/")) {
                                viewHolder.message.setVisibility(View.GONE);
                            } else {
                                viewHolder.message.setVisibility(View.VISIBLE);
                            }
                            viewHolder.message.setText(messageStr);
                        } catch (Exception e) {
                            Log.e("setLinkData: ", e.getMessage());
                        }
                    }
                }
            }

            if (baseMessage.getReplyCount() != 0) {
                try {
                    if (baseMessage.getMetadata() != null && baseMessage.getMetadata().has("replyAvatars")) {
                        JSONArray jsonArray = baseMessage.getMetadata().getJSONArray("replyAvatars");
                        viewHolder.lvReplyAvatar.setVisibility(View.VISIBLE);
                        for (int m = 0; m < jsonArray.length(); m++) {
                            if (viewHolder.lvReplyAvatar.getChildCount() < 4) {
                                Avatar avatarId = viewHolder.lvReplyAvatar.findViewById(m);
                                if (avatarId == null) {
                                    Avatar avatar = new Avatar(context.getApplicationContext());
                                    avatar.setId(m);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout
                                            .LayoutParams((int) Utils.dpToPx(context, 16), (int) Utils.dpToPx(context, 16));
                                    layoutParams.leftMargin = 8;
                                    layoutParams.rightMargin = 8;
                                    avatar.setLayoutParams(layoutParams);
                                    avatar.setAvatar(jsonArray.getString(m));
                                    viewHolder.lvReplyAvatar.addView(avatar, m);
                                }
                            }
                        }
                    } else {
                        viewHolder.lvReplyAvatar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "setImageData: " + e.getMessage());
                }
                viewHolder.tvThreadReplyCount.setVisibility(View.VISIBLE);
                viewHolder.tvThreadReplyCount.setText(baseMessage.getReplyCount() + " Replies");
            } else {
                viewHolder.lvReplyAvatar.setVisibility(View.GONE);
                viewHolder.tvThreadReplyCount.setVisibility(View.GONE);
            }
            viewHolder.tvThreadReplyCount.setOnClickListener(view -> {
                Intent intent = new Intent(context, CometChatThreadMessageActivity.class);
                intent.putExtra(StringContract.IntentStrings.NAME, baseMessage.getSender().getName());
                intent.putExtra(StringContract.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
                intent.putExtra(StringContract.IntentStrings.REPLY_COUNT, baseMessage.getReplyCount());
                intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getName());
                intent.putExtra(StringContract.IntentStrings.PARENT_ID, baseMessage.getId());
                intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE, baseMessage.getType());
                intent.putExtra(StringContract.IntentStrings.SENTAT, baseMessage.getSentAt());
                if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT))
                    intent.putExtra(StringContract.IntentStrings.TEXTMESSAGE, ((TextMessage) baseMessage).getText());
                else {
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((MediaMessage) baseMessage).getAttachment().getFileName());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, ((MediaMessage) baseMessage).getAttachment().getFileExtension());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((MediaMessage) baseMessage).getAttachment().getFileUrl());
                    intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, ((MediaMessage) baseMessage).getAttachment().getFileSize());
                }
                intent.putExtra(StringContract.IntentStrings.TYPE, baseMessage.getReceiverType());
                if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                    intent.putExtra(StringContract.IntentStrings.GUID, baseMessage.getReceiverUid());
                } else {
                    if (baseMessage.getReceiverUid().equals(loggedInUser.getUid()))
                        intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getSender().getUid());
                    else
                        intent.putExtra(StringContract.IntentStrings.UID, baseMessage.getReceiverUid());
                }
                context.startActivity(intent);
            });

            showMessageTime(viewHolder, baseMessage);
            String finalUrl = url;
            viewHolder.linkVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (finalUrl != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(finalUrl));
                        context.startActivity(intent);
                    }
                }
            });
            if (selectedItemList.contains(baseMessage.getId()))
                viewHolder.txtTime.setVisibility(View.VISIBLE);
            else
                viewHolder.txtTime.setVisibility(View.GONE);
//            if (i < selectedItems.length && selectedItems[i] == 0) {
//                viewHolder.txtTime.setVisibility(View.GONE);
//            } else
//                viewHolder.txtTime.setVisibility(View.VISIBLE);

            viewHolder.rlMessageBubble.setOnClickListener(view -> {
                if (isLongClickEnabled && !isImageMessageClick) {
                    setLongClickSelectedItem(baseMessage);
                    messageLongClick.setLongMessageClick(longselectedItemList);
                } else {
                    setSelectedMessage(baseMessage.getId());
                }
                notifyDataSetChanged();

            });
            viewHolder.rlMessageBubble.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!isImageMessageClick) {
                        isLongClickEnabled = true;
                        isTextMessageClick = true;
                        setLongClickSelectedItem(baseMessage);
                        messageLongClick.setLongMessageClick(longselectedItemList);
                        notifyDataSetChanged();
                    }
                    return true;
                }
            });
            viewHolder.itemView.setTag(R.string.message, baseMessage);
        }
    }

    public void setSelectedMessage(Integer id) {
        if (selectedItemList.contains(id))
            selectedItemList.remove(id);
        else
            selectedItemList.add(id);
    }

    public void setLongClickSelectedItem(BaseMessage baseMessage) {
        if (longselectedItemList.contains(baseMessage))
            longselectedItemList.remove(baseMessage);
        else
            longselectedItemList.add(baseMessage);
    }

    /**
     * This method is used to set avatar of groupMembers to show in groupMessages. If avatar of
     * group member is not available then it calls <code>setInitials(String name)</code> to show
     * first two letter of group member name.
     *
     * @param avatar    is a object of Avatar
     * @param avatarUrl is a String. It is url of avatar.
     * @param name      is a String. It is a name of groupMember.
     * @see Avatar
     */
    private void setAvatar(Avatar avatar, String avatarUrl, String name) {

        if (avatarUrl != null && !avatarUrl.isEmpty())
            Glide.with(context).load(avatarUrl).into(avatar);
        else
            avatar.setInitials(name);

    }

    public static String removeWords(String word, String remove) {
        return word.replace(remove, "");
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public long getHeaderId(int var1) {

        BaseMessage baseMessage = messageList.get(var1);
        return Long.parseLong(Utils.getDateId(baseMessage.getSentAt() * 1000));
    }

    @Override
    public DateItemHolder onCreateHeaderViewHolder(ViewGroup var1) {
        View view = LayoutInflater.from(var1.getContext()).inflate(R.layout.cc_message_list_header,
                var1, false);

        sp = context.getSharedPreferences("login", context.MODE_PRIVATE);
        currentLang = sp.getString("currentLang", currentLang);
        Log.e("currentLang", currentLang);

        return new DateItemHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(DateItemHolder var1, int var2, long var3) {
        BaseMessage baseMessage = messageList.get(var2);
        Date date = new Date(baseMessage.getSentAt() * 1000L);
        String formattedDate = Utils.getDate(date.getTime());
        var1.txtMessageDate.setBackground(context.getResources().getDrawable(R.drawable.cc_rounded_date_button));
        var1.txtMessageDate.setText(formattedDate);
    }

    /**
     * This method is used to maintain different viewType based on message category and type and
     * returns the different view types to adapter based on it.
     * <p>
     * Ex :- For message with category <b>CometChatConstants.CATEGORY_MESSAGE</b> and type
     * <b>CometChatConstants.MESSAGE_TYPE_TEXT</b> and message is sent by a <b>Logged-in user</b>,
     * It will return <b>RIGHT_TEXT_MESSAGE</b>
     *
     * @param position is a position of item in recyclerView.
     * @return It returns int which is value of view type of item.
     * @see MessageAdapter#onCreateViewHolder(ViewGroup, int)
     * @see BaseMessage
     */
    private int getItemViewTypes(int position) {
        BaseMessage baseMessage = messageList.get(position);
        HashMap<String, JSONObject> extensionList = Extensions.extensionCheck(baseMessage);
        if (baseMessage.getDeletedAt() == 0) {
            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_MESSAGE)) {
                switch (baseMessage.getType()) {

                    case CometChatConstants.MESSAGE_TYPE_TEXT:
                        if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                            if (extensionList != null && extensionList.containsKey("linkPreview") && extensionList.get("linkPreview") != null)
                                return RIGHT_LINK_MESSAGE;
                            else if (baseMessage.getMetadata() != null && baseMessage.getMetadata().has("reply"))
                                return RIGHT_REPLY_TEXT_MESSAGE;
                            else
                                return RIGHT_TEXT_MESSAGE;
                        } else {
                            if (extensionList != null && extensionList.containsKey("linkPreview") && extensionList.get("linkPreview") != null)
                                return LEFT_LINK_MESSAGE;
                            else if (baseMessage.getMetadata() != null && baseMessage.getMetadata().has("reply"))
                                return LEFT_REPLY_TEXT_MESSAGE;
                            else
                                return LEFT_TEXT_MESSAGE;
                        }
                    case CometChatConstants.MESSAGE_TYPE_AUDIO:
                        if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                            return RIGHT_AUDIO_MESSAGE;
                        } else {
                            return LEFT_AUDIO_MESSAGE;
                        }
                    case CometChatConstants.MESSAGE_TYPE_IMAGE:
                        if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                            return RIGHT_IMAGE_MESSAGE;
                        } else {
                            return LEFT_IMAGE_MESSAGE;
                        }
                    case CometChatConstants.MESSAGE_TYPE_VIDEO:
                        if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                            return RIGHT_VIDEO_MESSAGE;
                        } else {
                            return LEFT_VIDEO_MESSAGE;
                        }
                    case CometChatConstants.MESSAGE_TYPE_FILE:
                        if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                            return RIGHT_FILE_MESSAGE;
                        } else {
                            return LEFT_FILE_MESSAGE;
                        }
                    case CometChatConstants.MESSAGE_TYPE_CUSTOM:
                        if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                            return RIGHT_CUSTOM_MESSAGE;
                        } else {
                            return LEFT_CUSTOM_MESSAGE;
                        }
                    default:
                        return -1;
                }
            } else {
                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                    return ACTION_MESSAGE;
                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_CALL)) {
                    return CALL_MESSAGE;
                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_CUSTOM)) {
                    if (baseMessage.getSender().getUid().equals(loggedInUser.getUid()))
                        return RIGHT_CUSTOM_MESSAGE;
                    else if (baseMessage.getType().equalsIgnoreCase(StringContract.IntentStrings.STICKERS))
                        return RIGHT_STICKER_MESSAGE;
                    else
                        return LEFT_CUSTOM_MESSAGE;
                }
            }
        } else {
            if (baseMessage.getSender().getUid().equals(loggedInUser.getUid())) {
                return RIGHT_DELETE_MESSAGE;
            } else {
                return LEFT_DELETE_MESSAGE;
            }
        }
        return -1;

    }

    /**
     * This method is used to update message list of adapter.
     *
     * @param baseMessageList is list of baseMessages.
     */
    public void updateList(List<BaseMessage> baseMessageList) {
        setMessageList(baseMessageList);
    }

    /**
     * This method is used to set real time delivery receipt of particular message in messageList
     * of adapter by updating message.
     *
     * @param messageReceipt is a object of MessageReceipt.
     * @see MessageReceipt
     */
    public void setDeliveryReceipts(MessageReceipt messageReceipt) {

        for (int i = messageList.size() - 1; i >= 0; i--) {
            BaseMessage baseMessage = messageList.get(i);
            if (baseMessage.getDeliveredAt() == 0) {
                int index = messageList.indexOf(baseMessage);
                messageList.get(index).setDeliveredAt(messageReceipt.getDeliveredAt());
            }
        }
        notifyDataSetChanged();
    }

    /**
     * This method is used to set real time read receipt of particular message in messageList
     * of adapter by updating message.
     *
     * @param messageReceipt is a object of MessageReceipt.
     * @see MessageReceipt
     */
    public void setReadReceipts(MessageReceipt messageReceipt) {
        for (int i = messageList.size() - 1; i >= 0; i--) {
            BaseMessage baseMessage = messageList.get(i);
            if (baseMessage.getReadAt() == 0) {
                int index = messageList.indexOf(baseMessage);
                messageList.get(index).setReadAt(messageReceipt.getReadAt());
            }
        }

        notifyDataSetChanged();
    }

    /**
     * This method is used to add message in messageList when send by a user or when received in
     * real time.
     *
     * @param baseMessage is a object of BaseMessage. It is new message which will added.
     * @see BaseMessage
     */
    public void addMessage(BaseMessage baseMessage) {
//        if (!messageList.contains(baseMessage)) {
        messageList.add(baseMessage);
        selectedItemList.clear();
//        }
        notifyDataSetChanged();
    }

    /**
     * This method is used to update previous message with new message in messageList of adapter.
     *
     * @param baseMessage is a object of BaseMessage. It is new message which will be updated.
     */
    public void setUpdatedMessage(BaseMessage baseMessage) {

        JSONObject metadata = baseMessage.getMetadata();
        if (metadata != null) {
            try {
                JSONObject injectedObject = metadata.getJSONObject("@injected");
                if (injectedObject != null && injectedObject.has("extensions")) {
                    JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                    if (extensionsObject != null && extensionsObject.has("voice-transcription")) {
                        JSONObject transcriptionObject = extensionsObject.getJSONObject("voice-transcription");
                        Log.e("transcription", transcriptionObject.toString());
                    }
                }
            } catch (Exception e) {

            }

        }

        if (messageList.contains(baseMessage)) {
            int index = messageList.indexOf(baseMessage);
            messageList.remove(baseMessage);
            messageList.add(index, baseMessage);
            notifyItemChanged(index);
        }
    }

    public void resetList() {
        messageList.clear();
        notifyDataSetChanged();
    }

    public void clearLongClickSelectedItem() {
        isLongClickEnabled = false;
        isTextMessageClick = false;
        isImageMessageClick = false;
        longselectedItemList.clear();
        notifyDataSetChanged();
    }

    public BaseMessage getLastMessage() {
        if (messageList.size() > 0) {
            Log.e(TAG, "getLastMessage: " + messageList.get(messageList.size() - 1));
            return messageList.get(messageList.size() - 1);
        } else
            return null;
    }

    public int getPosition(BaseMessage baseMessage) {
        return messageList.indexOf(baseMessage);
    }

    class ImageMessageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private CardView cardView;
        private ProgressBar progressBar;
        private Avatar ivUser;
        public TextView txtTime, tvUser;
        private RelativeLayout rlMessageBubble;
        private TextView tvThreadReplyCount;
        private LinearLayout lvReplyAvatar;

        private RelativeLayout sensitiveLayout;

        public ImageMessageViewHolder(@NonNull View view) {
            super(view);
            int type = (int) view.getTag();
            imageView = view.findViewById(R.id.go_img_message);
            tvUser = view.findViewById(R.id.tv_user);
            cardView = view.findViewById(R.id.cv_image_message_container);
            progressBar = view.findViewById(R.id.img_progress_bar);
            txtTime = view.findViewById(R.id.txt_time);
            ivUser = view.findViewById(R.id.iv_user);
            rlMessageBubble = view.findViewById(R.id.rl_message);
            tvThreadReplyCount = view.findViewById(R.id.thread_reply_count);
            lvReplyAvatar = view.findViewById(R.id.reply_avatar_layout);
            sensitiveLayout = view.findViewById(R.id.sensitive_layout);
        }
    }

    public class ActionMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private TextView txtTime;

        public ActionMessageViewHolder(@NonNull View view) {
            super(view);
            int type = (int) view.getTag();
            textView = view.findViewById(R.id.go_txt_message);
            txtTime = view.findViewById(R.id.txt_time);
        }
    }

    class VideoMessageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ImageView playBtn;
        private CardView cardView;
        private ProgressBar progressBar;
        private Avatar ivUser;
        public TextView txtTime, tvUser;
        private RelativeLayout rlMessageBubble;
        private TextView tvThreadReplyCount;
        private LinearLayout lvReplyAvatar;

        public VideoMessageViewHolder(@NonNull View view) {
            super(view);
            int type = (int) view.getTag();
            imageView = view.findViewById(R.id.go_video_message);
            playBtn = view.findViewById(R.id.playBtn);
            tvUser = view.findViewById(R.id.tv_user);
            cardView = view.findViewById(R.id.cv_image_message_container);
            progressBar = view.findViewById(R.id.img_progress_bar);
            txtTime = view.findViewById(R.id.txt_time);
            ivUser = view.findViewById(R.id.iv_user);
            rlMessageBubble = view.findViewById(R.id.rl_message);
            tvThreadReplyCount = view.findViewById(R.id.thread_reply_count);
            lvReplyAvatar = view.findViewById(R.id.reply_avatar_layout);
        }
    }

    public class FileMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView fileName;
        private TextView fileExt;
        public TextView txtTime;
        private TextView fileSize;
        private TextView tvUser;
        private View view;
        private Avatar ivUser;
        private RelativeLayout rlMessageBubble;
        private TextView tvThreadReplyCount;
        private LinearLayout lvReplyAvatar;

        FileMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            fileSize = itemView.findViewById(R.id.tvFileSize);
            ivUser = itemView.findViewById(R.id.iv_user);
            tvUser = itemView.findViewById(R.id.tv_user);
            fileExt = itemView.findViewById(R.id.tvFileExtension);
            txtTime = itemView.findViewById(R.id.txt_time);
            fileName = itemView.findViewById(R.id.tvFileName);
            rlMessageBubble = itemView.findViewById(R.id.rl_message);
            tvThreadReplyCount = itemView.findViewById(R.id.thread_reply_count);
            lvReplyAvatar = itemView.findViewById(R.id.reply_avatar_layout);
            this.view = itemView;
        }
    }

    public class DeleteMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMessage;
        private RelativeLayout cardView;
        private View view;
        public TextView txtTime;
        public TextView tvUser;
        private ImageView imgStatus;
        private int type;
        private Avatar ivUser;
        private RelativeLayout rlMessageBubble;
        private TextView tvThreadReplyCount;
        private LinearLayout lvReplyAvatar;

        DeleteMessageViewHolder(@NonNull View view) {
            super(view);
            type = (int) view.getTag();
            tvUser = view.findViewById(R.id.tv_user);
            txtMessage = view.findViewById(R.id.go_txt_message);
            cardView = view.findViewById(R.id.cv_message_container);
            txtTime = view.findViewById(R.id.txt_time);
            imgStatus = view.findViewById(R.id.img_pending);
            ivUser = view.findViewById(R.id.iv_user);
            rlMessageBubble = view.findViewById(R.id.rl_message);
            tvThreadReplyCount = view.findViewById(R.id.thread_reply_count);
            lvReplyAvatar = view.findViewById(R.id.reply_avatar_layout);
            this.view = view;
        }
    }

    public class TextMessageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        private TextView txtMessage;
        private TextView tvThreadReplyCount;
        private RelativeLayout cardView;
        private View view;
        public TextView txtTime;
        public TextView tvUser;
        private ImageView imgStatus;
        private int type;
        private Avatar ivUser;
        private RelativeLayout rlMessageBubble;

        private RelativeLayout sentimentVw;

        private TextView viewSentimentMessage;
        private RelativeLayout replyLayout;
        private TextView replyUser;
        private TextView replyMessage;
        private LinearLayout lvReplyAvatar;

        TextMessageViewHolder(@NonNull View view) {
            super(view);

            type = (int) view.getTag();
            tvUser = view.findViewById(R.id.tv_user);
            imageView = view.findViewById(R.id.go_img_message);
            txtMessage = view.findViewById(R.id.go_txt_message);
            cardView = view.findViewById(R.id.cv_message_container);
            txtTime = view.findViewById(R.id.txt_time);
            imgStatus = view.findViewById(R.id.img_pending);
            ivUser = view.findViewById(R.id.iv_user);
            rlMessageBubble = view.findViewById(R.id.rl_message);
            replyLayout = view.findViewById(R.id.replyLayout);
            replyUser = view.findViewById(R.id.reply_user);
            replyMessage = view.findViewById(R.id.reply_message);
            tvThreadReplyCount = view.findViewById(R.id.thread_reply_count);
            lvReplyAvatar = view.findViewById(R.id.reply_avatar_layout);
            sentimentVw = view.findViewById(R.id.sentiment_layout);
            viewSentimentMessage = view.findViewById(R.id.view_sentiment);
            this.view = view;

        }
    }

    public class CustomMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMessage;
        private RelativeLayout cardView;
        private View view;
        public TextView txtTime;
        public TextView tvUser;
        private ImageView imgStatus;
        private int type;
        private Avatar ivUser;
        private RelativeLayout rlMessageBubble;


        CustomMessageViewHolder(@NonNull View view) {
            super(view);

            type = (int) view.getTag();
            tvUser = view.findViewById(R.id.tv_user);
            txtMessage = view.findViewById(R.id.go_txt_message);
            cardView = view.findViewById(R.id.cv_message_container);
            txtTime = view.findViewById(R.id.txt_time);
            imgStatus = view.findViewById(R.id.img_pending);
            ivUser = view.findViewById(R.id.iv_user);
            rlMessageBubble = view.findViewById(R.id.rl_message);
            this.view = view;
        }
    }

    public class AudioMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView length;
        private ImageView playBtn;
        private int type;
        private TextView tvUser;
        private Avatar ivUser;
        private RelativeLayout rlMessageBubble;
        private TextView txtTime;
        private TextView tvThreadReplyCount;
        private LinearLayout lvReplyAvatar;

        public AudioMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            type = (int) itemView.getTag();
            length = itemView.findViewById(R.id.audiolength_tv);
            playBtn = itemView.findViewById(R.id.playBtn);
            rlMessageBubble = itemView.findViewById(R.id.cv_message_container);
            tvUser = itemView.findViewById(R.id.tv_user);
            ivUser = itemView.findViewById(R.id.iv_user);
            txtTime = itemView.findViewById(R.id.txt_time);
            tvThreadReplyCount = itemView.findViewById(R.id.thread_reply_count);
            lvReplyAvatar = itemView.findViewById(R.id.reply_avatar_layout);
        }
    }

    public class LinkMessageViewHolder extends RecyclerView.ViewHolder {

        private TextView linkTitle;
        private TextView linkVisit;
        private TextView linkSubtitle;
        private TextView message;
        private ImageView videoLink;
        private RelativeLayout cardView;
        private View view;
        public TextView txtTime;
        private ImageView imgStatus;
        private ImageView linkImg;
        private int type;
        private TextView tvUser;
        private Avatar ivUser;
        private RelativeLayout rlMessageBubble;
        private TextView tvThreadReplyCount;
        private LinearLayout lvReplyAvatar;

        LinkMessageViewHolder(@NonNull View view) {
            super(view);

            type = (int) view.getTag();
            tvUser = view.findViewById(R.id.tv_user);
            linkTitle = view.findViewById(R.id.link_title);
            linkSubtitle = view.findViewById(R.id.link_subtitle);
            linkVisit = view.findViewById(R.id.visitLink);
            linkImg = view.findViewById(R.id.link_img);
            message = view.findViewById(R.id.message);
            videoLink = view.findViewById(R.id.videoLink);
            cardView = view.findViewById(R.id.cv_message_container);
            txtTime = view.findViewById(R.id.txt_time);
            imgStatus = view.findViewById(R.id.img_pending);
            ivUser = view.findViewById(R.id.iv_user);
            rlMessageBubble = view.findViewById(R.id.rl_message);
            tvThreadReplyCount = view.findViewById(R.id.thread_reply_count);
            lvReplyAvatar = view.findViewById(R.id.reply_avatar_layout);
            this.view = view;
        }
    }

    public class StickerMessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView stickerView;
        public TextView tvThreadReplyCount;
        public TextView txtTime;
        public TextView tvUser;
        public Avatar ivUser;

        public int type;
        public View view;

        public StickerMessageViewHolder(View itemView) {
            super(itemView);

            type = (int) itemView.getTag();
            stickerView = itemView.findViewById(R.id.sticker_view);
            tvUser = itemView.findViewById(R.id.tv_user);
            ivUser = itemView.findViewById(R.id.iv_user);
            tvThreadReplyCount = itemView.findViewById(R.id.thread_reply_count);
            txtTime = itemView.findViewById(R.id.txt_time);

            this.view = itemView;

        }
    }


    public class DateItemHolder extends RecyclerView.ViewHolder {
        TextView txtMessageDate;
        DateItemHolder(@NonNull View itemView) {
            super(itemView);
            txtMessageDate = itemView.findViewById(R.id.txt_message_date);
        }
    }

    public interface OnMessageLongClick {
        void setLongMessageClick(List<BaseMessage> baseMessage);
    }
}



