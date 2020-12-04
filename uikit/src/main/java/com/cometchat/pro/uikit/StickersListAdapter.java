package com.cometchat.pro.uikit;


import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Attachment;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;

import constant.StringContract;
import listeners.ComposeActionListener;
import screen.CometChatForwardMessageScreenActivity;
import utils.MediaUtils;
import utils.Utils;


public class StickersListAdapter extends RecyclerView.Adapter<StickersListAdapter.ViewHolder> {
    private ArrayList<DefaultSticker> listFoodModel = new ArrayList<>();
    private Context context;
    private static final String TAG = ComposeBox.class.getSimpleName();

    SharedPreferences sp;
    String Ids;
    String Types;
    BottomSheetDialog dialog;
    String tab;
    String team_logs_id;
    String memberId;
    String teamId;
    ComposeActionListener composeActionListener;


    public StickersListAdapter(ArrayList<DefaultSticker> listFoodModel, Context context, String tab, String team_logs_id, String memberId, String teamId, BottomSheetDialog dialog, ComposeActionListener composeActionListener) {
        this.listFoodModel = listFoodModel;
        this.context = context;
        this.dialog = dialog;
        this.tab = tab;
        this.team_logs_id = team_logs_id;
        this.memberId = memberId;
        this.teamId = teamId;
        this.composeActionListener = composeActionListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stickers, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DefaultSticker model = listFoodModel.get(position);
        sp = context.getSharedPreferences("login", context.MODE_PRIVATE);
        Ids = sp.getString("UserIds", Ids);
        Types = sp.getString("types", Types);

        Glide.with(context)
                .load(Uri.parse(model.getStickerUrl()))
                .into(holder.imgStickers);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiverID = Ids;
                String receiverType = Types;

                String url = listFoodModel.get(position).getStickerUrl();

                File file = new File(url);
                URL Fileurl = null;
                String type = null;
                String fileExt = null;
                File f = null;

                try {
                    Fileurl = new URL(url);
                    f = new File(String.valueOf(Fileurl));

                    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
                    if (extension != null) {
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        type = mime.getMimeTypeFromExtension(extension);
                    }
                    fileExt = MimeTypeMap.getFileExtensionFromUrl(file.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                JSONArray languageArray = new JSONArray();
                languageArray.put("");

                JSONObject stickerData = new JSONObject();

                if (tab.equals("1")) {

                    try {
                        stickerData.put("team_logs_id", 0);
                        stickerData.put("message_translation_languages", languageArray);
                        stickerData.put("url", listFoodModel.get(position).getStickerUrl());
                        stickerData.put("name", listFoodModel.get(position).getStickerName());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (tab.equals("3")) {
                    if (memberId != null && teamId != null) {
                        team_logs_id = memberId + "_-" + teamId + "_-" + tab;
                        try {
                            stickerData.put("team_logs_id", 0);
                            stickerData.put("message_translation_languages", languageArray);
                            stickerData.put("url", listFoodModel.get(position).getStickerUrl());
                            stickerData.put("name", listFoodModel.get(position).getStickerName());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (tab.equals("4")) {
                    if (memberId != null && teamId != null) {
                        team_logs_id = memberId + "_-" + teamId + "_-" + tab;
                        try {
                            stickerData.put("team_logs_id", 0);
                            stickerData.put("message_translation_languages", languageArray);
                            stickerData.put("url", listFoodModel.get(position).getStickerUrl());
                            stickerData.put("name", listFoodModel.get(position).getStickerName());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


                File file1 = new File(listFoodModel.get(position).getStickerUrl());
                Uri file2 = Uri.fromFile(new File(listFoodModel.get(position).getStickerUrl()));
                String fileExt2 = MimeTypeMap.getFileExtensionFromUrl(file2.toString());
                String filesize = String.valueOf(file1.length());
                String filepath = file.getPath();
                String filename = filepath.substring(filepath.lastIndexOf("/") + 1);

                long length = file1.length();
                length = length / 1024;
/*

                String messageType = CometChatConstants.MESSAGE_TYPE_IMAGE;

                MediaMessage message = new MediaMessage(receiverID, null, messageType, receiverType);

                Attachment attachment = new Attachment();
                attachment.setFileUrl(filepath);
                attachment.setFileMimeType(fileExt2);
                attachment.setFileSize(Integer.parseInt(filesize));
                attachment.setFileExtension(fileExt2);
                attachment.setFileName(filename);
                message.setAttachment(attachment);

                CometChat.sendMediaMessage(message, new CometChat.CallbackListener<MediaMessage>() {
                    @Override
                    public void onSuccess(MediaMessage mediaMessage) {
                        Log.d(TAG, "Media message sent successfully: " + mediaMessage.toString());
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Log.d(TAG, "Media message sending failed with exception: " + e.getMessage());
                    }
                });
*/


                sendCustomMessage(StringContract.IntentStrings.STICKERS, stickerData, receiverID, receiverType);

            }
        });
    }

    private void sendCustomMessage(String customType, JSONObject customData, String Ids, String type) {
        CustomMessage customMessage;

        if (type.equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_USER))
            customMessage = new CustomMessage(Ids, CometChatConstants.RECEIVER_TYPE_USER, customType, customData);
        else
            customMessage = new CustomMessage(Ids, CometChatConstants.RECEIVER_TYPE_GROUP, customType, customData);

        CometChat.sendCustomMessage(customMessage, new CometChat.CallbackListener<CustomMessage>() {
            @Override
            public void onSuccess(CustomMessage customMessage) {

            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listFoodModel.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        ImageView imgStickers;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.imgIdss);
            imgStickers = itemView.findViewById(R.id.stickesImgs);
            relativeLayout = itemView.findViewById(R.id.rlvs);

        }
    }
}
