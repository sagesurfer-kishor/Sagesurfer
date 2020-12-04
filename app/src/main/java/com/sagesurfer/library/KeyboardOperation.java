package com.sagesurfer.library;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 24-08-2017
 *         Last Modified on 15-12-2017
 */

public class KeyboardOperation {

    public static void hide(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

}
