package com.sagesurfer.library;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 17-07-2017
 *         Last Modified on 15-12-2017
 */

/*
* This class provides inbuilt messages for sos module while creating and forwarding sos
*/

public class SosInbuiltMessage {

    public static List<String> getList() {
        List<String> messageList = new ArrayList<>();
        messageList.add("Don't worry. I will call you in few minutes.");
        messageList.add("I am on my way");
        messageList.add("We are with you");
        messageList.add("I agree with you");
        messageList.add("I understand what you are going through.");
        messageList.add("Relax. Take a deep breath");
        messageList.add("Please take your medicine");
        messageList.add("Nobody will harm you.");
        return messageList;
    }

    public static List<String> getInbuiltMessage() {
        List<String> messageList = new ArrayList<>();
        messageList.add("Need help now!");
        messageList.add("I had a bad day at school");
        messageList.add("I had a fight");
        messageList.add("Feeling overwhelmed, please call");
        messageList.add("In trouble, please call");
        return messageList;
    }
/*
    static ArrayList<String> getAttendingMethods() {
        ArrayList<String> methodList = new ArrayList<>();
        methodList.add("Phone Call");
        methodList.add("In Person");
        methodList.add("Chat");
        return methodList;
    }

    */
}
