package com.modules.landing_question_form.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.landing_question_form.module.LandingQuestion_;
import com.sagesurfer.parser.Error_;
import com.sagesurfer.parser.GetJson_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kailash Karankal on 7/16/2019.
 */
public class LandingQuestionsParser_ {
    public static ArrayList<LandingQuestion_> parseLandingQuestion(String response, String action, Context _context, String TAG) {
        ArrayList<LandingQuestion_> intakeFormArrayList = new ArrayList<>();
        if (response == null) {
            LandingQuestion_ intakeForm_ = new LandingQuestion_();
            intakeForm_.setStatus(11);
            intakeFormArrayList.add(intakeForm_);
            return intakeFormArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            LandingQuestion_ intakeForm_ = new LandingQuestion_();
            intakeForm_.setStatus(13);
            intakeFormArrayList.add(intakeForm_);
            return intakeFormArrayList;
        }

        if (Error_.noData(response, action, _context) == 2) {
            LandingQuestion_ intakeForm_ = new LandingQuestion_();
            intakeForm_.setStatus(2);
            intakeFormArrayList.add(intakeForm_);
            return intakeFormArrayList;
        }

        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<LandingQuestion_>>() {
            }.getType();
            intakeFormArrayList = gson.fromJson(GetJson_.getArray(response, action).toString(), listType);
        }
        return intakeFormArrayList;
    }
}