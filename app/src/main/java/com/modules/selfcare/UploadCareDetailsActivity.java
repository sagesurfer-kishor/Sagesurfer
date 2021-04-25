package com.modules.selfcare;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.download.DownloadFile;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.views.ShowWebView;
import com.sagesurfer.views.VideoViewer;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.directory.DirectoryList;
import com.sagesurfer.models.CareUploaded_;
import com.sagesurfer.models.Comments_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.GetJson_;
import com.sagesurfer.parser.SelfCare_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.views.AttachmentViewer;
import com.sagesurfer.views.CircleTransform;
import com.storage.preferences.Preferences;


import java.util.ArrayList;

import java.util.HashMap;


import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 19-07-2017
 *         Last Modified on 14-12-2017
 */

public class UploadCareDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UploadCareDetailsActivity.class.getSimpleName();
    private String action = "";

    private ViewGroup viewGroup;
    private TextView acceptButton, rejectButton, warningFooter;
    private RelativeLayout footerLayout;
    private EditText commentBox;
    TextView categoryText;

    private CareUploaded_ careUploaded_;

    Toolbar toolbar;
    RelativeLayout topLayout;
    RelativeLayout mediaFooter;
    private MediaPlayer player;
    private String currentPlayingSong = "";
    private Long currentlyPlayingId = 0l;
    private Runnable timerRunnable;
    private Handler seekHandler = new Handler();
    long sentTimestamp = 01;
    private WebView webView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.care_details_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        toolbar = (Toolbar) findViewById(R.id.care_details_toolbar);
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

        Preferences.initialize(getApplicationContext());
        viewGroup = (ViewGroup) findViewById(R.id.care_details_comment_layout);

        acceptButton = (TextView) findViewById(R.id.care_details_accept);
        acceptButton.setOnClickListener(this);
        rejectButton = (TextView) findViewById(R.id.care_details_reject);
        rejectButton.setOnClickListener(this);
        warningFooter = (TextView) findViewById(R.id.care_details_warning_footer);

        footerLayout = (RelativeLayout) findViewById(R.id.care_details_footer);

        commentBox = (EditText) findViewById(R.id.care_details_footer_message_box);

        LinearLayout countLayout = (LinearLayout) findViewById(R.id.care_details_count_layout);
        countLayout.setVisibility(View.GONE);

        AppCompatImageView sendButton = (AppCompatImageView) findViewById(R.id.care_details_footer_send);
        sendButton.setOnClickListener(this);

        topLayout = (RelativeLayout) findViewById(R.id.care_details_top_layout);

        Intent data = getIntent();
        if (data.hasExtra(Actions_.GET_DATA)) {
            careUploaded_ = (CareUploaded_) data.getSerializableExtra(Actions_.GET_DATA);
            action = data.getStringExtra(General.ACTION);
            setButtons();
            setData();
        } else {
            onBackPressed();
        }

        Preferences.save(General.FROM_UPLODER_EDIT, "false");
    }

    // enable/disable action buttons based on action and user access
    private void setButtons() {
        if (!action.contains("re_")) {
            footerLayout.setVisibility(View.GONE);
            acceptButton.setText(this.getResources().getString(R.string.edit));
            rejectButton.setText(this.getResources().getString(R.string.delete));
            warningFooter.setVisibility(View.GONE);
        } else {
            if (action.equalsIgnoreCase(Actions_.RE_PENDING)) {
                acceptButton.setText(this.getResources().getString(R.string.accept));
                rejectButton.setText(this.getResources().getString(R.string.reject));
                if (Preferences.get(General.USER_ID).equalsIgnoreCase("" + careUploaded_.getUser_id())) {
                    footerLayout.setVisibility(View.GONE);
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                    warningFooter.setVisibility(View.VISIBLE);
                } else {
                    warningFooter.setVisibility(View.GONE);
                    acceptButton.setVisibility(View.VISIBLE);
                    rejectButton.setVisibility(View.VISIBLE);
                    footerLayout.setVisibility(View.VISIBLE);
                }
            } else {
                warningFooter.setVisibility(View.GONE);
                acceptButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.GONE);
            }
        }
    }

    // validate comment text for min/max length
    private boolean validate(String comment) {
        if (comment == null) {
            commentBox.setError("Enter Valid Comment");
            return false;
        }
        if (comment.length() < 3) {
            commentBox.setError("Min 3 char required");
            return false;
        }
        if (comment.length() > 250) {
            commentBox.setError("Max 250 char allowed");
            return false;
        }
        return true;
    }

    // make network call to post new comment
    private void makeComment(String message) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "post_comment_reviewer");
        requestMap.put(General.COMMENT, message);
        requestMap.put(General.ID, "" + careUploaded_.getId());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    JsonObject object = GetJson_.getObject(response, "post_comment");
                    if (object != null) {
                        if (object.has(General.STATUS)) {
                            showResponses(object.get(General.STATUS).getAsInt());
                        } else {
                            showResponses(11);
                        }
                    } else {
                        showResponses(12);
                    }
                } else {
                    showResponses(11);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showResponses(11);
    }

    private void showResponses(int status) {
        String message;
        if (status == 1) {
            message = this.getResources().getString(R.string.successful);
        } else {
            message = this.getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, commentBox, getApplicationContext());
        if (status == 1) {
            commentBox.setText("");
            getComments();
        }
    }

    // set data with respective values from data to fields
    private void setData() {
        mediaFooter = (RelativeLayout) findViewById(R.id.care_details_media_footer);
        AppCompatImageView fullScreen = (AppCompatImageView) findViewById(R.id.care_details_full_screen);
        fullScreen.setOnClickListener(this);

        TextView titleText = (TextView) findViewById(R.id.care_details_title);
        titleText.setText(careUploaded_.getTitle());
        TextView descriptionText = (TextView) findViewById(R.id.care_details_description);
        TextView websiteUrlText = (TextView) findViewById(R.id.care_details_website_url);
        descriptionText.setText(careUploaded_.getDescription());
        categoryText = (TextView) findViewById(R.id.care_details_category);
        categoryText.setText(careUploaded_.getCategory_name());
        final int type = SelfCareContentType_.nameToType(careUploaded_.getType());
        player = new MediaPlayer();
        webView = (WebView) findViewById(R.id.care_details_web_view);

        if (type == 1) { //image- image
            if (careUploaded_.getContent().length() > 0) {
                applyImage(careUploaded_.getContent());
            } else {
                topLayout.setVisibility(View.GONE);
            }
        }
        if (type == 2 || type == 6) { //Videos || Courses- video
            applyVideo();
        }
        if (type == 3) { //Text Articles- document and thumbnail image
            websiteUrlText.setVisibility(View.VISIBLE);
            //websiteUrlText.setText(FileOperations.getFileName(careUploaded_.getContent()));
            String styledText = "<u><font color='blue'>" + FileOperations.getFileName(careUploaded_.getContent()) + "</font></u>";
            websiteUrlText.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
            if (careUploaded_.getThumb_path().length() > 0) {
                topLayout.setVisibility(View.VISIBLE);
                applyImage(careUploaded_.getThumb_path());
            } else {
                topLayout.setVisibility(View.GONE);
            }

            websiteUrlText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 3) {
                        try {
                            DownloadFile downloadFile = new DownloadFile();
                            downloadFile.download(careUploaded_.getId(), careUploaded_.getContent(), FileOperations.getFileName(careUploaded_.getContent()), DirectoryList.ATTACHMENTS_DIR, UploadCareDetailsActivity.this);
                            //showDocument();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        if (type == 4) { //Blogs- website link and image
            websiteUrlText.setVisibility(View.VISIBLE);
            websiteUrlText.setText(careUploaded_.getContent());
            if (careUploaded_.getThumb_path().length() > 0) {
                topLayout.setVisibility(View.VISIBLE);
                applyImage(careUploaded_.getThumb_path());
            } else {
                topLayout.setVisibility(View.GONE);
            }
        }
        if (type == 5) { //Podcasts- image and audio
            if (careUploaded_.getThumb_path().length() > 0) {
                topLayout.setVisibility(View.VISIBLE);
                applyImage(careUploaded_.getThumb_path());
            } else {
                topLayout.setVisibility(View.GONE);
            }

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
                player.setDataSource(careUploaded_.getContent());
                player.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int audioDuration = player.getDuration();

            audioTime.setText(convertTimeStampToDurationTime(audioDuration));
            audioSeekBar.setProgress(0);

            /*if (sentTimestamp == currentlyPlayingId) {
                setBtnColor(audioPlayButton, false);
            } else {
                setBtnColor(audioPlayButton, true);
                if (audioDurations.get(sentTimestamp) == null) {
                    player.reset();
                    try {
                        File file = new File(message);
                        if (file.exists()) {
                            player.setDataSource(message);
                            player.prepare();
                        } *//*else {
                                AudioSharing.downloadAndStoreAudio(String.valueOf(message.remoteId), message.imageUrl, false);
                            }*//*
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int audioDuration = player.getDuration();
                    audioDurations.put(sentTimestamp, audioDuration);
                    audioTime.setText(CommonUtils.convertTimeStampToDurationTime(audioDuration));
                    //audioSeekBar.setMax(audioDuration);
                    audioSeekBar.setProgress(0);
                } else {
                    int time = audioDurations.get(sentTimestamp);
                    audioTime.setText(CommonUtils.convertTimeStampToDurationTime(time));
                    //audioSeekBar.setMax(time);
                    audioSeekBar.setProgress(0);
                }
            }*/

            audioPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(careUploaded_.getContent())) {
                        try {
                            if (sentTimestamp == currentlyPlayingId) {
                                currentPlayingSong = "";
                                currentlyPlayingId = 0l;
                                player.stop();
                                player.reset();
                                setBtnColor(playBtn, true);
                                try {
                                    player.setDataSource(careUploaded_.getContent());
                                    player.prepare();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                int audioDuration = player.getDuration();
                                currentAudioText.setText("00:00");
                                audioSeekBar.setMax(audioDuration);
                                audioSeekBar.setProgress(player.getCurrentPosition());
                            } else {
                                playAudio(careUploaded_.getContent(), sentTimestamp, playBtn, player, currentAudioText, audioSeekBar);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        if (type == 7) { //Intervention Tools- website link
            topLayout.setVisibility(View.GONE);
            websiteUrlText.setVisibility(View.VISIBLE);
            websiteUrlText.setText(careUploaded_.getContent());
        }

        if (type == 8) { //webinar- youtube video
            YouTubePlayerSupportFragment youtube_player_fragment = new YouTubePlayerSupportFragment();
            youtube_player_fragment.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    if (null == youTubePlayer) return;
                    if (!b) {
                        String currentString = careUploaded_.getContent();
                        int lastIndex = currentString.lastIndexOf("/");
                        String separated = currentString.substring(lastIndex + 1);
                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        youTubePlayer.cueVideo(separated);
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                }
            });
            FrameLayout youtube_player_frame = (FrameLayout) findViewById(R.id.care_details_youtube_framelayout);
            youtube_player_frame.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.care_details_youtube_framelayout, youtube_player_fragment);
            fragmentTransaction.commit();
        }
    }

    // make network call to fetch reviewers comment for respective self care content
    private void getComments() {
        viewGroup.removeAllViews();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, "get_reviewer_comments");
        requestMap.put(General.ID, "" + careUploaded_.getId());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    ArrayList<Comments_> commentsArrayList = SelfCare_.parseComments(response,
                            "get_reviewer_comments", getApplicationContext(), TAG);
                    int i = 0;
                    if (commentsArrayList.size() > 0) {
                        if (commentsArrayList.get(0).getStatus() == 1) {
                            for (Comments_ comment : commentsArrayList) {
                                addCommentLayout(comment, i);
                                i++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // apply video view if content is video
    private void applyVideo() {
        final ProgressBar progress = (ProgressBar) findViewById(R.id.care_details_pb);
        progress.setVisibility(View.VISIBLE);
        final VideoView videoView = (VideoView) findViewById(R.id.care_details_video_viewer);
        videoView.setVisibility(View.VISIBLE);
        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                progress.setVisibility(View.GONE);
                videoView.start();
            }
        });
        videoView.setVideoURI(Uri.parse(careUploaded_.getContent()));
    }

    // apply image view if content is in image format
    private void applyImage(final String imageUrl) {
        final ImageView imageView = (ImageView) findViewById(R.id.care_details_image_view);
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

    private void showDocument() {
        String url = careUploaded_.getContent();
        String doc = "<html>\n" +
                "<body>\n" +
                "\n" +
                "<iframe src=\"https://docs.google.com/gview?url=" + url +
                "&amp;embedded=true\" style=\"border:none;\"\n" +
                "width = 100% height = 100%></iframe>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        ShowWebView.loadData(webView, doc);
    }

    // add dynamic comment layout row to layout
    private void addCommentLayout(Comments_ comments_, int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.wall_comment_item, viewGroup, false);
        TextView name = (TextView) view.findViewById(R.id.textview_wallcommentitem_name);
        TextView time = (TextView) view.findViewById(R.id.textview_wallcommentitem_time);
        TextView message = (TextView) view.findViewById(R.id.textview_wallcommentitem_text);
        ImageView icon = (ImageView) view.findViewById(R.id.imageview_wallcommentitem_image);
        View bottomLine = view.findViewById(R.id.view_wallcommentitem);
        bottomLine.setVisibility(View.VISIBLE);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(getApplicationContext()).load(comments_.getProfile_pic())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(comments_.getProfile_pic()))
                        .transform(new CircleTransform(getApplicationContext())))
                .into(icon);
        message.setText(comments_.getComment());
        name.setText(comments_.getName());
        time.setText(GetTime.wallTime(comments_.getDate()));
        viewGroup.addView(view);
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
            playbtn.setBackgroundResource( R.drawable.ic_play_arrow);
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
                        mp.setDataSource(careUploaded_.getContent());
                        mp.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.reset();
                    try {
                        player.setDataSource(careUploaded_.getContent());
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
        bundle.putString(General.PATH, "" + careUploaded_.getId());
        bundle.putString(General.PATH, careUploaded_.getContent());
        bundle.putString(General.IMAGE, careUploaded_.getTitle());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(this.getFragmentManager().beginTransaction(), General.PATH);
    }

    // open file viewer dialog fragment
    @SuppressLint("CommitTransaction")
    private void openImage() {
        DialogFragment dialogFrag = new AttachmentViewer();
        Bundle bundle = new Bundle();
        bundle.putString(General.ID, "" + careUploaded_.getId());
        bundle.putString(General.PATH, careUploaded_.getContent());
        bundle.putString(General.IMAGE, careUploaded_.getTitle());
        bundle.putString(General.DIRECTORY, DirectoryList.DIR_SHARED_FILES);
        bundle.putString(General.SIZE, careUploaded_.getSize());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(this.getFragmentManager().beginTransaction(), General.URL_IMAGE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        if(Preferences.getBoolean(General.IS_EDIT)) {
            acceptButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
        }
        getComments();
        if (Preferences.get(General.FROM_UPLODER_EDIT).equalsIgnoreCase("true")) {
            careUploaded_.setCatagory_id(Long.parseLong(Preferences.get(General.UPLOADER_CATEGORY_ID)));
            careUploaded_.setCatagory_name(Preferences.get(General.UPLOADER_CATEGORY_NAME));
            categoryText.setText(careUploaded_.getCategory_name());
        }
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
            case R.id.care_details_footer_send:
                String comment = commentBox.getText().toString().trim();
                if (validate(comment)) {
                    makeComment(comment);
                }
                break;
            case R.id.mail_details_recipient_button:
                break;
            case R.id.care_details_full_screen:
                if (SelfCareContentType_.nameToType(careUploaded_.getType()) == 1) {
                    openImage();
                }

                if (SelfCareContentType_.nameToType(careUploaded_.getType()) == 2 || SelfCareContentType_.nameToType(careUploaded_.getType()) == 6) {
                    openVideo();
                }
                break;
            case R.id.care_details_accept:
                if (!action.contains("re_")) {
                    Intent editIntent = new Intent(getApplicationContext(), CareUploadActivity.class);
                    editIntent.putExtra(Actions_.SELF_CARE, careUploaded_);
                    editIntent.putExtra(General.ACTION, action);
                    startActivity(editIntent);
                    overridePendingTransition(0, 0);
                } else {
                    int status = CareUploaderActions.reviewActionApprove("" + careUploaded_.getUser_id(),
                            careUploaded_.getId(), TAG, getApplicationContext(), v, this);
                    if (status == 1) {
                        onBackPressed();
                    }
                }
                break;
            case R.id.care_details_reject:
                int status;
                if (!action.contains("re_")) {
                    status = CareUploaderActions.delete(action, careUploaded_.getId(), TAG, getApplicationContext(), v, this);
                } else {
                    status = CareUploaderActions.reviewActionDecline("" + careUploaded_.getUser_id(),
                            careUploaded_.getId(), TAG, getApplicationContext(), v, this);
                }
                if (status == 1) {
                    onBackPressed();
                }
                break;
        }
    }
}
