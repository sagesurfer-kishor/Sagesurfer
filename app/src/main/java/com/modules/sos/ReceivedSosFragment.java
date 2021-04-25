package com.modules.sos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Sos_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 17-07-2017
 * Last Modified on 14-12-2017
 */

public class ReceivedSosFragment extends Fragment {

    private static final String TAG = ReceivedSosFragment.class.getSimpleName();
    private ArrayList<ReceivedSos_> sosArrayList;
    private ArrayList<ReceivedSos_> searchSosArrayList;

    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private EditText editTextSearch;
    private CardView cardViewActionsSearch;
    private ReceivedSosListAdapter receivedSosListAdapter;
    private ImageButton imageButtonSetting;
    private FrameLayout frameLayoutFab;
    static FloatingActionButton createButton;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        sosArrayList = new ArrayList<>();

        cardViewActionsSearch = (CardView) view.findViewById(R.id.cardview_actions);
        cardViewActionsSearch.setVisibility(View.VISIBLE);
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);

        imageButtonSetting = (ImageButton) view.findViewById(R.id.imagebutton_setting);
        if (!CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            imageButtonSetting.setVisibility(View.GONE);
        }

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);

        frameLayoutFab = (FrameLayout) view.findViewById(R.id.framelayout_fab);
        createButton = (FloatingActionButton) view.findViewById(R.id.sos_update_float);
        frameLayoutFab.setVisibility(View.VISIBLE);

        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            frameLayoutFab.setVisibility(View.VISIBLE);
        } else {
            frameLayoutFab.setVisibility(View.GONE);
        }

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sosIntent = new Intent(activity.getApplicationContext(), CreateSosActivity.class);
                startActivity(sosIntent);
                activity.overridePendingTransition(0, 0);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.sos_updates));
                }
            });
        }

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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchSos();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchSos();
    }

    @Override
    public void onResume() {
        super.onResume();
        //mainActivityInterface.hideRevealView();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources()
                .getString(R.string.sos_updates));
        mainActivityInterface.setToolbarBackgroundColor();

        //int status = PerformReadTask.readAlert("" + goal_.getId(), General.SEL, TAG, getApplicationContext(), this);
    }

    //  make network call to fetch received sos
    private void fetchSos() {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SOS);
        requestMap.put(General.SEARCH_TEXT, "");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.FETCH_SOS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                sosArrayList = Sos_.parseReceivedSos(response, activity.getApplicationContext(), TAG);
                if (sosArrayList.size() <= 0 || sosArrayList.get(0).getStatus() != 1) {
                    if (sosArrayList.size() <= 0) {
                        status = 12;
                    } else {
                        status = sosArrayList.get(0).getStatus();
                    }
                    showError(true, status);
                    return;
                }
                showError(false, status);
                receivedSosListAdapter = new ReceivedSosListAdapter(activity, sosArrayList);
                listView.setAdapter(receivedSosListAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showError(true, status);
        }
    }

    public void performSearch() {
        searchSosArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (ReceivedSos_ receivedSosItem : sosArrayList) {
            if (receivedSosItem.getMessage() != null && receivedSosItem.getMessage().toLowerCase().contains(searchText.toLowerCase())) {
                searchSosArrayList.add(receivedSosItem);
            }
        }
        if (searchSosArrayList.size() > 0) {
            showError(false, 1);
            receivedSosListAdapter = new ReceivedSosListAdapter(activity, searchSosArrayList);
            listView.setAdapter(receivedSosListAdapter);
        } else {
            showError(true, 2);
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}
