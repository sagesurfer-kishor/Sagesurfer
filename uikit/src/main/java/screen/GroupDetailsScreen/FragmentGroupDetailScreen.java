package screen.GroupDetailsScreen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.Avatar;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.SharedMediaView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.GroupMemberAdapter;
import constant.StringContract;
import listeners.ClickListener;
import listeners.RecyclerTouchListener;
import okhttp3.RequestBody;
import screen.FragmentCometChatGroupList2;
import screen.addmember.CometChatAddMemberScreenActivity;
import screen.adminAndModeratorList.CometChatAdminModeratorListScreenActivity;
import screen.banmembers.CometChatBanMemberScreenActivity;
import screen.messagelist.CometChatMessageListActivity;
import screen.messagelist.General;
import screen.messagelist.NetworkCall_;
import screen.messagelist.Urls_;
import screen.unified.CometChatUnified;
import utils.FontUtils;
import utils.Utils;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentGroupDetailScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentGroupDetailScreen extends Fragment {


    private String mParam2;
    private View view;
    private String TAG = "CometChatGroupDetail";

    private Avatar groupIcon;

    private String groupType, ownerId;

    private TextView tvBanMemberCount, tvModeratorCount, tvAdminCount, tvGroupDesc, tvGroupName;

    private ArrayList<String> groupMemberUids = new ArrayList<>();

    private RecyclerView rvMemberList;

    private String guid, gName, gDesc, gPassword,groupId;

    private GroupMembersRequest groupMembersRequest;

    private GroupMemberAdapter groupMemberAdapter;

    private int adminCount;
    private NestedScrollView nested_scrollview;

    private int moderatorCount;
    String[] s = new String[0];

    private RelativeLayout rlAddMemberView, rlAdminListView, rlModeratorView;

    private RelativeLayout rlBanMembers;

    private String loggedInUserScope;

    private GroupMember groupMember;

    private TextView tvDelete, tv_adminstrators, tv_moderators;

    private TextView tvLoadMore;

    private List<GroupMember> groupMembers = new ArrayList<>();

    private AlertDialog.Builder dialog;

    private TextView tvMemberCount;

    private int groupMemberCount = 0;

    private static int LIMIT = 30;

    private User loggedInUser = CometChat.getLoggedInUser();

    private FontUtils fontUtils;

    private SharedMediaView sharedMediaView;

    private ImageView videoCallBtn, callBtn;


    private TextView dividerAdmin, dividerBan, dividerModerator, divider2;

    private BannedGroupMembersRequest banMemberRequest;

    public FragmentGroupDetailScreen() {

    }


   /* public static FragmentGroupDetailScreen newInstance(String param1, String param2) {
        FragmentGroupDetailScreen fragment = new FragmentGroupDetailScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initComponent() {
        dividerAdmin = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_seperator_admin);
        dividerModerator = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_seperator_moderator);
        dividerBan = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_seperator_ban);
        divider2 = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_seperator_1);
        groupIcon = this.view.findViewById(com.cometchat.pro.uikit.R.id.iv_group);
        tvGroupName = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_group_name);
        tvGroupDesc = this.view.findViewById(com.cometchat.pro.uikit.R.id.group_description);
        nested_scrollview = this.view.findViewById(com.cometchat.pro.uikit.R.id.nested_scrollview);

        tvGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroupDialog();
            }
        });
        tvMemberCount = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_members);
        tv_adminstrators = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_adminstrators);
        tv_moderators = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_moderators);
        tvAdminCount = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_admin_count);
        tvModeratorCount = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_moderator_count);
        tvBanMemberCount = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_ban_count);
        rvMemberList = this.view.findViewById(com.cometchat.pro.uikit.R.id.member_list);
        tvLoadMore = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_load_more);
        tvLoadMore.setText(String.format(getResources().getString(com.cometchat.pro.uikit.R.string.load_more_members), LIMIT));
        TextView tvAddMember = this.view.findViewById(com.cometchat.pro.uikit.R.id.tv_add_member);
        callBtn = this.view.findViewById(com.cometchat.pro.uikit.R.id.callBtn_iv);
        videoCallBtn = this.view.findViewById(com.cometchat.pro.uikit.R.id.video_callBtn_iv);
        rlBanMembers = this.view.findViewById(com.cometchat.pro.uikit.R.id.rlBanView);
        rlBanMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBanMemberListScreen();
            }
        });
        rlAddMemberView = this.view.findViewById(com.cometchat.pro.uikit.R.id.rl_add_member);
        rlAddMemberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMembers();
            }
        });
        rlAdminListView = this.view.findViewById(com.cometchat.pro.uikit.R.id.rlAdminView);
        rlAdminListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdminListScreen(false);
            }
        });
        rlModeratorView = this.view.findViewById(com.cometchat.pro.uikit.R.id.rlModeratorView);
        rlModeratorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdminListScreen(true);
            }
        });
        tvDelete = this.view.findViewById(R.id.tv_delete);
        TextView tvExit = this.view.findViewById(R.id.tv_exit);
        //toolbar = view.findViewById(com.cometchat.pro.uikit.R.id.groupDetailToolbar);

        //tvDelete.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        //tvExit.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        ///tvAddMember.setTypeface(fontUtils.getTypeFace(FontUtils.robotoRegular));

        // setSupportActionBar(toolbar);

       /* if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvMemberList.setLayoutManager(linearLayoutManager);
//        rvMemberList.setNestedScrollingEnabled(false);

        handleIntent();
        //checkDarkMode();

        sharedMediaView = this.view.findViewById(com.cometchat.pro.uikit.R.id.shared_media_view);
        sharedMediaView.setRecieverId(guid);
        sharedMediaView.setRecieverType(CometChatConstants.RECEIVER_TYPE_GROUP);
        sharedMediaView.reload();

        rvMemberList.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvMemberList, new ClickListener() {
            @Override
            public void onClick(View var1, int var2) {
                GroupMember user = (GroupMember) var1.getTag(com.cometchat.pro.uikit.R.string.user);
                if (loggedInUserScope != null && (loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN) || loggedInUserScope.equals(CometChatConstants.SCOPE_MODERATOR))) {
                    groupMember = user;
                    boolean isAdmin = user.getScope().equals(CometChatConstants.SCOPE_ADMIN);
                    boolean isSelf = loggedInUser.getUid().equals(user.getUid());
                    boolean isOwner = loggedInUser.getUid().equals(ownerId);
                    if (!isSelf) {
                        if (!isAdmin || isOwner) {
                            registerForContextMenu(rvMemberList);
                            getActivity().openContextMenu(var1);
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
        tvExit.setOnClickListener(view -> createDialog(getResources().getString(com.cometchat.pro.uikit.R.string.exit_group_title), getResources().getString(com.cometchat.pro.uikit.R.string.exit_group_message),
                getResources().getString(com.cometchat.pro.uikit.R.string.exit), getResources().getString(com.cometchat.pro.uikit.R.string.cancel), com.cometchat.pro.uikit.R.drawable.ic_exit_to_app));

        callBtn.setOnClickListener(view -> checkOnGoingCall(CometChatConstants.CALL_TYPE_AUDIO));

        videoCallBtn.setOnClickListener(view -> checkOnGoingCall(CometChatConstants.CALL_TYPE_VIDEO));

        tvDelete.setOnClickListener(view -> createDialog(getResources().getString(com.cometchat.pro.uikit.R.string.delete_group_title), getResources().getString(com.cometchat.pro.uikit.R.string.delete_group_message),
                getResources().getString(com.cometchat.pro.uikit.R.string.delete), getResources().getString(com.cometchat.pro.uikit.R.string.cancel), com.cometchat.pro.uikit.R.drawable.ic_delete_24dp));

        getGroupMembers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_detail_screen, container, false);
        initComponent();

        return view;
    }

    private void updateGroupDialog() {
        dialog = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(com.cometchat.pro.uikit.R.layout.update_group, null);
        Avatar avatar = view.findViewById(com.cometchat.pro.uikit.R.id.group_icon);
        TextInputEditText avatar_url = view.findViewById(com.cometchat.pro.uikit.R.id.icon_url_edt);
        if (groupIcon.getAvatarUrl() != null) {
            avatar.setVisibility(View.VISIBLE);
            avatar.setAvatar(groupIcon.getAvatarUrl());
            avatar_url.setText(groupIcon.getAvatarUrl());
        } else {
            avatar.setVisibility(View.GONE);
        }
        TextInputEditText groupName = view.findViewById(com.cometchat.pro.uikit.R.id.groupname_edt);
        TextInputEditText groupDesc = view.findViewById(com.cometchat.pro.uikit.R.id.groupdesc_edt);
        TextInputEditText groupOldPwd = view.findViewById(com.cometchat.pro.uikit.R.id.group_old_pwd);
        TextInputEditText groupNewPwd = view.findViewById(com.cometchat.pro.uikit.R.id.group_new_pwd);
        TextInputLayout groupOldPwdLayout = view.findViewById(com.cometchat.pro.uikit.R.id.input_group_old_pwd);
        TextInputLayout groupNewPwdLayout = view.findViewById(com.cometchat.pro.uikit.R.id.input_group_new_pwd);
        Spinner groupTypeSp = view.findViewById(com.cometchat.pro.uikit.R.id.groupTypes);
        MaterialButton updateGroupBtn = view.findViewById(com.cometchat.pro.uikit.R.id.updateGroupBtn);
        MaterialButton cancelBtn = view.findViewById(com.cometchat.pro.uikit.R.id.cancelBtn);
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
                    Glide.with(getActivity()).load(s.toString()).into(avatar);
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
                    groupName.setError(getString(com.cometchat.pro.uikit.R.string.fill_this_field));
                } else if (groupTypeSp.getSelectedItemPosition() == 2) {
                    if (gPassword != null && groupOldPwd.getText().toString().trim().isEmpty()) {
                        groupOldPwd.setError(getResources().getString(com.cometchat.pro.uikit.R.string.fill_this_field));
                    } else if (gPassword != null && !groupOldPwd.getText().toString().trim().equals(gPassword.trim())) {
                        groupOldPwd.setError(getResources().getString(com.cometchat.pro.uikit.R.string.password_not_matched));
                    } else if (groupNewPwd.getText().toString().trim().isEmpty()) {
                        groupNewPwd.setError(getResources().getString(com.cometchat.pro.uikit.R.string.fill_this_field));
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
                    Snackbar.make(rvMemberList, getResources().getString(com.cometchat.pro.uikit.R.string.group_updated), Snackbar.LENGTH_LONG).show();
                    getGroup();
                }
                dialog.dismiss();
            }

            @Override
            public void onError(CometChatException e) {
                if (rvMemberList != null) {
                    Snackbar.make(rvMemberList, getResources().getString(com.cometchat.pro.uikit.R.string.group_update_failed) + " " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
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
                Toast.makeText(getActivity(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This method is used whenever user click <b>Banned Members</b>. It takes user to
     * <code>CometChatBanMemberScreenActivity.class</code>
     *
     * @see CometChatBanMemberScreenActivity
     */
    private void openBanMemberListScreen() {
        Intent intent = new Intent(getActivity(), CometChatBanMemberScreenActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, guid);
        intent.putExtra(StringContract.IntentStrings.GROUP_NAME, gName);
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
        Intent intent = new Intent(getActivity(), CometChatAddMemberScreenActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, guid);
        intent.putExtra(StringContract.IntentStrings.GROUP_MEMBER, groupMemberUids);
        intent.putExtra(StringContract.IntentStrings.GROUP_NAME, gName);
        intent.putExtra(StringContract.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
        intent.putExtra(StringContract.IntentStrings.IS_ADD_MEMBER, true);
        startActivity(intent);
    }

    /**
     * This method is used whenever user click <b>Administrator</b>. It takes user to
     * <code>CometChatAdminListScreenActivity.class</code>
     *
     * @see CometChatAdminModeratorListScreenActivity
     */
    public void openAdminListScreen(boolean showModerators) {
        Intent intent = new Intent(getActivity(), CometChatAdminModeratorListScreenActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, guid);
        intent.putExtra(StringContract.IntentStrings.SHOW_MODERATORLIST, showModerators);
        intent.putExtra(StringContract.IntentStrings.GROUP_OWNER, ownerId);
        intent.putExtra(StringContract.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
        startActivity(intent);
    }

    /**
     * This method is used to handle the intent passed to this activity.
     */
    private void handleIntent() {

        if (getArguments().containsKey(StringContract.IntentStrings.GUID)) {
            guid = getArguments().getString(StringContract.IntentStrings.GUID);
            groupId = getArguments().getString(StringContract.IntentStrings.GUID);
            Log.i(TAG, "handleIntent: group id "+guid);
        }
        if (getArguments().containsKey(StringContract.IntentStrings.MEMBER_SCOPE)) {
            loggedInUserScope = guid = getArguments().getString(StringContract.IntentStrings.MEMBER_SCOPE);

            if (loggedInUserScope != null && loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN)) {
                rlAddMemberView.setVisibility(View.VISIBLE);
                rlBanMembers.setVisibility(View.VISIBLE);
                rlModeratorView.setVisibility(View.VISIBLE);
                tvDelete.setVisibility(View.VISIBLE);
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
        if (getArguments().containsKey(StringContract.IntentStrings.NAME)) {
            gName = getArguments().getString(StringContract.IntentStrings.NAME);
            tvGroupName.setText(gName);
        }
        if (getArguments().containsKey(StringContract.IntentStrings.AVATAR)) {
            String avatar = guid = getArguments().getString(StringContract.IntentStrings.AVATAR);
            if (avatar != null && !avatar.isEmpty())
                groupIcon.setAvatar(avatar);
            else
                groupIcon.setInitials(gName);
        }
        if (getArguments().containsKey
                (StringContract.IntentStrings.GROUP_DESC)) {
            gDesc = guid = getArguments().getString
                    (StringContract.IntentStrings.GROUP_DESC);
            tvGroupDesc.setText(gDesc);
        }
        if (getArguments().containsKey
                (StringContract.IntentStrings.GROUP_PASSWORD)) {
            gPassword = guid = getArguments().getString
                    (StringContract.IntentStrings.GROUP_PASSWORD);
        }
        if (getArguments().containsKey
                (StringContract.IntentStrings.GROUP_OWNER)) {
            ownerId = guid = getArguments().getString
                    (StringContract.IntentStrings.GROUP_OWNER);
        }
        if (getArguments().containsKey
                (StringContract.IntentStrings.MEMBER_COUNT)) {
            tvMemberCount.setVisibility(View.VISIBLE);
            groupMemberCount = Integer.parseInt(getArguments().getString(StringContract.IntentStrings.MEMBER_COUNT));
            tvMemberCount.setText((groupMemberCount) + " Members");
        }
        if (getArguments().containsKey
                (StringContract.IntentStrings.GROUP_TYPE)) {
            groupType = guid = getArguments().getString
                    (StringContract.IntentStrings.GROUP_TYPE);
        }
    }

    private void checkDarkMode() {
        if (Utils.isDarkMode(getActivity())) {
            //toolbar.setTitleTextColor(getResources().getColor(com.cometchat.pro.uikit.R.color.textColorWhite));
            //toolbar.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.darkModeBackground));
            tvGroupName.setTextColor(getResources().getColor(com.cometchat.pro.uikit.R.color.textColorWhite));
            nested_scrollview.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.cardview_dark_background));
            dividerAdmin.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.grey));
            dividerModerator.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.grey));
            dividerBan.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.grey));
            divider2.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.grey));
            tv_moderators.setTextColor(getResources().getColor(com.cometchat.pro.uikit.R.color.textColorWhite));
            tv_adminstrators.setTextColor(getResources().getColor(com.cometchat.pro.uikit.R.color.textColorWhite));
        } else {
            //toolbar.setTitleTextColor(getResources().getColor(com.cometchat.pro.uikit.R.color.textColorWhite));
            tvGroupName.setTextColor(getResources().getColor(com.cometchat.pro.uikit.R.color.primaryTextColor));
            dividerAdmin.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.light_grey));
            dividerModerator.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.light_grey));
            dividerBan.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.light_grey));
            divider2.setBackgroundColor(getResources().getColor(com.cometchat.pro.uikit.R.color.light_grey));
        }
    }

    /**
     * This method is used to get list of group members. It also helps to update other things like
     * Admin count.
     *
     * @see GroupMembersRequest#fetchNext(CometChat.CallbackListener)
     * @see GroupMember
     */
    private void getGroupMembers() {
        Log.i(TAG, "getGroupMembers: called "+guid);
        if (groupMembersRequest == null) {
            groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(groupId).setLimit(LIMIT).build();
        }
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Log.e(TAG, "onSuccess: get group members  " + groupMembers.size());
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
                        groupMemberAdapter = new GroupMemberAdapter(getActivity(), groupMembers,ownerId);
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
                Snackbar.make(rvMemberList, getResources().getString(com.cometchat.pro.uikit.R.string.group_member_list_error), Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + e.getMessage());
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

        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(getActivity());
        alert_dialog.setTitle(title);
        alert_dialog.setMessage(message);
        alert_dialog.setPositiveButton(positiveText, (dialogInterface, i) -> {

            if (positiveText.equalsIgnoreCase(getResources().getString(com.cometchat.pro.uikit.R.string.exit)))
                leaveGroup();

            else if (positiveText.equalsIgnoreCase(getResources().getString(com.cometchat.pro.uikit.R.string.delete))
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
     * This method is used to leave the loggedIn User from respective group.
     *
     * @see CometChat#leaveGroup(String, CometChat.CallbackListener)
     */
    private void leaveGroup() {
        CometChat.leaveGroup(groupId, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {

                Log.i(TAG, "onSuccess: leave group");
                User user = CometChat.getLoggedInUser();
                leftGroupMembersFromServer(groupId, user.getUid());
            }

            @Override
            public void onError(CometChatException e) {
                Snackbar.make(rlAddMemberView, getResources().getString(com.cometchat.pro.uikit.R.string.leave_group_error), Snackbar.LENGTH_SHORT).show();
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
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id., fragment, TAG);
        ft.commit();
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
        SharedPreferences domainUrlPref = getActivity().getSharedPreferences("domainUrlPref", MODE_PRIVATE);
        String domain = domainUrlPref.getString(General.DOMAIN, null);
        RequestBody requestBody = NetworkCall_.make(requestMap, domain + url, TAG, getActivity());
        Log.e(TAG, "leftGroupMembersFromServer: request body " + requestBody);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(domain + url, requestBody, TAG, getActivity());
                if (response != null) {
                    JSONObject responseJsonObj = new JSONObject(response);
                    Log.e(TAG, "leftGroupMembersFromServer : response" + response);
                    JSONArray left_group_members = responseJsonObj.getJSONArray("left_group_members");
                        try {
                            String msg = left_group_members.getJSONObject(0).getString("msg");
                            Toast.makeText(getActivity(), ""+msg, Toast.LENGTH_SHORT).show();

                            /**/

                            Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                            startActivity(intent);
                           /* Fragment fragment = GetFragments.get(id, bundle);
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            //ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                            ft.replace(R.id.app_bar_main_container, fragment, TAG);
                            ft.commit();*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    //navigateToGroupList();
                } else {
                    Toast.makeText(getActivity(), "Server error..", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkOnGoingCall(String callType) {
        if (CometChat.getActiveCall() != null && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING) && CometChat.getActiveCall().getSessionId() != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(getResources().getString(com.cometchat.pro.uikit.R.string.ongoing_call))
                    .setMessage(getResources().getString(com.cometchat.pro.uikit.R.string.ongoing_call_message))
                    .setPositiveButton(getResources().getString(com.cometchat.pro.uikit.R.string.join), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.joinOnGoingCall(getActivity());
                        }
                    }).setNegativeButton(getResources().getString(com.cometchat.pro.uikit.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            initiateGroupCall(guid, CometChatConstants.RECEIVER_TYPE_GROUP, callType);
        }
    }

    public void initiateGroupCall(String recieverID, String receiverType, String callType) {
        Call call = new Call(recieverID, receiverType, callType);
        CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                Utils.startGroupCallIntent(getActivity(), ((Group) call.getCallReceiver()), call.getType(), true, call.getSessionId());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: " + e.getMessage());
                if (rvMemberList != null)
                    Snackbar.make(rvMemberList, getResources().getString(com.cometchat.pro.uikit.R.string.call_initiate_error) + ":" + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
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
                Snackbar.make(rvMemberList, getResources().getString(com.cometchat.pro.uikit.R.string.group_delete_error), Snackbar.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void launchUnified() {
        Intent intent = new Intent(getActivity(), CometChatUnified.class);
        startActivity(intent);
    }
}