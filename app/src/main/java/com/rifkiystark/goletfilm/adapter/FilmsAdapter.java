package com.rifkiystark.goletfilm.adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilmsAdapter extends RecyclerView.Adapter<FilmsAdapter.FilmsViewHolder> {
    private ArrayList<Films> films;
    private Context context;

    @NonNull
    @Override
    public FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.cv_films,viewGroup,false);
        return new FilmsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmsViewHolder filmsViewHolder, int i) {
        filmsViewHolder.tvTitle.setText(films.get(i).getTitle());
        filmsViewHolder.tvRating.setText(films.get(i).getRating());
        filmsViewHolder.tvRelease.setText(films.get(i).getRelease());
        Glide.with(context)
                .load(films.get(i).getImg())
                .apply(new RequestOptions().override(240, 240))
                .into(filmsViewHolder.imgFilms);
        filmsViewHolder.cvFilms.setOnClickListener(new OnItemClickListener(i, new OnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(context, DetailFilmActivity.class);
                intent.putExtra("ID",films.get(position).getId());
                intent.putExtra("IMG",films.get(position).getImg());
                context.startActivity(intent);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return (films != null) ? films.size() : 0;
    }

    public FilmsAdapter(ArrayList<Films> films, Context context) {
        this.films = films;
        this.context = context;
    }

    public class FilmsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgv_film) ImageView imgFilms;
        @BindView(R.id.tv_judul) TextView tvTitle;
        @BindView(R.id.tv_rating) TextView tvRating;
        @BindView(R.id.tv_release) TextView tvRelease;
        @BindView(R.id.cv_films) CardView cvFilms;
        private final Context context;
        public FilmsViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this,itemView);
        }
    }
}
