package com.modules.selfgoal;

import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.library.ChangeCase;
import com.storage.preferences.AddGoalPreferences;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 22/03/2018
 *         Last Modified on
 */

/*
* This class is created to generate goal definition sentence according to unit and conditions
*/
class CreateSentence_ {

    private static final String TAG = CreateSentence_.class.getSimpleName();

    static String make_sentence(String goal_type, String quantity, String units,
                                String frequency, String start_date, String end_date,
                                String count_one, String occurrences) { 
        String name = AddGoalPreferences.get(General.NAME);
        StringBuilder stringBuilder = new StringBuilder();
        if (name.length() > 0 && frequency.length() > 0
                && start_date.length() > 0 && end_date.length() > 0 && count_one.length() > 0) {
            if (frequency.trim().length() <= 0) {
                return "";
            }
            if (quantity.length() < 0) {
                return "";
            }
            if (!timeValidation(frequency)) {
                return "";
            }
            if (!goal_type.equalsIgnoreCase("0")) {
                quantity = "1";
            }
            stringBuilder.append(name).append(" ");
            if (!quantity.equalsIgnoreCase("0")) {
                stringBuilder.append(quantity).append(" ");
            }
            if (units.trim().length() <= 0) {
                stringBuilder.append("time(s)");
                stringBuilder.append(" for each ");
                stringBuilder.append(subSentence(frequency, count_one));
            } else if (units.trim().length() > 0) {
                stringBuilder.append(units);
                if (occurrences.equalsIgnoreCase("1") && frequency.equalsIgnoreCase("month")) {
                    stringBuilder.append(" for ");
                } else {
                    stringBuilder.append(" for each ");
                }
                stringBuilder.append(subSentence(frequency, count_one));
            }
            stringBuilder.append("from ").append(GetTime.month_DdYyyy(start_date)).append(" ");
            stringBuilder.append("to ").append(GetTime.month_DdYyyy(end_date));
            stringBuilder.append(" (").append(GetTime.dateDifference(start_date, end_date, frequency)).append(")");
        } else {
            stringBuilder.append("");
        }
        String sentence = stringBuilder.toString();
        sentence = sentence.replaceAll("\\s+", " ");
        sentence = ChangeCase.toSentenceCase(sentence);
        return sentence;
    }

    private static boolean timeValidation(String frequency) {
        if (frequency.equalsIgnoreCase("Minute") || frequency.equalsIgnoreCase("hourly")) {
            if (!AddGoalPreferences.contains(General.START_MINUTE)) {
                return false;
            }
            if (!AddGoalPreferences.contains(General.START_HOUR)) {
                return false;
            }
            if (AddGoalPreferences.get(General.START_MINUTE) == null
                    || AddGoalPreferences.get(General.START_HOUR) == null) {
                return false;
            }
            if (AddGoalPreferences.get(General.START_MINUTE).length() <= 0 ||
                    AddGoalPreferences.get(General.START_MINUTE).equalsIgnoreCase("null")) {
                return false;
            }
            if (AddGoalPreferences.get(General.START_HOUR).length() <= 0 ||
                    AddGoalPreferences.get(General.START_HOUR).equalsIgnoreCase("null")) {
                return false;
            }
        }
        return true;
    }

    private static String subSentence(String frequency, String count_one) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        if (frequency.equalsIgnoreCase("Minute")) {
            if (frequency.equalsIgnoreCase("1")) {
                stringBuilder.append(AddGoalPreferences.get(General.START_MINUTE)).append(" Minute ");
            } else {
                stringBuilder.append(AddGoalPreferences.get(General.START_MINUTE)).append(" Minutes ");
            }
        } else if (frequency.equalsIgnoreCase("hourly") || frequency.equalsIgnoreCase("hour")) {
            stringBuilder.append(AddGoalPreferences.get(General.START_HOUR)).append(":")
                    .append(AddGoalPreferences.get(General.START_MINUTE)).append(" Hour(s) ");
        } else if (frequency.equalsIgnoreCase("daily") || frequency.equalsIgnoreCase("day")) {
            if (count_one.trim().length() <= 0 || count_one.equalsIgnoreCase("1")) {
                stringBuilder.append("day ");
            } else {
                if (count_one.equalsIgnoreCase("100")) {
                    stringBuilder.append(" weekday(s) ");
                } else {
                    stringBuilder.append(count_one).append(" day(s) ");
                }
            }
        } else if (frequency.equalsIgnoreCase("weekly") || frequency.equalsIgnoreCase("week")) {
            stringBuilder.append(" week ");
        } else if (frequency.equalsIgnoreCase("month")) {
            if (count_one.equalsIgnoreCase("1")) {
                stringBuilder.append(" month(s) ");
            } else {
                stringBuilder.append(" ").append(count_one).append(" month(s) ");
            }
        }
        return stringBuilder.toString();
    }
}
