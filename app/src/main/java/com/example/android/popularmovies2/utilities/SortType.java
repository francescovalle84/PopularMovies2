package com.example.android.popularmovies2.utilities;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * As suggested on review of PopularMovies1 use Android Annotations interface instead of Enums
 *
 * Created by franc on 22/05/2018.
 */

public class SortType {

    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String FAVORITE = "favorite";

    public SortType(@Season int sortType) {
        System.out.println("Sort type:" + sortType);
    }

    @StringDef({POPULAR, TOP_RATED, FAVORITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Season {
    }
}
