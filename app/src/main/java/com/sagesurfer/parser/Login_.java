package com.sagesurfer.parser;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.modules.landing_question_form.activity.LandingQuestionFormActivity;
import com.sagesurfer.animation.ActivityTransition;
import com.sagesurfer.collaborativecares.BuildConfig;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.library.CheckRole;
import com.sagesurfer.models.DrawerMenu_;
import com.sagesurfer.models.HomeMenu_;
import com.sagesurfer.models.RegisterUser;
import com.sagesurfer.models.UserInfo_;
import com.storage.preferences.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class Login_ {

    private static final String TAG = Login_.class.getSimpleName();

    public static UserInfo_ userInfoParser(String response, String jsonName, Context _context) {
        UserInfo_ userInfo = new UserInfo_();
        if (response != null) {
            Gson gson = new Gson();
            userInfo = gson.fromJson(GetJson_.getObject(response, jsonName).toString(), UserInfo_.class);

            Preferences.save("drawer", GetJson_.getArray(response, "drawer").toString());
            Preferences.save("home", GetJson_.getArray(response, "home").toString());
            Preferences.save("faq", GetJson_.getObject(response, "faq").toString());
            Preferences.save("terms_condtions", GetJson_.getObject(response, "terms_condtions").toString());
            Preferences.save("aboutus", GetJson_.getObject(response, "aboutus").toString());


            if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                //Senjam don't have landing questions
            } else {
                if (jsonName.equalsIgnoreCase(Actions_.GET_USER_DATA) && CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) { //Show landing question form when called from Sync && do not move to landing question for roles other than Youth
                    Preferences.save(General.IS_LANDING_QUESTION_FILLED, true);
                    Preferences.save(General.IS_FORM_SYNC_LANDING_QUESTION, false);
                    if (userInfo.getLanding_questions().equalsIgnoreCase("0")) {
                        Preferences.save(General.IS_LANDING_QUESTION_FILLED, false);
                        Preferences.save(General.IS_FORM_SYNC_LANDING_QUESTION, true);
                        Intent landingQuestionFormIntent = new Intent(_context, LandingQuestionFormActivity.class);
                        _context.startActivity(landingQuestionFormIntent, ActivityTransition.moveToNextAnimation(_context));
                    }
                }
            }

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
            if (jsonObject.has(Actions_.GET_USER_DATA)) {
                JsonObject object = jsonObject.get(Actions_.GET_USER_DATA).getAsJsonObject();

                if (object.has(General.SHOW_APPOINTMENT_POPUP)) {
                    int show_appointment_popup = object.get(General.SHOW_APPOINTMENT_POPUP).getAsInt();
                    Preferences.save(General.SHOW_APPOINTMENT_FILLED, false);
                    Preferences.save(General.APP_ID, object.get(General.APP_ID).getAsInt());
                    Preferences.save(General.IS_APPOINTMENT_ADD, object.get(General.IS_APPOINTMENT_ADD).getAsInt());
                    if (show_appointment_popup == 0) { //show show_appointment_popup
                        Preferences.save(General.SHOW_APPOINTMENT_FILLED, true);
                    }
                }

                Preferences.save(General.CLINICIAN_ID, 0);
                Preferences.save(General.CLINICIAN_USER_NAME, "");
                if (object.has(General.CLINICIAN_USER_NAME)) {
                    String strClinicianUserName = object.get(General.CLINICIAN_USER_NAME).getAsString();
                    int nClinicianID = object.get(General.CLINICIAN_ID).getAsInt();
                    Preferences.save(General.CLINICIAN_ID, nClinicianID);
                    Preferences.save(General.CLINICIAN_USER_NAME, strClinicianUserName);
                }

                if (object.has(General.IS_SHOW_SOWS)) {
                    int show_daily_dosing = object.get(General.IS_SHOW_SOWS).getAsInt();
                    Preferences.save(General.SHOW_SOWS_FILLED, false);
                    if (show_daily_dosing == 0) { //show daily dosing 10 Question PopUp
                        Preferences.save(General.SHOW_SOWS_FILLED, true);
                    }
                }

                if (object.has(General.SHOW_DAILY_DOSAGE)) {
                    int show_daily_dosing = object.get(General.SHOW_DAILY_DOSAGE).getAsInt();
                    String sejman_goal_id = String.valueOf(object.get(General.SENJAM_GOAL_ID).getAsInt());
                    Preferences.save(General.SHOW_DAILY_DOSAGE_FILLED, false);
                    if (show_daily_dosing == 0) { //show daily dosing compliance dialog
                        Preferences.save(General.SHOW_DAILY_DOSAGE_FILLED, true);
                        Preferences.save(General.SENJAM_GOAL_ID, sejman_goal_id);
                    }
                }

                if (object.has(General.SHOW_PRIVACY_POPUP)) {
                    int show_daily_dosing = object.get(General.SHOW_PRIVACY_POPUP).getAsInt();
                    String privacyUrl = object.get(General.PRIVACY_URL).getAsString();
                    Preferences.save(General.SHOW_PRIVACY_POPUP_FILLED, false);
                    if (show_daily_dosing == 0) { //show Privacy Popup
                        Preferences.save(General.SHOW_PRIVACY_POPUP_FILLED, true);
                        Preferences.save(General.PRIVACY_URL, privacyUrl);
                    }
                }

                if (object.has(General.SHOW_ONE_TIME_SURVEY)) {
                    int show_daily_dosing = object.get(General.SHOW_ONE_TIME_SURVEY).getAsInt();
                    Preferences.save(General.SHOW_ONE_TIME_SURVEY_FILLED, false);
                    if (show_daily_dosing == 0) { //show One Time Survey Popup
                        Preferences.save(General.SHOW_ONE_TIME_SURVEY_FILLED, true);
                    }
                }

                if (object.has(General.SHOW_DAILY_SURVEY)) {
                    int show_daily_survey = object.get(General.SHOW_DAILY_SURVEY).getAsInt();
                    Preferences.save(General.SHOW_DAILY_SURVEY_FILLED, false);
                    if (show_daily_survey == 0) { //show Daily Survey Popup
                        Preferences.save(General.SHOW_DAILY_SURVEY_FILLED, true);
                    }
                }

            }

            JsonParser jsonFaq = new JsonParser();
            JsonObject jsonFaqObject = jsonFaq.parse(response).getAsJsonObject();
            if (jsonFaqObject.has(Actions_.GET_FAQ)) {
                JsonObject jsonObjectFaq = jsonFaqObject.get(Actions_.GET_FAQ).getAsJsonObject();
                if (jsonObjectFaq.has(General.FAQ_URL)) {
                    String faq_url = jsonObjectFaq.get(General.FAQ_URL).getAsString();
                    Preferences.save(General.FAQ_URL, faq_url);
                }
            }


            JsonParser jsonTerms = new JsonParser();
            JsonObject jsonTermsObject = jsonTerms.parse(response).getAsJsonObject();
            if (jsonTermsObject.has(Actions_.GET_TERMS)) {
                JsonObject jsonObjectTerms = jsonTermsObject.get(Actions_.GET_TERMS).getAsJsonObject();
                if (jsonObjectTerms.has(General.TERMS_URL)) {
                    String Terms_url = jsonObjectTerms.get(General.TERMS_URL).getAsString();
                    Preferences.save(General.TERMS_URL, Terms_url);
                }
            }

            JsonParser jsonTerms1 = new JsonParser();
            JsonObject jsonTermsObject1 = jsonTerms1.parse(response).getAsJsonObject();
            if (jsonTermsObject1.has(Actions_.GET_ABOUTUS_URLS)) {
                JsonObject jsonObjectTerms1 = jsonTermsObject1.get(Actions_.GET_ABOUTUS_URLS).getAsJsonObject();
                String about = jsonObjectTerms1.get(General.URL_ABOUTS).getAsString().trim();
                Preferences.save(General.URL_ABOUTS, about);

            }
        }
        return userInfo;
    }

    public static List<HomeMenu_> homeMenuParser() {
        Gson gson = new Gson();
        Type homeType = new TypeToken<List<HomeMenu_>>() {
        }.getType();
        List<HomeMenu_> homeMenuList = gson.fromJson(Preferences.get("home"), homeType);
        Log.i(TAG, "homeMenuParser: "+Preferences.get("home"));
        if (homeMenuList.size() < 6) {
            for (int i = homeMenuList.size(); i < 6; i++) {
                HomeMenu_ homeMenu_ = new HomeMenu_();
                homeMenu_.setId(0);
                homeMenuList.add(homeMenu_);
            }
        }
        return homeMenuList;
    }

    public static List<DrawerMenu_> drawerMenuParser() {
        Gson gson = new Gson();
        Type homeType = new TypeToken<List<DrawerMenu_>>() {
        }.getType();
        return gson.fromJson(Preferences.get("drawer"), homeType);
    }

    public static ArrayList<RegisterUser> user_registered(String response, Context _context, String TAG) {
        ArrayList<RegisterUser> registerUserArrayList = new ArrayList<>();
        if (response == null) {
            RegisterUser registerUser = new RegisterUser();
            registerUser.setStatus(12);
            registerUserArrayList.add(registerUser);
            return registerUserArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            RegisterUser registerUser = new RegisterUser();
            registerUser.setStatus(13);
            registerUserArrayList.add(registerUser);
            return registerUserArrayList;
        }
        if (Error_.noData(response, Actions_.REGISTER_USER, _context) == 2) {
            RegisterUser registerUser = new RegisterUser();
            registerUser.setStatus(2);
            registerUserArrayList.add(registerUser);
            return registerUserArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, Actions_.REGISTER_USER);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<RegisterUser>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, Actions_.REGISTER_USER).toString(), listType);
        } else {
            RegisterUser registerUser = new RegisterUser();
            registerUser.setStatus(11);
            registerUserArrayList.add(registerUser);
            return registerUserArrayList;
        }
    }
}
