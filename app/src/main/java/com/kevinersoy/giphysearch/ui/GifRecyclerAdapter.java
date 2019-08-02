package com.kevinersoy.giphysearch.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.google.gson.Gson;
import com.kevinersoy.giphysearch.R;
import com.kevinersoy.giphysearch.model.giphy.GiphyData;

import java.util.Collections;
import java.util.List;

public class GifRecyclerAdapter extends RecyclerView.Adapter<GifRecyclerAdapter.ViewHolder>
        implements ListPreloader.PreloadModelProvider<GiphyData> {

    List<GiphyData> mList;
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private RequestBuilder<Drawable> mRequestBuilder;
    private ViewPreloadSizeProvider<GiphyData> mPreloadSizeProvider;
    public Gson mGson = new Gson();


    public GifRecyclerAdapter(Context context, List<GiphyData> list,
                              ViewPreloadSizeProvider<GiphyData> preloadSizeProvider,
                              RequestBuilder<Drawable> requestBuilder) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
        mPreloadSizeProvider = preloadSizeProvider;
        mRequestBuilder = requestBuilder;
    }

    public void updateList(List<GiphyData> list){
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.item_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.mGiphyData = mList.get(i);
        String imageUrl = mList.get(i).getContentUrl();
        mRequestBuilder.load(mList.get(i)).into(viewHolder.mImageView);
        mPreloadSizeProvider.setView(viewHolder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @NonNull
    @Override
    public List<GiphyData> getPreloadItems(int position) {
        return Collections.singletonList(mList.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder<Drawable> getPreloadRequestBuilder(@NonNull GiphyData item) {
        return mRequestBuilder.load(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        GiphyData mGiphyData;
        final ImageView mImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_gif);
            // TODO Set up click listener here
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TEST", "image clicked");
                    Intent intent = new Intent(mContext, FullscreenActivity.class);
                    intent.putExtra(FullscreenActivity.EXTRA_GIPHYDATA_JSON, mGson.toJson(mGiphyData));
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
