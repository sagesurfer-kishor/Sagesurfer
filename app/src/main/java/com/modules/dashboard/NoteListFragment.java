package com.modules.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Dashboards_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author girish M (girish@sagesurfer.com)
 * Created on 26/4/16.
 * Last Modified on 13/12/2017.
 */

public class NoteListFragment extends Fragment implements View.OnClickListener, NoteListAdapter.Delete {

    private SwipeMenuListView listView;
    private TextView listWarning;
    private EditText noteBox;
    private AppCompatImageView addNote;
    private LinearLayout errorLayout;
    private AppCompatImageView errorIcon;

    private ArrayList<Note_> noteList;
    private String consumer_id = "";
    private long note_id = 0;
    private static final String TAG = NoteListFragment.class.getSimpleName();

    private Activity activity;
    private MainActivityInterface mainActivityInterface;

    public static NoteListFragment newInstance(String _id) {
        NoteListFragment myFragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putString(General.ID, _id);
        myFragment.setArguments(args);
        return myFragment;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivityInterface = (MainActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainActivityInterface");
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

        View view = inflater.inflate(R.layout.note_list_view_layout, null);

        Preferences.initialize(getActivity().getApplicationContext());
        activity = getActivity();

        noteList = new ArrayList<>();

        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.make_a_note));
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.note_list_view_refresh);
        swipeRefreshLayout.setEnabled(false);

        listView = (SwipeMenuListView) view.findViewById(R.id.note_list_view);
        listWarning = (TextView) view.findViewById(R.id.note_list_view_warning);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        listView.setPadding(0, 7, 0, 7);
        listView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));
        listView.setOnItemClickListener(onItemClick);

        noteBox = (EditText) view.findViewById(R.id.note_list_input_box);
        noteBox.addTextChangedListener(textWatcher);

        addNote = (AppCompatImageView) view.findViewById(R.id.note_list_add);
        addNote.setOnClickListener(this);

        errorIcon = (AppCompatImageView) view.findViewById(R.id.note_list_view_error_icon);
        errorLayout = (LinearLayout) view.findViewById(R.id.note_list_error_view_layout);

        Bundle data = getArguments();
        if (data.containsKey(General.ID)) {
            consumer_id = data.getString(General.ID);
        }
        return view;
    }

    // note editor text watcher to enable/disable send button
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() <= 0) {
                addNote.setImageResource(R.drawable.ic_send_gray);
            } else {
                addNote.setImageResource(R.drawable.ic_send_blue);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // handle click for note list view item
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (noteList.get(position).getStatus() == 1) {
                note_id = noteList.get(position).getId();
                noteBox.setText(noteList.get(position).getMessage());
                noteBox.setSelection(noteBox.getText().toString().length());
                noteBox.requestFocus();
            }
        }
    };

    //show warning if status code is not 1
    private void setWarning(String message, int image) {
        listView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        listWarning.setText(message);
        errorIcon.setImageResource(image);
    }

    @Override
    public void onStart() {
        super.onStart();
        Preferences.initialize(getActivity().getApplicationContext());
        mainActivityInterface.setToolbarBackgroundColor();
        fetchData();
    }

    // make call to fetch all note list from server
    private void fetchData() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.STICKY_NOTES);
        requestMap.put("youth_id", consumer_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CARE_GIVER_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    noteList = Dashboards_.parseNotes(response, activity.getApplicationContext(), TAG);
                    if (noteList.size() > 0) {
                        if (noteList.get(0).getStatus() == 1) {
                            errorLayout.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            NoteListAdapter noteListAdapter = new NoteListAdapter(activity, noteList, this);
                            listView.setAdapter(noteListAdapter);
                        } else {
                            setWarning(GetErrorResources.getMessage(noteList.get(0).getStatus(),
                                    activity.getApplicationContext()), GetErrorResources.getIcon(noteList.get(0).getStatus()));
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            setWarning(activity.getApplicationContext().getResources().getString(R.string.internal_error_occurred), GetErrorResources.getIcon(noteList.get(0).getStatus()));
        }
    }

    // make call to add new note
    private void addNote(String noteText) {
        int result = 12;
        //setWarning("Loading.....", R.drawable.ic_no_data);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.ADD_STICKY);
        requestMap.put("text_note", noteText);
        requestMap.put("youth_id", consumer_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CARE_GIVER_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Actions_.ADD_STICKY)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.ADD_STICKY);
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.has(General.STATUS)) {
                            result = object.getInt(General.STATUS);
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == 1) {
            fetchData();
            noteBox.setText("");
        }
        showResponses(result);
    }

    private void showResponses(int status) {
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, addNote, activity.getApplicationContext());
    }

    // make call to update note selected
    private void updateNote(String noteText) {
        int result = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.UPDATE_STICKY);
        requestMap.put(General.ID, "" + note_id);
        requestMap.put("text_note", noteText);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CARE_GIVER_DASHBOARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(Actions_.UPDATE_STICKY)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Actions_.UPDATE_STICKY);
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.has(General.STATUS)) {
                            result = object.getInt(General.STATUS);
                        } else {
                            result = 11;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == 1) {
            fetchData();
            noteBox.setText("");
        }
        showResponses(result);
    }

    // validate note text
    private boolean isValidNote(String noteText) {
        return !(noteText == null || noteText.length() <= 0) && noteText.length() <= 1000;
    }

    // hide soft keyboard if open
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.note_list_add:
                hideKeyboard();
                String noteText = noteBox.getText().toString().trim();
                if (isValidNote(noteText)) {
                    if (note_id <= 0) {
                        addNote(noteText);
                    } else {
                        updateNote(noteText);
                    }
                } else {
                    noteBox.setError("Enter valid note");
                }
                break;
        }
    }

    @Override
    public void delete(int status) {
        showResponses(status);
        if (status == 1) {
            noteBox.setText("");
            note_id = 0;
        }
    }
}