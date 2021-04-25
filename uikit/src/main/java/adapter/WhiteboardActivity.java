package adapter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cometchat.pro.uikit.R;

import java.util.prefs.Preferences;

import screen.messagelist.General;

public class WhiteboardActivity extends AppCompatActivity {
    WebView webView;
    SharedPreferences sp;
    String Name;
    private static final String TAG = "WhiteboardActivity";
    private Toolbar toolbar;
    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiteboard);
        sp = getSharedPreferences("login", MODE_PRIVATE);

        toolbar = findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.textColorWhite));
        toolbar.setTitle("Whiteboard");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });

        String sessionId = getIntent().getStringExtra("whiteBoardUrl");
        Log.i(TAG, "onCreate: sessionId "+sessionId);
        String data = getIntent().getStringExtra("whiteBoardUrl");
        Log.i(TAG, "onCreate: Data "+data);
        webView = findViewById(R.id.whitebord_web);

        Name = sp.getString("flname", Name);

        if (sessionId != null) {
            webView.clearHistory();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.setWebViewClient(new WebClient());
            webView.clearCache(true);
            webView.loadUrl(sessionId + "&username=" + Name);
            Log.e("whiteboard my url", sessionId + "&username=" + Name);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
