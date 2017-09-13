package com.tysovsky.gmusictestapp;


import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.tysovsky.gmusic.Core.GMusicClient;
import com.tysovsky.gmusic.Models.GMusicSong;
import com.tysovsky.gmusic.Interfaces.GetAllSongsListener;
import com.tysovsky.gmusic.Interfaces.LoginListener;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SongsFragment songsFragment = new SongsFragment();
    LoginFragment loginFragment = new LoginFragment();

    GMusicClient gMusicClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        loginFragment.setMainActivity(this);
        loginFragment.setMainActivity(this);

        gMusicClient = new GMusicClient(this);

        if(gMusicClient.isAuthenticated()){
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, songsFragment, SongsFragment.TAG);
            fragmentTransaction.commit();
        }
        else{
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, loginFragment, LoginFragment.TAG);
            fragmentTransaction.commit();
        }

    }


    public void login(final String username, final String password){

        gMusicClient.loginAsync(username, password,
                Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID),
                new LoginListener() {
                    @Override
                    public void OnComplete(boolean success) {
                        if (success){
                            gMusicClient.getAllSongsAsync(new GetAllSongsListener() {
                                @Override
                                public void OnCompleted(List<GMusicSong> songs) {
                                    Toast.makeText(MainActivity.this, songs.size() + " songs retrieved", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        switch (id){
            case R.id.nav_songs:
                fragmentTransaction.replace(R.id.fragment_container, songsFragment, SongsFragment.TAG);
                fragmentTransaction.commit();
                break;
            case R.id.nav_login:

                fragmentTransaction.replace(R.id.fragment_container, loginFragment, LoginFragment.TAG);
                fragmentTransaction.commit();
                break;

            case R.id.nav_logout:
                gMusicClient.logout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
