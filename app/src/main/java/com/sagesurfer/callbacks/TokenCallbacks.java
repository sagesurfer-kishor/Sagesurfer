package com.sagesurfer.callbacks;

import org.json.JSONObject;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * OAuth call back to get token after successful authorization
 */

public interface TokenCallbacks {
    void tokenSuccessCallback(JSONObject jsonObject);

    void tokenFailCallback(JSONObject jsonObject);
}
