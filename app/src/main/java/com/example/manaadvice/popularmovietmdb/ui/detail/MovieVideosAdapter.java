package com.example.manaadvice.popularmovietmdb.ui.detail;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.manaadvice.popularmovietmdb.R;
import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.model.MovieVideo;
import com.example.manaadvice.popularmovietmdb.ui.grid.MoviesAdapter;
import com.example.manaadvice.popularmovietmdb.utils.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideoViewHolder> {
    private static final String YOUTUBE_THUMBNAIL = "https://img.youtube.com/vi/%s/mqdefault.jpg";
    private final Context context;

    @Nullable
    private List<MovieVideo> movieVideos;
    @Nullable
    private OnItemClickListener onItemClickListener;

    public MovieVideosAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setData(List<MovieVideo> movieVideos) {
        this.movieVideos = movieVideos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_video, parent, false);
        return new MovieVideoViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVideoViewHolder movieVideoViewHolder, int position) {
        if (movieVideos == null) {
            return;
        }
        MovieVideo video = movieVideos.get(position);
        if (video.isYoutubeVideo()) {
            Glide.with(context)
                    .load(String.format(YOUTUBE_THUMBNAIL, video.getKey()))
                    .placeholder(new ColorDrawable(context.getResources().getColor(R.color.accent_material_light)))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop()
                    .crossFade()
                    .into(movieVideoViewHolder.movieVideoThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return movieVideos == null ? 0 : movieVideos.size();
    }

    public MovieVideo getItem(int position) {
        if (movieVideos == null || position < 0 || position > movieVideos.size())
            return null;
        return movieVideos.get(position);
    }

    public class MovieVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.movie_video_thumbnail)
        ImageView movieVideoThumbnail;

        @Nullable
        private OnItemClickListener onItemClickListener;

        public MovieVideoViewHolder(View itemView, @Nullable OnItemClickListener onItemClickListener) {
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
