package com.example.moallemtask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;

public class videoplayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //displaying video and play it automatically
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        String URL = getIntent().getStringExtra("URL");
        VideoView video = findViewById(R.id.videoView);
        Uri uri = Uri.parse(URL);
        video.setVideoURI(uri);
        video.requestFocus();
        video.start();
    }
}
