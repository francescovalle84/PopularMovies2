package com.example.android.popularmovies2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Exposes a list of movies to the RecyclerView
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMovies;
    private ItemClickListener mClickListener;

    // Store context to be used in onBindViewHolder
    private Context mContext;

    public MovieAdapter() {

    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutIdForGridItem = R.layout.movie_grid_view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details, using the "position" argument that is conveniently passed into us.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String moviePosterPath = mMovies.get(position).getPosterPath();
        Picasso.with(mContext)
                .load(moviePosterPath)
                .into(holder.mMoviePosterImageView);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our app
     */
    @Override
    public int getItemCount() {
        if(null == mMovies) return 0;
        return mMovies.size();
    }

    public void setMovieData(ArrayList<Movie> movieData) {
        mMovies = movieData;
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovieData() {
        return mMovies;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public Movie getItem(int id) {
        return mMovies.get(id);
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Declare the data item
        private ImageView mMoviePosterImageView;

        private MovieAdapterViewHolder(View view) {
            super(view);
            mMoviePosterImageView = view.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
