package com.modules.selfcare;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Broadcast;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.ArrayOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.models.City_;
import com.sagesurfer.models.Country_;
import com.sagesurfer.models.State_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 5/28/2018
 *         Last Modified on 5/28/2018
 */

public class SelfCareFilterActivity extends AppCompatActivity implements View.OnClickListener, FilterChoiceDialogFragment.GetChoice {

    private static final String TAG = SelfCareFilterActivity.class.getSimpleName();

    private TextView clearButton;
    private ChipView languageChip, ageChip, categoryChip, typeChip, locationChip;

    private ArrayList<Choices_> categoryBackList, languageBackList, ageBackList, typeBackList;
    private ArrayList<ListItem> categoryList, languageList, ageList, typeList, locationList, stateChoiceList;
    private ArrayList<String> languageIdList, categoryIdList, ageIdList, typeIdList, locationIdList, stateIdList, cityIdList;
    private ArrayList<Country_> locationBackList;
    private ArrayList<LinkedHashMap<String, ArrayList<State_>>> stateList;
    private boolean isSelected = false, isBack = false, isToggleClear = false;

    Toolbar toolbar;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.self_care_filter_layout);

        categoryList = new ArrayList<>();
        languageList = new ArrayList<>();
        ageList = new ArrayList<>();
        typeList = new ArrayList<>();
        locationList = new ArrayList<>();
        stateList = new ArrayList<>();
        stateChoiceList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.care_filter_layout_toolbar);
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        setSupportActionBar(toolbar);

        ImageView backButton = (ImageView) findViewById(R.id.care_filter_category_add);
        backButton.setOnClickListener(this);

        ImageView categoryAdd = (ImageView) findViewById(R.id.care_filter_toolbar_back);
        categoryAdd.setOnClickListener(this);

        ImageView languageAdd = (ImageView) findViewById(R.id.care_filter_language_add);
        languageAdd.setOnClickListener(this);

        ImageView ageAdd = (ImageView) findViewById(R.id.care_filter_age_add);
        ageAdd.setOnClickListener(this);

        ImageView typeAdd = (ImageView) findViewById(R.id.care_filter_content_type_add);
        typeAdd.setOnClickListener(this);

        ImageView locationAdd = (ImageView) findViewById(R.id.care_filter_location_add);
        locationAdd.setOnClickListener(this);

        TextView applyButton = (TextView) findViewById(R.id.care_filter_toolbar_button_one);
        applyButton.setOnClickListener(this);

        clearButton = (TextView) findViewById(R.id.care_filter_toolbar_button_two);
        clearButton.setOnClickListener(this);
        toggleClear(false);

        Intent data = getIntent();
        if (data != null) {
            languageBackList = (ArrayList<Choices_>) data.getSerializableExtra("lang_list");
            languageList = reverseList(languageBackList);
            categoryBackList = (ArrayList<Choices_>) data.getSerializableExtra("cat_list");
            categoryList = reverseList(categoryBackList);
            ageBackList = (ArrayList<Choices_>) data.getSerializableExtra("age_list");
            ageList = reverseList(ageBackList);
            typeBackList = (ArrayList<Choices_>) data.getSerializableExtra("type_list");
            typeList = reverseList(typeBackList);
            locationBackList = (ArrayList<Country_>) data.getSerializableExtra("location_list");
            locationList = reverseLocationList(locationBackList);
            //stateList = (ArrayList<LinkedHashMap<String, ArrayList<State_>>>) data.getSerializableExtra("state_list");
            String str=  data.getStringExtra("state_list");
            Gson gson = new Gson();
            Type entityType = new TypeToken< ArrayList<LinkedHashMap<String, ArrayList<State_>>>>(){}.getType();
            stateList = gson.fromJson(str, entityType);
            stateChoiceList = reverseStateList(stateList);

            if (languageList != null && languageList.size() > 0) {
                setLanguageChip();
                //  pastLanguage = true;
            }
            if (categoryList != null && categoryList.size() > 0) {
                setCategoryChip();
                //pastCategory = true;
            }
            if (ageList != null && ageList.size() > 0) {
                setAgeChip();
                // pastAge = true;
            }
            if (typeList != null && typeList.size() > 0) {
                setTypeChip();
                // pastType = true;
            }
            if (locationList != null && locationList.size() > 0) {
                setLocationChip();
            }
        }
    }

    private void removeAll() {
        isSelected = true;
        if (categoryList != null && categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                categoryList.get(i).setSelected(false);
            }
            setCategoryChip();
        }

        if (languageList != null && languageList.size() > 0) {
            for (int i = 0; i < languageList.size(); i++) {
                languageList.get(i).setSelected(false);
            }
            setLanguageChip();
        }
        if (ageList != null && ageList.size() > 0) {
            for (int i = 0; i < ageList.size(); i++) {
                ageList.get(i).setSelected(false);
            }
            setAgeChip();
        }
        if (typeList != null && typeList.size() > 0) {
            for (int i = 0; i < typeList.size(); i++) {
                typeList.get(i).setSelected(false);
            }
            setTypeChip();
        }
        if (locationList != null && locationList.size() > 0) {
            for (int i = 0; i < locationList.size(); i++) {
                locationList.get(i).setSelected(false);
            }
            if (stateList != null && stateList.size() > 0) {
                for (int i = 0; i < stateList.size(); i++) {
                    LinkedHashMap<String, ArrayList<State_>> stateLinkedHashMap = stateList.get(i);

                    for (String key : stateLinkedHashMap.keySet()) {
                        for (int j = 0; j < stateLinkedHashMap.get(key).size(); j++) {
                            stateLinkedHashMap.get(key).get(j).setIs_selected(false);
                            if (stateLinkedHashMap.get(key).get(j).getCity() != null && stateLinkedHashMap.get(key).get(j).getCity().size() > 0) {
                                for (int k = 0; k < stateLinkedHashMap.get(key).get(j).getCity().size(); k++) {
                                    stateLinkedHashMap.get(key).get(j).getCity().get(k).setIs_selected(false);
                                }
                            }
                        }
                    }
                }
            }

            /*if(stateChoiceList != null && stateChoiceList.size() > 0) {
                for (int j = 0; j < stateChoiceList.size(); j++) {
                    stateChoiceList.get(j).setSelected(false);
                    if(stateChoiceList.get(j).getCityList() != null && stateChoiceList.get(j).getCityList().size() > 0) {
                        for (int k = 0; k < stateChoiceList.get(j).getCityList().size(); k++) {
                            stateChoiceList.get(j).getCityList().get(k).setSelected(false);
                        }
                    }
                }
            }*/
            //locationList = new ArrayList<>();
            //locationBackList = new ArrayList<>();
            setLocationChip();
        }
        toggleClear(false);
    }

    private void setLanguageChip() {
        List<Chip> mTagList2 = new ArrayList<>();
        for (int i = 0; i < languageList.size(); i++) {
            if (languageList.get(i).getSelected()) {
                mTagList2.add(new Tag(languageList.get(i).getName()));
                toggleClear(true);
            }
        }
        if (mTagList2.size() > 0) {
            toggleClear(true);
        } else {
            toggleClear(false);
        }
        ChipViewAdapter adapterLayout2 = new MainChipViewAdapter(this);
        languageChip = (ChipView) findViewById(R.id.care_filter_language_chip);
        languageChip.setAdapter(adapterLayout2);
        languageChip.setChipLayoutRes(R.layout.chip_close_layout);
        languageChip.setChipBackgroundColor(GetColor.getHomeIconBackgroundColorColorParse(true));
        languageChip.setChipBackgroundColorSelected(GetColor.getHomeIconBackgroundColorColorParse(true));
        languageChip.setChipList(mTagList2);
        languageChip.setOnChipClickListener(new OnChipClickListener() {
            @Override
            public void onChipClick(Chip chip) {
                languageChip.remove(chip);
                removeChoiceItem(chip.getText(), Actions_.GET_LANGUAGES);
                toggleClear(isFilterSelected());
            }
        });
    }

    private void setAgeChip() {
        List<Chip> mTagList2 = new ArrayList<>();
        for (int i = 0; i < ageList.size(); i++) {
            if (ageList.get(i).getSelected()) {
                mTagList2.add(new Tag(ageList.get(i).getName()));
            }
        }
        if (mTagList2.size() > 0) {
            toggleClear(true);
        } else {
            toggleClear(false);
        }
        ChipViewAdapter adapterLayout2 = new MainChipViewAdapter(this);
        ageChip = (ChipView) findViewById(R.id.care_filter_age_chip);
        ageChip.setAdapter(adapterLayout2);
        ageChip.setChipLayoutRes(R.layout.chip_close_layout);
        ageChip.setChipBackgroundColor(GetColor.getHomeIconBackgroundColorColorParse(false));
        ageChip.setChipBackgroundColorSelected(GetColor.getHomeIconBackgroundColorColorParse(false));
        ageChip.setChipList(mTagList2);
        ageChip.setOnChipClickListener(new OnChipClickListener() {
            @Override
            public void onChipClick(Chip chip) {
                ageChip.remove(chip);
                removeChoiceItem(chip.getText(), Actions_.GET_AGE);
                toggleClear(isFilterSelected());
            }
        });
    }

    private void setCategoryChip() {
        List<Chip> mTagList1 = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getSelected()) {
                mTagList1.add(new Tag(categoryList.get(i).getName()));
            }
        }
        if (mTagList1.size() > 0) {
            toggleClear(true);
        } else {
            toggleClear(false);
        }
        ChipViewAdapter adapterLayout = new MainChipViewAdapter(this);
        categoryChip = (ChipView) findViewById(R.id.care_filter_category_chip);
        categoryChip.setAdapter(adapterLayout);
        categoryChip.setChipLayoutRes(R.layout.chip_close_layout);
        categoryChip.setChipBackgroundColor(GetColor.getHomeIconBackgroundColorColorParse(false));
        categoryChip.setChipBackgroundColorSelected(GetColor.getHomeIconBackgroundColorColorParse(false));
        categoryChip.setChipList(mTagList1);
        categoryChip.setOnChipClickListener(new OnChipClickListener() {
            @Override
            public void onChipClick(Chip chip) {
                categoryChip.remove(chip);
                removeChoiceItem(chip.getText(), Actions_.GET_CATEGORY);
                toggleClear(isFilterSelected());
            }
        });
    }

    private void setTypeChip() {
        List<Chip> mTagList = new ArrayList<>();
        for (int i = 0; i < typeList.size(); i++) {
            if (typeList.get(i).getSelected()) {
                mTagList.add(new Tag(typeList.get(i).getName()));
            }
        }
        if (mTagList.size() > 0) {
            toggleClear(true);
        } else {
            toggleClear(false);
        }
        ChipViewAdapter adapterLayout = new MainChipViewAdapter(this);
        typeChip = (ChipView) findViewById(R.id.care_filter_content_type_chip);
        typeChip.setAdapter(adapterLayout);
        typeChip.setChipLayoutRes(R.layout.chip_close_layout);
        typeChip.setChipBackgroundColor(GetColor.getHomeIconBackgroundColorColorParse(false));
        typeChip.setChipBackgroundColorSelected(GetColor.getHomeIconBackgroundColorColorParse(false));
        typeChip.setChipList(mTagList);
        typeChip.setOnChipClickListener(new OnChipClickListener() {
            @Override
            public void onChipClick(Chip chip) {
                typeChip.remove(chip);
                removeChoiceItem(chip.getText(), Actions_.GET_TYPE);
                toggleClear(isFilterSelected());
            }
        });
    }

    private void setLocationChip() {
        List<Chip> mTagList = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getSelected()) {
                String stateName = "";
                LinkedHashMap<String, ArrayList<State_>> stateLinkedHashMap = stateList.get(i);

                for (String key : stateLinkedHashMap.keySet()) {
                    String tempStateName = "";
                    for (int j = 0; j < stateLinkedHashMap.get(key).size(); j++) {

                        String tempCityName = "";
                        if (stateLinkedHashMap.get(key).get(j).isSelected()) {
                            if(tempStateName.equalsIgnoreCase("")) {
                                tempStateName = "- " + stateLinkedHashMap.get(key).get(j).getName();
                            } else {
                                tempStateName = ", " + stateLinkedHashMap.get(key).get(j).getName();
                            }
                            for(int k = 0; k < stateLinkedHashMap.get(key).get(j).getCity().size(); k++) {
                                if (stateLinkedHashMap.get(key).get(j).getCity().get(k).isSelected()) {
                                    if(tempCityName.equalsIgnoreCase("")) {
                                        tempCityName = ": " + stateLinkedHashMap.get(key).get(j).getCity().get(k).getName();
                                    } else {
                                        tempCityName += ", " + stateLinkedHashMap.get(key).get(j).getCity().get(k).getName();
                                    }
                                }
                            }
                            stateName += tempStateName + "" + tempCityName;
                        }
                    }
                }

                mTagList.add(new Tag("[" + locationList.get(i).getName() + "] " + stateName));
            }
        }
        if (mTagList.size() > 0) {
            toggleClear(true);
        } else {
            toggleClear(false);
        }
        ChipViewAdapter adapterLayout = new MainChipViewAdapter(this);
        locationChip = (ChipView) findViewById(R.id.care_filter_location_chip);
        locationChip.setAdapter(adapterLayout);
        locationChip.setChipLayoutRes(R.layout.chip_close_layout);
        locationChip.setChipBackgroundColor(GetColor.getHomeIconBackgroundColorColorParse(false));
        locationChip.setChipBackgroundColorSelected(GetColor.getHomeIconBackgroundColorColorParse(false));
        locationChip.setChipList(mTagList);
        locationChip.setOnChipClickListener(new OnChipClickListener() {
            @Override
            public void onChipClick(Chip chip) {
                locationChip.remove(chip);
                removeChoiceItem(chip.getText(), Actions_.GET_LOCATIONS);
                toggleClear(isFilterSelected());
            }
        });
    }

    @SuppressLint("CommitTransaction")
    private void openChoiceDialog(String action) {
        @SuppressLint("CommitTransaction") FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment frag = getFragmentManager().findFragmentByTag(General.FILTER);
        if (frag != null) {
            fragmentTransaction.remove(frag);
        }
        fragmentTransaction.addToBackStack(null);
        FilterChoiceDialogFragment dialogFrag = new FilterChoiceDialogFragment();
        Bundle bundle = new Bundle();
        if (action.equals(Actions_.GET_CATEGORY)) {
            bundle.putSerializable(General.ACTION, action);
            bundle.putSerializable(General.TEAM_LIST, convertList(categoryList));
            bundle.putSerializable(General.FILTER, categoryList);
        }
        if (action.equals(Actions_.GET_LANGUAGES)) {
            bundle.putSerializable(General.ACTION, action);
            bundle.putSerializable(General.TEAM_LIST, convertList(languageList));
            bundle.putSerializable(General.FILTER, languageList);
        }
        if (action.equals(Actions_.GET_AGE)) {
            bundle.putSerializable(General.ACTION, action);
            bundle.putSerializable(General.TEAM_LIST, convertList(ageList));
            bundle.putSerializable(General.FILTER, ageList);
        }

        if (action.equals(Actions_.GET_TYPE)) {
            bundle.putSerializable(General.ACTION, action);
            bundle.putSerializable(General.TEAM_LIST, convertList(typeList));
            bundle.putSerializable(General.FILTER, typeList);
        }

        if (action.equals(Actions_.GET_LOCATIONS)) {
            bundle.putSerializable(General.ACTION, action);
            //bundle.putSerializable(General.TEAM_LIST, convertList(locationList));
            bundle.putSerializable(General.LOCATIONLIST, locationBackList);
            bundle.putSerializable(General.STATEBACKLIST, stateList);
            bundle.putSerializable(General.STATECHOICELIST, stateChoiceList);
            bundle.putSerializable(General.FILTER, locationList);
        }
        dialogFrag.setArguments(bundle);
        dialogFrag.show(getFragmentManager().beginTransaction(), General.FILTER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.care_filter_toolbar_back:
                isBack = true;
                getId();
                onBackPressed();
                break;
            case R.id.care_filter_category_add:
                openChoiceDialog(Actions_.GET_CATEGORY);
                break;
            case R.id.care_filter_language_add:
                openChoiceDialog(Actions_.GET_LANGUAGES);
                break;
            case R.id.care_filter_age_add:
                openChoiceDialog(Actions_.GET_AGE);
                break;
            case R.id.care_filter_content_type_add:
                openChoiceDialog(Actions_.GET_TYPE);
                break;
            case R.id.care_filter_location_add:
                openChoiceDialog(Actions_.GET_LOCATIONS);
                break;
            case R.id.care_filter_toolbar_button_one:
                isBack = false;
                getId();
                onBackPressed();
                break;
            case R.id.care_filter_toolbar_button_two:
                if (categoryIdList != null) {
                    categoryIdList.clear();
                }
                if (languageIdList != null) {
                    languageIdList.clear();
                }
                if (ageIdList != null) {
                    ageIdList.clear();
                }
                if (typeIdList != null) {
                    typeIdList.clear();
                }
                if (locationIdList != null) {
                    locationIdList.clear();
                }
                if (stateIdList != null) {
                    stateIdList.clear();
                }
                if (cityIdList != null) {
                    cityIdList.clear();
                }

                //remove state and city list
                //stateList = new ArrayList<>();
                //stateChoiceList = new ArrayList<>();
                removeAll();
                isBack = false;
                getId();
                onBackPressed();
                break;
        }
    }

    private void getId() {
        categoryIdList = new ArrayList<>();
        languageIdList = new ArrayList<>();
        ageIdList = new ArrayList<>();
        typeIdList = new ArrayList<>();
        locationIdList = new ArrayList<>();
        stateIdList = new ArrayList<>();
        cityIdList = new ArrayList<>();
        if (isBack) {
            categoryList = reverseList(categoryBackList);
            languageList = reverseList(languageBackList);
            typeList = reverseList(typeBackList);
            ageList = reverseList(ageBackList);
            locationList = reverseLocationList(locationBackList);
            stateList = convertStateList(stateChoiceList);
        }
        if (categoryList != null && categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getSelected()) {
                    categoryIdList.add(categoryList.get(i).getId());
                }
            }
        }
        if (languageList != null && languageList.size() > 0) {
            for (int i = 0; i < languageList.size(); i++) {
                if (languageList.get(i).getSelected()) {
                    languageIdList.add(languageList.get(i).getId());
                }
            }
        }
        if (ageList != null && ageList.size() > 0) {
            for (int i = 0; i < ageList.size(); i++) {
                if (ageList.get(i).getSelected()) {
                    ageIdList.add(ageList.get(i).getId());
                }
            }
        }
        if (typeList != null && typeList.size() > 0) {
            for (int i = 0; i < typeList.size(); i++) {
                if (typeList.get(i).getSelected()) {
                    typeIdList.add(typeList.get(i).getId());
                }
            }
        }
        if (locationList != null && locationList.size() > 0) {
            for (int i = 0; i < locationList.size(); i++) {
                if (locationList.get(i).getSelected()) {
                    locationIdList.add(locationList.get(i).getId());
                }
            }

            if (stateList != null && stateList.size() > 0) {
                for (int i = 0; i < stateList.size(); i++) {
                    LinkedHashMap<String, ArrayList<State_>> stateLinkedHashMap = stateList.get(i);

                    for (String key : stateLinkedHashMap.keySet()) {
                        for (int j = 0; j < stateLinkedHashMap.get(key).size(); j++) {
                            if (stateLinkedHashMap.get(key).get(j).isSelected()) {
                                stateIdList.add(String.valueOf(stateLinkedHashMap.get(key).get(j).getId()));
                                for(int k = 0; k < stateLinkedHashMap.get(key).get(j).getCity().size(); k++) {
                                    if (stateLinkedHashMap.get(key).get(j).getCity().get(k).isSelected()) {
                                        cityIdList.add(String.valueOf(stateLinkedHashMap.get(key).get(j).getCity().get(k).getId()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
       /* if (pastCategory) {
            if (categoryList != null && categoryList.size() > 0) {
                for (int i = 0; i < categoryList.size(); i++) {
                    if (categoryList.get(i).getSelected()) {
                        categoryIdList.add(categoryList.get(i).getId());
                    }
                }
            }
        } else {
            if (categoryList != null && categoryList.size() > 0) {
                for (int i = 0; i < categoryList.size(); i++) {
                    categoryList.get(i).setSelected(false);
                }
            }
        }
        //Log.e(TAG, "getId() categoryList: " + categoryList);
        if (pastLanguage) {
            if (languageList != null && languageList.size() > 0) {
                for (int i = 0; i < languageList.size(); i++) {
                    if (languageList.get(i).getSelected()) {
                        languageIdList.add(languageList.get(i).getId());
                    }
                }
            }
        } else {
            if (languageList != null && languageList.size() > 0) {
                for (int i = 0; i < languageList.size(); i++) {
                    languageList.get(i).setSelected(false);
                }
            }
        }
        if (pastAge) {
            if (ageList != null && ageList.size() > 0) {
                for (int i = 0; i < ageList.size(); i++) {
                    if (ageList.get(i).getSelected()) {
                        ageIdList.add(ageList.get(i).getId());
                    }
                }
            }
        } else {
            if (ageList != null && ageList.size() > 0) {
                for (int i = 0; i < ageList.size(); i++) {
                    ageList.get(i).setSelected(false);
                }
            }
        }

        if (pastType) {
            if (typeList != null && typeList.size() > 0) {
                for (int i = 0; i < typeList.size(); i++) {
                    if (typeList.get(i).getSelected()) {
                        typeIdList.add(typeList.get(i).getId());
                    }
                }
            }
        } else {
            if (typeList != null && typeList.size() > 0) {
                for (int i = 0; i < typeList.size(); i++) {
                    typeList.get(i).setSelected(false);
                }
            }
        }*/
        if (categoryIdList.size() > 0 || languageIdList.size() > 0 || ageIdList.size() > 0 || typeIdList.size() > 0 || locationIdList.size() > 0) {
            isSelected = true;
        }
    }

    private boolean isFilterSelected() {
        if (categoryList != null && categoryList.size() > 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getSelected()) {
                    return true;
                }
            }
        }
        if (languageList != null && languageList.size() > 0) {
            for (int i = 0; i < languageList.size(); i++) {
                if (languageList.get(i).getSelected()) {
                    return true;
                }
            }
        }
        if (ageList != null && ageList.size() > 0) {
            for (int i = 0; i < ageList.size(); i++) {
                if (ageList.get(i).getSelected()) {
                    return true;
                }
            }
        }
        if (typeList != null && typeList.size() > 0) {
            for (int i = 0; i < typeList.size(); i++) {
                if (typeList.get(i).getSelected()) {
                    return true;
                }
            }
        }
        if (locationList != null && locationList.size() > 0) {
            for (int i = 0; i < locationList.size(); i++) {
                if (locationList.get(i).getSelected()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void toggleClear(boolean isOn) {
        if (!isOn && !isToggleClear) {
            clearButton.setEnabled(false);
            clearButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        } else {
            isToggleClear = true;
            clearButton.setEnabled(true);
            clearButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        }
    }

    private void sendBroadcast() {
        String cat = "0", lang = "0", age = "0", type = "0", country = "0", state = "0", city = "0";
        if (categoryIdList != null && categoryIdList.size() > 0) {
            cat = ArrayOperations.stringListToString(categoryIdList);
        }
        if (languageIdList != null && languageIdList.size() > 0) {
            lang = ArrayOperations.stringListToString(languageIdList);
        }
        if (ageIdList != null && ageIdList.size() > 0) {
            age = ArrayOperations.stringListToString(ageIdList);
        }
        if (typeIdList != null && typeIdList.size() > 0) {
            type = ArrayOperations.stringListToString(typeIdList);
        }
        if (locationIdList != null && locationIdList.size() > 0) {
            country = ArrayOperations.stringListToString(locationIdList);
        }
        if (stateIdList != null && stateIdList.size() > 0) {
            state = ArrayOperations.stringListToString(stateIdList);
        }
        if (cityIdList != null && cityIdList.size() > 0) {
            city = ArrayOperations.stringListToString(cityIdList);
        }
        Intent intent = new Intent();
        intent.setAction(Broadcast.NEW_FILTER);
        intent.putExtra(General.CATEGORY, cat);
        intent.putExtra(General.LANGUAGE, lang);
        intent.putExtra(General.AGE, age);
        intent.putExtra(General.TYPE, type);
        intent.putExtra(General.LOCATION, country);
        intent.putExtra(General.STATE, state);
        intent.putExtra(General.CITY, city);
        intent.putExtra(General.IS_SELECTED, isSelected);

        Gson gson = new Gson();
        String stateListString = gson.toJson(stateList);
        if (isBack) {
            intent.putExtra("cat_list", categoryBackList);
            intent.putExtra("lang_list", languageBackList);
            intent.putExtra("age_list", ageBackList);
            intent.putExtra("type_list", typeBackList);
            intent.putExtra("location_list", locationBackList);
            intent.putExtra("state_list", stateListString);
        } else {
            intent.putExtra("cat_list", convertList(categoryList));
            intent.putExtra("lang_list", convertList(languageList));
            intent.putExtra("age_list", convertList(ageList));
            intent.putExtra("type_list", convertList(typeList));
            intent.putExtra("location_list", convertLocationList(locationList));
            intent.putExtra("state_list", stateListString);
        }
        sendBroadcast(intent);
    }

    private ArrayList<Choices_> convertList(ArrayList<ListItem> old_array) {
        ArrayList<Choices_> choicesList = new ArrayList<>();
        for (int i = 0; i < old_array.size(); i++) {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(1);
            choices_.setId(Long.valueOf(old_array.get(i).getId()));
            choices_.setName(old_array.get(i).getName());
            if (old_array.get(i).getSelected()) {
                choices_.setIs_selected("1");
            } else {
                choices_.setIs_selected("0");
            }
            choicesList.add(choices_);
        }
        return choicesList;
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

    private ArrayList<Country_> setLocationIsSelected(ArrayList<Country_> old_array) {
        ArrayList<Country_> locationArrayList = new ArrayList<>();
        for (int i = 0; i < old_array.size(); i++) {
            Country_ country_ = new Country_();
            country_.setStatus(1);
            country_.setId(Long.valueOf(old_array.get(i).getId()));
            country_.setName(old_array.get(i).getName());
            country_.setState(old_array.get(i).getState());
            country_.setLast_country(old_array.get(i).getLast_country());
            if (locationList.get(i).getSelected()) { //setting is_selected from locationList of FilterChoiceDialogFragment
                country_.setIs_selected(true);
            } else {
                country_.setIs_selected(false);
            }
            locationArrayList.add(country_);
        }
        return locationArrayList;
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
        for (int i = 0; i < old_array.size(); i++) {
            ListItem listItem = new ListItem();
            listItem.setName(old_array.get(i).getName());
            listItem.setId(String.valueOf(old_array.get(i).getId()));
            if (old_array.get(i).isSelected()) {
                listItem.setSelected(true);
            } else {
                listItem.setSelected(false);
            }
            list.add(listItem);
        }
        return list;
    }

    private ArrayList<ListItem> reverseStateList(ArrayList<LinkedHashMap<String, ArrayList<State_>>> old_array) {
        ArrayList<ListItem> stateList = new ArrayList<>();

        if(old_array != null) {
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

    private ArrayList<LinkedHashMap<String, ArrayList<State_>>> convertStateList(ArrayList<ListItem> old_array) {
        ArrayList<LinkedHashMap<String, ArrayList<State_>>> stateArrayList = new ArrayList<>();
        LinkedHashMap<String, ArrayList<State_>> stateHashMapList = new LinkedHashMap<>();

        for (int i = 0; i < locationBackList.size(); i++) {
            stateHashMapList = new LinkedHashMap<>();

            if(locationBackList.get(i).getStatus() == 1) {
                ArrayList<State_> stateList = new ArrayList<>();
                for(int j = 0; j < locationBackList.get(i).getState().size(); j++) {
                    for(int k = 0; k < old_array.size(); k++) {
                        String locationStateId = String.valueOf(locationBackList.get(i).getState().get(j).getId());
                        if(locationStateId.equalsIgnoreCase(old_array.get(k).getId())) {
                            ArrayList<City_> cityList = new ArrayList<>();
                            State_ stateItem = new State_();
                            stateItem.setId(locationBackList.get(i).getState().get(j).getId());
                            stateItem.setName(locationBackList.get(i).getState().get(j).getName());
                            stateItem.setStatus(old_array.get(k).getStatus());
                            if(old_array.get(k).getSelected()) {
                                stateItem.setIs_selected(true);
                            } else {
                                stateItem.setIs_selected(false);
                            }

                            for(int l = 0; l < old_array.get(k).getCityList().size(); l++) {
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

                if(stateList.size() == 0) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendBroadcast();
        finish();
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
    }

    private void removeChoiceItem(String chipText, String action) {
        if (action.equals(Actions_.GET_CATEGORY)) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getName().equals(chipText)) {
                    categoryList.get(i).setSelected(false);
                    isSelected = true;
                }
            }
        }
        if (action.equals(Actions_.GET_LANGUAGES)) {
            for (int i = 0; i < languageList.size(); i++) {
                if (languageList.get(i).getName().equals(chipText)) {
                    languageList.get(i).setSelected(false);
                    isSelected = true;
                }
            }
        }
        if (action.equals(Actions_.GET_AGE)) {
            for (int i = 0; i < ageList.size(); i++) {
                if (ageList.get(i).getName().equals(chipText)) {
                    ageList.get(i).setSelected(false);
                    isSelected = true;
                }
            }
        }
        if (action.equals(Actions_.GET_TYPE)) {
            for (int i = 0; i < typeList.size(); i++) {
                if (typeList.get(i).getName().equals(chipText)) {
                    typeList.get(i).setSelected(false);
                    isSelected = true;
                }
            }
        }
        if (action.equals(Actions_.GET_LOCATIONS)) {
            for (int i = 0; i < locationList.size(); i++) {
                String countryName = chipText.substring(chipText.indexOf("[") + 1, chipText.indexOf("]"));
                if (locationList.get(i).getName().equals(countryName)) {
                    locationList.get(i).setSelected(false);
                    isSelected = true;

                    LinkedHashMap<String, ArrayList<State_>> stateLinkedHashMap = stateList.get(i);

                    for (String key : stateLinkedHashMap.keySet()) {
                        for (int j = 0; j < stateLinkedHashMap.get(key).size(); j++) {
                            if (stateLinkedHashMap.get(key).get(j).isSelected()) {
                                stateLinkedHashMap.get(key).get(j).setIs_selected(false);
                                for(int k = 0; k < stateLinkedHashMap.get(key).get(j).getCity().size(); k++) {
                                    if (stateLinkedHashMap.get(key).get(j).getCity().get(k).isSelected()) {
                                        stateLinkedHashMap.get(key).get(j).getCity().get(k).setIs_selected(false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Actions_.GET_CATEGORY, categoryList);
        outState.putSerializable(Actions_.GET_LANGUAGES, languageList);
        outState.putSerializable(Actions_.GET_AGE, ageList);
        outState.putSerializable(Actions_.GET_TYPE, typeList);
        outState.putSerializable(Actions_.GET_LOCATIONS, locationList);

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Actions_.GET_CATEGORY)) {
                categoryList = (ArrayList<ListItem>) savedInstanceState.getSerializable(Actions_.GET_CATEGORY);
                setCategoryChip();
            }
            if (savedInstanceState.containsKey(Actions_.GET_LANGUAGES)) {
                languageList = (ArrayList<ListItem>) savedInstanceState.getSerializable(Actions_.GET_LANGUAGES);
                setLanguageChip();
            }
            if (savedInstanceState.containsKey(Actions_.GET_AGE)) {
                ageList = (ArrayList<ListItem>) savedInstanceState.getSerializable(Actions_.GET_AGE);
                setAgeChip();
            }
            if (savedInstanceState.containsKey(Actions_.GET_TYPE)) {
                typeList = (ArrayList<ListItem>) savedInstanceState.getSerializable(Actions_.GET_TYPE);
                setTypeChip();
            }
            if (savedInstanceState.containsKey(Actions_.GET_LOCATIONS)) {
                locationList = (ArrayList<ListItem>) savedInstanceState.getSerializable(Actions_.GET_LOCATIONS);
                setLocationChip();
            }
        }
    }

    @Override
    public void GetSelected(ArrayList<ListItem> selectionList, ArrayList<Country_> locationBackList_,
                            ArrayList<LinkedHashMap<String, ArrayList<State_>>> stateArrayList, ArrayList<ListItem> stateChoiceList_,
                            boolean isSelect, String action) {
        isSelected = isSelect;
        if (action.equals(Actions_.GET_LANGUAGES)) {
            languageList = selectionList;
            setLanguageChip();
        }
        if (action.equals(Actions_.GET_CATEGORY)) {
            categoryList = selectionList;
            setCategoryChip();
        }
        if (action.equals(Actions_.GET_AGE)) {
            ageList = selectionList;
            setAgeChip();
        }
        if (action.equals(Actions_.GET_TYPE)) {
            typeList = selectionList;
            setTypeChip();
        }
        if (action.equals(Actions_.GET_LOCATIONS)) {
            locationList = selectionList;
            locationBackList = locationBackList_;
            stateList = stateArrayList;
            stateChoiceList = stateChoiceList_;
            setLocationChip();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }
}
