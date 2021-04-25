package com.modules.selfcare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Broadcast;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.models.Country_;
import com.sagesurfer.models.State_;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/11/2018
 * Last Modified on 4/11/2018
 */

public class SelfCareFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SelfCareFragment.class.getSimpleName();
    @BindView(R.id.relativelayout_selfcarefragment_header)
    RelativeLayout relativeLayoutSelfCareFragmentHeader;
    @BindView(R.id.textview_selfcarefragment_section)
    TextView textViewSelfCareFragmentSection;
    @BindView(R.id.imageview_selfcarefragment_sort)
    AppCompatImageView imageViewSelfCareFragmentSort;
    @BindView(R.id.imageview_selfcarefragment_filter)
    AppCompatImageView imageViewSelfCareFragmentFilter;
    @BindView(R.id.linearlayout_selfcarefragment_filter)
    LinearLayout linearLayoutSelfCareFragmentFilter;
    @BindView(R.id.textview_selfcarefragment_contenttypetag)
    TextView textViewSelfCareFragmentContentTypeTag;
    @BindView(R.id.textview_selfcarefragment_contenttype)
    TextView textViewSelfCareFragmentContentType;
    @BindView(R.id.imageview_selfcarefragment_cancel)
    AppCompatImageView imageViewSelfCareFragmentCancel;
    @BindView(R.id.framelayout_selfcarefragment_container)
    FrameLayout frameLayoutSelfCareFragmentContainer;
    private int personal = 0, like = 0, comment = 0;
    private String category = "0", language = "0", age = "0", type = "0", location = "0", state = "0", city = "0";
    private ArrayList<Choices_> categoryList, languageList, ageList, typeList;
    private ArrayList<Country_> locationList;
    private ArrayList<LinkedHashMap<String, ArrayList<State_>>> stateList;
    private BroadcastReceiver receiver;
    private Activity activity;
    private Unbinder unbinder;
    private MainActivityInterface mainActivityInterface;

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

        View rootView = inflater.inflate(R.layout.fragment_self_care, null);
        unbinder = ButterKnife.bind(this, rootView);

        activity = getActivity();
        Preferences.initialize(activity.getApplicationContext());

        VectorDrawableCompat roleDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_angle_arrow_down, textViewSelfCareFragmentSection.getContext().getTheme());
        textViewSelfCareFragmentSection.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, roleDrawable, null);

        linearLayoutSelfCareFragmentFilter.setVisibility(View.GONE);

        textViewSelfCareFragmentSection.setText(activity.getApplicationContext().getResources().getString(R.string.all_content));
        textViewSelfCareFragmentSection.setOnClickListener(this);

        imageViewSelfCareFragmentSort.setOnClickListener(this);
        imageViewSelfCareFragmentFilter.setOnClickListener(this);

        //contentList = new ArrayList<>();
        categoryList = new ArrayList<>();
        languageList = new ArrayList<>();
        ageList = new ArrayList<>();
        typeList = new ArrayList<>();
        locationList = new ArrayList<>();
        stateList = new ArrayList<>();

        activity.registerReceiver(receiver, new IntentFilter(Broadcast.NEW_FILTER));
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //errorText.setVisibility(View.GONE);
                boolean isSelected = intent.getBooleanExtra(General.IS_SELECTED, false);
                if (isSelected) {
                    imageViewSelfCareFragmentFilter.setImageResource(R.drawable.vi_filter_blue);
                    //contentList = new ArrayList<>();
                    categoryList = new ArrayList<>();
                    languageList = new ArrayList<>();
                    ageList = new ArrayList<>();
                    typeList = new ArrayList<>();
                    locationList = new ArrayList<>();
                    stateList = new ArrayList<>();

                    category = intent.getStringExtra(General.CATEGORY);
                    age = intent.getStringExtra(General.AGE);
                    language = intent.getStringExtra(General.LANGUAGE);
                    type = intent.getStringExtra(General.TYPE);
                    location = intent.getStringExtra(General.LOCATION);
                    state = intent.getStringExtra(General.STATE);
                    city = intent.getStringExtra(General.CITY);
                    languageList = (ArrayList<Choices_>) intent.getSerializableExtra("lang_list");
                    categoryList = (ArrayList<Choices_>) intent.getSerializableExtra("cat_list");
                    ageList = (ArrayList<Choices_>) intent.getSerializableExtra("age_list");
                    typeList = (ArrayList<Choices_>) intent.getSerializableExtra("type_list");
                    locationList = (ArrayList<Country_>) intent.getSerializableExtra("location_list");
                    //stateList = (ArrayList<LinkedHashMap<String, ArrayList<State_>>>) intent.getSerializableExtra("state_list");
                    String str = intent.getStringExtra("state_list");
                    Gson gson = new Gson();
                    Type entityType = new TypeToken<ArrayList<LinkedHashMap<String, ArrayList<State_>>>>() {
                    }.getType();
                    stateList = gson.fromJson(str, entityType);
                    //toggleContentSelector();
                }
                openFragment();
            }
        };

        if (Preferences.get(General.SELFCARE_ID) != null && !Preferences.get(General.SELFCARE_ID).equalsIgnoreCase("")) {
            personal = 1;
            toggleSection();
        }

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.self_care));
        mainActivityInterface.setToolbarBackgroundColor();
        try {
            activity.registerReceiver(receiver, new IntentFilter(Broadcast.NEW_FILTER));
        } catch (Exception e) {
            e.printStackTrace();
        }

        openFragment();
        /*if (contentList == null || contentList.size() <= 0 || contentList.isEmpty()) {
            openFragment();
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // change title text based on section selected
    private void toggleSection() {
        if (personal == 1) {
            textViewSelfCareFragmentSection.setText(activity.getApplicationContext().getResources().getString(R.string.personal_content));
        } else {
            textViewSelfCareFragmentSection.setText(activity.getApplicationContext().getResources().getString(R.string.all_content));
        }
    }

    // show respective filter options menu
    private void showSectionPopup(final int type, View view) {
        final PopupMenu popup = new PopupMenu(activity, view);
        if (type == 1) {
            popup.getMenuInflater().inflate(R.menu.menu_selfcare_section, popup.getMenu());
        }
        if (type == 2) {
            popup.getMenuInflater().inflate(R.menu.menu_selfcare_sort, popup.getMenu());
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuselfcaresection_personal:
                        personal = 1;
                        break;
                    case R.id.menuselfcaresection_all:
                        personal = 0;
                        break;
                    case R.id.menuselfcaresort_like:
                        like = 1;
                        comment = 0;
                        break;
                    case R.id.menuselfcaresort_comment:
                        like = 0;
                        comment = 1;
                        break;
                    case R.id.menuselfcaresort_none:
                        like = 0;
                        comment = 0;
                        break;
                }
                popup.dismiss();
                if (type == 1) {
                    toggleSection();
                }
                openFragment();
                return true;
            }
        });
        popup.show();
    }

    private void openFragment() {
        try {
            if (category.equalsIgnoreCase("0") && language.equalsIgnoreCase("0") && age.equalsIgnoreCase("0") && type.equalsIgnoreCase("0") && location.equalsIgnoreCase("0")) {
                //  imageViewSelfCareFragmentFilter.setImageResource(R.drawable.vi_filter_gray);
                imageViewSelfCareFragmentFilter.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.vi_filter_gray));

            } else {
                // imageViewSelfCareFragmentFilter.setImageResource(R.drawable.vi_filter_blue);
                imageViewSelfCareFragmentFilter.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.vi_filter_blue));
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment = getFragmentManager().findFragmentByTag(TAG);
            if (fragment != null) {
                ft.remove(fragment).commit();
            }
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            ft.replace(R.id.framelayout_selfcarefragment_container, SelfCareContentListFragment.newInstance(personal, like, comment, category, age, language, type, location, state, city), TAG);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_selfcarefragment_section:
                showSectionPopup(1, v);
                break;

            case R.id.imageview_selfcarefragment_sort:
                showSectionPopup(2, v);
                break;

            case R.id.imageview_selfcarefragment_filter:
                Bundle bundleAnimation = ActivityOptions.makeCustomAnimation
                        (activity.getApplicationContext(), R.anim.animation_one, R.anim.animation_two).toBundle();
                Intent filterIntent = new Intent(activity.getApplicationContext(), SelfCareFilterActivity.class);
                filterIntent.putExtra(General.CATEGORY, category);
                filterIntent.putExtra(General.LANGUAGE, language);
                filterIntent.putExtra(General.TYPE, type);
                filterIntent.putExtra(General.AGE, age);
                filterIntent.putExtra(General.LOCATION, location);
                filterIntent.putExtra(General.STATE, state);
                filterIntent.putExtra(General.CITY, city);
                filterIntent.putExtra("cat_list", categoryList);
                filterIntent.putExtra("lang_list", languageList);
                filterIntent.putExtra("age_list", ageList);
                filterIntent.putExtra("type_list", typeList);
                filterIntent.putExtra("location_list", locationList);
                Gson gson = new Gson();
                String stateListString = gson.toJson(stateList);
                filterIntent.putExtra("state_list", stateListString);
                startActivity(filterIntent, bundleAnimation);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final View rootView = activity.findViewById(android.R.id.content);
            if (rootView != null) {
                rootView.cancelPendingInputEvents();
            }
        }
        //outState.putString(General.PERSONAL, String.valueOf(personal));
        outState.putString(General.CATEGORY, category);
        outState.putString(General.LANGUAGE, language);
        outState.putString(General.AGE, age);
        outState.putString(General.TYPE, type);
        outState.putString(General.LOCATION, location);
        outState.putString(General.STATE, state);
        outState.putString(General.CITY, city);
        outState.putSerializable("lang_list", languageList);
        outState.putSerializable("cat_list", categoryList);
        outState.putSerializable("age_list", ageList);
        outState.putSerializable("type_list", typeList);
        outState.putSerializable("location_list", locationList);
        outState.putSerializable("state_list", stateList);
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            //personal = savedInstanceState.getString(General.PERSONAL);
            if (savedInstanceState.containsKey(General.CATEGORY)) {
                category = savedInstanceState.getString(General.CATEGORY);
                language = savedInstanceState.getString(General.LANGUAGE);
                age = savedInstanceState.getString(General.AGE);
                type = savedInstanceState.getString(General.TYPE);
                location = savedInstanceState.getString(General.LOCATION);
                state = savedInstanceState.getString(General.STATE);
                city = savedInstanceState.getString(General.CITY);
                languageList = (ArrayList<Choices_>) savedInstanceState.getSerializable("lang_list");
                categoryList = (ArrayList<Choices_>) savedInstanceState.getSerializable("cat_list");
                ageList = (ArrayList<Choices_>) savedInstanceState.getSerializable("age_list");
                typeList = (ArrayList<Choices_>) savedInstanceState.getSerializable("type_list");
                locationList = (ArrayList<Country_>) savedInstanceState.getSerializable("location_list");
                stateList = (ArrayList<LinkedHashMap<String, ArrayList<State_>>>) savedInstanceState.getSerializable("state_list");
            }
            /*if (personal.equalsIgnoreCase("1")) {
                personal = "1";
                personalCare.setImageResource(R.drawable.ic_self_care_general);
                mainActivityInterface.SetTitle(activity.getApplicationContext().getResources().getString(R.string.self_education_personal));
                fetch();
            } else {
                personal = "0";
                personalCare.setImageResource(R.drawable.ic_personal_self_care);
                mainActivityInterface.SetTitle(activity.getApplicationContext().getResources().getString(R.string.self_education));
                fetch();
            }
            toggleContentSelector();*/
        }
    }
}