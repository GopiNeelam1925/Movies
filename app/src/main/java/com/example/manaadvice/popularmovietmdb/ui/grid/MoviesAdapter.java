package com.example.manaadvice.popularmovietmdb.ui.grid;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.manaadvice.popularmovietmdb.R;
import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.utils.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieGridItemViewHolder> {

    private static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_IMAGE_SIZE = "w780";

    private List<Movie> movies;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public MoviesAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public MovieGridItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_movie, parent, false);
        return new MovieGridItemViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieGridItemViewHolder movieViewHolder, int i) {
        movieViewHolder.moviePoster.setContentDescription(movies.get(i).getTitle());
        Glide.with(context)
                .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movies.get(i).getPosterPath())
                .placeholder(new ColorDrawable(context.getResources().getColor(R.color.accent_material_light)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .crossFade()
                .into(movieViewHolder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    @Nullable
    public Movie getItem(int position) {
        return movies.get(position);
    }

    public class MovieGridItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_movie_poster)
        ImageView moviePoster;

        private OnItemClickListener onItemClickListener;

        public MovieGridItemViewHolder(View itemView, @Nullable OnItemClickListener onItemClickListener) {
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
