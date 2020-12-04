package com.sagesurfer.library;

import android.webkit.MimeTypeMap;

import com.sagesurfer.constant.General;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 19/02/2018
 */

/*
* Perform file related operations like get size, type, name, etc.
*/

public class FileCheck {

    private static final double SPACE_KB = 1024;
    private static final double SPACE_MB = 1024 * SPACE_KB;
    private static final double SPACE_GB = 1024 * SPACE_MB;

    private static final String TAG = FileCheck.class.getSimpleName();

    public static boolean validSize(String file_path, int max_size) {
        File file = new File(file_path);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        double m = file_size / 1024.0;
        return m < max_size;
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

    public static double getSizeMB(String file_path) {
        File file = new File(file_path);
        int size = Integer.parseInt(String.valueOf(file.length() / 1024));
        return size / 1024.0;
    }

    public static long getCumulativeSize(ArrayList<HashMap<String, String>> fileList) {
        long sizeInBytes = 0;
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).containsKey(General.SIZE)) {
                try {
                    sizeInBytes = sizeInBytes + Long.parseLong(fileList.get(i).get(General.SIZE));
                } catch (NumberFormatException ex) {
                    sizeInBytes = 0;
                }
            }
        }
        return sizeInBytes;
    }

    public static long getTotalSize(ArrayList<HashMap<String, String>> fileList) {
        long size = 0;
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).containsKey(General.SIZE)) {
                size = size + Long.parseLong(fileList.get(i).get(General.SIZE));
            }
        }
        return size;
    }

    public static boolean isExist(String file_path) {
        return new File(file_path).exists();
    }

    public static String getName(String url) {
        String file_name = null;
        if (url.lastIndexOf(".") != -1) {
            file_name = url.substring(url.lastIndexOf("/") + 1);
        }
        return file_name;
    }

    public static String getMailAttachName(String name) {
        String file_name = null;
        if (name.lastIndexOf(".") != -1) {
            file_name = name.substring(name.indexOf("1_1") + 3);
        }
        return file_name;
    }

    static String getFileName(String url) {
        String file_name = null;
        if (url.lastIndexOf(".") != -1) {
            file_name = url.substring(url.lastIndexOf("=") + 1);
        }
        return file_name;
    }

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

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
