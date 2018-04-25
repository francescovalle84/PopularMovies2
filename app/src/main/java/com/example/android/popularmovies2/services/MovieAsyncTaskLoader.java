package com.example.android.popularmovies2.services;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies2.MainActivity;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.example.android.popularmovies2.utilities.OpenMovieJsonUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by franc on 07/04/2018.
 *
 * NOT USED RIGHT NOW
 */

public class MovieAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    ArrayList<Movie> mMovies = null;

    // Parameters
    Context context;
    Bundle bundle;
    ProgressBar mLoadingIndicator;
    String sortTypeExtra;

    public MovieAsyncTaskLoader(Context context, Bundle bundle, ProgressBar loadingIndicator, String sortTypeExtra) {
        super(context);
        this.context = context;
        this.bundle = bundle;
        this.mLoadingIndicator = loadingIndicator;
        this.sortTypeExtra = sortTypeExtra;
    }

    @Override
    protected void onStartLoading() {
        //super.onStartLoading();
        if(bundle == null) {
            return;
        }
        mLoadingIndicator.setVisibility(View.VISIBLE);

        if(mMovies != null) {
            deliverResult(mMovies);
        } else {
            forceLoad();
        }
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        String sortType = bundle.getString(sortTypeExtra);
        if(sortType == null || TextUtils.isEmpty(sortType)) {
            return null;
        }

        URL movieRequestUrl = NetworkUtils.buildUrl(sortType);

        try {
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            return OpenMovieJsonUtils.getMoviesFromJson(context, jsonMovieResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;    // Why returns null without printing the stack trace?
        }
    }

    @Override
    public void deliverResult(ArrayList<Movie> movies) {
        mMovies = movies;
        super.deliverResult(movies);
    }
}
