package com.firebase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 05-09-2017
 * Last Modified on 12-12-2017
 **/

/*
 * This file contains constants for firebase
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    static final int NOTIFICATION_ID = 100;
    static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static Map<String, List<String>> mapOfPosts = new HashMap<>();

}
