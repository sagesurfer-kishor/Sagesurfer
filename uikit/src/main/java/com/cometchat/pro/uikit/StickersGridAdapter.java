package com.cometchat.pro.uikit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;

import java.io.File;
import java.util.ArrayList;


class StickersGridAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<DefaultSticker> listFoodModel = new ArrayList<>();

    SharedPreferences sp;
    String Ids;
    String Types;

    public StickersGridAdapter(ArrayList<DefaultSticker> listFoodModel, Context context) {
        this.listFoodModel = listFoodModel;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return listFoodModel.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        {
            ViewHolder viewHolder;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            DefaultSticker model = listFoodModel.get(position);

            sp = mContext.getSharedPreferences("login", mContext.MODE_PRIVATE);
            Ids = sp.getString("UserIds", Ids);
            Types = sp.getString("types", Types);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_stickers, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.mTextView = (TextView) convertView.findViewById(R.id.imgIds);
                viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.stickesImgs);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Glide.with(mContext)
                    .load(Uri.parse(model.getStickerUrl()))
                    .into(viewHolder.mImageView);


            viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* String ss = listFoodModel.get(position).getStickerUrl();
                    Log.e("****", ss);
                    File audioFile = new File(ss);

                    String receiverID = Ids;
                    String messageType = CometChatConstants.MESSAGE_TYPE_IMAGE;
                    String receiverType = Types;
                    Log.d("TAG", "successfully: " + receiverID + "," + messageType + "," + receiverType + "," + audioFile);

                    MediaMessage mediaMessage = new MediaMessage(receiverID, audioFile, messageType, receiverType);
                    CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
                        @Override
                        public void onSuccess(MediaMessage mediaMessage) {
                            Log.d("TAG", "Media message sent successfully: " + mediaMessage.toString());
                        }

                        @Override
                        public void onError(CometChatException e) {
                            Log.d("TAG", "Media message sending failed with exception: " + e.getMessage());
                        }
                    });*/


                    // String messageText = "<img src=" + listFoodModel.get(position).getStickerUrl() + ">";

                    String s = "\"" + listFoodModel.get(position).getStickerUrl() + "\"";

                    Log.e("0", s);

                    String receiverID = Ids;
                    String messageText = "<img src=" + s + ">";
                    String receiverType = Types;

                    Log.d("TAG", "successfully: " + messageText);
                    TextMessage textMessage = new TextMessage(receiverID, messageText, receiverType);

                    CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
                        @Override
                        public void onSuccess(TextMessage textMessage) {
                            Log.d("TAG", "Message sent successfully: " + textMessage.toString());
                            ((Activity)mContext).finish();
                        }

                        @Override
                        public void onError(CometChatException e) {
                            Log.d("TAG", "Message sending failed with exception: " + e.getMessage());

                        }
                    });

                }
            });
            return convertView;
        }
    }

    public static class ViewHolder {
        TextView mTextView;
        ImageView mImageView;
    }

}
