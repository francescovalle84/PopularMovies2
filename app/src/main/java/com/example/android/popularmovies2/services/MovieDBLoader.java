package com.example.android.popularmovies2.services;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.View;

import com.example.android.popularmovies2.MainActivity;
import com.example.android.popularmovies2.model.Movie;

import java.util.ArrayList;

/**
 * Created by franc on 23/05/2018.
 */

public class MovieDBLoader implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private MainActivity mainActivity;

    public MovieDBLoader(MainActivity srcActivity) {
        mainActivity = srcActivity;
    }

    @Override
    public android.support.v4.content.Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle bundle) {
        return new MovieAsyncTaskLoader(mainActivity, bundle, mainActivity.getLoadingIndicator(), mainActivity.getSortTypeExtra());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mainActivity.getLoadingIndicator().setVisibility(View.INVISIBLE);
        if (data != null) {
            mainActivity.showMovieDataView();
            mainActivity.getMovieAdapter().setMovieData(data);
        } else {
            mainActivity.showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Movie>> loader) {

    }
}
