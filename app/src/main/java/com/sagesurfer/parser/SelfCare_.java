package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.CareUploaded_;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.models.City_;
import com.sagesurfer.models.Comments_;
import com.sagesurfer.models.Content_;
import com.sagesurfer.models.Country_;
import com.sagesurfer.models.State_;
import com.storage.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/11/2018
 *         Last Modified on 4/11/2018
 */

public class SelfCare_ {

    public static ArrayList<Content_> parseEvents(String response, Context _context, String TAG) {
        ArrayList<Content_> contentArrayList = new ArrayList<>();
        if (response == null) {
            Content_ event_ = new Content_();
            event_.setStatus(12);
            contentArrayList.add(event_);
            return contentArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Content_ event_ = new Content_();
            event_.setStatus(13);
            contentArrayList.add(event_);
            return contentArrayList;
        }
        if (Error_.noData(response, Actions_.GET_DATA, _context) == 2) {
            Content_ event_ = new Content_();
            event_.setStatus(2);
            contentArrayList.add(event_);
            return contentArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.GET_DATA);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Content_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.GET_DATA).toString(), listType);
        } else {
            Content_ event_ = new Content_();
            event_.setStatus(11);
            contentArrayList.add(event_);
            return contentArrayList;
        }
    }

    public static ArrayList<CareUploaded_> parseContent(String response, String jsonName, String TAG, Context _context) {
        ArrayList<CareUploaded_> contentArrayList = new ArrayList<>();
        if (response == null) {
            CareUploaded_ careUploaded_ = new CareUploaded_();
            careUploaded_.setStatus(12);
            contentArrayList.add(careUploaded_);
            return contentArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            CareUploaded_ careUploaded_ = new CareUploaded_();
            careUploaded_.setStatus(13);
            contentArrayList.add(careUploaded_);
            return contentArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            CareUploaded_ careUploaded_ = new CareUploaded_();
            careUploaded_.setStatus(2);
            contentArrayList.add(careUploaded_);
            return contentArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, jsonName);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<CareUploaded_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        } else {
            CareUploaded_ careUploaded_ = new CareUploaded_();
            careUploaded_.setStatus(11);
            contentArrayList.add(careUploaded_);
            return contentArrayList;
        }
    }

    public static ArrayList<Comments_> parseComments(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Comments_> commentsArrayList = new ArrayList<>();
        if (response == null) {
            Comments_ comments_ = new Comments_();
            comments_.setStatus(12);
            commentsArrayList.add(comments_);
            return commentsArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Comments_ comments_ = new Comments_();
            comments_.setStatus(13);
            commentsArrayList.add(comments_);
            return commentsArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            Comments_ comments_ = new Comments_();
            comments_.setStatus(2);
            commentsArrayList.add(comments_);
            return commentsArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, jsonName);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Comments_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        } else {
            Comments_ comments_ = new Comments_();
            comments_.setStatus(11);
            commentsArrayList.add(comments_);
            return commentsArrayList;
        }
    }

    public static ArrayList<Choices_> parseCategory(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Choices_> categoryArrayList = new ArrayList<>();
        if (response == null) {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(12);
            categoryArrayList.add(choices_);
            return categoryArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(13);
            categoryArrayList.add(choices_);
            return categoryArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(2);
            categoryArrayList.add(choices_);
            return categoryArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, jsonName);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Choices_>>() {}.getType();
            return gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        } else {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(11);
            categoryArrayList.add(choices_);
            return categoryArrayList;
        }
    }

    public static ArrayList<Choices_> parseCategory(String response, String json_name) {
        ArrayList<Choices_> choicesList = new ArrayList<>();
        if (response != null) {
            try {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.has(json_name)) {
                    JSONArray jsonArray = responseObject.getJSONArray(json_name);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has(General.STATUS)) {

                            String id = jsonObject.getString(General.ID);
                            String name = jsonObject.getString(General.NAME);

                            Choices_ choices_ = new Choices_();
                            choices_.setStatus(1);
                            choices_.setId(Long.valueOf(id));
                            choices_.setName(name);
                            choices_.setIs_selected("0");
                            choicesList.add(choices_);
                        } else {
                            Choices_ choices_ = new Choices_();
                            choices_.setStatus(2);
                            choicesList.add(choices_);
                        }
                    }
                } else {
                    Choices_ choices_ = new Choices_();
                    choices_.setStatus(11);
                    choicesList.add(choices_);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(12);
            choicesList.add(choices_);
        }
        return choicesList;
    }

    public static ArrayList<Choices_> getContentType(Context _mContext) {
        ArrayList<Choices_> choicesList = new ArrayList<>();
        String[] type = _mContext.getResources().getStringArray(R.array.self_care);
        for (int i = 0; i < type.length; i++) {
            Choices_ choices_ = new Choices_();
            choices_.setStatus(1);
            choices_.setId(i+1);
            choices_.setName(type[i]);
            choices_.setIs_selected("0");
            if(!type[i].equalsIgnoreCase(_mContext.getResources().getString(R.string.webinar))) {
                choicesList.add(choices_);
            } else {
                if(Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(_mContext.getResources().getString(R.string.sage015))){
                    choicesList.add(choices_);
                }
            }
        }
        return choicesList;
    }

    public static ArrayList<Country_> parseLocation(String response, String json_name, Context _context) {
        ArrayList<Country_> locationList = new ArrayList<>();
        if (response != null) {
            try {
                JSONObject responseObject = new JSONObject(response);
                int last_country = responseObject.getInt(General.LAST_COUNTRY);
                Preferences.save(General.LAST_COUNTRY, last_country);
                if (responseObject.has(json_name)) {
                    JSONArray jsonArray = responseObject.getJSONArray(json_name);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has(General.STATUS)) {
                            if(jsonObject.getInt(General.STATUS) == 2) {
                                Country_ country_ = new Country_();
                                country_.setStatus(2);
                                locationList.add(country_);
                            }
                            else {
                                ArrayList<State_> stateList = new ArrayList<>();
                                JSONArray jsonArrayState = jsonObject.getJSONArray("state");
                                for (int j = 0; j < jsonArrayState.length(); j++) {
                                    JSONObject jsonObjectState = jsonArrayState.getJSONObject(j);
                                    if (jsonObjectState.has(General.STATUS)) {
                                        if (jsonObjectState.getInt(General.STATUS) == 2) {
                                            State_ state_ = new State_();
                                            state_.setStatus(2);
                                            stateList.add(state_);
                                        }
                                        else {
                                            ArrayList<City_> cityList = new ArrayList<>();
                                            JSONArray jsonArrayCity = jsonObjectState.getJSONArray("city");
                                            for (int k = 0; k < jsonArrayCity.length(); k++) {
                                                JSONObject jsonObjectCity = jsonArrayCity.getJSONObject(k);
                                                if (jsonObjectCity.has(General.STATUS)) {
                                                    if (jsonObjectCity.getInt(General.STATUS) == 2) {
                                                        City_ city_ = new City_();
                                                        city_.setStatus(2);
                                                        cityList.add(city_);
                                                    }
                                                    else {
                                                        City_ city_ = new City_();
                                                        city_.setStatus(1);
                                                        city_.setId(Long.valueOf(jsonObjectCity.getString(General.ID)));
                                                        city_.setName(jsonObjectCity.getString(General.NAME));
                                                        city_.setIs_selected(false);

                                                        cityList.add(city_);
                                                    }
                                                } else {
                                                    City_ city_ = new City_();
                                                    city_.setStatus(2);
                                                    cityList.add(city_);
                                                }
                                            }

                                            State_ state_ = new State_();
                                            state_.setStatus(1);
                                            state_.setId(Long.valueOf(jsonObjectState.getString(General.ID)));
                                            state_.setName(jsonObjectState.getString(General.NAME));
                                            state_.setIs_selected(false);
                                            state_.setCity(cityList);

                                            stateList.add(state_);
                                        }
                                    } else {
                                        State_ state_ = new State_();
                                        state_.setStatus(2);
                                        stateList.add(state_);
                                    }
                                }

                                String id = jsonObject.getString(General.ID);
                                String name = jsonObject.getString(General.NAME);

                                Country_ country_ = new Country_();
                                country_.setStatus(1);
                                country_.setId(Long.valueOf(id));
                                country_.setName(name);
                                country_.setIs_selected(false);
                                country_.setState(stateList);

                                locationList.add(country_);
                            }
                        } else {
                            Country_ country_ = new Country_();
                            country_.setStatus(2);
                            locationList.add(country_);
                        }
                    }
                } else {
                    Country_ country_ = new Country_();
                    country_.setStatus(11);
                    locationList.add(country_);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Country_ country_ = new Country_();
            country_.setStatus(12);
            locationList.add(country_);
        }
        return locationList;
    }
}
