package com.example.manaadvice.popularmovietmdb.ui.grid;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.utils.Filtering;
import com.example.manaadvice.popularmovietmdb.utils.Objects;
import com.example.manaadvice.popularmovietmdb.utils.Repository;
import com.example.manaadvice.popularmovietmdb.utils.Resource;

import java.util.List;

import javax.inject.Inject;

public class MoviesViewModel extends ViewModel {

    private LiveData<Resource<List<Movie>>> movies;
    private LiveData<List<Movie>> favoriteMovies;
    private final MutableLiveData<String> sort = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<Filtering> filtering = new MutableLiveData<>();

    @Inject
    public MoviesViewModel(Repository repository){
        movies = Transformations.switchMap(filtering, filter -> {
            switch (filter) {
                case SORT: return repository.getMovies(this.sort.getValue());
                case SEARCH: return repository.getMoviesSearch(this.searchQuery.getValue());
                default: return repository.getMovies(this.sort.getValue());
            }
        });
        favoriteMovies = repository.getFavoriteMovies();
    }

    public void setSort(@NonNull String prefSort) {
        this.sort.setValue(prefSort);
        this.filtering.setValue(Filtering.SORT);
    }
    public void setSearchQuery(@NonNull String searchQuery) {
        if (Objects.equals(searchQuery, this.searchQuery.getValue()) || searchQuery.isEmpty() )
            return;
        this.searchQuery.setValue(searchQuery);
        this.filtering.setValue(Filtering.SEARCH);
    }

    public LiveData<Resource<List<Movie>>> getMovies() {
        return movies;
    }
    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }
}
