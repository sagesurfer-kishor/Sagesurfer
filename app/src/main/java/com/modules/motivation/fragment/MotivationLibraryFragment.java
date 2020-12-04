package com.modules.motivation.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.modules.motivation.activity.MotivationDetailsActivity;
import com.modules.motivation.adapter.MotivationLibraryListAdapter;
import com.modules.motivation.model.MotivationLibrary_;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.MotivationParser_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 2/12/2019.
 */

public class MotivationLibraryFragment extends Fragment {
    private static final String TAG = MotivationListFragment.class.getSimpleName();

    private ListView listViewRecent, listViewAll;
    private LinearLayout errorLayout;
    private TextView textView, textView1, errorText;
    private AppCompatImageView errorIcon;
    private EditText editTextSearch;
    private ArrayList<MotivationLibrary_> motivationLibraryArrayList = new ArrayList<>();
    private ArrayList<MotivationLibrary_> motivationLibrarySearchArrayList = new ArrayList<>();
    private MotivationLibraryListAdapter motivationLibraryListAdapter;
    private MainActivityInterface mainActivityInterface;

    private Activity activity;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.hidePlusIcon(true);
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

        View view = inflater.inflate(R.layout.fragment_motivation_library, null);
        activity = getActivity();

        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
        /*SwipeRefreshLayout swipeRefreshLayoutRecent = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_recent);
        swipeRefreshLayoutRecent.setEnabled(false);
        swipeRefreshLayoutRecent.setVisibility(View.GONE);*/
        SwipeRefreshLayout swipeRefreshLayoutAll = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_all);
        swipeRefreshLayoutAll.setRefreshing(false);
        swipeRefreshLayoutAll.setEnabled(false);

        /*textView = (TextView) view.findViewById(R.id.textview);
        textView1 = (TextView) view.findViewById(R.id.textview1);*/
        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        /*listViewRecent = (ListView) view.findViewById(R.id.swipe_menu_listview_recent);
        listViewRecent.setDividerHeight(0);
        listViewRecent.setNestedScrollingEnabled(false);*/
        listViewAll = (ListView) view.findViewById(R.id.swipe_menu_listview_all);
        listViewAll.setDividerHeight(0);
        listViewAll.setOnItemClickListener(onItemClick);
        //listViewAll.setNestedScrollingEnabled(false);

        /*VectorDrawableCompat searchDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_search_gray, editTextSearch.getContext().getTheme());
        editTextSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(searchDrawable, null, null, null);*/

        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                performSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        editTextSearch.setText("");
        mainActivityInterface.setMainTitle("MOTIVATION");
        mainActivityInterface.setToolbarBackgroundColor();
        fetchMotivationLibraryData();
    }

    // handle click for row item to open content details
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MotivationLibrary_ motivationLibrary_ = motivationLibraryArrayList.get(position);
            if (motivationLibrarySearchArrayList.size() > 0) {
                motivationLibrary_ = motivationLibrarySearchArrayList.get(position);
            }
            if (motivationLibrary_.getStatus() == 1) {
                Intent detailsIntent = new Intent(activity.getApplicationContext(), MotivationDetailsActivity.class);
                detailsIntent.putExtra(Actions_.GET_DATA, motivationLibrary_);
                startActivity(detailsIntent, ActivityTransition.moveToNextAnimation(activity.getApplicationContext()));
            }
        }
    };

    //  make network call to fetch journal data
    private void fetchMotivationLibraryData() {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_DATA);
        requestMap.put(General.IS_MOOD, "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_UPLIFT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {//		{"get_data":[{"error":"No Data","status":2}]}
                    motivationLibraryArrayList = MotivationParser_.parseMotivation(response, Actions_.GET_DATA, activity.getApplicationContext(), TAG);
                    if (motivationLibraryArrayList.size() > 0) {
                        if (motivationLibraryArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            MotivationLibraryListAdapter motivationLibraryListAdapter = new MotivationLibraryListAdapter(activity, motivationLibraryArrayList);
                            listViewAll.setAdapter(motivationLibraryListAdapter);
                        } else {
                            showError(true, motivationLibraryArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                } else {
                    showError(true, 11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true, status);
        }
    }

    public void performSearch() {
        motivationLibrarySearchArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (MotivationLibrary_ motivationLibararyItem : motivationLibraryArrayList) {
            if ((motivationLibararyItem.getTitle() != null && motivationLibararyItem.getTitle().toLowerCase().contains(searchText.toLowerCase()))) {
                motivationLibrarySearchArrayList.add(motivationLibararyItem);
            }
        }
        if (motivationLibrarySearchArrayList.size() > 0) {
            showError(false, 1);
            motivationLibraryListAdapter = new MotivationLibraryListAdapter(activity, motivationLibrarySearchArrayList);
            listViewAll.setAdapter(motivationLibraryListAdapter);
        } else {
            showError(true, 2);
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            listViewAll.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            listViewAll.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }

        if (status == 2) {
            if (isError) {
                listViewAll.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorIcon.setVisibility(View.GONE);

                errorText.setText("Use motivation library to assist you when in need.\n" +
                        "Click on a plus icon on the top right corner to add new entries.");
                // errorIcon.setImageResource(GetErrorResources.getIcon(status));
            } else {
                listViewAll.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
            }
        }
    }
}
