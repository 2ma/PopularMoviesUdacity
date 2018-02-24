package hu.am2.popularmovies.data.repository.local.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PopularMoviesProvider extends ContentProvider {

    private static final int FAVORITE = 100;
    private static final int FAVORITE_ID = 101;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private PopularMoviesDatabase database;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopularMoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, PopularMoviesContract.Favorites.PATH_FAVORITES, FAVORITE);
        uriMatcher.addURI(authority, PopularMoviesContract.Favorites.PATH_FAVORITES + "/*", FAVORITE_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        database = new PopularMoviesDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable
        String sortOrder) {

        Cursor cursor;
        final SQLiteDatabase db = database.getReadableDatabase();

        final int uriMatch = uriMatcher.match(uri);

        switch (uriMatch) {
            case FAVORITE: {
                cursor = db.query(PopularMoviesContract.Favorites.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            case FAVORITE_ID: {
                selection = PopularMoviesContract.Favorites.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PopularMoviesContract.Favorites.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int uriMatch = uriMatcher.match(uri);

        switch (uriMatch) {
            case FAVORITE: {
                return PopularMoviesContract.Favorites.CONTENT_DIR_TYPE;
            }
            case FAVORITE_ID: {
                return PopularMoviesContract.Favorites.CONTENT_ITEM_TYPE;
            }
            default:
                throw new UnsupportedOperationException("Unknown type for uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = database.getWritableDatabase();

        long id = db.insert(PopularMoviesContract.Favorites.TABLE_NAME, null, values);

        Uri newUri;
        if (id > 0) {
            newUri = PopularMoviesContract.Favorites.buildFavoritesUri(id);
        } else {
            throw new SQLException("Failed to insert row " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return newUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = database.getWritableDatabase();

        final int uriMatch = uriMatcher.match(uri);

        int rowsDeleted;

        switch (uriMatch) {
            case FAVORITE: {
                rowsDeleted = db.delete(PopularMoviesContract.Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITE_ID: {
                selection = PopularMoviesContract.Favorites.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = db.delete(PopularMoviesContract.Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Failed to delete, unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = database.getWritableDatabase();

        final int uriMatch = uriMatcher.match(uri);

        int rowsUpdated;

        if (uriMatch == FAVORITE_ID) {
            selection = PopularMoviesContract.Favorites.COLUMN_MOVIE_ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

            rowsUpdated = db.update(PopularMoviesContract.Favorites.TABLE_NAME, values, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Cannot update, unknown uri: " + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
