package com.kevinersoy.giphysearch.data;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.kevinersoy.giphysearch.model.giphy.GiphyData;
import com.kevinersoy.giphysearch.model.search.SearchFeed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Giphy {
    public enum Endpoint{
        TRENDING("https://api.giphy.com/v1/gifs/trending"),
        SEARCH("https://api.giphy.com/v1/gifs/search");

        public final String value;

        Endpoint(String value){
            this.value = value;
        }
    }


    private static final String TAG = "Giphy.class";
    private static final String API_KEY = "4LqqPWexE0BFM99ZdLim2SbwusOtt7vb"; // Debug key

    private Gson mGson = new Gson();
    private OkHttpClient mOkHttpClient = new OkHttpClient();

    // Singleton Instance
    private static Giphy mInstance;

    // Listeners
    private Set<GiphyCallbacks> mListeners = new HashSet<>();

    // Pagination
    private int mNextOffset = 0;
    private int mMaxCount = Integer.MAX_VALUE;
    private boolean mHasMore = true;

    // State
    private Endpoint mLastEndpoint;
    private String mLastSearhText;
    private List<GiphyData> data;

    private Giphy(){
        if(mInstance != null){
            throw new RuntimeException("Use getInstance() to get the single instance of this class");
        }
        data = new ArrayList<GiphyData>();
    }

    public static Giphy getInstance(){
        if(mInstance == null){
            synchronized (Giphy.class){
                if(mInstance == null){
                    Log.d(TAG, "Creating Giphy instance");
                    mInstance = new Giphy();
                }
            }
        }
        return mInstance;
    }

    public List<GiphyData> getData() {
        return data;
    }

    public void fetch(){
        Log.d(TAG, "Fetching more " + mLastEndpoint.value);
        if(mLastEndpoint != null && mHasMore){
            fetch(mLastEndpoint, mLastSearhText, false);
        }
    }

    public void fetch(Endpoint endpoint, String searchText, boolean clearPagination){
        if(clearPagination){
            mNextOffset = 0;
            mMaxCount = Integer.MAX_VALUE;
            mHasMore = true;
            data.clear();
        }

        mLastEndpoint = endpoint;
        mLastSearhText = searchText;

        Uri.Builder targetBuilder = Uri.parse(endpoint.value)
                .buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("offset", "" + mNextOffset);
        if(endpoint.equals(Endpoint.SEARCH))
            targetBuilder.appendQueryParameter("q", searchText);

        Uri target = targetBuilder.build();

        Request request = new Request.Builder()
                    .url(target.toString())
                    .build();

        Log.d(TAG, "Fetching : " + target.toString());
        Observable.fromCallable(() -> mOkHttpClient.newCall(request).execute())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::giphyResponseHandler);
    }

    private void giphyResponseHandler(Response response){
        if(response.isSuccessful()) {
            String json = "";
            try {
                json = response.body().string();
                Log.d(TAG, "Response : " + json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SearchFeed feed = mGson.fromJson(json, SearchFeed.class);
            if(feed == null) { return; }

            // Set Max Count if present in response
            mMaxCount = Math.max(feed.getPagination().getTotalCount(), mMaxCount);

            // Set offset for next search
            mNextOffset = feed.getPagination().getOffset() + feed.getPagination().getCount() + 1;

            if(mNextOffset >= mMaxCount){
                mHasMore = false;
            }

            data.addAll(feed.getDataList());

            for(GiphyCallbacks listener : mListeners){
                try{
                    listener.onGiphyDataReady(data);
                } catch(Exception e){
                    // Listener may have died
                    mListeners.remove(listener);
                }
            }

        }
    }

    public void registerListener(GiphyCallbacks listener){
        mListeners.add(listener);
    }

    public void unregisterListener(GiphyCallbacks listener){
        mListeners.remove(listener);
    }

    public interface GiphyCallbacks{
        void onGiphyDataReady(List<GiphyData> data);
    }
}
