package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.consent.ConsentFile_;
import com.modules.consent.Consumer_;
import com.sagesurfer.constant.Actions_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish M (girish@sagesurfer.com)
 * Created on 09-08-2017
 * Last Modified 26-09-2017
 */
public class Consent_ {

    public static ArrayList<Consumer_> parseConsumer(String response, String jsonName,
                                                     Context _context, String TAG) {
        ArrayList<Consumer_> consumerArrayList = new ArrayList<>();
        if (response == null) {
            return consumerArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            return consumerArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            return consumerArrayList;
        }
        String responseArray = GetJson_.getArray(response, jsonName).toString();
        if (responseArray == null) {
            return consumerArrayList;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Consumer_>>() {
        }.getType();
        consumerArrayList = gson.fromJson(responseArray, listType);

        return consumerArrayList;
    }

    public static ArrayList<ConsentFile_> parseConsent(String response, Context _context, String TAG) {
        ArrayList<ConsentFile_> consentFileArrayList = new ArrayList<>();
        if (response == null) {
            ConsentFile_ consentFile_ = new ConsentFile_();
            consentFile_.setStatus(12);
            consentFileArrayList.add(consentFile_);
            return consentFileArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            ConsentFile_ consentFile_ = new ConsentFile_();
            consentFile_.setStatus(13);
            consentFileArrayList.add(consentFile_);
            return consentFileArrayList;
        }

        if (Error_.noData(response, Actions_.GET_SHARED_FILES, _context) == 2) {
            ConsentFile_ consentFile_ = new ConsentFile_();
            consentFile_.setStatus(2);
            consentFileArrayList.add(consentFile_);
            return consentFileArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ConsentFile_>>() {
            }.getType();
            consentFileArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GET_SHARED_FILES)
                    .toString(), listType);
        }
        return consentFileArrayList;
    }

}
