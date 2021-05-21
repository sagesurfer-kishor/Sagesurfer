package screen.messagelist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.uikit.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import adapter.MessageAdapter;
import constant.StringContract;
import listeners.MessageActionCloseListener;
import listeners.OnMessageLongClick;

/**
 * Purpose - CometChatMessageListActivity.class is a Activity used to display messages using CometChatMessageScreen.class. It takes
 * parameter like TYPE to differentiate between User MessageScreen & Group MessageScreen.
 * <p>
 * It passes parameters like UID (userID) ,AVATAR (userAvatar) ,NAME (userName) ,STATUS (userStatus) to CometChatMessageScreen.class
 * if TYPE is CometChatConstant.RECEIVER_TYPE_USER
 * <p>
 * It passes parameters like GUID (groupID) ,AVATAR (groupIcon) ,NAME (groupName) ,GROUP_OWNER (groupOwner) to CometChatMessageScreen.class
 * if TYPE is CometChatConstant.RECEIVER_TYPE_GROUP
 *
 * @see CometChatConstants
 * @see CometChatMessageScreen
 */

public class CometChatMessageListActivity extends AppCompatActivity implements MessageAdapter.OnMessageLongClick {
    private static final String TAG = "CometChatMessageListAct";
    private OnMessageLongClick messageLongClick;
    Fragment fragment;
    {
        try {
            fragment = new CometChatMessageScreen();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    FloatingActionButton btnAdd;
    SharedPreferences sp;
    SharedPreferences preferencesCheckCurrentActivity;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        setContentView(R.layout.activity_cometchat_message_list);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        if (getIntent() != null) {
            Bundle bundle = new Bundle();
            bundle.putString(StringContract.IntentStrings.AVATAR, getIntent().getStringExtra(StringContract.IntentStrings.AVATAR));
            bundle.putString(StringContract.IntentStrings.NAME, getIntent().getStringExtra(StringContract.IntentStrings.NAME));
            bundle.putString(StringContract.IntentStrings.TYPE, getIntent().getStringExtra(StringContract.IntentStrings.TYPE));
            bundle.putString(StringContract.IntentStrings.TIME_ZONE, getIntent().getStringExtra(StringContract.IntentStrings.TYPE));
           // Preferences.save(General.USER_ID,getIntent().getStringExtra(General.USER_ID));
            //checked type is user or group

            Log.i(TAG, "onCreate: tabs -> "+getIntent().getStringExtra(StringContract.IntentStrings.TABS));
            Log.i(TAG, "onCreate: name -> "+getIntent().getStringExtra(StringContract.IntentStrings.NAME));
            Log.i(TAG, "onCreate: avtar -> "+getIntent().getStringExtra(StringContract.IntentStrings.AVATAR));
            Log.i(TAG, "onCreate: type -> "+getIntent().getStringExtra(StringContract.IntentStrings.TYPE));


            if (getIntent().hasExtra(StringContract.IntentStrings.TYPE) && getIntent().getStringExtra(StringContract.IntentStrings.TYPE).equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                bundle.putString(StringContract.IntentStrings.SENDER_ID, getIntent().getStringExtra(StringContract.IntentStrings.SENDER_ID));
                bundle.putString(StringContract.IntentStrings.STATUS, getIntent().getStringExtra(StringContract.IntentStrings.STATUS));
                bundle.putString(StringContract.IntentStrings.TABS, getIntent().getStringExtra(StringContract.IntentStrings.TABS));
                bundle.putString("teamId", getIntent().getStringExtra("teamId"));

                Log.e(TAG, "onCreate: UID"+getIntent().getStringExtra(StringContract.IntentStrings.UID) );
                Log.e(TAG, "onCreate: teamId"+getIntent().getStringExtra("teamId") );
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("UserIds", getIntent().getStringExtra(StringContract.IntentStrings.UID));
                editor.putString("types", getIntent().getStringExtra(StringContract.IntentStrings.TYPE));
                editor.commit();
            } else {
                bundle.putString(StringContract.IntentStrings.GUID, getIntent().getStringExtra(StringContract.IntentStrings.GUID));
                bundle.putString(StringContract.IntentStrings.GROUP_OWNER, getIntent().getStringExtra(StringContract.IntentStrings.GROUP_OWNER));
                //bundle.putInt(StringContract.IntentStrings.MEMBER_COUNT, getIntent().getIntExtra(StringContract.IntentStrings.MEMBER_COUNT, 0));
                bundle.putString(StringContract.IntentStrings.GROUP_TYPE, getIntent().getStringExtra(StringContract.IntentStrings.GROUP_TYPE));
                bundle.putString(StringContract.IntentStrings.GROUP_DESC, getIntent().getStringExtra(StringContract.IntentStrings.GROUP_DESC));
                bundle.putString(StringContract.IntentStrings.GROUP_PASSWORD, getIntent().getStringExtra(StringContract.IntentStrings.GROUP_PASSWORD));
                bundle.putString(StringContract.IntentStrings.TABS, getIntent().getStringExtra(StringContract.IntentStrings.TABS));
                if (getIntent().hasExtra(StringContract.IntentStrings.ALL_MEMBERS_STRING)) {
                    bundle.putString(StringContract.IntentStrings.ALL_MEMBERS_STRING, getIntent().getStringExtra(StringContract.IntentStrings.ALL_MEMBERS_STRING));
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("UserIds", getIntent().getStringExtra(StringContract.IntentStrings.GUID));
                editor.putString("types", getIntent().getStringExtra(StringContract.IntentStrings.TYPE));
                editor.commit();
            }
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.ChatFragment, fragment,"CometChatMessageScreenFragment").commit();
        }

        btnAdd = findViewById(R.id.addMember);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        preferencesCheckCurrentActivity = getSharedPreferences("preferencesCheckCurrentActivity", MODE_PRIVATE);
        editor = preferencesCheckCurrentActivity.edit();
        editor.putBoolean("IsChatScreen",false);
        editor.commit();
        Log.i(TAG, "onBackPressed: ");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLongMessageClick(List<BaseMessage> baseMessage) {
        if (fragment != null)
            ((OnMessageLongClick) fragment).setLongMessageClick(baseMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void handleDialogClose(DialogInterface dialog) {
        ((MessageActionCloseListener) fragment).handleDialogClose(dialog);
    }
}
