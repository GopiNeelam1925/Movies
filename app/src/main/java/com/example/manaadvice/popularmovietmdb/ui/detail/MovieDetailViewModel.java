package com.example.manaadvice.popularmovietmdb.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.model.MovieReview;
import com.example.manaadvice.popularmovietmdb.model.MovieVideo;
import com.example.manaadvice.popularmovietmdb.utils.Objects;
import com.example.manaadvice.popularmovietmdb.utils.Repository;
import com.example.manaadvice.popularmovietmdb.utils.Resource;

import java.util.List;

import javax.inject.Inject;

public class MovieDetailViewModel extends ViewModel {
    private Repository repository;
    private final LiveData<Resource<List<MovieVideo>>> movieVideos;
    private final LiveData<Resource<List<MovieReview>>> movieReviews;
    private final LiveData<Boolean> isMovieFav;

    private final MutableLiveData<Long> movieId = new MutableLiveData<>();

    @Inject
    public MovieDetailViewModel(Repository repository){
        this.repository = repository;
        movieVideos = Transformations.switchMap(movieId, id -> {
            return repository.getMovieVideos(id);
        });
        movieReviews = Transformations.switchMap(movieId, id -> {
            return repository.getMovieReviews(id);
        });
        isMovieFav = Transformations.switchMap(movieId, id -> {
            return repository.isMovieFav(id);
        });
    }

    public void setMovieId(@NonNull Long movieId) {
        if (Objects.equals(movieId, this.movieId.getValue()))
            return;
        this.movieId.setValue(movieId);
    }

    public LiveData<Resource<List<MovieVideo>>> getMovieVideos() {
        return this.movieVideos;
    }
    public LiveData<Resource<List<MovieReview>>> getMovieReviews() {
        return this.movieReviews;
    }

    public LiveData<Boolean> isMovieFav() {
        return this.isMovieFav;
    }
    public void addToFavorite(Movie movie) {
        this.repository.saveMovieAsFavorite(movie);
    }
    public void deleteFromFavorite(Movie movie) {
        this.repository.deleteMovieFavorite(movie);
    }
}
