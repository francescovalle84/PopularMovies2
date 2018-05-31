package com.example.android.popularmovies2.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies2.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */

public class NetworkUtils {

    final static String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";

    private static final String PATH_VIDEOS = "videos";
    private static final String PATH_REVIEWS = "reviews";
    private static final String PARAM_API_KEY = "api_key";

    final static String THEMOVIEDB_API_VALUE = BuildConfig.API_KEY;

    /**
     * Builds the URL used to query theMovieDb.
     *
     * @param sortType The sort to be used in the query.
     * @return The URL to use to query the movie db.
     */
    public static URL buildUrl(String sortType) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(sortType)
                .appendQueryParameter("api_key", THEMOVIEDB_API_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL buildTrailersURL(int movieId){
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(PATH_VIDEOS)
                .appendQueryParameter(PARAM_API_KEY, THEMOVIEDB_API_VALUE)
                .build();

        return uriToUrl(builtUri);
    }

    public static URL buildReviewsURL(int movieId){
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(PARAM_API_KEY, THEMOVIEDB_API_VALUE)
                .build();

        return uriToUrl(builtUri);
    }

    private static URL uriToUrl(Uri builtUri){
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            Log.e(NetworkUtils.class.getName(), "Malformed URL: " + url);
        }

        return url;
    }
}
