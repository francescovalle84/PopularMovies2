package com.example.android.popularmovies2.services;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.View;

import com.example.android.popularmovies2.MainActivity;
import com.example.android.popularmovies2.data.MoviesContract;
import com.example.android.popularmovies2.model.Movie;

import java.util.ArrayList;

/**
 * Created by franc on 23/05/2018.
 */

public class FavoriteLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private MainActivity mainActivity;

    public FavoriteLoader(MainActivity srcActivity) {
        mainActivity = srcActivity;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new FavoriteAsyncTaskLoader(mainActivity, bundle, mainActivity.getLoadingIndicator());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mainActivity.getLoadingIndicator().setVisibility(View.INVISIBLE);
        if (data != null) {
            mainActivity.showMovieDataView();
            mainActivity.getMovieAdapter().setMovieData(cursorToArrayList((data)));
        } else {
            mainActivity.showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private ArrayList<Movie> cursorToArrayList(Cursor cursor) {
        ArrayList<Movie> moviesList = new ArrayList<Movie>();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();

            do{
                int movieId = cursor.getInt(cursor.getColumnIndex(
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID));
                String movieTitle = cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE));
                String moviePosterPath = cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH));
                String movieOverview = cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW));
                String movieVoteAverage = cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE));
                String movieReleaseDate = cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));

                Movie movie = new Movie();
                movie.setId(movieId);
                movie.setTitle(movieTitle);
                movie.setPosterPath(moviePosterPath);
                movie.setOverview(movieOverview);
                movie.setVoteAverage(Float.valueOf(movieVoteAverage));
                movie.setReleaseDate(movieReleaseDate);

                moviesList.add(movie);

            } while(cursor.moveToNext());

        }

        return moviesList;
    }
}
