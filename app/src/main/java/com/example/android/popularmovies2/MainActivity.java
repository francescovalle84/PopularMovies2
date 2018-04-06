package com.example.android.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies2.adapter.ItemClickListener;
import com.example.android.popularmovies2.adapter.MovieAdapter;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.utilities.NetworkUtils;
import com.example.android.popularmovies2.utilities.OpenMovieJsonUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = findViewById(R.id.recyclerview_id);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById(R.id.tv_error_message);

        // Calculate the number of columns based on context resolution and width of grid item
        int numberOfColumns = calculateNumberOfColumns(this, 180);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our movie data with the Views that
         * will end up displaying our movie data.
         */
        mMovieAdapter = new MovieAdapter();
        mMovieAdapter.setClickListener(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the movie data. */
        loadMovieData(NetworkUtils.SortType.POPULAR);
    }

    @Override
    public void onItemClick(View view, int position) {

        Log.i("TAG", "You clicked number " + mMovieAdapter.getItem(position).getTitle() + ", which is at position " + position);
        launchMovieDetailActivity(position);
    }

    private void launchMovieDetailActivity(int position) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", mMovieAdapter.getItem(position));
        startActivity(intent);
    }

    // Create a menu to manage two type of sorting
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // If one of the menu items is clicked, load again data with the selected type of sorting
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NetworkUtils.SortType sortType;

        switch (item.getItemId()) {
            case R.id.sort_popular:
                sortType = NetworkUtils.SortType.POPULAR;
                loadMovieData(sortType);
                return true;
            case R.id.sort_top_rated:
                sortType = NetworkUtils.SortType.TOP_RATED;
                loadMovieData(sortType);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Calculate dynamically the number of posters that is possible to show in a row
     *
     * Source: https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
     *
     * @param context the context
     * @param widthOfGridItem the width of the single item
     * @return the number of columns that it is possible to show in a row
     */
    private int calculateNumberOfColumns(Context context, int widthOfGridItem) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / widthOfGridItem);
    }

    /**
     * Load movie data (in background) with the selected sorting type
     */
    private void loadMovieData(NetworkUtils.SortType sortType) {
        showMovieDataView();
        new FetchMovieTask().execute(sortType.getSortType());
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     *
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     *
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            /* If there's no sorting type specified, do nothing. */
            if (params.length == 0) {
                return null;
            }

            String sortType = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortType);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                return OpenMovieJsonUtils.getMoviesFromJson(MainActivity.this, jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }
}