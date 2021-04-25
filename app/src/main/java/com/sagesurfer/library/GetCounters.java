package com.sagesurfer.library;

import com.sagesurfer.constant.General;
import com.sagesurfer.models.Counters_;
import com.storage.preferences.Preferences;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class GetCounters {

    // get counter for drawer list menu based on menu id
    public static int drawer(int id) {
        Counters_ counters_ = Preferences.getCounters(General.COUNT);
        if (counters_ == null) {
            return 0;
        }
        switch (id) {
            case 1:
                return 0;
            case 2:
                return counters_.getSosUpdates();
            case 3:
                return counters_.getPostcard();
            case 4:
                return counters_.getAlerts();
            case 5:
                return 0;
            case 6:
                return 0;
            case 7:
                return counters_.getForm();
            case 8:
                return 0;
            case 9:
                return 0;
            case 10:
                return 0;
            case 11:
                return 0;
            case 12:
                return 0;
            case 13:
                return 0;
            case 15:
                return counters_.getAnnouncement();
            case 16:
                return counters_.getTaskList();
            case 17:
                return counters_.getFms();
            case 18:
                return counters_.getCalendar();
            case 19:
                return counters_.getTeamTalks();
            case 20:
                return counters_.getDashboardMessage();
            case 22:
                return counters_.getSosUpdates();
            case 24:
                return counters_.getSelfgoal();
            case 25:
                /*if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    return counters_.getYouth_selfcare();
                } else {*/
                    return counters_.getSelfcare();
                //}
            case 28:
                return 0;
            case 29:
                return counters_.getSosUpdates();
            case 90:
                return counters_.getPostcard();
            case 91:
                return counters_.getDraft();
            case 92:
                return counters_.getSent();
            case 93:
                return counters_.getTrash();
            default:
                return 0;

        }
    }

    // convert int to string along with checking number is greater than 99 then make it to 99+
    public static String convertCounter(int count) {
        if (count <= 0) {
            return "";
        }
        if (count > 99) {
            return "99+";
        }
        return "" + count;
    }

    // convert int to string along with checking number is greater than 99 then make it to 99+
    public static String convertCounterOne(int count) {
        if (count <= 0) {
            return "0";
        }
        if (count > 99) {
            return "99+";
        }
        return "" + count;
    }

    // convert int to string along with checking number is greater than 99 then make it to 99+
    public static String convertCounter(long count) {
        if (count <= 0) {
            return "";
        }
        if (count > 99) {
            return "99+";
        }
        return "" + count;
    }

    // convert int to string along with checking number is greater than 99 then make it to 99+
    public static String convertCounterLimit9(int count) {
        if (count <= 0) {
            return "";
        }
        if (count > 9) {
            return "9+";
        }
        return "" + count;
    }

    // check if integer is single digit and convert it to two digit value (eg. 1 => 01)
    public static String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
