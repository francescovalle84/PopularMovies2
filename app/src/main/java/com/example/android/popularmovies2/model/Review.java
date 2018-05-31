package com.example.android.popularmovies2.model;

/**
 * Created by franc on 31/05/2018.
 */

public class Review {

    private String mUser;
    private String mContent;

    public Review(String user, String content) {
        mUser = user;
        mContent = content;
    }

    public String getUser() {
        return mUser;
    }

    public String getContent() {
        return mContent;
    }
}
