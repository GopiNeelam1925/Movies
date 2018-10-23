package com.example.manaadvice.popularmovietmdb.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.example.manaadvice.popularmovietmdb.api.ApiResponse;
import com.example.manaadvice.popularmovietmdb.api.MovieDbService;
import com.example.manaadvice.popularmovietmdb.db.MovieDao;
import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.model.MovieResponse;
import com.example.manaadvice.popularmovietmdb.model.MovieReview;
import com.example.manaadvice.popularmovietmdb.model.MovieReviewsResponse;
import com.example.manaadvice.popularmovietmdb.model.MovieVideo;
import com.example.manaadvice.popularmovietmdb.model.MovieVideosResponse;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository {
    private final MovieDbService movieService;
    private final AppExecutors appExecutors;
    private final MovieDao movieDao;

    private final MutableLiveData<Boolean> isFav = new MutableLiveData<>();

    @Inject
    Repository(MovieDbService movieService, AppExecutors appExecutors, MovieDao movieDao){
        this.movieService = movieService;
        this.appExecutors = appExecutors;
        this.movieDao = movieDao;
    }

    public void saveMovieAsFavorite(Movie movie) {
        appExecutors.diskIO().execute(() -> {
            movieDao.insertMovie(movie);
        });
    }

    public void deleteMovieFavorite(Movie movie) {
        appExecutors.diskIO().execute(() -> {
            movieDao.deleteFromFavorite(movie);
        });
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return movieDao.getFavoriteMovies();
    }

    public LiveData<Boolean> isMovieFav(long movieId) {
        appExecutors.diskIO().execute(() -> {
            if(movieDao.isMovieFav(movieId) != 0) {
                isFav.postValue(true);
            } else {
                isFav.postValue(false);
            }
        });
        return isFav;
    }

    public LiveData<Resource<List<Movie>>> getMovies(String sort){
        return new NetworkResource<MovieResponse, List<Movie>>(appExecutors){
            @NonNull
            @Override
            protected LiveData<ApiResponse<MovieResponse>> createCall() {
                return movieService.discoverMovies(sort, 1);
            }

            @Override
            protected List<Movie> processResponse(ApiResponse<MovieResponse> response) {
                return response.body.getResults();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<MovieVideo>>> getMovieVideos(long movieId){
        return new NetworkResource<MovieVideosResponse, List<MovieVideo>>(appExecutors) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<MovieVideosResponse>> createCall() {
                return movieService.getMovieVideos(movieId);
            }

            @Override
            protected List<MovieVideo> processResponse(ApiResponse<MovieVideosResponse> response) {
                return response.body.getResults();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<MovieReview>>> getMovieReviews(long movieId){
        return new NetworkResource<MovieReviewsResponse, List<MovieReview>>(appExecutors) {

            @NonNull
            @Override
            protected LiveData<ApiResponse<MovieReviewsResponse>> createCall() {
                return movieService.getMovieReviews(movieId);
            }

            @Override
            protected List<MovieReview> processResponse(ApiResponse<MovieReviewsResponse> response) {
                return response.body.getResults();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Movie>>> getMoviesSearch(String query){
        return new NetworkResource<MovieResponse, List<Movie>>(appExecutors){
            @NonNull
            @Override
            protected LiveData<ApiResponse<MovieResponse>> createCall() {
                return movieService.searchMovies(query, null);
            }

            @Override
            protected List<Movie> processResponse(ApiResponse<MovieResponse> response) {
                return response.body.getResults();
            }

        }.asLiveData();
    }

    abstract class NetworkResource<RequestType, ResultType> {
        private final AppExecutors appExecutors;
        private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

        @MainThread
        NetworkResource(AppExecutors appExecutors) {
            this.appExecutors = appExecutors;
            result.setValue(Resource.loading(null));
            fetchData();
        }

        private void fetchData(){
            LiveData<ApiResponse<RequestType>> apiResponse = createCall();
            result.addSource(apiResponse, response -> {
                result.removeSource(apiResponse);
                if (response.isSuccessful() && response.body != null) {
                    result.setValue(Resource.success(processResponse(response)));
                } else {
                    result.setValue(Resource.error(response.errorMessage));
                }
            });
        }
        @NonNull
        @MainThread
        protected abstract LiveData<ApiResponse<RequestType>> createCall();

        @WorkerThread
        protected abstract ResultType processResponse(ApiResponse<RequestType> response);

        public LiveData<Resource<ResultType>> asLiveData() {
            return result;
        }
        public Resource<ResultType> asObject() {
            return result.getValue();
        }
    }
}
