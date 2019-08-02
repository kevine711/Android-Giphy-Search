package com.kevinersoy.giphysearch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.kevinersoy.giphysearch.GlideApp;
import com.kevinersoy.giphysearch.R;
import com.kevinersoy.giphysearch.model.giphy.GiphyData;

public class FullscreenActivity extends AppCompatActivity {

    public static final String EXTRA_GIPHYDATA_JSON = "com.kevinersoy.giphysearch.fullscreenactivity.giphyjson";

    GiphyData mGiphyData;
    ImageView mGifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mGifImageView = findViewById(R.id.gif_full_screen);

        Intent intent = getIntent();
        String json = intent.getStringExtra(EXTRA_GIPHYDATA_JSON);
        Gson gson = new Gson();
        mGiphyData = gson.fromJson(json, GiphyData.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlideApp.with(this)
                .asGif()
                .load(mGiphyData)
                .into(mGifImageView);
    }
}
