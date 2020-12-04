package com.modules.postcard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 19-07-2017
 *         Last Modified on 14-12-2017
 */

/*
* Get random color from color array which can be assigned to background of any circle/shape
*/


class PostcardManipulations {

    static boolean isValidImage(String image) {
        return !image.contains("avatar/user_native_female.png")
                && !image.contains("avatar/user_native.png") && !(!image.contains(".png")
                && !image.contains(".jpg") && !image.contains(".jpeg"));
    }

    static int getRandomMaterialColor(Context mContext) {
        int returnColor = Color.GRAY;
        int arrayId = mContext.getResources().getIdentifier("mdcolor_400", "array",
                mContext.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = mContext.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }
}
