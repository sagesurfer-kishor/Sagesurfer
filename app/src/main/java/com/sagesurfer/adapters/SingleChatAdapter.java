package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Chat;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckFileType;
import com.sagesurfer.library.FileCheck;
import com.sagesurfer.library.GetThumbnails;
import com.sagesurfer.library.HtmlRemover;
import com.sagesurfer.sage.emoji.custom.EmojiTextView;
import com.storage.database.operations.DatabaseUpdate_;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 19/02/2018
 */

public class SingleChatAdapter extends ArrayAdapter<HashMap<String, String>> {

    private ArrayList<HashMap<String, String>> messageList = new ArrayList<>();
    private final Activity activity;
    private ViewHolder holder;
    private final DatabaseUpdate_ updateOperations;

    public SingleChatAdapter(Activity activity, ArrayList<HashMap<String, String>> messageList, String friend_name) {
        super(activity, 0, messageList);
        this.messageList = messageList;
        this.activity = activity;
        updateOperations = new DatabaseUpdate_(activity.getApplicationContext());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        HashMap<String, String> currentMap;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            view = layoutInflater.inflate(R.layout.chat_message_item_layout, parent, false);
            holder = new ViewHolder();

            holder.messageText = (EmojiTextView) view.findViewById(R.id.chat_message_item_my_message);
            holder.myTime = (TextView) view.findViewById(R.id.chat_message_my_time);

            holder.fromName = (TextView) view.findViewById(R.id.chat_message_item_name);
            holder.fromTime = (TextView) view.findViewById(R.id.chat_message_from_time);
            holder.fromMessage = (EmojiTextView) view.findViewById(R.id.chat_message_item_message);

            holder.myLayout = (LinearLayout) view.findViewById(R.id.chat_message_item_my_layout);
            holder.fromLayout = (LinearLayout) view.findViewById(R.id.chat_message_item_from_layout);

            holder.myImage = (ImageView) view.findViewById(R.id.chat_message_item_my_image);
            holder.fromImage = (ImageView) view.findViewById(R.id.chat_message_item_from_image);

            holder.fromProgress = (ProgressBar) view.findViewById(R.id.chat_message_from_pb);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        currentMap = messageList.get(position);
        //Log.e("currentMap"," => "+currentMap);
        int self = Integer.parseInt(currentMap.get(Chat.SELF));
        if (Integer.parseInt(currentMap.get(General.IS_READ)) == 0) {
            updateOperations.updateMessageRead(currentMap.get(General.ID));
        }
        if (self == 1) {
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.fromLayout.setVisibility(View.GONE);
            setMy(currentMap);
        } else {
            holder.myLayout.setVisibility(View.GONE);
            holder.fromLayout.setVisibility(View.VISIBLE);
            setFrom(currentMap);
        }

        return view;
    }

    @SuppressWarnings("deprecation")
    private void setMy(HashMap<String, String> myMap) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        String timestamp = myMap.get(Chat.SENT);
        //String time = TimeSet.getChatTime(Long.parseLong(timestamp));
        holder.myTime.setText(timestamp);

        int message_type = Integer.parseInt(myMap.get(Chat.MESSAGE_TYPE));
        switch (message_type) {
            case Chat.INTENT_MESSAGE:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                String message = myMap.get(Chat.MESSAGE);
                if (message.length() < 25) {
                    holder.myLayout.setOrientation(LinearLayout.HORIZONTAL);
                } else {
                    holder.myLayout.setOrientation(LinearLayout.VERTICAL);
                }
                holder.messageText.setEmojiText(HtmlRemover.clearHtml(message));
                break;
            case Chat.INTENT_IMAGE:
                holder.myImage.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.GONE);
                switch (Integer.parseInt(myMap.get(General.DOWNLOAD_STATUS))) {
                    case 0:
                        if (FileCheck.isExist(myMap.get(Chat.MESSAGE))) {
                            Bitmap bitmap = BitmapFactory.decodeFile(myMap.get(Chat.MESSAGE), options);
                            BitmapDrawable bit = new BitmapDrawable(bitmap);
                            holder.myImage.setBackgroundDrawable(bit);
                        } else {
                            //holder.myImage.setBackgroundResource(R.drawable.ic_jpg_chat_doc);
                            holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(myMap.get(Chat.MESSAGE)));
                        }
                        break;
                    case 1:
                        //holder.myImage.setBackgroundResource(R.drawable.ic_image_download);
                        holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(myMap.get(Chat.MESSAGE)));
                        break;
                    case 2:
                        break;
                }
                break;
            case Chat.INTENT_VIDEO:
                holder.myImage.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.GONE);
                switch (Integer.parseInt(myMap.get(General.DOWNLOAD_STATUS))) {
                    case 0:
                        if (FileCheck.isExist(myMap.get(Chat.MESSAGE))) {
                            //holder.myImage.setBackgroundResource(R.drawable.ic_video_play);
                            holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(myMap.get(Chat.MESSAGE)));
                        } else {
                            //holder.myImage.setBackgroundResource(R.drawable.ic_video_download);
                            holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(myMap.get(Chat.MESSAGE)));
                        }
                        break;
                    case 1:
                        //holder.myImage.setBackgroundResource(R.drawable.ic_video_download);
                        holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(myMap.get(Chat.MESSAGE)));
                        break;
                    case 2:
                        break;
                }
                break;
            case Chat.INTENT_AUDIO:
                holder.myImage.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.GONE);

                switch (Integer.parseInt(myMap.get(General.DOWNLOAD_STATUS))) {
                    case 0:
                        if (FileCheck.isExist(myMap.get(Chat.MESSAGE))) {
                            //holder.myImage.setBackgroundResource(R.drawable.ic_audio_play);
                            holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(myMap.get(Chat.MESSAGE)));
                        }
                        break;
                    case 1:
                        //holder.myImage.setBackgroundResource(R.drawable.ic_audio_download);
                        holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(myMap.get(Chat.MESSAGE)));
                        break;
                    case 2:
                        break;
                }
                break;
            case Chat.INTENT_DOCUMENT:
                holder.myImage.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.GONE);
                String file = myMap.get(Chat.MESSAGE);

                switch (Integer.parseInt(myMap.get(General.DOWNLOAD_STATUS))) {
                    case 0:
                        if (FileCheck.isExist(file)) {
                            if (CheckFileType.docFile(file)) {
                                //holder.myImage.setBackgroundResource(R.drawable.ic_doc_chat_doc);
                                holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else if (CheckFileType.pdfFile(file)) {
                                //holder.myImage.setBackgroundResource(R.drawable.ic_pdf_chat_doc);
                                holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else if (CheckFileType.xlsFile(file)) {
                                //holder.myImage.setBackgroundResource(R.drawable.ic_xsl_chat_doc);
                                holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else if (CheckFileType.textFile(file)) {
                                //holder.myImage.setBackgroundResource(R.drawable.ic_text_chat_doc);
                                holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else if (CheckFileType.pptFile(file)) {
                                //holder.myImage.setBackgroundResource(R.drawable.ic_ppt_chat_doc);
                                holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else {
                                //holder.myImage.setBackgroundResource(R.drawable.ic_other_chat_doc);
                                holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            }
                        } else {
                            //holder.myImage.setBackgroundResource(R.drawable.ic_other_chat_doc);
                            holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                        }
                        break;
                    case 1:
                        //holder.myImage.setBackgroundResource(R.drawable.ic_doc_download);
                        holder.myImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                        break;
                    case 2:
                        break;
                }
                break;
            case Chat.AVCHAT_CALL_ACCEPTED:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(myMap.get(Chat.MESSAGE));
                break;

            case Chat.AVCHAT_END_CALL:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(myMap.get(Chat.MESSAGE));
                break;

            case Chat.AVCHAT_CANCEL_CALL:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(myMap.get(Chat.MESSAGE));
                break;

            case Chat.AVCHAT_INCOMING_CALL:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(myMap.get(Chat.MESSAGE));
                break;

            case Chat.AVCHAT_REJECT_CALL:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(myMap.get(Chat.MESSAGE));
                break;
            case Chat.AVCHAT_BUSY_CALL:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(myMap.get(Chat.MESSAGE));
                break;
            case Chat.AVCHAT_NO_ANSWER:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(myMap.get(Chat.MESSAGE));
                break;
            case Chat.AVCHAT_BUSY_TONE:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(myMap.get(Chat.MESSAGE));
                break;
            default:
                holder.myImage.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(HtmlRemover.clearHtml(myMap.get(Chat.MESSAGE)));
                break;
        }
    }

    @SuppressWarnings("deprecation")
    private void setFrom(HashMap<String, String> fromMap) {
        holder.fromProgress.setVisibility(View.GONE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        String timestamp = fromMap.get(Chat.SENT);
        //String time = TimeSet.getChatTime(Long.parseLong(timestamp));
        holder.fromTime.setText(timestamp);

        int message_type = Integer.parseInt(fromMap.get(Chat.MESSAGE_TYPE));
        holder.fromName.setVisibility(View.GONE);
        switch (message_type) {
            case Chat.INTENT_MESSAGE:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                String message = fromMap.get(Chat.MESSAGE);
                if (message.length() < 25) {
                    holder.fromLayout.setOrientation(LinearLayout.HORIZONTAL);
                } else {
                    holder.fromLayout.setOrientation(LinearLayout.VERTICAL);
                }
                holder.fromMessage.setEmojiText(HtmlRemover.clearHtml(message));
                break;
            case Chat.INTENT_INVITE:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                String room_name = "Unknown";
                try {
                    JSONObject jsonObject = new JSONObject(fromMap.get(Chat.MESSAGE));
                    room_name = jsonObject.getString("chatroom_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SpannableString spanString = new SpannableString("Invited you to join chatroom " + room_name + "\nClick here to join the conversation...");
                spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
                spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
                holder.fromMessage.setText(spanString);
                break;
            case Chat.INTENT_IMAGE:
                holder.fromImage.setVisibility(View.VISIBLE);
                holder.fromMessage.setVisibility(View.GONE);

                switch (Integer.parseInt(fromMap.get(General.DOWNLOAD_STATUS))) {
                    case 0:
                        if (FileCheck.isExist(fromMap.get(Chat.MESSAGE))) {
                            Bitmap bitmap = BitmapFactory.decodeFile(fromMap.get(Chat.MESSAGE), options);
                            BitmapDrawable bit = new BitmapDrawable(bitmap);
                            holder.fromImage.setBackgroundDrawable(bit);
                        } else {
                            //holder.fromImage.setImageResource(R.drawable.ic_jpg_chat_doc);
                            holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(fromMap.get(Chat.MESSAGE)));
                        }
                        break;
                    case 1:
                        //holder.fromImage.setBackgroundResource(R.drawable.ic_image_download);
                        holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(fromMap.get(Chat.MESSAGE)));
                        break;
                    case 2:
                        holder.fromProgress.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case Chat.INTENT_VIDEO:
                holder.fromImage.setVisibility(View.VISIBLE);
                holder.fromMessage.setVisibility(View.GONE);

                switch (Integer.parseInt(fromMap.get(General.DOWNLOAD_STATUS))) {
                    case 0:
                        if (FileCheck.isExist(fromMap.get(Chat.MESSAGE))) {
                            //holder.fromImage.setBackgroundResource(R.drawable.ic_video_play);
                            holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(fromMap.get(Chat.MESSAGE)));
                        }
                        break;
                    case 1:
                        //holder.fromImage.setBackgroundResource(R.drawable.ic_video_download);
                        holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(fromMap.get(Chat.MESSAGE)));
                        break;
                    case 2:
                        holder.fromProgress.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case Chat.INTENT_AUDIO:
                holder.fromImage.setVisibility(View.VISIBLE);
                holder.fromMessage.setVisibility(View.GONE);

                switch (Integer.parseInt(fromMap.get(General.DOWNLOAD_STATUS))) {
                    case 0:
                        if (FileCheck.isExist(fromMap.get(Chat.MESSAGE))) {
                            //holder.fromImage.setBackgroundResource(R.drawable.ic_audio_play);
                            holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(fromMap.get(Chat.MESSAGE)));
                        }
                        break;
                    case 1:
                        //holder.fromImage.setBackgroundResource(R.drawable.ic_audio_download);
                        holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(fromMap.get(Chat.MESSAGE)));
                        break;
                    case 2:
                        holder.fromProgress.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case Chat.INTENT_DOCUMENT:
                holder.fromImage.setVisibility(View.VISIBLE);
                holder.fromMessage.setVisibility(View.GONE);
                String file = fromMap.get(Chat.MESSAGE);

                switch (Integer.parseInt(fromMap.get(General.DOWNLOAD_STATUS))) {
                    case 0:

                        if (FileCheck.isExist(file)) {
                            if (CheckFileType.docFile(file)) {
                                //holder.fromImage.setBackgroundResource(R.drawable.ic_doc_chat_doc);
                                holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else if (CheckFileType.pdfFile(file)) {
                                //holder.fromImage.setBackgroundResource(R.drawable.ic_pdf_chat_doc);
                                holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else if (CheckFileType.xlsFile(file)) {
                                //holder.fromImage.setBackgroundResource(R.drawable.ic_xsl_chat_doc);
                                holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else if (CheckFileType.textFile(file)) {
                                //holder.fromImage.setBackgroundResource(R.drawable.ic_text_chat_doc);
                                holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else if (CheckFileType.pptFile(file)) {
                                //holder.fromImage.setBackgroundResource(R.drawable.ic_ppt_chat_doc);
                                holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            } else {
                                //holder.fromImage.setBackgroundResource(R.drawable.ic_other_chat_doc);
                                holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                            }
                        } else {
                            //holder.fromImage.setBackgroundResource(R.drawable.ic_other_chat_doc);
                            holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                        }
                        break;
                    case 1:
                        //holder.fromImage.setBackgroundResource(R.drawable.ic_doc_download);
                        holder.fromImage.setBackgroundResource(GetThumbnails.attachmentList(file));
                        break;
                    case 2:
                        holder.fromProgress.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case Chat.AVCHAT_CALL_ACCEPTED:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                holder.fromMessage.setText(fromMap.get(Chat.MESSAGE));
                break;

            case Chat.AVCHAT_END_CALL:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                holder.fromMessage.setText(fromMap.get(Chat.MESSAGE));
                break;

            case Chat.AVCHAT_CANCEL_CALL:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                holder.fromMessage.setText(fromMap.get(Chat.MESSAGE));
                break;

            case Chat.AVCHAT_INCOMING_CALL:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                holder.fromMessage.setText(fromMap.get(Chat.MESSAGE));
                break;

            case Chat.AVCHAT_REJECT_CALL:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                holder.fromMessage.setText(fromMap.get(Chat.MESSAGE));
                break;
            case Chat.AVCHAT_BUSY_CALL:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                holder.fromMessage.setText(fromMap.get(Chat.MESSAGE));
                break;
            case Chat.AVCHAT_NO_ANSWER:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                holder.fromMessage.setText(fromMap.get(Chat.MESSAGE));
                break;
            default:
                holder.fromImage.setVisibility(View.GONE);
                holder.fromMessage.setVisibility(View.VISIBLE);
                holder.fromMessage.setText(fromMap.get(Chat.MESSAGE));
                break;
        }
    }

    static class ViewHolder {
        TextView fromName, fromTime, myTime;
        EmojiTextView fromMessage, messageText;
        LinearLayout myLayout, fromLayout;
        ImageView myImage, fromImage;
        ProgressBar fromProgress, myProgress;
    }
}