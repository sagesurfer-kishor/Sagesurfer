package com.modules.selfcare;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagesurfer.adapters.FilterCountryExpandableListAdapter;
import com.sagesurfer.adapters.SimpleMultiSelectAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.models.City_;
import com.sagesurfer.models.Country_;
import com.sagesurfer.models.State_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SelfCare_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.views.EndlessScrollListener;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.RequestBody;

/**
 * @author girish M (girish@sagesurfer.com)
 * Created on 26/04/2016
 * Last Modified on 26/12/2017
 */

public class FilterChoiceDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = FilterChoiceDialogFragment.class.getSimpleName();
    private TextView warningText;
    private ListView listView;
    private FrameLayout frameLayoutLocation;
    SimpleMultiSelectAdapter simpleMultiSelectAdapter;
    private static ExpandableListView expandableListView;
    FilterCountryExpandableListAdapter filterCountryExpandableListAdapter;

    private String action = "";
    private boolean isChoice = false;
    private ArrayList<ListItem> choiceList;
    LinkedHashMap<String, ArrayList<State_>> stateHashMapList = new LinkedHashMap<>();
    private ArrayList<ListItem> stateChoiceList;
    ArrayList<LinkedHashMap<String, ArrayList<State_>>> stateBackArrayList = new ArrayList<>();
    ArrayList<LinkedHashMap<String, ArrayList<State_>>> tempStateArrayList = new ArrayList<>();
    private ArrayList<Choices_> backList;
    private ArrayList<Country_> locationBackList;
    static ArrayList<Integer> idsExpand = new ArrayList<Integer>();

    private Activity activity;
    private GetChoice getChoice;
    private RelativeLayout relativeLayoutToolbar;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

    interface GetChoice {
        void GetSelected(ArrayList<ListItem> selectionList, ArrayList<Country_> locationBackList,
                         ArrayList<LinkedHashMap<String, ArrayList<State_>>> stateArrayList, ArrayList<ListItem> stateChoiceList,
                         boolean isSelected, String action);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getChoice = (GetChoice) activity;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d.getWindow() != null) {
            int width = ViewGroup.LayoutParams.FILL_PARENT;
            int height = ViewGroup.LayoutParams.FILL_PARENT;
            d.getWindow().setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setStyle(Window.FEATURE_NO_TITLE, R.style.MY_DIALOG);
        d.setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        setRetainInstance(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multiselect_list_view_layout, null);

        choiceList = new ArrayList<>();
        backList = new ArrayList<>();
        locationBackList = new ArrayList<>();
        stateChoiceList = new ArrayList<>();
        stateBackArrayList = new ArrayList<>();

        activity = getActivity();

        Bundle data = getArguments();
        if (data != null) {
            if (data.containsKey(General.ACTION)) {
                action = data.getString(General.ACTION);
                choiceList = (ArrayList<ListItem>) data.getSerializable(General.FILTER);
                backList = (ArrayList<Choices_>) data.getSerializable(General.TEAM_LIST);
                stateChoiceList = (ArrayList<ListItem>) data.getSerializable(General.STATECHOICELIST);
                stateBackArrayList = (ArrayList<LinkedHashMap<String, ArrayList<State_>>>) data.getSerializable(General.STATEBACKLIST);
                locationBackList = (ArrayList<Country_>) data.getSerializable(General.LOCATIONLIST);
            } else {
                dismiss();
            }
        } else {
            dismiss();
        }
        ImageView backButton = (ImageView) view.findViewById(R.id.multi_select_list_dialog_back);
        backButton.setOnClickListener(this);

        relativeLayoutToolbar = (RelativeLayout) view.findViewById(R.id.relativelayout_toolbar);
        TextView submitText = (TextView) view.findViewById(R.id.multi_select_list_dialog_submit);
        submitText.setOnClickListener(this);

        warningText = (TextView) view.findViewById(R.id.multi_select_list_dialog_warning);

        listView = (ListView) view.findViewById(R.id.multi_select_list_dialog_list_view);
        simpleMultiSelectAdapter = new SimpleMultiSelectAdapter(getActivity(), choiceList);
        frameLayoutLocation = (FrameLayout) view.findViewById(R.id.framelayout_location);
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandablelistview_location);
        filterCountryExpandableListAdapter = new FilterCountryExpandableListAdapter(activity.getApplicationContext(), choiceList, stateBackArrayList);

        if (choiceList != null && choiceList.size() > 0) {
            if (action.equalsIgnoreCase(Actions_.GET_LOCATIONS)) {
                setExpandableListView();
            } else {
                setListView();
            }
        } else {
            if (action.equalsIgnoreCase(Actions_.GET_LOCATIONS)) {
                Preferences.save(General.LAST_COUNTRY, "0");
                getLocations();
            } else {
                getChoice();
            }
        }

        TextView titleText = (TextView) view.findViewById(R.id.multi_select_list_dialog_title);
        if (action.equals(Actions_.GET_CATEGORY)) {
            titleText.setText(getActivity().getApplicationContext().getResources().getString(R.string.category));
        }
        if (action.equals(Actions_.GET_LANGUAGES)) {
            titleText.setText(getActivity().getApplicationContext().getResources().getString(R.string.language));
        }
        if (action.equals(Actions_.GET_AGE)) {
            titleText.setText(getActivity().getApplicationContext().getResources().getString(R.string.age_group));
        }
        if (action.equals(Actions_.GET_TYPE)) {
            titleText.setText(getActivity().getApplicationContext().getResources().getString(R.string.content_type));
        }
        if (action.equals(Actions_.GET_LOCATIONS)) {
            titleText.setText(getActivity().getApplicationContext().getResources().getString(R.string.country));
        }

        expandableListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextLocations(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        return view;
    }


    private void setWarningText(String message, int icon) {
        listView.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        warningText.setText(message);
        warningText.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
    }

    private void getChoice() {
        if (action.equalsIgnoreCase(Actions_.GET_TYPE)) {
            showChoice("");
            return;
        }
        //setWarningText(R.string.loading, R.drawable.ic_no_data);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    showChoice(response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getLocations() {
        if (action.equalsIgnoreCase(Actions_.GET_TYPE)) {
            showChoice("");
            return;
        }
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_LOCATIONS);
        requestMap.put(General.LAST_COUNTRY, Preferences.get(General.LAST_COUNTRY));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    showChoice(response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showChoice(String response) {
        int result = 12;
        ArrayList<Choices_> list = new ArrayList<>();
        ArrayList<Country_> locationList = new ArrayList<>();
        try {
            if (response != null) {
                if (action.equals(Actions_.GET_CATEGORY)) {
                    list = SelfCare_.parseCategory(response, Actions_.GET_CATEGORY);
                }
                if (action.equals(Actions_.GET_LANGUAGES)) {
                    list = SelfCare_.parseCategory(response, Actions_.GET_LANGUAGES);
                }
                if (action.equals(Actions_.GET_AGE)) {
                    list = SelfCare_.parseCategory(response, Actions_.GET_AGE);
                }
                if (action.equals(Actions_.GET_TYPE)) {
                    list = SelfCare_.getContentType(activity.getApplicationContext());
                }
                if (action.equals(Actions_.GET_LOCATIONS)) {
                    locationList = SelfCare_.parseLocation(response, Actions_.GET_LOCATIONS, activity.getApplicationContext());
                }
                if (action.equalsIgnoreCase(Actions_.GET_LOCATIONS)) {
                    if (locationList.size() > 0) {
                        result = locationList.get(0).getStatus();
                    }
                } else {
                    if (list.size() > 0) {
                        result = list.get(0).getStatus();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String message = "";
        if (result == 1) {
            if (action.equalsIgnoreCase(Actions_.GET_LOCATIONS)) {
                tempStateArrayList = new ArrayList<>();
                choiceList.addAll(reverseLocationList(locationList));
                locationBackList.addAll(locationList);
                stateBackArrayList.addAll(tempStateArrayList);
                stateChoiceList.addAll(reverseStateList(tempStateArrayList));
                setExpandableListView();
            } else {
                choiceList = reverseList(list);
                backList = list;
                simpleMultiSelectAdapter.notifyDataSetChanged();
                setListView();
            }
        } else {
            message = this.getResources().getString(R.string.action_failed);
            SubmitSnackResponse.showSnack(result, message, activity.getApplicationContext());
        }
    }

    private ArrayList<ListItem> reverseList(ArrayList<Choices_> old_array) {
        ArrayList<ListItem> list = new ArrayList<>();
        for (int i = 0; i < old_array.size(); i++) {
            ListItem listItem = new ListItem();
            listItem.setName(old_array.get(i).getName());
            listItem.setId(String.valueOf(old_array.get(i).getId()));
            if (old_array.get(i).getIs_selected().equalsIgnoreCase("1")) {
                listItem.setSelected(true);
            } else {
                listItem.setSelected(false);
            }
            list.add(listItem);
        }
        return list;
    }

    private ArrayList<Country_> convertLocationList(ArrayList<ListItem> old_array) {
        ArrayList<Country_> locationArrayList = new ArrayList<>();
        for (int i = 0; i < old_array.size(); i++) {
            Country_ country_ = new Country_();
            country_.setStatus(1);
            country_.setId(Long.valueOf(old_array.get(i).getId()));
            country_.setName(old_array.get(i).getName());
            country_.setState(locationBackList.get(i).getState());
            country_.setLast_country(locationBackList.get(i).getLast_country());
            if (old_array.get(i).getSelected()) {
                country_.setIs_selected(true);
            } else {
                country_.setIs_selected(false);
            }
            locationArrayList.add(country_);
        }
        return locationArrayList;
    }

    private ArrayList<ListItem> reverseLocationList(ArrayList<Country_> old_array) {
        ArrayList<ListItem> list = new ArrayList<>();
        tempStateArrayList = new ArrayList<>();

        for (int i = 0; i < old_array.size(); i++) {
            stateHashMapList = new LinkedHashMap<>();
            ListItem listItem = new ListItem();
            listItem.setName(old_array.get(i).getName());
            listItem.setId(String.valueOf(old_array.get(i).getId()));
            if (old_array.get(i).isSelected()) {
                listItem.setSelected(true);
            } else {
                listItem.setSelected(false);
            }

            if (old_array.get(i).getStatus() == 1) {
                stateHashMapList.put(String.valueOf(old_array.get(i).getId()), old_array.get(i).getState());
                tempStateArrayList.add(stateHashMapList);
            }

            list.add(listItem);
        }
        return list;
    }

    private ArrayList<LinkedHashMap<String, ArrayList<State_>>> convertStateList(ArrayList<ListItem> old_array) {
        ArrayList<LinkedHashMap<String, ArrayList<State_>>> stateArrayList = new ArrayList<>();
        for (int i = 0; i < locationBackList.size(); i++) {
            stateHashMapList = new LinkedHashMap<>();

            if (locationBackList.get(i).getStatus() == 1) {
                ArrayList<State_> stateList = new ArrayList<>();
                for (int j = 0; j < locationBackList.get(i).getState().size(); j++) {
                    for (int k = 0; k < old_array.size(); k++) {
                        String locationStateId = String.valueOf(locationBackList.get(i).getState().get(j).getId());
                        if (locationStateId.equalsIgnoreCase(old_array.get(k).getId())) {
                            ArrayList<City_> cityList = new ArrayList<>();
                            State_ stateItem = new State_();
                            stateItem.setId(locationBackList.get(i).getState().get(j).getId());
                            stateItem.setName(locationBackList.get(i).getState().get(j).getName());
                            stateItem.setStatus(old_array.get(k).getStatus());
                            if (old_array.get(k).getSelected()) {
                                stateItem.setIs_selected(true);
                            } else {
                                stateItem.setIs_selected(false);
                            }

                            for (int l = 0; l < old_array.get(k).getCityList().size(); l++) {
                                City_ cityItem = new City_();
                                ListItem listItemCity = old_array.get(k).getCityList().get(l);

                                cityItem.setStatus(listItemCity.getStatus());
                                cityItem.setName(listItemCity.getName());
                                cityItem.setId(Long.valueOf(listItemCity.getId()));
                                if (listItemCity.getSelected()) {
                                    cityItem.setIs_selected(true);
                                } else {
                                    cityItem.setIs_selected(false);
                                }

                                cityList.add(cityItem);
                            }
                            stateItem.setCity(cityList);
                            stateList.add(stateItem);
                        }
                    }
                }

                if (stateList.size() == 0) {
                    stateList = new ArrayList<>();
                    State_ stateItem = new State_();
                    stateItem.setId(0);
                    stateItem.setName(null);
                    stateItem.setStatus(2);
                    stateItem.setIs_selected(false);
                    stateItem.setCity(null);
                    stateList.add(stateItem);
                }
                stateHashMapList.put(String.valueOf(locationBackList.get(i).getId()), stateList);
                stateArrayList.add(stateHashMapList);
            }
        }
        return stateArrayList;
    }

    private ArrayList<ListItem> reverseStateList(ArrayList<LinkedHashMap<String, ArrayList<State_>>> old_array) {
        ArrayList<ListItem> stateList = new ArrayList<>();

        if (old_array != null) {
            for (int i = 0; i < old_array.size(); i++) {
                LinkedHashMap<String, ArrayList<State_>> stateLinkedHashMap = old_array.get(i);

                for (String key : stateLinkedHashMap.keySet()) {
                    for (int j = 0; j < stateLinkedHashMap.get(key).size(); j++) {
                        if (stateLinkedHashMap.get(key).get(j).getStatus() == 1) {
                            ArrayList<ListItem> cityList = new ArrayList<>();

                            ListItem listItem = new ListItem();
                            listItem.setStatus(stateLinkedHashMap.get(key).get(j).getStatus());
                            listItem.setName(stateLinkedHashMap.get(key).get(j).getName());
                            listItem.setId(String.valueOf(stateLinkedHashMap.get(key).get(j).getId()));
                            if (stateLinkedHashMap.get(key).get(j).isSelected()) {
                                listItem.setSelected(true);
                            } else {
                                listItem.setSelected(false);
                            }

                            for (int k = 0; k < stateLinkedHashMap.get(key).get(j).getCity().size(); k++) {
                                ListItem listItemCity = new ListItem();
                                City_ cityItem = stateLinkedHashMap.get(key).get(j).getCity().get(k);
                                listItemCity.setStatus(cityItem.getStatus());
                                listItemCity.setName(cityItem.getName());
                                listItemCity.setId(String.valueOf(cityItem.getId()));
                                if (cityItem.isSelected()) {
                                    listItemCity.setSelected(true);
                                } else {
                                    listItemCity.setSelected(false);
                                }

                                cityList.add(listItemCity);
                            }

                            listItem.setCityList(cityList);

                            stateList.add(listItem);
                        }
                    }
                }
            }
        }
        return stateList;
    }

    private void setListView() {
        warningText.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        frameLayoutLocation.setVisibility(View.GONE);
        SimpleMultiSelectAdapter simpleMultiSelectAdapter = new SimpleMultiSelectAdapter(getActivity(), choiceList);
        listView.setAdapter(simpleMultiSelectAdapter);

    }

    private void setExpandableListView() {
        warningText.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        frameLayoutLocation.setVisibility(View.VISIBLE);

        FilterCountryExpandableListAdapter threeLevelListAdapterAdapter = new FilterCountryExpandableListAdapter(activity.getApplicationContext(), choiceList, stateBackArrayList);
        // set adapter
        expandableListView.setAdapter(threeLevelListAdapterAdapter);
    }

    private void loadNextLocations(int offset) {
        getLocations();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        assert dialog.getWindow() != null;
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.multi_select_list_dialog_back:
                isChoice = false;
                dismiss();
                break;
            case R.id.multi_select_list_dialog_submit:
                isChoice = true;
                dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!isChoice) {
            if (action.equalsIgnoreCase(Actions_.GET_LOCATIONS)) {
                getChoice.GetSelected(reverseLocationList(locationBackList), locationBackList,
                        convertStateList(stateChoiceList), stateChoiceList, false, action);
            } else {
                getChoice.GetSelected(reverseList(backList), locationBackList,
                        stateBackArrayList, stateChoiceList, false, action);
            }
        } else {
            if (action.equalsIgnoreCase(Actions_.GET_LOCATIONS)) {
                getChoice.GetSelected(choiceList, convertLocationList(choiceList), stateBackArrayList, reverseStateList(stateBackArrayList),
                        true, action);
            } else {
                getChoice.GetSelected(choiceList, locationBackList, stateBackArrayList, reverseStateList(stateBackArrayList),
                        true, action);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        relativeLayoutToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    public static void setExpandedGroup() {
        if (expandableListView != null
                && expandableListView.getExpandableListAdapter() != null) {
            final int groupCount = expandableListView.getExpandableListAdapter().getGroupCount();
            for (int i = 0; i < groupCount; i++) {
                if (expandableListView.isGroupExpanded(i)) {
                    idsExpand.add(i);
                }
            }
        }
    }

    public static void getExpandedIds() {
        if (idsExpand != null) {
            final int groupCount = idsExpand.size();
            for (int i = 0; i < groupCount; i++) {
                if (expandableListView != null) {
                    try {
                        expandableListView.expandGroup(idsExpand.get(i));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}