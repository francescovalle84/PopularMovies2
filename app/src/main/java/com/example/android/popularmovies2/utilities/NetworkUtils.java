package com.example.android.popularmovies2.utilities;

import android.net.Uri;

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

    /**
     * Used to manage several types of sorting
     * (for the moment only 'popular' and 'top rated')
     */
    public enum SortType {
        POPULAR ("popular"),
        TOP_RATED ("top_rated");

        private String sortType;

        SortType (String sortType) {
            this.sortType = sortType;
        }

        public String getSortType() {
            return this.sortType;
        }
    }

    final static String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";

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
}
