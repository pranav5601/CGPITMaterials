package com.example.pranav.cgpitmaterials.Api;


import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

import com.example.pranav.cgpitmaterials.Http.HttpConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;

public class APIServer {

    Context context;
    Properties properties;

    String Url;

    public static Message mess;

    public APIServer(Context context) {

        this.context = context;

        Resources resources = context.getResources();
        LoadAssetProperties load = new LoadAssetProperties();
        properties = load
                .loadRESTApiFile(resources, "rest.properties", context);
    }

    public void getLogin(final APIResponse listener, final String user_email,
                         final String password, final String phone_udid,
                         final String device_type) {

        Url = properties.getProperty("LOGIN");
        Handler handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case HttpConnection.DID_START: {
                        break;
                    }
                    case HttpConnection.DID_SUCCEED: {
                        getSuccessListener(message, listener);
                        break;
                    }
                    case HttpConnection.DID_ERROR: {
                        getErrorListener(message, listener);
                        break;
                    }
                }
            }
        };
        HttpConnection obj = new HttpConnection(handler);

        String[] key = { "user_email", "password", "udid", "device_type" };
        String[] value = { user_email, password, phone_udid, device_type };

        obj.setkey(key);
        obj.setvalue(value);
        obj.post(Url, "0");

    }

    public void getUnratedImages(final APIResponse listener,
                                 String authorization, int user_id) {

        Url = properties.getProperty("GET_UNRATED_IMAGES") + "user_id=" + user_id;
        Handler handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case HttpConnection.DID_START: {
                        break;
                    }
                    case HttpConnection.DID_SUCCEED: {
                        getSuccessListener(message, listener);
                        break;
                    }
                    case HttpConnection.DID_ERROR: {
                        getErrorListener(message, listener);
                        break;
                    }
                }
            }
        };
        HttpConnection obj = new HttpConnection(handler);

        obj.get(Url, authorization);

    }

    public void getSuccessListener(Message message, APIResponse listener) {
        try {
            JSONObject jsonObj = new JSONObject(message.obj.toString());
            listener.onSuccess(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getErrorListener(Message message, APIResponse listener) {

        Exception e = (Exception) message.obj;
        e.printStackTrace();
        listener.onFailure(e.toString());

    }

}
