package com.modules.selfcare;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.FileOperations;
import com.sagesurfer.library.GetColor;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.models.Comments_;
import com.sagesurfer.models.Content_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.SelfCare_;
import com.sagesurfer.snack.SubmitSnackResponse;
import com.sagesurfer.tasks.PerformReadTask;
import com.sagesurfer.views.AttachmentViewer;
import com.sagesurfer.views.CircleTransform;
import com.sagesurfer.views.VideoViewer;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * @author Kailash Karankal
 */

public class SelfCareDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SelfCareDetailsActivity.class.getSimpleName();
    @BindView(R.id.relativelayout_activityselfcaredetails_action)
    RelativeLayout relativeLayoutActivitySelfCareDetailsAction;
    @BindView(R.id.relativelayout_activityselfcaredetails_top)
    RelativeLayout relativeLayoutActivitySelfCareDetailsTop;
    @BindView(R.id.imageview_activityselfcaredetails)
    ImageView imageViewActivitySelfCareDetails;
    @BindView(R.id.videoview_activityselfcaredetails)
    VideoView videoViewActivitySelfCareDetails;
    @BindView(R.id.framelayout_activityselfcaredetails_youtube)
    FrameLayout frameLayoutActivitySelfCareDetailsYoutube;
    @BindView(R.id.relativelayout_activityselfcaredetails_media_footer)
    RelativeLayout relativeLayoutActivitySelfCareDetailsMediaFooter;
    @BindView(R.id.imageview_activityselfcaredetails_full_screen)
    AppCompatImageView imageViewActivitySelfCareDetailsFullScreen;
    @BindView(R.id.progressbar_activityselfcaredetails)
    ProgressBar progressBarActivitySelfCareDetails;
    @BindView(R.id.textview_activityselfcaredetails_title)
    TextView textViewActivitySelfCareDetailsTitle;

    @BindView(R.id.imageview_activityselfcaredetails_like)
    AppCompatImageView imageViewActivitySelfCareDetailsLike;

    @BindView(R.id.textview_activityselfcaredetails_like_count)
    TextView textViewActivitySelfCareDetailsLikeCount;
    @BindView(R.id.imageview_activityselfcaredetails_comment)
    AppCompatImageView imageViewActivitySelfCareDetailsComment;
    @BindView(R.id.textview_activityselfcaredetails_comment_count)
    TextView textViewActivitySelfCareDetailsCommentCount;
    @BindView(R.id.textview_activityselfcaredetails_shared_with)
    TextView textViewActivitySelfCareDetailsSharedWith;
    @BindView(R.id.textview_activityselfcaredetails_website_url)
    TextView textViewActivitySelfCareDetailsWebsiteUrl;
    @BindView(R.id.textview_activityselfcaredetails_description)
    TextView textViewActivitySelfCareDetailsDescription;
    @BindView(R.id.textview_activityselfcaredetails_category)
    TextView textViewActivitySelfCareDetailsCategory;
    @BindView(R.id.textview_activityselfcaredetails_comment_tag)
    TextView textViewActivitySelfCareDetailsCommentTag;
    @BindView(R.id.linearlayout_activityselfcaredetails_comment)
    ViewGroup linearLayoutActivitySelfCareDetailsComment;
    @BindView(R.id.relativelayout_activityselfcaredetails_footer)
    RelativeLayout relativeLayoutActivitySelfCareDetailsFooter;
    @BindView(R.id.edittext_activityselfcaredetails_footer_message)
    EditText editTextActivitySelfCareDetailsFooterMessage;
    /*@BindView(R.id.imageview_activityselfcaredetails_footer_send)
    ImageButton imageViewActivitySelfCareDetailsFooterSend;*/
    @BindView(R.id.imageview_activityselfcaredetails_footer_send)
    AppCompatImageButton imageViewActivitySelfCareDetailsFooterSend;

    private Content_ content_;
    private Toolbar toolbar;
    private MediaPlayer player;
    private String currentPlayingSong = "";
    private Long currentlyPlayingId = 0l;
    private Runnable timerRunnable;
    private Handler seekHandler = new Handler();
    long sentTimestamp = 01;
    private LinearLayout mLinearLayoutShare;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For setting status bar color
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, GetColor.getHomeIconBackgroundColorColorParse(false)));

        setContentView(R.layout.activity_self_care_details);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        toolbar = (Toolbar) findViewById(R.id.toolbar_activityselfcaredetails);
        mLinearLayoutShare = findViewById(R.id.linear_share);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(this.getResources().getColor(R.color.transparent));
        toolbar.setNavigationIcon(R.drawable.vi_left_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       /* View myView = findViewById(R.id.imageview_activityselfcaredetails_footer_send);
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = this.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        myView.setBackgroundResource(backgroundResource);*/

        relativeLayoutActivitySelfCareDetailsAction.setVisibility(View.GONE);
        relativeLayoutActivitySelfCareDetailsFooter.setVisibility(View.VISIBLE);
        imageViewActivitySelfCareDetailsFooterSend.setOnClickListener(this);

        Intent data = getIntent();
        if (data.hasExtra(Actions_.GET_DATA)) {
            content_ = (Content_) data.getSerializableExtra(Actions_.GET_DATA);
            setData();
        } else {
            onBackPressed();
        }

        if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")){
            mLinearLayoutShare.setVisibility(View.GONE);
        }else {
            mLinearLayoutShare.setVisibility(View.VISIBLE);
        }
    }

    // set values to respective field from received data
    private void setData() {
        imageViewActivitySelfCareDetailsFullScreen.setOnClickListener(this);
        imageViewActivitySelfCareDetails.setOnClickListener(this);
        textViewActivitySelfCareDetailsWebsiteUrl.setOnClickListener(this);

        textViewActivitySelfCareDetailsTitle.setText(content_.getTitle());
        if (content_.getShared_to().length() > 0) {
            textViewActivitySelfCareDetailsSharedWith.setVisibility(View.VISIBLE);
            textViewActivitySelfCareDetailsSharedWith.setText(getApplicationContext().getResources().getString(R.string.shared_with) + ": " + content_.getShared_to());
        }
        textViewActivitySelfCareDetailsDescription.setText(content_.getDescription());
        textViewActivitySelfCareDetailsCategory.setText(content_.getCategory());
        textViewActivitySelfCareDetailsLikeCount.setText(GetCounters.convertCounter(content_.getLike()));
        textViewActivitySelfCareDetailsCommentCount.setText(GetCounters.convertCounter(content_.getComments()));
        player = new MediaPlayer();
        String commentMessage;
        if (content_.getComments() > 0) {
            if (content_.getComments() == 1) {
                commentMessage = content_.getComments() + " " + getApplicationContext().getResources()
                        .getString(R.string.comment);
            } else {
                commentMessage = GetCounters.convertCounter(content_.getComments()) + " " + getApplicationContext().getResources().getString(R.string.comments);
            }
        } else {
            commentMessage = "0" + " " + getApplicationContext().getResources().getString(R.string.comment);
        }
        textViewActivitySelfCareDetailsCommentTag.setText(commentMessage);

        applyLike();
        applyComment();
        int type = SelfCareContentType_.nameToType(content_.getType());
        if (type == 1) { //image- image
            if (content_.getContent().length() > 0) {
                applyImage(content_.getContent());
            } else {
                relativeLayoutActivitySelfCareDetailsTop.setVisibility(View.GONE);
            }
        }
        if (type == 2 || type == 6) { //Videos || Courses- video
            applyVideo();
        }
        if (type == 3) { //Text Articles- document and thumbnail image
            textViewActivitySelfCareDetailsWebsiteUrl.setVisibility(View.VISIBLE);
            //textViewActivitySelfCareDetailsWebsiteUrl.setText(FileOperations.getFileName(content_.getContent()));

            SpannableString content = new SpannableString(FileOperations.getFileName(content_.getContent()));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            textViewActivitySelfCareDetailsWebsiteUrl.setText(content);

            if (content_.getThumb_path().length() > 0) {
                relativeLayoutActivitySelfCareDetailsTop.setVisibility(View.VISIBLE);
                applyImage(content_.getThumb_path());
            } else {
                relativeLayoutActivitySelfCareDetailsTop.setVisibility(View.GONE);
            }
        }
        if (type == 4) { //Blogs- website link and image
            textViewActivitySelfCareDetailsWebsiteUrl.setVisibility(View.VISIBLE);
            textViewActivitySelfCareDetailsWebsiteUrl.setText(content_.getContent());
            if (content_.getThumb_path().length() > 0) {
                relativeLayoutActivitySelfCareDetailsTop.setVisibility(View.VISIBLE);
                applyImage(content_.getThumb_path());
            } else {
                relativeLayoutActivitySelfCareDetailsTop.setVisibility(View.GONE);
            }
        }
        if (type == 5) { //Podcasts- image and audio
            if (content_.getThumb_path().length() > 0) {
                relativeLayoutActivitySelfCareDetailsTop.setVisibility(View.VISIBLE);
                applyImage(content_.getThumb_path());
            } else {
                relativeLayoutActivitySelfCareDetailsTop.setVisibility(View.GONE);
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
                player.setDataSource(content_.getContent());
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
                    if (!TextUtils.isEmpty(content_.getContent())) {
                        try {
                            if (sentTimestamp == currentlyPlayingId) {
                                currentPlayingSong = "";
                                currentlyPlayingId = 0l;
                                player.stop();
                                player.reset();
                                setBtnColor(playBtn, true);
                                try {
                                    player.setDataSource(content_.getContent());
                                    player.prepare();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                int audioDuration = player.getDuration();
                                currentAudioText.setText("00:00");
                                audioSeekBar.setMax(audioDuration);
                                audioSeekBar.setProgress(player.getCurrentPosition());
                            } else {
                                playAudio(content_.getContent(), sentTimestamp, playBtn, player, currentAudioText, audioSeekBar);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        if (type == 7) { //Intervention Tools- website link
            relativeLayoutActivitySelfCareDetailsTop.setVisibility(View.GONE);
            textViewActivitySelfCareDetailsWebsiteUrl.setVisibility(View.VISIBLE);
            textViewActivitySelfCareDetailsWebsiteUrl.setText(content_.getContent());
        }

        if (type == 8) { //webinar- youtube video
            YouTubePlayerSupportFragment youtube_player_fragment = new YouTubePlayerSupportFragment();
            youtube_player_fragment.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    if (null == youTubePlayer) return;
                    if (!b) {
                        String currentString = content_.getContent();
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
            frameLayoutActivitySelfCareDetailsYoutube.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.framelayout_activityselfcaredetails_youtube, youtube_player_fragment);
            fragmentTransaction.commit();
        }
    }

    // make network call to fetch comment for respective self care content
    private void getComments() {
        linearLayoutActivitySelfCareDetailsComment.removeAllViews();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_COMMENTS);
        requestMap.put(General.ID, "" + content_.getId());

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, this, this);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, this, this);
                if (response != null) {
                    ArrayList<Comments_> commentsArrayList = SelfCare_.parseComments(response, Actions_.GET_COMMENTS, getApplicationContext(), TAG);
                    int i = 0;
                    if (commentsArrayList.size() > 0) {
                        if (commentsArrayList.get(0).getStatus() == 1) {
                            for (Comments_ comment : commentsArrayList) {
                                addCommentLayout(comment, i);
                                i++;
                            }
                            textViewActivitySelfCareDetailsCommentTag.setText("" + i + " " + getApplicationContext().getResources().getString(R.string.comments));
                            textViewActivitySelfCareDetailsCommentCount.setText("" + i);
                            textViewActivitySelfCareDetailsCommentCount.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                            imageViewActivitySelfCareDetailsComment.setImageResource(R.drawable.vi_comment_blue);
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
        progressBarActivitySelfCareDetails.setVisibility(View.VISIBLE);
        videoViewActivitySelfCareDetails.setVisibility(View.VISIBLE);
        videoViewActivitySelfCareDetails.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBarActivitySelfCareDetails.setVisibility(View.GONE);
                videoViewActivitySelfCareDetails.start();
            }
        });
        videoViewActivitySelfCareDetails.setVideoURI(Uri.parse(content_.getContent()));
    }

    // apply video view if content is image
    private void applyImage(final String imageUrl) {
        imageViewActivitySelfCareDetails.setVisibility(View.VISIBLE);
        ViewTreeObserver viewTreeObserver = relativeLayoutActivitySelfCareDetailsTop.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                relativeLayoutActivitySelfCareDetailsTop.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = relativeLayoutActivitySelfCareDetailsTop.getMeasuredWidth();
                int height = relativeLayoutActivitySelfCareDetailsTop.getMeasuredHeight();

                //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .transition(withCrossFade())
                        .apply(new RequestOptions()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(width, height))
                        .into(imageViewActivitySelfCareDetails);
            }
        });
    }

    // apply like status of me to respective icon and text
    private void applyLike() {
        if (content_.getIs_like() > 0) {
            textViewActivitySelfCareDetailsLikeCount.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
            imageViewActivitySelfCareDetailsLike.setImageResource(R.drawable.vi_like_blue);
        } else {
            textViewActivitySelfCareDetailsLikeCount.setTextColor(getApplicationContext().getResources().getColor(R.color.text_color_primary));
            imageViewActivitySelfCareDetailsLike.setImageResource(R.drawable.vi_like_gray);
        }
    }

    // apply comment status of me to respective icon and text
    private void applyComment() {
        if (content_.getComments() > 0) {
            textViewActivitySelfCareDetailsCommentCount.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
            imageViewActivitySelfCareDetailsComment.setImageResource(R.drawable.vi_comment_blue);
        } else {
            textViewActivitySelfCareDetailsCommentCount.setTextColor(getApplicationContext().getResources().getColor(R.color.text_color_primary));
            imageViewActivitySelfCareDetailsComment.setImageResource(R.drawable.vi_comment_gray);
        }
    }

    // add comment row to layout
    private void addCommentLayout(Comments_ comments_, int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.wall_comment_item, linearLayoutActivitySelfCareDetailsComment, false);
        TextView textViewWallCommentItemName = (TextView) view.findViewById(R.id.textview_wallcommentitem_name);
        TextView textViewWallCommentItemTime = (TextView) view.findViewById(R.id.textview_wallcommentitem_time);
        TextView textViewWallCommentItemText = (TextView) view.findViewById(R.id.textview_wallcommentitem_text);
        ImageView imageViewWallCommentItemImage = (ImageView) view.findViewById(R.id.imageview_wallcommentitem_image);
        View viewWallCommentItem = view.findViewById(R.id.view_wallcommentitem);
        viewWallCommentItem.setVisibility(View.VISIBLE);

        //Monika M 2/6/18- Glide upgradation 3.7.0 to 4.1.1
        Glide.with(getApplicationContext()).load(comments_.getProfile_pic())
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(GetThumbnails.userIcon(comments_.getProfile_pic()))
                        .transform(new CircleTransform(getApplicationContext())))
                .into(imageViewWallCommentItemImage);
        textViewWallCommentItemText.setText(comments_.getComment());
        textViewWallCommentItemName.setText(comments_.getName());
        textViewWallCommentItemTime.setText(GetTime.wallTime(comments_.getDate()));
        linearLayoutActivitySelfCareDetailsComment.addView(view);
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
                        mp.setDataSource(content_.getContent());
                        mp.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.reset();
                    try {
                        player.setDataSource(content_.getContent());
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

    // open full screen video view dialog
    @SuppressLint("CommitTransaction")
    private void openVideo() {
        DialogFragment dialogFrag = new VideoViewer();
        Bundle bundle = new Bundle();
        bundle.putString(General.PATH, content_.getContent());
        bundle.putString(General.IMAGE, "");
        dialogFrag.setArguments(bundle);
        dialogFrag.show(this.getFragmentManager().beginTransaction(), General.PATH);
    }

    // open full screen image view dialog
    @SuppressLint("CommitTransaction")
    private void openImage() {
        DialogFragment dialogFrag = new AttachmentViewer();
        Bundle bundle = new Bundle();
        bundle.putString(General.PATH, content_.getContent());
        bundle.putString(General.IMAGE, "");
        bundle.putString(General.SIZE, content_.getSize());
        dialogFrag.setArguments(bundle);
        dialogFrag.show(this.getFragmentManager().beginTransaction(), General.URL_IMAGE);
    }

    private void showResponses(int status, View view) {
        String message;
        if (status == 1) {
            message = getApplicationContext().getResources().getString(R.string.successful);
        } else {
            message = getApplicationContext().getResources().getString(R.string.action_failed);
        }
        SubmitSnackResponse.showSnack(status, message, view, getApplicationContext());
    }

    // validate comment content for valid min and max length
    private boolean validate(String comment) {
        if (comment == null) {
            editTextActivitySelfCareDetailsFooterMessage.setError("Enter Valid Comment");
            return false;
        }
        if (comment.length() < 3) {
            editTextActivitySelfCareDetailsFooterMessage.setError("Min 3 char required");
            return false;
        }
        if (comment.length() > 250) {
            editTextActivitySelfCareDetailsFooterMessage.setError("Max 250 char allowed");
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundResource(GetColor.getHomeIconBackgroundColorColorParse(false));
        int status = PerformReadTask.readAlert("" + content_.getId(), General.SELFCARE, TAG, getApplicationContext(), this);
        getComments();
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
            case R.id.imageview_activityselfcaredetails_footer_send:
                String comment = editTextActivitySelfCareDetailsFooterMessage.getText().toString().trim();
                if (validate(comment)) {
                    int status = SelfCareOperations.comment("" + content_.getId(), comment, TAG, v, getApplicationContext(), this);
                    showResponses(status, v);
                    if (status == 1) {
                        editTextActivitySelfCareDetailsFooterMessage.setText("");
                        getComments();
                    }
                }
                break;
            case R.id.imageview_activityselfcaredetails_full_screen:
                if (SelfCareContentType_.nameToType(content_.getType()) == 1) {
                    openImage();
                }

                if (SelfCareContentType_.nameToType(content_.getType()) == 2 || SelfCareContentType_.nameToType(content_.getType()) == 6) {
                    openVideo();
                }
                break;

            case R.id.textview_activityselfcaredetails_website_url:
                Uri uri = Uri.parse(content_.getContent());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }
}
