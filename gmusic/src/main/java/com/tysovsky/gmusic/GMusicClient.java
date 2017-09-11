package com.tysovsky.gmusic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.tysovsky.gmusic.Core.HttpHandler;
import com.tysovsky.gmusic.Interfaces.GetAllSongsListener;
import com.tysovsky.gmusic.Interfaces.GetStreamUrlListener;
import com.tysovsky.gmusic.Interfaces.LoginListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import svarzee.gps.gpsoauth.AuthToken;
import svarzee.gps.gpsoauth.Gpsoauth;

/**
 * Created by tysovsky on 9/9/17.
 */

public class GMusicClient {

    public static final String TAG = "GMusicClient";


    private Context context;

    private OkHttpClient httpClient;
    private HttpHandler httpHandler;
    private String masterToken;
    private String authToken;

    private String androidId;
    private String androidIdBase10;

    private boolean isSubscribed;

    private static final String jsUrl = "https://mclients.googleapis.com/sj/v2.5/",
            jsStreamUrl = "https://mclients.googleapis.com/music/";


    public GMusicClient(Context context){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.context = context;
        httpClient = new OkHttpClient();
        httpHandler = new HttpHandler();

        //Check if already logged in
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        masterToken = prefs.getString(Constants.SP_MASTER_TOKEN, null);
        authToken = prefs.getString(Constants.SP_AUTH_TOKEN, null);
        androidId = prefs.getString(Constants.SP_ANDROID_ID, null);

        if (androidId != null) {
            androidIdBase10 = new BigInteger(androidId, 16).toString();
            androidIdBase10 = androidIdBase10.substring(0, androidIdBase10.length() - 3);
        }

    }

    public boolean login(String username, String password, String androidId){
        this.androidId = androidId;
        androidIdBase10 = new BigInteger(androidId, 16).toString();
        androidIdBase10 = androidIdBase10.substring(0, androidIdBase10.length() - 3);
        try {

            Gpsoauth gpsoauth = new Gpsoauth(httpClient);

            masterToken = gpsoauth.performMasterLoginForToken(username, password, androidId);

            Response oauthRes = gpsoauth.performOAuth(username, masterToken, androidId,
                    "sj", "com.google.android.music",
                    "38918a453d07199354f8b19af05ec6562ced5788");

            String[] responses = oauthRes.body().string().split("\n");
            for (int i = 0; i < responses.length; i++){
                if (responses[i].contains("Auth=")){
                    authToken=responses[i].substring(5);
                }
            }

            //Save the tokens to SharedPreferences
            SharedPreferences.Editor prefsBuilder = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
            prefsBuilder.putString(Constants.SP_MASTER_TOKEN, masterToken);
            prefsBuilder.putString(Constants.SP_AUTH_TOKEN, authToken);
            prefsBuilder.putString(Constants.SP_ANDROID_ID, androidId);
            prefsBuilder.commit();

            return true;

        }
        catch (Exception e){
            Log.d(TAG, "Exception logging in: " + e.getMessage());
        }
        return false;
    }
    public void loginAsync(final String username, final String password, final String androidId, final LoginListener listener){
        httpHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.OnComplete(login(username, password, androidId));
            }
        });
    }


    public ArrayList<GMusicSong> getAllSongs(){

        HttpUrl.Builder urlBuilder = HttpUrl.parse(jsUrl+"trackfeed").newBuilder()
                .addQueryParameter("alt", "json")
                .addQueryParameter("dv", "0")
                .addQueryParameter("hl", "en_US")
                .addQueryParameter("include-tracks", "true")
                .addQueryParameter("tier", "aa")
                .addQueryParameter("updated-min", "-1");


        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{max-results:20000}");


        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "GoogleLogin auth="+authToken)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()){
                return null;
            }

            ArrayList<GMusicSong> songs = new ArrayList<>();

            JSONArray jArray = new JSONObject(response.body().string()).getJSONObject("data").getJSONArray("items");

            for (int i = 0; i < jArray.length(); i++){
                JSONObject jSong = jArray.getJSONObject(i);
                GMusicSong song = new GMusicSong();
                song.id = UUID.fromString(jSong.getString("id"));
                song.artist = jSong.getString("artist");
                song.album = jSong.getString("album");
                song.title = jSong.getString("title");
                songs.add(song);

            }

            return songs;

        }

        catch (Exception e){
            Log.d(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }
    public void getAllSongsAsync(final GetAllSongsListener listener){
        httpHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.OnCompleted(getAllSongs());
            }
        });
    }


    public String getStreamingUrl(GMusicSong song){
        String[] crypto = getSigAndSalt(song.id.toString());
        HttpUrl.Builder urlBuilder = HttpUrl.parse(jsStreamUrl+"mplay").newBuilder()
                .addQueryParameter("net", "mob")
                .addQueryParameter("dv", "0")
                .addQueryParameter("hl", "en_US")
                .addQueryParameter("opt", "hi")
                .addQueryParameter("tier", "aa")
                .addQueryParameter("sig", crypto[0])
                .addQueryParameter("slt", crypto[1])
                .addQueryParameter("songid", song.id.toString())
                .addQueryParameter("pt", "e");

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("X-Device-ID", androidIdBase10)
                .addHeader("Authorization", "GoogleLogin auth="+authToken)
                .get()
                .build();


        String streamUrl = null;

        try (Response response = httpClient.newCall(request).execute()) {

            streamUrl =  response.networkResponse().toString();
            streamUrl = streamUrl.substring(streamUrl.indexOf("url=")+4, streamUrl.length()-1);

        }

        catch (Exception e){
            Log.d(TAG, "Exception: " + e.getMessage());
        }
        return streamUrl;
    }
    public void getStreamingUrlAsync(final GMusicSong song, final GetStreamUrlListener listener){
        httpHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.OnCompleted(getStreamingUrl(song));
            }
        });
    }


    public void logout(){
        SharedPreferences.Editor prefsBuilder = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        prefsBuilder.remove(Constants.SP_MASTER_TOKEN);
        prefsBuilder.remove(Constants.SP_AUTH_TOKEN);
        prefsBuilder.commit();

    }

    public boolean isAuthenticated(){
        if (masterToken != null && authToken != null){
            return true;
        }
        else{
            return false;
        }
    }


    /**
     * Get signature and salt for a request
     * @param songId UUID of a song to sign
     * @return array of strings where the first string is the signature and the second one is the salt
     */
    public String[] getSigAndSalt(String songId){

        try {
            byte[] s1 = Base64.decode("VzeC4H4h+T2f0VI180nVX8x+Mb5HiTtGnKgH52Otj8ZCGDz9jRWyHb6QXK0JskSiOgzQfwTY5xgLLSdUSreaLVMsVVWfxfa8Rw==", Base64.DEFAULT);
            byte[] s2 = Base64.decode("ZAPnhUkYwQ6y5DdQxWThbvhJHN8msQ1rqJw0ggKdufQjelrKuiGGJI30aswkgCWTDyHkTGK9ynlqTkJ5L4CiGGUabGeo8M6JTQ==", Base64.DEFAULT);

            byte[] s3 = new byte[s1.length];

            int i = 0;
            for (byte b : s1)
                s3[i] = (byte)(b ^ s2[i++]);

            String key = new String(s3, "ASCII");

            String salt = String.valueOf(System.currentTimeMillis());

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            javax.crypto.spec.SecretKeySpec secret = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA1");
            mac.init(secret);
            byte[] digest = mac.doFinal((songId+salt).getBytes());

            String signature = Base64.encodeToString(digest, Base64.URL_SAFE);
            signature = signature.replace("\n","");
            signature = signature.replace("=","");

            String[] res = new String[]{signature, salt};


            return res;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
