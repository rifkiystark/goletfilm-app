package com.rifkiystark.goletfilm.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rifkiystark.goletfilm.OnItemClickListener;
import com.rifkiystark.goletfilm.R;
import com.rifkiystark.goletfilm.activity.DetailFilmActivity;
import com.rifkiystark.goletfilm.model.Films;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteFilmsAdapter extends RecyclerView.Adapter<FavoriteFilmsAdapter.FilmsViewHolder> {
    private Cursor cursor;
    private Context context;

    @NonNull
    @Override
    public FavoriteFilmsAdapter.FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.cv_films,viewGroup,false);
        return new FavoriteFilmsAdapter.FilmsViewHolder(view);
    }

    public void setListFavorite(Cursor cursor){
        this.cursor = cursor;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteFilmsAdapter.FilmsViewHolder filmsViewHolder, int i) {
        final Films films = getItem(i);
        filmsViewHolder.tvTitle.setText(films.getTitle());
        filmsViewHolder.tvRating.setText(films.getRating());
        filmsViewHolder.tvRelease.setText(films.getRelease());
        Glide.with(context)
                .load(films.getImg())
                .apply(new RequestOptions().override(240, 240))
                .into(filmsViewHolder.imgFilms);
        filmsViewHolder.cvFilms.setOnClickListener(new OnItemClickListener(i, new OnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(context,DetailFilmActivity.class);
                intent.putExtra("ID",films.getId());
                intent.putExtra("IMG",films.getImg());
                context.startActivity(intent);
            }
        }));
    }

    private Films getItem(int i) {
        if(!cursor.moveToPosition(i)){
            throw new IllegalStateException("Position Invalid");

        }
        return new Films(cursor);
    }

    @Override
    public int getItemCount() {
        if(cursor == null){
            return 0;
        }else{
            return cursor.getCount();
        }
    }

    public FavoriteFilmsAdapter(Context context) {
        this.context = context;
    }

    public class FilmsViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.imgv_film) ImageView imgFilms;
        @BindView(R.id.tv_judul) TextView tvTitle;
        @BindView(R.id.tv_rating) TextView tvRating;
        @BindView(R.id.tv_release) TextView tvRelease;
        @BindView(R.id.cv_films) CardView cvFilms;
        public FilmsViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this,itemView);

        }

    }
}
