package com.modules.selfcare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
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

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.CareUploaded_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SelfCare_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 12-09-2017
 * Last Modified on 14-12-2017
 */

public class UploadedContentListFragment extends Fragment {
    private static final String TAG = UploadedContentListFragment.class.getSimpleName();
    private String action = "";
    private ArrayList<CareUploaded_> careUploadedArrayList;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private ListView listView;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private CardView cardViewActionsSearch;
    private EditText editTextSearch;
    private ImageButton imageButtonSetting;
    private ArrayList<CareUploaded_> searchCareUploaderArrayList;

    public static UploadedContentListFragment newInstance(String action) {
        UploadedContentListFragment postcardListFragment = new UploadedContentListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(General.ACTION, action);
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

        View view = inflater.inflate(R.layout.list_view_layout, null);
        activity = getActivity();
        Preferences.initialize(activity);

        Bundle data = getArguments();
        if (data.containsKey(General.ACTION)) {
            action = data.getString(General.ACTION);
        }
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight((int) activity.getApplicationContext().getResources().getDimension(R.dimen.list_divider));
        listView.setOnItemClickListener(onItemClick);
        listView.setPadding(0, 0, 0, (int) ((activity.getApplicationContext().getResources()
                .getDimension(R.dimen.actionBarSize) + (2 * activity.getApplicationContext()
                .getResources().getDimension(R.dimen.activity_horizontal_margin)))));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //mainActivityInterface.hideRevealView();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        subSrearchFunctiaonality(view);

        return view;
    }

    private void subSrearchFunctiaonality(View view) {
        cardViewActionsSearch = (CardView) view.findViewById(R.id.cardview_actions);
        cardViewActionsSearch.setVisibility(View.VISIBLE);
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);

        imageButtonSetting = (ImageButton) view.findViewById(R.id.imagebutton_setting);
        if (!CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            imageButtonSetting.setVisibility(View.GONE);
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
    }


    public void performSearch() {
        searchCareUploaderArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (CareUploaded_ careUploaded : careUploadedArrayList) {
            if (careUploaded.getTitle() != null && careUploaded.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                searchCareUploaderArrayList.add(careUploaded);
            }
        }
        if (searchCareUploaderArrayList.size() > 0) {
            showError(false, 1);
            UploadContentListAdapter uploadContentListAdapter = new UploadContentListAdapter(activity, searchCareUploaderArrayList, action);
            listView.setAdapter(uploadContentListAdapter);
            careUploadedArrayList = searchCareUploaderArrayList;
        } else {
            showError(true, 2);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //mainActivityInterface.hideRevealView();
        mainActivityInterface.setToolbarBackgroundColor();
        Preferences.save(General.IS_EDIT, false);
        if (action.trim().length() > 0) {
            getFiles();
        }
    }

    // handle click for row item to open content details
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CareUploaded_ careUploaded_ = careUploadedArrayList.get(position);
            if (careUploaded_.getStatus() == 1) {
                Intent detailsIntent = new Intent(activity.getApplicationContext(), UploadCareDetailsActivity.class);
                detailsIntent.putExtra(Actions_.GET_DATA, careUploaded_);
                detailsIntent.putExtra(General.ACTION, action);
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
            }
        }
    };

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

    // make network call to fetch self care files
    private void getFiles() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                Log.d("Uploader", response);

                if (response != null) {
                    careUploadedArrayList = SelfCare_.parseContent(response, action, TAG, activity.getApplicationContext());
                    if (careUploadedArrayList != null && careUploadedArrayList.size() > 0) {
                        if (careUploadedArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            UploadContentListAdapter uploadContentListAdapter = new UploadContentListAdapter(activity, careUploadedArrayList, action);
                            listView.setAdapter(uploadContentListAdapter);
                        } else {
                            showError(true, careUploadedArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 2);
                    }
                } else {
                    showError(true, 11);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }
}
