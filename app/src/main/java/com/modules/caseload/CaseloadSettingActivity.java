package com.modules.caseload;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.selfcare.ListItem;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.snack.ShowToast;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monika on 7/4/2018.
 */

public class CaseloadSettingActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = CaseloadSettingActivity.class.getSimpleName();

    @BindView(R.id.textview_events)
    TextView textViewEvents;
    @BindView(R.id.linearlayout_events_checkbox)
    LinearLayout linearLayoutEvents;
    @BindView(R.id.checkbox_events)
    CheckBox checkBoxEvents;
    @BindView(R.id.textview_plan)
    TextView textViewPlan;
    @BindView(R.id.linearlayout_plan_checkbox)
    LinearLayout linearLayoutPlan;
    @BindView(R.id.checkbox_plan)
    CheckBox checkBoxPlan;
    @BindView(R.id.textview_mood)
    TextView textViewMood;
    @BindView(R.id.linearlayout_mood_checkbox)
    LinearLayout linearLayoutMood;
    @BindView(R.id.checkbox_mood)
    CheckBox checkBoxMood;
    @BindView(R.id.textview_adherence)
    TextView textViewAdherence;
    @BindView(R.id.linearlayout_adherence_checkbox)
    LinearLayout linearLayoutAdherences;
    @BindView(R.id.checkbox_adherence)
    CheckBox checkBoxAdherence;
    @BindView(R.id.textview_progress_note)
    TextView textViewProgressNote;
    @BindView(R.id.linearlayout_progress_note_checkbox)
    LinearLayout linearLayoutProgressNote;
    @BindView(R.id.checkbox_progress_note)
    CheckBox checkBoxProgressNote;
    @BindView(R.id.textview_tasklist)
    TextView textViewTasklist;
    @BindView(R.id.linearlayout_tasklist_checkbox)
    LinearLayout linearLayoutTasklist;
    @BindView(R.id.checkbox_tasklist)
    CheckBox checkBoxTasklist;
    @BindView(R.id.textview_status)
    TextView textViewStatus;
    @BindView(R.id.linearlayout_status_checkbox)
    LinearLayout linearLayoutStatus;
    @BindView(R.id.checkbox_status)
    CheckBox checkBoxStatus;

    Toolbar toolbar;
    //private GetChoice getChoice;
    private ArrayList<ListItem> settingArrayList = new ArrayList<>();

   /* interface GetChoice {
        void GetSelected(ArrayList<ListItem> settingArrayList);
    }*/

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_caseload_setting);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.caseload_setting_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(getResources().getString(R.string.sage015))){
            textViewProgressNote.setText(getResources().getString(R.string.note));
        } else {
            textViewProgressNote.setText(getResources().getString(R.string.progress_note));
        }

        linearLayoutEvents.setOnClickListener(this);
        linearLayoutPlan.setOnClickListener(this);
        linearLayoutMood.setOnClickListener(this);
        linearLayoutAdherences.setOnClickListener(this);
        linearLayoutProgressNote.setOnClickListener(this);
        linearLayoutTasklist.setOnClickListener(this);
        linearLayoutStatus.setOnClickListener(this);
        checkBoxEvents.setOnClickListener(this);
        checkBoxPlan.setOnClickListener(this);
        checkBoxMood.setOnClickListener(this);
        checkBoxAdherence.setOnClickListener(this);
        checkBoxProgressNote.setOnClickListener(this);
        checkBoxTasklist.setOnClickListener(this);
        checkBoxStatus.setOnClickListener(this);

        //getChoice = (GetChoice) this;

        /*String[] textViews = {"textViewEvents", "textViewPlan"};
        for(int i = 0; i < 6; i++) {
            ListItem settingListItem = new ListItem();
            settingListItem.setId( "" + i);
            //settingListItem.setName("Events");
            settingListItem.setSelected(false);
            settingArrayList.add(settingListItem);
        }*/
        /*ListItem settingListItem = new ListItem();
        settingListItem.setId("1");
        settingListItem.setName("Events");
        settingListItem.setSelected(false);
        settingArrayList.add(settingListItem);*/
        String settingListString = Preferences.get(General.SETTING_LIST);
        Gson gson = new Gson();
        Type entityType = new TypeToken<ArrayList<ListItem>>(){}.getType();
        settingArrayList = gson.fromJson(settingListString, entityType);

        for(int i = 0; i < settingArrayList.size(); i++) {
            if(settingArrayList.get(i).getSelected()) {
                if(i == 0) {
                    checkBoxEvents.setChecked(true);
                } else if(i == 1) {
                    checkBoxPlan.setChecked(true);
                } else if(i == 2) {
                    checkBoxTasklist.setChecked(true);
                } else if(i == 3) {
                    checkBoxAdherence.setChecked(true);
                } else if(i == 4) {
                    checkBoxProgressNote.setChecked(true);
                } else if(i == 5) {
                    checkBoxMood.setChecked(true);
                } else if(i == 6) {
                    checkBoxStatus.setChecked(true);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //getChoice.GetSelected(settingArrayList);
        if(countSelectedItems() < 2) {
            ShowToast.toast("Please select 2 items", getApplicationContext());
        } else {
            super.onBackPressed();
            this.overridePendingTransition(0,0);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearlayout_events_checkbox:
                setCheckbox(checkBoxEvents, 0);
               /* if(checkBoxEvents.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxEvents.setChecked(true);
                        settingArrayList.get(0).setSelected(true);

                        Gson gson = new Gson();
                        String settingListString = gson.toJson(settingArrayList);
                        Preferences.save(General.SETTING_LIST, settingListString);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxEvents.setChecked(false);
                    settingArrayList.get(0).setSelected(false);
                }*/
                break;

            case R.id.checkbox_events:
                setCheckbox(checkBoxEvents, 0);
                /*if(checkBoxEvents.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxEvents.setChecked(true);
                        settingArrayList.get(0).setSelected(true);

                        Gson gson = new Gson();
                        String settingListString = gson.toJson(settingArrayList);
                        Preferences.save(General.SETTING_LIST, settingListString);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxEvents.setChecked(false);
                    settingArrayList.get(0).setSelected(false);
                }*/
                break;

            case R.id.linearlayout_plan_checkbox:
                setCheckbox(checkBoxPlan, 1);
                /*if(checkBoxPlan.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxPlan.setChecked(true);
                        settingArrayList.get(1).setSelected(true);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxPlan.setChecked(false);
                    settingArrayList.get(1).setSelected(false);
                }*/
                break;

            case R.id.checkbox_plan:
                setCheckbox(checkBoxPlan, 1);
                /*if(checkBoxPlan.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxPlan.setChecked(true);
                        settingArrayList.get(2).setSelected(true);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxPlan.setChecked(false);
                    settingArrayList.get(2).setSelected(false);
                }*/
                break;

            case R.id.linearlayout_tasklist_checkbox:
                setCheckbox(checkBoxTasklist, 2);
                /*if(checkBoxTasklist.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxTasklist.setChecked(true);
                        settingArrayList.get(5).setSelected(true);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxTasklist.setChecked(false);
                    settingArrayList.get(5).setSelected(false);
                }*/
                break;

            case R.id.checkbox_tasklist:
                setCheckbox(checkBoxTasklist, 2);
                /*if(checkBoxTasklist.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxTasklist.setChecked(true);
                        settingArrayList.get(5).setSelected(true);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxTasklist.setChecked(false);
                    settingArrayList.get(5).setSelected(false);
                }*/
                break;

            case R.id.linearlayout_adherence_checkbox:
                setCheckbox(checkBoxAdherence, 3);
                /*if(checkBoxAssessments.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxAssessments.setChecked(true);
                        settingArrayList.get(3).setSelected(true);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxAssessments.setChecked(false);
                    settingArrayList.get(3).setSelected(false);
                }*/
                break;

            case R.id.checkbox_adherence:
                setCheckbox(checkBoxAdherence, 3);
                /*if(checkBoxAssessments.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxAssessments.setChecked(true);
                        settingArrayList.get(3).setSelected(true);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxAssessments.setChecked(false);
                    settingArrayList.get(3).setSelected(false);
                }*/
                break;

            case R.id.linearlayout_progress_note_checkbox:
                setCheckbox(checkBoxProgressNote, 4);
                /*if(checkBoxGoals.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxGoals.setChecked(true);
                        settingArrayList.get(4).setSelected(true);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxGoals.setChecked(false);
                    settingArrayList.get(4).setSelected(false);
                }*/
                break;

            case R.id.checkbox_progress_note:
                setCheckbox(checkBoxProgressNote, 4);
                /*if(checkBoxGoals.isChecked()) {
                    if(countSelectedItems() < 2) {
                        checkBoxGoals.setChecked(true);
                        settingArrayList.get(4).setSelected(true);
                    } else {
                        ShowToast.toast("You can only select 2 items", getApplicationContext());
                    }
                } else {
                    checkBoxGoals.setChecked(false);
                    settingArrayList.get(4).setSelected(false);
                }*/
                break;

            case R.id.linearlayout_mood_checkbox:
                setCheckbox(checkBoxMood, 5);
                break;

            case R.id.checkbox_mood:
                setCheckbox(checkBoxMood, 5);
                break;

            case R.id.linearlayout_status_checkbox:
                setCheckbox(checkBoxStatus, 6);
                break;

            case R.id.checkbox_status:
                setCheckbox(checkBoxStatus, 6);
                break;
        }
    }

    public void setCheckbox(CheckBox checkBoxItem, int position) {
        //if(!checkBoxItem.isChecked()) {
        if(!settingArrayList.get(position).getSelected()) {
            if(countSelectedItems() < 2) {
                checkBoxItem.setChecked(true);
                settingArrayList.get(position).setSelected(true);

                Gson gson = new Gson();
                String settingListString = gson.toJson(settingArrayList);
                Preferences.save(General.SETTING_LIST, settingListString);
            } else {
                ShowToast.toast("Please select 2 items", getApplicationContext());
            }
        } else {
            checkBoxItem.setChecked(false);
            settingArrayList.get(position).setSelected(false);

            Gson gson = new Gson();
            String settingListString = gson.toJson(settingArrayList);
            Preferences.save(General.SETTING_LIST, settingListString);
        }
    }

    public int countSelectedItems() {
        int count = 0;
        for(int i = 0; i < settingArrayList.size(); i++) {
            if(settingArrayList.get(i).getSelected()) {
                count++;
                if(i == 0) {
                    checkBoxEvents.setChecked(true);
                } else if(i == 1) {
                    checkBoxPlan.setChecked(true);
                } else if(i == 2) {
                    checkBoxTasklist.setChecked(true);
                } else if(i == 3) {
                    checkBoxAdherence.setChecked(true);
                } else if(i == 4) {
                    checkBoxProgressNote.setChecked(true);
                } else if(i == 5) {
                    checkBoxMood.setChecked(true);
                } else if(i == 6) {
                    checkBoxStatus.setChecked(true);
                }
            } else {
                if(i == 0) {
                    checkBoxEvents.setChecked(false);
                } else if(i == 1) {
                    checkBoxPlan.setChecked(false);
                } else if(i == 2) {
                    checkBoxTasklist.setChecked(false);
                } else if(i == 3) {
                    checkBoxAdherence.setChecked(false);
                } else if(i == 4) {
                    checkBoxProgressNote.setChecked(false);
                } else if(i == 5) {
                    checkBoxMood.setChecked(false);
                } else if(i == 6) {
                    checkBoxStatus.setChecked(false);
                }
            }
        }
        return count;
    }
}
