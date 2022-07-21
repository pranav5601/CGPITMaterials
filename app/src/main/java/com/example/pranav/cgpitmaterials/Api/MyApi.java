package com.example.pranav.cgpitmaterials.Api;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.pranav.cgpitmaterials.Http.HttpConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;

public class MyApi {
    Context context;
    Properties properties;
    public static Message message;

    public MyApi(Context context){

        this.context=context;
        Resources resources=context.getResources();
        LoadAssetProperties load=new LoadAssetProperties();
        properties=load.loadRESTApiFile(resources,"rest.properties",context);
    }


    public void sendToken(final APIResponse listener,String enrollment,String semester,String branch,String token){
        String url=properties.getProperty("SENDTOKEN");
        Handler handler=new Handler(){

            public void handleMessage(Message message){
                switch (message.what){
                    case HttpConnection.DID_START:
                        break;
                    case HttpConnection.DID_SUCCEED:
                        getSuccessListener(message, listener);
                        break;

                    case HttpConnection.DID_ERROR:
                        getErrorListener(message, listener);
                        break;
                }
            }
        };

        HttpConnection obj=new HttpConnection(handler);
        String key[]={"enrollment","semester","branch","token"};
        String value[]={enrollment,semester,branch,token};
        obj.setkey(key);
        obj.setvalue(value);
        obj.post(url,"0");

    }

    public void sendNotification(final APIResponse listener,String semester,String branch, String title, String description){
        String url=properties.getProperty("CALLNOTIFICATION");
        Log.e("14nov",url+" "+semester+" "+branch+" "+ title+" "+message);

        Handler handler=new Handler(){

            public void handleMessage(Message message){
                switch (message.what){
                    case HttpConnection.DID_START:
                        break;
                    case HttpConnection.DID_SUCCEED:
                        getSuccessListener(message, listener);
                        break;

                    case HttpConnection.DID_ERROR:
                        getErrorListener(message, listener);
                        break;
                }
            }
        };

        HttpConnection obj=new HttpConnection(handler);
        String key[]={"semester","branch","title","desc"};
        String value[]={semester,branch,title,description};
        obj.setkey(key);
        obj.setvalue(value);
        obj.post(url,"0");

    }

    public void deleteToken(final APIResponse listener,String enrollment,String token){
        String url=properties.getProperty("DELETETOKEN");
        Handler handler=new Handler(){

            public void handleMessage(Message message){
                switch (message.what){
                    case HttpConnection.DID_START:
                        break;
                    case HttpConnection.DID_SUCCEED:
                        getSuccessListener(message, listener);
                        break;

                    case HttpConnection.DID_ERROR:
                        getErrorListener(message, listener);
                        break;
                }
            }
        };

        HttpConnection obj=new HttpConnection(handler);
        String key[]={"enrollment","token"};
        String value[]={enrollment,token};
        obj.setkey(key);
        obj.setvalue(value);
        obj.post(url,"0");

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
