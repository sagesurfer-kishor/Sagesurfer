package com.modules.sos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.sagesurfer.animation.ActivityTransition;
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
 *         Created on 17-07-2017
 *         Last Modified on 14-12-2017
 */
public class MySosFragment extends Fragment implements View.OnClickListener/*, MySosListAdapter.MySosListAdapterListener*/ {

    private static final String TAG = MySosFragment.class.getSimpleName();

    private ArrayList<MySos_> sosArrayList;
    private ArrayList<MySos_> searchSosArrayList;

    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private FrameLayout frameLayoutFab;
    static FloatingActionButton createButton;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private EditText editTextSearch;
    private CardView cardViewActionsSearch;
    private ImageButton imageButtonSetting;
    private MySosListAdapter mySosListAdapter;

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
        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            imageButtonSetting.setVisibility(View.VISIBLE);
            imageButtonSetting.setOnClickListener(this);
        } else {
            imageButtonSetting.setVisibility(View.GONE);
        }

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);
        //listView.setOnItemClickListener(onItemClick);

        frameLayoutFab = (FrameLayout) view.findViewById(R.id.framelayout_fab);
        createButton = (FloatingActionButton) view.findViewById(R.id.sos_update_float);
        createButton.setOnClickListener(this);

        if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isCoordinator(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isNaturalSupportId(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isParentId(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {

            frameLayoutFab.setVisibility(View.VISIBLE);
            //getHeight(createButton);
        } else {
            frameLayoutFab.setVisibility(View.GONE);
            //getHeight(SosUpdatesFragment.createButton);
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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //mainActivityInterface.hideRevealView();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.sos_updates));
        mainActivityInterface.setToolbarBackgroundColor();
        fetchSos();
    }

    //make network call fetch sos from server
    private void fetchSos() {
        int status = 11;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "my_sos");
        requestMap.put(General.SEARCH_TEXT, "");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.FETCH_SOS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    sosArrayList = Sos_.parseMySos(response, activity.getApplicationContext(), TAG);
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
                    mySosListAdapter = new MySosListAdapter(activity, sosArrayList);
                    listView.setAdapter(mySosListAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void performSearch() {
        searchSosArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if(searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for(MySos_ mySosItem : sosArrayList){
            if(mySosItem.getMessage() != null && mySosItem.getMessage().toLowerCase().contains(searchText.toLowerCase())) {
                searchSosArrayList.add(mySosItem);
            }
        }
        if (searchSosArrayList.size() > 0) {
            showError(false, 1);
            mySosListAdapter = new MySosListAdapter(activity, searchSosArrayList);
            listView.setAdapter(mySosListAdapter);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sos_update_float:
                Intent sosIntent = new Intent(activity.getApplicationContext(), CreateSosActivity.class);
                startActivity(sosIntent);
                activity.overridePendingTransition(0, 0);
                break;

            case R.id.imagebutton_setting:
                Intent intent = new Intent(activity.getApplicationContext(), SosSettingActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }
}
