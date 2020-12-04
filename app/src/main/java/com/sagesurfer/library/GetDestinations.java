package com.sagesurfer.library;

import com.sagesurfer.directory.DirectoryList;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 19/02/2018
 */

public class GetDestinations {

    public static String getChatVideo(String url){
        return DirectoryList.SENT_IMAGE+ FileCheck.getFileName(url);
    }

    public static String getChatAudio(String url){
        return DirectoryList.SENT_IMAGE+ FileCheck.getFileName(url);
    }

    public static String getChatImage(String url){
        return DirectoryList.SENT_IMAGE+ FileCheck.getFileName(url);
    }

    public static String getChatDocument(String url){
        return DirectoryList.SENT_IMAGE+ FileCheck.getFileName(url);
    }


    public static String getChatVideoReceived(String url){
        return DirectoryList.RECEIVED_VIDEO+ FileCheck.getFileName(url);
    }

    public static String getChatAudioReceived(String url){
        return DirectoryList.RECEIVED_AUDIO+ FileCheck.getFileName(url);
    }

    public static String getChatImageReceived(String url){
        return DirectoryList.RECEIVED_IMAGE+ FileCheck.getFileName(url);
    }

    public static String getChatDocumentReceived(String url){
        return DirectoryList.RECEIVED_DOCUMENT+ FileCheck.getFileName(url);
    }


}
