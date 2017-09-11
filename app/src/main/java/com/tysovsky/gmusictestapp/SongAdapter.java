package com.tysovsky.gmusictestapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tysovsky.gmusic.GMusicSong;

import java.util.List;

/**
 * Created by tysovsky on 9/10/17.
 */

public class SongAdapter extends ArrayAdapter<GMusicSong> {
    public SongAdapter(Context context, List<GMusicSong> songs){
        super(context, 0, songs);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_view, parent, false);
        }

        GMusicSong song = getItem(position);

        TextView teTitle = (TextView)convertView.findViewById(R.id.song_title);
        TextView teArtist = (TextView)convertView.findViewById(R.id.song_artist);
        TextView teAlbum = (TextView)convertView.findViewById(R.id.song_album);

        teTitle.setText(song.title);
        teArtist.setText(song.artist);
        teAlbum.setText(song.album);

        return convertView;
    }
}
