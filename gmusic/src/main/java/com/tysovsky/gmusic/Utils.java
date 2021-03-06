package com.tysovsky.gmusic;

import android.util.Base64;

import com.tysovsky.gmusic.Models.GMusicSong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by tysovsky on 11/09/17.
 */

public class Utils {
    /**
     * Get signature and salt for a request
     * @param songId UUID of a song to sign
     * @return array of strings where the first string is the signature and the second one is the salt
     */
    public static String[] getSigAndSalt(String songId){

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

    public static String androidIdHexToDecimal(String hexAndroidId){
        String androidIdDecimal= new BigInteger(hexAndroidId, 16).toString();
        androidIdDecimal = androidIdDecimal.substring(0, androidIdDecimal.length() - 3);

        return androidIdDecimal;
    }

    public static ArrayList<GMusicSong> ConvertJsonToGMusicSongList(String jsonResponse) throws JSONException {
        ArrayList<GMusicSong> songs = new ArrayList<>();

        JSONArray jArray = new JSONObject(jsonResponse).getJSONObject("data").getJSONArray("items");

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
}
