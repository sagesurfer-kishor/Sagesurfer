package listeners;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;

import screen.CometChatCallActivity;
import screen.CometChatStartCallActivity;
import screen.messagelist.General;
import screen.messagelist.Preferences;
import utils.Utils;

import static android.content.Context.MODE_PRIVATE;
import static org.webrtc.ContextUtils.getApplicationContext;

/**
 * CometChatCallListener.class is used to add and remove CallListener in app.
 * It also has method to make call to user passed in parameter;
 */
public class CometChatCallListener {

    public static boolean isInitialized;
    static int developmenenen = 0;

    /**
     * This method is used to add CallListener in app
     *
     * @param TAG     is a unique Identifier
     * @param context is a object of Context.
     */
    String sessionId;

    /*this listner is call when user get call*/
    public static void addCallListener(String TAG, Context context) {
        isInitialized = true;

        CometChat.addCallListener(TAG, new CometChat.CallListener() {
            @Override
            public void onIncomingCallReceived(Call call) {
                /*this method is called when user get call in his phone but before accepted it calls..*/
                if (CometChat.getActiveCall() == null) {
                    Log.i(TAG, General.MY_TAG + " onIncomingCallReceived: ");
                    /*This spCallScreenFlag preferences is create for opening call receiving popup only once because it is opening call receving popup 3 times */

                    SharedPreferences spCallScreenFlag = context.getSharedPreferences(" call_popup_preferences ", MODE_PRIVATE);
                    SharedPreferences.Editor spEditorCallScreenFlag = spCallScreenFlag.edit();
                    spEditorCallScreenFlag.putBoolean("openCallPopup", true);
                    spEditorCallScreenFlag.apply();

                    Log.i(TAG, General.MY_TAG+" onIncomingCallReceived: openCallPopup " + spCallScreenFlag.getBoolean("openCallPopup", false));

                    if (call.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                        /*here we are opening the call receive popup so that user can accept the call..*/
                        Log.i(TAG, General.MY_TAG + " onIncomingCallReceived: initiater " + (User) call.getCallInitiator());
                        Log.i(TAG, General.MY_TAG + " onIncomingCallReceived: type " + call.getType());
                        Log.i(TAG, General.MY_TAG + " onIncomingCallReceived: sessionId " + call.getSessionId());
                        Utils.startCallIntent(context, (User) call.getCallInitiator(), call.getType(),
                                false, call.getSessionId());
                    } else {
                        Utils.startGroupCallIntent(context, (Group) call.getReceiver(), call.getType(),
                                false, call.getSessionId());
                    }
                } else {
                    CometChat.rejectCall(call.getSessionId(), CometChatConstants.CALL_STATUS_BUSY, new CometChat.CallbackListener<Call>() {
                        @Override
                        public void onSuccess(Call call) {
                            Log.i(TAG, General.MY_TAG + " onSuccess: rejectCall");
                        }

                        @Override
                        public void onError(CometChatException e) {
                            Toast.makeText(context, General.MY_TAG + " Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onOutgoingCallAccepted(Call call) {
                /*code updated by rahul maske and deva for calling session was regenerating and creating new instances
                 * */

                if (CometChatStartCallActivity.activity == null) {
                    if (CometChatCallActivity.callActivity != null) {
                        CometChatCallActivity.cometChatAudioHelper.stop(false);
                        Utils.startCall(CometChatCallActivity.callActivity, call);
                    }
                } else {
                    CometChatStartCallActivity.activity.finish();
                    if (CometChatCallActivity.callActivity != null) {
                        CometChatCallActivity.cometChatAudioHelper.stop(false);
                        Utils.startCall(CometChatCallActivity.callActivity, call);
                    }

               /* SharedPreferences sharedPreferencesSessionId = context.getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
                String myCallGeneratedSession = sharedPreferencesSessionId.getString("sessionId", null);
                SharedPreferences.Editor spSessionEditor = sharedPreferencesSessionId.edit();

                if (!sharedPreferencesSessionId.getBoolean("onGoingCall", false)) {
                    Log.i(TAG, General.MY_TAG + " onOutgoingCallAccepted: onGoingCall condition");
                    //call.getSessionId()
                    if (CometChatCallActivity.callActivity != null) {
                        Log.i(TAG, General.MY_TAG + " onOutgoingCallAccepted: ");
                        spSessionEditor.putBoolean("onGoingCall", true);
                        CometChatCallActivity.cometChatAudioHelper.stop(false);
                        Utils.startCall(CometChatCallActivity.callActivity, call);
                    } else {
                        Log.i(TAG, "onOutgoingCallAccepted: 1");
                    }

                } else {
                    Log.i(TAG, "onOutgoingCallAccepted: 3");
                }*/

                /*if (call.getSessionId() != myCallGeneratedSession) {
                    developmenenen = 0;
                }
                    developmenenen++;

                    if (developmenenen != 1) {
                        Log.e(TAG, "onOutgoingCallAccepted: developmenenen!=1");
                    } else {
                        Log.e(TAG, "onOutgoingCallAccepted: " + sharedPreferencesSessionId.getString("sessionId", null));
                        if (CometChat.getActiveCall() == null) {
                            //call.getSessionId()
                            if (CometChatCallActivity.callActivity != null) {
                                CometChatCallActivity.cometChatAudioHelper.stop(false);
                                Utils.startCall(CometChatCallActivity.callActivity, call);
                            }
                        }
                    }*/
                    /*var sessionId = call.sessionId;
                        if(sessionId !=$('#comet_chat_video_session').val()) {
                            developmenenen=0;
                        }
                        $('#comet_chat_video_session').val(sessionId);
                        developmenenen++;
                        if(developmenenen !=1){
                            return false;
}                    */

                    //commetchat team suggested code comented because it was not working
                /*if (CometChat.getActiveCall() == null) {
                    //call.getSessionId()
                    if (CometChatCallActivity.callActivity != null) {
                        CometChatCallActivity.cometChatAudioHelper.stop(false);
                        Utils.startCall(CometChatCallActivity.callActivity, call);
                    }
                }else {
                    CometChatStartCallActivity.activity.finish();
                    if (CometChatCallActivity.callActivity != null) {
                        CometChatCallActivity.cometChatAudioHelper.stop(false);
                        Utils.startCall(CometChatCallActivity.callActivity, call);
                    }
                }*/
                }
            }
            @Override
            public void onOutgoingCallRejected(Call call) {
                Log.i(TAG, General.MY_TAG + " onOutgoingCallRejected: ");
                if (CometChatCallActivity.callActivity != null)
                    CometChatCallActivity.callActivity.finish();
            }

            @Override
            public void onIncomingCallCancelled(Call call) {
                if (CometChatCallActivity.callActivity != null)
                    Log.i(TAG, General.MY_TAG + " onIncomingCallCancelled: " + call);
                CometChatCallActivity.callActivity.finish();
            }
        });
    }

    /**
     * It is used to remove call listener from app.
     *
     * @param TAG is a unique Identifier
     */
    public static void removeCallListener(String TAG) {

        isInitialized = false;
        CometChat.removeCallListener(TAG);
    }

    /**
     * This method is used to make a initiate a call.
     *
     * @param context      is a object of Context.
     * @param receiverId   is a String, It is unique receiverId. It can be either uid of user or
     *                     guid of group
     * @param receiverType is a String, It can be either CometChatConstant.RECEIVER_TYPE_USER or
     *                     CometChatConstant.RECEIVER_TYPE_GROUP
     * @param callType     is a String, It is call type which can be either CometChatConstant.CALL_TYPE_AUDIO
     *                     or CometChatConstant.CALL_TYPE_VIDEO
     * @see CometChat#initiateCall(Call, CometChat.CallbackListener)
     */
    public static void makeCall(Context context, String receiverId, String receiverType, String callType) {
        Utils.initiatecall(context, receiverId, receiverType, callType);
    }
}
