package com.modules.sos;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 04-08-2017
 *         Last Modified on 14-12-2017
 */

/*
* This class contains message list needed for sos operations
* actionMessages are display message for user with respective actions like attending/ non attending
*/

class GetMessages_ {

    static String actionMessage(String name, int method) {
        String message = "Thanks!!! Looks like you are attending " + name + ". Click \"Ok\" to confirm";
        switch (method) {
            case 1:
                message = "Thanks!!! Looks like you are making phone to call to " + name + ". Click \"Ok\" to confirm";
                break;
            case 2:
                message = "Thanks!!! Looks like you are meeting " + name + ". Click \"Ok\" to confirm.";
                break;
            case 3:
                message = "Thanks!!! Looks like you are going to text / video chat with the " + name + ". Click \"Ok\" to confirm";
                break;
            case 4:
                message = "Thanks!!! Looks like you are reaching out to " + name + " through other means. Click \"Ok\" to confirm.";
                break;
            case 7:
                message = "Sorry to hear that you would not be able to attend the sos. Please click ok to confirm.";
                //message = "People with strong family or social connection are generally healthier than those who lack a support network.";
                break;
            default:
                return message;
        }
        return message;
    }

    static String getAttendingMethods(int type, String method) {
        switch (type) {
            case 1:
                return "Phone Call";
            case 2:
                return "In Person";
            case 3:
                return "Chat";
            default:
                return method;
        }
    }
}
