package com.example.android.popularmovies2.utilities;

import com.example.android.popularmovies2.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 31/05/2018.
 */

public class OpenReviewJsonUtils {

    public static List<Review> getReviewsFromJson(String string) throws JSONException {

        List<Review> reviews = new ArrayList<Review>();
        JSONObject reviewList = new JSONObject(string);

        if (reviewList.has("results") && reviewList.getJSONArray("results").length() != 0) {
            JSONArray resultsArray = reviewList.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject review = resultsArray.getJSONObject(i);

                String user = "anonymous";
                if (review.has("author")) {
                    if (!review.getString("author").equals("")) {
                        user = review.getString("author");
                    }
                }

                String content = "N/A";
                if (review.has("content")) {
                    if (!review.getString("content").equals("")) {
                        content = review.getString("content");
                    }
                }
                reviews.add(new Review(user, content));
            }
        }

        return reviews;
    }
}
