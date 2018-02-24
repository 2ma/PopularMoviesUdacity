package hu.am2.popularmovies.data.repository.local.database;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

import hu.am2.popularmovies.BuildConfig;

public class PopularMoviesContract {

    static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class Favorites {

        public static final String PATH_FAVORITES = "favorites";

        public static final String TABLE_NAME = "favorites";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_URL = "poster_url";

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static Uri buildFavoritesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] PROJECTION = {
            COLUMN_MOVIE_ID,
            COLUMN_TITLE,
            COLUMN_SYNOPSIS,
            COLUMN_USER_RATING,
            COLUMN_RELEASE_DATE,
            COLUMN_POSTER_URL
        };

        public static final int ROW_MOVIE_ID = 0;
        public static final int ROW_TITLE = 1;
        public static final int ROW_SYNOPSIS = 2;
        public static final int ROW_USER_RATING = 3;
        public static final int ROW_RELEASE_DATE = 4;
        public static final int ROW_POSTER_URL = 5;
    }
}
