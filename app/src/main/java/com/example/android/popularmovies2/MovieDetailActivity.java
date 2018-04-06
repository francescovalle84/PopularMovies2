package com.example.android.popularmovies2;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies2.model.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        setTitle("Movie details");
        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");
            Log.d("FILM: ", movie.getTitle());
            populateUI(movie);
        }
        else {
            closeOnError();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_loading_detail, Toast.LENGTH_SHORT).show();
    }

    /**
     * Populate the UI of the detail activity
     * @param movie the movie
     */
    private void populateUI(Movie movie) {

        TextView titleView = findViewById(R.id.tv_movie_title);
        titleView.setText(movie.getOriginalTitle());

        ImageView posterView = findViewById(R.id.iv_movie_poster_detail);
        Picasso
                .with(this)
                .load(movie.getPosterPath())
                .into(posterView);

        TextView releaseDateView = findViewById(R.id.tv_release_date);
        releaseDateView.setText(movie.getReleaseDate().substring(0, 4));

        TextView voteAverageView = findViewById(R.id.tv_vote_average);
        String voteAverageStr = String.valueOf(movie.getVoteAverage()) + "/10.0";
        voteAverageView.setText(voteAverageStr);

        TextView overviewView = findViewById(R.id.tv_overview);
        overviewView.setText(movie.getOverview());
    }
}