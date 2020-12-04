
package com.modules.sos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sagesurfer.adapters.SingleItemAdapter;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.KeyboardOperation;
import com.sagesurfer.library.SosInbuiltMessage;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 25-08-2017
 *         Last Modified on 14-12-2017
 */

public class ForwardSosActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ForwardSosActivity.class.getSimpleName();
    private int sos_id = 0;

    private ListView listView;

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

        setContentView(R.layout.forward_sos_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar_layout);
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

        TextView titleText = (TextView) findViewById(R.id.textview_activitytoolbar_title);
        titleText.setText(this.getResources().getString(R.string.forward));

        TextView postButton = (TextView) findViewById(R.id.textview_activitytoolbar_post);
        postButton.setVisibility(View.VISIBLE);
        postButton.setText(this.getResources().getString(R.string.send));
        postButton.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.forward_sos_list_view);

        Intent data = getIntent();
        if (data != null) {
            if (data.hasExtra(General.ID)) {
                sos_id = data.getIntExtra(General.ID, 0);
            }
        }
        if (sos_id == 0) {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        SingleItemAdapter singleItemAdapter = new SingleItemAdapter(this, SosInbuiltMessage.getList(), false);
        listView.setAdapter(singleItemAdapter);
        listView.setOnItemClickListener(onItemClickListener);
    }

    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            EditText messageBox = (EditText) findViewById(R.id.forward_sos_message_box);
            String message = SosInbuiltMessage.getList().get(position) + " ";
            messageBox.setText(message);
            messageBox.setSelection(messageBox.getText().toString().length());
        }
    };

    private boolean validate(String message) {
        return !(message == null || message.length() < 6 || message.length() > 140);
    }

    private void send(String message) {
        int status = SosOperations_.forward("" + sos_id, message, getApplicationContext(), this);
        if (status == 1) {
            onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_activitytoolbar_post:
                EditText messageBox = (EditText) findViewById(R.id.forward_sos_message_box);
                String message = messageBox.getText().toString().trim();
                KeyboardOperation.hide(getApplicationContext(), messageBox.getWindowToken());
                if (validate(message)) {
                    send(message);
                } else {
                    messageBox.setError("Invalid message\nMin 6 char required\nMax 140 char allowed");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0,0);
        finish();
    }
}
