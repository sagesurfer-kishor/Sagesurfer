package com.sagesurfer.icons;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.storage.preferences.Preferences;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * This class returns home menu icon based on menu id
 */

public class GetHomeMenuIcon {
    public static int get(int id) {
        switch (id) {
            case 3:
                return R.drawable.ic_senjam_message_icon;
            case 22:
                return R.drawable.vi_drawer_sos;
            case 23:
                return R.drawable.vi_home_emergency_call;
            case 24:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")){
                    return R.drawable.self_goal_white_home_icon;
                }else {
                    return R.drawable.self_goal_icon;
                }
            case 25:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023")) {
                    return R.drawable.self_care_home_icon;
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) {
                    return R.drawable.ic_mhaw_home_selfcare;
                } else {
                    return R.drawable.vi_home_self_care;
                }
            case 26:
                return R.drawable.vi_home_notification;
            case 27:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023")) {
                    return R.drawable.planner_home_icon;
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) {
                    return R.drawable.ic_mhaw_home_planner;
                } else {
                    return R.drawable.vi_home_planner;
                }
            case 29:
                return R.drawable.vi_drawer_sos;
            case 30:
                return R.drawable.vi_home_updates;
            case 32:
                return R.drawable.vi_home_admin_approval;
            case 34:
                return R.drawable.mood;
            case 35:
                return R.drawable.vi_home_task_list;
            case 36:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) {
                    return R.drawable.chat_home_icon;
                } else {
                    return R.drawable.vi_home_chat;
                }
            case 41:
                return R.drawable.vi_home_caseload;
            case 47:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) {
                    return R.drawable.team_home_icon;
                } else {
                    return R.drawable.vi_home_team;
                }
            case 48:
                return R.drawable.vi_home_report1;
            case 49:
                return R.drawable.vi_home_adherence;
            case 55:
                return R.drawable.vi_home_report2;
            case 57:
                return R.drawable.vi_home_updates;
            case 58:
                return R.drawable.vi_home_snapshot;
            case 59:
                return R.drawable.vi_home_search;
            case 60:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) {
                    return R.drawable.team_home_icon;
                } else {
                    return R.drawable.vi_home_team;
                }
            case 62:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) {
                    return R.drawable.ic_mhaw_home_selfgoalreport;
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023")) {
                    return R.drawable.ic_wh_home_selfgoalreport;
                } else {
                    return R.drawable.vi_home_report1;
                }
            case 63:
                return R.drawable.vi_home_report1;
            case 67:
                return R.drawable.vi_home_admin_approval;
            case 76:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")){
                    return R.drawable.self_goal_white_home_icon;
                }else {
                    return R.drawable.self_goal_icon;
                }
            case 77:
                return R.drawable.journal_big;
            case 78:
                return R.drawable.vi_drawer_assessment;
            case 80:
                return R.drawable.appointment_white_icon;
            case 53:
                return R.drawable.vi_drawer_setting;
            default:
                return R.drawable.primary_circle;
        }
    }
}
