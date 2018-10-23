package com.example.manaadvice.popularmovietmdb.api;

import android.arch.lifecycle.LiveData;

import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.model.MovieResponse;
import com.example.manaadvice.popularmovietmdb.model.MovieReviewsResponse;
import com.example.manaadvice.popularmovietmdb.model.MovieVideosResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbService {
    @GET("discover/movie")
    LiveData<ApiResponse<MovieResponse>> discoverMovies(@Query("sort_by") String sortBy,
                                                        @Query("page") Integer page);

    @GET("movie/{id}/videos")
    LiveData<ApiResponse<MovieVideosResponse>> getMovieVideos(@Path("id") long movieId);

    @GET("movie/{id}/reviews")
    LiveData<ApiResponse<MovieReviewsResponse>> getMovieReviews(@Path("id") long movieId);

    @GET("search/movie")
    LiveData<ApiResponse<MovieResponse>> searchMovies(@Query("query") String query,
                                                              @Query("page") Integer page);
}
