package com.rifkiystark.goletfilm.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rifkiystark.goletfilm.database.DatabaseContract;
import com.rifkiystark.goletfilm.helper.FilmsHelper;

import static android.support.constraint.Constraints.TAG;
import static com.rifkiystark.goletfilm.database.DatabaseContract.AUTHORITY;
import static com.rifkiystark.goletfilm.database.DatabaseContract.CONTENT_URI;

public class FilmsProvider extends ContentProvider {

    private static final int FILMS = 1;
    private static final int FILMS_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, DatabaseContract.TABLE_NAME, FILMS);
        sUriMatcher.addURI(AUTHORITY,
                DatabaseContract.TABLE_NAME+ "/#",
                FILMS_ID);
    }

    private FilmsHelper filmsHelper;

    @Override
    public boolean onCreate() {
        filmsHelper = new FilmsHelper(getContext());
        filmsHelper.open();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Cursor cursor;
        switch(sUriMatcher.match(uri)){
            case FILMS:
                cursor = filmsHelper.queryProvider();

                break;
            case FILMS_ID:
                cursor = filmsHelper.queryByIdProvider(uri.getLastPathSegment());

                break;
            default:
                cursor = null;
                break;
        }

        if (cursor != null){
            cursor.setNotificationUri(getContext().getContentResolver(),uri);

        }

        return cursor;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        Log.d(TAG, "insert: ");
        long added ;

        switch (sUriMatcher.match(uri)){
            case FILMS:
                added = filmsHelper.insertProvider(contentValues);

                break;
            default:
                added = 0;
                break;
        }

        if (added > 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return Uri.parse(CONTENT_URI + "/" + added);
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        int updated ;
        switch (sUriMatcher.match(uri)) {
            case FILMS_ID:
                updated =  filmsHelper.updateProvider(uri.getLastPathSegment(),contentValues);
                break;
            default:
                updated = 0;
                break;
        }

        if (updated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        int deleted;
        switch (sUriMatcher.match(uri)) {
            case FILMS_ID:
                deleted =  filmsHelper.deleteProvider(uri.getLastPathSegment());
                break;
            default:
                deleted = 0;
                break;
        }

        if (deleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleted;
    }

}
