package com.rifkiystark.filmfavorit.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rifkiystark.filmfavorit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rifkiystark.filmfavorit.database.DatabaseContract.FavoriteColumns.IMG;
import static com.rifkiystark.filmfavorit.database.DatabaseContract.FavoriteColumns.RATING;
import static com.rifkiystark.filmfavorit.database.DatabaseContract.FavoriteColumns.TANGGAL;
import static com.rifkiystark.filmfavorit.database.DatabaseContract.FavoriteColumns.TITLE;
import static com.rifkiystark.filmfavorit.database.DatabaseContract.getColumnString;

public class FilmsFavoriteAdapter extends CursorAdapter {
    @BindView(R.id.tv_judul)TextView tvJudul;
    @BindView(R.id.tv_rating)TextView tvRating;
    @BindView(R.id.tv_release)TextView tvRilis;
    @BindView(R.id.imgv_film)ImageView imgFilm;

    public FilmsFavoriteAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.cv_films,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if(cursor != null){
            ButterKnife.bind(this,view);
            tvJudul.setText(getColumnString(cursor,TITLE));
            tvRilis.setText(getColumnString(cursor,TANGGAL));
            tvRating.setText(getColumnString(cursor,RATING));
            Glide.with(context)
                    .load(getColumnString(cursor,IMG))
                    .apply(new RequestOptions().override(240, 240))
                    .into(imgFilm);
        }
    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }
}
