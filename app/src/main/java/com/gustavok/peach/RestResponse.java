package com.gustavok.peach;

import org.json.JSONObject;

/**
 *
 * Created by GustavoK on 04/05/16.
 */
public class RestResponse {
    private JSONObject response;

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }
}
