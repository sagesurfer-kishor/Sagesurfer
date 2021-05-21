package secure;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * This class is encode string to Base64 and decode Base64 encode to normal string
 */

public class _Base64 {

    public static String encode(String text) {
        byte[] data = new byte[0];
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static String decode(String base64) {
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
