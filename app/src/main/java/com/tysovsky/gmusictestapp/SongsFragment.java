package com.tysovsky.gmusictestapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by tysovsky on 9/10/17.
 */

public class SongsFragment extends Fragment {

    public static final String TAG = "SongsFragment";

    ListView songsListView;
    SongAdapter songAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        songsListView = (ListView)view.findViewById(R.id.songs_list_view);

        return view;
    }
}
