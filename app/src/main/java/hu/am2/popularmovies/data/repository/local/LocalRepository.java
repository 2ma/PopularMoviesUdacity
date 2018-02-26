package hu.am2.popularmovies.data.repository.local;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import hu.am2.popularmovies.data.repository.local.database.PopularMoviesContract;
import hu.am2.popularmovies.data.repository.remote.model.MovieModel;

public class LocalRepository {
    private final ContentResolver contentResolver;

    @Inject
    public LocalRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @WorkerThread
    public List<MovieModel> getAllFavorites() {
        Cursor cursor = contentResolver.query(PopularMoviesContract.Favorites.CONTENT_URI, PopularMoviesContract.Favorites.PROJECTION, null,
            null, null);
        List<MovieModel> movies = new ArrayList<>();

        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        final MovieModel movie = new MovieModel();
                        movie.setTitle(cursor.getString(PopularMoviesContract.Favorites.ROW_TITLE));
                        movie.setId(cursor.getInt(PopularMoviesContract.Favorites.ROW_MOVIE_ID));
                        movie.setSynopsis(cursor.getString(PopularMoviesContract.Favorites.ROW_SYNOPSIS));
                        movie.setReleaseDate(cursor.getString(PopularMoviesContract.Favorites.ROW_RELEASE_DATE));
                        movie.setUserRating(cursor.getDouble(PopularMoviesContract.Favorites.ROW_USER_RATING));
                        movie.setPosterUrl(cursor.isNull(PopularMoviesContract.Favorites.ROW_POSTER_URL) ? "" : cursor.getString
                            (PopularMoviesContract.Favorites.ROW_POSTER_URL));
                        movie.setBackDrop(cursor.isNull(PopularMoviesContract.Favorites.ROW_BACKDROP_URL) ? "" : cursor.getString
                            (PopularMoviesContract.Favorites.ROW_BACKDROP_URL));
                        movies.add(movie);
                    }
                } else {
                    movies = Collections.emptyList();
                }
            } finally {
                cursor.close();
            }
        } else {
            movies = Collections.emptyList();
        }

        return movies;
    }

    @WorkerThread
    public Boolean isMovieFavorite(int movieId) {
        final Uri movieUri = PopularMoviesContract.Favorites.buildFavoritesUri(movieId);
        Cursor cursor = contentResolver.query(movieUri, PopularMoviesContract.Favorites.PROJECTION, null, null, null);

        if (cursor == null) {
            return false;
        } else {
            try {
                return cursor.getCount() > 0;
            } finally {
                cursor.close();
            }
        }
    }

    @WorkerThread
    public void setFavorite(MovieModel movie) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(PopularMoviesContract.Favorites.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(PopularMoviesContract.Favorites.COLUMN_TITLE, movie.getTitle());
        contentValues.put(PopularMoviesContract.Favorites.COLUMN_SYNOPSIS, movie.getSynopsis());
        contentValues.put(PopularMoviesContract.Favorites.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(PopularMoviesContract.Favorites.COLUMN_USER_RATING, movie.getUserRating());
        contentValues.put(PopularMoviesContract.Favorites.COLUMN_POSTER_URL, movie.getPosterUrl());
        contentValues.put(PopularMoviesContract.Favorites.COLUMN_BACKDROP_URL, movie.getBackDrop());
        contentResolver.insert(PopularMoviesContract.Favorites.CONTENT_URI, contentValues);
    }

    @WorkerThread
    public void deleteFavorite(int movieId) {
        Uri movieUri = PopularMoviesContract.Favorites.buildFavoritesUri(movieId);
        contentResolver.delete(movieUri, null, null);
    }
}
