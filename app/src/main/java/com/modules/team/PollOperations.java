package com.modules.team;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;
import com.storage.preferences.Preferences;

import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 10-08-2017
 *         Last Modified on 14-12-2017
 */

/*
* This class contain poll operation methods
* Network call to add/change vote, delete vote and like/unlike
* Give color and drawable for progress bar for vote percentage
*/

class PollOperations {

    static int getPercent(int total, int value) {
        if (total == 0 || value == 0) {
            return 0;
        }
        return (value * 100) / total;
    }

    static int getVoteColor(int position, Context context) {
        switch (position) {
            case 0:
                return context.getResources().getColor(R.color.poll_vote_1);
            case 1:
                return context.getResources().getColor(R.color.poll_vote_2);
            case 2:
                return context.getResources().getColor(R.color.poll_vote_3);
            case 3:
                return context.getResources().getColor(R.color.poll_vote_4);
            case 4:
                return context.getResources().getColor(R.color.colorPrimary);
            default:
                return context.getResources().getColor(R.color.colorPrimary);
        }
    }

    static Drawable getProgressDrawable(int position, Context context) {
        switch (position) {
            case 0:
                return context.getResources().getDrawable(R.drawable.vote_progress_one);
            case 1:
                return context.getResources().getDrawable(R.drawable.vote_progress_two);
            case 2:
                return context.getResources().getDrawable(R.drawable.vote_progress_three);
            case 3:
                return context.getResources().getDrawable(R.drawable.vote_progress_four);
            case 4:
                return context.getResources().getDrawable(R.drawable.vote_progress_primary);
            default:
                return context.getResources().getDrawable(R.drawable.vote_progress_primary);
        }
    }


    static Poll_ vote(long poll_id, long vote_id, String TAG, Context context, Activity activity) {
        Poll_ poll_ = new Poll_();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VOTE);
        requestMap.put(General.ID, "" + poll_id);
        requestMap.put("poll_option_id", "" + vote_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.POLL_OPERATION_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);

        try {
            String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
            if (response != null) {
                if (Error_.oauth(response, context) == 13) {
                    poll_.setStatus(13);
                    return poll_;
                }

                JsonObject jsonObject = GetJson_.getObject(response, Actions_.VOTE);
                int status = jsonObject.get(General.STATUS).getAsInt();
                if (status == 1) {
                    Gson gson = new Gson();
                    poll_ = gson.fromJson(jsonObject.get("poll"), Poll_.class);
                    poll_.setStatus(1);
                    return poll_;
                } else {
                    poll_.setStatus(status);
                    return poll_;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        poll_.setStatus(12);
        return poll_;
    }

    static Poll_ deleteVote(long poll_id, String TAG, Context context, Activity activity) {
        Poll_ poll_ = new Poll_();

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.DELETE_VOTE);
        requestMap.put(General.ID, "" + poll_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.POLL_OPERATION_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);

        try {
            String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
            if (response != null) {
                if (Error_.oauth(response, context) == 13) {
                    poll_.setStatus(13);
                    return poll_;
                }

                JsonObject jsonObject = GetJson_.getObject(response, Actions_.DELETE_VOTE);
                int status = jsonObject.get(General.STATUS).getAsInt();
                if (status == 1) {
                    Gson gson = new Gson();
                    poll_ = gson.fromJson(jsonObject.get("poll"), Poll_.class);
                    poll_.setStatus(1);
                    return poll_;
                } else {
                    poll_.setStatus(status);
                    return poll_;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        poll_.setStatus(12);
        return poll_;
    }

    static int[] likeUnlike(long poll_id, int is_like, String TAG, Context context, Activity activity) {
        int[] result = {12, 0, is_like};
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.LIKE_UNLIKE);
        requestMap.put("feed_id", "" + poll_id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.WALL_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, context, activity);
                if (response != null) {
                    if (Error_.oauth(response, context) == 13) {
                        result[0] = 13;
                    } else {
                        JsonArray jsonArray = GetJson_.getArray(response, General.LIKE);
                        if (jsonArray == null) {
                            result[0] = 11;
                        } else {
                            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                            if (jsonObject.has(General.STATUS)) {
                                int status = jsonObject.get(General.STATUS).getAsInt();
                                if (status == 1) {
                                    result[1] = jsonObject.get(General.COUNT).getAsInt();
                                    if (is_like == 1) {
                                        result[2] = 0;
                                    } else {
                                        result[2] = 1;
                                    }
                                }
                                result[0] = status;

                            } else {
                                result[0] = 11;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
