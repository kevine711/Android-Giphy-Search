package com.kevinersoy.giphysearch.data;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.kevinersoy.giphysearch.model.giphy.GiphyData;
import com.kevinersoy.giphysearch.model.giphy.GiphyImage;

import java.io.InputStream;

public class GiphyModelLoader extends BaseGlideUrlLoader<GiphyData> {

    public GiphyModelLoader(ModelLoader<GlideUrl, InputStream> urlLoader) {
        super(urlLoader);
    }

    @Override
    public boolean handles(@NonNull GiphyData giphyData) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<GiphyData, InputStream>{

        @NonNull
        @Override
        public ModelLoader<GiphyData, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new GiphyModelLoader(multiFactory.build(GlideUrl.class, InputStream.class));
        }

        @Override
        public void teardown() {
            // Do nothing
        }
    }

    @Override
    protected String getUrl(GiphyData giphyData, int width, int height, Options options) {
        GiphyImage fixedHeight = giphyData.getImages().getFixedHeight();
        int fixedHeightDifference = getDifference(fixedHeight, width, height);
        GiphyImage fixedWidth = giphyData.getImages().getFixedWidth();
        int fixedWidthDifference = getDifference(fixedWidth, width, height);
        if(fixedHeightDifference < fixedWidthDifference && !TextUtils.isEmpty(fixedHeight.getUrl())){
            return fixedHeight.getUrl();
        } else if(!TextUtils.isEmpty(fixedWidth.getUrl())){
            return fixedWidth.getUrl();
        } else if(!TextUtils.isEmpty(giphyData.getImages().getOriginal().getUrl())){
            return giphyData.getImages().getOriginal().getUrl();
        } else{
            return null;
        }
    }

    private static int getDifference(GiphyImage gifImage, int width, int height) {
        return Math.abs(width - Integer.parseInt(gifImage.getWidth()))
                + Math.abs(height - Integer.parseInt(gifImage.getHeight()));
    }

}
