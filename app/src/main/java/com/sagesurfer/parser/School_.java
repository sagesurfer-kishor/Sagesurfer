package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.assignment.werhope.model.Student;
import com.modules.team.team_invitation_werhope.model.Invitation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class School_ {
    public static ArrayList<Student> parseStudentList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Student> studentArrayList = new ArrayList<>();
        if (response == null) {
            Student student = new Student();
            student.setStatus(11);
            studentArrayList.add(student);
            return studentArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Student student = new Student();
            student.setStatus(13);
            studentArrayList.add(student);
            return studentArrayList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            Student student = new Student();
            student.setStatus(2);
            studentArrayList.add(student);
            return studentArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Student>>() {
            }.getType();
            studentArrayList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return studentArrayList;
    }

    public static ArrayList<Invitation> parseInvitationList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Invitation> invitationArrayList = new ArrayList<>();
        if (response == null) {
            Invitation invitation = new Invitation();
            invitation.setStatus(11);
            invitationArrayList.add(invitation);
            return invitationArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Invitation invitation = new Invitation();
            invitation.setStatus(13);
            invitationArrayList.add(invitation);
            return invitationArrayList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            Invitation invitation = new Invitation();
            invitation.setStatus(2);
            invitationArrayList.add(invitation);
            return invitationArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Invitation>>() {
            }.getType();
            invitationArrayList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return invitationArrayList;
    }


}
