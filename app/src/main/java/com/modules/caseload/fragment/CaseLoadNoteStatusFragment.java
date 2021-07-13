package com.modules.caseload.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.modules.caseload.PeerAddNoteActivity;
import com.modules.caseload.ProgressAddNoteActivity;
import com.modules.caseload.fragment.PeerNoteAllFragment;
import com.modules.caseload.observer.ObserverID;
import com.sagesurfer.adapters.CaseloadProgressNoteAdapter;
import com.sagesurfer.collaborativecares.Application;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.CaseloadProgressNote_;
import com.storage.preferences.Preferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Kailash Karankal on 8/24/2019.
 */
public class CaseLoadNoteStatusFragment extends Fragment implements View.OnClickListener, Observer {
    private MainActivityInterface mainActivityInterface;
    private FragmentActivity mContext;
    private Activity activity;
    private CaseloadProgressNoteAdapter caseloadProgressNoteAdapter;
    public ArrayList<CaseloadProgressNote_> caseloadProgressNoteArrayList = new ArrayList<>();
    private Toolbar toolbar;
    private TextView textViewToolbarTitle;
    private RecyclerView recyclerView;
    private AppCompatImageView imageViewAdd;
    private LinearLayout linearLayoutPeerNote;
    private TabLayout tabLayoutPeerNote;
    private FrameLayout frameLayoutPeerNote;
    private Application application;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mContext = (FragmentActivity) activity;
            mainActivityInterface = (MainActivityInterface) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " " + e.toString());
        }

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showHideBellIcon(true);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        application = (Application) activity.getApplication();
        application.getObserver().addObserver(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu,
                                    @NonNull @NotNull MenuInflater inflater) {
        menu.clear();

        // Add the new menu items
        inflater.inflate(R.menu.menuadd, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {

        if (item.getItemId()==R.id.filemenuadd)
        {
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage013))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage048))) {
                Intent detailsIntent = new Intent(activity.getApplicationContext(), PeerAddNoteActivity.class);
                startActivity(detailsIntent);
            } else {
                Intent detailsIntent = new Intent(activity.getApplicationContext(), ProgressAddNoteActivity.class);
                startActivity(detailsIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.fragment_caseload_peer_note_status, container, false);

        activity = getActivity();

        toolbar = (Toolbar) view.findViewById(R.id.caseload_progress_note_toolbar);
        imageViewAdd = view.findViewById(R.id.imageview_add);
        textViewToolbarTitle = view.findViewById(R.id.caseload_progress_note_title);
        recyclerView = view.findViewById(R.id.recycler_view);
        linearLayoutPeerNote = view.findViewById(R.id.linearlayout_peer_note);
        tabLayoutPeerNote = view.findViewById(R.id.tab_layout_peer_note);
        frameLayoutPeerNote = view.findViewById(R.id.framelayout_peer_note);
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


            tabLayoutPeerNote.addTab(tabLayoutPeerNote.newTab().setText(getResources().getString(R.string.all)));
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
            caseloadProgressNoteAdapter = new CaseloadProgressNoteAdapter(activity, activity.getApplicationContext(), caseloadProgressNoteArrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(caseloadProgressNoteAdapter);
        }

        return view;
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
                    Intent detailsIntent = new Intent(activity.getApplicationContext(), PeerAddNoteActivity.class);
                    startActivity(detailsIntent);
                } else {
                    Intent detailsIntent = new Intent(activity.getApplicationContext(), ProgressAddNoteActivity.class);
                    startActivity(detailsIntent);
                }
                break;
        }
    }

    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            assert tab.getText() != null;
            //  setTabName(tab.getPosition());
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

    private void setTabName(int selectedTab) {
        String tab_count = Preferences.get("tab_count");

        tabLayoutPeerNote.getTabAt(0).setText("All");
        tabLayoutPeerNote.getTabAt(1).setText("Pending");
        tabLayoutPeerNote.getTabAt(2).setText("Approved");
        tabLayoutPeerNote.getTabAt(3).setText("Rejected");

        if (tabLayoutPeerNote.getSelectedTabPosition() == 0) {
            if (tab_count.equals("")) {
                tabLayoutPeerNote.getTabAt(selectedTab).setText("All");
            } else {
                tabLayoutPeerNote.getTabAt(selectedTab).setText("All " + "(" + GetCounters.convertCounterOne(Integer.parseInt(tab_count)) + ")");
            }
        } else if (tabLayoutPeerNote.getSelectedTabPosition() == 1) {
            if (tab_count.equals("")) {
                tabLayoutPeerNote.getTabAt(selectedTab).setText("Pending");
            } else {
                tabLayoutPeerNote.getTabAt(selectedTab).setText("Pending " + "(" + GetCounters.convertCounterOne(Integer.parseInt(tab_count)) + ")");
            }
        } else if (tabLayoutPeerNote.getSelectedTabPosition() == 2) {
            if (tab_count.equals("")) {
                tabLayoutPeerNote.getTabAt(selectedTab).setText("Approved");
            } else {
                tabLayoutPeerNote.getTabAt(selectedTab).setText("Approved " + "(" + GetCounters.convertCounterOne(Integer.parseInt(tab_count)) + ")");
            }
        } else if (tabLayoutPeerNote.getSelectedTabPosition() == 3) {
            if (tab_count.equals("")) {
                tabLayoutPeerNote.getTabAt(selectedTab).setText("Rejected");
            } else {
                tabLayoutPeerNote.getTabAt(selectedTab).setText("Rejected " + "(" + GetCounters.convertCounterOne(Integer.parseInt(tab_count)) + ")");
            }
        }
    }

    private void setTabFragment(String fragment_name) {
        Bundle bundle = new Bundle();

        Fragment fragment = new PeerNoteAllFragment();

        if (fragment_name.substring(0, 3).equalsIgnoreCase(getResources().getString(R.string.all))) {
            bundle.putSerializable(General.ACTION, Actions_.GET_All);
            fragment.setArguments(bundle);
        } else if (fragment_name.substring(0, 7).equalsIgnoreCase(getResources().getString(R.string.pending))) {
            bundle.putSerializable(General.ACTION, Actions_.PENDING_ALL);
            fragment.setArguments(bundle);
        } else if (fragment_name.substring(0, 8).equalsIgnoreCase(getResources().getString(R.string.approved))) {
            bundle.putSerializable(General.ACTION, Actions_.APPROVED_All);
            fragment.setArguments(bundle);
        } else if (fragment_name.substring(0, 8).equalsIgnoreCase(getResources().getString(R.string.rejected))) {
            bundle.putSerializable(General.ACTION, Actions_.REJECTED_All);
            fragment.setArguments(bundle);
        } else {
            bundle.putSerializable(General.ACTION, Actions_.GET_DRAFT);
            fragment.setArguments(bundle);
        }
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_All);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        oldFragment = fragmentManager.findFragmentByTag(Actions_.GET_All);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.replace(R.id.framelayout_peer_note, fragment, Actions_.GET_All);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle("Note Status");
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void update(Observable observable, Object o) {
        if (application.getObserver().getValue() == ObserverID.tabCounter) {
            setTabName(tabLayoutPeerNote.getSelectedTabPosition());
        }
    }
}
