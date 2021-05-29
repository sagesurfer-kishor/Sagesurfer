package screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.BannedGroupMembersRequest;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.Avatar;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.SharedMediaView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.Banmemberlistadapter;
import adapter.CallHistoryAdapter;
import adapter.GroupMemberAdapter;
import adapter.GroupMembersAdapter;
import adapter.MembersListAdapter;
import constant.StringContract;
import listeners.ClickListener;
import listeners.RecyclerTouchListener;
import okhttp3.RequestBody;
import screen.Models.GetAddNewMember;
import screen.addmember.CometChatAddMemberScreenActivity;
import screen.adminAndModeratorList.CometChatAdminModeratorListScreenActivity;
import screen.banmembers.CometChatBanMemberScreenActivity;
import screen.messagelist.General;
import screen.messagelist.NetworkCall_;
import screen.messagelist.Preferences;
import screen.messagelist.Urls_;
import screen.unified.CometChatUnified;
import secure.GroupTeam_;
import utils.FontUtils;
import utils.Utils;

import static utils.Utils.UserToGroupMember;


public class CometChatGroupDetailScreenActivity extends AppCompatActivity {

    private String TAG = "CometChatGroupDetail";

    private Avatar groupIcon;

    private String groupType;
    private ArrayList<GetAddNewMember> getgroupmemberArrayList = new ArrayList<>();
    private String ownerId;
    private TextView btnSearch;
    private TextView tvGroupName;
    private TextView error;
    private MessagesRequest messageRequest;
    private EditText et_search_user;
    private CallHistoryAdapter callHistoryAdapter;
    private TextView tvGroupDesc;
    private List<BaseMessage> callList = new ArrayList<>();
    private TextView tvAdminCount;
    TextView tv_exit;
    private TextView tvModeratorCount;

    private TextView tvBanMemberCount;

    private ArrayList<String> groupMemberUids = new ArrayList<>();

    private RecyclerView rvMemberList;

    private String guid, gName, gDesc, gPassword;

    private GroupMembersRequest groupMembersRequest;
    private androidx.appcompat.app.AlertDialog alertDialog;
    private GroupMemberAdapter groupMemberAdapter;

    private int adminCount;
    private NestedScrollView nested_scrollview;

    private int moderatorCount;
    private RecyclerView recyclerView;
    String[] s = new String[0];
    private LinearLayout historyView;
    private RelativeLayout rlAddMemberView;
    private MembersListAdapter memberListAdapter;
    private RelativeLayout rlAdminListView;

    private RelativeLayout rlModeratorView;

    private RelativeLayout rlBanMembers;

    private String loggedInUserScope;

    private GroupMember groupMember;
    private TextView tv_delete, tv_adminstrators, tv_moderators;
    private TextView tvLoadMore;
    private List<GroupMember> groupMembers = new ArrayList<>();
    private AlertDialog.Builder dialog;
    private TextView tvMemberCount;
    private int groupMemberCount = 0;
    private RecyclerView history_rv;
    private static int LIMIT = 30;
    LinearLayout shared_media_layout;
    RelativeLayout rlAdminView;
    private User loggedInUser = CometChat.getLoggedInUser();

    private FontUtils fontUtils;

    private SharedMediaView sharedMediaView;

    private ImageView videoCallBtn;

    private ImageView callBtn;

    private TextView dividerAdmin, dividerBan, dividerModerator, divider2;

    private BannedGroupMembersRequest banMemberRequest;

    private MaterialToolbar toolbar;
    private RelativeLayout rl_load_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comet_chat_group_detail_screen);
        fontUtils = FontUtils.getInstance(this);
        initComponent();

    }

    private void initComponent() {
        dividerAdmin = findViewById(R.id.tv_seperator_admin);
        dividerModerator = findViewById(R.id.tv_seperator_moderator);
        dividerBan = findViewById(R.id.tv_seperator_ban);
        divider2 = findViewById(R.id.tv_seperator_1);
        groupIcon = findViewById(R.id.iv_group);
        tvGroupName = findViewById(R.id.tv_group_name);
        tvGroupDesc = findViewById(R.id.group_description);
        nested_scrollview = findViewById(R.id.nested_scrollview);
        historyView = findViewById(R.id.history_view);
        history_rv = findViewById(R.id.history_rv);
        rl_load_more = findViewById(R.id.rl_load_more);
        shared_media_layout = findViewById(R.id.shared_media_layout);
        tvGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroupDialog();
            }
        });
        tvMemberCount = findViewById(R.id.tv_members);
        tv_adminstrators = findViewById(R.id.tv_adminstrators);
        tv_moderators = findViewById(R.id.tv_moderators);
        tvAdminCount = findViewById(R.id.tv_admin_count);
        tvModeratorCount = findViewById(R.id.tv_moderator_count);
        tvBanMemberCount = findViewById(R.id.tv_ban_count);
        rvMemberList = findViewById(R.id.member_list);
        tvLoadMore = findViewById(R.id.tv_load_more);
        tvLoadMore.setText(String.format(getResources().getString(R.string.load_more_members), LIMIT));
        TextView tvAddMember = findViewById(R.id.tv_add_member);
        callBtn = findViewById(R.id.callBtn_iv);
        videoCallBtn = findViewById(R.id.video_callBtn_iv);
        rlBanMembers = findViewById(R.id.rlBanView);
        rlBanMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBanMemberListScreen();
            }
        });
        rlAddMemberView = findViewById(R.id.rl_add_member);
        rlAddMemberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMembers();
            }
        });
        rlAdminListView = findViewById(R.id.rlAdminView);
        rlAdminListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdminListScreen(false);
            }
        });
        rlModeratorView = findViewById(R.id.rlModeratorView);
        rlModeratorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdminListScreen(true);
            }
        });
        tv_delete = findViewById(R.id.tv_delete);
        tv_exit = findViewById(R.id.tv_exit);
        toolbar = findViewById(R.id.groupDetailToolbar);

        tv_delete.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        tv_exit.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        tvAddMember.setTypeface(fontUtils.getTypeFace(FontUtils.robotoRegular));

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMemberList.setLayoutManager(linearLayoutManager);
//        rvMemberList.setNestedScrollingEnabled(false);

        handleIntent();
        checkDarkMode();

        sharedMediaView = findViewById(R.id.shared_media_view);
        sharedMediaView.setRecieverId(guid);
        sharedMediaView.setRecieverType(CometChatConstants.RECEIVER_TYPE_GROUP);
        sharedMediaView.reload();

        rvMemberList.addOnItemTouchListener(new RecyclerTouchListener(this, rvMemberList, new ClickListener() {
            @Override
            public void onClick(View var1, int var2) {
                GroupMember user = (GroupMember) var1.getTag(R.string.user);
                if (loggedInUserScope != null && (loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN) || loggedInUserScope.equals(CometChatConstants.SCOPE_MODERATOR))) {
                    groupMember = user;
                    boolean isAdmin = user.getScope().equals(CometChatConstants.SCOPE_ADMIN);
                    boolean isSelf = loggedInUser.getUid().equals(user.getUid());
                    boolean isOwner = loggedInUser.getUid().equals(ownerId);
                    if (!isSelf) {
                        if (!isAdmin || isOwner) {
                            registerForContextMenu(rvMemberList);
                            openContextMenu(var1);
                        }
                    }
                }
            }

            @Override
            public void onLongClick(View var1, int var2) {

            }
        }));

        tvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGroupMembers();
            }
        });
        tv_exit.setOnClickListener(view -> createDialog(getResources().getString(R.string.exit_group_title), getResources().getString(R.string.exit_group_message),
                getResources().getString(R.string.exit), getResources().getString(R.string.cancel), R.drawable.ic_exit_to_app));

        callBtn.setOnClickListener(view -> checkOnGoingCall(CometChatConstants.CALL_TYPE_AUDIO));

        videoCallBtn.setOnClickListener(view -> checkOnGoingCall(CometChatConstants.CALL_TYPE_VIDEO));

        tv_delete.setOnClickListener(view -> createDialog(getResources().getString(R.string.delete_group_title), getResources().getString(R.string.delete_group_message),
                getResources().getString(R.string.delete), getResources().getString(R.string.cancel), R.drawable.ic_delete_24dp));

    }

    private void checkDarkMode() {
        if (Utils.isDarkMode(this)) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.textColorWhite));
            toolbar.setBackgroundColor(getResources().getColor(R.color.darkModeBackground));
            tvGroupName.setTextColor(getResources().getColor(R.color.textColorWhite));
            nested_scrollview.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
            dividerAdmin.setBackgroundColor(getResources().getColor(R.color.grey));
            dividerModerator.setBackgroundColor(getResources().getColor(R.color.grey));
            dividerBan.setBackgroundColor(getResources().getColor(R.color.grey));
            divider2.setBackgroundColor(getResources().getColor(R.color.grey));
            tv_moderators.setTextColor(getResources().getColor(R.color.textColorWhite));
            tv_adminstrators.setTextColor(getResources().getColor(R.color.textColorWhite));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.textColorWhite));
            tvGroupName.setTextColor(getResources().getColor(R.color.primaryTextColor));
            dividerAdmin.setBackgroundColor(getResources().getColor(R.color.light_grey));
            dividerModerator.setBackgroundColor(getResources().getColor(R.color.light_grey));
            dividerBan.setBackgroundColor(getResources().getColor(R.color.light_grey));
            divider2.setBackgroundColor(getResources().getColor(R.color.light_grey));
        }
    }

    private void checkOnGoingCall(String callType) {
        if (CometChat.getActiveCall() != null && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING) && CometChat.getActiveCall().getSessionId() != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.ongoing_call))
                    .setMessage(getResources().getString(R.string.ongoing_call_message))
                    .setPositiveButton(getResources().getString(R.string.join), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.joinOnGoingCall(CometChatGroupDetailScreenActivity.this);
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            initiateGroupCall(guid, CometChatConstants.RECEIVER_TYPE_GROUP, callType);
        }
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.group_action_menu, menu);

        menu.findItem(R.id.item_make_admin).setVisible(false);

        menu.setHeaderTitle("Group Action");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.item_remove) {
            kickMember();
        } else if (item.getItemId() == R.id.item_ban) {
            banMember();
        }

        return super.onContextItemSelected(item);
    }

    public void initiateGroupCall(String recieverID, String receiverType, String callType) {
        Call call = new Call(recieverID, receiverType, callType);
        CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                Utils.startGroupCallIntent(CometChatGroupDetailScreenActivity.this, ((Group) call.getCallReceiver()), call.getType(), true, call.getSessionId());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: " + e.getMessage());
                if (rvMemberList != null)
                    Snackbar.make(rvMemberList, getResources().getString(R.string.call_initiate_error) + ":" + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This method is used to create dialog box on click of events like <b>Delete Group</b> and <b>Exit Group</b>
     *
     * @param title
     * @param message
     * @param positiveText
     * @param negativeText
     * @param drawableRes
     */
    private void createDialog(String title, String message, String positiveText, String negativeText, int drawableRes) {

        MaterialAlertDialogBuilder alert_dialog = new MaterialAlertDialogBuilder(CometChatGroupDetailScreenActivity.this,
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
        alert_dialog.setTitle(title);
        alert_dialog.setMessage(message);
        alert_dialog.setPositiveButton(positiveText, (dialogInterface, i) -> {

            if (positiveText.equalsIgnoreCase(getResources().getString(R.string.exit)))
                leaveGroup();

            else if (positiveText.equalsIgnoreCase(getResources().getString(R.string.delete))
                    && loggedInUserScope.equalsIgnoreCase(CometChatConstants.SCOPE_ADMIN))
                deleteGroup();

        });

        alert_dialog.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert_dialog.create();
        alert_dialog.show();

    }


    /**
     * This method is used to handle the intent passed to this activity.
     */
    private void handleIntent() {
        if (getIntent().hasExtra(StringContract.IntentStrings.GUID)) {
            guid = getIntent().getStringExtra(StringContract.IntentStrings.GUID);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.MEMBER_SCOPE)) {
            loggedInUserScope = getIntent().getStringExtra(StringContract.IntentStrings.MEMBER_SCOPE);
            if (loggedInUserScope != null && loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN)) {
                rlAddMemberView.setVisibility(View.VISIBLE);
                rlBanMembers.setVisibility(View.VISIBLE);
                rlModeratorView.setVisibility(View.VISIBLE);
                tv_delete.setVisibility(View.VISIBLE);
            } else if (loggedInUserScope != null && loggedInUserScope.equals(CometChatConstants.SCOPE_MODERATOR)) {
                rlAddMemberView.setVisibility(View.GONE);
                rlBanMembers.setVisibility(View.VISIBLE);
                rlModeratorView.setVisibility(View.VISIBLE);
                rlAdminListView.setVisibility(View.VISIBLE);
            } else {
                dividerModerator.setVisibility(View.GONE);
                dividerAdmin.setVisibility(View.GONE);
                rlAdminListView.setVisibility(View.GONE);
                rlModeratorView.setVisibility(View.GONE);
                rlBanMembers.setVisibility(View.GONE);
                rlAddMemberView.setVisibility(View.GONE);
            }
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.NAME)) {
            gName = getIntent().getStringExtra(StringContract.IntentStrings.NAME);
            tvGroupName.setText(gName);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.AVATAR)) {
            String avatar = getIntent().getStringExtra(StringContract.IntentStrings.AVATAR);
            if (avatar != null && !avatar.isEmpty())
                groupIcon.setAvatar(avatar);
            else
                groupIcon.setInitials(gName);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.GROUP_DESC)) {
            gDesc = getIntent().getStringExtra(StringContract.IntentStrings.GROUP_DESC);
            tvGroupDesc.setText(gDesc);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.GROUP_PASSWORD)) {
            gPassword = getIntent().getStringExtra(StringContract.IntentStrings.GROUP_PASSWORD);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.GROUP_OWNER)) {
            ownerId = getIntent().getStringExtra(StringContract.IntentStrings.GROUP_OWNER);
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.MEMBER_COUNT)) {
            tvMemberCount.setVisibility(View.VISIBLE);
            groupMemberCount = getIntent().getIntExtra(StringContract.IntentStrings.MEMBER_COUNT, 0);
            tvMemberCount.setText((groupMemberCount) + " Members");
        }
        if (getIntent().hasExtra(StringContract.IntentStrings.GROUP_TYPE)) {
            groupType = getIntent().getStringExtra(StringContract.IntentStrings.GROUP_TYPE);
        }

        if (getIntent().hasExtra(StringContract.IntentStrings.FROM_CALL_LIST)) {
            fetchCallHistory();
            historyView.setVisibility(View.VISIBLE);
            rlModeratorView.setVisibility(View.GONE);
            rlAdminListView.setVisibility(View.GONE);
            tvMemberCount.setVisibility(View.GONE);
            rlAddMemberView.setVisibility(View.GONE);
            rvMemberList.setVisibility(View.GONE);
            rl_load_more.setVisibility(View.GONE);
            shared_media_layout.setVisibility(View.GONE);
            tv_delete.setVisibility(View.VISIBLE);
            tv_exit.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method is used whenever user click <b>Banned Members</b>. It takes user to
     * <code>CometChatBanMemberScreenActivity.class</code>
     *
     * @see CometChatBanMemberScreenActivity
     */
    private void openBanMemberListScreen() {
        Intent intent = new Intent(this, CometChatBanMemberScreenActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, guid);
        intent.putExtra(StringContract.IntentStrings.GROUP_NAME, gName);
        intent.putExtra(StringContract.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
        startActivity(intent);
    }


    /**
     * This method is used whenever user click <b>Administrator</b>. It takes user to
     * <code>CometChatAdminListScreenActivity.class</code>
     *
     * @see CometChatAdminModeratorListScreenActivity
     */
    public void openAdminListScreen(boolean showModerators) {
        Intent intent = new Intent(this, CometChatAdminModeratorListScreenActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, guid);
        intent.putExtra(StringContract.IntentStrings.SHOW_MODERATORLIST, showModerators);
        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, ownerId);
        intent.putExtra(StringContract.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
        startActivity(intent);
    }

    /**
     * This method is used whenever user click <b>Add Member</b>. It takes user to
     * <code>CometChatAddMemberScreenActivity.class</code>
     *
     * @see CometChatAddMemberScreenActivity
     */
    public void addMembers() {
       /* Intent intent = new Intent(this, CometChatAddMemberScreenActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, guid);
        intent.putExtra(StringContract.IntentStrings.GROUP_MEMBER, groupMemberUids);
        intent.putExtra(StringContract.IntentStrings.GROUP_NAME, gName);
        intent.putExtra(StringContract.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
        intent.putExtra(StringContract.IntentStrings.IS_ADD_MEMBER, true);
        startActivity(intent);*/
        dialogViewMembers(groupType);
    }

    /**
     * This method is used to delete Group. It is used only if loggedIn user is admin.
     */
    private void deleteGroup() {
        CometChat.deleteGroup(guid, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                launchUnified();
            }

            @Override
            public void onError(CometChatException e) {
                Snackbar.make(rvMemberList, getResources().getString(R.string.group_delete_error), Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void launchUnified() {

        Intent intent = new Intent(CometChatGroupDetailScreenActivity.this, CometChatUnified.class);
        startActivity(intent);
        finish();
    }


    /**
     * This method is used to kick group member from the group. It is used only if loggedIn user is admin.
     *
     * @see CometChat#kickGroupMember(String, String, CometChat.CallbackListener)
     */
    private void kickMember() {
        CometChat.kickGroupMember(groupMember.getUid(), guid, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "onSuccess: " + s);
                tvMemberCount.setText((groupMemberCount - 1) + " Members");
                groupMemberUids.remove(groupMember.getUid());
                groupMemberAdapter.removeGroupMember(groupMember);
            }

            @Override
            public void onError(CometChatException e) {
                Snackbar.make(rvMemberList, String.format(getResources().getString(R.string.cannot_remove_member), groupMember.getName()), Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    /**
     * This method is used to ban group member from the group. It is used only if loggedIn user is admin.
     *
     * @see CometChat#banGroupMember(String, String, CometChat.CallbackListener)
     */
    private void banMember() {
        CometChat.banGroupMember(groupMember.getUid(), guid, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "onSuccess: " + s);
                tvMemberCount.setText((groupMemberCount - 1) + " Members");
//                int count = Integer.parseInt(tvBanMemberCount.getText().toString());
//                tvBanMemberCount.setText(String.valueOf(++count));
                groupMemberUids.remove(groupMember.getUid());
                groupMemberAdapter.removeGroupMember(groupMember);
            }

            @Override
            public void onError(CometChatException e) {
                Snackbar.make(rvMemberList, String.format(getResources().getString(R.string.cannot_remove_member), groupMember.getName()), Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    /**
     * This method is used to get list of group members. It also helps to update other things like
     * Admin count.
     *
     * @see GroupMembersRequest#fetchNext(CometChat.CallbackListener)
     * @see GroupMember
     */
    private void getGroupMembers() {
        if (groupMembersRequest == null) {
            groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(guid).setLimit(LIMIT).build();
        }
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Log.e(TAG, "onSuccess: " + groupMembers.size());
                if (groupMembers != null && groupMembers.size() != 0) {
                    adminCount = 0;
                    moderatorCount = 0;
                    groupMemberUids.clear();
                    s = new String[groupMembers.size()];
                    for (int j = 0; j < groupMembers.size(); j++) {
                        groupMemberUids.add(groupMembers.get(j).getUid());
                        if (groupMembers.get(j).getScope().equals(CometChatConstants.SCOPE_ADMIN)) {
                            adminCount++;
                        }
                        if (groupMembers.get(j).getScope().equals(CometChatConstants.SCOPE_MODERATOR)) {
                            moderatorCount++;
                        }
                        s[j] = groupMembers.get(j).getName();
                    }
//                    tvAdminCount.setText(adminCount+"");
//                    tvModeratorCount.setText(moderatorCount+"");
                    if (groupMemberAdapter == null) {
                        groupMemberAdapter = new GroupMemberAdapter(CometChatGroupDetailScreenActivity.this, groupMembers, ownerId);
                        rvMemberList.setAdapter(groupMemberAdapter);
                    } else {
                        groupMemberAdapter.addAll(groupMembers);
                    }
                    if (groupMembers.size() < LIMIT) {
                        tvLoadMore.setVisibility(View.GONE);
                    }
                } else {
                    tvLoadMore.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Snackbar.make(rvMemberList, getResources().getString(R.string.group_member_list_error), Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

//    private void getBannedMemberCount() {
//        banMemberRequest = new BannedGroupMembersRequest.BannedGroupMembersRequestBuilder(guid).setLimit(100).build();
//        banMemberRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
//            @Override
//            public void onSuccess(List<GroupMember> groupMembers) {
//                if (groupMembers.size()>=99) {
//                    tvBanMemberCount.setText("99+");
//                } else {
//                    tvBanMemberCount.setText(groupMembers.size()+"");
//                }
//            }
//
//            @Override
//            public void onError(CometChatException e) {
//                Log.e(TAG, "onError: "+e.getMessage()+"\n"+e.getCode());
//            }
//        });
//    }

    /**
     * This method is used to leave the loggedIn User from respective group.
     *
     * @see CometChat#leaveGroup(String, CometChat.CallbackListener)
     */
    private void leaveGroup() {
        CometChat.leaveGroup(guid, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {

                Log.i(TAG, "onSuccess: leave group");
                User user = CometChat.getLoggedInUser();
                leftGroupMembersFromServer(guid, user.getUid());
            }

            @Override
            public void onError(CometChatException e) {
                Snackbar.make(rlAddMemberView, getResources().getString(R.string.leave_group_error), Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void navigateToGroupList() {
        //Bundle bundle = new Bundle();
        //bundle.putString("OpenGroupFragment", "OpenGroupFragment");
        Log.i(TAG, "navigateToGroupList: ");
        /*Intent intent = new Intent(CometChatGroupDetailScreenActivity.this, CometChatMessageListActivity.class);
        intent.putExtra("OpenGroupFragment", "OpenGroupFragment");
        startActivity(intent);*/

        FragmentCometChatGroupList2 fragment = new FragmentCometChatGroupList2();
        //fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id., fragment, TAG);
        ft.commit();

    }

    /**
     * This method is used to add group listener in this screen to receive real-time events.
     *
     * @see CometChat#addGroupListener(String, CometChat.GroupListener)
     */
    public void addGroupListener() {
        CometChat.addGroupListener(TAG, new CometChat.GroupListener() {
            @Override
            public void onGroupMemberJoined(Action action, User joinedUser, Group joinedGroup) {
                Log.e(TAG, "onGroupMemberJoined: " + joinedUser.getUid());
                if (joinedGroup.getGuid().equals(guid))
                    updateGroupMember(joinedUser, false, false, action);
            }

            @Override
            public void onGroupMemberLeft(Action action, User leftUser, Group leftGroup) {
                Log.d(TAG, "onGroupMemberLeft: ");
                if (leftGroup.getGuid().equals(guid))
                    updateGroupMember(leftUser, true, false, action);
            }

            @Override
            public void onGroupMemberKicked(Action action, User kickedUser, User kickedBy, Group kickedFrom) {
                Log.d(TAG, "onGroupMemberKicked: ");
                if (kickedFrom.getGuid().equals(guid))
                    updateGroupMember(kickedUser, true, false, action);
            }

            @Override
            public void onGroupMemberScopeChanged(Action action, User updatedBy, User updatedUser, String scopeChangedTo, String scopeChangedFrom, Group group) {
                Log.d(TAG, "onGroupMemberScopeChanged: ");
                if (group.getGuid().equals(guid))
                    updateGroupMember(updatedUser, false, true, action);
            }

            @Override
            public void onMemberAddedToGroup(Action action, User addedby, User userAdded, Group addedTo) {
                if (addedTo.getGuid().equals(guid))
                    updateGroupMember(userAdded, false, false, action);
            }

            @Override
            public void onGroupMemberBanned(Action action, User bannedUser, User bannedBy, Group bannedFrom) {
                if (bannedFrom.getGuid().equals(guid)) {
//                    int count = Integer.parseInt(tvBanMemberCount.getText().toString());
//                    tvBanMemberCount.setText(String.valueOf(++count));
                    updateGroupMember(bannedUser, true, false, action);
                }
            }

            @Override
            public void onGroupMemberUnbanned(Action action, User unbannedUser, User unbannedBy, Group unbannedFrom) {
                if (unbannedFrom.getGuid().equals(guid)) {
//                    int count = Integer.parseInt(tvBanMemberCount.getText().toString());
//                    tvBanMemberCount.setText(String.valueOf(--count));
                }
            }
        });
    }

    /**
     * This method is used to update group members from events recieved in real time. It updates or removes
     * group member from list based on parameters passed.
     *
     * @param user          is a object of User.
     * @param isRemoved     is a boolean which helps to know whether group member needs to be removed from list or not.
     * @param isScopeUpdate is a boolean which helps to know whether group member scope is updated or not.
     * @param action        is object of Action.
     * @see Action
     * @see GroupMember
     * @see User
     * @see utils.Utils#UserToGroupMember(User, boolean, String)
     */
    private void updateGroupMember(User user, boolean isRemoved, boolean isScopeUpdate, Action action) {
        if (groupMemberAdapter != null) {
            if (!isRemoved && !isScopeUpdate) {
                groupMemberAdapter.addGroupMember(UserToGroupMember(user, false, action.getOldScope()));
                int count = ++groupMemberCount;
                tvMemberCount.setText(count + " Members");
            } else if (isRemoved && !isScopeUpdate) {
                groupMemberAdapter.removeGroupMember(UserToGroupMember(user, false, action.getOldScope()));
                int count = --groupMemberCount;
                tvMemberCount.setText(count + " Members");
                if (action.getNewScope() != null) {
                    if (action.getNewScope().equals(CometChatConstants.SCOPE_ADMIN)) {
                        adminCount = adminCount - 1;
//                        tvAdminCount.setText(String.valueOf(adminCount));
                    } else if (action.getNewScope().equals(CometChatConstants.SCOPE_MODERATOR)) {
                        moderatorCount = moderatorCount - 1;
//                        tvModeratorCount.setText(String.valueOf(moderatorCount));
                    }
                }
            } else if (!isRemoved) {
                groupMemberAdapter.updateMember(UserToGroupMember(user, true, action.getNewScope()));
                if (action.getNewScope().equals(CometChatConstants.SCOPE_ADMIN)) {
                    adminCount = adminCount + 1;
//                    tvAdminCount.setText(String.valueOf(adminCount));
                    if (user.getUid().equals(loggedInUser.getUid())) {
                        rlAddMemberView.setVisibility(View.VISIBLE);
                        loggedInUserScope = CometChatConstants.SCOPE_ADMIN;
                        tv_delete.setVisibility(View.VISIBLE);
                    }
                } else if (action.getNewScope().equals(CometChatConstants.SCOPE_MODERATOR)) {
                    moderatorCount = moderatorCount + 1;
//                    tvModeratorCount.setText(String.valueOf(moderatorCount));
                    if (user.getUid().equals(loggedInUser.getUid())) {
                        rlBanMembers.setVisibility(View.VISIBLE);
                        loggedInUserScope = CometChatConstants.SCOPE_MODERATOR;
                    }
                } else if (action.getOldScope().equals(CometChatConstants.SCOPE_ADMIN)) {
                    adminCount = adminCount - 1;
//                    tvAdminCount.setText(String.valueOf(adminCount));
                } else if (action.getOldScope().equals(CometChatConstants.SCOPE_MODERATOR)) {
                    moderatorCount = moderatorCount - 1;
//                    tvModeratorCount.setText(String.valueOf(moderatorCount));
                }
            }
        }
    }


    private void updateGroupDialog() {
        dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.update_group, null);
        Avatar avatar = view.findViewById(R.id.group_icon);
        TextInputEditText avatar_url = view.findViewById(R.id.icon_url_edt);
        if (groupIcon.getAvatarUrl() != null) {
            avatar.setVisibility(View.VISIBLE);
            avatar.setAvatar(groupIcon.getAvatarUrl());
            avatar_url.setText(groupIcon.getAvatarUrl());
        } else {
            avatar.setVisibility(View.GONE);
        }
        TextInputEditText groupName = view.findViewById(R.id.groupname_edt);
        TextInputEditText groupDesc = view.findViewById(R.id.groupdesc_edt);
        TextInputEditText groupOldPwd = view.findViewById(R.id.group_old_pwd);
        TextInputEditText groupNewPwd = view.findViewById(R.id.group_new_pwd);
        TextInputLayout groupOldPwdLayout = view.findViewById(R.id.input_group_old_pwd);
        TextInputLayout groupNewPwdLayout = view.findViewById(R.id.input_group_new_pwd);
        Spinner groupTypeSp = view.findViewById(R.id.groupTypes);
        MaterialButton updateGroupBtn = view.findViewById(R.id.updateGroupBtn);
        MaterialButton cancelBtn = view.findViewById(R.id.cancelBtn);
        groupName.setText(gName);
        groupDesc.setText(gDesc);
        if (groupType != null && groupType.equals(CometChatConstants.GROUP_TYPE_PUBLIC)) {
            groupTypeSp.setSelection(0);
            groupOldPwdLayout.setVisibility(View.GONE);
            groupNewPwdLayout.setVisibility(View.GONE);
        } else if (groupType != null && groupType.equals(CometChatConstants.GROUP_TYPE_PRIVATE)) {
            groupTypeSp.setSelection(1);
            groupOldPwdLayout.setVisibility(View.GONE);
            groupNewPwdLayout.setVisibility(View.GONE);
        } else {
            groupTypeSp.setSelection(2);
            groupOldPwdLayout.setVisibility(View.VISIBLE);
            groupNewPwdLayout.setVisibility(View.VISIBLE);
        }

        groupTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItemPosition() == 2) {
                    if (gPassword == null) {
                        groupOldPwdLayout.setVisibility(View.GONE);
                    } else
                        groupOldPwdLayout.setVisibility(View.VISIBLE);
                    groupNewPwdLayout.setVisibility(View.VISIBLE);
                } else {
                    groupOldPwdLayout.setVisibility(View.GONE);
                    groupNewPwdLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        avatar_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    avatar.setVisibility(View.VISIBLE);
                    Glide.with(CometChatGroupDetailScreenActivity.this).load(s.toString()).into(avatar);
                } else
                    avatar.setVisibility(View.GONE);
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.setView(view);
        updateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group group = new Group();
                group.setDescription(groupDesc.getText().toString().trim());
                if (groupName.getText().toString().isEmpty()) {
                    groupName.setError(getString(R.string.fill_this_field));
                } else if (groupTypeSp.getSelectedItemPosition() == 2) {
                    if (gPassword != null && groupOldPwd.getText().toString().trim().isEmpty()) {
                        groupOldPwd.setError(getResources().getString(R.string.fill_this_field));
                    } else if (gPassword != null && !groupOldPwd.getText().toString().trim().equals(gPassword.trim())) {
                        groupOldPwd.setError(getResources().getString(R.string.password_not_matched));
                    } else if (groupNewPwd.getText().toString().trim().isEmpty()) {
                        groupNewPwd.setError(getResources().getString(R.string.fill_this_field));
                    } else {
                        group.setName(groupName.getText().toString());
                        group.setGuid(guid);
                        group.setGroupType(CometChatConstants.GROUP_TYPE_PASSWORD);
                        group.setPassword(groupNewPwd.getText().toString());
                        group.setIcon(avatar_url.getText().toString());
                        updateGroup(group, alertDialog);
                    }
                } else if (groupTypeSp.getSelectedItemPosition() == 1) {
                    group.setName(groupName.getText().toString());
                    group.setGuid(guid);
                    group.setGroupType(CometChatConstants.GROUP_TYPE_PRIVATE);
                    group.setIcon(avatar_url.getText().toString());
                } else {
                    group.setName(groupName.getText().toString());
                    group.setGroupType(CometChatConstants.GROUP_TYPE_PUBLIC);
                    group.setIcon(avatar_url.getText().toString());
                }
                group.setGuid(guid);
                updateGroup(group, alertDialog);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void updateGroup(Group group, AlertDialog dialog) {
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (rvMemberList != null) {
                    Snackbar.make(rvMemberList, getResources().getString(R.string.group_updated), Snackbar.LENGTH_LONG).show();
                    getGroup();
                }
                dialog.dismiss();
            }

            @Override
            public void onError(CometChatException e) {
                if (rvMemberList != null) {
                    Snackbar.make(rvMemberList, getResources().getString(R.string.group_update_failed) + " " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * This method is used to remove group listener.
     */
    public void removeGroupListener() {
        CometChat.removeGroupListener(TAG);
    }

    /**
     * This method is used to get Group Details.
     *
     * @see CometChat#getGroup(String, CometChat.CallbackListener)
     */
    private void getGroup() {

        CometChat.getGroup(guid, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                gName = group.getName();
                tvGroupName.setText(gName);
                groupIcon.setAvatar(group.getIcon());
                loggedInUserScope = group.getScope();
                groupMemberCount = group.getMembersCount();
                groupType = group.getGroupType();
                gDesc = group.getDescription();
                tvGroupDesc.setText(gDesc);
                tvMemberCount.setText(groupMemberCount + " Members");
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(CometChatGroupDetailScreenActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGroup();
        groupMembersRequest = null;
        if (groupMemberAdapter != null) {
            groupMemberAdapter.resetAdapter();
            groupMemberAdapter = null;

        }
//        getBannedMemberCount();
        getGroupMembers();
        addGroupListener();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        removeGroupListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeGroupListener();
    }

    private void leftGroupMembersFromServer(String guid, String user_id) {
        Log.i(TAG, "leftGroupMembersFromServer: userId " + user_id);
        String[] arrUser_id = user_id.split("_");
        String action = "left_group_members";
        String url = Urls_.MOBILE_COMET_CHAT_TEAMS;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, arrUser_id[0]);
        requestMap.put(General.COMETCHAT_GROUP_ID, guid);
        SharedPreferences domainUrlPref = getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String domain = domainUrlPref.getString(General.DOMAIN, null);
        RequestBody requestBody = NetworkCall_.make(requestMap, domain + url, TAG, CometChatGroupDetailScreenActivity.this);
        Log.e(TAG, "leftGroupMembersFromServer: request body " + requestBody);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(domain + url, requestBody, TAG, CometChatGroupDetailScreenActivity.this);
                if (response != null) {
                    JSONObject responseJsonObj = new JSONObject(response);
                    Log.e(TAG, "leftGroupMembersFromServer : response" + response);
                    JSONArray left_group_members = responseJsonObj.getJSONArray("left_group_members");

                    for (int i = 0; i <= left_group_members.length(); i++) {
                        try {
                            String msg = left_group_members.getJSONObject(i).getString("msg");
                            Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();

                            /*Fragment fragment = GetFragments.get(id, bundle);
                            FragmentTransaction ft = myContext.getSupportFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                            ft.replace(R.id.app_bar_main_container, fragment, TAG);
                            ft.commit();*/
                            Intent intent = new Intent(CometChatGroupDetailScreenActivity.this, Class.forName("com.sagesurfer.collaborativecares.MainActivity"));
                            String team_logs_id = "";
                            intent.putExtra("team_logs_id", team_logs_id);
                            intent.putExtra("receiver", guid);
                            intent.putExtra("sender", user_id);
                            intent.putExtra("receiverType", "group");
                            intent.putExtra("username", user_id);
                            intent.putExtra("type", "groupMember");
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    navigateToGroupList();
                } else {
                    Toast.makeText(CometChatGroupDetailScreenActivity.this, "Server error..", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void dialogViewMembers(String groupType) {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(CometChatGroupDetailScreenActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.member_details, null);
        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();

        final TextView tv_allMember = view.findViewById(R.id.allMemberList);
        final TextView tv_addMembers = view.findViewById(R.id.addNewMemberList);
        final TextView tv_blockedMember = view.findViewById(R.id.blockedMemberList);

        recyclerView = view.findViewById(R.id.detailMemberList);
        final LinearLayout linearLayout = view.findViewById(R.id.lv_search);

        error = view.findViewById(R.id.error);
        et_search_user = view.findViewById(R.id.search_name);

        tv_allMember.setBackgroundResource(R.color.colorPrimary);
        tv_addMembers.setBackgroundResource(R.color.textColorWhite);
        tv_blockedMember.setBackgroundResource(R.color.textColorWhite);
        linearLayout.setVisibility(View.GONE);
        Preferences.initialize(CometChatGroupDetailScreenActivity.this);
        String groupId = String.valueOf(guid);
        Preferences.save("gId", groupId);
        // get all group member
        Log.i(TAG, "dialogViewMembers: ");
        getTeamsMember(groupId);

        tv_allMember.setBackgroundResource(R.color.colorPrimary);
        tv_addMembers.setBackgroundResource(R.color.textColorWhite);
        tv_blockedMember.setBackgroundResource(R.color.textColorWhite);

        ImageView btn_CloseDialog = view.findViewById(R.id.btnMemberDismiss);
        btnSearch = view.findViewById(R.id.btnsearch);
        btnSearch.setVisibility(View.GONE);

        btn_CloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        tv_allMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String groupId = String.valueOf(guid);
                Preferences.save("gId", groupId);

                //  get all team member for selected group
                getTeamsMember(groupId);

                tv_allMember.setBackgroundResource(R.color.colorPrimary);
                tv_addMembers.setBackgroundResource(R.color.textColorWhite);
                tv_blockedMember.setBackgroundResource(R.color.textColorWhite);

                linearLayout.setVisibility(View.GONE);
            }
        });

        String userId = Preferences.get(General.USER_ID);
        groupId = String.valueOf(guid);

        if (!Preferences.get("owner").equals(userId)) {
            tv_addMembers.setVisibility(View.GONE);
            tv_blockedMember.setVisibility(View.GONE);
        } else {
            // add member
            tv_addMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv_allMember.setBackgroundResource(R.color.textColorWhite);
                    tv_addMembers.setBackgroundResource(R.color.colorPrimary);
                    tv_blockedMember.setBackgroundResource(R.color.textColorWhite);
                    linearLayout.setVisibility(View.VISIBLE);
                    String action = "get_add_new_members";
                    String userId = Preferences.get(General.USER_ID);
                    String groupId = String.valueOf(guid);
                    // get all user list from db
                    fetchMembersFromServerToAddInGroup(action, userId, groupId, groupType);
                }
            });
        }

        tv_blockedMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.GONE);

                tv_blockedMember.setBackgroundResource(R.color.colorPrimary);
                tv_allMember.setBackgroundResource(R.color.textColorWhite);
                tv_addMembers.setBackgroundResource(R.color.textColorWhite);

                // block member list for group
                getBlockedUserList();
            }
        });

        // friend conversion search action
        et_search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchMember(s.toString());

            }
        });

        alertDialog.show();
    }

    private void searchMember(String search) {
        memberListAdapter.getFilter().filter(search);
    }


    private void getTeamsMember(String groupId) {
        GroupMembersRequest groupMembersRequest = null;
        String GUID = groupId;
        int limit = 30;

        groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(GUID)
                .setLimit(limit).build();
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                Log.e(TAG, "Group Member list fetched successfully : " + list.toString());
                if (list.size() > 0) {
                    GroupMembersAdapter caseloadListAdapter = new GroupMembersAdapter(CometChatGroupDetailScreenActivity.this, list);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CometChatGroupDetailScreenActivity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(caseloadListAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    error.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Group Member list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    /*fetching all the members to add in the group where we have add member button option on list.
     * (Second tab of dialog view members) commented by rahul maske*/
    private void fetchMembersFromServerToAddInGroup(String action, String userId, String groupId, String groupType) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, userId);
        requestMap.put(General.GROUP_ID, groupId);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMET_CHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, CometChatGroupDetailScreenActivity.this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, CometChatGroupDetailScreenActivity.this);
                if (response != null) {
                    getgroupmemberArrayList = GroupTeam_.parsegroupMember(response, "get_add_new_members", CometChatGroupDetailScreenActivity.this, TAG);
                    memberListAdapter = new MembersListAdapter(CometChatGroupDetailScreenActivity.this, getgroupmemberArrayList, groupType);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CometChatGroupDetailScreenActivity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(memberListAdapter);

                    recyclerView.setVisibility(View.VISIBLE);
                    error.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*Here all the ban user for groups we are fetching and showing to list
     * update and commented by rahul maske*/
    private void getBlockedUserList() {
        BannedGroupMembersRequest bannedGroupMembersRequest;
        String GUID = String.valueOf(guid);
        int limit = 50;
        bannedGroupMembersRequest = new BannedGroupMembersRequest.BannedGroupMembersRequestBuilder(GUID).setLimit(limit).build();
        bannedGroupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                if (list.size() > 0) {
                    //Banmemberlistadapter banmemberadapter = new Banmemberlistadapter(CometChatGroupDetailScreenActivity.this, list);
                    Banmemberlistadapter banmemberadapter = new Banmemberlistadapter(CometChatGroupDetailScreenActivity.this, list);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CometChatGroupDetailScreenActivity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(banmemberadapter);

                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Banned Group Member list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private void fetchCallHistory() {
        if (messageRequest == null) {
            messageRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(guid).setCategory(CometChatConstants.CATEGORY_CALL).setLimit(30).build();
        }
        messageRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> messageList) {
                if (messageList.size() != 0) {
                    callList.addAll(messageList);
                    setCallHistoryAdapter(messageList);
                }
                if (callList.size() != 0)
                    historyView.setVisibility(View.VISIBLE);
                else
                    historyView.setVisibility(View.GONE);
            }

            @Override
            public void onError(CometChatException e) {
            }
        });
    }

    private void setCallHistoryAdapter(List<BaseMessage> messageList) {
        if (callHistoryAdapter == null) {
            callHistoryAdapter = new CallHistoryAdapter(CometChatGroupDetailScreenActivity.this, messageList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
            history_rv.setLayoutManager(linearLayoutManager);
            history_rv.setAdapter(callHistoryAdapter);
        } else
            callHistoryAdapter.updateList(messageList);
    }
}