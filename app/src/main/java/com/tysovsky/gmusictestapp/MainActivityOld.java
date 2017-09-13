package com.tysovsky.gmusictestapp;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tysovsky.gmusic.Core.GMusicClient;
import com.tysovsky.gmusic.Models.GMusicSong;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivityOld extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener,MediaPlayer.OnBufferingUpdateListener{

    EditText etUsername, etPassword;
    Button btnLogin, btnGetSongs;

    GMusicClient gMusicClient;
    MediaPlayer mediaPlayer;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);

//        etUsername = (EditText)findViewById(R.id.etUsername);
//        etPassword = (EditText)findViewById(R.id.etPassword);
//        btnLogin = (Button)findViewById(R.id.btnLogin);
//        btnLogin.setOnClickListener(this);
//        btnGetSongs = (Button)findViewById(R.id.btnGetSongs);
//        btnGetSongs.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);


        ListView songListView = (ListView)findViewById(R.id.songListView);
        gMusicClient = new GMusicClient(this);
        final ArrayList<GMusicSong> songs = gMusicClient.getAllSongs();

        SongAdapter adapter = new SongAdapter(this, songs);
        songListView.setAdapter(adapter);


        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String url = gMusicClient.getStreamingUrl(songs.get(i));
                try {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //gMusicClient.getStreamingUrl(null);
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.btnLogin:
//                gMusicClient.login(etUsername.getText().toString(), etPassword.getText().toString(), Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
//                break;
//            case R.id.btnGetSongs:
//                gMusicClient.getAllSongs();
//                break;
//        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
