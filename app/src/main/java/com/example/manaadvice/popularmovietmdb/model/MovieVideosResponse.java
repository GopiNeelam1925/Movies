package com.example.manaadvice.popularmovietmdb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideosResponse {
    @SerializedName("id")
    private long movieId;

    @SerializedName("results")
    private List<MovieVideo> results;

    public MovieVideosResponse(long movieId, List<MovieVideo> results) {
        this.movieId = movieId;
        this.results = results;
    }

    public long getMovieId() {
        return movieId;
    }

    public List<MovieVideo> getResults() {
        return results;
    }
}
