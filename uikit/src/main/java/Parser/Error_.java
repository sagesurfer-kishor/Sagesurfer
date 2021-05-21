package Parser;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 13/03/2018
 *         Last Modified on
 */

public class Error_ {

    public static int oauth(String response, Context _context) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
        if (jsonObject.has("status")) {
            int result = jsonObject.get("status").getAsInt();
            if (result == 13) {
                //ShowToast.authenticationFailed(_context);
            }
            return result;
        }
        return 0;
    }

    public static int noData(String response, String jsonName, Context _context) {
        JsonArray jsonArray = GetJson_.getArray(response, jsonName);
        if (jsonArray != null) {
            JsonObject object = jsonArray.get(0).getAsJsonObject();
            if (object.has("status")) {
                int result = object.get("status").getAsInt();
                if (result == 2) {
                    return result;
                }
            }
        }
        return 0;
    }
}
