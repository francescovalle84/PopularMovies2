package com.example.android.popularmovies2;

import android.database.Cursor;
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
import com.example.android.popularmovies2.services.FavoriteLoader;
import com.example.android.popularmovies2.services.MovieDBLoader;
import com.example.android.popularmovies2.utilities.SortType;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    // Loader constant id
    private static final int MOVIE_DB_LOADER_ID = 22;
    private static final int FAVORITE_LOADER_ID = 23;

    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> movieDBLoader;
    private LoaderManager.LoaderCallbacks<Cursor> favoriteLoader;

    private BottomNavigationView bottomNavigationView;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    // A constant to save and restore the sortType
    private String SORT_TYPE_EXTRA;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Stetho debugger
        Stetho.initializeWithDefaults(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure loaders
        movieDBLoader = new MovieDBLoader(this);
        favoriteLoader = new FavoriteLoader(this);

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
                        //Toast.makeText(MainActivity.this, "My favorite clicked", Toast.LENGTH_SHORT).show();
                        //loadMovieData(SortType.FAVORITE);
                        loadFavoriteData(SortType.FAVORITE);
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
        loadMovieData(SortType.POPULAR);
        //loadFavoriteData(SortType.FAVORITE);

        /*
        Bundle movieBundle = new Bundle();
        movieBundle.putString(SORT_TYPE_EXTRA, SortType.POPULAR);
        getSupportLoaderManager().initLoader(MOVIE_DB_LOADER_ID, movieBundle, movieDBLoader);
        */
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
        Loader<ArrayList<Movie>> movieLoader = loaderManager.getLoader(MOVIE_DB_LOADER_ID);

        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_DB_LOADER_ID, movieBundle, movieDBLoader);
        } else {
            loaderManager.restartLoader(MOVIE_DB_LOADER_ID, movieBundle, movieDBLoader);
        }
    }

    private void loadFavoriteData(String sortType) {
        showMovieDataView();

        Bundle movieBundle = new Bundle();
        movieBundle.putString(SORT_TYPE_EXTRA, sortType);

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(FAVORITE_LOADER_ID, movieBundle, favoriteLoader);

    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    public void showMovieDataView() {
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
    public void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public Context getContext() {
        return this;
    }

    public MovieAdapter getMovieAdapter() {
        return mMovieAdapter;
    }

    public ProgressBar getLoadingIndicator() {
        return mLoadingIndicator;
    }

    public String getSortTypeExtra() {
        return SORT_TYPE_EXTRA;
    }
}