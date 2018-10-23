package com.example.manaadvice.popularmovietmdb.ui.grid;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.example.manaadvice.popularmovietmdb.R;
import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.ui.detail.MovieDetailFragment;
import com.example.manaadvice.popularmovietmdb.utils.OnItemClickListener;
import com.example.manaadvice.popularmovietmdb.utils.SortHelper;
import com.example.manaadvice.popularmovietmdb.utils.SortingDialogFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.ARG_MOVIE;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.SELECTED_FAVORITE_MOVIES_GRID;
import static com.example.manaadvice.popularmovietmdb.utils.Constants.SELECTED_MOVIES_GRID;

public class MoviesGridFragment extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String SELECTED_GRID = "grid_selected";
    private String selectedGrid;
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    private MoviesViewModel viewModel;

    private MoviesAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private SearchView searchView;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.movies_grid)
    RecyclerView recyclerView;
    @BindView(R.id.view_no_movies)
    RelativeLayout noMoviesView;

    @Inject
    SortHelper sortHelper;

    public static MoviesGridFragment newInstance(String selectedMovieGrid) {
        MoviesGridFragment fragment = new MoviesGridFragment();
        Bundle args = new Bundle();
        args.putString(SELECTED_GRID, selectedMovieGrid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedGrid = getArguments().getString(SELECTED_GRID);
        }
        viewModel = ViewModelProviders.of(getActivity(),viewModelFactory).get(MoviesViewModel.class);

        if(selectedGrid.equals(SELECTED_MOVIES_GRID)){
            viewModel.setSort(sortHelper.getPrefSortValue());
            getMovies();
            setHasOptionsMenu(true);
        } else {
            viewModel.getFavoriteMovies().observe(this, favoriteMovies -> {
                if(favoriteMovies != null) {
                    adapter.setData(favoriteMovies);
                    //swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        ButterKnife.bind(this, rootView);

        initMoviesGrid();
        initSwipeRefreshLayout();
        return rootView;
    }

    private void initMoviesGrid() {
        adapter = new MoviesAdapter(getContext());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int columns = getResources().getInteger(R.integer.movies_columns);
        gridLayoutManager = new GridLayoutManager(getActivity(), columns);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onItemClick(View itemView, int position) {
        onItemSelectedListener.onItemSelected(adapter.getItem(position));
    }

    private OnItemSelectedListener onItemSelectedListener;
    public interface OnItemSelectedListener {
        void onItemSelected(Movie movie);
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_material_dark, R.color.accent_material_light);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if(selectedGrid.equals(SELECTED_MOVIES_GRID))
            getMovies();
        else
            swipeRefreshLayout.setRefreshing(false);
    }

    public void getMovies() {
        viewModel.getMovies().observe(this, movies -> {
            if(movies != null && movies.data != null) {
                adapter.setData(movies.data);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            onItemSelectedListener = (OnItemSelectedListener) context;
        } else {
            throw new IllegalStateException("The activity must implement " + OnItemSelectedListener.class.getSimpleName() + " interface.");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_menu, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        if (searchManager != null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                viewModel.setSort(sortHelper.getPrefSortValue());
                getMovies();
                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideSoftKeyBoard();
                return false;

            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()){
                    if(!newText.substring(newText.length() - 1).equals(" ")) {
                        viewModel.setSearchQuery(newText);
                        getMovies();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_show_sort_by_dialog:
                showSortByDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortByDialog() {
        DialogFragment sortingDialogFragment = new SortingDialogFragment();
        sortingDialogFragment.show(getFragmentManager(), SortingDialogFragment.TAG);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED)) {
                viewModel.setSort(sortHelper.getPrefSortValue());
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
