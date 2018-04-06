package com.example.android.popularmovies2.utilities;

import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies2.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 14/03/2018.
 */

public class OpenMovieJsonUtils {

    private static final String LOG_TAG = OpenMovieJsonUtils.class.getSimpleName();

    public static ArrayList<Movie> getMoviesFromJson(Context context, String movieJsonStr) throws JSONException {

        Log.d(LOG_TAG, "Json string: " + movieJsonStr);

        ArrayList<Movie> movies = new ArrayList<>();

        JSONObject movieList = new JSONObject(movieJsonStr);
        JSONArray resultsArray = movieList.getJSONArray("results");

        for(int i = 0; i < resultsArray.length(); i++) {

            // Create a new movie object a populate it
            Movie movie = new Movie();
            JSONObject movieInfo = resultsArray.getJSONObject(i);

            movie.setVoteCount(movieInfo.getInt("vote_count"));
            movie.setId(movieInfo.getInt("id"));
            movie.setVideo(movieInfo.getBoolean("video"));
            movie.setVoteAverage((float) movieInfo.getDouble("vote_average"));
            movie.setTitle(movieInfo.getString("title"));
            movie.setPopularity((float) movieInfo.getDouble("popularity"));
            movie.setPosterPath(movieInfo.getString("poster_path"));
            movie.setOriginalLanguage(movieInfo.getString("original_language"));
            movie.setOriginalTitle(movieInfo.getString("original_title"));
            List<Integer> genreIds = new ArrayList<>();
            JSONArray genreIdsArray = movieInfo.getJSONArray("genre_ids");
            for(int j = 0; j < genreIdsArray.length(); j++) {
                genreIds.add((Integer) genreIdsArray.get(j));
            }
            movie.setGenreIds(genreIds);
            movie.setBackdropPath(movieInfo.getString("backdrop_path"));
            movie.setAdult(movieInfo.getBoolean("adult"));
            movie.setOverview(movieInfo.getString("overview"));
            movie.setReleaseDate(movieInfo.getString("release_date"));

            movies.add(movie);

            Log.d(LOG_TAG, "Film added: " + movie.getTitle() + " (ID: " + movie.getId() + ")");
        }

        return movies;
    }
}
