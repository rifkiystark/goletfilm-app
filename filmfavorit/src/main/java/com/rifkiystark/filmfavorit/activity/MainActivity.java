package com.rifkiystark.filmfavorit.activity;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.rifkiystark.filmfavorit.R;
import com.rifkiystark.filmfavorit.adapter.FilmsFavoriteAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rifkiystark.filmfavorit.database.DatabaseContract.CONTENT_URI;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private FilmsFavoriteAdapter filmsFavoriteAdapter;
    private final int ID = 135;
    @BindView(R.id.listview_favorite) ListView lvFavorit;

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ID, null, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        filmsFavoriteAdapter = new FilmsFavoriteAdapter(this,null,true);
        lvFavorit.setAdapter(filmsFavoriteAdapter);
        getSupportLoaderManager().initLoader(ID,null,this);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        filmsFavoriteAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        filmsFavoriteAdapter.swapCursor(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(ID);
    }
}
