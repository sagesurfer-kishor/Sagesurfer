package com.modules.caseload.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.modules.caseload.adapter.PeerNoteAllAdapter;
import com.modules.caseload.observer.ObserverID;
import com.sagesurfer.collaborativecares.Application;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.CaseloadPeerNote_;
import com.sagesurfer.models.Members_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Kailash Karankal on 9/9/2019.
 */
public class PeerNoteAllFragment extends Fragment {
    private static final String TAG = PeerNoteAllFragment.class.getSimpleName();
    private String action = "";
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private ListView listView;
    private Activity activity;
    private CardView cardViewActionsSearch;
    private EditText editTextSearch;
    private ImageButton imageButtonDesc, imageButtonAsc, noteFilter;
    private Spinner spinnerPeerMentor;
    private ArrayList<Members_> peerMentorArrayList = new ArrayList<>();
    public ArrayList<CaseloadPeerNote_> caseloadPeerNoteArrayList = new ArrayList<>(), caseloadSearchPeerNoteArrayList = new ArrayList<>();
    private PeerNoteAllAdapter peerNoteAllAdapter;
    private PopupWindow popupWindow = new PopupWindow();
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay, startIndex = 0;
    private String date = "", end_date = "";

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

        View view = inflater.inflate(R.layout.peer_note_all_layout, null);

        activity = getActivity();

        Bundle data = getArguments();
        if (data.containsKey(General.ACTION)) {
            action = data.getString(General.ACTION);
        }

        initUi(view);

        setOnClickListeners();

        return view;
    }

    private void initUi(View view) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(0);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        spinnerPeerMentor = (Spinner) view.findViewById(R.id.spinner_peer_mentor);
        cardViewActionsSearch = (CardView) view.findViewById(R.id.cardview_actions);
        cardViewActionsSearch.setVisibility(View.VISIBLE);
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);

        imageButtonDesc = (ImageButton) view.findViewById(R.id.imagebutton_desc);
        imageButtonAsc = (ImageButton) view.findViewById(R.id.imagebutton_asc);
        noteFilter = (ImageButton) view.findViewById(R.id.note_filter);
    }

    private void setOnClickListeners() {
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
                // performSearch();
                String searchText = editTextSearch.getText().toString().trim();
                fetchPeerParticipantData("", "", "", searchText);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        imageButtonDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear previous data
                caseloadPeerNoteArrayList.clear();

                imageButtonAsc.setVisibility(View.VISIBLE);
                imageButtonDesc.setVisibility(View.GONE);
                fetchPeerParticipantData("", "", "ASC", "");
            }
        });

        imageButtonAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear previous data
                caseloadPeerNoteArrayList.clear();

                imageButtonAsc.setVisibility(View.GONE);
                imageButtonDesc.setVisibility(View.VISIBLE);
                fetchPeerParticipantData("", "", "DESC", "");
            }
        });

        noteFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteDateFilterDialogBox(v);
            }
        });

        //listView.setOnScrollListener(new EndlessScrollListener());
    }

    private void noteDateFilterDialogBox(View v) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View customView = inflater.inflate(R.layout.date_filter_popup, null);
        popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        popupWindow.isShowing();

        final Button resetBtn = customView.findViewById(R.id.reset_btn);
        final Button applyBtn = customView.findViewById(R.id.apply_btn);
        final TextView fromDateTxt = customView.findViewById(R.id.from_date_txt);
        final TextView toDateTxt = customView.findViewById(R.id.to_date_txt);
        final ImageView closeIcon = customView.findViewById(R.id.close_icon);

        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (Preferences.get(General.START_DATE) != null && Preferences.get(General.END_DATE) != null) {
            fromDateTxt.setText(Preferences.get(General.START_DATE));
            toDateTxt.setText(Preferences.get(General.END_DATE));
        }

        fromDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                Preferences.save(General.START_DATE, date);
                                try {
                                    fromDateTxt.setText(date);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_WEEK, -6);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        toDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                Preferences.save(General.END_DATE, end_date);
                                try {
                                    toDateTxt.setText(end_date);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.DAY_OF_WEEK, -6);
                datePickerDialog1.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog1.show();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferences.save(General.START_DATE, "");
                Preferences.save(General.END_DATE, "");
                fromDateTxt.setText("");
                toDateTxt.setText("");
                popupWindow.dismiss();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (action.trim().length() > 0) {
                    String dateOne = fromDateTxt.getText().toString().trim();
                    String dateTwo = toDateTxt.getText().toString().trim();
                    fetchPeerParticipantData(dateOne, dateTwo, "", "");
                }
                popupWindow.dismiss();
            }
        });

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    public void performSearch() {
        caseloadSearchPeerNoteArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (CaseloadPeerNote_ caseloadPeerNote : caseloadPeerNoteArrayList) {
            if (caseloadPeerNote.getPp_name() != null && caseloadPeerNote.getPp_name().toLowerCase().contains(searchText.toLowerCase())) {
                caseloadSearchPeerNoteArrayList.add(caseloadPeerNote);
            }
        }
        if (caseloadSearchPeerNoteArrayList.size() > 0) {
            showError(false, 1);
            peerNoteAllAdapter = new PeerNoteAllAdapter(activity, activity.getApplicationContext(), caseloadSearchPeerNoteArrayList, action);
            listView.setAdapter(peerNoteAllAdapter);
        } else {
            showError(true, 2);
        }
    }

    public class EndlessScrollListener implements AbsListView.OnScrollListener {
        private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                loading = true;
                // fetchPeerParticipantData("", "", "", "", true);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchPeerMentor();

        if (action.trim().length() > 0) {
            fetchPeerParticipantData("", "", "", "");
        }
    }

    private void fetchPeerMentor() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MENTOR_LIST);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    peerMentorArrayList = new ArrayList<>();
                    Members_ peerMentor = new Members_();
                    peerMentor.setUser_id(0);

                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage021)) ||
                            Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage022))) {
                        peerMentor.setUsername("Select Wellness Associates");
                    } else {
                        peerMentor.setUsername("All Peer Mentor");
                    }

                    peerMentor.setStatus(1);
                    peerMentorArrayList.add(peerMentor);

                    peerMentorArrayList.addAll(CaseloadParser_.parsePeerMentor(response, General.RESULT, activity.getApplicationContext(), TAG));

                    ArrayList<String> peerMentorNameList = new ArrayList<String>();
                    for (int i = 0; i < peerMentorArrayList.size(); i++) {
                        peerMentorNameList.add(peerMentorArrayList.get(i).getUsername());
                    }

                    if (peerMentorArrayList.size() > 0) {
                        ArrayAdapter<String> adapterConsumer = new ArrayAdapter<String>(activity, R.layout.drop_down_selected_text_item_layout, peerMentorNameList);
                        adapterConsumer.setDropDownViewResource(R.layout.drop_down_text_item_layout);
                        spinnerPeerMentor.setAdapter(adapterConsumer);
                        spinnerPeerMentor.setOnItemSelectedListener(onPeerMentorSelected);
                        spinnerPeerMentor.setSelection(0);

                        Preferences.save(General.MENTOR_ID, peerMentorArrayList.get(0).getUser_id());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final AdapterView.OnItemSelectedListener onPeerMentorSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerPeerMentor.setSelection(position);
            if (position == 0) {
                Preferences.save(General.MENTOR_ID, "0");
            } else {
                Preferences.save(General.MENTOR_ID, peerMentorArrayList.get(position).getUser_id());
                if (action.trim().length() > 0) {
                    fetchPeerParticipantData("", "", "", "");
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void fetchPeerParticipantData(String startDate, String endDate, String sort, String searchText) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ALL_NOTES);
        requestMap.put(General.TYPE, action);
        requestMap.put(General.LOGIN_USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.PM_ID, Preferences.get(General.MENTOR_ID));

        if (startDate.equalsIgnoreCase("")) {
            requestMap.put(General.START_DATE, "");
        } else {
            requestMap.put(General.START_DATE, startDate);
        }
        if (endDate.equalsIgnoreCase("")) {
            requestMap.put(General.END_DATE, "");
        } else {
            requestMap.put(General.END_DATE, endDate);
        }
        if (sort.equalsIgnoreCase("ASC")) {
            requestMap.put(General.SORT, "ASC");
        } else {
            requestMap.put(General.SORT, "DESC");
        }
        if (searchText.equalsIgnoreCase("")) {
            requestMap.put(General.SEARCH_TEXT, "");
        } else {
            requestMap.put(General.SEARCH_TEXT, searchText);
        }
        requestMap.put(General.START, "-1");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ADD_NOTES_API;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    caseloadPeerNoteArrayList = CaseloadParser_.parseAllPeerNote(response, Actions_.ALL_NOTES, activity.getApplicationContext(), TAG);

                    JsonParser parser = new JsonParser();
                    JsonObject jObject = parser.parse(response).getAsJsonObject();
                    JsonObject JsonObjectAllNotes = jObject.getAsJsonObject(Actions_.ALL_NOTES);

                    String start = String.valueOf(JsonObjectAllNotes.get("start"));
                    startIndex = Integer.parseInt(start);

                    String count = String.valueOf(JsonObjectAllNotes.get("count"));
                    Preferences.save("tab_count", Integer.parseInt(count));

                    String visitDate;

                    if (caseloadPeerNoteArrayList.size() > 0) {

                        if (caseloadPeerNoteArrayList.get(0).getStatus() == 2) {
                            Preferences.save("tab_count", Integer.parseInt(count));
                            Application.getInstance().getObserver().setValue(ObserverID.tabCounter);
                            showError(true, 2);
                        } else {
                            for (int i = 0; i < caseloadPeerNoteArrayList.size(); i++) {
                                visitDate = caseloadPeerNoteArrayList.get(i).getVisit_date();

                                Calendar c = Calendar.getInstance();
                                int currentDay = c.get(Calendar.DAY_OF_MONTH);
                                int currentMonth = c.get(Calendar.MONTH) + 1;
                                int currentYear = c.get(Calendar.YEAR);

                                String todayDate = currentYear + "-" + GetCounters.checkDigit(currentMonth) + "-" + GetCounters.checkDigit(currentDay);
                                String yesterdayDateTime = currentYear + "-" + GetCounters.checkDigit(currentMonth) + "-" + GetCounters.checkDigit(currentDay - 1);

                                if (visitDate.equals(todayDate)) {
                                    caseloadPeerNoteArrayList.get(i).setHeader_title("Today");
                                } else if (visitDate.equals(yesterdayDateTime)) {
                                    caseloadPeerNoteArrayList.get(i).setHeader_title("Yesterday");
                                } else {
                                    caseloadPeerNoteArrayList.get(i).setHeader_title("Last Week");
                                }
                            }

                            showError(false, 1);
                            peerNoteAllAdapter = new PeerNoteAllAdapter(activity, activity.getApplicationContext(), caseloadPeerNoteArrayList, action);
                            listView.setAdapter(peerNoteAllAdapter);

                            Application.getInstance().getObserver().setValue(ObserverID.tabCounter);
                        }
                    } else {
                        showError(true, 2);
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
