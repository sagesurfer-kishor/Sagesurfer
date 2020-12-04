package com.sagesurfer.validator;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 * Created on 13-09-2017
 * Last Modified on 15-12-2017
 */

public class FileSharing {

    // check folder name for valid name
    public static boolean isValidFolderName(CharSequence seq) {
        int len = seq.length();
        for (int i = 0; i < len; i++) {
            char c = seq.charAt(i);
            // Test for all positive cases
            if ('0' <= c && c <= '9') continue;
            if ('a' <= c && c <= 'z') continue;
            if ('A' <= c && c <= 'Z') continue;
            if (c == ' ') continue;
            if (c == '-') continue;
            if (c == '_') continue;
            return false;
        }
        // All seen chars were valid, succeed
        return true;
    }
}
