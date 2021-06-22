package com.cometchat.pro.uikit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;

import com.aghajari.emojiview.listener.SimplePopupAdapter;
import com.aghajari.emojiview.view.AXEmojiPager;
import com.aghajari.emojiview.view.AXEmojiPopup;
import com.aghajari.emojiview.view.AXSingleEmojiView;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;;
import com.cometchat.pro.models.CustomMessage;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import adapter.WhiteboardActivity;
import constant.StringContract;
import listeners.ComposeActionListener;
import utils.AudioVisualizer.AudioRecordView;
import utils.MediaUtils;
import utils.Utils;


public class ComposeBox extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "CometChatMessageListAct";

    /**
     * Change this to {@code false} when you want to use the downloadable Emoji font.
     */
    private static final boolean USE_BUNDLED_EMOJI = true;

    // [U+1F469] (WOMAN) + [U+200D] (ZERO WIDTH JOINER) + [U+1F4BB] (PERSONAL COMPUTER)
    private static final String WOMAN_TECHNOLOGIST = "\uD83D\uDC69\u200D\uD83D\uDCBB";

    // [U+1F469] (WOMAN) + [U+200D] (ZERO WIDTH JOINER) + [U+1F3A4] (MICROPHONE)
    private static final String WOMAN_SINGER = "\uD83D\uDC69\u200D\uD83C\uDFA4";

    static final String EMOJI = WOMAN_TECHNOLOGIST + " " + WOMAN_SINGER;

    private AudioRecordView audioRecordView;

    private MediaRecorder mediaRecorder;

    private MediaPlayer mediaPlayer;

    private Runnable timerRunnable;

    private Handler seekHandler = new Handler(Looper.getMainLooper());

    private Timer timer = new Timer();

    private String audioFileNameWithPath;

    private boolean isOpen, isRecording, isPlaying, voiceMessage;

    public ImageView ivAudio, ivCamera, ivGallery, ivFile, ivSend, ivArrow, ivMic, ic_whiteboard,ivDelete, ivWb;

    private SeekBar voiceSeekbar;

    private Chronometer recordTime;

    public AppCompatEditText etComposeBox;

    private RelativeLayout composeBox;

    private RelativeLayout flBox;

    private RelativeLayout voiceMessageLayout;

    private ConstraintLayout cl_ActionContainer;

    private boolean hasFocus;

    private ComposeActionListener composeActionListener;

    private Context context;

    ImageView whiteboardRequest;
    AppCompatImageView emojiImg;
    AXEmojiPopup emojiPopup;

    private int color;
    AppCompatImageView btnStickes;

    ArrayList<String> ar = new ArrayList<String>();
    SharedPreferences sp;
    String GUID = null;
    String UID;

    String ID = null;
    String reciverType;

    private Bundle bundle = new Bundle();
    public boolean isGalleryVisible = true, isAudioVisible = true, isCameraVisible = true,
            isFileVisible = true, isLocationVisible = true;

    public ComposeBox(Context context) {
        super(context);

        initViewComponent(context, null, -1, -1);
    }

    public ComposeBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewComponent(context, attrs, -1, -1);
    }

    public ComposeBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViewComponent(context, attrs, defStyleAttr, -1);
    }

    private void initViewComponent(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        View view = View.inflate(context, R.layout.layout_compose_box, null);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.ComposeBox, 0, 0);
        color = a.getColor(R.styleable.ComposeBox_color, getResources().getColor(R.color.colorPrimary));
        addView(view);

        this.context = context;

        ViewGroup viewGroup = (ViewGroup) view.getParent();
        viewGroup.setClipChildren(false);

        sp = context.getSharedPreferences("login", context.MODE_PRIVATE);
        UID = sp.getString("IDS", UID);
        reciverType = sp.getString("types", reciverType);

        mediaPlayer = new MediaPlayer();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isMusicActive()) {
            audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        stopRecording(true);
                    }
                }
            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        composeBox = this.findViewById(R.id.message_box);
        flBox = this.findViewById(R.id.flBox);
        ivMic = this.findViewById(R.id.ivMic);
        ic_whiteboard = this.findViewById(R.id.ic_whiteboard);
        ivDelete = this.findViewById(R.id.ivDelete);
        audioRecordView = this.findViewById(R.id.record_audio_visualizer);
        voiceMessageLayout = this.findViewById(R.id.voiceMessageLayout);
        recordTime = this.findViewById(R.id.record_time);
        voiceSeekbar = this.findViewById(R.id.voice_message_seekbar);
        ivCamera = this.findViewById(R.id.ivCamera);
        ivGallery = this.findViewById(R.id.ivImage);
        ivAudio = this.findViewById(R.id.ivAudio);
        ivFile = this.findViewById(R.id.ivFile);
        ivSend = this.findViewById(R.id.ivSend);
        ivArrow = this.findViewById(R.id.ivArrow);

        whiteboardRequest = this.findViewById(R.id.ic_whiteboard);
        emojiImg = this.findViewById(R.id.imageView);

        etComposeBox = this.findViewById(R.id.etComposeBox);
        cl_ActionContainer = this.findViewById(R.id.rlActionContainers);

        ivArrow.setImageTintList(ColorStateList.valueOf(color));
        ivCamera.setImageTintList(ColorStateList.valueOf(color));
        ivGallery.setImageTintList(ColorStateList.valueOf(color));

        ivFile.setImageTintList(ColorStateList.valueOf(color));
        ivSend.setImageTintList(ColorStateList.valueOf(color));

        btnStickes = this.findViewById(R.id.ic_stickers);
        btnStickes.setOnClickListener(this);

        ivAudio.setOnClickListener(this);
        ivArrow.setOnClickListener(this);
        ivSend.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivFile.setOnClickListener(this);
        ivMic.setOnClickListener(this);
        ivGallery.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
        emojiImg.setOnClickListener(this);
        whiteboardRequest.setOnClickListener(this);

        AXEmojiPager emojiPager = new AXEmojiPager(context);
        AXSingleEmojiView singleEmojiView = new AXSingleEmojiView(context);
        emojiPager.addPage(singleEmojiView, R.drawable.ic_msg_panel_smiles);

        emojiPager.setSwipeWithFingerEnabled(true);
        emojiPager.setEditText(etComposeBox);

        emojiPopup = new AXEmojiPopup(emojiPager);

        etComposeBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (composeActionListener != null) {
                    composeActionListener.beforeTextChanged(charSequence, i, i1, i2);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (composeActionListener != null) {
                    composeActionListener.onTextChanged(charSequence, i, i1, i2);
                    String mention = etComposeBox.getText().toString().trim();
                    Log.d(TAG, ar.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (composeActionListener != null) {
                    composeActionListener.afterTextChanged(editable);
                }
            }
        });

        emojiPopup.setPopupListener(new SimplePopupAdapter() {
            @Override
            public void onShow() {
                Drawable dr = AppCompatResources.getDrawable(context, R.drawable.ic_msg_panel_kb);
                DrawableCompat.setTint(DrawableCompat.wrap(dr), Color.BLACK);
                emojiImg.setImageDrawable(dr);
            }

            @Override
            public void onDismiss() {
                Drawable dr = AppCompatResources.getDrawable(context, R.drawable.emij_icon);
                DrawableCompat.setTint(DrawableCompat.wrap(dr), Color.BLACK);
                emojiImg.setImageDrawable(dr);
            }
        });

        if (Utils.isDarkMode(context)) {
            composeBox.setBackgroundColor(getResources().getColor(R.color.darkModeBackground));
            ivAudio.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            ivMic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_white_24dp));
            flBox.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
            etComposeBox.setTextColor(getResources().getColor(R.color.textColorWhite));
            ivArrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            ivSend.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            ivCamera.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            ivGallery.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            ivFile.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
        } else {
            composeBox.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
            ivAudio.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            ivMic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_grey_24dp));
            etComposeBox.setTextColor(getResources().getColor(R.color.primaryTextColor));
            ivSend.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            flBox.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            ivArrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
            ivCamera.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            ivFile.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            ivFile.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }
        a.recycle();
    }

    public void setText(String text) {
        etComposeBox.setText(text);
    }

    public void setColor(int color) {
        ivSend.setImageTintList(ColorStateList.valueOf(color));
        ivCamera.setImageTintList(ColorStateList.valueOf(color));
        ivGallery.setImageTintList(ColorStateList.valueOf(color));
        ivFile.setImageTintList(ColorStateList.valueOf(color));
        ivArrow.setImageTintList(ColorStateList.valueOf(color));
    }

    public void setComposeBoxListener(ComposeActionListener composeActionListener) {
        this.composeActionListener = composeActionListener;
        this.composeActionListener.getCameraActionView(ivCamera);
        this.composeActionListener.getGalleryActionView(ivGallery);
        this.composeActionListener.getFileActionView(ivFile);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivDelete) {
            stopRecording(true);
            stopPlayingAudio();
            voiceMessageLayout.setVisibility(GONE);
            etComposeBox.setVisibility(View.VISIBLE);
            ivArrow.setVisibility(View.VISIBLE);
            ivMic.setVisibility(View.VISIBLE);
            ivMic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_grey_24dp));
            isPlaying = false;
            isRecording = false;
            voiceMessage = false;
            ivDelete.setVisibility(GONE);
            ivSend.setVisibility(View.GONE);
        }
        if (view.getId() == R.id.ivCamera) {
            composeActionListener.onCameraActionClicked(ivCamera);
        }
        if (view.getId() == R.id.ivImage) {
            composeActionListener.onGalleryActionClicked(ivGallery);
        }
        if (view.getId() == R.id.ivSend) {
            if (!voiceMessage) {
                if (etComposeBox.getText().length() > 0) {
                    //textView.setText(etComposeBox.getText().toString());
                    composeActionListener.onSendActionClicked(etComposeBox);
                    etComposeBox.setText("");
                }
            } else {
                composeActionListener.onVoiceNoteComplete(audioFileNameWithPath);
                audioFileNameWithPath = "";
                voiceMessageLayout.setVisibility(GONE);
                etComposeBox.setVisibility(View.VISIBLE);
                ivSend.setVisibility(GONE);
                ivArrow.setVisibility(View.VISIBLE);
                ivMic.setVisibility(View.VISIBLE);
                isRecording = false;
                isPlaying = false;
                voiceMessage = false;
                ivMic.setImageResource(R.drawable.ic_mic_grey_24dp);
            }

        }
        if (view.getId() == R.id.ivAudio) {
            composeActionListener.onAudioActionClicked(ivAudio);
        }
        if (view.getId() == R.id.ivFile) {
            composeActionListener.onFileActionClicked(ivFile);
        }
        if (view.getId() == R.id.ivArrow) {
           /* if (isOpen) {
                closeActionContainer();
            } else {
                openActionContainer();
            }*/


            final BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.attchment_view);
            dialog.setCanceledOnTouchOutside(false);

            ImageView ivCamera = dialog.findViewById(R.id.ivCamera);
            ImageView ivImage = dialog.findViewById(R.id.ivImage);
            ImageView ivAudio = dialog.findViewById(R.id.ivAudio);
            ImageView ivFile = dialog.findViewById(R.id.ivFile);
            ImageView close = dialog.findViewById(R.id.close);
            //ImageView ivLocation = dialog.findViewById(R.id.ivLocation);


            ivCamera.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    composeActionListener.onCameraActionClicked(ivFile);
                    dialog.dismiss();
                }
            });
            ivImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    composeActionListener.onGalleryActionClicked(ivFile);
                    dialog.dismiss();
                }
            });
            ivAudio.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    composeActionListener.onAudioActionClicked(ivFile);
                    dialog.dismiss();
                }
            });
            ivFile.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    composeActionListener.onFileActionClicked(ivFile);
                    dialog.dismiss();
                }
            });

            /*ivLocation.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    composeActionListener.onLocationActionClicked();
                    dialog.dismiss();
                }
            });*/

            close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }

         /*   FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
            bundle.putBoolean("isGalleryVisible", isGalleryVisible);
            bundle.putBoolean("isCameraVisible", isCameraVisible);
            bundle.putBoolean("isFileVisible", isFileVisible);
            bundle.putBoolean("isAudioVisible", isAudioVisible);
            bundle.putBoolean("isLocationVisible", isLocationVisible);
            composeBoxActionFragment.setArguments(bundle);
            composeBoxActionFragment.show(fm, composeBoxActionFragment.getTag());*/
        if (view.getId() == R.id.ivMic) {
            if (Utils.hasPermissions(context, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                if (isOpen) {
                    closeActionContainer();
                }
                if (!isRecording) {
                    startRecord();
                    ivMic.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_24dp));
                    isRecording = true;
                    isPlaying = false;
                } else {
                    if (isRecording && !isPlaying) {
                        isPlaying = true;
                        stopRecording(false);
                        recordTime.stop();
                    }
                    ivMic.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_24dp));
                    audioRecordView.setVisibility(GONE);
                    ivSend.setVisibility(View.VISIBLE);
                    ivDelete.setVisibility(View.VISIBLE);
                    voiceSeekbar.setVisibility(View.VISIBLE);
                    voiceMessage = true;
                    if (audioFileNameWithPath != null)
                        startPlayingAudio(audioFileNameWithPath);
                    else
                        Toast.makeText(getContext(), "No File Found. Please", Toast.LENGTH_LONG).show();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Activity) context).requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            StringContract.RequestCode.RECORD);
                }
            }
        }

        if (view.getId() == R.id.imageView) {
            emojiPopup.toggle();
        }
        if (view.getId() == R.id.ic_stickers) {

            composeActionListener.onStickerClicked();

        }

        if (view.getId() == R.id.ic_whiteboard) {
            JSONObject body = new JSONObject();
            try {
                body.put("receiver", UID);
                body.put("receiverType", reciverType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CometChat.callExtension("whiteboard", "POST", "/v1/create/", body, new CometChat.CallbackListener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    try {
                        String loudScreaming = jsonObject.getJSONObject("data").getString("board_url");
                        composeActionListener.onSendCustomActionClicked(loudScreaming);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e("Whiteboard response", String.valueOf(e));
                }
            });
        }
    }

    private void SendCustomMessage(String loudScreaming, String id) {
        String UID = id;
        String customType = "extension_whiteboard";
        JSONObject customData = new JSONObject();
        try {
            customData.put("messaage", "whiteboard request send successfully");
            customData.put("url", loudScreaming);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject metadataObject = new JSONObject();
        JSONArray languageArray = new JSONArray();
        languageArray.put("");
        CustomMessage customMessage = new CustomMessage(UID, CometChatConstants.RECEIVER_TYPE_USER, customType, customData);
        try {
            metadataObject.put("message_translation_languages", languageArray);
            metadataObject.put("whiteboard_URL_one", loudScreaming);

            customMessage.setMetadata(metadataObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage customMessage) {
                Log.e(TAG, customMessage.toString());

                MediaUtils.playSendSound(context, R.raw.outgoing_message);


                if (loudScreaming != null) {
                    Intent intent = new Intent(context, WhiteboardActivity.class);
                    intent.putExtra("whiteBoardUrl", loudScreaming);
                    context.startActivity(intent);
                }


                /*final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog);

                WebView text = (WebView) dialog.findViewById(R.id.webView);
                text.getSettings().setJavaScriptEnabled(true);
                text.loadUrl(loudScreaming);

                ImageView dialogButton = dialog.findViewById(R.id.btn_dialog);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();*/
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, e.getMessage());
                Log.e("ERROR", e.toString());
            }
        });

    }

    public void openActionContainer() {
        ivArrow.setRotation(45f);
        isOpen = true;
        Animation rightAnimate = AnimationUtils.loadAnimation(getContext(), R.anim.animate_right_slide);
        cl_ActionContainer.startAnimation(rightAnimate);
        cl_ActionContainer.setVisibility(View.VISIBLE);
    }

    public void closeActionContainer() {
        ivArrow.setRotation(0);
        isOpen = false;
        Animation leftAnim = AnimationUtils.loadAnimation(getContext(), R.anim.animate_left_slide);
        cl_ActionContainer.startAnimation(leftAnim);
        cl_ActionContainer.setVisibility(GONE);
    }

    public void startRecord() {
        etComposeBox.setVisibility(GONE);
        recordTime.setBase(SystemClock.elapsedRealtime());
        recordTime.start();
        ivArrow.setVisibility(GONE);
        voiceSeekbar.setVisibility(GONE);
        voiceMessageLayout.setVisibility(View.VISIBLE);
        audioRecordView.recreate();
        audioRecordView.setVisibility(View.VISIBLE);
        startRecording();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startPlayingAudio(String path) {
        try {
            if (timerRunnable != null) {
                seekHandler.removeCallbacks(timerRunnable);
                timerRunnable = null;
            }

            mediaPlayer.reset();
            if (Utils.hasPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } else {
                ((Activity) context).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        StringContract.RequestCode.READ_STORAGE);
            }

            final int duration = mediaPlayer.getDuration();
            voiceSeekbar.setMax(duration);
            recordTime.setBase(SystemClock.elapsedRealtime());
            recordTime.start();

            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    int pos = mediaPlayer.getCurrentPosition();
                    voiceSeekbar.setProgress(pos);

                    if (mediaPlayer.isPlaying() && pos < duration) {
//                        audioLength.setText(Utils.convertTimeStampToDurationTime(player.getCurrentPosition()));
                        seekHandler.postDelayed(this, 100);
                    } else {
                        seekHandler
                                .removeCallbacks(timerRunnable);
                        timerRunnable = null;
                    }
                }

            };
            seekHandler.postDelayed(timerRunnable, 100);
            mediaPlayer.setOnCompletionListener(mp -> {
                seekHandler
                        .removeCallbacks(timerRunnable);
                timerRunnable = null;
                mp.stop();
                recordTime.stop();
//                audioLength.setText(Utils.convertTimeStampToDurationTime(duration));
                voiceSeekbar.setProgress(0);
//                playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            });

        } catch (Exception e) {
            Log.e("playAudioError: ", e.getMessage());
            stopPlayingAudio();
            ;
        }
    }

    private void stopPlayingAudio() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    private void startRecording() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            audioFileNameWithPath = Utils.getOutputMediaFile(getContext());
            mediaRecorder.setOutputFile(audioFileNameWithPath);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int currentMaxAmp = 0;
                    try {
                        currentMaxAmp = mediaRecorder != null ? mediaRecorder.getMaxAmplitude() : 0;
                        audioRecordView.update(currentMaxAmp);
                        if (mediaRecorder == null)
                            timer = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 100);
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stopRecording(boolean isCancel) {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                if (isCancel) {
                    new File(audioFileNameWithPath).delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
