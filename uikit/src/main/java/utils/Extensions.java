package utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.uikit.Sticker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import listeners.ExtensionResponseListener;

public class Extensions {

    private static final String TAG = "Extensions";

    public static boolean getImageModeration(Context context, BaseMessage baseMessage) {
        boolean result = false;
        try {
            HashMap<String, JSONObject> extensionList = extensionCheck(baseMessage);
            if (extensionList != null && extensionList.containsKey("imageModeration")) {
                JSONObject imageModeration = extensionList.get("imageModeration");
                String confidence = imageModeration.getString("confidence");
                if (Integer.parseInt(confidence) > 50) {
                    result = true;
                } else {
                    result = false;
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static String getThumbnailGeneration(Context context, BaseMessage baseMessage) {
        String resultUrl = null;
        try {
            HashMap<String, JSONObject> extensionList = extensionCheck(baseMessage);
            if (extensionList != null && extensionList.containsKey("thumbnailGeneration")) {
                JSONObject thumbnailGeneration = extensionList.get("thumbnailGeneration");
                resultUrl = thumbnailGeneration.getString("url_small");
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return resultUrl;
    }


    public static List<String> getSmartReplyList(BaseMessage baseMessage) {

        HashMap<String, JSONObject> extensionList = extensionCheck(baseMessage);
        if (extensionList != null && extensionList.containsKey("smartReply")) {
            JSONObject replyObject = extensionList.get("smartReply");
            List<String> replyList = new ArrayList<>();
            try {
                replyList.add(replyObject.getString("reply_positive"));
                replyList.add(replyObject.getString("reply_neutral"));
                replyList.add(replyObject.getString("reply_negative"));
            } catch (Exception e) {
                Log.e(TAG, "onSuccess: " + e.getMessage());
            }
            return replyList;
        }
        return null;
    }


    public static HashMap<String, JSONObject> extensionCheck(BaseMessage baseMessage) {
        JSONObject metadata = baseMessage.getMetadata();
        HashMap<String, JSONObject> extensionMap = new HashMap<>();
        try {
            if (metadata != null) {
                JSONObject injectedObject = metadata.getJSONObject("@injected");
                if (injectedObject != null && injectedObject.has("extensions")) {
                    JSONObject extensionsObject = injectedObject.getJSONObject("extensions");
                    if (extensionsObject != null && extensionsObject.has("link-preview")) {
                        JSONObject linkPreviewObject = extensionsObject.getJSONObject("link-preview");
                        JSONArray linkPreview = linkPreviewObject.getJSONArray("links");
                        if (linkPreview.length() > 0) {
                            extensionMap.put("linkPreview", linkPreview.getJSONObject(0));
                        }

                    }
                    if (extensionsObject != null && extensionsObject.has("smart-reply")) {
                        extensionMap.put("smartReply", extensionsObject.getJSONObject("smart-reply"));
                    }
                    if (extensionsObject != null && extensionsObject.has("message-translation")) {
                        extensionMap.put("messageTranslation", extensionsObject.getJSONObject("message-translation"));
                    }
                    if (extensionsObject != null && extensionsObject.has("profanity-filter")) {
                        extensionMap.put("profanityFilter", extensionsObject.getJSONObject("profanity-filter"));
                    }
                    if (extensionsObject != null && extensionsObject.has("image-moderation")) {
                        extensionMap.put("imageModeration", extensionsObject.getJSONObject("image-moderation"));
                    }
                    if (extensionsObject != null && extensionsObject.has("sentiment-analysis")) {
                        extensionMap.put("sentimentAnalysis", extensionsObject.getJSONObject("sentiment-analysis"));
                    }
                }
                return extensionMap;
            } else
                return null;
        } catch (Exception e) {
            Log.e(TAG, "isLinkPreview: " + e.getMessage());
        }
        return null;
    }



    public static boolean checkSentiment(BaseMessage baseMessage) {
        boolean result = false;
        HashMap<String, JSONObject> extensionList = extensionCheck(baseMessage);
        try {
            if (extensionList.containsKey("sentimentAnalysis")) {
                JSONObject sentimentAnalysis = extensionList.get("sentimentAnalysis");
                String str = sentimentAnalysis.getString("sentiment");
                if (str.equals("negative"))
                    result = true;
                else
                    result = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "checkSentiment: " + e.getMessage());
        }
        return result;
    }

    public static String checkProfanityMessage(BaseMessage baseMessage) {
        String result = ((TextMessage) baseMessage).getText();
        HashMap<String, JSONObject> extensionList = Extensions.extensionCheck(baseMessage);
        if (extensionList != null) {
            try {
                if (extensionList.containsKey("profanityFilter")) {
                    JSONObject profanityFilter = extensionList.get("profanityFilter");
                    String profanity = profanityFilter.getString("profanity");
                    String cleanMessage = profanityFilter.getString("message_clean");
                    if (profanity.equals("no"))
                        result = ((TextMessage) baseMessage).getText();
                    else
                        result = cleanMessage;
                } else {
                    result = ((TextMessage) baseMessage).getText().trim();
                }
            } catch (Exception e) {
                Log.e(TAG, "checkProfanityMessage:Error: " + e.getMessage());
            }
        }
        return result;
    }

    public static void fetchStickers(ExtensionResponseListener extensionResponseListener) {
        CometChat.callExtension("stickers", "GET", "/v1/fetch", null, new CometChat.CallbackListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                extensionResponseListener.OnResponseSuccess(jsonObject);
            }
            @Override
            public void onError(CometChatException e) {
                extensionResponseListener.OnResponseFailed(e);
            }
        });
    }

    public static HashMap<String,List<Sticker>> extractStickersFromJSON(JSONObject jsonObject) {
        List<Sticker> stickers = new ArrayList<>();
        if (jsonObject != null) {
            try {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                JSONArray defaultStickersArray = dataObject.getJSONArray("defaultStickers");
                Log.d(TAG, "getStickersList: defaultStickersArray "+defaultStickersArray.length());
                for (int i = 0; i < defaultStickersArray.length(); i++) {
                    JSONObject stickerObject = defaultStickersArray.getJSONObject(i);
                    String stickerOrder = stickerObject.getString("stickerOrder");
                    String stickerSetId = stickerObject.getString("stickerSetId");
                    String stickerUrl = stickerObject.getString("stickerUrl");
                    String stickerSetName = stickerObject.getString("stickerSetName");
                    String stickerName = stickerObject.getString("stickerName");
                    Sticker sticker = new Sticker(stickerName,stickerUrl,stickerSetName);
                    stickers.add(sticker);
                }
                if (dataObject.has("customStickers")) {
                    JSONArray customSticker = dataObject.getJSONArray("customStickers");
                    Log.d(TAG, "getStickersList: customStickersArray " + customSticker.length());
                    for (int i = 0; i < customSticker.length(); i++) {
                        JSONObject stickerObject = defaultStickersArray.getJSONObject(i);
                        String stickerOrder = stickerObject.getString("stickerOrder");
                        String stickerSetId = stickerObject.getString("stickerSetId");
                        String stickerUrl = stickerObject.getString("stickerUrl");
                        String stickerSetName = stickerObject.getString("stickerSetName");
                        String stickerName = stickerObject.getString("stickerName");
                        Sticker sticker = new Sticker(stickerName, stickerUrl, stickerSetName);
                        stickers.add(sticker);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        HashMap<String,List<Sticker>> stickerMap = new HashMap();
        for (int i=0;i<stickers.size()-1;i++) {
            if (stickerMap.containsKey(stickers.get(i).getSetName())) {
                stickerMap.get(stickers.get(i).getSetName()).add(stickers.get(i));
            } else {
                List<Sticker> list = new ArrayList<>();
                list.add(stickers.get(i));
                stickerMap.put(stickers.get(i).getSetName(),list);
            }
        }
        return stickerMap;
    }

}
