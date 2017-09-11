package com.tysovsky.gmusic.Core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by tysovsky on 9/10/17.
 */

public class HttpHandler extends Handler {

    private static HandlerThread mHttpThread;


    public HttpHandler(Looper looper){
        super(looper);
    }

}
