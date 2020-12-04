package com.sagesurfer.library;

import com.modules.appointment.model.AppointmentReason_;
import com.modules.appointment.model.Staff;
import com.modules.wall.Attachment_;
import com.sagesurfer.models.CaseloadPeerNoteReject_;
import com.sagesurfer.models.Choices_;
import com.sagesurfer.models.Friends_;
import com.sagesurfer.models.Teams_;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 31-07-2017
 * Last Modified on 08-07-2017
 */

/*
 * This class return selected items from given list
 */

public class GetSelected {

    public static String wallTeams(ArrayList<Teams_> teamsArrayList) {
        String team_id = "0";
        if (teamsArrayList == null || teamsArrayList.size() <= 0) {
            return team_id;
        }
        ArrayList<Integer> selectedId = new ArrayList<>();
        for (Teams_ teams_ : teamsArrayList) {
            if (teams_.getSelected()) {
                selectedId.add(teams_.getId());
            }
        }
        if (selectedId.size() > 0) {
            team_id = ArrayOperations.listToString(selectedId);
        }
        return team_id;
    }

    public static ArrayList<Friends_> getSelectedUserList(ArrayList<Friends_> teamsArrayList) {
        ArrayList<Friends_> friendsArrayList = new ArrayList<>();
        if (teamsArrayList == null || teamsArrayList.size() <= 0) {
            return friendsArrayList;
        }
        for (Friends_ friends_ : teamsArrayList) {
            if (friends_.getSelected()) {
                friendsArrayList.add(friends_);
            }
        }
        return friendsArrayList;
    }

    public static ArrayList<Friends_> setSelectedUserList(ArrayList<Friends_> friendArrayList, String sharedWithIds) {
        ArrayList<Friends_> friendsArrayList = new ArrayList<>();
        if (friendArrayList == null || friendArrayList.size() <= 0) {
            return friendsArrayList;
        }
        ArrayList<Integer> selectedId = new ArrayList<>();
        if (sharedWithIds.contains(",")) {
            String[] splitStr = sharedWithIds.trim().split(",");
            for (int i = 0; i < splitStr.length; i++) {
                selectedId.add(Integer.parseInt(splitStr[i]));
            }
        }
        for (int i = 0; i < friendArrayList.size(); i++) {
            for (int j = 0; j < selectedId.size(); j++) {
                if (friendArrayList.get(i).getUserId() == selectedId.get(j)) {
                    friendArrayList.get(i).setSelected(true);
                }
            }
        }
        return friendArrayList;
    }

    public static String wallUsers(ArrayList<Friends_> usersArrayList) {
        String user_id = "0";
        if (usersArrayList == null || usersArrayList.size() <= 0) {
            return user_id;
        }
        ArrayList<Integer> selectedId = new ArrayList<>();
        for (Friends_ friends_ : usersArrayList) {
            if (friends_.getSelected()) {
                selectedId.add(friends_.getUserId());
            }
        }
        if (selectedId.size() > 0) {
            user_id = ArrayOperations.listToString(selectedId);
        }
        return user_id;
    }

    public static String wallUsersName(ArrayList<Friends_> usersArrayList) {
        String user_id = "";
        if (usersArrayList == null || usersArrayList.size() <= 0) {
            return user_id;
        }
        ArrayList<String> selectedId = new ArrayList<>();
        for (Friends_ friends_ : usersArrayList) {
            if (friends_.getSelected()) {
                selectedId.add(friends_.getName());
            }
        }
        if (selectedId.size() > 0) {
            user_id = ArrayOperations.stringListToString(selectedId);
        }
        return user_id;
    }

    public static String wallAttachments(ArrayList<Attachment_> attachmentArrayList) {
        String id = "0";
        if (attachmentArrayList == null || attachmentArrayList.size() <= 0) {
            return id;
        }
        ArrayList<Integer> selectedId = new ArrayList<>();
        for (Attachment_ attachment_ : attachmentArrayList) {
            if (attachment_.isNewFile()) {
                selectedId.add(attachment_.getId());
            }
        }
        if (selectedId.size() > 0) {
            id = ArrayOperations.listToString(selectedId);
        }
        return id;
    }

    public static String deleteAttachments(ArrayList<Attachment_> attachmentArrayList) {
        String id = "0";
        if (attachmentArrayList == null || attachmentArrayList.size() <= 0) {
            return id;
        }
        ArrayList<Integer> selectedId = new ArrayList<>();
        for (Attachment_ attachment_ : attachmentArrayList) {
            selectedId.add(attachment_.getId());
        }
        if (selectedId.size() > 0) {
            id = ArrayOperations.listToString(selectedId);
        }
        return id;
    }

    public static String rejectReasons(ArrayList<CaseloadPeerNoteReject_> rejectReasonArrayList) {
        String reject_reason_id = "0";
        if (rejectReasonArrayList == null || rejectReasonArrayList.size() <= 0) {
            return reject_reason_id;
        }
        ArrayList<Integer> selectedId = new ArrayList<>();
        for (CaseloadPeerNoteReject_ rejectReson : rejectReasonArrayList) {
            if (rejectReson.isSelected()) {
                selectedId.add(rejectReson.getReason_id());
            }
        }
        if (selectedId.size() > 0) {
            reject_reason_id = ArrayOperations.listToString(selectedId);
        }
        return reject_reason_id;
    }

    public static String cancelAppointment(ArrayList<AppointmentReason_> rejectReasonArrayList) {
        String reject_reason_id = "0";
        if (rejectReasonArrayList == null || rejectReasonArrayList.size() <= 0) {
            return reject_reason_id;
        }
        ArrayList<Integer> selectedId = new ArrayList<>();
        for (AppointmentReason_ rejectReson : rejectReasonArrayList) {
            if (rejectReson.isSelected()) {
                selectedId.add(rejectReson.getId());
            }
        }
        if (selectedId.size() > 0) {
            reject_reason_id = ArrayOperations.listToString(selectedId);
        }
        return reject_reason_id;
    }


    public static String getIds(ArrayList<Staff> rejectReasonArrayList) {
        String reject_reason_id = "0";
        if (rejectReasonArrayList == null || rejectReasonArrayList.size() <= 0) {
            return reject_reason_id;
        }
        ArrayList<Integer> selectedId = new ArrayList<>();
        for (Staff rejectReson : rejectReasonArrayList) {
            if (rejectReson.isSelected()) {
                selectedId.add(rejectReson.getId());
            }
        }
        if (selectedId.size() > 0) {
            reject_reason_id = ArrayOperations.listToString(selectedId);
        }
        return reject_reason_id;
    }


    public static String selectedAge(ArrayList<Choices_> ageArrayList) {
        String age_id = "";
        if (ageArrayList == null || ageArrayList.size() <= 0) {
            return age_id;
        }
        ArrayList<Long> selectedId = new ArrayList<>();
        for (Choices_ choices_ : ageArrayList) {
            if (choices_.getIs_selected().equalsIgnoreCase("1")) {
                selectedId.add(choices_.getId());
            }
        }
        if (selectedId.size() > 0) {
            age_id = ArrayOperations.listLongToString(selectedId);
        }
        return age_id;
    }

    public static String selectedAgeName(ArrayList<Choices_> ageArrayList) {
        String age_id = "";
        if (ageArrayList == null || ageArrayList.size() <= 0) {
            return age_id;
        }
        ArrayList<String> selectedId = new ArrayList<>();
        for (Choices_ choices_ : ageArrayList) {
            if (choices_.getIs_selected().equalsIgnoreCase("1")) {
                selectedId.add(choices_.getName());
            }
        }
        if (selectedId.size() > 0) {
            age_id = ArrayOperations.stringListToString(selectedId);
        }
        return age_id;
    }
}
