package com.example.manaadvice.popularmovietmdb.ui.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.manaadvice.popularmovietmdb.R;
import com.example.manaadvice.popularmovietmdb.model.MovieReview;
import com.example.manaadvice.popularmovietmdb.utils.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewViewHolder> {

    @Nullable
    private List<MovieReview> movieReviews;
    @Nullable
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setData(List<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_review, parent, false);
        return new MovieReviewViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewViewHolder holder, int position) {
        if (movieReviews == null) {
            return;
        }
        MovieReview review = movieReviews.get(position);
        holder.content.setText(review.getContent());
        holder.author.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return movieReviews == null ? 0 : movieReviews.size();
    }

    public MovieReview getItem(int position) {
        if (movieReviews == null || position < 0 || position > movieReviews.size())
            return null;
        return movieReviews.get(position);
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_movie_review_content)
        TextView content;
        @BindView(R.id.text_movie_review_author)
        TextView author;

        @Nullable
        private OnItemClickListener onItemClickListener;

        public MovieReviewViewHolder(View itemView, @Nullable OnItemClickListener onItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

}
