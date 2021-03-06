package com.example.android.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies2.adapter.ReviewAdapter;
import com.example.android.popularmovies2.adapter.TrailerAdapter;
import com.example.android.popularmovies2.data.MoviesContract;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.model.Review;
import com.example.android.popularmovies2.model.Trailer;
import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.example.android.popularmovies2.utilities.OpenReviewJsonUtils;
import com.example.android.popularmovies2.utilities.OpenTrailerJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.ItemClickListener {

    private final String MOVIE_EXTRA = "movie";

    // Keep track if movie is in favorite list or not
    private int movieId;

    private Button favoriteButton;
    private RecyclerView mTrailerRecyclerView;
    private RecyclerView mReviewRecyclerView;

    private TrailerAdapter trailerAdapter;
    private TextView mTrailerEmptyTextView;
    private ReviewAdapter reviewAdapter;
    private TextView mReviewEmptyTextView;
    Movie selectedMovie = null;

    long movieDbId;
    private static final int DETAILS_LOADER_ID = 10;

    SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        setTitle(R.string.details_title);
        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MOVIE_EXTRA)) {
            Movie movie = intent.getParcelableExtra(MOVIE_EXTRA);
            Log.d("MOVIE: ", movie.getTitle());

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
    private void populateUI(Movie movie, int id) {

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
            favoriteButton.setText(R.string.favorite_remove);

        } else {
            favoriteButton.setText(R.string.favorite_add);
        }

        mTrailerRecyclerView = findViewById(R.id.rv_trailers);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTrailerEmptyTextView = findViewById(R.id.trailer_empty_tv);

        mReviewRecyclerView = findViewById(R.id.rv_reviews);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mReviewEmptyTextView = findViewById(R.id.review_empty_tv);

        // Load trailers and reviews with AsyncTasks
        URL trailersUrl = NetworkUtils.buildTrailersURL(movie.getId());
        URL reviewsUrl = NetworkUtils.buildReviewsURL(movie.getId());

        new TrailersAsyncTask().execute(trailersUrl);
        new ReviewsAsyncTask().execute(reviewsUrl);
    }

    public class ReviewsAsyncTask extends AsyncTask<URL, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(URL... urls) {

            URL url = urls[0];

            List<Review> reviews;

            try {
                String jsonReviewResponse = NetworkUtils.getResponseFromHttpUrl(url);
                return OpenReviewJsonUtils.getReviewsFromJson(jsonReviewResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if(reviews != null && !reviews.isEmpty()){
                reviewAdapter = new ReviewAdapter(getApplicationContext(), reviews);
                mReviewRecyclerView.setAdapter(reviewAdapter);
                mReviewEmptyTextView.setVisibility(View.GONE);

            } else {
                mReviewEmptyTextView.setText(R.string.no_review_found);
            }
        }
    }

    public class TrailersAsyncTask extends AsyncTask<URL, Void, List<Trailer>> {

        @Override
        protected List<Trailer> doInBackground(URL... urls) {

            URL url = urls[0];

            List<Trailer> trailers;

            try {
                String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(url);
                return OpenTrailerJsonUtils.getTrailersFromJson(jsonTrailerResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if(trailers != null & !trailers.isEmpty()){
                trailerAdapter = new TrailerAdapter(getApplicationContext(), trailers);
                mTrailerRecyclerView.setAdapter(trailerAdapter);
                mTrailerEmptyTextView.setVisibility(View.GONE);
                trailerAdapter.setClickListener(MovieDetailActivity.this);

            } else {
                mTrailerEmptyTextView.setText(R.string.no_trailer_found);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Trailer selectedTrailer = trailerAdapter.getItem(position);

        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(selectedTrailer.getTrailerPath()));
        startActivity(youtubeIntent);
    }

    /**
     * Is called when the "Mark as favorite" button is pressed
     */
    public void onClickMarkAsFavorite(View view) {

        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();

        // Put movie information into the ContentValues
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MOVIE_EXTRA)) {
            Movie movie = intent.getParcelableExtra(MOVIE_EXTRA);
            Log.d("FILM: ", movie.getTitle());
            // If movie is already in favorities
            if(movieId != -1) {
                removeMovie(movieId);
                movieId = -1;
                favoriteButton.setText(R.string.favorite_add);
            } else {
                movieId = addNewMovie(movie);
                favoriteButton.setText(R.string.favorite_remove);
            }
        }
        else {
            closeOnError();
        }
    }

    private int searchInFavorities(Movie movie) {

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
    private int addNewMovie(Movie movie){
        ContentValues cv = new ContentValues();

        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
        cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());

        Uri uri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, cv);
        String id = uri.getPathSegments().get(1);

        return Integer.parseInt(id);
    }

    // Remove movie from favorities
    private void removeMovie(long id){
        String stringId = Long.toString(id);
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        getContentResolver().delete(uri, null, null);
    }

}