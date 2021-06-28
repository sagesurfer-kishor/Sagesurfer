package screen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Attachment;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.CometChatConversationList;
import com.cometchat.pro.uikit.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ConversationListAdapter;
import constant.StringContract;
import listeners.OnItemClickListener;
import screen.messagelist.CometChatMessageListActivity;
import screen.messagelist.CometChatMessageScreen;
import screen.messagelist.General;
import utils.FontUtils;
import utils.MediaUtils;
import utils.Utils;

/**
 * Purpose - CometChatForwardMessageScreenActivity class is a fragment used to display list of users to which
 * we will forward the message.
 * Created on - 20th December 2019
 * <p>
 * Modified on  - 16th January 2020
 */

public class CometChatForwardMessageScreenActivity extends AppCompatActivity {
    private static final String TAG = "CometChatForward";

    private CometChatConversationList rvConversationList;

    private HashMap<String, Conversation> userList = new HashMap<>();

    private ConversationListAdapter conversationListAdapter;

    private ConversationsRequest conversationsRequest;

    private EditText etSearch;

    private ImageView clearSearch;

    private String name, avatar;

    private TextView tv_data_not_found;

    private MaterialButton forwardBtn;

    private ChipGroup selectedUsers;

    private String textMessage = "";
    int listSize;

    private FontUtils fontUtils;
    Fragment fragment;

    {
        try {
            fragment = new CometChatMessageScreen();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
   /* Fragment fragment;

    {
        try {
            fragment = new CometChatMessageScreen();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }*/

    private String messageType;
    String avatarUrl, status, uname, type, tabs, team_Ids, SenderId, memberCount, groupDesc, groupPassword, groupType, groupOwner;

    private String mediaMessageUrl, mediaMessageExtension, mediaMessageName, mediaMessageMime;

    private int mediaMessageSize;

    private int id;
    private int listCounter = 0;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comet_chat_forward_message_screen);
        fontUtils = FontUtils.getInstance(this);
        handler = new Handler();
        handleIntent();
        Log.i(TAG, "onCreate: CometChatForward");
        init();
        tv_data_not_found = findViewById(R.id.tv_data_not_found);
    }


    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            messageType = CometChatConstants.MESSAGE_TYPE_TEXT;
            textMessage = sharedText;
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            messageType = StringContract.IntentStrings.INTENT_MEDIA_MESSAGE;
            mediaMessageUrl = imageUri.toString();
            Log.e(TAG, "handleSendImage: " + mediaMessageUrl);
        }
    }

    /**
     * This method is used to handle parameter passed to this class.
     */
    private void handleIntent() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }
        Log.i(TAG, "handleIntent: tabs -> " + getIntent().getStringExtra(StringContract.IntentStrings.TABS));
        if (getIntent().hasExtra(StringContract.IntentStrings.TYPE)) {
            this.type = getIntent().getStringExtra(StringContract.IntentStrings.TYPE);
            Log.i(TAG, "handleIntent:  Type = " + getIntent().getStringExtra(StringContract.IntentStrings.TYPE));
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.MESSAGE_TYPE)) {
            messageType = getIntent().getStringExtra(StringContract.IntentStrings.MESSAGE_TYPE);
            Log.i(TAG, "handleIntent: messageType = " + getIntent().getStringExtra(StringContract.IntentStrings.MESSAGE_TYPE));
        }

        if (getIntent().hasExtra(CometChatConstants.MESSAGE_TYPE_TEXT)) {
            textMessage = getIntent().getStringExtra(CometChatConstants.MESSAGE_TYPE_TEXT);
            Log.i(TAG, "handleIntent: textMessage = " + textMessage);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL)) {
            mediaMessageUrl = getIntent().getStringExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_URL);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE)) {
            mediaMessageSize = getIntent().getIntExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_SIZE, 0);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION)) {
            mediaMessageExtension = getIntent().getStringExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_EXTENSION);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME)) {
            mediaMessageName = getIntent().getStringExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_NAME);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_MIME_TYPE)) {
            mediaMessageMime = getIntent().getStringExtra(StringContract.IntentStrings.MESSAGE_TYPE_IMAGE_MIME_TYPE);
        }

        if (getIntent().hasExtra(StringContract.IntentStrings.UID)) {
            SenderId = getIntent().getStringExtra(StringContract.IntentStrings.UID);
            Log.i(TAG, "handleIntent: SenderId " + SenderId);
        }

        if (getIntent().hasExtra(StringContract.IntentStrings.NAME)) {
            uname = getIntent().getStringExtra(StringContract.IntentStrings.NAME);
        }

        if (getIntent().hasExtra(StringContract.IntentStrings.STATUS)) {
            status = getIntent().getStringExtra(StringContract.IntentStrings.STATUS);
        }

        if (getIntent().hasExtra(StringContract.IntentStrings.ID)) {
            id = getIntent().getIntExtra(StringContract.IntentStrings.ID, 0);
        }

        if (getIntent().hasExtra(StringContract.IntentStrings.TABS)) {
            tabs = getIntent().getStringExtra(StringContract.IntentStrings.TABS);
            Log.i(TAG, "handleIntent: tabs = " + tabs);
        }

        if (getIntent().hasExtra("teamId")) {
            team_Ids = getIntent().getStringExtra("teamId");
            Log.i(TAG, "handleIntent: team_Ids = " + team_Ids);
        }

        /*avatarUrl = getIntent().getStringExtra(""+StringContract.IntentStrings.AVATAR);
        status =  getIntent().getStringExtra(StringContract.IntentStrings.STATUS);
        uname =  getIntent().getStringExtra(StringContract.IntentStrings.NAME);
        mtype =  getIntent().getStringExtra(StringContract.IntentStrings.TYPE);
        tabs =  getIntent().getStringExtra(StringContract.IntentStrings.TABS);
        team_Ids =  getIntent().getStringExtra("teamId");

        if (mtype != null && type.equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
            SenderId = getIntent().getStringExtra(StringContract.IntentStrings.GUID);
            memberCount =getIntent().getStringExtra(StringContract.IntentStrings.MEMBER_COUNT);
            groupDesc = getIntent().getStringExtra(StringContract.IntentStrings.GROUP_DESC);
            groupPassword = getIntent().getStringExtra(StringContract.IntentStrings.GROUP_PASSWORD);
            groupType = getIntent().getStringExtra(StringContract.IntentStrings.GROUP_TYPE);
            groupOwner = getIntent().getStringExtra(StringContract.IntentStrings.GROUP_OWNER);

        }*/
    }

    /**
     * This method is used to initialize the views
     */
    public void init() {
        // Inflate the layout 
        MaterialToolbar toolbar = findViewById(R.id.forward_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Utils.changeToolbarFont(toolbar) != null) {
            Utils.changeToolbarFont(toolbar).setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        }
        selectedUsers = findViewById(R.id.selected_user);

        forwardBtn = findViewById(R.id.btn_forward);

        rvConversationList = findViewById(R.id.rv_conversation_list);

        etSearch = findViewById(R.id.search_bar);

        clearSearch = findViewById(R.id.clear_search);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1)
                    clearSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() != 0) {
                    if (conversationListAdapter != null)
                        conversationListAdapter.getFilter().filter(editable.toString());
                }
            }
        });

        etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (conversationListAdapter != null)
                    conversationListAdapter.getFilter().filter(textView.getText().toString());
                clearSearch.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        clearSearch.setOnClickListener(view1 -> {
            etSearch.setText("");
            clearSearch.setVisibility(View.GONE);
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            // Hide the soft keyboard
            inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        });

        rvConversationList.setItemClickListener(new OnItemClickListener<Conversation>() {
            @Override
            public void OnItemClick(Conversation conversation, int position) {
                if (userList != null && userList.size() < 5) {
                    if (!userList.containsKey(conversation.getConversationId())) {
                        userList.put(conversation.getConversationId(), conversation);
                        Chip chip = new Chip(CometChatForwardMessageScreenActivity.this);

                        if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            name = ((User) conversation.getConversationWith()).getName();
                            avatar = ((User) conversation.getConversationWith()).getAvatar();
                        } else {
                            name = ((Group) conversation.getConversationWith()).getName();
                            avatar = ((Group) conversation.getConversationWith()).getIcon();
                        }
                        chip.setText(name);
                        Glide.with(CometChatForwardMessageScreenActivity.this).load(avatar).placeholder(R.drawable.ic_contacts).transform(new CircleCrop()).into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                chip.setChipIcon(resource);
                            }
                        });
                        chip.setCloseIconVisible(true);
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View vw) {
                                userList.remove(conversation.getConversationId());
                                selectedUsers.removeView(vw);
                                checkUserList();

                            }
                        });
                        selectedUsers.addView(chip, 0);
                    }
                    checkUserList();
                } else {
                    Toast.makeText(CometChatForwardMessageScreenActivity.this, "You cannot forward message to more than 5 members", Toast.LENGTH_LONG).show();
                }
            }

        });

        //It sends message to selected users present in userList using thread. So UI thread doesn't get heavy.
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listSize = userList.size();
                Log.i(TAG, General.MY_TAG + "onClick: forward btn clicked...");
                if (messageType != null && messageType.equals(CometChatConstants.MESSAGE_TYPE_TEXT)) {

                    for (int i = 0; i <= userList.size() - 1; i++) {
                        listCounter = listCounter + 1;
                        Log.i(TAG, General.MY_TAG + " run: listCounter " + listCounter);
                        Conversation conversation = new ArrayList<>(userList.values()).get(i);
                        TextMessage message;
                        String uid;
                        String type;

                        Log.e(TAG, "run: " + conversation.getConversationId());
                        SharedPreferences forwardPref = getSharedPreferences("forwardPref", MODE_PRIVATE);
                        Log.i(TAG, "onClick: Broadcast onJsonReceived 1");

                        if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER)) {
                            uid = ((User) conversation.getConversationWith()).getUid();
                            type = CometChatConstants.RECEIVER_TYPE_USER;
                            Log.i(TAG, "onClick: roadcast onJsonReceived user id pref " + forwardPref.getString("UserId", ""));
                            Log.i(TAG, "onClick: roadcast onJsonReceived user id pref " + uid);
                            /*if (uid.equalsIgnoreCase(forwardPref.getString("UserId", ""))){
                                Log.i(TAG, "onClick: Broadcast onJsonReceived user");
                                Intent intent = new Intent("forwardIntent");
                                Bundle bundle = new Bundle();
                                bundle.putString("uid", ""+uid);
                                bundle.putString("textMessage", ""+textMessage);
                                bundle.putString("type", ""+type);
                                TestModel testModel=new TestModel();
                                testModel.setBaseMessage(conversation.getLastMessage());
                                bundle.putParcelable("message", (Parcelable) testModel);
                                intent.putExtras(bundle);
                                LocalBroadcastManager.getInstance(CometChatForwardMessageScreenActivity.this).sendBroadcast(intent);
                                finish();
                            }*/
                        } else {
                            Log.i(TAG, "onClick: Broadcast onJsonReceived group ");
                            uid = ((Group) conversation.getConversationWith()).getGuid();
                            type = CometChatConstants.RECEIVER_TYPE_GROUP;
                            //commented code is running but temoporary commented and will use when work on this
                           /* if (uid.equalsIgnoreCase(forwardPref.getString("UserId", ""))){
                                Intent intent = new Intent("forwardIntent");
                                Bundle bundle = new Bundle();
                                bundle.putString("uid", ""+uid);
                                bundle.putString("textMessage", ""+textMessage);
                                bundle.putString("type", ""+type);
                                bundle.putString("message", ""+conversation);
                                intent.putExtras(bundle);
                                LocalBroadcastManager.getInstance(CometChatForwardMessageScreenActivity.this).sendBroadcast(intent);
                            }*/
                        }

                        if (i == userList.size() - 1) {
                            Intent intent = createIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK/* | Intent.FLAG_ACTIVITY_CLEAR_TASK*/);
                            startActivity(intent);
                            finish();
                            //Intent intent = new Intent(CometChatForwardMessageScreenActivity.this, CometChatUI.class);}

                            message = new TextMessage(uid, textMessage, type);
                            setMetadata(message);
                        }
                    }
                    /*commented by rahul maske to redirect on back page and it was sending on CometChatUnified.class */
                            /*if (i == userList.size() - 1) {
                                Log.i(TAG, "onClick: messageTypeText");
                                Intent intent = new Intent(CometChatForwardMessageScreenActivity.this, CometChatUnified.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }*/
                    //};
                    //Thread mythread = new Thread(runnable);
                    // mythread.start();
                } else if (messageType != null && !messageType.equals(StringContract.IntentStrings.INTENT_MEDIA_MESSAGE)) {
                    //new Thread(() -> {
                    Log.i(TAG, General.MY_TAG + " onClick: INTENT_MEDIA_MESSAGE ");
                    Runnable runnableMedia = new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i <= userList.size() - 1; i++) {
                                listCounter = listCounter + 1;
                                Conversation conversation = new ArrayList<>(userList.values()).get(i);
                                MediaMessage message;
                                String uid;
                                String type;

                                Log.e(TAG, "run: " + conversation.getConversationId());

                                if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER)) {
                                    uid = ((User) conversation.getConversationWith()).getUid();
                                    type = CometChatConstants.RECEIVER_TYPE_USER;
                                } else {
                                    uid = ((Group) conversation.getConversationWith()).getGuid();
                                    type = CometChatConstants.RECEIVER_TYPE_GROUP;
                                }

                                message = new MediaMessage(uid, null, messageType, type);
                                Attachment attachment = new Attachment();
                                attachment.setFileUrl(mediaMessageUrl);
                                attachment.setFileMimeType(mediaMessageMime);
                                attachment.setFileSize(mediaMessageSize);
                                attachment.setFileExtension(mediaMessageExtension);
                                attachment.setFileName(mediaMessageName);
                                message.setAttachment(attachment);
                                Log.e(TAG, "onClick: " + attachment.toString());
                                sendMediaMessage(message);

                                if (listCounter == listSize) {
                                    Log.i(TAG, "onSuccess: listCounter" + listCounter + " listSize " + listSize);
                                }

                                /*commented by rahul maske to redirect on back page and it was sending on CometChatUnified.class */
                            /*if (i == userList.size() - 1) {
                                Log.i(TAG, "onClick: messageTypeMedia");
                                Intent intent = new Intent(CometChatForwardMessageScreenActivity.this, CometChatUnified.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }*/
                            }
                        }
                    };
                    Thread myThreadMedia = new Thread(runnableMedia);
                    myThreadMedia.start();

                    //}).start();
                } else {
                    Log.i(TAG, General.MY_TAG + " onClick: messageTypeElse");
                    Runnable runnableMedia = new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i <= userList.size() - 1; i++) {
                                listCounter = listCounter + 1;
                                Conversation conversation = new ArrayList<>(userList.values()).get(i);
                                MediaMessage message;
                                String uid;
                                String type;
                                Log.e(TAG, "run: " + conversation.getConversationId());
                                if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER)) {
                                    uid = ((User) conversation.getConversationWith()).getUid();
                                    type = CometChatConstants.RECEIVER_TYPE_USER;
                                } else {
                                    uid = ((Group) conversation.getConversationWith()).getGuid();
                                    type = CometChatConstants.RECEIVER_TYPE_GROUP;
                                }
                                File file = MediaUtils.getRealPath(CometChatForwardMessageScreenActivity.this, Uri.parse(mediaMessageUrl));
                                message = new MediaMessage(uid, file, CometChatConstants.MESSAGE_TYPE_IMAGE, type);
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("path", mediaMessageUrl);
                                    message.setMetadata(jsonObject);
                                } catch (Exception e) {
                                    Log.e(TAG, General.MY_TAG + " onError: " + e.getMessage());
                                }
                                sendMediaMessage(message);
                                if (listCounter == listSize) {
                                    Log.i(TAG, "onSuccess: listCounter" + listCounter + " listSize " + listSize);
                                    onBackPressed();
                                }
                                /*commented by rahul maske to redirect on back page and it was sending on CometChatUnified.class */
                            /*if (i == userList.size() - 1) {
                                Log.i(TAG, "onClick: messageTypeMedia");
                                Intent intent = new Intent(CometChatForwardMessageScreenActivity.this, CometChatUnified.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }*/
                            }

                        }
                    };
                    Thread myThreadMedia = new Thread(runnableMedia);
                    myThreadMedia.start();
                }
            }
        });
        rvConversationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    makeConversationList();
                }

            }
        });

    }

    public void setMetadata(TextMessage message) {
        Log.i(TAG, General.MY_TAG + " setMetadata: ");
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
                break;

            case "4":
                break;
        }
        message.setMetadata(jsonObject);
        sendMessage(message);
       /* if (listCounter == listSize) {
            Log.i(TAG, "onSuccess: listCounter" + listCounter + " listSize " + listSize);
            Toast.makeText(this, "Message forwarded successfully", Toast.LENGTH_SHORT).show();
            *//*new Handler().post(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            });*//*




        }*/
    }

    private void sendMessage(TextMessage message) {
        CometChat.sendMessage(message, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                Log.e(TAG, General.MY_TAG + " onSuccess: receiver uid" + textMessage.getReceiverUid());
                /*called previous activity by rahul maske */
                /*if (listCounter == listSize) {
                    Log.i(TAG, "onSuccess: listCounter"+listCounter +" listSize "+listSize);
                    onBackPressed();
                    */

                /* if (listCounter==listSize-1) {
                 *//* Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                            Toast.makeText(CometChatForwardMessageScreenActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                        }
                    });*//*

                 *//*Intent intent = createIntent();
                    startActivity(intent);
                    finish();*//*
                }
            }
*/
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, General.MY_TAG + " onError: " + e.getMessage());
            }
        });
    }

    public void sendMediaMessage(MediaMessage mediaMessage) {
        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                Log.d(TAG, "sendMediaMessage onSuccess: " + mediaMessage.toString());
                /*called previous activity by rahul maske */
                if (listCounter == listSize - 1) {
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    });
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserList() {
        Log.e(TAG, "checkUserList: " + userList.size());
        if (userList.size() > 0) {
            forwardBtn.setVisibility(View.VISIBLE);
        } else {
            forwardBtn.setVisibility(View.GONE);
        }
    }

    /**
     * This method is used to fetch conversations
     */
    private void makeConversationList() {
        if (conversationsRequest == null) {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(50).build();
        }
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversationsList) {
                if (conversationsList.size() != 0) {
                    List<Conversation> conversationsListCustom = new ArrayList<>();
                    for (Conversation item : conversationsList) {
                        if (tabs.equals("1")) {
                            if (item.getConversationType().equals("user")) {
                                try {
                                    if (item.getLastMessage().getMetadata().getString("team_logs_id").equalsIgnoreCase("0")) {
                                        Log.i(TAG, "onSuccess: user conversation " + item);
                                        if (!((User) item.getConversationWith()).getUid().equalsIgnoreCase(SenderId)) {
                                            conversationsListCustom.add(item);
                                        }else{
                                            Log.i(TAG, "onSuccess: user from chat " + ((User) item.getConversationWith()).getName());
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (tabs.equals("2")) {
                            if (item.getConversationType().equals("group")) {
                                Log.i(TAG, "onSuccess: group conversation " + item);
                                if (!((Group) item.getConversationWith()).getGuid().equalsIgnoreCase(SenderId)) {
                                    conversationsListCustom.add(item);
                                }else{
                                    Log.i(TAG, "onSuccess: user from chat " + ((Group) item.getConversationWith()).getName());
                                }
                            }
                        }
                        /*if (item.getConversationType().equals("user")) {
                            if (tabs.equals("1")) {
                                conversationsListCustom.add(item);
                            }
                        }else if (item.getConversationType().equals("group")) {
                        if (tabs.equals("2")) {
                            conversationsListCustom.add(item);
                        }
                    }*/
                    }
                    setAdapter(conversationsListCustom);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(List<Conversation> conversations) {
        if (conversationListAdapter == null) {
            conversationListAdapter = new ConversationListAdapter(this, conversations);
            rvConversationList.setAdapter(conversationListAdapter);
        } else {
            conversationListAdapter.updateList(conversations);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        conversationsRequest = null;
        conversationListAdapter = null;
        makeConversationList();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        CometChat.removeMessageListener(TAG);
        userList.clear();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public Intent createIntent() {
        Intent intent = new Intent(CometChatForwardMessageScreenActivity.this, CometChatMessageListActivity.class);
        if (getIntent() != null) {
            Log.i(TAG, "createIntent: avatar " + avatar);
            Log.i(TAG, "createIntent: uname " + uname);
            Log.i(TAG, "createIntent: messageType " + messageType);
            Log.i(TAG, "createIntent: SenderId " + SenderId);
            Log.i(TAG, "createIntent: status " + status);
            Log.i(TAG, "createIntent: tabs " + tabs);
            Log.i(TAG, "createIntent: team_Ids " + team_Ids);
            intent.putExtra(StringContract.IntentStrings.AVATAR, avatar);
            intent.putExtra(StringContract.IntentStrings.NAME, uname);
            Log.i(TAG, "createIntent: " + messageType);
            intent.putExtra(StringContract.IntentStrings.TYPE, type);
            intent.putExtra(StringContract.IntentStrings.TIME_ZONE, messageType);
            if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                intent.putExtra(StringContract.IntentStrings.SENDER_ID, SenderId);
                intent.putExtra(StringContract.IntentStrings.STATUS, status);
                intent.putExtra(StringContract.IntentStrings.TABS, tabs);
                intent.putExtra("teamId", team_Ids);
            } else {
                intent.putExtra(StringContract.IntentStrings.GUID, getIntent().getStringExtra(StringContract.IntentStrings.GUID));
                intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, getIntent().getStringExtra(StringContract.IntentStrings.GROUP_OWNER));
                intent.putExtra(StringContract.IntentStrings.MEMBER_COUNT, getIntent().getIntExtra(StringContract.IntentStrings.MEMBER_COUNT, 0));
                intent.putExtra(StringContract.IntentStrings.GROUP_TYPE, getIntent().getStringExtra(StringContract.IntentStrings.GROUP_TYPE));
                intent.putExtra(StringContract.IntentStrings.GROUP_DESC, getIntent().getStringExtra(StringContract.IntentStrings.GROUP_DESC));
                intent.putExtra(StringContract.IntentStrings.GROUP_PASSWORD, getIntent().getStringExtra(StringContract.IntentStrings.GROUP_PASSWORD));
                intent.putExtra(StringContract.IntentStrings.TABS, getIntent().getStringExtra(StringContract.IntentStrings.TABS));
                intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, groupOwner);
            }
        }
        return intent;
    }
}
