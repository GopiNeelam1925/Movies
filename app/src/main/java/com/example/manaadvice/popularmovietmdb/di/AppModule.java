package com.example.manaadvice.popularmovietmdb.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.manaadvice.popularmovietmdb.api.AuthorizationInterceptor;
import com.example.manaadvice.popularmovietmdb.api.MovieDbService;
import com.example.manaadvice.popularmovietmdb.api.MovieDeserializer;
import com.example.manaadvice.popularmovietmdb.db.MovieDao;
import com.example.manaadvice.popularmovietmdb.db.MovieDb;
import com.example.manaadvice.popularmovietmdb.model.Movie;
import com.example.manaadvice.popularmovietmdb.utils.LiveDataCallAdapterFactory;
import com.example.manaadvice.popularmovietmdb.utils.SortHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.manaadvice.popularmovietmdb.utils.Constants.MOVIE_DB;

@Module(includes = ViewModelModule.class)
class AppModule {

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final long CACHE_SIZE = 10 * 1024 * 1024;    // 10 MB
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;

    @Provides
    @Singleton
    Cache providesOkHttpCache(Application application) {
        return new Cache(application.getCacheDir(), CACHE_SIZE);
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(Cache cache) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Builder builder = new Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new AuthorizationInterceptor())
                .cache(cache);

        return builder.build();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(OkHttpClient okHttpClient) {
        //Gson gson = new GsonBuilder().registerTypeAdapter(Movie.class, new MovieDeserializer<Movie>()).create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    MovieDbService providesMovieDbService(Retrofit retrofit) {
        return retrofit.create(MovieDbService.class);
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    SortHelper providesSortHelper(SharedPreferences sharedPreferences) {
        return new SortHelper(sharedPreferences);
    }

    @Singleton @Provides
    MovieDb provideDb(Application app) {
        return Room.databaseBuilder(app, MovieDb.class, MOVIE_DB)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton @Provides
    MovieDao provideMovieDao(MovieDb db) {
        return db.movieDao();
    }
}
