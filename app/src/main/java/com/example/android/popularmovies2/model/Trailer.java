package com.example.android.popularmovies2.model;

/**
 * Created by franc on 30/05/2018.
 */

public class Trailer {

    public static final String TRAILER_URL_BASE = "https://www.youtube.com/watch?v=";

    private String mTrailerName;
    private String mTrailerPath;
    private String mTrailerThumbnail;

    public Trailer(String name, String path) {
        mTrailerName = name;
        mTrailerPath = TRAILER_URL_BASE + path;
        mTrailerThumbnail = "http://img.youtube.com/vi/" + path + "/0.jpg";;
    }

    public String getTrailerName() {
        return mTrailerName;
    }

    public String getTrailerPath() {
        return mTrailerPath;
    }

    public String getTrailerThumbnail() {
        return mTrailerThumbnail;
    }
}
