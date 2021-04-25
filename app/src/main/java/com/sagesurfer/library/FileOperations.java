package com.sagesurfer.library;

import android.webkit.MimeTypeMap;

import com.sagesurfer.models.PostcardAttachment_;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/3/2018
 *         Last Modified on 4/3/2018
 */

/*
* File related operations are done through this class like get file size from file
* Convert byte size to readable size
* get file extension, name and check file/folder is present of not
*/

public class FileOperations {

    private static final double SPACE_KB = 1024;
    private static final double SPACE_MB = 1024 * SPACE_KB;
    private static final double SPACE_GB = 1024 * SPACE_MB;

    public static String bytes2String(long sizeInBytes) {
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);
        try {
            if (sizeInBytes < SPACE_KB) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if (sizeInBytes < SPACE_MB) {
                return nf.format(sizeInBytes / SPACE_KB) + " KB";
            } else if (sizeInBytes < SPACE_GB) {
                return nf.format(sizeInBytes / SPACE_MB) + " MB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }
        return "N/A";
    }

    public static String getFileExtension(String filePath) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(filePath);
        try {
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if (type == null) {
                ext = filePath.substring(filePath.lastIndexOf("."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ext;
    }

    public static String getSize(String file_path) {
        File file = new File(file_path);
        int size = Integer.parseInt(String.valueOf(file.length() / 1024));
        String hrSize;
        double m = size / 1024.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(size).concat(" KB");
        }
        return hrSize;
    }

    public static String getFileName(String url) {
        String file_name = null;
        if (url.lastIndexOf(".") != -1) {
            file_name = url.substring(url.lastIndexOf("/") + 1);
        }
        return file_name;
    }

    public static double getSizeMB(String file_path) {
        File file = new File(file_path);
        int size = Integer.parseInt(String.valueOf(file.length() / 1024));
        return size / 1024.0;
    }

    public static long getCumulativeSize(ArrayList<PostcardAttachment_> attachmentArrayList) {
        long sizeInBytes = 0;
        for (PostcardAttachment_ postcardAttachment_ : attachmentArrayList) {
            try {
                sizeInBytes = sizeInBytes + postcardAttachment_.getSize();
            } catch (NumberFormatException ex) {
                sizeInBytes = 0;
            }
        }
        return sizeInBytes;
    }

    public static void checkFolderPresence(String directory) {
        File myDir = new File(directory);
        if (!myDir.exists()) {
            myDir.mkdir();
        }
    }

}
