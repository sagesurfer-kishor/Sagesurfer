package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.assessment.Forms_;
import com.modules.assessment_screener.AssessmentScreener;
import com.sagesurfer.constant.Actions_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kailash karankal
 * Created on 10-08-2017
 * Last Modified 26-09-2017
 */
public class Assessment_ {

    public static ArrayList<Forms_> parseList(String response, Context _context, String TAG) {
        ArrayList<Forms_> formsArrayList = new ArrayList<>();
        if (response == null) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(11);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(13);
            formsArrayList.add(forms_);
            return formsArrayList;
        }

        if (Error_.noData(response, Actions_.GET_LIST, _context) == 2) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(2);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Forms_>>() {
            }.getType();
            formsArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GET_LIST)
                    .toString(), listType);
        }
        return formsArrayList;
    }

    public static ArrayList<Forms_> parsePendingFormsCounter(String response, Context _context, String TAG) {
        ArrayList<Forms_> formsArrayList = new ArrayList<>();
        if (response == null) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(11);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(13);
            formsArrayList.add(forms_);
            return formsArrayList;
        }

        if (Error_.noData(response, Actions_.PENDING_LIST_COUNTER, _context) == 2) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(2);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Forms_>>() {
            }.getType();
            formsArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.PENDING_LIST_COUNTER)
                    .toString(), listType);
        }
        return formsArrayList;
    }

    public static ArrayList<Forms_> parseSubmittedFormsList(String response, Context _context, String TAG) {
        ArrayList<Forms_> formsArrayList = new ArrayList<>();
        if (response == null) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(11);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(13);
            formsArrayList.add(forms_);
            return formsArrayList;
        }

        if (Error_.noData(response, Actions_.SUBMITED_LIST, _context) == 2) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(2);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Forms_>>() {
            }.getType();
            formsArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.SUBMITED_LIST)
                    .toString(), listType);
        }
        return formsArrayList;
    }


    public static ArrayList<Forms_> parseSubmittedFormData(String response, Context _context, String TAG) {
        ArrayList<Forms_> formsArrayList = new ArrayList<>();
        if (response == null) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(11);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(13);
            formsArrayList.add(forms_);
            return formsArrayList;
        }

        if (Error_.noData(response, Actions_.GET_FORM_DETAILS, _context) == 2) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(2);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Forms_>>() {
            }.getType();
            formsArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GET_FORM_DETAILS)
                    .toString(), listType);
        }
        return formsArrayList;
    }

    public static ArrayList<Forms_> parseSubmittedFormsCounter(String response, Context _context, String TAG) {
        ArrayList<Forms_> formsArrayList = new ArrayList<>();
        if (response == null) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(11);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(13);
            formsArrayList.add(forms_);
            return formsArrayList;
        }

        if (Error_.noData(response, Actions_.SUBMITED_LIST_COUNTER, _context) == 2) {
            Forms_ forms_ = new Forms_();
            forms_.setStatus(2);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Forms_>>() {
            }.getType();
            formsArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.SUBMITED_LIST_COUNTER)
                    .toString(), listType);
        }
        return formsArrayList;
    }

    public static ArrayList<AssessmentScreener> parseAssessmentScreener(String response, Context _context, String TAG) {
        ArrayList<AssessmentScreener> formsArrayList = new ArrayList<>();
        if (response == null) {
            AssessmentScreener forms_ = new AssessmentScreener();
            forms_.setStatus(11);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            AssessmentScreener forms_ = new AssessmentScreener();
            forms_.setStatus(13);
            formsArrayList.add(forms_);
            return formsArrayList;
        }

        if (Error_.noData(response, Actions_.GET_ASSESSMENT_SCREENER, _context) == 2) {
            AssessmentScreener forms_ = new AssessmentScreener();
            forms_.setStatus(2);
            formsArrayList.add(forms_);
            return formsArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<AssessmentScreener>>() {
            }.getType();
            formsArrayList = gson.fromJson(GetJson_.getArray(response, Actions_.GET_ASSESSMENT_SCREENER)
                    .toString(), listType);
        }
        return formsArrayList;
    }
}
