package com.example.user.mediaplayerdemo.activity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.user.mediaplayerdemo.R;
import com.example.user.mediaplayerdemo.helper.Utilities;
import com.example.user.mediaplayerdemo.model.SongsModel;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by satish on 24/1/16.
 */
public class MediaPlayerActivity extends AppCompatActivity {
    private ArrayList<SongsModel> songsModels;
    private ImageView btnPlay;
    private ImageView btnForward;
    private ImageView btnBackward;
    private ImageView btnPlaylist;
    private ImageView btnStop;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songNumberLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    ;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private Utilities utils;
    private String filePath = "";
    private String fileName = "";
    private int songNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        // All player buttons
        btnPlay = (ImageView) findViewById(R.id.id_play);
        btnForward = (ImageView) findViewById(R.id.id_forward);
        btnBackward = (ImageView) findViewById(R.id.id_backward);
        btnPlaylist = (ImageView) findViewById(R.id.id_list);
        btnStop = (ImageView) findViewById(R.id.id_stop);
        songProgressBar = (SeekBar) findViewById(R.id.id_seekBar);
        songTitleLabel = (TextView) findViewById(R.id.id_song_name);
        songNumberLabel = (TextView) findViewById(R.id.id_song_number);
        songCurrentDurationLabel = (TextView) findViewById(R.id.lbl_start_time);
        songTotalDurationLabel = (TextView) findViewById(R.id.lbl_end_time);
        songsModels = new ArrayList<>();
        utils = new Utilities();
        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        fileName = intent.getStringExtra("fileName");
        songNumber = intent.getIntExtra("fileNumber", 0);
        getMusicList();
        try {
            if (filePath == null) {
                SongsModel songsModel = songsModels.get(0);
                mediaPlayer.setDataSource(songsModel.getFilePath());
                mediaPlayer.prepare();
                songTitleLabel.setText(songsModel.getFileName());
                songNumberLabel.setText(String.valueOf(songsModel.getSong_number()));
            } else {
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                songTitleLabel.setText(fileName);
                songNumberLabel.setText(String.valueOf(songNumber));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        updateProgressBar();
        if (!mediaPlayer.isPlaying()) {
            btnPlay.setImageResource(R.mipmap.ic_play);
        }
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.mipmap.ic_play);
                    }
                } else {
                    // Resume song
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.mipmap.ic_pause);
                    }
                }
            }
        });
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // get current song position
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            }
        });
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), SongsListActivity.class);
                startActivityForResult(i, 100);
            }
        });

        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                // forward or backward to certain seconds
                mediaPlayer.seekTo(currentPosition);
                // update timer progress again
                updateProgressBar();
            }
        });
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();
            Log.d("time", "" + totalDuration + " " + currentDuration);
            // Displaying Total Duration time
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));
            // Displaying time completed playing
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    private void getMusicList() {
        @SuppressWarnings("deprecation") final Cursor mCursor = managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");
        Log.d("audio list", "" + mCursor.getCount());
        if (mCursor.moveToFirst()) {
            int i = 1;
            do {
                SongsModel mediaFileListModel = new SongsModel();
                mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                mediaFileListModel.setSong_number(i);
                songsModels.add(mediaFileListModel);
                i++;
            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }
}
