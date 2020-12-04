package com.modules.blog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.TeamDetails_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 21-09-2017
 * Last Modified on 13-12-2017
 */


public class BlogListFragment extends Fragment implements BlogListAdapter.OnItemClickListener {

    private static final String TAG = BlogListFragment.class.getSimpleName();
    private ArrayList<Blog_> blogArrayList;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private BlogListAdapter blogListAdapter;

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

        View view = inflater.inflate(R.layout.list_view_layout, null);

        activity = getActivity();
        blogArrayList = new ArrayList<>();

        ListView listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView.setDividerHeight(0);

        blogListAdapter = new BlogListAdapter(activity, blogArrayList, this);
        listView.setAdapter(blogListAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //mainActivityInterface.hideRevealView();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.blogs));
        mainActivityInterface.setToolbarBackgroundColor();
        fetchBlog();
    }

    // Interface used from blog list adapter
    @Override
    public void onItemClickListener(Blog_ blog_) {
        if (blog_.getStatus() == 1) {
            Intent detailsIntent = new Intent(activity.getApplicationContext(), BlogDetailsActivity.class);
            detailsIntent.putExtra(Actions_.GET_BLOG_NEW, blog_);
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        }
    }

    // Make network call to fetch all blog records from server
    private void fetchBlog() {
        blogArrayList.clear();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_BLOG_NEW);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    blogArrayList.addAll(TeamDetails_.parseBlog(response, activity.getApplicationContext(), TAG));
                    blogListAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
