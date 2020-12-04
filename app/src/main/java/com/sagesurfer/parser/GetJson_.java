package com.sagesurfer.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class GetJson_ {

    public static JsonArray getArray(String response, String jsonName) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
        if (jsonObject.has(jsonName)) {
            return jsonObject.getAsJsonArray(jsonName);
        } else {
            return null;
        }
    }

    public static JsonObject getObject(String response, String jsonName) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
        return jsonObject.getAsJsonObject(jsonName);
    }

    public static JsonObject getJson(String response) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(response).getAsJsonObject();
    }
}
