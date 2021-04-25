package com.sagesurfer.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.Counters_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Error_;
import com.storage.database.constants.TableList_;
import com.storage.database.operations.DatabaseDeleteRecord_;
import com.storage.preferences.Preferences;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class PerformGetCounterTask {

    private static final String TAG = PerformGetCounterTask.class.getSimpleName();
    public static Counters_ getCounters(Context _context) {
        Counters_ counters_ = null;
        Preferences.initialize(_context);
        String url = Preferences.get(General.DOMAIN) + Urls_.COUNTER_URL;
        HashMap<String, String> map = new HashMap<>();
        map.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        RequestBody requestBody = NetworkCall_.make(map, url, TAG, _context);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, _context);
                if (response != null && Error_.oauth(response, _context) == 0) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
                    if (jsonObject.has("details")) {
                        JsonObject object = jsonObject.get("details").getAsJsonObject();
                        //JsonArray jarray = jsonObject.get("details").getAsJsonArray();
                        //JsonObject object = jarray.get(0).getAsJsonObject();
                        if (!object.has("error")) {
                            new DeleteRecords(jsonObject).execute(_context);
                            Gson gson = new Gson();
                            counters_ = gson.fromJson(jsonObject.toString(), Counters_.class);
                            Preferences.save(General.COUNT, counters_);
                        }
                    } else {
                        if (!jsonObject.has("error")) {
                            new DeleteRecords(jsonObject).execute(_context);
                            Gson gson = new Gson();
                            counters_ = gson.fromJson(jsonObject.toString(), Counters_.class);
                            Preferences.save(General.COUNT, counters_);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return counters_;
    }

    private static void deleteData(JsonObject counterJson, Context context) {
        JsonObject jsonObject = counterJson.get(General.ID).getAsJsonObject();
        if (jsonObject == null) {
            return;
        }

        DatabaseDeleteRecord_ deleteOperation = new DatabaseDeleteRecord_(context);

        String announcement = jsonObject.get(TableList_.TABLE_ANNOUNCEMENT).getAsString();
        String task_list = jsonObject.get("tasklist").getAsString();
        String unjoin = jsonObject.get("unjoin").getAsString();
        //String events = jsonObject.get(TableList_.TABLE_EVENTS).getAsString();
        String fms = jsonObject.get(TableList_.TABLE_FMS).getAsString();
        //String team_talk = jsonObject.get(TableList_.TABLE_TEAM_TALK).getAsString();

        if (announcement != null && announcement.length() > 0) {
            List<String> announcementList = Arrays.asList(announcement.split(","));
            if (announcementList.size() > 0) {
                for (int i = 0; i < announcementList.size(); i++) {
                    deleteOperation.deleteRecord(TableList_.TABLE_ANNOUNCEMENT, announcementList.get(i), General.ID);
                }
            }
        }

        if (task_list != null && task_list.length() > 0) {
            List<String> taskList = Arrays.asList(task_list.split(","));
            if (taskList.size() > 0) {
                for (int i = 0; i < taskList.size(); i++) {
                    deleteOperation.deleteRecord(TableList_.TABLE_TASK_LIST, taskList.get(i), General.ID);
                }
            }
        }

        /*if (events != null && events.length() > 0) {
            List<String> eventList = Arrays.asList(events.split(","));
            if (eventList.size() > 0) {
                for (int i = 0; i < eventList.size(); i++) {
                    deleteOperation.deleteRecord(TableList_.TABLE_EVENTS, eventList.get(i), General.ID);
                }
            }
        }*/

        if (fms != null && fms.length() > 0) {
            List<String> fmsList = Arrays.asList(fms.split(","));
            if (fmsList.size() > 0) {
                for (int i = 0; i < fmsList.size(); i++) {
                    deleteOperation.deleteRecord(TableList_.TABLE_FMS, fmsList.get(i), General.ID);
                }
            }
        }

        /*if (team_talk != null && team_talk.length() > 0) {
            List<String> teamTalkList = Arrays.asList(team_talk.split(","));
            if (teamTalkList.size() > 0) {
                for (int i = 0; i < teamTalkList.size(); i++) {
                    deleteOperation.deleteRecord(TableList_.TABLE_TEAM_TALK, teamTalkList.get(i), General.ID);
                }
            }
        }*/

        if (unjoin != null && unjoin.length() > 0) {
            if (!unjoin.equalsIgnoreCase("0")) {
                List<String> team_list = Arrays.asList(unjoin.split(","));
                if (team_list.size() > 0) {
                    for (int i = 0; i < team_list.size(); i++) {
                        deleteOperation.deleteRecord(TableList_.TABLE_ANNOUNCEMENT, team_list.get(i), General.GROUP_ID);
                        deleteOperation.deleteRecord(TableList_.TABLE_TASK_LIST, team_list.get(i), General.GROUP_ID);
                        deleteOperation.deleteRecord(TableList_.TABLE_EVENTS, team_list.get(i), General.GROUP_ID);
                        deleteOperation.deleteRecord(TableList_.TABLE_FMS, team_list.get(i), General.GROUP_ID);
                        deleteOperation.deleteRecord(TableList_.TABLE_TEAM_TALK, team_list.get(i), General.GROUP_ID);
                    }
                }
            }
        }
    }

    private static class DeleteRecords extends AsyncTask<Context, Void, Void> {
        JsonObject jsonObject;

        DeleteRecords(JsonObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected Void doInBackground(Context... context) {
            deleteData(jsonObject, context[0]);
            return null;
        }
    }
}
