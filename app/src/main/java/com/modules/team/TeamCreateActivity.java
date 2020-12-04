package com.modules.team;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.modules.wall.Attachment_;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.FileUpload;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetSelected;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.library.PathUtils;
import com.sagesurfer.library.UriUtils;
import com.sagesurfer.network.MakeCall;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.TeamTypeList_;
import com.sagesurfer.snack.ShowLoader;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class TeamCreateActivity extends AppCompatActivity {
    private static final String TAG = TeamCreateActivity.class.getSimpleName();
    private AppCompatImageView postButton;
    private TextView teamDescLimitTxt, URLAvailable;
    private EditText teamName, teamUrl, teamDescription;
    private CheckBox teamTeamsAndConditionCheckBox;
    private RadioButton publicPrivacy, privatePrivacy, openAcess, closeAccess, mPremiumPrivacy, mPlatformPrivarcy;
    private ImageView uploadTeamImg, uploadIcon, defaultUploadIcon, closeImg, defaultCloseImg;
    private String teamNameValue = "", teamUrlValue = "", teamDescValue = "", teamTextClientValue = "", teamTextCategoryValue = "", teamTextSubCategoryValue = "";
    private Boolean publicPrivacyState = false, privatePrivacyState = false, openAcessState = false, closeAccessState = false, premiumPrivacyState = false, platformPrivacyState = false;
    private String publicPrivacyValue, privatePrivacyValue, openAccessValue, closeAccessValue;
    private int premiumPrivacyValue, platformPrivacyValue;
    private ArrayList<Attachment_> attachmentArrayList;
    private RelativeLayout uploadBannerLayoutHide, uploadBannerLayoutShow, defaultBannerLayout;
    private PopupWindow popupWindow = new PopupWindow();

    private TextView mTxtSubCategory, mTxtCategory, mTxtClient;

    LinearLayout linearLayoutTeamTypeSpinner, mLinearLayoutFac, mLinearLayoutAccess, mLinearLayoutPrivate, mLinearLayoutPublic, mLinearLayoutPublicOnly, mLinearLayoutPremiumPlatform;
    Spinner spinnerTeamType;
    private ArrayList<TeamTypeModel> clientArrayList = new ArrayList<>();
    private ArrayList<TeamTypeModel> categoryArrayList = new ArrayList<>();
    private ArrayList<TeamTypeModel> subCategoryArrayList = new ArrayList<>();
    private int MODE_CLIENT = 1;
    private int MODE_CATEGORY = 2;
    private int MODE_SUB_CATEGORY = 3;

    private int CLIENT_REQUEST_CODE = 11;
    private int CATEGORY_REQUEST_CODE = 12;
    private int SUB_CATEGORY_REQUEST_CODE = 13;

    private static final String KEY_ID = "key_id";
    private static final String KEY_VALUE = "key_value";

    private String name;
    private int selectedClientId = 0, selectedCategoryId = 0, selectedSubCategoryId = 0;
    private int nTeamType = 0;
    private Boolean isPopUpCloseManually = false;
    private SparseIntArray mErrorString;

    @SuppressLint({"RestrictedApi", "NewApi", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_team_create);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_cancel_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mErrorString = new SparseIntArray();

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setPadding(100, 0, 0, 0);
        titleText.setText(getResources().getString(R.string.create_team));

        postButton = (AppCompatImageView) findViewById(R.id.imageview_toolbar_save);
        postButton.setVisibility(View.VISIBLE);

        attachmentArrayList = new ArrayList<>();

        initUI();

        setClickListeners();
        getTeamClientData();
        getTeamCategoryData();
    }

    private void initUI() {
        teamDescLimitTxt = (TextView) findViewById(R.id.team_desc_limit);
        URLAvailable = (TextView) findViewById(R.id.url_available_or_not);

        teamName = (EditText) findViewById(R.id.team_name_txt);
        teamUrl = (EditText) findViewById(R.id.team_url_txt);
//        mTxtTeamType = (TextView) findViewById(R.id.team_type_txt);
        teamUrl.addTextChangedListener(teamUrlTextEditorWatcher);

        teamDescription = (EditText) findViewById(R.id.describe_story__txt);
        teamDescription.addTextChangedListener(mTextEditorWatcher);

        publicPrivacy = findViewById(R.id.radiobutton_public);
        privatePrivacy = findViewById(R.id.radiobutton_private);

        mPremiumPrivacy = findViewById(R.id.radiobutton_premium);
        mPlatformPrivarcy = findViewById(R.id.radiobutton_platform);

        openAcess = findViewById(R.id.radiobutton_open);
        closeAccess = findViewById(R.id.radiobutton_closed);

        uploadIcon = findViewById(R.id.vi_heart_img_one);
        uploadTeamImg = findViewById(R.id.set_upload_image);
        defaultUploadIcon = findViewById(R.id.set_default_upload_image);
        closeImg = findViewById(R.id.close_img);
        defaultCloseImg = findViewById(R.id.defualt_close_img);

        uploadBannerLayoutHide = findViewById(R.id.hide_banner_layout);
        uploadBannerLayoutShow = findViewById(R.id.show_banner_layout);
        defaultBannerLayout = findViewById(R.id.show_default_banner_layout);

        spinnerTeamType = findViewById(R.id.spinner_team_type);
        linearLayoutTeamTypeSpinner = findViewById(R.id.linearlayout_teamtype_spinner);
        mLinearLayoutFac = findViewById(R.id.main_lineary_layout_fac);
        mLinearLayoutAccess = findViewById(R.id.linear_layout_access);
        mLinearLayoutPrivate = findViewById(R.id.linear_private);
        mLinearLayoutPublic = findViewById(R.id.linear_public);
        mLinearLayoutPublicOnly = findViewById(R.id.linear_public_only);
        mLinearLayoutPremiumPlatform = findViewById(R.id.linear_premium_platform);

        mTxtSubCategory = findViewById(R.id.sub_category_name_txt);
        mTxtCategory = findViewById(R.id.category_name_txt);
        mTxtClient = findViewById(R.id.client_name_txt);

        teamTeamsAndConditionCheckBox = (CheckBox) findViewById(R.id.create_team_checkBox);

        String[] mTeamType = new String[]{};
        if (isLoginUserCanCreateFacilitatedTeam() && isLoginUserCanCreateNonFacilitatedTeam()) {
            mTeamType = new String[]{"Facilitated", "Non Facilitated"};
        } else if (isLoginUserCanCreateFacilitatedTeam() && !isLoginUserCanCreateNonFacilitatedTeam()) {
            mTeamType = new String[]{"Facilitated"};
        } else if (!isLoginUserCanCreateFacilitatedTeam() && isLoginUserCanCreateNonFacilitatedTeam()) {
            mTeamType = new String[]{"Non Facilitated"};
        }

        ArrayAdapter<String> adapterTeamType = new ArrayAdapter<String>(this, R.layout.drop_down_selected_text_item_layout, mTeamType);
        adapterTeamType.setDropDownViewResource(R.layout.drop_down_text_item_layout);
        spinnerTeamType.setAdapter(adapterTeamType);

//            spinnerTeamType.setOnItemSelectedListener(onTeamSelected);

        spinnerTeamType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerTeamType.setSelection(position);

                Boolean isFaciliatedTeamSelected = true;

                if (isLoginUserCanCreateFacilitatedTeam() && isLoginUserCanCreateNonFacilitatedTeam()) {

                    if (spinnerTeamType.getSelectedItemPosition() == 0) {

                        isFaciliatedTeamSelected = true;
                    } else {
                        isFaciliatedTeamSelected = false;
                    }

                } else if (isLoginUserCanCreateFacilitatedTeam() && !isLoginUserCanCreateNonFacilitatedTeam()) {

                    isFaciliatedTeamSelected = true;
                } else if (!isLoginUserCanCreateFacilitatedTeam() && isLoginUserCanCreateNonFacilitatedTeam()) {

                    isFaciliatedTeamSelected = false;
                }

                if (isFaciliatedTeamSelected) {
                    nTeamType = 1;
                    mLinearLayoutFac.setVisibility(View.VISIBLE);
                    mLinearLayoutAccess.setVisibility(View.GONE);
                    mLinearLayoutPrivate.setVisibility(View.GONE);
                    mLinearLayoutPublic.setVisibility(View.GONE);
                    mLinearLayoutPublicOnly.setVisibility(View.GONE);
                    mLinearLayoutPremiumPlatform.setVisibility(View.VISIBLE);
                } else {
                    nTeamType = 2;
                    mLinearLayoutFac.setVisibility(View.GONE);
                    mLinearLayoutAccess.setVisibility(View.VISIBLE);
                    mLinearLayoutPremiumPlatform.setVisibility(View.GONE);

                    if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
                        mLinearLayoutPublic.setVisibility(View.VISIBLE);
                        mLinearLayoutPublicOnly.setVisibility(View.GONE);
                        // mLinearLayoutPrivate.setVisibility(View.VISIBLE);
                    } else {
                        mLinearLayoutPublicOnly.setVisibility(View.VISIBLE);
                        mLinearLayoutPublic.setVisibility(View.GONE);
                        mLinearLayoutPrivate.setVisibility(View.GONE);
                    }
                }

                Log.e("SelectedTeamType", "" + nTeamType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTeamType.setSelection(0);

        setDefaultBanner();
    }


    /*This Function is for create facilitated team */
    private Boolean isLoginUserCanCreateFacilitatedTeam() {

        Boolean hasPermission = false;

        if ((Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) && Preferences.get(General.ROLE_ID).equals(General.UserRoleMHAW_SystemAdministrator)) {
            hasPermission = true;
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) && Preferences.get(General.ROLE_ID).equals(General.UserRoleTarzana_CashManager)) {
            hasPermission = true;
        }


        return hasPermission;
    }

    /*This Function is for create non-facilitated team */
    private Boolean isLoginUserCanCreateNonFacilitatedTeam() {

        Boolean hasPermission = true;

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024)) && Preferences.get(General.ROLE_ID).equals(General.UserRoleTarzana_CashManager)) {
            hasPermission = false;
        }


        return hasPermission;
    }

    private void setDefaultBanner() {

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage008))) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.team_banner_trial)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(defaultUploadIcon);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage023))) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.team_banner_werhope)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(defaultUploadIcon);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage024))) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.team_banner_tarzana)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(defaultUploadIcon);
        } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage026)) || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage027))) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.team_banner_mhaw)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(defaultUploadIcon);
        } else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.team_banner_werhope)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(defaultUploadIcon);
        }

        defaultCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadBannerLayoutHide.setVisibility(View.VISIBLE);
                defaultBannerLayout.setVisibility(View.GONE);
                uploadBannerLayoutShow.setVisibility(View.GONE);
                ShowToast.toast("Removed", TeamCreateActivity.this);
            }
        });
    }

    private final TextWatcher teamUrlTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            teamUrlValue = teamUrl.getText().toString().trim();
            checkedGroupUrlPresentOrNot(teamUrlValue);
        }
    };

    private void checkedGroupUrlPresentOrNot(String teamUrl) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.CHECK_URL);
        requestMap.put(General.GROUP_URL, teamUrl);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.CHECK_URL);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        URLAvailable.setText("Team URL :" + jsonAddJournal.get("url").getAsString());
                        URLAvailable.setTextColor(getResources().getColor(R.color.text_color_tertiary));
                    } else {
                        URLAvailable.setText("Sorry Team URL " + teamUrl + " not available");
                        URLAvailable.setTextColor(getResources().getColor(R.color.self_goal_red));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            teamDescLimitTxt.setText(String.valueOf(s.length()) + "/265");
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private void setClickListeners() {
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamNameValue = teamName.getText().toString().trim();
                teamUrlValue = teamUrl.getText().toString().trim();
                teamDescValue = teamDescription.getText().toString().trim();
                teamTextClientValue = mTxtClient.getText().toString().trim();
                teamTextCategoryValue = mTxtCategory.getText().toString().trim();
                teamTextSubCategoryValue = mTxtSubCategory.getText().toString().trim();


                if (nTeamType == 1) {
                    premiumPrivacyState = mPremiumPrivacy.isChecked();
                    platformPrivacyState = mPlatformPrivarcy.isChecked();
                } else {
                    publicPrivacyState = publicPrivacy.isChecked();
                    privatePrivacyState = privatePrivacy.isChecked();

                }

                openAcessState = openAcess.isChecked();
                closeAccessState = closeAccess.isChecked();


                if (publicPrivacyState) {
                    publicPrivacyValue = "1";
                } else if (privatePrivacyState) {
                    privatePrivacyValue = "2";
                } else if (premiumPrivacyState) {
                    premiumPrivacyValue = 3;
                } else if (platformPrivacyState) {
                    platformPrivacyValue = 4;
                }

                if (openAcessState) {
                    openAccessValue = "open";
                } else if (closeAccessState) {
                    closeAccessValue = "closed";
                }

                if (CreateTeamValidation(teamNameValue, teamUrlValue, teamDescValue, teamTextClientValue, teamTextCategoryValue, teamTextSubCategoryValue, v)) {
                    createNewTeamAPI(teamNameValue, teamUrlValue, teamDescValue);
                }
            }
        });

        uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(TeamCreateActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

//                Intent journalIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                journalIntent.setType("*/*");
//                startActivityForResult(journalIntent, Chat.INTENT_FILE);
            }
        });

        closeImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                new RemoveAttachment(0).execute();
            }
        });

        teamTeamsAndConditionCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsAndConditionPopUp(v);
            }
        });

        mTxtClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clientArrayList.get(0).getStatus() == 1) {

                    /*we have to pass clientArraylist, Mode and selectClientId to other activity*/
                    /*mode is define for understanding ArrayList Because we have to pass three to four
                        ArrayList into same activity*/

                    Intent intent = new Intent(TeamCreateActivity.this, TeamClentCategoryActivity.class);
                    intent.putExtra("Mode", MODE_CLIENT);
                    intent.putExtra("selectedClientId", selectedClientId);
                    intent.putExtra("clientArrayList", clientArrayList);
                    startActivityForResult(intent, CLIENT_REQUEST_CODE);
                } else {
                    Toast.makeText(TeamCreateActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mTxtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*we have to pass CategoryArrayList, Mode and selectCategoryId to other activity*/
                    /*mode is define for understanding ArrayList Because we have to pass three to four
                        ArrayList into same activity*/

                Intent intent = new Intent(TeamCreateActivity.this, TeamClentCategoryActivity.class);
                intent.putExtra("Mode", MODE_CATEGORY);
                intent.putExtra("selectedCategoryId", selectedCategoryId);
                intent.putExtra("categoryArrayList", categoryArrayList);
                startActivityForResult(intent, CATEGORY_REQUEST_CODE);
            }
        });

        mTxtSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*we have to pass SubCategoryArrayList, Mode and selectSubCategoryId to other activity*/
                    /*mode is define for understanding ArrayList Because we have to pass three to four
                        ArrayList into same activity*/

                Intent intent = new Intent(TeamCreateActivity.this, TeamClentCategoryActivity.class);
                intent.putExtra("Mode", MODE_SUB_CATEGORY);
                intent.putExtra("selectedSubCategoryId", selectedSubCategoryId);
                intent.putExtra("subCategoryArrayList", subCategoryArrayList);
                startActivityForResult(intent, SUB_CATEGORY_REQUEST_CODE);
            }
        });

    }

    private void showTermsAndConditionPopUp(View v) {
        isPopUpCloseManually = false;
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View customView = inflater.inflate(R.layout.werhope_create_team_dialog, null);
            popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            popupWindow.isShowing();
            teamTeamsAndConditionCheckBox.setChecked(false);
            /*popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                }
            });*/

            WebView web = (WebView) customView.findViewById(R.id.webview);
            web.loadUrl("file:///android_asset/werhope_terms_and_condition.html");

            Button close = customView.findViewById(R.id.closed_btn);
            Button accept = customView.findViewById(R.id.accept_btn);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPopUpCloseManually = true;
                    teamTeamsAndConditionCheckBox.setChecked(false);
                    popupWindow.dismiss();
                }
            });

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPopUpCloseManually = true;
                    teamTeamsAndConditionCheckBox.setChecked(true);
                    popupWindow.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Chat.INTENT_FILE:
                    //String file_path = PathUtils.getFilePath(getApplicationContext(), data.getData());

                    String file_path = UriUtils.getPathFromUri(getApplicationContext(), data.getData());


                    if (file_path == null || file_path.trim().length() <= 0) {
                        Toast.makeText(this, this.getResources().getString(R.string.valid_file_error), Toast.LENGTH_LONG).show();
                        return;
                    }
                    double size = FileOperations.getSizeMB(file_path);
                    if (size > 10) {
                        Toast.makeText(this, this.getResources().getString(R.string.max_10_mb_allowed), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (CheckFileType.isDocument(file_path) || CheckFileType.xlsFile(file_path)
                            || CheckFileType.pdfFile(file_path) || CheckFileType.imageFile(file_path)) {
                        new UploadFile().execute(file_path);
                    } else {
                        ShowToast.toast("Please Select Valid File", getApplicationContext());
                    }
                    break;
            }
        }
        /*Call back data from other activity and differentiate According to there resultCode and
           Set name for client , category and sub category */
        else if (resultCode == CLIENT_REQUEST_CODE) {
            name = data.getStringExtra(KEY_VALUE);
            selectedClientId = data.getIntExtra(KEY_ID, 0);
            mTxtClient.setText(name);
        } else if (resultCode == CATEGORY_REQUEST_CODE) {
            name = data.getStringExtra(KEY_VALUE);
            selectedCategoryId = data.getIntExtra(KEY_ID, 0);
            mTxtCategory.setText(name);
            getTeamSubCategoryData(String.valueOf(selectedCategoryId));
        } else if (resultCode == SUB_CATEGORY_REQUEST_CODE) {
            name = data.getStringExtra(KEY_VALUE);
            selectedSubCategoryId = data.getIntExtra(KEY_ID, 0);
            mTxtSubCategory.setText(name);
        }
    }

    // make background call to upload file/attachment
    @SuppressLint({"StaticFieldLeak", "NewApi"})
    private class UploadFile extends AsyncTask<String, Void, Integer> {
        ShowLoader showLoader;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader = new ShowLoader();
            showLoader.showUploadDialog(TeamCreateActivity.this);
        }

        @Override
        protected Integer doInBackground(String... params) {
            int status = 12;
            String path = params[0];
            String file_name = FileOperations.getFileName(params[0]);
            String url = Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER;
            String user_id = Preferences.get(General.USER_ID);
            try {
                String response = FileUpload.uploadFile(params[0], file_name, user_id, url,
                        Actions_.FMS, getApplicationContext(), TeamCreateActivity.this);
                if (response != null) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                    if (jsonObject.has(Actions_.FMS)) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray(Actions_.FMS);
                        if (jsonArray != null) {
                            JsonObject object = jsonArray.get(0).getAsJsonObject();
                            if (object.has(General.STATUS)) {
                                status = object.get(General.STATUS).getAsInt();
                                if (status == 1) {
                                    Attachment_ attachment_ = new Attachment_();
                                    attachment_.setId(object.get(General.ID).getAsInt());
                                    attachment_.setPath(path);
                                    attachment_.setImage(file_name);
                                    attachment_.setSize(FileOperations.getSize(path));
                                    attachment_.setPost(true);
                                    attachmentArrayList.add(attachment_);
                                }
                            } else {
                                status = 11;
                            }
                        } else {
                            status = 11;
                        }
                    }
                } else {
                    status = 11;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (showLoader != null)
                showLoader.dismissUploadDialog();
            switch (result) {
                case 1:
                    ShowToast.toast(getApplicationContext().getResources().
                            getString(R.string.upload_successful), getApplicationContext());
                    setUploadTeamFile();
                    break;
                case 2:
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.failed), getApplicationContext());
                    break;
                case 11:
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.internal_error_occurred), getApplicationContext());
                    break;
                case 12:
                    ShowToast.toast(getApplicationContext().getResources()
                            .getString(R.string.network_error_occurred), getApplicationContext());
                    break;
                default:
                    break;
            }
        }
    }

    // background call to remove uploaded file
    @SuppressLint({"StaticFieldLeak", "NewApi"})
    private class RemoveAttachment extends AsyncTask<Void, Void, Void> {
        final int position;

        RemoveAttachment(int position) {
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... params) {
            RequestBody getBody = new FormBody.Builder()
                    .add(General.ACTION, Actions_.DELETE)
                    .add(General.USER_ID, Preferences.get(General.USER_ID))
                    .add(General.ID, GetSelected.wallAttachments(attachmentArrayList))
                    .build();
            try {
                MakeCall.postNormal(Preferences.get(General.DOMAIN).replaceAll(General.INSATNCE_NAME, "") + Urls_.MOBILE_UPLOADER, getBody, TAG, TeamCreateActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            removeAttachmentId(position);
            ShowToast.toast("Removed", TeamCreateActivity.this);
        }
    }

    @SuppressLint("SetTextI18n")
    private void removeAttachmentId(int position) {
        if (attachmentArrayList.size() > 0) {
            attachmentArrayList.remove(position);
            setUploadTeamFile();
        }
    }

    private void setUploadTeamFile() {
        if (attachmentArrayList.size() > 0) {
            setAttachmentImage(attachmentArrayList.get(0).getPath(), uploadTeamImg);
            uploadBannerLayoutHide.setVisibility(View.GONE);
            uploadBannerLayoutShow.setVisibility(View.VISIBLE);
        } else {
            uploadBannerLayoutHide.setVisibility(View.VISIBLE);
            uploadBannerLayoutShow.setVisibility(View.GONE);
        }
    }

    // set attachment file thumbnail/icon based on file type
    private void setAttachmentImage(String path, ImageView attachmentImage) {
        if (CheckFileType.imageFile(path)) {
            Glide.with(getApplicationContext())
                    .load(path)
                    .transition(withCrossFade())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) //.RESULT
                    .into(attachmentImage);
        } else {
            attachmentImage.setImageResource(GetThumbnails.attachmentList(path));
        }
    }

    private void createNewTeamAPI(String teamName, String teamUrl, String teamDesc) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.CREATE_TEAM);
        requestMap.put(General.NAME, teamName);
        requestMap.put(General.TEAM_TYPE, String.valueOf(nTeamType));
        requestMap.put(General.GROUP_URL, teamUrl);

        if (publicPrivacyState) {
            requestMap.put(General.PRIVACY, publicPrivacyValue);
        } else if (privatePrivacyState) {
            requestMap.put(General.PRIVACY, privatePrivacyValue);
        } else if (premiumPrivacyState) {
            requestMap.put(General.PRIVACY, String.valueOf(premiumPrivacyValue));
        } else if (platformPrivacyState) {
            requestMap.put(General.PRIVACY, String.valueOf(platformPrivacyValue));
        }

        if (openAcessState) {
            requestMap.put(General.ACCESS, openAccessValue);
        } else {
            requestMap.put(General.ACCESS, closeAccessValue);
        }

        requestMap.put(General.YOUTH_ID, String.valueOf(selectedClientId));
        requestMap.put(General.CATEGORY_ID, String.valueOf(selectedCategoryId));
        requestMap.put(General.SUB_CATEGORY_ID, String.valueOf(selectedSubCategoryId));

        requestMap.put(General.LOG_ID, GetSelected.wallAttachments(attachmentArrayList));
        requestMap.put(General.DESCRIPTION, teamDesc);
        requestMap.put(General.ANONYMOUS, "2");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, getApplicationContext(), this);

        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, getApplicationContext(), this);
                if (response != null) {
                    JsonObject jsonObject = GetJson_.getJson(response);
                    JsonObject jsonAddJournal = jsonObject.getAsJsonObject(Actions_.CREATE_TEAM);
                    if (jsonAddJournal.get(General.STATUS).getAsInt() == 1) {
                        Toast.makeText(TeamCreateActivity.this, jsonAddJournal.get(General.MSG).getAsString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(TeamCreateActivity.this, jsonAddJournal.get(General.ERROR).getAsString(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorName", e.getMessage());
            }
        }
    }

    private boolean CreateTeamValidation(String teamName, String teamUrl, String teamDescription, String teamTextClient, String teamTextCategory, String teamTextSubCategory, View view) {

        if (teamName.length() < 4 || teamName.length() > 50) {
            ShowSnack.viewWarning(view, "Team Name: Min 4 Char Required", getApplicationContext());
            return false;
        }

       /* if (teamUrl.length() < 5 || teamUrl.length() > 50) {
            ShowSnack.viewWarning(view, "Enter valid Team URl", getApplicationContext());
            return false;
        }*/

        if (teamDescription == null || teamDescription.trim().length() <= 0) {
            ShowSnack.viewWarning(view, "Story Description: Min 4 Char Required", getApplicationContext());
            return false;
        }

        if (!teamTeamsAndConditionCheckBox.isChecked()) {
            ShowSnack.viewWarning(view, "Please accept the term and condition.", getApplicationContext());
            return false;
        }

        if (nTeamType == 1) {
            if (teamTextClient == null || teamTextClient.trim().length() <= 0) {
                ShowSnack.viewWarning(view, "Please select client", getApplicationContext());
                return false;
            }

            if (teamTextCategory == null || teamTextCategory.trim().length() <= 0) {
                ShowSnack.viewWarning(view, "Please select category", getApplicationContext());
                return false;
            }

            if (teamTextSubCategory == null || teamTextSubCategory.trim().length() <= 0) {
                ShowSnack.viewWarning(view, "Please select subcategory", getApplicationContext());
                return false;
            }
        }


        return true;
    }

    private void getTeamClientData() {
        String action = Actions_.GET_TEAM_CREATE_CLIENT_MHAW;
        HashMap<String, String> requestMap = new HashMap<>();

        clientArrayList.clear();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.GROUP_ID, String.valueOf(0));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    clientArrayList = TeamTypeList_.parseTeamTypeClientList(response, action, this, TAG);
                    if (clientArrayList.size() > 0) {
                        if (clientArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
                            Log.e("ResponseClient", response);
                        } else if (clientArrayList.get(0).getStatus() == 2) {
//                            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
                            Log.e("ErrorClientListOne", "" + clientArrayList.get(0).getStatus());
                        }
                    } else {

                        Log.e("ErrorClientListTwo", "" + clientArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    private void getTeamCategoryData() {
        String action = Actions_.GET_CATEGORY_MHAW;
        HashMap<String, String> requestMap = new HashMap<>();

        categoryArrayList.clear();
        requestMap.put(General.ACTION, action);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    categoryArrayList = TeamTypeList_.parseTeamTypeClientList(response, action, this, TAG);
                    if (categoryArrayList.size() > 0) {
                        if (categoryArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
//                            getTeamSubCategoryData(String.valueOf(clientArrayList.get(0).getId()));
                            Log.e("ResponseCategory", response);

                        } else {

                            Log.e("ErrorClientListOne", "" + categoryArrayList.get(0).getStatus());
                        }
                    } else {

                        Log.e("ErrorClientListTwo", "" + categoryArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    private void getTeamSubCategoryData(String ID) {
        String action = Actions_.GET_SUB_CATEGORY_MHAW;
        HashMap<String, String> requestMap = new HashMap<>();

        subCategoryArrayList.clear();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.CAT_ID, ID);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_TEAM_OPERATIONS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    subCategoryArrayList = TeamTypeList_.parseTeamTypeClientList(response, action, this, TAG);
                    if (subCategoryArrayList.size() > 0) {
                        if (subCategoryArrayList.get(0).getStatus() == 1) {
//                            showError(false, 1);
                            Log.e("ResponseSubCategory", response);

                        } else {
                            Log.e("ErrorClientListOne", "" + subCategoryArrayList.get(0).getStatus());
                        }
                    } else {
                        Log.e("ErrorClientListTwo", "" + subCategoryArrayList.get(0).getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ErrorAppoResuClient", e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    Intent journalIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    journalIntent.setType("*/*");
                    startActivityForResult(journalIntent, Chat.INTENT_FILE);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(findViewById(android.R.id.content), mErrorString.get(requestCode),
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
