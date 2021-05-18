package com.sagesurfer.library;

import android.content.Context;
import android.graphics.Color;

import com.sagesurfer.collaborativecares.R;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 21-07-2017
 * Last Modified on 15-12-2017
 */

/*
 * This class have methods to return color code for files and task list status
 */

public class GetColor {

    // color code of background color for multiple file extensions/type
    public static int getFileIconBackgroundColor(String file_name) {
        if (CheckFileType.imageFile(file_name)) {
            return R.color.color_image;
        }
        if (CheckFileType.docFile(file_name)) {
            return R.color.color_doc;
        }
        if (CheckFileType.pptFile(file_name)) {
            return R.color.color_ppt;
        }
        if (CheckFileType.pdfFile(file_name)) {
            return R.color.color_pdf;
        }
        if (CheckFileType.textFile(file_name)) {
            return R.color.color_text;
        }
        if (CheckFileType.xmlFile(file_name)) {
            return R.color.color_xml;
        }
        if (CheckFileType.htmlFile(file_name)) {
            return R.color.color_html;
        }
        if (CheckFileType.xlsFile(file_name)) {
            return R.color.color_xls;
        }
        return R.color.colorPrimary;
    }

    // color code for test based on task status
    public static int getTaskStatusColor(String status, Context _context) {
        if (status.equalsIgnoreCase("Assigned")) {
            return _context.getResources().getColor(R.color.bg_dark_self_goal_missed);
        }
        if (status.equalsIgnoreCase("In-Progress")) {
            return _context.getResources().getColor(R.color.task_in_progress);
        }
        if (status.equalsIgnoreCase("On Hold")) {
            return _context.getResources().getColor(R.color.task_on_hold);
        }
        if (status.equalsIgnoreCase("Completed")) {
            return _context.getResources().getColor(R.color.task_completed);
        }
        if (status.equalsIgnoreCase("Deferred/Deactivate") || status.equalsIgnoreCase("Deferred / Deactivate")) {
            return _context.getResources().getColor(R.color.task_deferred);
        }

        if (status.equalsIgnoreCase("Hold-decline")) {
            return _context.getResources().getColor(R.color.task_hold_decline);
        }

        if (status.equalsIgnoreCase("nonsage-decline")) {
            return _context.getResources().getColor(R.color.task_non_sage_decline);
        }

        return _context.getResources().getColor(R.color.colorPrimary);
    }

    // color code of background color for home icons
    //For Toolbar color changing depending on background color of home icons
    //true: setting textcolor, image color
    //false: setting toolbar, tablayout color
    public static int getHomeIconBackgroundColorColorParse(Boolean colorParse) {
        int color;
        //int iconNumber = Integer.parseInt(Preferences.get(General.HOME_ICON_NUMBER));
        int iconNumber = 0;
        if (colorParse) {
            if (iconNumber == 0) {
                color = Color.parseColor("#0D79C2"); //R.color.colorPrimary
            } else if (iconNumber == 1) {
                color = Color.parseColor("#6c5ce7"); //R.color.bg_home_menu_one
            } else if (iconNumber == 2) {
                color = Color.parseColor("#00cec9"); //R.color.bg_home_menu_two
            } else if (iconNumber == 3) {
                color = Color.parseColor("#e17055"); //R.color.bg_home_menu_three
            } else if (iconNumber == 4) {
                color = Color.parseColor("#ff7675"); //R.color.bg_home_menu_four
            } else if (iconNumber == 5) {
                color = Color.parseColor("#a29bfe"); //R.color.bg_home_menu_five
            } else if (iconNumber == 6) {
                color = Color.parseColor("#fdcb6e"); //R.color.bg_home_menu_six
            } else {
                color = Color.parseColor("#0D79C2"); //R.color.colorPrimary
            }
            return color;
        } else {
            switch (iconNumber) {
                case 0:
                    return R.color.colorPrimary;
                case 1:
                    return R.color.bg_home_menu_one;
                case 2:
                    return R.color.bg_home_menu_two;
                case 3:
                    return R.color.bg_home_menu_three;
                case 4:
                    return R.color.bg_home_menu_four;
                case 5:
                    return R.color.bg_home_menu_five;
                case 6:
                    return R.color.bg_home_menu_six;
                default:
                    return R.color.colorPrimary;
            }
        }
    }

    public static int getHomeIconBackgroundDrawable(int iconNumber) {
        int color=Color.parseColor("#ffffff");
        //int iconNumber = Integer.parseInt(Preferences.get(General.HOME_ICON_NUMBER));
       /* if (iconNumber == 0) {
            color = Color.parseColor("#0D79C2"); //R.color.colorPrimary
        } else if (iconNumber == 1) {
            color = Color.parseColor("#6c5ce7"); //R.color.bg_home_menu_one
        } else if (iconNumber == 2) {
            color = Color.parseColor("#00cec9"); //R.color.bg_home_menu_two
        } else if (iconNumber == 3) {
            color = Color.parseColor("#e17055"); //R.color.bg_home_menu_three
        } else if (iconNumber == 4) {
            color = Color.parseColor("#ff7675"); //R.color.bg_home_menu_four
        } else if (iconNumber == 5) {
            color = Color.parseColor("#a29bfe"); //R.color.bg_home_menu_five
        } else if (iconNumber == 6) {
            color = Color.parseColor("#fdcb6e"); //R.color.bg_home_menu_six
        } else {
            color = Color.parseColor("#0D79C2"); //R.color.colorPrimary
        }*/
        return color;
    }
}
