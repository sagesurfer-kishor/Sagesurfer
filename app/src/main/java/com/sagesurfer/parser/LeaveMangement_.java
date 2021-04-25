package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modules.leave_management.models.LeaveManagement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kailash Karankal on 12/18/2019.
 */
public class LeaveMangement_ {

    public static ArrayList<LeaveManagement> parseLeaveCoachList(String response, String action, Context _context, String TAG) {
        ArrayList<LeaveManagement> leaveManagementArrayList = new ArrayList<>();
        if (response == null) {
            LeaveManagement leaveManagement = new LeaveManagement();
            leaveManagement.setStatus(11);
            leaveManagementArrayList.add(leaveManagement);
            return leaveManagementArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            LeaveManagement leaveManagement = new LeaveManagement();
            leaveManagement.setStatus(13);
            leaveManagementArrayList.add(leaveManagement);
            return leaveManagementArrayList;
        }

        if (Error_.noData(response, action, _context) == 2) {
            LeaveManagement leaveManagement = new LeaveManagement();
            leaveManagement.setStatus(2);
            leaveManagementArrayList.add(leaveManagement);
            return leaveManagementArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<LeaveManagement>>() {
            }.getType();
            leaveManagementArrayList = gson.fromJson(GetJson_.getArray(response, action)
                    .toString(), listType);
        }
        return leaveManagementArrayList;
    }
}
