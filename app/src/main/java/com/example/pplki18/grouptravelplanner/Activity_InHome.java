package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract.UserEntry;
import com.example.pplki18.grouptravelplanner.utils.GlideApp;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_InHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHelper myDb;
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

        setHeaderInfo();

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

    public void setHeaderInfo(){
        View headerView = navigationView.getHeaderView(0);

        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_InHome.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        ImageView header_photo = headerView.findViewById(R.id.nav_profile);
        TextView header_fullname = headerView.findViewById(R.id.user_fullname);
        TextView header_status = headerView.findViewById(R.id.user_status);

        // set profile picture
        String photoUrl = sessionManager.getUserDetails().get(sessionManager.KEY_PHOTO_URL);
        Log.d("PHOTO_URL", photoUrl);
        Uri uri = Uri.parse(photoUrl);
        GlideApp.with(this)
                .load(uri)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .centerCrop()
                .apply(new RequestOptions().override(120, 120))
                .apply(RequestOptions.circleCropTransform())
                .into(header_photo);

        header_fullname.setText(sessionManager.getUserDetails().get(sessionManager.KEY_FULLNAME));

        String status = null;
        if(sessionManager.isOnTrip()){
            status = "On Trip";
        }
        else{
            status = "Not On Trip";
        }
        header_status.setText("Status: " + status);
    }

}
