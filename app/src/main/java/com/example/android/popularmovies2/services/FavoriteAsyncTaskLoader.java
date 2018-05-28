package com.example.android.popularmovies2.services;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies2.MainActivity;
import com.example.android.popularmovies2.data.MoviesContract;
import com.example.android.popularmovies2.model.Movie;

import java.util.ArrayList;

/**
 * Created by franc on 27/05/2018.
 */

public class FavoriteAsyncTaskLoader extends AsyncTaskLoader<Cursor> {

    private Cursor mMovies = null;

    // Parameters
    private Context context;
    private Bundle bundle;
    private ProgressBar mLoadingIndicator;

    public FavoriteAsyncTaskLoader(Context context, Bundle bundle, ProgressBar loadingIndicator) {
        super(context);
        this.context = context;
        this.bundle = bundle;
        this.mLoadingIndicator = loadingIndicator;
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
    public Cursor loadInBackground() {
        try{
            Cursor cursor =  context.getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            int numResults = cursor.getCount();

            return cursor;
        } catch (Exception e){
            Log.e(MainActivity.class.getName(), "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(Cursor movies) {
        mMovies = movies;
        super.deliverResult(movies);
    }
}
