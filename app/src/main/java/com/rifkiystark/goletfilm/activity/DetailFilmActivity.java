package com.rifkiystark.goletfilm.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rifkiystark.goletfilm.widget.FavoriteStackWidget;
import com.rifkiystark.goletfilm.R;
import com.rifkiystark.goletfilm.helper.FilmsHelper;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static android.provider.BaseColumns._ID;
import static com.rifkiystark.goletfilm.database.DatabaseContract.CONTENT_URI;
import static com.rifkiystark.goletfilm.database.DatabaseContract.FavoriteColumns.IMG;
import static com.rifkiystark.goletfilm.database.DatabaseContract.FavoriteColumns.RATING;
import static com.rifkiystark.goletfilm.database.DatabaseContract.FavoriteColumns.TANGGAL;
import static com.rifkiystark.goletfilm.database.DatabaseContract.FavoriteColumns.TITLE;

public class DetailFilmActivity extends AppCompatActivity {
    int id;
    String genre,rating,negara,tahun_rilis,sinopsis,judul,img, img_poster;
    @BindView(R.id.tv_juduldetail) TextView tvJudul;
    @BindView(R.id.tv_genredetail) TextView tvGenre;
    @BindView(R.id.tv_ratingdetail) TextView tvRating;
    @BindView(R.id.tv_tahundetail) TextView tvTahunRilis;
    @BindView(R.id.tv_negaradetail) TextView tvNegara;
    @BindView(R.id.tv_sinopsis) TextView tvSinopsis;
    @BindView(R.id.img_detailfilm) ImageView imgFilm;
    @BindView(R.id.pb) RelativeLayout pb;
    FilmsHelper filmsHelper;
    Boolean isFavorite = false;
    Uri uri;

    private static String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_film);

        ButterKnife.bind(this);
        id = getIntent().getIntExtra("ID",0);
        img_poster = getIntent().getStringExtra("IMG");
        getDetail(String.valueOf(id));

        filmsHelper = new FilmsHelper(getApplicationContext());
        filmsHelper.open();
        uri = Uri.parse(CONTENT_URI+"/"+id);
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                isFavorite = true; ///Ambil data untuk mengecek apakah film sudah ada di tabel favorite
            }
            cursor.close();
        }


    }

    private void getDetail(String id) {
        String url ="https://api.themoviedb.org/3/movie/"+id+"?api_key=c2c1a3a284cd3e51b272c7e112984d77&language=en-US";
        AsyncHttpClient detail = new AsyncHttpClient();
        detail.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try{
                    JSONObject responseObject = new JSONObject(result);
                    img = responseObject.getString("backdrop_path"); //img
                    judul = responseObject.getString("original_title"); //judul
                    int genre_length = responseObject.getJSONArray("genres").length();

                    genre = ""; //genre
                    for (int i = 0;i < genre_length;i++){
                        genre += responseObject.getJSONArray("genres").getJSONObject(i).getString("name");
                        if(i < genre_length -1){
                            genre += ", ";
                        }
                    }

                    rating = responseObject.getString("vote_average"); //rating
                    tahun_rilis = responseObject.getString("release_date");
                    if(!tahun_rilis.equals("")){
                        String datearr[] = tahun_rilis.split("-");
                        String tahun = datearr[0];
                        int bulan = Integer.parseInt(datearr[1])-1;
                        String hari = datearr[2];
                        String namabulan[] = {"Janurai","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
                        tahun_rilis = hari+" "+namabulan[bulan]+" "+tahun; //tahun
                    }

                    negara = responseObject.getJSONArray("production_countries").getJSONObject(0).getString("name"); //negara
                    sinopsis = responseObject.getString("overview");
                    setView(genre,judul,negara,rating,tahun_rilis,img,sinopsis);

                }catch (Exception e){
                    e.printStackTrace();
                }
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(DetailFilmActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
            }
        });
    }

    void setView(String genre,String judul, String negara, String rating, String tahun_rilis, String img, String sinopsis){
        tvGenre.setText(genre);
        tvJudul.setText(judul);
        tvNegara.setText(negara);
        tvRating.setText(rating);
        tvTahunRilis.setText(tahun_rilis);
        tvSinopsis.setText(sinopsis);
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w780"+img)
                .into(imgFilm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_activity_options_menu,menu);
        if(isFavorite){
            menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic_thumb_up_red_24dp));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(isFavorite){
            item.setIcon(getResources().getDrawable(R.drawable.ic_thumb_up_white_24dp));
            isFavorite = false;
            getContentResolver().delete(uri, null,null);
            Toast.makeText(this, getResources().getString(R.string.toast_remove_favorite), Toast.LENGTH_SHORT).show();
            updateFavoriteWidget();
        }else{
            item.setIcon(getResources().getDrawable(R.drawable.ic_thumb_up_red_24dp));
            isFavorite = true;
            ContentValues contentValues = new ContentValues();
            contentValues.put(_ID,id);
            contentValues.put(TITLE,judul);
            contentValues.put(RATING,rating);
            contentValues.put(TANGGAL,tahun_rilis);
            contentValues.put(IMG,img_poster);
            getContentResolver().insert(CONTENT_URI,contentValues);
            Toast.makeText(this, getResources().getString(R.string.toast_add_favorite), Toast.LENGTH_SHORT).show();
            updateFavoriteWidget();
        }

        return true;
    }

    private void updateFavoriteWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, FavoriteStackWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view);
    }
}
