package com.example.android.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies2.data.MoviesContract;
import com.example.android.popularmovies2.model.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    // Keep track if movie is in favorite list or not
    private long movieId;

    private Button favoriteButton;

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

            // Update movieId
            movieId = searchInFavorities(movie);

            populateUI(movie, movieId);
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
    private void populateUI(Movie movie, long id) {

        TextView titleView = findViewById(R.id.tv_movie_title);
        titleView.setText(movie.getTitle());

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

        favoriteButton = findViewById(R.id.favoriteButton);
        // If movie is already in favorities
        if(id != -1) {
            favoriteButton.setText("Remove");

        } else {
            favoriteButton.setText("Add");
        }
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
            // If movie is already in favorities
            if(movieId != -1) {
                removeMovie(movieId);
                movieId = -1;
                favoriteButton.setText("Add");
            } else {
                movieId = addNewMovie(movie);
                favoriteButton.setText("Remove");
            }
        }
        else {
            closeOnError();
        }
    }

    private long searchInFavorities(Movie movie) {

        String[] whereIs = {Integer.toString(movie.getId())};
        try {
            Cursor cursor = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                    MoviesContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                    whereIs,
                    null);

            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID));

        } catch (Exception e){
            Log.e(MainActivity.class.getName(), "Failed to asynchronously load data.");
            e.printStackTrace();
            return -1;
        }

    }

    // Add movie to favorities
    private long addNewMovie(Movie movie){
        ContentValues cv = new ContentValues();

        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());

        Uri uri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, cv);
        String id = uri.getPathSegments().get(1);

        return Long.parseLong(id);
    }

    // Remove movie from favorities
    private void removeMovie(long id){
        String stringId = Long.toString(id);
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        getContentResolver().delete(uri, null, null);
    }

}