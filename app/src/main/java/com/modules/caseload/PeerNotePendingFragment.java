package com.modules.caseload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
 import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sagesurfer.adapters.CaseloadPeerNoteAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.CaseloadPeerNote_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 10/16/2018.
 */

public class PeerNotePendingFragment extends Fragment {
    private static final String TAG = PeerNotePendingFragment.class.getSimpleName();
    private String action = "";
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private ListView listView;
    private CaseloadPeerNoteAdapter caseloadPeerNoteAdapter;
    public ArrayList<CaseloadPeerNote_> caseloadPeerNoteArrayList = new ArrayList<>();
    private Activity activity;
    private MainActivityInterface mainActivityInterface;

    private CardView cardViewActionsSearch;
    private EditText editTextSearch;
    private ImageButton imageButtonSetting;
    private ArrayList<CaseloadPeerNote_> searchPendingNoteArrayList;


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //mainActivityInterface = (MainActivityInterface) activity;
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

        Bundle data = getArguments();
        if (data.containsKey(General.ACTION)) {
            action = data.getString(General.ACTION);
        }
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);
        //listView.setOnItemClickListener(onItemClick);
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
        searchPendingNoteArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (CaseloadPeerNote_ caseloadPeerNote : caseloadPeerNoteArrayList) {
            if (caseloadPeerNote.getSubject() != null && caseloadPeerNote.getSubject().toLowerCase().contains(searchText.toLowerCase())) {
                searchPendingNoteArrayList.add(caseloadPeerNote);
            }
        }
        if (searchPendingNoteArrayList.size() > 0) {
            showError(false, 1);
            caseloadPeerNoteAdapter = new CaseloadPeerNoteAdapter(activity, activity.getApplicationContext(), searchPendingNoteArrayList, action);
            listView.setAdapter(caseloadPeerNoteAdapter);
        } else {
            showError(true, 2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //mainActivityInterface.hideRevealView();
        //mainActivityInterface.setToolbarBackgroundColor();
        if (action.trim().length() > 0) {
            getPeerCaseloadNoteListData();
        }
    }

    /*// handle click for row item to open content details
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CaseloadPeerNote_ caseloadPeerNote_ = caseloadPeerNoteArrayList.get(position);
            if (caseloadPeerNote_.getStatus() == 1) {
                Intent detailsIntent = new Intent(activity.getApplicationContext(), PeerNoteDetailsActivity.class);
                detailsIntent.putExtra(General.ID, caseloadPeerNote_.getVisit_id());
                startActivity(detailsIntent, ActivityTransition.moveToNextAnimation(activity.getApplicationContext()));
            }
        }
    };*/

    //make network call to fetch all Peer Notes
    private void getPeerCaseloadNoteListData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        if (CheckRole.isSupervisor(Integer.parseInt(Preferences.get(General.ROLE_ID)))
                || CheckRole.isInstanceAdmin(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            requestMap.put(General.LOGIN_USER_ID, Preferences.get(General.MENTOR_ID));
        } else {
            requestMap.put(General.LOGIN_USER_ID, Preferences.get(General.USER_ID));
        }
        requestMap.put(General.CUST_ID, Preferences.get(General.CONSUMER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    caseloadPeerNoteArrayList = CaseloadParser_.parsePeerNote(response, General.RESULT, activity.getApplicationContext(), TAG);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(General.STATUS) && jsonObject.getInt(General.STATUS) == 1) {
                        if (caseloadPeerNoteArrayList.size() > 0) {
                            showError(false, 1);
                            caseloadPeerNoteAdapter = new CaseloadPeerNoteAdapter(activity, activity.getApplicationContext(), caseloadPeerNoteArrayList, action);
                            listView.setAdapter(caseloadPeerNoteAdapter);
                        } else {
                            showError(true, 2);
                        }
                    } else {
                        showError(true, jsonObject.getInt(General.STATUS));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
