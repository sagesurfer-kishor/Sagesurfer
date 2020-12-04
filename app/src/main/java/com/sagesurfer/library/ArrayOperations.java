package com.sagesurfer.library;

import java.util.ArrayList;


/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 24/03/2018
 *         Last Modified on
 */

/*
* This class converts list of integer or string to comma separated string avoiding extra any white space
*/

public class ArrayOperations {

    private static final String TAG = ArrayOperations.class.getSimpleName();

    public static String listToString(ArrayList<Integer> list) {
        String dataList = list.toString();
        dataList = dataList.replaceAll("\\[", "").replaceAll("]", "");
        dataList = dataList.replace(" ", "");
        return dataList;
    }

    public static String stringListToString(ArrayList<String> list) {
        String dataList = list.toString();
        dataList = dataList.replaceAll("\\[", "").replaceAll("]", "");
        dataList = dataList.replace(" ", "");
        return dataList;
    }

    public static String listLongToString(ArrayList<Long> list) {
        String dataList = list.toString();
        dataList = dataList.replaceAll("\\[", "").replaceAll("]", "");
        dataList = dataList.replace(" ", "");
        return dataList;
    }

}
