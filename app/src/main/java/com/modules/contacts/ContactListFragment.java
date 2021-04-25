package com.modules.contacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Contacts_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Broadcast;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.TeamDetails_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 12-08-2017
 * Last Modified on 13-12-2017
 */

public class ContactListFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ContactListFragment.class.getSimpleName();
    private ArrayList<Contacts_> contactsArrayList;
    private ArrayList<Contacts_> searchContactsArrayList;
    private Map<String, Integer> mapIndex;

    private Activity activity;
    private BroadcastReceiver receiver;
    private AllContactListAdapter contactListAdapter;

    private ScrollView indexScroll;
    private LinearLayout errorLayout, indexLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private ListView listView;
    //private RecyclerView recyclerView;
    private MainActivityInterface mainActivityInterface;
    String[] contactAlphabet;
    TextView previousSelectedIndex;
    int indexSelectedAlphabet = -1;
    private EditText editTextSearch;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityInterface = (MainActivityInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //For setting status bar color
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), GetColor.getHomeIconBackgroundColorColorParse(false)));

        View view = inflater.inflate(R.layout.contact_list_layout, null);

        activity = getActivity();
        contactsArrayList = new ArrayList<>();

        Preferences.initialize(activity.getApplicationContext());

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);
        listView.setDividerHeight(3);
        listView.setOnItemClickListener(onItemClick);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        indexLayout = (LinearLayout) view.findViewById(R.id.contact_list_side_index);
        indexScroll = (ScrollView)view.findViewById(R.id.contact_list_side_index_scroll);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(Broadcast.SEARCH_BROADCAST)) {
                    String query = intent.getStringExtra(Broadcast.SEARCH_BROADCAST);
                    if (query.length() == 0 || query.equals("~")) {
                        indexScroll.setVisibility(View.VISIBLE);
                        contactListAdapter.filterContacts("");
                    } else {
                        indexScroll.setVisibility(View.GONE);
                        contactListAdapter.filterContacts(query);
                    }
                }
            }
        };

        contactAlphabet = getResources().getStringArray(R.array.contact_alphabet_array);
        getIndexList();

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
    public void onResume() {
        super.onResume();
        getContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.unregisterReceiver(receiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.contacts));
        mainActivityInterface.setToolbarBackgroundColor();
        getActivity().registerReceiver(receiver, new IntentFilter(Broadcast.SEARCH_BROADCAST));
    }

    public void performSearch() {
        searchContactsArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if(searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for(Contacts_ ccntactstItem : contactsArrayList){
            if(ccntactstItem.getFirstName() != null && ccntactstItem.getFirstName().toLowerCase().contains(searchText.toLowerCase())) {
                searchContactsArrayList.add(ccntactstItem);
            }
        }
        if (searchContactsArrayList.size() > 0) {
            showError(false, 1);
            AllContactListAdapter contactListAdapter = new AllContactListAdapter(activity, searchContactsArrayList);
            listView.setAdapter(contactListAdapter);
        } else {
            showError(true, 2);
        }
    }

    // Handle on click for contact list item to show contact details dialog
    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (contactsArrayList.get(position).getStatus() == 1) {
              //  openDetails(position);
            }
        }
    };

    public void onClick(View view) {
       if(indexSelectedAlphabet != -1) {
           previousSelectedIndex.setBackgroundColor(activity.getResources().getColor(R.color.contact_alphabet));
       }
        TextView selectedIndex = (TextView) view;
        selectedIndex.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        for (int i = 0; i < contactAlphabet.length; i++) {
            if(contactAlphabet[i].equalsIgnoreCase(selectedIndex.getText().toString())) {
                indexSelectedAlphabet = i;
                previousSelectedIndex = (TextView) view;
            }
        }

        //setting contact array list position, on click of Alphabet on right pane
        for (int i = 0; i < contactsArrayList.size(); i++) {
            String name = contactsArrayList.get(i).getFirstName().toUpperCase();
            String index = name.substring(0, 1);
            if(index.equalsIgnoreCase(contactAlphabet[indexSelectedAlphabet])) {
                listView.setSelection(i);
                break;
            }
        }
    }

    // Open contact details dialog
    @SuppressLint("CommitTransaction")
    private void openDetails(int position) {
        DialogFragment dialogFrag = new ContactDetailsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Actions_.TEAM_CONTACT, contactsArrayList.get(position));
        dialogFrag.setArguments(bundle);
        dialogFrag.show(activity.getFragmentManager().beginTransaction(), General.COMMENT_COUNT);
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            indexScroll.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            indexScroll.setVisibility(View.VISIBLE);
        }
    }

    // Get identical display index from contact list
    private void getIndexList() {
        mapIndex = new LinkedHashMap<>();
        for (int i = 0; i < contactAlphabet.length; i++) {
            String alphabet = contactAlphabet[i].toUpperCase();

            if (mapIndex.get(alphabet) == null)
                mapIndex.put(alphabet, i);
        }
        displayIndex();
    }

    // Show display index on right side of screen
    @SuppressLint("InflateParams")
    private void displayIndex() {
        TextView textView;
        List<String> indexList = new ArrayList<>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.contact_list_side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(this);
            indexLayout.addView(textView);
        }
    }

    //Make network call to fetch all contacts from server
    private void getContacts() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.USER_CONTACTS);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_CONTACT_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    contactsArrayList = TeamDetails_.parseContacts(response, Actions_.USER_CONTACTS, activity.getApplicationContext(), TAG);
                    if (contactsArrayList.size() > 0) {
                        if (contactsArrayList.get(0).getStatus() == 1) {
                            contactListAdapter = new AllContactListAdapter(activity, contactsArrayList);
                            listView.setAdapter(contactListAdapter);
                        } else {
                            showError(true, contactsArrayList.get(0).getStatus());
                        }
                    } else {
                        showError(true, 12);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}