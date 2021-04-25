package com.sagesurfer.directory;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class DirectoryList {

    private static final String ROOT_DIR = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String DIR_MAIN = ROOT_DIR + "/Collaborative Care/";
    public static final String DIR_MY_PIC = DIR_MAIN + "my_pic.jpg";
    public static final String DIR_SHARED_FILES = DIR_MAIN + "Shared Files/";
    public static final String DIR_CONSENT_FILES = DIR_MAIN + "Consent Files/";
    public static final String DIR_CHAT = DIR_MAIN + "Chat/";
    public static final String DIR_PROFILE = "Profiles/";
    public static final String ATTACHMENTS_DIR = DIR_MAIN + "Attachments/";
    public static final String WALL_DOWNLOAD = DIR_MAIN + "Wall Downloads/";
    public static final String DIR_NOTES = DIR_MAIN + "Notes/";

    public static final String SENT_IMAGE = DIR_CHAT + "Sent Images/";
    public static final String SENT_VIDEO = DIR_CHAT + "Sent Videos/";
    public static final String SENT_AUDIO = DIR_CHAT + "Sent Audio/";
    public static final String SENT_DOCUMENT = DIR_CHAT + "Sent Documents/";

    public static final String RECEIVED_IMAGE = DIR_CHAT + "Received Images/";
    public static final String RECEIVED_VIDEO = DIR_CHAT + "Received Videos/";
    public static final String RECEIVED_AUDIO = DIR_CHAT + "Received Audio/";
    public static final String RECEIVED_DOCUMENT = DIR_CHAT + "Received Documents/";
}
