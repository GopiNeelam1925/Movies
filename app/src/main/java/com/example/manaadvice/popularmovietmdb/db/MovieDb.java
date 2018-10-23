package com.example.manaadvice.popularmovietmdb.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.model.MovieReview;
import com.example.manaadvice.popularmovietmdb.model.MovieVideo;

/**
 * Main database description.
 */

@Database(entities = {Movie.class, MovieReview.class, MovieVideo.class}, version = 1)
public abstract class MovieDb extends RoomDatabase {
    abstract public MovieDao movieDao();
}
