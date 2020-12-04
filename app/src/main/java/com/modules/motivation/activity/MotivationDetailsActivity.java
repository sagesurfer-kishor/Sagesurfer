package com.modules.motivation.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.modules.motivation.fragment.MotivationContentType_;
import com.modules.motivation.model.MotivationLibrary_;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.views.AttachmentViewer;
import com.sagesurfer.views.VideoViewer;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Kailash on 3/28/2019.
 */

public class MotivationDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MotivationDetailsActivity.class.getSimpleName();

    private MotivationLibrary_ motivation_;

    Toolbar toolbar;
    RelativeLayout topLayout;
    RelativeLayout mediaFooter;
    private MediaPlayer player;
    private String currentPlayingSong = "";
    private Long currentlyPlayingId = 0l;
    private Runnable timerRunnable;
    private Handler seekHandler = new Handler();
    long sentTimestamp = 01;
    private ImageView imageView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_motivation_details);
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
        titleText.setText("");

        LinearLayout linearLayoutActions = (LinearLayout) findViewById(R.id.linearlayout_toolbar_self_goal_details);
        linearLayoutActions.setVisibility(View.VISIBLE);
        AppCompatImageButton postButton = (AppCompatImageButton) findViewById(R.id.imagebutton_toolbar_submit);
        postButton.setVisibility(View.GONE);
        postButton.setOnClickListener(this);
        AppCompatImageButton editButton = (AppCompatImageButton) findViewById(R.id.imagebutton_toolbar_edit);
        editButton.setOnClickListener(this);
        AppCompatImageButton deleteButton = (AppCompatImageButton) findViewById(R.id.imagebutton_toolbar_pin);
        deleteButton.setImageResource(R.drawable.vi_delete_white);
        deleteButton.setOnClickListener(this);

        Preferences.initialize(getApplicationContext());
        topLayout = (RelativeLayout) findViewById(R.id.motivation_details_top_layout);

        Intent data = getIntent();
        if (data.hasExtra(Actions_.GET_DATA)) {
            motivation_ = (MotivationLibrary_) data.getSerializableExtra(Actions_.GET_DATA);
            setData();
        } else {
            onBackPressed();
        }

        //Preferences.save(General.FROM_UPLODER_EDIT, "false");
    }

    // set data with respective values from data to fields
    private void setData() {
        mediaFooter = (RelativeLayout) findViewById(R.id.motivation_details_media_footer);
        AppCompatImageView fullScreen = (AppCompatImageView) findViewById(R.id.motivation_details_full_screen);
        fullScreen.setOnClickListener(this);

        TextView titleText = (TextView) findViewById(R.id.motivation_details_title);
        titleText.setText(motivation_.getTitle());
        TextView descriptionText = (TextView) findViewById(R.id.motivation_details_description);
        descriptionText.setText(motivation_.getDescription());
        imageView = (ImageView) findViewById(R.id.motivation_details_image_view);
        TextView websiteUrlText = (TextView) findViewById(R.id.motivation_details_website_url);

        final int type = MotivationContentType_.nameToType(motivation_.getContent_type());
        player = new MediaPlayer();

        if (type == 1) { //image- image
            if (motivation_.getContent_path().length() > 0) {
                applyImage(motivation_.getContent_path());
            } else {
                topLayout.setVisibility(View.GONE);
            }
        }
        if (type == 2) { //Videos || Courses- video
            applyVideo();
        }
        if (type == 3) { //Audio
            RelativeLayout audioNoteContainer = (RelativeLayout) findViewById(R.id.relativeLayoutAudioContainer);
            audioNoteContainer.setVisibility(View.VISIBLE);
            ImageView audioPlayButton = (ImageView) findViewById(R.id.imageViewPlay);
            TextView currentAudioTime = (TextView) findViewById(R.id.textViewCurrentTime);
            TextView audioTime = (TextView) findViewById(R.id.textViewTotalTime);
            SeekBar audioSeekbar = (SeekBar) findViewById(R.id.seek_bar_audio);

            final TextView currentAudioText = currentAudioTime;
            final TextView audioText = audioTime;
            final SeekBar audioSeekBar = audioSeekbar;
            final ImageView playBtn = audioPlayButton;

            audioSeekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                audioSeekBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }

            player.reset();
            try {
                player.setDataSource(motivation_.getContent_path());
                player.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int audioDuration = player.getDuration();

            audioTime.setText(convertTimeStampToDurationTime(audioDuration));
            audioSeekBar.setProgress(0);

            audioPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(motivation_.getContent_path())) {
                        try {
                            if (sentTimestamp == currentlyPlayingId) {
                                currentPlayingSong = "";
                                currentlyPlayingId = 0l;
                                player.stop();
                                player.reset();
                                setBtnColor(playBtn, true);
                                try {
                                    player.setDataSource(motivation_.getContent_path());
                                    player.prepare();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                int audioDuration = player.getDuration();
                                currentAudioText.setText("00:00");
                                audioSeekBar.setMax(audioDuration);
                                audioSeekBar.setProgress(player.getCurrentPosition());
                            } else {
                                playAudio(motivation_.getContent_path(), sentTimestamp, playBtn, player, currentAudioText, audioSeekBar);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        if (type == 4) { //Text Articles- document
            imageView.setVisibility(View.VISIBLE);
            websiteUrlText.setVisibility(View.VISIBLE);
            //websiteUrlText.setText(FileOperations.getFileName(motivation_.getContent()));
            String styledText = "<u><font color='blue'>" + FileOperations.getFileName(motivation_.getContent_path()) + "</font></u>";
            websiteUrlText.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            websiteUrlText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 3) {
                        try {
                            DownloadFile downloadFile = new DownloadFile();
                            downloadFile.download(motivation_.getId(), motivation_.getContent_path(), FileOperations.getFileName(motivation_.getContent_path()), DirectoryList.ATTACHMENTS_DIR, MotivationDetailsActivity.this);
                            //showDocument();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    // apply video view if content is video
    private void applyVideo() {
        final ProgressBar progress = (ProgressBar) findViewById(R.id.motivation_details_pb);
        progress.setVisibility(View.VISIBLE);
        final VideoView videoView = (VideoView) findViewById(R.id.motivation_details_video_viewer);
        videoView.setVisibility(View.VISIBLE);
        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                progress.setVisibility(View.GONE);
                videoView.start();
            }
        });
        videoView.setVideoURI(Uri.parse(motivation_.getContent_path()));
    }

    // apply image view if content is in image format
    private void applyImage(final String imageUrl) {
        imageView.setVisibility(View.VISIBLE);
        ViewTreeObserver viewTreeObserver = topLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                topLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = (int) getApplicationContext().getResources().getDimension(R.dimen.care_details_image_height);
                int height = topLayout.getMeasuredHeight();

                //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(width, height)
                                .centerCrop())
                        .into(imageView);
            }
        });
    }

    public static String convertTimeStampToDurationTime(long audioDuration) {
        String finalTimerString = "";
        String secondsString = "";
        // Convert total duration into time
        int hours = (int) (audioDuration / (1000 * 60 * 60));
        int minutes = (int) (audioDuration % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((audioDuration % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    public void setBtnColor(ImageView playbtn, boolean isPlayBtn) {
        if (isPlayBtn) {
            playbtn.setBackgroundResource(R.drawable.ic_play_arrow);
        } else {
            playbtn.setBackgroundResource(R.drawable.ic_pause);
        }
        playbtn.getBackground().setColorFilter(new LightingColorFilter(Color.BLACK, Color.BLACK));
    }

    public void playAudio(String message, final long sentTimestamp, final ImageView playBtn, final MediaPlayer player, final TextView currentAudioText, final SeekBar audioSeekBar) {
        try {
            currentPlayingSong = message;
            currentlyPlayingId = sentTimestamp;
            if (timerRunnable != null) {
                seekHandler.removeCallbacks(timerRunnable);
                timerRunnable = null;
            }
            setBtnColor(playBtn, false);
            player.reset();
            player.setDataSource(currentPlayingSong);
            player.prepare();
            player.start();

            final int duration = player.getDuration();
            audioSeekBar.setMax(duration);
            timerRunnable = new Runnable() {
                @Override
                public void run() {

                    int pos = player.getCurrentPosition();
                    audioSeekBar.setProgress(pos);

                    if (player.isPlaying() && pos < duration) {
                        currentAudioText.setText(convertTimeStampToDurationTime(player.getCurrentPosition()));
                        seekHandler.postDelayed(this, 250);
                    } else {
                        seekHandler.removeCallbacks(timerRunnable);
                        timerRunnable = null;
                    }
                }

            };
            seekHandler.postDelayed(timerRunnable, 100);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentPlayingSong = "";
                    currentlyPlayingId = 0l;
                    setBtnColor(playBtn, true);
                    seekHandler.removeCallbacks(timerRunnable);
                    timerRunnable = null;
                    mp.reset();
                    try {
                        mp.setDataSource(motivation_.getContent_path());
                        mp.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.reset();
                    try {
                        player.setDataSource(motivation_.getContent_path());
                        player.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    currentAudioText.setText("00:00");
                    audioSeekBar.setProgress(0);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // open video viewer dialog fragment
    @SuppressLint("CommitTransaction")
    private void openVideo() {
        DialogFragment dialogFrag = new VideoViewer();
        Bundle bundle = new Bundle();
        bundle.putString(General.PATH, "" + motivation_.getId());
        bundle.putString(General.PATH, motivation_.getContent_path());
        bundle.putString(General.IMAGE, motivation_.getTitle());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(this.getFragmentManager().beginTransaction(), General.PATH);
    }

    // open file viewer dialog fragment
    @SuppressLint("CommitTransaction")
    private void openImage() {
        DialogFragment dialogFrag = new AttachmentViewer();
        Bundle bundle = new Bundle();
        bundle.putString(General.ID, "" + motivation_.getId());
        bundle.putString(General.PATH, motivation_.getContent_path());
        bundle.putString(General.IMAGE, motivation_.getTitle());
        bundle.putString(General.DIRECTORY, DirectoryList.DIR_SHARED_FILES);
        bundle.putString(General.SIZE, motivation_.getSize());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(this.getFragmentManager().beginTransaction(), General.URL_IMAGE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        /*if (Preferences.get(General.FROM_UPLODER_EDIT).equalsIgnoreCase("true")) {
            motivation_.setCatagory_id(Long.parseLong(Preferences.get(General.UPLOADER_CATEGORY_ID)));
            motivation_.setCatagory_name(Preferences.get(General.UPLOADER_CATEGORY_NAME));
            categoryText.setText(motivation_.getCategory_name());
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (player.isPlaying()) {
                seekHandler.removeCallbacks(timerRunnable);
                timerRunnable = null;
                player.reset();
                player.prepare();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_right_to_left);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagebutton_toolbar_edit:
                Intent editIntent = new Intent(getApplicationContext(), CreateMotivationActivity.class);
                editIntent.putExtra(Actions_.UPDATE_UPLIFT_DATA, motivation_);
                startActivity(editIntent, ActivityTransition.moveToNextAnimation(getApplicationContext()));
                finish();
                break;
            case R.id.imagebutton_toolbar_pin: //delete
                deleteConfirmation();
                break;
            case R.id.care_details_full_screen:
                if (MotivationContentType_.nameToType(motivation_.getContent_type()) == 1) {
                    openImage();
                }
                if (MotivationContentType_.nameToType(motivation_.getContent_type()) == 2 || MotivationContentType_.nameToType(motivation_.getContent_type()) == 6) {
                    openVideo();
                }
                break;
        }
    }

    //open delete confirmation dialog box
    private void deleteConfirmation() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView title = (TextView) dialog.findViewById(R.id.delete_confirmation_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
        subTitle.setText(this.getResources().getString(R.string.motivation_delete_confirmation));
        title.setText(this.getResources().getString(R.string.action_confirmation));

        TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int response = deleteMotivation();
                showResponses(response, v);
                dialog.dismiss();
            }
        });

        AppCompatImageButton cancelButton = (AppCompatImageButton) dialog.findViewById(R.id.delete_confirmation_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public int deleteMotivation() {
        int result = 12;

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_UPLIFT);
        requestMap.put(General.ID, "" + motivation_.getId());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_UPLIFT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Actions_.DELETE_UPLIFT);
                    JSONObject object = jsonArray.getJSONObject(0);
                    if (object.has(General.STATUS)) {
                        result = object.getInt(General.STATUS);
                    } else {
                        result = 11;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void showResponses(int status, View view) {
        String message = "";
        if (status == 1) {
            message = getApplicationContext().getResources().getString(R.string.successful);
        } else if (status == 3) {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
        if (status == 1) {
            onBackPressed();
        }
    }
}