package hu.am2.popularmovies.data.repository.local.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PopularMoviesDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "popular_movies.db";

    public PopularMoviesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PopularMoviesContract.Favorites.TABLE_NAME + " (" +
            PopularMoviesContract.Favorites.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY NOT NULL, " +
            PopularMoviesContract.Favorites.COLUMN_TITLE + " TEXT NOT NULL, " +
            PopularMoviesContract.Favorites.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
            PopularMoviesContract.Favorites.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
            PopularMoviesContract.Favorites.COLUMN_POSTER_URL + " TEXT, " +
            PopularMoviesContract.Favorites.COLUMN_BACKDROP_URL + " TEXT, " +
            PopularMoviesContract.Favorites.COLUMN_USER_RATING + " REAL NOT NULL );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
