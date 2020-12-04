package screen.messagelist;


import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser_ {

    private static final String TAG = JsonParser_.class.getSimpleName();

    // parse json response after authorization call
    public static int authorization(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(General.STATUS)) {
                    if (jsonObject.has(Oauth.CODE)) {
                        OauthPreferences.save(Oauth.CODE, jsonObject.getString(Oauth.CODE));
                    }
                    return jsonObject.getInt(General.STATUS);
                } else {
                    return 11;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return 12;
        }
        return 12;
    }

    //handle json response for oauth token
    public static int token(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(Oauth.ACCESS_TOKEN) && jsonObject.has(Oauth.REFRESH_TOKEN)) {
                    OauthPreferences.save(Oauth.ACCESS_TOKEN, jsonObject.getString(Oauth.ACCESS_TOKEN));
                    OauthPreferences.save(Oauth.REFRESH_TOKEN, jsonObject.getString(Oauth.REFRESH_TOKEN));
                    OauthPreferences.save(Oauth.SCOPE, jsonObject.getString(Oauth.SCOPE));
                    Long expires_in = jsonObject.getLong(Oauth.EXPIRES_IN);
                    Long sys_time = System.currentTimeMillis();
                    Long expires_at = (expires_in * 1000) + sys_time;
                    OauthPreferences.save(Oauth.EXPIRES_IN, "" + expires_in);
                    OauthPreferences.save(Oauth.EXPIRES_AT, "" + expires_at);
                    return 1;
                } else {
                    if (jsonObject.has(General.STATUS)) {
                        return jsonObject.getInt(General.STATUS);
                    } else {
                        return 11;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return 12;
        }
        return 12;
    }

    // given token object from json response received
    public static Token_ token_(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(Oauth.ACCESS_TOKEN) && jsonObject.has(Oauth.REFRESH_TOKEN)) {
                    OauthPreferences.save(Oauth.ACCESS_TOKEN, jsonObject.getString(Oauth.ACCESS_TOKEN));
                    OauthPreferences.save(Oauth.REFRESH_TOKEN, jsonObject.getString(Oauth.REFRESH_TOKEN));
                    OauthPreferences.save(Oauth.SCOPE, jsonObject.getString(Oauth.SCOPE));
                    Long expires_in = jsonObject.getLong(Oauth.EXPIRES_IN);
                    Long sys_time = System.currentTimeMillis();
                    Long expires_at = (expires_in * 1000) + sys_time;
                    OauthPreferences.save(Oauth.EXPIRES_IN, "" + expires_in);
                    OauthPreferences.save(Oauth.EXPIRES_AT, "" + expires_at);
                    return new Token_(jsonObject.getLong(Oauth.EXPIRES_IN),
                            jsonObject.getString(Oauth.SCOPE), jsonObject.getString(Oauth.REFRESH_TOKEN),
                            jsonObject.getString(Oauth.ACCESS_TOKEN), 1);
                } else {
                    return new Token_(0L, "", "", "", 13);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new Token_(0L, "", "", "", 12);
    }
}

