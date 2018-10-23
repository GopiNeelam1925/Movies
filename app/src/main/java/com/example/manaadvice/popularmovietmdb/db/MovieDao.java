package com.example.manaadvice.popularmovietmdb.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.manaadvice.popularmovietmdb.model.Movie;

import java.util.List;

@Dao
public abstract class MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertMovie(Movie movie);

    @Query("SELECT * FROM movie")
    public abstract LiveData<List<Movie>> getFavoriteMovies();

    @Query("SELECT COUNT(*) FROM movie WHERE id=:movieId")
    public abstract int isMovieFav(long movieId);

    @Delete
    public abstract void deleteFromFavorite(Movie movie);
}
