package com.sagesurfer.icons;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.storage.preferences.Preferences;

/**
 * @author Kailash Karankal
 */

/*
 * This class returns drawer menu icon based on menu id
 */

public class   GetDrawerIcon {
    public static int get(int id) {
        switch (id) {
            case 0:
                return R.drawable.logout_side;
            case 1:
                return R.drawable.side_wall;
            case 2:
                return R.drawable.sos_side;
            case 3:
                return R.drawable.side_messages;
            case 4:
                return R.drawable.vi_drawer_alerts;
            case 5:
                return R.drawable.vi_drawer_crisisplan;
            case 7:
                return R.drawable.assess_side;
            case 8:
                return R.drawable.side_team;
            case 9:
                return R.drawable.vi_home_chat;
            case 10:
                return R.drawable.side_contact;
            case 11:
                    return R.drawable.side_con_consent;
            case 12:
                return R.drawable.side_uploader;
            case 13:
                return R.drawable.headset_side;
            case 15:
                return R.drawable.vi_drawer_announcements;
            case 16:
                return R.drawable.vi_home_task_list;
            case 17:
                return R.drawable.vi_drawer_file_sharing;
            case 18:
                return R.drawable.vi_drawer_calendar;
            /*case 19:
                return R.drawable.vi_drawer_teamtalk;*/
            case 20:
                return R.drawable.vi_drawer_platform_messages;
            /*case 21:
                return R.drawable.vi_drawer_supervisor_messages;*/
            case 28:
                return R.drawable.side_reviewer;
            case 34:
                return R.drawable.mood_side;
            case 40:
                return R.drawable.vi_drawer_dashboard;
            case 50:
                return R.drawable.home_side;
            case 51:
                return R.drawable.mood_drawer_icon;
            case 52:
                return R.drawable.vi_drawer_uplift;
            case 53:
                return R.drawable.vi_drawer_setting;
            case 54:
                return R.drawable.vi_drawer_planner;
            case 62: //for instance domain only
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013")) {
                    return R.drawable.vi_drawer_notes;
                }
            case 64:
                return R.drawable.vi_drawer_notes;
            case 65:
                return R.drawable.journaling_icon;
            case 66:
                return R.drawable.re_assignment;
            case 67:
                return R.drawable.set_leave_icon;
            case 68:
                return R.drawable.leave_management_icon;
            case 71:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")){
                    return R.drawable.ic_mhaw_home_selfcare;
                }else {
                    return R.drawable.self_care_home_icon;
                }
            case 72:
                return R.drawable.multiple_user_regi_icon;
            case 73:
                return R.drawable.assignement_icon;
            case 74:
                return R.drawable.health_servey_icon;
            case 75:
                return R.drawable.goal_assignment_icon;
            case 76:
                return R.drawable.vi_home_report1;
            case 79:
                return R.drawable.invite_friends;
            case 81:
                return R.drawable.vi_home_appintment;
            case 82:
                return R.drawable.vi_home_appintment;
            default:
                return R.drawable.primary_circle;
        }
    }
}
