package com.modules.postcard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Mail_;
import com.sagesurfer.tasks.PerformReadTask;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 18-07-2017
 * Last Modified on 14-12-2017
 */

public class PostcardListFragment extends Fragment implements PostcardListAdapter.MessageAdapterListener {
    private static final String TAG = PostcardListFragment.class.getSimpleName();
    private ArrayList<Postcard_> mailArrayList;
    private String folder = "inbox";

    private RecyclerView recyclerView;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static PostcardListFragment newInstance(String folder) {
        PostcardListFragment postcardListFragment = new PostcardListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(General.TYPE, folder);
        postcardListFragment.setArguments(bundle);
        return postcardListFragment;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.post_card_list_layout, null);

        activity = getActivity();

        mailArrayList = new ArrayList<>();

        Bundle data = getArguments();
        if (data.containsKey(General.TYPE)) {
            folder = data.getString(General.TYPE);
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMails(activity);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        createButton.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivityInterface.setToolbarBackgroundColor();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMails(activity);
    }

    public void getMails(Activity activity) {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MAILS);
        requestMap.put(General.LAST_DATE, "0");
        requestMap.put(General.TYPE, folder);
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_POSTCARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this.activity, this.activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this.activity, this.activity);
                if (response != null) {
                    mailArrayList = Mail_.parseMail(response, folder, this.activity.getApplicationContext(), TAG);
                    if (mailArrayList.size() <= 0) {
                        Postcard_ postcard_ = new Postcard_();
                        postcard_.setStatus(status);
                        mailArrayList.add(postcard_);
                    }
                    PostcardListAdapter mAdapter = new PostcardListAdapter(this.activity.getApplicationContext(), mailArrayList, this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.activity.getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onIconClicked(int position) {
    }

    @Override
    public void onIconImportantClicked(int position) {
    }

    @Override
    public void onMessageRowClicked(int position) {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MAILS);
        requestMap.put(General.LAST_DATE, "0");
        requestMap.put(General.TYPE, folder);
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_POSTCARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    mailArrayList = Mail_.parseMail(response, folder, activity.getApplicationContext(), TAG);
                    if (mailArrayList.size() <= 0) {
                        Postcard_ postcard_ = new Postcard_();
                        postcard_.setStatus(status);
                        mailArrayList.add(postcard_);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mailArrayList.get(position).getStatus() == 1) {
            if (mailArrayList.get(position).getFolder().equalsIgnoreCase("draft")) {
                Intent nextIntent = new Intent(activity.getApplicationContext(), CreateMailActivity.class);
                nextIntent.putExtra(General.ACTION, Actions_.DRAFT);
                nextIntent.putExtra(General.POSTCARD, mailArrayList.get(position));
                startActivity(nextIntent);
            } else if (mailArrayList.get(position).getFolder().equalsIgnoreCase("outbox")) {
                Intent nextIntent = new Intent(activity.getApplicationContext(), MailDetailsActivity.class);
                nextIntent.putExtra(General.ACTION, "outbox");
                nextIntent.putExtra(General.POSTCARD, mailArrayList.get(position));
                startActivity(nextIntent);
            } else {
                Intent nextIntent = new Intent(activity.getApplicationContext(), MailDetailsActivity.class);
                nextIntent.putExtra(General.ACTION, "");
                nextIntent.putExtra(General.POSTCARD, mailArrayList.get(position));
                startActivity(nextIntent);
            }
        }

        PerformReadTask.messageReadUnRead("" + mailArrayList.get(position).getId(), mailArrayList.get(position).getType(), TAG, activity.getApplicationContext(), activity);

    }

    @Override
    public void onRowLongClicked(int position) {
    }
}
