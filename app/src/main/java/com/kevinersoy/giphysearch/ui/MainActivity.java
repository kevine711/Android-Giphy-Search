package com.kevinersoy.giphysearch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.kevinersoy.giphysearch.EndlessRecyclerViewScrollListener;
import com.kevinersoy.giphysearch.GlideApp;
import com.kevinersoy.giphysearch.R;
import com.kevinersoy.giphysearch.data.Giphy;
import com.kevinersoy.giphysearch.model.giphy.GiphyData;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Giphy.GiphyCallbacks {

    private static final String TAG = "MainActivity.class";

    private SearchView mSearchView;
    private GifRecyclerAdapter mGifRecyclerAdapter;
    private EndlessRecyclerViewScrollListener mScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = findViewById(R.id.text_search);

        setup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register as listener with Giphy Singleton
        Giphy.getInstance().registerListener(this);
        setupClickListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister as listener with Giphy Singleton
        Giphy.getInstance().unregisterListener(this);
    }

    private void setup() {
        // Setup recyclerview
        setupRecyclerView();

        // Initialize with trending gifs
        if(Giphy.getInstance().getData().size() == 0){
            giphySearch("");
        } else{
            Log.d(TAG, "Giphy has data : " + Giphy.getInstance().getData().size());
            onGiphyDataReady(Giphy.getInstance().getData());
        }
    }

    private void setupClickListeners() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s != null){
                    giphySearch(s);
                    mSearchView.clearFocus();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s == null || s.length() == 0){
                    giphySearch("");
                    return true;
                }
                return false;
            }
        });
    }

    private void setupRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_gifs);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        RequestBuilder<Drawable> gifItemRequest = GlideApp.with(this)
                .asDrawable();
        ViewPreloadSizeProvider<GiphyData> preloadSizeProvider =
                new ViewPreloadSizeProvider<>();

        mGifRecyclerAdapter = new GifRecyclerAdapter(this, null, preloadSizeProvider, gifItemRequest);
        recyclerView.setAdapter(mGifRecyclerAdapter);

        RecyclerViewPreloader<GiphyData> preloader =
                new RecyclerViewPreloader<>(GlideApp.with(this), mGifRecyclerAdapter, preloadSizeProvider, 10);
        recyclerView.addOnScrollListener(preloader);

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Giphy.getInstance().fetch();
            }
        };
        recyclerView.addOnScrollListener(mScrollListener);
    }

    private void giphySearch(String text){
        if(text == null || text.length() == 0){
            // Show trending gifs
            Giphy.getInstance().fetch(Giphy.Endpoint.TRENDING, null, true);
            mGifRecyclerAdapter.updateList(null);
        } else {
            // Search
            Giphy.getInstance().fetch(Giphy.Endpoint.SEARCH, text, true);
            mGifRecyclerAdapter.updateList(null);
        }
    }


    @Override
    public void onGiphyDataReady(List<GiphyData> data) {
        mGifRecyclerAdapter.updateList(data);
        Log.d("MainActivity", "Updating List : " + data.size());
    }
}
