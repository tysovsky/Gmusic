package com.tysovsky.gmusic.Calls;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by tysovsky on 9/10/17.
 */

public abstract class ListCall extends BaseCall {
    public ListCall(OkHttpClient client) {
        super(client);
    }


    @Override
    public HttpUrl buildUrl() {
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .addQueryParameter("alt", "json")
                .addQueryParameter("")
    }
}
