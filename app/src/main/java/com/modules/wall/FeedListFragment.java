package com.modules.wall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.Compare;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.library.GetFragments;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Wall_;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.views.CircleTransform;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 08-07-2017
 * Last Modified on 15-12-2017
 */

public class FeedListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener {

    private static final String TAG = FeedListFragment.class.getSimpleName();
    private ArrayList<Feed_> feedArrayList, searchFeedArrayList;

    private ListView listView;
    private ImageView profilePhoto;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private PopupWindow popupWindow = new PopupWindow();

    private FeedListAdapter feedListAdapter;
    private Activity activity;
    private MainActivityInterface mainActivityInterface;
    private FloatingActionButton createButton;

    private CardView cardViewActionsSearch;
    private EditText editTextSearch;
    private ImageButton imageButtonSetting, imageButtonFilter;

    //wall filter part
    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date = "", end_date = "";
    private String lastWeek = "", lastMonth = "";


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

        View view = inflater.inflate(R.layout.wall_feed_fragment_layout, null);
        activity = getActivity();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.wall));

        feedArrayList = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        listView = (ListView) view.findViewById(R.id.swipe_menu_listview);

        cardViewActionsSearch = (CardView) view.findViewById(R.id.cardview_actions);
        imageButtonSetting = (ImageButton) view.findViewById(R.id.imagebutton_setting);
        imageButtonFilter = (ImageButton) view.findViewById(R.id.notification_filter);
        imageButtonFilter.setVisibility(View.VISIBLE);

        feedListAdapter = new FeedListAdapter(activity, feedArrayList);
        listView.setAdapter(feedListAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //mainActivityInterface.hideRevealView();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        profilePhoto = (ImageView) view.findViewById(R.id.wall_feed_fragment_image);

        errorText = (TextView) view.findViewById(R.id.textview_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.imageview_error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.linealayout_error);

        LinearLayout postLayout = (LinearLayout) view.findViewById(R.id.wall_feed_fragment_post_layout);
        postLayout.setOnClickListener(this);


        createButton = (FloatingActionButton) view.findViewById(R.id.fab);

        createButton.setVisibility(View.VISIBLE);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attachmentIntent = new Intent(activity.getApplicationContext(), WallPostActivity.class);
                activity.startActivity(attachmentIntent);
                activity.overridePendingTransition(0, 0);
            }
        });

        setProfilePhoto();

        subSrearchFunctiaonality(view);

        feedFilterData();

        return view;
    }

    private void feedFilterData() {
        imageButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View customView = inflater.inflate(R.layout.dialog_wall_filter, null);
                    popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.isShowing();

                    final LinearLayout dateLayout, dateSelectionLayout;
                    final TextView clearDateSelection;
                    final TextView startDate, endDate;
                    final ImageView imageviewSave, imageviewBack;
                    final Calendar calendar;
                    final CheckBox lastWeekCheckBox, lastMonthCheckBox;

                    dateLayout = customView.findViewById(R.id.date_layout);
                    dateSelectionLayout = customView.findViewById(R.id.date_selection);
                    lastWeekCheckBox = customView.findViewById(R.id.check_box_week);
                    lastMonthCheckBox = customView.findViewById(R.id.check_box_month);
                    clearDateSelection = customView.findViewById(R.id.clear_selection_date);
                    imageviewSave = customView.findViewById(R.id.imageview_toolbar_save);
                    imageviewBack = customView.findViewById(R.id.imageview_back);
                    startDate = customView.findViewById(R.id.start_date_txt);
                    endDate = customView.findViewById(R.id.end_date_txt);


                    clearDateSelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lastWeekCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                            startDate.setText("");
                            endDate.setText("");
                        }
                    });


                    dateSelectionLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dateLayout.setVisibility(View.VISIBLE);
                            lastWeekCheckBox.setChecked(false);
                            lastMonthCheckBox.setChecked(false);
                        }
                    });

                    calendar = Calendar.getInstance();
                    mYear = calendar.get(Calendar.YEAR);
                    mMonth = calendar.get(Calendar.MONTH);
                    mDay = calendar.get(Calendar.DAY_OF_MONTH);


                    startDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            monthOfYear = (monthOfYear + 1);
                                            sDay = dayOfMonth;
                                            sMonth = monthOfYear;
                                            sYear = year;

                                            date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                            try {
                                                startDate.setText(date);
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


                    endDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog1 = new DatePickerDialog(activity,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            monthOfYear = (monthOfYear + 1);
                                            sDay = dayOfMonth;
                                            sMonth = monthOfYear;
                                            sYear = year;

                                            end_date = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);

                                            try {
                                                int result = Compare.validEndDate(end_date, date);
                                                if (result == 1) {
                                                    endDate.setText(end_date);
                                                } else {
                                                    end_date = null;
                                                    endDate.setText(null);
                                                    ShowSnack.textViewWarning(endDate, activity.getResources()
                                                            .getString(R.string.invalid_date), activity);

                                                }

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


                    if (Preferences.getBoolean("last_week")) {
                        lastWeekCheckBox.setChecked(true);
                    } else {
                        lastWeekCheckBox.setChecked(false);
                    }

                    lastWeekCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                lastWeek = "last_week";
                                lastMonthCheckBox.setChecked(false);
                                Preferences.save("last_week", true);
                                startDate.setText("");
                                endDate.setText("");
                            } else {
                                lastWeek = "";
                                Preferences.save("last_week", false);
                            }
                        }
                    });

                    if (Preferences.getBoolean("last_month")) {
                        lastMonthCheckBox.setChecked(true);
                    } else {
                        lastMonthCheckBox.setChecked(false);
                    }

                    lastMonthCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                            if (checked) {
                                lastMonth = "last_month";
                                lastWeekCheckBox.setChecked(false);
                                Preferences.save("last_month", true);
                                startDate.setText("");
                                endDate.setText("");
                            } else {
                                lastMonth = "";
                                Preferences.save("last_month", false);
                            }
                        }
                    });

                    imageviewSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            filterDareWiseWall();
                        }
                    });

                    imageviewBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void filterDareWiseWall() {
        feedArrayList.clear();
        showError(true, 20);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_FEED);

        if (!date.equals("") && !end_date.equals("")) {
            requestMap.put("date_type", "date");
            requestMap.put("start_date", date);
            requestMap.put("end_date", end_date);
        } else if (lastWeek.equals("last_week")) {
            requestMap.put("date_type", lastWeek);
        } else if (lastMonth.equals("last_month")) {
            requestMap.put("date_type", lastMonth);
        } else {
            requestMap.put("date_type", "0");
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.WALL_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    feedArrayList = Wall_.parseFeed(response, getActivity().getApplicationContext(), TAG);

                    if (feedArrayList.size() > 0) {
                        if (feedArrayList.get(0).getStatus() == 1) {
                            showError(false, 1);
                            feedListAdapter = new FeedListAdapter(getActivity(), feedArrayList);
                            listView.setAdapter(feedListAdapter);
                            popupWindow.dismiss();
                        } else {
                            showError(true, feedArrayList.get(0).getStatus());
                            popupWindow.dismiss();
                        }
                    } else {
                        showError(true, 2);
                        popupWindow.dismiss();
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showError(true, 11);
    }

    private void subSrearchFunctiaonality(View view) {
        cardViewActionsSearch.setVisibility(View.VISIBLE);
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
        imageButtonSetting.setVisibility(View.GONE);
        view = (View) view.findViewById(R.id.view_part);
        view.setVisibility(View.GONE);

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
        searchFeedArrayList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (Feed_ feed : feedArrayList) {
            if (feed.getName() != null && feed.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchFeedArrayList.add(feed);
            } else if (feed.getFeed() != null && feed.getFeed().toLowerCase().contains(searchText.toLowerCase())) {
                searchFeedArrayList.add(feed);
            }
        }
        if (searchFeedArrayList.size() > 0) {
            showError(false, 1);
            FeedListAdapter feedListAdapter = new FeedListAdapter(getActivity(), searchFeedArrayList);
            listView.setAdapter(feedListAdapter);
        } else {
            showError(true, 2);
        }
    }

    private void setProfilePhoto() {
        Glide.with(activity.getApplicationContext())
                .load(new File(Preferences.get(General.LOCAL_IMAGE)))
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_user_male)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(activity.getApplicationContext())))
                .into(profilePhoto);
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

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle(activity.getApplicationContext().getResources().getString(R.string.wall));
        mainActivityInterface.setToolbarBackgroundColor();
        getFeed();
    }

    // make network call to fetch all wall feed
    private void getFeed() {
        //DatabaseRetrieve_ databaseRetrieve_ = new DatabaseRetrieve_(activity.getApplicationContext());
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_FEED);
        requestMap.put(General.LAST_DATE, "0");
        //requestMap.put(General.LAST_DATE, databaseRetrieve_.retrieveUpdateLog(General.FEED));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.WALL_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    showError(false, 1);
                    feedArrayList = Wall_.parseFeed(response, getActivity().getApplicationContext(), TAG);
                    if (feedArrayList.size() <= 0) {
                        swipeRefreshLayout.setRefreshing(false);
                        showError(true, 2);
                        return;
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        feedListAdapter = new FeedListAdapter(getActivity(), feedArrayList);
                        listView.setAdapter(feedListAdapter);
                    }
                    return;
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showError(true, 12);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
        showError(true, 11);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getFeed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wall_feed_fragment_post_layout:
                Intent attachmentIntent = new Intent(activity.getApplicationContext(), WallPostActivity.class);
                activity.startActivity(attachmentIntent);
                activity.overridePendingTransition(0, 0);

                break;
        }
    }


}
