package com.modules.team;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.parser.Invitations_;

import java.util.ArrayList;

import butterknife.BindView;

public class TeamPeerStaffListActivity extends AppCompatActivity implements TeamPeerStaffAdapter.TeamPeerStaffListener {

    private static final String TAG = TeamPeerStaffListActivity.class.getSimpleName();
    @BindView(R.id.edittext_search)
    EditText editTextSearch;
    @BindView(R.id.imagebutton_filter)
    AppCompatImageButton imageButtonFilter;
    @BindView(R.id.imagebutton_setting)
    AppCompatImageButton imageButtonSetting;
    RecyclerView recyclerView;

    private Invitations_ selectedSupervisor;
    private TeamPeerStaffAdapter adapter;
    private ArrayList<Invitations_> invitationsArrayList = new ArrayList<>();

    private static final String KEY_ID = "key_id";
    private static final String KEY_VALUE = "key_value";
    private int PEER_STAFF_REQUEST_CODE = 12;
    private TextView mTxtDone;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_team_peer_staff_list);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);

        mTxtDone = toolbar.findViewById(R.id.txt_done);
        mTxtDone.setVisibility(View.VISIBLE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTxtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.putExtra("invitations_arrayList", invitationsArrayList);
                setResult(PEER_STAFF_REQUEST_CODE, intent);
                finish();
            }
        });

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(50, 0, 0, 0);
        titleText.setText("Select Peer Staff");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle extra = getIntent().getExtras();

        invitationsArrayList = (ArrayList<Invitations_>) getIntent().getSerializableExtra("invitationsPeerStaffArrayList");


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeamPeerStaffAdapter(this, invitationsArrayList, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


    }


    @Override
    public void onTeamPeerStaffLayoutClicked(final ArrayList<Invitations_> invitations_arrayList) {
        this.invitationsArrayList = invitations_arrayList;
    }
}
