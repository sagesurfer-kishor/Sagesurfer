package com.sagesurfer.library;

/**
 * @author Kailash Karankal
 */

public class NotificationTypeDetector {

    // get type of notification in integer based on name in string
    public static int getType(String type) {
        if (type.equalsIgnoreCase("Message Board") || type.equalsIgnoreCase("msg_board")) {
            return 1;
        }
        if (type.equalsIgnoreCase("Announcement") || type.equalsIgnoreCase("annoucement")) {
            return 2;
        }
        if (type.equalsIgnoreCase("File Management System") || type.equalsIgnoreCase("fms")) {
            return 3;
        }
        if (type.equalsIgnoreCase("New Member")) {
            return 4;
        }
        if (type.equalsIgnoreCase("Unjoin Member")) {
            return 5;
        }
        if (type.equalsIgnoreCase("Blogs") || type.equalsIgnoreCase("Blog")) {
            return 6;
        }
        if (type.equalsIgnoreCase("Discussion Forum")
                || type.equalsIgnoreCase("team_talk") || type.equalsIgnoreCase("team_discussion")) {
            return 7;
        }
        if (type.equalsIgnoreCase("Video")) {
            return 8;
        }
        if (type.equalsIgnoreCase("Poll")) {
            return 9;
        }
        if (type.equalsIgnoreCase("group_invitation")) {
            return 10;
        }
        if (type.equalsIgnoreCase("invitation")) {
            return 11;
        }
        if (type.equalsIgnoreCase("tasklist")) {
            return 12;
        }
        if (type.equalsIgnoreCase("gallery")) {
            return 13;
        }
        if (type.equalsIgnoreCase("strength")) {
            return 14;
        }
        if (type.equalsIgnoreCase("success")) {
            return 15;
        }
        if (type.equalsIgnoreCase("livestream")) {
            return 16;
        }
        if (type.equalsIgnoreCase("upload_selfcare")) {
            return 17;
        }
        if (type.equalsIgnoreCase("comment_selfcare")) {
            return 18;
        }
        if (type.equalsIgnoreCase("decline_selfcare")) {
            return 19;
        }
        if (type.equalsIgnoreCase("approve_selfcare")) {//self care
            return 20;
        }
        if (type.equalsIgnoreCase("calendar")) { //event
            return 21;
        }
        if (type.equalsIgnoreCase("selfgoal")) {//self goal
            return 22;
        }
        if (type.equalsIgnoreCase("addnote")) { //HnS Notes
            return 23;
        }
        if (type.equalsIgnoreCase("mood")) { //Mood
            return 24;
        }
        if (type.equalsIgnoreCase("accept_event")) { //Accept Event
            return 25;
        }
        if (type.equalsIgnoreCase("decline_event")) { //Decline Event
            return 26;
        }
        if (type.equalsIgnoreCase("approved_note")) { //HnS Approved Notes
            return 27;
        }
        if (type.equalsIgnoreCase("rejected_note")) { //HnS Rejected Notes
            return 28;
        }
        if (type.equalsIgnoreCase("updated_note")) { //HnS Updated Notes
            return 29;
        }
        if (type.equalsIgnoreCase("assessment")) { //Assessment
            return 34;
        }

        if (type.equalsIgnoreCase("team_invitation")) { //team invitation
            return 30;
        }

        if (type.equalsIgnoreCase("team_request_decline")) { //team_request_decline
            return 37;
        }

        if (type.equalsIgnoreCase("team_request_accept")) { //team_request_accept
            return 38;
        }

        if (type.equalsIgnoreCase("friend_request_decline")) { //friend_request_decline
            return 39;
        }

        if (type.equalsIgnoreCase("friend_request_accept")) { //friend_request_accept
            return 40;
        }
        if (type.equalsIgnoreCase("share_selfcare")) { //share_selfcare
            return 41;
        }

        if (type.equalsIgnoreCase("add_journal")) { //add_journal
            return 42;
        }

        if (type.equalsIgnoreCase("update_journal")) { //update_journal
            return 43;
        }

        if (type.equalsIgnoreCase("delete_journal")) { //delete_journal
            return 44;
        }

        if (type.equalsIgnoreCase("assign_goal")) { //assign_goal
            return 45;
        }

        if (type.equalsIgnoreCase("bhs")) { //assign_goal
            return 46;
        }

        if (type.equalsIgnoreCase("assign_student")) { //assign_student
            return 47;
        }

        if (type.equalsIgnoreCase("student_reassignment")) { //student_reassignment
            return 48;
        }

        if (type.equalsIgnoreCase("add_leave")) { //add_leave
            return 49;
        }

        if (type.equalsIgnoreCase("unset_goal")) { //unset_goal
            return 50;
        }

        if (type.equalsIgnoreCase("progress_note")) { //progress_note
            return 51;
        }

        if (type.equalsIgnoreCase("edit_progress_note")) { //edit_progress_note
            return 52;
        }

        if (type.equalsIgnoreCase("delete_progress_note")) { //delete_progress_note
            return 53;
        }

        if (type.equalsIgnoreCase("delete_goal")) { //delete_goal
            return 54;
        }

        if (type.equalsIgnoreCase("edit_leave")) { //edit_leave
            return 55;
        }

        if (type.equalsIgnoreCase("delete_leave")) { //delete_leave
            return 56;
        }

        if (type.equalsIgnoreCase("platform_youth_message")) { //platform_youth_message
            return 57;
        }

        if (type.equalsIgnoreCase("add_appointment")) { //add_appointment
            return 58;
        }

        if (type.equalsIgnoreCase("updated_appointment")) { //updated_appointment
            return 59;
        }

        if (type.equalsIgnoreCase("cancel_appointment")) { //cancel_appointment
            return 60;
        }

        if (type.equalsIgnoreCase("rescheduled_appointment")) { //rescheduled_appointment
            return 61;
        }

        if (type.equalsIgnoreCase("delete_appointment")) { //delete_appointment
            return 62;
        }

        if (type.equalsIgnoreCase("peer_supervisor_notification")) { //peer_supervisor_notification
            return 63;
        }
        if (type.equalsIgnoreCase("submit_sows")) { //submit_sows
            return 64;
        }
        if (type.equalsIgnoreCase("immunity_survey")) { //immunity_survey
            return 65;
        }
        if (type.equalsIgnoreCase("submit_one_time_survey")) { //submit_one_time_survey
            return 66;
        }
        if (type.equalsIgnoreCase("selfgoal_due")) { //selfgoal_due
            return 67;
        }
        if (type.equalsIgnoreCase("dailysurvey_due")) { //dailysurvey_due
            return 68;
        }

        // newly added by kishor k 06/08/2020

        if (type.equalsIgnoreCase("caseload")) { //caseload
            return 69;
        }

        if (type.equalsIgnoreCase("cometchat_request")) { //caseload
            return 70;
        }


        return 0;
    }
}
