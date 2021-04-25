package com.modules.support;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.views.CircularReveal;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/3/2018
 * Last Modified on 4/3/2018
 */

public class FaqActivity extends AppCompatActivity implements FaqListAdapter.FaqInterface {

    private static final String TAG = FaqActivity.class.getSimpleName();

    @BindView(R.id.textview_support_faq_six_tag)
    TextView textViewSupportFaqSixTag;
    @BindView(R.id.expandablelistview_support_faq)
    ExpandableListView expandableListViewSupportFaq;
    @BindView(R.id.textview_support_faq_warning)
    TextView textViewSupportFaqWarning;
    @BindView(R.id.button_support_faq_view_more)
    Button buttonSupportFaqViewMore;

    private int lastExpandedPosition = -1;
    private ArrayList<Question> questionArrayList;

    private Toolbar searchToolbar;
    private Menu search_menu;
    private MenuItem item_search;

    private FaqListAdapter faqListAdapter;

    Toolbar toolbar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        questionArrayList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        setSearchToolbar();
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView textViewActivityToolbarTitle = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        textViewActivityToolbarTitle.setText(this.getResources().getString(R.string.faq));

        AppCompatImageButton imageButtonActivityToolbarSearch = (AppCompatImageButton) findViewById(R.id.imagebutton_activitytoolbar_send);
        imageButtonActivityToolbarSearch.setVisibility(View.VISIBLE);
        imageButtonActivityToolbarSearch.setImageResource(R.drawable.vi_search_white);

        textViewSupportFaqWarning.setVisibility(View.GONE);
        textViewSupportFaqSixTag.setVisibility(View.VISIBLE);

        expandableListViewSupportFaq.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expandableListViewSupportFaq.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        setList(6);
    }

    // set search view toolbar with appropriate circular revel animation
    private void setSearchToolbar() {
        searchToolbar = (Toolbar) findViewById(R.id.searchtoolbar);
        searchToolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        if (searchToolbar != null) {
            searchToolbar.inflateMenu(R.menu.menu_search);
            search_menu = searchToolbar.getMenu();

            searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        CircularReveal.circleReveal(R.id.searchtoolbar, 1, true, false, FaqActivity.this);
                    else
                        searchToolbar.setVisibility(View.GONE);
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);

            MenuItemCompat.setOnActionExpandListener(item_search, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        CircularReveal.circleReveal(R.id.searchtoolbar, 1, true, false, FaqActivity.this);
                    } else
                        searchToolbar.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });
            initSearchView();
        }
    }

    // set search view toolbar with it's actions
    private void initSearchView() {
        final SearchView searchView = (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Enable/Disable Submit button in the keyboard
        searchView.setSubmitButtonEnabled(false);
        // Change search close button image
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.vi_cancel_white);

        // set hint and the text colors
        EditText txtSearch = ((EditText) searchView.findViewById(R.id.search_src_text));
        txtSearch.setHint("Search...");
        txtSearch.setHintTextColor(getResources().getColor(R.color.white));
        txtSearch.setTextColor(getResources().getColor(R.color.white));
        txtSearch.setBackgroundColor(this.getResources().getColor(R.color.transparent));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            private void callSearch(String query) {
                if (faqListAdapter != null) {
                    faqListAdapter.filterList(query);
                }
            }
        });
    }

    // assign list to list adapters
    private void setList(int size) {
        questionArrayList = SetStandardGroups(size);
        faqListAdapter = new FaqListAdapter(this, questionArrayList, this);
        expandableListViewSupportFaq.setAdapter(faqListAdapter);
    }

    // create fine list for collapsible view with parent and child
    private ArrayList<Question> SetStandardGroups(int size) {
        ArrayList<Question> questionArrayList = new ArrayList<>();
        ArrayList<String> quesList = FaqList.questionList();
        ArrayList<String> ansList = FaqList.ansList();
        ArrayList<Answer> ch_list;

        int j = 0;
        for (String group_name : quesList) {
            if (j < size) {
                Question gru = new Question();
                gru.setName(group_name);

                ch_list = new ArrayList<>();

                Answer ch = new Answer();
                ch.setName(ansList.get(j));
                ch_list.add(ch);
                gru.setItems(ch_list);
                questionArrayList.add(gru);
                j++;
            }
        }
        return questionArrayList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0,0);
        finish();
    }

    @OnClick({R.id.button_support_faq_view_more, R.id.imagebutton_activitytoolbar_send})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.button_support_faq_view_more:
                textViewSupportFaqSixTag.setText(this.getResources().getString(R.string.all_question));
                noData(false);
                buttonSupportFaqViewMore.setVisibility(View.GONE);
                setList(FaqList.questionList().size());
                break;

            case R.id.imagebutton_activitytoolbar_send:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    CircularReveal.circleReveal(R.id.searchtoolbar, 1, true, true, FaqActivity.this);
                } else {
                    searchToolbar.setVisibility(View.VISIBLE);
                }
                item_search.expandActionView();
                break;
        }
    }

    @Override
    public void noData(boolean isData) {
        if (isData) {
            textViewSupportFaqWarning.setVisibility(View.VISIBLE);
            expandableListViewSupportFaq.setVisibility(View.GONE);
        } else {
            textViewSupportFaqWarning.setVisibility(View.GONE);
            expandableListViewSupportFaq.setVisibility(View.VISIBLE);
        }
    }
}
