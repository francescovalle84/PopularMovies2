package com.example.android.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies2.data.MoviesContract;
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

    /**
     * Is called when the "Mark as favorite" button is pressed
     */
    public void onClickMarkAsFavorite(View view) {

        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();

        // Put movie information into the ContentValues
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            Movie movie = intent.getParcelableExtra("movie");
            Log.d("FILM: ", movie.getTitle());
            contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
            contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
            contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
            contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());

            // Insert new movie data via a ContentResolver
            Uri uri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);
        }
        else {
            closeOnError();
        }
    }
}