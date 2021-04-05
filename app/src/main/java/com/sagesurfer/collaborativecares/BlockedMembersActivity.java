package com.sagesurfer.collaborativecares;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.core.BlockedUsersRequest;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.firebase.SyncJCometchatService;
import com.modules.appointment.adapter.AppointmentListAdapter;
import com.sagesurfer.adapters.Blockeduserlistadapter;
import com.sagesurfer.library.GetColor;

import java.util.List;

public class BlockedMembersActivity extends AppCompatActivity {

    private static final String TAG = BlockedMembersActivity.class.getSimpleName();
    RecyclerView recyclerView;
    TextView txtView;

    Toolbar toolbar;
    TextView titleText;

    private static final int JOB_ID = 123;
    private JobScheduler mScheduler;

    Blockeduserlistadapter banmemberadapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContentView(R.layout.activity_blockedmembers);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);

        titleText = (TextView) toolbar.findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getString(R.string.blockmember));
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        recyclerView = findViewById(R.id.blockuser_list);
        txtView = findViewById(R.id.blocked_list_error);

        Button start = findViewById(R.id.startSync);
        Button stop = findViewById(R.id.stopSync);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName serviceName = new ComponentName(getPackageName(),
                        SyncJCometchatService.class.getName());
                JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setRequiresDeviceIdle(true)
                        .setRequiresCharging(true);

                builder.setOverrideDeadline(5 * 1000);

                JobInfo myJobInfo = builder.build();
                mScheduler.schedule(myJobInfo);

                Toast.makeText(BlockedMembersActivity.this, "start", Toast.LENGTH_SHORT).show();
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.cancel(123);
                Log.d(TAG, "Job cancelled");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // fetched all blocked friend
        fetchBlockuser();
    }

    private void fetchBlockuser() {
        // fetched all blocked user list
        BlockedUsersRequest blockedUsersRequest = new BlockedUsersRequest.BlockedUsersRequestBuilder()
                .setLimit(10)
                .setDirection(BlockedUsersRequest.DIRECTION_BLOCKED_BY_ME).build();
        blockedUsersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                Log.e(TAG, users.toString());

                if (!users.isEmpty()) {
                    banmemberadapter = new Blockeduserlistadapter(BlockedMembersActivity.this, users);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(BlockedMembersActivity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(banmemberadapter);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    txtView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, e.getMessage());
            }
        });

    }
}
