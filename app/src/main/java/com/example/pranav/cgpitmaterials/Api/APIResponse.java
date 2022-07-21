package com.example.pranav.cgpitmaterials.Api;

import org.json.JSONObject;


    public interface APIResponse {

        public void onSuccess(JSONObject object);

        public void onFailure(String error);

    }

