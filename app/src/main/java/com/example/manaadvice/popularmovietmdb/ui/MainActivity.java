package com.example.manaadvice.popularmovietmdb.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.example.manaadvice.popularmovietmdb.R;
import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.ui.detail.MovieDetailActivity;
import com.example.manaadvice.popularmovietmdb.ui.detail.MovieDetailFragment;
import com.example.manaadvice.popularmovietmdb.ui.grid.MoviesGridFragment;
import com.example.manaadvice.popularmovietmdb.utils.SortHelper;
import com.example.manaadvice.popularmovietmdb.utils.SortingDialogFragment;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

import static com.example.manaadvice.popularmovietmdb.utils.Constants.ARG_MOVIE;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.SELECTED_FAVORITE_MOVIES_GRID;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.SELECTED_MOVIES_GRID;

public class MainActivity extends AppCompatActivity implements MoviesGridFragment.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String SELECTED_MOVIE_KEY = "MovieSelected";
    private static final String SELECTED_NAVIGATION_ITEM_KEY = "SelectedNavigationItem";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.movie_detail_container)
    ScrollView movieDetailContainer;
    @Nullable
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Inject
    SortHelper sortHelper;

    private boolean twoPaneMode;
    private Movie selectedMovie = null;
    private int selectedNavigationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_grid_container, MoviesGridFragment.newInstance(SELECTED_MOVIES_GRID)).commit();
        }

        // setup toolbar
        setSupportActionBar(toolbar);

        // setup NavigationDrawer
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.round_menu_white_24);
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(this);

        twoPaneMode = movieDetailContainer != null;
        if (twoPaneMode && selectedMovie == null) {
            movieDetailContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SELECTED_MOVIE_KEY, selectedMovie);
        outState.putInt(SELECTED_NAVIGATION_ITEM_KEY, selectedNavigationItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            selectedMovie = savedInstanceState.getParcelable(SELECTED_MOVIE_KEY);
            selectedNavigationItem = savedInstanceState.getInt(SELECTED_NAVIGATION_ITEM_KEY);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (twoPaneMode && movieDetailContainer != null) {
            movieDetailContainer.setVisibility(View.VISIBLE);
            selectedMovie = movie;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, MovieDetailFragment.newInstance(movie))
                    .commit();
            setupFab();
        } else {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra(ARG_MOVIE, movie);
            this.startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.drawer_item_explore:
                if (selectedNavigationItem != 0) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movies_grid_container, MoviesGridFragment.newInstance(SELECTED_MOVIES_GRID))
                            .commit();
                    selectedNavigationItem = 0;
                    hideMovieDetailContainer();
                }
                drawerLayout.closeDrawers();
                updateTitle();
                return true;
            case R.id.drawer_item_favorites:
                if (selectedNavigationItem != 1) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movies_grid_container, MoviesGridFragment.newInstance(SELECTED_FAVORITE_MOVIES_GRID))
                            .commit();
                    selectedNavigationItem = 1;
                    hideMovieDetailContainer();
                }
                drawerLayout.closeDrawers();
                updateTitle();
                return true;
            default:
                return false;
        }
    }

    private void updateTitle() {
        if (selectedNavigationItem == 0) {
            String[] sortTitles = getResources().getStringArray(R.array.pref_sort_by_labels);
            int currentSortIndex = sortHelper.getSortByPreference().ordinal();
            String title = Character.toString(sortTitles[currentSortIndex].charAt(0)).toUpperCase(Locale.US) +
                    sortTitles[currentSortIndex].substring(1);
            setTitle(title);
        } else if (selectedNavigationItem == 1) {
            setTitle(getResources().getString(R.string.favorites_grid_title));
        }
    }

    private void hideMovieDetailContainer() {
        selectedMovie = null;
        setupFab();
        if (twoPaneMode && movieDetailContainer != null) {
            movieDetailContainer.setVisibility(View.GONE);
        }
    }

    private void setupFab() {
        if (fab != null) {
            if (twoPaneMode && selectedMovie != null) {
                /*if (favoritesService.isFavorite(selectedMovie)) {
                    fab.setImageResource(R.drawable.ic_favorite_white);
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_white_border);
                }*/
                fab.show();
            } else {
                fab.hide();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
        updateTitle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED)) {
                hideMovieDetailContainer();
                updateTitle();
            }
        }
    };
}
