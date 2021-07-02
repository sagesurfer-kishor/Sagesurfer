package screen.threadconversation;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.Avatar;
import com.cometchat.pro.uikit.ComposeBox;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.SmartReplyList;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import adapter.ThreadAdapter;
import constant.StringContract;
import listeners.ComposeActionListener;
import listeners.MessageActionCloseListener;
import listeners.OnItemClickListener;
import listeners.OnMessageLongClick;
import listeners.StickyHeaderDecoration;
import screen.CometChatForwardMessageScreenActivity;
import screen.CometChatGroupDetailScreenActivity;
import screen.CometChatUserDetailScreenActivity;
import screen.MessageActionFragment;
import screen.messagelist.General;
import utils.Extensions;
import utils.FontUtils;
import utils.KeyBoardUtils;
import utils.MediaUtils;
import utils.Utils;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Purpose - CometChatMessageScreen class is a fragment used to display list of messages and perform certain action on click of message.
 * It also provide search bar to perform search operation on the list of messages. User can send text,images,video and file as messages
 * to each other and in groups. User can also perform actions like edit message,delete message and forward messages to other user and groups.
 *
 * @see CometChat
 * @see User
 * @see Group
 * @see TextMessage
 * @see MediaMessage
 * <p>
 * Created on - 20th December 2019
 * <p>
 * Modified on  - 16th January 2020
 */

public class CometChatThreadMessageScreen extends Fragment implements View.OnClickListener,
        OnMessageLongClick, MessageActionCloseListener {

    private static final String TAG = "CometChatThreadScreen";

    private static final int LIMIT = 30;

    private RelativeLayout bottomLayout;

    private String name = "";

    private String conversationName = "";

    private MessagesRequest messagesRequest;    //Used to fetch messages.

    private ComposeBox composeBox;
    private String team_logs_id = "";
    private MediaRecorder mediaRecorder;

    private MediaPlayer mediaPlayer;

    private String audioFileNameWithPath;

    private RecyclerView rvChatListView;    //Used to display list of messages.

    private ThreadAdapter messageAdapter;

    private LinearLayoutManager linearLayoutManager;

    private SmartReplyList rvSmartReply;

    private ShimmerFrameLayout messageShimmer;

    /**
     * <b>Avatar</b> is a UI Kit Component which is used to display user and group avatars.
     */
    private TextView tvName;

    private TextView tvTypingIndicator;

    private Avatar senderAvatar;

    private TextView senderName;

    private TextView tvSentAt;

    private String NextGroupOrUserId;

    private Context context;

    private LinearLayout blockUserLayout;

    private TextView blockedUserName;

    private StickyHeaderDecoration stickyHeaderDecoration;

    private String avatarUrl;

    private Toolbar toolbar;

    private boolean isBlockedByMe;

    private String loggedInUserScope;

    private RelativeLayout editMessageLayout;

    private TextView tvMessageTitle;

    private TextView tvMessageSubTitle;

    private RelativeLayout replyMessageLayout;

    private TextView replyTitle;

    private TextView replyMessage;

    private ImageView replyMedia;

    private ImageView replyClose;

    private BaseMessage baseMessage;

    private List<BaseMessage> baseMessages = new ArrayList<>();

    private List<BaseMessage> messageList = new ArrayList<>();

    private boolean isEdit;

    private boolean isReply;

    private Timer timer = new Timer();

    private Timer typingTimer = new Timer();

    private View view;

    private boolean isNoMoreMessages;

    private FontUtils fontUtils;

    private User loggedInUser = CometChat.getLoggedInUser();

    String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean isInProgress;

    private boolean isSmartReplyClicked;

    private RelativeLayout onGoingCallView;

    private TextView onGoingCallTxt;

    private ImageView onGoingCallClose;

    public int count = 0;

    private long messageSentAt;

    private String messageType;

    private String message;

    private String messageFileName;

    private int messageSize;

    private String messageMimeType;

    private String messageExtension;

    private int parentId;

    private String type, tabs;

    private String groupOwnerId;

    private TextView textMessage;

    private ImageView imageMessage;

    private VideoView videoMessage;

    private RelativeLayout fileMessage, message_layout;

    private TextView fileName;

    private TextView fileSize;

    private TextView fileExtension;

    private TextView sentAt;

    private String team_id;

    private int replyCount;

    private TextView tvReplyCount;

    SharedPreferences sp;

    private NestedScrollView nestedScrollView;

    private LinearLayout noReplyMessages;

    //
    // private ImageView ivForwardMessage;

    private boolean isParent = true;

    //private ImageView ivMoreOption;
    public CometChatThreadMessageScreen() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleArguments();
        if (getActivity() != null)
            fontUtils = FontUtils.getInstance(getActivity());
    }

    /**
     * This method is used to handle arguments passed to this fragment.
     */
    private void handleArguments() {
        if (getArguments() != null) {
            parentId = getArguments().getInt(StringContract.IntentStrings.PARENT_ID, 0);
            replyCount = getArguments().getInt(StringContract.IntentStrings.REPLY_COUNT, 0);
            type = getArguments().getString(StringContract.IntentStrings.TYPE);
            NextGroupOrUserId = getArguments().getString(StringContract.IntentStrings.ID);
            avatarUrl = getArguments().getString(StringContract.IntentStrings.AVATAR);
            name = getArguments().getString(StringContract.IntentStrings.NAME);
            conversationName = getArguments().getString(StringContract.IntentStrings.CONVERSATION_NAME);
            messageType = getArguments().getString(StringContract.IntentStrings.MESSAGE_TYPE);
            messageSentAt = getArguments().getLong(StringContract.IntentStrings.SENTAT);
            tabs = getArguments().getString(StringContract.IntentStrings.TABS);
            team_id = getArguments().getString(StringContract.IntentStrings.TEAM_ID);
            Log.i(TAG, "handleArguments: tabs " + tabs);
            if (messageType.equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                message = getArguments().getString(StringContract.IntentStrings.TEXTMESSAGE);
            } else {
                message = getArguments().getString(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL);
                messageFileName = getArguments().getString(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME);
                messageExtension = getArguments().getString(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION);
                messageSize = getArguments().getInt(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, 0);
                messageMimeType = getArguments().getString(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_MIME_TYPE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_thread_message, container, false);
        initViewComponent(view);
        return view;
    }


    /**
     * This is a main method which is used to initialize the view for this fragment.
     *
     * @param view
     */
    private void initViewComponent(View view) {
        setHasOptionsMenu(true);
        nestedScrollView = view.findViewById(R.id.nested_scrollview);
        noReplyMessages = view.findViewById(R.id.no_reply_layout);
        //ivMoreOption = view.findViewById(R.id.ic_more_option);
        //ivMoreOption.setOnClickListener(this);
        // ivForwardMessage = view.findViewById(R.id.ic_forward_option);
        //ivForwardMessage.setOnClickListener(this);
        sp = context.getSharedPreferences("login", MODE_PRIVATE);
        textMessage = view.findViewById(R.id.tv_textMessage);
        message_layout = view.findViewById(R.id.message_layout);
        textMessage.setText(message);
        imageMessage = view.findViewById(R.id.iv_imageMessage);
        videoMessage = view.findViewById(R.id.vv_videoMessage);
        fileMessage = view.findViewById(R.id.rl_fileMessage);
        fileName = view.findViewById(R.id.tvFileName);
        fileSize = view.findViewById(R.id.tvFileSize);
        fileExtension = view.findViewById(R.id.tvFileExtension);

        Glide.with(context).load(message).into(imageMessage);
        MediaController mediacontroller = new MediaController(getContext());
        mediacontroller.setAnchorView(videoMessage);
        videoMessage.setMediaController(mediacontroller);
        videoMessage.setVideoURI(Uri.parse(message));

        if (messageFileName != null)
            fileName.setText(messageFileName);
        if (messageExtension != null)
            fileExtension.setText(messageExtension);

        fileSize.setText(Utils.getFileSize(messageSize));
        message_layout.setVisibility(VISIBLE);
        if (messageType.equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
            textMessage.setVisibility(GONE);
            imageMessage.setVisibility(View.VISIBLE);
            videoMessage.setVisibility(GONE);
            fileMessage.setVisibility(GONE);
        } else if (messageType.equals(CometChatConstants.MESSAGE_TYPE_VIDEO)) {
            videoMessage.setVisibility(VISIBLE);
            imageMessage.setVisibility(GONE);
            textMessage.setVisibility(GONE);
            fileMessage.setVisibility(GONE);
        } else if (messageType.equals(CometChatConstants.MESSAGE_TYPE_FILE) ||
                messageType.equals(CometChatConstants.MESSAGE_TYPE_AUDIO)) {
            fileMessage.setVisibility(VISIBLE);
            imageMessage.setVisibility(GONE);
            videoMessage.setVisibility(GONE);
            textMessage.setVisibility(GONE);
        } else if (messageType.equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
            videoMessage.setVisibility(GONE);
            fileMessage.setVisibility(GONE);
            imageMessage.setVisibility(GONE);
            textMessage.setVisibility(View.VISIBLE);
        }
        bottomLayout = view.findViewById(R.id.bottom_layout);
        composeBox = view.findViewById(R.id.message_box);
        messageShimmer = view.findViewById(R.id.shimmer_layout);
        composeBox = view.findViewById(R.id.message_box);
        composeBox.ivMic.setVisibility(GONE);
        composeBox.ivSend.setVisibility(VISIBLE);
        composeBox.ic_whiteboard.setVisibility(GONE);
        setComposeBoxListener();

        rvSmartReply = view.findViewById(R.id.rv_smartReply);
        editMessageLayout = view.findViewById(R.id.editMessageLayout);
        tvMessageTitle = view.findViewById(R.id.tv_message_layout_title);
        tvMessageSubTitle = view.findViewById(R.id.tv_message_layout_subtitle);
        ImageView ivMessageClose = view.findViewById(R.id.iv_message_close);
        ivMessageClose.setOnClickListener(this);

        replyMessageLayout = view.findViewById(R.id.replyMessageLayout);
        replyTitle = view.findViewById(R.id.tv_reply_layout_title);
        replyMessage = view.findViewById(R.id.tv_reply_layout_subtitle);
        replyMedia = view.findViewById(R.id.iv_reply_media);
        replyClose = view.findViewById(R.id.iv_reply_close);
        replyClose.setOnClickListener(this);

        senderAvatar = view.findViewById(R.id.av_sender);
        setAvatar();
        senderName = view.findViewById(R.id.tv_sender_name);
        senderName.setText(name);
        sentAt = view.findViewById(R.id.tv_message_time);
        sentAt.setText(String.format(getString(R.string.sentAtTxt), Utils.getMessageDate(messageSentAt)));
        tvReplyCount = view.findViewById(R.id.thread_reply_count);
        rvChatListView = view.findViewById(R.id.rv_message_list);
        if (replyCount > 0) {
            noReplyMessages.setVisibility(GONE);
            if (replyCount == 1)
                tvReplyCount.setText(replyCount + " Reply");
            else
                tvReplyCount.setText(replyCount + " Replies");
        } else {
            noReplyMessages.setVisibility(VISIBLE);
            tvReplyCount.setText("No reply");
        }

        MaterialButton unblockUserBtn = view.findViewById(R.id.btn_unblock_user);
        unblockUserBtn.setOnClickListener(this);
        blockedUserName = view.findViewById(R.id.tv_blocked_user_name);
        blockUserLayout = view.findViewById(R.id.blocked_user_layout);
        tvName = view.findViewById(R.id.tv_name);
        tvTypingIndicator = view.findViewById(R.id.tv_typing);
        toolbar = view.findViewById(R.id.chatList_toolbar);
        toolbar.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        tvName.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        tvName.setText(String.format(getString(R.string.thread_in_name), conversationName));
        setAvatar();
        rvChatListView.setLayoutManager(linearLayoutManager);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Utils.isDarkMode(context)) {
            //ivMoreOption.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            // ivForwardMessage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            bottomLayout.setBackgroundColor(getResources().getColor(R.color.darkModeBackground));
            toolbar.setBackgroundColor(getResources().getColor(R.color.grey));
            editMessageLayout.setBackground(getResources().getDrawable(R.drawable.left_border_dark));
            replyMessageLayout.setBackground(getResources().getDrawable(R.drawable.left_border_dark));
            composeBox.setBackgroundColor(getResources().getColor(R.color.darkModeBackground));
            rvChatListView.setBackgroundColor(getResources().getColor(R.color.darkModeBackground));
            tvName.setTextColor(getResources().getColor(R.color.textColorWhite));
        } else {
            //ivMoreOption.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryTextColor)));
            //ivForwardMessage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryTextColor)));
            bottomLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            editMessageLayout.setBackground(getResources().getDrawable(R.drawable.left_border));
            replyMessageLayout.setBackground(getResources().getDrawable(R.drawable.left_border));
            composeBox.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
            rvChatListView.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
            tvName.setTextColor(getResources().getColor(R.color.textColorWhite));

        }

        KeyBoardUtils.setKeyboardVisibilityListener(getActivity(), (View) rvChatListView.getParent(), keyboardVisible -> {
            if (keyboardVisible) {
                scrollToBottom();
            }
        });


        // Uses to fetch next list of messages if rvChatListView (RecyclerView) is scrolled in downward direction.
        rvChatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                //for toolbar elevation animation i.e stateListAnimator
                toolbar.setSelected(rvChatListView.canScrollVertically(-1));
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (!isNoMoreMessages && !isInProgress) {
                    if (linearLayoutManager.findFirstVisibleItemPosition() == 10 || !rvChatListView.canScrollVertically(-1)) {
                        isInProgress = true;
                        fetchMessage();
                    }
                }
            }

        });
        rvSmartReply.setItemClickListener(new OnItemClickListener<String>() {
            @Override
            public void OnItemClick(String var, int position) {
                if (!isSmartReplyClicked) {
                    isSmartReplyClicked = true;
                    rvSmartReply.setVisibility(GONE);
                    sendMessage(var);
                }
            }
        });

        //Check Ongoing Call
        onGoingCallView = view.findViewById(R.id.ongoing_call_view);
        onGoingCallClose = view.findViewById(R.id.close_ongoing_view);
        onGoingCallTxt = view.findViewById(R.id.ongoing_call);
        checkOnGoingCall();
    }

    private void checkOnGoingCall() {
        if (CometChat.getActiveCall() != null && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING) && CometChat.getActiveCall().getSessionId() != null) {
            if (onGoingCallView != null)
                onGoingCallView.setVisibility(View.VISIBLE);
            if (onGoingCallTxt != null) {
                onGoingCallTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGoingCallView.setVisibility(View.GONE);
                        Utils.joinOnGoingCall(getContext());
                    }
                });
            }
            if (onGoingCallClose != null) {
                onGoingCallClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGoingCallView.setVisibility(GONE);
                    }
                });
            }
        } else if (CometChat.getActiveCall() != null) {
            if (onGoingCallView != null)
                onGoingCallView.setVisibility(GONE);
            Log.e(TAG, "checkOnGoingCall: " + CometChat.getActiveCall().toString());
        }
    }

    private void setComposeBoxListener() {
        composeBox.setComposeBoxListener(new ComposeActionListener() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    sendTypingIndicator(false);
                } else {
                    sendTypingIndicator(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (typingTimer == null) {
                    typingTimer = new Timer();
                }
                endTypingTimer();
            }

            @Override
            public void onAudioActionClicked(ImageView audioIcon) {
                if (Utils.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startActivityForResult(MediaUtils.openAudio(getActivity()), StringContract.RequestCode.AUDIO);
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.AUDIO);
                }
            }

            @Override
            public void onCameraActionClicked(ImageView cameraIcon) {
                if (Utils.hasPermissions(getContext(), CAMERA_PERMISSION)) {
                    startActivityForResult(MediaUtils.openCamera(getContext()), StringContract.RequestCode.CAMERA);
                } else {
                    requestPermissions(CAMERA_PERMISSION, StringContract.RequestCode.CAMERA);
                }
            }


            @Override
            public void onGalleryActionClicked(ImageView galleryIcon) {
                if (Utils.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startActivityForResult(MediaUtils.openGallery(getActivity()), StringContract.RequestCode.GALLERY);
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.GALLERY);
                }
            }

            @Override
            public void onFileActionClicked(ImageView fileIcon) {
                if (Utils.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startActivityForResult(MediaUtils.getFileIntent(StringContract.IntentStrings.EXTRA_MIME_DOC), StringContract.RequestCode.FILE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StringContract.RequestCode.FILE);
                }
            }

            @Override
            public void onSendActionClicked(EditText editText) {
                String message = editText.getText().toString().trim();
                editText.setText("");
                editText.setHint(getString(R.string.message));
                if (isEdit) {
                    if (isParent)
                        editThread(message);
                    else {
                        editMessage(baseMessage, message);
                    }
                    editMessageLayout.setVisibility(GONE);
                } else if (isReply) {
                    replyMessage(baseMessage, message);
                    replyMessageLayout.setVisibility(GONE);
                } else if (!message.isEmpty()) {
                    Log.i(TAG, "onSendActionClicked: message clicked");
                    sendMessage(message);
                }
            }

            @Override
            public void onVoiceNoteComplete(String string) {
                if (string != null) {
                    File audioFile = new File(string);
                    sendMediaMessage(audioFile, CometChatConstants.MESSAGE_TYPE_AUDIO);
                }
            }
        });
    }

    private void editThread(String editMessage) {
        isEdit = false;

        TextMessage textmessage;
        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            textmessage = new TextMessage(NextGroupOrUserId, editMessage, CometChatConstants.RECEIVER_TYPE_USER);
        else
            textmessage = new TextMessage(NextGroupOrUserId, editMessage, CometChatConstants.RECEIVER_TYPE_GROUP);
        sendTypingIndicator(true);
        textmessage.setId(parentId);
        CometChat.editMessage(textmessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage baseMessage) {
                Log.i(TAG, "onSuccess: editThread response " + baseMessage);
                textMessage.setText(((TextMessage) baseMessage).getText());
                message = ((TextMessage) baseMessage).getText();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            case StringContract.RequestCode.CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    startActivityForResult(MediaUtils.openCamera(getActivity()), StringContract.RequestCode.CAMERA);
                else
                    showSnackBar(view.findViewById(R.id.message_box), getResources().getString(R.string.grant_camera_permission));
                break;
            case StringContract.RequestCode.GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startActivityForResult(MediaUtils.openGallery(getActivity()), StringContract.RequestCode.GALLERY);
                else
                    showSnackBar(view.findViewById(R.id.message_box), getResources().getString(R.string.grant_storage_permission));
                break;
            case StringContract.RequestCode.FILE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startActivityForResult(MediaUtils.getFileIntent(StringContract.IntentStrings.EXTRA_MIME_DOC), StringContract.RequestCode.FILE);
                else
                    showSnackBar(view.findViewById(R.id.message_box), getResources().getString(R.string.grant_storage_permission));
                break;
        }
    }

    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Incase if user is blocked already, then this method is used to unblock the user .
     *
     * @see CometChat#unblockUsers(List, CometChat.CallbackListener)
     */
    private void unblockUser() {
        ArrayList<String> uids = new ArrayList<>();
        uids.add(NextGroupOrUserId);
        CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                Snackbar.make(rvChatListView, String.format(getResources().getString(R.string.user_unblocked), name), Snackbar.LENGTH_LONG).show();
                blockUserLayout.setVisibility(GONE);
                isBlockedByMe = false;
                messagesRequest = null;
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This method is used to fetch message of users & groups. For user it fetches previous 100 messages at
     * a time and for groups it fetches previous 30 messages. You can change limit of messages by modifying
     * number in <code>setLimit()</code>
     * This method also mark last message as read using markMessageAsRead() present in this class.
     * So all the above messages get marked as read.
     *
     * @see MessagesRequest#fetchPrevious(CometChat.CallbackListener)
     */
    private void fetchMessage() {
        if (messagesRequest == null) {
            messagesRequest = new MessagesRequest.MessagesRequestBuilder().setLimit(LIMIT).setParentMessageId(parentId).hideMessagesFromBlockedUsers(true).build();
        }
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                isInProgress = false;
                List<BaseMessage> filteredMessageList = filterBaseMessages(baseMessages);
                /*Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {*/
                        initMessageAdapter(filteredMessageList);
                        if (baseMessages.size() != 0) {
                            stopHideShimmer();
                            BaseMessage baseMessage = baseMessages.get(baseMessages.size() - 1);
                            markMessageAsRead(baseMessage);
                        }
                        if (baseMessages.size() == 0) {
                            stopHideShimmer();
                            isNoMoreMessages = true;
                        }
                    }
               /* }, 2000);
            }*/

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void stopHideShimmer() {
        messageShimmer.stopShimmer();
        messageShimmer.setVisibility(GONE);
    }


    public List<BaseMessage> filterBaseMessages(List<BaseMessage> baseMessages) {
        List<BaseMessage> tempList = new ArrayList<>();
        int editAction = 0;
        for (BaseMessage baseMessage : baseMessages) {
            JSONObject metadata = baseMessage.getMetadata();
            Log.e("base message => ", baseMessage.toString());
            switch (tabs) {
                // Frirends chat
                case "1":
                    Log.e("tab 1 friend", baseMessage.toString());
                    if (baseMessage.getCategory().equals("custom")) {
                        int team_log;
                        Log.e(TAG, General.MY_TAG + " filterBaseMessages: metadata " + baseMessage.getMetadata());
                        try {
                            team_log = metadata.getInt("team_logs_id");
                            Log.e(TAG, General.MY_TAG + " filterBaseMessages: team_log_id " + metadata.getInt("team_logs_id"));
                            if (team_log == 0) {
                                if (baseMessage.getType().equals("extension_whiteboard")) {
                                    try {
                                        if (baseMessage.getMetadata().has("deleted_one_to_one")) {
                                            if (baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("0")) {
                                                baseMessage.setDeletedAt(12345);
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            Log.i(TAG, General.MY_TAG + " filterBaseMessages: type = extension_whiteboard condition");
                                            String s = null;
                                            s = baseMessage.getMetadata().getString("whiteboard_URL_one");
                                            Log.i(TAG, General.MY_TAG + " filterBaseMessages: whiteboard_URL_one" + s);
                                            if (!s.isEmpty()) {
                                                Log.e(TAG, General.MY_TAG + " filterBaseMessages2: " + baseMessage.toString());
                                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                    Action action = ((Action) baseMessage);
                                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    } else {
                                                        Log.i(TAG, "filterBaseMessages:  " + General.MY_TAG + " delete else ");
                                                        tempList.add(baseMessage);
                                                    }
                                                } else {
                                                    Log.i(TAG, "filterBaseMessages:  " + General.MY_TAG + " action else ");
                                                    tempList.add(baseMessage);
                                                }
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if ((baseMessage.getType().equals("stickers"))) {
                                    String s = null;
                                    try {
                                        s = baseMessage.getMetadata().getString("stickers");
                                        if (!s.isEmpty()) {
                                            Log.e(TAG, "filterBaseMessages: " + baseMessage.toString());
                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //implementation 'com.cometchat:pro-android-chat-sdk:2.3.1'
                    }
                    else if (baseMessage.getCategory().equals("message")) {
                        String team_log;
//                        Log.i(TAG, "filterBaseMessages: Reply at checking " + baseMessage.getMetadata() + " message " + ((TextMessage) baseMessage).getText());
                        if (metadata != null || baseMessage.getDeletedAt() != 0) {
                            try {
                                if (metadata != null) {
                                    team_log = metadata.getString("team_logs_id");
                                } else {
                                    team_log = "0";
                                }
                                if (team_log.equals("0") || baseMessage.getDeletedAt() != 0) {
                                    if (baseMessage.getType().equals("text")) {
                                        if (metadata != null && baseMessage.getEditedAt() != 0) {
                                            /*Here we are marking message as deleted so that user can check that it is deleted */
                                            //Log.i(TAG, "filterBaseMessages: edited at block 0" + ((TextMessage) baseMessage).getText());
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    /*setting delated at with some value to understand this is deleted message.. this value is just random value...*/
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            } else {
                                                //                                             Log.i(TAG, "filterBaseMessages: edited at block 1");
                                                try {
                                                    long id = baseMessage.getId();
                                                    String textMessage = ((TextMessage) baseMessage).getText();
                                                    String currentLang = sp.getString("currentLang", null);
                                                    JSONObject body = new JSONObject();
                                                    JSONArray languages = new JSONArray();
                                                    languages.put(currentLang);
                                                    body.put("msgId", id);
                                                    body.put("languages", languages);
                                                    body.put("text", textMessage);
                                                    Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                    // Do something after 5s = 5000ms
                                                    CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                            new CometChat.CallbackListener<JSONObject>() {
                                                                @Override
                                                                public void onSuccess(JSONObject jsonObject) {
                                                                    try {
                                                                        Log.i(TAG, "filterBaseMessages: edited at block 3");
                                                                        JSONObject meta = baseMessage.getMetadata();
                                                                        meta.accumulate("values", jsonObject);
                                                                        String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                        // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                        //baseMessage.setMetadata(meta);

                                                                        ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                        baseMessage.setMetadata(meta);
                                                                        messageAdapter.setUpdatedMessage(baseMessage);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onError(CometChatException e) {
                                                                    // Some error occured
                                                                    Log.i(TAG, "onError: " + e.getMessage());
                                                                    Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                                }
                                                            });


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    tempList.add(baseMessage);
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                //                                          Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                tempList.add(baseMessage);
                                            }
                                        } else if (metadata != null && metadata.has("type")) {
                                            //this is for replied message
                                            if (metadata.has("message") && metadata.getString("type").equalsIgnoreCase("reply")) {
                                                Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                                                JSONObject body = new JSONObject();
                                                JSONArray languages = new JSONArray();
                                                languages.put(sp.getString("currentLang", "en"));
                                                body.put("msgId", baseMessage.getId());
                                                body.put("languages", languages);
                                                body.put("text", metadata.getString("message"));
                                                Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                // Do something after 5s = 5000ms
                                                CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                        new CometChat.CallbackListener<JSONObject>() {
                                                            @Override
                                                            public void onSuccess(JSONObject jsonObject) {
                                                                try {
                                                                    // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                                                    JSONObject meta = baseMessage.getMetadata();
                                                                    //meta.accumulate("values", jsonObject);
                                                                    String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                                                    Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                                    metadata.remove("message");
                                                                    metadata.put("message", messageTranslatedString);
                                                                    messageAdapter.setUpdatedMessage(baseMessage);
                                                                    Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                                                    // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                    //baseMessage.setMetadata(meta);
                                                                    // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                    //baseMessage.setMetadata(meta);


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(CometChatException e) {
                                                                // Some error occured
                                                                Log.i(TAG, "onError: " + e.getMessage());
                                                                Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                            }
                                                        });
                                                try {
                                                    if (metadata.has("@injected")) {
                                                        JSONObject injectedObject = null;
                                                        injectedObject = metadata.getJSONObject("@injected");
                                                        if (injectedObject.has("extensions")) {
                                                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                            if (extensionsObject.has("message-translation")) {
                                                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                                HashMap<String, String> translationsMap = new HashMap<String, String>();
                                                                for (int i = 0; i < translations.length(); i++) {
                                                                    JSONObject translation = translations.getJSONObject(i);
                                                                    String translatedText = translation.getString("message_translated");
                                                                    String translatedLanguage = translation.getString("language_translated");
                                                                    translationsMap.put(translatedLanguage, translatedText);
                                                                    Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                                ((TextMessage) baseMessage).setText(translatedText);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                    }
                                                                }
                                                            }

                                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                                Action action = ((Action) baseMessage);
                                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                    tempList.add(baseMessage);
                                                                } else {
                                                                    tempList.add(baseMessage);
                                                                }
                                                            } else {
                                                                tempList.add(baseMessage);
                                                            }
                                                        }
                                                    } else {
                                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                            Action action = ((Action) baseMessage);
                                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                tempList.add(baseMessage);
                                                            } else {
                                                                tempList.add(baseMessage);
                                                            }
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        } else if (metadata != null || baseMessage.getReadAt() != 0) {
                                            /*This code is use to check if the text message is reply message from ios or normal message
                                             * if it is replied msg and send from ios then we change metadata to display the text message
                                             * code is written by rahul maske
                                             */

                                                /*Handler handler=new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {*/
                                            try {
                                                if (metadata.has("@injected")) {
                                                    JSONObject injectedObject = null;
                                                    injectedObject = metadata.getJSONObject("@injected");
                                                    if (injectedObject.has("extensions")) {
                                                        JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                        if (extensionsObject.has("message-translation")) {
                                                            JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                            JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                            HashMap<String, String> translationsMap = new HashMap<String, String>();
                                                            for (int i = 0; i < translations.length(); i++) {
                                                                JSONObject translation = translations.getJSONObject(i);
                                                                String translatedText = translation.getString("message_translated");
                                                                String translatedLanguage = translation.getString("language_translated");
                                                                translationsMap.put(translatedLanguage, translatedText);
                                                                Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                                                if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                    if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                        if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                            ((TextMessage) baseMessage).setText(translatedText);
                                                                        }
                                                                    }
                                                                } else {
                                                                    ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                }
                                                            }
                                                        }

                                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                            Action action = ((Action) baseMessage);
                                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                tempList.add(baseMessage);
                                                            } else {
                                                                tempList.add(baseMessage);
                                                            }
                                                        } else {

                                                            tempList.add(baseMessage);
                                                        }
                                                    }
                                                } else {
                                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                        Action action = ((Action) baseMessage);
                                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                            tempList.add(baseMessage);
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                                   /* }
                                                },1000);*/

                                            /*else {
                                                try {
                                                    if (metadata.has("@injected")) {
                                                        JSONObject injectedObject = null;
                                                        injectedObject = metadata.getJSONObject("@injected");
                                                        if (injectedObject.has("extensions")) {
                                                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                            if (extensionsObject.has("message-translation")) {
                                                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                                HashMap<String, String> translationsMap = new HashMap<String, String>();
                                                                for (int i = 0; i < translations.length(); i++) {
                                                                    JSONObject translation = translations.getJSONObject(i);
                                                                    String translatedText = translation.getString("message_translated");
                                                                    String translatedLanguage = translation.getString("language_translated");
                                                                    translationsMap.put(translatedLanguage, translatedText);
                                                                    Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                                ((TextMessage) baseMessage).setText(translatedText);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                    }

                                                                }
                                                            }

                                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                                Action action = ((Action) baseMessage);
                                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                    tempList.add(baseMessage);
                                                                } else {
                                                                    tempList.add(baseMessage);
                                                                }
                                                            } else {

                                                                tempList.add(baseMessage);
                                                            }
                                                        }
                                                    } else {
                                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                            Action action = ((Action) baseMessage);
                                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                                tempList.add(baseMessage);
                                                            } else {
                                                                tempList.add(baseMessage);
                                                            }
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }*/

                                        } else {
                                            Log.e(TAG, "filterBaseMessages: 1.3");
                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    tempList.add(baseMessage);
                                                } else {
                                                    tempList.add(baseMessage);

                                                }
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        }

                                    } else if (baseMessage.getType().equals("image")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            }
                                        }
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {

                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("file")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            }
                                        }
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {

                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("audio")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            }
                                        }
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("video")) {
                                        if (baseMessage.getEditedAt() != 0) {
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("0")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            }
                                        }
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i(TAG, "filterBaseMessages: message id " + baseMessage.getId());
                        }
                    }
                    else if (baseMessage.getCategory().equals("call")) {
                        int team_log;
                        try {
                            // team_log = metadata.getInt("team_logs_id");
                            // team_log = metadata.getInt("team_logs_id");
                            team_log = metadata.getInt("team_logs_id");
                            if (team_log == 0) {
                                if (baseMessage.getType().equals("audio")) {
                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                        Action action = ((Action) baseMessage);
                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                        } else {
                                            Log.i(TAG, "filterBaseMessages: friend audio call log1");
                                            //Log.i(TAG, "filterBaseMessages: friend audio call log3 "+baseMessage);

                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else if (baseMessage.getType().equals("video")) {
                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                        Action action = ((Action) baseMessage);
                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                        } else {
                                            Log.i(TAG, "filterBaseMessages: friend video call log1");
                                          /*  Log.i(TAG, "filterBaseMessages: call BaseMessage" + ((Action) baseMessage).getAction());
                                            Log.i(TAG, "filterBaseMessages: call BaseMessage" + baseMessage.getSentAt());
                                            Log.i(TAG, "filterBaseMessages: call BaseMessage" + baseMessage.getDeletedAt());
                                            Log.i(TAG, "filterBaseMessages: call BaseMessage" + ((Action) baseMessage).getMessage());*/
                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        /*Log.i(TAG, "filterBaseMessages: call BaseMessage" + ((Action) baseMessage).getAction());
                                        Log.i(TAG, "filterBaseMessages: call BaseMessage" + baseMessage.getSentAt());
                                        Log.i(TAG, "filterBaseMessages: call BaseMessage" + baseMessage.getDeletedAt());
                                        Log.i(TAG, "filterBaseMessages: call BaseMessage" + ((Action) baseMessage).getMessage());*/
                                        Log.i(TAG, "filterBaseMessages: friend video call log2");
                                        tempList.add(baseMessage);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    else {
                        Log.e(TAG, "filterBaseMessages: " + baseMessage.toString());
                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                            Action action = ((Action) baseMessage);
                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            tempList.add(baseMessage);
                        }
                    }
                    break;

                // group chat
                case "2":
                    Log.i(TAG, "filterBaseMessages: case Groups");
                    Log.e("tab 2 group ", baseMessage.toString());
                    if (baseMessage.getCategory().equals("custom")) {
                        if (baseMessage.getType().equals("extension_whiteboard")) {
                            String s = null;
                            try {
                                if (baseMessage.getDeletedAt() != 0) {
                                    tempList.add(baseMessage);
                                } else {
                                    s = baseMessage.getMetadata().getString("whiteboard_URL_group");
                                    if (!s.isEmpty()) {
                                        Log.i(TAG, "filterBaseMessages: whiteboard custom url " + s);
                                        Log.e(TAG, "filterBaseMessages: 2.1" + baseMessage.toString());
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (baseMessage.getCategory().equals("message")) {
                        if (baseMessage.getType().equals("text")) {
                            /*This code is use to check if the text message is reply message from ios or normal message
                             * if it is replied msg and send from ios then we change metadata to display the text message
                             * code is written by rahul maske */
                            if (baseMessage.getDeletedAt() != 0) {
                                Log.i(TAG, "filterBaseMessages block check: deleted message..1");
                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else if (metadata != null && baseMessage.getEditedAt() != 0) {
                                Log.i(TAG, "filterBaseMessages block check: edited message..");
                                Log.i(TAG, "filterBaseMessages: edit and deleted message message");
                                /*Here we are marking message as deleted so that user can check that it is deleted */
                                try {
                                    Log.i(TAG, "filterBaseMessages: edited at block 0" + ((TextMessage) baseMessage).getText());
                                    if (metadata.has("deleted_one_to_one")) {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } else {
                                        /*this is normal edited message functionality */
                                        Log.i(TAG, "filterBaseMessages: edited at block 1");
                                        long id = baseMessage.getId();
                                        String textMessage = ((TextMessage) baseMessage).getText();
                                        String currentLang = sp.getString("currentLang", null);
                                        JSONObject body = new JSONObject();
                                        JSONArray languages = new JSONArray();
                                        languages.put(currentLang);
                                        body.put("msgId", id);
                                        body.put("languages", languages);
                                        body.put("text", textMessage);
                                        Log.i(TAG, "filterBaseMessages: edited at block 2");
                                        // Do something after 5s = 5000ms
                                        CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                new CometChat.CallbackListener<JSONObject>() {
                                                    @Override
                                                    public void onSuccess(JSONObject jsonObject) {
                                                        try {
                                                            Log.i(TAG, "filterBaseMessages: edited at block 3");
                                                            JSONObject meta = baseMessage.getMetadata();
                                                            meta.accumulate("values", jsonObject);
                                                            String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                            // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                            //baseMessage.setMetadata(meta);

                                                            ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                            baseMessage.setMetadata(meta);
                                                            messageAdapter.setUpdatedMessage(baseMessage);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(CometChatException e) {
                                                        // Some error occured
                                                        Log.i(TAG, "onError: " + e.getMessage());
                                                        Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                    }
                                                });
                                    }

                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                        Action action = ((Action) baseMessage);
                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            tempList.add(baseMessage);
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        Log.i(TAG, "filterBaseMessages: edited at block 5");
                                        tempList.add(baseMessage);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (metadata != null && metadata.has("type")) {
                                Log.i(TAG, "filterBaseMessages block check: replied message..");
                                if (metadata.has("type")) {
                                    try {
                                        if (metadata.has("message")) {
                                            Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                                            JSONObject body = new JSONObject();
                                            JSONArray languages = new JSONArray();
                                            languages.put(sp.getString("currentLang", "en"));
                                            body.put("languages", languages);
                                            body.put("text", metadata.getString("message"));
                                            Log.i(TAG, "filterBaseMessages: edited at block 2");
                                            // Do something after 5s = 5000ms
                                            CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                    new CometChat.CallbackListener<JSONObject>() {
                                                        @Override
                                                        public void onSuccess(JSONObject jsonObject) {
                                                            try {
                                                                // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                                                JSONObject meta = baseMessage.getMetadata();

                                                                //meta.accumulate("values", jsonObject);
                                                                String messageTranslatedString = null;

                                                                messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                                metadata.remove("message");
                                                                metadata.put("message", messageTranslatedString);
                                                                messageAdapter.setUpdatedMessage(baseMessage);
                                                                Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                                                // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                //baseMessage.setMetadata(meta);
                                                                // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                //baseMessage.setMetadata(meta);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }

                                                        @Override
                                                        public void onError(CometChatException e) {
                                                            // Some error occured
                                                            Log.i(TAG, "onError: " + e.getMessage());
                                                            Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                        }
                                                    });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        JSONObject injectedObject = metadata.getJSONObject("@injected");
                                        if (injectedObject.has("extensions")) {
                                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                            if (extensionsObject.has("message-translation")) {
                                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                for (int i = 0; i < translations.length(); i++) {
                                                    JSONObject translation = translations.getJSONObject(i);
                                                    String translatedText = translation.getString("message_translated");
                                                    String translatedLanguage = translation.getString("language_translated");
                                                    translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 3 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                ((TextMessage) baseMessage).setText(translatedText);
                                                            }
                                                        }
                                                    } else {
                                                        ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                    }
                                                }
                                            }
                                        }

                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                tempList.add(baseMessage);
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            Log.i(TAG, "filterBaseMessages: edited at block 5");
                                            tempList.add(baseMessage);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            } else {
                                //normal message
                                Log.i(TAG, "filterBaseMessages block check: normal message..");
                                try {
                                    if (metadata.has("@injected")) {
                                        JSONObject injectedObject = null;
                                        injectedObject = metadata.getJSONObject("@injected");
                                        if (injectedObject.has("extensions")) {
                                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                            if (extensionsObject.has("message-translation")) {
                                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                HashMap<String, String> translationsMap = new HashMap<String, String>();
                                                for (int i = 0; i < translations.length(); i++) {
                                                    JSONObject translation = translations.getJSONObject(i);
                                                    String translatedText = translation.getString("message_translated");
                                                    String translatedLanguage = translation.getString("language_translated");
                                                    translationsMap.put(translatedLanguage, translatedText);
                                                    Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                ((TextMessage) baseMessage).setText(translatedText);
                                                            }
                                                        }
                                                    } else {
                                                        ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                    }
                                                }
                                            }

                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    tempList.add(baseMessage);
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {

                                                tempList.add(baseMessage);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (baseMessage.getType().equals("image")) {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }

                        } else if (baseMessage.getType().equals("file")) {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else if (baseMessage.getType().equals("audio")) {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }

                        } else if (baseMessage.getType().equals("video")) {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            if (baseMessage.getEditedAt() != 0) {
                                if (metadata.has("deleted_one_to_one")) {
                                    try {
                                        if (metadata.getString("deleted_one_to_one").equals("0")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                Action action = ((Action) baseMessage);
                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                tempList.add(baseMessage);
                            }

                        }

                    } else {
                        Log.e(TAG, "filterBaseMessages: 2.3" + baseMessage.toString());
                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                            Action action = ((Action) baseMessage);

                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            tempList.add(baseMessage);
                        }
                    }
                    break;
                // myteam chat
                case "3":
                    Log.i(TAG, "filterBaseMessages: case my team");
                    Log.e("tab 3 myteam", baseMessage.toString());
                    if (baseMessage.getCategory().equals("custom")) {
                        String team_log;
                        try {
                            team_log = metadata.getString("team_logs_id");
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs case 3", String.valueOf(ids.length));

                            if (ids.length == 3 || ids.length == 4) {

                                String[] arrayTeamId = team_log.split("_-");
                                String part2 = arrayTeamId[1]; // 034556 //team id fetching

                                if (team_id.equals(part2)) {
                                    if (baseMessage.getType().equals("extension_whiteboard")) {
                                        String s = null;
                                        try {
                                            if (baseMessage.getMetadata().has("deleted_one_to_one")) {
                                                if (baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("1")
                                                        || baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("2")) {
                                                    baseMessage.setDeletedAt(12345);
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                s = baseMessage.getMetadata().getString("whiteboard_URL");
                                                if (!s.isEmpty()) {
                                                    Log.e(TAG, "filterBaseMessages:3.1 " + baseMessage.toString());
                                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                        Action action = ((Action) baseMessage);
                                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (baseMessage.getCategory().equals("message")) {
                        String team_log;
                        try {
                            if (baseMessage.getDeletedAt() != 0) {
                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            }
                            team_log = metadata.getString("team_logs_id");
                            String underscoreSplittedArray[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs case 3", String.valueOf(underscoreSplittedArray.length));
                            /*if (underscoreSplittedArray.length == 3 || underscoreSplittedArray.length == 4) {
                                String[] underscoreDashSplittedArray = team_log.split("_-");
                                String part2 = underscoreDashSplittedArray[1];
                                if (team_id.equals(part2)) {*/
                            if (baseMessage.getType().equals("text")) {
                                if (metadata != null && baseMessage.getEditedAt() != 0) {
                                    /*Here we are marking message as deleted so that user can check that it is deleted */
                                    try {
                                        Log.i(TAG, "filterBaseMessages: edited at block 0" + ((TextMessage) baseMessage).getText());
                                        if (metadata.has("deleted_one_to_one")) {
                                            if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                baseMessage.setDeletedAt(12345);
                                            }
                                        } else {
                                            Log.i(TAG, "filterBaseMessages: edited at block 1");

                                            long id = baseMessage.getId();
                                            String textMessage = ((TextMessage) baseMessage).getText();
                                            String currentLang = sp.getString("currentLang", null);
                                            JSONObject body = new JSONObject();
                                            JSONArray languages = new JSONArray();
                                            languages.put(currentLang);
                                            body.put("msgId", id);
                                            body.put("languages", languages);
                                            body.put("text", textMessage);
                                            Log.i(TAG, "filterBaseMessages: edited at block 2");
                                            // Do something after 5s = 5000ms
                                            CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                    new CometChat.CallbackListener<JSONObject>() {
                                                        @Override
                                                        public void onSuccess(JSONObject jsonObject) {
                                                            try {
                                                                Log.i(TAG, "filterBaseMessages: edited at block 3");
                                                                JSONObject meta = baseMessage.getMetadata();
                                                                meta.accumulate("values", jsonObject);
                                                                String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                //baseMessage.setMetadata(meta);

                                                                ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                baseMessage.setMetadata(meta);
                                                                messageAdapter.setUpdatedMessage(baseMessage);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(CometChatException e) {
                                                            // Some error occured
                                                            Log.i(TAG, "onError: " + e.getMessage());
                                                            Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                        }
                                                    });
                                        }

                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                tempList.add(baseMessage);
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            Log.i(TAG, "filterBaseMessages: edited at block 5");
                                            tempList.add(baseMessage);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (metadata != null && metadata.has("type")) {
                                    if (metadata.has("type")) {
                                        try {
                                            if (metadata.has("message")) {
                                                Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                                                JSONObject body = new JSONObject();
                                                JSONArray languages = new JSONArray();
                                                languages.put(sp.getString("currentLang", "en"));
                                                body.put("languages", languages);
                                                body.put("text", metadata.getString("message"));
                                                Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                // Do something after 5s = 5000ms
                                                CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                        new CometChat.CallbackListener<JSONObject>() {
                                                            @Override
                                                            public void onSuccess(JSONObject jsonObject) {
                                                                try {
                                                                    // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                                                    JSONObject meta = baseMessage.getMetadata();

                                                                    //meta.accumulate("values", jsonObject);
                                                                    String messageTranslatedString = null;

                                                                    messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                    Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                                    metadata.remove("message");
                                                                    metadata.put("message", messageTranslatedString);
                                                                    messageAdapter.setUpdatedMessage(baseMessage);
                                                                    Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                                                    // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                    //baseMessage.setMetadata(meta);
                                                                    // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                    //baseMessage.setMetadata(meta);
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                            @Override
                                                            public void onError(CometChatException e) {
                                                                // Some error occured
                                                                Log.i(TAG, "onError: " + e.getMessage());
                                                                Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                            }
                                                        });
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            JSONObject injectedObject = metadata.getJSONObject("@injected");
                                            if (injectedObject.has("extensions")) {
                                                JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                if (extensionsObject.has("message-translation")) {
                                                    JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                    JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                    HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                    for (int i = 0; i < translations.length(); i++) {
                                                        JSONObject translation = translations.getJSONObject(i);
                                                        String translatedText = translation.getString("message_translated");
                                                        String translatedLanguage = translation.getString("language_translated");
                                                        translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 3 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                        if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                            if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                    ((TextMessage) baseMessage).setText(translatedText);
                                                                }
                                                            }
                                                        } else {
                                                            ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                        }
                                                    }
                                                }
                                            }

                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    tempList.add(baseMessage);
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                tempList.add(baseMessage);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }
                                } else {
                                    try {
                                        JSONObject injectedObject = metadata.getJSONObject("@injected");
                                        if (injectedObject.has("extensions")) {
                                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                            if (extensionsObject.has("message-translation")) {
                                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                for (int i = 0; i < translations.length(); i++) {
                                                    JSONObject translation = translations.getJSONObject(i);
                                                    String translatedText = translation.getString("message_translated");
                                                    String translatedLanguage = translation.getString("language_translated");
                                                    translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 3 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                ((TextMessage) baseMessage).setText(translatedText);
                                                            }
                                                        }
                                                    } else {
                                                        ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                    }
                                                }
                                            }
                                        }

                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                //tempList.add(baseMessage);
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            Log.i(TAG, "filterBaseMessages: edited at block 5");
                                            tempList.add(baseMessage);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (baseMessage.getType().equals("image")) {
                                if (baseMessage.getEditedAt() != 0) {
                                    if (metadata.has("deleted_one_to_one")) {
                                        try {
                                            if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                baseMessage.setDeletedAt(12345);
                                                tempList.add(baseMessage);

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }

                            } else if (baseMessage.getType().equals("file")) {
                                if (baseMessage.getEditedAt() != 0) {
                                    if (metadata.has("deleted_one_to_one")) {
                                        try {
                                            if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                baseMessage.setDeletedAt(12345);
                                                tempList.add(baseMessage);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else if (baseMessage.getType().equals("audio")) {
                                if (baseMessage.getEditedAt() != 0) {
                                    if (metadata.has("deleted_one_to_one")) {
                                        try {
                                            if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                baseMessage.setDeletedAt(12345);
                                                tempList.add(baseMessage);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else if (baseMessage.getType().equals("video")) {
                                if (baseMessage.getEditedAt() != 0) {
                                    if (metadata.has("deleted_one_to_one")) {
                                        try {
                                            if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                baseMessage.setDeletedAt(12345);
                                                tempList.add(baseMessage);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                if (baseMessage.getEditedAt() != 0) {
                                    if (metadata.has("deleted_one_to_one")) {
                                        try {
                                            if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                baseMessage.setDeletedAt(12345);
                                                tempList.add(baseMessage);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }

                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (baseMessage.getCategory().equals("call")) {
                        String team_log;
                        try {
                            team_log = metadata.getString("team_logs_id");
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs", String.valueOf(ids.length));

                            if (ids.length == 3 || ids.length == 4) {

                                String[] parts = team_log.split("_-");
                                String part2 = parts[1]; // 034556

                                if (team_id.equals(part2)) {

                                    if (baseMessage.getType().equals("audio")) {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("video")) {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "filterBaseMessages: 3.3 else" + baseMessage.toString());
                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                            Action action = ((Action) baseMessage);
                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            tempList.add(baseMessage);
                        }
                    }
                    break;

                // join team
                case "4":
                    Log.i(TAG, "filterBaseMessages: case join team " + baseMessage.getCategory());
                    if (baseMessage.getCategory().equals("custom")) {
                        String team_log;
                        try {
                            team_log = metadata.getString("team_logs_id");
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs", String.valueOf(ids.length));
                            if (ids.length == 4 || ids.length == 3) {

                                String[] parts = team_log.split("_-");
                                String part2 = parts[1]; // 034556

                                if (team_id.equals(part2)) {
                                    Log.i(TAG, "filterBaseMessages: message for join team  " + baseMessage);
                                    if (baseMessage.getType().equals("extension_whiteboard")) {
                                        String s = null;
                                        try {
                                            if (baseMessage.getMetadata().has("deleted_one_to_one")) {
                                                if (baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("2")
                                                        || baseMessage.getMetadata().getString("deleted_one_to_one").equalsIgnoreCase("1")) {
                                                    baseMessage.setDeletedAt(12345);
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                s = baseMessage.getMetadata().getString("whiteboard_URL");
                                                if (!s.isEmpty()) {
                                                    Log.e(TAG, "filterBaseMessages: 4.1" + baseMessage.toString());
                                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                        Action action = ((Action) baseMessage);
                                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (baseMessage.getCategory().equals("message")) {
                        Log.i(TAG, "filterBaseMessages: category is Message");
                        String team_log;
                        try {
                            if (baseMessage.getDeletedAt() != 0) {
                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            }
                            team_log = metadata.getString("team_logs_id");
                            Log.i(TAG, "filterBaseMessages: team_log_ids " + team_log);
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("Team tabs", "" + String.valueOf(ids.length));
                            Log.e("Team team_logs_id", team_log);
                            /* if (ids.length == 4 || ids.length == 3) {*/

                            String[] parts = team_log.split("_-");
                            String part2 = parts[1]; // 034556
                            Log.i("Teams parts2", " " + part2);
                            Log.i("Teams team_Ids", " " + team_id);

                            /*if (team_id.equals(part2)) {*/
                            Log.i(TAG, "filterBaseMessages: message for join team  " + baseMessage);
                            if (baseMessage.getType().equals("text")) {
                                if (metadata != null) {
                                    /*This code is use to check if the text message is reply message from ios or normal message
                                     * if it is replied msg and send from ios then we change metadata to display the text message
                                     * code is written by rahul maske 27-03-2021*/
                                    /* if (metadata.has("type")) {
                                     */
                                            /*try {
                                                    JSONObject customMetadata = new JSONObject();
                                                    JSONObject reply = new JSONObject();
                                                    reply.put("name", baseMessage.getSender().getName());
                                                    reply.put("type", baseMessage.getType());
                                                    reply.put("avatar", baseMessage.getSender().getAvatar());
                                                    reply.put("message", baseMessage.getMetadata().getString("message"));
                                                    customMetadata.put("reply", reply);
                                                    customMetadata.put("readByMeAt", baseMessage.getReadAt());
                                                    customMetadata.put("deliveredToMeAt", baseMessage.getDeliveredToMeAt());
                                                    customMetadata.put("deletedAt", baseMessage.getDeletedAt());
                                                    customMetadata.put("editedAt", baseMessage.getEditedAt());
                                                    customMetadata.put("deletedBy", baseMessage.getDeletedBy());
                                                    customMetadata.put("editedBy", baseMessage.getEditedBy());
                                                    customMetadata.put("updatedAt", baseMessage.getUpdatedAt());
                                                    //readByMeAt=0, deliveredToMeAt=0, deletedAt=0, editedAt=0, deletedBy='null',
                                                    // editedBy='null', updatedAt=1616823540
                                                    baseMessage.setMetadata(customMetadata);
                                                    tempList.add(baseMessage);
                                                    Log.i(TAG, "filterBaseMessages: iosReply name" + baseMessage.getSender().getName());
                                                    Log.i(TAG, "filterBaseMessages: iosReply type " + baseMessage.getType());
                                                    Log.i(TAG, "filterBaseMessages: iosReply avtar " + baseMessage.getSender().getAvatar());
                                                    Log.i(TAG, "filterBaseMessages: iosReply message " + baseMessage.getMetadata().getString("message"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }*/
                                            /*
                                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                    Action action = ((Action) baseMessage);
                                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                        tempList.add(baseMessage);
                                                    } else {
                                                        tempList.add(baseMessage);
                                                    }
                                                } else {
                                                    Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                    tempList.add(baseMessage);
                                                }
                                            }*/

                                    if (metadata != null && baseMessage.getEditedAt() != 0) {
                                        /*Here we are marking message as deleted so that user can check that it is deleted */
                                        try {
                                            Log.i(TAG, "filterBaseMessages: edited at block 0" + ((TextMessage) baseMessage).getText());
                                            if (metadata.has("deleted_one_to_one")) {
                                                if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                                    baseMessage.setDeletedAt(12345);
                                                }
                                            } else {
                                                Log.i(TAG, "filterBaseMessages: edited at block 1");

                                                long id = baseMessage.getId();
                                                String textMessage = ((TextMessage) baseMessage).getText();
                                                String currentLang = sp.getString("currentLang", null);
                                                JSONObject body = new JSONObject();
                                                JSONArray languages = new JSONArray();
                                                languages.put(currentLang);
                                                body.put("languages", languages);
                                                body.put("text", textMessage);
                                                Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                // Do something after 5s = 5000ms
                                                CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                        new CometChat.CallbackListener<JSONObject>() {
                                                            @Override
                                                            public void onSuccess(JSONObject jsonObject) {
                                                                try {
                                                                    Log.i(TAG, "filterBaseMessages: edited at block 3");
                                                                    JSONObject meta = baseMessage.getMetadata();
                                                                    meta.accumulate("values", jsonObject);
                                                                    String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                    // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                    //baseMessage.setMetadata(meta);

                                                                    ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                    baseMessage.setMetadata(meta);
                                                                    messageAdapter.setUpdatedMessage(baseMessage);
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(CometChatException e) {
                                                                // Some error occured
                                                                Log.i(TAG, "onError: " + e.getMessage());
                                                                Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                            }
                                                        });

                                            }
                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                    tempList.add(baseMessage);
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                tempList.add(baseMessage);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if (metadata != null && metadata.has("type")) {
                                        if (metadata.has("type")) {
                                            try {
                                                if (metadata.has("message")) {
                                                    Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                                                    JSONObject body = new JSONObject();
                                                    JSONArray languages = new JSONArray();
                                                    languages.put(sp.getString("currentLang", "en"));
                                                    body.put("languages", languages);
                                                    body.put("text", metadata.getString("message"));
                                                    Log.i(TAG, "filterBaseMessages: edited at block 2");
                                                    // Do something after 5s = 5000ms
                                                    CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                                            new CometChat.CallbackListener<JSONObject>() {
                                                                @Override
                                                                public void onSuccess(JSONObject jsonObject) {
                                                                    try {
                                                                        // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                                                        JSONObject meta = baseMessage.getMetadata();

                                                                        //meta.accumulate("values", jsonObject);
                                                                        String messageTranslatedString = null;

                                                                        messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");

                                                                        Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                                                        metadata.remove("message");
                                                                        metadata.put("message", messageTranslatedString);
                                                                        messageAdapter.setUpdatedMessage(baseMessage);
                                                                        Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                                                        // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                                                        //baseMessage.setMetadata(meta);
                                                                        // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                                                        //baseMessage.setMetadata(meta);

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }

                                                                @Override
                                                                public void onError(CometChatException e) {
                                                                    // Some error occured
                                                                    Log.i(TAG, "onError: " + e.getMessage());
                                                                    Log.i(TAG, "filterBaseMessages: edited at block 4");
                                                                }
                                                            });
                                                }

                                                try {
                                                    JSONObject injectedObject = metadata.getJSONObject("@injected");
                                                    if (injectedObject.has("extensions")) {
                                                        JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                        if (extensionsObject.has("message-translation")) {
                                                            JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                            JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                            HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                            for (int i = 0; i < translations.length(); i++) {
                                                                JSONObject translation = translations.getJSONObject(i);
                                                                String translatedText = translation.getString("message_translated");
                                                                String translatedLanguage = translation.getString("language_translated");
                                                                translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 3 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                                if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                    if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                        if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                            ((TextMessage) baseMessage).setText(translatedText);
                                                                        }
                                                                    }
                                                                } else {
                                                                    ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                        Action action = ((Action) baseMessage);
                                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                            tempList.add(baseMessage);
                                                        } else {
                                                            tempList.add(baseMessage);
                                                        }
                                                    } else {
                                                        Log.i(TAG, "filterBaseMessages: edited at block 5");
                                                        tempList.add(baseMessage);
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {

                                        Log.i(TAG, "filterBaseMessages: normal message...");
                                        try {
                                            JSONObject injectedObject = metadata.getJSONObject("@injected");
                                            if (injectedObject.has("extensions")) {
                                                JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                if (extensionsObject.has("message-translation")) {
                                                    JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                    JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                    HashMap<String, String> translationsMap = new HashMap<String, String>();

                                                    for (int i = 0; i < translations.length(); i++) {
                                                        JSONObject translation = translations.getJSONObject(i);
                                                        String translatedText = translation.getString("message_translated");
                                                        String translatedLanguage = translation.getString("language_translated");
                                                        translationsMap.put(translatedLanguage, translatedText);
                                                                /*if (translatedLanguage.equals(currentLang)) {
                                                                    if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage).setText(translatedText);
                                                                        Log.i(TAG, "filterBaseMessages: case 4 translated text " + translatedText);
                                                                    }
                                                                }*/
                                                        if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                            if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                    ((TextMessage) baseMessage).setText(translatedText);
                                                                }
                                                            }
                                                        } else {
                                                            ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Log.e(TAG, "filterBaseMessages: 4.2" + baseMessage.toString());
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }
                                           /* Log.e(TAG, "filterBaseMessages: 4.2" + baseMessage.toString());
                                            if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                                Action action = ((Action) baseMessage);
                                                if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                        action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                                } else {
                                                    tempList.add(baseMessage);
                                                }
                                            } else {
                                                tempList.add(baseMessage);
                                            }*/
                                } else {
                                    Log.e(TAG, "filterBaseMessages: 3.2" + baseMessage.toString());
                                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                        Action action = ((Action) baseMessage);
                                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                }
                            } else if (baseMessage.getType().equals("image")) {
                                if (baseMessage.getEditedAt() != 0) {
                                    if (metadata.has("deleted_one_to_one")) {
                                        if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                        tempList.add(baseMessage);
                                    }
                                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }

                            } else if (baseMessage.getType().equals("file")) {
                                if (baseMessage.getEditedAt() != 0) {
                                    if (metadata.has("deleted_one_to_one")) {
                                        if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                        tempList.add(baseMessage);
                                    }
                                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else if (baseMessage.getType().equals("audio")) {
                                if (baseMessage.getEditedAt() != 0) {
                                    if (metadata.has("deleted_one_to_one")) {
                                        if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                        tempList.add(baseMessage);
                                    }
                                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else if (baseMessage.getType().equals("video")) {
                                if (baseMessage.getEditedAt() != 0) {
                                    if (metadata.has("deleted_one_to_one")) {
                                        if (metadata.getString("deleted_one_to_one").equals("1") || metadata.getString("deleted_one_to_one").equals("2")) {
                                            baseMessage.setDeletedAt(12345);
                                        }
                                        tempList.add(baseMessage);
                                    }
                                } else if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            } else {
                                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                    Action action = ((Action) baseMessage);
                                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                    } else {
                                        tempList.add(baseMessage);
                                    }
                                } else {
                                    tempList.add(baseMessage);
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (baseMessage.getCategory().equals("call")) {
                        String team_log;
                        try {
                            team_log = metadata.getString("team_logs_id");
                            String ids[] = team_log.split(Pattern.quote("_"));
                            Log.e("tabs", String.valueOf(ids.length));
                            if (ids.length == 4 || ids.length == 3) {

                                String[] parts = team_log.split("_-");
                                String part2 = parts[1]; // 034556

                                if (team_id.equals(part2)) {
                                    if (baseMessage.getType().equals("audio")) {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    } else if (baseMessage.getType().equals("video")) {
                                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                                            Action action = ((Action) baseMessage);
                                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                                            } else {
                                                tempList.add(baseMessage);
                                            }
                                        } else {
                                            tempList.add(baseMessage);
                                        }
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e(TAG, "filterBaseMessages: 4.3" + baseMessage.toString());
                        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                            Action action = ((Action) baseMessage);
                            if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                                    action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            } else {
                                tempList.add(baseMessage);
                            }
                        } else {
                            tempList.add(baseMessage);
                        }
                    }
                    break;
            }
        }
        tempList.size();
        return tempList;
    }

/*this is previous filter bas message method now commenting in this code and we are making changes
* commented and updated by rahul maske on 02-07-2021*/

    /*private List<BaseMessage> filterBaseMessages(List<BaseMessage> baseMessages) {
        List<BaseMessage> tempList = new ArrayList<>();
        for (BaseMessage baseMessage : baseMessages) {
            Log.e(TAG, "filterBaseMessages: is " + baseMessage);
            if (baseMessage.getDeletedAt() != 0) {
                Log.i(TAG, "filterBaseMessages: delete block");
                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                    Action action = ((Action) baseMessage);
                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                    } else {
                        tempList.add(baseMessage);
                    }
                } else {
                    tempList.add(baseMessage);
                }
            } else if (baseMessage.getMetadata() != null && baseMessage.getEditedAt() != 0) {
                Log.i(TAG, "filterBaseMessages: edit block");
                if (baseMessage.getMetadata().has("deleted_one_to_one")) {
                    try {
                        if (baseMessage.getMetadata().getString("deleted_one_to_one").equals("0")) {
                            *//*setting delated at with some value to understand this is deleted message.. this value is just random value...*//*
                            baseMessage.setDeletedAt(12345);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Log.i(TAG, "filterBaseMessages: edited at block 1");
                    try {
                        long id = baseMessage.getId();
                        String textMessage = ((TextMessage) baseMessage).getText();
                        Log.i(TAG, "filterBaseMessages: edited text message is " + ((TextMessage) baseMessage).getText() + " id "+id);
                        String currentLang = sp.getString("currentLang", "en");
                        JSONObject body = new JSONObject();
                        JSONArray languages = new JSONArray();
                        languages.put(currentLang);
                        body.put("msgId", id);
                        body.put("languages", languages);
                        body.put("text", textMessage);
                        Log.i(TAG, "filterBaseMessages: edited at block languages " + languages);
                        // Do something after 5s = 5000ms
                        CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                new CometChat.CallbackListener<JSONObject>() {
                                    @Override
                                    public void onSuccess(JSONObject jsonObject) {
                                        try {
                                            Log.i(TAG, "onSuccess: edited at block");
                                            JSONObject meta = baseMessage.getMetadata();
                                            meta.accumulate("values", jsonObject);
                                            String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                            Log.i(TAG, "filterBaseMessages: edited at block 3" + messageTranslatedString);
                                            BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                            //baseMessage.setMetadata(meta);
                                            Log.i(TAG, "filterBaseMessages: edited at block 3");
                                            // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                            //baseMessage.setMetadata(meta);

                                            ((TextMessage) baseMessage1).setText(messageTranslatedString);
                                            baseMessage1.setMetadata(meta);
                                            messageAdapter.setUpdatedMessage(baseMessage1);

                                            *//*try {
                                                JSONObject injectedObject = null;
                                                injectedObject = baseMessage1.getMetadata().getJSONObject("@injected");
                                                if (injectedObject.has("extensions")) {
                                                    JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                                                    if (extensionsObject.has("message-translation")) {
                                                        JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                                        JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                                        HashMap<String, String> translationsMap = new HashMap<String, String>();
                                                        for (int i = 0; i < translations.length(); i++) {
                                                            JSONObject translation = translations.getJSONObject(i);
                                                            String translatedText = translation.getString("message_translated");
                                                            String translatedLanguage = translation.getString("language_translated");
                                                            translationsMap.put(translatedLanguage, translatedText);
                                                            Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                                            if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                                                if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                                                    if (baseMessage1.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                                        ((TextMessage) baseMessage1).setText(translatedText);
                                                                    }
                                                                }
                                                            } else {
                                                                ((TextMessage) baseMessage1).setText(((TextMessage) baseMessage1).getText());
                                                            }
                                                        }
                                                    }

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }*//*


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(CometChatException e) {
                                        // Some error occured
                                        Log.i(TAG, "onError: " + e.getMessage());
                                        Log.i(TAG, "filterBaseMessages: edited at block 4");
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                        Action action = ((Action) baseMessage);
                        if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                            tempList.add(baseMessage);
                        } else {
                            tempList.add(baseMessage);
                        }
                    } else {
                        tempList.add(baseMessage);
                    }
                }
            } else if (baseMessage.getMetadata().has("@injected")) {
                Log.i(TAG, "filterBaseMessages: normal message block1");
                try {
                    JSONObject injectedObject = null;
                    injectedObject = baseMessage.getMetadata().getJSONObject("@injected");
                    Log.i(TAG, "filterBaseMessages: normal message block2");
                    if (injectedObject.has("extensions")) {
                        Log.i(TAG, "filterBaseMessages: normal message block3");
                        JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                        if (extensionsObject.has("message-translation")) {
                            Log.i(TAG, "filterBaseMessages: normal message block4");
                            JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                            JSONArray translations = messageTranslationObject.getJSONArray("translations");
                            HashMap<String, String> translationsMap = new HashMap<String, String>();
                            for (int i = 0; i < translations.length(); i++) {
                                JSONObject translation = translations.getJSONObject(i);
                                String translatedText = translation.getString("message_translated");
                                String translatedLanguage = translation.getString("language_translated");
                                translationsMap.put(translatedLanguage, translatedText);
                                Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));

                                if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                    if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                        if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                            ((TextMessage) baseMessage).setText(translatedText);
                                        }
                                    }
                                } else {
                                    ((TextMessage) baseMessage).setText(((TextMessage) baseMessage).getText());
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                    Action action = ((Action) baseMessage);
                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) || action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                        tempList.add(baseMessage);
                    } else {
                        tempList.add(baseMessage);
                    }
                } else {
                    tempList.add(baseMessage);
                }
            } else {
                Log.i(TAG, "filterBaseMessages: else block ");
                if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {
                    Action action = ((Action) baseMessage);
                    if (action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_DELETED) ||
                            action.getAction().equals(CometChatConstants.ActionKeys.ACTION_MESSAGE_EDITED)) {
                    } else {
                        tempList.add(baseMessage);
                    }
                } else {
                    tempList.add(baseMessage);
                }
            }
        }
        return tempList;
    }*/

    private void getSmartReplyList(BaseMessage baseMessage) {
        HashMap<String, JSONObject> extensionList = Extensions.extensionCheck(baseMessage);
        if (extensionList != null && extensionList.containsKey("smartReply")) {
            rvSmartReply.setVisibility(View.VISIBLE);
            JSONObject replyObject = extensionList.get("smartReply");
            List<String> replyList = new ArrayList<>();
            try {
                replyList.add(replyObject.getString("reply_positive"));
                replyList.add(replyObject.getString("reply_neutral"));
                replyList.add(replyObject.getString("reply_negative"));
            } catch (Exception e) {
                Log.e(TAG, "onSuccess: " + e.getMessage());
            }
            setSmartReplyAdapter(replyList);
        } else {
            rvSmartReply.setVisibility(GONE);
        }
    }

    private void setSmartReplyAdapter(List<String> replyList) {
        rvSmartReply.setSmartReplyList(replyList);
        scrollToBottom();
    }


    /**
     * This method is used to initialize the message adapter if it is empty else it helps
     * to update the messagelist in adapter.
     *
     * @param messageList is a list of messages which will be added.
     */
    private void initMessageAdapter(List<BaseMessage> messageList) {
        if (messageAdapter == null) {
            messageAdapter = new ThreadAdapter(getActivity(), messageList, type);
            rvChatListView.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();
        } else {
            messageAdapter.updateList(messageList);
        }
        if (!isBlockedByMe && rvSmartReply.getAdapter().getItemCount() == 0 && rvSmartReply.getVisibility() == GONE) {
            BaseMessage lastMessage = messageAdapter.getLastMessage();
            checkSmartReply(lastMessage);
        }
    }

    /**
     * This method is used to send typing indicator to other users and groups.
     *
     * @param isEnd is boolean which is used to differentiate between startTyping & endTyping Indicators.
     * @see CometChat#startTyping(TypingIndicator)
     * @see CometChat#endTyping(TypingIndicator)
     */
    private void sendTypingIndicator(boolean isEnd) {
        if (isEnd) {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                CometChat.endTyping(new TypingIndicator(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_USER));
            } else {
                CometChat.endTyping(new TypingIndicator(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_GROUP));
            }
        } else {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                CometChat.startTyping(new TypingIndicator(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_USER));
            } else {
                CometChat.startTyping(new TypingIndicator(NextGroupOrUserId, CometChatConstants.RECEIVER_TYPE_GROUP));
            }
        }
    }

    private void endTypingTimer() {
        if (typingTimer != null) {
            typingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendTypingIndicator(true);
                }
            }, 2000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        switch (requestCode) {
            case StringContract.RequestCode.AUDIO:
                if (data != null) {
                    File file = MediaUtils.getRealPath(getContext(), data.getData());
                    ContentResolver cr = getActivity().getContentResolver();
                    sendMediaMessage(file, CometChatConstants.MESSAGE_TYPE_AUDIO);
                }
                break;
            case StringContract.RequestCode.GALLERY:
                if (data != null) {

                    File file = MediaUtils.getRealPath(getContext(), data.getData());
                    ContentResolver cr = getActivity().getContentResolver();
                    String mimeType = cr.getType(data.getData());
                    if (mimeType != null && mimeType.contains("image")) {
                        if (file.exists())
                            sendMediaMessage(file, CometChatConstants.MESSAGE_TYPE_IMAGE);
                        else
                            Snackbar.make(rvChatListView, R.string.file_not_exist, Snackbar.LENGTH_LONG).show();
                    } else {
                        if (file.exists())
                            sendMediaMessage(file, CometChatConstants.MESSAGE_TYPE_VIDEO);
                        else
                            Snackbar.make(rvChatListView, R.string.file_not_exist, Snackbar.LENGTH_LONG).show();
                    }
                }

                break;
            case StringContract.RequestCode.CAMERA:
                File file;
                if (Build.VERSION.SDK_INT >= 29) {
                    file = MediaUtils.getRealPath(getContext(), MediaUtils.uri);
                } else {
                    file = new File(MediaUtils.pictureImagePath);
                }
                if (file.exists())
                    sendMediaMessage(file, CometChatConstants.MESSAGE_TYPE_IMAGE);
                else
                    Snackbar.make(rvChatListView, R.string.file_not_exist, Snackbar.LENGTH_LONG).show();

                break;
            case StringContract.RequestCode.FILE:
                if (data != null)
                    sendMediaMessage(MediaUtils.getRealPath(getActivity(), data.getData()), CometChatConstants.MESSAGE_TYPE_FILE);
                break;
            case StringContract.RequestCode.BLOCK_USER:
                name = data.getStringExtra("");
                break;
        }

    }


    /**
     * This method is used to send media messages to other users and group.
     *
     * @param file     is an object of File which is been sent within the message.
     * @param filetype is a string which indicate a type of file been sent within the message.
     * @see CometChat#sendMediaMessage(MediaMessage, CometChat.CallbackListener)
     * @see MediaMessage
     */
    private void sendMediaMessage(File file, String filetype) {
        MediaMessage mediaMessage;

        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            mediaMessage = new MediaMessage(NextGroupOrUserId, file, filetype, CometChatConstants.RECEIVER_TYPE_USER);
        else
            mediaMessage = new MediaMessage(NextGroupOrUserId, file, filetype, CometChatConstants.RECEIVER_TYPE_GROUP);

        /*JSONArray languageArray = new JSONArray();
        languageArray.put("en");
        languageArray.put("hi");
        languageArray.put("ar");
        languageArray.put("ko");
        languageArray.put("de");
        languageArray.put("fr");
        languageArray.put("fr-CA");
        languageArray.put("es");
        languageArray.put("ru");
        languageArray.put("pt");
        languageArray.put("vi");
        languageArray.put("ur");

        JSONObject jsonObject = new JSONObject();

        switch (tabs) {
            //Friend chat
            case "1":
                try {
                    jsonObject.put("team_logs_id", 0);
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            //group
            case "2":

                try {
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: Hello its tab 2 sending msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "3":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 3; //+"_sage036"
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: TeamId " + teamId);
                    Log.i(TAG, "sendMessage: Hello its tab3 sending msg " + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                team_logs_id = receiverId + "_-" + teamId + "_-" + 4; //+"_sage036"
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: Hello its tab4 sending msg" + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

        textMessage.setMetadata(jsonObject);*/


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("path", file.getAbsolutePath());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mediaMessage.setMetadata(jsonObject);
        mediaMessage.setParentMessageId(parentId);
        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                noReplyMessages.setVisibility(GONE);
                Log.d(TAG, "sendMediaMessage onSuccess: " + mediaMessage.toString());
                if (messageAdapter != null) {
                    setReply();
                    messageAdapter.addMessage(mediaMessage);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(CometChatException e) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method is used to get details of reciever.
     *
     * @see CometChat#getUser(String, CometChat.CallbackListener)
     */
    private void getUser() {

        CometChat.getUser(NextGroupOrUserId, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {

                if (getActivity() != null) {
                    if (user.isBlockedByMe()) {
                        isBlockedByMe = true;
                        rvSmartReply.setVisibility(GONE);
                        toolbar.setSelected(false);
                        blockedUserName.setText("You've blocked " + user.getName());
                        blockUserLayout.setVisibility(View.VISIBLE);
                    } else {
                        isBlockedByMe = false;
                        blockUserLayout.setVisibility(GONE);
                    }
                    tvName.setText(String.format(getString(R.string.thread_in_name), user.getName()));
                    Log.d(TAG, "onSuccess: " + user.toString());
                }

            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAvatar() {
        if (avatarUrl != null && !avatarUrl.isEmpty())
            senderAvatar.setAvatar(avatarUrl);
        else {
            senderAvatar.setInitials(name);
        }
    }

    /**
     * This method is used to get Group Details.
     *
     * @see CometChat#getGroup(String, CometChat.CallbackListener)
     */
    private void getGroup() {

        CometChat.getGroup(NextGroupOrUserId, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (getActivity() != null) {
                    loggedInUserScope = group.getScope();
                    groupOwnerId = group.getOwner();

                    tvName.setText(String.format(getString(R.string.thread_in_name), group.getName()));
                }

            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }

    /**
     * This method is used to send Text Message to other users and groups.
     *
     * @param message is a String which is been sent as message.
     * @see TextMessage
     * @see CometChat#sendMessage(TextMessage, CometChat.CallbackListener)
     */
    private void sendMessage(String message) {
        String team_logs_id;
        Log.i(TAG, "sendMessage: block ");
        TextMessage textMessage;
        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_USER);
        else
            textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_GROUP);


        JSONArray languageArray = new JSONArray();
        languageArray.put("en");
        languageArray.put("hi");
        languageArray.put("ar");
        languageArray.put("ko");
        languageArray.put("de");
        languageArray.put("fr");
        languageArray.put("fr-CA");
        languageArray.put("es");
        languageArray.put("ru");
        languageArray.put("pt");
        languageArray.put("vi");
        languageArray.put("ur");

        JSONObject jsonObject = new JSONObject();
        switch (tabs) {
            //Friend chat
            case "1":
                try {
                    Log.i(TAG, "sendMessage: friend chat ");
                    jsonObject.put("team_logs_id", 0);
                    jsonObject.put("message_translation_languages", languageArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            //group
            case "2":

                try {
                    jsonObject.put("message_translation_languages", languageArray);
                    Log.i(TAG, "sendMessage: Hello its tab 2 sending msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "3":
                team_logs_id = NextGroupOrUserId + "_-" + team_id + "_-" + 3; //+"_sage036"
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    //Log.i(TAG, "sendMessage: TeamId " + teamId);
                    //Log.i(TAG, "sendMessage: Hello its tab3 sending msg " + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                team_logs_id = NextGroupOrUserId + "_-" + team_id + "_-" + 4; //+"_sage036"
                try {
                    jsonObject.put("team_logs_id", team_logs_id);
                    jsonObject.put("message_translation_languages", languageArray);
                    //Log.i(TAG, "sendMessage: Hello its tab4 sending msg" + team_logs_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        //textMessage.setCategory(CometChatConstants.CATEGORY_MESSAGE);
        textMessage.setParentMessageId(parentId);
        sendTypingIndicator(true);
        textMessage.setMetadata(jsonObject);
        Log.i(TAG, "sendMessage: metadata set properly ");
        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                Log.i(TAG, "sendMessage onSuccess: threadedTextMessageSent " + textMessage);
                noReplyMessages.setVisibility(GONE);
                setReply();
                isSmartReplyClicked = false;
                if (textMessage.getMetadata() != null) {
                    JSONObject injectedObject = null;
                    try {
                        injectedObject = textMessage.getMetadata().getJSONObject("@injected");

                        if (injectedObject.has("extensions")) {
                            JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                            if (extensionsObject.has("message-translation")) {
                                JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                                JSONArray translations = messageTranslationObject.getJSONArray("translations");
                                HashMap<String, String> translationsMap = new HashMap<String, String>();

                                for (int i = 0; i < translations.length(); i++) {
                                    JSONObject translation = translations.getJSONObject(i);
                                    String translatedText = translation.getString("message_translated");
                                    String translatedLanguage = translation.getString("language_translated");
                                    translationsMap.put(translatedLanguage, translatedText);
                                    Log.i(TAG, "filterBaseMessages: currentLang " + sp.getString("currentLang", "en"));
                                    if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {
                                        if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                            if (textMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                                ((TextMessage) textMessage).setText(translatedText);
                                            }
                                        }
                                    } else {
                                        ((TextMessage) textMessage).setText(((TextMessage) textMessage).getText());
                                    }
                                }
                            }

                            Log.e(TAG, "filterBaseMessages: on MessageTextSend" + textMessage.toString());
                            if (messageAdapter != null) {
                                MediaUtils.playSendSound(context, R.raw.outgoing_message);
                                messageAdapter.addMessage(textMessage);
                                scrollToBottom();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });

    }

    /**
     * This method is used to delete the message.
     *
     * @param baseMessage is an object of BaseMessage which is being used to delete the message.
     * @see BaseMessage
     * @see CometChat#deleteMessage(int, CometChat.CallbackListener)
     */
    private void deleteMessage(BaseMessage baseMessage) {
        CometChat.deleteMessage(baseMessage.getId(), new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage baseMessage) {
                if (messageAdapter != null)
                    messageAdapter.setUpdatedMessage(baseMessage);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });
    }

    /**
     * This method is used to edit the message. This methods takes old message and change text of old
     * message with new message i.e String and update it.
     *
     * @param baseMessage is an object of BaseMessage, It is a old message which is going to be edited.
     * @param message     is String, It is a new message which will be replaced with text of old message.
     * @see TextMessage
     * @see BaseMessage
     * @see CometChat#editMessage(BaseMessage, CometChat.CallbackListener)
     */
    private void editMessage(BaseMessage baseMessage, String message) {

        isEdit = false;
        isParent = true;
        TextMessage textMessage;
        if (baseMessage.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            textMessage = new TextMessage(baseMessage.getReceiverUid(), message, CometChatConstants.RECEIVER_TYPE_USER);
        else
            textMessage = new TextMessage(baseMessage.getReceiverUid(), message, CometChatConstants.RECEIVER_TYPE_GROUP);
        sendTypingIndicator(true);
        textMessage.setId(baseMessage.getId());
        CometChat.editMessage(textMessage, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage message) {
                if (messageAdapter != null) {
                        try {
                            long id = baseMessage.getId();
                            Log.i(TAG, "filterBaseMessages: text message is " + ((TextMessage) message).getText());
                            String currentLang = sp.getString("currentLang", null);
                            JSONObject body = new JSONObject();
                            JSONArray languages = new JSONArray();
                            languages.put(currentLang);
                            body.put("msgId", id);
                            body.put("languages", languages);
                            body.put("text", ((TextMessage) message).getText());
                            Log.i(TAG, "filterBaseMessages: edited at block " + baseMessage);
                            // Do something after 5s = 5000ms
                            CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                    new CometChat.CallbackListener<JSONObject>() {
                                        @Override
                                        public void onSuccess(JSONObject jsonObject) {
                                            Log.e(TAG, "onSuccess: message-translation " + jsonObject);
                                            //baseMessage.setMetadata();
                                            BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                            Log.i(TAG, "onSuccess: baseMessage 1 " + baseMessage1);
                                            Log.i(TAG, "onSuccess: baseMessage " + baseMessage);

                                            messageAdapter.setUpdatedMessage(baseMessage1);

                                            //messageAdapter.setEditedMessage(baseMessage, baseMessage1);
                                        }

                                        @Override
                                        public void onError(CometChatException e) {
                                            // Some error occured
                                            Log.i(TAG, "onError: " + e.getMessage());
                                            Log.i(TAG, "filterBaseMessages: edited at block 4");
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                       /* try {
                            JSONObject body = new JSONObject();
                            JSONArray languages = new JSONArray();
                            String currentLang = sp.getString("currentLang", null);
                            languages.put(sp.getString("currentLang", currentLang));
                            body.put("msgId", message.getId());
                            body.put("languages", languages);
                            body.put("text", "" + message);
                            CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                                    new CometChat.CallbackListener<JSONObject>() {
                                        @Override
                                        public void onSuccess(JSONObject jsonObject) {
                                            Log.e(TAG, "onSuccess: message-translation " + jsonObject);
                                            //baseMessage.setMetadata(.);

                                            BaseMessage baseMessage2 = createMetadataForEditedMessage(message, jsonObject);
                                            Log.i(TAG, "onSuccess: translated message "+baseMessage2);
                                            //messageAdapter.setUpdatedMessage(baseMessage1);
                                            messageAdapter.setEditedMessage(baseMessage, baseMessage2);
                                        }

                                        @Override
                                        public void onError(CometChatException e) {
                                            Log.e(TAG, "onError: " + e.getMessage());
                                        }
                                    });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    //messageAdapter.setUpdatedMessage(message);
                }
            }


            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });
    }

    public BaseMessage createMetadataForEditedMessage(BaseMessage baseMessage1, JSONObject
            jsonObject) {
        try {
            String team_logs_id = baseMessage1.getMetadata().getString("team_logs_id");
            long readByMeAt = baseMessage1.getReadByMeAt();
            long deliveredToMeAt = baseMessage1.getDeliveredToMeAt();
            long deletedAt = baseMessage1.getDeletedAt();
            long editedAt = baseMessage1.getEditedAt();
            String deletedBy = baseMessage1.getDeletedBy();
            String editedBy = baseMessage1.getEditedBy();
            long updatedAt = baseMessage1.getUpdatedAt();
            JSONArray message_translation_languages = baseMessage1.getMetadata().getJSONArray("message_translation_languages");
            JSONObject metadata = new JSONObject();
            JSONObject injected = new JSONObject();
            JSONObject extensions = new JSONObject();
            JSONObject message_translation = new JSONObject();
            if (jsonObject.has("data")) {
                //translations.put(jsonObject.getJSONObject("data").getJSONArray("translations"));
                message_translation.put("translations", jsonObject.getJSONObject("data").getJSONArray("translations"));
                Log.i(TAG, "createMetadataForEditedMessage: changed log " + jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getJSONObject("message_translated"));
                extensions.put("message-translation", message_translation);
                injected.put("extensions", extensions);
                metadata.put("@injected", injected);
                metadata.put("team_logs_id", team_logs_id);
                metadata.put("readByMeAt", readByMeAt);
                metadata.put("deliveredToMeAt", deliveredToMeAt);
                metadata.put("deletedAt", deletedAt);
                metadata.put("editedAt", editedAt);
                metadata.put("deletedBy", deletedBy);
                metadata.put("editedBy", editedBy);
                metadata.put("updatedAt", updatedAt);

                baseMessage1.setMetadata(metadata);
                Log.i(TAG, "createMetadataForEditedMessage: metadata is " + metadata);
            } else {
                Log.i(TAG, "createMetadataForEditedMessage: message-translation not found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return baseMessage1;
    }


    /**
     * This method is used to send reply message by link previous message with new message.
     *
     * @param baseMessage is a linked message
     * @param message     is a String. It will be new message sent as reply.
     */
    private void replyMessage(BaseMessage baseMessage, String message) {
        isReply = false;
        try {
            TextMessage textMessage;
            if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
                textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_USER);
            else
                textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_GROUP);

            JSONObject replyObject = new JSONObject();
            //replyObject.put("replyToMessage", baseMessage);
            replyObject.put("type", "reply");
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
//                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_TEXT);
                replyObject.put("message", ((TextMessage) baseMessage).getText());
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
//                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_IMAGE);
                replyObject.put("message", "image");
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_VIDEO)) {
//                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_VIDEO);
                replyObject.put("message", "video");
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_FILE)) {
//                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_FILE);
                replyObject.put("message", "file");
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_AUDIO)) {
                // replyObject.put("type", CometChatConstants.MESSAGE_TYPE_AUDIO);
                replyObject.put("message", "audio");
            }
            replyObject.put("name", baseMessage.getSender().getName());
            replyObject.put("avatar", baseMessage.getSender().getAvatar());
            // jsonObject.put("reply", replyObject);

            JSONArray languageArray = new JSONArray();
            languageArray.put("en");
            languageArray.put("hi");
            languageArray.put("ar");
            languageArray.put("ko");
            languageArray.put("de");
            languageArray.put("fr");
            languageArray.put("fr-CA");
            languageArray.put("es");
            languageArray.put("ru");
            languageArray.put("pt");
            languageArray.put("vi");
            languageArray.put("ur");

            replyObject.put("message_translation_languages", languageArray);
            switch (tabs) {
                case "1":
                    try {
                        replyObject.put("team_logs_id", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "2":

                    //textMessage.setMetadata(jsonObject);
                    break;

                case "3":
                    if (NextGroupOrUserId != null && team_id != null) {
                        team_logs_id = NextGroupOrUserId + "_-" + team_id + "_-" + 3;
                        try {
                            replyObject.put("team_logs_id", team_logs_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //textMessage.setMetadata(jsonObject);
                    }
                    break;

                case "4":
                    if (NextGroupOrUserId != null && team_id != null) {
                        team_logs_id = NextGroupOrUserId + "_-" + team_id + "_-" + 4;
                        try {
                            replyObject.put("team_logs_id", team_logs_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // textMessage.setMetadata(jsonObject);
                    }
                    break;
            }

            textMessage.setMetadata(replyObject);
            textMessage.setParentMessageId(parentId);
            sendTypingIndicator(true);
            CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
                @Override
                public void onSuccess(TextMessage textMessage) {
                    if (messageAdapter != null) {
                        MediaUtils.playSendSound(context, R.raw.outgoing_message);
                        Log.e(TAG, "onSuccess: replied message response is " + textMessage);
                        filterTextMessage(textMessage, "");
                        //messageAdapter.addMessage(textMessage);
                        scrollToBottom();
                    }
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e(TAG, "onError: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "replyMessage: " + e.getMessage());
        }

        /*Previous thread code for reply message and now implementhing code as main chat screen*/
       /* isReply = false;
        try {
            TextMessage textMessage;
            if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
                textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_USER);
            else
                textMessage = new TextMessage(NextGroupOrUserId, message, CometChatConstants.RECEIVER_TYPE_GROUP);

            JSONObject jsonObject = new JSONObject();
            JSONObject replyObject = new JSONObject();
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_TEXT);
                replyObject.put("message", ((TextMessage) baseMessage).getText());
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_IMAGE);
                replyObject.put("message", "image");
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_VIDEO)) {
                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_VIDEO);
                replyObject.put("message", "video");
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_FILE)) {
                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_FILE);
                replyObject.put("message", "file");
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_AUDIO)) {
                replyObject.put("type", CometChatConstants.MESSAGE_TYPE_AUDIO);
                replyObject.put("message", "audio");
            }
            replyObject.put("name", baseMessage.getSender().getName());
            replyObject.put("avatar", baseMessage.getSender().getAvatar());
            jsonObject.put("reply", replyObject);

            textMessage.setParentMessageId(parentId);
            textMessage.setMetadata(jsonObject);
            sendTypingIndicator(true);
            CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
                @Override
                public void onSuccess(TextMessage textMessage) {
                    if (messageAdapter != null) {
                        MediaUtils.playSendSound(context, R.raw.outgoing_message);
                        messageAdapter.addMessage(textMessage);
                        scrollToBottom();
                    }
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e(TAG, "onError: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "replyMessage: " + e.getMessage());
        }*/
    }

    private void scrollToBottom() {
        if (messageAdapter != null && messageAdapter.getItemCount() > 0) {
            rvChatListView.scrollToPosition(messageAdapter.getItemCount() - 1);
            final int scrollViewHeight = nestedScrollView.getHeight();
            if (scrollViewHeight > 0) {
                final View lastView = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
                final int lastViewBottom = lastView.getBottom() + nestedScrollView.getPaddingBottom();
                final int deltaScrollY = lastViewBottom - scrollViewHeight - nestedScrollView.getScrollY();
                /* If you want to see the scroll animation, call this. */
                nestedScrollView.smoothScrollBy(0, deltaScrollY);
            }
        }
    }

    /**
     * This method is used to mark users & group message as read.
     *
     * @param baseMessage is object of BaseMessage.class. It is message which is been marked as read.
     */
    private void markMessageAsRead(BaseMessage baseMessage) {
        if (type.equals(CometChatConstants.RECEIVER_TYPE_USER))
            CometChat.markAsRead(baseMessage.getId(), baseMessage.getSender().getUid(), baseMessage.getReceiverType());
        else
            CometChat.markAsRead(baseMessage.getId(), baseMessage.getReceiverUid(), baseMessage.getReceiverType());
    }


    /**
     * This method is used to add message listener to recieve real time messages between users &
     * groups. It also give real time events for typing indicators, edit message, delete message,
     * message being read & delivered.
     *
     * @see CometChat#addMessageListener(String, CometChat.MessageListener)
     */
    private void addMessageListener() {

        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                Log.d(TAG, "onTextMessageReceived: " + message.toString());
                onMessageReceived(message);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                Log.d(TAG, "onMediaMessageReceived: " + message.toString());
                onMessageReceived(message);
            }

            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                Log.e(TAG, "onTypingStarted: " + typingIndicator);
                setTypingIndicator(typingIndicator, true);
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                Log.d(TAG, "onTypingEnded: " + typingIndicator.toString());
                setTypingIndicator(typingIndicator, false);
            }

            @Override
            public void onMessagesDelivered(MessageReceipt messageReceipt) {
                Log.d(TAG, "onMessagesDelivered: " + messageReceipt.toString());
                setMessageReciept(messageReceipt);

            }

            @Override
            public void onMessagesRead(MessageReceipt messageReceipt) {
                Log.e(TAG, "onMessagesRead: " + messageReceipt.toString());
                setMessageReciept(messageReceipt);
            }

            @Override
            public void onMessageEdited(BaseMessage message) {
                Log.d(TAG, "onMessageEdited: " + message.toString());
                updateMessage(message);
            }

            @Override
            public void onMessageDeleted(BaseMessage message) {
                Log.d(TAG, "onMessageDeleted: ");
                updateMessage(message);
            }
        });
    }

    private void setMessageReciept(MessageReceipt messageReceipt) {
        if (messageAdapter != null) {
            if (messageReceipt.getReceivertype().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                if (NextGroupOrUserId != null && messageReceipt.getSender().getUid().equals(NextGroupOrUserId)) {
                    if (messageReceipt.getReceiptType().equals(MessageReceipt.RECEIPT_TYPE_DELIVERED))
                        messageAdapter.setDeliveryReceipts(messageReceipt);
                    else
                        messageAdapter.setReadReceipts(messageReceipt);
                }
            }
        }
    }

    private void setTypingIndicator(TypingIndicator typingIndicator, boolean isShow) {
        if (typingIndicator.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER)) {
            Log.e(TAG, "onTypingStarted: " + typingIndicator);
            if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(typingIndicator.getSender().getUid())) {
                if (typingIndicator.getMetadata() == null)
                    typingIndicator(typingIndicator, isShow);
            }
        } else {
            if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(typingIndicator.getReceiverId()))
                typingIndicator(typingIndicator, isShow);
        }
    }

    private void onMessageReceived(BaseMessage message) {
        JSONObject metadata = message.getMetadata();
        switch (tabs) {
            case "1":
                int team_log;
                try {
                    if(message.getParentMessageId()==parentId) {
                        if (metadata.has("team_logs_id")) {
                            team_log = metadata.getInt("team_logs_id");
                            if (team_log == 0) {
                                /*for normal message and for android  reply message this block will execute*/
                                if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                                    if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getSender().getUid())) {

                                        Log.i(TAG, "onMessageReceived: case 1 reply block");
                                        filterTextMessage(message, "");
                                        //scrollToBottom();
                                        //}
                                    } else if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getReceiverUid())
                                            && message.getSender().getUid().equalsIgnoreCase(loggedInUser.getUid())) {
                                        Log.i(TAG, "onMessageReceived: else part");
                                        //if (!checkIOSReplyOrNot(metadata, message, General.CHATSCREEN_RECEIVED_MESSAGE)) {
                                        setMessage(message);
                                        scrollToBottom();
                                        //}
                                    }
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "2":
                Log.i(TAG, "onMessageReceived: case 2 ");
                try {
                    if(message.getParentMessageId()==parentId) {
                        if (!metadata.has("team_logs_id")) {
                            /*if (!checkIOSReplyOrNot(metadata, message, General.CHATSCREEN_RECEIVED_MESSAGE)) {*/
                           /* setMessage(message);
                            scrollToBottom();*/
                            filterTextMessage(message, "");
                            /*}*/
                            Log.i(TAG, "onMessageReceived: group case setMessage");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case "3":
                String logss;
                try {
                    if(message.getParentMessageId()==parentId) {
                    logss = metadata.getString("team_logs_id");
                    if (!logss.isEmpty() && !logss.equals("0")) {
                        if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                            if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getSender().getUid())) {
                                /*for normal message and for android  reply message this block will execute*/

                                Log.i(TAG, "onMessageReceived: case 1 reply block");
                                filterTextMessage(message, "");

                            } else if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getReceiverUid())
                                    && message.getSender().getUid().equalsIgnoreCase(loggedInUser.getUid())) {
                                Log.i(TAG, "onMessageReceived: else part");

                                setMessage(message);
                                scrollToBottom();

                            }
                        }
                    }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "4":
                String logs;
                try {
                    if(message.getParentMessageId()==parentId) {
                    Log.i(TAG, "onMessageReceived: case 4 ");
                    logs = metadata.getString("team_logs_id");
                    Log.e(TAG, "onMessageReceived: " + logs);
                    if (!logs.isEmpty() && !logs.equals("0")) {
                        if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {

                            if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getSender().getUid())) {
                                /*for normal message and for android  reply message this block will execute*/

                                Log.i(TAG, "onMessageReceived: case 1 reply block");
                                filterTextMessage(message, "");

                            } else if (NextGroupOrUserId != null && NextGroupOrUserId.equalsIgnoreCase(message.getReceiverUid())
                                    && message.getSender().getUid().equalsIgnoreCase(loggedInUser.getUid())) {
                                Log.i(TAG, "onMessageReceived: else part");

                                setMessage(message);
                                scrollToBottom();

                            }
                            /*commented because it was not working for ios replied messages on 17-06-2021 by rahul maske*/
                            /* if (SenderId != null && SenderId.equalsIgnoreCase(message.getSender().getUid())) {
                             *//*if (!checkIOSReplyOrNot(metadata, message, General.CHATSCREEN_RECEIVED_MESSAGE)) {*//*
                             *//*setMessage(message);
                                    scrollToBottom();*//*
                                filterTextMessage(message, "");
                                *//*}*//*
                            } else if (SenderId != null && SenderId.equalsIgnoreCase(message.getReceiverUid()) && message.getSender().getUid().equalsIgnoreCase(loggedInUser.getUid())) {

                                *//*if (!checkIOSReplyOrNot(metadata, message, General.CHATSCREEN_RECEIVED_MESSAGE)) {*//*
                                setMessage(message);
                                scrollToBottom();
                                *//*}*//*
                            }*/
                        }
                    }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        /* MediaUtils.playSendSound(context, R.raw.incoming_message);
        if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            if (Id != null && Id.equalsIgnoreCase(message.getSender().getUid())) {
                if (message.getParentMessageId() == parentId)
                    setMessage(message);
            } else if (Id != null && Id.equalsIgnoreCase(message.getReceiverUid()) && message.getSender().getUid().equalsIgnoreCase(loggedInUser.getUid())) {
                if (message.getParentMessageId() == parentId)
                    setMessage(message);
            }
        } else {
            if (Id != null && Id.equalsIgnoreCase(message.getReceiverUid())) {
                if (message.getParentMessageId() == parentId)
                    setMessage(message);
            }
        }*/
    }

    public void filterTextMessage(@NotNull BaseMessage message, String string) {
        Log.i(TAG, "filterTextMessage: string -> " + string + " message " + message);
        JSONObject metadata = message.getMetadata();
        if (message.getType().equals("text")) {
            try {
                if (metadata.has("message") && metadata.getString("type").equalsIgnoreCase("reply")) {
                    Log.i(TAG, "filterBaseMessages: " + metadata.getString("message"));
                    JSONObject body = new JSONObject();
                    JSONArray languages = new JSONArray();
                    languages.put(sp.getString("currentLang", "en"));
                    body.put("msgId", message.getId());
                    body.put("languages", languages);
                    body.put("text", metadata.getString("message"));
                    Log.i(TAG, "filterTextMessage: reply from iOS ");
                    // Do something after 5s = 5000ms
                    CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                            new CometChat.CallbackListener<JSONObject>() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
                                    try {
                                        // Log.i(TAG, "filterBaseMessages: edited at block 3 translated response for replied message "+jsonObject);
                                        JSONObject meta = message.getMetadata();
                                        //meta.accumulate("values", jsonObject);
                                        String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                        Log.i(TAG, "filterBaseMessages: onSuccess at block 3 messageTranslatedString " + messageTranslatedString);
                                        metadata.remove("message");
                                        metadata.put("message", messageTranslatedString);
                                        //messageAdapter.setUpdatedMessage(message);
                                        Log.i(TAG, "filterBaseMessages onSuccess: metadata for replied translate " + metadata);
                                        // BaseMessage baseMessage1 = createMetadataForEditedMessageNew(baseMessage, jsonObject);
                                        //baseMessage.setMetadata(meta);
                                        // ((TextMessage) baseMessage).setText(messageTranslatedString);
                                        //baseMessage.setMetadata(meta);
                                        transaletAndSetMessage(metadata, message, string);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(CometChatException e) {
                                    // Some error occured
                                    Log.i(TAG, "onError: " + e.getMessage());
                                    Log.i(TAG, "filterBaseMessages: edited at block 4");
                                }
                            });
                } else {
                    transaletAndSetMessage(metadata, message, string);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            setMessage(message);
            scrollToBottom();
        }

    }

    public void transaletAndSetMessage(JSONObject metadata, BaseMessage message, String string) throws JSONException {
        Log.i(TAG, "transaletAndSetMessage: block 1 " + message);
        if (metadata != null) {
            if (!string.equalsIgnoreCase("reply")) {
                JSONObject injectedObject = metadata.getJSONObject("@injected");
                if (injectedObject.has("extensions")) {
                    JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                    if (extensionsObject.has("message-translation")) {
                        Log.i(TAG, "transaletAndSetMessage: block 2");
                        JSONObject messageTranslationObject = extensionsObject.getJSONObject("message-translation");
                        JSONArray translations = messageTranslationObject.getJSONArray("translations");
                        HashMap<String, String> translationsMap = new HashMap<String, String>();
                        for (int i = 0; i < translations.length(); i++) {
                            JSONObject translation = translations.getJSONObject(i);
                            String translatedText = translation.getString("message_translated");
                            String translatedLanguage = translation.getString("language_translated");
                            translationsMap.put(translatedLanguage, translatedText);
                            Log.i(TAG, "transaletAndSetMessage: currentLang " + sp.getString("currentLang", "en"));
                            /* if (!sp.getString("currentLang", "en").equalsIgnoreCase("en")) {*/
                            if (translatedLanguage.equals(sp.getString("currentLang", "en"))) {
                                if (message.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                                    ((TextMessage) message).setText(translatedText);
                                }
                            }

                        /*} else {
                            ((TextMessage) message).setText(((TextMessage) message).getText());
                        }*/
                        }
                    }
                }
            }
            if (messageAdapter != null) {
                Log.i(TAG, "transaletAndSetMessage: block 5 " + string);
                if (string.equals("reply")) {
                    Log.i(TAG, "transaletAndSetMessage: block 3");
                    messageAdapter.addMessage(message);
                    markMessageAsRead(message);
                    scrollToBottom();
                } else {
                    Log.i(TAG, "transaletAndSetMessage: block 4");
                    messageAdapter.addMessage(message);
                    checkSmartReply(message);
                    markMessageAsRead(message);
                    scrollToBottom();
                    if ((messageAdapter.getItemCount() - 1) - ((LinearLayoutManager) rvChatListView.getLayoutManager()).findLastVisibleItemPosition() < 5)
                        scrollToBottom();
                }
            } else {
                Log.i(TAG, "transaletAndSetMessage: block 4");
                messageList.add(message);
                initMessageAdapter(messageList);
            }
        }
    }

    /**
     * This method is used to update edited message by calling <code>setEditMessage()</code> of adapter
     *
     * @param message is an object of BaseMessage and it will replace with old message.
     * @see BaseMessage
     */
    private void updateMessage(BaseMessage message) {
        messageAdapter.setUpdatedMessage(message);
    }


    /**
     * This method is used to mark message as read before adding them to list. This method helps to
     * add real time message in list.
     *
     * @param message is an object of BaseMessage, It is recieved from message listener.
     * @see BaseMessage
     */
    private void setMessage(BaseMessage message) {
        setReply();
        noReplyMessages.setVisibility(GONE);
        if (messageAdapter != null) {
            messageAdapter.addMessage(message);
            checkSmartReply(message);
            markMessageAsRead(message);
            if ((messageAdapter.getItemCount() - 1) - ((LinearLayoutManager) rvChatListView.getLayoutManager()).findLastVisibleItemPosition() < 5)
                scrollToBottom();
        } else {
            messageList.add(message);
            initMessageAdapter(messageList);
        }
    }

    private void setReply() {
        replyCount = replyCount + 1;
        tvReplyCount.setVisibility(VISIBLE);
        if (replyCount == 1)
            tvReplyCount.setText(replyCount + " Reply");
        else
            tvReplyCount.setText(replyCount + " Replies");
    }

    private void checkSmartReply(BaseMessage lastMessage) {
        if (lastMessage != null && !lastMessage.getSender().getUid().equals(loggedInUser.getUid())) {
            if (lastMessage.getMetadata() != null) {
                getSmartReplyList(lastMessage);
            }
        }
    }

    /**
     * This method is used to display typing status to user.
     *
     * @param show is boolean, If it is true then <b>is Typing</b> will be shown to user
     *             If it is false then it will show user status i.e online or offline.
     */
    private void typingIndicator(TypingIndicator typingIndicator, boolean show) {
        if (messageAdapter != null) {
            if (show) {
                if (typingIndicator.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER))
                    tvTypingIndicator.setText("is Typing...");
                else
                    tvTypingIndicator.setText(typingIndicator.getSender().getName() + " is Typing...");
            } else {
                tvTypingIndicator.setVisibility(GONE);
            }

        }
    }

    /**
     * This method is used to remove message listener
     *
     * @see CometChat#removeMessageListener(String)
     */
    private void removeMessageListener() {
        CometChat.removeMessageListener(TAG);
    }

    /**
     * This method is used to remove user presence listener
     *
     * @see CometChat#removeUserListener(String)
     */
    private void removeUserListener() {
        CometChat.removeUserListener(TAG);
    }


    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        if (messageAdapter != null)
            messageAdapter.stopPlayingAudio();
        removeMessageListener();
        sendTypingIndicator(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        messageAdapter = null;
        messagesRequest = null;
        checkOnGoingCall();
        fetchMessage();
        isNoMoreMessages = false;
        addMessageListener();

        if (type != null) {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                new Thread(this::getUser).start();
            } else {
                new Thread(this::getGroup).start();
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.iv_close_message_action) {
            if (messageAdapter != null) {
                messageAdapter.clearLongClickSelectedItem();
                messageAdapter.notifyDataSetChanged();
            }
        } /*else if (id == R.id.ic_more_option) {
            MessageActionFragment messageActionFragment = new MessageActionFragment();
            Bundle bundle = new Bundle();
            if (messageType.equals(CometChatConstants.MESSAGE_TYPE_TEXT))
                bundle.putBoolean("copyVisible", true);
            else
                bundle.putBoolean("copyVisible", false);

            bundle.putBoolean("forwardVisible", true);
            if (name.equals(loggedInUser.getName()) && messageType.equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                bundle.putBoolean("editVisible", true);
            } else {
                bundle.putBoolean("editVisible", false);
            }
            bundle.putString("type", CometChatThreadMessageActivity.class.getName());
            messageActionFragment.setArguments(bundle);
            showBottomSheet(messageActionFragment);
        }*/ /*else if (id == R.id.ic_forward_option) {
            isParent = true;
            startForwardThreadActivity();
        }*/ else if (id == R.id.iv_message_close) {
            if (messageAdapter != null) {
                messageAdapter.clearLongClickSelectedItem();
                messageAdapter.notifyDataSetChanged();
            }
            isEdit = false;
            baseMessage = null;
            editMessageLayout.setVisibility(GONE);
        } else if (id == R.id.iv_reply_close) {
            if (messageAdapter != null) {
                messageAdapter.clearLongClickSelectedItem();
                messageAdapter.notifyDataSetChanged();
            }
            isReply = false;
            baseMessage = null;
            replyMessageLayout.setVisibility(GONE);
        } else if (id == R.id.btn_unblock_user) {
            unblockUser();
        } else if (id == R.id.chatList_toolbar) {
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                Intent intent = new Intent(getContext(), CometChatUserDetailScreenActivity.class);
                intent.putExtra(StringContract.IntentStrings.UID, NextGroupOrUserId);
                intent.putExtra(StringContract.IntentStrings.NAME, conversationName);
                intent.putExtra(StringContract.IntentStrings.AVATAR, avatarUrl);
                intent.putExtra(StringContract.IntentStrings.IS_BLOCKED_BY_ME, isBlockedByMe);
                intent.putExtra(StringContract.IntentStrings.TYPE, type);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), CometChatGroupDetailScreenActivity.class);
                intent.putExtra(StringContract.IntentStrings.GUID, NextGroupOrUserId);
                intent.putExtra(StringContract.IntentStrings.NAME, conversationName);
                intent.putExtra(StringContract.IntentStrings.AVATAR, avatarUrl);
                intent.putExtra(StringContract.IntentStrings.TYPE, type);
                intent.putExtra(StringContract.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
                intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, groupOwnerId);
                startActivity(intent);
            }
        }
    }

    @Override
    public void setLongMessageClick(List<BaseMessage> baseMessagesList) {
        Log.e(TAG, "setLongMessageClick: " + baseMessagesList);
        isReply = true;
        isEdit = false;
        isParent = false;
        MessageActionFragment messageActionFragment = new MessageActionFragment();
        replyMessageLayout.setVisibility(GONE);
        editMessageLayout.setVisibility(GONE);
        boolean copyVisible = true;
        boolean threadVisible = false;
        boolean replyVisible = true;
        boolean editVisible = true;
        boolean deleteVisible = true;
        boolean shareVisible = true;
        boolean forwardVisible = false;

        List<BaseMessage> textMessageList = new ArrayList<>();
        List<BaseMessage> mediaMessageList = new ArrayList<>();
        for (BaseMessage baseMessage : baseMessagesList) {
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                textMessageList.add(baseMessage);
            } else {
                mediaMessageList.add(baseMessage);
            }
        }
        if (textMessageList.size() == 1) {
            BaseMessage basemessage = textMessageList.get(0);
            if (basemessage != null && basemessage.getSender() != null) {
                if (!(basemessage instanceof Action) && basemessage.getDeletedAt() == 0) {
                    baseMessage = basemessage;
                    threadVisible = false;
                    if (basemessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())) {
                        deleteVisible = true;
                        editVisible = true;
                        if (tabs.equalsIgnoreCase("1") || tabs.equalsIgnoreCase("2")) {
                            forwardVisible = false;
                        } else {
                            forwardVisible = false;
                        }

                    } else {
                        editVisible = false;
                        if (tabs.equalsIgnoreCase("1") || tabs.equalsIgnoreCase("2")) {
                            forwardVisible = false;
                        } else {
                            forwardVisible = false;
                        }
                        if (loggedInUserScope != null && (loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN) || loggedInUserScope.equals(CometChatConstants.SCOPE_MODERATOR))) {
                            deleteVisible = true;
                        } else {
                            deleteVisible = false;
                        }
                    }
                }
            }
        }

        if (mediaMessageList.size() == 1) {
            BaseMessage basemessage = mediaMessageList.get(0);
            if (basemessage != null && basemessage.getSender() != null) {
                if (!(basemessage instanceof Action) && basemessage.getDeletedAt() == 0) {
                    baseMessage = basemessage;
                    copyVisible = false;
                    threadVisible = false;
                    if (basemessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())) {
                        deleteVisible = true;
                        editVisible = false;
                        if (tabs.equalsIgnoreCase("1") || tabs.equalsIgnoreCase("2")) {
                            forwardVisible = false;
                        } else {
                            forwardVisible = false;
                        }
                    } else {
                        if (loggedInUserScope != null && (loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN) || loggedInUserScope.equals(CometChatConstants.SCOPE_MODERATOR))) {
                            deleteVisible = true;
                        } else {
                            deleteVisible = false;
                        }
                        if (tabs.equalsIgnoreCase("1") || tabs.equalsIgnoreCase("2")) {
                            forwardVisible = false;
                        } else {
                            forwardVisible = false;
                        }
                        editVisible = false;
                    }
                }
            }
        }


        baseMessages = baseMessagesList;
        Bundle bundle = new Bundle();


        bundle.putBoolean("copyVisible", copyVisible);
        bundle.putBoolean("threadVisible", threadVisible);
        bundle.putBoolean("editVisible", editVisible);
        bundle.putBoolean("deleteVisible", deleteVisible);
        bundle.putBoolean("replyVisible", replyVisible);
        bundle.putBoolean("forwardVisible", forwardVisible);
        bundle.putBoolean("shareVisible", shareVisible);
        bundle.putString("type", CometChatThreadMessageActivity.class.getName());
        messageActionFragment.setArguments(bundle);
        showBottomSheet(messageActionFragment);
    }

    private void showBottomSheet(MessageActionFragment messageActionFragment) {
        messageActionFragment.show(getFragmentManager(), messageActionFragment.getTag());
        messageActionFragment.setMessageActionListener(new MessageActionFragment.MessageActionListener() {
            @Override
            public void onThreadMessageClick() {

            }

            @Override
            public void onEditMessageClick() {
                if (isParent)
                    editParentMessage();
                else
                    editThreadMessage();
            }

            @Override
            public void onReplyMessageClick() {
                replyMessage();
            }

            @Override
            public void onForwardMessageClick() {
                if (isParent)
                    startForwardThreadActivity();
                else
                    startForwardMessageActivity();
            }

            @Override
            public void onDeleteMessageClick() {
                deleteMessage(baseMessage);
                if (messageAdapter != null) {
                    messageAdapter.clearLongClickSelectedItem();
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCopyMessageClick() {
                String message = "";
                for (BaseMessage bMessage : baseMessages) {
                    if (bMessage.getDeletedAt() == 0 && bMessage instanceof TextMessage) {
                        message = message + "[" + Utils.getLastMessageDate(bMessage.getSentAt()) + "] " + bMessage.getSender().getName() + ": " + ((TextMessage) bMessage).getText();
                    }
                }
                Log.e(TAG, "onCopy: " + message);
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("MessageAdapter", message);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, getResources().getString(R.string.text_copied_clipboard), Toast.LENGTH_LONG).show();
                if (messageAdapter != null) {
                    messageAdapter.clearLongClickSelectedItem();
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onShareMessageClick() {
                shareMessage();
            }

            @Override
            public void onMessageInfoClick() {

            }
        });
    }

    public void shareMessage() {
        Log.i(TAG, "shareMessage: ");
        if (baseMessage != null && baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, getResources().getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, ((TextMessage) baseMessage).getText());
            shareIntent.setType("text/plain");
            Intent intent = Intent.createChooser(shareIntent, getResources().getString(R.string.share_message));
            startActivity(intent);
        } else if (baseMessage != null && baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
            String mediaName = ((MediaMessage) baseMessage).getAttachment().getFileName();
            Glide.with(context).asBitmap().load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), resource, mediaName, null);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                    shareIntent.setType(((MediaMessage) baseMessage).getAttachment().getFileMimeType());
                    Intent intent = Intent.createChooser(shareIntent, getResources().getString(R.string.share_message));
                    startActivity(intent);
                }
            });
        }
    }

    private void editParentMessage() {
        if (message != null && messageType.equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
            isEdit = true;
            isReply = false;
            tvMessageTitle.setText(getResources().getString(R.string.edit_message));
            tvMessageSubTitle.setText(message);
            composeBox.ivSend.setVisibility(View.VISIBLE);
            editMessageLayout.setVisibility(View.VISIBLE);
            composeBox.etComposeBox.setText(message);

            try {
                /*when user long press on message and then select edit
                 * 1. we will convert that message in english and show it in composbox edittext so that user can edit that message
                 * 2. and when we send message then edit message will called and then message will edited
                 * code added by rahul maske */
                JSONObject body = new JSONObject();
                JSONArray languages = new JSONArray();
                languages.put("en");
                body.put("msgId", baseMessage.getId());
                body.put("languages", languages);
                body.put("text", "" + ((TextMessage) baseMessage).getText());

                CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                        new CometChat.CallbackListener<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                // Result of translations
                                Log.i(TAG, "onSuccess: edited message json response" + jsonObject);
                                try {
                                    String messageTranslatedString = jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated");
                                    Log.i(TAG, "onSuccess: translated string  " + messageTranslatedString);
                                    composeBox.etComposeBox.setText(messageTranslatedString);
                                    if (messageAdapter != null) {
                                        messageAdapter.setSelectedMessage(baseMessage.getId());
                                        messageAdapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(CometChatException e) {
                                // Some error occured
                            }
                        });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void editThreadMessage() {
        if (baseMessage != null && baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
            isEdit = true;
            isReply = false;
            tvMessageTitle.setText(getResources().getString(R.string.edit_message));
            tvMessageSubTitle.setText(((TextMessage) baseMessage).getText());
            composeBox.ivSend.setVisibility(View.VISIBLE);
            editMessageLayout.setVisibility(View.VISIBLE);
            composeBox.etComposeBox.setText(((TextMessage) baseMessage).getText());
            /*if (messageAdapter != null) {
                messageAdapter.setSelectedMessage(baseMessage.getId());
                messageAdapter.notifyDataSetChanged();
            }*/

            try {
                /*when user long press on message and then select edit
                 * 1. we will convert that message in english and show it in composbox edittext so that user can edit that message
                 * 2. and when we send message then edit message will called and then message will edited
                 * code added by rahul maske */
                JSONObject body = new JSONObject();
                JSONArray languages = new JSONArray();
                languages.put("en");
                body.put("msgId", baseMessage.getId());
                body.put("languages", languages);
                body.put("text", "" + ((TextMessage) baseMessage).getText());

                CometChat.callExtension("message-translation", "POST", "/v2/translate", body,
                        new CometChat.CallbackListener<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                // Result of translations
                                Log.i(TAG, "onSuccess: edited message json response" + jsonObject);
                                try {
                                    String messageTranslatedString = jsonObject.getJSONObject("data")
                                            .getJSONArray("translations").getJSONObject(0)
                                            .getString("message_translated");

                                    Log.i(TAG, "onSuccess: translated string  " + messageTranslatedString);
                                    composeBox.etComposeBox.setText(messageTranslatedString);
                                    if (messageAdapter != null) {
                                        messageAdapter.setSelectedMessage(baseMessage.getId());
                                        messageAdapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(CometChatException e) {
                                // Some error occured
                            }
                        });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void startForwardThreadActivity() {
        Intent intent = new Intent(getContext(), CometChatForwardMessageScreenActivity.class);
        intent.putExtra(StringContract.IntentStrings.TYPE, messageType);
        intent.putExtra(StringContract.IntentStrings.TABS, tabs);
        if (messageType.equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
            intent.putExtra(CometChatConstants.MESSAGE_TYPE_TEXT, message);
            intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.MESSAGE_TYPE_TEXT);
        } else if (messageType.equals(CometChatConstants.MESSAGE_TYPE_IMAGE) ||
                messageType.equals(CometChatConstants.MESSAGE_TYPE_AUDIO) ||
                messageType.equals(CometChatConstants.MESSAGE_TYPE_VIDEO) ||
                messageType.equals(CometChatConstants.MESSAGE_TYPE_FILE)) {
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, message);
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, message);
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_MIME_TYPE, messageMimeType);
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, messageExtension);
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, messageSize);
        }
        startActivity(intent);
    }

    private void startForwardMessageActivity() {
        Intent intent = new Intent(getContext(), CometChatForwardMessageScreenActivity.class);
        if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
            intent.putExtra(StringContract.IntentStrings.TABS, tabs);
            intent.putExtra(CometChatConstants.MESSAGE_TYPE_TEXT, ((TextMessage) baseMessage).getText());
            intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.MESSAGE_TYPE_TEXT);
        } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE) ||
                baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_AUDIO) ||
                baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_VIDEO) ||
                baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_FILE)) {
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME, ((MediaMessage) baseMessage).getAttachment().getFileName());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL, ((MediaMessage) baseMessage).getAttachment().getFileUrl());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_MIME_TYPE, ((MediaMessage) baseMessage).getAttachment().getFileMimeType());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION, ((MediaMessage) baseMessage).getAttachment().getFileExtension());
            intent.putExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, ((MediaMessage) baseMessage).getAttachment().getFileSize());
            intent.putExtra(StringContract.IntentStrings.TYPE, baseMessage.getType());
        }
        startActivity(intent);
    }

    private void replyMessage() {
        if (baseMessage != null) {
            isReply = true;
            replyTitle.setText(baseMessage.getSender().getName());
            replyMedia.setVisibility(View.VISIBLE);
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {
                replyMessage.setText(((TextMessage) baseMessage).getText());
                replyMedia.setVisibility(GONE);
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
                replyMessage.setText(getResources().getString(R.string.shared_a_image));
                Glide.with(context).load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(replyMedia);
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_AUDIO)) {
                String messageStr = String.format(getResources().getString(R.string.shared_a_audio),
                        Utils.getFileSize(((MediaMessage) baseMessage).getAttachment().getFileSize()));
                replyMessage.setText(messageStr);
                replyMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_library_music_24dp, 0, 0, 0);
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_VIDEO)) {
                replyMessage.setText(getResources().getString(R.string.shared_a_video));
                Glide.with(context).load(((MediaMessage) baseMessage).getAttachment().getFileUrl()).into(replyMedia);
            } else if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_FILE)) {
                String messageStr = String.format(getResources().getString(R.string.shared_a_file),
                        Utils.getFileSize(((MediaMessage) baseMessage).getAttachment().getFileSize()));
                replyMessage.setText(messageStr);
                replyMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_insert_drive_file_black_24dp, 0, 0, 0);
            }
            composeBox.ivSend.setVisibility(View.VISIBLE);
            replyMessageLayout.setVisibility(View.VISIBLE);
            if (messageAdapter != null) {
                messageAdapter.setSelectedMessage(baseMessage.getId());
                messageAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        if (messageAdapter != null)
            messageAdapter.clearLongClickSelectedItem();
    }

    /*In this method we are making the matadata for edited message
     * method is created by rahul maske */
    public BaseMessage createMetadataForEditedMessageNew(BaseMessage baseMessage1, JSONObject
            jsonObject) {
        try {
            // String team_logs_id = baseMessage1.getMetadata().getString("team_logs_id");
            long readByMeAt = baseMessage1.getReadByMeAt();
            long deliveredToMeAt = baseMessage1.getDeliveredToMeAt();
            long deletedAt = baseMessage1.getDeletedAt();
            long editedAt = baseMessage1.getEditedAt();
            String deletedBy = baseMessage1.getDeletedBy();
            String editedBy = baseMessage1.getEditedBy();
            long updatedAt = baseMessage1.getUpdatedAt();
            JSONArray message_translation_languages = baseMessage1.getMetadata().getJSONArray("message_translation_languages");
            JSONObject metadata = new JSONObject();
            JSONObject injected = new JSONObject();
            JSONObject extensions = new JSONObject();
            JSONObject message_translation = new JSONObject();
            if (jsonObject.has("data")) {
                //translations.put(jsonObject.getJSONObject("data").getJSONArray("translations"));
                message_translation.put("translations", jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0));
                extensions.put("message-translation", message_translation);
                injected.put("extensions", extensions);

                metadata.put("@injected", injected);
                // metadata.put("team_logs_id", team_logs_id);
                metadata.put("readByMeAt", readByMeAt);
                metadata.put("deliveredToMeAt", deliveredToMeAt);
                metadata.put("deletedAt", deletedAt);
                metadata.put("editedAt", editedAt);
                metadata.put("deletedBy", deletedBy);
                metadata.put("editedBy", editedBy);
                metadata.put("updatedAt", updatedAt);
                ((TextMessage) baseMessage1).setText( jsonObject.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("message_translated"));
                baseMessage1.setMetadata(metadata);

                Log.i(TAG, "createMetadataForEditedMessage: metadata is " + metadata);
            } else {
                Log.i(TAG, "createMetadataForEditedMessage: message-translation not found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return baseMessage1;
    }
}
