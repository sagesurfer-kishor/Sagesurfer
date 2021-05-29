package com.sagesurfer.tasks;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.Teams_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Team_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 14-07-2017
 * Last Modified on 15-12-2017
 */


public class PerformGetTeamsTask {
    private static final String TAG = "PerformGetTeamsTask";
    public static ArrayList<Teams_> get(String action,  Context context, String tag, boolean isTeamDetails, Activity activity) {
        ArrayList<Teams_> groupList = new ArrayList<>();
        Preferences.initialize(context);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.CODE, Preferences.get(General.DOMAIN_CODE));
        requestMap.put(General.ISFORTEAMCHAT, "0");
        if (isTeamDetails) {
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
            requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        }
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMETCHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, tag, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, tag, context, activity);
                if (response != null) {
                    groupList = Team_.parseTeams(response, action, context, tag);
                    if (isTeamDetails) {
                        if (groupList.size() > 0) {
                            if (isTeamDetails) {
                                for (int i = 0; i < groupList.size(); i++) {
                                    Teams_ objTeam = groupList.get(i);
                                    if (String.valueOf(objTeam.getId()).equals(Preferences.get(General.GROUP_ID))) {
                                        Preferences.save(General.GROUP_ID, "" + objTeam.getId());
                                        Preferences.save(General.GROUP_NAME, objTeam.getName());
                                        Preferences.save(General.OWNER_ID, objTeam.getOwnerId());
                                        Preferences.save(General.MODERATOR, objTeam.getModerator());
                                        Preferences.save(General.PERMISSION, objTeam.getPermission());
                                        Preferences.save(General.IS_MODERATOR, objTeam.getIs_moderator());
                                        break;
                                    }
                                }

                            } else {
                                Preferences.save(General.GROUP_ID, "" + groupList.get(0).getId());
                                Preferences.save(General.GROUP_NAME, groupList.get(0).getName());
                                Preferences.save(General.OWNER_ID, groupList.get(0).getOwnerId());
                                Preferences.save(General.MODERATOR, groupList.get(0).getModerator());
                                Preferences.save(General.PERMISSION, groupList.get(0).getPermission());
                                Preferences.save(General.IS_MODERATOR, groupList.get(0).getIs_moderator());
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return groupList;
    }


    public static ArrayList<Teams_> getNormalTeams(String action, Context context, String tag, boolean isTeamDetails, Activity activity) {
        ArrayList<Teams_> groupList = new ArrayList<>();
        Preferences.initialize(context);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.CODE, Preferences.get(General.DOMAIN_CODE));
        requestMap.put(General.ISFORTEAMCHAT, "0");
        if (isTeamDetails) {
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
            requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        }
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_NORMAL_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, tag, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, tag, context, activity);
                if (response != null) {
                    groupList = Team_.parseTeams(response, action, context, tag);
                    if (isTeamDetails) {
                        if (groupList.size() > 0) {
                            if (isTeamDetails) {
                                for (int i = 0; i < groupList.size(); i++) {
                                    Teams_ objTeam = groupList.get(i);
                                    if (String.valueOf(objTeam.getId()).equals(Preferences.get(General.GROUP_ID))) {
                                        Preferences.save(General.GROUP_ID, "" + objTeam.getId());
                                        Preferences.save(General.GROUP_NAME, objTeam.getName());
                                        Preferences.save(General.OWNER_ID, objTeam.getOwnerId());
                                        Preferences.save(General.MODERATOR, objTeam.getModerator());
                                        Preferences.save(General.PERMISSION, objTeam.getPermission());
                                        Preferences.save(General.IS_MODERATOR, objTeam.getIs_moderator());
                                        break;
                                    }
                                }

                            } else {
                                Preferences.save(General.GROUP_ID, "" + groupList.get(0).getId());
                                Preferences.save(General.GROUP_NAME, groupList.get(0).getName());
                                Preferences.save(General.OWNER_ID, groupList.get(0).getOwnerId());
                                Preferences.save(General.MODERATOR, groupList.get(0).getModerator());
                                Preferences.save(General.PERMISSION, groupList.get(0).getPermission());
                                Preferences.save(General.IS_MODERATOR, groupList.get(0).getIs_moderator());
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return groupList;
    }


    public static ArrayList<Teams_> getTeams(String action, String teamType, Context context, String tag, boolean isTeamDetails, Activity activity) {
        ArrayList<Teams_> groupList = new ArrayList<>();
        Preferences.initialize(context);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.TEAM_TYPE, teamType);
        requestMap.put(General.CODE, Preferences.get(General.DOMAIN_CODE));
        if (isTeamDetails) {
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
            requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        }
        Log.e("requestMap", requestMap.toString());
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMETCHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, tag, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, tag, context, activity);
                Log.e("Response", response);
                if (response != null) {
                    groupList = Team_.parseTeams(response, action, context, tag);
                    if (isTeamDetails) {
                        if (groupList.size() > 0) {
                            Preferences.save(General.GROUP_ID, "" + groupList.get(0).getId());
                            Preferences.save(General.GROUP_NAME, groupList.get(0).getName());
                            Preferences.save(General.OWNER_ID, groupList.get(0).getOwnerId());
                            Preferences.save(General.MODERATOR, groupList.get(0).getModerator());
                            Preferences.save(General.PERMISSION, groupList.get(0).getPermission());
                            Preferences.save(General.IS_MODERATOR, groupList.get(0).getIs_moderator());
                            Preferences.save(General.TYPE, groupList.get(0).getType());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return groupList;
    }

    // make network call to change task status
    public static int changeStatus(Context context, String tag, String _status, int id, Activity activity) {
        int status = 12;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TASK_LIST_STATUS);
        requestMap.put(General.TASK_ID, "" + id);
        requestMap.put(General.TASK_STATUS, _status);
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.OTHER_OPERATIONS_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, tag, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, tag, context, activity);
                if (response != null && !response.equalsIgnoreCase("13")) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject object = jsonParser.parse(response).getAsJsonObject();
                    if (object.has(General.STATUS)) {
                        status = object.get(General.STATUS).getAsInt();
                    } else {
                        status = 11;
                    }
                } else {
                    status = 13;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    public static ArrayList<Teams_> getSearchTeams(String action, String teamType, Context context, String tag, String searchText, Activity activity) {
        ArrayList<Teams_> groupList = new ArrayList<>();
        Preferences.initialize(context);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.CODE, Preferences.get(General.DOMAIN_CODE));
        requestMap.put(General.SEARCH, searchText);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMETCHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, tag, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, tag, context, activity);
                if (response != null) {
                    groupList = Team_.parseTeams(response, action, context, tag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return groupList;
    }

    public static ArrayList<Teams_> getNormalSearchTeams(String action, String teamType, Context context, String tag, String searchText, Activity activity) {
        ArrayList<Teams_> groupList = new ArrayList<>();
        Preferences.initialize(context);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put(General.CODE, Preferences.get(General.DOMAIN_CODE));
        requestMap.put(General.SEARCH, searchText);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_NORMAL_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, tag, context, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, tag, context, activity);
                if (response != null) {
                    groupList = Team_.parseTeams(response, action, context, tag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return groupList;
    }

    public static ArrayList<Teams_>getMyteam(String action, String team_type, Context context, String tag, boolean isTeamDetails, Activity activity) {
        ArrayList<Teams_> groupList = new ArrayList<>();
        Preferences.initialize(context);

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);
        requestMap.put("user_id", Preferences.get(General.USER_ID));
        requestMap.put("team_type", team_type);
        requestMap.put("domain_code", Preferences.get(General.DOMAIN_CODE));
        requestMap.put("debug", "1");
        requestMap.put(General.CODE, Preferences.get(General.DOMAIN_CODE));
        requestMap.put(General.ISFORTEAMCHAT, "0");
        if (isTeamDetails) {
            requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
            requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        }

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_COMETCHAT_TEAMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, tag, context, activity);
        if (requestBody != null) {
            Log.i(TAG, "getMyteam: request"+requestBody);
            try {
                String response = NetworkCall_.post(url, requestBody, tag, context, activity);
                if (response != null) {
                    Log.e("cometchat_teams", response);
                    groupList = Team_.parseTeams(response, action, context, tag);
                    if (isTeamDetails) {
                        Log.e("^^^^^", groupList.toString());
                        if (groupList.size() > 0) {
                            if (isTeamDetails) {
                                for (int i = 0; i < groupList.size(); i++) {
                                    Teams_ objTeam = groupList.get(i);
                                    if (String.valueOf(objTeam.getId()).equals(Preferences.get(General.GROUP_ID))) {
                                        Preferences.save(General.GROUP_ID, "" + objTeam.getId());
                                        Preferences.save(General.GROUP_NAME, objTeam.getName());
                                        Preferences.save(General.OWNER_ID, objTeam.getOwnerId());
                                        Preferences.save(General.MODERATOR, objTeam.getModerator());
                                        Preferences.save(General.PERMISSION, objTeam.getPermission());
                                        Preferences.save(General.IS_MODERATOR, objTeam.getIs_moderator());
                                        break;
                                    }
                                }

                            } else {
                                Preferences.save(General.GROUP_ID, "" + groupList.get(0).getId());
                                Preferences.save(General.GROUP_NAME, groupList.get(0).getName());
                                Preferences.save(General.OWNER_ID, groupList.get(0).getOwnerId());
                                Preferences.save(General.MODERATOR, groupList.get(0).getModerator());
                                Preferences.save(General.PERMISSION, groupList.get(0).getPermission());
                                Preferences.save(General.IS_MODERATOR, groupList.get(0).getIs_moderator());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return groupList;
    }


}
