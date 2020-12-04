package com.sagesurfer.icons;

import com.sagesurfer.collaborativecares.R;

/**
 * Kailash Karankal
 */

/*
 * This class returns notification icon based on menu id
 */

public class GetNotifyIcons {

    public static int getNotifyIcon(String type) {
        if (type.equalsIgnoreCase("Message Board") || type.equalsIgnoreCase("msg_board")) {
            return R.drawable.vi_notify_message;
        }
        if (type.equalsIgnoreCase("Announcement") || type.equalsIgnoreCase("announcement")) {
            return R.drawable.vi_notify_announcement;
        }
        if (type.equalsIgnoreCase("File Management System") || type.equalsIgnoreCase("fms")) {
            return R.drawable.vi_notify_fms;
        }
        if (type.equalsIgnoreCase("New Member")) {
            return R.drawable.vi_notify_user;
        }
        if (type.equalsIgnoreCase("Unjoin Member")) {
            return R.drawable.vi_notify_user;
        }
        if (type.equalsIgnoreCase("Blogs") || type.equalsIgnoreCase("Blog")) {
            return R.drawable.vi_notify_blog;
        }
        if (type.equalsIgnoreCase("Discussion Forum") || type.equalsIgnoreCase("team_talk")) {
            return R.drawable.vi_notify_team_talk;
        }
        if (type.equalsIgnoreCase("Video") || type.equalsIgnoreCase("livestream")) {
            return R.drawable.vi_notify_video;
        }
        if (type.equalsIgnoreCase("Poll")) {
            return R.drawable.vi_notify_poll;
        }
        if (type.equalsIgnoreCase("group_invitation")) {
            return R.drawable.vi_notify_team;
        }
        if (type.equalsIgnoreCase("invitation")) {
            return R.drawable.vi_notify_user;
        }
        if (type.equalsIgnoreCase("tasklist")) {
            return R.drawable.vi_notify_task_list;
        }
        if (type.equalsIgnoreCase("gallery")) {
            return R.drawable.vi_notify_gallery;
        }
        if (type.equalsIgnoreCase("strength")) {
            return R.drawable.vi_notify_strength;
        }
        if (type.equalsIgnoreCase("success")) {
            return R.drawable.vi_notify_strength;
        }
        if (type.equalsIgnoreCase("upload_selfcare")
                || type.equalsIgnoreCase("comment_selfcare")
                || type.equalsIgnoreCase("decline_selfcare")
                || type.equalsIgnoreCase("approve_selfcare")) {
            return R.drawable.ic_notification_selfcare;
        }
        if (type.equalsIgnoreCase("calendar")
                || type.equalsIgnoreCase("accept_event")
                || type.equalsIgnoreCase("decline_event")) {
            return R.drawable.event_icon;
        }
        if (type.equalsIgnoreCase("selfgoal")) {
            return R.drawable.ic_notification_selfgoal;
        }
        if (type.equalsIgnoreCase("addnote")
                || type.equalsIgnoreCase("approved_note")
                || type.equalsIgnoreCase("rejected_note")
                || type.equalsIgnoreCase("updated_note")) {
            return R.drawable.notes_img;
        }
        if (type.equalsIgnoreCase("mood")) {
            return R.drawable.vi_drawer_mood;
        }

        if (type.equalsIgnoreCase("team_invitation")) {
            return R.drawable.vi_notify_team;
        }

        if (type.equalsIgnoreCase("team_request_decline") || type.equalsIgnoreCase("team_request_accept")) {
            return R.drawable.vi_notify_team;
        }

        if (type.equalsIgnoreCase("friend_request_accept") || type.equalsIgnoreCase("friend_request_decline")) {
            return R.drawable.vi_notify_user;
        }

        if (type.equalsIgnoreCase("share_selfcare")) {
            return R.drawable.ic_notification_selfcare;
        }

        if (type.equalsIgnoreCase("Assessment")) {
            return R.drawable.vi_drawer_assessment;
        }

        if (type.equalsIgnoreCase("add_journal") || type.equalsIgnoreCase("update_journal") ||
                type.equalsIgnoreCase("delete_journal")) {
            return R.drawable.journaling_icon;
        }

        if (type.equalsIgnoreCase("assign_goal")) {
            return R.drawable.ic_notification_selfgoal;
        }

        if (type.equalsIgnoreCase("bhs")) {
            return R.drawable.health_servey_icon;
        }

        if (type.equalsIgnoreCase("assign_student")) {
            return R.drawable.assignement_icon;
        }

        if (type.equalsIgnoreCase("student_reassignment")) {
            return R.drawable.reassignement_notification_icon;
        }

        if (type.equalsIgnoreCase("add_leave")) {
            return R.drawable.leave_managment_small_icon;
        }

        if (type.equalsIgnoreCase("edit_leave")) {
            return R.drawable.leave_managment_small_icon;
        }

        if (type.equalsIgnoreCase("delete_leave")) {
            return R.drawable.leave_managment_small_icon;
        }

        if (type.equalsIgnoreCase("unset_goal")) {
            return R.drawable.ic_notification_selfgoal;
        }

        if (type.equalsIgnoreCase("progress_note")) {
            return R.drawable.caseload_progress_note;
        }

        if (type.equalsIgnoreCase("edit_progress_note")) {
            return R.drawable.caseload_progress_note;
        }

        if (type.equalsIgnoreCase("delete_progress_note")) {
            return R.drawable.caseload_progress_note;
        }

        if (type.equalsIgnoreCase("delete_goal")) {
            return R.drawable.ic_notification_selfgoal;
        }

        if (type.equalsIgnoreCase("platform_youth_message")) {
            return R.drawable.vi_notify_message;
        }

        if (type.equalsIgnoreCase("add_appointment")) {
            return R.drawable.appointment_black_icon;
        }

        if (type.equalsIgnoreCase("updated_appointment")) {
            return R.drawable.appointment_black_icon;
        }

        if (type.equalsIgnoreCase("cancel_appointment")) {
            return R.drawable.appointment_black_icon;
        }

        if (type.equalsIgnoreCase("rescheduled_appointment")) {
            return R.drawable.appointment_black_icon;
        }

        if (type.equalsIgnoreCase("delete_appointment")) {
            return R.drawable.appointment_black_icon;
        }

        if (type.equalsIgnoreCase("peer_supervisor_notification")) {
            return R.drawable.assignement_icon;
        }

        //newly added by kishor k on 06/08/2020

        if (type.equalsIgnoreCase("caseload")) {
            return R.drawable.vi_home_caseload;
        }

        return R.drawable.vi_ppt_file;
    }
}
