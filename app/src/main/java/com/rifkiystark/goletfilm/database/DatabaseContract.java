package com.rifkiystark.goletfilm.database;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static String TABLE_NAME = "favorite";

    public static final class FavoriteColumns implements BaseColumns {
        public static String TITLE = "title";
        public static String TANGGAL = "tanggal";
        public static String RATING = "rating";
        public static String IMG = "img";
    }

    public static final String AUTHORITY = "com.rifkiystark.goletfilm";
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(AUTHORITY)
            .appendPath(TABLE_NAME)
            .build();
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString( cursor.getColumnIndex(columnName) );
    }
    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt( cursor.getColumnIndex(columnName) );
    }
    public static long getColumnLong(Cursor cursor, String columnName) {
        return cursor.getLong( cursor.getColumnIndex(columnName) );
    }

}
