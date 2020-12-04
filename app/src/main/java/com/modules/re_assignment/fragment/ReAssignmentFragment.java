package com.modules.re_assignment.fragment;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.re_assignment.activity.CreateReAssignmentActivity;
import com.modules.re_assignment.adapter.ReAssignmentListAdapter;
import com.modules.re_assignment.model.ReAssignment;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 12/20/2019.
 */
public class ReAssignmentFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ReAssignmentFragment.class.getSimpleName();
    private ArrayList<ReAssignment> reAssignmentArrayList = new ArrayList<>(), searchLeaveArrayList;
    RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private FloatingActionButton createButton, createLeave;
    private CardView cardViewActionsSearch;
    private EditText editTextSearch;
    private ReAssignmentListAdapter reAssignmentListAdapter;

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

        View view = inflater.inflate(R.layout.re_assignment_list_layout, null);
        activity = getActivity();

        recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        cardViewActionsSearch = (CardView) view.findViewById(R.id.cardview_actions);
        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);
        createButton = (FloatingActionButton) view.findViewById(R.id.fab);
        createButton.setImageResource(R.drawable.re_assignment_icon_white);
        createLeave = (FloatingActionButton) view.findViewById(R.id.listview_fab);
        createLeave.setImageResource(R.drawable.re_assignment_icon_white);
        createButton.setOnClickListener(this);
        createLeave.setOnClickListener(this);
        createButton.setVisibility(View.VISIBLE);

        subSrearchFunctiaonality(view);

        return view;
    }

    private void subSrearchFunctiaonality(View view) {
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
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
        searchLeaveArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (ReAssignment reAssignment : reAssignmentArrayList) {
            if (reAssignment.getName() != null && reAssignment.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchLeaveArrayList.add(reAssignment);
            }
        }

        if (searchLeaveArrayList.size() > 0) {
            showErrorOne(false, 1);

            if (Preferences.get(General.ROLE_ID).equals("6")) {
                reAssignmentListAdapter = new ReAssignmentListAdapter(activity, searchLeaveArrayList);
                recyclerView.setAdapter(reAssignmentListAdapter);
            } else {
                reAssignmentListAdapter = new ReAssignmentListAdapter(activity, searchLeaveArrayList);
                recyclerView.setAdapter(reAssignmentListAdapter);
            }

        } else {
            showErrorOne(true, 2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.re_assignment_list));
        mainActivityInterface.setToolbarBackgroundColor();

        getReAssignmentList();
    }

    private void getReAssignmentList() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_REASSIGNMENT_LISTING);
        requestMap.put(General.SEARCH, "");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_ASSIGNMENT;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {

                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.GET_REASSIGNMENT_LISTING);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ReAssignment>>() {
                        }.getType();

                        reAssignmentArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GET_REASSIGNMENT_LISTING).toString(), listType);

                        if (reAssignmentArrayList.size() > 0) {
                            if (reAssignmentArrayList.get(0).getStatus() == 1) {
                                showError(false, 1);
                                reAssignmentListAdapter = new ReAssignmentListAdapter(activity, reAssignmentArrayList);
                                recyclerView.setAdapter(reAssignmentListAdapter);
                            } else {
                                showError(true, reAssignmentArrayList.get(0).getStatus());
                            }
                        } else {
                            showError(true, 2);
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 20);
    }

    private void showErrorOne(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
            createButton.setVisibility(View.GONE);
            createLeave.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            createLeave.setVisibility(View.VISIBLE);
            createButton.setVisibility(View.VISIBLE);
        }
    }


    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            if (Preferences.get(General.ROLE_ID).equals("6")) {
                createButton.setVisibility(View.GONE);
                createLeave.setVisibility(View.VISIBLE);
            } else {
                createButton.setVisibility(View.GONE);
                createLeave.setVisibility(View.VISIBLE);
            }

            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (Preferences.get(General.ROLE_ID).equals("6")) {
                createButton.setVisibility(View.VISIBLE);
                createLeave.setVisibility(View.GONE);
            } else {
                createButton.setVisibility(View.VISIBLE);
                createLeave.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent reassignment = new Intent(activity.getApplicationContext(), CreateReAssignmentActivity.class);
                activity.startActivity(reassignment);
                break;
        }
    }

}


