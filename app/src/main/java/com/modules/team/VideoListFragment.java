package com.modules.team;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Video_;
import com.sagesurfer.views.VideoViewer;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.TeamDetails_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 08-07-2017
 *         Last Modified on 14-12-2017
 */

public class VideoListFragment extends Fragment implements VideoGalleryAdapter.VideoAdapterListener {

    private static final String TAG = VideoListFragment.class.getSimpleName();
    private ArrayList<Video_> videoArrayList;

    private Activity activity;

    private RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.swipe_refresh_recycle_view_layout, null);

        activity = getActivity();
        videoArrayList = new ArrayList<>();

        Preferences.initialize(activity.getApplicationContext());

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));

        errorText = (TextView) view.findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.swipe_refresh_recycler_view__error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getVideos();
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // make network call to fetch video list from server
    private void getVideos() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_VIDEOS);
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    videoArrayList = TeamDetails_.parseVideo(response, activity.getApplicationContext(), TAG);
                    if (videoArrayList.size() > 0) {
                        if (videoArrayList.get(0).getStatus() == 1) {

                            Collections.reverse(videoArrayList);

                            VideoGalleryAdapter adapter = new VideoGalleryAdapter(
                                    activity.getApplicationContext(), videoArrayList, this);

                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(activity, 2);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.addItemDecoration(new GridSpacingItemDecoration(
                                    2, dpToPx(10), true));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);

                            showError(false, 1);
                        } else {
                            showError(true, videoArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 12);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClicked(int position) {
        openVideo(position);
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    // open single video in full screen video viewer dialog
    @SuppressLint("CommitTransaction")
    private void openVideo(int position) {
        DialogFragment dialogFrag = new VideoViewer();
        Bundle bundle = new Bundle();
        bundle.putString(General.PATH, videoArrayList.get(position).getVideoPath());
        bundle.putString(General.IMAGE, videoArrayList.get(position).getTitle());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), General.PATH);
    }
}

