package com.modules.motivation.fragment;

/**
 * Created by Kailash.
 */

public class MotivationContentType_ {
    public static int nameToType(String type) {
        if (type.equalsIgnoreCase("Images") || type.equalsIgnoreCase("1")) {
            return 1;
        }
        if (type.equalsIgnoreCase("Videos") || type.equalsIgnoreCase("2")) {
            return 2;
        }
        if (type.equalsIgnoreCase("Audio") || type.equalsIgnoreCase("3")) {
            return 3;
        }
        if (type.equalsIgnoreCase("Text Articles") || type.equalsIgnoreCase("4")) {
            return 4;
        }

        return 0;
    }
}
