package com.modules.team;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.modules.wall.CommentDialog;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.TeamDetails_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 18-07-2017
 * Last Modified on 14-12-2017
 */

public class PollListFragment extends Fragment implements View.OnClickListener, CommentDialog.callGetPollMethod {

    private static final String TAG = PollListFragment.class.getSimpleName();
    private ArrayList<Poll_> pollArrayList;

    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private static Activity activity;
    private MainActivityInterface mainActivityInterface;

    
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        //noinspection deprecation
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.changeDrawerIcon(true);
        }
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

        View view = inflater.inflate(R.layout.swipe_refresh_recycle_view_layout, null);

        activity = getActivity();
        pollArrayList = new ArrayList<>();

        Preferences.initialize(activity.getApplicationContext());

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_refresh_layout_recycler_view);

        errorText = (TextView) view.findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.swipe_refresh_recycler_view__error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        fab = (FloatingActionButton) view.findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        fab.setOnClickListener(this);
        fab.setImageResource(R.drawable.ic_add_white);
        /*if(Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))){
            if (SosValidation.checkId(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                    || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        } else {
            if (SosValidation.checkId(Integer.parseInt(Preferences.get(General.ROLE_ID))) || SosValidation.checkParentId(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        }*/

        //Changed after discussion with Sagar and Nirmal as Sagar have implemented owner_id and isModerator on backend for all instances
        if ((Preferences.get(General.OWNER_ID) != null && Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID)))
                || (Preferences.get(General.OWNER_ID) != null && Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1"))
                || (Preferences.get(General.OWNER_ID) != null && Preferences.get(General.IS_CC).equalsIgnoreCase("1"))) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));
        getPoll();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: called");
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.poll));
        mainActivityInterface.setToolbarBackgroundColor();
        fab.setBackgroundTintList(ColorStateList.valueOf(GetColor.getHomeIconBackgroundColorColorParse(true)));

        try{
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipe_refresh_layout_recycler_view_float:
                Intent createIntent = new Intent(activity.getApplicationContext(), CreatePollActivity.class);
                startActivity(createIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
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

    // make network call to fetch poll posted in respective team
    public void getPoll() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_POLL);
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    pollArrayList = TeamDetails_.parsePoll(response, activity.getApplicationContext(), TAG);
                    if (pollArrayList.size() > 0) {
                        if (pollArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            PollListAdapter pollListAdapter = new PollListAdapter(activity, pollArrayList);

                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(activity, 1);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            float offsetPx = getResources().getDimension(R.dimen.profile_size)
                                    + (2 * getResources().getDimension(R.dimen.activity_vertical_margin));
                            BottomOffsetDecoration bottomOffsetDecoration = new BottomOffsetDecoration((int) offsetPx);
                            recyclerView.addItemDecoration(bottomOffsetDecoration);
                            recyclerView.setAdapter(pollListAdapter);

                        } else {
                            showError(true, pollArrayList.get(0).getStatus());
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
    public void getPollFromCommentBackPressed() {
        getPoll();
        Log.i(TAG, "getPollFromCommentBackPressed: ");
    }

    private class BottomOffsetDecoration extends RecyclerView.ItemDecoration {
        private final int mBottomOffset;

        BottomOffsetDecoration(int bottomOffset) {
            mBottomOffset = bottomOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int dataSize = state.getItemCount();
            int position = parent.getChildAdapterPosition(view);
            if (dataSize > 0 && position == dataSize - 1) {
                outRect.set(0, 0, 0, mBottomOffset);
            } else {
                outRect.set(0, 0, 0, 0);
            }

        }
    }
}
