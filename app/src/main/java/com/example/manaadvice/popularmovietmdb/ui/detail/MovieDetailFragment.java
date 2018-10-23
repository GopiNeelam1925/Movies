package com.example.manaadvice.popularmovietmdb.ui.detail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.manaadvice.popularmovietmdb.R;
import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.model.MovieReview;
import com.example.manaadvice.popularmovietmdb.model.MovieVideo;
import com.example.manaadvice.popularmovietmdb.ui.grid.MoviesViewModel;
import com.example.manaadvice.popularmovietmdb.utils.Status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

import static com.example.manaadvice.popularmovietmdb.utils.Constants.ARG_MOVIE;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.POSTER_IMAGE_BASE_URL;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.POSTER_IMAGE_SIZE;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.VOTE_GOOD;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.VOTE_NORMAL;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.VOTE_PERFECT;


public class MovieDetailFragment extends Fragment {
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel viewModel;

    @BindView(R.id.image_movie_detail_poster)
    ImageView movieImagePoster;
    @BindView(R.id.text_movie_original_title)
    TextView movieOriginalTitle;
    @BindView(R.id.text_movie_user_rating)
    TextView movieUserRating;
    @BindView(R.id.text_movie_production)
    TextView movieProduction;
    @BindView(R.id.text_movie_release_date)
    TextView movieReleaseDate;
    @BindView(R.id.text_movie_overview)
    TextView movieOverview;
    @BindView(R.id.card_movie_detail)
    CardView cardMovieDetail;
    @BindView(R.id.card_movie_overview)
    CardView cardMovieOverview;

    @BindView(R.id.card_movie_videos)
    CardView cardMovieVideos;
    @BindView(R.id.movie_videos)
    RecyclerView movieVideos;

    @BindView(R.id.card_movie_reviews)
    CardView cardMovieReviews;
    @BindView(R.id.movie_reviews)
    RecyclerView movieReviews;

    private Movie movie;
    private MovieVideosAdapter videosAdapter;
    private MovieReviewsAdapter reviewsAdapter;

    public MovieDetailFragment() {
        // Required empty public constructor
    }


    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = getArguments().getParcelable(ARG_MOVIE);
        }
        viewModel = ViewModelProviders.of((MovieDetailActivity)getActivity(),viewModelFactory).get(MovieDetailViewModel.class);
        viewModel.setMovieId(movie.getId());
        viewModel.getMovieVideos().observe(this, movieVideos -> {
            if(movieVideos != null && movieVideos.data != null) {
                videosAdapter.setData(movieVideos.data);
                updateMovieVideosCard();
            }
        });
        viewModel.getMovieReviews().observe(this, movieReviews -> {
            if(movieReviews != null && movieReviews.data != null) {
                reviewsAdapter.setData(movieReviews.data);
                updateMovieReviewsCard();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        initViews();
        initVideosList();
        initReviewsList();
        return rootView;
    }

    private void initViews() {
        Glide.with(this)
                .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getPosterPath())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(movieImagePoster);
        movieOriginalTitle.setText(movie.getOriginalTitle());
        movieUserRating.setText(String.format(Locale.US, "%.1f", movie.getAverageVote()));
        movieUserRating.setTextColor(getRatingColor(movie.getAverageVote()));
        //String releaseDate = String.format(getString(R.string.movie_detail_release_date), movie.getReleaseDate());
        movieReleaseDate.setText(changeFormatOfDate(movie.getReleaseDate()));
        movieOverview.setText(movie.getOverview());

        // Set elevation
        ViewCompat.setElevation(cardMovieDetail, convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
        ViewCompat.setElevation(cardMovieVideos, convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
        ViewCompat.setElevation(cardMovieOverview, convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
        ViewCompat.setElevation(cardMovieReviews, convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
    }

    private void initVideosList() {
        videosAdapter = new MovieVideosAdapter(getContext());
        videosAdapter.setOnItemClickListener((itemView, position) -> onMovieVideoClicked(position));
        movieVideos.setAdapter(videosAdapter);
        movieVideos.setItemAnimator(new DefaultItemAnimator());
        //movieVideos.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.movie_item_offset));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        movieVideos.setLayoutManager(layoutManager);
    }

    private void onMovieVideoClicked(int position) {
        MovieVideo video = videosAdapter.getItem(position);
        if (video != null && video.isYoutubeVideo()) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + video.getKey()));
            startActivity(intent);
        }
    }

    private void initReviewsList() {
        reviewsAdapter = new MovieReviewsAdapter();
        reviewsAdapter.setOnItemClickListener((itemView, position) -> onMovieReviewClicked(position));
        movieReviews.setAdapter(reviewsAdapter);
        movieReviews.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        movieReviews.setLayoutManager(layoutManager);
    }

    private void onMovieReviewClicked(int position) {
        MovieReview review = reviewsAdapter.getItem(position);
        if (review != null && review.getReviewUrl() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getReviewUrl()));
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMovieVideosCard();
        updateMovieReviewsCard();
    }

    private void updateMovieVideosCard() {
        if (videosAdapter == null || videosAdapter.getItemCount() == 0) {
            cardMovieVideos.setVisibility(View.GONE);
        } else {
            cardMovieVideos.setVisibility(View.VISIBLE);
        }
    }

    private void updateMovieReviewsCard() {
        if (reviewsAdapter == null || reviewsAdapter.getItemCount() == 0) {
            cardMovieReviews.setVisibility(View.GONE);
        } else {
            cardMovieReviews.setVisibility(View.VISIBLE);
        }
    }

    @ColorInt
    private int getRatingColor(double averageVote) {
        if (averageVote >= VOTE_PERFECT) {
            return ContextCompat.getColor(getContext(), R.color.vote_perfect);
        } else if (averageVote >= VOTE_GOOD) {
            return ContextCompat.getColor(getContext(), R.color.vote_good);
        } else if (averageVote >= VOTE_NORMAL) {
            return ContextCompat.getColor(getContext(), R.color.vote_normal);
        } else {
            return ContextCompat.getColor(getContext(), R.color.vote_bad);
        }
    }

    public float convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private String changeFormatOfDate(String releaseDate){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            Date date = sdf.parse(releaseDate);
            String dateFormat = formattedDate.format(date);
            return dateFormat;
        } catch (Exception e) {
            return releaseDate;
        }
    }
}
