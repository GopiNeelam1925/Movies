package com.example.manaadvice.popularmovietmdb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieReviewsResponse {
    @SerializedName("id")
    private long movieId;

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<MovieReview> results;

    @SerializedName("total_pages")
    private int totalPages;

    public MovieReviewsResponse(long movieId, int page, List<MovieReview> results, int totalPages) {
        this.movieId = movieId;
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
    }

    public long getMovieId() {
        return movieId;
    }

    public int getPage() {
        return page;
    }

    public List<MovieReview> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
