package com.modules.caseload;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.sagesurfer.adapters.CaseloadProgressNoteAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.CaseloadProgressNote_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

/**
 * Created by Monika on 7/10/2018.
 */

public class CaseloadProgressNoteActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CaseloadProgressNoteActivity.class.getSimpleName();
    @BindView(R.id.caseload_progress_note_title)
    TextView textViewToolbarTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.imageview_add)
    AppCompatImageView imageViewAdd;
    @BindView(R.id.linearlayout_peer_note)
    LinearLayout linearLayoutPeerNote;
    @BindView(R.id.tab_layout_peer_note)
    TabLayout tabLayoutPeerNote;
    @BindView(R.id.framelayout_peer_note)
    FrameLayout frameLayoutPeerNote;
    @BindView(R.id.linearlayout_error)
    LinearLayout linearLayoutError;
    @BindView(R.id.imageview_error)
    AppCompatImageView imageViewError;
    @BindView(R.id.textview_error_message)
    TextView textViewErrorMessage;

    private CaseloadProgressNoteAdapter caseloadProgressNoteAdapter;
    public ArrayList<CaseloadProgressNote_> caseloadProgressNoteArrayList = new ArrayList<>();
    private Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));
        setContentView(R.layout.activity_caseload_progress_note);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.caseload_progress_note_toolbar);
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

        imageViewAdd.setOnClickListener(this);

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage013))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage048))) {
            textViewToolbarTitle.setText(getResources().getString(R.string.note));
            recyclerView.setVisibility(View.GONE);
            linearLayoutPeerNote.setVisibility(View.VISIBLE);
            tabLayoutPeerNote.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
            tabLayoutPeerNote.setOnTabSelectedListener(tabSelectedListener);
            tabLayoutPeerNote.addTab(tabLayoutPeerNote.newTab().setText(getResources().getString(R.string.pending)));
            tabLayoutPeerNote.addTab(tabLayoutPeerNote.newTab().setText(getResources().getString(R.string.approved)));
            tabLayoutPeerNote.addTab(tabLayoutPeerNote.newTab().setText(getResources().getString(R.string.rejected)));
            if (CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                imageViewAdd.setVisibility(View.VISIBLE);
                tabLayoutPeerNote.addTab(tabLayoutPeerNote.newTab().setText(getResources().getString(R.string.draft)));
            } else {
                imageViewAdd.setVisibility(View.GONE);
            }
        } else {
            textViewToolbarTitle.setText(getResources().getString(R.string.progress_note));
            recyclerView.setVisibility(View.VISIBLE);
            linearLayoutPeerNote.setVisibility(View.GONE);
            caseloadProgressNoteAdapter = new CaseloadProgressNoteAdapter(this, getApplicationContext(), caseloadProgressNoteArrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(caseloadProgressNoteAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage013))) {
        } else {
            getCaseloadProgressNoteListData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_add:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage013))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage048))) {
                    Intent detailsIntent = new Intent(getApplicationContext(), PeerAddNoteActivity.class);
                    startActivity(detailsIntent);
                    overridePendingTransition(0, 0);
                } else {
                    Intent detailsIntent = new Intent(getApplicationContext(), ProgressAddNoteActivity.class);
                    startActivity(detailsIntent);
                    overridePendingTransition(0, 0);
                }
                break;
        }
    }

    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            assert tab.getText() != null;
            String fragment_name = tab.getText().toString();
            setTabFragment(fragment_name);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void setTabFragment(String fragment_name) {
        Bundle bundle = new Bundle();
        Fragment fragment = new PeerNotePendingFragment();
        if (fragment_name.equalsIgnoreCase(getResources().getString(R.string.pending))) {
            bundle.putSerializable(General.ACTION, Actions_.GET_PENDING);
            fragment.setArguments(bundle);
        } else if (fragment_name.equalsIgnoreCase(getResources().getString(R.string.approved))) {
            bundle.putSerializable(General.ACTION, Actions_.GET_APPROVED);
            fragment.setArguments(bundle);
        } else if (fragment_name.equalsIgnoreCase(getResources().getString(R.string.rejected))) {
            bundle.putSerializable(General.ACTION, Actions_.GET_REJECTED);
            fragment.setArguments(bundle);
        } else {
            bundle.putSerializable(General.ACTION, Actions_.GET_DRAFT);
            fragment.setArguments(bundle);
        }
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PENDING);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_PENDING);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.replace(R.id.framelayout_peer_note, fragment, Actions_.GET_PENDING);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }

    //make network call to fetch all progress notes for ndesign/design
    private void getCaseloadProgressNoteListData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_PROGRESS_NOTES_LIST);
        requestMap.put(General.CONSUMER_ID, Preferences.get(General.CONSUMER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    caseloadProgressNoteArrayList = CaseloadParser_.parseProgressNote(response, Actions_.GET_PROGRESS_NOTES_LIST, getApplicationContext(), TAG);
                    if (caseloadProgressNoteArrayList.size() > 0) {
                        if (caseloadProgressNoteArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            caseloadProgressNoteAdapter = new CaseloadProgressNoteAdapter(this, getApplicationContext(), caseloadProgressNoteArrayList);
                            recyclerView.setAdapter(caseloadProgressNoteAdapter);
                        } else {
                            showError(true, caseloadProgressNoteArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            linearLayoutError.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            textViewErrorMessage.setText(GetErrorResources.getMessage(status, getApplicationContext()));
            imageViewError.setImageResource(GetErrorResources.getIcon(status));
        } else {
            linearLayoutError.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
