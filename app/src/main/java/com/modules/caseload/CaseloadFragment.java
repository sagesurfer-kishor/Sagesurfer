package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.modules.caseload.mhaw.adapter.MhawCaseloadListNewAdapter;
import com.modules.caseload.senjam.adapter.SenjamCaseloadListAdapter;
import com.modules.caseload.werhope.adapter.CaseloadListNewAdapter;
import com.sagesurfer.adapters.CaseloadListAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Caseload_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 * Created on 5/24/2019
 * Last Modified on 5/24/2019
 */

public class CaseloadFragment extends Fragment implements CaseloadListAdapter.CaseloadListAdapterListener, View.OnClickListener/*, CaseloadSettingActivity.GetChoice*/ {

    private static final String TAG = CaseloadFragment.class.getSimpleName();

    @BindView(R.id.edittext_search)
    EditText editTextSearch;

    @BindView(R.id.imagebutton_filter)
    AppCompatImageButton imageButtonFilter;

    @BindView(R.id.imagebutton_setting)
    AppCompatImageButton imageButtonSetting;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.linearlayout_error)
    LinearLayout linearLayoutError;

    @BindView(R.id.imageview_error)
    AppCompatImageView imageViewError;

    @BindView(R.id.textview_error_message)
    TextView textViewErrorMessage;

    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private Unbinder unbinder;
    private String sort = General.ALPHABETICAL_SORT;
    public ArrayList<Caseload_> caseloadArrayList = new ArrayList<>(), searchCaseloadArrayList = new ArrayList<>();
    public CaseloadListAdapter caseloadListAdapter;
    private int item_selection = 1;
    private View view;
    private String senjam_patient_id = "";

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivityInterface = (MainActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " " + e.toString());
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
            view = inflater.inflate(R.layout.fragment_caseload_werhope, container, false);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
            view = inflater.inflate(R.layout.fragment_caseload_werhope, container, false);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {
            view = inflater.inflate(R.layout.fragment_caseload_werhope, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_caseload, container, false);
        }


        if (getArguments() != null) {
            senjam_patient_id = getArguments().getString("senjam_patient_id");
        }

        unbinder = ButterKnife.bind(this, view);
        activity = getActivity();

        imageButtonFilter.setOnClickListener(this);

        int color = Color.parseColor("#757575");
        imageButtonSetting.setColorFilter(color);
        imageButtonSetting.setImageResource(R.drawable.vi_drawer_setting);
        imageButtonSetting.setOnClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        caseloadListAdapter = new CaseloadListAdapter(activity.getApplicationContext(), caseloadArrayList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(caseloadListAdapter);

        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    editTextSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert in != null;
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

    public void performSearch() {
        searchCaseloadArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert in != null;
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (Caseload_ caseLoadItem : caseloadArrayList) {
            if (caseLoadItem.getUsername() != null && caseLoadItem.getUsername().toLowerCase().contains(searchText.toLowerCase())) {
                searchCaseloadArrayList.add(caseLoadItem);
                Preferences.save(General.CASELOAD_LIST, true);
            }
        }
        if (searchCaseloadArrayList.size() > 0) {
            showError(false, 1);
            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage024))) {
                CaseloadListNewAdapter caseloadListAdapter = new CaseloadListNewAdapter(activity, searchCaseloadArrayList);
                recyclerView.setAdapter(caseloadListAdapter);
            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {
                MhawCaseloadListNewAdapter caseloadListAdapter = new MhawCaseloadListNewAdapter(activity, searchCaseloadArrayList);
                recyclerView.setAdapter(caseloadListAdapter);
            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage030))
                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage031))) {
                SenjamCaseloadListAdapter senjamCaseloadListAdapter = new SenjamCaseloadListAdapter(activity, searchCaseloadArrayList, senjam_patient_id);
                recyclerView.setAdapter(senjamCaseloadListAdapter);
            } else {
                caseloadListAdapter = new CaseloadListAdapter(activity.getApplicationContext(), searchCaseloadArrayList, this);
                recyclerView.setAdapter(caseloadListAdapter);
            }
        } else {
            showError(true, 2);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))) {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.peer_participant_engagement));
        } else {
            mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.caseload));
        }
        mainActivityInterface.setToolbarBackgroundColor();

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
            getCaseloadData(Actions_.GET_CASELOAD_DATA_WER);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
            getCaseloadData(Actions_.GET_CASELOAD_DATA_TARZ);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026))) {
            getCaseloadData(Actions_.GET_CASELOAD_DATA);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
            getCaseloadData(Actions_.GET_CASELOAD_DATA);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031))) {
            getCaseloadData(Actions_.GET_CASELOAD_DATA);
        } else {
            getCaseloadData(Actions_.GET_CASELOAD_DATA);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //make network call to fetch all consumers for Care Coordinator / Clinician
    private void getCaseloadData(String action) {
        HashMap<String, String> requestMap = new HashMap<>();
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
            requestMap.put(General.SEARCH, "");
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage030))
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage031)))
        {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
            requestMap.put(General.SEARCH, "");
        } else {
            requestMap.put(General.ACTION, action);
            requestMap.put(General.SORT, sort);
        }
        requestMap.put(General.ROLE_ID, Preferences.get(General.ROLE_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));
        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_CASELOAD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                Log.e("CaseloadResponse", response);
                if (response != null) {
                    caseloadArrayList = CaseloadParser_.parseCaseload(response, action, activity.getApplicationContext(), TAG);
                    Log.e("uuuu", caseloadArrayList.toString());
                    if (caseloadArrayList.size() > 0) {
                        if (caseloadArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);

                            if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage023)) ||
                                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage024))
                                    || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage025))) {

                                if (Preferences.getBoolean(General.CASELOAD_LIST)) {
                                    CaseloadListNewAdapter caseloadListAdapter = new CaseloadListNewAdapter(activity, searchCaseloadArrayList);
                                    recyclerView.setAdapter(caseloadListAdapter);
                                } else {
                                    CaseloadListNewAdapter caseloadListAdapter = new CaseloadListNewAdapter(activity, caseloadArrayList);
                                    recyclerView.setAdapter(caseloadListAdapter);
                                }

                            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage026)) ||
                                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage027))) {


                                if (Preferences.getBoolean(General.CASELOAD_LIST)) {
                                    MhawCaseloadListNewAdapter caseloadListAdapter = new MhawCaseloadListNewAdapter(activity, searchCaseloadArrayList);
                                    recyclerView.setAdapter(caseloadListAdapter);

                                } else {
                                    MhawCaseloadListNewAdapter caseloadListAdapter = new MhawCaseloadListNewAdapter(activity, caseloadArrayList);
                                    recyclerView.setAdapter(caseloadListAdapter);
                                }

                            } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage030)) ||
                                    Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(activity.getResources().getString(R.string.sage031))) {

                                if (Preferences.getBoolean(General.CASELOAD_LIST)) {
                                    SenjamCaseloadListAdapter senjamCaseloadListAdapter = new SenjamCaseloadListAdapter(activity, searchCaseloadArrayList, senjam_patient_id);
                                    recyclerView.setAdapter(senjamCaseloadListAdapter);
                                } else {
                                    SenjamCaseloadListAdapter senjamCaseloadListAdapter = new SenjamCaseloadListAdapter(activity, caseloadArrayList, senjam_patient_id);
                                    recyclerView.setAdapter(senjamCaseloadListAdapter);
                                    if (!TextUtils.isEmpty(senjam_patient_id)) {
                                        for (int i = 0; i < caseloadArrayList.size(); i++) {
                                            if (senjam_patient_id.equalsIgnoreCase(String.valueOf(caseloadArrayList.get(i).getUser_id()))) {
                                                recyclerView.scrollToPosition(i);
                                                break;
                                            }
                                        }
                                    }
                                }

                            } else {
                                caseloadListAdapter = new CaseloadListAdapter(activity.getApplicationContext(), caseloadArrayList, this);
                                recyclerView.setAdapter(caseloadListAdapter);
                            }

                        } else {
                            showError(true, caseloadArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, caseloadArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            linearLayoutError.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            textViewErrorMessage.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            imageViewError.setImageResource(GetErrorResources.getIcon(status));
        } else {
            linearLayoutError.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagebutton_filter:
                showFilterPopupMenu(1, v);
                break;

            case R.id.imagebutton_setting:
                Intent detailsIntent = new Intent(activity.getApplicationContext(), CaseloadSettingActivity.class);
                startActivity(detailsIntent);
                activity.overridePendingTransition(0, 0);
                break;
        }
    }

    // show respective filter options menu
    private void showFilterPopupMenu(final int type, View view) {
        final PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.menu_caseload_sort, popup.getMenu());
        MenuItem itemAlphabetic = popup.getMenu().findItem(R.id.menucaseloadsort_alphabetical);
        MenuItem itemWorsening = popup.getMenu().findItem(R.id.menucaseloadsort_worsening);
        MenuItem itemLastContacted = popup.getMenu().findItem(R.id.menucaseloadsort_lastcontacted);
        if (item_selection == 1) {
            itemAlphabetic.setChecked(true);
        }
        if (item_selection == 2) {
            itemWorsening.setChecked(true);
        }
        if (item_selection == 3) {
            itemLastContacted.setChecked(true);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            MenuItem subMenuItem;

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menucaseloadsort_alphabetical:
                        item.setChecked(!item.isChecked());
                        item_selection = 1;
                        sort = General.ALPHABETICAL_SORT;
                        break;
                    case R.id.menucaseloadsort_worsening:
                        item.setChecked(!item.isChecked());
                        item_selection = 2;
                        sort = General.WORSENING_SORT;
                        break;
                    case R.id.menucaseloadsort_lastcontacted:
                        item.setChecked(!item.isChecked());
                        item_selection = 3;
                        sort = General.LAST_CONTACTED_SORT;
                        break;
                }
                popup.dismiss();
                getCaseloadData(Actions_.GET_CASELOAD_DATA);
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onDetailsLayoutClicked(Caseload_ caseload_) {
        DialogFragment dialogFrag = new CaseloadActionsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Actions_.TEAM_DATA, caseload_);
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), General.COMMENT_COUNT);
    }
}