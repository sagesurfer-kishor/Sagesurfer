package screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.rtc.model.AudioMode;
import com.cometchat.pro.uikit.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;

import constant.StringContract;
import okhttp3.RequestBody;
import screen.messagelist.General;
import screen.messagelist.NetworkCall_;
import screen.messagelist.Urls_;

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
                //Snackbar.make(rl_call_screen, "User Left: " + user.getName(), Snackbar.LENGTH_LONG).show();
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
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        logOutEnrtyCometchat();
                    }
                };
                Thread callLog=new Thread(runnable);
                callLog.start();
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


    // added by rahul maske but not used
    private void logOutEnrtyCometchat() {
        SharedPreferences UserInfoForUIKitPref = getApplicationContext().getSharedPreferences("UserInfoForUIKitPref", MODE_PRIVATE);
        String UserId = UserInfoForUIKitPref.getString(General.USER_ID, null);
        SharedPreferences domainUrlPref = getApplicationContext().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String DomainURL = domainUrlPref.getString(General.DOMAIN, null);
        SharedPreferences preferensesCalling = getApplicationContext().getSharedPreferences("callingPreferences", MODE_PRIVATE);
        String group_id=preferensesCalling.getString("group_id","");
        String team_id=preferensesCalling.getString("team_id","");

        Log.i(TAG, "Calling test tag logOutEnrtyCometchat: type"+preferensesCalling.getString("type","")+" chat_type "+ preferensesCalling.getString("chat_type","")
        +" Group_id "+group_id +" UserId "+UserId);
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "cometchat_outlog");
        requestMap.put(General.MISSED_CALL, "");
        requestMap.put(General.COMET_CHAT_TYPE, "" + preferensesCalling.getString("type",""));
        requestMap.put(General.CHAT_TYPE, preferensesCalling.getString("chat_type",""));
        requestMap.put(General.GROUP_ID,  group_id);
        requestMap.put(General.USER_ID, UserId);
        requestMap.put(General.TEAM_ID, team_id);

        RequestBody requestBody = NetworkCall_.make(requestMap, DomainURL + url, TAG, getApplicationContext());
        // Log.e(TAG, "saveChatLogToTheServer: request body " + requestBody);
        Log.i(TAG,  " Calling test tag  logOutEnrtyCometchat: Domain " + DomainURL + url);
        try {
            if (requestBody != null) {
                String response = NetworkCall_.post(DomainURL + url, requestBody, TAG, getApplicationContext());
                if (response != null) {
                    Log.i(TAG, "Calling test tag  logOutEnrtyCometchat:  response " + response);
                } else {
                    Log.i(TAG, "Calling test tag logOutEnrtyCometchat:  null  ");
                }
            } else {
                Log.i(TAG, "Calling test tag  logOutEnrtyCometchat:  null2");
            }
        } catch (Exception e) {
            Log.i(TAG, "Calling test tag logOutEnrtyCometchat: " + e.getMessage());
            e.printStackTrace();
        }
    }
}