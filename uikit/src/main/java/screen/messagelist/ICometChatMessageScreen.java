package screen.messagelist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TypingIndicator;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
/* this interface is create by rahul maske
 * it include all the methods which are implemented in CometChatMessageScreen
 * */
public interface ICometChatMessageScreen {
    public void handleArguments();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    public void initViewComponent(View view);
    public void checkOnGoingCall(String callType);
    public void initiatecall(Context context, String recieverID, String receiverType, String callType);
    public void initiateGroupCall(String recieverID, String receiverType, String callType);
    public void checkOnGoingCall();
    public void setComposeBoxListener();
    public void getLocation();
    public void initAlert(JSONObject customData);
    public void sendCustomMessage(String stickers, JSONObject stickerData);
    public void turnOnLocation();
    public void SendCustomMessage(String loudScreaming, String id);
    public void initLocation();
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    public void showSnackBar(View view, String message);
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater);
    public boolean onOptionsItemSelected(@NonNull MenuItem item);
    public void getMember();
    public void unblockUser();
    public void setSubTitle(String... users);
    public void fetchMessage();
    public List<BaseMessage> filterBaseMessages(List<BaseMessage> baseMessages);
    public void stopHideShimmer();
    public void getSmartReplyList(BaseMessage baseMessage);
    public void setSmartReplyAdapter(List<String> replyList);
    public void initMessageAdapter(List<BaseMessage> messageList);
    public void sendTypingIndicator(boolean isEnd);
    public void endTypingTimer();
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    public void sendMediaMessage(File file, String filetype);
    public void getUser();
    public void setAvatar();
    public void getGroup();
    public void sendMessage(String message);
    public void deleteMessage(BaseMessage baseMessage);
    public void editMessage(BaseMessage baseMessage, String message);
    public void addGroupListener();
    public void addUserListener();
    public void markMessageAsRead(BaseMessage baseMessage);
    public void addMessageListener();
    public void setMessageReciept(MessageReceipt messageReceipt);
    public void setTypingIndicator(TypingIndicator typingIndicator, boolean isShow);
    public void onMessageReceived(BaseMessage message);
    public void setMessage(BaseMessage message);
    public void checkSmartReply(BaseMessage lastMessage);
    public void typingIndicator(TypingIndicator typingIndicator, boolean show);
    public void removeMessageListener();
    public void removeUserListener();
    public void onPause();
    public void removeGroupListener();
    public void onResume();
    public void onAttach(Context context);

    public void onClick(View view);
    public void startForwardMessageActivity();
    public void shareMessage();
    public void startThreadActivity();
    public void setLongMessageClick(List<BaseMessage> baseMessagesList);
    public void replyMessage();
    public void handleDialogClose(DialogInterface dialog);
}
