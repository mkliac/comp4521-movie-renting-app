package com.example.comp4521project;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // ref from https://www.youtube.com/watch?v=B1s2GFI-1H0
        VideoView videoView = findViewById(R.id.videoView);
        Bundle extras = getIntent().getExtras();
        Uri uri = Uri.parse(extras.getString("videoUri"));
        videoView.setVideoURI(uri);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
    }
}
