package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.pplki18.grouptravelplanner.utils.SessionManager;

public class Activity_InHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Button buttonLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_home);
        init();

        navigationView.setNavigationItemSelectedListener(this);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);

        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_InHome.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
                    new Fragment_GroupList()).commit();
            navigationView.setCheckedItem(R.id.nav_group_list);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_group_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
                        new Fragment_GroupList()).commit();
                break;
            case R.id.nav_friend_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_home,
                        new Fragment_Friends()).commit();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        sessionManager = new SessionManager(getApplicationContext());

        drawer = findViewById(R.id.drawer_home);
        toolbar = findViewById(R.id.toolbar_home);
        navigationView = findViewById(R.id.nav_home);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        buttonLogout  = findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sessionManager.logoutUser();
                    }
                }
        );
    }

}
