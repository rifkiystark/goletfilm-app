package com.rifkiystark.goletfilm.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rifkiystark.goletfilm.NotificationReceiver;
import com.rifkiystark.goletfilm.SharedPref;
import com.rifkiystark.goletfilm.fragment.FavoritFragment;
import com.rifkiystark.goletfilm.fragment.NowPlayingFragment;
import com.rifkiystark.goletfilm.R;
import com.rifkiystark.goletfilm.fragment.SearchFragment;
import com.rifkiystark.goletfilm.fragment.UpComingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.bottom_nav) BottomNavigationView bottomNavigation;
    Fragment active, searchFragment, nowplayingFragment, upcomingFragment, favoritFragment;
    private static final String TAG = "tag";
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        if(savedInstanceState != null){
            Log.d(TAG, "onCreate: SavedInstanceState");
            searchFragment = (SearchFragment)getSupportFragmentManager().getFragment(savedInstanceState,"search");
            nowplayingFragment = (NowPlayingFragment)getSupportFragmentManager().getFragment(savedInstanceState,"nowplaying");
            upcomingFragment = (UpComingFragment)getSupportFragmentManager().getFragment(savedInstanceState,"upcoming");
            favoritFragment = (FavoritFragment)getSupportFragmentManager().getFragment(savedInstanceState,"favorit");
            active = getSupportFragmentManager().getFragment(savedInstanceState,"active");
        }else{
            searchFragment = new SearchFragment();
            nowplayingFragment = new NowPlayingFragment();
            upcomingFragment = new UpComingFragment();
            favoritFragment = new FavoritFragment();
            setFragment(searchFragment,"search");
            setFragment(nowplayingFragment, "nowplaying");
            setFragment(upcomingFragment, "upcoming");
            setFragment(favoritFragment,"favorit");
            getSupportFragmentManager().beginTransaction().show(searchFragment).commit();
            active = searchFragment;
        }

        sharedPref = new SharedPref(this);
        if(sharedPref.reminderActivated()){
            NotificationReceiver notificationReceiver = new NotificationReceiver();
            notificationReceiver.setReleaseDate(this);
            notificationReceiver.setReminder(this);
        }
    }



    private void setActiveFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(active)
                .show(fragment)
                .commit();
        active = fragment;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        String title = null;
        switch (menuItem.getItemId()){
            case R.id.menu_search:
                if(bottomNavigation.getSelectedItemId() != R.id.menu_search){
                    fragment = searchFragment;
                    title = getResources().getString(R.string.app_name);
                }
                break;
            case R.id.menu_now_playing:
                if(bottomNavigation.getSelectedItemId() != R.id.menu_now_playing){
                    fragment = nowplayingFragment;
                    title = getResources().getString(R.string.now_playing);
                }
                break;
            case R.id.menu_upcoming:
                if(bottomNavigation.getSelectedItemId() != R.id.menu_upcoming){
                    fragment = upcomingFragment;
                    title = getResources().getString(R.string.up_coming);
                }
                break;
            case R.id.menu_favorite:
                if(bottomNavigation.getSelectedItemId() != R.id.menu_favorite){
                    fragment = favoritFragment;
                    title = getResources().getString(R.string.favorite);
                }
                break;
        }

        if(fragment != null){
            setActiveFragment(fragment);
            getSupportActionBar().setTitle(title);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu,menu);
        sharedPref = new SharedPref(this);
        if(!sharedPref.reminderActivated()){
            menu.findItem(R.id.action_change_reminder).setTitle(R.string.reminder_off);
        }
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            SearchView searchView = (SearchView) (menu.findItem(R.id.search)).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.carifilm));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    bottomNavigation.setSelectedItemId(R.id.menu_search);
                    String QUERY = query.replaceAll(" ","%20");
                    getSupportFragmentManager().beginTransaction().remove(searchFragment).commit();
                    searchFragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("QUERY",QUERY);
                    searchFragment.setArguments(bundle);
                    setFragment(searchFragment, "search");
                    setActiveFragment(searchFragment);
                    return true;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_settings){
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
        }else if(item.getItemId() == R.id.action_change_reminder){
            sharedPref = new SharedPref(this);
            if(sharedPref.reminderActivated()){
                sharedPref.editReminnder(false);
                item.setTitle(R.string.reminder_off);
                NotificationReceiver notificationReceiver = new NotificationReceiver();
                notificationReceiver.cancelAlarm(this,1);
                notificationReceiver.cancelAlarm(this,2);
            }else{
                sharedPref.editReminnder(true);
                item.setTitle(R.string.reminder);
                NotificationReceiver notificationReceiver = new NotificationReceiver();
                notificationReceiver.setReleaseDate(this);
                notificationReceiver.setReminder(this);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_fragment, fragment,tag)
                .hide(fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "search",searchFragment);
        getSupportFragmentManager().putFragment(outState, "nowplaying",nowplayingFragment);
        getSupportFragmentManager().putFragment(outState, "upcoming",upcomingFragment);
        getSupportFragmentManager().putFragment(outState, "favorit",favoritFragment);
        getSupportFragmentManager().putFragment(outState, "active",active);
    }

}
