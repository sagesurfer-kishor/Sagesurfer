package com.modules.selfcare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.models.Content_;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SelfCare_;
import com.sagesurfer.selectors.MultiUserSelectorDialog;
import com.sagesurfer.tasks.PerformGetUsersTask;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/11/2018
 *         Last Modified on 4/11/2018
 */

public class SelfCareContentListFragment extends Fragment implements SelfCareContentListAdapter.SelfCareContentListAdapterListener{
    private static final String TAG = SelfCareContentListFragment.class.getSimpleName();

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.swipe_menu_listview)
    SwipeMenuListView swipeMenuListView;

    @BindView(R.id.linealayout_error)
    LinearLayout lineaLayoutError;

    @BindView(R.id.imageview_error_icon)
    AppCompatImageView imageViewErrorIcon;

    @BindView(R.id.textview_error_message)
    TextView textViewErrorMessage;

    private ArrayList<Content_> contentArrayList;

    private String personal = "0", like = "0", comment = "0", category = "0", search = "", language = "0", age = "0", type = "0", location = "0", state = "0", city = "0";

    private Activity activity;
    private Unbinder unbinder;
    private ArrayList<Friends_> friendsArrayList = new ArrayList<>();

    public static SelfCareContentListFragment newInstance(int personal, int like, int comment, String category, String age, String language, String type, String location, String state, String city) {
        SelfCareContentListFragment contentListFragment = new SelfCareContentListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(General.PERSONAL, "" + personal);
        bundle.putString(General.LIKE, "" + like);
        bundle.putString(General.COMMENT, "" + comment);
        bundle.putString(General.CATEGORY, "" + category);
        bundle.putString(General.AGE, "" + age);
        bundle.putString(General.LANGUAGE, "" + language);
        bundle.putString(General.TYPE, "" + type);
        bundle.putString(General.LOCATION, "" + location);
        bundle.putString(General.STATE, "" + state);
        bundle.putString(General.CITY, "" + city);
        contentListFragment.setArguments(bundle);
        return contentListFragment;
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
        unbinder = ButterKnife.bind(this, view);

        activity = getActivity();
        contentArrayList = new ArrayList<>();

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        swipeMenuListView.setDividerHeight(0);
        swipeMenuListView.setOnItemClickListener(onItemClick);

        Bundle data = getArguments();
        if (data != null) {
            personal = data.getString(General.PERSONAL);
            like = data.getString(General.LIKE);
            comment = data.getString(General.COMMENT);
            category = data.getString(General.CATEGORY);
            age = data.getString(General.AGE);
            language = data.getString(General.LANGUAGE);
            type = data.getString(General.TYPE);
            location = data.getString(General.LOCATION);
            state = data.getString(General.STATE);
            city = data.getString(General.CITY);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetch();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //handle on click for list row to open respective details page
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Content_ content_ = contentArrayList.get(position);
            if (content_.getStatus() == 1) {
                Intent detailsIntent = new Intent(activity.getApplicationContext(), SelfCareDetailsActivity.class);
                detailsIntent.putExtra(Actions_.GET_DATA, content_);
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
            }
        }
    };

    private void showError(boolean isError, int status) {
        if (isError) {
            lineaLayoutError.setVisibility(View.VISIBLE);
            swipeMenuListView.setVisibility(View.GONE);
            textViewErrorMessage.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            imageViewErrorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            lineaLayoutError.setVisibility(View.GONE);
            swipeMenuListView.setVisibility(View.VISIBLE);
        }
    }

    // make call to fetch content list from server
    private void fetch() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.GET_DATA);
        requestMap.put(General.SEARCH, "");
        requestMap.put(General.CATEGORY, category);
        requestMap.put(General.LANGUAGE, language);
        requestMap.put(General.LIKE, like);
        requestMap.put(General.COMMENT, comment);
        requestMap.put(General.TYPE, "" + type);
        requestMap.put(General.COUNTRY, "" + location);
        requestMap.put(General.STATE, "" + state);
        requestMap.put(General.CITY, "" + city);
        requestMap.put(General.PERSONAL, personal);
        requestMap.put(General.AGE, age);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    contentArrayList = SelfCare_.parseEvents(response, activity.getApplicationContext(), TAG);
                    if (contentArrayList.size() > 0) {
                        if (contentArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            SelfCareContentListAdapter careContentListAdapter =
                                    new SelfCareContentListAdapter(activity, contentArrayList, this);
                            swipeMenuListView.setAdapter(careContentListAdapter);
                            return;
                        } else {
                            showError(true, contentArrayList.get(0).getStatus());
                            return;
                        }
                    } else {
                        showError(true, 12);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }

    @Override
    public void onItemClicked(int position, String itemView) {
        Content_ content_ = contentArrayList.get(position);
        if (itemView.equalsIgnoreCase("mainContainer")) {
            friendsArrayList = PerformGetUsersTask.get(Actions_.MY_FRIENDS, contentArrayList.get(position).getShared_to_ids(), activity.getApplicationContext(), TAG, activity);
            Bundle bundle = new Bundle();
            android.app.DialogFragment dialogFrag = new MultiUserSelectorDialog();
            bundle.putSerializable(Actions_.MY_FRIENDS, friendsArrayList);
            bundle.putString(General.ID, "" + contentArrayList.get(position).getId());
            dialogFrag.setArguments(bundle);
            dialogFrag.show(activity.getFragmentManager().beginTransaction(), Actions_.MY_FRIENDS);
        } else if (itemView.equalsIgnoreCase("listItem")) {
            Intent detailsIntent = new Intent(activity.getApplicationContext(), SelfCareDetailsActivity.class);
            detailsIntent.putExtra(Actions_.GET_DATA, content_);
            startActivity(detailsIntent);
            activity.overridePendingTransition(0, 0);
        }
    }
}