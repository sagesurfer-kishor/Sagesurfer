package com.sagesurfer.library;

import android.webkit.MimeTypeMap;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Monika M(monikam@sagesurfer.com)
 *         Created on 4/3/2018
 *         Last Modified on 4/3/2018
 */

/*
* This file extract and compare file extension for multiple file types vis image, audio, video, etc.
*/

public class CheckFileType {

    public static boolean videoFile(String filepath) {
        //3gp, .mp4, .flv, .wmv, .webm, .avi, .mov
        Set<String> words = new HashSet<>();
        words.add("mp4");
        words.add("3gp");
        words.add("flv");
        words.add("wmv");
        words.add("webm");
        words.add("avi");
        words.add("mov");
        for (String word : words) {
            if (FileOperations.getFileExtension(filepath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean imageFile(String filepath) {
        filepath = filepath.toLowerCase();
        Set<String> words = new HashSet<>();
        words.add("jpeg");
        words.add("jpg");
        words.add("jfif");
        words.add("png");
        words.add("PNG");
        words.add("JPG");
        words.add("JPEG");
        words.add("tiff");
        words.add("bmp");
        words.add("gif");
        words.add("pgm");
        words.add("pbm");
        for (String word : words) {
            if (FileOperations.getFileExtension(filepath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean audioFile(String audioPath) {
        //.mp3, .aac, .m4a, .ogg, .oga, .wav
        Set<String> words = new HashSet<>();
        words.add("mp3");
        words.add("aac");
        words.add("m4a");
        words.add("ogg");
        words.add("oga");
        words.add("mpeg");
        words.add("wav");
        for (String word : words) {
            if (FileOperations.getFileExtension(audioPath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean executableFile(String audioPath) {
        //.mp3, .aac, .m4a, .ogg, .oga, .wav
        Set<String> words = new HashSet<>();
        words.add("exe");
        words.add("deb");
        words.add("rpm");
        words.add("apk");
        words.add("lnk");
        words.add("bat");
        for (String word : words) {
            if (FileOperations.getFileExtension(audioPath).contains(word)) {
                return true;
            }
        }
        return false;
    }


    public static boolean docFile(String docPath) {
        Set<String> words = new HashSet<>();
        words.add("doc");
        words.add("docx");
        words.add("DOC");
        words.add("DOCX");
        words.add("rtf");
        for (String word : words) {
            if (FileOperations.getFileExtension(docPath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean pptFile(String docPath) {
        Set<String> words = new HashSet<>();
        words.add("ppt");
        words.add("pptx");
        words.add("PPT");
        words.add("PPTX");
        for (String word : words) {
            if (FileOperations.getFileExtension(docPath).contains(word)) {
                return true;
            }
        }
        return false;
    }


    public static boolean xlsFile(String xlsPath) {
        //.mp3, .aac, .m4a, .ogg, .oga, .wav
        Set<String> words = new HashSet<>();
        words.add("xls");
        words.add("xlsx");
        words.add("XLS");
        words.add("XLSX");
        for (String word : words) {
            if (FileOperations.getFileExtension(xlsPath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    static boolean xmlFile(String xlsPath) {
        Set<String> words = new HashSet<>();
        words.add("xml");
        for (String word : words) {
            if (FileOperations.getFileExtension(xlsPath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    static boolean htmlFile(String xlsPath) {
        Set<String> words = new HashSet<>();
        words.add("html");
        for (String word : words) {
            if (FileOperations.getFileExtension(xlsPath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean pdfFile(String pdfPath) {
        Set<String> words = new HashSet<>();
        words.add("pdf");
        for (String word : words) {
            if (FileOperations.getFileExtension(pdfPath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static boolean textFile(String textPath) {
        Set<String> words = new HashSet<>();
        words.add(".txt");
        for (String word : words) {
            if (FileOperations.getFileExtension(textPath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    static boolean csvFile(String textPath) {
        //.mp3, .aac, .m4a, .ogg, .oga, .wav
        Set<String> words = new HashSet<>();
        words.add("csv");
        for (String word : words) {
            if (FileOperations.getFileExtension(textPath).contains(word)) {
                return true;
            }
        }
        return false;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static boolean isDocument(String filePath) {
        //.doc , .docx, .pdf, .xls, .txt, .html, xlsx, .ppt, .pptx + above image/audio/video
        Set<String> words = new HashSet<>();
        words.add("doc");
        words.add("docx");
        words.add("pdf");
        words.add("xls");
        words.add("xlsx");
        words.add("ppt");
        words.add("pptx");
        words.add("txt");
        words.add("html");
        words.add("xml");
        for (String word : words) {
            if (FileOperations.getFileExtension(filePath).contains(word)) {
                return true;
            }
        }
        return false;
    }
}
