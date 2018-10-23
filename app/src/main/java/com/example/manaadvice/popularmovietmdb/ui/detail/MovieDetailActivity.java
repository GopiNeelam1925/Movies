package com.example.manaadvice.popularmovietmdb.ui.detail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.manaadvice.popularmovietmdb.R;
import com.example.manaadvice.popularmovietmdb.model.Movie;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

import static com.example.manaadvice.popularmovietmdb.utils.Constants.ARG_MOVIE;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.BACKDROP_IMAGE_SIZE;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.POSTER_IMAGE_BASE_URL;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String IS_FAVORITE_MOVIE = "is_favorite_movie";
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel viewModel;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.backdrop_image)
    ImageView movieBackdropImage;
    @BindView(R.id.fab)
    FloatingActionButton favoriteFab;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    private Movie movie;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);
        movie = getIntent().getParcelableExtra(ARG_MOVIE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_grid_container, MovieDetailFragment.newInstance(movie))
                    .commit();
        }
        initToolbar();
        ViewCompat.setElevation(nestedScrollView,
                convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
        ViewCompat.setElevation(favoriteFab,
                convertDpToPixel(getResources().getInteger(R.integer.movie_detail_fab_elevation_in_dp)));

        viewModel = ViewModelProviders.of(MovieDetailActivity.this, viewModelFactory).get(MovieDetailViewModel.class);

        viewModel.isMovieFav().observe(this, favorite -> {
            isFavorite = favorite;
            updateIsFavoriteFab();
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
        collapsingToolbarLayout.setTitle(movie.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        Glide.with(this)
                .load(POSTER_IMAGE_BASE_URL + BACKDROP_IMAGE_SIZE + movie.getBackdropPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(movieBackdropImage);
    }

    @OnClick(R.id.fab)
    void onFavoriteFabClicked() {
        if(isFavorite) {
            viewModel.deleteFromFavorite(movie);
            showSnackbar(R.string.message_removed_from_favorites);
        } else {
            viewModel.addToFavorite(movie);
            showSnackbar(R.string.message_added_to_favorites);
        }
        isFavorite = !isFavorite;
        updateIsFavoriteFab();
    }

    private void updateIsFavoriteFab(){
        if(isFavorite)
            favoriteFab.setImageResource(R.drawable.round_favorite_white_24);
        else
            favoriteFab.setImageResource(R.drawable.round_favorite_border_white_24);
    }

    private void showSnackbar(@StringRes int messageResourceId) {
        Snackbar.make(coordinatorLayout, getString(messageResourceId), Snackbar.LENGTH_LONG).show();
    }

    public float convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
