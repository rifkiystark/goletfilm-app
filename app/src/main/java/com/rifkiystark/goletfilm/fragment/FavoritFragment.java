package com.rifkiystark.goletfilm.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rifkiystark.goletfilm.R;
import com.rifkiystark.goletfilm.adapter.FavoriteFilmsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;
import static com.rifkiystark.goletfilm.database.DatabaseContract.CONTENT_URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritFragment extends Fragment {

    @BindView(R.id.recy_favorite)RecyclerView recyclerView;
    @BindView(R.id.pb)RelativeLayout progressBar;
    Context context;
    FavoriteFilmsAdapter favoriteFilmsAdapter;
    Cursor cursorFilms;

    public FavoritFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        favoriteFilmsAdapter = new FavoriteFilmsAdapter(context);
        favoriteFilmsAdapter.setListFavorite(cursorFilms);
        recyclerView.setAdapter(favoriteFilmsAdapter);
        new LoadFilmsAsync().execute();
        progressBar.setVisibility(View.GONE);
    }

    private class LoadFilmsAsync extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return context.getContentResolver().query(CONTENT_URI,null,null,null,null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            cursorFilms = cursor;
            favoriteFilmsAdapter.setListFavorite(cursorFilms);
            favoriteFilmsAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     *
     * Load Data Ketika Fragment Favorit Di Tampilkan
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            new LoadFilmsAsync().execute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadFilmsAsync().execute();
    }
}
