package com.rifkiystark.filmfavorit.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.rifkiystark.filmfavorit.database.DatabaseContract;

import static com.rifkiystark.filmfavorit.database.DatabaseContract.getColumnInt;
import static com.rifkiystark.filmfavorit.database.DatabaseContract.getColumnString;

public class Films implements Parcelable {
    private int id;
    private String title, release, rating, img;

    public Films(int id, String title, String release, String rating, String img) {
        this.id = id;
        this.title = title;
        this.release = release;
        this.rating = rating;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.release);
        dest.writeString(this.rating);
        dest.writeString(this.img);
    }

    protected Films(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.release = in.readString();
        this.rating = in.readString();
        this.img = in.readString();
    }

    public static final Parcelable.Creator<Films> CREATOR = new Parcelable.Creator<Films>() {
        @Override
        public Films createFromParcel(Parcel source) {
            return new Films(source);
        }

        @Override
        public Films[] newArray(int size) {
            return new Films[size];
        }
    };

    public Films(Cursor cursor) {
        this.id = getColumnInt(cursor, DatabaseContract.FavoriteColumns.ID);
        this.title = getColumnString(cursor, DatabaseContract.FavoriteColumns.TITLE);
        this.release = getColumnString(cursor, DatabaseContract.FavoriteColumns.TANGGAL);
        this.rating = getColumnString(cursor, DatabaseContract.FavoriteColumns.RATING);
        this.img = getColumnString(cursor, DatabaseContract.FavoriteColumns.IMG);
    }
}