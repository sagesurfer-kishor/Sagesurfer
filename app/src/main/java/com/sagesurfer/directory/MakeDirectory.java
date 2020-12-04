package com.sagesurfer.directory;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class MakeDirectory {
    private static ArrayList<String> makeDirList() {
        ArrayList<String> dirList = new ArrayList<>();
        dirList.add(DirectoryList.DIR_MAIN);
        dirList.add(DirectoryList.DIR_SHARED_FILES);
        dirList.add(DirectoryList.DIR_CHAT);
        dirList.add(DirectoryList.ATTACHMENTS_DIR);
        dirList.add(DirectoryList.DIR_PROFILE);

        dirList.add(DirectoryList.RECEIVED_IMAGE);
        dirList.add(DirectoryList.RECEIVED_VIDEO);
        dirList.add(DirectoryList.RECEIVED_AUDIO);
        dirList.add(DirectoryList.RECEIVED_DOCUMENT);

        return dirList;
    }

    public static void makeDirectories() {
        for (int i = 0; i < makeDirList().size(); i++) {
            File folder = new File(makeDirList().get(i));
            if (!folder.exists()) {
                @SuppressWarnings("unused")
                boolean success = folder.mkdir();
            }
        }
    }
}
