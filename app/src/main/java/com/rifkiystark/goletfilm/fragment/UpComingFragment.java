package com.rifkiystark.goletfilm.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rifkiystark.goletfilm.BuildConfig;
import com.rifkiystark.goletfilm.model.Films;
import com.rifkiystark.goletfilm.adapter.FilmsAdapter;
import com.rifkiystark.goletfilm.R;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;



/**
 * A simple {@link Fragment} subclass.
 */
public class UpComingFragment extends Fragment {

    private ArrayList<Films> films;
    private Context CONTEXT;
    @BindView(R.id.recy_upcoming) RecyclerView recyclerView;
    @BindView(R.id.pb) RelativeLayout pb;
    String API = BuildConfig.TMDB_API_KEY;
    RecyclerView.LayoutManager layoutManager;
    FilmsAdapter filmsAdapter;
    Parcelable pLayoutManager;

    public UpComingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("LAYOUT", layoutManager.onSaveInstanceState());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_up_coming, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);
        if(savedInstanceState != null){
            pLayoutManager = savedInstanceState.getParcelable("LAYOUT");
            set_recyFilms();
        }else{
            String url = "https://api.themoviedb.org/3/movie/upcoming?api_key="+API+"&language=en-US";
            get_films(url);
        }
        pb.setVisibility(View.GONE);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CONTEXT = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CONTEXT = null;
    }

    private void get_films(String url) {

        AsyncHttpClient get_films = new AsyncHttpClient();
        get_films.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                int jml_data;
                try{
                    films = new ArrayList<>();
                    JSONObject responseObject = new JSONObject(result);
                    //Log.d(TAG, "onSuccess: " + result);
                    jml_data = responseObject.getJSONArray("results").length();
                    //Log.d(TAG, "onSuccess: " + jml_data);
                    if(jml_data > 0){
                        for (int i = 0;i < jml_data;i++){
                            int id = responseObject.getJSONArray("results").getJSONObject(i).getInt("id");
                            String rating = getResources().getString(R.string.rating)+ " : " +responseObject.getJSONArray("results").getJSONObject(i).getString("vote_average");
                            String img = "https://image.tmdb.org/t/p/w92/"+responseObject.getJSONArray("results").getJSONObject(i).getString("poster_path");
                            String title = responseObject.getJSONArray("results").getJSONObject(i).getString("original_title");
                            String release = responseObject.getJSONArray("results").getJSONObject(i).getString("release_date");
                            String tanggal = getResources().getString(R.string.tahun_rilis) + " : -";
                            if(!release.equals("")){
                                String datearr[] = release.split("-");
                                String tahun = datearr[0];
                                int bulan = Integer.parseInt(datearr[1])-1;
                                String hari = datearr[2];
                                String namabulan[] = {"Janurai","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
                                tanggal = "Tanggal Rilis : " +hari+" "+namabulan[bulan]+" "+tahun;
                            }
                            films.add(new Films(id,title,tanggal,rating,img));
                        }
                        set_recyFilms();
                    }else{
                        Toast.makeText(CONTEXT, "Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                pb.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(CONTEXT, error.getMessage(), Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
            }
        });

    }

    private void set_recyFilms() {

        filmsAdapter = new FilmsAdapter(films,CONTEXT);
        layoutManager = new LinearLayoutManager(CONTEXT);
        if(pLayoutManager != null){
            layoutManager.onRestoreInstanceState(pLayoutManager);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(filmsAdapter);
    }
}
