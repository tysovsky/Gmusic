package com.tysovsky.gmusic.Interfaces;

import com.tysovsky.gmusic.Models.GMusicSong;

import java.util.List;

/**
 * Created by tysovsky on 9/10/17.
 */

public interface GetAllSongsListener {
    void OnCompleted(int status, List<GMusicSong> songs);
}
