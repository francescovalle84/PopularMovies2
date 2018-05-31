package com.example.android.popularmovies2.utilities;

import com.example.android.popularmovies2.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 30/05/2018.
 */

public class OpenTrailerJsonUtils {

    public static List<Trailer> getTrailersFromJson(String string) throws JSONException {

        List<Trailer> trailers = new ArrayList<Trailer>();
        JSONObject trailerList = new JSONObject(string);

        if(trailerList.has("results")) {
            JSONArray resultsArray = trailerList.getJSONArray("results");

            if (resultsArray.length() != 0) {
                for(int i = 0; i < resultsArray.length(); i++){
                    JSONObject trailer = resultsArray.getJSONObject(i);


                    String trailerPath = "N/A";
                    if(trailer.has("key")){
                        if(!trailer.getString("key").equals("")){
                            trailerPath = trailer.getString("key");
                        }
                    }

                    String trailerName = "Trailer";
                    if(trailer.has("name")){
                        if(!trailer.getString("name").equals("")){
                            trailerName = trailer.getString("name");
                        }
                    }

                    trailers.add(new Trailer(trailerName, trailerPath));

                }
            }
        }

        return trailers;
    }
}
