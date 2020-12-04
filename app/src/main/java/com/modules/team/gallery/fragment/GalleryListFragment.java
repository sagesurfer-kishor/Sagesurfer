package com.modules.team.gallery.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.util.TypedValue;
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
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.team.TeamDetailsActivity;
import com.modules.team.gallery.activity.CreateAlbumActivity;
import com.modules.team.gallery.adapter.ImageGalleryAdapter;
import com.modules.team.gallery.adapter.ListViewGalleryAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.interfaces.MainActivityInterface;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetErrorResources;
import com.sagesurfer.models.Gallery_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.TeamDetails_;
import com.sagesurfer.views.TextWatcherExtended;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @author Kailash Karankal
 */

public class GalleryListFragment extends Fragment {
    private static final String TAG = GalleryListFragment.class.getSimpleName();
    private View view;
    private ArrayList<Gallery_> galleryArrayList = new ArrayList<>(), gallerySearchList;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewList;
    private LinearLayout errorLayout;
    private TextView errorText;
    private AppCompatImageView errorIcon;
    private static Activity activity;
    private MainActivityInterface mainActivityInterface;
    private EditText editTextSearch;
    private CardView cardViewActionsSearch;
    private Boolean isList = false;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListViewGalleryAdapter listViewGalleryAdapter;
    private ImageView searchIcon;
    private static ImageView deleteGallery;
    private RelativeLayout countLayout;
    private TextView countAlbum, cancelTxt, listVievLabel;
    private ImageGalleryAdapter imageGalleryAdapter;
    private String msg;
    private AlertDialog alertDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<String> gallerySelectedIds = new ArrayList<String>();
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

        view = inflater.inflate(R.layout.main_gallery_layout, null);

        activity = getActivity();
        isList = true;

        Preferences.initialize(activity.getApplicationContext());

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_refresh_layout_recycler_view);
        recyclerView.setBackgroundColor(activity.getApplicationContext().getResources().getColor(R.color.screen_background));
        mLayoutManager = new GridLayoutManager(activity, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewList = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(mLinearLayoutManager);

        errorText = (TextView) view.findViewById(R.id.swipe_refresh_recycler_view_error_message);
        errorIcon = (AppCompatImageView) view.findViewById(R.id.swipe_refresh_recycler_view__error_icon);

        errorLayout = (LinearLayout) view.findViewById(R.id.swipe_refresh_recycler_view_error_layout);

        FloatingActionButton createAlbum = view.findViewById(R.id.swipe_refresh_layout_recycler_view_float);
        createAlbum.setImageResource(R.drawable.ic_add_white);

       /* if (CheckRole.showInviteMember(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
            createAlbum.setVisibility(View.GONE);
        } else {
            createAlbum.setVisibility(View.VISIBLE);
        }*/

        if (Preferences.get(General.OWNER_ID).equalsIgnoreCase(Preferences.get(General.USER_ID))
                || Preferences.get(General.IS_MODERATOR).equalsIgnoreCase("1")
                || !General.isCurruntUserHasPermissionToOnlyViewCantPerformAnyAction()) {
            createAlbum.setVisibility(View.VISIBLE);
        } else {
            createAlbum.setVisibility(View.GONE);
        }


        createAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sosIntent = new Intent(activity.getApplicationContext(), CreateAlbumActivity.class);
                startActivity(sosIntent);
                activity.overridePendingTransition(0, 0);
            }
        });

        cardViewActionsSearch = (CardView) view.findViewById(R.id.cardview_actions);
        cardViewActionsSearch.setVisibility(View.GONE);
        editTextSearch = (EditText) view.findViewById(R.id.edittext_search);
        searchIcon = (ImageView) view.findViewById(R.id.search_icon);
        deleteGallery = (ImageView) view.findViewById(R.id.delete_gallery);
        countLayout = view.findViewById(R.id.count_layout);
        countAlbum = view.findViewById(R.id.count_album_txt);
        cancelTxt = view.findViewById(R.id.cancel_txt);
        listVievLabel = view.findViewById(R.id.list_view_label);

        setClickListeners();

        searchFilterFunctionality();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        isList = true;

        if (isList) {
            recyclerViewList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            listVievLabel.setText("Grid View");
        } else {
            recyclerViewList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivityInterface.setMainTitle("Gallery");
        mainActivityInterface.setToolbarBackgroundColor();

        getGallery();

        try {
            ((TeamDetailsActivity) getActivity()).inviteButtonSetVisibility();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDeleteButton() {
        deleteGallery.setVisibility(View.VISIBLE);
    }

    public static void hideDeleteButton() {
        deleteGallery.setVisibility(View.GONE);
    }

    private void setClickListeners() {
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewActionsSearch.setVisibility(View.VISIBLE);
                countLayout.setVisibility(View.GONE);
            }
        });

        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewActionsSearch.setVisibility(View.GONE);
                countLayout.setVisibility(View.VISIBLE);
                editTextSearch.setText("");
            }
        });

        deleteGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup viewGroup = v.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(activity).inflate(R.layout.delete_gallery_popup, viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setView(dialogView);

                alertDialog = builder.create();

                final Button okBtn = dialogView.findViewById(R.id.ok_btn);
                final ImageView closeIcon = dialogView.findViewById(R.id.close_icon);

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteGalleryWebService();

                        if (isList) {
                            recyclerViewList.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerViewList.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        alertDialog.dismiss();
                    }
                });

                closeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }

    private void searchFilterFunctionality() {


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
        gallerySearchList = new ArrayList<>();
        String searchText = editTextSearch.getText().toString().trim();
        if (searchText.length() == 0) {
            editTextSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
        for (Gallery_ gallery_ : galleryArrayList) {
            if (gallery_.getName() != null) {
                if ((gallery_.getName() != null && gallery_.getName().toLowerCase().contains(searchText.toLowerCase()))) {
                    gallerySearchList.add(gallery_);
                }
            }
        }
        if (gallerySearchList.size() > 0) {
            showError(false, 1);

            if (isList) {
                ImageGalleryAdapter adapter = new ImageGalleryAdapter(activity, gallerySearchList);
                recyclerView.setAdapter(adapter);
            } else {
                ListViewGalleryAdapter listViewGalleryAdapter = new ListViewGalleryAdapter(activity, gallerySearchList);
                recyclerViewList.setAdapter(listViewGalleryAdapter);
                recyclerView.setVisibility(View.GONE);
            }

        } else {
            showError(true, 2);
        }
    }

    private void showError(boolean isError, int status) {
        if (isError) {
            errorLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            errorText.setText(GetErrorResources.getGalleryMessage(status, activity.getApplicationContext()));
            errorIcon.setImageResource(GetErrorResources.getIcon(status));
        } else {
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // make network call to fetch galley for specific team based on team id
    private void getGallery() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_GALLERY);
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.GET_GALLERY_IMAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    galleryArrayList = TeamDetails_.parseGallery(response, activity.getApplicationContext(), TAG);
                    if (galleryArrayList.size() > 0) {
                        if (galleryArrayList.get(0).getStatus() == 1) {

                            if (galleryArrayList.size() == 0) {
                                searchIcon.setVisibility(View.GONE);
                            } else {
                                searchIcon.setVisibility(View.VISIBLE);
                                countAlbum.setText("Total Albums (" + galleryArrayList.size() + ")");
                            }

                            if (isList) {
                                imageGalleryAdapter = new ImageGalleryAdapter(activity, galleryArrayList);
                                recyclerView.setAdapter(imageGalleryAdapter);
                            } else {
                                listViewGalleryAdapter = new ListViewGalleryAdapter(activity, galleryArrayList);
                                recyclerViewList.setAdapter(listViewGalleryAdapter);
                            }

                            showError(false, 1);

                        } else {
                            showError(true, galleryArrayList.get(0).getStatus());
                            countAlbum.setText("Total Albums (" + 0 + ")");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the fragment menu items.
        inflater.inflate(R.menu.gallery_menu, menu);

        final ImageView item1 = (ImageView) menu.findItem(R.id.action_gallery_view).getActionView().findViewById(R.id.list_view_img);
        item1.setBackgroundResource(R.drawable.view_list);
        listVievLabel.setText("Grid View");

        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isList) {
                    isList = false;
                    item1.setBackgroundResource(R.drawable.four_squares_list);
                    listVievLabel.setText("List View");

                    recyclerViewList.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                    listViewGalleryAdapter = new ListViewGalleryAdapter(activity, galleryArrayList);
                    //  mLayoutManager = new LinearLayoutManager(activity);
                    //  recyclerView.setLayoutManager(mLayoutManager);
                    recyclerViewList.setAdapter(listViewGalleryAdapter);
                    listViewGalleryAdapter.notifyDataSetChanged();
                } else {
                    isList = true;
                    item1.setBackgroundResource(R.drawable.view_list);
                    listVievLabel.setText("Grid View");

                    recyclerViewList.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    imageGalleryAdapter = new ImageGalleryAdapter(activity, galleryArrayList);
                    mLayoutManager = new GridLayoutManager(activity, 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(imageGalleryAdapter);
                    imageGalleryAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_gallery_view) {
        }
        return super.onOptionsItemSelected(item);
    }


    private void deleteGalleryWebService() {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GALLERY_DELETE);
        requestMap.put("ids", getIds());
        requestMap.put(General.GROUP_ID, Preferences.get(General.TEAM_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.DELETE_GALLERY_IMAGE;

        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    JsonArray jsonArray = GetJson_.getArray(response, Actions_.GALLERY_DELETE);
                    if (jsonArray != null) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Gallery_>>() {
                        }.getType();

                        galleryArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GALLERY_DELETE).toString(), listType);

                        if (galleryArrayList.size() > 0) {
                            if (galleryArrayList.get(0).getStatus() == 1) {
                                msg = galleryArrayList.get(0).getMsg();
                                getGallery();
                                hideDeleteButton();
                                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private String getIds() {
        gallerySelectedIds.clear();
        if (galleryArrayList != null && galleryArrayList.size() > 0) {
            for (int i = 0; i < galleryArrayList.size(); i++) {
                if (galleryArrayList.get(i).isSelectImgs()) {
                    gallerySelectedIds.add(String.valueOf(galleryArrayList.get(i).getId()));
                }
            }
        }

        return gallerySelectedIds.toString()
                .replace("[", "")
                .replace("]", "").trim();
    }


    // handle spacing and edge curves for card view
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    // convert dp to pixel(px)
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
