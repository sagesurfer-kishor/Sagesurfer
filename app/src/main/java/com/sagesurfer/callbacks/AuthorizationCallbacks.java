package com.sagesurfer.callbacks;

import org.json.JSONObject;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * OAuth call back after authorization
 */

public interface AuthorizationCallbacks {

    void authorizationSuccessCallback(JSONObject jsonObject);

    void authorizationFailCallback(JSONObject jsonObject);
}
