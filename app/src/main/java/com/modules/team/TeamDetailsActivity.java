package com.modules.team;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.Config;
import com.firebase.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.modules.announcement.AnnouncementListFragment;
import com.modules.calendar.CalendarFragment;
import com.modules.crisis.CrisisFragment;
import com.modules.fms.FileSharingListFragment;
import com.modules.messageboard.MessageBoardFragment;
import com.modules.task.TeamTaskListFragment;
import com.modules.team.gallery.fragment.GalleryListFragment;
import com.modules.team.team_invitation_werhope.activity.TeamInviteMemberActivity;
import com.modules.teamtalk.fragment.TeamTalkListFragment;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.CometChatTeamMembers_;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.models.TeamCounters_;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.Team_;
import com.sagesurfer.tasks.PerformGetTeamsTask;
import com.sagesurfer.tasks.PerformGetUsersTask;
import com.sagesurfer.views.CircleGray;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;
import com.szugyi.circlemenu.view.CircleImageView;
import com.szugyi.circlemenu.view.CircleLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Monika on 6/7/2018.
 */

public class TeamDetailsActivity extends AppCompatActivity implements MainActivityInterface, ContactRecycleViewAdapter.ContactRecycleViewAdapterListener,
        CircleLayout.OnCenterClickListener, CircleLayout.OnItemClickListener {
    private static final String TAG = TeamDetailsActivity.class.getSimpleName();
    @BindView(R.id.linearlayout_team)
    LinearLayout linearLayoutTeam;
    @BindView(R.id.view_vertical_one)
    View viewVerticalOne;
    @BindView(R.id.linearlayout_tasklist)
    LinearLayout linearLayoutTaskList;
    @BindView(R.id.textview_tasklist_counter)
    Button buttonTaskListCounter;
    @BindView(R.id.imageview_tasklist)
    AppCompatImageView imageViewTaskList;
    @BindView(R.id.view_vertical_two)
    View viewVerticalTwo;
    @BindView(R.id.linearlayout_events)
    LinearLayout linearLayoutEvents;
    @BindView(R.id.textview_events_counter)
    Button buttonEventsCounter;
    @BindView(R.id.imageview_events)
    AppCompatImageView imageViewEvents;
    @BindView(R.id.view_horizontal_one)
    View viewHorizontalOne;
    @BindView(R.id.linearlayout_fileshare)
    LinearLayout linearLayoutFileShare;
    @BindView(R.id.textview_fileshare_counter)
    Button buttonFileShareCounter;
    @BindView(R.id.imageview_fileshare)
    AppCompatImageView imageViewFileShare;
    @BindView(R.id.view_vertical_three)
    View viewVerticalThree;
    @BindView(R.id.linearlayout_gallery)
    LinearLayout linearLayoutGallery;
    @BindView(R.id.textview_gallery_counter)
    Button buttonGalleryCounter;
    @BindView(R.id.imageview_gallery)
    AppCompatImageView imageViewGallery;
    @BindView(R.id.view_vertical_four)
    View viewVerticalFour;
    @BindView(R.id.linearlayout_teamtalk)
    LinearLayout linearLayoutTeamTalk;
    @BindView(R.id.textview_teamtalk_counter)
    Button buttonTeamTalkCounter;
    @BindView(R.id.imageview_teamtalk)
    AppCompatImageView imageViewTeamTalk;
    @BindView(R.id.textview_teamtalk)
    TextView textViewTeamTalk;
    @BindView(R.id.view_horizontal_two)
    View viewHorizontalTwo;
    @BindView(R.id.linearlayout_crisisplan)
    LinearLayout linearLayoutCrisisPlan;
    @BindView(R.id.textview_crisisplan_counter)
    Button buttonCrisisPlanCounter;
    @BindView(R.id.imageview_crisisplan)
    AppCompatImageView imageViewCrisisPlan;
    @BindView(R.id.linearlayout_messageboard)
    LinearLayout linearLayoutMessageBoard;
    @BindView(R.id.textview_messageboard_counter)
    Button buttonMessageBoardCounter;
    @BindView(R.id.imageview_messageboard)
    AppCompatImageView imageViewMessageBoard;
    @BindView(R.id.view_vertical_five)
    View viewVerticalFive;
    @BindView(R.id.linearlayout_announcements)
    LinearLayout linearLayoutAnnouncements;
    @BindView(R.id.textview_announcement_counter)
    Button buttonAnnouncementCounter;
    @BindView(R.id.imageview_announcements)
    AppCompatImageView imageViewAnnouncements;
    @BindView(R.id.view_vertical_six)
    View viewVerticalSix;
    @BindView(R.id.linearlayout_stats)
    LinearLayout linearLayoutStats;
    @BindView(R.id.textview_stats_counter)
    Button buttonStatsCounter;
    @BindView(R.id.imageview_stats)
    AppCompatImageView imageViewStats;
    /*@BindView(R.id.view_vertical_seven)
    View viewVerticalSeven;*/
    @BindView(R.id.linearlayout_poll)
    LinearLayout linearLayoutPoll;
    @BindView(R.id.textview_poll_counter)
    Button buttonPollCounter;
    @BindView(R.id.imageview_poll)
    AppCompatImageView imageViewPoll;
    @BindView(R.id.framelayout_circlularimages)
    FrameLayout frameLayoutCircularImages;
    @BindView(R.id.linearlayout_statastics)
    LinearLayout linearLayoutStatastics;
    @BindView(R.id.framelayout_banner)
    FrameLayout frameLayoutBanner;
    @BindView(R.id.linearlayout_horizontal_members)
    LinearLayout linearLayoutHorizontalMembers;
    @BindView(R.id.textview_teamdetails_username0)
    TextView textViewUsername0;
    @BindView(R.id.textview_teamdetails_role0)
    TextView textViewRole0;
    @BindView(R.id.textview_teamdetails_username1)
    TextView textViewUsername1;
    @BindView(R.id.textview_teamdetails_role1)
    TextView textViewRole1;
    @BindView(R.id.textview_teamdetails_username2)
    TextView textViewUsername2;
    @BindView(R.id.textview_teamdetails_role2)
    TextView textViewRole2;
    @BindView(R.id.textview_teamdetails_username3)
    TextView textViewUsername3;
    @BindView(R.id.textview_teamdetails_role3)
    TextView textViewRole3;
    @BindView(R.id.textview_teamdetails_username4)
    TextView textViewUsername4;
    @BindView(R.id.textview_teamdetails_role4)
    TextView textViewRole4;
    @BindView(R.id.textview_teamdetails_username5)
    TextView textViewUsername5;
    @BindView(R.id.textview_teamdetails_role5)
    TextView textViewRole5;
    @BindView(R.id.textview_teamdetails_username6)
    TextView textViewUsername6;
    @BindView(R.id.textview_teamdetails_role6)
    TextView textViewRole6;

    private int team_id = 0;
    private Toolbar toolbar;
    private TextView titleText;
    private Boolean fromFragment = false;
    private ArrayList<Teams_> teamArrayList = new ArrayList<>();
    private TeamCounters_ teamCounter = new TeamCounters_();
    private ArrayList<Friends_> contactsArrayList = new ArrayList<>();
    private CircleLayout circleLayout, circleLayoutDots;
    private CircleGray circle0, circle1, circle2, circle3, circle4, circle5, circle6;
    private CircleImageView circleImageViewMember0, circleImageViewMember1, circleImageViewMember2, circleImageViewMember3, circleImageViewMember4, circleImageViewMember5, circleImageViewMember6;
    private ImageView memberStatusIcon0, memberStatusIcon1, memberStatusIcon2, memberStatusIcon3, memberStatusIcon4, memberStatusIcon5, memberStatusIcon6;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<CometChatTeamMembers_> cometChatTeamMemberList = new ArrayList<>();
    private Teams_ team = new Teams_();
    ArrayList<TeamCounters_> teamCountersArrayList = new ArrayList<>();
    private ImageView banner;
    private RelativeLayout relativeLayoutImage0;
    public AppCompatImageView postButton;
    private boolean showIcon = false;

    @SuppressLint({"RestrictedApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_team_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initViews();

        titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setImageResource(R.drawable.invite_team_member);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inviteMemberIntent = new Intent(TeamDetailsActivity.this, TeamInviteMemberActivity.class);
                inviteMemberIntent.putExtra(General.TEAM, team);
                startActivity(inviteMemberIntent);
            }
        });
        postButton.setVisibility(View.GONE);
        InviteButton();



       /* if (((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage0 23)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025))) ||
                (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage028))) ||
                (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage008))) &&
                (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID)) ||
                        Preferences.get(General.TEAM_TYPE).equalsIgnoreCase(General.TEAM_TYPE_FACILITATED))
                && Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")){
            postButton.setVisibility(View.VISIBLE);
        }*/

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                assert intent.getAction() != null;
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    if (!Preferences.getBoolean(General.IS_PUSH_NOTIFICATION_SENT)) {
                        NotificationUtils.clearNotifications(getApplicationContext());
                        for (Map.Entry<String, List<String>> entry : Config.mapOfPosts.entrySet()) {
                            String key = entry.getKey();
                            Preferences.save(General.IS_PUSH_NOTIFICATION_SENT, true);
                            showNotificationMessage(entry.getValue().get(0), entry.getValue().get(1), entry.getValue().get(2), entry.getKey());
                        }
                    }
                }
            }
        };
    }

    public void initViews() {
        int color = GetColor.getHomeIconBackgroundColorColorParse(true);

        banner = (ImageView) findViewById(R.id.team_details_banner);
        circleLayout = (CircleLayout) findViewById(R.id.circle_layout);
//        circleLayout.getRadius();

        circleLayoutDots = (CircleLayout) findViewById(R.id.circle_layout_dots);
        circleLayout.setOnCenterClickListener(this); //for center item click in circular layout "circleimageview_teamdetails_member0"
        circleLayout.setOnItemClickListener(this); //for item click in circular layout "circleimageview_teamdetails_member1", etc
        relativeLayoutImage0 = (RelativeLayout) findViewById(R.id.relativelayout_image0);
        circle0 = (CircleGray) findViewById(R.id.circle0);
        circle1 = (CircleGray) findViewById(R.id.circle1);
        circle2 = (CircleGray) findViewById(R.id.circle2);
        circle3 = (CircleGray) findViewById(R.id.circle3);
        circle4 = (CircleGray) findViewById(R.id.circle4);
        circle5 = (CircleGray) findViewById(R.id.circle5);
        circle6 = (CircleGray) findViewById(R.id.circle6);
        circleImageViewMember0 = (CircleImageView) findViewById(R.id.circleimageview_teamdetails_member0);
        circleImageViewMember1 = (CircleImageView) findViewById(R.id.circleimageview_teamdetails_member1);
        circleImageViewMember2 = (CircleImageView) findViewById(R.id.circleimageview_teamdetails_member2);
        circleImageViewMember3 = (CircleImageView) findViewById(R.id.circleimageview_teamdetails_member3);
        circleImageViewMember4 = (CircleImageView) findViewById(R.id.circleimageview_teamdetails_member4);
        circleImageViewMember5 = (CircleImageView) findViewById(R.id.circleimageview_teamdetails_member5);
        circleImageViewMember6 = (CircleImageView) findViewById(R.id.circleimageview_teamdetails_member6);

        memberStatusIcon0 = (ImageView) findViewById(R.id.member_status_icon0);
        memberStatusIcon1 = (ImageView) findViewById(R.id.member_status_icon1);
        memberStatusIcon2 = (ImageView) findViewById(R.id.member_status_icon2);
        memberStatusIcon3 = (ImageView) findViewById(R.id.member_status_icon3);
        memberStatusIcon4 = (ImageView) findViewById(R.id.member_status_icon4);
        memberStatusIcon5 = (ImageView) findViewById(R.id.member_status_icon5);
        memberStatusIcon6 = (ImageView) findViewById(R.id.member_status_icon6);

        imageViewStats.setColorFilter(color);
        imageViewStats.setImageResource(R.drawable.vi_drawer_teams);

        imageViewTaskList.setColorFilter(color);
        imageViewTaskList.setImageResource(R.drawable.vi_home_task_list);

        imageViewEvents.setColorFilter(color);
        imageViewEvents.setImageResource(R.drawable.vi_caseload_events);

        imageViewFileShare.setColorFilter(color);
        imageViewFileShare.setImageResource(R.drawable.vi_drawer_file_sharing);

        imageViewGallery.setColorFilter(color);
        imageViewGallery.setImageResource(R.drawable.vi_notify_gallery);

        imageViewTeamTalk.setColorFilter(color);
        imageViewTeamTalk.setImageResource(R.drawable.vi_drawer_teamtalk);

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015") && CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            linearLayoutCrisisPlan.setVisibility(View.GONE);
            linearLayoutMessageBoard.setVisibility(View.VISIBLE);
            imageViewMessageBoard.setColorFilter(color);
            imageViewMessageBoard.setImageResource(R.drawable.vi_team_messageboard);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021")) {
            linearLayoutCrisisPlan.setVisibility(View.GONE);
            linearLayoutMessageBoard.setVisibility(View.VISIBLE);
            imageViewMessageBoard.setColorFilter(color);
            imageViewMessageBoard.setImageResource(R.drawable.vi_team_messageboard);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage006") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage035")) {
            linearLayoutCrisisPlan.setVisibility(View.VISIBLE);
            linearLayoutMessageBoard.setVisibility(View.GONE);
            imageViewCrisisPlan.setColorFilter(color);
            imageViewCrisisPlan.setImageResource(R.drawable.vi_drawer_crisisplan);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage024")) {
            linearLayoutPoll.setVisibility(View.GONE);
            linearLayoutCrisisPlan.setVisibility(View.GONE);
            linearLayoutMessageBoard.setVisibility(View.VISIBLE);
            imageViewMessageBoard.setColorFilter(color);
            imageViewMessageBoard.setImageResource(R.drawable.vi_team_messageboard);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage008") ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) {
            linearLayoutStats.setVisibility(View.GONE);
            linearLayoutCrisisPlan.setVisibility(View.GONE);
            linearLayoutMessageBoard.setVisibility(View.VISIBLE);
            imageViewMessageBoard.setColorFilter(color);
            imageViewMessageBoard.setImageResource(R.drawable.vi_team_messageboard);
        } else {
            linearLayoutCrisisPlan.setVisibility(View.GONE);
            linearLayoutMessageBoard.setVisibility(View.VISIBLE);
            imageViewMessageBoard.setColorFilter(color);
            imageViewMessageBoard.setImageResource(R.drawable.vi_team_messageboard);
        }

        imageViewAnnouncements.setColorFilter(color);
        imageViewAnnouncements.setImageResource(R.drawable.vi_drawer_announcements);

        imageViewPoll.setColorFilter(color);
        imageViewPoll.setImageResource(R.drawable.vi_notify_poll);

        viewVerticalOne.setBackgroundColor(color);
        viewVerticalTwo.setBackgroundColor(color);
        viewVerticalThree.setBackgroundColor(color);
        viewVerticalFour.setBackgroundColor(color);
        viewVerticalFive.setBackgroundColor(color);
        viewVerticalSix.setBackgroundColor(color);
        //viewVerticalSeven.setBackgroundColor(color);
        viewVerticalOne.setBackgroundColor(color);
        viewHorizontalOne.setBackgroundColor(color);
        viewHorizontalTwo.setBackgroundColor(color);

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
            textViewTeamTalk.setText(getResources().getString(R.string.team_discussion));
        } else {
            textViewTeamTalk.setText(getResources().getString(R.string.team_talk));
        }
    }

    // show notification if push is received
    private void showNotificationMessage(String title, String message, String type, String timestamp) {
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        resultIntent.putExtra(General.MESSAGE, message);
        resultIntent.putExtra(General.TIMESTAMP, timestamp);
        resultIntent.putExtra(General.TITLE, title);
        resultIntent.putExtra(General.TYPE, type);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timestamp, type, resultIntent);
    }

    @Override
    public void onBackPressed() {
        if (fromFragment) {
            fromFragment = false;
            Preferences.save(General.TEAM_ANNOUNCEMENT_ID, "");
            Preferences.save(General.TASKLIST_ID, "");

            Teams_ team_ = new Teams_();
            team_.setId(Integer.parseInt(Preferences.get(General.TEAM_ID)));
            team_.setName(Preferences.get(General.TEAM_NAME));
            team_.setBanner(Preferences.get(General.BANNER_IMG));

            Intent detailsIntent = new Intent(getApplicationContext(), TeamDetailsActivity.class);
            detailsIntent.putExtra(General.TEAM, team);
            startActivity(detailsIntent);
            finish();
        } else {
            super.onBackPressed();
            finish();
        }
    }

//    public void showhideInviteButton(){
//        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
//                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025))) {
//            //Visible and Invisible the invite button
//            if (team.getType() == 2) {
//                if (CheckRole.showFabIcon(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
//                    if (fromFragment) {
//                        postButton.setVisibility(View.GONE);
//                    } else {
//                        postButton.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    postButton.setVisibility(View.GONE);
//                }
//            } else {
//                if (team.getIs_modarator() == 1 || Preferences.get(General.ROLE_ID).equals("6")) {
//                    if (fromFragment) {
//                        postButton.setVisibility(View.GONE);
//                    } else {
//                        postButton.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    if (fromFragment) {
//                        postButton.setVisibility(View.VISIBLE);
//                    } else {
//                        postButton.setVisibility(View.GONE);
//                    }
//                }
//            }
//        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage028))) {
//            if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
//                if (team.getType() == 2) {
//                    if (fromFragment) {
//                        postButton.setVisibility(View.GONE);
//                    } else {
//                        postButton.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    postButton.setVisibility(View.GONE);
//                }
//
//            } else {
//                if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
//                        || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")) {
//                    if (fromFragment) {
//                        postButton.setVisibility(View.GONE);
//                    } else {
//                        postButton.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    if (showIcon) {
//                        postButton.setVisibility(View.VISIBLE);
//                    } else {
//                        postButton.setVisibility(View.GONE);
//                    }
//                }
//            }
//        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
//            if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
//                postButton.setVisibility(View.GONE);
//            } else {
//                if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
//                        || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")) {
//                    if (fromFragment) {
//                        postButton.setVisibility(View.GONE);
//                    } else {
//                        postButton.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    postButton.setVisibility(View.GONE);
//                }
//            }
//        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage008))) {
//            if (Preferences.get("Owner_ID").equalsIgnoreCase(Preferences.get(General.USER_ID)) || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")) {
//                if (fromFragment) {
//                    postButton.setVisibility(View.GONE);
//                } else {
//                    postButton.setVisibility(View.VISIBLE);
//                }
//            } else {
//                postButton.setVisibility(View.GONE);
//            }
//        }
//    }

    public void InviteButton() {
        /*Above condition for show and hide Team Invitation Button as per Roll*/
        if (((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage025))) ||
                (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage028))) ||
                (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage008))) &&
                (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID)) ||
                        (Preferences.get(General.TYPE).equalsIgnoreCase(General.TEAM_TYPE_FACILITATED)
                                && Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")))) {
            postButton.setVisibility(View.VISIBLE);
        } else {
            postButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        Intent data = getIntent();

        if (data != null && data.hasExtra(General.TEAM)) {
            team = (Teams_) data.getSerializableExtra(General.TEAM);
            showIcon = data.getBooleanExtra("showIcon", true);
            team_id = team.getId();

            titleText.setText(team.getName());

            /*  setData(team);
             */
            /*  showhideInviteButton();*/
//            InviteButton();

        } else {
            if (Preferences.getBoolean(General.IS_PUSH_NOTIFICATION)) {
                for (Map.Entry<String, List<String>> entry : Config.mapOfPosts.entrySet()) {
                    String timestamp = entry.getKey();
                    String group_id = entry.getValue().get(3);
                    if (!group_id.equalsIgnoreCase("0")) {
                        Preferences.save(General.GROUP_ID, group_id);
                    }
                }
                Config.mapOfPosts = new HashMap<>();
                team_id = Integer.parseInt(Preferences.get(General.GROUP_ID));
            } else {
                onBackPressed();
            }
        }

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        getCometChatTeamMembers();

        fetchTeamDetails();
    }

    // set team banner image
   /* private void setData(Teams_ teams_) {
        Glide.with(getApplicationContext()).load(teams_)
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(banner);
    }*/

    //make network call to fetch team contacts(cometchat) with team id
    private void getCometChatTeamMembers() {
        try {
            cometChatTeamMemberList = PerformGetUsersTask.getCometChatTeamMembers(Actions_.COMETCHAT, TeamDetailsActivity.this, TAG, this);
            if (cometChatTeamMemberList.size() > 0 && cometChatTeamMemberList.size() <= 6) {
                if (cometChatTeamMemberList.get(0).getStatus() == 1) {
                    frameLayoutCircularImages.setVisibility(View.VISIBLE);
                    frameLayoutBanner.setVisibility(View.GONE);

                    CircleGray[] circle = {circle1, circle2, circle3, circle4, circle5, circle6};
                    CircleImageView[] circleImageView = {circleImageViewMember1, circleImageViewMember2, circleImageViewMember3, circleImageViewMember4, circleImageViewMember5, circleImageViewMember6};
                    ImageView[] memberStatusIcon = {memberStatusIcon1, memberStatusIcon2, memberStatusIcon3, memberStatusIcon4, memberStatusIcon5, memberStatusIcon6};
                    TextView[] circleTextViewUsername = {textViewUsername1, textViewUsername2, textViewUsername3, textViewUsername4, textViewUsername5, textViewUsername6};
                    TextView[] circleTextViewRole = {textViewRole1, textViewRole2, textViewRole3, textViewRole4, textViewRole5, textViewRole6};

                    /*CometChatTeamMembers_ cometChatTeamMembers_ = new CometChatTeamMembers_();
                    cometChatTeamMembers_.setId(Integer.parseInt(Preferences.get(General.USER_ID)));
                    cometChatTeamMembers_.setA(Preferences.get(General.PROFILE_IMAGE));
                    cometChatTeamMembers_.setFirstname(Preferences.get(General.FIRST_NAME));
                    cometChatTeamMembers_.setRole(Preferences.get(General.ROLE));
                    cometChatTeamMembers_.setS(CometChatKeys.StatusKeys.STATUS);
                    cometChatTeamMemberList.add(cometChatTeamMembers_);

                    int totalMembers = 0;

                    if (!Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
                        if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                            if (showIcon) {
                                totalMembers = cometChatTeamMemberList.size() - 1;
                            } else {
                                totalMembers = cometChatTeamMemberList.size() - 2;
                            }
                        }
                    }

                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
                        totalMembers = cometChatTeamMemberList.size() - 1;
                    } else {
                        totalMembers = cometChatTeamMemberList.size() - 1;
                    }
                    8/
                     */
                    int totalMembers = 0;
                    totalMembers = cometChatTeamMemberList.size() - 1;

                    boolean isConsumerPresent = false, isConsumerView0 = false;
                    for (int i = 0; i < cometChatTeamMemberList.size(); i++) {
                        if (cometChatTeamMemberList.get(i).getRole().contains("Consumer") || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Youth")
                                || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Adult") || cometChatTeamMemberList.get(i).getRole().contains("Peer Participant")
                                || cometChatTeamMemberList.get(i).getRole().contains("Guest") || cometChatTeamMemberList.get(i).getRole().contains("Student") || cometChatTeamMemberList.get(i).getRole().contains("Client")) {
                            isConsumerPresent = true;
                            break;
                        }
                    }
                    for (int i = 0; i < cometChatTeamMemberList.size(); i++) {
                        int index = 0;
                        if (cometChatTeamMemberList.size() == 1 || !isConsumerView0) {
                            index = i;
                        } else {
                            index = i - 1;
                        }
                        if (isConsumerPresent) {
                            if (cometChatTeamMemberList.get(i).getRole().contains("Consumer") || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Youth")
                                    || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Adult") || cometChatTeamMemberList.get(i).getRole().contains("Peer Participant")
                                    || cometChatTeamMemberList.get(i).getRole().contains("Guest") || cometChatTeamMemberList.get(i).getRole().contains("Student") || cometChatTeamMemberList.get(i).getRole().contains("Client")) {
                                isConsumerView0 = true;
                                textViewUsername0.setText(cometChatTeamMemberList.get(i).getFirstname());
//                                circleTextViewUsername[index].setText(cometChatTeamMemberList.get(i).getFirstname());
                                Glide.with(getApplicationContext()).load(cometChatTeamMemberList.get(i).getA())
                                        .thumbnail(0.5f)
                                        .transition(withCrossFade())
                                        .apply(new RequestOptions()
                                                .placeholder(GetThumbnails.userIcon(cometChatTeamMemberList.get(i).getA()))
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .transform(new CircleTransform(getApplicationContext())))
                                        .into(circleImageViewMember0);
//                                        .into(circleImageView[index]);


                            } else {
//                                textViewUsername0.setText(cometChatTeamMemberList.get(i).getFirstname());
                                circleTextViewUsername[index].setText(cometChatTeamMemberList.get(i).getFirstname());
                                if (cometChatTeamMemberList.get(i).getRole() != null) {
                                    circleTextViewRole[index].setText("(" + cometChatTeamMemberList.get(i).getRole() + ")");
                                }

                                Glide.with(getApplicationContext())
                                        .load(cometChatTeamMemberList.get(i).getA())
                                        .thumbnail(0.5f)
                                        .transition(withCrossFade())
                                        .apply(new RequestOptions()
                                                .placeholder(GetThumbnails.userIcon(cometChatTeamMemberList.get(i).getA()))
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .transform(new CircleTransform(getApplicationContext())))
                                        .into(circleImageView[index]);


                            }
                        } else {
                            String id = String.valueOf(cometChatTeamMemberList.get(i).getId());
                            char ids = id.charAt(0);
                            String u_id = new String(String.valueOf(ids));
                            if (Integer.parseInt(u_id) == Integer.parseInt(Preferences.get(General.USER_ID))) {
                                isConsumerView0 = true;
                                textViewUsername0.setText(cometChatTeamMemberList.get(i).getFirstname());
//                                circleTextViewUsername[index].setText(cometChatTeamMemberList.get(i).getFirstname());
                                Glide.with(getApplicationContext()).load(cometChatTeamMemberList.get(i).getA())
                                        .thumbnail(0.5f)
                                        .transition(withCrossFade())
                                        .apply(new RequestOptions()
                                                .placeholder(GetThumbnails.userIcon(cometChatTeamMemberList.get(i).getA()))
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .transform(new CircleTransform(getApplicationContext())))
                                        .into(circleImageViewMember0);
//                                        .into(circleImageView[index]);
                            } else {
//                                textViewUsername0.setText(cometChatTeamMemberList.get(i).getFirstname());
                                circleTextViewUsername[index].setText(cometChatTeamMemberList.get(i).getFirstname());
                                if (cometChatTeamMemberList.get(i).getRole() != null) {
                                    circleTextViewRole[index].setText("(" + cometChatTeamMemberList.get(i).getRole() + ")");
                                }

                                Glide.with(getApplicationContext())
                                        .load(cometChatTeamMemberList.get(i).getA())
                                        .thumbnail(0.5f)
                                        .transition(withCrossFade())
                                        .apply(new RequestOptions()
                                                .placeholder(GetThumbnails.userIcon(cometChatTeamMemberList.get(i).getA()))
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .transform(new CircleTransform(getApplicationContext())))
                                        .into(circleImageView[index]);


                            }
                        }
                    }

                    //removing circle view based on totalMembersCount
                    for (int i = 0; i < 6; i++) {
                        if (i >= totalMembers) {
                            if (circleLayout.getChildCount() != totalMembers && circleLayout.getChildCount() > 0) {
                                circleLayout.removeViewAt(circleLayout.getChildCount() - 1);
                                circleLayoutDots.removeViewAt(circleLayoutDots.getChildCount() - 1);
                            }
                        }
                    }
                } else {
                    frameLayoutCircularImages.setVisibility(View.GONE);
                    frameLayoutBanner.setVisibility(View.VISIBLE);
                    linearLayoutHorizontalMembers.setVisibility(View.GONE);
                }
            } else {
                if (cometChatTeamMemberList.size() == 0) {
                    circleLayout.setVisibility(View.GONE);
                    circleLayoutDots.setVisibility(View.GONE);
                    CometChatTeamMembers_ cometChatTeamMembers_ = new CometChatTeamMembers_();
                    cometChatTeamMembers_.setId(Integer.parseInt(Preferences.get(General.USER_ID)));
                    cometChatTeamMembers_.setA(Preferences.get(General.PROFILE_IMAGE));
                    cometChatTeamMembers_.setName(Preferences.get(General.FIRST_NAME));
                    cometChatTeamMembers_.setRole(Preferences.get(General.ROLE));
                    cometChatTeamMembers_.setS("");
                    cometChatTeamMemberList.add(cometChatTeamMembers_);

                    textViewUsername0.setText(cometChatTeamMemberList.get(0).getName());

                    Glide.with(getApplicationContext()).load(cometChatTeamMemberList.get(0).getA())
                            .thumbnail(0.5f)
                            .transition(withCrossFade())
                            .apply(new RequestOptions()
                                    .placeholder(GetThumbnails.userIcon(cometChatTeamMemberList.get(0).getA()))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(new CircleTransform(getApplicationContext())))
                            .into(circleImageViewMember0);
                } else if (cometChatTeamMemberList.size() > 0 && cometChatTeamMemberList.get(0).getStatus() == 1) { //if (listing page) memberCount > 7
                    frameLayoutCircularImages.setVisibility(View.GONE);
                    frameLayoutBanner.setVisibility(View.VISIBLE);
                    linearLayoutHorizontalMembers.setVisibility(View.VISIBLE);

                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(layoutManager);

                    ContactRecycleViewAdapter adapter = new ContactRecycleViewAdapter(getApplicationContext(), cometChatTeamMemberList, this);
                    recyclerView.setAdapter(adapter);
                } else {
                    frameLayoutCircularImages.setVisibility(View.GONE);
                    frameLayoutBanner.setVisibility(View.VISIBLE);
                    linearLayoutHorizontalMembers.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            frameLayoutCircularImages.setVisibility(View.GONE);
            frameLayoutBanner.setVisibility(View.VISIBLE);
            linearLayoutHorizontalMembers.setVisibility(View.GONE);
        }
        Log.e("Radius", "" + circleLayout.getRadius());
    }

    // make network call to fetch team at a glance
    private void fetchTeamDetails() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, String.valueOf(team_id));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_ALL_DATA;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this.getApplicationContext(), this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this.getApplicationContext(), this);
                Log.e("response_detail", response);
                if (response != null) {
                    //team data parsing
                    Preferences.save(General.GROUP_ID, String.valueOf(team_id));
                    teamArrayList = PerformGetTeamsTask.get(Actions_.TEAM_DATA, this, TAG, true, this);

                    if (Preferences.getBoolean(General.IS_PUSH_NOTIFICATION)) {
                        Preferences.save(General.IS_PUSH_NOTIFICATION, false);
                        titleText.setText(teamArrayList.get(0).getName());

                        Preferences.save(General.GROUP_OWNER_ID, teamArrayList.get(0).getOwnerId());
                        Preferences.save(General.IS_MODERATOR, teamArrayList.get(0).getIs_moderator());

                        Glide.with(getApplicationContext()).load(teamArrayList.get(0).getBanner())
                                .thumbnail(0.5f)
                                .transition(withCrossFade())
                                .apply(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(banner);
                    }

                    Glide.with(getApplicationContext()).load(Uri.parse(teamArrayList.get(0).getBanner()))
                            .thumbnail(0.5f)
                            .transition(withCrossFade())
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(banner);


                    //team counter parsing
                    teamCountersArrayList = Team_.parseTeamCounter(response, Actions_.GET_TEAM_COUNTER, this.getApplicationContext(), TAG);
                    setTeamCounters();

                    //team at a glance parsing
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.AT_A_GLANCE);
                    if (jsonArray != null) {
                        JsonObject object = jsonArray.get(0).getAsJsonObject();
                        setTeamAtAGlance(object);
                    }
                }
                /* showhideInviteButton();*/
                InviteButton();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTeamCounters() {
        if (teamCountersArrayList.get(0).getTasklist() != 0) {
            buttonTaskListCounter.setVisibility(View.VISIBLE);
            buttonTaskListCounter.setText(String.valueOf(teamCountersArrayList.get(0).getTasklist()));
        }

        if (teamCountersArrayList.get(0).getCalendar() != 0) {
            buttonEventsCounter.setVisibility(View.VISIBLE);
            buttonEventsCounter.setText(String.valueOf(teamCountersArrayList.get(0).getCalendar()));
        }

        if (teamCountersArrayList.get(0).getFms() != 0) {
            buttonFileShareCounter.setVisibility(View.VISIBLE);
            buttonFileShareCounter.setText(String.valueOf(teamCountersArrayList.get(0).getFms()));
        }

        if (teamCountersArrayList.get(0).getTeamtalk() != 0) {
            buttonTeamTalkCounter.setVisibility(View.VISIBLE);
            buttonTeamTalkCounter.setText(String.valueOf(teamCountersArrayList.get(0).getTeamtalk()));
        }

        if (teamCountersArrayList.get(0).getAnnouncement() != 0) {
            buttonAnnouncementCounter.setVisibility(View.VISIBLE);
            buttonAnnouncementCounter.setText(String.valueOf(teamCountersArrayList.get(0).getAnnouncement()));
        }

        if (teamCountersArrayList.get(0).getPoll() != 0) {
            buttonPollCounter.setVisibility(View.VISIBLE);
            buttonPollCounter.setText(String.valueOf(teamCountersArrayList.get(0).getPoll()));
        }

        if (teamCountersArrayList.get(0).getMessageboard() != 0) {
            buttonMessageBoardCounter.setVisibility(View.VISIBLE);
            buttonMessageBoardCounter.setText(String.valueOf(teamCountersArrayList.get(0).getMessageboard()));
        }
    }

    // set counter of team at a glance to respective fields
    private void setTeamAtAGlance(JsonObject object) {
        TextView memberCount = (TextView) findViewById(R.id.team_details_members_count);
        TextView successCount = (TextView) findViewById(R.id.team_details_success_count);
        TextView crisisCount = (TextView) findViewById(R.id.team_details_crisis_count);
        TextView supportCount = (TextView) findViewById(R.id.team_details_support_count);
        TextView timeCount = (TextView) findViewById(R.id.team_details_time_count);

        if (object.get(General.STATUS).getAsInt() == 1) {
            memberCount.setText(object.get("group_members").getAsString());
            successCount.setText(object.get("success").getAsString());
            crisisCount.setText(object.get("crisis").getAsString());
            supportCount.setText(object.get("no_of_support").getAsString());
            timeCount.setText(object.get("time_spend").getAsString());
        } else {
            memberCount.setText("0");
            successCount.setText("0");
            crisisCount.setText("0");
            supportCount.setText("0");
            timeCount.setText("0");
        }
    }

    //CircleLayout.OnCenterClickListener
    //for center item click in circular layout "circleimageview_teamdetails_member0"
    @Override
    public void onCenterClick() {
        int clickIndex = 0;
        boolean isConsumerPresent = false, isConsumerView0 = false;
        for (int i = 0; i < cometChatTeamMemberList.size(); i++) {
            if (cometChatTeamMemberList.get(i).getRole().contains("Consumer") || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Youth")
                    || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Adult") || cometChatTeamMemberList.get(i).getRole().contains("Peer Participant")
                    || cometChatTeamMemberList.get(i).getRole().contains("Guest") || cometChatTeamMemberList.get(i).getRole().contains("Student") || cometChatTeamMemberList.get(i).getRole().contains("Client")) {
                isConsumerPresent = true;
                clickIndex = i;
                break;
            }
        }
        for (int i = 0; i < cometChatTeamMemberList.size(); i++) {
            if (!isConsumerPresent) {
                if (cometChatTeamMemberList.get(i).getId() == Integer.parseInt(Preferences.get(General.USER_ID))) {
                    clickIndex = i;
                    break;
                }
            }
        }
        onItemClicked(clickIndex, true);
    }

    //CircleLayout.OnItemClickListener
    //for item click in circular layout "circleimageview_teamdetails_member1", etc
    @Override
    public void onItemClick(View view) {
        boolean isConsumerPresent = false;
        for (int i = 0; i < cometChatTeamMemberList.size(); i++) {
            if (cometChatTeamMemberList.get(i).getRole().contains("Consumer") || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Youth")
                    || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Adult") || cometChatTeamMemberList.get(i).getRole().contains("Peer Participant")
                    || cometChatTeamMemberList.get(i).getRole().contains("Guest") || cometChatTeamMemberList.get(i).getRole().contains("Student") || cometChatTeamMemberList.get(i).getRole().contains("Client")) {
                isConsumerPresent = true;
                break;
            }
        }
        switch (view.getId()) {
            case R.id.linearlayout_member1:
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID))) || !isConsumerPresent) {
                    onItemClicked(0, true);
                } else {
                    onItemClicked(1, true);
                }
                //onClicked(1);
                break;

            case R.id.linearlayout_member2:
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID))) || !isConsumerPresent) {
                    onItemClicked(1, true);
                } else {
                    onItemClicked(2, true);
                }
                //onClicked(2);
                break;

            case R.id.linearlayout_member3:
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID))) || !isConsumerPresent) {
                    onItemClicked(2, true);
                } else {
                    onItemClicked(3, true);
                }
                //onClicked(3);
                break;

            case R.id.linearlayout_member4:
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID))) || !isConsumerPresent) {
                    onItemClicked(3, true);
                } else {
                    onItemClicked(4, true);
                }
                //onClicked(4);
                break;

            case R.id.linearlayout_member5:
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID))) || !isConsumerPresent) {
                    onItemClicked(4, true);
                } else {
                    onItemClicked(5, true);
                }
                //onClicked(5);
                break;

            case R.id.linearlayout_member6:
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID))) || !isConsumerPresent) {
                    onItemClicked(5, true);
                } else {
                    onItemClicked(6, true);
                }
                //onClicked(6);
                break;
        }
    }

    public void onClicked(int position) {
        int clickIndex = 0;
        TextView[] circleTextViewUsername = {textViewUsername1, textViewUsername2, textViewUsername3, textViewUsername4, textViewUsername5, textViewUsername6};

        boolean isConsumerPresent = false, isConsumerView0 = false;
        for (int i = 0; i < cometChatTeamMemberList.size(); i++) {
            if (cometChatTeamMemberList.get(i).getRole().contains("Consumer") || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Youth")
                    || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Adult") || cometChatTeamMemberList.get(i).getRole().contains("Peer Participant")
                    || cometChatTeamMemberList.get(i).getRole().contains("Guest") || cometChatTeamMemberList.get(i).getRole().contains("Student") || cometChatTeamMemberList.get(i).getRole().contains("Client")) {
                isConsumerPresent = true;
                break;
            }
        }
        for (int i = 0; i < cometChatTeamMemberList.size(); i++) {
            int index = 0;
            if (cometChatTeamMemberList.size() == 1 || !isConsumerView0) {
                index = i;
            } else {
                index = i - 1;
            }
            if (isConsumerPresent) {
                if (cometChatTeamMemberList.get(i).getRole().contains("Consumer") || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Youth")
                        || cometChatTeamMemberList.get(i).getRole().contains("Consumer-Adult") || cometChatTeamMemberList.get(i).getRole().contains("Peer Participant") || cometChatTeamMemberList.get(i).getRole().contains("Guest") || cometChatTeamMemberList.get(i).getRole().contains("Student") || cometChatTeamMemberList.get(i).getRole().contains("Client")) {
                    isConsumerView0 = true;
                    //textViewUsername0.setText(cometChatTeamMemberList.get(i).getName());
                } else {

                    if (cometChatTeamMemberList.get(i).getName().equalsIgnoreCase(circleTextViewUsername[index].getText().toString())) {
                        clickIndex = i;
                        break;
                    }
                }
            } else {
                if (cometChatTeamMemberList.get(i).getId() == Integer.parseInt(Preferences.get(General.USER_ID))) {
                    isConsumerView0 = true;
                    //textViewUsername0.setText(cometChatTeamMemberList.get(i).getName());

                } else {

                    if (cometChatTeamMemberList.get(i).getName().equalsIgnoreCase(circleTextViewUsername[index].getText().toString())) {
                        clickIndex = i;
                        break;
                    }
                }
            }
        }
        onItemClicked(clickIndex, true);
    }

    @OnClick({R.id.linearlayout_stats, R.id.linearlayout_tasklist, R.id.linearlayout_events,
            R.id.linearlayout_fileshare, R.id.linearlayout_gallery, R.id.linearlayout_teamtalk,
            R.id.linearlayout_crisisplan, R.id.linearlayout_messageboard, R.id.linearlayout_announcements, R.id.linearlayout_poll})
    public void onButtonClick(View view) {

        linearLayoutTeam.setVisibility(View.GONE);
        postButton.setVisibility(View.GONE);
        Fragment fragment = new Fragment();
        switch (view.getId()) {
            case R.id.linearlayout_stats:
                fromFragment = true;
                fragment = new MemberStatisticsFragment();
                replaceFragment(fragment);
                break;

            case R.id.linearlayout_tasklist:
                fromFragment = true;
                fragment = new TeamTaskListFragment();
                replaceFragment(fragment);
                break;

            case R.id.linearlayout_events:
                fromFragment = true;
                fragment = new CalendarFragment();
                replaceFragment(fragment);
                break;

            case R.id.linearlayout_fileshare:
                fromFragment = true;
                fragment = new FileSharingListFragment();
                replaceFragment(fragment);
                break;

            case R.id.linearlayout_gallery:
                fromFragment = true;
                fragment = new GalleryListFragment();
                replaceFragment(fragment);
                break;

            case R.id.linearlayout_teamtalk:
                fromFragment = true;
                fragment = new TeamTalkListFragment();
                replaceFragment(fragment);
                break;

            case R.id.linearlayout_crisisplan:
                fromFragment = true;
                fragment = new CrisisFragment();
                replaceFragment(fragment);
                break;

            case R.id.linearlayout_messageboard:
                fromFragment = true;
                fragment = new MessageBoardFragment();
                replaceFragment(fragment);
                break;

            case R.id.linearlayout_announcements:
                fromFragment = true;
                fragment = new AnnouncementListFragment();
                replaceFragment(fragment);
                break;

            case R.id.linearlayout_poll:
                fromFragment = true;
                fragment = new PollListFragment();
                replaceFragment(fragment);
                break;
        }
        Preferences.save(General.GROUP_ID, String.valueOf(team_id));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PROFILE_DATA);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PROFILE_DATA);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.replace(R.id.app_bar_main_container, fragment, Actions_.GET_PROFILE_DATA);
        ft.commit();
    }

    //set Title on toolbar
    public void setMainTitle(String title) {
        titleText.setText(title);
    }

    //set Title on toolbar
    public void setToolbarBackgroundColor() {
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    //set add icon on toolbar from MainActivityInterface
    public void toggleAdd(boolean isVisible) {
    }

    //set mood toolbar from MainActivityInterface
    public void setMoodToolbar(int moodId) {
    }

    @Override
    public void onItemClicked(int position, boolean isFromCircularView) {
        if (cometChatTeamMemberList.get(position).getId() != Integer.parseInt(Preferences.get(General.USER_ID))) {
            final String contactId = String.valueOf(cometChatTeamMemberList.get(position).getId());

        }
    }

    // this function is for set visbility of invite button in all fragment
    public void inviteButtonSetVisibility() {
        postButton.setVisibility(View.GONE);
    }
}
