package com.example.android.popularmovies2;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies2.adapter.ItemClickListener;
import com.example.android.popularmovies2.adapter.MovieAdapter;
import com.example.android.popularmovies2.model.Movie;
import com.example.android.popularmovies2.services.MovieAsyncTaskLoader;
import com.example.android.popularmovies2.utilities.SortType;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    // Loader constant id
    private static final int MOVIE_LOADER = 22;

    private BottomNavigationView bottomNavigationView;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    // A constant to save and restore the sortType
    private String SORT_TYPE_EXTRA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our BottomNavigationView from the xml
         */
        bottomNavigationView = findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nb_most_popular:
                        loadMovieData(SortType.POPULAR);
                        break;
                    case R.id.nb_top_rated:
                        loadMovieData(SortType.TOP_RATED);
                        break;
                    case R.id.nb_favorite:
                        Toast.makeText(MainActivity.this, "My favorite clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });

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
        //loadMovieData(NetworkUtils.SortType.POPULAR);

        Bundle movieBundle = new Bundle();
        movieBundle.putString(SORT_TYPE_EXTRA, SortType.POPULAR);
        getSupportLoaderManager().initLoader(MOVIE_LOADER, movieBundle, this);

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

    /**
     * Calculate dynamically the number of posters that is possible to show in a row
     * <p>
     * Source: https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
     *
     * @param context         the context
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
    private void loadMovieData(String sortType) {
        showMovieDataView();

        Bundle movieBundle = new Bundle();
        movieBundle.putString(SORT_TYPE_EXTRA, sortType);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LOADER);

        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_LOADER, movieBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER, movieBundle, this);
        }
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
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
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, final Bundle bundle) {

        return new MovieAsyncTaskLoader(this, bundle, mLoadingIndicator, SORT_TYPE_EXTRA);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMovieDataView();
            mMovieAdapter.setMovieData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    public Context getContext() {
        return this;
    }
}