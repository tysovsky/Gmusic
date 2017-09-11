package com.tysovsky.gmusic.Calls;

import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.framed.Header;

/**
 * Created by tysovsky on 9/10/17.
 */

public abstract class BaseCall {

    private static final String jsUrl = "https://mclients.googleapis.com/sj/v2.5/",
            jsStreamUrl = "https://mclients.googleapis.com/music/";

    private Request mRequest;
    private HttpUrl mHttpUrl;
    private ArrayList<Header> headers;
    private OkHttpClient mHttpClient;

    public BaseCall(OkHttpClient client){
        mHttpClient = client;

    }




    public Response perform() throws java.io.IOException{
        return mHttpClient.newCall(mRequest).execute();
    }

    public void performAsync(Callback callback){
        mHttpClient.newCall(mRequest).enqueue(callback);
    }


}
