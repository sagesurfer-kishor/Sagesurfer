package screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.rtc.model.AudioMode;
import com.cometchat.pro.uikit.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import constant.StringContract;
import screen.messagelist.General;

public class CometChatStartCallActivity extends AppCompatActivity {
    public static CometChatStartCallActivity activity;
    private RelativeLayout rl_call_screen;
    private String sessionID;
    private static final String TAG = "CometChatStartCallActiv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
        setContentView(R.layout.activity_comet_chat_start_call);



        Log.e(TAG , General.MY_TAG + " onCreate: ");
        rl_call_screen = findViewById(R.id.call_view);


        sessionID = getIntent().getStringExtra(StringContract.IntentStrings.SESSION_ID);
        CallSettings callSettings = new CallSettings.CallSettingsBuilder(this, rl_call_screen)
                .setSessionId(sessionID)
                .setMode(CallSettings.MODE_SINGLE)
                .build();

        Log.e(TAG, General.MY_TAG +"onCreate: sessionId "+sessionID);
        CometChat.startCall(callSettings, new CometChat.OngoingCallListener() {
            @Override
            public void onUserJoined(User user) {
                Log.e(TAG ,General.MY_TAG +  " startCall -> onUserJoined: "+ user.getUid());
            }

            @Override
            public void onUserLeft(User user) {
                Snackbar.make(rl_call_screen, "User Left: " + user.getName(), Snackbar.LENGTH_LONG).show();
                Log.e(TAG ,General.MY_TAG +  " startCall -> onUserLeft: "+ user.getUid());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG ,General.MY_TAG +  " startCall -> onError: "+ e.getMessage());

            }

            @Override
            public void onCallEnded(Call call) {
                Log.e(General.MY_TAG + " onCallEnded: ",call.toString());
                Log.e(TAG ,General.MY_TAG +  " startCall -> onCallEnded: "+ call.toString());
                finish();
            }

            @Override
            public void onUserListUpdated(List<User> list) {

            }

            @Override
            public void onAudioModesUpdated(List<AudioMode> list) {

            }
        });
    }


    // added by rahul maske but not used
    public void endCall() {
        CometChat.endCall(sessionID, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                // handle end call success
                Log.i(TAG, General.MY_TAG +" onSuccess: endCall() " + call.toString());
            }

            @Override
            public void onError(CometChatException e) {
                // handled end call error
                Log.i(TAG, General.MY_TAG +" onError: endCall() " + e.getMessage());
            }
        });
    }
}