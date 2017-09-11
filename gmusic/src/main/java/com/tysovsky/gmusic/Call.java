package com.tysovsky.gmusic;

import okhttp3.OkHttpClient;

/**
 * Created by tysovsky on 9/9/17.
 */

public abstract class Call {
    public static final int LOGIN = 0,
                            LOGOUT = 1,
                            GET_ALL_SONGS = 2,
                            GET_STREAM_URL = 3;

}

