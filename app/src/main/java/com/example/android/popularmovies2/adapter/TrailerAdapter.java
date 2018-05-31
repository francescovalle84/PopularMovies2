package com.example.android.popularmovies2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by franc on 30/05/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<Trailer> mData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ItemClickListener mClickListener;

    public TrailerAdapter(Context context, List<Trailer> data){
        mData = data;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.trailers_view, parent, false);
        return new TrailerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trailer trailer = mData.get(position);
        String thumbnailUrl = trailer.getTrailerThumbnail();

        Picasso.with(mContext)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_clear_black)
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Trailer getItem(int id){
        return mData.get(id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.trailer_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener != null){
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public void setClickListener(TrailerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
}
