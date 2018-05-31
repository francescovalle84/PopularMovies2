package com.example.android.popularmovies2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.Review;

import java.util.List;

/**
 * Created by franc on 31/05/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> mData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public ReviewAdapter(Context context, List<Review> data){
        mData = data;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.reviews_view, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Review review = mData.get(position);
        holder.mUser.setText(review.getUser());
        holder.mContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mUser;
        TextView mContent;

        public ViewHolder(View itemView) {
            super(itemView);

            mUser = itemView.findViewById(R.id.review_item_user_tv);
            mContent = itemView.findViewById(R.id.review_item_content_tv);
        }
    }
}
